package com.l2client.controller.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.l2client.component.Component;
import com.l2client.component.IdentityComponent;

/**
 * Singleton which manages entites, components and queries around them.
 * @see ComponentSystem
 */
// FIXME JUnit test
// FIXME entity crwation should be done here 
public class EntityManager {

	private static EntityManager singleton = null;
	// ent:map of components, componenttype:map of entities
	private HashMap<Integer, HashMap<Class<? extends Component>, Component>> entityComponents;
	private HashMap<Class<? extends Component>, HashMap<Integer, Component>> components;
	private HashMap<Component, IdentityComponent> entities;
	private HashSet<Integer> entityIds;

	private EntityManager() {
		singleton = this;
		entityComponents = new HashMap<Integer, HashMap<Class<? extends Component>,Component>>();
		components = new HashMap<Class<? extends Component>, HashMap<Integer,Component>>();
		entities = new HashMap<Component, IdentityComponent>();
		entityIds = new HashSet<Integer>();

	}

	public static EntityManager get() {
		if (singleton != null)
			return singleton;
		else {
			new EntityManager();
			return singleton;

		}
	}
	
	public int getEntityId(Component c){
		int ret = -1;
		IdentityComponent ent = entities.get(c);
		if(ent != null)
			ret = ent.getId();
		
		return ret;
	}
	
	public IdentityComponent getEntity(Component c){
		return entities.get(c);
	}

	public ArrayList<Component> getComponents(int entityId) {
		HashMap<Class<? extends Component>, Component> set = entityComponents
				.get(entityId);
		if (set != null)
			return new ArrayList<Component>(set.values());

		return new ArrayList<Component>();
	}
	

	public ArrayList<Component> getComponents(Class<? extends Component> type) {
		HashMap<Integer, Component> set = components.get(type);
		if (set != null)
			return new ArrayList<Component>(set.values());

		return new ArrayList<Component>();
	}
	

	public Component getComponent(int entityId, Class<? extends Component> type) {
		HashMap<Class<? extends Component>, Component> set = entityComponents
				.get(entityId);
		if (set != null)
			return set.get(type);
		else
			return null;
	}

	public void addComponent(int entityId, Component com) {
		HashMap<Class<? extends Component>, Component> set = entityComponents
		.get(entityId);
		if(set == null){
			set = new HashMap<Class<? extends Component>, Component>();
			entityComponents.put(entityId, set);
		}
		set.put(com.getClass(), com);
		addComponents(entityId, com);
		entities.put(com, (IdentityComponent)getComponent(entityId, IdentityComponent.class));
	}
	private void addComponents(int entityId, Component com){
		HashMap<Integer, Component> comps = components.get(com.getClass());
		if(comps == null){
			comps = new HashMap<Integer, Component>();
			components.put(com.getClass(), comps);
		}
		comps.put(entityId, com);
	}
	
	public void removeComponent(int entityId, Component com) {
		HashMap<Class<? extends Component>, Component> set = entityComponents
		.get(entityId);
		if(set != null){
			set.remove(com.getClass());
			if(set.isEmpty())
				entityComponents.remove(entityId);
			removeComponents(entityId, com.getClass());
		}
		entities.remove(com);
	}
	private void removeComponents(int entityId, Class<? extends Component> type){
		HashMap<Integer, Component> comps = components.get(type);
		if(comps != null){
			comps.remove(entityId);
			if(comps.isEmpty())
				components.remove(type);
		}

	}

	public void deleteEntity(int entityId){
		HashMap<Class<? extends Component>, Component> set = entityComponents
		.get(entityId);
		if(set != null){
			{
				for(Component v : set.values()){
					if(v instanceof IdentityComponent)
						entities.remove(v);
					removeComponent(entityId, v);
				}

			}
			set.clear();
		}
		entityIds.remove(entityId);
		
	}

	public Entity createEntity(int i) {
		Entity ret = null;
		if(!entityIds.contains(i)){
			entityIds.add(i);
			IdentityComponent idc = new IdentityComponent(i, new Entity(i));
			addComponent(i, idc);		
			entities.put(idc, idc);
			ret = idc.getEntity();
		} else {
			System.out.println("Create called but ID "+i+"already used ?!?");
		}
		return ret;
	}
}
