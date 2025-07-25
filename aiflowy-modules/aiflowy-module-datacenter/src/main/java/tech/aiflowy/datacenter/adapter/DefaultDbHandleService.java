package tech.aiflowy.datacenter.adapter;

import com.alibaba.fastjson2.JSONObject;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.row.RowKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tech.aiflowy.common.constant.enums.EnumFieldType;
import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.datacenter.entity.DatacenterTable;
import tech.aiflowy.datacenter.entity.DatacenterTableFields;
import tech.aiflowy.datacenter.utils.SqlInjectionUtils;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Component("defaultDbHandleService")
public class DefaultDbHandleService extends DbHandleService {

    private static final Logger log = LoggerFactory.getLogger(DefaultDbHandleService.class);

    @Override
    public void createTable(DatacenterTable table) {
        // 设置为 [tb_dynamic_表名_tableId] 的格式
        String actualTable = table.getActualTable();
        SqlInjectionUtils.checkIdentifier(actualTable);
        // 表注释
        String tableDesc = table.getTableDesc();
        SqlInjectionUtils.checkComment(tableDesc);

        List<DatacenterTableFields> fields = table.getFields();
        StringBuilder sql = new StringBuilder("CREATE TABLE " + actualTable + " (");
        sql.append("`id` bigint unsigned NOT NULL COMMENT '主键',");
        sql.append("`dept_id` bigint unsigned NOT NULL COMMENT '部门ID',");
        sql.append("`tenant_id` bigint unsigned NOT NULL COMMENT '租户ID',");
        sql.append("`created` datetime NOT NULL COMMENT '创建时间',");
        sql.append("`created_by` bigint unsigned NOT NULL COMMENT '创建者',");
        sql.append("`modified` datetime NOT NULL COMMENT '修改时间',");
        sql.append("`modified_by` bigint unsigned NOT NULL COMMENT '修改者',");
        sql.append("`remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '备注',");
        for (DatacenterTableFields field : fields) {
            Integer required = field.getRequired();
            String fieldName = SqlInjectionUtils.checkIdentifier(field.getFieldName());
            String fieldDesc = SqlInjectionUtils.checkComment(field.getFieldDesc());
            sql.append("`").append(fieldName).append("` ")
                    .append(convertFieldType(field.getFieldType())).append(" ")
                    .append(required == 1 ? "NOT NULL" : "NULL").append(" ")
                    .append("COMMENT '").append(fieldDesc).append("',");
        }
        sql.append("PRIMARY KEY (id) USING BTREE");
        sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='").append(tableDesc).append("';");
        log.info("建表语句 >>> {}", sql);
        Db.selectObject(sql.toString());
    }

    @Override
    public void updateTable(DatacenterTable table, DatacenterTable record) {
        String tableDesc = table.getTableDesc();
        SqlInjectionUtils.checkComment(tableDesc);
        String actualTable = record.getActualTable();
        // 只允许改表备注
        if (!tableDesc.equals(record.getTableDesc())) {
            String sql = "ALTER TABLE `" + actualTable + "` "
                    + "COMMENT '" + tableDesc + "';";
            log.info("修改表备注语句 >>> {}", sql);
            Db.selectObject(sql);
        }
    }

    @Override
    public void deleteTable(DatacenterTable table) {
        String actualTable = table.getActualTable();
        String sql = "DROP TABLE IF EXISTS `" + actualTable + "`;";
        log.info("删除表语句 >>> {}", sql);
        Db.selectObject(sql);
    }

    @Override
    public String convertFieldType(Integer fieldType) {
        if (EnumFieldType.INTEGER.getCode().equals(fieldType)) {
            return "int";
        }
        if (EnumFieldType.BOOLEAN.getCode().equals(fieldType)) {
            return "int";
        }
        if (EnumFieldType.TIME.getCode().equals(fieldType)) {
            return "datetime";
        }
        if (EnumFieldType.NUMBER.getCode().equals(fieldType)) {
            return "decimal(20,6)";
        }
        return "text";
    }

