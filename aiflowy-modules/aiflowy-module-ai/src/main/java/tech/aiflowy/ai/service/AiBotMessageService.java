package tech.aiflowy.ai.service;

import com.mybatisflex.core.service.IService;
import tech.aiflowy.ai.entity.AiBotMessage;
import tech.aiflowy.common.domain.Result;

/**
 * Bot 消息记录表 服务层。
 *
 * @author michael
 * @since 2024-11-04
 */
public interface AiBotMessageService extends IService<AiBotMessage> {


    Result messageList(String botId, String sessionId, String tempUserId, String tempUserSessionId);

    boolean removeMsg(String botId, String sessionId);
}
