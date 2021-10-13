package com.github.mybatis.database.model;

import com.github.mybatis.database.model.TableField;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

import java.util.Objects;

import static com.github.mybatis.util.GeneratorHelper.isBlank;


/**
 * 预定义好的字段
 *
 * @author wudarui
 */
@SuppressWarnings({"rawtypes", "unused", "UnusedReturnValue"})
@Getter
@Setter
@Accessors(chain = true)
public class CustomizedColumn {
    @Setter(AccessLevel.NONE)
    private String columnName;

    private String fieldName;

    private Class javaType;
    /**
     * typeHandler
     */
    private Class<? extends TypeHandler> typeHandler;
    /**
     * 默认不是大字段
     */
    @Setter(AccessLevel.NONE)
    private boolean notLarge = true;
    /**
     * 不生成映射字段
     */
    @Setter(AccessLevel.NONE)
    private boolean exclude = false;
    /**
     * update默认值
     */
    private String update;
    /**
     * insert默认值
     */
    private String insert;

    public CustomizedColumn(String columnName, String fieldName, Class javaType) {
        this.columnName = columnName;
        this.fieldName = fieldName;
        this.javaType = javaType;
    }

    public CustomizedColumn(String columnName) {
        this.columnName = columnName;
    }

    /**
     * 设置为大字段
     *
     * @return ignore
     */
    public com.github.mybatis.database.model.CustomizedColumn setLarge() {
        this.notLarge = false;
        return this;
    }

    /**
     * 设置为排除字段
     *
     * @return ignore
     */
    public com.github.mybatis.database.model.CustomizedColumn setExclude() {
        this.exclude = true;
        return this;
    }

    /**
     * 根据预设的字段设置初始化映射关系
     *
     * @param field 字段映射
     */
    public void initField(TableField field) {
        if (!isBlank(this.fieldName)) {
            field.setName(this.fieldName);
        }
        if (this.javaType != null) {
            field.setJavaType(this.javaType);
        }
        if (this.typeHandler != null && !Objects.equals(UnknownTypeHandler.class, this.typeHandler)) {
            field.setTypeHandler(this.typeHandler);
        }
        if (!this.notLarge) {
            field.setIsLarge(false);
        }
        if (!isBlank(this.insert)) {
            field.setInsert(this.insert);
        }
        if (!isBlank(this.update)) {
            field.setUpdate(this.update);
        }
    }
}