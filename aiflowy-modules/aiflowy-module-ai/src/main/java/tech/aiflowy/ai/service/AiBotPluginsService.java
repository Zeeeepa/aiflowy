package tech.aiflowy.ai.service;

import com.mybatisflex.core.service.IService;
import tech.aiflowy.ai.entity.AiBotPlugins;
import tech.aiflowy.common.domain.Result;

/**
 *  服务层。
 *
 * @author michael
 * @since 2025-04-07
 */
public interface AiBotPluginsService extends IService<AiBotPlugins> {

    Result getList(String botId);

    Result doRemove(String botId, String pluginId);
}
