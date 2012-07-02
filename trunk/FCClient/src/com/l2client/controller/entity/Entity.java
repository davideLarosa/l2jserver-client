package com.l2client.controller.entity;

import com.jme3.scene.Node;

/**
 * Just a reference to an id for entities based on components.
 * @see ComponentSystem
 * @see EntityManager
 * 
 */
public class Entity extends Node {

	private int id;

	Entity(int id){
		super("Entity: "+id);
		this.id = id;
	}
	
	public int getId(){
		return id;
	}
}
