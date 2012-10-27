package com.l2client.network.game.ServerPackets;

import com.l2client.app.Singleton;
import com.l2client.model.l2j.ServerValues;

/**
 * Movement packet from the server, move information contains:
 *  
 * the object id of the object to be moved
 * current position vector in integer format (which will be scaled down by ServerCoordinates.getScaleFactor()
 * target position vector in integer format (which will be scaled down by ServerCoordinates.getScaleFactor()
 * 
 * 
 */
public final class MoveToLocation extends GameServerPacket
{

	/**
	 * Reads the objectId and the current and target vector and triggers a move to
	 * action (should be moved out of the clientfacade to here
	 */
	@Override
	public void handlePacket()
	{
		log.finer("Read from Server "+this.getClass().getSimpleName());
		int objId = readD();
		float tX = ServerValues.getClientCoord(readD());
		float tZ = ServerValues.getClientCoord(readD());
		float tY = ServerValues.getClientCoord(readD());
//		float tY = tZ;
//		tZ = tt;
		float cX = ServerValues.getClientCoord(readD());
		float cZ = ServerValues.getClientCoord(readD());
		float cY = ServerValues.getClientCoord(readD());
//		float cY = cZ;
//		cZ = ct;

		if(tX==cX && tY == cY && tZ == cZ)
			return;
		
		Singleton.get().getPosSystem().initMoveTo(objId, tX, tY, tZ, cX, cY, cZ);
	}
}
