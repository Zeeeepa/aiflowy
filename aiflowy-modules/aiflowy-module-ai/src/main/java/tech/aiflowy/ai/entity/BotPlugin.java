package tech.aiflowy.ai.entity;

import com.mybatisflex.annotation.RelationOneToOne;
import com.mybatisflex.annotation.Table;
import tech.aiflowy.ai.entity.base.BotPluginBase;


/**
 *  实体类。
 *
 * @author michael
 * @since 2025-04-07
 */
@Table("tb_bot_plugin")
public class BotPlugin extends BotPluginBase {

    @RelationOneToOne(selfField = "pluginId", targetField = "id")
    private Plugin plugin;

    public Plugin getAiPlugin() {
        return plugin;
    }

    public void setAiPlugin(Plugin plugin) {
        this.plugin = plugin;
    }
}
