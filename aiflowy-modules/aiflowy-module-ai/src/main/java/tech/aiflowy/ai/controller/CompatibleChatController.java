
package tech.aiflowy.ai.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.agentsflex.core.llm.ChatOptions;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.functions.Function;
import com.agentsflex.core.llm.functions.Parameter;
import com.agentsflex.core.llm.response.FunctionCaller;
import com.agentsflex.core.prompt.Prompt;
import com.agentsflex.core.prompt.TextPrompt;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tech.aiflowy.ai.entity.AiBot;
import tech.aiflowy.ai.entity.AiBotKnowledge;
import tech.aiflowy.ai.entity.AiLlm;
import tech.aiflowy.ai.entity.AiPluginTool;
import tech.aiflowy.ai.entity.AiWorkflow;
import tech.aiflowy.ai.entity.openAi.error.OpenAiErrorResponse;
import tech.aiflowy.ai.entity.openAi.request.ChatMessage;
import tech.aiflowy.ai.entity.openAi.request.OpenAiChatRequest;
import tech.aiflowy.ai.entity.openAi.response.UnifiedLlmResponse;
import tech.aiflowy.ai.service.*;
import tech.aiflowy.common.ai.MySseEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import tech.aiflowy.ai.entity.AiKnowledge;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.ai.entity.AiBotPlugins;
import tech.aiflowy.ai.entity.AiBotWorkflow;
import com.agentsflex.core.llm.ChatContext;
import com.agentsflex.core.llm.StreamResponseListener;
import com.agentsflex.core.llm.response.AiMessageResponse;
import tech.aiflowy.common.util.Maps;
import tech.aiflowy.ai.entity.openAi.response.PlatformType;
import tech.aiflowy.ai.entity.openAi.response.ResponseConverter;
import com.alibaba.fastjson2.JSON;
import org.slf4j.LoggerFactory;

/**
 * 兼容 openAi api 的，调用 bot 的控制器
 */
@RestController
@SaIgnore
public class CompatibleChatController {

    private final Logger logger = LoggerFactory.getLogger(CompatibleChatController.class);

    @Resource
    private AiBotApiKeyService aiBotApiKeyService;

    @Resource
    private AiBotService aiBotService;

    @Resource
    private AiBotLlmService aiBotLlmService;

    @Resource
    private AiLlmService aiLlmService;

    @Resource
    private AiBotPluginsService aiBotPluginsService;

    @Resource
    private AiPluginToolService aiPluginToolService;

    @Resource
    private AiBotKnowledgeService aiBotKnowledgeService;;

    @Resource
    private AiKnowledgeService aiKnowledgeService;

    @Resource
    private AiBotWorkflowService aiBotWorkflowService;

    @Resource
    private AiWorkflowService aiWorkflowService;

    @PostMapping("/v1/chat/completions")
    public Object chat(@RequestBody
    OpenAiChatRequest params, HttpServletRequest request, HttpServletResponse response) {

        // 校验 apiKey
        String authorization = request.getHeader("Authorization");

        if (!StringUtils.hasLength(authorization)) {
            return new OpenAiErrorResponse("Invalid token", "invalid_request_error", "authorization", "401");
        }

        String apiKey = authorization.replace("Bearer ", "");
        if (!StringUtils.hasLength(apiKey)) {
            return new OpenAiErrorResponse("Invalid token", "invalid_request_error", "authorization", "401");
        }

        // 校验 messages
        List<Object> messages = params.getMessages();
        if (messages == null || messages.isEmpty()) {
            return new OpenAiErrorResponse("Bad Request", "messages can not be null or empty", "messages", "400");
        }

        BigInteger botId = null;
        try {
            botId = aiBotApiKeyService.decryptApiKey(apiKey);
        } catch (Exception e) {
            return new OpenAiErrorResponse("Invalid token", "invalid_request_error", "authorization", "401");
        }

        AiBot aiBot = aiBotService.getById(botId);
        if (aiBot == null) {
            return new OpenAiErrorResponse("Bot Not Found", "resource_not_found_error", null, "404");
        }

        // 校验 llm
        AiLlm aiLlm = aiLlmService.getById(aiBot.getLlmId());
        if (aiLlm == null) {
            return new OpenAiErrorResponse("Llm Not Found", "resource_not_found_error", null, "404");
        }

        Boolean stream = params.getStream() != null ? params.getStream() : true;

        ChatOptions chatOptions = buildChatOptions(params, aiLlm);

        buildFunctions(aiBot, chatOptions);

        if (stream) {
            return handleStreamChat(aiLlm, chatOptions, response);
        } else {
            return handleNotStreamChat(aiLlm, chatOptions, response);
        }

    }

