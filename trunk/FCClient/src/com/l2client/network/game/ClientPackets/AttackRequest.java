package com.l2client.network.game.ClientPackets;

import com.l2client.model.l2j.ServerValues;

/**
 * 0x01 Attack a target
 */
public final class AttackRequest extends GameClientPacket {

	/**
	 * Creates an AttackRequest packet
	 * @param targetID		Object ID of the target
	 * @param posX			original position x (of ?)
	 * @param posY			original position y (of ?)
	 * @param posZ			original position z (of ?)
	 * @param shiftClick	shift click (true) or simple click (false)
	 */
	public AttackRequest(int targetID, float posX, float posY, float posZ, boolean shiftClick){
		writeC(0x01);
		writeD(targetID);
		writeD(ServerValues.getServerCoord(posX));//Switch z and y
		writeD(ServerValues.getServerCoord(posZ));
		writeD(ServerValues.getServerCoord(posY));
		if(shiftClick)
			writeC(1);
		else
			writeC(0);
	}
}
