package com.l2client.network.game.ServerPackets;

import com.jme3.math.Vector3f;
import com.l2client.app.Singleton;
import com.l2client.model.l2j.ServerValues;

public class TeleportToLocation extends GameServerPacket {

	@Override
	public void handlePacket() {
		log.fine("Read from Server "+this.getClass().getSimpleName());
		int objId = readD();
		Vector3f tPos = new Vector3f();
		tPos.x = ServerValues.getClientCoord(readD());
		tPos.z = ServerValues.getClientCoord(readD());
		tPos.y = ServerValues.getClientCoord(readD());
		readD();
		float heading = ServerValues.getClientHeading(readD());
		//initialize at least the navmesh and the basics to be loaded
		Singleton.get().getTerrainManager().prepareTeleport(tPos);

		Singleton.get().getPosSystem().initTeleportTo(objId, tPos.x, tPos.y, tPos.z, heading);	
	}

}
