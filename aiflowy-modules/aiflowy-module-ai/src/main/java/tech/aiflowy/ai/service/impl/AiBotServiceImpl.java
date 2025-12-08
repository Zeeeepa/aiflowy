package tech.aiflowy.ai.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.agentsflex.core.message.AiMessage;
import com.agentsflex.core.message.ToolMessage;
import com.agentsflex.core.model.chat.ChatModel;
import com.agentsflex.core.model.chat.ChatOptions;
import com.agentsflex.core.model.chat.StreamResponseListener;
import com.agentsflex.core.model.chat.response.AiMessageResponse;
import com.agentsflex.core.model.chat.tool.GlobalToolInterceptors;
import com.agentsflex.core.model.client.StreamContext;
import com.agentsflex.core.prompt.MemoryPrompt;
import com.agentsflex.core.util.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tech.aiflowy.ai.entity.AiBot;
import tech.aiflowy.ai.mapper.AiBotMapper;
import tech.aiflowy.ai.service.AiBotService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import tech.aiflowy.ai.utils.CustomBeanUtils;
import tech.aiflowy.common.ai.ChatSseEmitter;
import tech.aiflowy.common.ai.inteceptor.ToolLoggingInterceptor;
import tech.aiflowy.common.util.StringUtil;
import tech.aiflowy.common.web.exceptions.BusinessException;
import tech.aiflowy.ai.utils.RegexUtils;
import com.mybatisflex.core.query.QueryWrapper;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  服务层实现。
 *
 * @author michael
 * @since 2024-08-23
 */
@Service
public class AiBotServiceImpl extends ServiceImpl<AiBotMapper, AiBot> implements AiBotService {

    private static final Logger log = LoggerFactory.getLogger(AiBotServiceImpl.class);

    private static final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Resource(name = "sseThreadPool")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    // 30秒发一次心跳
    private static final long HEARTBEAT_INTERVAL = 30 * 1000L;
    // 心跳消息
    private static final String HEARTBEAT_MESSAGE = "ping";
    @Override
    public AiBot getDetail(String id) {
        AiBot aiBot = null;

        if (id.matches(RegexUtils.ALL_NUMBER)){
            aiBot = getById(id);

            if (aiBot == null) {
               aiBot = getByAlias(id);
            }

        }else{
            aiBot = getByAlias(id);
        }

        return aiBot;
    }

    @Override
    public AiBot getByAlias(String alias){
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq("alias",alias);
        return getOne(queryWrapper);
    }

