package com.l2client.navigation;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.mesh.IndexBuffer;
import com.jme3.util.BufferUtils;
import com.l2client.controller.area.IArea;
import com.l2client.navigation.Line2D.LINE_CLASSIFICATION;

/**
 * A navigation.Mesh wrapper containing the nav mesh in world coordinates
 * 
 * @author tmi
 * 
 */
public class NavigationMesh extends Mesh {

	private static final long serialVersionUID = 1L;
	//BOUNDS hit flag for bitwise flagging on more than one bounds hit
	private static final int BOUNDS_LEFT = 1;
	private static final int BOUNDS_RIGHT = 2;
	private static final int BOUNDS_TOP = 4;
	private static final int BOUNDS_BOTTOM = 8;
	private static final int BOUNDS_TOP_LEFT = 5;
	private static final int BOUNDS_TOP_RIGHT = 6;
	private static final int BOUNDS_BOTTOM_LEFT = 9;
	private static final int BOUNDS_BOTTOM_RIGHT = 10;
	//BORDER slots 8 tiles clockwise starting from top left
	private static final int BORDER_TOP_LEFT = 0;
	private static final int BORDER_TOP = 1;
	private static final int BORDER_TOP_RIGHT = 2;
	private static final int BORDER_RIGHT = 3;
	private static final int BORDER_BOTTOM_RIGHT = 4;
	private static final int BORDER_BOTTOM = 5;
	private static final int BORDER_BOTTOM_LEFT = 6;
	private static final int BORDER_LEFT = 7;

	private Vector3f worldTranslation = Vector3f.ZERO;

	private Line2D top = null;
	private Line2D right = null;
	private Line2D bottom = null;
	private Line2D left = null;

	//A map of cell - borderflag entries
	HashMap<Cell, Integer> borders = new HashMap<Cell, Integer>();
	//FIXME initialization
	private ArrayList<HashSet<Cell>> allBorders = new ArrayList<HashSet<Cell>>(8);
	
	public NavigationMesh(){
		for(int i=0;i<8;i++)
			allBorders.add(new HashSet<Cell>());	
	}

	public void write(JmeExporter e) throws IOException {

		super.write(e);
		OutputCapsule capsule = e.getCapsule(this);
		capsule.write(top, "top", null);
		capsule.write(right, "right", null);
		capsule.write(bottom, "bottom", null);
		capsule.write(left, "left", null);
		capsule.write(worldTranslation, "worldpos", null);
		
		//FIXME ev. override ? and do it only once?
		HashMap<Cell, Integer> tmp = new HashMap<Cell, Integer>();
		for(int i =0; i<m_CellArray.size(); i++)
			tmp.put(m_CellArray.get(i), i);
		
		int r=0;
		int[] vals = new int[borders.keySet().size()];
		for(Cell i : borders.keySet())
			vals[r++]=tmp.get(i);
		capsule.write(vals, "borders_keys",null);
		r=0;
		for(Integer i : borders.values())
			vals[r++]=i;
		capsule.write(vals, "borders_values",null);
			
		for(int i=0;i<8;i++){
			vals = new int[allBorders.get(i).size()];
			r = 0;
			for(Cell c : allBorders.get(i))
				vals[r++]=tmp.get(c);
			capsule.write(vals, "allborders"+i,null);
		}
	}

	public void read(JmeImporter e) throws IOException {
		super.read(e);
		InputCapsule capsule = e.getCapsule(this);
		top = (Line2D) capsule.readSavable("top", null);
		right = (Line2D) capsule.readSavable("right", null);
		bottom = (Line2D) capsule.readSavable("bottom", null);
		left = (Line2D) capsule.readSavable("left", null);
		worldTranslation = (Vector3f) capsule.readSavable("worldpos", null);
		//FIXME loading and storing of border information
		int[] bKeys = capsule.readIntArray("borders_keys",null);
		int[] vKeys = capsule.readIntArray("borders_values",null);

		for(int i=0; i<bKeys.length;i++)
			borders.put(m_CellArray.get(i), new Integer(vKeys[i]));
		
		for(int i=0;i<8;i++){
			int[] b = capsule.readIntArray("allborders"+i,null);
			for(int c :b)
				allBorders.get(i).add(m_CellArray.get(c));
		}
	}

	/**
	 * Creates the NavMesh for the vertices of the passed TriMesh, by also considering world translation on creation
	 * @param tri
	 */
	//TODO testcase needed for translated trimesh
	public void loadFromGeom(Geometry geom) {
		com.jme3.scene.Mesh tri = geom.getMesh();
		loadFromMesh(tri, geom.getWorldTranslation());
	}
	
