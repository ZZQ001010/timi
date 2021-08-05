package com.github.mybatis.database.model;

import cn.org.atool.fluent.mybatis.metadata.DbType;
import com.github.mybatis.database.config.impl.GlobalConfig;
import com.github.mybatis.database.config.impl.RelationConfig;
import com.github.mybatis.database.config.impl.TableConfigSet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import schemacrawler.schema.Column;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.github.mybatis.util.GeneratorHelper.isBlank;

/**
 * 表信息，关联到当前字段信息
 *
 * @author darui.wu
 */
@SuppressWarnings({"UnusedReturnValue", "rawtypes", "unused"})
@Getter
@Setter
@Accessors(chain = true)
public class TableSetter {
    /*
     ****************以下是配置信息******************
     */
    /**
     * 表名
     */
    private String tableName;
    /**
     * 时间类型对应策略
     */
    private DateType dateType = DateType.ONLY_DATE;
    /**
     * 需要去掉的表前缀
     */
    @Setter(AccessLevel.NONE)
    private String[] tablePrefix;
    /**
     * 当前表的去掉的前缀
     */
    @Setter(AccessLevel.NONE)
    private String matchedPrefix = "";

    /**
     * entity前缀部分(去掉Entity余下的部分)
     */
    private String entityPrefix;
    /**
     * entity后缀部分(默认是Entity, 可以设置成自定义的, 比如PoJo)
     */
    private String entitySuffix = ConfigKey.Entity_Default_Suffix;

    @Setter(AccessLevel.NONE)
    private List<RelationConfig> relations = new ArrayList<>();

    /**
     * 主键的sequence name指定
     */
    private String seqName;
    /**
     * 特殊字段指定(排除，别名等)
     */
    @Setter(AccessLevel.NONE)
    private Map<String, CustomizedColumn> columns = new HashMap<>();

    /* ********************************************* */
    /* ***************以下是数据库信息***************** */
    /* ********************************************* */
    /**
     * 注释
     */
    private String comment;
    /**
     * 字段列表
     */
    @Setter(AccessLevel.NONE)
    private List<TableField> fields = new ArrayList<>();

    @Setter(AccessLevel.NONE)
    private Map<String, TableField> fieldMap = new HashMap<>();
    /**
     * 所有字段拼接串
     */
    @Setter(AccessLevel.NONE)
    private String fieldNames;

    /* ********************************************* */
    /* ************以下外部导入或内部初始化************* */
    /* ********************************************* */
    /**
     * 全局配置
     */
    @Setter(AccessLevel.NONE)
    private final GlobalConfig globalConfig;
    /**
     * 表配置
     */
    @Setter(AccessLevel.NONE)
    private final TableConfigSet tableConfig;
    /**
     * 所有字段类型列表
     */
    @Setter(AccessLevel.NONE)
    private final Set<String> importTypes = new HashSet<>();
    /**
     * 执行模板生成各个步骤产生的上下文信息，比如Mapper名称等，供其他模板生成时引用
     */
    @Setter(AccessLevel.NONE)
    private Map<FieldType, String> fileTypeName = new HashMap<>();

    public TableSetter(String tableName, GlobalConfig globalConfig, TableConfigSet tableConfig) {
        this(tableName, null, globalConfig, tableConfig);
    }

    public TableSetter(String tableName, String entityPrefix, GlobalConfig globalConfig, TableConfigSet tableConfig) {
        this.tableName = tableName;
        this.entityPrefix = entityPrefix;
        this.globalConfig = globalConfig;
        this.tableConfig = tableConfig;
    }

    public com.github.mybatis.database.model.TableSetter setTablePrefix(String... tablePrefix) {
        if (!this.hasPrefix()) {
            this.tablePrefix = tablePrefix;
        }
        return this;
    }

