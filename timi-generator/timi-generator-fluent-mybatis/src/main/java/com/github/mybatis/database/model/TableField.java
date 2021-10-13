package com.github.mybatis.database.model;

import com.github.mybatis.database.config.impl.GlobalConfig;
import com.github.mybatis.database.model.CustomizedColumn;
import com.github.mybatis.database.model.FieldType;
import com.github.mybatis.database.model.Naming;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.TypeHandler;
import schemacrawler.schema.Column;

import java.util.Date;
import java.util.Objects;

import static com.github.mybatis.util.GeneratorHelper.isBlank;

/**
 * 表字段信息
 *
 * @author darui.wu
 */
@SuppressWarnings({"rawtypes", "unused"})
@Getter
@Accessors(chain = true)
public class TableField implements Comparable<com.github.mybatis.database.model.TableField> {
    @Getter(AccessLevel.NONE)
    private final GlobalConfig globalConfig;
    /**
     * 字段类别
     */
    @Setter
    private FieldType category = FieldType.Common;
    /**
     * 数据库字段名称
     */
    private final String columnName;
    /**
     * 显式指定Entity字段名称
     */
    @Setter
    private String name;
    /**
     * 显式指定字段java类型
     */
    @Setter
    private Class javaType;
    /**
     * 显式指定字段typeHandler
     */
    @Setter
    private Class<? extends TypeHandler> typeHandler;
    /**
     * insert时默认值
     */
    @Setter
    private String insert;
    /**
     * update时默认值
     */
    @Setter
    private String update;
    /**
     * 是否大字段
     */
    @Setter
    private Boolean isLarge;
    /**
     * 字段名称（首字母大写）
     */
    private String capitalName;
    /**
     * 数据库字段类型
     */
    private String jdbcType;
    /**
     * 非空
     */
    private boolean notNull = false;
    /**
     * 默认值
     */
    private String defaults = null;
    /**
     * 字段注释
     */
    private String comment;

    public TableField(GlobalConfig globalConfig, String columnName, CustomizedColumn defined) {
        this.globalConfig = globalConfig;
        this.columnName = columnName;
        if (defined != null) {
            defined.initField(this);
        }
    }

    public void initNamingAndType(com.github.meta.Column column) {
//        this.jdbcType = column.getType().getName() + column.getWidth();
        this.initFieldNameIfNeed();
        String capitalName = Naming.capitalFirst(this.removeIsIfNeed(this.name, globalConfig));
        this.initJavaTypeIfNeed(column);
        // is not null
//        this.notNull = !column.isNullable();
//        this.defaults = column.getDefaultValue();
        if (!isBlank(this.defaults) && this.isStringType()) {
            this.defaults = "'" + this.defaults + "'";
        }
        this.comment = column.getDescription();
    }

    private boolean isStringType() {
        return Objects.equals(String.class, this.javaType);
    }

    /**
     * 如果没有预设类型, 从column中获取对应的java类型
     *
     * @param column column元数据定义
     */
    private void initJavaTypeIfNeed(com.github.meta.Column column) {
        if (this.javaType != null) {
            return;
        }
        try {
            this.javaType = Class.forName(column.getJavaType().getFullyQualifiedName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (Date.class.isAssignableFrom(this.javaType)) {
            this.javaType = Date.class;
        }
    }

    /**
     * 如果没有预设类型, 从column中获取对应的java类型
     * <p>
     * 根据字段名称进行驼峰转换
     */
    private void initFieldNameIfNeed() {
        // 如果字段名称没有预设
        if (!isBlank(this.name)) {
            return;
        }
        Naming naming = globalConfig.getColumnNaming();
        if (naming == Naming.underline_to_camel) {
            this.name = Naming.underlineToCamel(this.columnName);
        } else {
            this.name = this.columnName;
        }
    }

    /**
     * Boolean类型is前缀处理
     *
     * @param input        字段名称
     * @param globalConfig GlobalConfig
     * @return ignore
     */
    private String removeIsIfNeed(String input, GlobalConfig globalConfig) {
        if (globalConfig.needRemoveIsPrefix(input, this.getJavaType())) {
            return input.substring(2);
        } else {
            return input;
        }
    }

    public String getType() {
        return javaType.getSimpleName();
    }

    /**
     * 是否主键
     *
     * @return ignore
     */
    public boolean isPrimary() {
        return this.category == FieldType.PrimaryKey || this.category == FieldType.PrimaryId;
    }

    /**
     * 是否自增主键
     *
     * @return ignore
     */
    public boolean isPrimaryId() {
        return this.category == FieldType.PrimaryId;
    }

    /**
     * 是否gmt字段
     *
     * @return ignore
     */
    public boolean isGmt() {
        return this.category == FieldType.GmtCreate || this.category == FieldType.GmtModified;
    }

    public String getColumnType() {
        return jdbcType;
    }

    /**
     * 是否逻辑删除字段
     *
     * @return ignore
     */
    public boolean isDeleted() {
        return this.category == FieldType.IsDeleted;
    }

    /**
     * 按字母排序
     *
     * @param field TableField
     * @return 排序
     */
    @Override
    public int compareTo(com.github.mybatis.database.model.TableField field) {
        if (field == null) {
            return 1;
        }
        int order = this.category.compareTo(field.category);
        if (order == 0) {
            order = this.name.compareTo(field.name);
        }
        return order;
    }
}