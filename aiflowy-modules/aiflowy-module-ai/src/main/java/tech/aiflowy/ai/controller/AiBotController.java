package tech.aiflowy.ai.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.agentsflex.core.llm.ChatContext;
import com.agentsflex.core.llm.ChatOptions;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.StreamResponseListener;
import com.agentsflex.core.llm.functions.Function;
import com.agentsflex.core.llm.response.AiMessageResponse;
import com.agentsflex.core.llm.response.FunctionCaller;
import com.agentsflex.core.message.AiMessage;
import com.agentsflex.core.message.HumanMessage;
import com.agentsflex.core.message.SystemMessage;
import com.agentsflex.core.prompt.HistoriesPrompt;
import com.agentsflex.core.prompt.TextPrompt;
import com.agentsflex.core.prompt.ToolPrompt;
import com.agentsflex.core.react.ReActAgent;
import com.agentsflex.core.react.ReActAgentListener;
import com.agentsflex.core.react.ReActStep;
import com.agentsflex.core.util.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alicp.jetcache.Cache;
import com.mybatisflex.core.query.QueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tech.aiflowy.ai.entity.*;
import tech.aiflowy.ai.mapper.AiBotConversationMessageMapper;
import tech.aiflowy.ai.service.*;
import tech.aiflowy.ai.utils.AiBotChatUtil;
import tech.aiflowy.ai.utils.AiBotMessageIframeMemory;
import tech.aiflowy.common.ai.ChatManager;
import tech.aiflowy.common.ai.MySseEmitter;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import tech.aiflowy.common.util.Maps;
import tech.aiflowy.common.util.StringUtil;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.web.jsonbody.JsonBody;
import tech.aiflowy.system.entity.SysApiKey;
import tech.aiflowy.system.mapper.SysApiKeyMapper;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 控制层。
 *
 * @author michael
 * @since 2024-08-23
 */
@RestController
@RequestMapping("/api/v1/aiBot")
public class AiBotController extends BaseCurdController<AiBotService, AiBot> {

    private final AiLlmService aiLlmService;
    private final AiBotWorkflowService aiBotWorkflowService;
    private final AiBotKnowledgeService aiBotKnowledgeService;
    private final AiBotMessageService aiBotMessageService;
    @Resource
    private SysApiKeyMapper aiBotApiKeyMapper;
    @Resource
    private AiBotConversationMessageService aiBotConversationMessageService;
    @Resource
    private AiBotConversationMessageMapper aiBotConversationMessageMapper;
    @Resource
    private AiBotService aiBotService;

    @Autowired
    @Qualifier("defaultCache") // 指定 Bean 名称
    private Cache<String, Object> cache;

    private static final Logger logger = LoggerFactory.getLogger(AiBotController.class);

    public AiBotController(AiBotService service, AiLlmService aiLlmService, AiBotWorkflowService aiBotWorkflowService, AiBotKnowledgeService aiBotKnowledgeService, AiBotMessageService aiBotMessageService) {
        super(service);
        this.aiLlmService = aiLlmService;
        this.aiBotWorkflowService = aiBotWorkflowService;
        this.aiBotKnowledgeService = aiBotKnowledgeService;
        this.aiBotMessageService = aiBotMessageService;
    }

    @Resource
    private AiBotPluginsService aiBotPluginsService;
    @Resource
    private AiPluginToolService aiPluginToolService;

    @PostMapping("updateOptions")
    public Result updateOptions(@JsonBody("id") BigInteger id, @JsonBody("options") Map<String, Object> options) {
        AiBot aiBot = service.getById(id);
        Map<String, Object> existOptions = aiBot.getOptions();
        if (existOptions == null) {
            existOptions = new HashMap<>();
        }
        if (options != null) {
            existOptions.putAll(options);
        }
        aiBot.setOptions(existOptions);
        service.updateById(aiBot);
        return Result.success();
    }


