package com.l2client.network.game.ServerPackets;

import com.l2client.app.Singleton;
import com.l2client.model.l2j.ServerValues;

public class MoveToPawn extends GameServerPacket {

	@Override
	public void handlePacket() {
		log.finer("Read from Server "+this.getClass().getSimpleName());
		int objId = readD();
		int tgt = readD();//TODO target id currently not used
		float distance = ServerValues.getClientCoord(readD());//TODO distance to target currently not used
		float cX = ServerValues.getClientCoord(readD());
		float cZ = ServerValues.getClientCoord(readD());
		float cY = ServerValues.getClientCoord(readD());
//		float cY = cZ;
//		cZ = ct;
		float tX = ServerValues.getClientCoord(readD());
		float tZ = ServerValues.getClientCoord(readD());
		float tY = ServerValues.getClientCoord(readD());
//		float tY = tZ;
//		tZ = tt;

		if(cX==tX && cY == tY && cZ == tZ)
			return;

		Singleton.get().getPosSystem().initMoveTo(objId, tX, tY, tZ, cX, cY, cZ);
	}

}
