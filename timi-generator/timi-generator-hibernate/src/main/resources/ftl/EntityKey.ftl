package ${packageName};

<#list imports as impt>
import ${impt};
</#list>

/**
 * @author kite-maven-plugin
 */
public class ${utils.dbName2ClassName(table.dbName)}Key implements Serializable {
	<#assign arguments="" />
	<#assign toString="" />
	<#list table.primaryKeyColumns as pk>
	<#assign type = pk.javaType.getShortName() />
	<#if pk.domain ? exists>
	<#if pk.domain.type ? exists>
	<#assign type = pk.domain.type.getShortName() />
	</#if>
	</#if>
	<#assign arguments=arguments + type + " " + utils.dbName2PropertyName(pk.dbName) />
	<#assign toString=toString + "String.valueOf(${utils.dbName2PropertyName(pk.dbName)})"/>
	private ${type} ${utils.dbName2PropertyName(pk.dbName)};
	
	<#if pk_has_next>
	<#assign arguments= arguments + ", " />
	<#assign toString=toString + " + \"|\" + "/>
	</#if>
	</#list>
	public ${utils.dbName2ClassName(table.dbName)}Key() {
		
	}

  	public ${utils.dbName2ClassName(table.dbName)}Key(${arguments}) {
	  	<#list table.primaryKeyColumns as pk>
	  	this.${utils.dbName2PropertyName(pk.dbName)} = ${utils.dbName2PropertyName(pk.dbName)};
	  	</#list>
  	}
  
	<#list table.primaryKeyColumns as pk>
	<#assign type = pk.javaType.getShortName() />
	<#if pk.domain ? exists>
	<#if pk.domain.type ? exists>
	<#assign type = pk.domain.type.getShortName() />
	</#if>
	</#if>
	/**
	 * <p>${pk.textName}</p>
	 */
	public ${type} get${utils.dbName2ClassName(pk.dbName)}() {
		return ${utils.dbName2PropertyName(pk.dbName)};
	}
	
	/**
	 * <p>${pk.textName}</p>
	 */
	public void set${utils.dbName2ClassName(pk.dbName)}(${type} ${utils.dbName2PropertyName(pk.dbName)}) {
		<#if trimStrings>
		this.${utils.dbName2PropertyName(pk.dbName)} = ${utils.dbName2PropertyName(pk.dbName)} == null ? null : ${utils.dbName2PropertyName(pk.dbName)}.trim();
		<#else>
		this.${utils.dbName2PropertyName(pk.dbName)} = ${utils.dbName2PropertyName(pk.dbName)};
		</#if>
	}
	
	</#list>
	
	public String toString() {
	    return ${toString};
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		if (obj == this) { return true; }
		if (obj.getClass() != getClass()) {return false;}
		${utils.dbName2ClassName(table.dbName)}Key rhs = (${utils.dbName2ClassName(table.dbName)}Key) obj;
		return KC.equalsBuilder()
		  	<#list table.primaryKeyColumns as pk>
		  	.append(${utils.dbName2PropertyName(pk.dbName)}, rhs.${utils.dbName2PropertyName(pk.dbName)})
		  	</#list>
		  	.isEquals();
	}
	
	@Override
	public int hashCode() {
		return KC.hashCodeBuilder()
			<#list table.primaryKeyColumns as pk>
			.append(${utils.dbName2PropertyName(pk.dbName)})
			</#list>
			.toHashCode();
	}
}