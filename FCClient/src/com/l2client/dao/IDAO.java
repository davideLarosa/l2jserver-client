package com.l2client.dao;

import com.l2client.gui.actions.BaseUsable;

/**
 * Interface definition for data stores for client side data
 * npc information
 * actions
 * items
 * spells
 * skills
 * etc..
 *
 */
public interface IDAO {
	/**
	 * Fetch names from npc table based on template id
	 * 
	 * @param templateID id of the template to be loaded
	 * @return String representing the name or an empty Sting in case no name was loaded or an error occured
	 */
	public abstract String getNpcName(int templateID);
	
	/**
	 * Fetch the name of the game model or null to be used for the secified npc
	 * @param templateId id of the npc the gamemodel should be looked up
	 * @return String representing the model template (e.g. goblin or dwarwarrior etc.) or null in case none is defined
	 */
	public abstract String getNpcGameModel(int templateId);

	/**
	 * Load all actions 
	 * @return an array of BaseUsable's (actions)
	 */
	public abstract BaseUsable[] loadAllActions();
	
	/**
	 * do any needed initialize here
	 */
	public abstract void init();
	/**
	 * do any needed cleanup here
	 */
	public abstract void finit();
}