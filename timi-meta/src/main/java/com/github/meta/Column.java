package com.github.meta;

import org.apache.ibatis.ibator.api.dom.java.FullyQualifiedJavaType;

public class Column {

	// 由importer填写
	private String dbName;

	private String textName;

	private String description;

	private int length;

	private int scale;

	private boolean mandatory;

	private FullyQualifiedJavaType javaType;

	private boolean lob = false;

	private boolean identity = false;

	private boolean version = false;

	private String hint;

	/**
	 * 指定Date型的具体分类，取值为 {@link TemporalType}中的值字符串
	 */
	private String temporal;

	private Domain domain;

	/**
	 * 用于指定当前类型为简单类型，而不是在解析类时出现的数组、Collection等
	 */
	private boolean simpleType = true;

	// 由总控填写
	private String propertyName;

	private String id;

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isLob() {
		return lob;
	}

	public void setLob(boolean lob) {
		this.lob = lob;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getTextName() {
		return textName;
	}

	public void setTextName(String textName) {
		this.textName = textName;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public FullyQualifiedJavaType getJavaType() {
		return javaType;
	}

	public void setJavaType(FullyQualifiedJavaType javaType) {
		this.javaType = javaType;
	}

	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}

	public String getTemporal() {
		return temporal;
	}

	public void setTemporal(String temporal) {
		this.temporal = temporal;
	}

	public boolean isIdentity() {
		return identity;
	}

	public void setIdentity(boolean identity) {
		this.identity = identity;
	}

	public boolean isVersion() {
		return version;
	}

	public void setVersion(boolean version) {
		this.version = version;
	}

	public boolean isSimpleType() {
		return simpleType;
	}

	public void setSimpleType(boolean simpleType) {
		this.simpleType = simpleType;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}
	
	@Override
	public String toString() {
		return "Column{" + "dbName='" + dbName + '\'' + ", textName='" + textName + '\'' + ", description='"
				+ description + '\'' + ", length=" + length + ", scale=" + scale + ", mandatory=" + mandatory
				+ ", javaType=" + javaType + ", lob=" + lob + ", identity=" + identity + ", version=" + version
				+ ", hint='" + hint + '\'' + ", temporal='" + temporal + '\'' + ", domain=" + domain + ", simpleType="
				+ simpleType + ", propertyName='" + propertyName + '\'' + ", id='" + id + '\'' + '}';
	}
}
