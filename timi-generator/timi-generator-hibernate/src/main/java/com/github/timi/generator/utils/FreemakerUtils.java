package com.github.timi.generator.utils;

import com.github.constants.Constants;
import com.github.meta.Column;
import com.github.meta.Database;
import com.github.meta.Table;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.ibatis.ibator.api.dom.java.FullyQualifiedJavaType;

import java.util.Date;
import java.util.HashSet;
import java.util.Random;

/**
 */
public class FreemakerUtils {

	private Random rand;

	/**
	 * 用于import去重
	 */
	private HashSet<String> imports;

	public FreemakerUtils() {
		rand = new Random();
		imports = new HashSet<String>();
	}

	public String dbName2ClassName(String dbName) {
		String s = dbName.trim();
		boolean allUpperCaseOrNumeric = true;
		for (char c : s.toCharArray()) {
			if (c != '_' && !CharUtils.isAsciiNumeric(c) && !CharUtils.isAsciiAlphaUpper(c)) {
				allUpperCaseOrNumeric = false;
				break;
			}
		}
		if (allUpperCaseOrNumeric) {
			// 为应对Java类定义的情况，只有在全大写时才需要定义
			s = s.toLowerCase();
			s = WordUtils.capitalizeFully(s, new char[]{'_'});
			s = StringUtils.remove(s, "_");
		}
		if (!StringUtils.isAlpha(StringUtils.left(s, 1))) // 避免首个不是字母的情况
			s = "_" + s;
		return firstCharUpper(s);
	}

	public String dbName2PropertyName(String dbName) {
		return WordUtils.uncapitalize(dbName2ClassName(dbName));
	}

	public String length2MaxValue(int length) {
		return StringUtils.repeat("9", length);
	}

	public String length2MaxValue(int length, int scale) {
		return StringUtils.repeat("9", length - scale) + "." + StringUtils.repeat("9", scale);
	}

	public String processLength(Column column) {
		if (column.getLength() > 0) {
			if ("BigDecimal".equals(column.getJavaType().getShortName()) && column.getScale() != 0) {
				return (column.getLength() + 1) + "";
			}
			return column.getLength() + "";
		} else {
			return "null";
		}
	}

	public String getRandom() {
		int n;
		do {
			n = rand.nextInt();
		} while (Constants.CONST.numbers.contains(n));
		Constants.CONST.numbers.add(n);
		return n + "";
	}

	public boolean notImport(String impt) {
		if (imports.contains(impt)) {
			return false;
		} else {
			imports.add(impt);
		}
		return true;
	}

	public boolean notNeedImport(String impt) {
		if (impt.contains("java.lang")) {
			return true;
		}
		return false;
	}

	public boolean isPrimaryKey(Table table, Column col) {
		return table.getPrimaryKeyColumns().contains(col);
	}

	public String hasCreateUser(Table table) {
		String createUser = null;
		for (Column col : table.getColumns()) {
			if ("CREATE_USER".equals(col.getDbName()) || (col.getDescription() != null
					&& "///@createUser".equalsIgnoreCase(col.getDescription().trim()))) {
				createUser = this.dbName2PropertyName(col.getDbName());
				break;
			}
		}
		return createUser;
	}

	public String hasCreatedDatetime(Table table) {
		String createdDatetime = null;
		for (Column col : table.getColumns()) {
			// 非日期类型不生成
			if (!isDateColumn(col)) {
				continue;
			}
			if ("CREATE_TIME".equals(col.getDbName())
					|| (col.getDescription() != null && "///@create".equalsIgnoreCase(col.getDescription().trim()))) {
				createdDatetime = this.dbName2PropertyName(col.getDbName());
				break;
			}
		}
		return createdDatetime;
	}

	public String hasJpaversion(Table table) {
		String jpaversion = null;
		for (Column col : table.getColumns()) {
			if ("JPA_VERSION".equals(col.getDbName())) {
				jpaversion = this.dbName2PropertyName(col.getDbName());
				break;
			}
		}
		return jpaversion;
	}

