package com.github.mybatis.javafile.template;

import com.github.mybatis.database.DbTypeOfGenerator;
import com.github.mybatis.database.config.impl.RelationConfig;
import com.github.mybatis.database.model.ConfigKey;
import com.github.mybatis.database.model.TableField;
import com.github.mybatis.database.model.TableSetter;
import com.github.mybatis.javafile.template.AbstractTemplateFile;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import javax.lang.model.element.Modifier;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.github.mybatis.util.ClassNames.CN_Class;
import static com.github.mybatis.util.ClassNames.CN_String;
import static com.github.mybatis.util.ClassNames.FM_FluentDbType;
import static com.github.mybatis.util.ClassNames.FM_FluentMybatis;
import static com.github.mybatis.util.ClassNames.FM_IEntity;
import static com.github.mybatis.util.ClassNames.FM_LogicDelete;
import static com.github.mybatis.util.ClassNames.FM_RichEntity;
import static com.github.mybatis.util.ClassNames.FM_TableField;
import static com.github.mybatis.util.ClassNames.FM_TableId;
import static com.github.mybatis.util.ClassNames.FM_TableSupplier;
import static com.github.mybatis.util.ClassNames.FM_Version;
import static com.github.mybatis.util.ClassNames.Lombok_Accessors;
import static com.github.mybatis.util.ClassNames.Lombok_Data;
import static com.github.mybatis.util.ClassNames.Lombok_EqualsAndHashCode;
import static com.github.mybatis.util.GeneratorHelper.isBlank;

/**
 * Entity代码生成构造
 *
 * @author wudarui
 */
@SuppressWarnings("rawtypes")
public class EntityFile extends AbstractTemplateFile {

    public EntityFile(TableSetter table) {
        super(table);
        this.packageName = entityPackage(table);
        this.klassName = entityClass(table);
    }

    public static TypeName entityName(TableSetter table) {
        return ClassName.get(entityPackage(table), entityClass(table));
    }

    public static String entityPackage(TableSetter table) {
        return table.getBasePackage() + ".entity";
    }

    /**
     * 返回Entity类名
     *
     * @param table TableSetter
     * @return ignore
     */
    public static String entityClass(TableSetter table) {
        String entityPrefix = table.getEntityPrefix();
        if (entityPrefix.endsWith(table.getEntitySuffix())) {
            return entityPrefix;
        } else {
            return entityPrefix + table.getEntitySuffix();
        }
    }

    @Override
    protected void build(TypeSpec.Builder spec) {
        spec.addAnnotation(AnnotationSpec.builder(SuppressWarnings.class).addMember("value", "$S", "unchecked").build())
            .addAnnotation(Lombok_Data)
            .addAnnotation(AnnotationSpec.builder(Lombok_Accessors).addMember("chain", "true").build())
            .addAnnotation(AnnotationSpec.builder(Lombok_EqualsAndHashCode).addMember("callSuper", "false").build())
            .addAnnotation(this.fluentMybatisAnnotation())
            .addJavadoc("$T: 数据映射实体定义\n\n", super.className())
            .addJavadoc("$L", "@author Powered By Fluent Mybatis");
        // 继承接口
        spec.superclass(FM_RichEntity);
        this.addSuperInterface(spec, table.getEntityInterfaces());

        spec.addField(FieldSpec.builder(long.class, "serialVersionUID",
            Modifier.STATIC, Modifier.FINAL, Modifier.PRIVATE)
            .initializer("1L")
            .build());
        TableField primary = null;
        for (TableField field : table.getFields()) {
            FieldSpec.Builder fb = FieldSpec.builder(field.getJavaType(), field.getName(), Modifier.PRIVATE);
            fb.addJavadoc("$L", field.getComment());
            if (field.isPrimary() && !"user".equals(table.getSeqName())) {
                primary = field;
                fb.addAnnotation(this.getTableIdAnnotation(field));
            } else {
                fb.addAnnotation(this.getTableFieldAnnotation(field));
            }
            // 判断是否逻辑删除字段
            if (Objects.equals(field.getColumnName(), table.getLogicDeleted())) {
                fb.addAnnotation(FM_LogicDelete);
            }
            // 判断是否乐观锁字段
            if (Objects.equals(field.getColumnName(), table.getVersionField())) {
                fb.addAnnotation(FM_Version);
            }
            spec.addField(fb.build());
        }
        if (primary != null) {
            spec.addMethod(this.m_findPk(primary));
        }
        spec.addMethod(this.m_entityClass());
        spec.addMethod(this.m_changeTableBelongTo(FM_TableSupplier, "supplier"));
        spec.addMethod(this.m_changeTableBelongTo(CN_String, "table"));
        for (RelationConfig relation : table.getRelations()) {
            spec.addMethod(this.m_relation(relation));
        }
    }

    private MethodSpec m_changeTableBelongTo(ClassName pType, String pName) {
        return MethodSpec.methodBuilder("changeTableBelongTo")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addAnnotation(Override.class)
            .addParameter(pType, pName)
            .returns(this.className())
            .addStatement("return super.changeTableBelongTo($L)", pName)
            .build();
    }

    private MethodSpec m_entityClass() {
        return MethodSpec.methodBuilder("entityClass")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addAnnotation(Override.class)
            .returns(parameterizedType(CN_Class, WildcardTypeName.subtypeOf(FM_IEntity)))
            .addStatement("return $L.class", entityClass(table))
            .build();
    }