    /**
     * 记录创建字段名称
     */
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private String gmtCreate;
    /**
     * 记录修改字段名称
     */
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private String gmtModified;
    /**
     * 逻辑删除字段名称
     */
    @Setter(AccessLevel.NONE)
    private String logicDeleted;
    /**
     * 乐观锁字段名称
     */
    @Setter(AccessLevel.NONE)
    private String versionField;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static final String NOW = "now()";

    public com.github.mybatis.database.model.TableSetter setGmtCreate(String gmtCreate) {
        if (isBlank(gmtCreate) || !isBlank(this.gmtCreate)) {
            return this;
        }
        this.gmtCreate = gmtCreate;
        this.setColumn(this.gmtCreate, f -> f.setInsert(NOW));
        return this;
    }

    public com.github.mybatis.database.model.TableSetter setGmtModified(String gmtModified) {
        if (isBlank(gmtModified) || !isBlank(this.gmtModified)) {
            return this;
        }
        this.gmtModified = gmtModified;
        this.setColumn(this.gmtModified, f -> f.setInsert(NOW).setUpdate(NOW));
        return this;
    }

    public com.github.mybatis.database.model.TableSetter setLogicDeleted(String logicDeleted) {
        if (isBlank(logicDeleted) || !isBlank(this.logicDeleted)) {
            return this;
        }
        this.logicDeleted = logicDeleted;
        this.setColumn(this.logicDeleted, f -> f.setJavaType(Boolean.class).setInsert("0"));
        return this;
    }

    public com.github.mybatis.database.model.TableSetter setVersionField(String versionField) {
        if (isBlank(versionField) || !isBlank(this.versionField)) {
            return this;
        }
        this.versionField = versionField;
        // 换做FM定义的DbType类型, 加上转义符
        DbType dbType = DbType.valueOf(this.globalConfig.getDbType().name());
        this.setColumn(this.versionField, f -> f
            .setJavaType(Long.class)
            .setInsert("0")
            .setUpdate(dbType.wrap(this.versionField) + " + 1"));
        return this;
    }

    public com.github.mybatis.database.model.TableSetter setColumn(String columnName, String propertyName) {
        this.getDefinedColumn(columnName).setFieldName(propertyName);
        return this;
    }

    public com.github.mybatis.database.model.TableSetter setColumn(String column, Consumer<CustomizedColumn> consumer) {
        CustomizedColumn customizedColumn = this.getDefinedColumn(column);
        consumer.accept(customizedColumn);
        return this;
    }

    private CustomizedColumn getDefinedColumn(String column) {
        if (isBlank(column)) {
            throw new RuntimeException("The column can't be null.");
        }
        if (!this.columns.containsKey(column)) {
            this.columns.put(column, new CustomizedColumn(column));
        }
        return this.columns.get(column);
    }

    public com.github.mybatis.database.model.TableSetter setExcludes(String... columnNames) {
        for (String column : columnNames) {
            this.columns.put(column, new CustomizedColumn(column).setExclude());
        }
        return this;
    }

    /**
     * 字段是否被排除
     *
     * @param field 字段
     * @return ignore
     */
    private boolean isExclude(String field) {
        CustomizedColumn column = this.columns.get(field);
        return column != null && column.isExclude();
    }

    /**
     * 处理表  名称
     */
    public void initTable(List<com.github.meta.Column> columns) {
        this.initEntityPrefix();
        this.initTableFields(columns);
    }

    private void initEntityPrefix() {
        if (!isBlank(this.entityPrefix)) {
            return;
        }
        String prefix = this.getNoPrefixTableName();
        if (globalConfig.getTableNaming() == Naming.underline_to_camel) {
            prefix = Naming.underlineToCamel(prefix);
        }
        this.entityPrefix = Naming.capitalFirst(prefix);
    }

    /**
     * 返回
     *
     * @return ignore
     */
    public String getNoPrefixTableName() {
        if (this.hasPrefix()) {
            String noPrefix = Naming.removePrefix(this.tableName, this.tablePrefix);
            this.matchedPrefix = this.tableName.substring(0, this.tableName.length() - noPrefix.length());
            return noPrefix;
        } else {
            return this.tableName;
        }
    }

