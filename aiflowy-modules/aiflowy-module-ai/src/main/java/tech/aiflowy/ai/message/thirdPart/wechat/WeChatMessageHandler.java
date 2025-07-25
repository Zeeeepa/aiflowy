
package tech.aiflowy.ai.message.thirdPart.wechat;

import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

import javax.servlet.http.HttpServletRequest;

import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tech.aiflowy.ai.message.thirdPart.MessageHandler;
import tech.aiflowy.ai.utils.MessageTypeConstants;

import java.math.BigInteger;
import java.util.Map;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Resource;
import me.chanjar.weixin.mp.api.WxMpService;
import tech.aiflowy.ai.service.AiBotApiKeyService;
import com.agentsflex.core.react.ReActAgent;
import com.agentsflex.core.llm.Llm;
import com.agentsflex.core.llm.functions.Function;
import com.agentsflex.core.prompt.HistoriesPrompt;
import com.agentsflex.core.llm.ChatOptions;
import tech.aiflowy.ai.message.thirdPart.ThirdPartChatMemory;
import com.alicp.jetcache.Cache;
import java.util.concurrent.TimeUnit;
import tech.aiflowy.common.web.exceptions.BusinessException;

@Component
public class WeChatMessageHandler implements MessageHandler {

    private static final Logger log = LoggerFactory.getLogger(WeChatMessageHandler.class);

    private static final List<String> SUPPORTED_MESSAGE_TYPES = Arrays.asList(
        MessageTypeConstants.MSG_TYPE_TEXT,
        MessageTypeConstants.MSG_TYPE_IMAGE,
        MessageTypeConstants.MSG_TYPE_VOICE,
        MessageTypeConstants.MSG_TYPE_EVENT
    );

    @Resource
    private WxMpService wxMpService;

    @Resource
    @Qualifier("defaultCache")
    private Cache<String, Object> cache;

    @Override
    public Object handleMessage(Object messageData, Map<String, Object> contextData, Map<String, Object> agentParams) {

        log.info("messageData:{}", messageData);
        log.info("contextData:{}", contextData);
        log.info("agentParams:{}", agentParams);

        // 提取信息
        HttpServletRequest request = (HttpServletRequest) contextData.getOrDefault("request", null);
        String signature = ((String) contextData.getOrDefault("signature", ""));
        String timestamp = ((String) contextData.getOrDefault("timestamp", ""));
        String nonce = ((String) contextData.getOrDefault("nonce", ""));
        String encryptType = ((String) contextData.getOrDefault("encryptType", ""));
        String msgSignature = ((String) contextData.getOrDefault("msgSignature", ""));

        // 校验签名
        if (!this.wxMpService.checkSignature(timestamp, nonce, signature)) {
            log.error("签名验证失败，wxMpService:{},wxMpService.config:{}",this.wxMpService,this.wxMpService.getWxMpConfigStorage());
            return "";
        }

        WxMpXmlMessage inMessage = null;

        // 处理加密消息
        if (StringUtils.hasLength(encryptType) && "aes".equals(encryptType)) {
            inMessage = WxMpXmlMessage.fromEncryptedXml(messageData.toString(),
                this.wxMpService.getWxMpConfigStorage(), timestamp, nonce, msgSignature);
        } else {
            // 处理明文消息
            inMessage = WxMpXmlMessage.fromXml(messageData.toString());
        }

        WxMpXmlOutMessage outMessage = handle(inMessage, request, agentParams);

        if (outMessage != null) {
            return outMessage.toXml();
        }

        return "";
    }

    @Override
    public String getPlatformType() {
        return MessageTypeConstants.PLATFORM_WECHAT;
    }

    @Override
    public boolean supportMessageType(String messageType) {
        return SUPPORTED_MESSAGE_TYPES.contains(messageType);
    }

    /**
     * 处理接收到的微信消息
     */
    public WxMpXmlOutMessage handle(WxMpXmlMessage inMessage, HttpServletRequest request,
        Map<String, Object> agentParams) {

        String msgType = inMessage.getMsgType();
        String openId = inMessage.getFromUser();

        log.info("收到消息类型: {}, 来自用户: {}", msgType, openId);

        WxMpXmlOutMessage outMessage = null;

        try {
            switch (msgType) {
                case WxConsts.XmlMsgType.TEXT:
                    outMessage = handleTextMessage(inMessage, agentParams);
                    break;
                case WxConsts.XmlMsgType.IMAGE:
                case WxConsts.XmlMsgType.VOICE:
                case WxConsts.XmlMsgType.EVENT:
                default:
                    break;
            }
        } catch (Exception e) {
            log.error("处理消息异常", e);
            outMessage = createTextReplyMessage(inMessage, "系统繁忙，请稍后再试");
        }

        return outMessage;
    }

