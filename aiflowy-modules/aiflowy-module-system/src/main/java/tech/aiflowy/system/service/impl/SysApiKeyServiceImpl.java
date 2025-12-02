package tech.aiflowy.system.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.exceptions.BusinessException;
import tech.aiflowy.system.entity.SysApiKey;
import tech.aiflowy.system.entity.SysApiKeyResourcePermission;
import tech.aiflowy.system.entity.SysApiKeyResourcePermissionRelationship;
import tech.aiflowy.system.mapper.SysApiKeyMapper;
import tech.aiflowy.system.service.SysApiKeyResourcePermissionRelationshipService;
import tech.aiflowy.system.service.SysApiKeyResourcePermissionService;
import tech.aiflowy.system.service.SysApiKeyService;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * 服务层实现。
 *
 * @author Administrator
 * @since 2025-04-18
 */
@Service
public class SysApiKeyServiceImpl extends ServiceImpl<SysApiKeyMapper, SysApiKey> implements SysApiKeyService {

    @Resource
    private SysApiKeyResourcePermissionRelationshipService permissionRelationshipService;

    @Resource
    private SysApiKeyResourcePermissionService permissionService;

    @Override
    public void checkApiKey(String apiKey) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select("api_key", "status", "expired_at")
                .from("tb_sys_api_key")
                .where("api_key = ? ", apiKey);
        SysApiKey aiBotApiKey = getOne(queryWrapper);
        if (aiBotApiKey == null) {
            throw new BusinessException("该apiKey不存在");
        }
        if (aiBotApiKey.getStatus() == 0) {
            throw new BusinessException("该apiKey未启用");
        }
        if (aiBotApiKey.getExpiredAt().getTime() < new Date().getTime()) {
            throw new BusinessException("该apiKey已失效");
        }
    }

    @Override
    public void checkApikeyPermission(String apiKey, String requestURI) {
        QueryWrapper queryWrapper = QueryWrapper.create().eq(SysApiKey::getApiKey, apiKey);
        SysApiKey sysApiKey = getOne(queryWrapper);
        if (sysApiKey == null) {
            throw new BusinessException("该apiKey不存在");
        }
        if (sysApiKey.getStatus() == 0) {
            throw new BusinessException("该apiKey未启用");
        }
        if (sysApiKey.getExpiredAt().getTime() < new Date().getTime()) {
            throw new BusinessException("该apiKey已失效");
        }
        QueryWrapper queryWrapperShip = QueryWrapper.create().select(SysApiKeyResourcePermissionRelationship::getApiKeyResourceId).eq(SysApiKeyResourcePermissionRelationship::getApiKeyId, sysApiKey.getId());
        // 获取到当前apiKey可以访问哪些资源
        List<BigInteger> resourcePermissionsId = permissionRelationshipService.listAs(queryWrapperShip, BigInteger.class);
        List<SysApiKeyResourcePermission> allPermissions = permissionService.list();
        boolean hasPermission = false;
        for (BigInteger resourcePermission : resourcePermissionsId) {
            for (SysApiKeyResourcePermission allPermission : allPermissions) {
                if (resourcePermission.equals(allPermission.getId()) && allPermission.getRequestInterface().equals(requestURI)) {
                    hasPermission = true;
                    break;
                }
            }
        }
        if (!hasPermission) {
            throw new BusinessException("该apiKey没有权限访问该资源");
        }
    }
}
