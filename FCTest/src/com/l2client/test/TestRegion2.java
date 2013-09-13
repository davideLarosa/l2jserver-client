package com.l2client.test;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.util.TangentBinormalGenerator;
import com.l2client.app.Singleton;
import com.l2client.controller.area.IArea;
import com.l2client.navigation.EntityNavigationManager;
import com.l2client.navigation.TiledNavMesh;

public class TestRegion2 extends SimpleApplication {
	
	static float upd = 0;
	static int run = 0;
	
	Node debugNodes = new Node("debugs");

	EntityNavigationManager em;
    public static void main(String[] args){
        TestRegion2 app = new TestRegion2();
        app.start();
    }

    @Override
    public void simpleInitApp() {
    	flyCam.setMoveSpeed(50);
		assetManager = new DesktopAssetManager(Thread.currentThread().getContextClassLoader().getResource("com/l2client/asset/loader.cfg"));

    	em = Singleton.get().getNavManager();
    	rootNode.attachChild(debugNodes);

        DirectionalLight light = new DirectionalLight();
        light.setDirection((new Vector3f(-0.5f, -1f, -0.5f)).normalize());
        rootNode.addLight(light);

        AmbientLight ambLight = new AmbientLight();
        ambLight.setColor(new ColorRGBA(0.2f, 0.2f, 0.2f, 1f));
        rootNode.addLight(ambLight);
    	
    	cam.setLocation(cam.getLocation().add(0, 120, 50));
    	cam.setFrustumFar(5000f);
    	cam.setFrustumNear(1f);

    	addNavWithMesh(em,"/export/4_0.obj");
    	addNavWithMesh(em,"/export/4_1.obj");
    	addNavWithMesh(em,"/export/4_2.obj");
    	addNavWithMesh(em,"/export/4_3.obj");
    	addNavWithMesh(em,"/export/5_0.obj");
    	addNavWithMesh(em,"/export/5_1.obj");
    	addNavWithMesh(em,"/export/5_2.obj");
    	addNavWithMesh(em,"/export/5_3.obj");
    	addNavWithMesh(em,"/export/6_0.obj");
    	addNavWithMesh(em,"/export/6_1.obj");
    	addNavWithMesh(em,"/export/6_2.obj");
    	addNavWithMesh(em,"/export/6_3.obj");
    	addNavWithMesh(em,"/export/7_0.obj");
    	addNavWithMesh(em,"/export/7_1.obj");
    	addNavWithMesh(em,"/export/7_2.obj");
    	addNavWithMesh(em,"/export/7_3.obj");
    	
    	Box b = new Box(1f, 2f, 1f);
    	Geometry g = new Geometry("RefBox", b);
    	Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        g.setMaterial(mat);
        g.setLocalTranslation(256f,2.2f,0f);
    	rootNode.attachChild(g);
    }
    
    @Override
	public void simpleUpdate(float tpf){

    }

	private void addNavWithMesh(EntityNavigationManager em, String file) {
		try {
//			NavigationMesh m = getNavMesh(meshFile+".jnv", 128, x, y, z);

			Geometry grid = (Geometry) assetManager.loadAsset(file+".j3o");
			 Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		        mat.setColor("Color", ColorRGBA.randomColor());
			grid.setMaterial(mat);
			TangentBinormalGenerator.fixTileNormals(grid.getMesh(), IArea.TERRAIN_SIZE_HALF, 0.05f);
			TangentBinormalGenerator.generate(grid.getMesh(), false);
			grid.updateGeometricState();
			rootNode.attachChild(grid);
			TiledNavMesh m = (TiledNavMesh) assetManager.loadAsset(file+".jnv");//BinaryImporter.getInstance().load(new File(file+".jnv"));
			em.attachMesh(m);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
