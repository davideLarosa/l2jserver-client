package com.l2client.component;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.l2client.app.Singleton;
import com.l2client.controller.entity.Entity;
import com.l2client.controller.entity.ISpatialPointing;
import com.l2client.controller.entity.SpatialPointIndex;
import com.l2client.model.l2j.ServerCoordinates;
import com.l2client.model.l2j.ServerValues;
import com.l2client.network.game.ClientPackets.Appearing;

/**
 * Singleton component system responsible for 
 * - updating positions of movers
 * - rotating positions when moving towards heading
 * - keeping a spatial index of positioned objects
 * 
 * for general working of a component system have a look at @see ComponentSystem
 */
public class PositioningSystem extends ComponentSystem {

	//rotate 180 deg per sec
	private static final float ROTATION_PER_SEC = FastMath.PI;
	private static PositioningSystem inst = null;
	private SpatialPointIndex index = null;
	static float DEBUG_UPDATE_INTERVAL = 5f;//all 5 sec
	float debugUpdate = 0f;
	
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
		} else if(c instanceof LoggingComponent){
			LoggingComponent l = (LoggingComponent)c;
			onUpdateDebug(l, tpf);
		}
	}
	
	private void onUpdateDebug(LoggingComponent l, float tpf) {
		l.lastUpdate+=tpf;
		if(l.lastUpdate > l.debugTime){
			l.lastUpdate = 0f;
			IdentityComponent i = Singleton.get().getEntityManager().getEntity(l);
			Entity ent = i.getEntity();
			if(ent != null){
			Vector3f v = i.getEntity().getWorldTranslation();
			log.fine("ENTITY:"+i.getId()+" at:"+v+" -> ("+ServerValues.getServerCoord(v.x)+
					","+ServerValues.getServerCoord(v.y)+","+ServerValues.getServerCoord(v.z)+")");
			}
		}
	}

	/**
	 * Movement based on straight line interpolation between two points
	 * @param com
	 * @param dt
	 * @return
	 */
	private boolean move(SimplePositionComponent com, float dt) {
		if(com.teleport){
			com.currentPos.set(com.goalPos);
			com.startPos.set(com.goalPos);
			com.heading = com.targetHeading;
			com.teleport = false;
			if(Singleton.get().getEntityManager().isPlayerComponent(com)){
				Singleton.get().getClientFacade().sendValidatePosition(com);
				Singleton.get().getClientFacade().sendGamePacket(new Appearing());
			}	
			int id = Singleton.get().getEntityManager().getEntityId(com);
			if (id > -1) {
				EnvironmentComponent env = (EnvironmentComponent) Singleton.get().getEntityManager().getComponent(id, EnvironmentComponent.class);
				if (env != null) {
					env.movement = -1;
					env.changed = true;
					log.finer("Stop from teleport");
				} else
					log.severe("No EnvironmentComponent found with entity id "
									+ id + ", perhaps just create one?");
			} else
				log.severe("No entity id found as owner of Component"
								+ com);
			
			return true;
		}
		
		if (!com.currentPos.equals(com.goalPos)) {
			float cSpeed = com.running ? com.runSpeed : com.walkSpeed;
			// target reached then remove self

			Vector3f dist = com.goalPos.subtract(com.currentPos);
			float len = dist.length();
			//SIGNAL EARLY FOR A STOP BEFORE GOAL IS REACHED
			if(len < 0.1f*cSpeed){
				//inform Environment component we should stop, so animation etc. can also stop.
				int id = Singleton.get().getEntityManager().getEntityId(com);
				if (id > -1) {
					EnvironmentComponent env = (EnvironmentComponent) Singleton.get().getEntityManager().getComponent(id, EnvironmentComponent.class);
					if (env != null) {
						env.movement = -1;
						env.changed = true;
						log.finer("Should stop now");
					} else
						log.severe("No EnvironmentComponent found with entity id "
										+ id + ", perhaps just create one?");
				} else
					log.severe("No entity id found as owner of Component"
									+ com);
			}
			if (len <= (dt * cSpeed)) {
				// stop movement
				com.currentPos.set(com.goalPos);
				com.startPos.set(com.goalPos);
				if(Singleton.get().getEntityManager().isPlayerComponent(com)){
					Singleton.get().getClientFacade().sendValidatePosition(com);
				}	
				return true;
			} else {
				// move
				// TODO the npcdata should be updated too
				Vector3f dir = dist.normalizeLocal();
				com.currentPos.addLocal(dir.multLocal(dt * cSpeed));
				// TODO update spatial index with SimplePositioningComponents too..
//				index.update(com);
				return true;
			}
		}

		return false;
	}
	
	/**
	 * Rotate the heading towards targetHeading by @see PositioningSystem.ROTATION_PER_SEC
	 * @param com	The positioning component
	 * @param dt	The delta time 
	 * @return		true: we rotated, false: nothing to rotate
	 */
	private boolean rotate(SimplePositionComponent com, float dt) {
		if (com.heading != com.targetHeading) {
//			boolean debug = Singleton.get().getEntityManager().isPlayerComponent(com);
//			if(debug)
//				log.fine("Heading:"+com.heading+" target:"+com.targetHeading);

			float angle = angleBetween(com.heading, com.targetHeading);
			// difference is approx. 3°
			if (FastMath.abs(angle) > 0.05f) {			
				if (angle < 0f) {// rotate left
					com.heading -= (ROTATION_PER_SEC * dt);
				} else {// rotate right
					com.heading += (ROTATION_PER_SEC * dt);
				}
//				if(debug)
//					log.fine("Heading:"+com.heading+" target:"+com.targetHeading+" angle:"+angle);
				if (com.heading > FastMath.TWO_PI)
					com.heading -= FastMath.TWO_PI;
				if (com.heading < -FastMath.TWO_PI)
					com.heading += FastMath.TWO_PI;
//				if(debug)
//					log.fine("Heading:"+com.heading+" target:"+com.targetHeading+" angle:"+angle);
			} else
				com.heading = com.targetHeading;
			
//			if(debug)
//				log.fine("Heading:"+com.heading+" target:"+com.targetHeading);
			return true;

		} else
			return false;
	}
	

	/**
	 * Movement of a PositioningComponent, e.g. movement based on a navmesh
	 * @param com
	 * @param dt
	 * @return
	 */
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
			Singleton.get().getNavManager().ResolveMotionOnMesh(com, endPos);
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
	
	/**
	 * 
	 * @param objId	l2j object id to be moved
	 * @param tX	target x position in client coords
	 * @param tY	target y position in client coords
	 * @param tZ	target z position in client coords
	 * @param cX	current x position in client coords
	 * @param cY	current y position in client coords
 	 * @param cZ	current z position in client coords
	 */
	public void initMoveTo(int objId, float tX, float tY, float tZ, float cX,
			float cY, float cZ) {
		
		SimplePositionComponent pos = (SimplePositionComponent) Singleton.get().getEntityManager().getComponent(objId, SimplePositionComponent.class);
		if (pos != null){
			boolean debug = Singleton.get().getEntityManager().isPlayerComponent(pos);	
			if(debug)
				log.fine("trigger move of " + objId + " from " + cX + "," + cY
						+ "," + cZ + " to " + tX + "," + tY + "," + tZ);
	
			synchronized (pos) {	
					pos.startPos.set(cX,0,cZ);
					if(pos.currentPos.distance(pos.startPos)> .9f){
						if(debug)
						log.warning("WARP !! move of " + objId + " from " + cX + "," + cY
								+ "," + cZ + " to " + tX + "," + tY + "," + tZ +" but current position is "+pos.currentPos+" goal is "+pos.goalPos+" speed "+(pos.running?pos.runSpeed:pos.walkSpeed));
					}
					pos.goalPos.set(tX,0,tZ);
					pos.currentPos.set(pos.startPos);	
					Vector3f v = pos.goalPos.subtract(pos.startPos).normalizeLocal();
					float angle = -FastMath.atan2(v.x, v.z);//-PI, +PI
					if(angle < 0)
						angle+=(FastMath.TWO_PI);//shift to 0, 2PI range
					
					if(debug)
						log.fine("Heading:"+pos.heading+" targetHeading:"+pos.targetHeading+" newTargetHeading:"+angle);
					pos.targetHeading = angle;
			}
			EnvironmentComponent env = (EnvironmentComponent) Singleton.get().getEntityManager().getComponent(objId, EnvironmentComponent.class);
			if (env != null){
				if(pos.running)
					env.movement = 1;
				else
					env.movement = 0;
				env.changed = true;
			}else {
				Singleton.get().getEntityManager().dumpComponents(objId);
				log.severe("No EnvironmentComponent found with entity id "+objId+", perhaps just create one?");
			}

		}
		else {
			Singleton.get().getEntityManager().dumpComponents(objId);
			log.severe("No SimplePositioningComonent found with entity id "+objId+", perhaps just create one?");
		}
	}
	
	public static void main(String[] args) {
		System.out.println(" c17 n259 "+(angleBetween(17f*FastMath.DEG_TO_RAD, 259f*FastMath.DEG_TO_RAD))*FastMath.RAD_TO_DEG);
		System.out.println(" c17 n25 "+(angleBetween(17f*FastMath.DEG_TO_RAD, 25f*FastMath.DEG_TO_RAD))*FastMath.RAD_TO_DEG);
		System.out.println(" c25 n17 "+(angleBetween(25f*FastMath.DEG_TO_RAD, 17f*FastMath.DEG_TO_RAD))*FastMath.RAD_TO_DEG);
		System.out.println(" c17 n191 "+(angleBetween(17f*FastMath.DEG_TO_RAD, 191f*FastMath.DEG_TO_RAD))*FastMath.RAD_TO_DEG);
		System.out.println(" c259 n17 "+(angleBetween(259f*FastMath.DEG_TO_RAD, 17f*FastMath.DEG_TO_RAD))*FastMath.RAD_TO_DEG);
		System.out.println(" c303 n115 "+(angleBetween(303f*FastMath.DEG_TO_RAD, 115f*FastMath.DEG_TO_RAD))*FastMath.RAD_TO_DEG);
		System.out.println(" c115 n303 "+(angleBetween(115f*FastMath.DEG_TO_RAD, 303f*FastMath.DEG_TO_RAD))*FastMath.RAD_TO_DEG);
		System.out.println(" c17 n-17 "+(angleBetween(17f*FastMath.DEG_TO_RAD, -17f*FastMath.DEG_TO_RAD))*FastMath.RAD_TO_DEG);
		System.out.println(" c370 n-17 "+(angleBetween(370f*FastMath.DEG_TO_RAD, -17f*FastMath.DEG_TO_RAD))*FastMath.RAD_TO_DEG);
		System.out.println(" c-17 n370 "+(angleBetween(-17f*FastMath.DEG_TO_RAD, 370f*FastMath.DEG_TO_RAD))*FastMath.RAD_TO_DEG);
		System.out.println(" c350 n-17 "+(angleBetween(350f*FastMath.DEG_TO_RAD, -17f*FastMath.DEG_TO_RAD))*FastMath.RAD_TO_DEG);
		System.out.println(" c-17 n350 "+(angleBetween(-17f*FastMath.DEG_TO_RAD, 350f*FastMath.DEG_TO_RAD))*FastMath.RAD_TO_DEG);
	}
	
}
