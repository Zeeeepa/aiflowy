SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_datacenter_table
-- ----------------------------
DROP TABLE IF EXISTS `tb_datacenter_table`;
CREATE TABLE `tb_datacenter_table`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '主键',
  `dept_id` bigint UNSIGNED NOT NULL COMMENT '部门ID',
  `tenant_id` bigint UNSIGNED NOT NULL COMMENT '租户ID',
  `table_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '数据表名',
  `table_desc` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '数据表描述',
  `actual_table` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '物理表名',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '数据状态',
  `created` datetime NOT NULL COMMENT '创建时间',
  `created_by` bigint UNSIGNED NOT NULL COMMENT '创建者',
  `modified` datetime NOT NULL COMMENT '修改时间',
  `modified_by` bigint UNSIGNED NOT NULL COMMENT '修改者',
  `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '扩展项',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '数据中枢表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_datacenter_table_fields
-- ----------------------------
DROP TABLE IF EXISTS `tb_datacenter_table_fields`;
CREATE TABLE `tb_datacenter_table_fields`  (
  `id` bigint UNSIGNED NOT NULL COMMENT '主键',
  `table_id` bigint UNSIGNED NOT NULL COMMENT '数据表ID',
  `field_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '字段名称',
  `field_desc` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '字段描述',
  `field_type` int NOT NULL COMMENT '字段类型',
  `required` int NOT NULL DEFAULT 0 COMMENT '是否必填',
  `created` datetime NOT NULL COMMENT '创建时间',
  `created_by` bigint UNSIGNED NOT NULL COMMENT '创建者',
  `modified` datetime NOT NULL COMMENT '修改时间',
  `modified_by` bigint UNSIGNED NOT NULL COMMENT '修改者',
  `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '扩展项',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '数据中枢字段表' ROW_FORMAT = Dynamic;

INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (300817858217091072, 259169540360232960, 0, '数据中枢', '/datacenter/table', '', 'BookFilled', 1, '', 53, 0, '2025-07-10 15:23:46', 1, '2025-07-10 15:24:36', 1, '', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (300818298270883840, 300817858217091072, 1, '查询', '', '', '', 1, '/api/v1/datacenterTable/query', 0, 0, '2025-07-10 15:25:31', 1, '2025-07-10 15:25:31', 1, '', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (300818387710222336, 300817858217091072, 1, '保存', '', '', '', 1, '/api/v1/datacenterTable/save', 0, 0, '2025-07-10 15:25:53', 1, '2025-07-10 15:25:53', 1, '', 0);
INSERT INTO `tb_sys_menu` (`id`, `parent_id`, `menu_type`, `menu_title`, `menu_url`, `component`, `menu_icon`, `is_show`, `permission_tag`, `sort_no`, `status`, `created`, `created_by`, `modified`, `modified_by`, `remark`, `is_deleted`) VALUES (300818488214134784, 300817858217091072, 1, '删除', '', '', '', 1, '/api/v1/datacenterTable/remove', 0, 0, '2025-07-10 15:26:17', 1, '2025-07-10 15:26:17', 1, '', 0);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (300817858284199936, 1, 300817858217091072);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (300818298325409792, 1, 300818298270883840);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (300818387789914112, 1, 300818387710222336);
INSERT INTO `tb_sys_role_menu` (`id`, `role_id`, `menu_id`) VALUES (300818488344158208, 1, 300818488214134784);

SET FOREIGN_KEY_CHECKS = 1;


ALTER TABLE tb_ai_bot
    ADD alias VARCHAR(255) NOT NULL DEFAULT '' COMMENT '别名' AFTER id;

UPDATE tb_ai_bot SET alias = CONCAT('bot_', id) WHERE alias = '';

CREATE UNIQUE INDEX tb_ai_bot_alias_uindex
    ON tb_ai_bot (alias);

ALTER TABLE tb_ai_plugin
    ADD alias VARCHAR(255) NOT NULL DEFAULT '' COMMENT '别名' AFTER id;

UPDATE tb_ai_plugin SET alias = CONCAT('plugin', id) WHERE alias = '';

CREATE UNIQUE INDEX tb_ai_plugin_alias_uindex
    ON tb_ai_plugin (alias);

ALTER TABLE tb_ai_workflow
    ADD alias VARCHAR(255) NOT NULL DEFAULT '' COMMENT '别名' AFTER id;

UPDATE tb_ai_workflow SET alias = CONCAT('workflow', id) WHERE alias = '';

CREATE UNIQUE INDEX tb_ai_workflow_alias_uindex
    ON tb_ai_workflow (alias);

ALTER TABLE tb_ai_knowledge
    ADD alias VARCHAR(255) NOT NULL DEFAULT '' COMMENT '别名' AFTER id;

UPDATE tb_ai_knowledge SET alias = CONCAT('knowledge', id) WHERE alias = '';

CREATE UNIQUE INDEX tb_ai_knowledge_alias_uindex
    ON tb_ai_knowledge (alias);