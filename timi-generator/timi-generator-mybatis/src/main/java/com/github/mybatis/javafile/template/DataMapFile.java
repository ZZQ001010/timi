package com.github.mybatis.javafile.template;

import com.github.mybatis.database.model.TableField;
import com.github.mybatis.database.model.TableSetter;
import com.github.mybatis.javafile.template.AbstractTemplateFile;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.util.Date;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.github.mybatis.util.ClassNames.Test4J_ColumnDef;
import static com.github.mybatis.util.ClassNames.Test4J_DataMap;
import static com.github.mybatis.util.ClassNames.Test4J_EqModes;
import static com.github.mybatis.util.ClassNames.Test4J_IDataMap;
import static com.github.mybatis.util.ClassNames.Test4J_IDatabase;
import static com.github.mybatis.util.ClassNames.Test4J_KeyValue;
import static com.github.mybatis.util.ClassNames.Test4J_ScriptTable;
import static com.github.mybatis.util.GeneratorHelper.isBlank;

public class DataMapFile extends AbstractTemplateFile {

    public DataMapFile(TableSetter table) {
        super(table);
        this.packageName = dmPackage(table);
        this.klassName = dmClassName(table);
    }

    public static ClassName dmName(TableSetter table) {
        return ClassName.get(dmPackage(table), dmClassName(table));
    }

    public static String dmPackage(TableSetter table) {
        return table.getBasePackage() + ".dm";
    }

    public static String dmClassName(TableSetter table) {
        return table.getEntityPrefix() + "DataMap";
    }

    @Override
    protected void build(TypeSpec.Builder builder) {
        builder.addAnnotation(AnnotationSpec
            .builder(Test4J_ScriptTable)
            .addMember("value", "$S", table.getTableName())
            .build())
            .superclass(parameterizedType(Test4J_DataMap, dmName(table)));
        builder.addAnnotation(AnnotationSpec
            .builder(SuppressWarnings.class)
            .addMember("value", "{$S, $S}", "unused", "rawtypes")
            .build());

        builder
            .addJavadoc("$T: 表(实体)数据对比(插入)构造器\n\n", super.className())
            .addJavadoc("@author Powered By Test4J");

        builder.addField(FieldSpec.builder(boolean.class, "isTable",
            Modifier.PRIVATE)
            .build());
        builder.addField(FieldSpec.builder(parameterizedType(Supplier.class, Boolean.class),
            "supplier", Modifier.PRIVATE, Modifier.FINAL)
            .initializer("() -> this.isTable")
            .build());
        for (TableField field : table.getFields()) {
            builder.addField(this.buildField(field));
        }
        builder.addMethod(this.m_constructor1());
        builder.addMethod(this.m_constructor2());
        builder.addMethod(this.m_init());
        builder.addMethod(this.m_with());
        builder.addMethod(this.m_table_0());
        builder.addMethod(this.m_table_1());
        builder.addMethod(this.m_entity_0());
        builder.addMethod(this.m_entity_1());
        // clean insert method
        builder.addMethod(this.m_eqTable());
        builder.addMethod(this.m_eqQuery_1());
        builder.addMethod(this.m_eqQuery_2());
        builder.addMethod(this.m_clean());
        builder.addMethod(this.m_insert());
        builder.addMethod(this.m_cleanAndInsert());
        // Factory Class
        builder.addType(this.clazz_Factory());
    }

    private MethodSpec m_eqQuery_1() {
        return this.initPublicMethod("eqQuery")
            .addJavadoc("DataMap数据和表[$L]数据比较", table.getTableName())
            .varargs(true)
            .addParameter(String.class, "query")
            .addParameter(Test4J_EqModes, "modes")
            .addStatement("$T.db.table($S).queryWhere(query).eqDataMap(this, modes)", Test4J_IDatabase, table.getTableName())
            .addStatement("return this")
            .build();
    }

    private MethodSpec m_eqQuery_2() {
        return this.initPublicMethod("eqQuery")
            .addJavadoc("DataMap数据和表[$L]数据比较", table.getTableName())
            .varargs(true)
            .addParameter(Test4J_IDataMap, "query")
            .addParameter(Test4J_EqModes, "modes")
            .addStatement("$T.db.table($S).queryWhere(query).eqDataMap(this, modes)", Test4J_IDatabase, table.getTableName())
            .addStatement("return this")
            .build();
    }

    private MethodSpec m_eqTable() {
        return this.initPublicMethod("eqTable")
            .addJavadoc("DataMap数据和表[$L]数据比较", table.getTableName())
            .varargs(true)
            .addParameter(Test4J_EqModes, "modes")
            .addStatement("$T.db.table($S).query().eqDataMap(this, modes)", Test4J_IDatabase, table.getTableName())
            .addStatement("return this")
            .build();
    }

    private MethodSpec m_clean() {
        return this.initPublicMethod("clean")
            .addJavadoc("清空[$L]表数据", table.getTableName())
            .addStatement("$T.db.cleanTable($S)", Test4J_IDatabase, table.getTableName())
            .addStatement("return this")
            .build();
    }

    private MethodSpec m_insert() {
        return this.initPublicMethod("insert")
            .addJavadoc("插入[$L]表数据", table.getTableName())
            .addStatement("$T.db.table($S).insert(this)", Test4J_IDatabase, table.getTableName())
            .addStatement("return this")
            .build();
    }

    private MethodSpec m_cleanAndInsert() {
        return this.initPublicMethod("cleanAndInsert")
            .addJavadoc("先清空, 再插入[$L]表数据", table.getTableName())
            .addStatement("return this.clean().insert()")
            .build();
    }

