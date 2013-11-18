package com.l2client.component;

import java.util.HashMap;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.l2client.app.Singleton;
import com.l2client.controller.entity.Entity;
import com.l2client.controller.entity.ISpatialPointing;
import com.l2client.controller.entity.SpatialPointIndex;
import com.l2client.model.l2j.ServerValues;
import com.l2client.navigation.Path;
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
	enum MoveType {
		move,
		stop,
		teleport
	};
	class QueuedMove {
		int objId;
		float cX, cY, cZ;
		float tX,tY,tZ;
		MoveType type;
		float heading;
	}

	//rotate 180 deg per sec
	private static final float ROTATION_PER_SEC = FastMath.PI;
	private static PositioningSystem inst = null;
	private SpatialPointIndex index = null;
	static float DEBUG_UPDATE_INTERVAL = 5f;//all 5 sec
	float debugUpdate = 0f;
	private HashMap<Integer, QueuedMove>queuedMoves = null;
	
	private PositioningSystem(){
		inst = this;
		index = new SpatialPointIndex(10);
		queuedMoves = new HashMap<Integer, PositioningSystem.QueuedMove>();
	}
	
	public static PositioningSystem get(){
		if(inst != null)
			return inst;
		
		new PositioningSystem();
		
		return inst;
	}

	/**
	 * Also have to update spatial index managed here
	 */
	@Override 
	public void addComponentForUpdate(Component c){
		super.addComponentForUpdate(c);
		if(c instanceof PositioningComponent) {
			PositioningComponent p = (PositioningComponent)c;
			index.put(p);
			log.fine("Positioning Entities on Mesh after Load of NavMesh");
			Singleton.get().getNavManager().snapToGround(p);
			synchronized (queuedMoves) {
				int id = Singleton.get().getEntityManager().getEntityId(c);
				if (queuedMoves.containsKey(id)) {
					log.severe("Processing queued move for entity id "+id);
					QueuedMove move = queuedMoves.remove(id);
					switch (move.type) {
					case move:
						initMoveTo(move.objId, move.tX, move.tY, move.tZ,
								move.cX, move.cY, move.cZ);
						break;
					case stop:
						initStopAt(move.objId, move.tX, move.tY, move.tZ,
								move.heading);
					case teleport:
						initTeleportTo(move.objId, move.tX, move.tY, move.tZ,
								move.heading);
					}
				}
			}
		}
	}
	
	/**
	 * Also have to update spatial index managed here
	 * FIXME check this is working correctly, what if we delete one which is currently updated, better queue for removal.
	 */
	@Override 
	public void removeComponentForUpdate(Component c){
		super.removeComponentForUpdate(c);
		if(c instanceof PositioningComponent) {
			index.remove((ISpatialPointing) c);
		}
	}

	@Override
	public void onUpdateOf(Component c, float tpf) {
		//FIXME more intelligent way to casting
		if(c instanceof PositioningComponent) {
			PositioningComponent pc = (PositioningComponent)c;
			if(move(pc, tpf)){
				index.update(pc);
			}
			rotate(pc, tpf);
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
			//FIXME can be null this is baaad
			Entity ent = i.getEntity();
			if(ent != null){
				Vector3f v = i.getEntity().getWorldTranslation();
				log.fine("ENTITY:"+i.getId()+" at:"+v+" -> ("+ServerValues.getServerString(v.x, v.y, v.z)+")");
//System.out.println("ENTITY:"+i.getId()+" at JME:"+v+" -> L2J:("+ServerValues.getServerCoord(v.x)+
//		","+ServerValues.getServerCoord(v.y)+","+ServerValues.getServerCoord(v.z)+")");
				  ISpatialPointing[] obs = index.getObjectsInRange((int)v.x, (int)v.z, 100);
				  for(ISpatialPointing s : obs){
					  log.fine(Singleton.get().getEntityManager().getEntityId((Component) s)+" at x:"+s.getX()+" z:"+s.getZ()+" size:"+s.getSize());
				  }
			}
		}
	}

