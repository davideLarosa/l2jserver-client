package com.l2client.component;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.l2client.controller.entity.EntityManager;
import com.l2client.controller.entity.ISpatialPointing;
import com.l2client.controller.entity.SpatialPointIndex;
import com.l2client.navigation.EntityNavigationManager;

public class PositioningSystem extends ComponentSystem {

	//rotate 90 deg üer sec
	private static final float ROTATION_PER_SEC = FastMath.HALF_PI;
	private static PositioningSystem inst = null;
	private SpatialPointIndex index = null;
	
	private PositioningSystem(){
		inst = this;
		index = new SpatialPointIndex(10);
	}
	
	public static PositioningSystem get(){
		if(inst != null)
			return inst;
		
		new PositioningSystem();
		
		return inst;
	}

	@Override
	public void onUpdateOf(Component c, float tpf) {
		//FIXME more intelligent way to casting
		if(c instanceof PositioningComponent)
			move((PositioningComponent)c, tpf);
		else if(c instanceof SimplePositionComponent){
			SimplePositionComponent pos = (SimplePositionComponent)c;
			move(pos, tpf);
			rotate(pos, tpf);
			
		}
	}
	
	private boolean move(SimplePositionComponent com, float dt) {
		if(!com.currentPos.equals(com.goalPos)){
				float cSpeed = com.running?com.runSpeed:com.walkSpeed;
	            //target reached then remove self
				
				Vector3f dist = com.goalPos.subtract(com.currentPos);
	            if (dist.length() <= (dt*cSpeed)) {
//	            	com.speed = 0f;  
	            	com.currentPos.set(com.goalPos);
	            	com.startPos.set(com.goalPos);
					int id = EntityManager.get().getEntityId(com);
					if (id > -1) {
						EnvironmentComponent env = (EnvironmentComponent) EntityManager
								.get().getComponent(id, EnvironmentComponent.class);
						if (env != null) {
							env.movement = -1;
							env.changed = true;
						}else
		    				System.out.println("No EnvironmentComponent found with entity id "+id+", perhaps just create one?");
	    			}else
	    				System.out.println("No entity id found as owner of Component"+com);

	            	return true;
	            } else {
	            //TODO the npcdata should be updated too
	            //move
	            //TODO turn towards target direction and move
	            Vector3f dir = dist.normalizeLocal();
	            com.currentPos.addLocal(dir.multLocal(dt*cSpeed));
	            }
		}
			
		return false;
	}
	
	private boolean rotate(SimplePositionComponent com, float dt){
		if (com.heading != com.targetHeading){
			
		if(FastMath.abs(com.heading - com.targetHeading ) > 0.05f){
			float angle = angleBetween(com.heading, com.targetHeading);
			if(angle < 0f){//rotate left
				com.heading -=(ROTATION_PER_SEC *dt);
			} else {//rotate right
				com.heading +=(ROTATION_PER_SEC *dt);
			}
		} else
			com.heading = com.targetHeading;
			return true;
		
		} else
			return false;
	}
	

	private boolean move(PositioningComponent com, float dt) {
		//are we moving??
		if(com.nextWayPoint != null){
			com.lastPosition.set(com.position);
			//compute direction from current location to target
			Vector3f cDir = com.nextWayPoint.Position.subtract(com.position);
			com.direction = cDir;
			//currently no acc/dcc only linear time
			com.speed = com.maxSpeed*dt;
			
			Vector3f endPos;
			if(com.speed*com.speed > cDir.lengthSquared())
				endPos = com.nextWayPoint.Position.clone();
			else
				endPos = com.position.add(cDir.normalizeLocal().multLocal(com.speed));
			
			//now try to walk it
			EntityNavigationManager.get().ResolveMotionOnMesh(com, endPos);
			//reached the waypoint, is it the last? stop, otherwise go to next one
			if(com.cell == com.path.EndPoint().Cell){
				if(endPos.distanceSquared(com.path.EndPoint().Position) < 0.000001f){
					com.position.set(com.path.EndPoint().Position);
					com.acc = 0f;
					com.speed = 0f;
					com.nextWayPoint = null;
					com.direction = Vector3f.ZERO;
					com.path = null;
					return true;
				}
			}
				
			com.position.set(endPos);
			index.update(com);
			return false;
		}
		return false;
	}

	
	//FIXME really ISpatialPointing, or PositioningComponents ?
	public ISpatialPointing[] getEntitiesAt(float x, float z, float distance){
//System.out.println("INDEX:-------------------");
//System.out.println(index.toString());
//System.out.println("INDEX:-------------------");
		return index.getObjectsInRange((int)x, (int)z, (int)distance);
	}
	
	public static float angleBetween(float current, float target){
		float delta = target - current;
		if(delta > FastMath.PI){
			delta = delta - FastMath.TWO_PI;
		} else {
			if (delta < -FastMath.PI){
				delta = FastMath.TWO_PI + delta;
			}
		}
		return delta;
	}
	
	public static void main(String[] args) {
		System.out.println(" c17 n259 "+(angleBetween(17f*FastMath.DEG_TO_RAD, 259f*FastMath.DEG_TO_RAD))*FastMath.RAD_TO_DEG);
		System.out.println(" c17 n25 "+(angleBetween(17f*FastMath.DEG_TO_RAD, 25f*FastMath.DEG_TO_RAD))*FastMath.RAD_TO_DEG);
		System.out.println(" c25 n17 "+(angleBetween(25f*FastMath.DEG_TO_RAD, 17f*FastMath.DEG_TO_RAD))*FastMath.RAD_TO_DEG);
		System.out.println(" c17 n191 "+(angleBetween(17f*FastMath.DEG_TO_RAD, 191f*FastMath.DEG_TO_RAD))*FastMath.RAD_TO_DEG);
		System.out.println(" c259 n17 "+(angleBetween(259f*FastMath.DEG_TO_RAD, 17f*FastMath.DEG_TO_RAD))*FastMath.RAD_TO_DEG);
	}
}