	public String hasLastModifiedDatetime(Table table) {
		String updateDatetime = null;
		for (Column col : table.getColumns()) {
			// 非日期类型不生成
			if (!isDateColumn(col)) {
				continue;
			}
			if ("UPDATE_TIME".equals(col.getDbName())
					|| (col.getDescription() != null && "///@update".equalsIgnoreCase(col.getDescription().trim()))) {
				updateDatetime = this.dbName2PropertyName(col.getDbName());
				break;
			}
			if ("LST_UPD_TIME".equals(col.getDbName())
					|| (col.getDescription() != null && "///@update".equalsIgnoreCase(col.getDescription().trim()))) {
				updateDatetime = this.dbName2PropertyName(col.getDbName());
				break;
			}
		}
		return updateDatetime;
	}

	public String hasUpdateUser(Table table) {
		String updateUser = null;
		for (Column col : table.getColumns()) {
			if ("LST_UPD_USER".equals(col.getDbName()) || "UPDATE_USER".equals(col.getDbName())
					|| (col.getDescription() != null
							&& "///@updateUser".equalsIgnoreCase(col.getDescription().trim()))) {
				updateUser = this.dbName2PropertyName(col.getDbName());
				break;
			}
		}
		return updateUser;
	}

	public String needPrePersist(Table table) {
		String needPreCreate = null;
		if (this.hasCreatedDatetime(table) != null || this.hasLastModifiedDatetime(table) != null
				|| this.hasCreateUser(table) != null || this.hasUpdateUser(table) != null
				|| this.hasJpaversion(table) != null) {
			needPreCreate = "true";
		}
		return needPreCreate;
	}

	public String needPreUpdate(Table table) {
		String needPreUpdate = null;
		if (this.hasLastModifiedDatetime(table) != null || this.hasUpdateUser(table) != null) {
			needPreUpdate = "true";
		}
		return needPreUpdate;
	}

	public boolean isEnum(FullyQualifiedJavaType javaType) {
		Class<?> fieldType = null;
		try {
			fieldType = ClassUtils.getClass(javaType.getFullyQualifiedName());
		} catch (ClassNotFoundException e) {
		}
		return fieldType.isEnum();
	}

	public boolean isOracle(Database database) {
		return "oracle".equalsIgnoreCase(database.getDatabaseType());
	}

	public boolean hasSeqDesc(Column column) {
		return column.getDescription() != null && column.getDescription().toLowerCase().startsWith("///@seq:");
	}

	public boolean hasKiteSeqDesc(Column column) {
		return column.getDescription() != null && column.getDescription().toLowerCase().startsWith("///@kiteseq");
	}

	public boolean hasUuidSeqDesc(Column column) {
		return column.getDescription() != null && column.getDescription().toLowerCase().startsWith("///@uuidseq");
	}

	// create_time、create_user更新时增加不更新
	public boolean notUpdate(Column column) {
		boolean bol = false;
		if ("CREATE_TIME".equals(column.getDbName())
				|| (column.getDescription() != null && "///@create".equalsIgnoreCase(column.getDescription().trim()))) {
			bol = true;
		} else if ("CREATE_USER".equals(column.getDbName()) || (column.getDescription() != null
				&& "///@createUser".equalsIgnoreCase(column.getDescription().trim()))) {
			bol = true;
		}
		return bol;
	}

	/**
	 * 首字母如果为小写则转大写
	 */
	public String firstCharUpper(String str) {
		char[] cs = str.toCharArray();
		if (cs[0] >= 'a' && cs[0] <= 'z') {
			cs[0] -= 32;
		}
		return String.valueOf(cs);
	}

	private boolean isDateColumn(Column col) {
		if (col.getJavaType().getFullyQualifiedName().equals(Date.class.getCanonicalName())) {
			return true;
		} else {
			return false;
		}
	}

	public String getRelClassType(Column col) {
		String classType = col.getJavaType().getFullyQualifiedName();
		if (String.class.getCanonicalName().equals(classType)) {
			if (col.getDomain() != null) {
				if (col.getDomain().getType() != null) {
					classType = col.getDomain().getType().getFullyQualifiedName();
				}
			}
		}
		return classType;
	}
}