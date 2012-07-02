package com.l2client.model.l2j;

public class Skill {

	private boolean passive;
	private boolean enchanted;
	private boolean disabled;
	private int id;
	private int level;

	public void setPassive(boolean b) {
		this.passive = b;
	}

	public void setLevel(int l) {
		this.level = l;
	}

	public void setId(int i) {
		this.id = i;
	}

	public void setDisabled(boolean b) {
		this.disabled = b;
	}

	public void setEnchanted(boolean b) {
		this.enchanted = b;
	}

}
