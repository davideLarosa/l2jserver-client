package com.l2client.navigation;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.export.binary.BinaryImporter;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.l2client.navigation.Cell.ClassifyResult;

/**
 * NavigationManager working on TiledNavMeshes.
 */
public class NavigationManager {

	protected static Logger log = Logger.getLogger(NavigationManager.class.getName());
	/**
	 * Optimized paths try to get as straight as possible paths, otherwise cell wall midpoints are used for paths
	 */
	public static boolean USE_OPTIMZED_PATH = false;
	protected static NavigationManager singleton = null;
	protected volatile HashMap<Integer, TiledNavMesh> meshes = new HashMap<Integer, TiledNavMesh>(cacheSize);
	protected volatile ArrayList<Integer> lruCache = new ArrayList<Integer>(cacheSize);
	
	private static int cacheSize = 30;

	public static NavigationManager get() {
		if(singleton == null){
			synchronized (NavigationManager.class) {
				if(singleton == null){
					singleton = new NavigationManager();
				}
			}
		}
		return singleton;
	}

	protected NavigationManager() {
	}
	
	public void setCacheSize(int newSize){
		synchronized (meshes) {
			if(lruCache.size()> newSize)
				clear();
			cacheSize = newSize;
		}
	}
	
	private void clear(){
		meshes.clear();
		lruCache.clear();
	}
	
	TiledNavMesh checkCache(int tileId, TiledNavMesh m){
		//tile holen, schon drin?
		   //lrucache id raus und vorne rein
		//sonst
		   // tile laden
		   //lrucache nicht voll?
		      //id aufnemhen, tile in hash rein
		   //sonst
		      //letzte lru id holen, aus hash raus
		      //id aufnehmen tile in hash rein
        long dT =  System.currentTimeMillis();
		if(m == null){
			m = meshes.get(tileId);
			if(m == null)
				m = loadNavMesh(tileId);
			if(m == null)
				log.severe("Navmesh not found for tile"+tileId);
			else
				if(m.hashCode() != tileId)
					log.severe("hashcode of tile != tileid");
		}
		
		synchronized (meshes) {

			if(meshes.get(tileId) != null){
				//no need for meshes put already in
				lruCache.remove(Integer.valueOf(tileId));
				lruCache.add(tileId);
				log.info("Navmesh already in lru "+tileId);
			} else {
				if(lruCache.size() >= cacheSize){
					int removed = lruCache.remove(0);
					meshes.remove(removed);
					log.info("Cache full removed "+removed);
				}
				lruCache.add(tileId);
				log.info("Navmesh added to lru "+tileId);
			}
			meshes.put(tileId,m);
		}
		log.info("Lookup took "+(System.currentTimeMillis()-dT)+" milli seconds");
		return m;
	}
	
