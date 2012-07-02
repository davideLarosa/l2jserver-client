package com.l2client.component;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.l2client.controller.entity.Entity;
import com.l2client.controller.entity.EntityManager;

/**
 * Singleton component system responsible for 
 * - synchronizing positions between the component computed an the jMonkeyEngine
 * 
 * for general working of a component system have a look at @see ComponentSystem
 */
public class JmeUpdateSystem extends ComponentSystem {

	private static JmeUpdateSystem inst = null;
	
	private JmeUpdateSystem(){
		inst = this;
	}
	
	public static JmeUpdateSystem get(){
		if(inst != null)
			return inst;
		
		new JmeUpdateSystem();
		
		return inst;
	}
//	public boolean syncPosition(PositioningComponent com, float dt) {
//		IdentityComponent e = EntityManager.get().getEntity(com);
//		if(e != null)
//		{
//			e.getEntity().setLocalTranslation(com.position.x, com.position.y+com.heightOffset, com.position.z);
//		}
//			
//		return false;
//	}

	@Override
	public void onUpdateOf(Component c, float tpf) {
		if(c instanceof PositioningComponent){
//			syncPosition((PositioningComponent) c, tpf);
			PositioningComponent com = (PositioningComponent) c;
			IdentityComponent e = EntityManager.get().getEntity(com);
			if(e != null)
			{
				e.getEntity().setLocalTranslation(com.position.x, com.position.y+com.heightOffset, com.position.z);
			}
		}else if(c instanceof SimplePositionComponent){
			SimplePositionComponent com = (SimplePositionComponent) c;
			IdentityComponent e = EntityManager.get().getEntity(com);
			if(e != null)
			{
				Entity ent = e.getEntity();
				ent.setLocalTranslation(com.currentPos.x, 0f /* currently ignoring height com.currentPos.y */, com.currentPos.z);
				ent.setLocalRotation(new Quaternion().fromAngleNormalAxis(com.heading, Vector3f.UNIT_Y));
			}			
		}
	}
}
