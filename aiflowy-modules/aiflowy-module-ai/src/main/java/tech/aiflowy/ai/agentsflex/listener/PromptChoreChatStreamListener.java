package tech.aiflowy.ai.agentsflex.listener;

import com.agentsflex.core.model.chat.StreamResponseListener;
import com.agentsflex.core.model.chat.response.AiMessageResponse;
import com.agentsflex.core.model.client.StreamContext;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * 系统提示词优化监听器
 */
public class PromptChoreChatStreamListener implements StreamResponseListener {

    private final SseEmitter sseEmitter;

    public PromptChoreChatStreamListener(SseEmitter sseEmitter) {
        this.sseEmitter = sseEmitter;
    }
    @Override
    public void onStart(StreamContext context) {
        StreamResponseListener.super.onStart(context);
    }

    @Override
    public void onMessage(StreamContext context, AiMessageResponse response) {
        String content = response.getMessage().getContent();
        if (content != null) {
            try {
                sseEmitter.send(content);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onStop(StreamContext context) {
        StreamResponseListener.super.onStop(context);
    }

    @Override
    public void onFailure(StreamContext context, Throwable throwable) {
        StreamResponseListener.super.onFailure(context, throwable);
    }
}
