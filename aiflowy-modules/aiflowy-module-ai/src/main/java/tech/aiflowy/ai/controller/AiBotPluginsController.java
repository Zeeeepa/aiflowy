package tech.aiflowy.ai.controller;

import org.springframework.web.bind.annotation.PostMapping;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.tree.Tree;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.ai.entity.AiBotPlugins;
import tech.aiflowy.ai.service.AiBotPluginsService;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.common.web.jsonbody.JsonBody;

import javax.annotation.Resource;
import java.util.List;

/**
 *  控制层。
 *
 * @author michael
 * @since 2025-04-07
 */
@RestController
@RequestMapping("/api/v1/aiBotPlugins")
public class AiBotPluginsController extends BaseCurdController<AiBotPluginsService, AiBotPlugins> {

    public AiBotPluginsController(AiBotPluginsService service) {
        super(service);
    }

    @Resource
    private AiBotPluginsService aiBotPluginsService;

    @GetMapping("list")
    public Result list(AiBotPlugins entity, Boolean asTree, String sortKey, String sortType){

        QueryWrapper queryWrapper = QueryWrapper.create(entity, buildOperators(entity));
        queryWrapper.orderBy(buildOrderBy(sortKey, sortType, getDefaultOrderBy()));

        List<AiBotPlugins> aiBotPlugins = service.getMapper().selectListWithRelationsByQuery(queryWrapper);

        List<AiBotPlugins> list = Tree.tryToTree(aiBotPlugins, asTree);

         return Result.success(list);
    }

    @PostMapping("/getList")
    public Result getList(@JsonBody(value = "botId", required = true) String botId){
//        QueryWrapper queryWrapper = QueryWrapper.create().select("plugin_id").where("bot_id = ?", botId);
//        List<AiBotPlugins> list = service.list(queryWrapper);
        return aiBotPluginsService.getList(botId);
    }

    @PostMapping("/doRemove")
    public Result doRemove(@JsonBody(value = "botId", required = true) String botId,
                           @JsonBody(value = "pluginId", required = true) String pluginId){
        return aiBotPluginsService.doRemove(botId, pluginId);
    }

}
