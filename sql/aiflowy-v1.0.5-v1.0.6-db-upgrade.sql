SET FOREIGN_KEY_CHECKS = 0;
-- ----------------------------
-- 修改 quartz 表名 开始，对于没有忽略大小写的数据库有效
-- ----------------------------
-- 改回默认
RENAME TABLE tb_qrtz_blob_triggers TO qrtz_blob_triggers;
RENAME TABLE tb_qrtz_calendars TO qrtz_calendars;
RENAME TABLE tb_qrtz_cron_triggers TO qrtz_cron_triggers;
RENAME TABLE tb_qrtz_fired_triggers TO qrtz_fired_triggers;
RENAME TABLE tb_qrtz_job_details TO qrtz_job_details;
RENAME TABLE tb_qrtz_locks TO qrtz_locks;
RENAME TABLE tb_qrtz_paused_trigger_grps TO qrtz_paused_trigger_grps;
RENAME TABLE tb_qrtz_scheduler_state TO qrtz_scheduler_state;
RENAME TABLE tb_qrtz_simple_triggers TO qrtz_simple_triggers;
RENAME TABLE tb_qrtz_simprop_triggers TO qrtz_simprop_triggers;
RENAME TABLE tb_qrtz_triggers TO qrtz_triggers;
-- 改为大写
RENAME TABLE qrtz_blob_triggers TO TB_QRTZ_BLOB_TRIGGERS;
RENAME TABLE qrtz_calendars TO TB_QRTZ_CALENDARS;
RENAME TABLE qrtz_cron_triggers TO TB_QRTZ_CRON_TRIGGERS;
RENAME TABLE qrtz_fired_triggers TO TB_QRTZ_FIRED_TRIGGERS;
RENAME TABLE qrtz_job_details TO TB_QRTZ_JOB_DETAILS;
RENAME TABLE qrtz_locks TO TB_QRTZ_LOCKS;
RENAME TABLE qrtz_paused_trigger_grps TO TB_QRTZ_PAUSED_TRIGGER_GRPS;
RENAME TABLE qrtz_scheduler_state TO TB_QRTZ_SCHEDULER_STATE;
RENAME TABLE qrtz_simple_triggers TO TB_QRTZ_SIMPLE_TRIGGERS;
RENAME TABLE qrtz_simprop_triggers TO TB_QRTZ_SIMPROP_TRIGGERS;
RENAME TABLE qrtz_triggers TO TB_QRTZ_TRIGGERS;
-- ----------------------------
-- 修改 quartz 表名 结束
-- ----------------------------
SET FOREIGN_KEY_CHECKS = 1;