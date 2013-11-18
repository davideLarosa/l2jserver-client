package com.l2client.network.game.ServerPackets;

import com.l2client.app.Singleton;
import com.l2client.model.l2j.ServerValues;

public class MoveToPawn extends GameServerPacket {

	@Override
	public void handlePacket() {
		log.finer("Read from Server "+this.getClass().getSimpleName());
		int objId = readD();
		int tgt = readD();//TODO target id currently not used
		float distance = ServerValues.getClientScaled(readD());//TODO distance to target currently not used
		float cX = ServerValues.getClientCoordX(readD());
		float cZ = ServerValues.getClientCoordZ(readD());
		float cY = ServerValues.getClientCoordY(readD());
//		float cY = cZ;
//		cZ = ct;
		float tX = ServerValues.getClientCoordX(readD());
		float tZ = ServerValues.getClientCoordZ(readD());
		float tY = ServerValues.getClientCoordY(readD());
//		float tY = tZ;
//		tZ = tt;

		if(cX==tX && cY == tY && cZ == tZ)
			return;

		Singleton.get().getPosSystem().initMoveTo(objId, tX, tY, tZ, cX, cY, cZ);
	}

}
