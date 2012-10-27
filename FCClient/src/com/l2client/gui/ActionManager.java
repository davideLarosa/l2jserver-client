package com.l2client.gui;

import java.util.HashMap;
import java.util.logging.Logger;

import com.l2client.app.Singleton;
import com.l2client.gui.actions.BaseUsable;

//FIXME think about using a static NULLAction
public final class ActionManager {

	private static Logger logger = Logger.getLogger(ActionManager.class.getName());
	private final static ActionManager instance = new ActionManager();
//	private HashMap<Icon, BaseUsable> iconMap = new HashMap<Icon, BaseUsable>();
	private HashMap<Integer, BaseUsable> idMap = new HashMap<Integer, BaseUsable>();
	private static int loaded = -1;
	
	private ActionManager(){
	}
	
	public static ActionManager getInstance(){
		return instance;
	}
	
	private void addAction(BaseUsable base){
		idMap.put(base.getId(), base);
	}

	public BaseUsable getAction(int id) {
		return idMap.get(id);
	}
	
	public BaseUsable[] getActions(){
		return idMap.values().toArray(new BaseUsable[0]);
	}

	public void loadActions(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				if(loaded > -1) //in load or loaded!
					return;
				
				loaded = 0;
				BaseUsable[] arr = Singleton.get().getDataManager().loadAllActions();
				if(arr.length <=0){
					loaded = 1;
					logger.severe("Failed to load actions, DAO returned 0 actions");
					return;
				}
				
				for(BaseUsable u : arr){
					addAction(u);
				}
				loaded = 1;
			}
		}).start();
	}
	
	public static boolean isLoaded(){
		return loaded>0?true:false;
	}
}
