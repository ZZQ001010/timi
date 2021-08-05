package com.github.mybatis.javafile.template;

import com.github.mybatis.database.model.TableSetter;
import com.github.mybatis.javafile.template.AbstractTemplateFile;
import com.github.mybatis.javafile.template.DaoInterfaceFile;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import static com.github.mybatis.util.ClassNames.Spring_Repository;

/**
 * dao implement代码生成构造
 *
 * @author wudarui
 */
public class DaoImplementFile extends AbstractTemplateFile {

    public DaoImplementFile(TableSetter table) {
        super(table);
        this.packageName = daoImplPackage(table);
        this.klassName = daoImplClass(table);
    }

    public static TypeName daoImplementName(TableSetter table) {
        return ClassName.get(daoImplPackage(table), daoImplClass(table));
    }

    public static String daoImplPackage(TableSetter table) {
        return table.getBasePackage() + ".dao.impl";
    }

    public static String daoImplClass(TableSetter table) {
        return table.getEntityPrefix() + "DaoImpl";
    }

    @Override
    protected void build(TypeSpec.Builder spec) {
        spec.addAnnotation(Spring_Repository)
            .addJavadoc("$T: 数据操作接口实现\n", super.className())
            .addJavadoc("$L", JavaDoc)
            .addJavadoc("@author Powered By Fluent Mybatis");
        spec.superclass(ClassName.get(
            table.getBasePackage() + ".dao.base",
            table.getEntityPrefix() + "BaseDao"));
        spec.addSuperinterface(DaoInterfaceFile.daoInterfaceName(table));
    }

    public static final String JavaDoc = "\n这只是一个减少手工创建的模板文件\n" +
        "可以任意添加方法和实现, 更改作者和重定义类名\n<p/>";

    @Override
    protected boolean isInterface() {
        return false;
    }
}