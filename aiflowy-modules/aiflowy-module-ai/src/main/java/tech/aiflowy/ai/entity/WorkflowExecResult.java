package tech.aiflowy.ai.entity;

import com.mybatisflex.annotation.Table;
import tech.aiflowy.ai.entity.base.WorkflowExecResultBase;


/**
 * 工作流执行记录 实体类。
 *
 * @author ArkLight
 * @since 2025-05-28
 */
@Table(value = "tb_workflow_exec_result", comment = "工作流执行记录")
public class WorkflowExecResult extends WorkflowExecResultBase {

    public Long getExecTime() {
        if (getEndTime() == null) {
            return null;
        }
        return getEndTime().getTime() - getStartTime().getTime();
    }
}
