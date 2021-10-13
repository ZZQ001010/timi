package com.github.mybatis.database;

import com.github.mybatis.database.DbTypeOfGenerator;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * IGlobalConfigSet 全局配置
 *
 * @author darui.wu Created by darui.wu on 2020/6/1.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface IGlobalConfigSet {
    /**
     * 设置生成的文件输出目录(不包括package名称, 只指定src目录)
     *
     * @param outputDir 文件输出目录
     * @return ignore
     */
    com.github.mybatis.database.IGlobalConfigSet setOutputDir(String outputDir);

    /**
     * 设置生成的文件输出目录(不包括package名称, 只指定src目录)
     *
     * @param outputDir     mapper,bean等文件输出路径
     * @param testOutputDir 测试辅助文件（TableDataMap, EntityDataMap, TableMix等文件输出路径)
     * @param daoOutputDir  dao文件输出路径
     * @return ignore
     */
    com.github.mybatis.database.IGlobalConfigSet setOutputDir(String outputDir, String testOutputDir,
            String daoOutputDir);

    /**
     * 设置生成fluent mybatis文件的数据库链接信息
     *
     * @param dataSource 数据源
     * @return IGlobalConfigSet
     */
    com.github.mybatis.database.IGlobalConfigSet setDataSource(DbTypeOfGenerator dbType, DataSource dataSource);

    /**
     * 设置生成fluent mybatis文件的数据库链接信息
     * 默认使用mysql数据库, driver="com.mysql.jdbc.Driver"
     *
     * @param url      数据库链接url
     * @param username 用户名
     * @param password 密码
     * @return ignore
     */
    com.github.mybatis.database.IGlobalConfigSet setDataSource(String url, String username, String password);

    /**
     * 设置生成fluent mybatis文件的数据库链接信息
     *
     * @param dbType   数据库类型
     * @param url      数据库链接url
     * @param username 用户名
     * @param password 密码
     * @return ignore
     */
    com.github.mybatis.database.IGlobalConfigSet setDataSource(DbTypeOfGenerator dbType, String driver, String url,
            String username, String password);

    /**
     * 生成的fluent mybatis的基础包路径
     *
     * @param basePackage base package name
     * @return IGlobalConfigSet
     */
    com.github.mybatis.database.IGlobalConfigSet setBasePackage(String basePackage);

    /**
     * 生成dao类路径
     *
     * @param daoPackage dao package
     * @return ignore
     */
    com.github.mybatis.database.IGlobalConfigSet setDaoPackage(String daoPackage);

    /**
     * 设置字段生成顺序
     *
     * @param alphabetOrder true:字母序
     * @return ignore
     */
    com.github.mybatis.database.IGlobalConfigSet setAlphabetOrder(boolean alphabetOrder);

    /**
     * 设置schema name
     *
     * @param schemaName schema name
     * @return ignore
     */
    com.github.mybatis.database.IGlobalConfigSet setSchemaName(String schemaName);

    /**
     * 返回数据库链接
     *
     * @return Connection
     */
    Connection getConnection();
}