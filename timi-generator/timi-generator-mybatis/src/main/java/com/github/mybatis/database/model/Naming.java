package com.github.mybatis.database.model;

import com.github.mybatis.util.GeneratorHelper;

import java.util.regex.Pattern;

/**
 * 数据库表到文件命名转换策略
 *
 * @author wudarui
 */
public enum Naming {
    /**
     * 不做任何改变，原样输出
     */
    no_change,
    /**
     * 下划线转驼峰命名
     */
    underline_to_camel;

    public static String underlineToCamel(String name) {
        if (GeneratorHelper.isBlank(name)) {
            return "";
        }
        String tempName = name;
        if (isCapitalMode(name) || isMixedMode(name)) {
            tempName = name.toLowerCase();
        }
        String[] camels = tempName.split("_");
        StringBuilder result = new StringBuilder();
        for (String word : camels) {
            if (!GeneratorHelper.isBlank(word)) {
                result.append(result.length() == 0 ? word : capitalFirst(word));
            }
        }
        return result.toString();
    }

    private static final Pattern CAPITAL_MODE = Pattern.compile("^[0-9A-Z/_]+$");

    public static boolean isCapitalMode(String word) {
        return null != word && CAPITAL_MODE.matcher(word).matches();
    }

    public static boolean isMixedMode(String word) {
        return matches(".*[A-Z]+.*", word) && matches(".*[/_]+.*", word);
    }

    public static boolean matches(String regex, String input) {
        return null != regex && null != input && Pattern.matches(regex, input);
    }

    /**
     * 去掉指定的前缀
     *
     * @param name   字段名称
     * @param prefix 前缀
     * @return ignore
     */
    public static String removePrefix(String name, String... prefix) {
        if (GeneratorHelper.isBlank(name)) {
            return "";
        }
        if (prefix == null) {
            return name;
        }
        String lowerCase = name.toLowerCase();
        for (String pf : prefix) {
            if (lowerCase.startsWith(pf.toLowerCase())) {
                return name.substring(pf.length());
            }
        }
        return name;
    }

    /**
     * 实体首字母大写
     *
     * @param name 待转换的字符串
     * @return 转换后的字符串
     */
    public static String capitalFirst(String name) {
        if (!GeneratorHelper.isBlank(name)) {
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        } else {
            return "";
        }
    }

    /**
     * 首字母小写
     *
     * @param name name
     * @return ignore
     */
    public static String lowerFirst(String name) {
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }
}