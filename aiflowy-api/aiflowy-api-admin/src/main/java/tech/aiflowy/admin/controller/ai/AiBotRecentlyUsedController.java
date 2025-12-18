package tech.aiflowy.admin.controller.ai;

import tech.aiflowy.ai.entity.AiBotRecentlyUsed;
import tech.aiflowy.ai.service.AiBotRecentlyUsedService;
import tech.aiflowy.common.annotation.UsePermission;
import tech.aiflowy.common.web.controller.BaseCurdController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 最近使用 控制层。
 *
 * @author ArkLight
 * @since 2025-12-18
 */
@RestController
@RequestMapping("/api/v1/aiBotRecentlyUsed")
@UsePermission(moduleName = "/api/v1/aiBot")
public class AiBotRecentlyUsedController extends BaseCurdController<AiBotRecentlyUsedService, AiBotRecentlyUsed> {
    public AiBotRecentlyUsedController(AiBotRecentlyUsedService service) {
        super(service);
    }
}