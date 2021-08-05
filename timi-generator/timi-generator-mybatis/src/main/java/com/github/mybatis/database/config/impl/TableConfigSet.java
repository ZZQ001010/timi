package com.github.mybatis.database.config.impl;

import com.github.mybatis.database.ITableConfigSet;
import com.github.mybatis.database.config.impl.GlobalConfig;
import com.github.mybatis.database.config.impl.RelationConfig;
import com.github.mybatis.database.model.TableSetter;
import com.github.mybatis.javafile.template.EntityFile;
import com.github.mybatis.util.SchemaKits;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import schemacrawler.schema.Table;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 全局构建配置项
 *
 * @author wudarui
 */
@Data
@Accessors(chain = true)
public class TableConfigSet implements ITableConfigSet {
    private final GlobalConfig globalConfig;
    /**
     * 需要处理的表
     */
    @Getter
    private Map<String, TableSetter> tables = new HashMap<>();

    public TableConfigSet(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    @Override
    public ITableConfigSet table(String tableName) {
        return this.table(tableName, (table) -> {
        });
    }

    @Override
    public ITableConfigSet table(String tableName, Consumer<TableSetter> consumer) {
        TableSetter table = new TableSetter(tableName, this.globalConfig, this);
        consumer.accept(table);
        this.tables.put(tableName, table);
        return this;
    }

    @Override
    public void foreach(Consumer<TableSetter> consumer) {
        this.tables.values().forEach(consumer);
    }

    @Override
    public TableSetter getTableSetter(String tableName) {
        return this.tables.get(tableName);
    }

//    /**
//     * 获取所有的数据库表信息
//     */
//    public void initTables() {
//        Set<String> existed = new HashSet<>();
//        Collection<Table> tables = SchemaKits.getTables(this.globalConfig.getConnection(), this.globalConfig.getSchemaName());
//        for (Table table : tables) {
//            String tableName = table.getName();
//            TableSetter tableSetter = this.getTables().get(tableName);
//            if (tableSetter == null) {
//                continue;
//            }
//            existed.add(tableName);
//            tableSetter.setComment(table.getRemarks());
//            tableSetter.initTable(table.getColumns());
//        }
//
//        Set<String> all = this.getTables().keySet();
//        for (String table : all) {
//            if (!existed.contains(table)) {
//                System.err.println("Table '" + table + "' not exist in database !!!");
//                this.getTables().remove(table);
//            }
//        }
//    }

    /**
     * 初始化关联关系
     */
    public void initRelations() {
        for (Map.Entry<String, TableSetter> entry : this.getTables().entrySet()) {
            TableSetter source = entry.getValue();
            if (source.getRelations().isEmpty()) {
                continue;
            }
            for (RelationConfig relation : source.getRelations()) {
                TableSetter target = this.getTables().get(relation.getTargetTable());
                // 来源表相关信息设置
                relation.setSourcePackage(EntityFile.entityPackage(source));
                relation.setSourceEntity(EntityFile.entityClass(source));
                // 目标表相关信息设置
                relation.setTargetNoSuffix(target.getEntityPrefix());
                relation.setTargetPackage(EntityFile.entityPackage(target));
                relation.setTargetEntity(EntityFile.entityClass(target));
                relation.properties(source, target);
            }
        }
    }

    /**
     * 增加表设置
     *
     * @param tc TableConfigSet
     */
    public void add(com.github.mybatis.database.config.impl.TableConfigSet tc) {
        this.tables.putAll(tc.getTables());
    }
}