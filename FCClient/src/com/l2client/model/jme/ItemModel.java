package com.l2client.model.jme;

import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.l2client.app.Singleton;
import com.l2client.model.l2j.ItemInstance;

public class ItemModel extends VisibleModel {

	
	public ItemModel(ItemInstance item) {
		super(null);
		name = ENTITY_PREFIX+item.name+"_"+item.objectId;
		this.setShadowMode(ShadowMode.CastAndReceive);
	}

	/**
	 * Returns an item model
	 */
	protected Node createVisuals() {
		if (vis == null) {	
			Spatial s = Singleton.get().getAssetManager().getJmeAssetMan().loadModel("items/bottle/HealthFlask.j3o");
			if(s instanceof Node ){
				vis = (Node) s.clone(false);
				vis.setName(name);
//				vis.setLocalTranslation(0f, 1f, 0f);
			}
		}
		
		return vis;
	}
	
	/**
	 * no label
	 */
	public void updateLabel() {}
	
	/**
	 * no selection marker
	 */
	public void addSelectionMarker(ColorRGBA color){
	}
	
	/**
	 * no selection marker
	 */
	public void removeSelectionMarker(){
	}
	
	/**
	 * no health bar
	 */
	public void updateHealthbar(float percent){
	}

}
