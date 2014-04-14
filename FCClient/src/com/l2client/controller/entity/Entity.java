package com.l2client.controller.entity;

import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.l2client.component.ComponentSystem;

/**
 * Just a reference to an id for entities based on components.
 * @see ComponentSystem
 * @see EntityManager
 * 
 */
public class Entity extends Node {
	
	/**
	 * Identifier used for userData on jme geometries
	 */
	public static final String ENTITY_ID = "entity_id";

	private int id;

	Entity(int id){
		super("Entity: "+id);
		this.id = id;
		this.setShadowMode(ShadowMode.Inherit);
	}
	
	public int getId(){
		return id;
	}
}
