package tech.aiflowy.usercenter.controller.ai;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.ai.entity.AiWorkflowCategory;
import tech.aiflowy.ai.service.AiWorkflowCategoryService;
import tech.aiflowy.common.annotation.UsePermission;
import tech.aiflowy.common.domain.Result;
import tech.aiflowy.common.web.controller.BaseCurdController;

import java.io.Serializable;
import java.util.Collection;

/**
 * 工作流分类
 *
 * @author ArkLight
 * @since 2025-12-11
 */
@RestController
@RequestMapping("/userCenter/aiWorkflowCategory")
@UsePermission(moduleName = "/api/v1/aiWorkflow")
public class UcAiWorkflowCategoryController extends BaseCurdController<AiWorkflowCategoryService, AiWorkflowCategory> {

    public UcAiWorkflowCategoryController(AiWorkflowCategoryService service) {
        super(service);
    }

    @Override
    protected Result<?> onSaveOrUpdateBefore(AiWorkflowCategory entity, boolean isSave) {
        return Result.fail("-");
    }

    @Override
    protected Result<?> onRemoveBefore(Collection<Serializable> ids) {
        return Result.fail("-");
    }
}