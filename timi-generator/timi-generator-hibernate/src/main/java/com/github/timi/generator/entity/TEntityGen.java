/**
 * Project Name:ark-maven-plugin <br/>
 * File Name:TEntityGen.java <br/>
 * Package Name:.ark.maven.generator <br/>
 * Date:2016年12月27日下午2:37:31 <br/>
 * Copyright (c) 2016, Sunline All Rights Reserved.
 * 
 */
package com.github.timi.generator.entity;

import com.github.entity.Configuration;
import com.github.meta.Column;
import com.github.meta.Database;
import com.github.meta.Table;
import com.github.timi.generator.AbsClassGenerator;
import com.github.timi.generator.enums.EntityConstantCase;
import com.github.timi.generator.utils.FreemakerUtils;
import com.github.timi.generator.utils.GeneratorUtils;
import org.apache.maven.plugin.logging.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ClassName：TEntityGen <br/>
 * Description：Q对象文件生成类 <br/>
 * Date： 2016年12月27日 下午2:37:31 <br/>
 * 
 * @author yanghm <br/>
 * @update [修改日期:yyyy年MM月dd日 修改内容:? 修改人：?]
 */
public class TEntityGen extends AbsClassGenerator {

	private String packageSuffix = ".shared.model";

	private String template = "TEntity.ftl";

	private Database database;

	private String outputDirectory;

	private String basePackage;

	private boolean useAutoTrimType;
	
	private EntityConstantCase entityConstantCase;

	private Table table;

	public TEntityGen(Log log, Database database, String basePackage, String outputDirectory, boolean useAutoTrimType, EntityConstantCase entityConstantCase) {
		logger = log;
		this.database = database;
		this.basePackage = basePackage;
		this.outputDirectory = outputDirectory;
		this.useAutoTrimType = useAutoTrimType;
		this.entityConstantCase = null == entityConstantCase ? EntityConstantCase.upperCase : entityConstantCase;
	}