	/**
	 * call before attaching or anything
	 * @param positions
	 * @param indices
	 * @param worldtrans
	 */
    public void loadFromData(Vector3f[] positions, short[][] indices, Vector3f worldtrans){

		this.worldTranslation = worldtrans;
			
		clearBorderPoints();
		createBounds();
		
        Plane up = new Plane();
        up.setPlanePoints(Vector3f.UNIT_X, Vector3f.ZERO, Vector3f.UNIT_Z);
        up.getNormal();

        Vector3f vertA = null;
        Vector3f vertB = null;
        Vector3f vertC = null;
        Cell c = null;
        for (int i = 0; i < indices.length; i++) {
            vertA = positions[indices[i][0]];
            vertB = positions[indices[i][1]];
            vertC = positions[indices[i][2]];
            
            Plane p = new Plane();
            p.setPlanePoints(vertA, vertB, vertC);
            if (up.pseudoDistance(p.getNormal()) <= 0.0f) {
                System.out.println("Warning, normal of the plane faces downward!!!");
                continue;
            }
            
            borderCorrection(vertA);
            borderCorrection(vertB);
            borderCorrection(vertC);
            
            vertA = vertA.add(worldtrans);
            vertB = vertB.add(worldtrans);
            vertC = vertC.add(worldtrans);

            c = AddCell(vertA, vertB, vertC);
            storeBorderCell(c);
        }

        LinkCells();
    }
	
    public void loadFromMesh(com.jme3.scene.Mesh mesh, Vector3f worldtrans) {
    	worldTranslation = worldtrans;
		
		clearBorderPoints();
		createBounds();

        Plane up = new Plane();
        up.setPlanePoints(Vector3f.UNIT_X, Vector3f.ZERO, Vector3f.UNIT_Z);
        up.getNormal();

        IndexBuffer ib = mesh.getIndexBuffer();
        FloatBuffer pb = mesh.getFloatBuffer(Type.Position);
        pb.clear();
        for (int i = 0; i < mesh.getTriangleCount()*3; i+=3){
            int i1 = ib.get(i+0);
            int i2 = ib.get(i+1);
            int i3 = ib.get(i+2);
            Vector3f a = new Vector3f();
            Vector3f b = new Vector3f();
            Vector3f c = new Vector3f();
            BufferUtils.populateFromBuffer(a, pb, i1);
            BufferUtils.populateFromBuffer(b, pb, i2);
            BufferUtils.populateFromBuffer(c, pb, i3);
            
            borderCorrection(a);
            borderCorrection(b);
            borderCorrection(c);
            
            a=a.add(worldtrans);
            b=b.add(worldtrans);
            c=c.add(worldtrans);
            


            Plane p = new Plane();
            p.setPlanePoints(a, b, c);
            if (up.pseudoDistance(p.getNormal()) <= 0.0f) {
                System.out.println("Warning, normal of the plane faces downward!!!");
                continue;
            }

            storeBorderCell(AddCell(a, b, c));
        }

        LinkCells();
    }
	

	/**
	 * Vertices being near the border, currently 0.001f away will be clamped to the 
	 * border. Input values are expected to be in local coordinates (before translating to the final world position)
	 * @param v vertex who's values should be checked and corrected, the values can change
	 */
	private void borderCorrection(Vector3f v) {
		float borderDelta = 0.001f;// if within this range to a border it will
									// be clamped to the border
		v.x = clampToBorder(v.x, borderDelta);
		v.y = clampToBorder(v.y, borderDelta);
		v.z = clampToBorder(v.z, borderDelta);
	}

	/**
	 * clamp a value to the border if it is within the specified delta away from the border side
	 * @param x value whcih possibly is near a border, in local coordinates
	 * @param borderDelta
	 * @return
	 */
	private float clampToBorder(float x, float borderDelta) {
		if (FastMath.abs(x - IArea.TERRAIN_SIZE_HALF) <= borderDelta)
			return IArea.TERRAIN_SIZE_HALF;
		if (FastMath.abs(x + IArea.TERRAIN_SIZE_HALF) <= borderDelta)
			return -IArea.TERRAIN_SIZE_HALF;
		return x;
	}

	/**
	 * when bounds for top is set we initialize the border point arrays otherwise we 
	 * clear any and set them to null
	 */
	private void clearBorderPoints() {
		//any bounds set at all?
		borders.clear();
		for(HashSet<Cell> set  : allBorders)
			set.clear();
	}
	
