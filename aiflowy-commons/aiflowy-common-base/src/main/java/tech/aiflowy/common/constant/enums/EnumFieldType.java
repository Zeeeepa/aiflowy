package tech.aiflowy.common.constant.enums;

import tech.aiflowy.common.annotation.DictDef;

/**
 * 数据中枢 - 字段类型枚举
 */
@DictDef(name = "字段类型", code = "fieldType", keyField = "code", labelField = "text")
public enum EnumFieldType {

    STRING(1, "String"),
    INTEGER(2, "Integer"),
    TIME(3, "Time"),
    NUMBER(4, "Number"),
    BOOLEAN(5, "Boolean"),

    ;

    private Integer code;
    private String text;

    EnumFieldType(Integer code, String text) {
        this.code = code;
        this.text = text;
    }

    public static EnumFieldType getByCode(Integer code) {
        if (null == code) {
            return null;
        }
        for (EnumFieldType type : EnumFieldType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new RuntimeException("内容类型非法");
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }}