    private MethodSpec m_Factory_entity_0() {
        return this.initPublicMethod("entity")
            .addStatement("return $T.entity()", dmName(table))
            .build();
    }

    private MethodSpec m_Factory_entity_1() {
        return this.initPublicMethod("entity")
            .addParameter(int.class, "size")
            .addStatement("return  $T.entity(size)", dmName(table))
            .build();
    }

    private MethodSpec m_Factory_initTable_0() {
        return this.initPublicMethod("initTable")
            .addStatement("return $T.table().init()", dmName(table))
            .build();
    }

    private MethodSpec m_Factory_initTable_1() {
        return this.initPublicMethod("initTable")
            .addParameter(int.class, "size")
            .addStatement("return  $T.table(size).init()", dmName(table))
            .build();
    }

    private MethodSpec m_Factory_table_0() {
        return this.initPublicMethod("table")
            .addStatement("return $T.table()", dmName(table))
            .build();
    }

    private MethodSpec m_Factory_table_1() {
        return this.initPublicMethod("table")
            .addParameter(int.class, "size")
            .addStatement("return  $T.table(size)", dmName(table))
            .build();
    }

    private MethodSpec m_entity_1() {
        return this.initStaticMethod("entity")
            .addParameter(int.class, "size")
            .addStatement("return new $T(false, size)", dmName(table))
            .build();
    }

    private MethodSpec m_entity_0() {
        return this.initStaticMethod("entity")
            .addStatement("return new $T(false, 1)", dmName(table))
            .build();
    }

    private MethodSpec m_table_1() {
        return this.initStaticMethod("table")
            .addParameter(int.class, "size")
            .addStatement("return new $T(true, size)", dmName(table))
            .build();
    }

    private MethodSpec m_table_0() {
        return this.initStaticMethod("table")
            .addStatement("return new $T(true, 1)", dmName(table))
            .build();
    }


    private MethodSpec m_with() {
        MethodSpec.Builder builder = this.initPublicMethod("with")
            .addParameter(parameterizedType(ClassName.get(Consumer.class), dmName(table)), "init")
            .addStatement("init.accept(this)")
            .addStatement("return this");
        return builder.build();
    }

    private MethodSpec m_init() {
        MethodSpec.Builder builder = this.initPublicMethod("init");
        builder.addJavadoc("创建$L\n", this.klassName);
        builder.addJavadoc("初始化主键和gmtCreate, gmtModified, isDeleted等特殊值");
        for (TableField field : table.getFields()) {
            if (field.isPrimaryId()) {
                builder.addStatement("this.$L.autoIncrease()", field.getName());
            }
            if (field.isGmt()) {
                builder.addStatement("this.$L.values(new $T())", field.getName(), Date.class);
            }
            if (field.isDeleted() && field.getJavaType() == Boolean.class) {
                builder.addStatement("this.$L.values(false)", field.getName());
            }
        }
        builder.addStatement("return this");
        return builder.build();
    }

    private FieldSpec buildField(TableField field) {
        FieldSpec.Builder builder = FieldSpec.builder(parameterizedType(Test4J_KeyValue, dmName(table)),
            field.getName(), Modifier.PUBLIC, Modifier.FINAL, Modifier.TRANSIENT);
        AnnotationSpec.Builder ab = AnnotationSpec.builder(Test4J_ColumnDef)
            .addMember("value", "$S", field.getColumnName())
            .addMember("type", "$S", field.getJdbcType());
        if (field.isPrimary()) {
            ab.addMember("primary", "true");
        }
        if (field.isPrimaryId()) {
            ab.addMember("autoIncrease", "true");
        }
        if (field.isNotNull()) {
            ab.addMember("notNull", "true");
        }
        if (!isBlank(field.getDefaults())) {
            ab.addMember("defaultValue", "$S", field.getDefaults());
        }

        builder.addAnnotation(ab.build());
        builder.initializer("new KeyValue<>(this, $S, $S, supplier)", field.getColumnName(), field.getName());
        return builder.build();
    }

    private MethodSpec m_constructor1() {
        return MethodSpec.constructorBuilder()
            .addParameter(boolean.class, "isTable")
            .addStatement("super()")
            .addStatement("this.isTable = isTable")
            .build();
    }

    private MethodSpec m_constructor2() {
        return MethodSpec.constructorBuilder()
            .addParameter(boolean.class, "isTable")
            .addParameter(int.class, "size")
            .addStatement("super(size)")
            .addStatement("this.isTable = isTable")
            .build();
    }

    private TypeSpec clazz_Factory() {
        return TypeSpec.classBuilder("Factory")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
//            .addField(this.f_tableName())
            .addMethod(this.m_Factory_table_0())
            .addMethod(this.m_Factory_table_1())
            .addMethod(this.m_Factory_initTable_0())
            .addMethod(this.m_Factory_initTable_1())
            .addMethod(this.m_Factory_entity_0())
            .addMethod(this.m_Factory_entity_1())
            .build();
    }

    @Override
    protected boolean isInterface() {
        return false;
    }

    private MethodSpec.Builder initStaticMethod(String method) {
        return MethodSpec.methodBuilder(method)
            .returns(dmName(table))
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
    }

    private MethodSpec.Builder initPublicMethod(String method) {
        return MethodSpec.methodBuilder(method)
            .returns(dmName(table))
            .addModifiers(Modifier.PUBLIC);
    }
}