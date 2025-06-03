package tech.aiflowy.ai.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.agentsflex.core.memory.ChatMemory;
import com.agentsflex.core.message.AiMessage;
import com.agentsflex.core.message.FunctionCall;
import com.agentsflex.core.message.Message;
import com.alicp.jetcache.Cache;
import tech.aiflowy.ai.entity.AiBotConversationMessage;
import tech.aiflowy.ai.entity.AiBotMessage;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AiBotMessageIframeMemory implements ChatMemory {
    private final BigInteger botId;
    private final  String tempUserId;
    private final  String sessionId;

    private Cache<String, Object> cache;

    public AiBotMessageIframeMemory(BigInteger botId, String tempUserId, String sessionId, Cache<String, Object> cache) {
        this.botId = botId;
        this.tempUserId = tempUserId;
        this.sessionId = sessionId;
        this.cache = cache;
    }

    @Override
    public List<Message> getMessages() {
        List<AiBotConversationMessage> result = (List<AiBotConversationMessage>)cache.get(tempUserId + ":" + botId);
        List<AiBotMessage> aiBotMessages = null;
        for (int i = 0; i < result.size(); i++) {
            if (result.get(i).getSessionId().equals(sessionId)) {
                aiBotMessages = result.get(i).getAiBotMessageList();
            }
        }
        List<Message> messages = new ArrayList<>(aiBotMessages.size());
        for (AiBotMessage aiBotMessage : aiBotMessages) {
            Message message = aiBotMessage.toMessage();
            if (message != null) messages.add(message);
        }
        return messages;
    }

    @Override
    public void addMessage(Message message) {
        List<AiBotConversationMessage> result = (List<AiBotConversationMessage>)cache.get(tempUserId + ":" + botId);
        if (message instanceof AiMessage) {
            AiMessage m = (AiMessage) message;
            List<FunctionCall> calls = m.getCalls();
            if (CollectionUtil.isNotEmpty(calls)) {
                return;
            }
            for (int i = 0; i < result.size(); i++) {
                if (result.get(i).getSessionId().equals(sessionId)) {
                    List<AiBotMessage> aiBotMessageList = result.get(i).getAiBotMessageList();
                    AiBotMessage aiBotMessage = new AiBotMessage();
                    aiBotMessage.setRole("assistant");
                    aiBotMessage.setCreated(new Date());
                    aiBotMessage.setContent(m.getFullContent());
                    aiBotMessage.setTotalTokens(m.getTotalTokens());
                    aiBotMessage.setPromptTokens(m.getPromptTokens());
                    aiBotMessage.setCompletionTokens(m.getCompletionTokens());
                    aiBotMessageList.add(aiBotMessage);
                    result.get(i).setAiBotMessageList(aiBotMessageList);
                    cache.put(tempUserId + ":" + botId, result);
                }
            }
        }
    }

    @Override
    public Object id() {
        return null;
    }
}
