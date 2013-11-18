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
		float tX = ServerValues.getClientCoordX(readD());
		float tZ = ServerValues.getClientCoordZ(readD());
		float tY = ServerValues.getClientCoordY(readD());
//		float tY = tZ;
//		tZ = tt;
		float cX = ServerValues.getClientCoordX(readD());
		float cZ = ServerValues.getClientCoordZ(readD());
		float cY = ServerValues.getClientCoordY(readD());
//		float cY = cZ;
//		cZ = ct;

		if(tX==cX && tY == cY && tZ == cZ)
			return;
		
		log.fine("move " + objId + " from " + cX + "," + cY
				+ "," + cZ + " to " + tX + "," + tY + "," + tZ);
		
		Singleton.get().getPosSystem().initMoveTo(objId, tX, tY, tZ, cX, cY, cZ);
	}
}
