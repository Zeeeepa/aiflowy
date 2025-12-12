package tech.aiflowy.ai.entity;

import com.agentsflex.core.model.chat.tool.Tool;
import com.mybatisflex.annotation.Table;
import tech.aiflowy.ai.entity.base.AiWorkflowBase;

/**
 * 实体类。
 *
 * @author michael
 * @since 2024-08-23
 */

@Table("tb_ai_workflow")
public class AiWorkflow extends AiWorkflowBase {

    public Tool toFunction(boolean needEnglishName) {
        return new AiWorkflowFunction(this, needEnglishName);
    }
}
