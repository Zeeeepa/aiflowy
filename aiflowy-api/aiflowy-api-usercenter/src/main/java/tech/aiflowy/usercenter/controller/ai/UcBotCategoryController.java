package tech.aiflowy.usercenter.controller.ai;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.ai.entity.BotCategory;
import tech.aiflowy.ai.service.BotCategoryService;
import tech.aiflowy.common.annotation.UsePermission;
import tech.aiflowy.common.web.controller.BaseCurdController;

/**
 * bot分类 控制层。
 *
 * @author ArkLight
 * @since 2025-12-18
 */
@RestController
@RequestMapping("/userCenter/botCategory")
@UsePermission(moduleName = "/api/v1/bot")
public class UcBotCategoryController extends BaseCurdController<BotCategoryService, BotCategory> {
    public UcBotCategoryController(BotCategoryService service) {
        super(service);
    }
}