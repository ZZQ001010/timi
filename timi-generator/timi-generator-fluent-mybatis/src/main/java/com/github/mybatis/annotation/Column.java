package com.github.mybatis.annotation;

import com.github.mybatis.database.model.FieldType;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Column: 字段定义
 *
 * @author wudarui
 */
@SuppressWarnings("rawtypes")
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Column {
    /**
     * 数据库字段名称和Entity属性名称定义
     * o "column" 只指定column名称, 属性名按默认规则处理
     * o "column:property" 定义对应关系
     * 举例
     * value = {"gmt_create", "gmt_modified:gmtModified", "other:other"}
     *
     * @return ignore
     */
    String[] value();

    /**
     * insert的默认值
     *
     * @return ignore
     */
    String insert() default "";

    /**
     * update的默认值
     *
     * @return ignore
     */
    String update() default "";

    /**
     * 是否大字段
     *
     * @return ignore
     */
    boolean isLarge() default false;

    /**
     * 显式指定字段对应的java类型
     *
     * @return ignore
     */
    Class javaType() default Object.class;

    /**
     * 显式指定jdbc type
     *
     * @return JdbcType
     */
    JdbcType jdbcType() default JdbcType.UNDEFINED;

    /**
     * type handler
     *
     * @return ignore
     */
    Class<? extends TypeHandler> typeHandler() default UnknownTypeHandler.class;

    /**
     * 指定字段类型
     *
     * @return ignore
     */
    FieldType category() default FieldType.Common;
}