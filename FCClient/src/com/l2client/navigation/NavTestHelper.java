package com.l2client.navigation;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.BillboardControl;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Line;
import com.l2client.component.PositioningComponent;
import com.l2client.component.PositioningSystem;
import com.l2client.navigation.Path.WAYPOINT;

public class NavTestHelper {

	public static Path findPath(EntityNavigationManager em, Vector3f sPos, Vector3f ePos) {

        Path pa = new Path();
        long dT =  System.currentTimeMillis();
        boolean foundPath = em.buildNavigationPath(pa, sPos, ePos);
        System.out.println("path building took "+(System.currentTimeMillis()-dT)+" milli seconds");
        if(foundPath){
        	System.out.println("Found path from "+sPos+" to "+ePos);
        	for(WAYPOINT  w : pa.m_WaypointList)
        		System.out.println("  -"+w.Position+" cost:"+w.Cell.m_ArrivalCost+" cell:"+w.Cell);
        	return pa;
        } else {
        	System.out.println("NO PATH from "+sPos+" to "+ePos);
        }
        return null;
	}
	
	public static boolean arePointsInCells(Path pa){
		for(WAYPOINT w : pa.m_WaypointList){
			if(!w.Cell.IsPointInCellCollumn(w.Position)){
				System.out.println("Waypoint with point not in cell !!"+w.Position+" "+w.Cell.m_CenterPoint);
				return false;
			}
		}
		return true;
	}
	
	public static boolean areCellsConnected(Path pa){
		Cell last = pa.m_WaypointList.get(0).Cell;
		Cell current = null;
		Cell end = pa.EndPoint().Cell;
		for(int i = 1; i<pa.m_WaypointList.size();i++){
			current = pa.m_WaypointList.get(i).Cell;
			if(current == end) break;
			if(current == last) continue;
			boolean found = false;
			for(Cell c : last.m_Link){
				if(c == current){ 
					found = true;
					break;
				}
			}
			if(!found){
				System.out.println("Waypoint Cells have no link, error in pathfinding!! last:"+last+" last.pos:"+last.m_CenterPoint+" :"+current+" current.pos:"+current.m_CenterPoint);
//				for(WAYPOINT  w : pa.m_WaypointList){
//	        		System.out.println("  -"+w.Position);
//	        	}
				return false;
			}
			last = current;
		}
		
		return true;
	}
	
	public static Path walkPath(EntityNavigationManager em, Vector3f sPos, Vector3f ePos, float maxSpeed) {

        Path pa = new Path();
        boolean foundPath = em.buildNavigationPath(pa, sPos, ePos);
        if(foundPath){
        	System.out.println("Found path from "+sPos+" to "+ePos);

        	for(WAYPOINT  w : pa.m_WaypointList)
        		System.out.println("  -"+w.Position+" cost:"+w.Cell.m_ArrivalCost+" cell:"+w.Cell.hashCode());
        	
        	
//        	WAYPOINT cur = pa.m_WaypointList.get(0);
//        	WAYPOINT last = null;
//        	while(cur != pa.EndPoint() && cur != last){
//        		System.out.println("  - opt:"+cur.Position);
//        		last = cur;
//        		cur = pa.GetFurthestVisibleWayPoint(cur);
//        	}
        	
        	PositioningSystem mover = PositioningSystem.get();
        	PositioningComponent com = new PositioningComponent();
        	com.initByWayPoint(pa);
        	com.acc =0f;
        	com.direction = Vector3f.ZERO;
        	com.heading = 0f;
        	com.maxAcc = 2f;
        	com.maxDcc = 3f;
        	com.maxSpeed = maxSpeed;
        	com.speed = 0f;
        	mover.addComponentForUpdate(com);
        	float dt = 0f;
        	float maxt = 200f;
        	float step = 0.2f;
        	while(dt < maxt){
        		dt +=step;
        		mover.update(step);
        		if(com.path == null){
        			System.out.println("Reached:"+com.position);
        			return pa;
        		}
        		
        		System.out.println("- walking to:"+com.position);
        	}        	
        } else {
        	System.out.println("NO PATH from "+sPos+" to "+ePos);
        }
        return null;
	}
	
