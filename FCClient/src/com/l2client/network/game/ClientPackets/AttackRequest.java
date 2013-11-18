package com.l2client.network.game.ClientPackets;

import com.l2client.model.l2j.ServerValues;

/**
 * 0x01 Attack a target, 0x32 too, 0x1f just Action but same setup
 */
public final class AttackRequest extends GameClientPacket {

	/**
	 * Creates an AttackRequest or an Action packet
	 * @param targetID		Object ID of the target
	 * @param posX			original position x (of ?)
	 * @param posY			original position y (of ?)
	 * @param posZ			original position z (of ?)
	 * @param shiftClick	shift click (true) or simple click (false)
	 * @param noAttack		on true creates an Action packet (0x1f), on false an AttackRequest packet (0x01)
	 */
	public AttackRequest(int targetID, float posX, float posY, float posZ, boolean shiftClick, boolean noAttack){
		if(noAttack)
			writeC(0x1f);
		else
			writeC(0x01);
		
		writeD(targetID);
		writeD(ServerValues.getServerCoordX(posX));//Switch z and y
		writeD(ServerValues.getServerCoordY(posZ));
		writeD(ServerValues.getServerCoordZ(posY));
		
		if(shiftClick)
			writeC(1);
		else
			writeC(0);
	}
}
