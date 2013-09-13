package com.l2client.navigation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.jme3.math.Vector3f;
import com.l2client.app.Singleton;
import com.l2client.component.PositioningComponent;
import com.l2client.component.PositioningSystem;
import com.l2client.controller.area.IArea;
import com.l2client.controller.entity.ISpatialPointing;
import com.l2client.navigation.Path.WAYPOINT;



/**
 * manages entites and queries around them, runtime storage backend for the entity component system
 *
 */
public class EntityNavigationManager {
	
	private static Logger log = Logger.getLogger(EntityNavigationManager.class.getName());
	
	/**
	 * Optimized paths try to get as straight as possible paths, otherwise cell wall midpoints are used for paths
	 */
	public static boolean USE_OPTIMZED_PATH = false;//true;

	private static EntityNavigationManager singleton = null;
	
	private volatile Set<TiledNavMesh>meshes = new HashSet<TiledNavMesh>();
	
	private EntityNavigationManager() {
		singleton = this;
	}
	
	public static EntityNavigationManager get() {
		if(singleton != null)
			return singleton;
		else {
			new EntityNavigationManager();
			return singleton;
			
		}
	}

	/**
	 * returns the mesh of position p
	 * meshes are a part of a terrain tile and occupy the same area, or at least should
	 * the will in no case contain less (for overlapping sometimes the do contain more space
	 * As it is supposed to contain maximally 9 meshes this should be no problem 
	 * regarding computation time
	 * @param pos
	 * @return
	 */
	public NavMesh getMesh(Vector3f pos) {
		for(TiledNavMesh mesh : meshes)
			if(mesh.isPointInTile(pos.x,pos.z))
				return mesh;
		
		return null;
	}

	public void detachMesh(TiledNavMesh m) {
			meshes.remove(m);
	}

	public void attachMesh(TiledNavMesh m) {
			meshes.add(m);
			log.info("Added NavMesh at "+m.getPosition().x+"/"+m.getPosition().z);

		if(meshes.size()>26)//25 5x5 @see ITileManager and one more from a precache for teleport..
			log.warning("NavigationManager attachMesh has more meshes than expected ("+meshes.size()+"> 26) , is a remove leak present?");

		ISpatialPointing [] ents = Singleton.get().getPosSystem().getEntitiesAt(m.getPosition().x, m.getPosition().z, IArea.TERRAIN_SIZE_HALF);
		if(ents != null && ents.length>0) {
			log.info("Found "+ents.length+" entities within "+IArea.TERRAIN_SIZE_HALF+" radius of "+m.getPosition().x+"/"+m.getPosition().z+", will position them on navmesh");
			positionEntitiesOnMesh(ents, m);
		}
	}
	
	private void positionEntitiesOnMesh(ISpatialPointing[] ents,
			TiledNavMesh m) {
		PositioningComponent com = null;
		for(int i=0;i <ents.length;i++){
			com = (PositioningComponent) ents[i];
			snapToGround(com);
		}
		
	}

	public Cell FindClosestCell(Vector3f Point, boolean mustBeBorderCell) {
		TiledNavMesh na = getNavMesh(Point);
		Cell c = null;
		if(na != null){
			c = na.FindClosestCell(Point);
			if(c != null){
				if(mustBeBorderCell && !na.isBorderCell(c)){
					c = null;
					log.finest("NavigationManager FindClosestCell has no border cell for coordinates "+Point);
				} else {
					log.finest("NavigationManager FindClosestCell found borderCell:"+c);
					return c;
				}
			}
		} else
			log.warning("NavigationManager FindClosestCell has no NavigationMesh for coordinates "+Point);
		log.severe("NavigationManager FindClosestCell has no mesh for coordinates "+Point);
		return c;
	}

	public TiledNavMesh getNavMesh(Vector3f worldPos) {
		for(TiledNavMesh mesh : meshes)
			if(mesh.isPointInTile(worldPos.x,worldPos.z))
				return mesh;
		
		return null;
	}
	
	/**
	 * Juts for debug use
	 * @return
	 */
	public Collection<TiledNavMesh> getNavMeshes(){
		return meshes;
	}
	
	
	public void ResolveMotionOnMesh(PositioningComponent comp, Vector3f endPos) {
		if(comp.mesh.isBorderCell(comp.cell)) {
			if(comp.position.distanceSquared(endPos) < 0.00000001f){
				WAYPOINT c = null;
				if(USE_OPTIMZED_PATH)
					for(WAYPOINT p : comp.path.m_OptimalWaypointList){
						//one after current
						if(c != null){
							comp.cell = p.Cell;
							comp.mesh = (TiledNavMesh) p.mesh;
							comp.nextWayPoint = p;
							endPos.set(c.Position);
							comp.targetHeading = PositioningSystem.getHeading(comp.position, c.Position);
							return;
						}
						//find current
						if(p == comp.nextWayPoint)
							c = p;
					}
				else
					for(WAYPOINT p : comp.path.m_WaypointList){
						//one after current
						if(c != null){
							comp.cell = p.Cell;
							comp.mesh = (TiledNavMesh) p.mesh;
							comp.nextWayPoint = p;
							endPos.set(c.Position);
							comp.targetHeading = PositioningSystem.getHeading(comp.position, c.Position);
							return;
						}
						//find current
						if(p == comp.nextWayPoint)
							c = p;
					}
				//nothing here baaaaad
				return;
			}
		}
		comp.cell = comp.mesh.ResolveMotionOnMesh(comp.position, comp.cell, endPos);
	}
	
