package tech.aiflowy.system.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.exceptions.BusinessException;
import tech.aiflowy.system.entity.SysApiKey;
import tech.aiflowy.system.mapper.SysApiKeyMapper;
import tech.aiflowy.system.service.SysApiKeyService;

import java.util.Date;

/**
 *  服务层实现。
 *
 * @author Administrator
 * @since 2025-04-18
 */
@Service
public class SysApiKeyServiceImpl extends ServiceImpl<SysApiKeyMapper, SysApiKey>  implements SysApiKeyService {

    @Override
    public void checkApiKey(String apiKey) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select("api_key", "status", "expired_at")
                .from("tb_sys_api_key")
                .where("api_key = ? ", apiKey);
        SysApiKey aiBotApiKey =  getOne(queryWrapper);
        if (aiBotApiKey == null ){
            throw new BusinessException("该apiKey不存在");
        }
        if (aiBotApiKey.getStatus() == 0 ){
            throw new BusinessException("该apiKey未启用");
        }
        if (aiBotApiKey.getExpiredAt().getTime() < new Date().getTime()){
            throw new BusinessException("该apiKey已失效");
        }
    }
}
