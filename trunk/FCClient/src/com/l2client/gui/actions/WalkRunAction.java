package com.l2client.gui.actions;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;

public class WalkRunAction extends Action {

	public WalkRunAction(int id, String actionName) {
		super(id, actionName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addKeyMapping(InputManager man) {
		 man.addMapping("WalkRunAction", new KeyTrigger(KeyInput.KEY_W));
	     man.addListener(this, "WalkRunAction");
	}

	@Override
	public void removeKeyMapping(InputManager man) {
		man.deleteMapping("WalkRunAction");
		man.removeListener(this);
	}

	@Override
	public void onAnalog(String name, float value, float tpf) {
		// TODO Auto-generated method stub
	}
}
