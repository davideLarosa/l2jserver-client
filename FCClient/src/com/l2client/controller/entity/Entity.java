package com.l2client.controller.entity;

import com.jme3.scene.Node;

/**
 * A movable object in the game world
 * 
 * Portions Copyright (C) Greg Snook, 2000
 * 
 * @author TR
 * 
 */
//FIXME should be onyl an id and a bunch of components
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
