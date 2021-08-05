#表${table.dbName}的中文本地化文件
<#if table.columns ? exists>
<#list table.columns as column> 
${utils.dbName2PropertyName(column.dbName)}=${column.textName}
${utils.dbName2PropertyName(column.dbName)}Hint=<#if column.hint ? exists>${column.hint}<#else>null</#if>
</#list>
</#if>