    /**
     * shif dingy
     *
     * @return ignore
     */
    private boolean hasPrefix() {
        return this.tablePrefix != null && this.tablePrefix.length > 0;
    }

    /**
     * 是否已找到id字段
     */
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private transient boolean haveId = false;

    /**
     * 增加数据库字段
     *
     * @return 所有未排除字段
     */
    private List<TableField> initTableFields(List<com.github.meta.Column> columns) {
        for (com.github.meta.Column column : columns) {
            String columnName = column.getTextName();
            TableField field = this.initTableField(column);
            if (field == null || this.isExclude(columnName)) {
                continue;
            }
            if (!field.isPrimary()) {
                field.setCategory(this.getFieldCategory(columnName));
            }
            this.fields.add(field);
            this.fieldMap.put(columnName, field);
        }
        if (this.globalConfig.isAlphabetOrder()) {
            Collections.sort(this.fields);
        }
        this.fieldNames = fields.stream().map(TableField::getColumnName).collect(Collectors.joining(", "));
        return fields;
    }

    private FieldType getFieldCategory(String fieldName) {
        if (fieldName.equalsIgnoreCase(this.gmtCreate)) {
            return FieldType.GmtCreate;
        } else if (fieldName.equalsIgnoreCase(this.gmtModified)) {
            return FieldType.GmtModified;
        } else if (fieldName.equalsIgnoreCase(this.logicDeleted)) {
            return FieldType.IsDeleted;
        } else {
            return FieldType.Common;
        }
    }

    private TableField initTableField(com.github.meta.Column column) {
        String columnName = column.getTextName();
        if (this.isExclude(columnName)) {
            return null;
        }
        TableField field = new TableField(this.globalConfig, columnName, this.columns.get(columnName));

//        boolean primary = column.isPartOfPrimaryKey();
//        boolean autoIncrease = column.isAutoIncremented();
//        // 处理ID, 只取第一个，并放到list中的索引为0的位置
//        if (primary && !haveId) {
//            field.setCategory(autoIncrease ? FieldType.PrimaryId : FieldType.PrimaryKey);
//            haveId = true;
//        }
        field.initNamingAndType(column);
        return field;
    }

    /**
     * base dao 导入的自定义接口
     * key: 接口类
     * value: 泛型
     */
    @Setter(AccessLevel.NONE)
    private Class defaults;

    @Setter(AccessLevel.NONE)
    private Class superMapper;

    public com.github.mybatis.database.model.TableSetter setDefaults(Class defaults) {
        this.defaults = defaults;
        return this;
    }

    public com.github.mybatis.database.model.TableSetter setSuperMapper(Class superMapper) {
        this.superMapper = superMapper;
        return this;
    }

    /**
     * Entity 导入的自定义接口
     * key: 接口类
     * value: 泛型列表
     */
    @Setter(AccessLevel.NONE)
    private List<Class> entityInterfaces = new ArrayList<>();

    public com.github.mybatis.database.model.TableSetter addEntityInterface(Class interfaceType) {
        this.entityInterfaces.add(interfaceType);
        return this;
    }

    /**
     * mapper类bean名称前缀
     */
    private String mapperBeanPrefix = "";

    public com.github.mybatis.database.model.TableSetter setMapperPrefix(String mapperBeanPrefix) {
        this.mapperBeanPrefix = mapperBeanPrefix;
        return this;
    }

    public String getBasePackage() {
        return globalConfig.getBasePackage();
    }

    /**
     * 根据column返回属性名称
     *
     * @param column 字段
     * @return 属性名称
     */
    public String getField(String column) {
        if (!this.fieldMap.containsKey(column)) {
            throw new RuntimeException(String.format("the field[%s] of table[%s] not found.", column, tableName));
        }
        try {
            return this.fieldMap.get(column).getName();
        } catch (Exception e) {
            throw new RuntimeException(String.format("getField[table=%s, column=%s] error:%s", tableName, column, e.getMessage()), e);
        }
    }
}