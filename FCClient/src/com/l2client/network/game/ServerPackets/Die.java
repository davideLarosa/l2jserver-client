package com.l2client.network.game.ServerPackets;

import com.l2client.animsystem.jme.actions.CallActions;
import com.l2client.app.Singleton;
import com.l2client.component.Component;
import com.l2client.component.EnvironmentComponent;
import com.l2client.component.L2JComponent;
import com.l2client.component.TargetComponent;
import com.l2client.controller.entity.EntityManager;

public class Die extends GameServerPacket {

	@Override
	public void handlePacket() {
		log.fine("Read from Server "+this.getClass().getSimpleName());
        int id = readD();
        // NOTE:
        // 6d 00 00 00 00 - to nearest village
        // 6d 01 00 00 00 - to hide away
        // 6d 02 00 00 00 - to castle
        // 6d 03 00 00 00 - to siege HQ
        // sweepable
        // 6d 04 00 00 00 - FIXED
    
        int whereTo = 0;
        readD();//nearestVillage 
        if(readD()>0) whereTo = 1;//clanhall
        readD();//hideaway/clan
        if(readD()>0) whereTo = 2;//castle
        if(readD()>0) whereTo = 4;//siegeHQ
        if(readD()>0) whereTo = 5;//fixed or festival
        if(readD()>0) whereTo = 3;//fort
    	//signal animation death
        Singleton.get().getAnimSystem().callAction(CallActions.Die, id);
    	
    	EntityManager ent = Singleton.get().getEntityManager();
    	//update health properly
    	L2JComponent com = (L2JComponent) ent.getComponent(id, L2JComponent.class);
    	com.l2jEntity.setCurrentHp(0);
    	
    	//player died
    	int playerId = _client.getCharHandler().getSelectedObjectId();
        if(id == playerId) {
	        Singleton.get().getGuiController().displayReviveJPanel(whereTo, null);
        } else { //target died?
        	TargetComponent tgt = (TargetComponent) ent.getComponent(playerId, TargetComponent.class);
        	if(tgt.getCurrentTarget() == id){
        		//remove it and signla env change
        		tgt.setNoTarget();
        		EnvironmentComponent env = (EnvironmentComponent) ent.getComponent(playerId, EnvironmentComponent.class);
        		env.changed = true;
        		
        	}
        }
	}

}
