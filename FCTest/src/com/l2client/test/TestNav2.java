package com.l2client.test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.export.binary.BinaryImporter;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.l2client.app.Singleton;
import com.l2client.controller.area.IArea;
import com.l2client.navigation.EntityNavigationManager;
import com.l2client.navigation.NavTestHelper;
import com.l2client.navigation.Path;
import com.l2client.navigation.TiledNavMesh;

public class TestNav2 extends SimpleApplication {
	
	static float upd = 0;
	static int run = 0;
	
	Node debugNodes = new Node("debugs");

	EntityNavigationManager em;
    public static void main(String[] args){
        TestNav2 app = new TestNav2();
        app.start();
    }

    @Override
    public void simpleInitApp() {
    	 flyCam.setMoveSpeed(20);
    	assetManager = new DesktopAssetManager(true);
    	em = Singleton.get().getNavManager();
    	rootNode.attachChild(debugNodes);
    	cam.setLocation(cam.getLocation().add(0, 50, 50));

    	addNavWithMesh(em,"navtest/grid.0_0.j3o",0, 0, 0);
    	addNavWithMesh(em,"navtest/grid.1_0.j3o",IArea.TERRAIN_SIZE,0,0);
    	addNavWithMesh(em,"navtest/grid.0_1.j3o",0,0,-IArea.TERRAIN_SIZE);
    	addNavWithMesh(em,"navtest/grid.1_1.j3o",IArea.TERRAIN_SIZE,0,-IArea.TERRAIN_SIZE);

        rootNode.attachChild(NavTestHelper.getText(assetManager, "0/0", 0, 5f, 0, 2f));
        rootNode.attachChild(NavTestHelper.getText(assetManager, IArea.TERRAIN_SIZE+"/0", IArea.TERRAIN_SIZE, 5f, 0, 2f));
        rootNode.attachChild(NavTestHelper.getText(assetManager, IArea.TERRAIN_SIZE+"/-"+IArea.TERRAIN_SIZE, IArea.TERRAIN_SIZE, 0, -IArea.TERRAIN_SIZE, 2f));
        rootNode.attachChild(NavTestHelper.getText(assetManager, "0/-"+IArea.TERRAIN_SIZE, 0, 5f, -IArea.TERRAIN_SIZE, 2f));
        

//        NavigationMesh n = em.getNavMesh(new Vector3f(128,0,0));
//        System.out.println("Navmesh with 128/0 in it is:"+n+" ? 128/0 ?");
//        n = em.getNavMesh(new Vector3f(0,0,-128));
//        System.out.println("Navmesh with 0/-128 in it is:"+n+" ? 0/-128 ?");
//        n = em.getNavMesh(new Vector3f(128,0,-128));
//        System.out.println("Navmesh with 128/-128 in it is:"+n+" ? 128/-128 ?");
//        n = em.getNavMesh(new Vector3f(0,0,0));
//        System.out.println("Navmesh with 0/0 in it is:"+n+" ? 0/0 ?");
        
//        Vector3f sPos = new Vector3f(1.1f,0,1f);
//        Vector3f ePos = new Vector3f(2.9f,0,2.9f);
        Path p = null;
//      NavTestHelper.findPath(em, sPos, ePos);

//        p = NavTestHelper.findPath(em, sPos, ePos);
        
//        NavTestHelper.debugShowPath(assetManager, rootNode, p);
//        NavTestHelper.areCellsConnected(p);
//        NavTestHelper.arePointsInCells(p);
//        NavTestHelper.findPath(em, new Vector3f(0,0,-62), new Vector3f(0,0,-66));
        p= NavTestHelper.walkPath(em,  new Vector3f(0,0,-62), new Vector3f(0,0,-66), 8f);
        if(p != null){
        NavTestHelper.debugShowPath(assetManager, rootNode, p);
        NavTestHelper.debugShowCost(assetManager, rootNode, p, ColorRGBA.Cyan);
        }
    }
    
    @Override
	public void simpleUpdate(float tpf){
    	
    	if(upd <0f){
    		enqueue(new Callable<Object>() {

				@Override
				public Object call() throws Exception {
					debugNodes.detachAllChildren();
		    		Path p = new Path();
//					p = NavTestHelper.findPath(em, new Vector3f(1.1f,0,1f), new Vector3f(3.1f,0,3f));
					p = NavTestHelper.findPath(em, new Vector3f(260f,0,-20f), new Vector3f(270f,0,10f));
					if(p != null){
//						p.optimize();
					NavTestHelper.debugShowCost(assetManager, debugNodes, p, ColorRGBA.Cyan);
					NavTestHelper.debugShowPath(assetManager, debugNodes, p);
					NavTestHelper.areCellsConnected(p);
					NavTestHelper.arePointsInCells(p);
					}
					return null;
				}
    			
			});

    		upd =5f;
    	}
    	upd -=tpf;
    }

	private void addNavWithMesh(EntityNavigationManager em, String meshFile,
			int x, int y, int z) {
		try {
//			NavigationMesh m = getNavMesh(meshFile+".jnv", 128, x, y, z);
			TiledNavMesh m = getMeshNav(meshFile, IArea.TERRAIN_SIZE, x, y, z);
			em.attachMesh(m);
			Geometry grid = (Geometry) assetManager.loadAsset(meshFile);
			grid.setLocalTranslation(x, y, z);
//	        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//	        mat.setColor("Color", ColorRGBA.LightGray);
//			grid.setMaterial(mat);
			grid.updateGeometricState();
			rootNode.attachChild(grid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    
    private Spatial getQuad(float size, float x, float y, float z){
    	Quad q = new Quad(size,size,true);
        //Box b = new Box(Vector3f.ZERO, 1, 1, 1);
        Geometry geom = new Geometry("Box", q);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", assetManager.loadTexture("com/l2client/test/material/chterraingrass01_n.dds"));
        geom.setMaterial(mat);
//        geom.setLocalTranslation((-0.5f*size)+x,(-0.5f*size)+y,z);  
        geom.rotate(-FastMath.HALF_PI, 0, 0);
        geom.setLocalTranslation((-0.5f*size)+x,y,(0.5f*size)+z);
        
        return geom;
    }
    
    private TiledNavMesh getNavMesh(String file, float size, float x, float y, float z) throws IOException{
    	return (TiledNavMesh) BinaryImporter.getInstance().load(new File(file));
    }
    
    private TiledNavMesh getMeshNav(String file, float size, float x, float y, float z){
		TiledNavMesh m;
		Geometry g00 = (Geometry) assetManager.loadAsset(file);
		if(g00 == null){
			System.out.println("Asset for "+file+"is missing");
			return null;
		}
		g00.setLocalTranslation(x,y,z);
		g00.updateGeometricState();
		m = new TiledNavMesh();
        
		m.loadFromGeom(g00);
		return m;
    }
    
    private TiledNavMesh getNavMesh(String name, Vector3f worldtrans){
    	TiledNavMesh m = new TiledNavMesh();
    	m.loadFromData(new Vector3f[] {new Vector3f(-IArea.TERRAIN_SIZE_HALF,0,IArea.TERRAIN_SIZE_HALF),new Vector3f(-IArea.TERRAIN_SIZE_HALF,0,-IArea.TERRAIN_SIZE_HALF), new Vector3f(IArea.TERRAIN_SIZE_HALF,0,-IArea.TERRAIN_SIZE_HALF), new Vector3f(IArea.TERRAIN_SIZE_HALF,0,IArea.TERRAIN_SIZE_HALF)}, new short[][] {{0,2,1},{0,3,2}}, worldtrans);
    	return m;
    }

}
