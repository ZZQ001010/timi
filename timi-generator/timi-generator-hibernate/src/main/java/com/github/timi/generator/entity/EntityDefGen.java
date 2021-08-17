/**
 * Project Name:ark-maven-plugin <br/>
 * File Name:EntityDefGen.java <br/>
 * Package Name:.ark.maven.generator <br/>
 * Date:2016年12月27日下午2:37:31 <br/>
 * Copyright (c) 2016, Sunline All Rights Reserved.
 * 
 */
package com.github.timi.generator.entity;

import com.github.entity.Configuration;
import com.github.meta.Column;
import com.github.meta.Database;
import com.github.meta.Domain;
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
 * ClassName：EntityDefGen <br/>
 * Description：枚举类文件生成类 <br/>
 * Date： 2016年12月27日 下午2:37:31 <br/>
 * 
 * @author yanghm <br/>
 * @update [修改日期:yyyy年MM月dd日 修改内容:? 修改人：?]
 */
public class EntityDefGen extends AbsClassGenerator {

	private String packageSuffix = ".shared.enums";

	private String template = "EntityDef.ftl";

	private Database database;

	private String outputDirectory;

	private String basePackage;

	private Domain domain;

	public EntityDefGen(Log log, Database database, String basePackage, String outputDirectory) {
		logger = log;
		this.database = database;
		this.basePackage = basePackage;
		this.outputDirectory = outputDirectory;
	}

	/*
	 * (non-Javadoc)
	 * @see .ark.maven.generator.IGenerator#generateFiles()
	 */
	@Override
	public void generateFiles(Configuration config, List<Database> source, Log log) {
		for (Table table : database.getTables()) {
			for (Column column : table.getColumns()) {
				if (column.getDomain() != null && column.getDomain().getIsDef()) {
					this.domain = column.getDomain();
					this.generateFile();
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see .ark.maven.generator.IGenerator#initFileName()
	 */
	@Override
	protected String initFileName() {
		return GeneratorUtils.dbName2ClassName(domain.getCode()) + "Def";
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
		data.put("packageName", this.basePackage + packageSuffix);
		data.put("domain", domain);
		data.put("utils", new FreemakerUtils());
		return data;
	}

	/*
	 * (non-Javadoc)
	 * @see .ark.maven.generator.comm.AbsClassGenerator#initImports()
	 */
	@Override
	protected Set<String> initImports() {
		Set<String> imports = new HashSet<String>();
		return imports;
	}
}
