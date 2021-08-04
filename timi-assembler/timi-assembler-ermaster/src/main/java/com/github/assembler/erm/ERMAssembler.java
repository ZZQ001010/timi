package com.github.assembler.erm;


import com.github.assembler.IAssembler;
import com.github.assembler.utils.DomanUtil;
import com.github.meta.Column;
import com.github.meta.Database;
import com.github.meta.Domain;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.meta.Table;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.ibator.api.dom.java.FullyQualifiedJavaType;
import org.apache.maven.plugin.logging.Log;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 导入ERMaster的定义文件
 * 
 * @author fanghj
 * 
 */
public class ERMAssembler implements IAssembler {
	
		private Log logger;

		public ERMAssembler(Log logger)
		{
			this.logger = logger;
		}
	
	private FullyQualifiedJavaType fqjtInteger = new FullyQualifiedJavaType("java.lang.Integer");
	
	private FullyQualifiedJavaType fqjtLong = new FullyQualifiedJavaType("java.lang.Long");
	
	private FullyQualifiedJavaType fqjtBigDecimal = new FullyQualifiedJavaType("java.math.BigDecimal");
	
	private FullyQualifiedJavaType fqjtDate = new FullyQualifiedJavaType("java.util.Date");
	
	private static final Pattern hintPattern = Pattern.compile("\\[\\[.*\\]\\]");
	
	private String basePackage;
	
	private List<String> errorMessage = new ArrayList<String>();
	
