package com.l2client.network.game.ServerPackets;

import com.l2client.app.Singleton;
import com.l2client.component.EnvironmentComponent;
import com.l2client.component.TargetComponent;
import com.l2client.controller.entity.EntityManager;

public class TargetUnselected extends GameServerPacket {

	@Override
	public void handlePacket() {
		log.fine("Read from Server "+this.getClass().getSimpleName());
		int id = readD();
//		Vector3f tPos = new Vector3f();
//		readD();//x
//		readD();//y
//		readD();//z
//		readD();//0x00
		EntityManager em = Singleton.get().getEntityManager();
		TargetComponent tc = (TargetComponent) em.getComponent(id, TargetComponent.class);
		tc.setTarget(TargetComponent.NO_TARGET);
		tc.color = null;
		EnvironmentComponent env = (EnvironmentComponent) em.getComponent(id, EnvironmentComponent.class);
		if (env != null){
			env.changed = true;
		}else
			log.severe("No EnvironmentComponent found with entity id "+id+", perhaps just create one?");
	}

}
