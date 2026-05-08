package com.learn.mlanalysissysbackend.utils;

import java.math.BigDecimal;

public class MyParseSafely {

    // 解析为 Long（支持科学计数法、整数字符串、空值）
    public static Long parseLongSafely(String str) {
        if (str == null || str.trim().isEmpty()) {
            return 0L;
        }
        String cleaned = str.trim().replace(",", ""); // 移除千分位逗号
        try {
            // 尝试直接解析为 Long
            return Long.parseLong(cleaned);
        } catch (NumberFormatException e1) {
            try {
                // 使用 BigDecimal 解析科学计数法，再转为 Long（确保没有小数部分）
                BigDecimal bd = new BigDecimal(cleaned);
                return bd.longValueExact(); // 如果有小数部分会抛异常
            } catch (Exception e2) {
                throw new IllegalArgumentException("无法转换为 Long: " + str);
            }
        }
    }

    // 解析为 Integer（支持科学计数法、整数字符串）
    public static Integer parseIntegerSafely(String str) {
        Long longVal = parseLongSafely(str);
        if (longVal < Integer.MIN_VALUE || longVal > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("数值超出 Integer 范围: " + str);
        }
        return longVal.intValue();
    }

    // 解析为 Double（支持科学计数法）
    public static Double parseDoubleSafely(String str) {
        if (str == null || str.trim().isEmpty()) {
            return 0.0;
        }
        String cleaned = str.trim().replace(",", "");
        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            // 使用 BigDecimal 兜底（支持高精度科学计数法）
            return new BigDecimal(cleaned).doubleValue();
        }
    }
}