    private Object handleNotStreamChat(AiLlm aiLlm, ChatOptions chatOptions,
        HttpServletResponse response) {

        response.setContentType("application/json;charset=utf-8");

        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        RequestContextHolder.setRequestAttributes(sra, true);

        PlatformType platformType = PlatformType.getByBrand(aiLlm.getBrand());

        Llm llm = aiLlm.toLlm();
        chatOptions.setEnableThinking(false);
        AiMessageResponse aiResponse = llm.chat(new TextPrompt(""), chatOptions);
        UnifiedLlmResponse convertResponse = ResponseConverter.handleResponse(aiResponse.getResponse(),
            platformType);
        String json = JSON.toJSONString(convertResponse);

        logger.info("大模型回复：{}", json);

        return convertResponse;
    }

    private SseEmitter handleStreamChat(AiLlm aiLlm, ChatOptions chatOptions,
        HttpServletResponse response) {

        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");

        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        RequestContextHolder.setRequestAttributes(sra, true);

        MySseEmitter mySseEmitter = new MySseEmitter(1000 * 60 * 300L);

        PlatformType platformType = PlatformType.getByBrand(aiLlm.getBrand());

        Llm llm = aiLlm.toLlm();
        llm.chatStream("", new StreamResponseListener() {
            @Override
            public void onMessage(ChatContext chatContext, AiMessageResponse aiMessageResponse) {
                List<FunctionCaller> functionCallers = aiMessageResponse.getFunctionCallers();

                if (functionCallers != null && !functionCallers.isEmpty()) {

                    // todo function calling 逻辑

                } else {
                    UnifiedLlmResponse convertResponse = ResponseConverter.handleResponse(aiMessageResponse
                        .getResponse(),
                        platformType);
                    String json = JSON.toJSONString(convertResponse);
                    logger.info("大模型回复：{}", json);
                    mySseEmitter.send(json);
                }

            }

            @Override
            public void onStop(ChatContext context) {
                mySseEmitter.complete();
            }

            @Override
            public void onFailure(ChatContext context, Throwable throwable) {
                logger.error("fail:{}", throwable.getMessage());
                mySseEmitter.send(throwable.getMessage());
                mySseEmitter.completeWithError(throwable);
            }

        }, chatOptions);

        return mySseEmitter;

    }

    private ChatOptions buildChatOptions(OpenAiChatRequest params, AiLlm aiLlm) {

        ChatOptions chatOptions = params.buildChatOptions(aiLlm);

        return chatOptions;
    }

    private void buildFunctionJsonArray(List<Map<String, Object>> functionsJsonArray, List<Function> functions) {
        for (Function function : functions) {
            Map<String, Object> functionRoot = new HashMap<>();
            functionRoot.put("type", "function");

            Map<String, Object> functionObj = new HashMap<>();
            functionRoot.put("function", functionObj);

            functionObj.put("name", function.getName());
            functionObj.put("description", function.getDescription());

            Map<String, Object> parametersObj = new HashMap<>();
            functionObj.put("parameters", parametersObj);
            parametersObj.put("type", "object");

            Map<String, Object> propertiesObj = new HashMap<>();
            parametersObj.put("properties", propertiesObj);

            addParameters(function.getParameters(), propertiesObj, parametersObj);

            functionsJsonArray.add(functionRoot);
        }
    }

