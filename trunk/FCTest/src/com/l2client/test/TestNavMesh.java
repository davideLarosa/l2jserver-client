package com.l2client.test;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import com.jme3.app.R.string;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.export.binary.BinaryImporter;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.l2client.model.l2j.ServerValues;
import com.l2client.navigation.TiledNavMesh;

public class TestNavMesh extends SimpleApplication implements ActionListener {

	static int current = 0;
	
	static String LOAD_URL = "";
	
	private static String fileEnding = "nav.jnv";//nav2 is l2j tiles smaller navs (optimized), no changes in own tiles

	private static FilenameFilter navFileFilter = new FilenameFilter() {

		@Override
		public boolean accept(File dir, String name) {
			if (name.endsWith(fileEnding)) {
				return true;
			}

			return false;
		}
	};
	
	private ArrayList<File> files = new ArrayList<File>();
	
//	44 PM com.l2client.navigation.TiledNavMesh getDebugBorderMesh
//	Schwerwiegend: Debug Borders for:TiledNavMesh x:142, z:206 worldPos:(-4608.0, 0.0, 15872.0) extents:128 Borders top/tr/right/rb/bottom/bl/left/lt:257/1/126/0/0/0/137/1
//	Dez 04, 2013 3:06:44 PM com.l2client.navigation.TiledNavMesh getDebugBorderMesh
//	Schwerwiegend: Debug Borders for:TiledNavMesh x:141, z:206 worldPos:(-4864.0, 0.0, 15872.0) extents:128 Borders top/tr/right/rb/bottom/bl/left/lt:113/1/131/0/0/0/123/0
//	Dez 04, 2013 3:06:44 PM com.l2client.navigation.TiledNavMesh getDebugBorderMesh
//	Schwerwiegend: Debug Borders for:TiledNavMesh x:141, z:207 worldPos:(-4864.0, 0.0, 16128.0) extents:128 Borders top/tr/right/rb/bottom/bl/left/lt:208/1/104/1/256/1/130/1
//	Dez 04, 2013 3:06:44 PM com.l2client.navigation.TiledNavMesh getDebugBorderMesh
//	Schwerwiegend: Debug Borders for:TiledNavMesh x:143, z:206 worldPos:(-4352.0, 0.0, 15872.0) extents:128 Borders top/tr/right/rb/bottom/bl/left/lt:257/1/434/0/0/0/414/1
//	Dez 04, 2013 3:06:44 PM com.l2client.navigation.TiledNavMesh getDebugBorderMesh
//	Schwerwiegend: Debug Borders for:TiledNavMesh x:142, z:207 worldPos:(-4608.0, 0.0, 16128.0) extents:128 Borders top/tr/right/rb/bottom/bl/left/lt:293/1/109/2/255/2/100/1
//	Dez 04, 2013 3:06:44 PM com.l2client.navigation.TiledNavMesh getDebugBorderMesh
//	Schwerwiegend: Debug Borders for:TiledNavMesh x:143, z:207 worldPos:(-4352.0, 0.0, 16128.0) extents:128 Borders top/tr/right/rb/bottom/bl/left/lt:0/0/0/0/0/0/0/0
//	Dez 04, 2013 3:06:44 PM com.l2client.navigation.TiledNavMesh getDebugBorderMesh
//	Warnung: Navmesh without any bordercells:TiledNavMesh x:143, z:207 worldPos:(-4352.0, 0.0, 16128.0) extents:128 Borders top/tr/right/rb/bottom/bl/left/lt:0/0/0/0/0/0/0/0
//	Dez 04, 2013 3:06:45 PM com.l2client.network.game.ServerPackets.MoveToLocation handlePacket

	
//	Node bboxes = new Node("debug bboxes");
	Node nav = new Node("debug nav");
	Node border = new Node("debug border");
	TiledNavMesh navMesh = null;

	private Material matWireframe;

	
	
    public TestNavMesh(String directory) {
		super();
		if(directory != null)
			LOAD_URL = directory;
	}

	public static void main(String[] args){
    	if(args.length <= 0){
    		System.out.println("ERROR: Directory is missing as paramter");
    		return;
    	}
        TestNavMesh app = new TestNavMesh(args[0]);
        app.start();
    }

