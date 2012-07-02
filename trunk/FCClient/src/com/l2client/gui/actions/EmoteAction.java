package com.l2client.gui.actions;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;

public class EmoteAction extends Action {

	public EmoteAction(int id, String actionName) {
		super(id, actionName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addKeyMapping(InputManager man) {
		 man.addMapping("EmoteAction", new KeyTrigger(KeyInput.KEY_E));
	     man.addListener(this, "EmoteAction");
	}

	@Override
	public void removeKeyMapping(InputManager man) {
		man.deleteMapping("EmoteAction");
		man.removeListener(this);
	}

	@Override
	public void onAnalog(String name, float value, float tpf) {
		// TODO Auto-generated method stub
	}

}
