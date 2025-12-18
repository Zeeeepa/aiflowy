package tech.aiflowy.usercenter.controller.ai;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.ai.entity.AiBotRecentlyUsed;
import tech.aiflowy.ai.service.AiBotRecentlyUsedService;
import tech.aiflowy.common.annotation.UsePermission;
import tech.aiflowy.common.web.controller.BaseCurdController;

/**
 * 最近使用 控制层。
 *
 * @author ArkLight
 * @since 2025-12-18
 */
@RestController
@RequestMapping("/userCenter/aiBotRecentlyUsed")
@UsePermission(moduleName = "/api/v1/aiBot")
public class UcBotRecentlyUsedController extends BaseCurdController<AiBotRecentlyUsedService, AiBotRecentlyUsed> {
    public UcBotRecentlyUsedController(AiBotRecentlyUsedService service) {
        super(service);
    }
}