package tech.aiflowy.ai.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import tech.aiflowy.ai.entity.AiBotPlugins;
import tech.aiflowy.ai.entity.AiPlugin;
import tech.aiflowy.ai.mapper.AiBotPluginsMapper;
import tech.aiflowy.ai.mapper.AiPluginMapper;
import tech.aiflowy.ai.service.AiBotPluginsService;
import org.springframework.stereotype.Service;
import tech.aiflowy.common.domain.Result;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.List;

/**
 *  服务层实现。
 *
 * @author michael
 * @since 2025-04-07
 */
@Service
public class AiBotPluginsServiceImpl extends ServiceImpl<AiBotPluginsMapper, AiBotPlugins>  implements AiBotPluginsService{

    @Resource
    private AiBotPluginsMapper aiBotPluginsMapper;

    @Resource
    private AiPluginMapper aiPluginMapper;

    @Override
    public Result getList(String botId) {
        QueryWrapper queryWrapper = QueryWrapper.create().select("plugin_tool_id").where("bot_id = ?", botId);
        List<BigInteger> pluginIds = aiBotPluginsMapper.selectListByQueryAs(queryWrapper, BigInteger.class);
        List<AiPlugin> aiPlugins = aiPluginMapper.selectListByIds(pluginIds);
        return Result.success(aiPlugins);
    }

    @Override
    public Result doRemove(String botId, String pluginToolId) {
        QueryWrapper queryWrapper = QueryWrapper.create().select("id")
                .from("tb_ai_bot_plugins")
                .where("bot_id = ?", botId)
                .where("plugin_tool_id = ?", pluginToolId);
        BigInteger id = aiBotPluginsMapper.selectOneByQueryAs(queryWrapper, BigInteger.class);
        int delete = aiBotPluginsMapper.deleteById(id);
        if (delete <= 0) {
            return Result.fail();
        }
        return Result.success();
    }
}
