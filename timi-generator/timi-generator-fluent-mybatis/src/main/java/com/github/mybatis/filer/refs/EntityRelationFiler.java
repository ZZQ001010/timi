package com.github.mybatis.filer.refs;

import com.github.mybatis.entity.EntityRefMethod;
import com.github.mybatis.entity.FluentEntity;
import com.github.mybatis.entity.FluentList;
import com.github.mybatis.javafile.AbstractFile;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

/**
 * IEntityRelation 接口类生成
 *
 * @author darui.wu
 */
public class EntityRelationFiler extends AbstractFile {
    private static final String EntityRelation = "IEntityRelation";

    public static ClassName getClassName() {
        return ClassName.get(FluentList.refsPackage(), EntityRelation);
    }

    public EntityRelationFiler() {
        this.packageName = FluentList.refsPackage();
        this.klassName = EntityRelation;
        this.comment = "实体类间自定义的关联关系接口";
    }

    @Override
    protected void build(TypeSpec.Builder spec) {
        for (FluentEntity fluent : FluentList.getFluents()) {
            for (EntityRefMethod refMethod : fluent.getRefMethods()) {
                if (refMethod.isAbstractMethod()) {
                    spec.addMethod(this.m_refMethod(fluent, refMethod));
                }
            }
        }
    }

    private MethodSpec m_refMethod(FluentEntity fluent, EntityRefMethod refField) {
        return MethodSpec.methodBuilder(refField.getRefMethod(fluent))
            .addParameter(fluent.entity(), "entity")
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .returns(refField.getJavaType())
            .addJavadoc("{@link $L#$L}", fluent.getClassName(), refField.getName())
            .build();
    }

    @Override
    protected boolean isInterface() {
        return true;
    }
}