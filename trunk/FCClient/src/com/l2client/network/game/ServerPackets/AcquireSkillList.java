package com.l2client.network.game.ServerPackets;

import java.util.ArrayList;

import com.l2client.model.l2j.SkillTemplate;

/**
 * A list of skills
 * 
 */
public class AcquireSkillList extends GameServerPacket
{
	public  void handlePacket()
	{
		log.fine("Read from Server "+this.getClass().getSimpleName());

		int type = readD();//0 usual, 1 fishing, 2 clans, 6 special
		int skills = readD();//# of skill descriptions following
		ArrayList<SkillTemplate> sList = new ArrayList<SkillTemplate>();
		for(int i=0;i<skills;i++){
			SkillTemplate s = new SkillTemplate();
			s.setId(readD());
			s.setNextLevel(readD());
			s.setMaxLevel(readD());
			s.setSpCost(readD());
			s.setRequirements(readD());
			sList.add(s);
			if(type == 3){
				readD();//unknown 0
			}
		}
	}
}
