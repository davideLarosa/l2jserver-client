package com.l2client.gui.actions;

import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import com.l2client.component.TargetComponent;
import com.l2client.controller.entity.EntityManager;
import com.l2client.model.network.ClientFacade;
import com.l2client.network.game.ClientPackets.AttackRequest;
import com.l2client.network.game.ClientPackets.GameClientPacket;


public class AttackAction extends Action {

	public AttackAction(int id, String actionName) {
		super(id, actionName);
	}

	@Override
	public void addKeyMapping(InputManager man) {
		 man.addMapping(name, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
	     man.addListener(this, name);
	}

	@Override
	public void removeKeyMapping(InputManager man) {
		man.deleteMapping(name);
		man.removeListener(this);
	}

	@Override
	public void onAnalog(String name, float value, float tpf) {
		ClientFacade f = ClientFacade.get();
		TargetComponent com = (TargetComponent) EntityManager.get().getComponent(f.getCharHandler().getSelectedObjectId(), TargetComponent.class);
		if(com != null){
			GameClientPacket p = new AttackRequest(com.targetID, com.pos.x, com.pos.y, com.pos.z, value > 0f?true:false);
			f.sendPacket(p);		
		}
	}
}