	@SuppressWarnings("unchecked")
	public Database assemble(File ermSource, String tablePattern) throws DocumentException {
		Database result = new Database();
		
		// 解析源文件
		SAXReader sar = new SAXReader();
		Document docSource = sar.read(ermSource);
		
		// 获取数据库类型
		for (Element nodeDB : (List<Element>)docSource.selectNodes("/diagram/settings/database")) {
			result.setDatabaseType(nodeDB.getText());
		}
		
		// 先取所有的word，组建Column
		Map<String, Element> words = new HashMap<String, Element>();
		Map<String, Domain> domains = new HashMap<String, Domain>();
		for (Element nodeWord : (List<Element>)docSource.selectNodes("/diagram/dictionary/word")) {
			String id = nodeWord.elementText("id");
			words.put(id, nodeWord);
			
			// Domain
			Domain domain = parseDomain(nodeWord.elementText("physical_name"), nodeWord.elementText("description"));
			if (domain != null) {
				domains.put(id, domain);
				result.getDomains().add(domain);
			}
		}
		
		// 取出table
		Map<String, Element> tables = new HashMap<String, Element>();
		for (Element nodeTable : (List<Element>)docSource.selectNodes("/diagram/contents/table"))
			tables.put(nodeTable.elementText("id"), nodeTable);
		
		// 开始组建Database对象
		Map<String, Column> allColumns = new HashMap<String, Column>(); // 全局column映射，以id为key
		int i = 0;
		List<String> failTableNames = new ArrayList<String>();
		String[] unalloweColumn = new String[]{"id"};
		for (Element nodeTable : tables.values()) {
			i++;
			Table table = new Table();
			table.setDbName(nodeTable.elementText("physical_name"));
			table.setTextName(nodeTable.elementText("logical_name"));
			System.out.println("[INFO] 构建表" + i + "/" + tables.size() + ": " + table.getDbName());
			//			logger.debug(table.getDbName());
			
			Set<String> columnNames = new HashSet<String>(); // 防重复
			
			// 处理字段
			for (Element nodeColumn : (List<Element>)nodeTable.selectNodes("columns/*")) {
				Column column = new Column();
				//				logger.debug(nodeColumn.getName());
				
				String word_id = nodeColumn.elementText("word_id");
				if (word_id == null) {
					// 没找到的话就找referenced_column
					Element node = nodeColumn;
					
					do {
						String refId = node.elementText("referenced_column");
						if (refId == null)
							throw new IllegalArgumentException();
						node = (Element)docSource.selectSingleNode("//table/columns/*[id='" + refId + "']");
						word_id = node.elementText("word_id");
					} while (StringUtils.isEmpty(word_id));
				}
				
				Element nodeWord = words.get(word_id);
				
				// 以本node的物理名优先
				String physicalName = nodeColumn.elementText("physical_name");
				if (StringUtils.isBlank(physicalName)) {
					physicalName = nodeWord.elementText("physical_name");
					if (ArrayUtils.contains(unalloweColumn, physicalName.toLowerCase())) {
						failTableNames.add(table.getDbName());
					}
				}
				column.setDbName(physicalName);
				column.setIdentity(Boolean.valueOf(nodeColumn.elementText("auto_increment")));
				
				// 逻辑名也一样处理
				String logicalName = nodeColumn.elementText("logical_name");
				if (StringUtils.isBlank(logicalName))
					logicalName = nodeWord.elementText("logical_name");
				
				column.setTextName(logicalName);
				column.setDescription(nodeWord.elementText("description"));
				column.setId(nodeColumn.elementText("id"));
				column.setMandatory(Boolean.parseBoolean(nodeColumn.elementText("not_null")));
				
				// 从description解析hint
				column.setHint(extractHint(column.getDescription()));
				
				// 解析类型
				String type = nodeWord.elementText("type");
				String length = nodeWord.elementText("length");
				String decimal = nodeWord.elementText("decimal");
				if ("char".equals(type)) {
					column.setJavaType(FullyQualifiedJavaType.getStringInstance());
					column.setLength(1);
				} else if ("character(n)".equals(type) || "varchar(n)".equals(type)) {
					column.setJavaType(FullyQualifiedJavaType.getStringInstance());
					column.setLength(Integer.parseInt(length));
				} else if ("decimal".equals(type)) {
					System.out.println(MessageFormat.format("[INFO] decimal没有指定长度，按Integer处理。[{0}], {1}", type,
							column.getDbName()));
					column.setJavaType(fqjtInteger);
					column.setLength(1);
				} else if ("decimal(p)".equals(type) || "numeric(p)".equals(type)) {
					int l = Integer.parseInt(length);
					if (l <= 9)
						column.setJavaType(fqjtInteger);
					else if (l <= 18)
						column.setJavaType(fqjtLong);
					else
						column.setJavaType(fqjtBigDecimal);
					column.setLength(l);
				} else if ("decimal(p,s)".equals(type) || "numeric(p,s)".equals(type)) {
					int l = Integer.parseInt(length);
					int s = Integer.parseInt(decimal);
					if (s == 0 && l <= 18) {
						if (l <= 9) {
							column.setJavaType(fqjtInteger);
						} else {
							column.setJavaType(fqjtLong);
						}
					} else {
						column.setJavaType(fqjtBigDecimal);
					}
					column.setLength(l);
					column.setScale(s);
				} else if ("integer".equals(type)) {
					column.setJavaType(fqjtInteger);
					column.setLength(9);
				} else if ("bigint".equals(type)) {
					//					column.setJavaType(fqjtBigDecimal);	//鉴于smartgwt在处理long型数据时会有些问题，所以Java里使用BigDecimal来处理bigint
					//					column.setLength(20);
					column.setJavaType(fqjtLong);
					column.setLength(18);
				} else if ("date".equals(type)) {
					column.setJavaType(fqjtDate);
					column.setTemporal("DATE");
				} else if ("time".equals(type)) {
					column.setJavaType(fqjtDate);
					column.setTemporal("TIME");
				} else if ("timestamp".equals(type) || "datetime".equals(type)) {
					column.setJavaType(fqjtDate);
					column.setTemporal("TIMESTAMP");
				} else if ("clob".equals(type) || type.endsWith("text")) {
					column.setJavaType(FullyQualifiedJavaType.getStringInstance());
					column.setLob(true);
				} else if (type.endsWith("blob")) {
					column.setJavaType(new FullyQualifiedJavaType("byte[]"));
					column.setLob(true);
				} else {
					System.out.println(MessageFormat.format("[INFO] 无法识别的类型[{0}]，跳过, {1}", type, column.getDbName()));
					continue;
				}
				
				if (type.startsWith("numeric"))
					System.err.println(MessageFormat.format("[WARN] 建议不要使用numeric，用decimal代替[{0}], {1}", type,
							column.getDbName()));
				if (type.startsWith("timestamp"))
					System.err.println(MessageFormat.format("[WARN] 建议不要使用timestamp，用datetime代替[{0}], {1}", type,
							column.getDbName()));
				
				// 处理Version
				column.setVersion("JPA_VERSION".equalsIgnoreCase(column.getDbName())
						|| "JPA_TIMESTAMP".equalsIgnoreCase(column.getDbName()));
				
				// 有unique_key按单列约束处理
				if ("true".equals(nodeColumn.elementText("unique_key"))) {
					List<Column> unique = new ArrayList<Column>();
					unique.add(column);
					table.getUniques().add(unique);
				}
				
				if (columnNames.contains(column.getDbName())) // 字段重复
				{
					System.err.println(MessageFormat.format("[WARN] 字段重复，跳过 {0}", column.getDbName()));
					continue;
				}
				columnNames.add(column.getDbName());
				
				allColumns.put(column.getId(), column);
				table.getColumns().add(column);
				
				if (Boolean.parseBoolean(nodeColumn.elementText("primary_key"))) {
					table.getPrimaryKeyColumns().add(column);
				}
				
				// domain
				if (domains.containsKey(word_id)) {
					// 如果有domain，设置之
					column.setDomain(domains.get(word_id));
					// 临时补丁：在这里设置类型，需要重构得更优雅一点
					//					if (column.getDomain().getJavaType() == null)
					//						column.getDomain().setJavaType(column.getdo.getJavaType());
				}
			}
			if (table.getPrimaryKeyColumns().isEmpty()) // 没有主键就跳过
			{
				System.out.println("[INFO] " + table.getDbName() + " 没有主键，跳过");
				continue;
			}
			
			// 处理索引
			for (Element nodeIndex : (List<Element>)nodeTable.selectNodes("indexes/*")) // 源文件有拼写错误，所以这里用*，希望以后版本会改掉(1.0.0)
			{
				List<Column> index = new ArrayList<Column>();
				for (Element nodeColumn : (List<Element>)nodeIndex.selectNodes("columns/column")) {
					index.add(allColumns.get(nodeColumn.elementText("id")));
				}
				table.getIndexes().add(index);
			}
			// 唯一约束也按索引处理
			for (Element nodeIndex : (List<Element>)nodeTable
					.selectNodes("complex_unique_key_list/complex_unique_key")) {
				List<Column> unique = new ArrayList<Column>();
				for (Element nodeColumn : (List<Element>)nodeIndex.selectNodes("columns/column")) {
					unique.add(allColumns.get(nodeColumn.elementText("id")));
				}
				table.getUniques().add(unique);
			}
			
			result.getTables().add(table);
		}
		if (errorMessage.size() > 0) {
			for (String errorMsg : errorMessage) {
				System.err.println(errorMsg);
			}
			throw new IllegalArgumentException("枚举描述信息不完整，请修复枚举描述信息后再编译");
		}
		if (!failTableNames.isEmpty()) {
			logger.warn(String.format("表%s中存在不建议使用的字段名%s，在将来的版本中可能会直接不允许编译", failTableNames, unalloweColumn));
		}
		// 处理Sequence
		for (Element nodeName : (List<Element>)docSource.selectNodes("/diagram/sequence_set/sequence/name"))
			result.getSequences().add(nodeName.getText());
		
		return result;
	}
	
