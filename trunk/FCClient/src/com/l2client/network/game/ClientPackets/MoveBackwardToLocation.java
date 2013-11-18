package com.l2client.network.game.ClientPackets;

import com.l2client.model.l2j.ServerValues;

/**
 * 0x0f Movement request package of client char to location, scaled down by the reverse of @see ServerCoordinates.getScaleFactor()
 * (Currently same as mult by 8)
 */
public class MoveBackwardToLocation extends GameClientPacket
{
	/**
	 * Constructor of the move package, paramters are in ClientCoordinates and will be converted to ServerCoordinates
	 * @param tx	Target x
	 * @param ty	Target y
	 * @param tz	Target z
	 * @param ox	Current x
	 * @param oy	Current y
	 * @param oz	Current z
	 * @param mouse	Mouse move of Key move (currently ignored)
	 */
	public MoveBackwardToLocation(float tx, float ty, float tz, float ox, float oy, float oz, boolean mouse)
	{
		writeC(0x0f);
		writeD(ServerValues.getServerCoordX(tx));//_targetX
		writeD(ServerValues.getServerCoordY(tz));//_targetY
		writeD(ServerValues.getServerCoordZ(ty));//_targetZ
		writeD(ServerValues.getServerCoordX(ox));//_originX
		writeD(ServerValues.getServerCoordY(oz));//_originY
		writeD(ServerValues.getServerCoordZ(oy));//_originZ
		writeD(1);//_moveMovement is 0 if cursor keys are used  1 if mouse is used

	}

}
