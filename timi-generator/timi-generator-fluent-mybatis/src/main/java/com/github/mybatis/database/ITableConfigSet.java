package com.github.mybatis.database;

import com.github.mybatis.database.model.TableSetter;

import java.util.function.Consumer;

/**
 * ITableConfig
 *
 * @author darui.wu Created by darui.wu on 2020/6/1.
 */
@SuppressWarnings("unused")
public interface ITableConfigSet {
    /**
     * 增加表tableName映射关系
     *
     * @param tableName 表名
     * @return ignore
     */
    com.github.mybatis.database.ITableConfigSet table(String tableName);

    /**
     * 增加表tableName映射关系
     *
     * @param tableName 表名
     * @param consumer  setter consumer
     * @return ignore
     */
    com.github.mybatis.database.ITableConfigSet table(String tableName, Consumer<TableSetter> consumer);

    /**
     * 对所有表统一处理
     *
     * @param consumer setter consumer
     */
    void foreach(Consumer<TableSetter> consumer);

    /**
     * 返回表设置
     *
     * @param tableName 表名
     * @return TableSetter
     */
    TableSetter getTableSetter(String tableName);
}