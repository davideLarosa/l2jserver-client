package com.l2client.gui.actions;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.l2client.app.Singleton;
import com.l2client.component.TargetComponent;
import com.l2client.model.network.ClientFacade;
import com.l2client.network.game.ClientPackets.AttackRequest;
import com.l2client.network.game.ClientPackets.GameClientPacket;


public class AttackAction extends Action {

	public AttackAction(int id, String actionName) {
		super(id, actionName);
	}

	@Override
	public void addKeyMapping(InputManager man) {
		 man.addMapping(name, new KeyTrigger(KeyInput.KEY_A));//new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
	     man.addListener(this, name);
	}

	@Override
	public void removeKeyMapping(InputManager man) {
		man.deleteMapping(name);
		man.removeListener(this);
	}

	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
//		// only execute on button/key release
		if (!isPressed) {
			ClientFacade f = Singleton.get().getClientFacade();
			TargetComponent com = (TargetComponent) Singleton.get().getEntityManager().getComponent(f.getCharHandler().getSelectedObjectId(), TargetComponent.class);
			if(com != null){
				GameClientPacket p = new AttackRequest(com.getCurrentTarget(), com.pos.x, com.pos.y, com.pos.z, false, false);
				f.sendGamePacket(p);		
			}
		}
	}
}
