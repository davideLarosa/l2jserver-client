package com.l2client.network.game.ServerPackets;

import java.util.ArrayList;

import com.l2client.model.l2j.Skill;

/**
 * A list of skills
 * 
 */
public class SkillList extends GameServerPacket
{
	public  void handlePacket()
	{
		log.fine("Read from Server "+this.getClass().getSimpleName());

		int skills = readD();//# of skills following
		ArrayList<Skill> sList = new ArrayList<Skill>();
		for(int i=0;i<skills;i++){
			Skill s = new Skill();
			s.setPassive(readD()!=0);
			s.setLevel(readD());
			s.setId(readD());
			s.setDisabled(readC()!=0);
			s.setEnchanted(readC()!=0);
			sList.add(s);
		}
	}
}
