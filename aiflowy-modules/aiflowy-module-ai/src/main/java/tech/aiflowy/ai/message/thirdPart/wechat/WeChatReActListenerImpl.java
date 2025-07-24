
package tech.aiflowy.ai.message.thirdPart.wechat;
import com.agentsflex.core.react.ReActAgentListener;
import com.agentsflex.core.react.ReActStep;
import com.agentsflex.core.llm.ChatContext;
import com.agentsflex.core.llm.response.AiMessageResponse;
import com.agentsflex.core.llm.functions.Function;
import java.util.List;

import me.chanjar.weixin.common.error.WxErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.alicp.jetcache.Cache;

public class WeChatReActListenerImpl implements ReActAgentListener {

    private static final Logger log = LoggerFactory.getLogger(WeChatReActListenerImpl.class);

    private String toUserOpenId;
    private WxMpService wxMpService;
    private ServletRequestAttributes sra;
    private Cache<String,Object> cache;

    private String batchMessageContent = "";
    private String thinkingMessage = "";

    private boolean actionExcute = false;



    public WeChatReActListenerImpl(String toUserOpenId,WxMpService wxMpService,ServletRequestAttributes sra,Cache<String,Object> cache){
        this.toUserOpenId = toUserOpenId;
        this.wxMpService = wxMpService;
        this.cache = cache;
        this.sra = sra;
        RequestContextHolder.setRequestAttributes(sra, true);
    }

    @Override
    public void onChatResponseStream(ChatContext context, AiMessageResponse response) {

        log.info("onChatResponseStream------->thinkingMessage:{}",thinkingMessage);

        String reasoningContent = response.getMessage().getReasoningContent();
        if (StringUtils.hasLength(reasoningContent)){
            if (!StringUtils.hasLength(thinkingMessage)){
                thinkingMessage = "请稍等，我正在思考....";
                sendMessage(thinkingMessage);
            }
        }

    }

    @Override
    public void onActionStart(ReActStep step) {

        log.info("onActionStart------->thinkingMessage:{}",thinkingMessage);

        actionExcute = true;
        batchMessageContent += "需要调用工具回答此问题，正在调用工具，请稍等...";
        sendMessage(batchMessageContent);
    }


    @Override
    public void onActionEnd(ReActStep step, Object result) {
        batchMessageContent += "工具调用完成。\n" ;
    }

    @Override
    public void onFinalAnswer(String finalAnswer) {

        log.info("onFialAnswer------->thinkingMessage:{}------------->finalMessage:{}",thinkingMessage,finalAnswer);


        batchMessageContent += finalAnswer;
        sendMessage(batchMessageContent);

        cache.remove("wechat:" + toUserOpenId + ":answering");

    }

    @Override
    public void onNonActionResponseStream(ChatContext context) {

        log.info("onFialAnswer------->thinkingMessage:{}------------->finalMessage:{}",thinkingMessage,context.getLastAiMessage().getFullContent());

        if (actionExcute){
            log.info("执行了 Action ，最终答案将在 FinalAnswer 中输出，忽略。");
            return;
        }

  
        batchMessageContent += context.getLastAiMessage().getFullContent();

        
        sendMessage(batchMessageContent);

        cache.remove("wechat:" + toUserOpenId + ":answering");
    }


    @Override
    public void onActionInvokeError(Exception e) {
        log.error("工具调用失败：{}",e.getMessage());
        batchMessageContent += "工具调用失败....请稍后重试";
        sendMessage(batchMessageContent);

    }

    @Override
    public void onStepParseError(String content) {
        log.error("解析步骤出错：{}",content);
        batchMessageContent += "生成回复失败....请稍后重试";
        sendMessage(batchMessageContent);
    }

    @Override
    public void onActionNotMatched(ReActStep step, List<Function> functions) {
        log.error("未找到匹配工具，step:{},functions:{}",step,functions);
        batchMessageContent += "生成回复失败...未找到可调用工具....请稍后重试";
        sendMessage(batchMessageContent);
    }

    @Override
    public void onError(Exception error) {
        log.error("大模型执行报错：{}",error.getMessage());
        batchMessageContent += "大模型调用出错....请稍后重试";
        sendMessage(batchMessageContent);
    }

    private void sendMessage(String content){
        WxMpKefuMessage message = WxMpKefuMessage.TEXT().toUser(toUserOpenId).content(content).build();
        try {
            wxMpService.getKefuService().sendKefuMessage(message);
        } catch (WxErrorException e) {
            log.error("发送客服消息失败：{}",e.getMessage());
        } finally {
            batchMessageContent = "";
        }
    }

}
