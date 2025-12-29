
package tech.aiflowy.usercenter.controller.ai;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.agentsflex.core.message.SystemMessage;
import com.agentsflex.core.message.UserMessage;
import com.agentsflex.core.model.chat.ChatModel;
import com.agentsflex.core.model.chat.ChatOptions;
import com.agentsflex.core.model.chat.tool.Tool;
import com.agentsflex.core.prompt.MemoryPrompt;
import com.alicp.jetcache.Cache;
import com.mybatisflex.core.keygen.impl.SnowFlakeIDKeyGenerator;
import com.mybatisflex.core.query.QueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tech.aiflowy.ai.entity.*;
import tech.aiflowy.ai.mapper.BotConversationMapper;
import tech.aiflowy.ai.service.*;
import tech.aiflowy.common.annotation.UsePermission;
import tech.aiflowy.common.audio.core.AudioServiceManager;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.satoken.util.SaTokenUtil;
import tech.aiflowy.common.util.MapUtil;
import tech.aiflowy.common.util.Maps;
import tech.aiflowy.common.util.SSEUtil;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.common.web.exceptions.BusinessException;
import tech.aiflowy.common.web.jsonbody.JsonBody;
import tech.aiflowy.core.chat.protocol.sse.ChatSseEmitter;
import tech.aiflowy.core.chat.protocol.sse.ChatSseUtil;
import tech.aiflowy.system.mapper.SysApiKeyMapper;

import javax.annotation.Resource;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;

import static tech.aiflowy.ai.entity.table.BotPluginTableDef.BOT_PLUGIN;
import static tech.aiflowy.ai.entity.table.PluginItemTableDef.PLUGIN_ITEM;

/**
 * 控制层。
 *
 * @author michael
 * @since 2024-08-23
 */
@RestController
@RequestMapping("/userCenter/bot")
@UsePermission(moduleName = "/api/v1/bot")
public class UcBotController extends BaseCurdController<BotService, Bot> {

    private final ModelService modelService;
    private final BotWorkflowService botWorkflowService;
    private final BotDocumentCollectionService botDocumentCollectionService;
    private final BotMessageService botMessageService;
    @Resource
    private SysApiKeyMapper aiBotApiKeyMapper;
    @Resource
    private BotConversationService botConversationService;
    @Resource
    private BotConversationMapper botConversationMapper;
    @Resource
    private BotService botService;
    @Autowired
    @Qualifier("defaultCache") // 指定 Bean 名称
    private Cache<String, Object> cache;
    @Resource
    private AudioServiceManager audioServiceManager;

    private static final Logger logger = LoggerFactory.getLogger(UcBotController.class);

    public UcBotController(BotService service, ModelService modelService, BotWorkflowService botWorkflowService,
                           BotDocumentCollectionService botDocumentCollectionService, BotMessageService botMessageService) {
        super(service);
        this.modelService = modelService;
        this.botWorkflowService = botWorkflowService;
        this.botDocumentCollectionService = botDocumentCollectionService;
        this.botMessageService = botMessageService;
    }

    @Resource
    private BotPluginService botPluginService;
    @Resource
    private PluginItemService pluginItemService;
    @Resource
    private BotConversationService conversationMessageService;

    @GetMapping("/generateConversationId")
    public Result<Long> generateConversationId() {
        long nextId = new SnowFlakeIDKeyGenerator().nextId();
        return Result.ok(nextId);
    }

    @PostMapping("updateOptions")
    @SaCheckPermission("/api/v1/bot/save")
    public Result<Void> updateOptions(@JsonBody("id") BigInteger id,
                                      @JsonBody("options") Map<String, Object> options) {
        Bot aiBot = service.getById(id);
        Map<String, Object> existOptions = aiBot.getOptions();
        if (existOptions == null) {
            existOptions = new HashMap<>();
        }
        if (options != null) {
            existOptions.putAll(options);
        }
        aiBot.setOptions(existOptions);
        service.updateById(aiBot);
        return Result.ok();
    }

    @PostMapping("updateLlmOptions")
    @SaCheckPermission("/api/v1/bot/save")
    public Result<Void> updateLlmOptions(@JsonBody("id")
                                         BigInteger id, @JsonBody("llmOptions")
                                         Map<String, Object> llmOptions) {
        Bot aiBot = service.getById(id);
        Map<String, Object> existLlmOptions = aiBot.getModelOptions();
        if (existLlmOptions == null) {
            existLlmOptions = new HashMap<>();
        }
        if (llmOptions != null) {
            existLlmOptions.putAll(llmOptions);
        }
        aiBot.setModelOptions(existLlmOptions);
        service.updateById(aiBot);
        return Result.ok();
    }

