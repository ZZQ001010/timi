package ${packageName};

<#assign className=utils.dbName2ClassName(table.dbName) />
<#assign pkType=table.primaryKeyColumns[0].getJavaType().getShortName() />
<#if (table.primaryKeyColumns?size>1)>
<#assign pkType=className + "Key" />
</#if>
<#list imports as impt>
import ${impt};
</#list>

/**
 * @author kite-maven-plugin
 */
public interface R${className} extends JpaRepository<${className}, ${pkType}>, QuerydslPredicateExecutor<${className}> {
	<#list table.uniques as groups>
	<#assign methodName="findBy" />
    <#assign arguments="" />
	<#list groups as unique>
	<#assign methodName= methodName + utils.dbName2ClassName(unique.dbName) />
	<#assign type = unique.javaType.getShortName() />
	<#if unique.domain ? exists>
	<#if unique.domain.type ? exists>
	<#assign type = unique.domain.type.getShortName() />
	</#if>
	</#if>
	<#assign arguments=arguments + type + " " + utils.dbName2PropertyName(unique.dbName) />
	<#if unique_has_next>
	<#assign methodName= methodName + "And" />
	<#assign arguments= arguments + ", " />
	</#if>
	</#list>
	
	public ${className} ${methodName}(${arguments});
	</#list>
	<#list table.indexes as groups>
	<#assign methodName="findBy" />
    <#assign arguments="" />
	<#list groups as indexes>
	<#assign methodName= methodName + utils.dbName2ClassName(indexes.dbName) />
	<#assign type = indexes.javaType.getShortName() />
	<#if indexes.domain ? exists>
	<#if indexes.domain.type ? exists>
	<#assign type = indexes.domain.type.getShortName() />
	</#if>
	</#if>
	<#assign arguments=arguments + type + " " + utils.dbName2PropertyName(indexes.dbName) />
	<#if indexes_has_next>
	<#assign methodName= methodName + "And" />
	<#assign arguments= arguments + ", " />
	</#if>
	</#list>
	
	public List<${className}> ${methodName}(${arguments});
	</#list>
}