	/**
	 * Calculates the border lines. Requirement: worldtranslation is set.
	 */
	private void createBounds(){
		top = new Line2D(
				new Vector2f(worldTranslation.x-IArea.TERRAIN_SIZE_HALF, worldTranslation.z-IArea.TERRAIN_SIZE_HALF), 
				new Vector2f(worldTranslation.x+IArea.TERRAIN_SIZE_HALF, worldTranslation.z-IArea.TERRAIN_SIZE_HALF) 
		);
		right = new Line2D(top.EndPointB(),
				new Vector2f(worldTranslation.x+IArea.TERRAIN_SIZE_HALF, worldTranslation.z+IArea.TERRAIN_SIZE_HALF) 
		);
		bottom = new Line2D(right.EndPointB(), 
				new Vector2f(worldTranslation.x-IArea.TERRAIN_SIZE_HALF, worldTranslation.z+IArea.TERRAIN_SIZE_HALF) 
		);
		left = new Line2D(bottom.EndPointB(),top.EndPointA());
		System.out.println(this+" top:"+top);
		System.out.println(this+" right:"+right);
		System.out.println(this+" bottom:"+bottom);
		System.out.println(this+" left:"+left);
	}

	private HashSet<Cell> getBorderCells(int where) {
		HashSet<Cell> ret = new HashSet<Cell>();
		switch (where) {
		case BOUNDS_LEFT:
			ret.addAll(allBorders.get(BORDER_TOP_LEFT));ret.addAll(allBorders.get(BORDER_LEFT));ret.addAll(allBorders.get(BORDER_BOTTOM_LEFT));break;
		case BOUNDS_RIGHT:
			ret.addAll(allBorders.get(BORDER_TOP_RIGHT));ret.addAll(allBorders.get(BORDER_RIGHT));ret.addAll(allBorders.get(BORDER_BOTTOM_RIGHT));break;
		case BOUNDS_TOP:
			ret.addAll(allBorders.get(BORDER_TOP_LEFT));ret.addAll(allBorders.get(BORDER_TOP));ret.addAll(allBorders.get(BORDER_TOP_RIGHT));break;
		case BOUNDS_BOTTOM:
			ret.addAll(allBorders.get(BORDER_BOTTOM_LEFT));ret.addAll(allBorders.get(BORDER_BOTTOM));ret.addAll(allBorders.get(BORDER_BOTTOM_RIGHT));break;
		case BOUNDS_TOP_LEFT:
			ret.addAll(allBorders.get(BORDER_LEFT));ret.addAll(allBorders.get(BORDER_TOP_LEFT));ret.addAll(allBorders.get(BORDER_TOP));break;
		case BOUNDS_TOP_RIGHT:
			ret.addAll(allBorders.get(BORDER_TOP));ret.addAll(allBorders.get(BORDER_TOP_RIGHT));ret.addAll(allBorders.get(BORDER_RIGHT));break;
		case BOUNDS_BOTTOM_LEFT:
			ret.addAll(allBorders.get(BORDER_LEFT));ret.addAll(allBorders.get(BORDER_BOTTOM_LEFT));ret.addAll(allBorders.get(BORDER_BOTTOM));break;
		case BOUNDS_BOTTOM_RIGHT:
			ret.addAll(allBorders.get(BORDER_BOTTOM));ret.addAll(allBorders.get(BORDER_BOTTOM_RIGHT));ret.addAll(allBorders.get(BORDER_RIGHT));break;
		}
		return ret;
	}
	
	/**
	 * checks if the any point of the cell is on, or outside our bounds
	 * @param start start point in world coords
	 * @param end end point in world coords
	 * @return 1-4 for top, right, bottom, left lines or 0 if start to end lie not on bounds
	 */
	private void storeBorderCell(Cell c) {
//		if (hasBounds()) {
			int bound = 0;
			int fbound = 0;
			//for each Vertex of a cell
			for (Vector3f v : c.m_Vertex) {
				//check the bounds it crosses
				bound = getBoundingSide(v.x, v.z);
				
				//add cell to the corresponding quadrant ( could be added several times so a hashset is used)
				switch (bound) {
				case BOUNDS_LEFT:
					allBorders.get(BORDER_LEFT).add(c);break;
				case BOUNDS_RIGHT:
					allBorders.get(BORDER_RIGHT).add(c);break;
				case BOUNDS_TOP:
					allBorders.get(BORDER_TOP).add(c);break;
				case BOUNDS_BOTTOM:
					allBorders.get(BORDER_BOTTOM).add(c);break;
				case BOUNDS_TOP_LEFT:
					allBorders.get(BORDER_TOP_LEFT).add(c);break;
				case BOUNDS_TOP_RIGHT:
					allBorders.get(BORDER_TOP_RIGHT).add(c);break;
				case BOUNDS_BOTTOM_LEFT:
					allBorders.get(BORDER_BOTTOM_LEFT).add(c);break;
				case BOUNDS_BOTTOM_RIGHT:
					allBorders.get(BORDER_BOTTOM_RIGHT).add(c);break;
				}
				//add the bounds for this vertex to the overall bounds of this cell
				fbound |= bound;
			}
			//add it to the total of border cells with the overall bounds indicator
			borders.put(c, fbound);

//		}
	}
	
