package tech.aiflowy.common.util;

import com.alibaba.fastjson2.JSON;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

public class SSEUtil {

    public static SseEmitter sseEmitterForContent(String content) {
        SseEmitter emitter = new SseEmitter((long) (1000 * 60 * 2));
        try {
            String jsonString = JSON.toJSONString(Maps.of("content", content));
            emitter.send(jsonString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            emitter.complete();
        }
        return emitter;
    }
}
