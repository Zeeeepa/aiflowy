package tech.aiflowy.ai.service.impl;

import tech.aiflowy.ai.entity.AiBotWorkflow;
import tech.aiflowy.ai.mapper.AiBotWorkflowMapper;
import tech.aiflowy.ai.service.AiBotWorkflowService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import com.mybatisflex.core.query.QueryWrapper;

/**
 *  服务层实现。
 *
 * @author michael
 * @since 2024-08-28
 */
@Service
public class AiBotWorkflowServiceImpl extends ServiceImpl<AiBotWorkflowMapper, AiBotWorkflow> implements AiBotWorkflowService {

    @Override
    public List<AiBotWorkflow> listByBotId(BigInteger botId) {

        QueryWrapper queryWrapper = QueryWrapper.create();
        queryWrapper.eq("bot_id",botId);

        return list(queryWrapper);
    }
}
