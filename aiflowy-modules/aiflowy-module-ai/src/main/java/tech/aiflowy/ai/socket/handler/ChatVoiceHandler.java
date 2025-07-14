package tech.aiflowy.ai.socket.handler;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatVoiceHandler extends TextWebSocketHandler {

    public static Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    private static Logger logger = LoggerFactory.getLogger(ChatVoiceHandler.class);

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        String sessionId = session.getAttributes().get("sessionId").toString();
        sessionMap.put(sessionId, session);
        logger.info("连接建立：session:{}",sessionId);

    }

    public static void sendJsonVoiceMessage(String sessionId,String messageSessionId,String voiceJson){
        WebSocketSession session = sessionMap.get(sessionId);

        if (session != null && session.isOpen()) {
            try {
                Map<String,String> map = new HashMap<>();
                map.put("data",voiceJson);
                map.put("sessionId",sessionId);
                map.put("messageSessionId",messageSessionId);
                session.sendMessage(new TextMessage(JSON.toJSONString(map)));
            } catch (IOException e) {
                logger.error("发送语音消息失败",e);
            }

            return;
        }

        logger.error("发送语音消息失败：WebSocket 连接为空或连接已关闭");

    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = session.getAttributes().get("sessionId").toString();
        if (status.getCode() == 1000){
            sessionMap.remove(sessionId);
            logger.info("{}WebSocket -> 连接关闭：{},{}",sessionId,status.getCode(),status.getReason());
            return;
        }

        logger.info("{}WebSocket -> 连接关闭失败，非手动关闭，保留sessioin：{},{}",sessionId,status.getCode(),status.getReason());

    }
}
