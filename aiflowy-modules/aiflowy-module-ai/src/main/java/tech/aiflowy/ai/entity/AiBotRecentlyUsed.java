package tech.aiflowy.ai.entity;

import com.mybatisflex.annotation.Table;
import tech.aiflowy.ai.entity.base.AiBotRecentlyUsedBase;

/**
 * 最近使用 实体类。
 *
 * @author ArkLight
 * @since 2025-12-18
 */
@Table(value = "tb_ai_bot_recently_used", comment = "最近使用")
public class AiBotRecentlyUsed extends AiBotRecentlyUsedBase {
}
