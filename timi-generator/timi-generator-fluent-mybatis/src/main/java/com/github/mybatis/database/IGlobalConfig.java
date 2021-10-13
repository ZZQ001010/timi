package com.github.mybatis.database;

import com.github.mybatis.database.IGlobalConfigSet;
import com.github.mybatis.database.ITableConfig;

import java.util.function.Consumer;

/**
 * IGenerator
 *
 * @author darui.wu Created by darui.wu on 2020/5/22.
 */
public interface IGlobalConfig {
    /**
     * 设置生成的全局配置
     *
     * @param consumer config consumer
     * @return ignore
     */
    ITableConfig globalConfig(Consumer<IGlobalConfigSet> consumer);
}