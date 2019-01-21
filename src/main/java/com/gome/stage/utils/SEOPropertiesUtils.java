package com.gome.stage.utils;

import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * Description:动态获取diamond配置
 * User: qinruixin-ds
 * Date: 2018-11-19
 * Time: 16:14
 */
public class SEOPropertiesUtils {

    private static Properties properties;

    public static Properties getProperties() {
        return properties;
    }

    public static void setProperties(Properties properties) {
        SEOPropertiesUtils.properties = properties;
    }

    public static String getStringValueByKey(String key, String defaultV) {
        Object value = getValueByKey(key);
        if (value == null) {
            return defaultV;
        }
        return (String) value;
    }

    public static Object getValueByKey(String key) {
        if (properties == null) {
            System.out.println("properties为null");
             return null;
        }
        Object value = properties.get(key);
        if (value == null) {
            System.out.println("diamond中"+key+"配置没有找到");
        }
        return value;
    }

    public static int getIntValueByKey(String key, int defaultV) {
        Object value = getValueByKey(key);
        if (value == null) {
            return defaultV;
        }
        return Integer.parseInt((String) value);
    }

    public static double getDoubleValueByKey(String key, double defaultV) {
        Object value = getValueByKey(key);
        if (value == null) {
            return defaultV;
        }
        return Double.parseDouble((String) value);
    }

    public static Long getLongValueByKey(String key, Long defaultV) {
        Object value = getValueByKey(key);
        if (value == null) {
            return defaultV;
        }
        return Long.parseLong((String) value);
    }

    public static boolean getBooleanValueByKey(String key, boolean defaultV) {
        Object value = getValueByKey(key);
        if (value == null) {
            return defaultV;
        }
        return Boolean.parseBoolean((String) (value));
    }
}
