package tech.aiflowy.system.service;

import com.mybatisflex.core.service.IService;
import tech.aiflowy.system.entity.SysApiKey;

/**
 *  服务层。
 *
 * @author Administrator
 * @since 2025-04-18
 */
public interface SysApiKeyService extends IService<SysApiKey> {

    void checkApiKey(String apiKey);

    void checkApikeyPermission(String apiKey, String requestURI);
}
