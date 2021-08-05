package ${packageName};

<#list imports as impt>
import ${impt};
</#list>

/**
 * @author kite-maven-plugin
 */
@Singleton
public class U${utils.dbName2ClassName(table.dbName)}<#if isClass> implements HasRandomAlias</#if> {
    @Inject
    private ${utils.dbName2ClassName(table.dbName)}Constants constants;	
	<#list table.columns as column>
	<#if column.domain ? exists>
	<#if utils.notImport(utils.dbName2ClassName(column.domain.code))>
	
	@Inject
    private ${utils.dbName2ClassName(column.domain.code)}DomainClient ${utils.dbName2PropertyName(column.domain.code)}DomainClient;
    </#if>
	</#if>
	</#list>
	<#list table.columns as column>
	<#assign type=column.javaType.getShortName()>
	<#if type=="BigDecimal">
	
	public final DecimalColumnHelper ${utils.dbName2ClassName(column.dbName)}() {
        return new DecimalColumnHelper("${utils.dbName2PropertyName(column.dbName)}", constants.${utils.dbName2PropertyName(column.dbName)}(), ${utils.processLength(column)}, BigDecimal.ZERO, BigDecimal.valueOf(${utils.length2MaxValue(column.length,column.scale)}), ${column.scale})<#if column.hint ? exists>.hint(constants.${utils.dbName2PropertyName(column.dbName)}Hint())</#if>;
    }
	<#elseif type=="Boolean">
	
	public final BooleanColumnHelper ${utils.dbName2ClassName(column.dbName)}() {
		return new BooleanColumnHelper("${utils.dbName2PropertyName(column.dbName)}", constants.${utils.dbName2PropertyName(column.dbName)}())<#if column.hint ? exists>.hint(constants.${utils.dbName2PropertyName(column.dbName)}Hint())</#if>;
    }
	<#elseif type=="Date">
	
	public final DateColumnHelper ${utils.dbName2ClassName(column.dbName)}() {
    	return new DateColumnHelper("${utils.dbName2PropertyName(column.dbName)}", constants.${utils.dbName2PropertyName(column.dbName)}(), true, <#if column.temporal=="TIMESTAMP">true<#else>false</#if>)<#if column.hint ? exists>.hint(constants.${utils.dbName2PropertyName(column.dbName)}Hint())</#if>;
    }
	<#elseif type=="Integer">
	
	public final IntegerColumnHelper ${utils.dbName2ClassName(column.dbName)}() {
        return new IntegerColumnHelper("${utils.dbName2PropertyName(column.dbName)}", constants.${utils.dbName2PropertyName(column.dbName)}(), ${utils.processLength(column)}, 0, ${utils.length2MaxValue(column.length)})<#if column.hint ? exists>.hint(constants.${utils.dbName2PropertyName(column.dbName)}Hint())</#if>;
    }
    <#elseif type=="Long">
    
	public final DecimalColumnHelper ${utils.dbName2ClassName(column.dbName)}() {
        return new DecimalColumnHelper("${utils.dbName2PropertyName(column.dbName)}", constants.${utils.dbName2PropertyName(column.dbName)}(), ${utils.processLength(column)}, BigDecimal.ZERO, BigDecimal.valueOf(${utils.length2MaxValue(column.length)}L), ${column.scale})<#if column.hint ? exists>.hint(constants.${utils.dbName2PropertyName(column.dbName)}Hint())</#if>;
    }
	<#else>
	<#if column.domain ? exists >
	<#if column.domain.type ? exists >
	
	public final EnumColumnHelper<${column.domain.type.getShortName()}> ${utils.dbName2ClassName(column.dbName)}() {
	    return new EnumColumnHelper<${column.domain.type.getShortName()}>("${utils.dbName2PropertyName(column.dbName)}", constants.${utils.dbName2PropertyName(column.dbName)}(), ${column.domain.type.getShortName()}.class)<#if column.hint ? exists>.hint(constants.${utils.dbName2PropertyName(column.dbName)}Hint())</#if>{
	    	public DomainClientSupport<String> getDomain(){
				return ${column.domain.type.getShortName() ? uncap_first}DomainClient;
			}
	    };
    }
	<#else>
	
	public final EnumColumnHelper<${utils.dbName2ClassName(column.domain.code)}Def> ${utils.dbName2ClassName(column.dbName)}() {
	    return new EnumColumnHelper<${utils.dbName2ClassName(column.domain.code)}Def>("${utils.dbName2PropertyName(column.dbName)}", constants.${utils.dbName2PropertyName(column.dbName)}(), ${utils.dbName2ClassName(column.domain.code)}Def.class)<#if column.hint ? exists>.hint(constants.${utils.dbName2PropertyName(column.dbName)}Hint())</#if>{
		    public DomainClientSupport<String> getDomain(){
				return ${utils.dbName2PropertyName(column.domain.code)}DomainClient;
			}
	    };
	}
	</#if>
	<#else>
	
	public final TextColumnHelper ${utils.dbName2ClassName(column.dbName)}() {
    	return new TextColumnHelper("${utils.dbName2PropertyName(column.dbName)}", constants.${utils.dbName2PropertyName(column.dbName)}(), ${utils.processLength(column)})<#if column.hint ? exists>.hint(constants.${utils.dbName2PropertyName(column.dbName)}Hint())</#if>;
    }
	</#if>
	</#if>
	</#list>

	<#if isClass>
    public int getAlias() {
        return ${utils.getRandom()};
    }
    </#if>

    public Map buildValueMap(MapData data) {
        Map map = new HashMap();
        <#list table.columns as column>
        <#assign ptype = column.javaType.getShortName() />
        <#if ptype!="Integer" && ptype!="Double">
        <#assign ptype = "String" />
        </#if>
        map.put("${utils.dbName2PropertyName(column.dbName)}", data.get${ptype}("${utils.dbName2PropertyName(column.dbName)}"));
        </#list>
        return map;
    }
}