package com.l2client.controller.handlers;

/**
 * common superclass used for similarities between npc's and players
 *
 */
//TODO move Entity movement here
public abstract class AbstractEntityHandler extends AbstractHandler {
	public abstract Integer[] getObjectIDs();
	
	/**
	 * Initializes the move on the visual part of the entity
	 * 
	 * @param id object id of the entity to be moved
	 * @param tx x coordinate the entity should move to
	 * @param ty y coordinate the entity should move to
	 * @param tz z coordinate the entity should move to
	 */
	public abstract void initMoveToAction(int id, float tx, float ty, float tz);
}
