package ${packageName};

<#list imports as impt>
import ${impt};
</#list>
import cn.sunline.common.annotation.LogicalName;

<#assign thisName = utils.dbName2PropertyName(table.dbName) />
<#assign className = utils.dbName2ClassName(table.dbName) />
<#assign boName = "b${className}" />
<#assign boClassName = "B${className}" />
/**
 * @author kite-maven-plugin
 * ${table.textName}
 */
@SuppressWarnings("serial")
@Entity
@LogicalName("${table.textName}")
<#if (table.uniques?size < 1)>
<#lt>@Table(name="${table.dbName}")
</#if>
<#if (table.uniques?size >= 1)>
<#lt>@Table(name="${table.dbName}",uniqueConstraints = {<#list table.uniques as unique>@UniqueConstraint(columnNames={<#list unique as column>"${column.dbName}"<#if column_has_next>,</#if></#list>})<#if unique_has_next>,</#if></#list>})
</#if>
<#if (table.primaryKeyColumns?size > 1)>
<#lt>@IdClass(${className}Key.class)
</#if>
public class ${className} implements PrimaryKey<<#if (table.primaryKeyColumns?size > 1)>${className}Key<#else>${table.primaryKeyColumns[0].javaType.getShortName()}</#if>>, Serializable, HasMapping {
	
	public static final String TABLE_NAME = "${table.dbName}";
		
	<#list table.columns as column>
		<#if utils.isPrimaryKey(table,column)>
	@Id
			<#if utils.hasKiteSeqDesc(column)>
	@GeneratedValue(generator = "idGenerator${className}")
	@GenericGenerator(name = "idGenerator${className}", strategy = "cn.sunline.dbs.generator.HbIdIncreGenerator", parameters = {@Parameter(name = "tableName", value = "${table.getDbName()}")})
			<#elseif utils.hasUuidSeqDesc(column)>
	@GeneratedValue(generator = "idGenerator${className}")
	@GenericGenerator(name = "idGenerator${className}", strategy = "cn.sunline.dbs.generator.HbIdUuidGenerator", parameters = {@Parameter(name = "tableName", value = "${table.getDbName()}")})
			<#elseif column.identity>
				<#if utils.isOracle(database)>
	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator="${table.getDbName()}_GEN")
					<#if utils.hasSeqDesc(column) >
	@SequenceGenerator(name="${table.getDbName()}_GEN",sequenceName="${column.getDescription().replace("///@seq:", "")}",allocationSize=1)
					<#else>
	@SequenceGenerator(name="${table.getDbName()}_GEN",sequenceName="${table.getDbName()}_SEQ",allocationSize=1)
					</#if>
				<#else>
	@GeneratedValue(strategy=GenerationType.IDENTITY)
				</#if>
			</#if>
		</#if>
		<#if column.domain ? exists >
	@Enumerated(EnumType.STRING)
		</#if>
		<#if column.temporal ? exists >
	@Temporal(value=TemporalType.${column.temporal})
		</#if>
		<#if column.lob>
	@Lob
			<#if column.javaType.getShortName() == "byte[]" >
	@Basic(fetch=FetchType.LAZY)
			</#if>
		</#if>
	@LogicalName("${column.textName}")
	@Column(name="${column.dbName}", nullable=<#if column.mandatory>false<#else>true</#if><#if utils.notUpdate(column)>, updatable = false</#if><#if column.javaType.getShortName() == "BigDecimal">, precision=${column.length}, scale=${column.scale}<#elseif  (column.javaType.getShortName() == "String") && !(column.domain ? exists)>, length=${column.length}</#if>)
		<#if useAutoTrimType && (column.javaType.getShortName() == "String")>
	@Type(type = "com.sunline.ark.support.base.AutoTrimStringType")
		</#if>
		<#if column.version>
	@Version
		</#if>
		<#assign type = column.javaType.getShortName() />
		<#if column.domain ? exists>
			<#assign type = utils.dbName2ClassName(column.dbName) + "Def" />
			<#if column.domain.type ? exists>
				<#assign type = column.domain.type.getShortName() />
			</#if>
		</#if>
	private ${type} ${utils.dbName2PropertyName(column.dbName)};
		
	</#list>
	<#list table.columns as column>
	<#if entityConstantCase == "classCase">
	public static final String P_${utils.dbName2ClassName(column.dbName)} = "${utils.dbName2PropertyName(column.dbName)}";
	<#else>
	public static final String P_${column.dbName} = "${utils.dbName2PropertyName(column.dbName)}";
	</#if>
	
	</#list>
	public ${className} () {};
	
	public ${className} (${boClassName} ${boName}) {
		this.fillValueFromBO(${boName});
	};
	
	<#if utils.needPrePersist(table) ? exists>
	@PrePersist
	protected void onCreate() {
			<#if utils.hasCreatedDatetime(table) ? exists>
		this.${utils.hasCreatedDatetime(table)} = new Date();
		</#if>
		<#if utils.hasLastModifiedDatetime(table) ? exists>
		this.${utils.hasLastModifiedDatetime(table)} = new Date();
		</#if>
		<#if utils.hasCreateUser(table) ? exists>
		if (this.${utils.hasCreateUser(table)} == null)
			this.${utils.hasCreateUser(table)} = null == cn.sunline.common.KC.threadLocal.getUserId() ? "KITE_SYS" : cn.sunline.common.KC.threadLocal.getUserId();
		</#if>
		<#if utils.hasUpdateUser(table) ? exists>
		if (this.${utils.hasUpdateUser(table)} == null)
			this.${utils.hasUpdateUser(table)} = null == cn.sunline.common.KC.threadLocal.getUserId() ? "KITE_SYS" : cn.sunline.common.KC.threadLocal.getUserId();
		</#if>
		<#if utils.hasJpaversion(table) ? exists>
		this.${utils.hasJpaversion(table)} = 0;
		</#if>
	}
	</#if>
	
	<#if utils.needPreUpdate(table) ? exists>
	@PreUpdate
	protected void onUpdate() {
			<#if utils.hasLastModifiedDatetime(table) ? exists>
		this.${utils.hasLastModifiedDatetime(table)} = new Date();
		</#if>
		<#if utils.hasUpdateUser(table) ? exists>
		if (this.${utils.hasUpdateUser(table)} == null)
			this.${utils.hasUpdateUser(table)} = null == cn.sunline.common.KC.threadLocal.getUserId() ? "KITE_SYS" : cn.sunline.common.KC.threadLocal.getUserId();
		</#if>
	}
	
	</#if>
	<#list table.columns as column>
		<#assign type = column.javaType.getShortName() />
		<#if column.domain ? exists>
			<#assign type = utils.dbName2ClassName(column.dbName) + "Def" />
			<#if column.domain.type ? exists>
				<#assign type = column.domain.type.getShortName() />
			</#if>
		</#if>
	/**
	 * <p>${column.textName}</p>
	 	<#if (column.description ? exists) && (column.description != "") && !(column.domain ? exists)>
	 * <p>${column.description}</p>
	 	</#if>
	 */
	public ${type} get${utils.dbName2ClassName(column.dbName)}() {
		return ${utils.dbName2PropertyName(column.dbName)};
	}
	
	/**
	 * <p>${column.textName}</p>
	 	<#if (column.description ? exists) && (column.description != "") && !(column.domain ? exists)>
	 * <p>${column.description}</p>
	 	</#if>
	 */
	public void set${utils.dbName2ClassName(column.dbName)}(${type} ${utils.dbName2PropertyName(column.dbName)}) {
		this.${utils.dbName2PropertyName(column.dbName)} = ${utils.dbName2PropertyName(column.dbName)};
	}
	
	</#list>
	/**
	 * <p>将当前实体对象转化为map返回</p>
	 */
	public Map<String, Serializable> convertToMap() {
		HashMap<String, Serializable> map = new HashMap<String, Serializable>();
		<#list table.columns as column>
			<#if column.domain ? exists>
		map.put(<#if entityConstantCase == "classCase">P_${utils.dbName2ClassName(column.dbName)}<#else>P_${column.dbName}</#if>, ${utils.dbName2PropertyName(column.dbName)} == null ? null : ${utils.dbName2PropertyName(column.dbName)}.toString());
			<#else>
		map.put(<#if entityConstantCase == "classCase">P_${utils.dbName2ClassName(column.dbName)}<#else>P_${column.dbName}</#if>, ${utils.dbName2PropertyName(column.dbName)});
			</#if>
		</#list>
		return map;
	}

	/**
	 * <p>从map更新当前实体对象</p>
	 */
	public void updateFromMap(Map<String, Serializable> map) {
	<#list table.columns as column>
		<#assign cname = utils.dbName2ClassName(column.dbName) />
		<#assign ptype = column.javaType.getShortName() />
		<#if entityConstantCase == "classCase">
			<#assign pname = utils.dbName2ClassName(column.dbName) />
		<#else>
			<#assign pname = column.dbName />
		</#if>
		<#if column.domain ? exists>
			<#assign ename = utils.dbName2ClassName(column.dbName) + "Def" />
			<#if column.domain.type ? exists>
				<#assign ename = column.domain.type.getShortName() />
			</#if>
		if (map.containsKey(P_${pname})) { this.set${cname}(DataTypeUtils.getEnumValue(map.get(P_${pname}), ${ename}.class)); }
		<#else>
			<#if ptype != "byte[]">
		if (map.containsKey(P_${pname})) { this.set${cname}(DataTypeUtils.get${ptype}Value(map.get(P_${pname}))); }
			</#if>
		</#if>
	</#list>
	}

	/**
	 * <p>为当前实体对象除了主键字段之外的值为空的成员变量赋予默认值，字符串=""，数字类型字段=0</p>
	 */
	public void fillDefaultValues() {
	<#list table.columns as column>
		<#if !column.identity>
			<#assign type = column.javaType.getShortName() />
			<#assign value = "null" />
			<#if type == "String" && !(column.domain ? exists)>
				<#assign value = "\"\"" />
			<#elseif type == "BigDecimal">
				<#assign value = "BigDecimal.ZERO" />
			<#elseif type == "Integer">
				<#assign value = "0" />
			<#elseif type == "Long">
				<#assign value = "0L" />
			<#elseif type == "Date">
				<#assign value = "new Date()" />
			</#if>
		if (${utils.dbName2PropertyName(column.dbName)} == null) { ${utils.dbName2PropertyName(column.dbName)} = ${value}; }
		</#if>
	</#list>
	}
	
	/**
	 * <p>将当前实体对象转换为对应的BO输出</p>
	 */
	public ${boClassName} toBO() {
		${boClassName} ${boName} = new ${boClassName}();
	<#list table.columns as column>
		<#assign cName = utils.dbName2ClassName(column.dbName) />
		${boName}.set${cName}(this.get${cName}());
	</#list>
		return ${boName};
	}
	
	/**
	 * <p>根据BO更新当前实体对象的值</p>
	 */
	public ${className} fillValueFromBO(${boClassName} ${boName}) {
	<#list table.columns as column>
		<#assign cName = utils.dbName2ClassName(column.dbName) />
		this.set${cName}(${boName}.get${cName}());
	</#list>
		return this;
	}
	
	/**
	 * <p>根据BO更新当前实体对象的值</p>
	 * <p>如果BO属性值为空，不修改对应的entity属性值</p>
	 * <p>已排除由hibernate或底层架构自动更新值的属性(主键、jpaversion、创建时间/人、///@seq:、///@kiteseq、///@uuidseq)</p>
	 */
	public ${className} updateValueFromBO(${boClassName} ${boName}) {
	<#list table.columns as column>
		<#if (!column.identity && !column.version && !utils.notUpdate(column) && 
			  !utils.hasSeqDesc(column) && !utils.hasKiteSeqDesc(column) && !utils.hasUuidSeqDesc(column)) >
			<#assign cName = utils.dbName2ClassName(column.dbName) />
		if(${boName}.get${cName}() != null){
			this.set${cName}(${boName}.get${cName}());
		}
		</#if>
	</#list>
		return this;
	}
	
	/**
	 * <p>将实体对象的List转换为BO对象List返回</p>
	 */
	public static List<${boClassName}> convertToBOList(Iterable<${className}> ${thisName}List) {
		if (${thisName}List != null) {
			List<${boClassName}> boList = new ArrayList<${boClassName}>();
			for (${className} ${thisName} : ${thisName}List) {
				boList.add(${thisName}.toBO());
			}
			return boList;
		} else {
			return null;
		}
	}
	
	/**
	 * <p>将BO对象的List转换为实体对象List返回</p>
	 */
	public static List<${className}> convertToEntityList(Iterable<${boClassName}> ${boName}List) {
		if (${boName}List != null) {
			List<${className}> ${thisName}List = new ArrayList<${className}>();
			for (${boClassName} ${boName} : ${boName}List) {
				${thisName}List.add(new ${className}(${boName}));
			}
			return ${thisName}List;
		} else {
			return null;
		}
	}

	<#if (table.primaryKeyColumns?size > 1)>
	public ${className}Key pk() {
		${className}Key key = new ${className}Key();
		<#list table.primaryKeyColumns as pk>
		key.set${utils.dbName2ClassName(pk.dbName)}(${utils.dbName2PropertyName(pk.dbName)});
		</#list>
		return key;
	}
	<#else>
	public ${table.primaryKeyColumns[0].javaType.getShortName()} pk() {
		return ${utils.dbName2PropertyName(table.primaryKeyColumns[0].dbName)};
	}
	</#if>
}