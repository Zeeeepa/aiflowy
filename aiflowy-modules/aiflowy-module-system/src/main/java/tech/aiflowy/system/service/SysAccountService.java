package tech.aiflowy.system.service;

import tech.aiflowy.common.third.auth.entity.PlatformUser;
import tech.aiflowy.system.entity.SysAccount;
import com.mybatisflex.core.service.IService;

/**
 * 用户表 服务层。
 *
 * @author ArkLight
 * @since 2025-03-14
 */
public interface SysAccountService extends IService<SysAccount> {

    void syncRelations(SysAccount entity);

    SysAccount getByUsername(String userKey);

    SysAccount savePlatformUser(String platform, PlatformUser userInfo);
}
