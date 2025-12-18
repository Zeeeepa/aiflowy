package tech.aiflowy.admin.controller.ai;

import tech.aiflowy.ai.entity.AiBotCategory;
import tech.aiflowy.ai.service.AiBotCategoryService;
import tech.aiflowy.common.annotation.UsePermission;
import tech.aiflowy.common.web.controller.BaseCurdController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * bot分类 控制层。
 *
 * @author ArkLight
 * @since 2025-12-18
 */
@RestController
@RequestMapping("/api/v1/aiBotCategory")
@UsePermission(moduleName = "/api/v1/aiBot")
public class AiBotCategoryController extends BaseCurdController<AiBotCategoryService, AiBotCategory> {
    public AiBotCategoryController(AiBotCategoryService service) {
        super(service);
    }
}