//package com.github.mybatis.javafile;
//
//import com.github.mybatis.annotation.Column;
//import com.github.mybatis.annotation.Relation;
//import com.github.mybatis.annotation.RelationType;
//import com.github.mybatis.annotation.Table;
//import com.github.mybatis.annotation.Tables;
//import com.github.mybatis.database.IGlobalConfig;
//import com.github.mybatis.database.IGlobalConfigSet;
//import com.github.mybatis.database.ITableConfigSet;
//import com.github.mybatis.database.config.impl.RelationConfig;
//import com.github.mybatis.database.config.impl.TableConfigSet;
//import com.github.mybatis.database.model.FieldType;
//import com.github.mybatis.database.model.Pair;
//import com.github.mybatis.database.model.TableSetter;
//import com.github.mybatis.util.SchemaKits;
//import org.apache.ibatis.type.UnknownTypeHandler;
//
//import javax.sql.DataSource;
//import java.util.Map;
//import java.util.Objects;
//import java.util.function.Consumer;
//
//import static com.github.mybatis.database.config.impl.RelationConfig.parseRelations;
//import static com.github.mybatis.database.model.ConfigKey.NOT_DEFINED;
//import static com.github.mybatis.util.GeneratorHelper.isBlank;
//
///**
// * 根据注解生成Entity文件
// *
// * @author wudarui
// */
//@SuppressWarnings({"unchecked", "rawtypes"})
//public class AnnotationGenerator {
//    /**
//     * 生成代码入口
//     *
//     * @param dataSource 显式指定数据源
//     * @param clazz      代码生成注解类
//     */
//    public static void generate(DataSource dataSource, Class clazz) {
//        Tables tables = (Tables) clazz.getAnnotation(Tables.class);
//        if (tables.tables().length == 0) {
//            throw new RuntimeException("The @Tables Annotation not found.");
//        }
//        com.github.mybatis.javafile.AnnotationGenerator generator = new com.github.mybatis.javafile.AnnotationGenerator(tables);
//        IGlobalConfig globalConfig = generator.globalConfig();
//        globalConfig
//            .globalConfig(generator.getGlobalConfig(dataSource, tables))
//            .tables(tc -> {
//                for (Table table : tables.tables()) {
//                    buildTableConfig(generator, tc, table);
//                }
//            })
//            .relations(tc -> {
//                for (Relation relation : tables.relations()) {
//                    buildRelationConfig(tc, relation);
//                }
//            })
//            .execute();
//    }
//
//    private static void buildRelationConfig(TableConfigSet tc, Relation relation) {
//        String source = relation.source();
//        String target = relation.target();
//        if (!tc.getTables().containsKey(source)) {
//            throw new RuntimeException("table[" + source + "] not found.");
//        }
//        if (!tc.getTables().containsKey(target)) {
//            throw new RuntimeException("table[" + target + "] not found.");
//        }
//        RelationType type = relation.type();
//        Map<String, String> relations = parseRelations(relation);
//        // 设置变量名称
//        String[] methods = relation.method().split(":");
//        RelationConfig sourceRelationConfig = new RelationConfig(tc, source, target);
//        sourceRelationConfig.setCached(relation.cache());
//        buildSourceRelation(sourceRelationConfig, type, relations, methods[0]);
//        if (type.is2way()) {
//            RelationConfig targetRelationConfig = new RelationConfig(tc, target, source);
//            buildTargetRelation(targetRelationConfig, type, relations, methods.length > 1 ? methods[1] : null);
//            targetRelationConfig.setCached(relation.cache());
//        }
//    }
//
//    private static void buildTargetRelation(RelationConfig relation, RelationType type, Map<String, String> relations, String method) {
//        relation.setMany(type.isSourceMany());
//        relation.setRelation(relations, true);
//        if (!isBlank(method)) {
//            relation.setMethod(method.trim());
//        }
//    }
//
//    private static void buildSourceRelation(RelationConfig relation, RelationType type, Map<String, String> relations, String method) {
//        relation.setMany(type.isTargetMany());
//        relation.setRelation(relations, false);
//        if (!isBlank(method)) {
//            relation.setMethod(method.trim());
//        }
//    }
//
//    private static void buildTableConfig(
//            com.github.mybatis.javafile.AnnotationGenerator generator, ITableConfigSet tc, Table table) {
//        for (String tableName : table.value()) {
//            Pair pair = new Pair(tableName);
//            Consumer<TableSetter> consumer = generator.getTableConfig(table);
//            tc.table(pair.getKey(), consumer);
//            if (isBlank(pair.getValue())) {
//                continue;
//            }
//            TableSetter tableSetter = tc.getTableSetter(pair.getKey());
//            String entity = pair.getValue();
//            if (pair.getValue().endsWith(tableSetter.getEntitySuffix())) {
//                entity = entity.substring(0, entity.length() - tableSetter.getEntitySuffix().length());
//            }
//            tableSetter.setEntityPrefix(entity);
//        }
//    }
//
//    private Consumer<IGlobalConfigSet> getGlobalConfig(DataSource dataSource, Tables tables) {
//        return g -> {
//            if (dataSource == null) {
//                g.setDataSource(tables.dbType(), tables.driver(), tables.url(), tables.username(), tables.password());
//                g.setSchemaName(SchemaKits.getSchemaName(tables.schema(), tables.url()));
//            } else {
//                g.setDataSource(tables.dbType(), dataSource);
//                g.setSchemaName(SchemaKits.getSchemaName(tables.schema(), dataSource));
//            }
//            g.setOutputDir(this.srcDir, this.testDir, this.daoDir);
//            g.setBasePackage(tables.basePack());
//            g.setDaoPackage(tables.basePack());
//            g.setAlphabetOrder(tables.alphabetOrder());
//        };
//    }
//
//    private Consumer<TableSetter> getTableConfig(Table table) {
//        return t -> {
//            if (table.excludes().length > 0) {
//                t.setExcludes(table.excludes());
//            }
//            t.setGmtCreate(value(table.gmtCreated(), tables.gmtCreated()));
//            t.setGmtModified(value(table.gmtModified(), tables.gmtModified()));
//            t.setLogicDeleted(value(table.logicDeleted(), tables.logicDeleted()));
//            t.setVersionField(value(table.version(), tables.version()));
//            t.setSeqName(table.seqName());
//            t.setTablePrefix(value(table.tablePrefix(), tables.tablePrefix()));
//            t.setMapperPrefix(value(table.mapperPrefix(), tables.mapperPrefix()));
//            // Entity 后缀定义
//            t.setEntitySuffix(tables.entitySuffix());
//            if (!Object.class.equals(table.defaults())) {
//                t.setDefaults(table.defaults());
//            }
//            if (!Object.class.equals(table.superMapper())) {
//                t.setSuperMapper(table.superMapper());
//            }
//            for (Class entity : table.entity()) {
//                t.addEntityInterface(entity);
//            }
//            for (Column column : table.columns()) {
//                this.setTableColumn(t, column);
//            }
//        };
//    }
//
//    /**
//     * 显式定义字段处理
//     *
//     * @param ts     表定义全局配置
//     * @param column 具体的字段定义
//     */
//    private void setTableColumn(TableSetter ts, Column column) {
//        for (String columnName : column.value()) {
//            Pair pair = new Pair(columnName);
//            ts.setColumn(pair.getKey(), c -> {
//                /* 先处理category, 保证个性化设置的覆盖 **/
//                if (column.category() == FieldType.GmtCreate) {
//                    ts.setGmtCreate(pair.getKey());
//                } else if (column.category() == FieldType.GmtModified) {
//                    ts.setGmtModified(pair.getKey());
//                } else if (column.category() == FieldType.IsDeleted) {
//                    ts.setLogicDeleted(pair.getKey());
//                }
//                /* 个性化设置 **/
//                if (!isBlank(pair.getValue())) {
//                    c.setFieldName(pair.getValue());
//                }
//                if (!isBlank(column.insert())) {
//                    c.setInsert(column.insert());
//                }
//                if (!isBlank(column.update())) {
//                    c.setUpdate(column.update());
//                }
//                if (column.isLarge()) {
//                    c.setLarge();
//                }
//                if (!Objects.equals(column.javaType(), Object.class)) {
//                    c.setJavaType(column.javaType());
//                }
//                if (!Objects.equals(column.typeHandler(), UnknownTypeHandler.class)) {
//                    c.setTypeHandler(column.typeHandler());
//                }
//            });
//        }
//    }
//
//    private IGlobalConfig globalConfig() {
//        return TemplateGenerator.build(!isBlank(tables.srcDir()), !isBlank(tables.testDir()));
//    }
//
//    private final Tables tables;
//
//    private final String srcDir;
//
//    private final String testDir;
//
//    private final String daoDir;
//
//    private AnnotationGenerator(Tables tables) {
//        this.tables = tables;
//        this.srcDir = System.getProperty("user.dir") + "/" + tables.srcDir() + "/";
//        this.testDir = System.getProperty("user.dir") + "/" + tables.testDir() + "/";
//        if (isBlank(tables.daoDir())) {
//            this.daoDir = this.srcDir;
//        } else {
//            this.daoDir = System.getProperty("user.dir") + "/" + tables.daoDir() + "/";
//        }
//    }
//
//    private String value(String value1, String value2) {
//        return !NOT_DEFINED.equals(value1) ? value1 : NOT_DEFINED.equals(value2) ? "" : value2;
//    }
//
//    private String[] value(String[] value1, String[] value2) {
//        return isDefined(value1) ? value1 : isDefined(value2) ? value2 : new String[0];
//    }
//
//    private boolean isDefined(String[] value) {
//        return value.length != 1 || !Objects.equals(value[0], NOT_DEFINED);
//    }
//}