package com.l2client.network.game.ServerPackets;

import com.l2client.app.Singleton;
import com.l2client.model.l2j.ServerValues;

/**
 * Stop right at the described spot
 */
public final class StopMove extends GameServerPacket
{

	/**
	 * Reads the objectId and the current and target vector 
	 */
	@Override
	public void handlePacket()
	{
		log.finer("Read from Server "+this.getClass().getSimpleName());
		int objId = readD();
		float tX = ServerValues.getClientCoordX(readD());
		float tZ = ServerValues.getClientCoordZ(readD());
		float tY = ServerValues.getClientCoordY(readD());
log.info("Coords:"+tX+","+tY+","+tZ);
		float heading = ServerValues.getClientHeading(readD());
System.out.println("StopMove received heading "+heading+" for "+objId);
		Singleton.get().getPosSystem().initStopAt(objId, tX, tY, tZ, heading);
	}
}
