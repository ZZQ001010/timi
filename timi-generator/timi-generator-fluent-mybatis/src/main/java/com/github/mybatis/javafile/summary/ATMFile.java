package com.github.mybatis.javafile.summary;

import com.github.mybatis.database.model.Naming;
import com.github.mybatis.database.model.TableSetter;
import com.github.mybatis.javafile.summary.AbstractSummaryFile;
import com.github.mybatis.javafile.template.DataMapFile;
import com.github.mybatis.javafile.template.TableMixFile;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.mybatis.util.ClassNames.Test4J_IDataSourceScript;
import static com.github.mybatis.util.ClassNames.Test4J_MixProxy;

public class ATMFile extends AbstractSummaryFile {

    public ATMFile(String basePackage, List<TableSetter> tables) {
        super(basePackage, tables);
        this.klassName = "ATM";
    }

    @Override
    protected void build(TypeSpec.Builder spec) {
        spec.addJavadoc("$L", "ATM: Application Table Manager\n\n")
            .addJavadoc("$L", "@author Powered By Test4J");
        spec.addField(this.f_dataMap())
            .addField(this.f_table())
            .addField(this.f_mixes())
            .addType(this.type_Table())
            .addType(this.type_DataMap())
            .addType(this.type_Mixes())
            .addType(this.type_Script());
    }

    private FieldSpec f_dataMap() {
        return FieldSpec.builder(TypeVariableName.get("DataMap"), "dataMap", Modifier.STATIC, Modifier.FINAL, Modifier.PUBLIC)
            .initializer("new DataMap()")
            .build();
    }

    private FieldSpec f_table() {
        return FieldSpec.builder(TypeVariableName.get("Table"), "table", Modifier.STATIC, Modifier.FINAL, Modifier.PUBLIC)
            .initializer("new Table()")
            .build();
    }

    private FieldSpec f_mixes() {
        return FieldSpec.builder(TypeVariableName.get("Mixes"), "mixes", Modifier.STATIC, Modifier.FINAL, Modifier.PUBLIC)
            .initializer("new Mixes()")
            .build();
    }

    private TypeSpec type_Script() {
        TypeSpec.Builder builder = this.getTypeBuilder("Script", false)
            .addSuperinterface(Test4J_IDataSourceScript)
            .addJavadoc("应用数据库创建脚本构造");

        MethodSpec.Builder mb = MethodSpec
            .methodBuilder("getTableKlass")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override.class)
            .returns(parameterizedType(List.class, Class.class))
            .addCode("return list(\n")
            .addCode(this.tables.stream().map(t -> "\t" + t.getEntityPrefix() + "DataMap.class").collect(Collectors.joining(",\n")))
            .addCode("\n);");

        builder.addMethod(mb.build());
        builder.addMethod(MethodSpec
            .methodBuilder("getIndexList")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override.class)
            .returns(TypeVariableName.get("IndexList"))
            .addStatement("return new IndexList()")
            .build());
        return builder.build();
    }

    private TypeSpec type_Mixes() {
        TypeSpec.Builder builder = this.getTypeBuilder("Mixes", false)
            .addJavadoc("应用表数据操作");

        MethodSpec.Builder mb = MethodSpec.methodBuilder("cleanAllTable")
            .addModifiers(Modifier.PUBLIC);
        for (TableSetter table : this.tables) {
            String mixName = Naming.lowerFirst(table.getEntityPrefix()) + "TableMix";
            builder.addField(FieldSpec
                .builder(TableMixFile.mixClass(table), mixName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .initializer("$T.proxy($T.class)", Test4J_MixProxy, TableMixFile.mixClass(table))
                .build());
            mb.addStatement("this.$L.clean$LTable()", mixName, table.getEntityPrefix());
        }
        builder.addMethod(mb.build());
        return builder.build();
    }

    private TypeSpec type_Table() {
        TypeSpec.Builder builder = this.getTypeBuilder("Table", false)
            .addJavadoc("应用表名");
        for (TableSetter table : this.tables) {
            builder.addField(FieldSpec
                .builder(String.class, Naming.lowerFirst(table.getEntityPrefix()))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .initializer("$S", table.getTableName())
                .build());
        }
        return builder.build();
    }

    private TypeSpec type_DataMap() {
        TypeSpec.Builder builder = this.getTypeBuilder("DataMap", false)
            .addJavadoc("table or entity data构造器");
        for (TableSetter table : this.tables) {
            builder.addField(FieldSpec
                .builder(TypeVariableName.get(DataMapFile.dmClassName(table) + ".Factory"), Naming.lowerFirst(table.getEntityPrefix()))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .initializer("new $T.Factory()", DataMapFile.dmName(table))
                .build());
        }
        return builder.build();
    }

    @Override
    protected boolean isInterface() {
        return true;
    }

    private TypeSpec.Builder getTypeBuilder(String className, boolean isInterface) {
        if (isInterface) {
            return TypeSpec.interfaceBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        } else {
            return TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        }
    }
}