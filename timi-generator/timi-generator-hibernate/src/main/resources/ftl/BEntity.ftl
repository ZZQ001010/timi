package ${packageName};

<#list imports as impt>
import ${impt};
</#list>
import cn.sunline.common.annotation.LogicalName;

/**
 * @author kite-maven-plugin
 * ${table.textName}
 */
@SuppressWarnings("serial")
@LogicalName("${table.textName}")
public class B${utils.dbName2ClassName(table.dbName)} implements Serializable {
		
	<#list table.columns as column>
		<#assign type = column.javaType.getShortName() />
		<#if column.domain ? exists>
			<#assign type = utils.dbName2ClassName(column.dbName) + "Def" />
			<#if column.domain.type ? exists>
				<#assign type = column.domain.type.getShortName() />
			</#if>
		</#if>
	@LogicalName("${column.textName}")
    private ${type} ${utils.dbName2PropertyName(column.dbName)};
    
	</#list>
	<#list table.columns as column>
	public static final String P_${column.dbName} = "${utils.dbName2PropertyName(column.dbName)}";
	
	</#list>
	
	<#list table.columns as column>
		<#assign type = column.javaType.getShortName() />
		<#if column.domain ? exists>
			<#assign type = utils.dbName2ClassName(column.dbName) + "Def" />
			<#if column.domain.type ? exists>
				<#assign type = column.domain.type.getShortName() />
			</#if>
		</#if>
	/**
	 * <p>${column.textName}</p>
	 	<#if (column.description ? exists) && (column.description != "") && !(column.domain ? exists)>
	 * <p>${column.description}</p>
	 	</#if>
	 */
	public ${type} get${utils.dbName2ClassName(column.dbName)}() {
		return ${utils.dbName2PropertyName(column.dbName)};
	}
	
	/**
	 * <p>${column.textName}</p>
	 	<#if (column.description ? exists) && (column.description != "") && !(column.domain ? exists)>
	 * <p>${column.description}</p>
	 	</#if>
	 */
	public void set${utils.dbName2ClassName(column.dbName)}(${type} ${utils.dbName2PropertyName(column.dbName)}) {
		this.${utils.dbName2PropertyName(column.dbName)} = ${utils.dbName2PropertyName(column.dbName)};
	}
	
	</#list>
}