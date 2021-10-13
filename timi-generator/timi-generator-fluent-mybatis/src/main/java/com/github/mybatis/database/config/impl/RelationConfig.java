package com.github.mybatis.database.config.impl;

import com.github.mybatis.annotation.Relation;
import com.github.mybatis.database.IRelationConfig;
import com.github.mybatis.database.config.impl.TableConfigSet;
import com.github.mybatis.database.model.Naming;
import com.github.mybatis.database.model.TableSetter;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.mybatis.util.ClassNames.FM_RefMethod;
import static com.github.mybatis.util.GeneratorHelper.isBlank;

/**
 * 关联关系设置
 *
 * @author darui.wu
 */
@Data
@Accessors(chain = true)
public class RelationConfig implements IRelationConfig {
    /**
     * 方法名称
     */
    private String method;

    private String sourceTable;

    private String sourcePackage;

    private String sourceEntity;

    private String targetTable;

    private String targetNoSuffix;

    private String targetPackage;

    private String targetEntity;

    private boolean isMany;
    /**
     * 是否缓存结果
     */
    private boolean cached;
    /**
     * 关联关系, 原始字段
     */
    private Map<String, String> relationByColumns = new HashMap<>();
    /**
     * 关联关系, Entity字段
     */
    private Map<String, String> relationByFields = new HashMap<>();

    public RelationConfig(TableConfigSet tableConfigSet, String source, String target) {
        this.sourceTable = source;
        this.targetTable = target;
        tableConfigSet.getTables().get(source).getRelations().add(this);
    }

    @Override
    public IRelationConfig table(String targetTable) {
        this.targetTable = targetTable;
        return this;
    }

    @Override
    public IRelationConfig setRelation(Map<String, String> relation, boolean reversed) {
        for (Map.Entry<String, String> entry : relation.entrySet()) {
            if (reversed) {
                this.relationByColumns.put(entry.getValue(), entry.getKey());
            } else {
                this.relationByColumns.put(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    /**
     * 根据column关系翻译成Entity属性关系
     *
     * @param source 来源表设置
     * @param target 目标表设置
     */
    public void properties(TableSetter source, TableSetter target) {
        for (Map.Entry<String, String> pair : this.relationByColumns.entrySet()) {
            String sourceField = source.getField(pair.getKey());
            String targetField = target.getField(pair.getValue());
            /*
             * Entity上的 @RefField 值设置和 @Relation上where设置方向是相反的
             */
            this.relationByFields.put(targetField, sourceField);
        }
    }

    private ClassName targetEntityClass() {
        return ClassName.get(this.targetPackage, this.targetEntity);
    }

    /**
     * 构造返回类型
     *
     * @return ignore
     */
    public TypeName returnType() {
        if (isMany) {
            return ParameterizedTypeName.get(ClassName.get(List.class), this.targetEntityClass());
        } else {
            return this.targetEntityClass();
        }
    }

    /**
     * 关联字段名称
     *
     * @return ignore
     */
    public String methodName() {
        if (!isBlank(this.method)) {
            return this.method;
        } else if (isMany) {
            return "find" + Naming.capitalFirst(this.targetNoSuffix) + "List";
        } else {
            return "find" + Naming.capitalFirst(this.targetNoSuffix);
        }
    }

    public AnnotationSpec refMethodAnnotation() {
        AnnotationSpec.Builder spec = AnnotationSpec.builder(FM_RefMethod);
        if (!this.relationByFields.isEmpty()) {
            String mapping = this.relationByFields.entrySet().stream()
                .map(e -> e.getKey() + " = " + e.getValue())
                .collect(Collectors.joining(" && "));
            spec.addMember("value", "$S", mapping);
        }
        return spec.build();
    }

    /**
     * 解析关联关系字段
     *
     * @param relation @Relation
     * @return ignore
     */
    public static Map<String, String> parseRelations(Relation relation) {
        String values = relation.where();
        Map<String, String> relations = new HashMap<>();
        if (isBlank(values)) {
            return relations;
        }
        /*
         * 不管单个&还是多个&&都处理
         */
        String[] pairs = values.split("&");
        for (String pair : pairs) {
            if (isBlank(pair)) {
                continue;
            }
            if (!isEquation(pair)) {
                throw new RuntimeException("the format of relation must be 'column_of_table1=column_of_table2', actual:" + pair);
            }
            String[] items = pair.split("=");
            relations.put(items[0].trim(), items[1].trim());
        }
        return relations;
    }

    /**
     * 是否等式
     *
     * @param expression 表达式
     * @return ignore
     */
    public static boolean isEquation(String expression) {
        if (isBlank(expression)) {
            return false;
        } else {
            return expression.trim().matches("[a-zA-Z0-9_]+\\s*=\\s*[a-zA-Z0-9_]+");
        }
    }
}