	public static void debugShowCell(AssetManager assetMan, com.jme3.scene.Node root, Cell c,
			ColorRGBA color, boolean doLinkVis) {
		Geometry geom = getLine(c.m_Vertex[0], c.m_Vertex[1]);	
		Material mat = new Material(assetMan, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", color);
		geom.setMaterial(mat);
		root.attachChild(geom);		
		geom = getLine(c.m_Vertex[1], c.m_Vertex[2]);
		geom.setMaterial(mat);
		root.attachChild(geom);
		geom = getLine(c.m_Vertex[2], c.m_Vertex[0]);
		geom.setMaterial(mat);
		root.attachChild(geom);
		if(doLinkVis){
		if(c.m_Link[0] != null){
		geom = getLine(c.m_WallMidpoint[0],c.m_Link[0].m_CenterPoint, 0.5f);
		mat = new Material(assetMan, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom.setMaterial(mat);
		root.attachChild(geom);
		}
		if(c.m_Link[1] != null){
		geom = getLine(c.m_WallMidpoint[1],c.m_Link[1].m_CenterPoint, 0.5f);
		mat = new Material(assetMan, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Red);
		geom.setMaterial(mat);
		root.attachChild(geom);
		}
		if(c.m_Link[2] != null){
		geom = getLine(c.m_WallMidpoint[2],c.m_Link[2].m_CenterPoint, 0.5f);
		mat = new Material(assetMan, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Green);
		geom.setMaterial(mat);
		root.attachChild(geom);
		}}
	}
	
	public static void debugShowBox(AssetManager assetMan, com.jme3.scene.Node root, Vector3f position, ColorRGBA color, float x, float y, float z) {
		Box b = new Box(position, x, y, z);
		Geometry geom = new Geometry("Box", b);
		Material mat = new Material(assetMan, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", color);
		geom.setMaterial(mat);
		root.attachChild(geom);
	}
	
	public static void debugShowCost(AssetManager assetMan, com.jme3.scene.Node root, Path pa, ColorRGBA color){
		if(pa.m_WaypointList.size() <=0)
			return;
    	int id = pa.m_WaypointList.get(0).Cell.m_SessionID;
    	for(Cell c :pa.m_WaypointList.get(0).mesh.m_CellArray)
    		if(c.m_ArrivalCost > 0f && c.m_SessionID == id) { 
    			System.out.println("  --"+c.m_CenterPoint+" arrivalcost:"+c.m_ArrivalCost+" pathcost:"+c.PathfindingCost());
    			//debugShowBox(assetMan, root, c.CenterPoint(), color, .1f, 4f+c.m_ArrivalCost, .1f);
    			String s = ""+c.PathfindingCost();
    			root.attachChild(getText(assetMan, s.length()>4?s.substring(0,4):s, c.m_CenterPoint.x, c.m_CenterPoint.y, c.m_CenterPoint.z, 0.1f));
    		}
	}

	public static void debugShowPath(AssetManager assetMan, com.jme3.scene.Node root, Path p) {
		float width = 0.05f;
		NavTestHelper.debugShowBox(assetMan, root, p.m_WaypointList.get(0).Position, ColorRGBA.Green, width, .8f, width);
		NavTestHelper.debugShowCell(assetMan, root, p.m_WaypointList.get(0).Cell, ColorRGBA.Blue, true);
		for(int i = 1; i< p.m_WaypointList.size()-1;i++){
			ColorRGBA color = ColorRGBA.White.mult(((float)i/p.m_WaypointList.size()));
			NavTestHelper.debugShowBox(assetMan, root, p.m_WaypointList.get(i).Position, color, width, .8f, width);
			debugShowLine(assetMan, root, p.m_WaypointList.get(i-1).Position, p.m_WaypointList.get(i).Position, color);
			NavTestHelper.debugShowCell(assetMan, root, p.m_WaypointList.get(i).Cell, ColorRGBA.Blue, true);
        }
		NavTestHelper.debugShowBox(assetMan, root, p.EndPoint().Position, ColorRGBA.Red, width, .8f, width);
//		debugShowLine(p.m_WaypointList.get(p.m_WaypointList.size()-1).Position, p.EndPoint().Position, ColorRGBA.White);
	}
	
	private static void debugShowLine(AssetManager assetMan, com.jme3.scene.Node root, Vector3f start, Vector3f end,
			ColorRGBA color) {
		Line b = new Line(start, end);//, 0.1f, 0.4f, 0.1f);
		b.setLineWidth(2f);
		Geometry geom = new Geometry("Box", b);
		Material mat = new Material(assetMan, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", color);
		geom.setMaterial(mat);
		root.attachChild(geom);		
	}
	
	public static void debugShowMesh(AssetManager assetMan, com.jme3.scene.Node root, NavigationMesh mesh) {
		int cnt = mesh.m_CellArray.size();
		Material mat = new Material(assetMan, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.White);
		Cell c = null;
		Geometry geom = null;
		for(int i=0; i<cnt;i++){
			
//			if(i>2)
//				return;
			c = mesh.m_CellArray.get(i);
//			geom = getLine(c.m_Vertex[0], c.m_Vertex[1]);
//			geom.setMaterial(mat);
//			root.attachChild(geom);	
//			geom = getLine(c.m_Vertex[1], c.m_Vertex[2]);
//			geom.setMaterial(mat);
//			root.attachChild(geom);
//			geom = getLine(c.m_Vertex[2], c.m_Vertex[0]);
//			geom.setMaterial(mat);
//			root.attachChild(geom);
//			if(i%2!=0)
				debugShowCell(assetMan, root, c, ColorRGBA.White, true);
		}
	}
	
	private static Geometry getLine(Vector3f st, Vector3f en){
		return getLine(st, en, 3f);
	}
	
	private static Geometry getLine(Vector3f st, Vector3f en, float size){
		Line b = new Line(st, en);//, 0.1f, 0.4f, 0.1f);
		b.setLineWidth(size);
		return new Geometry("",b);
	}
	
    public static Spatial getText(AssetManager assetMan, String t, float x, float y, float z, float size){
        BitmapFont fnt = assetMan.loadFont("Interface/Fonts/Default.fnt");
        BitmapText label = new BitmapText(fnt, false);
        label.setBox(new Rectangle(0, 0, 12, 8));
        label.setQueueBucket(Bucket.Transparent);
        label.setSize( size );
        label.setText(t);
        label.setColor(ColorRGBA.White);
        label.addControl(new BillboardControl());
        label.setLocalTranslation(x, y, z);
        return label;
    }

	public static void areCellLinksWithinRange(NavigationMesh m, float maxDist) {
		for(int i=0;i<m.m_CellArray.size();i++){
			Cell c = m.m_CellArray.get(i);
			for(int j=0;j<3;j++){
				if(c.m_Link[j]!= null){
					if(c.m_CenterPoint.distance(c.m_Link[j].m_CenterPoint)>maxDist){
						System.out.println("ERROR: Links between "+c+" and "+c.m_Link[j]+" are further than "+maxDist+" apart:"+c.m_CenterPoint.distance(c.m_Link[j].m_CenterPoint));
						System.out.println("ERROR: c1:"+c.m_Vertex[0]+" c2:"+c.m_Vertex[1]+" c3:"+c.m_Vertex[2]);
						System.out.println("ERROR: l1:"+c.m_Link[j].m_Vertex[0]+" l2:"+c.m_Link[j].m_Vertex[1]+" l3:"+c.m_Link[j].m_Vertex[2]);
					}
				}
			}
		}
	}

	public static void printPath(Path pa) {
		if (pa != null) {
			System.out.println("Found path from " + pa.StartPoint() + " to "
					+ pa.EndPoint());
			for (WAYPOINT w : pa.m_WaypointList)
				System.out.println("  -" + w.Position + " cost:"
						+ w.Cell.m_ArrivalCost + " cell:" + w.Cell);
		}
	}
}
