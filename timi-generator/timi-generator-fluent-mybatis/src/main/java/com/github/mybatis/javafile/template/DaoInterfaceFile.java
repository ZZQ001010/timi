package com.github.mybatis.javafile.template;

import com.github.mybatis.database.model.TableSetter;
import com.github.mybatis.javafile.template.AbstractTemplateFile;
import com.github.mybatis.javafile.template.EntityFile;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;

import static com.github.mybatis.javafile.template.DaoImplementFile.JavaDoc;
import static com.github.mybatis.util.ClassNames.FM_IBaseDao;

/**
 * dao interface代码生成构造
 *
 * @author wudarui
 */
public class DaoInterfaceFile extends AbstractTemplateFile {
    public DaoInterfaceFile(TableSetter table) {
        super(table);
        this.packageName = daoPackage(table);
        this.klassName = daoClass(table);
    }

    public static ClassName daoInterfaceName(TableSetter table) {
        return ClassName.get(daoPackage(table), daoClass(table));
    }

    public static String daoPackage(TableSetter table) {
        return table.getBasePackage() + ".dao.intf";
    }

    public static String daoClass(TableSetter table) {
        return table.getEntityPrefix() + "Dao";
    }

    @Override
    protected void build(TypeSpec.Builder spec) {
        spec.addSuperinterface(parameterizedType(FM_IBaseDao, EntityFile.entityName(table)))
            .addJavadoc("$T: 数据操作接口\n", super.className())
            .addJavadoc("$L", JavaDoc)
            .addJavadoc("@author Powered By Fluent Mybatis");
    }

    @Override
    protected boolean isInterface() {
        return true;
    }
}