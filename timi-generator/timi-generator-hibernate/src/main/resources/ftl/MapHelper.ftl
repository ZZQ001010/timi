package ${packageName};

<#assign className=utils.dbName2ClassName(table.dbName)>
<#list imports as impt>
import ${impt};
</#list>

/**
 * @author kite-maven-plugin
 */
public class ${className}MapHelper {

    public static Map<String, Serializable> convertToMap(${className} obj) {
		HashMap<String, Serializable> map = new HashMap<String, Serializable>();
		<#list table.columns as column>
		<#if column.simpleType>
		map.put("${column.propertyName}", obj.${column.propertyName});
		</#if>
		</#list>
        return map;
    }

    public static void updateFromMap(${className} obj, Map<String, Serializable> map) {
		<#list table.columns as column>
		<#if column.simpleType>
		<#if utils.isEnum(column.javaType)>
		if (map.containsKey("${column.propertyName}")) obj.${column.propertyName} = DataTypeUtils.getEnumValue(map.get("${column.propertyName}"), ${column.javaType.getShortName()}.class);
		<#else>
		if (map.containsKey("${column.propertyName}")) obj.${column.propertyName} = DataTypeUtils.get${column.javaType.getShortName()}Value(map.get("${column.propertyName}"));
		</#if>
		</#if>
		</#list>
    }
}