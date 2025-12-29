package tech.aiflowy.ai.agentsflex.listener;

import com.agentsflex.core.message.AiMessage;
import com.agentsflex.core.message.ToolMessage;
import com.agentsflex.core.model.chat.ChatModel;
import com.agentsflex.core.model.chat.StreamResponseListener;
import com.agentsflex.core.model.chat.response.AiMessageResponse;
import com.agentsflex.core.model.client.StreamContext;
import com.agentsflex.core.prompt.MemoryPrompt;
import com.alibaba.fastjson.JSON;
import org.apache.catalina.connector.ClientAbortException;
import tech.aiflowy.common.util.StringUtil;
import tech.aiflowy.core.chat.protocol.ChatDomain;
import tech.aiflowy.core.chat.protocol.ChatEnvelope;
import tech.aiflowy.core.chat.protocol.ChatType;
import tech.aiflowy.core.chat.protocol.MessageRole;
import tech.aiflowy.core.chat.protocol.sse.ChatSseEmitter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatStreamListener implements StreamResponseListener {

    private final ChatModel chatModel;
    private final MemoryPrompt memoryPrompt;
    private final ChatSseEmitter sseEmitter;


    public ChatStreamListener(ChatModel chatModel, MemoryPrompt memoryPrompt, ChatSseEmitter sseEmitter) {
        this.chatModel = chatModel;
        this.memoryPrompt = memoryPrompt;
        this.sseEmitter = sseEmitter;
    }

    @Override
    public void onStart(StreamContext context) {
        StreamResponseListener.super.onStart(context);
    }

    @Override
    public void onMessage(StreamContext context, AiMessageResponse aiMessageResponse) {
        try {
            AiMessage aiMessage = aiMessageResponse.getMessage();
            if (aiMessage == null) {
                return;
            }
            if ((aiMessage.getFinished() != null) && StringUtil.hasText(aiMessage.getFullReasoningContent()) && aiMessage.isFinalDelta()) {
                aiMessage.setContent(aiMessage.getFullReasoningContent());
                memoryPrompt.addMessage(aiMessage);
                return;
            }
            if (aiMessage.isFinalDelta() && aiMessageResponse.hasToolCalls()) {
                List<ToolMessage> toolMessages = aiMessageResponse.executeToolCallsAndGetToolMessages();
                for (ToolMessage toolMessage : toolMessages) {
                    memoryPrompt.addMessage(toolMessage);
                }
                chatModel.chatStream(memoryPrompt, this);
            } else {
                String delta = aiMessageResponse.getMessage().getContent();
                if (StringUtil.hasText(delta)) {
                    ChatEnvelope<Map<String, String>> chatEnvelope = new ChatEnvelope<>();
                    chatEnvelope.setDomain(ChatDomain.LLM);
                    chatEnvelope.setType(ChatType.MESSAGE);
                    Map<String, String> deletaMap = new HashMap<>();
                    deletaMap.put("delta", delta);
                    deletaMap.put("role", MessageRole.ASSISTANT.getValue());
                    chatEnvelope.setPayload(deletaMap);
                    sseEmitter.send(chatEnvelope);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onStop(StreamContext context) {
        System.out.println("onStop");
        memoryPrompt.addMessage(context.getAiMessage());
        ChatEnvelope<Map<String, String>> chatEnvelope = new ChatEnvelope<>();
        chatEnvelope.setDomain(ChatDomain.SYSTEM);
        sseEmitter.sendDone(chatEnvelope);
        StreamResponseListener.super.onStop(context);
    }

    @Override
    public void onFailure(StreamContext context, Throwable throwable) {
        if (throwable != null && throwable.getCause() instanceof ClientAbortException) {
            sseEmitter.complete();
        }
    }
}
