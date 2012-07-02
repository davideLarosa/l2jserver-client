package com.l2client.model.l2j;

/**
 * A skill
 * 
 */
public class SkillTemplate {
	int id;
    int nextLevel;
	int maxLevel;
	int spCost;
	int requirements;
	String icon ="";
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getNextLevel() {
		return nextLevel;
	}
	public void setNextLevel(int nextLevel) {
		this.nextLevel = nextLevel;
	}
	public int getMaxLevel() {
		return maxLevel;
	}
	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}
	public int getSpCost() {
		return spCost;
	}
	public void setSpCost(int spCost) {
		this.spCost = spCost;
	}
	public int getRequirements() {
		return requirements;
	}
	public void setRequirements(int requirements) {
		this.requirements = requirements;
	}
}
