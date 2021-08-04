package com.github.meta;

import java.util.ArrayList;
import java.util.List;

public class Database {

	private String databaseType;

	private List<Table> tables = new ArrayList<Table>();

	private List<Relationship> relationships = new ArrayList<Relationship>();

	private List<Domain> domains = new ArrayList<Domain>();

	private List<String> sequences = new ArrayList<String>();

	public String getDatabaseType() {
		return databaseType;
	}

	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
	}

	public List<Table> getTables() {
		return tables;
	}

	public void setTables(List<Table> tables) {
		this.tables = tables;
	}

	public List<Relationship> getRelationships() {
		return relationships;
	}

	public void setRelationships(List<Relationship> relationships) {
		this.relationships = relationships;
	}

	public List<Domain> getDomains() {
		return domains;
	}

	public void setDomains(List<Domain> domains) {
		this.domains = domains;
	}

	public List<String> getSequences() {
		return sequences;
	}

	public void setSequences(List<String> sequences) {
		this.sequences = sequences;
	}
	
	@Override
	public String toString() {
		return "Database{" + "databaseType='" + databaseType + '\'' + ", tables=" + tables + ", relationships="
				+ relationships + ", domains=" + domains + ", sequences=" + sequences + '}';
	}
}
