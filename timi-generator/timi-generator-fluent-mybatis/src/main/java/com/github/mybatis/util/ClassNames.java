package com.github.mybatis.util;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import static com.github.mybatis.util.ClassNamePackage.Database_Package;
import static com.github.mybatis.util.ClassNamePackage.Datagen_Package;
import static com.github.mybatis.util.ClassNamePackage.Mybatis_Annotation_Package;
import static com.github.mybatis.util.ClassNamePackage.Mybatis_Base_Package;
import static com.github.mybatis.util.ClassNamePackage.Spring_Annotation_Package;
import static com.github.mybatis.util.ClassNamePackage.Test4J_Db_Annotation_Pack;
import static com.squareup.javapoet.ClassName.get;

/**
 * 常量ClassName定义
 *
 * @author wudarui
 */
public interface ClassNames {
    ClassName Test4J_IDataSourceScript = get(Database_Package, "IDataSourceScript");

    ClassName Test4J_ScriptTable = get(Test4J_Db_Annotation_Pack, "ScriptTable");

    ClassName Test4J_DataMap = get("org.test4j.tools.datagen", "DataMap");

    ClassName Test4J_IDataMap = get(Datagen_Package, "IDataMap");

    ClassName Test4J_IDatabase = get(Database_Package, "IDatabase");

    ClassName Test4J_KeyValue = get(Datagen_Package, "KeyValue");

    ClassName Test4J_ColumnDef = get(Test4J_Db_Annotation_Pack, "ColumnDef");

    ClassName Test4J_MixProxy = get("org.test4j.module.spec.internal", "MixProxy");

    ClassName Test4J_EqMode = get("org.test4j.hamcrest.matcher.modes", "EqMode");

    TypeName Test4J_EqModes = ArrayTypeName.of(Test4J_EqMode);
    /**
     * fluent mybatis annotations
     */
    ClassName FM_IBaseDao = get(Mybatis_Base_Package, "IBaseDao");

    ClassName FM_IEntity = get(Mybatis_Base_Package, "IEntity");

    ClassName FM_TableId = get(Mybatis_Annotation_Package, "TableId");

    ClassName FM_Version = get(Mybatis_Annotation_Package, "Version");

    ClassName FM_LogicDelete = get(Mybatis_Annotation_Package, "LogicDelete");

    ClassName FM_TableField = get(Mybatis_Annotation_Package, "TableField");

    ClassName FM_RefMethod = get("cn.org.atool.fluent.mybatis.annotation", "RefMethod");

    ClassName FM_FluentMybatis = get(Mybatis_Annotation_Package, "FluentMybatis");

    ClassName FM_FluentDbType = get("cn.org.atool.fluent.mybatis.metadata", "DbType");

    ClassName FM_RichEntity = get("cn.org.atool.fluent.mybatis.base", "RichEntity");

    ClassName FM_TableSupplier = get("cn.org.atool.fluent.mybatis.functions", "TableSupplier");

    /**
     * lombok annotations
     */
    ClassName Lombok_Data = get("lombok", "Data");

    ClassName Lombok_Accessors = get("lombok.experimental", "Accessors");

    ClassName Lombok_EqualsAndHashCode = get("lombok", "EqualsAndHashCode");
    /**
     * spring annotations
     */
    ClassName Spring_Repository = get(Spring_Annotation_Package, "Repository");

    ClassName CN_Class = ClassName.get(Class.class);

    ClassName CN_String = ClassName.get(String.class);
}