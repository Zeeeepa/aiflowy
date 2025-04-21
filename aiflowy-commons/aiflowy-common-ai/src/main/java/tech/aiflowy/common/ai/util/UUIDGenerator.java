package tech.aiflowy.common.ai.util;

import java.util.UUID;

public class UUIDGenerator {

    /**
     * 生成32位不带"-"的UUID字符串
     * @return 32位不重复的UUID字符串
     */
    public static String generateUUID() {
        // 使用UUID类生成标准的UUID
        UUID uuid = UUID.randomUUID();
        // 去掉"-"并转换为小写，返回32位的字符串
        return uuid.toString().replace("-", "").toLowerCase();
    }

    public static void main(String[] args) {
        // 测试生成32位UUID
        for (int i = 0; i < 5; i++) {
            System.out.println(generateUUID());
        }
    }
}