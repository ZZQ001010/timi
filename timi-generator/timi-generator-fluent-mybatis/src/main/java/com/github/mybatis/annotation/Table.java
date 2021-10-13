package com.github.mybatis.annotation;

import com.github.mybatis.annotation.Column;
import com.github.mybatis.database.model.ConfigKey;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.github.mybatis.database.model.ConfigKey.NOT_DEFINED;

/**
 * Table: 表设置
 *
 * @author wudarui
 */
@SuppressWarnings("rawtypes")
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Table {
    /**
     * 表名称列表
     * <p>
     * o 格式"table_name": 使用默认Entity名称
     * o 格式"table_name:EntityName": 使用指定Entity名称
     *
     * @return ignore
     */
    String[] value();

    /**
     * 排除字段列表
     *
     * @return ignore
     */
    String[] excludes() default {NOT_DEFINED};

    /**
     * 显式指定字段转换属性
     *
     * @return ignore
     */
    Column[] columns() default {};

    /**
     * 生成Entity文件时, 需要去除的表前缀
     *
     * @return ignore
     */
    String[] tablePrefix() default {NOT_DEFINED};

    /**
     * 生成Mapper bean时在bean name前缀
     *
     * @return ignore
     */
    String mapperPrefix() default NOT_DEFINED;

    /**
     * 记录创建字段
     *
     * @return ignore
     */
    String gmtCreated() default NOT_DEFINED;

    /**
     * 记录修改字段
     *
     * @return ignore
     */
    String gmtModified() default NOT_DEFINED;

    /**
     * 逻辑删除字段
     *
     * @return ignore
     */
    String logicDeleted() default NOT_DEFINED;

    /**
     * 乐观锁字段
     *
     * @return ignore
     */
    String version() default ConfigKey.NOT_DEFINED;

    /**
     * 表对应的seq_name, 下面2个特殊值, 表示强制设置主键特性
     * <pre>
     *  "auto": 显式设置为自增主键, 对应 @TableId(auto=true, seqName="auto")
     *  "user": 显式设置为用户自定义, 对应 @TableId(auto=false, seqName="user")
     *   else : 对应 @TableId(auto=false, seqName=seqName())
     * </pre>
     *
     * @return ignore
     */
    String seqName() default "";

    /**
     * entity类自定义接口
     *
     * @return ignore
     */
    Class[] entity() default {};

    /**
     * crud默认操作自定义实现(extends IDefaultSetter)
     * <p>
     * 这里默认值定义成Object.class主要是避免只需要测试代码生成时, 不需要依赖fluent-mybatis包
     *
     * @return ignore
     */
    Class defaults() default Object.class;

    /**
     * 生成的Mapper类继承的自定义接口类( extends IMapper)
     * <p>
     * 这里默认值定义成Object.class主要是避免只需要测试代码生成时, 不需要依赖fluent-mybatis包
     *
     * @return Class
     */
    Class superMapper() default Object.class;
}