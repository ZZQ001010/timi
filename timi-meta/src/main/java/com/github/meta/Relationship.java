package com.github.meta;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库中的关系信息
 * 
 * @author fanghj
 */
public class Relationship {

	private Table parent;

	private Table child;

	private List<JoinColumn> joinColumns = new ArrayList<JoinColumn>();

	public boolean isOne2One() {
		// 判是否是一对一关系
		// 当且仅当关系中的所有外键字段都为child表的主键字段且数目相同时,才为一对一
		for (JoinColumn jc : joinColumns) {
			if (!child.getPrimaryKeyColumns().contains(jc.getFk()))
				return false;
		}
		if (joinColumns.size() != child.getPrimaryKeyColumns().size())
			return false;
		;
		return true;
	}

	public Table getParent() {
		return parent;
	}

	public void setParent(Table parent) {
		this.parent = parent;
	}

	public Table getChild() {
		return child;
	}

	public void setChild(Table child) {
		this.child = child;
	}

	public List<JoinColumn> getJoinColumns() {
		return joinColumns;
	}

	public void setJoinColumns(List<JoinColumn> joinColumns) {
		this.joinColumns = joinColumns;
	}

}