	/*
	 * (non-Javadoc)
	 * @see .ark.maven.generator.IGenerator#generateFiles()
	 */
	@Override
	public void generateFiles(Configuration config, List<Database> source, Log log) {
		for (Table table : database.getTables()) {
			this.table = table;
			this.generateFile();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see .ark.maven.generator.IGenerator#initFileName()
	 */
	@Override
	protected String initFileName() {
		return GeneratorUtils.dbName2ClassName(table.getDbName());
	}

	/*
	 * (non-Javadoc)
	 * @see .ark.maven.generator.IGenerator#initFilePath()
	 */
	@Override
	protected String initFilePath() {
		String packageName = this.basePackage + packageSuffix;
		packageName = outputDirectory + "/" + packageName.replace(".", "/") + "/";
		return packageName;
	}

	/*
	 * (non-Javadoc)
	 * @see .ark.maven.generator.IGenerator#initTemplate()
	 */
	@Override
	protected String initTemplate() {
		return template;
	}

	/*
	 * (non-Javadoc)
	 * @see .ark.maven.generator.IGenerator#initData()
	 */
	@Override
	protected Map<String, Object> initData() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("basePackage", this.basePackage);
		data.put("packageName", this.basePackage + packageSuffix);
		data.put("table", table);
		data.put("database", database);
		data.put("useAutoTrimType", useAutoTrimType);
		data.put("utils", new FreemakerUtils());
		data.put("entityConstantCase", this.entityConstantCase);
		return data;
	}

	/*
	 * (non-Javadoc)
	 * @see .ark.maven.generator.comm.AbsClassGenerator#initImports()
	 */
	@Override
	protected Set<String> initImports() {
		Set<String> imports = new HashSet<String>();
		FreemakerUtils utils = new FreemakerUtils();
		imports.add(javax.persistence.Column.class.getCanonicalName());
		imports.add(javax.persistence.Entity.class.getCanonicalName());
		imports.add("cn.sunline.common.shared.DataTypeUtils");
		imports.add("cn.sunline.common.shared.HasMapping");
		imports.add("cn.sunline.dbs.shared.PrimaryKey");
		imports.add(basePackage + ".model.bo.B" + utils.dbName2ClassName(table.getDbName()));
		imports.add(java.io.Serializable.class.getCanonicalName());
		imports.add(HashMap.class.getCanonicalName());
		imports.add(Map.class.getCanonicalName());
		imports.add(java.util.ArrayList.class.getCanonicalName());
		imports.add(java.util.List.class.getCanonicalName());
		imports.add(javax.persistence.Table.class.getCanonicalName());
		if (table.getUniques()!=null&&table.getUniques().size()>0) {
			imports.add(javax.persistence.UniqueConstraint.class.getCanonicalName());
		}
		for (Column column : table.getColumns()) {
			String javaType = column.getJavaType().getShortName();
			if (java.math.BigDecimal.class.getSimpleName().equals(javaType)) {
				imports.add(java.math.BigDecimal.class.getCanonicalName());
			} else if (java.util.Date.class.getSimpleName().equals(javaType)) {
				imports.add(java.util.Date.class.getCanonicalName());
				if (column.getTemporal() != null) {
					imports.add(javax.persistence.Temporal.class.getCanonicalName());
					imports.add(javax.persistence.TemporalType.class.getCanonicalName());
				}
			} else if (String.class.getSimpleName().equals(javaType)) {
				if (column.getDomain() != null) {
					imports.add(javax.persistence.EnumType.class.getCanonicalName());
					imports.add(javax.persistence.Enumerated.class.getCanonicalName());
					imports.add(column.getDomain().getType().getFullyQualifiedName());
				}
				if (useAutoTrimType) {
					imports.add(org.hibernate.annotations.Type.class.getCanonicalName());
				}
			} else if ("byte[]".equals(javaType)) {
				imports.add(javax.persistence.Basic.class.getCanonicalName());
				imports.add(javax.persistence.FetchType.class.getCanonicalName());
			}

			if (utils.isPrimaryKey(table, column)) {
				imports.add(javax.persistence.Id.class.getCanonicalName());
//				if (table.getPrimaryKeyColumns().size() == 1) {
					// 使用本系统的id生成方式
					if (column.getDescription() != null
							&& column.getDescription().toLowerCase().startsWith("///@kiteseq")) {
						// 使用kite_sequence表生成id
						imports.add(javax.persistence.GeneratedValue.class.getCanonicalName());
						imports.add(org.hibernate.annotations.GenericGenerator.class.getCanonicalName());
						imports.add(org.hibernate.annotations.Parameter.class.getCanonicalName());
					} else if (column.getDescription() != null
							&& column.getDescription().toLowerCase().startsWith("///@uuidseq")) {
						imports.add(javax.persistence.GeneratedValue.class.getCanonicalName());
						imports.add(org.hibernate.annotations.GenericGenerator.class.getCanonicalName());
						imports.add(org.hibernate.annotations.Parameter.class.getCanonicalName());
					} else if (column.isIdentity()) {// 设置了自增类型
						if ("oracle".equalsIgnoreCase(database.getDatabaseType())) {
							// ORACLE
							imports.add(javax.persistence.GeneratedValue.class.getCanonicalName());
							imports.add(javax.persistence.GenerationType.class.getCanonicalName());
							imports.add(javax.persistence.SequenceGenerator.class.getCanonicalName());
						} else {
							// MYSQL DB2
							imports.add(javax.persistence.GeneratedValue.class.getCanonicalName());
							imports.add(javax.persistence.GenerationType.class.getCanonicalName());
						}
					}
//				}
			}
			if (column.isLob()) {
				imports.add(javax.persistence.Lob.class.getCanonicalName());
			}
			if (column.isVersion()) {
				imports.add(javax.persistence.Version.class.getCanonicalName());
			}
		}
		if (utils.needPrePersist(table) != null) {
			imports.add(javax.persistence.PrePersist.class.getCanonicalName());
//            imports.add(javax.persistence.PreUpdate.class.getCanonicalName());
		}
		if (utils.needPreUpdate(table) != null) {
			imports.add(javax.persistence.PreUpdate.class.getCanonicalName());
		}
		if (table.getPrimaryKeyColumns().size() > 1) {
			imports.add(javax.persistence.IdClass.class.getCanonicalName());
		}

		return imports;
	}
}
