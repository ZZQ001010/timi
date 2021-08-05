package ${packageName};

<#list imports as impt>
import ${impt};
</#list>

/**
 * @author kite-maven-plugin
 */
public interface ${utils.dbName2ClassName(table.dbName)}Constants extends Constants {
	<#if table.columns ? exists>
	<#list table.columns as column> 
	String ${utils.dbName2PropertyName(column.dbName)}();
	
	<#if column.hint ? exists>
	String ${utils.dbName2PropertyName(column.dbName)}Hint();
	
	</#if>
	</#list>
	</#if>
}