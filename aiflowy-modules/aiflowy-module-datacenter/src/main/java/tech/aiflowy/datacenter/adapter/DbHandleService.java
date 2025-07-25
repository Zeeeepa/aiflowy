package tech.aiflowy.datacenter.adapter;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSONObject;
import tech.aiflowy.common.constant.enums.EnumFieldType;
import tech.aiflowy.common.entity.LoginAccount;
import tech.aiflowy.common.web.exceptions.BusinessException;
import tech.aiflowy.datacenter.entity.DatacenterTable;
import tech.aiflowy.datacenter.entity.DatacenterTableFields;

import java.math.BigDecimal;
import java.math.BigInteger;

public abstract class DbHandleService {

    public abstract void createTable(DatacenterTable table);

    public abstract void updateTable(DatacenterTable table, DatacenterTable record);

    public abstract void deleteTable(DatacenterTable table);

    /**
     * @see tech.aiflowy.common.constant.enums.EnumFieldType
     */
    public abstract String convertFieldType(Integer fieldType);

    public abstract void addField(DatacenterTable entity, DatacenterTableFields field);

    public abstract void deleteField(DatacenterTable entity, DatacenterTableFields field);

    public abstract void updateField(DatacenterTable entity, DatacenterTableFields fieldRecord, DatacenterTableFields field);

    public abstract void saveValue(DatacenterTable entity, JSONObject object, LoginAccount account);

    public abstract void updateValue(DatacenterTable entity, JSONObject object, LoginAccount account);

    public abstract void removeValue(DatacenterTable entity, BigInteger id, LoginAccount account);
    public Object convertFieldValue(Integer fieldType, String fieldValue) {
        if (fieldType.equals(EnumFieldType.INTEGER.getCode()) || fieldType.equals(EnumFieldType.BOOLEAN.getCode())) {
            return Integer.parseInt(fieldValue);
        }
        if (fieldType.equals(EnumFieldType.TIME.getCode())) {
            return DateUtil.parse(fieldValue);
        }
        if (fieldType.equals(EnumFieldType.NUMBER.getCode())) {
            return new BigDecimal(fieldValue);
        }
        return fieldValue;
    }
}
