package tech.aiflowy.ai.entity;

import com.agentsflex.core.model.chat.tool.Tool;
import com.mybatisflex.annotation.Table;
import tech.aiflowy.ai.entity.base.WorkflowBase;

/**
 * 实体类。
 *
 * @author michael
 * @since 2024-08-23
 */

@Table("tb_workflow")
public class Workflow extends WorkflowBase {

    public Tool toFunction(boolean needEnglishName) {
        return new WorkflowTool(this, needEnglishName);
    }
}
