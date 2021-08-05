package ${packageName};
<#assign className=utils.dbName2ClassName(table.dbName) />
<#assign propertyName=utils.dbName2PropertyName(table.dbName) />

<#list imports as impt>
import ${impt};
</#list>

/**
 * @author kite-maven-plugin
 * Q${className} is a Querydsl query type for ${className}
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class Q${className} extends EntityPathBase<${className}> {

    private static final long serialVersionUID = ${utils.getRandom()};

    public static final Q${className} ${propertyName} = new Q${className}("${propertyName}");

	<#list table.columns as column>
	<#assign name=utils.dbName2PropertyName(column.dbName) />
	<#assign columnType=column.javaType.getShortName() />
	<#if columnType=="BigDecimal">
	public final NumberPath<java.math.BigDecimal> ${name} = createNumber("${name}", java.math.BigDecimal.class);
	
	<#elseif columnType=="Boolean">
	public final BooleanPath ${name} = createBoolean("${name}");
	
	<#elseif columnType=="Date" >
	<#if column.temporal=="TIMESTAMP">
	public final DateTimePath<java.util.Date> ${name} = createDateTime("${name}", java.util.Date.class);
	
	<#else>
	public final DatePath<java.util.Date> ${name} = createDate("${name}", java.util.Date.class);
	
	</#if>
	<#elseif columnType=="Integer">
	public final NumberPath<Integer> ${name} = createNumber("${name}", Integer.class);
	
	<#elseif columnType=="String">
	<#if column.domain ? exists >
	<#if column.domain.type ? exists >
	public final EnumPath<${column.domain.type.getFullyQualifiedName()}> ${name} = createEnum("${name}", ${column.domain.type.getFullyQualifiedName()}.class);
	
	<#else>
	public final EnumPath<${basePackage}.shared.enums.${utils.dbName2ClassName(column.domain.code)}Def> ${name} = createEnum("${name}", ${basePackage}.shared.enums.${utils.dbName2ClassName(column.domain.code)}Def.class);
	
	</#if>
	<#else>
	public final StringPath ${name} = createString("${name}");
	
	</#if>
	</#if>
	</#list>

    public Q${className}(String variable) {
        super(${className}.class, forVariable(variable));
    }

    @SuppressWarnings("all")
    public Q${className}(Path<? extends ${className}> path) {
        super((Class)path.getType(), path.getMetadata());
    }

    public Q${className}(PathMetadata metadata) {
        super(${className}.class, metadata);
    }

}

