package com.github.api.assembler.utils;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.text.WordUtils;

/**
 * @author zzq
 * @date 2021/8/4
 */
public class StringUtils {
    
    public static String dbName2ClassName(String dbName) {
        String s = dbName;
        
        boolean allUpperCaseOrNumeric = true;
        for (char c : s.toCharArray()) {
            if (c != '_' && !CharUtils.isAsciiNumeric(c) && !CharUtils.isAsciiAlphaUpper(c)) {
                allUpperCaseOrNumeric = false;
                break;
            }
        }
        
        if (allUpperCaseOrNumeric) {
            // 为应对Java类定义的情况，只有在全大写时才需要定义
            // TODO 这是临时方案
            s = s.toLowerCase();
            s = WordUtils.capitalizeFully(s, new char[]{'_'});
            s = org.apache.commons.lang3.StringUtils.remove(s, "_");
        }
        
        if (!org.apache.commons.lang3.StringUtils.isAlpha(org.apache.commons.lang3.StringUtils.left(s, 1))) // 避免首个不是字母的情况
            s = "_" + s;
        return s;
    }
    
}