	/**
	 * is this mesh responsible for the mesh navigation ?
	 * @param pos
	 * @return
	 */
	//TODO consider decoupling from TerrainTriMesh 
	public boolean isPointInTile(float posX, float posZ) {
		//would it possibly be inside?
		if(posX <=(worldTranslation.x+IArea.TERRAIN_SIZE_HALF) &&
			posX >= (worldTranslation.x -IArea.TERRAIN_SIZE_HALF) &&
			posZ <=(worldTranslation.z+IArea.TERRAIN_SIZE_HALF) &&
			posZ >= (worldTranslation.z -IArea.TERRAIN_SIZE_HALF)) {
			return true;
		}
		return false;
	}
	
	private int getBoundingSide(float x, float z){
		int bounds = 0;
//		if (hasBounds()) {
				//left ?
				if(x <=(worldTranslation.x-IArea.TERRAIN_SIZE_HALF))
					bounds |= BOUNDS_LEFT;
				//right ?
				if(x >= (worldTranslation.x+IArea.TERRAIN_SIZE_HALF))
					bounds |= BOUNDS_RIGHT;
				//top ?
				if(z <=(worldTranslation.z-IArea.TERRAIN_SIZE_HALF))
					bounds |= BOUNDS_TOP;
				//bottom ?
				if(z >= (worldTranslation.z+IArea.TERRAIN_SIZE_HALF))
					bounds |= BOUNDS_BOTTOM;
//		}
		return bounds;
	}

//	//FIXME nav mesh switching the new way ( when next cell is not on this mesh)
//	private Cell ResolveMotionOnMesh(Vector3f StartPos, Cell StartCell, Vector3f EndPos) {
//		if(borders.containsKey(StartCell)) {
//			if(isPointInTile(EndPos.x,EndPos.z))
//				return this.mesh.ResolveMotionOnMesh(StartPos, StartCell, EndPos);
//			else{
//				if (isPointInTile(StartPos.x, StartPos.z)) {
//					Cell c = EntityNavigationManager.get().FindClosestCell(
//							EndPos, true);
//					if (c != null)
//						c.MapVectorHeightToCell(EndPos);
//					
//					return c;
//				} else {
//					//FIXME switch navmesh!
//					return null;
//				}
//			}
//		} else
//			return this.mesh.ResolveMotionOnMesh(StartPos, StartCell, EndPos);
//	}

	
//	//FIXME remove this, only needed for first time placemenet otherwise path used
//	public Cell FindClosestCell(Vector3f Point) {
//		if(isPointInTile(Point.x,Point.z))
//			return this.mesh.FindClosestCell(Point);
//		
//		return null;
////removed circular loop !!
////		return EntityNavigationManager.get().FindClosestCell(Point);
//	}

	public boolean isBorderCell(Cell c) {
		return borders.containsKey(c);
	}

	//distance between must be within SimpleTerrainManger.TERRAIN_SIZE
	public boolean isNeighbourOf(NavigationMesh endMesh) {
		Vector3f dist = worldTranslation.subtract(endMesh.worldTranslation);
		if(FastMath.abs(dist.x)>IArea.TERRAIN_SIZE || 
				FastMath.abs(dist.x)>IArea.TERRAIN_SIZE)
			return false;
		
		return true;

	}
	
	/**
	 * Builds a path on this mesh, or builds a path from start to end (direct route)
	 * The NavigationMesh is not responsible for mesh crossing. This is done in resolve motion on mesh and
	 * the path planning tool
	 * @param NavPath
	 * @param StartCell
	 * @param StartPos
	 * @param EndCell
	 * @param EndPos
	 * @return
	 */
	//FIXME move over to navmanager
	//the points are on this mesh for sure
	boolean buildNavigationPath(Path NavPath, Vector3f StartPos, Vector3f EndPos) {
		return BuildNavigationPath(NavPath, FindClosestCell(StartPos), StartPos, FindClosestCell(EndPos), EndPos);	
	}