    private void addParameters(Parameter[] parameters, Map<String, Object> propertiesObj,
        Map<String, Object> parametersObj) {
        List<String> requiredProperties = new ArrayList<>();
        for (Parameter parameter : parameters) {
            Map<String, Object> parameterObj = new HashMap<>();
            parameterObj.put("type", parameter.getType());
            parameterObj.put("description", parameter.getDescription());
            parameterObj.put("enum", parameter.getEnums());
            if (parameter.isRequired()) {
                requiredProperties.add(parameter.getName());
            }

            List<Parameter> children = parameter.getChildren();
            if (children != null && !children.isEmpty() && "object".equalsIgnoreCase(parameter.getType())) {
                Map<String, Object> childrenObj = new HashMap<>();
                parameterObj.put("properties", childrenObj);
                addParameters(children.toArray(new Parameter[0]), childrenObj, parameterObj);
            }

            propertiesObj.put(parameter.getName(), parameterObj);
        }

        if (!requiredProperties.isEmpty()) {
            parametersObj.put("required", requiredProperties);
        }
    }

    private ArrayList<Function> buildFunctions(AiBot aiBot, ChatOptions chatOptions) {

        BigInteger botId = aiBot.getId();
        ArrayList<Function> functionList = new ArrayList<>();

        // 绑定知识库
        List<AiBotKnowledge> aiBotKnowledgeList = aiBotKnowledgeService.listByBotId(botId);
        if (aiBotKnowledgeList != null && !aiBotKnowledgeList.isEmpty()) {
            List<BigInteger> knowledgeIds = aiBotKnowledgeList.stream()
                .map(AiBotKnowledge::getKnowledgeId)
                .collect(Collectors.toList());
            List<AiKnowledge> aiKnowledgeList = aiKnowledgeService.listByIds(knowledgeIds);

            if (aiKnowledgeList != null && !aiKnowledgeList.isEmpty()) {
                aiKnowledgeList.forEach(aiKnowledge -> {
                    Function function = aiKnowledge.toFunction(true);
                    functionList.add(function);
                });
            }
        }

        // 绑定插件
        List<AiPluginTool> aiPluginToolList = (List<AiPluginTool>) aiPluginToolService.getPluginToolList(botId).data();
        if (aiPluginToolList != null && !aiPluginToolList.isEmpty()) {
            aiPluginToolList.forEach(aiPluginTool -> {
                Function function = aiPluginTool.toFunction();
                functionList.add(function);
            });
        }

        // 绑定工作流
        List<AiBotWorkflow> aiBotWorkflowList = aiBotWorkflowService.listByBotId(botId);
        if (aiBotWorkflowList != null && !aiBotWorkflowList.isEmpty()) {
            List<BigInteger> workflowIds = aiBotWorkflowList.stream()
                .map(AiBotWorkflow::getWorkflowId)
                .collect(Collectors.toList());
            List<AiWorkflow> aiWorkflowList = aiWorkflowService.listByIds(workflowIds);

            if (aiWorkflowList != null && !aiWorkflowList.isEmpty()) {
                aiWorkflowList.forEach(workflow -> {
                    Function function = workflow.toFunction(true);
                    functionList.add(function);
                });
            }
        }

        ArrayList<Map<String, Object>> functionJsonArray = new ArrayList<>();
        buildFunctionJsonArray(functionJsonArray, functionList);

        if (functionJsonArray != null && !functionJsonArray.isEmpty()) {
            Map<String, Object> extra = chatOptions.getExtra();
            if (extra != null && !extra.isEmpty()) {

                if (extra.get("payload") != null) {
                    List<Map<String, Object>> sparkFunctions = new ArrayList<>();
                    Map<String, Object> payload = (Map<String, Object>) extra.get("payload");

                    functionJsonArray.forEach(function -> {
                        Map<String, Object> functionObj = (Map<String, Object>) function.get("function");
                        sparkFunctions.add(
                            Maps.of("name", functionObj.get("name"))
                                .set("description", functionObj.get("description"))
                                .set("parameters", functionObj.get("parameters"))
                                .set("required", functionObj.get("required"))
                        );

                    });
                    payload.put("functions", Maps.of("text", sparkFunctions));

                } else {
                    extra.put("tools", functionJsonArray);
                    extra.put("tool_choice", "auto");
                }

            }
        }

        return functionList;

    }

}
