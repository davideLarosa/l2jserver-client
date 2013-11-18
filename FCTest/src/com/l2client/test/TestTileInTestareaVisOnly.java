package com.l2client.test;

import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.WireBox;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.StripBox;
import com.l2client.app.Singleton;
import com.l2client.component.JmeUpdateSystem;
import com.l2client.component.PositioningSystem;
import com.l2client.controller.SceneManager;
import com.l2client.controller.SceneManager.Action;
import com.l2client.controller.area.TileTerrainManager;
import com.l2client.controller.entity.EntityManager;
import com.l2client.model.l2j.ServerValues;
import com.l2client.navigation.EntityNavigationManager;
import com.l2client.navigation.TiledNavMesh;

public class TestTileInTestareaVisOnly extends SimpleApplication implements ActionListener {
	
	static float upd = 0;
	static int run = 0;
	
	Node bboxes = new Node("debug bboxes");
	Node navs = new Node("debug navs");
	Node debugNodes = new Node("debugs");
	Node scene = new Node("scene");
	Node walker = null;
	private Material matWireframe;

	EntityNavigationManager enm;
	TileTerrainManager tm;
	SceneManager sm;
	PositioningSystem ps;
	private EntityManager em;
	private JmeUpdateSystem js;
//	private PositioningComponent pc;
	private Singleton sin = Singleton.get();
	
//	private Material grass_mat;
	
	
    public static void main(String[] args){
        TestTileInTestareaVisOnly app = new TestTileInTestareaVisOnly();
        app.start();
    }

