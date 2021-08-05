package com.github.mybatis.database;

import java.util.Map;

/**
 * IRelationConfigSet
 *
 * @author darui.wu Created by darui.wu on 2020/6/1.
 */
@SuppressWarnings("UnusedReturnValue")
public interface IRelationConfig {
    /**
     * 增加表targetTable关联关系
     *
     * @param targetTable target table
     * @return ignore
     */
    com.github.mybatis.database.IRelationConfig table(String targetTable);

    /**
     * 是否List
     *
     * @param isMany is list
     * @return ignore
     */
    com.github.mybatis.database.IRelationConfig setMany(boolean isMany);

    /**
     * 设置关联关系
     *
     * @param relation 关系设置
     * @param reversed 关系反转
     * @return ignore
     */
    com.github.mybatis.database.IRelationConfig setRelation(Map<String, String> relation, boolean reversed);
}