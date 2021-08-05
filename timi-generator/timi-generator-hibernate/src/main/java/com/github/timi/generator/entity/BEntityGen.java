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
 * <p>
 * 生成BO对象
 * </p>
 * @version 1.0 2017年9月29日 linxiaocheng 修改内容:初版
 */ 
public class BEntityGen extends AbsClassGenerator {

	private String packageSuffix = ".model.bo";

	private String template = "BEntity.ftl";

	private Database database;

	private String outputDirectory;

	private String basePackage;

	private Table table;

	public BEntityGen(Log log, Database database, String basePackage, String outputDirectory) {
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
		return "B" + GeneratorUtils.dbName2ClassName(table.getDbName());
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
//		FreemakerUtils utils = new FreemakerUtils();
//		imports.add("cn.sunline.common.shared.DataTypeUtils");
//		imports.add("cn.sunline.common.shared.HasMapping");
		imports.add(java.io.Serializable.class.getCanonicalName());
//		imports.add(java.util.HashMap.class.getCanonicalName());
//		imports.add(java.util.Map.class.getCanonicalName());
		for (Column column : table.getColumns()) {
			String javaType = column.getJavaType().getShortName();
			if (java.math.BigDecimal.class.getSimpleName().equals(javaType)) {
				imports.add(java.math.BigDecimal.class.getCanonicalName());
			} else if (java.util.Date.class.getSimpleName().equals(javaType)) {
				imports.add(java.util.Date.class.getCanonicalName());
			} else if (String.class.getSimpleName().equals(javaType)) {
				if (column.getDomain() != null) {
					imports.add(column.getDomain().getType().getFullyQualifiedName());
				}
			}
		}
		return imports;
	}
}
