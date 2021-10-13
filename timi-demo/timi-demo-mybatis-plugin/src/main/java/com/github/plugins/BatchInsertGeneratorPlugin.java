package com.github.plugins;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.VisitableElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.config.GeneratedKey;
import org.mybatis.generator.internal.util.StringUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zzq
 * @date 2021/10/13
 */
public class BatchInsertGeneratorPlugin extends PluginAdapter {
    
    protected static final Logger logger = LoggerFactory.getLogger(BatchInsertGeneratorPlugin.class);
    
    protected List<String> warnings;
    
    public static final String METHOD_BATCH_INSERT = "batchInsert";
    
    @Override
    public boolean validate(List<String> warnings) {
        this.warnings = warnings;
        // 插件使用前提是targetRuntime为MyBatis3
        if (StringUtility.stringHasValue(getContext().getTargetRuntime()) && "MyBatis3".equalsIgnoreCase(getContext().getTargetRuntime()) == false) {
            warnings.add("itfsw:插件" + this.getClass().getTypeName() + "要求运行targetRuntime必须为MyBatis3！");
            return false;
        }
    
        return true;
    }
    
    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        super.initialized(introspectedTable);
        if (StringUtility.stringHasValue(introspectedTable.getTableConfiguration().getAlias())) {
            warnings.add("itfsw:插件" + this.getClass().getTypeName() + "请不要配置alias属性，这个属性官方支持也很混乱，导致插件支持会存在问题！");
        }
    }
    
    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        // 1. batchInsert
        FullyQualifiedJavaType listType = FullyQualifiedJavaType.getNewListInstance();
        listType.addTypeArgument(introspectedTable.getRules().calculateAllFieldsClass());
        Method mBatchInsert = generateMethod(
                METHOD_BATCH_INSERT,
                JavaVisibility.DEFAULT,
                FullyQualifiedJavaType.getIntInstance(),
                new Parameter(listType, "list", "@org.apache.ibatis.annotations.Param(\"list\")")
    
        );
        // interface 增加方法
        List<Method> methods = interfaze.getMethods();
        methods.add(mBatchInsert);
        logger.debug("itfsw(批量插入插件):" + interfaze.getType().getShortName() + "增加batchInsert方法。");
        return true;
    }
    
    /**
     * @author zzq
     * @date 2021/10/13 下午4:59
     *
     *   TODO foreach 没有编写
     *
     */
    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        // 1. batchInsert
        XmlElement batchInsertEle = new XmlElement("insert");
        batchInsertEle.addAttribute(new Attribute("id", METHOD_BATCH_INSERT));
        // 参数类型
        batchInsertEle.addAttribute(new Attribute("parameterType", "map"));
        // 添加注释(!!!必须添加注释，overwrite覆盖生成时，@see XmlFileMergerJaxp.isGeneratedNode会去判断注释中是否存在OLD_ELEMENT_TAGS中的一点，例子：@mbg.generated)
        //        commentGenerator.addComment(batchInsertEle);
    
        // 使用JDBC的getGenereatedKeys方法获取主键并赋值到keyProperty设置的领域模型属性中。所以只支持MYSQL和SQLServer
        useGeneratedKeys(batchInsertEle, introspectedTable, null);
    
        batchInsertEle.addElement(new TextElement("insert into " + introspectedTable.getFullyQualifiedTableNameAtRuntime()));
        for (VisitableElement element : generateKeys(ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns()))) {
            batchInsertEle.addElement(element);
        }
    
        // 添加foreach节点
        XmlElement foreachElement = new XmlElement("foreach");
        foreachElement.addAttribute(new Attribute("collection", "list"));
        foreachElement.addAttribute(new Attribute("item", "item"));
        foreachElement.addAttribute(new Attribute("separator", ","));
    
        for (VisitableElement element : generateValues(ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns()), "item.")) {
            foreachElement.addElement(element);
        }
    
        // values 构建
        batchInsertEle.addElement(new TextElement("values"));
        batchInsertEle.addElement(foreachElement);
        document.getRootElement().addElement(batchInsertEle);
        logger.debug("itfsw(批量插入插件):" + introspectedTable.getMyBatis3XmlMapperFileName() + "增加batchInsert实现方法。");
    
        // 2. batchInsertSelective
        XmlElement batchInsertSelectiveEle = new XmlElement("insert");
        return true;
    }
    
    
    private List<VisitableElement> generateValues(List<IntrospectedColumn> removeIdentityAndGeneratedAlwaysColumns, String s) {
        return new ArrayList<>();
    }
    
    private List<VisitableElement> generateKeys(List<IntrospectedColumn> columns) {
        return new ArrayList<>();
    }
    
    public static void useGeneratedKeys(XmlElement element, IntrospectedTable introspectedTable, String prefix) {
        GeneratedKey gk = introspectedTable.getGeneratedKey();
        if (gk != null) {
            IntrospectedColumn introspectedColumn = safeGetColumn(introspectedTable, gk.getColumn());
            // if the column is null, then it's a configuration error. The
            // warning has already been reported
            if (introspectedColumn != null) {
                // 使用JDBC的getGenereatedKeys方法获取主键并赋值到keyProperty设置的领域模型属性中。所以只支持MYSQL和SQLServer
                element.addAttribute(new Attribute("useGeneratedKeys", "true"));
                element.addAttribute(new Attribute("keyProperty", (prefix == null ? "" : prefix) + introspectedColumn.getJavaProperty()));
                element.addAttribute(new Attribute("keyColumn", introspectedColumn.getActualColumnName()));
            }
        }
    }
    
    /**
     * 安全获取column 通过正则获取的name可能包含beginningDelimiter&&endingDelimiter
     * @param introspectedTable
     * @param columnName
     * @return
     */
    public static IntrospectedColumn safeGetColumn(IntrospectedTable introspectedTable, String columnName) {
        // columnName
        columnName = columnName.trim();
        // 过滤
        String beginningDelimiter = introspectedTable.getContext().getBeginningDelimiter();
        if (StringUtility.stringHasValue(beginningDelimiter)) {
            columnName = columnName.replaceFirst("^" + beginningDelimiter, "");
        }
        String endingDelimiter = introspectedTable.getContext().getEndingDelimiter();
        if (StringUtility.stringHasValue(endingDelimiter)) {
            columnName = columnName.replaceFirst(endingDelimiter + "$", "");
        }
        
        return introspectedTable.getColumn(columnName).get();
    }
    
    
    
    
    
    
    
    
    
    /**
     * 生成方法
     * @param methodName 方法名
     * @param visibility 可见性
     * @param returnType 返回值类型
     * @param parameters 参数列表
     * @return
     */
    public static Method generateMethod(String methodName, JavaVisibility visibility, FullyQualifiedJavaType returnType, Parameter... parameters) {
        Method method = new Method(methodName);
        method.setVisibility(visibility);
        method.setAbstract(true);
        method.setReturnType(returnType);
        if (parameters != null) {
            for (Parameter parameter : parameters) {
                method.addParameter(parameter);
            }
        }
        
        return method;
    }
    
}
