package com.l2client.network.game.ClientPackets;


/**
 * 0x39 Request Usage of a Skill
 */
public final class RequestMagicSkillUse extends GameClientPacket {

	/**
	 * Creates an AttackRequest or an Action packet
	 * @param id		skill id
	 * @param shiftClick	shift click (true) or simple click (false), used to prevent movement, if not in range
	 * @param ctrl			ctrl pressed or not, used to force ATTACK on players
	 */
	public RequestMagicSkillUse(int id, boolean shiftClick, boolean ctrl){
		
		writeC(0x39);		
		writeD(id);
		if(ctrl)
			writeD(1);
		else
			writeD(0);
		if(shiftClick)
			writeC(1);
		else
			writeC(0);
	}
}