    /**
     * 设置关联字段和关联get方法
     *
     * @param relation RelationConfig
     * @return MethodSpec
     */
    private MethodSpec m_relation(RelationConfig relation) {
        String methodName = relation.methodName();
        return MethodSpec.methodBuilder(methodName)
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(relation.refMethodAnnotation())
            .returns(relation.returnType())
            .addJavadoc("$L", "实现定义在{@link cn.org.atool.fluent.mybatis.base.IRefs}子类Refs上")
            .addStatement("return super.invoke($S, $L)", methodName, relation.isCached())
            .build();
    }

    /**
     * 构造 @TableField(...)
     *
     * @param field TableField
     * @return ignore
     */
    private AnnotationSpec getTableFieldAnnotation(TableField field) {
        AnnotationSpec.Builder builder = AnnotationSpec.builder(FM_TableField)
            .addMember("value", "$S", field.getColumnName());
        if (!isBlank(field.getInsert())) {
            builder.addMember("insert", "$S", field.getInsert());
        }
        if (!isBlank(field.getUpdate())) {
            builder.addMember("update", "$S", field.getUpdate());
        }
        if (field.getIsLarge() != null && !field.getIsLarge()) {
            builder.addMember("notLarge", "$L", Boolean.FALSE.toString());
        }
        if (field.getTypeHandler() != null) {
            builder.addMember("typeHandler", "$T.class", field.getTypeHandler());
        }
        return builder.build();
    }

    /**
     * 构造 @TableId(...)
     *
     * @param field TableField
     * @return ignore
     */
    private AnnotationSpec getTableIdAnnotation(TableField field) {
        AnnotationSpec.Builder builder = AnnotationSpec.builder(FM_TableId)
            .addMember("value", "$S", field.getColumnName());
        String seqName = table.getSeqName();
        if (!this.isAuto(seqName, field.isPrimaryId())) {
            builder.addMember("auto", "$L", Boolean.FALSE.toString());
        }
        if (!isBlank(seqName) && !"auto".equals(seqName)) {
            builder.addMember("seqName", "$S", seqName);
        }
        return builder.build();
    }

    boolean isAuto(String seqName, boolean isPrimaryId) {
        return "auto".equals(seqName) || ("".equals(seqName) && isPrimaryId);
    }

    private MethodSpec m_findPk(TableField primary) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("findPk")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .returns(Serializable.class);
        builder.addStatement("return this.$L", primary.getName());
        return builder.build();
    }

    private void addSuperInterface(TypeSpec.Builder builder, List<Class> interfaces) {
        if (interfaces == null || interfaces.size() == 0) {
            return;
        }
        for (Class _interface : interfaces) {
            if (hasEntityType(_interface.getSimpleName(), _interface.getTypeParameters())) {
                builder.addSuperinterface(parameterizedType(ClassName.get(_interface), this.className()));
            } else {
                builder.addSuperinterface(_interface);
            }
        }
    }

    /**
     * 泛型参数只有一个，且可以设置为Entity对象
     *
     * @param interfaceName 接口名称
     * @param typeVariables 接口泛型参数列表
     * @return ignore
     */
    private boolean hasEntityType(String interfaceName, TypeVariable[] typeVariables) {
        if (typeVariables.length != 1) {
            return false;
        }
        for (Type bound : typeVariables[0].getBounds()) {
            String tn = bound.getTypeName();
            if (Objects.equals(tn, interfaceName) || Allow_Entity_Bounds.contains(tn)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Entity自定义接口的泛型如果是下列类型子类型，可以使用Entity作为泛型参数
     */
    private static final Set<String> Allow_Entity_Bounds = new HashSet<>();

    static {
        Allow_Entity_Bounds.add("cn.org.atool.fluent.mybatis.base.IEntity");
        Allow_Entity_Bounds.add(Object.class.getName());
        Allow_Entity_Bounds.add(Serializable.class.getName());
    }

    /**
     * 生成 @FluentMybatis注解
     *
     * @return ignore
     */
    private AnnotationSpec fluentMybatisAnnotation() {
        AnnotationSpec.Builder builder = AnnotationSpec
            .builder(FM_FluentMybatis);

        builder.addMember("table", "$S", table.getTableName());
        if (!isBlank(table.getMapperBeanPrefix())) {
            builder.addMember("mapperBeanPrefix", "$S", table.getMapperBeanPrefix());
        }
        if (table.getDefaults() != null) {
            builder.addMember("defaults", "$T.class", table.getDefaults());
        }
        if (table.getSuperMapper() != null) {
            builder.addMember("superMapper", "$T.class", table.getSuperMapper());
        }
        if (!Objects.equals(table.getEntitySuffix(), ConfigKey.Entity_Default_Suffix)) {
            builder.addMember("suffix", "$S", table.getEntitySuffix());
        }
        if (DbTypeOfGenerator.MYSQL != table.getGlobalConfig().getDbType()) {
            builder.addMember("dbType", "$T.$L",
                FM_FluentDbType, table.getGlobalConfig().getDbType().name());
        }
        return builder.build();
    }

    @Override
    protected boolean isInterface() {
        return false;
    }
}