package com.github.meta;

public class JoinColumn {

	private Column pk;

	private Column fk;

	public Column getPk() {
		return pk;
	}

	public void setPk(Column pk) {
		this.pk = pk;
	}

	public Column getFk() {
		return fk;
	}

	public void setFk(Column fk) {
		this.fk = fk;
	}
}
