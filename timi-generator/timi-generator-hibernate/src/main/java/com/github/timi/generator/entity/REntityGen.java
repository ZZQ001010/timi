/**
 * Project Name:ark-maven-plugin <br/>
 * File Name:REntityGen.java <br/>
 * Package Name:com.sunline.ark.maven.generator <br/>
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
import com.github.timi.generator.utils.FreemakerUtils;
import com.github.timi.generator.utils.GeneratorUtils;
import org.apache.maven.plugin.logging.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ClassName：REntityGen <br/>
 * Description：R对象文件生成类 <br/>
 * Date： 2016年12月27日 下午2:37:31 <br/>
 * 
 * @author yanghm <br/>
 * @update [修改日期:yyyy年MM月dd日 修改内容:? 修改人：?]
 */
public class REntityGen extends AbsClassGenerator {

	private String packageSuffix = ".server.repos";

	private String template = "REntity.ftl";

	private Database database;

	private String outputDirectory;

	private String basePackage;

	private Table table;

	public REntityGen(Log log, Database database, String basePackage, String outputDirectory) {
		logger = log;
		this.database = database;
		this.basePackage = basePackage;
		this.outputDirectory = outputDirectory;
	}

	/*
	 * (non-Javadoc)
	 * @see com.sunline.ark.maven.generator.IGenerator#generateFiles()
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
	 * @see com.sunline.ark.maven.generator.IGenerator#initFileName()
	 */
	@Override
	protected String initFileName() {
		return "R" + GeneratorUtils.dbName2ClassName(table.getDbName());
	}

	/*
	 * (non-Javadoc)
	 * @see com.sunline.ark.maven.generator.IGenerator#initFilePath()
	 */
	@Override
	protected String initFilePath() {
		String packageName = this.basePackage + packageSuffix;
		packageName = outputDirectory + "/" + packageName.replace(".", "/") + "/";
		return packageName;
	}

	/*
	 * (non-Javadoc)
	 * @see com.sunline.ark.maven.generator.IGenerator#initTemplate()
	 */
	@Override
	protected String initTemplate() {
		return template;
	}

	/*
	 * (non-Javadoc)
	 * @see com.sunline.ark.maven.generator.IGenerator#initData()
	 */
	@Override
	protected Map<String, Object> initData() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("basePackage", this.basePackage);
		data.put("packageName", this.basePackage + packageSuffix);
		data.put("table", table);
		data.put("database", database);
		data.put("utils", new FreemakerUtils());
		return data;
	}

	/*
	 * (non-Javadoc)
	 * @see com.sunline.ark.maven.generator.comm.AbsClassGenerator#initImports()
	 */
	@Override
	protected Set<String> initImports() {
		Set<String> imports = new HashSet<String>();
		imports.add("org.springframework.data.jpa.repository.JpaRepository");
		imports.add("org.springframework.data.querydsl.QuerydslPredicateExecutor");

		FreemakerUtils utils = new FreemakerUtils();
		String className = utils.dbName2ClassName(table.getDbName());
		imports.add(basePackage + ".shared.model." + className);

		String pkClass = table.getPrimaryKeyColumns().get(0).getJavaType().getFullyQualifiedName();
		if (table.getPrimaryKeyColumns().size() > 1) {
			pkClass = basePackage + ".shared.model." + className + "Key";
		}
		if (!utils.notNeedImport(pkClass)) {
			imports.add(pkClass);
		}
		// 添加唯一索引findBy
		for (List<Column> uniques : table.getUniques()) {
			for (Column unique : uniques) {
				String uqClass = utils.getRelClassType(unique);
				if (!utils.notNeedImport(uqClass)) {
					imports.add(uqClass);
				}
			}
		}
		// 索引findBy
		if (table.getIndexes() != null && table.getIndexes().size() > 0) {
			imports.add(List.class.getCanonicalName());
			for (List<Column> indexes : table.getIndexes()) {
				for (Column index : indexes) {
					String classType = utils.getRelClassType(index);
					if (!utils.notNeedImport(classType)) {
						imports.add(classType);
					}
				}
			}
		}
		return imports;
	}
}
