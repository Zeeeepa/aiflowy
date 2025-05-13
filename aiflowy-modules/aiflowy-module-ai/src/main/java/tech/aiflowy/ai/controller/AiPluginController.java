package tech.aiflowy.ai.controller;

import org.springframework.web.bind.annotation.PostMapping;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.controller.BaseCurdController;
import tech.aiflowy.ai.entity.AiPlugin;
import tech.aiflowy.ai.service.AiPluginService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.common.web.jsonbody.JsonBody;

import javax.annotation.Resource;

/**
 *  控制层。
 *
 * @author Administrator
 * @since 2025-04-25
 */
@RestController
@RequestMapping("/api/v1/aiPlugin")
public class AiPluginController extends BaseCurdController<AiPluginService, AiPlugin> {
    public AiPluginController(AiPluginService service) {
        super(service);
    }

    @Resource
    AiPluginService aiPluginService;

    @Override
    protected Result onSaveOrUpdateBefore(AiPlugin entity, boolean isSave) {
//        LoginAccount loginUser = SaTokenUtil.getLoginAccount();
//        commonFiled(entity,loginUser.getId(),loginUser.getTenantId(), loginUser.getDeptId());
//
        return super.onSaveOrUpdateBefore(entity, isSave);
    }

    @PostMapping("/plugin/save")
    public Result savePlugin(@JsonBody AiPlugin aiPlugin){

        return aiPluginService.savePlugin(aiPlugin);
    }

    @PostMapping("/plugin/remove")
    public Result removePlugin(@JsonBody(value = "id", required = true) String id){

        return aiPluginService.removePlugin(id);
    }

    @PostMapping("/plugin/update")
    public Result updatePlugin(@JsonBody AiPlugin aiPlugin){

        return aiPluginService.updatePlugin(aiPlugin);
    }

    @PostMapping("/getList")
    public Result getList(@JsonBody(value = "botId", required = true) String botId){
//        QueryWrapper queryWrapper = QueryWrapper.create().select("plugin_id").where("bot_id = ?", botId);
//        List<AiBotPlugins> list = service.list(queryWrapper);
        return aiPluginService.getList(botId);
    }
}