    @Override
    public void addField(DatacenterTable entity, DatacenterTableFields field) {
        String fieldName = field.getFieldName();
        SqlInjectionUtils.checkIdentifier(fieldName);

        String fieldDesc = field.getFieldDesc();
        SqlInjectionUtils.checkComment(fieldDesc);

        Integer fieldType = field.getFieldType();
        Integer required = field.getRequired();
        String sql = "ALTER TABLE `" + entity.getActualTable() + "`"
                + " ADD COLUMN `" + fieldName + "` " + convertFieldType(fieldType) + " "
                + (required == 1 ? "NOT NULL" : "NULL") + " "
                + "COMMENT '" + fieldDesc + "';";
        log.info("添加字段语句 >>> {}", sql);
        Db.selectObject(sql);
    }

    @Override
    public void deleteField(DatacenterTable entity, DatacenterTableFields field) {

        String fieldName = field.getFieldName();
        SqlInjectionUtils.checkIdentifier(fieldName);

        String sql = "ALTER TABLE `" + entity.getActualTable() + "`"
                + " DROP COLUMN `" + fieldName + "`;";
        log.info("删除字段语句 >>> {}", sql);
        Db.selectObject(sql);
    }

    @Override
    public void updateField(DatacenterTable entity, DatacenterTableFields fieldRecord, DatacenterTableFields field) {
        String actualTable = entity.getActualTable();
        // 是否必填
        Integer required = field.getRequired();
        // 字段名称
        String fieldName = field.getFieldName();
        SqlInjectionUtils.checkIdentifier(fieldName);
        // 字段描述
        String fieldDesc = field.getFieldDesc();
        SqlInjectionUtils.checkComment(fieldDesc);

        String nullable = required == 1 ? "NOT NULL " : "NULL ";
        String desc = "COMMENT '" + fieldDesc + "';";

        boolean isUpdate = false;
        String handleType = "MODIFY COLUMN `" + fieldRecord.getFieldName() + "` ";

        if (!required.equals(fieldRecord.getRequired())) {
            isUpdate = true;
        }

        if (!fieldDesc.equals(fieldRecord.getFieldDesc())) {
            isUpdate = true;
        }

        if (!fieldName.equals(fieldRecord.getFieldName())) {
            isUpdate = true;
            handleType = "CHANGE COLUMN `" + fieldRecord.getFieldName() + "` `" + fieldName + "` ";
        }

        if (isUpdate) {
            String sql = "ALTER TABLE `" + actualTable + "` "
                    + handleType
                    + convertFieldType(field.getFieldType()) + " "
                    + nullable
                    + desc;
            log.info("更新字段语句 >>> {}", sql);
            Db.selectObject(sql);
        }
    }

    @Override
    public void saveValue(DatacenterTable entity, JSONObject object, LoginAccount account) {
        String actualTable = entity.getActualTable();
        List<DatacenterTableFields> fields = entity.getFields();

        Row row = Row.ofKey(RowKey.SNOW_FLAKE_ID);
        row.put("dept_id", account.getDeptId());
        row.put("tenant_id", account.getTenantId());
        row.put("created", new Date());
        row.put("created_by", account.getId());
        row.put("modified", new Date());
        row.put("modified_by", account.getId());
        row.put("remark", object.get("remark"));
        for (DatacenterTableFields field : fields) {
            String fieldName = field.getFieldName();
            row.put(fieldName, object.get(fieldName));
        }

        Db.insert(actualTable, row);
    }

    @Override
    public void updateValue(DatacenterTable entity, JSONObject object, LoginAccount account) {
        String actualTable = entity.getActualTable();
        List<DatacenterTableFields> fields = entity.getFields();

        Row row = Row.ofKey("id", object.get("id"));
        row.put("modified", new Date());
        row.put("modified_by", account.getId());
        for (DatacenterTableFields field : fields) {
            String fieldName = field.getFieldName();
            row.put(fieldName, object.get(fieldName));
        }

        Db.updateById(actualTable, row);
    }

    @Override
    public void removeValue(DatacenterTable entity, BigInteger id, LoginAccount account) {
        String actualTable = entity.getActualTable();
        Row row = Row.ofKey("id", id);
        Db.deleteById(actualTable, row);
    }
}
