package com.github.mybatis.database;

import com.github.mybatis.database.ITableConfigSet;
import com.github.mybatis.database.config.impl.TableConfigSet;

import java.util.function.Consumer;

/**
 * ITableConfig
 *
 * @author darui.wu Created by darui.wu on 2020/5/27.
 */
public interface ITableConfig {
    /**
     * 设置要生成的表
     *
     * @param consumer Consumer
     * @return ITableConfig
     */
    com.github.mybatis.database.ITableConfig tables(Consumer<ITableConfigSet> consumer);

    /**
     * 设置表关联关系
     *
     * @param consumer Consumer
     * @return ITableConfig
     */
    com.github.mybatis.database.ITableConfig relations(Consumer<TableConfigSet> consumer);

    /**
     * 执行生成
     */
    void execute();
}