	public boolean buildNavigationPathToBorder(Path navPath, Cell startCell, Vector3f startPos,
			Vector3f endPos) {
//		if (hasBounds()) {
			// get intersection point of direct route and border lines of tile
			Line2D l = new Line2D(startPos.x, startPos.z, endPos.x, endPos.z);
			Vector2f cross = new Vector2f();
			LINE_CLASSIFICATION classi = top.Intersection(l, cross);
			
			//check which side we cross
			if(classi != LINE_CLASSIFICATION.SEGMENTS_INTERSECT){
				classi = right.Intersection(l, cross);
				if(classi != LINE_CLASSIFICATION.SEGMENTS_INTERSECT){
					classi = bottom.Intersection(l, cross);
					if(classi != LINE_CLASSIFICATION.SEGMENTS_INTERSECT){
						classi = left.Intersection(l, cross);
						if(classi != LINE_CLASSIFICATION.SEGMENTS_INTERSECT)
							return false;
					}
				}
			}
			
			//TODO new plan, we have a crossing, ok, now find the closest cell on that border on our side, the cross must be shifted to that cell
			// then go from there to the final destination



			if(cross != null){
				int where = getBoundingSide(cross.x, cross.y);
				//collect all boundig cells on that tiles
				HashSet<Cell> targets = getBorderCells(where);
				//max dist can not be more than the span of a mesh tile
				float max = (IArea.TERRAIN_SIZE*IArea.TERRAIN_SIZE) +
							(IArea.TERRAIN_SIZE*IArea.TERRAIN_SIZE) + 0.01f;
				float dist = 0f;
				Cell targetCell = null;
				Vector3f goal = null;
				//loop over them and find the closest one //what if none on that sides, why loop at some which are completely off ???
				for(Cell c : targets){
					//prefere computed cross section over midpoints
					if(c.IsPointInCellCollumn(cross)){
						targetCell = c;
						goal = new Vector3f(cross.x, 0, cross.y);
						c.MapVectorHeightToCell(goal);
						break;
					}
					//all cell midpoints 
					for(Vector3f point : c.m_WallMidpoint){
						//not needed, as this will not work, midpoints will be always on the line
//						//to find the one on the border
//						if(!isPointInTile(point.x, point.z)){
							//and which is closer than the ones before the current
							dist = cross.distanceSquared(point.x, point.z);
							if(dist<max){
								max = dist;
								targetCell = c;
								goal = point;
							}
//						}
					}
				}
				//build path to that midsection point
				if(targetCell != null){
					if(startCell != null)
						return BuildNavigationPath(navPath, startCell, startPos, targetCell, goal);
					else
						return BuildNavigationPath(navPath, FindClosestCell(startPos), startPos, targetCell, goal);
				}
				else
					return false;
			}
//		}
		return false;
	}

//	public Cell ResolveMotionOnMesh(Vector3f startPos, Cell startCell,
//			Vector3f endPos) {
//		return this.mesh.ResolveMotionOnMesh(startPos, startCell, endPos);
//	}

	
	//FIXME redo debug box border rendering
//	public void bordersDebugRender(com.jme3.scene.Node rootNode) {
//		if(debugRoot == null)
//			debugRoot = new com.jme3.scene.Node("Borderpoints of Mesh "+this.getName());
//		
//		rootNode.detachChild(debugRoot);
//		debugRoot.detachAllChildren();
//		for(Vector3f vec : borderPoints){
//			Box b = new Box(vec,0.8f,0.8f,0.8f);
//			//todo just a color
//	        debugRoot.attachChild(b);
//		}
//		rootNode.attachChild(debugRoot);
//		debugRoot.updateRenderState();
//	}
	@Override
	public String toString(){
		StringBuilder str = new StringBuilder(this.getClass().getSimpleName());
		str.append("x:").append((int)worldTranslation.x/IArea.TERRAIN_SIZE).append(",z:").append((int)worldTranslation.z/IArea.TERRAIN_SIZE).append(" worldPos:").append(worldTranslation).append(" extents:").append(IArea.TERRAIN_SIZE_HALF);
		
		return str.toString();
	}

	/**
	 * 
	 * @return a clone of the current position
	 */
	public Vector3f getPosition() {
		return worldTranslation.clone();
	}
}
