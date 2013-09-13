package com.l2client.network.game.ServerPackets;

import com.l2client.app.Singleton;
import com.l2client.component.PositioningComponent;
import com.l2client.model.l2j.ServerValues;

/**
 * Start of a rotation initiated from the server (just a passthrough, its all based on our startrotation client packet in l2j)
 *
 *		writeC(0x7a);
		writeD(_charObjId);
		writeD(_degree);
		writeD(_side);
		writeD(_speed);
 */
//TODO it seems only players do this, when?
public class StartRotation extends GameServerPacket {

	@Override
	public void handlePacket() {
		log.fine("Read from Server "+this.getClass().getSimpleName());
		//object which should be rotated
		int objId = readD();
		//degrees to ratate
		int deg = readD();
		//what we put in before
		int side = readD();
		//server always sends 0
		int speed = readD();
		
		log.fine("Received StartRotation for "+objId+" deg "+deg+" side "+side);
		PositioningComponent pos = (PositioningComponent) Singleton.get().getEntityManager().getComponent(objId, PositioningComponent.class);
		if(pos != null){
			pos.targetHeading = ServerValues.getClientHeading(deg);
		} else 
			log.severe("No SimplePositioningComponent found for entity "+objId);
		
	}

}
