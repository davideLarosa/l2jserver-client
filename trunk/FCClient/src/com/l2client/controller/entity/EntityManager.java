package com.l2client.controller.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

import com.l2client.component.Component;
import com.l2client.component.ComponentSystem;
import com.l2client.component.IdentityComponent;

/**
 * Singleton which manages entities, components and queries around them.
 * @see ComponentSystem
 */
// FIXME JUnit test of EntityManager, not sure add/remove works without flaws
// FIXME entity creation should be done here 
public class EntityManager {
	
	private static Logger log = Logger.getLogger(EntityManager.class.getName());

	private static EntityManager singleton = null;
	// entid to component class/components map
	private HashMap<Integer, HashMap<Class<? extends Component>, Component>> entityComponents;
	//component class to entid/component s
	private HashMap<Class<? extends Component>, HashMap<Integer, Component>> components;
	//component to entity
	private HashMap<Component, IdentityComponent> entities;
	//all entities
	private HashSet<Integer> entityIds;
	
	//TODO remove this, just for debug stuff
	private Integer playerID =-1;

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
		log.finer("Adding component "+com+" to "+entityId);
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
		log.finer("Removing component "+com+" from "+entityId);
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
		if(entityIds.contains(entityId)){
			HashMap<Class<? extends Component>, Component> set = entityComponents
			.get(entityId);
			if(set != null){
				Component[] arr = set.values().toArray(new Component[set.values().size()]);
				Component id = null;
				for(Component v : arr){
					if(v instanceof IdentityComponent)
						id = v; //remember id component for later removal
					else
						removeComponent(entityId, v);
				}
				if(id != null){//id is the last to remove
					removeComponent(entityId, id);
				} else
					log.severe("No ID component found on "+entityId);
	
				set.clear();
			}
			entityIds.remove(entityId);
			log.fine("Entity deleted with id "+entityId);
		} else {
			log.severe("Delete of not registered entity requested :"+entityId);
		}
		
	}

	public Entity createEntity(int i) {
		Entity ret = null;
		IdentityComponent idc = null;
		if(!entityIds.contains(i)){
			idc = new IdentityComponent(i, new Entity(i));
			entityIds.add(i);
			addComponent(i, idc);		
			entities.put(idc, idc);
			log.fine("Entity created with id "+i);
		} else {
			log.severe("Create called but ID "+i+"already used ?!?");
			idc = (IdentityComponent) getComponent(i, IdentityComponent.class);
		}
		if(idc != null){
			ret = idc.getEntity();
		} else {
			log.severe("No entity on ID component for entity "+i+" with id comp "+idc);
			dumpComponents(i);
		}
		return ret;
	}

	public boolean isPlayerComponent(Component com) {
		return (getComponent(playerID, com.getClass()) == com);
	}
	
	public void setPlayerId(int id){
		playerID = id;
		log.fine("Player set to "+playerID);
	}
	
	public void dumpComponents(int entityId){
		System.out.println("DUMP start of "+entityId);
		for(Component c : getComponents(entityId)){
			System.out.println("DUMP of "+entityId+":"+c);
		}
	}

	public void dumpAllComponents() {
		synchronized(this){
			System.out.println("DUMP start by entityIDs -------------------------------");
			for(int i : entityIds){
				dumpComponents(i);
			}
			System.out.println("DUMP start by entities -------------------------------");
			for(Component c : entities.keySet()){
				System.out.println("DUMP of "+c+" on idc "+entities.get(c));
			}
			System.out.println("DUMP start by components -------------------------------");
			for( Class<? extends Component> cl : components.keySet() ){
				System.out.println("   class :"+cl);
				HashMap<Integer, Component> comps = components.get(cl);
				for(int i : comps.keySet())
					System.out.println("DUMP of "+i+" comp "+comps.get(i));
			}
			System.out.println("DUMP start by entityComponents -------------------------------");
			for(int i : entityComponents.keySet()){
				HashMap<Class<? extends Component>, Component> c = entityComponents.get(i);
				for(Component com : c.values()){
					System.out.println("DUMP of "+i+" comp "+com);
				}
			}
		}
	}
}
