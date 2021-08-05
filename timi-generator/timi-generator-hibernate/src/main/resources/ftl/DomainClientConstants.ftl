package ${packageName};

<#list imports as impt>
import ${impt};
</#list>

/**
 * @author kite-maven-plugin
 */
public interface ${utils.dbName2ClassName(domain.code)}Constants extends ConstantsWithLookup {
	<#if domain.valueMap ? exists>
	<#list domain.valueMap ? keys as key> 
	String ${key}();
	
	</#list>
	</#if>
}