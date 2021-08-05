package com.github.mybatis.database.config.impl;

import com.github.mybatis.database.DbTypeOfGenerator;
import com.github.mybatis.database.IGlobalConfigSet;
import com.github.mybatis.database.model.DataSourceSetter;
import com.github.mybatis.database.model.Naming;
import com.github.mybatis.util.GeneratorHelper;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Objects;

/**
 * 策略配置项
 *
 * @author darui.wu
 */
@SuppressWarnings("rawtypes")
@Data
@Accessors(chain = true)
public class GlobalConfig implements IGlobalConfigSet {
    /**
     * 数据库表映射到实体的命名策略
     */
    private Naming tableNaming = Naming.underline_to_camel;
    /**
     * 数据库表字段映射到实体的命名策略
     * <p>未指定按照 naming 执行</p>
     */
    @Getter(AccessLevel.NONE)
    private Naming columnNaming;
    /**
     * Boolean类型字段是否移除is前缀（默认 false）<br>
     * 比如 : 数据库字段名称 : 'is_xxx',类型为 : tinyint. 在映射实体的时候则会去掉is,在实体类中映射最终结果为 xxx
     */
    private boolean removeIsPrefix = false;
    /**
     * schemaName
     */
    @Getter
    private String schemaName;
    /**
     * 代码package前缀
     */
    @Getter(AccessLevel.NONE)
    private String basePackage;

    private String daoPackage;

    @Setter(AccessLevel.NONE)
    private String packageDir;

    @Setter(AccessLevel.NONE)
    private String daoDir;
    /**
     * 代码生成路径
     */
    @Setter(AccessLevel.NONE)
    private String outputDir = System.getProperty("user.dir") + "/target/generate/base";
    /**
     * 测试代码生成路径
     */
    @Setter(AccessLevel.NONE)
    private String testOutputDir = System.getProperty("user.dir") + "/target/generate/test";
    /**
     * dao代码生成路径
     */
    @Setter(AccessLevel.NONE)
    private String daoOutputDir = System.getProperty("user.dir") + "/target/generate/dao";
    /**
     * 开发人员
     */
    private String author = "generate code";
    /**
     * 字段按字母序排列
     */
    @Setter
    private boolean alphabetOrder = true;

    public Naming getColumnNaming() {
        return columnNaming == null ? tableNaming : columnNaming;
    }

    @Override
    public IGlobalConfigSet setBasePackage(String basePackage) {
        this.basePackage = basePackage;
        this.packageDir = '/' + basePackage.replace('.', '/') + '/';
        if (GeneratorHelper.isBlank(this.daoPackage)) {
            this.setDaoPackage(basePackage);
        }
        return this;
    }

    @Override
    public IGlobalConfigSet setDaoPackage(String daoPackage) {
        this.daoPackage = daoPackage;
        this.daoDir = '/' + daoPackage.replace('.', '/') + '/';
        return this;
    }

    public String getBasePackage() {
        if (GeneratorHelper.isBlank(basePackage)) {
            throw new RuntimeException("The base package should be set.");
        }
        return basePackage;
    }

    @Override
    public IGlobalConfigSet setOutputDir(String outputDir) {
        return this.setOutputDir(outputDir, outputDir, outputDir);
    }

    @Override
    public IGlobalConfigSet setOutputDir(String outputDir, String testOutputDir, String daoOutputDir) {
        this.outputDir = outputDir;
        this.testOutputDir = testOutputDir;
        this.daoOutputDir = daoOutputDir;
        return this;
    }

    /**
     * 数据源配置
     */
    private DataSourceSetter dataSourceSetter;

    @Override
    public IGlobalConfigSet setDataSource(DbTypeOfGenerator dbType, DataSource dataSource) {
        this.dataSourceSetter = new DataSourceSetter(dbType, dataSource);
        return this;
    }

    @Override
    public IGlobalConfigSet setDataSource(String url, String username, String password) {
        return this.setDataSource(DbTypeOfGenerator.MYSQL, "com.mysql.jdbc.Driver", url, username, password);
    }

    @Override
    public IGlobalConfigSet setDataSource(DbTypeOfGenerator dbType, String driver, String url, String username, String password) {
        this.dataSourceSetter = new DataSourceSetter(dbType, driver, url, username, password);
        return this;
    }

    @Override
    public Connection getConnection() {
        return this.dataSourceSetter.getConn();
    }

    public DbTypeOfGenerator getDbType() {
        return this.dataSourceSetter.getDbType();
    }

    /**
     * 是否需要去掉is前缀
     *
     * @param fieldName 字段名称
     * @param fieldType 字段类型
     * @return ignore
     */
    public boolean needRemoveIsPrefix(String fieldName, Class fieldType) {
        if (!this.isRemoveIsPrefix()) {
            return false;
        } else if (!Objects.equals(boolean.class, fieldType)) {
            return false;
        } else {
            return fieldName.startsWith("is");
        }
    }
}