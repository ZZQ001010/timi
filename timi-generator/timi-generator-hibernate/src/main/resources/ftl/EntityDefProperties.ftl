<#if domain.valueMap ? exists>
	<#list domain.valueMap?keys as key>
${packageName}.${utils.dbName2ClassName(domain.code)}Def.${key}=<#if (domain.valueMap[key]?size gt indexNum)>${domain.valueMap[key][indexNum]}<#else>${key}</#if>
	</#list>
</#if>