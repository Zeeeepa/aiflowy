package tech.aiflowy.ai.entity;

import tech.aiflowy.ai.entity.base.BotWorkflowBase;
import com.mybatisflex.annotation.RelationOneToOne;
import com.mybatisflex.annotation.Table;

/**
 *  实体类。
 *
 * @author michael
 * @since 2024-08-28
 */

@Table("tb_bot_workflow")
public class BotWorkflow extends BotWorkflowBase {

    @RelationOneToOne(selfField = "workflowId", targetField = "id")
    private Workflow workflow;

    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }
}