	public boolean buildNavigationPath(Path navPath, Vector3f startPos, Vector3f endPos) {

		if(endPos.distanceSquared(startPos)>(IArea.TERRAIN_SIZE*IArea.TERRAIN_SIZE)){
			log.warning("NO PATH - start and end points are further away than "+IArea.TERRAIN_SIZE);
			return false;
		}
		return buildNavigationPath(navPath, getNavMesh(startPos), startPos, getNavMesh(endPos), endPos);
	}

	private boolean buildNavigationPath(Path navPath, TiledNavMesh startMesh,
			Vector3f startPos, TiledNavMesh endMesh, Vector3f endPos) {
		//check params, as we can be fed from the outside
		if(navPath != null){
			if(startMesh!= null && endMesh != null){
				if(startMesh != endMesh){
					log.finer(" Building - path between borders ? "+startMesh+"-"+startPos+" to:"+endMesh+"-"+endPos);
					//check for neighboring path
					if(startMesh.isNeighbourOf(endMesh))
					{
						log.finer(" - path for neighbors :"+startMesh+"-"+startPos+" to:"+endMesh+"-"+endPos);
						navPath.WaypointList().clear();
						if(startMesh.buildNavigationPathToBorder(navPath, null, startPos, endPos)){
							Path endPath = new Path();
							if(USE_OPTIMZED_PATH)
								navPath.optimize();
							log.finer(" - path part 1 to border found :"+startPos+" to:"+endPos+" with crossing at:"+navPath.EndPoint().Position);
							//FIXME this one is direction dependant, so we have to invert search direction
							if(endMesh.buildNavigationPathToBorder(endPath, null, endPos, navPath.EndPoint().Position)){
								log.fine(" - path part 2 to border found :"+navPath.EndPoint().Position+" to:"+endPos);
								//do the crossing points match ?
								if(!(navPath.EndPoint().Position.distanceSquared(endPath.EndPoint().Position)< 0.0000001f)){
									log.finer("NO PATH - no path between border crosses:"+startMesh+"-"+navPath.EndPoint().Position+" to:"+endMesh+"-"+endPath.EndPoint().Position);
									navPath.WaypointList().clear();
									return false;
								}
								
								if(USE_OPTIMZED_PATH)
									endPath.optimize();
									
								//reverse addition needed because of direction dependency
								//add all, because the waypoint crossing will be in twice, but with different cells
								for(int i=endPath.m_WaypointList.size()-1;i>=0;i--)
									navPath.m_WaypointList.add(endPath.m_WaypointList.get(i));
								
								//same for optimized
								if(USE_OPTIMZED_PATH)
									for(int i=endPath.m_OptimalWaypointList.size()-1;i>=0;i--)
										navPath.m_OptimalWaypointList.add(endPath.m_OptimalWaypointList.get(i));
								
								navPath.EndPoint().Cell.MapVectorHeightToCell(navPath.EndPoint().Position);
								return true;
							} else {
								navPath.WaypointList().clear();
								log.finer("NO PATH - no path between borders:"+startMesh+"-"+startPos+" to:"+endMesh+"-"+endPos);
							}
						}log.finer("NO PATH - no path to border:"+startMesh+"-"+endPos);
					} else log.finer("NO PATH - Path for non neighbors requested:"+startMesh+" to:"+endMesh);
				} else {
					if(startMesh.buildNavigationPath(navPath, startPos, endPos)){
						if(USE_OPTIMZED_PATH)
							navPath.optimize();
						
						navPath.EndPoint().Cell.MapVectorHeightToCell(navPath.EndPoint().Position);
						return true;
					}else 
						return false;
				}
			} else 
				log.finer("NO PATH - start or end param missing:"+startMesh+" to:"+endMesh);
		} else 
			log.finer("NO PATH - Path param missing");
		return false;
	}
	
	@Override
	public String toString(){
		StringBuilder str = new StringBuilder(this.getClass().getSimpleName());
		str.append(" Meshes total:").append(meshes.size());
		for(TiledNavMesh n : meshes)
			str.append('\n').append(n.toString());
		
		return str.toString();
	}

	public int getMeshCount() {
		return meshes.size();
	}

	public void snapToGround(Vector3f p) {
		Cell c = FindClosestCell(p, false);
		if(c != null)
			c.MapVectorHeightToCell(p);
	}
	
	public void snapToGround(PositioningComponent com){
		int id = Singleton.get().getEntityManager().getEntityId(com);
		log.fine("Snap to Ground for "+id+" at "+com.position);
		Cell c = FindClosestCell(com.goalPos, false);
		if(c != null){
			c.MapVectorHeightToCell(com.goalPos);
			com.cell = c;
			com.teleport = true;//needed for real positioning on ground @see PositioningSystem.initTeleportTo()
			log.fine("Snap to Ground succeeded for"+id+" from "+com.position+ " to "+com.goalPos);
		}else
			log.fine("Snap to Ground FAILED for"+id+" around "+com.position);
	}

}
