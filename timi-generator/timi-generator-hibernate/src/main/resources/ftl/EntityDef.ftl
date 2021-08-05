package ${packageName};

/**
 * @author kite-maven-plugin
 */
public enum ${utils.dbName2ClassName(domain.code)}Def {
	<#if domain.valueMap ? exists>
	<#list domain.valueMap?keys as key>
	
	/**
	 * ${domain.valueMap[key][0]} 
	 */	
	${key}<#if key_has_next>,<#else>;</#if>
	</#list>
	</#if>
}