package tech.aiflowy.usercenter.controller.ai;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.ai.entity.AiBotCategory;
import tech.aiflowy.ai.service.AiBotCategoryService;
import tech.aiflowy.common.annotation.UsePermission;
import tech.aiflowy.common.web.controller.BaseCurdController;

/**
 * bot分类 控制层。
 *
 * @author ArkLight
 * @since 2025-12-18
 */
@RestController
@RequestMapping("/userCenter/aiBotCategory")
@UsePermission(moduleName = "/api/v1/aiBot")
public class UcAiBotCategoryController extends BaseCurdController<AiBotCategoryService, AiBotCategory> {
    public UcAiBotCategoryController(AiBotCategoryService service) {
        super(service);
    }
}