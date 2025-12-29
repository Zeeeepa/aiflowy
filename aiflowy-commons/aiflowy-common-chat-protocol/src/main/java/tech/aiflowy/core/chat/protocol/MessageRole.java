package tech.aiflowy.core.chat.protocol;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 对话消息角色枚举
 */
public enum MessageRole {
    /**
     * 用户角色：用户发送的消息
     */
    USER("user"),

    /**
     * 助手角色：AI/机器人返回的消息
     */
    ASSISTANT("assistant"),

    /**
     * 系统角色：系统级提示、日志、状态消息
     */
    SYSTEM("system"),

    /**
     * 工具角色：工具自动发送/返回的消息
     */
    TOOL("tool");

    private final String value;

    /**
     * 私有构造方法，绑定枚举常量与对应值
     * @param value 数据库存储/协议传输的字符串值
     */
    MessageRole(String value) {
        this.value = value;
    }

    /**
     * 获取枚举对应的字符串值（用于数据库持久化、协议传输）
     * @JsonValue 注解：Jackson 序列化时，自动返回该值（避免序列化出枚举名称）
     * @return 小写字符串（如 "user"、"assistant"）
     */
    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
