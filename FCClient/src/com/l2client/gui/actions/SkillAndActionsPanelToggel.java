package com.l2client.gui.actions;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;

public class SkillAndActionsPanelToggel extends Action {

	public SkillAndActionsPanelToggel(int id, String actionName) {
		super(id, actionName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addKeyMapping(InputManager man) {
		 man.addMapping(name, new KeyTrigger(KeyInput.KEY_L));
	     man.addListener(this, name);
	}

	@Override
	public void removeKeyMapping(InputManager man) {
		man.deleteMapping(name);
		man.removeListener(this);
	}

	@Override
	public void onAnalog(String name, float value, float tpf) {
		// TODO Auto-generated method stub
	}
}