    /**
     * 处理文本消息
     */
    private WxMpXmlOutMessage handleTextMessage(WxMpXmlMessage inMessage, Map<String, Object> agentParams) {

        String content = inMessage.getContent();
        log.info("收到文本消息: {}", content);

        // 将发送消息的用户的 openId 当做 sessionId
        String sessionId = inMessage.getFromUser();

        Boolean isAnswering = (Boolean) cache.get("wechat:" + sessionId + ":answering");
        String replyContent = "";
        if (isAnswering != null && isAnswering) {
            log.info("收到文本消息，但是当前用户上一条消息大模型正在回复，忽略此条消息。message:{}", content);
            return createTextReplyMessage(inMessage, replyContent);
        }

        Llm llm = (Llm) agentParams.get("llm");
        List<Function> functions = (List<Function>) agentParams.get("functions");
        HistoriesPrompt historiesPrompt = (HistoriesPrompt) agentParams.get("historiesPrompt");
        ChatOptions chatOptions = (ChatOptions) agentParams.get("chatOptions");
        BigInteger botId = (BigInteger) agentParams.get("botId");

        String platform = "wechat";

        // 判断是否为清空上下文指令
        if ("/clearContext".equalsIgnoreCase(content.trim())) {

            cache.remove(platform + ":" + botId + ":" + sessionId);
            WxMpKefuMessage message = WxMpKefuMessage.TEXT().toUser(sessionId).content("bot记忆已清空~").build();
            try {
                wxMpService.getKefuService().sendKefuMessage(message);
            } catch (WxErrorException e) {
                log.error("发送客服消息失败：{}", e.getMessage());
            }

            return createTextReplyMessage(inMessage, replyContent);
        }

        // 将此用户的答复状态设置为 true 缓存 5 分钟
        cache.put("wechat:" + inMessage.getFromUser() + ":answering", true, 5, TimeUnit.MINUTES);

        // 发送消息提示用户：
        WxMpKefuMessage message = WxMpKefuMessage.TEXT()
            .toUser(sessionId)
            .content("大模型正在生成回复，请稍候...")
            .build();
        try {
            wxMpService.getKefuService().sendKefuMessage(message);
        } catch (WxErrorException e) {
            log.error("发送客服消息失败：{}", e.getMessage());
        }

        ReActAgent reActAgent = new ReActAgent(llm, functions, content, historiesPrompt);

        reActAgent.setChatOptions(chatOptions);

        ThirdPartChatMemory chatMemory = new ThirdPartChatMemory(cache, botId, sessionId, platform);

        historiesPrompt.setMemory(chatMemory);

        String promptTemplate = "你是一个 ReAct Agent，结合 Reasoning（推理）和 Action（行动）来解决问题。\n" + "但在处理用户问题时，请首先判断：\n"
            + "1. 如果问题可以通过你的常识或已有知识直接回答 → 请忽略 ReAct 框架，直接输出自然语言回答。\n"
            + "2. 如果问题需要调用特定工具才能解决（如查询、计算、获取外部信息等）→ 请严格按照 ReAct 格式响应。\n\n" + "如果你选择使用 ReAct 模式，请遵循以下格式：\n"
            + "Thought: 描述你对当前问题的理解，包括已知信息和缺失信息，说明你下一步将采取什么行动及其原因。\n" + "Action: 从下方列出的工具中选择一个合适的工具，仅输出工具名称，不得虚构。\n"
            + "Action Input: 使用标准 JSON 格式提供该工具所需的参数，禁止使用任何形式的代码块格式，包括但不限于'```json'、'```sql'、'```java'，确保字段名与工具描述一致。\n\n"
            + "在 ReAct 模式下，如果你已获得足够信息可以直接回答用户，请输出：\n" + "Final Answer: [你的回答]\n\n" + "注意事项：\n"
            + "1. 每次只能选择一个工具并执行一个动作。\n" + "2. 在未收到工具执行结果前，不要自行假设其输出。\n" + "3. 不得编造工具或参数，所有工具均列于下方。\n"
            + "4. 输出顺序必须为：Thought → Action → Action Input。\n"
            + "5. **回答完用户问题后立即结束，严禁以任何形式询问、建议、猜测用户后续操作或步骤，如使用\"如果需要...\"、\"您是否需要...\"、\"可以进一步...\"、\"下一步建议\"等相似语义的表述**\n"
            + "6. 回复前需判断当前输出是否为Final Answer，**必须严格遵守：当需要回复的内容是Final Answer时，禁止输出Thought、Action、Action Input**，示例：\n"
            + "\t[正确示例1]\n" + "\t\tFinal Answer:张三的年龄是35岁\n\n" + "\t[正确示例2]\n"
            + "\t\tFinal Answer:张三的邮箱是：aabbcc@qq.com\n\n" + "\t[错误示例]\n"
            + "\t\tThought: 根据查询结果，张三的年龄是35岁\n\t\tFinal Answer:张三的年龄是35岁\n\n" + "\t[错误示例2]\n"
            + "\t\tThought: 根据工具返回的结果，查询成功并返回了数据。数据中有一行记录，显示年龄为35岁。因此，我已获得足够信息来回答用户的问题。下一步是输出最终答案。\n" + "\n"
            + "\t\tFinal Answer: 张三的年龄是35岁。\n\n" + "\t**出现任意类似以上错误示例的回复将被视为极其严重的行为错误！**"
            + "9. 严格按照规定格式输出Thought、Action、Action Input、Final Answer；\n" + "\n" + "违反以上任一指令视为严重行为错误，必须严格遵守。\n\n"
            + "### 可用工具列表：\n" + "{tools}\n\n" + "### 用户问题如下：\n" + "{user_input}";

        reActAgent.setStreamable(true);

        reActAgent.setPromptTemplate(promptTemplate);

        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        reActAgent.addListener(new WeChatReActListenerImpl(sessionId, wxMpService, sra, cache));

        reActAgent.run();

        return createTextReplyMessage(inMessage, replyContent);
    }

