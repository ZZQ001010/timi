package com.github.mybatis.annotation;

import com.github.mybatis.annotation.Relation;
import com.github.mybatis.annotation.Table;
import com.github.mybatis.database.DbTypeOfGenerator;
import com.github.mybatis.database.model.ConfigKey;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 需要Entity类表定义
 *
 * @author wudarui
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Tables {
    /**
     * 数据库类型
     *
     * @return ignore
     */
    DbTypeOfGenerator dbType() default DbTypeOfGenerator.MYSQL;

    /**
     * 数据库驱动
     * <pre>
     * 默认mysql
     * <= 5 : com.mysql.jdbc.Driver
     * >= 5 : com.mysql.cj.jdbc.Driver
     * </pre>
     *
     * @return 数据库驱动
     */
    String driver() default "com.mysql.cj.jdbc.Driver";

    /**
     * 数据库链接url
     *
     * @return ignore
     */
    String url() default "";

    /**
     * 数据库用户名
     *
     * @return ignore
     */
    String username() default "";

    /**
     * 数据库用户密码
     *
     * @return ignore
     */
    String password() default "";

    /**
     * 数据库schema
     *
     * @return ignore
     */
    String schema() default "";

    /**
     * FluentMybatis Entity代码目录
     * 相对于跟目录System.getProperty("user.dir")路径
     * 一般是 "src/main/java"
     * 或者是 "subProject/src/main/java"
     *
     * @return ignore
     */
    String srcDir() default "";

    /**
     * 辅助测试代码目录
     * 相对于跟目录System.getProperty("user.dir")路径
     * 一般是 "src/test/java"
     * 或者是 "subProject/src/test/java"
     *
     * @return ignore
     */
    String testDir() default "";

    /**
     * dao接口和实现默认生成路径, 当srcDir有值时有效
     * 相对于跟目录System.getProperty("user.dir")路径
     * 默认和srcDir一样
     *
     * @return ignore
     */
    String daoDir() default "";

    /**
     * 生成文件的base package路径, 不包含 ".entity", ".dao"部分
     * 默认和生成定义类相同
     *
     * @return ignore
     */
    String basePack() default "";

    /**
     * 指定数据库表名
     *
     * @return ignore
     */
    Table[] tables();

    /**
     * 关联关系
     *
     * @return ignore
     */
    Relation[] relations() default {};

    /*
     * ========== 下面定义可以被 @Table 定义覆盖 ==========
     */

    /**
     * 生成Entity文件时, 需要去除的表前缀
     *
     * @return ignore
     */
    String[] tablePrefix() default {ConfigKey.NOT_DEFINED};

    /**
     * 生成Mapper bean时在bean name前缀
     *
     * @return ignore
     */
    String mapperPrefix() default ConfigKey.NOT_DEFINED;

    /**
     * 记录创建字段
     *
     * @return ignore
     */
    String gmtCreated() default ConfigKey.NOT_DEFINED;

    /**
     * 生成的数据实体的后缀
     *
     * @return 后缀定义
     */
    String entitySuffix() default ConfigKey.Entity_Default_Suffix;

    /**
     * 记录修改字段
     *
     * @return ignore
     */
    String gmtModified() default ConfigKey.NOT_DEFINED;

    /**
     * 逻辑删除字段
     *
     * @return ignore
     */
    String logicDeleted() default ConfigKey.NOT_DEFINED;

    /**
     * 乐观锁字段
     *
     * @return ignore
     */
    String version() default ConfigKey.NOT_DEFINED;

    /**
     * 生成的字段按字母序排列
     *
     * @return true:字母序; false: 数据库定义顺序
     */
    boolean alphabetOrder() default true;
}