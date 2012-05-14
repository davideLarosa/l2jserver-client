package com.l2client.model.network;

import com.l2client.app.Assembler;

/**
 * Simple and basic class representing the necessary field for creation of a new character
 * hence all members are public
 */
public class NewCharSummary {

	public int objectId;
	public String name;
	public int race;
	public int sex;
	public int classId;
	public int hair;
	public int hairColor;
	public int templateId;
	public Assembler assembler = null;
}
