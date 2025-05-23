package tech.aiflowy.ai.service;

import com.mybatisflex.core.service.IService;
import tech.aiflowy.ai.entity.AiPluginCategories;
import tech.aiflowy.common.domain.Result;

/**
 *  服务层。
 *
 * @author Administrator
 * @since 2025-05-21
 */
public interface AiPluginCategoriesService extends IService<AiPluginCategories> {

    Result doRemoveCategory(Integer id);
}
