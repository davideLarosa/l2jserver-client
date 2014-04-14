package com.l2client.model.jme;

import com.jme3.math.Vector3f;
import com.jme3.scene.control.BillboardControl;

public class MessagesBillboardControl extends BillboardControl {
	
	private float ttl = 1f;
	private float speed = 1f;
	private float lived = 0f;
	
	public MessagesBillboardControl(){}
	
	public MessagesBillboardControl(float timeToLive, float unitsPerSecond){
		super();
		ttl = timeToLive;
		speed = unitsPerSecond;
	}
	
	public void setTravelSpeed(float unitsPerSecond){
		speed = unitsPerSecond;
	}
	
	public void setTimeToLive(float timeToLive){
		ttl = timeToLive;
	}

    @Override
    protected void controlUpdate(float tpf) {
    	lived += tpf;
    	if(lived > ttl){
    		//remove
    		if(spatial != null){
    			spatial.removeFromParent();
    			spatial.removeControl(this);
    			spatial = null;
    		}
    	} else {
    		//update position
    		if(spatial != null){
    			Vector3f v = spatial.getLocalTranslation();
    			v.y+= speed*tpf;
    			spatial.setLocalTranslation(v.x, v.y, v.z);
    		}
    	}
    }
}
