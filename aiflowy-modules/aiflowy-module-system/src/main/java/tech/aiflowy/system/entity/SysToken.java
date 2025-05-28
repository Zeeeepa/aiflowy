package tech.aiflowy.system.entity;

import com.mybatisflex.annotation.Table;
import tech.aiflowy.system.entity.base.SysTokenBase;


/**
 * iframe 嵌入用 Token 表 实体类。
 *
 * @author Administrator
 * @since 2025-05-26
 */
@Table(value = "tb_sys_token", comment = "iframe 嵌入用 Token 表")
public class SysToken extends SysTokenBase {
}
