#Domain ${code}的中文本地化文件
<#if valueMap ? exists>
<#list valueMap ? keys as key> 
${key}=${valueMap[key]}
</#list>
</#if>
