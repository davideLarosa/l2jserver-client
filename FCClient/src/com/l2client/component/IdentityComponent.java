package com.l2client.component;

import com.l2client.controller.entity.Entity;

public class IdentityComponent implements Component {

	private int id;

	private Entity entity;

	public IdentityComponent(int id, Entity owner) {
		this.id = id;
		this.entity = owner;
	}
	
	public int getId() {
		return id;
	}

	public Entity getEntity() {
		return entity;
	}
	
}