	TiledNavMesh loadNavMesh(int tileId){
		TiledNavMesh ret = null;
		tileId -= 100000000;
		int x = tileId/10000;
		int z = tileId%10000;
		if(x>0 && z > 0){
//			//From Tile.getTileFromWorldXPosition for L2JServer
//			x = (x>>8)+160;// +160 (20*8 tiles) because 0 in x is in tile 160 not in tile 0
//			z = (z>>8)+144; //+144 because 0 in z is in tile 144 not in tile 0
			InputStream is = NavigationManager.class.getClassLoader().getResourceAsStream("tile/"+x+"_"+z+"/nav.jnv");
			if(is!= null){
				try {
					//load tiled namvmesh
					ret = (TiledNavMesh) BinaryImporter.getInstance().load(is);
					//add it to the navmanager
					if(ret != null){
						//nav meshes are from 0/0 to tile.x/tile.y for conveniance so move it
						//nm.setPosition(new Vector3f(Tile.getWorldPositionOfXTile(x), 0f,Tile.getWorldPositionOfZTile(z)));
						ret.setPosition(new Vector3f((x-160)<<8, 0f, (z-144)<<8));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return ret;
	}

	public void removeMesh(TiledNavMesh m) {
		synchronized (meshes) {
			meshes.remove(m.hashCode());
			lruCache.remove(Integer.valueOf(m.hashCode()));
		}
		log.info("Removed NavMesh "+m.hashCode()+" at "+m.getPosition().x+"/"+m.getPosition().z);
	}
	
	public void addMesh(TiledNavMesh m) {
		checkCache(m.hashCode(), m);
		log.info("Added NavMesh "+m.hashCode()+" at "+m.getPosition().x+"/"+m.getPosition().z);
	}

	public Cell FindClosestCell(Vector3f point, boolean mustBeBorderCell) {
		TiledNavMesh na = getNavMesh(point.x, point.z);
		Cell c = null;
		if(na != null){
			c = na.FindClosestCell(point);
			if(c != null){
				if(mustBeBorderCell && !na.isBorderCell(c)){
					c = null;
					log.finest("NavigationManager FindClosestCell has no border cell for coordinates "+point);
				} else {
					log.finest("NavigationManager FindClosestCell found borderCell:"+c);
					return c;
				}
			} else
				log.finest("NavigationManager FindClosestCell has no Cell near:"+point);
		} else
			log.warning("NavigationManager FindClosestCell has no NavigationMesh for coordinates "+point);
		log.severe("NavigationManager FindClosestCell has no mesh for coordinates "+point);
		return c;
	}
	/**
	 * returns the mesh of position p
	 * meshes are a part of a terrain tile and occupy the same area, or at least should
	 * the will in no case contain less (for overlapping sometimes the do contain more space
	 * As it is supposed to contain maximally 9 meshes this should be no problem 
	 * regarding computation time
	 * @param x, z (groundplane) in worldcoordinates
	 * @return
	 */
	TiledNavMesh getNavMesh(float x, float z) {
//		for(TiledNavMesh mesh : meshes) {
//			if(mesh.isPointInTile(x, z)){
//				return mesh;
//			}
//		}
		return checkCache(TiledNavMesh.getHashCode(x, z), null);
	}
	
	TiledNavMesh getNavMesh(int meshId) {
		return checkCache(meshId, null);
	}

	/**
	 * Juts for debug use
	 * @return
	 */
	public TiledNavMesh[] getNavMeshes() {
		return meshes.values().toArray(new TiledNavMesh[meshes.size()]);
	}
	
	int resolveMotionNewWay(Vector3f startPos, int startCell, int startMesh, Vector3f endPos) {
		TiledNavMesh me = getNavMesh(startMesh);
		Cell ce = me.getCell(startCell);
		ce = resolveMotionNewWay(startPos, ce, endPos);
		if(ce != null)
			return ce.id;
		else
			return -1;
	}

	Cell resolveMotionNewWay(Vector3f StartPos, Cell StartCell, Vector3f EndPos) {
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
						Cell c = FindClosestCell(EndPos, true);
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
					Cell c = FindClosestCell(EndPos, true);
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

	/**
	 * Builds a path across meshes, or builds a path from start to end (direct route)
	 * The NavigationMesh is not responsible for mesh crossing. This is done in resolve motion on mesh and
	 * the path planning tool
	 * @param NavPath
	 * @param StartPos
	 * @param EndPos
	 * @return
	 */
	public boolean buildNavigationPath(Path navPath, Vector3f startPos, Vector3f endPos) {
        long dT =  System.currentTimeMillis();
		boolean ret = buildNavigationPath(navPath, getNavMesh(startPos.x, startPos.z), startPos, getNavMesh(endPos.x, endPos.z), endPos);
		log.info("path building took "+(System.currentTimeMillis()-dT)+" milli seconds");
		return ret;
	}
	
	boolean buildNavigationPath(Path navPath, TiledNavMesh startMesh,
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
						navPath.clear();
						if(startMesh.buildNavigationPathToBorder(navPath, null, startPos, endPos)){
							Path endPath = new Path();
							if(USE_OPTIMZED_PATH)
								navPath.optimize();

							log.finer(" - path part 1 to border found :"+startPos+" to:"+endPos+" with crossing at:"+navPath.internalEndPoint().Position);
							//FIXME this one is direction dependant, so we have to invert search direction
							if(endMesh.buildNavigationPathToBorder(endPath, null, endPos, navPath.internalEndPoint().Position)){
								log.fine(" - path part 2 to border found :"+navPath.internalEndPoint().Position+" to:"+endPos);
								//do the crossing points match ?
								if(!(navPath.internalEndPoint().Position.distanceSquared(endPath.internalEndPoint().Position)< 0.000001f)){
									log.finer("NO PATH - no path between border crosses:"+startMesh+"-"+navPath.internalEndPoint().Position+" to:"+endMesh+"-"+endPath.internalEndPoint().Position);
									navPath.clear();
									return false;
								}
								
//								log.finest("PRE OPT\n"+endPath.toString());
								if(USE_OPTIMZED_PATH)
									endPath.optimize();
								
//								log.finest("POST OPT\n"+endPath.toString());
								//reverse addition needed because of direction dependency
								//add all, because the waypoint crossing will be in twice, but with different cells
								navPath.copyReverseInReverseOrder(endPath);
//								for(int i=endPath.m_WaypointList.size()-1;i>=0;i--)
//									navPath.m_WaypointList.add(endPath.m_WaypointList.get(i));
								
//								//same for optimized
//								if(USE_OPTIMZED_PATH)
//									for(int i=endPath.m_OptimalWaypointList.size()-1;i>=0;i--)
//										navPath.m_OptimalWaypointList.add(endPath.m_OptimalWaypointList.get(i));
//								//TODO not needed, check and remove this
//								navPath.EndPoint().Cell.MapVectorHeightToCell(navPath.EndPoint().position);
//								log.finest("FINAL \n"+navPath.toString());
//								if(Level.FINEST.equals(log.getLevel())){
									System.out.println(navPath.toString());
//								}
								return true;
							} else {
								navPath.clear();
								log.finer("NO PATH - no path between borders:"+startMesh+"-"+startPos+" to:"+endMesh+"-"+endPos);
							}
						}log.finer("NO PATH - no path to border:"+startMesh+"-"+endPos);
					} else log.finer("NO PATH - Path for non neighbors requested:"+startMesh+" to:"+endMesh);
				} else {
					if(startMesh.buildNavigationPath(navPath, startPos, endPos)){
						if(USE_OPTIMZED_PATH)
							navPath.optimize();
//						//TODO not needed, check and remove this
//						navPath.EndPoint().Cell.MapVectorHeightToCell(navPath.EndPoint().position);
//						if(Level.FINEST.equals(log.getLevel())){
							log.finest(navPath.toString());
//						}
						return true;
					}else 
						return false;
				}
			} else 
				log.warning("NO PATH - start or end param missing:"+startMesh+" to:"+endMesh);
		} else 
			log.severe("NO PATH - Path param missing");
		return false;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder(this.getClass().getSimpleName());
		str.append(" Meshes total:").append(meshes.size());
		for(Integer n : meshes.keySet())
			str.append('\n').append("Mesh:"+n+" ").append(meshes.get(n));
		str.append('\n');
		for(Integer i : lruCache)
			str.append('\n').append("lru:").append(i);
		
		return str.toString();
	}

	public int getMeshCount() {
		return meshes.size();
	}

	public void snapToGround(Vector3f p) {
		log.fine("Snap to Ground around "+p);
		Cell c = FindClosestCell(p, false);
		if(c != null) {
			c.MapVectorHeightToCell(p);
			log.fine("Snap to Ground succeeded at "+p);
		} else
			log.fine("Snap to Ground FAILED around "+p);
	}


}