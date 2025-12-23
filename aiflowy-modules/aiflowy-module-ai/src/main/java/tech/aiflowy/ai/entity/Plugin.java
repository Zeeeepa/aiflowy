package tech.aiflowy.ai.entity;

import com.mybatisflex.annotation.RelationOneToMany;
import com.mybatisflex.annotation.Table;
import tech.aiflowy.ai.entity.base.PluginBase;

import java.util.List;


/**
 *  实体类。
 *
 * @author Administrator
 * @since 2025-04-25
 */
@Table("tb_plugin")
public class Plugin extends PluginBase {

    @RelationOneToMany(selfField = "id", targetField = "pluginId", targetTable = "tb_plugin_item")
    private List<PluginItem> tools;

    public String getTitle() {
        return this.getName();
    }

    public List<PluginItem> getTools() {
        return tools;
    }

    public void setTools(List<PluginItem> tools) {
        this.tools = tools;
    }
}