    @Override
    public void simpleInitApp() {
    	
    	cacheFileNames(new File(LOAD_URL));

    	cam.setLocation(new Vector3f(30f, 50f,-30f));//ServerValues.getClientCoords(-71453,258305,-3104));//
    	cam.setFrustumFar(1000f);
    	cam.setFrustumNear(1f);
//    	cam.lookAt(new Vector3f(-9902.244f, 19.241596f, 8288.143f), Vector3f.UNIT_Y);
    	flyCam.setMoveSpeed(50f);
    	
        matWireframe = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matWireframe.setColor("Color", ColorRGBA.Green);
        matWireframe.getAdditionalRenderState().setWireframe(true);

        DirectionalLight light = new DirectionalLight();
        light.setDirection((new Vector3f(-0.5f, -1f, -0.5f)).normalize());
        light.setColor(new ColorRGBA(0.8f,0.8f,0.8f,1f));
        rootNode.addLight(light);

        AmbientLight ambLight = new AmbientLight();
        ambLight.setColor(new ColorRGBA(0.1f, 0.1f, 0.1f, 1f));
        rootNode.addLight(ambLight);
    	
    	
        inputManager.addListener(this, "print_scenegraph", "print_cam_location", "print_bboxes", "toggle_navmesh", "toggle_navbodermesh", "nextMesh");
        inputManager.addMapping("print_scenegraph", new KeyTrigger(KeyInput.KEY_F6));
        inputManager.addMapping("print_cam_location", new KeyTrigger(KeyInput.KEY_F1));
        inputManager.addMapping("print_bboxes", new KeyTrigger(KeyInput.KEY_F7));
        inputManager.addMapping("toggle_navmesh", new KeyTrigger(KeyInput.KEY_F8));
        inputManager.addMapping("nextMesh", new KeyTrigger(KeyInput.KEY_F9));
        inputManager.addMapping("toggle_navbodermesh", new KeyTrigger(KeyInput.KEY_F10));
        
//        rootNode.attachChild(bboxes);
        rootNode.attachChild(nav);
    	rootNode.attachChild(border);
     }
    

	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		if(name.equals("print_scenegraph") && !isPressed){
			printHierarchy(rootNode, "");
		} else if(name.equals("print_cam_location") && !isPressed){
			printCamLocation();
		} else if(name.equals("toggle_navmesh") && !isPressed){
			toggelNavMeshes();
		} else if(name.equals("toggle_navbodermesh") && !isPressed){
			toggelNavBorderMeshes();
		} else if(name.equals("nextMesh") && !isPressed){
			loadAttachNextMesh();
		}
	}
	


	protected void printHierarchy(Spatial n, String indent) {
		System.out.println(indent+n.getName()+":"+n.getClass()+" at "+n.getWorldTranslation()+ " bounds:"+n.getWorldBound());
		for(Light l :n.getLocalLightList()){
			System.out.println(indent+"  + Light:"+l.getType()+" Color:"+l.getColor());
		}
		if(n instanceof Node)
			for(Spatial c : ((Node)n).getChildren()){
				printHierarchy(c, indent+" ");
			}
		
		for(int i = 0; i<n.getNumControls(); i++)
			System.out.println(indent+"Controller:"+n.getControl(i).getClass());
	}
    
    
    static float t=0f;
    
    @Override
	public void simpleUpdate(float tpf){
    	
    	super.simpleUpdate(tpf);

//    	t+=tpf;
//    	if(t>=20f){
//    		t=0f;
//			System.out.println("Cam@"+cam.getLocation());    
//			System.out.println("Walker@:"+walker.getLocalTranslation());
//			PositioningComponent pc = (PositioningComponent)em.getComponent(((Entity)walker).getId(), PositioningComponent.class);
//			if(pc != null){
//				System.out.print("Ent 1@:"+pc.position+" path:"+pc.path);
//				if(pc.nextWayPoint != null)
//					System.out.println(" heading:"+pc.nextWayPoint.Position);
//				else
//					System.out.println(" heading: not moving");
//			
//			}
//    	}
    }

	   
    private void toggelNavMeshes(){
    	if(nav.getChildren().size() > 0){
    		nav.detachAllChildren();
    	} else {
    		if(navMesh != null) {
    			Geometry db = navMesh.getDebugMesh();
    			db.setMaterial(this.matWireframe.clone());
    			nav.attachChild(db);	
    		}
    	}
    }

	private void toggelNavBorderMeshes() {
    	if(border.getChildren().size() > 0){
    		border.detachAllChildren();
    	} else {
    		if(navMesh != null) {
        		Material mat = matWireframe.clone();
                mat.setColor("Color", ColorRGBA.Blue);
    			Geometry db = navMesh.getDebugBorderMesh();
    			//we just put it slightly off to beter see the border
    			db.setLocalTranslation(new Vector3f(0f, 0.5f, 0f).addLocal(db.getLocalTranslation()));
    			System.out.println("Border has "+db.getVertexCount()+" vertices");
    			db.setMaterial(mat);
    			border.attachChild(db);	
    		}
    	}
    }
    private void printCamLocation() {
    	System.out.println("Camera loc.:"+cam.getLocation()+ " @ server:"+ServerValues.getServerString(cam.getLocation().x, cam.getLocation().y, cam.getLocation().z));
	}
    
    private void cacheFileNames(File directory){
    	if (!directory.isDirectory()) {
    		if(navFileFilter.accept(directory.getParentFile(), directory.getName())){
				files.add(directory);
    		}
    	} else {
    		File[] list = directory.listFiles();
    		for(File f : list){
    			cacheFileNames(f);
    		}
    	}
    }
    
    
    private void loadAttachNextMesh(){
    	try {
    		if(files.size() > 0) {
    			if(current >= files.size())
    				current = 0;
    			
    			File f = files.get(current++);
    			System.out.println("loading:"+f);
    			
    			navMesh = (TiledNavMesh) BinaryImporter.getInstance().load(f);
    			System.out.println("loaded:"+f);
//    			navMesh.loadFromGeom(navMesh.getDebugMesh());
    			border.detachAllChildren();
    			nav.detachAllChildren();
    			toggelNavMeshes();
    			System.out.println("attached:"+f);
    			toggelNavBorderMeshes();
    			System.out.println("border added:"+f);
    			
    		}
    		cam.setLocation(new Vector3f(-100f,300f,-100f).add(navMesh.getPosition()));
    		cam.lookAt(navMesh.getPosition(), Vector3f.UNIT_Y);

		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
}