	private Domain parseDomain(String code, String desc) {
		try {
			Domain domain = null;
			
			BufferedReader br = new BufferedReader(new StringReader(desc));
			String line = br.readLine();
			if (StringUtils.isNotBlank(line)) {
				if (line.startsWith("///@EnumRef:")) {
					// 以@打头为引用现有枚举类
					// 2017-10-26 不需要枚举的enumInfo，注释
					String type = StringUtils.remove(line.trim(), "///@EnumRef:");
					Class<?> enumClass =  ClassUtils.getClass(type);
					if (!enumClass.isEnum()) {
						throw new IllegalArgumentException(String.format("发现使用了///@EnumRef:注解，但类%s不是一个枚举类", type));
					}
					domain = new Domain();
					domain.setCode(code);
					domain.setValueMap(new LinkedHashMap<String, String[]>());
					domain.setType(new FullyQualifiedJavaType(type));
					domain.setCode(domain.getType().getShortName());
					domain.setIsDef(false);
					Enum<?>[] enums = (Enum<?>[])enumClass.getEnumConstants();
					Set<String> enumNameSet = new HashSet<String>();
					for (Enum<?> enumOne : enums) {
						enumNameSet.add(enumOne.name());
					}
					Set<String> descKeySet = new HashSet<String>();
					do {
						line = br.readLine();
						// 跳过空行
						if (StringUtils.isNotBlank(line) && !line.startsWith("--")) {
							String[] keyValue = line.split("\\|");
							if (keyValue.length < 2) {
								errorMessage.add(String.format("字段%s枚举描述格式错误，描述格式应该为：key|描述,每个key+描述占一行", code));
								//								throw new IllegalArgumentException(KC.format.format("字段[{0}]枚举描述格式错误，描述格式应该为：key|描述,每个key+描述占一行", code));
							}
							descKeySet.add(keyValue[0]);
						}
					} while (line != null);
					if (descKeySet.size() == 0) {
						errorMessage.add(String.format("发现字段%s应用了枚举，但没有写枚举描述，描述格式为：key|描述,每个key+描述占一行", code));
					}
					for (Object keyString : descKeySet.toArray()) {
						if (enumNameSet.contains((String)keyString)) {
							enumNameSet.remove((String)keyString);
							descKeySet.remove((String)keyString);
						}
					}
					if (enumNameSet.size() > 0) {
						String message = String.format("字段%s枚举描述不全，还有枚举[", code);
						for (String enumName : enumNameSet) {
							message += enumName + ",";
						}
						message = message.substring(0, message.length() - 1) + "]没有相应的描述";
						errorMessage.add(message);
						//						throw new IllegalArgumentException(message);
					}
					if (descKeySet.size() > 0) {
						String message = String.format("字段%s枚举描述有多余的项，枚举[", code);
						for (String enumName : descKeySet) {
							message += enumName + ",";
						}
						message = message.substring(0, message.length() - 1) + "]的描述在枚举类：" + type + "没有对应的字段";
						errorMessage.add(message);
						//						throw new IllegalArgumentException(message);
					}
				} else if (line.startsWith("///@EnumGen")) {
					domain = new Domain();
					domain.setCode(code);
					domain.setType(new FullyQualifiedJavaType(basePackage + ".shared.enums."
							+ com.github.assembler.utils.StringUtils.dbName2ClassName(domain.getCode()) + "Def"));
					domain.setValueMap(new LinkedHashMap<String, String[]>());
					domain.setIsDef(true);
					do {
						line = br.readLine();
						// 跳过空行
						if (StringUtils.isNotBlank(line)) {
							String kv[] = line.split("\\|");
							if (kv.length < 2) {
								throw new IllegalArgumentException("键值对语法错[" + code + "]:" + line);
							}
							String key = kv[0];
							key = StringUtils.replace(key, ".", "_");
							domain.getValueMap().put(key, DomanUtil.getValues(kv));
						}
					} while (line != null);
				} else if ("///".equals(StringUtils.trim(line))) {
					System.err.println(String.format("[WARN] 发现字段%s的描述中有\"///\"，此种枚举配置方式已废弃，请使用\"///@EnumRef:\"来指定枚举，或者使用\"///@EnumGen\"生成枚举", code));
				}
			}
			return domain;
		} catch (Throwable t) {
			throw new IllegalArgumentException(t);
		}
	}
	
	public String extractHint(String desc) {
		Matcher m = hintPattern.matcher(desc);
		if (!m.find())
			return null;
		
		return desc.substring(m.start() + 2, m.end() - 2);
	}
	
	/**
	 * @return basePackage
	 */
	public String getBasePackage() {
		return basePackage;
	}
	
	/**
	 * @param basePackage
	 */
	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}
}
