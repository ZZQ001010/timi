package ${packageName};

<#list imports as impt>
import ${impt};
</#list>

/**
 * @author kite-maven-plugin
 */
@Singleton
public class ${utils.dbName2ClassName(domain.code)}DomainClient extends DomainClientSupport<String> {
    @Inject
    private ${utils.dbName2ClassName(domain.code)}Constants constants;

    protected void fill(Map<String, String> map, boolean valueWithinLabel) {
    	<#if domain.valueMap ? exists>
		<#list domain.valueMap ? keys as key> 
		map.put("${key}", (valueWithinLabel ? "${key}" + " - ": "") + constants.${key}());
		</#list>
		</#if>
    }
}