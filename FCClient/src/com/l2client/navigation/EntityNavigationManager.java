package com.l2client.navigation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.l2client.app.Singleton;
import com.l2client.component.PositioningComponent;
import com.l2client.component.PositioningSystem;
import com.l2client.controller.area.IArea;
import com.l2client.controller.entity.ISpatialPointing;
import com.l2client.navigation.Cell.ClassifyResult;
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
			if(comp.position.distanceSquared(endPos) < 0.000001f){
				WAYPOINT c = null;
				
				ArrayList<WAYPOINT> arr;
				if(USE_OPTIMZED_PATH)
					arr = comp.path.m_OptimalWaypointList;
				else
					arr = comp.path.m_WaypointList;
				
				for(WAYPOINT p : arr){
					//one after current
					if(c != null){
						comp.cell = p.Cell;
						if(comp.mesh != p.mesh)
							System.out.println("Opt crossing from "+comp.mesh+" to "+p.mesh);
						comp.mesh = (TiledNavMesh) p.mesh;
						comp.nextWayPoint = p;
						endPos.set(c.Position);
						if(comp.position.distanceSquared(c.Position) > 0.00001f)
							comp.targetHeading = PositioningSystem.getHeading(comp.position, c.Position);
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
		comp.cell = resolveMotionNewWay(comp.position, comp.cell, endPos);
	}
	
	private Cell resolveMotionNewWay(Vector3f StartPos, Cell StartCell,	Vector3f EndPos) {
			// create a 2D motion path from our Start and End positions, tossing out
			// their Y values to project them
			// down to the XZ plane.
			Line2D MotionPath = new Line2D(new Vector2f(StartPos.x, StartPos.z),
					new Vector2f(EndPos.x, EndPos.z));

			// these three will hold the results of our tests against the cell walls
			ClassifyResult Result = null;

			// TestCell is the cell we are currently examining.
			Cell TestCell = StartCell;

			Result = TestCell.ClassifyPathToCell(MotionPath);

			// if exiting the cell...
			if (Result.result == Cell.PATH_RESULT.EXITING_CELL) {
				// Set if we are moving to an adjacent cell or we have hit a
				// solid (unlinked) edge
				if (Result.cell != null) {
					// moving on. Set our motion origin to the point of
					// intersection with this cell
					// and continue, using the new cell as our test cell.
					MotionPath.SetEndPointA(Result.intersection);
					TestCell = Result.cell;
				} else {
					//FIXME this could also be the case of switching meshes :-< check this !!
					Cell c = Singleton.get().getNavManager().FindClosestCell(EndPos, true);
					if(c!= null && c != TestCell){
//System.out.println("Mesh switching");
							TestCell =c;
					}
					else{
						//FIXME thid should push the entity more away from a wall than it does at the moment and make it move more perpendicular to the wall
//System.out.println("Hitting a wall!");
						// we have hit a solid wall. Resolve the collision and
						// correct our path.
						MotionPath.SetEndPointA(Result.intersection);
						TestCell.ProjectPathOnCellWall(Result.side, MotionPath);
	
						// add some friction to the new MotionPath since we are
						// scraping against a wall.
						// we do this by reducing the magnatude of our motion by 10%
						Vector2f Direction = MotionPath.EndPointB().subtract(
								MotionPath.EndPointA()).mult(0.9f);
						// Direction.mult(0.9f);
						MotionPath.SetEndPointB(MotionPath.EndPointA().add(
								Direction));
					}
				}
			} else if (Result.result == Cell.PATH_RESULT.NO_RELATIONSHIP) {
//System.out.println("NO RELATION");
				//FIXME this could also be the case of optimized meshes
				Cell c =Singleton.get().getNavManager().FindClosestCell(EndPos, true);
				if(c!= null && c != TestCell){
						TestCell =c;
				} else {
				// Although theoretically we should never encounter this case,
				// we do sometimes find ourselves standing directly on a vertex
				// of the cell.
				// This can be viewed by some routines as being outside the
				// cell.
				// To accomodate this rare case, we can force our starting point
				// to be within
				// the current cell by nudging it back so we may continue.
				Vector2f NewOrigin = MotionPath.EndPointA();
				TestCell.ForcePointToCellCollumn(NewOrigin);
//					MotionPath.SetEndPointA(NewOrigin);
				//we do not want to iterate we just want them to stop at the wall and not cet out
				MotionPath.SetEndPointB(NewOrigin);
				}
			}

		// Update the new control point position,
		// solving for Y using the Plane member of the NavigationCell
		EndPos.x = MotionPath.EndPointB().x;
		EndPos.y = 0.0f;
		EndPos.z = MotionPath.EndPointB().y;
		TestCell.MapVectorHeightToCell(EndPos);

		return TestCell;
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
								
//								log.finest("PRE OPT\n"+endPath.toString());
								if(USE_OPTIMZED_PATH)
									endPath.optimize();
								
//								log.finest("POST OPT\n"+endPath.toString());
								//reverse addition needed because of direction dependency
								//add all, because the waypoint crossing will be in twice, but with different cells
								for(int i=endPath.m_WaypointList.size()-1;i>=0;i--)
									navPath.m_WaypointList.add(endPath.m_WaypointList.get(i));
								
								//same for optimized
								if(USE_OPTIMZED_PATH)
									for(int i=endPath.m_OptimalWaypointList.size()-1;i>=0;i--)
										navPath.m_OptimalWaypointList.add(endPath.m_OptimalWaypointList.get(i));
								
								navPath.EndPoint().Cell.MapVectorHeightToCell(navPath.EndPoint().Position);
//								log.finest("FINAL \n"+navPath.toString());
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
