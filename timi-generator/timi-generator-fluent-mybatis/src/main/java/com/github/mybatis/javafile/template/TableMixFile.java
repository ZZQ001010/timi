package com.github.mybatis.javafile.template;

import com.github.mybatis.database.model.TableSetter;
import com.github.mybatis.javafile.template.AbstractTemplateFile;
import com.github.mybatis.javafile.template.DataMapFile;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

import static com.github.mybatis.database.model.ConfigKey.QuotaStr;
import static com.github.mybatis.util.ClassNames.Test4J_EqModes;

/**
 * TableMixFile
 *
 * @author wudarui
 */
@SuppressWarnings("unused")
public class TableMixFile extends AbstractTemplateFile {
    static ClassName IMix = ClassName.get("org.test4j.module.spec", "IMix");

    static ClassName Step = ClassName.get("org.test4j.module.spec.annotations", "Step");

    static ClassName EqMode = ClassName.get("org.test4j.hamcrest.matcher.modes", "EqMode");

    public TableMixFile(TableSetter table) {
        super(table);
        this.packageName = mixPackage(table);
        this.klassName = mixClassName(table);
    }

    public static String mixPackage(TableSetter table) {
        return table.getBasePackage() + ".mix";
    }

    public static String mixClassName(TableSetter table) {
        return table.getEntityPrefix() + "TableMix";
    }

    public static ClassName mixClass(TableSetter table) {
        return ClassName.get(mixPackage(table), mixClassName(table));
    }

    @Override
    protected void build(TypeSpec.Builder builder) {
        builder
            .addSuperinterface(IMix)
            .addJavadoc("数据库[$L]表数据准备和校验通用方法\n\n", table.getTableName())
            .addJavadoc("@author Powered By Test4J");

        builder.addMethod(this.m_clean_table());
        builder.addMethod(this.m_ready_table());
        builder.addMethod(this.m_check_table());
        builder.addMethod(this.m_check_table_where_1());
        builder.addMethod(this.m_check_table_where_2());

        builder.addMethod(this.m_count_table_1());
        builder.addMethod(this.m_count_table_2());
        builder.addMethod(this.m_count_table_3());
    }

    private MethodSpec m_count_table_3() {
        return this.initMethod("count")
            .addAnnotation(this.stepAnnotation("验证表[$L]有{1}条数据", table.getTableName()))
            .addParameter(int.class, "count")
            .addStatement("db.table($S).query().sizeEq(count)", table.getTableName())
            .addStatement("return this")
            .build();
    }

    private MethodSpec m_count_table_2() {
        return this.initMethod("count")
            .addAnnotation(this.stepAnnotation("验证表[$L]有{1}条符合条件{2}的数据", table.getTableName()))
            .addParameter(int.class, "count")
            .addParameter(String.class, "where")
            .addStatement("db.table($S).queryWhere(where).sizeEq(count)", table.getTableName())
            .addStatement("return this")
            .build();
    }

    private MethodSpec m_count_table_1() {
        return this.initMethod("count")
            .addAnnotation(this.stepAnnotation("验证表[$L]有{1}条符合条件{2}的数据", table.getTableName()))
            .addParameter(int.class, "count")
            .addParameter(DataMapFile.dmName(table), "where")
            .addStatement("db.table($S).queryWhere(where).sizeEq(count)", table.getTableName())
            .addStatement("return this")
            .build();
    }

    private MethodSpec m_check_table_where_2() {
        return this.initMethod("check")
            .addAnnotation(this.stepAnnotation("验证表[$L]有符合条件{1}的数据{2}", table.getTableName()))
            .varargs(true)
            .addParameter(DataMapFile.dmName(table), "where")
            .addParameter(DataMapFile.dmName(table), "data")
            .addParameter(Test4J_EqModes, "modes")
            .addStatement("db.table($S).queryWhere(where).eqDataMap(data, modes)", table.getTableName())
            .addStatement("return this")
            .build();
    }

    private MethodSpec m_check_table_where_1() {
        return this.initMethod("check")
            .addAnnotation(this.stepAnnotation("验证表[$L]有符合条件{1}的数据{2}", table.getTableName()))
            .varargs(true)
            .addParameter(String.class, "where")
            .addParameter(DataMapFile.dmName(table), "data")
            .addParameter(Test4J_EqModes, "modes")
            .addStatement("db.table($S).queryWhere(where).eqDataMap(data, modes)", table.getTableName())
            .addStatement("return this")
            .build();
    }

    private MethodSpec m_check_table() {
        return this.initMethod("check")
            .addAnnotation(this.stepAnnotation("验证表[$L]有全表数据{1}", table.getTableName()))
            .varargs(true)
            .addParameter(DataMapFile.dmName(table), "data")
            .addParameter(Test4J_EqModes, "modes")
            .addStatement("db.table($S).query().eqDataMap(data, modes)", table.getTableName())
            .addStatement("return this")
            .build();
    }

    private MethodSpec m_ready_table() {
        return this.initMethod("ready")
            .addAnnotation(this.stepAnnotation("准备表[$L]数据{1}", table.getTableName()))
            .addParameter(DataMapFile.dmName(table), "data")
            .addStatement("db.table($S).insert(data)", table.getTableName())
            .addStatement("return this")
            .build();
    }

    private MethodSpec m_clean_table() {
        return this.initMethod("clean")
            .addAnnotation(this.stepAnnotation("清空表[$L]数据", table.getTableName()))
            .addStatement("db.table($S).clean()", table.getTableName())
            .addStatement("return this")
            .build();
    }

    @Override
    protected boolean isInterface() {
        return false;
    }

    private AnnotationSpec stepAnnotation(String format, Object... args) {
        return AnnotationSpec.builder(Step)
            .addMember("value", QuotaStr + format + QuotaStr, args)
            .build();
    }

    private MethodSpec.Builder initMethod(String method) {
        return MethodSpec.methodBuilder(method + table.getEntityPrefix() + "Table")
            .addModifiers(Modifier.PUBLIC)
            .returns(mixClass(table));
    }
}