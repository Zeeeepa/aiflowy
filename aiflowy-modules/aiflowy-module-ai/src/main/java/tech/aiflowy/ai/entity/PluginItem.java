package tech.aiflowy.ai.entity;

import com.agentsflex.core.model.chat.tool.Tool;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import tech.aiflowy.ai.entity.base.PluginItemBase;


/**
 *  实体类。
 *
 * @author Administrator
 * @since 2025-04-27
 */
@Table("tb_plugin_item")
public class PluginItem extends PluginItemBase {

    @Column(ignore = true)
    private boolean joinBot;

    public boolean isJoinBot() {
        return joinBot;
    }

    public void setJoinBot(boolean joinBot) {
        this.joinBot = joinBot;
    }

    public Tool toFunction() {
        return new PluginTool(this);
    }

}
