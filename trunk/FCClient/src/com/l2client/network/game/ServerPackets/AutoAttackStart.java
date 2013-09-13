package com.l2client.network.game.ServerPackets;

import com.l2client.animsystem.jme.actions.CallActions;
import com.l2client.app.Singleton;
import com.l2client.component.PositioningComponent;
import com.l2client.component.PositioningSystem;
import com.l2client.component.TargetComponent;
import com.l2client.controller.entity.EntityManager;

public class AutoAttackStart extends GameServerPacket {

	@Override
	public void handlePacket() {
		log.fine("Read from Server "+this.getClass().getSimpleName());
		int whoAttacked = readD();
		log.finer("AUTOATTACK STARTED by+"+whoAttacked);
		//face at target
		EntityManager em = Singleton.get().getEntityManager();
		PositioningComponent pcpos = (PositioningComponent) em.getComponent(whoAttacked, PositioningComponent.class);
		TargetComponent tc = (TargetComponent) em.getComponent(whoAttacked, TargetComponent.class);
		PositioningComponent npcpos = null;
		if(tc!=null && tc.hasTarget()) {
			log.finer("AUTOATTACK STARTED by+"+whoAttacked+" target comp :"+tc.getCurrentTarget());
			npcpos = (PositioningComponent) em.getComponent(tc.getCurrentTarget(), PositioningComponent.class);

			if(pcpos != null && npcpos != null){
				pcpos.targetHeading = PositioningSystem.getHeading(pcpos.position, npcpos.position);
			}
		}
		//start default attack animation
		Singleton.get().getAnimSystem().callAction(CallActions.DefaultAttack, whoAttacked);
	}

}
