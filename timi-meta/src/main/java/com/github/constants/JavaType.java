package com.github.constants;

import org.apache.ibatis.ibator.api.dom.java.FullyQualifiedJavaType;

/**
 * @author zzq
 * @date 2021/8/6
 */
public final class JavaType {
    
    public static final FullyQualifiedJavaType fqjtInteger = new FullyQualifiedJavaType("java.lang.Integer");
    
    public static final  FullyQualifiedJavaType fqjtLong = new FullyQualifiedJavaType("java.lang.Long");
    
    public static final  FullyQualifiedJavaType fqjtBigDecimal = new FullyQualifiedJavaType("java.math.BigDecimal");
    
    public static final  FullyQualifiedJavaType fqjtDate = new FullyQualifiedJavaType("java.util.Date");
    
    public static final  FullyQualifiedJavaType fqjtByteArr = new FullyQualifiedJavaType("byte[]");
    
}
