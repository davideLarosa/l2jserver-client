package com.l2client.test;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.l2client.app.Singleton;
import com.l2client.component.JmeUpdateSystem;
import com.l2client.component.PositioningSystem;
import com.l2client.controller.SceneManager;
import com.l2client.controller.area.TileTerrainManager;
import com.l2client.controller.entity.EntityManager;
import com.l2client.navigation.EntityNavigationManager;

public class TestTileInTestareaVisOnly extends SimpleApplication implements ActionListener {
	
	static float upd = 0;
	static int run = 0;
	
	Node debugNodes = new Node("debugs");
	Node scene = new Node("scene");
	Node walker = null;

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
    	cam.setLocation(new Vector3f(-9916.567f, 33.88786f, 8376.85f));
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

        DirectionalLight light = new DirectionalLight();
        light.setDirection((new Vector3f(-0.5f, -1f, -0.5f)).normalize());
        rootNode.addLight(light);

        AmbientLight ambLight = new AmbientLight();
        ambLight.setColor(new ColorRGBA(0.8f, 0.8f, 0.8f, 1f));
        rootNode.addLight(ambLight);
    	
//        grass_mat = assetManager.loadMaterial("/vegetation/grass/grass/grass.j3m");
    	
    	rootNode.attachChild(scene);
    	sm.setRoot(scene);
    	tm.addSkyDome(cam);
    	
    	
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
    	
        inputManager.addListener(this, "print_scenegraph", "print_cam_location");
        inputManager.addMapping("print_scenegraph", new KeyTrigger(KeyInput.KEY_F6));
        inputManager.addMapping("print_cam_location", new KeyTrigger(KeyInput.KEY_F1));
    }
    

	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		if(name.equals("print_scenegraph") && !isPressed){
			printHierarchy(rootNode, "");
		} else if(name.equals("print_cam_location") && !isPressed){
			System.out.println("Camera loc.:"+cam.getLocation());
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
    
}
