SET FOREIGN_KEY_CHECKS = 0;
-- ----------------------------

ALTER TABLE `tb_ai_knowledge`
    ADD COLUMN `english_name` varchar(256) NULL COMMENT '英文名称' AFTER `search_engine_enable`;

ALTER TABLE `tb_ai_workflow`
    ADD COLUMN `english_name` varchar(256) NULL COMMENT '英文名称' AFTER `modified_by`;
-- ----------------------------
SET FOREIGN_KEY_CHECKS = 1;