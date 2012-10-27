package com.l2client.gui.actions;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;

public class CompanionAction extends Action {

	public CompanionAction(int id, String actionName) {
		super(id, actionName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addKeyMapping(InputManager man) {
		 man.addMapping("CompanionAction", new KeyTrigger(KeyInput.KEY_C));
	     man.addListener(this, "CompanionAction");
	}

	@Override
	public void removeKeyMapping(InputManager man) {
		man.deleteMapping("CompanionAction");
		man.removeListener(this);
	}

	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		// TODO Auto-generated method stub
	}
}
