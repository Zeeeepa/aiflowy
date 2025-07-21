package tech.aiflowy.ai.entity.openAi.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 大模型响应转换工具类
 * 负责将不同平台的响应格式转换为统一格式
 */
public class ResponseConverter {

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 将星火响应转换为统一格式
     * 
     * @param sparkResponse 星火响应对象
     * @return 统一格式响应
     */
    public static UnifiedLlmResponse convertSparkResponse(SparkResponse sparkResponse) {
        if (sparkResponse == null) {
            return null;
        }

        UnifiedLlmResponse unifiedResponse = new UnifiedLlmResponse();

        // 设置基本信息
        unifiedResponse.setId(generateChatCompletionId());
        unifiedResponse.setObject("chat.completion");
        unifiedResponse.setCreated(System.currentTimeMillis() / 1000);
        unifiedResponse.setModel("spark"); // 可根据实际模型设置

        // 转换选择项
        if (sparkResponse.getPayload() != null && sparkResponse.getPayload().getChoices() != null) {
            List<UnifiedLlmResponse.Choice> choices = new ArrayList<>();
            UnifiedLlmResponse.Choice choice = new UnifiedLlmResponse.Choice();
            choice.setIndex(0);

            // 转换消息内容
            UnifiedLlmResponse.Message message = new UnifiedLlmResponse.Message();
            message.setRole("assistant");

            // 拼接文本内容
            if (sparkResponse.getPayload().getChoices().getText() != null) {
                StringBuilder content = new StringBuilder();
                for (SparkResponse.Payload.Choices.Text text : sparkResponse.getPayload().getChoices().getText()) {
                    if (text.getContent() != null) {
                        content.append(text.getContent());
                    }
                }
                message.setContent(content.toString());
            }

            choice.setMessage(message);

            // 设置完成原因
            if (sparkResponse.getHeader() != null && sparkResponse.getHeader().getStatus() != null) {
                if (sparkResponse.getHeader().getStatus() == 2) {
                    choice.setFinishReason("stop");
                } else {
                    choice.setFinishReason(null); // 未完成
                }
            }

            choices.add(choice);
            unifiedResponse.setChoices(choices);
        }

        // 转换使用统计
        if (sparkResponse.getPayload() != null && sparkResponse.getPayload().getUsage() != null) {
            UnifiedLlmResponse.Usage usage = new UnifiedLlmResponse.Usage();
            SparkResponse.Payload.Usage.Text textUsage = sparkResponse.getPayload().getUsage().getText();

            if (textUsage != null) {
                usage.setPromptTokens(textUsage.getPromptTokens());
                usage.setCompletionTokens(textUsage.getCompletionTokens());
                usage.setTotalTokens(textUsage.getTotalTokens());
            }

            unifiedResponse.setUsage(usage);
        }

        return unifiedResponse;
    }

    /**
     * 将JSON字符串解析为统一响应格式
     * 适用于OpenAI兼容的平台：DeepSeek, OpenAI, 阿里百炼, 火山引擎, 百度千帆, Gitee AI, 硅基流动, Ollama
     * 
     * @param jsonResponse JSON响应字符串
     * @return 统一格式响应
     */
    public static UnifiedLlmResponse parseUnifiedResponse(String jsonResponse) {
        try {
            return objectMapper.readValue(jsonResponse, UnifiedLlmResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("解析统一响应格式失败", e);
        }
    }

    /**
     * 将JSON字符串解析为星火响应格式
     * 
     * @param jsonResponse JSON响应字符串
     * @return 星火响应对象
     */
    public static SparkResponse parseSparkResponse(String jsonResponse) {
        try {
            return objectMapper.readValue(jsonResponse, SparkResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("解析星火响应格式失败", e);
        }
    }


    /**
     * 生成聊天完成ID
     * 
     * @return 生成的ID
     */
    private static String generateChatCompletionId() {
        return "chatcmpl-" + UUID.randomUUID().toString().replace("-", "").substring(0, 29);
    }



    /**
     * 根据平台类型处理响应
     * 
     * @param jsonResponse JSON响应字符串
     * @param platformType 平台类型
     * @return 统一格式响应
     */
    public static UnifiedLlmResponse handleResponse(String jsonResponse, PlatformType platformType) {
        if (jsonResponse == null || jsonResponse.trim().isEmpty()) {
            return null;
        }

        switch (platformType) {
            case SPARK:
                // 星火平台使用特殊处理
                SparkResponse sparkResponse = parseSparkResponse(jsonResponse);
                return convertSparkResponse(sparkResponse);
            case DEEPSEEK:
            case OPENAI:
            case BAILIAN:
            case VOLCENGINE:
            case QIANFAN:
            case GITEE_AI:
            case SILICONFLOW:
            case OLLAMA:
            default:
                // 其他平台使用OpenAI兼容格式
                return parseUnifiedResponse(jsonResponse);
        }
    }

    /**
     * 创建错误响应
     * 
     * @param errorMessage 错误信息
     * @param errorCode    错误代码
     * @return 错误响应对象
     */
    public static UnifiedLlmResponse createErrorResponse(String errorMessage, String errorCode) {
        UnifiedLlmResponse response = new UnifiedLlmResponse();
        response.setId(generateChatCompletionId());
        response.setObject("error");
        response.setCreated(System.currentTimeMillis() / 1000);

        List<UnifiedLlmResponse.Choice> choices = new ArrayList<>();
        UnifiedLlmResponse.Choice choice = new UnifiedLlmResponse.Choice();
        choice.setIndex(0);
        choice.setFinishReason("error");

        UnifiedLlmResponse.Message message = new UnifiedLlmResponse.Message();
        message.setRole("assistant");
        message.setContent("Error: " + errorMessage + " (Code: " + errorCode + ")");
        choice.setMessage(message);

        choices.add(choice);
        response.setChoices(choices);

        return response;
    }


}