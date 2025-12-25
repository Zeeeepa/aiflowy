package tech.aiflowy.ai.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.agentsflex.core.memory.ChatMemory;
import com.agentsflex.core.message.UserMessage;
import com.agentsflex.core.model.chat.ChatModel;
import com.agentsflex.core.model.chat.ChatOptions;
import com.agentsflex.core.model.chat.StreamResponseListener;
import com.agentsflex.core.prompt.MemoryPrompt;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tech.aiflowy.ai.entity.Bot;
import tech.aiflowy.ai.entity.BotMessage;
import tech.aiflowy.ai.agentsflex.memory.DefaultBotMessageMemory;
import tech.aiflowy.ai.agentsflex.memory.BotMessageMemory;
import tech.aiflowy.ai.mapper.BotMapper;
import tech.aiflowy.ai.service.BotMessageService;
import tech.aiflowy.ai.service.BotService;
import tech.aiflowy.ai.agentsflex.listener.ChatStreamListener;
import tech.aiflowy.ai.utils.CustomBeanUtils;
import tech.aiflowy.ai.utils.RegexUtils;
import tech.aiflowy.common.ai.ChatSseEmitter;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import tech.aiflowy.common.web.exceptions.BusinessException;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.ArrayList;
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
public class BotServiceImpl extends ServiceImpl<BotMapper, Bot> implements BotService {

    private static final Logger log = LoggerFactory.getLogger(BotServiceImpl.class);

    private static final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Resource
    private BotMessageService botMessageService;

    @Resource(name = "sseThreadPool")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    // 30秒发一次心跳
    private static final long HEARTBEAT_INTERVAL = 30 * 1000L;
    // 心跳消息
    private static final String HEARTBEAT_MESSAGE = "ping";

    @Override
    public Bot getDetail(String id) {
        Bot aiBot = null;

        if (id.matches(RegexUtils.ALL_NUMBER)) {
            aiBot = getById(id);

            if (aiBot == null) {
                aiBot = getByAlias(id);
            }

        } else {
            aiBot = getByAlias(id);
        }

        return aiBot;
    }

    @Override
    public Bot getByAlias(String alias) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq("alias", alias);
        return getOne(queryWrapper);
    }

    @Override
    public SseEmitter startChat(BigInteger botId, ChatModel chatModel, String prompt, MemoryPrompt memoryPrompt, ChatOptions chatOptions, BigInteger conversationId, List<Map<String, String>> messages, UserMessage userMessage) {

        SseEmitter emitter = ChatSseEmitter.create();
        if (messages != null && !messages.isEmpty()) {
            ChatMemory defaultChatMemory = new DefaultBotMessageMemory(conversationId, emitter, messages);
            defaultChatMemory.addMessage(new UserMessage(prompt));
            memoryPrompt.setMemory(defaultChatMemory);
        } else {
            BotMessageMemory memory = new BotMessageMemory(botId, SaTokenUtil.getLoginAccount().getId(), conversationId, botMessageService);
            memoryPrompt.setMemory(memory);
        }

        memoryPrompt.addMessage(userMessage);

        String emitterKey = StpUtil.getLoginIdAsString() + "_" + conversationId;

        emitter.onCompletion(() -> {
            emitters.remove(emitterKey);
            log.debug("SSE连接完成，移除用户[{}]的Emitter", emitterKey);
        });
        emitters.put(emitterKey, emitter);
        log.debug("新增SSE连接，用户[{}]，当前活跃连接数：{}", emitterKey, emitters.size());
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        threadPoolTaskExecutor.execute(() -> {
            ServletRequestAttributes sra = (ServletRequestAttributes) requestAttributes;
            RequestContextHolder.setRequestAttributes(sra, true);
            StreamResponseListener streamResponseListener = new ChatStreamListener(chatModel, memoryPrompt, emitter);
            chatModel.chatStream(memoryPrompt, streamResponseListener);
        });

        return emitter;
    }

    @Override
    public void updateBotLlmId(Bot aiBot) {
        Bot bot = getById(aiBot.getId());

        if (bot == null) {
            log.error("修改bot的llmId失败，bot不存在！");
            throw new BusinessException("bot不存在！");
        }

        bot.setModelId(aiBot.getModelId());

        updateById(bot, false);

    }


    @Override
    public boolean updateById(Bot entity) {
        Bot aiBot = getById(entity.getId());
        if (aiBot == null) {
            throw new BusinessException("bot 不存在");
        }

        CustomBeanUtils.copyPropertiesIgnoreNull(entity, aiBot);

        if ("".equals(aiBot.getAlias())) {
            aiBot.setAlias(null);
        }


        return super.updateById(aiBot, false);
    }


    public static List<BotMessage> parseFrontMessage(List<Map<String, String>> messages) {
        List<BotMessage> newMessages = new ArrayList<>();
        if (messages == null || messages.isEmpty()) {
            return newMessages;
        }
        messages.forEach(item -> {
            BotMessage botMessage = new BotMessage();
            botMessage.setRole(item.get("role"));
            botMessage.setContent(item.get("content"));
            newMessages.add(botMessage);
        });
        return newMessages;
    }
}
