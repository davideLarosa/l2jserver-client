package com.l2client.network.game.ServerPackets;

import com.l2client.app.Singleton;
import com.l2client.component.EnvironmentComponent;
import com.l2client.component.SimplePositionComponent;

/**
 * 
 * 		writeC(0x28);
		writeD(_charObjId);
		writeD(_running ? RUN : WALK); //1,0
		writeD(0); //c2
 *
 */
public class ChangeMoveType extends GameServerPacket {

	@Override
	public void handlePacket() {
		log.fine("Read from Server "+this.getClass().getSimpleName());
		int objId = readD();
		int run = readD();
		readD();
		SimplePositionComponent com = (SimplePositionComponent) Singleton.get().getEntityManager().getComponent(objId, SimplePositionComponent.class);
		if(com != null){
			com.running = run>0?true:false;
			EnvironmentComponent env = (EnvironmentComponent) Singleton.get().getEntityManager().getComponent(objId, EnvironmentComponent.class);
			if(env != null){
				env.changed = true;
			} else
				log.severe("Found NO EnvironmentComponent for entity with ID:"+objId);
		} else
			log.severe("Found NO SimplePositioningComponent for entity with ID:"+objId);

	}

}