    @Override
    public SseEmitter startChat(BigInteger botId, ChatModel chatModel, String prompt, MemoryPrompt memoryPrompt, ChatOptions chatOptions, String sessionId) {

        GlobalToolInterceptors.addInterceptor(new ToolLoggingInterceptor());

        // 完成时移除emitter
        String currentUserId = StpUtil.getLoginIdAsString();
        SseEmitter emitter = ChatSseEmitter.create();
        emitter.onCompletion(() -> {
            emitters.remove(currentUserId);
            log.debug("SSE连接完成，移除用户[{}]的Emitter", currentUserId);
        });
        emitter.onTimeout(() -> {
            emitters.remove(currentUserId);
            emitter.complete();
            log.warn("SSE连接超时，移除用户[{}]的Emitter", currentUserId);
        });
        emitter.onError((e) -> {
            emitters.remove(currentUserId);
            emitter.completeWithError(e);
            log.error("SSE连接异常，移除用户[{}]的Emitter", currentUserId, e);
        });
        System.out.println("emitters大小" + emitters.size()) ;
        emitters.put(currentUserId, emitter);
        log.debug("新增SSE连接，用户[{}]，当前活跃连接数：{}", currentUserId, emitters.size());
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        threadPoolTaskExecutor.execute(() -> {
            final boolean[] hasFinished = {false};
            ServletRequestAttributes sra = (ServletRequestAttributes) requestAttributes;
            RequestContextHolder.setRequestAttributes(requestAttributes, true);
            final StreamResponseListener streamResponseListener = new StreamResponseListener() {
                @Override
                public void onMessage(StreamContext streamContext, AiMessageResponse aiMessageResponse) {
                    AiMessage aiMessage = aiMessageResponse.getMessage();
                    if (aiMessage == null) {
                        return;
                    }

                    if (aiMessage.isFinalDelta() && aiMessageResponse.hasToolCalls()) {
                        List<ToolMessage> toolMessages = aiMessageResponse.executeToolCallsAndGetToolMessages();
                        for (ToolMessage toolMessage : toolMessages) {
                            memoryPrompt.addMessage(toolMessage);
                        }
                        chatModel.chatStream(memoryPrompt, this);
                    }


                    try {
                        if (currentUserId != null) {
                            SseEmitter emitter = emitters.get(currentUserId);
                            if (emitter != null) {
                                String fullText = aiMessageResponse.getMessage().getFullContent();
                                String delta = aiMessageResponse.getMessage().getContent();
                                if (StringUtil.hasText(delta)) {
                                    emitter.send(delta);
                                }
                            }
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }


                }

                @Override
                public void onStart(StreamContext context) {
                    StreamResponseListener.super.onStart(context);
                }

                @Override
                public void onStop(StreamContext context) {
                    RequestContextHolder.setRequestAttributes(sra, true);
                    if (hasFinished[0]) {
                        return;
                    }
                    AiMessage aiMessage = context.getAiMessage();
                    String finishReason = aiMessage.getFinishReason();
                    if (!StringUtil.hasText(finishReason)) {
                        return;
                    }
                    // 检查是否有工具调用请求
                    if (aiMessage.getFinished() && "stop".equals(aiMessage.getFinishReason()) && CollectionUtil.hasItems(aiMessage.getToolCalls())) {
                        hasFinished[0] = true;
                        AiMessageResponse aiMessageResponse = new AiMessageResponse(context.getChatContext(), prompt, aiMessage);
                        List<ToolMessage> toolMessages = aiMessageResponse.executeToolCallsAndGetToolMessages();
                        String newPrompt = "请根据以下内容回答用户，内容是:\n" + toolMessages + "\n 用户的问题是：" + prompt;

                        chatFunctionCallStream(newPrompt, chatModel, emitter, chatOptions);
                    }
                    if ("stop".equals(finishReason) && !CollectionUtil.hasItems(aiMessage.getToolCalls())) {
                        hasFinished[0] = true;
                        emitter.complete();
                        emitters.remove(currentUserId);
                    }

                }

                @Override
                public void onFailure(StreamContext context, Throwable throwable) {
                    StreamResponseListener.super.onFailure(context, throwable);
                }
            };


            chatModel.chatStream(memoryPrompt, streamResponseListener);
        });

        return emitter;
    }

    public void chatFunctionCallStream (String newPrompt, ChatModel chatModel, SseEmitter emitter, ChatOptions chatOptions){
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        chatModel.chatStream(newPrompt, new StreamResponseListener() {
            @Override
            public void onMessage(StreamContext streamContext, AiMessageResponse aiMessageResponse) {
                try {
                    RequestContextHolder.setRequestAttributes(sra, true);
                    if (emitter != null) {
                        String fullText = aiMessageResponse.getMessage().getFullContent();
                        String delta = aiMessageResponse.getMessage().getContent();
                        if (StringUtil.hasText(delta)) {
                            emitter.send(delta);
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        }, chatOptions);

    }

    // 定时任务：30s发送心跳 + 清理无效连接
    @Scheduled(fixedRate = HEARTBEAT_INTERVAL)
    public void heartbeatTask() {
        threadPoolTaskExecutor.execute(() -> {
            if (emitters.isEmpty()) return;

            Iterator<Map.Entry<String, SseEmitter>> iterator = emitters.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, SseEmitter> entry = iterator.next();
                String userId = entry.getKey();
                SseEmitter emitter = entry.getValue();

                try {
                    // 发送comment类型心跳（前端不触发message事件，仅保活）
                    emitter.send(SseEmitter.event().comment(HEARTBEAT_MESSAGE));
                } catch (Exception e) {
                    // 发送失败=连接失效，立即清理
                    log.error("心跳发送失败，清理用户{}的SSE连接", userId, e);
                    iterator.remove();
                    try {
                        emitter.complete();
                    } catch (Exception ex) {
                        log.warn("关闭失效Emitter失败", ex);
                    }
                }
            }
        });
    }


    @Override
    public void updateBotLlmId(AiBot aiBot) {
        AiBot byId = getById(aiBot.getId());

        if (byId == null) {
            log.error("修改bot的llmId失败，bot不存在！");
            throw new BusinessException("bot不存在！");
        }

        byId.setLlmId(aiBot.getLlmId());

        updateById(byId,false);

    }


    @Override
    public boolean updateById(AiBot entity) {
        AiBot aiBot = getById(entity.getId());
        if (aiBot == null) {
            throw new BusinessException("bot 不存在");
        }

        CustomBeanUtils.copyPropertiesIgnoreNull(entity,aiBot);

        if ("".equals(aiBot.getAlias())){
            aiBot.setAlias(null);
        }


        return super.updateById(aiBot,false);
    }
}