    @Override
    public void simpleInitApp() {

    	sin.init(TileTerrainManager.get());
    	/*
human fighter 
			 -71453,258305,-3104
jme tile          142/207
l2j tile          17/25
human mage -90918,248070,-3570

elf fighter/mage 46115,41141,-3440
jme tile          171/154
l2j tile          21/19

darkelf fighter/mage 28456, 10997, -4224
jme tile          166/146
l2j tile          20/18
dwarf 108512,-174026,-400
jme tile          186/101
l2j tile          23/12
orc fighter/mage -56693,-113610,-690
jme tile          146/116
l2j tile          18/14
kamael -125464,37776,1152
jme tile          129/153
l2j tile          16/19


testarea:

-9916.567f, 33.88786f, 8376.85f

    	 * */
    	cam.setLocation(new Vector3f(-9856.284f, 17.075874f, 8463.943f));//ServerValues.getClientCoords(-71453,258305,-3104));//
    	cam.setFrustumFar(1000f);
    	cam.setFrustumNear(1f);
    	cam.lookAt(new Vector3f(-9902.244f, 19.241596f, 8288.143f), Vector3f.UNIT_Y);
    	flyCam.setMoveSpeed(50f);
    	
		assetManager = sin.getAssetManager().getJmeAssetMan();
    	enm = sin.getNavManager();
    	enm.USE_OPTIMZED_PATH = true;
    	em = sin.getEntityManager();
    	tm = (TileTerrainManager) sin.getTerrainManager();
    	tm.setLoadedAtOrigin(true);//models not at world coords (all models != nav)
    	sm = sin.getSceneManager();
    	js = JmeUpdateSystem.get();
    	ps = sin.getPosSystem();
		tm.update(cam.getLocation());
    	rootNode.attachChild(debugNodes);
    	
        matWireframe = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matWireframe.setColor("Color", ColorRGBA.Green);
        matWireframe.getAdditionalRenderState().setWireframe(true);

        DirectionalLight light = new DirectionalLight();
        light.setDirection((new Vector3f(-0.5f, -1f, -0.5f)).normalize());
        rootNode.addLight(light);

        AmbientLight ambLight = new AmbientLight();
        ambLight.setColor(new ColorRGBA(0.8f, 0.8f, 0.8f, 1f));
        rootNode.addLight(ambLight);
    	
//        grass_mat = assetManager.loadMaterial("/vegetation/grass/grass/grass.j3m");
    	
    	rootNode.attachChild(scene);
    	sm.setRoot(scene);
//    	tm.addSkyDome(cam);
    	tm.addSkyDome();
    	
//		FilterPostProcessor fpp = new FilterPostProcessor(Singleton.get().getAssetManager().getJmeAssetMan());
//		SSAOFilter ssaoFilter = new SSAOFilter(12.940201f, 43.928635f,
//				0.32999992f, 0.6059958f);
//		fpp.addFilter(ssaoFilter);
//		viewPort.addProcessor(fpp);

//    	while(enm.getMeshCount()<4)
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	
    	System.out.println("EntityNavManager finished"); 
    	
        inputManager.addListener(this, "print_scenegraph", "print_cam_location", "print_bboxes", "toggle_navmesh", "drop_a_box", "toggle_navbodermesh");
        inputManager.addMapping("print_scenegraph", new KeyTrigger(KeyInput.KEY_F6));
        inputManager.addMapping("print_cam_location", new KeyTrigger(KeyInput.KEY_F1));
        inputManager.addMapping("print_bboxes", new KeyTrigger(KeyInput.KEY_F7));
        inputManager.addMapping("toggle_navmesh", new KeyTrigger(KeyInput.KEY_F8));
        inputManager.addMapping("drop_a_box", new KeyTrigger(KeyInput.KEY_F9));
        inputManager.addMapping("toggle_navbodermesh", new KeyTrigger(KeyInput.KEY_F10));
        
        rootNode.attachChild(bboxes);
        rootNode.attachChild(navs);
    }
    

	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		if(name.equals("print_scenegraph") && !isPressed){
			printHierarchy(rootNode, "");
		} else if(name.equals("print_cam_location") && !isPressed){
			printCamLocation();
		} else if(name.equals("print_bboxes") && !isPressed){
			toggelBBoxes();
		} else if(name.equals("toggle_navmesh") && !isPressed){
			toggelNavMeshes();
		} else if(name.equals("drop_a_box") && !isPressed){
			dropBox();
		} else if(name.equals("toggle_navbodermesh") && !isPressed){
			toggelNavBorderMeshes();
		}
	}
	


	protected void printHierarchy(Spatial n, String indent) {
		System.out.println(indent+n.getName()+":"+n.getClass()+" at "+n.getWorldTranslation()+ " bounds:"+n.getWorldBound());
		if(n instanceof Node)
			for(Spatial c : ((Node)n).getChildren()){
//				if(c instanceof Geometry){
//					c.setCullHint(Spatial.CullHint.Dynamic);
//			        c.setQueueBucket(RenderQueue.Bucket.Transparent);
//					Material m = ((Geometry) c).getMaterial();
//					StringBuilder s = new StringBuilder();
//					s.append("Def ").append(m.getMaterialDef().getAssetName()).append(" ");
//					s.append("Asset ").append(m.getAssetName()).append(" ");
//					s.append("Sort ").append(m.getSortId()).append(" ");
//					s.append("Params ");
//					for(MatParam p : m.getParams())
//						s.append(p.getName()).append(":").append(p.getValue()).append("\n");
//					RenderState rs = m.getAdditionalRenderState();
//					s.append("\n");
//					s.append(rs.toString());
//					if(rs.getBlendMode().equals(BlendMode.Alpha)){
//						rs.setBlendMode(BlendMode.Off);
//						rs.setColorWrite(true);
//						rs.setDepthTest(true);
//						m.setBoolean("UseMaterialColors", false);
//						m.setBoolean("UseAlpha", false);
//						m.setFloat("Shininess",0f);
//					}
//					m.setTransparent(true);
////					System.out.println(s.toString());
//					if(c.getName().startsWith("weed1")){
//						c.setMaterial(grass_mat);
//					}
//				}
				printHierarchy(c, indent+" ");
			}
		
		for(int i = 0; i<n.getNumControls(); i++)
			System.out.println(indent+"Controller:"+n.getControl(i).getClass());
	}
    
    
    static float t=0f;
    
    @Override
	public void simpleUpdate(float tpf){
    	
    	//not by singleton but by selected systems
    	tm.update(cam.getLocation());
    	sm.update(tpf);
    	ps.update(tpf);
    	js.update(tpf);
    	
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
    
    
    private void toggelBBoxes(){
    	if(bboxes.getChildren().size()<=0){
    		addBBoxes(rootNode);
    	}
    	else if (bboxes.getChildren().size()>0){
    		removeBBoxesFromRoot();
    	}
    }
	private void removeBBoxesFromRoot() {
		bboxes.detachAllChildren();
	}


	private void addBBoxes(Node n) {
		for(Spatial s : n.getChildren()){
			if(s instanceof Geometry){
				Node a = s.getParent();
				if(a==null)return;
				BoundingVolume bound = ((Geometry) s).getModelBound();
				if(bound instanceof BoundingBox) {
					WireBox b = new WireBox(((BoundingBox) bound).getXExtent(), ((BoundingBox) bound).getYExtent(), ((BoundingBox) bound).getZExtent());
					Geometry g = new Geometry(null, b);
					g.setLocalTransform(s.getWorldTransform());
					g.setMaterial(matWireframe);
					bboxes.attachChild(g);
				}
			}
			if(s instanceof Node) {
				addBBoxes((Node) s);
			}
		}
	}
	   
    private void toggelNavMeshes(){
    	if(navs.getChild("NavMeshes") != null){
    		navs.detachChildNamed("NavMeshes");
    	} else {
    		Node node = new Node("NavMeshes");
    		TiledNavMesh[] array = Singleton.get().getNavManager().getNavMeshes().toArray(new TiledNavMesh[0]);
    		for(TiledNavMesh t : array){
    			Geometry g = t.getDebugMesh();
    			g.setMaterial(matWireframe);
    			node.attachChild(g);
    		}
    		navs.attachChild(node);		
    	}
    }


    
    private void dropBox() {
    	Geometry g = new Geometry("Box",new Box());
    	g.setMaterial(matWireframe);
    	Vector3f pos = cam.getLocation().clone();
    	sin.getNavManager().snapToGround(pos);
    	sin.getSceneManager().changeAnyNode(rootNode, g, Action.ADD);
    	
	}

	private void toggelNavBorderMeshes() {
    	if(navs.getChild("NavBorderMeshes") != null){
    		navs.detachChildNamed("NavBorderMeshes");
    	} else {
    		Node node = new Node("NavBorderMeshes");
    		Material mat = matWireframe.clone();
            mat.setColor("Color", ColorRGBA.Blue);
    		TiledNavMesh[] array = Singleton.get().getNavManager().getNavMeshes().toArray(new TiledNavMesh[0]);
    		for(TiledNavMesh t : array){
    			Geometry g = t.getDebugBorderMesh();
    			g.setMaterial(mat);
    			node.attachChild(g);
    		}
    		navs.attachChild(node);
    	}
    }
    private void printCamLocation() {
    	System.out.println("Camera loc.:"+cam.getLocation()+ " @ server:"+ServerValues.getServerString(cam.getLocation().x, cam.getLocation().y, cam.getLocation().z));
	}
}
