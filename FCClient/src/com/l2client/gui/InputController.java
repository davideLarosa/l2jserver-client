package com.l2client.gui;

import java.util.ArrayList;
import java.util.Stack;

import com.jme3.input.InputManager;
import com.l2client.gui.actions.BaseUsable;
import com.l2client.model.network.ClientFacade;

/**
 * Input controller for pushing and popping input handlers so each gui can have its own
 *
 */
public final class InputController {

	private static InputController singleton;
	private InputManager inputManager = null;
	private Stack<ArrayList<BaseUsable>> actions = null;
	
	private InputController(){	
		actions = new Stack<ArrayList<BaseUsable>>();
	}
	
	public static InputController get(){
		if(singleton == null){
			synchronized (InputController.class) {
				if(singleton == null){
					singleton = new InputController();
				}
			}
		}
		return singleton;
	}
	
	/**
	 * put an input handler on the stack, disable input of handler below
	 * @param in
	 */
	public void pushInput(ArrayList<BaseUsable> actions){
		if(!this.actions.isEmpty())
			for(BaseUsable u : this.actions.peek())
				u.removeKeyMapping(inputManager);
		
		this.actions.push(actions);
		
		for(BaseUsable u : actions)
			u.addKeyMapping(inputManager);
	}
	
	/**
	 * remove the top input handler, disable its input and enable the new one
	 * @return
	 */
	public ArrayList<BaseUsable> popInput(){
		ArrayList<BaseUsable> top = new ArrayList<BaseUsable>();
		
		if(!actions.isEmpty()){
			top=actions.pop();
		
			for(BaseUsable u : top)
				u.removeKeyMapping(inputManager);
		}
		
		if(!actions.isEmpty())
			for(BaseUsable u : actions.peek())
				u.addKeyMapping(inputManager);

		return top;
	}
	
	/**
	 * adds the actions to the current input mapping without disabling any old
	 * @return
	 */
	public void addInput(ArrayList<BaseUsable> in){
		ArrayList<BaseUsable> top = actions.peek();
		top.addAll(in);
		for(BaseUsable u : in){
			u.addKeyMapping(inputManager);
		}
			
	}
	
	/**
	 * removes the actions from the current input mapping without disabling any old
	 * @return
	 */
	public void removeInput(ArrayList<BaseUsable> in){
		ArrayList<BaseUsable> top = actions.peek();
		top.removeAll(in);
	}

	public void initialize(InputManager inputManager) {
		this.inputManager = inputManager;
	}

	public InputManager getInputManager() {
		return inputManager;
	}
}
