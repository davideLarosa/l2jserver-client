package com.l2client.controller.handlers;

import com.l2client.app.Singleton;
import com.l2client.component.Component;
import com.l2client.component.EnvironmentComponent;
import com.l2client.component.IdentityComponent;
import com.l2client.component.ItemComponent;
import com.l2client.component.PositioningComponent;
import com.l2client.controller.SceneManager.Action;
import com.l2client.controller.entity.Entity;
import com.l2client.model.jme.ItemModel;
import com.l2client.model.jme.NPCModel;
import com.l2client.model.l2j.ItemInstance;
import com.l2client.model.l2j.ServerValues;
import com.l2client.model.network.NewCharSummary;

public class ItemHandler {

	public void addItem(final int charId, final int objId, final int itemId, final int x, final int y, final int z,
			final boolean stackable, final long count) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				Entity ent = Singleton.get().getEntityManager().createEntity(objId);
				ItemInstance item = new ItemInstance();
				item.charId = charId;
				item.objectId = objId;
				item.itemId = itemId;
				item.worldPosition = 	ServerValues.getClientCoords(x, y, z);
				item.stackable = stackable;
				item.count = count;
				
				ItemModel i = new ItemModel(item);
				i.attachVisuals();
				Singleton.get().getNavManager().snapToGround(item.worldPosition);
				ent.setLocalTranslation(item.worldPosition);
				ent.setName(i.getName());
				ent.attachChild(i);
				
				
				Singleton.get().getSceneManager().changeItemNode(ent,Action.ADD);
			}
		}).start();
		
	}
	
	public void removeItem(int obj) {
		Singleton s = Singleton.get();
		IdentityComponent id = (IdentityComponent) s.getEntityManager().getComponent(obj, IdentityComponent.class);
		if(id != null){
			Entity e = id.getEntity();
			if(e != null)
				s.getSceneManager().changeItemNode(e,Action.REMOVE);		
		} else {
System.out.println("ERROR!! Remove of "+id+" but no ID comp found!?! NO comps removed :-(");			
		}
		s.getEntityManager().deleteEntity(obj);
System.out.println("REMOVE of "+id+" finished");			
		
	}

}
