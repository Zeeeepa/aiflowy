package tech.aiflowy.common.util;

import java.util.Map;

/**
 * Map工具类，提供从Map中安全获取指定类型值的方法。
 */
public class MapUtil {

    /**
     * 从Map中获取字符串类型的值。
     *
     * @param map 包含键值对的Map对象
     * @param key 要查找的键
     * @return 对应键的字符串值，如果不存在或转换失败则返回null
     */
    public static String getString(Map<String, Object> map, String key) {
        if (map == null) return null;
        return toString(map.get(key));
    }

    /**
     * 从Map中获取字符串类型的值，并支持默认值。
     *
     * @param map          包含键值对的Map对象
     * @param key          要查找的键
     * @param defaultValue 默认返回值
     * @return 对应键的字符串值，如果不存在或转换失败则返回默认值
     */
    public static String getString(Map<String, Object> map, String key, String defaultValue) {
        if (map == null) return defaultValue;
        String value = toString(map.get(key));
        return value == null ? defaultValue : value;
    }

    /**
     * 从Map中获取整数类型的值。
     *
     * @param map 包含键值对的Map对象
     * @param key 要查找的键
     * @return 对应键的整数值，如果不存在或转换失败则返回null
     */
    public static Integer getInteger(Map<String, Object> map, String key) {
        if (map == null) return null;
        return toInteger(map.get(key));
    }

    /**
     * 从Map中获取整数类型的值，并支持默认值。
     *
     * @param map          包含键值对的Map对象
     * @param key          要查找的键
     * @param defaultValue 默认返回值
     * @return 对应键的整数值，如果不存在或转换失败则返回默认值
     */
    public static Integer getInteger(Map<String, Object> map, String key, Integer defaultValue) {
        if (map == null) return defaultValue;
        Integer value = toInteger(map.get(key));
        return value == null ? defaultValue : value;
    }

    /**
     * 从Map中获取长整数类型的值。
     *
     * @param map 包含键值对的Map对象
     * @param key 要查找的键
     * @return 对应键的长整数值，如果不存在或转换失败则返回null
     */
    public static Long getLong(Map<String, Object> map, String key) {
        if (map == null) return null;
        return toLong(map.get(key));
    }

    /**
     * 从Map中获取长整数类型的值，并支持默认值。
     *
     * @param map          包含键值对的Map对象
     * @param key          要查找的键
     * @param defaultValue 默认返回值
     * @return 对应键的长整数值，如果不存在或转换失败则返回默认值
     */
    public static Long getLong(Map<String, Object> map, String key, Long defaultValue) {
        if (map == null) return defaultValue;
        Long value = toLong(map.get(key));
        return value == null ? defaultValue : value;
    }

    /**
     * 从Map中获取双精度浮点数类型的值。
     *
     * @param map 包含键值对的Map对象
     * @param key 要查找的键
     * @return 对应键的双精度浮点数值，如果不存在或转换失败则返回null
     */
    public static Double getDouble(Map<String, Object> map, String key) {
        if (map == null) return null;
        return toDouble(map.get(key));
    }

    /**
     * 从Map中获取双精度浮点数类型的值，并支持默认值。
     *
     * @param map          包含键值对的Map对象
     * @param key          要查找的键
     * @param defaultValue 默认返回值
     * @return 对应键的双精度浮点数值，如果不存在或转换失败则返回默认值
     */
    public static Double getDouble(Map<String, Object> map, String key, Double defaultValue) {
        if (map == null) return defaultValue;
        Double value = toDouble(map.get(key));
        return value == null ? defaultValue : value;
    }

    /**
     * 从Map中获取布尔类型的值。
     *
     * @param map 包含键值对的Map对象
     * @param key 要查找的键
     * @return 对应键的布尔值，如果不存在或转换失败则返回null
     */
    public static Boolean getBoolean(Map<String, Object> map, String key) {
        if (map == null) return null;
        return toBoolean(map.get(key));
    }

    /**
     * 从Map中获取布尔类型的值，并支持默认值。
     *
     * @param map          包含键值对的Map对象
     * @param key          要查找的键
     * @param defaultValue 默认返回值
     * @return 对应键的布尔值，如果不存在或转换失败则返回默认值
     */
    public static Boolean getBoolean(Map<String, Object> map, String key, Boolean defaultValue) {
        if (map == null) return defaultValue;
        Boolean value = toBoolean(map.get(key));
        return value == null ? defaultValue : value;
    }

    /**
     * 将给定对象转换为字符串表示形式。
     *
     * @param obj 待转换的对象
     * @return 字符串结果，若原对象为null则返回null；如果是String类型直接返回；否则调用toString()
     */
    private static String toString(Object obj) {
        if (obj == null) return null;
        if (obj instanceof String) return (String) obj;
        return obj.toString();
    }

    /**
     * 将给定对象转换为整数。
     *
     * @param obj 待转换的对象
     * @return 整数结果，若原对象为null则返回null；如果是Number子类则取其int值；否则尝试解析字符串
     */
    private static Integer toInteger(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Number) return ((Number) obj).intValue();
        return Integer.parseInt(obj.toString());
    }

    /**
     * 将给定对象转换为长整数。
     *
     * @param obj 待转换的对象
     * @return 长整数结果，若原对象为null则返回null；如果是Number子类则取其long值；否则尝试解析字符串
     */
    private static Long toLong(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Number) return ((Number) obj).longValue();
        return Long.parseLong(obj.toString());
    }

    /**
     * 将给定对象转换为双精度浮点数。
     *
     * @param obj 待转换的对象
     * @return 双精度浮点数结果，若原对象为null则返回null；如果是Number子类则取其double值；否则尝试解析字符串
     */
    private static Double toDouble(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Number) return ((Number) obj).doubleValue();
        return Double.parseDouble(obj.toString());
    }

    /**
     * 将给定对象转换为布尔值。
     *
     * @param obj 待转换的对象
     * @return 布尔值结果，若原对象为null则返回null；如果是Boolean类型直接返回；否则尝试解析字符串
     */
    private static Boolean toBoolean(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Boolean) return (Boolean) obj;
        return Boolean.parseBoolean(obj.toString());
    }

}
