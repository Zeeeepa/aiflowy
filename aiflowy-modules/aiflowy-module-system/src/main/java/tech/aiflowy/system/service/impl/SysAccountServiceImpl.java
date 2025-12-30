package tech.aiflowy.system.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.mybatisflex.core.keygen.impl.SnowFlakeIDKeyGenerator;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.aiflowy.common.constant.Constants;
import tech.aiflowy.common.constant.enums.EnumAccountType;
import tech.aiflowy.common.constant.enums.EnumDataStatus;
import tech.aiflowy.common.third.auth.entity.PlatformUser;
import tech.aiflowy.system.entity.SysAccount;
import tech.aiflowy.system.entity.SysAccountPosition;
import tech.aiflowy.system.entity.SysAccountRole;
import tech.aiflowy.system.mapper.SysAccountMapper;
import tech.aiflowy.system.mapper.SysAccountPositionMapper;
import tech.aiflowy.system.mapper.SysAccountRoleMapper;
import tech.aiflowy.system.service.SysAccountService;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户表 服务层实现。
 *
 * @author ArkLight
 * @since 2025-03-14
 */
@Service
public class SysAccountServiceImpl extends ServiceImpl<SysAccountMapper, SysAccount> implements SysAccountService {

    @Resource
    private SysAccountRoleMapper sysAccountRoleMapper;
    @Resource
    private SysAccountPositionMapper sysAccountPositionMapper;

    @Override
    public void syncRelations(SysAccount entity) {
        if (entity == null || entity.getId() == null) {
            return;
        }

        SnowFlakeIDKeyGenerator generator = new SnowFlakeIDKeyGenerator();

        //sync roleIds
        List<BigInteger> roleIds = entity.getRoleIds();
        if (roleIds != null) {
            QueryWrapper delW = QueryWrapper.create();
            delW.eq(SysAccountRole::getAccountId, entity.getId());
            sysAccountRoleMapper.deleteByQuery(delW);
            if (!roleIds.isEmpty()) {
                List<SysAccountRole> rows = new ArrayList<>(roleIds.size());
                roleIds.forEach(roleId -> {
                    SysAccountRole row = new SysAccountRole();
                    row.setAccountId(entity.getId());
                    row.setRoleId(roleId);
                    rows.add(row);
                });
                sysAccountRoleMapper.insertBatch(rows);
            }
        }

        //sync positionIds
        List<BigInteger> positionIds = entity.getPositionIds();
        if (positionIds != null) {
            QueryWrapper delW = QueryWrapper.create();
            delW.eq(SysAccountPosition::getAccountId, entity.getId());
            sysAccountPositionMapper.deleteByQuery(delW);
            if (!positionIds.isEmpty()) {
                List<SysAccountPosition> rows = new ArrayList<>(positionIds.size());
                positionIds.forEach(positionId -> {
                    SysAccountPosition row = new SysAccountPosition();
                    row.setAccountId(entity.getId());
                    row.setPositionId(positionId);
                    rows.add(row);
                });
                sysAccountPositionMapper.insertBatch(rows);
            }
        }
    }

    @Override
    public SysAccount getByUsername(String userKey) {
        QueryWrapper w = QueryWrapper.create();
        w.eq(SysAccount::getLoginName, userKey);
        return getOne(w);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysAccount savePlatformUser(String platform, PlatformUser userInfo) {
        String userKey = userInfo.getUserKey();
        SysAccount record = new SysAccount();
        record.setDeptId(Constants.DEFAULT_DEPT_ID);
        record.setTenantId(Constants.DEFAULT_TENANT_ID);
        record.setLoginName(userKey);
        record.setPassword(BCrypt.hashpw(userKey));
        record.setAccountType(EnumAccountType.NORMAL.getCode());
        record.setNickname(userInfo.getNickname());
        record.setAvatar(userInfo.getAvatar());
        record.setStatus(EnumDataStatus.AVAILABLE.getCode());
        record.setCreated(new Date());
        record.setCreatedBy(Constants.SUPER_ADMIN_ID);
        record.setModified(new Date());
        record.setModifiedBy(Constants.SUPER_ADMIN_ID);
        record.setRemark(platform);
        save(record);
        // TODO 分配一个默认角色，目前是超级管理员
        SysAccountRole accountRole = new SysAccountRole();
        accountRole.setAccountId(record.getId());
        accountRole.setRoleId(Constants.SUPER_ADMIN_ROLE_ID);
        sysAccountRoleMapper.insert(accountRole);
        return record;
    }
}
