package com.l2client.test;

import com.jme3.app.SimpleApplication;
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
import com.jme3.scene.shape.Box;
import com.l2client.app.Singleton;
import com.l2client.component.JmeUpdateSystem;
import com.l2client.component.PositioningComponent;
import com.l2client.component.PositioningSystem;
import com.l2client.controller.SceneManager;
import com.l2client.controller.area.TileTerrainManager;
import com.l2client.controller.entity.Entity;
import com.l2client.controller.entity.EntityManager;
import com.l2client.navigation.Cell;
import com.l2client.navigation.EntityNavigationManager;
import com.l2client.navigation.NavTestHelper;
import com.l2client.navigation.Path;
import com.l2client.navigation.TiledNavMesh;

public class TestTileInTestarea extends SimpleApplication implements ActionListener {
	
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
	private PositioningComponent pc;
	private Singleton sin = Singleton.get();
	
	
    public static void main(String[] args){
        TestTileInTestarea app = new TestTileInTestarea();
        app.start();
    }

    @Override
    public void simpleInitApp() {

    	sin.init(TileTerrainManager.get());
    	cam.setLocation(new Vector3f(-9980f,50f,8450f));
    	cam.setFrustumFar(1000f);
    	cam.setFrustumNear(1f);
    	cam.lookAt(new Vector3f(-9980f,-50f,8450), Vector3f.UNIT_Y);
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
        ambLight.setColor(new ColorRGBA(0.2f, 0.2f, 0.2f, 1f));
        rootNode.addLight(ambLight);
    	

    	
    	rootNode.attachChild(scene);
    	sm.setRoot(scene);

//    	while(enm.getMeshCount()<4)
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	
    	System.out.println("EntityNavManager finished"); 
    	
    	Box b = new Box(.5f,1f,.5f);
    	Geometry walker = new Geometry("wakler", b);
    	Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        walker.setMaterial(mat);
//        269.5173, -284.07605, 87.175156 to 476.54184, -171.21466, 454.62576
        //Entity e = placeObject(new Vector3f(-9969.057f, 15.097652f, 8429.296f), new Vector3f(-9979.581f, 28.097652f, 8420.083f));
        Entity e = placeObject(new Vector3f(-9890.341f, 14.610833f, 8302.891f), new Vector3f(-9979.581f, 28.097652f, 8420.083f));
        if(e != null){
        	e.attachChild(walker);
        	rootNode.attachChild(e);
        	this.walker = e;
        }

        System.out.println("Entity placed"); 
        
    	Box b2 = new Box(.3f, 1.3f, .3f);
    	Geometry g2 = new Geometry("tgt", b2);
    	Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", ColorRGBA.Green);
        g2.setMaterial(mat2);
        Vector3f p = new Vector3f(-9979.581f, 28.097652f, 8420.083f);
        enm.snapToGround(p);
        g2.setLocalTranslation(p);
    	rootNode.attachChild(g2);
    	
    	System.out.println("Target placed at:"+p); 
        inputManager.addListener(this, "print_scenegraph");
        inputManager.addMapping("print_scenegraph", new KeyTrigger(KeyInput.KEY_F6));
        
		if(pc.path != null)
			NavTestHelper.debugShowPath(assetManager, debugNodes, pc.path);
		

    	for(TiledNavMesh mesh : enm.getNavMeshes()){
    		NavTestHelper.debugShowBox(assetManager, debugNodes, mesh.getPosition(), ColorRGBA.White, 128f,0.5f,128f);
    	}

    }
    

	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		if(name.equals("print_scenegraph") && !isPressed){
			printHierarchy(rootNode, "");
		}
	}
	
    protected void printHierarchy(Spatial n, String indent) {
		System.out.println(indent+n.getName()+":"+n.getClass()+" at "+n.getWorldTranslation()+ " bounds:"+n.getWorldBound());
		if(n instanceof Node)
			for(Spatial c : ((Node)n).getChildren())
				printHierarchy(c, indent+" ");
		
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

    	t+=tpf;
    	if(t>=4f){
    		t=0f;
			System.out.println("Cam@"+cam.getLocation());    
			System.out.println("Walker@:"+walker.getLocalTranslation());
			PositioningComponent pc = (PositioningComponent)em.getComponent(((Entity)walker).getId(), PositioningComponent.class);
			if(pc != null){
				System.out.print("Ent 1@:"+pc.position+" path:"+pc.path);
				if(pc.nextWayPoint != null)
					System.out.println(" heading:"+pc.nextWayPoint.Position);
				else
					System.out.println(" heading: not moving");
			
			}
    	}
    }
    
    private Entity placeObject(final Vector3f position, final Vector3f destination/*can be null*/){
    	final Entity ent =  createEntity(position);

					if(ent != null && destination != null){
				        Path pa = new Path();
				        PositioningComponent pc = (PositioningComponent) em.getComponent(ent.getId(), PositioningComponent.class);
				        if(pc != null){
					        boolean foundPath = enm.buildNavigationPath(pa, pc.position, destination);
					        if(foundPath){
					        	NavTestHelper.printPath(pa);
					        	pc.initByWayPoint(pa);
					        	pc.acc =0f;
					        	pc.direction = Vector3f.ZERO;
					        	pc.heading = 0f;
					        	pc.maxAcc = 2f;
					        	pc.maxDcc = 3f;
					        	pc.maxSpeed = 4f;
					        	pc.speed = 0f;
					        	}
					        }
						} else
							System.out.println("No PosComp on Entity "+ent.getId());

		return ent;
    	
    }
    static int id = 1;

	private Entity createEntity(Vector3f pos) {

		Entity e = em.createEntity(id++);
		if (e != null) {
			Cell c = enm.FindClosestCell(pos, false);
			pc = new PositioningComponent();
			if (c != null) {
				pc.cell = c;
				c.MapVectorHeightToCell(pos);
				System.out.println("Entity placed at "+pos); 
			} else {
				System.out.println("No cell found near "+pos);
			}
			pc.position.set(pos);
			pc.heightOffset = 1f;
			
			em.addComponent(e.getId(), pc);
			ps.addComponentForUpdate(pc);
			js.addComponentForUpdate(pc);
	    	
//	    	e.setLocalTranslation(pos);
	    	
		}
		return e;
	}
}
