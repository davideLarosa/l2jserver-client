package com.l2client.network.game.ServerPackets;

import com.jme3.math.Vector3f;
import com.l2client.app.Singleton;
import com.l2client.component.EnvironmentComponent;
import com.l2client.component.SimplePositionComponent;
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

		SimplePositionComponent pos = (SimplePositionComponent) Singleton.get().getEntityManager().getComponent(objId, SimplePositionComponent.class);
		if (pos != null){
			log.info("trigger teleport of " + objId + " from " + pos.currentPos + " to " + tPos);
	
			synchronized (pos) {	
					pos.teleport = true;
					pos.goalPos.set(tPos.x,0f,tPos.z);//FIXME ignores height atm
					pos.targetHeading = heading;
					log.info(+objId+" New Heading "+pos.targetHeading+" received");
			}
		}
		else
			log.severe("No SimplePositioningComonent found with entity id "+objId+", perhaps just create one?");
		
	}

}
