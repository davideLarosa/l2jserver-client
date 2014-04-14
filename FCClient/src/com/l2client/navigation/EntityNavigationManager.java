package com.l2client.navigation;

import java.util.ArrayList;

import com.jme3.math.Vector3f;
import com.l2client.app.Singleton;
import com.l2client.component.PositioningComponent;
import com.l2client.component.PositioningSystem;
import com.l2client.controller.area.IArea;
import com.l2client.controller.entity.ISpatialPointing;
import com.l2client.navigation.Path.InternalWayPoint;
import com.l2client.navigation.Path.WayPoint;



/**
 * manages entity related navigation issues, mostly to do with component stuff do decouple this from the NavigationManager
 *
 */
public class EntityNavigationManager extends NavigationManager {
	
	public static NavigationManager get() {
		if(singleton == null){
			synchronized (NavigationManager.class) {
				if(singleton == null){
					singleton = new EntityNavigationManager();
				}
			}
		}
		return singleton;
	}

	protected EntityNavigationManager() {
	}
	
	public void detachMesh(TiledNavMesh m) {
		removeMesh(m);
	}

	public void attachMesh(TiledNavMesh m) {
		addMesh(m);
		
		if(meshes.size()>26)//25 5x5 @see ITileManager and one more from a precache for teleport..
			log.warning("NavigationManager attachMesh has more meshes than expected ("+meshes.size()+"> 26) , is a remove leak present?");

		ISpatialPointing [] ents = Singleton.get().getPosSystem().getEntitiesAt(m.getPosition().x, m.getPosition().z, IArea.TERRAIN_SIZE_HALF);
		if(ents != null && ents.length>0) {
			log.info("Found "+ents.length+" entities within "+IArea.TERRAIN_SIZE_HALF+" radius of "+m.getPosition().x+"/"+m.getPosition().z+", will position them on navmesh");
			positionEntitiesOnMesh(ents, m);
		} else
			log.info("No entities within "+IArea.TERRAIN_SIZE_HALF+" radius of "+m.getPosition().x+"/"+m.getPosition().z+", will position them on navmesh");

	}
	
	private void positionEntitiesOnMesh(ISpatialPointing[] ents, TiledNavMesh m) {
		PositioningComponent com = null;
		log.fine("Positioning Entities on Mesh after Load of NavMesh");
		for(int i=0;i <ents.length;i++){
			com = (PositioningComponent) ents[i];
			snapToGround(com);
		}
		
	}

	public void ResolveMotionOnMesh(PositioningComponent comp, Vector3f endPos) {
		if((getNavMesh(comp.mesh)).isBorderCell(comp.cell)) {
			if(comp.position.distanceSquared(endPos) < 0.000001f){
				WayPoint c = null;
				
				ArrayList<WayPoint> arr = comp.path.WaypointList();
				
				for(WayPoint p : arr){
					//one after current
					if(c != null){
						comp.cell = p.cell;
						if(comp.mesh != p.mesh)
							System.out.println("Opt crossing from "+comp.mesh+" to "+p.mesh);
						comp.mesh = p.mesh;
						comp.nextWayPoint = p;
						endPos.set(c.position);
						if(comp.position.distanceSquared(p.position) > 0.00001f) {
							comp.targetHeading = PositioningSystem.getHeading(comp.position, p.position);
							System.out.println("ResolveMotoionOnMesh new Heading "+comp.targetHeading+" for ent "+Singleton.get().getEntityManager().getEntityId(comp));
						}
						return;
					}
					//found current
					if(p == comp.nextWayPoint)
						c = p;
				}


				//nothing here baaaaad
				return;
			}
		}
//		comp.cell = comp.mesh.ResolveMotionOnMesh(comp.position, comp.cell, endPos);
		comp.cell = resolveMotionNewWay(comp.position, comp.cell, comp.mesh, endPos);
	}

	public void snapToGround(PositioningComponent com){
		int id = Singleton.get().getEntityManager().getEntityId(com);
		log.fine("Snap to Ground for "+id+" at "+com.position);
		Cell c = FindClosestCell(com.goalPos, false);
		if(c != null){
			c.MapVectorHeightToCell(com.goalPos);
			com.cell = c.id;
			com.teleport = true;//needed for real positioning on ground @see PositioningSystem.initTeleportTo()
			log.fine("Snap to Ground succeeded for"+id+" from "+com.position+ " to "+com.goalPos);
		}else
			log.fine("Snap to Ground FAILED for"+id+" around "+com.position);
	}

}