    @PostMapping("updateLlmOptions")
    public Result updateLlmOptions(@JsonBody("id") BigInteger id, @JsonBody("llmOptions") Map<String, Object> llmOptions) {
        AiBot aiBot = service.getById(id);
        Map<String, Object> existLlmOptions = aiBot.getLlmOptions();
        if (existLlmOptions == null) {
            existLlmOptions = new HashMap<>();
        }
        if (llmOptions != null) {
            existLlmOptions.putAll(llmOptions);
        }
        aiBot.setLlmOptions(existLlmOptions);
        service.updateById(aiBot);
        return Result.success();
    }

    /**
     * 当前系统用户调用对话
     *
     * @param prompt
     * @param botId
     * @param sessionId
     * @param isExternalMsg
     * @param response
     * @return
     */
    @PostMapping("chat")
    @SaIgnore
    public SseEmitter chat(@JsonBody(value = "prompt", required = true) String prompt,
                           @JsonBody(value = "botId", required = true) BigInteger botId,
                           @JsonBody(value = "sessionId", required = true) String sessionId,
                           @JsonBody(value = "isExternalMsg") int isExternalMsg,
                           @JsonBody(value = "tempUserId") String tempUserId,
                           HttpServletResponse response) {
        response.setContentType("text/event-stream");
        AiBot aiBot = service.getById(botId);
        boolean login = StpUtil.isLogin();

        if (!login) {

            Object o = aiBot.getOptions().get("anonymousEnabled");
            if (o == null) {
                return ChatManager.getInstance().sseEmitterForContent(JSON.toJSONString(Maps.of("content","此bot不支持匿名访问")));
            }

            boolean anonymousEnabled = (boolean) o;
            if (!anonymousEnabled) {
                return ChatManager.getInstance().sseEmitterForContent(JSON.toJSONString(Maps.of("content","此bot不支持匿名访问")));
            }
        }

        if (aiBot == null) {
            return ChatManager.getInstance().sseEmitterForContent("机器人不存在");
        }

        Map<String, Object> llmOptions = aiBot.getLlmOptions();
        String systemPrompt = llmOptions != null ? (String) llmOptions.get("systemPrompt") : null;
        AiLlm aiLlm = aiLlmService.getById(aiBot.getLlmId());

        if (aiLlm == null) {
            return ChatManager.getInstance().sseEmitterForContent("LLM不存在");
        }

        Llm llm = aiLlm.toLlm();

        if (llm == null) {
            return ChatManager.getInstance().sseEmitterForContent("LLM获取为空");
        }
        final HistoriesPrompt historiesPrompt = new HistoriesPrompt();
        if (llmOptions != null && llmOptions.get("maxMessageCount") != null) {
            Object maxMessageCount = llmOptions.get("maxMessageCount");
            historiesPrompt.setMaxAttachedMessageCount(Integer.parseInt(String.valueOf(maxMessageCount)));
        }
        if (StringUtils.hasLength(systemPrompt)) {
            historiesPrompt.setSystemMessage(SystemMessage.of(systemPrompt));
        }
        if (StpUtil.isLogin()){
            AiBotMessageMemory memory = new AiBotMessageMemory(botId, SaTokenUtil.getLoginAccount().getId(),
                    sessionId, isExternalMsg, aiBotMessageService, aiBotConversationMessageMapper,
                    aiBotConversationMessageService);
            historiesPrompt.setMemory(memory);

        } else {
            AiBotMessageIframeMemory memory = new AiBotMessageIframeMemory(botId, tempUserId, sessionId, cache,aiBotConversationMessageService,prompt);
            historiesPrompt.setMemory(memory);

        }
        boolean needEnglishName = AiBotChatUtil.needEnglishName(llm);

        MySseEmitter emitter = new MySseEmitter(1000 * 60 * 300L);
        List<Function> functions = buildFunctionList(Maps.of("botId", botId).set("needEnglishName", needEnglishName));

        ReActAgent reActAgent = new ReActAgent(llm, functions, prompt, historiesPrompt);

        reActAgent.setStreamable(true);
        long userId = StpUtil.getLoginIdAsLong();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Boolean needRefresh = aiBotConversationMessageService.needRefreshConversationTitle(sessionId, prompt, llm, botId, userId, isExternalMsg);
                if (needRefresh){
                    try {
                        emitter.send(SseEmitter.event().name("refreshSession"));
                    } catch (IOException e) {
                        logger.error("创建会话报错", e);
                    }
                }
            }
        }).start();

        reActAgent.addListener(new ReActAgentListener() {

            @Override
            public void onFinalAnswer(String finalAnswer) {
                logger.info("onFinalAnswer,{}",finalAnswer);
                AiMessage message = new AiMessage();
                message.setContent(finalAnswer);
                emitter.sendAndComplete(JSON.toJSONString(message));
            }

            @Override
            public void onNonActionResponseStream(ChatContext context) {
                logger.info("onNonActionResponseStream");
                String fullContent = context.getLastAiMessage().getFullContent();
                AiMessage message   = new AiMessage();
                message.setContent(fullContent);
                emitter.sendAndComplete(JSON.toJSONString(message));
            }

            @Override
            public void onError(Exception error) {
                logger.error("onError:", error);
                AiMessage aiMessage = new AiMessage();
                aiMessage.setContent("大模型调用出错，请检查配置");
                boolean hasUnsupportedApiError = containsUnsupportedApiError(error.getMessage());
                if (hasUnsupportedApiError) {
                    String errMessage = error.getMessage() + "\n**以下是 AIFlowy 提供的可查找当前错误的方向**\n**1: 在 AIFlowy 中，Bot 对话需要大模型携带 function_calling 功能**" +
                            "\n**2: 请查看当前模型是否支持 function_calling 调用？**";
                    aiMessage.setContent(errMessage);
                }
                emitter.send(JSON.toJSONString(aiMessage));
                emitter.completeWithError(error);
            }

            @Override
            public void onActionStart(ReActStep step) {
                logger.info("onActionStart");

                AiMessage thoughtMessage = new AiMessage();
                thoughtMessage.setContent(step.getThought() + "\n");
                emitter.send(JSON.toJSONString(thoughtMessage));

                AiMessage toolCallMessage = new AiMessage();
                toolCallMessage.setContent("\uD83D\uDCCB 正在调用工具，请稍等" + "\n\n");
                toolCallMessage.setFullContent("\uD83D\uDCCB 正在调用工具，请稍等" + "\n\n");
                toolCallMessage.setMetadataMap(Maps.of("showContent",toolCallMessage.getContent()).set("type",0));
                emitter.send(JSON.toJSONString(toolCallMessage));
                historiesPrompt.addMessage(toolCallMessage);
                String actionInput = step.getActionInput();
                logger.info("onActionStart:{}", actionInput);
                // 验证 JSON 格式
                try {
                    JSON.parseObject(actionInput);
                    logger.info("Action Input JSON 格式正确");
                } catch (Exception e) {
                    logger.error("Action Input JSON 格式错误: " + actionInput);
                    logger.info("开始json矫正");
                    // 调用大模型矫正 JSON 格式
                    StringBuilder prompt = new StringBuilder();
                    prompt.append("你是一个 JSON 格式矫正专家。请修复下面的 JSON 格式错误。\n\n");
                    prompt.append("## 需要修复的 JSON:\n");
                    prompt.append("```\n");
                    prompt.append(actionInput);
                    prompt.append("\n```\n\n");
                    prompt.append("## 修复要求:\n");
                    prompt.append("1. 输出标准的 JSON 格式，必须以 { 开始，以 } 结束\n");
                    prompt.append("2. 所有键名和字符串值必须用双引号包围\n");
                    prompt.append("3. 移除多余的逗号、括号或其他语法错误\n");
                    prompt.append("4. 保持原有的参数名和参数值不变，只修复格式\n");
                    prompt.append("5. 不要添加任何解释或注释\n\n");
                    prompt.append("## 常见错误修复示例:\n");
                    prompt.append("错误: {sql: 'SELECT * FROM users'}\n");
                    prompt.append("正确: {\"sql\": \"SELECT * FROM users\"}\n\n");
                    prompt.append("错误: {\"name\": \"test\",}\n");
                    prompt.append("正确: {\"name\": \"test\"}\n\n");
                    prompt.append("错误: {'key': \"value\"}\n");
                    prompt.append("正确: {\"key\": \"value\"}\n\n");
                    prompt.append("请直接输出修复后的 JSON，不要包含任何其他内容:");

                    logger.info("----------------------------矫正提示词-------------------------------");
                    logger.info(prompt.toString());
                    logger.info("--------------------------------------------------------------------");
                    AiMessageResponse chat = llm.chat(new TextPrompt(prompt.toString()));
                    AiMessage message = chat.getMessage();
                    String replace = message.getContent().replace("```json", "").replace("```", "");
                    logger.info("校正后json:" + replace);
                    if (StringUtils.hasLength(replace)) {
                        step.setActionInput(replace);
                    }
                }
            }

            @Override
            public void onActionEnd(ReActStep step, Object result) {
                logger.info("onActionEnd----> step:{},result:{}", step, result);
                AiMessage aiMessage =  new AiMessage();
                aiMessage.setFullContent("\uD83D\uDD0D Query Result:" + result.toString() + "\n\n");
                aiMessage.setContent("\uD83D\uDD0D Query Result:" + result.toString() + "\n\n");
                aiMessage.setMetadataMap(Maps.of("showContent",aiMessage.getContent()).set("type",0));
                historiesPrompt.addMessage(aiMessage);
                emitter.send(JSON.toJSONString(aiMessage));
            }
        });

        reActAgent.run();

        return emitter;
    }

    /**
     * 外部用户调用智能体进行对话
     * 需要用户传 apiKey 对用户进行身份验证
     *
     * @param stream [true: 返回sse false： 返回json
     * @return
     */
    @SaIgnore
    @PostMapping("externalChat")
    public Object externalChat(
            @JsonBody(value = "messages", required = true) List<AiBotMessage> messages,
            @JsonBody(value = "botId", required = true) BigInteger botId,
            @JsonBody(value = "stream", required = false) boolean stream,
            HttpServletResponse response,
            HttpServletRequest request
    ) {
        // 设置响应类型
        if (stream) {
            response.setContentType("text/event-stream");
        } else {
            response.setContentType("application/json");
        }

        // 获取 API Key 和 Bot 信息
        String apiKey = request.getHeader("Authorization");
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select("api_key", "status", "expired_at")
                .from("tb_sys_api_key")
                .where("api_key = ? ", apiKey);
        SysApiKey aiBotApiKey = aiBotApiKeyMapper.selectOneByQuery(queryWrapper);
        if (aiBotApiKey == null) {
            return createResponse(stream, JSON.toJSONString(errorRespnseMsg(1, "该apiKey不存在")));
        }
        if (aiBotApiKey.getStatus() == 0) {
            return createResponse(stream, JSON.toJSONString(errorRespnseMsg(2, "该apiKey未启用")));
        }

        if (aiBotApiKey.getExpiredAt().getTime() < new Date().getTime()) {
            return createResponse(stream, JSON.toJSONString(errorRespnseMsg(3, "该apiKey已失效")));

        }

        AiBot aiBot = service.getById(botId);
        if (aiBot == null) {
            return createResponse(stream, JSON.toJSONString(errorRespnseMsg(4, "机器人不存在")));
        }

        Map<String, Object> llmOptions = aiBot.getLlmOptions();
        String systemPrompt = llmOptions != null ? (String) llmOptions.get("systemPrompt") : null;

        AiLlm aiLlm = aiLlmService.getById(aiBot.getLlmId());
        if (aiLlm == null) {
            return createResponse(stream, JSON.toJSONString(errorRespnseMsg(5, "LLM不存在")));
        }

        Llm llm = aiLlm.toLlm();
        AiBotExternalMessageMemory messageMemory = new AiBotExternalMessageMemory(messages);
        HistoriesPrompt historiesPrompt = new HistoriesPrompt();
        if (llmOptions != null && llmOptions.get("maxMessageCount") != null) {
            Object maxMessageCount = llmOptions.get("maxMessageCount");
            historiesPrompt.setMaxAttachedMessageCount(Integer.parseInt(String.valueOf(maxMessageCount)));
        }
        if (systemPrompt != null) {
            historiesPrompt.setSystemMessage(SystemMessage.of(systemPrompt));
        }
        historiesPrompt.setMemory(messageMemory);

        String prompt = messages.get(messages.size() - 1).getContent();
        boolean needEnglishName = AiBotChatUtil.needEnglishName(llm);

        HumanMessage humanMessage = new HumanMessage();

        // 添加插件、工作流、知识库相关的 Function Calling
        appendPluginToolFunction(botId, humanMessage);
        appendWorkflowFunctions(botId, humanMessage, needEnglishName);
        appendKnowledgeFunctions(botId, humanMessage, needEnglishName);

        historiesPrompt.addMessage(humanMessage);
        ChatOptions chatOptions = getChatOptions(llmOptions);
        // 根据 responseType 返回不同的响应
        if (stream) {
            MySseEmitter emitter = new MySseEmitter((long) (1000 * 60 * 2));
            final Boolean[] needClose = {true};

            if (humanMessage.getFunctions() != null && !humanMessage.getFunctions().isEmpty()) {
                try {
                    AiMessageResponse aiMessageResponse = llm.chat(historiesPrompt,chatOptions);
                    function_call(aiMessageResponse, emitter, needClose, historiesPrompt, llm, prompt, true,chatOptions);
                } catch (Exception e) {
                    emitter.completeWithError(e);
                }

                if (needClose[0]) {
                    System.out.println("function chat complete");
                    emitter.complete();
                }
            } else {
                llm.chatStream(historiesPrompt, new StreamResponseListener() {
                    @Override
                    public void onMessage(ChatContext context, AiMessageResponse response) {
                        try {
                            function_call(response, emitter, needClose, historiesPrompt, llm, prompt, true,chatOptions);
                        } catch (Exception e) {
                            emitter.completeWithError(e);
                        }
                    }

                    @Override
                    public void onStop(ChatContext context) {

                        if (needClose[0]) {
                            System.out.println("normal chat complete");
                            emitter.complete();
                        }
                    }

                    @Override
                    public void onFailure(ChatContext context, Throwable throwable) {
                        emitter.completeWithError(throwable);
                    }
                },chatOptions);
            }

            return emitter;
        } else {
            AiMessageResponse resultFunctionCall;
            if (humanMessage.getFunctions() != null && !humanMessage.getFunctions().isEmpty()) {
                try {
                    AiMessageResponse aiMessageResponse = llm.chat(historiesPrompt,chatOptions);
                    resultFunctionCall = jsonResultJsonFunctionCall(aiMessageResponse, historiesPrompt, llm, prompt,chatOptions);
                    return JSON.toJSONString(resultFunctionCall.getMessage(), new SerializeConfig());
                } catch (Exception e) {
                    return createErrorResponse(e);
                }
            } else {
                AiMessageResponse messageResponse = llm.chat(historiesPrompt,chatOptions);
                resultFunctionCall = jsonResultJsonFunctionCall(messageResponse, historiesPrompt, llm, prompt,chatOptions);
                AiBotExternalMsgJsonResult result = handleMessageResult(resultFunctionCall.getMessage());
                return JSON.toJSONString(result, new SerializeConfig());
            }
        }
    }

    private AiBotExternalMsgJsonResult handleMessageResult(AiMessage aiMessage) {
        AiBotExternalMsgJsonResult messageResult = new AiBotExternalMsgJsonResult();
        messageResult.setCreated(new Date().getTime());
        AiBotExternalMsgJsonResult.Usage usage = new AiBotExternalMsgJsonResult.Usage();
        if (aiMessage.getTotalTokens() != null){
            usage.setTotalTokens(aiMessage.getTotalTokens());
        }
        if (aiMessage.getCompletionTokens() != null){
            usage.setCompletionTokens(aiMessage.getCompletionTokens());
        }
        if (aiMessage.getPromptTokens() != null){
            usage.setPromptTokens(aiMessage.getPromptTokens());
        }
        messageResult.setUsage(usage);
        AiBotExternalMsgJsonResult.Choice choice = new AiBotExternalMsgJsonResult.Choice();
        AiBotExternalMsgJsonResult.Message message = new AiBotExternalMsgJsonResult.Message();
        message.setContent(aiMessage.getContent());
        message.setRole("assistant");
        choice.setMessage(message);
        messageResult.setChoices(choice);
        messageResult.setStatus(aiMessage.getStatus().name());
        return messageResult;
    }

    // 辅助方法：创建响应
    private Object createResponse(boolean stream, String content) {
        if (stream) {
            MySseEmitter emitter = new MySseEmitter((long) (1000 * 60 * 2));
            emitter.send(content);
            emitter.complete();
            return emitter;
        } else {
            return ResponseEntity.ok(content);
        }
    }

    // 辅助方法：创建错误响应
    private Object createErrorResponse(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    /**
     * @param aiMessageResponse 大模型返回的消息
     * @param emitter
     * @param needClose         是否需要关闭流
     * @param historiesPrompt   消息历史记录
     * @param llm               大模型
     * @param prompt            提示词
     * @param isExternalChatApi true 外部系统调用bot  false 内部系统调用bot
     */
    private String function_call(AiMessageResponse aiMessageResponse,
                                 MySseEmitter emitter,
                                 Boolean[] needClose,
                                 HistoriesPrompt historiesPrompt,
                                 Llm llm,
                                 String prompt,
                                 boolean isExternalChatApi,
                                 ChatOptions chatOptions) {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        RequestContextHolder.setRequestAttributes(sra, true);
        String content = aiMessageResponse.getMessage().getContent();
        Object messageContent = aiMessageResponse.getMessage();
        if (StringUtil.hasText(content)) {
            // 如果是外部系统调用chat
            if (isExternalChatApi) {
                AiBotExternalMsgJsonResult result = handleMessageStreamJsonResult(aiMessageResponse.getMessage());

                emitter.send(JSON.toJSONString(result, new SerializeConfig()));
            } else {
                emitter.send(JSON.toJSONString(messageContent));
            }

        }
        System.out.println("function call 接收到的参数message：" + aiMessageResponse);
        llm.chatStream(ToolPrompt.of(aiMessageResponse), new StreamResponseListener() {
            @Override
            public void onMessage(ChatContext context, AiMessageResponse response) {
                System.out.println("function call <UNK>message<UNK>" + aiMessageResponse);
                String content = response.getMessage().getContent();
                if (StringUtil.hasText(content)) {
                    System.out.println("if content"  + content);
                    emitter.send(JSON.toJSONString(response.getMessage()));
                }
            }

            @Override
            public void onStop(ChatContext context) {
                AiMessage lastAiMessage = context.getLastAiMessage();
                if (lastAiMessage != null) {
                    historiesPrompt.addMessage(lastAiMessage);
                }
                System.out.println(lastAiMessage);
                System.out.println("function call complete");
                emitter.complete();
            }

            @Override
            public void onFailure(ChatContext context, Throwable throwable) {
                logger.error("function_call报错:",throwable);
                AiMessage aiMessage = new AiMessage();
                aiMessage.setContent("未查询到相关信息...");
                emitter.send(JSON.toJSONString(aiMessage));
                System.out.println("function call complete with error");
            }
        },chatOptions);

        return JSON.toJSONString(messageContent);
    }

    @GetMapping("getDetail")
    @SaIgnore
    public Result getDetail(String id) {
        return aiBotService.getDetail(id);
    }

    @Override
    protected Result onSaveOrUpdateBefore(AiBot entity, boolean isSave) {
        if (isSave) {
            // 设置默认值
            entity.setLlmOptions(getDefaultLlmOptions());
        }
        return super.onSaveOrUpdateBefore(entity, isSave);
    }

    private ChatOptions getChatOptions(Map<String, Object> llmOptions) {
        ChatOptions defaultOptions = new ChatOptions();
        if (llmOptions != null) {
            Object topK = llmOptions.get("topK");
            Object maxReplyLength = llmOptions.get("maxReplyLength");
            Object temperature = llmOptions.get("temperature");
            Object topP = llmOptions.get("topP");
            if (topK != null) {
                defaultOptions.setTopK(Integer.parseInt(String.valueOf(topK)));
            }
            if (maxReplyLength != null) {
                defaultOptions.setMaxTokens(Integer.parseInt(String.valueOf(maxReplyLength)));
            }
            if (temperature != null) {
                defaultOptions.setTemperature(Float.parseFloat(String.valueOf(temperature)));
            }
            if (topP != null) {
                defaultOptions.setTopP(Float.parseFloat(String.valueOf(topP)));
            }
        }
        return defaultOptions;
    }

    private Map<String, Object> getDefaultLlmOptions() {
        Map<String, Object> defaultLlmOptions = new HashMap<>();
        defaultLlmOptions.put("temperature", 0.7);
        defaultLlmOptions.put("topK", 4);
        defaultLlmOptions.put("maxReplyLength", 2048);
        defaultLlmOptions.put("topP", 0.7);
        defaultLlmOptions.put("maxMessageCount", 3);
        return defaultLlmOptions;
    }

    private Map<String, Object> errorRespnseMsg(int errorCode, String message) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("error", errorCode);
        result.put("message", message);
        return result;
    }

    private AiBotExternalMsgJsonResult handleMessageStreamJsonResult(AiMessage message) {
        AiBotExternalMsgJsonResult result = new AiBotExternalMsgJsonResult();
        AiBotExternalMsgJsonResult.Choice choice = new AiBotExternalMsgJsonResult.Choice();
        AiBotExternalMsgJsonResult.Delta delta = new AiBotExternalMsgJsonResult.Delta();
        delta.setRole("assistant");
        delta.setContent(message.getContent());
        choice.setDelta(delta);
        result.setCreated(new Date().getTime());
        result.setChoices(choice);
        result.setStatus(message.getStatus().name());

        return result;
    }

    private AiMessageResponse jsonResultJsonFunctionCall(AiMessageResponse aiMessageResponse,
                                                         HistoriesPrompt historiesPrompt,
                                                         Llm llm,
                                                         String prompt,
                                                         ChatOptions chatOptions) {
        List<FunctionCaller> functionCallers = aiMessageResponse.getFunctionCallers();
        if (CollectionUtil.hasItems(functionCallers)) {
            for (FunctionCaller functionCaller : functionCallers) {
                Object result = functionCaller.call();
                if (ObjectUtil.isNotEmpty(result)) {
                    String newPrompt = "请根据以下内容回答用户，内容是:\n" + result + "\n 用户的问题是：" + prompt;
                    historiesPrompt.addMessageTemporary(new HumanMessage(newPrompt));
                    return llm.chat(historiesPrompt,chatOptions);
                }
            }
        }
        return aiMessageResponse;
    }


    private List<Function> buildFunctionList(Map<String,Object> buildParams){

        if (buildParams == null || buildParams.isEmpty()) {
            throw new IllegalArgumentException("buildParams is empty");
        }

        List<Function> functionList = new ArrayList<>();

        BigInteger botId = (BigInteger) buildParams.get("botId");
        if (botId == null) {
            throw new IllegalArgumentException("botId is empty");
        }
        Boolean needEnglishName = (Boolean) buildParams.get("needEnglishName");
        if (needEnglishName == null) {
            needEnglishName = false;
        }

        QueryWrapper queryWrapper = QueryWrapper.create();

        // 工作流 function 集合
        queryWrapper.eq(AiBotWorkflow::getBotId,botId);
        List<AiBotWorkflow> aiBotWorkflows = aiBotWorkflowService.getMapper().selectListWithRelationsByQuery(queryWrapper);
        if (aiBotWorkflows != null &&  !aiBotWorkflows.isEmpty()) {
            for (AiBotWorkflow aiBotWorkflow : aiBotWorkflows) {
                Function function = aiBotWorkflow.getWorkflow().toFunction(needEnglishName);
                functionList.add(function);
            }
        }

        // 知识库 function 集合
        queryWrapper =  QueryWrapper.create();
        queryWrapper.eq(AiBotKnowledge::getBotId,botId);
        List<AiBotKnowledge> aiBotKnowledges = aiBotKnowledgeService.getMapper().selectListWithRelationsByQuery(queryWrapper);
        if (aiBotKnowledges != null && !aiBotKnowledges.isEmpty()) {
            for (AiBotKnowledge aiBotKnowledge : aiBotKnowledges) {
                Function function = aiBotKnowledge.getKnowledge().toFunction(needEnglishName);
                functionList.add(function);
            }
        }

        // 插件 function 集合
        queryWrapper = QueryWrapper.create();
        queryWrapper.select("plugin_tool_id").eq(AiBotPlugins::getBotId, botId);
        List<BigInteger> pluginToolIds = aiBotPluginsService.getMapper().selectListWithRelationsByQueryAs(queryWrapper, BigInteger.class);

        if (pluginToolIds == null || pluginToolIds.isEmpty()) {
            return functionList;
        }

        QueryWrapper queryTool = QueryWrapper.create()
                .select("*")
                .from("tb_ai_plugin_tool")
                .in("id", pluginToolIds);
        List<AiPluginTool> aiPluginTools = aiPluginToolService.getMapper().selectListWithRelationsByQuery(queryTool);
        if (aiPluginTools != null && !aiPluginTools.isEmpty()) {
            for (AiPluginTool aiPluginTool : aiPluginTools){
                functionList.add(aiPluginTool.toFunction());
            }
        }


        return functionList;
    }

    private void appendWorkflowFunctions(BigInteger botId, HumanMessage humanMessage, boolean needEnglishName) {
        QueryWrapper queryWrapper = QueryWrapper.create().eq(AiBotWorkflow::getBotId, botId);
        List<AiBotWorkflow> aiBotWorkflows = aiBotWorkflowService.getMapper().selectListWithRelationsByQuery(queryWrapper);
        if (aiBotWorkflows != null) {
            for (AiBotWorkflow aiBotWorkflow : aiBotWorkflows) {
                Function function = aiBotWorkflow.getWorkflow().toFunction(needEnglishName);
                humanMessage.addFunction(function);
            }
        }
    }

    private void appendKnowledgeFunctions(BigInteger botId, HumanMessage humanMessage, boolean needEnglishName) {
        QueryWrapper queryWrapper = QueryWrapper.create().eq(AiBotKnowledge::getBotId, botId);
        List<AiBotKnowledge> aiBotKnowledges = aiBotKnowledgeService.getMapper().selectListWithRelationsByQuery(queryWrapper);
        if (aiBotKnowledges != null) {
            for (AiBotKnowledge aiBotKnowledge : aiBotKnowledges) {
                Function function = aiBotKnowledge.getKnowledge().toFunction(needEnglishName);
                humanMessage.addFunction(function);
            }
        }
    }

    private void appendPluginToolFunction(BigInteger botId, HumanMessage humanMessage) {
        QueryWrapper queryWrapper = QueryWrapper.create().select("plugin_tool_id").eq(AiBotPlugins::getBotId, botId);
        List<BigInteger> pluginToolIds = aiBotPluginsService.getMapper().selectListWithRelationsByQueryAs(queryWrapper, BigInteger.class);

        if (pluginToolIds == null || pluginToolIds.isEmpty()) {
            return;
        }

        QueryWrapper queryTool = QueryWrapper.create()
                .select("*")
                .from("tb_ai_plugin_tool")
                .in("id", pluginToolIds);
        List<AiPluginTool> aiPluginTools = aiPluginToolService.getMapper().selectListWithRelationsByQuery(queryTool);
        for (AiPluginTool item : aiPluginTools) {
            humanMessage.addFunction(item.toFunction());
        }


    }

    private boolean containsUnsupportedApiError(String message) {
        if (message == null) {
            return false;
        }
        // 检查是否包含"暂不支持该接口"或其他相关关键词
        return message.contains("暂不支持该接口") ||
                message.contains("不支持接口") ||
                message.contains("接口不支持") ||
                message.contains("The tool call is not supported")
                ;
    }
}
