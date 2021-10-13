package com.github.mybatis.database;

import lombok.Getter;

import static com.github.mybatis.util.GeneratorHelper.isBlank;


/**
 * 数据库类型
 *
 * @author wudarui
 */
@Getter
public enum DbTypeOfGenerator {
    /**
     * MYSQL
     */
    MYSQL("mysql", "MySql数据库"),
    /**
     * MARIADB
     */
    MARIADB("mariadb", "MariaDB数据库"),
    /**
     * ORACLE
     */
    ORACLE("oracle", "Oracle数据库"),
    /**
     * DB2
     */
    DB2("db2", "DB2数据库"),
    /**
     * H2
     */
    H2("h2", "H2数据库"),
    /**
     * SQLITE
     */
    SQLITE("sqlite", "SQLite数据库"),
    /**
     * POSTGRE
     */
    POSTGRE_SQL("postgresql", "Postgre数据库"),
    /**
     * SQLSERVER
     */
    SQL_SERVER("sqlserver", "SQLServer数据库");

    /**
     * 数据库名称
     */
    private final String db;
    /**
     * 描述
     */
    private final String desc;

    DbTypeOfGenerator(String db, String desc) {
        this.db = db;
        this.desc = desc;
    }

    /**
     * 判断数据库类型
     *
     * @param driverName 驱动器类
     * @param url        数据库链接
     * @return 类型枚举值
     */
    public static com.github.mybatis.database.DbTypeOfGenerator getDbType(String driverName, String url) {
        com.github.mybatis.database.DbTypeOfGenerator dbType = isDbType(driverName);
        if (dbType == null) {
            dbType = isDbType(url);
        }
        return dbType == null ? com.github.mybatis.database.DbTypeOfGenerator.MYSQL : dbType;
    }

    private static com.github.mybatis.database.DbTypeOfGenerator isDbType(String info) {
        if (isBlank(info)) {
            return null;
        }
        String temp = info.toLowerCase();
        com.github.mybatis.database.DbTypeOfGenerator[] dbTypes = com.github.mybatis.database.DbTypeOfGenerator.values();
        for (com.github.mybatis.database.DbTypeOfGenerator type : dbTypes) {
            if (temp.contains(type.db)) {
                return type;
            }
        }
        return null;
    }
}