    @PostMapping("voiceInput")
    @SaIgnore
    public Result<String> voiceInput(@RequestParam("audio")
                                     MultipartFile audioFile) {

        String recognize = null;
        try {
            recognize = audioServiceManager.audioToText(audioFile.getInputStream());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return Result.ok("", recognize);
    }

    /**
     * 处理聊天请求的接口方法
     *
     * @param prompt    用户输入的聊天内容，必须提供
     * @param botId     聊天机器人的唯一标识符，必须提供
     * @param conversationId 会话ID，用于标识当前对话会话，必须提供
     * @param messages  历史消息，用于提供上下文，可选
     * @return 返回SseEmitter对象，用于服务器向客户端推送聊天响应数据
     */
    @PostMapping("chat")
    @SaIgnore
    public SseEmitter chat(
            @JsonBody(value = "prompt", required = true) String prompt,
            @JsonBody(value = "botId", required = true) BigInteger botId,
            @JsonBody(value = "conversationId", required = true) BigInteger conversationId,
            @JsonBody(value = "messages") List<Map<String, String>> messages

    ) {

        if (!StringUtils.hasLength(prompt)) {
            throw new BusinessException("提示词不能为空！");
        }
        String conversationIdStr = conversationId.toString();
        Bot aiBot = service.getById(botId);
        if (aiBot == null) {
            return ChatSseUtil.sendSystemError(conversationIdStr, "机器人不存在");

        }

        boolean login = StpUtil.isLogin();
        if (!login && !aiBot.isAnonymousEnabled()) {
            return ChatSseUtil.sendSystemError(conversationIdStr, "此bot不支持匿名访问");
        }

        Map<String, Object> llmOptions = aiBot.getModelOptions();
        String systemPrompt = MapUtil.getString(llmOptions, "systemPrompt");
        Model model = modelService.getModelInstance(aiBot.getModelId());
        if (model == null) {
            return ChatSseUtil.sendSystemError(conversationIdStr, "模型不存在，请检查配置");
        }

        ChatModel chatModel = model.toChatModel();
        if (chatModel == null) {
           return ChatSseUtil.sendSystemError(conversationIdStr, "对话模型获取失败，请检查配置");
        }

        final MemoryPrompt memoryPrompt = new MemoryPrompt();
        Integer maxMessageCount = MapUtil.getInteger(llmOptions, "maxMessageCount");
        if (maxMessageCount != null) {
            memoryPrompt.setMaxAttachedMessageCount(maxMessageCount);
        }

        if (StringUtils.hasLength(systemPrompt)) {
            memoryPrompt.setSystemMessage(SystemMessage.of(systemPrompt));
        }

        UserMessage userMessage = new UserMessage(prompt);
        userMessage.addTools(buildFunctionList(Maps.of("botId", botId).set("needEnglishName", false)));

        ChatOptions chatOptions = getChatOptions(llmOptions);

        BotConversation conversation = conversationMessageService.getById(conversationId);
        if (conversation == null) {
            conversation = new BotConversation();
            conversation.setId(conversationId);
            if (prompt.length() > 200) {
                conversation.setTitle(prompt.substring(0, 200));
            } else {
                conversation.setTitle(prompt);
            }
            conversation.setBotId(botId);
            conversation.setAccountId(SaTokenUtil.getLoginAccount().getId());
            commonFiled(conversation, SaTokenUtil.getLoginAccount().getId(), SaTokenUtil.getLoginAccount().getTenantId(), SaTokenUtil.getLoginAccount().getDeptId());
            conversationMessageService.save(conversation);
        }

        return botService.startChat(botId, chatModel, prompt, memoryPrompt, chatOptions, conversationId, messages, userMessage);

    }

    @PostMapping("updateLlmId")
    @SaCheckPermission("/api/v1/bot/save")
    public Result<Void> updateBotLlmId(@RequestBody
                                       Bot aiBot) {
        service.updateBotLlmId(aiBot);
        return Result.ok();
    }

    @GetMapping("getDetail")
    @SaIgnore
    public Result<Bot> getDetail(String id) {
        return Result.ok(botService.getDetail(id));
    }

    @Override
    @SaIgnore
    public Result<Bot> detail(String id) {
        Bot data = botService.getDetail(id);
        if (data == null) {
            return Result.ok(data);
        }

        Map<String, Object> llmOptions = data.getModelOptions();
        if (llmOptions == null) {
            llmOptions = new HashMap<>();
        }

        if (data.getModelId() == null) {
            return Result.ok(data);
        }

        BigInteger llmId = data.getModelId();
        Model llm = modelService.getById(llmId);

        if (llm == null) {
            data.setModelId(null);
            return Result.ok(data);
        }

        Map<String, Object> options = llm.getOptions();

        if (options != null && !options.isEmpty()) {

            // 获取是否多模态
            Boolean multimodal = (Boolean) options.get("multimodal");
            llmOptions.put("multimodal", multimodal != null && multimodal);

        }

        return Result.ok(data);
    }

    @Override
    protected Result<?> onSaveOrUpdateBefore(Bot entity, boolean isSave) {

        String alias = entity.getAlias();

        if (StringUtils.hasLength(alias)) {
            Bot aiBot = service.getByAlias(alias);


            if (aiBot != null && isSave) {
                throw new BusinessException("别名已存在！");
            }

            if (aiBot != null && aiBot.getId().compareTo(entity.getId()) != 0) {
                throw new BusinessException("别名已存在！");
            }

        }


        if (isSave) {
            // 设置默认值
            entity.setModelOptions(getDefaultLlmOptions());
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
            Object thinkingEnabled = llmOptions.get("thinkingEnabled");

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
            if (thinkingEnabled != null) {
                defaultOptions.setThinkingEnabled(Boolean.parseBoolean(String.valueOf(thinkingEnabled)));
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
        defaultLlmOptions.put("maxMessageCount", 10);
        return defaultLlmOptions;
    }

    private Map<String, Object> errorRespnseMsg(int errorCode, String message) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("error", errorCode);
        result.put("message", message);
        return result;
    }

    @Override
    protected Result<?> onRemoveBefore(Collection<Serializable> ids) {
        QueryWrapper queryWrapperKnowledge = QueryWrapper.create().in(BotDocumentCollection::getBotId, ids);
        botDocumentCollectionService.remove(queryWrapperKnowledge);
        QueryWrapper queryWrapperBotWorkflow = QueryWrapper.create().in(BotWorkflow::getBotId, ids);
        botWorkflowService.remove(queryWrapperBotWorkflow);
        QueryWrapper queryWrapperBotPlugins = QueryWrapper.create().in(BotPlugin::getBotId, ids);
        botPluginService.remove(queryWrapperBotPlugins);
        return super.onRemoveBefore(ids);
    }

    private List<Tool> buildFunctionList(Map<String, Object> buildParams) {

        if (buildParams == null || buildParams.isEmpty()) {
            throw new IllegalArgumentException("buildParams is empty");
        }

        List<Tool> functionList = new ArrayList<>();

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
        queryWrapper.eq(BotWorkflow::getBotId, botId);
        List<BotWorkflow> botWorkflows = botWorkflowService.getMapper()
                .selectListWithRelationsByQuery(queryWrapper);
        if (botWorkflows != null && !botWorkflows.isEmpty()) {
            for (BotWorkflow botWorkflow : botWorkflows) {
                Tool function = botWorkflow.getWorkflow().toFunction(needEnglishName);
                functionList.add(function);
            }
        }

        // 知识库 function 集合
        queryWrapper = QueryWrapper.create();
        queryWrapper.eq(BotDocumentCollection::getBotId, botId);
        List<BotDocumentCollection> botDocumentCollections = botDocumentCollectionService.getMapper()
                .selectListWithRelationsByQuery(queryWrapper);
        if (botDocumentCollections != null && !botDocumentCollections.isEmpty()) {
            for (BotDocumentCollection botDocumentCollection : botDocumentCollections) {
                Tool function = botDocumentCollection.getKnowledge().toFunction(needEnglishName);
                functionList.add(function);
            }
        }

        // 插件 function 集合
        queryWrapper = QueryWrapper.create();
        queryWrapper.select(BOT_PLUGIN.PLUGIN_ITEM_ID).eq(BotPlugin::getBotId, botId);
        List<BigInteger> pluginToolIds = botPluginService.getMapper()
                .selectListWithRelationsByQueryAs(queryWrapper, BigInteger.class);
        if (pluginToolIds != null && !pluginToolIds.isEmpty()) {
            QueryWrapper queryTool = QueryWrapper.create()
                    .select(PLUGIN_ITEM.ALL_COLUMNS)
                    .from(PLUGIN_ITEM)
                    .where(PLUGIN_ITEM.ID.in(pluginToolIds));
            List<PluginItem> pluginItems = pluginItemService.getMapper().selectListWithRelationsByQuery(queryTool);
            if (pluginItems != null && !pluginItems.isEmpty()) {
                for (PluginItem pluginItem : pluginItems) {
                    functionList.add(pluginItem.toFunction());
                }
            }
        }


        return functionList;
    }
}