//	/**
//	 * Movement based on straight line interpolation between two points
//	 * @param com
//	 * @param dt
//	 * @return
//	 */
//	private boolean move(SimplePositionComponent com, float dt) {
//		if(com.teleport){
//
//			com.currentPos.set(com.goalPos);
//			com.startPos.set(com.goalPos);
//			com.heading = com.targetHeading;
//			com.teleport = false;
//			if(Singleton.get().getEntityManager().isPlayerComponent(com)){
//				Singleton.get().getClientFacade().sendValidatePosition(com);
//				Singleton.get().getClientFacade().sendGamePacket(new Appearing());
//			}	
//			int id = Singleton.get().getEntityManager().getEntityId(com);
//			if (id > -1) {
//				EnvironmentComponent env = (EnvironmentComponent) Singleton.get().getEntityManager().getComponent(id, EnvironmentComponent.class);
//				if (env != null) {
//					env.movement = -1;
//					env.changed = true;
//					log.finer("Stop from teleport");
//				} else
//					log.severe("No EnvironmentComponent found with entity id "
//									+ id + ", perhaps just create one?");
//			} else
//				log.severe("No entity id found as owner of Component"
//								+ com);
//			
//			return true;
//		}
//		
//		if (!com.currentPos.equals(com.goalPos)) {
//			float cSpeed = com.running ? com.runSpeed : com.walkSpeed;
//			// target reached then remove self
//
//			Vector3f dist = com.goalPos.subtract(com.currentPos);
//			float len = dist.length();
//			//SIGNAL EARLY FOR A STOP BEFORE GOAL IS REACHED
//			if(len < 0.1f*cSpeed){
//				//inform Environment component we should stop, so animation etc. can also stop.
//				int id = Singleton.get().getEntityManager().getEntityId(com);
//				if (id > -1) {
//					EnvironmentComponent env = (EnvironmentComponent) Singleton.get().getEntityManager().getComponent(id, EnvironmentComponent.class);
//					if (env != null) {
//						env.movement = -1;
//						env.changed = true;
//						log.finer("Should stop now");
//					} else
//						log.severe("No EnvironmentComponent found with entity id "
//										+ id + ", perhaps just create one?");
//				} else
//					log.severe("No entity id found as owner of Component"
//									+ com);
//			}
//			if (len <= (dt * cSpeed)) {
//				// stop movement
//				com.currentPos.set(com.goalPos);
//				com.startPos.set(com.goalPos);
//				if(Singleton.get().getEntityManager().isPlayerComponent(com)){
//					Singleton.get().getClientFacade().sendValidatePosition(com);
//				}	
//				return true;
//			} else {
//				// move
//				// TODO the npcdata should be updated too
//				Vector3f dir = dist.normalizeLocal();
//				com.currentPos.addLocal(dir.multLocal(dt * cSpeed));
//				// TODO update spatial index with SimplePositioningComponents too..
//				return true;
//			}
//		}
//
//		return false;
//	}
	
	/**
	 * Rotate the heading towards targetHeading by @see PositioningSystem.ROTATION_PER_SEC
	 * @param com	The positioning component
	 * @param dt	The delta time 
	 * @return		true: we rotated, false: nothing to rotate
	 */
	private boolean rotate(PositioningComponent com, float dt) {
		if (com.heading != com.targetHeading) {

		//FIXME add Code for looking at next waypoint! 	
//			Vector3f v = pos.goalPos.subtract(pos.startPos).normalizeLocal();
//			float angle = -FastMath.atan2(v.x, v.z);//-PI, +PI
//			if(angle < 0)
//				angle+=(FastMath.TWO_PI);//shift to 0, 2PI range
//			
//			if(debug)
//				log.fine("Heading:"+pos.heading+" targetHeading:"+pos.targetHeading+" newTargetHeading:"+angle);
//			pos.targetHeading = angle;
			
			
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
				if (com.heading < 0f)
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
		if(com.teleport){

			com.position.set(com.goalPos);
			haltPosComponent(com);
			com.cell = null;
			com.mesh = null;
			com.teleport = false;
			Path pa = new Path();
			if(Singleton.get().getNavManager().buildNavigationPath(pa, com.position.add(0.01f, 0f, 0f), com.goalPos)){
				com.initByWayPoint(pa);//not really a waypoint but a point quite near our end point
			}
			if(Singleton.get().getEntityManager().isPlayerComponent(com)){
				Singleton.get().getClientFacade().sendValidatePosition(com);
				Singleton.get().getClientFacade().sendGamePacket(new Appearing());
			}	
			signalStopToEnv(com);
			
			return true;
		}
		
		//are we moving on a navmesh??
		if(com.nextWayPoint != null){
			com.lastPosition.set(com.position);
			//compute direction from current location to target
			com.direction = com.nextWayPoint.Position.subtract(com.position);
			//currently no acc/dcc only linear time
			com.speed = com.maxSpeed*dt;
			
			Vector3f endPos;
			if(com.speed*com.speed > com.direction.lengthSquared())
				endPos = com.nextWayPoint.Position.clone();
			else
				endPos = com.position.add(com.direction.normalize().multLocal(com.speed));
			
			//now try to walk it
			Singleton.get().getNavManager().ResolveMotionOnMesh(com, endPos);
			//targetheading done inside ResolveMotionOnMesh as waypoints are swithed accordingly
				
			com.position.set(endPos);
			
			//reached the waypoint, is it the last? stop, otherwise go to next one
			if(com.cell == com.path.EndPoint().Cell){
				if(endPos.distanceSquared(com.path.EndPoint().Position) < 0.000001f){
					com.position.set(com.path.EndPoint().Position);
					signalStopToEnv(com);
					haltPosComponent(com);
					com.path = null;
					return true;
				}
			}

			return true;
		}else {
			//the case when no waypoint is given, no cell and no mesh is given but we have a goal != pos as a fallback currently for areas with no tiles/navmeshes..
			if (com.mesh == null && com.cell ==null && !com.position.equals(com.goalPos)) {
				// target reached then remove self
				com.direction = com.goalPos.subtract(com.position);
				float len = com.direction.lengthSquared();
				//SIGNAL EARLY FOR A STOP BEFORE GOAL IS REACHED
				if(len < 0.1f*com.maxSpeed*com.maxSpeed){
					signalStopToEnv(com);
				}
				if (len <= (dt * com.maxSpeed)) {
					// stop movement
					com.position.set(com.goalPos);
					com.startPos.set(com.goalPos);
					if(Singleton.get().getEntityManager().isPlayerComponent(com)){
						Singleton.get().getClientFacade().sendValidatePosition(com);
					}	
					return true;
				} else {
					// move
					// TODO the npcdata should be updated too
					com.position.addLocal(com.direction.normalize().multLocal(dt * com.maxSpeed));
					return true;
				}
			}			
		}
		return false;
	}

	/**
	 * @param com
	 */
	void signalStopToEnv(PositioningComponent com) {
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
	 * compute heading from start point to end point (use only x, z component)
	 * @param from		start position
	 * @param to		target position
	 * @return			a float in radians representing the desired heading
	 */
	public static float getHeading(Vector3f from, Vector3f to){
		float heading = FastMath.atan2(to.z - from.z, to.x - from.x);//in radians -PI to PI
		heading -= FastMath.HALF_PI;//this is special to jme...
		if (heading < 0)
		{
			heading += FastMath.TWO_PI;
		}
		return heading;
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
		
		PositioningComponent pos = (PositioningComponent) Singleton.get().getEntityManager().getComponent(objId, PositioningComponent.class);
		if (pos != null){
			boolean debug = Singleton.get().getEntityManager().isPlayerComponent(pos);	
			if(debug)
				log.fine("trigger move of " + objId + " from " + cX + "," + cY
						+ "," + cZ + " to " + tX + "," + tY + "," + tZ);
	
			synchronized (pos) {	
					pos.startPos.set(cX,cY,cZ);
					if(pos.position.distance(pos.startPos)> .9f){
						if(debug)
						log.warning("WARP !! move of " + objId + " from " + cX + "," + cY
								+ "," + cZ + " to " + tX + "," + tY + "," + tZ +" but current position is "+pos.position+" goal is "+pos.goalPos+" speed "+(pos.running?pos.runSpeed:pos.walkSpeed));
					}
					pos.goalPos.set(tX,tY,tZ);
					//FIXME this just shifts the position, not good... rework
					pos.position.set(pos.startPos);	
					Path pa = new Path();
					if(Singleton.get().getNavManager().buildNavigationPath(pa, pos.position, pos.goalPos)){
						pos.initByWayPoint(pa);
					} else {
						log.severe("Failed to place entity start:"+pos.position+" goal:"+pos.goalPos);
					}
					if(pos.position.distanceSquared(pos.goalPos) > 0.00001f)//only do this if the two are different, otherwise we get odd results
						pos.targetHeading = PositioningSystem.getHeading(pos.position, pos.goalPos);
					pos.maxSpeed = pos.running ? pos.runSpeed : pos.walkSpeed;
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
			log.severe("No SimplePositioningComonent found with entity id "+objId+", move is queued");
			synchronized (queuedMoves) {
				QueuedMove move = queuedMoves.get(objId);
				if (move == null) {
					move = new QueuedMove();
					move.objId = objId;
					move.tX = tX;
					move.tY = tY;
					move.tZ = tZ;
					move.cX = cX;
					move.cY = cY;
					move.cZ = cZ;
					move.type = MoveType.move;
				}
				queuedMoves.put(objId, move);
			}
		}
	}
	
	/**
	 * 
	 * @param objId	l2j object id to be teleportd
	 * @param tX	target x position in client coords
	 * @param tY	target y position in client coords
	 * @param tZ	target z position in client coords
	 * @param heading	the new heading
	 */
	public void initTeleportTo(int objId, float tX, float tY, float tZ, float heading) {		
		
		PositioningComponent pos = (PositioningComponent) Singleton.get().getEntityManager().getComponent(objId, PositioningComponent.class);
		if (pos != null){
			boolean debug = Singleton.get().getEntityManager().isPlayerComponent(pos);	
			if(debug)
				log.info("trigger teleport of " + objId + " from " + pos.position + " to " + + tX + "," + tY + "," + tZ);
			
			synchronized (pos) {	
				pos.teleport = true;
				pos.goalPos.set(tX,tY,tZ);
				//rest done on move
//				haltPosComponent(pos);
//				pos.nextWayPoint = null;
//				pos.path = null;
//				pos.cell = null;
//				pos.mesh = null;
				if(debug)
					log.fine("Heading:"+pos.heading+" targetHeading:"+pos.targetHeading+" newTargetHeading:"+heading);
				pos.targetHeading = heading;
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
			log.severe("No SimplePositioningComonent found with entity id "+objId+", teleport queued");
			synchronized (queuedMoves) {
				QueuedMove move = queuedMoves.get(objId);
				if (move == null) {
					move = new QueuedMove();
					move.objId = objId;
					move.tX = tX;
					move.tY = tY;
					move.tZ = tZ;
					move.heading = heading;
					move.type = MoveType.teleport;
				}
				queuedMoves.put(objId, move);
			}
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
	
	public void haltPosComponent(PositioningComponent com){
		com.goalPos.set(com.position);
		com.startPos.set(com.position);
		com.acc = 0f;
		com.speed = 0f;
		com.nextWayPoint = null;
		com.direction = Vector3f.ZERO;
		com.path = null;		
System.out.println("PosSystem.haltPosComp: heading"+com.heading+" set to "+com.targetHeading+" for ent "+Singleton.get().getEntityManager().getEntityId(com));
//		com.heading = com.targetHeading;
	}

	public void setNewGoalForPosComponent(Vector3f tPos, PositioningComponent com) {
		com.goalPos.set(tPos);
		com.path = null;
		com.nextWayPoint = null;
		Path pa = new Path();
		if(Singleton.get().getNavManager().buildNavigationPath(pa, com.position, com.goalPos)){
			com.initByWayPoint(pa);
		}	
	}

	public void initStopAt(int objId, float tX, float tY, float tZ,
			float heading) {
		Vector3f tPos = new Vector3f(tX, tY, tZ);
		PositioningComponent pos = (PositioningComponent) Singleton.get().getEntityManager().getComponent(objId, PositioningComponent.class);
		if (pos != null){
				log.finest("trigger STOP of " + objId + " at " + tPos.x + "," + tPos.y
							+ "," + tPos.z + " heading " + heading);
		
				synchronized (pos) {						
						if(pos.position.distance(pos.startPos)> .9f){
							log.warning("WARP (dist greater than 0.9) !! stop of " + objId + " at " + pos.startPos +" but current position is "+pos.position+" goal is "+pos.goalPos+" speed "+(pos.running?pos.runSpeed:pos.walkSpeed));
							//TODO can this be replaced by a initMoveTo?
							Singleton.get().getPosSystem().setNewGoalForPosComponent(tPos, pos);
						} else {
							pos.position.set(tPos);
							Singleton.get().getPosSystem().haltPosComponent(pos);
						}
						pos.targetHeading = heading;
						log.finest("New Heading "+pos.targetHeading+" received for Ent "+objId);						
				}
				signalStopToEnv(pos);
		}
		else {
			log.severe("No SimplePositioningComonent found with entity id "+objId+" stop queued");
			synchronized (queuedMoves) {
				QueuedMove move = queuedMoves.get(objId);
				if (move == null) {
					move = new QueuedMove();
					move.objId = objId;
					move.tX = tX;
					move.tY = tY;
					move.tZ = tZ;
					move.heading = heading;
					move.type = MoveType.stop;
				}
				queuedMoves.put(objId, move);
			}
		}
	}
	
}
