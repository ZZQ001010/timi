/**
 * Project Name:ark-maven-plugin <br/>
 * File Name:EntityKeyGen.java <br/>
 * Package Name:com.sunline.ark.maven.generator <br/>
 * Date:2016年12月27日下午2:37:31 <br/>
 * Copyright (c) 2016, Sunline All Rights Reserved.
 * 
 */
package com.github.timi.generator.entity;

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
 * ClassName：EntityKeyGen <br/>
 * Description：表主键(多主键情况)key对象文件生成类 <br/>
 * Date： 2016年12月27日 下午2:37:31 <br/>
 * 
 * @author yanghm <br/>
 * @update [修改日期:yyyy年MM月dd日 修改内容:? 修改人：?]
 */
public class EntityKeyGen extends AbsClassGenerator {

	private String packageSuffix = ".shared.model";

	private String template = "EntityKey.ftl";

	private Database database;

	private String outputDirectory;

	private String basePackage;

	private boolean trimStrings;

	private Table table;

	public EntityKeyGen(Log log, Database database, String basePackage, String outputDirectory, boolean trimStrings) {
		logger = log;
		this.database = database;
		this.basePackage = basePackage;
		this.outputDirectory = outputDirectory;
		this.trimStrings = trimStrings;
	}

	/*
	 * (non-Javadoc)
	 * @see com.sunline.ark.maven.generator.IGenerator#generateFiles()
	 */
	@Override
	public void generateFiles() {
		for (Table table : database.getTables()) {
			if (table.getPrimaryKeyColumns().size() > 1) {
				this.table = table;
				this.generateFile();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.sunline.ark.maven.generator.IGenerator#initFileName()
	 */
	@Override
	protected String initFileName() {
		return GeneratorUtils.dbName2ClassName(table.getDbName()) + "Key";
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
		data.put("trimStrings", trimStrings);
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
		imports.add("cn.sunline.common.KC");
		imports.add(java.io.Serializable.class.getCanonicalName());
		FreemakerUtils utils = new FreemakerUtils();
		List<Column> pks = table.getPrimaryKeyColumns();
		if (pks != null && pks.size() > 0) {
			for (Column pk : pks) {
				String classType = utils.getRelClassType(pk);
				if (!utils.notNeedImport(classType)) {
					imports.add(classType);
				}
			}
		}
		return imports;
	}
}
