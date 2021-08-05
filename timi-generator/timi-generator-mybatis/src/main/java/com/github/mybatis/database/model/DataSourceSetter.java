package com.github.mybatis.database.model;

import com.github.mybatis.database.DbTypeOfGenerator;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.github.mybatis.util.GeneratorHelper.isBlank;

/**
 * 数据库配置
 *
 * @author darui.wu
 */
@SuppressWarnings("rawtypes")
@Setter
@Accessors(chain = true)
public class DataSourceSetter {
    /**
     * 数据库类型
     */
    @Getter
    private DbTypeOfGenerator dbType;
    /**
     * 驱动名称
     */
    private String driverName;
    /**
     * 驱动连接的URL
     */
    private String url;
    /**
     * 数据库连接用户名
     */
    private String username;
    /**
     * 数据库连接密码
     */
    private String password;

    private DataSource dataSource;

    public DataSourceSetter(DbTypeOfGenerator dbType, DataSource dataSource) {
        if (isBlank(url) && dataSource == null) {
            throw new RuntimeException("Database connection url cannot cannot be null");
        }
        this.dbType = dbType;
        this.dataSource = dataSource;
    }

    public DataSourceSetter(DbTypeOfGenerator dbType, String driverName, String url, String username, String password) {
        if (url == null) {
            throw new RuntimeException("Database connection url cannot cannot be null");
        }
        this.driverName = driverName;
        this.url = url;
        this.username = username;
        this.password = password;
        this.dbType = dbType;
        if (null == this.dbType) {
            this.dbType = DbTypeOfGenerator.getDbType(this.driverName, this.url);
        }
    }


    /**
     * 创建数据库连接对象
     *
     * @return Connection
     */
    public Connection getConn() {
        try {
            if (dataSource != null) {
                return dataSource.getConnection();
            } else if (!isBlank(url)) {
                this.initDriverIfNeed();
                return DriverManager.getConnection(url, username, password);
            } else {
                throw new RuntimeException("dataSource needs to be specified.");
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Get Connection error:" + e.getMessage(), e);
        }
    }

    private Class driverClass;

    private void initDriverIfNeed() throws ClassNotFoundException {
        if (driverClass == null) {
            this.driverClass = Class.forName(driverName);
        }
    }
}