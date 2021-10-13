package com.github.mybatis.annotation;

import com.github.mybatis.annotation.RelationType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表关联关系设置
 *
 * @author darui.wu
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Relation {
    /**
     * 方法名称
     * 如果双向都要显式设置Entity方法名称, 使用冒号 : 隔开
     * 如果未设置名称, 使用默认名称构造, 单个对象 findXyz(), 多个对象 listXyz()
     *
     * @return method
     */
    String method() default "";

    /**
     * 来源表
     *
     * @return ignore
     */
    String source();

    /**
     * 目标表
     *
     * @return ignore
     */
    String target();

    /**
     * 绑定关系
     *
     * @return ignore
     */
    RelationType type();

    /**
     * 关联字段设置, 格式:  源表字段=目标表字段
     * "source_column1=target_column1 && source_column2=target_column2"
     * <p>
     * 如果设置了关联关系, 框架可以实现关联查询
     * 如果不设置关系, 则可以自行实现关联查询
     *
     * @return ignore
     */
    String where() default "";

    /**
     * 是否缓存结果
     *
     * @return ignore
     */
    boolean cache() default true;
}