    /**
     * 处理图片消息
     */
    private WxMpXmlOutMessage handleImageMessage(WxMpXmlMessage inMessage) {
        log.info("收到图片消息, MediaId: {}", inMessage.getMediaId());
        return createTextReplyMessage(inMessage, "收到您发送的图片，我们正在处理中...");
    }

    /**
     * 处理语音消息
     */
    private WxMpXmlOutMessage handleVoiceMessage(WxMpXmlMessage inMessage) {
        log.info("收到语音消息, MediaId: {}", inMessage.getMediaId());
        return createTextReplyMessage(inMessage, "收到您的语音消息，我们正在分析中...");
    }

    /**
     * 处理事件消息
     */
    private WxMpXmlOutMessage handleEventMessage(WxMpXmlMessage inMessage) {
        String event = inMessage.getEvent();
        log.info("收到事件: {}", event);

        String replyContent = "接收事件";

        switch (event) {
            case WxConsts.EventType.SUBSCRIBE:
                break;
            case WxConsts.EventType.UNSUBSCRIBE:
                // 取消关注事件，微信不会推送回复消息
                return null;
            case WxConsts.EventType.CLICK:
                replyContent = handleMenuClick(inMessage);
                break;
            default:
        }

        return createTextReplyMessage(inMessage, replyContent);
    }

    /**
     * 处理菜单点击事件
     */
    private String handleMenuClick(WxMpXmlMessage inMessage) {
        String eventKey = inMessage.getEventKey();
        log.info("菜单点击事件Key: {}", eventKey);

        return "您点击了菜单：" + eventKey;
    }

    /**
     * 处理默认消息
     */
    private WxMpXmlOutMessage handleDefaultMessage(WxMpXmlMessage inMessage) {
        return createTextReplyMessage(inMessage, "感谢您的消息，我们已收到！");
    }

    /**
     * 创建文本回复消息
     */
    private WxMpXmlOutMessage createTextReplyMessage(WxMpXmlMessage inMessage, String content) {
        return WxMpXmlOutMessage.TEXT()
            .content(content)
            .fromUser(inMessage.getToUser())
            .toUser(inMessage.getFromUser())
            .build();
    }
}
