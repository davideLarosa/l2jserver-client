package com.l2client.test;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
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

public class TestTile1 extends SimpleApplication {
	
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
        TestTile1 app = new TestTile1();
        app.start();
    }

    @Override
    public void simpleInitApp() {
    	sin.init(TileTerrainManager.get());
    	cam.setLocation(new Vector3f(390f,10f,10f));
    	cam.setFrustumFar(1000f);
    	cam.setFrustumNear(1f);
    	cam.lookAt(new Vector3f(256f,-50,0), Vector3f.UNIT_Y);
    	flyCam.setMoveSpeed(50f);
    	
		assetManager = sin.getAssetManager().getJmeAssetMan();
    	enm = sin.getNavManager();
    	enm.USE_OPTIMZED_PATH = true;
    	em = sin.getEntityManager();
    	tm = (TileTerrainManager) sin.getTerrainManager();
    	tm.setLoadedAtOrigin(false);//models already at world coords (nav & environment)
    	sm = sin.getSceneManager();
    	js = sin.getJmeSystem();
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

    	while(enm.getMeshCount()<9)
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	
    	
    	Box b = new Box(.5f,1f,.5f);
    	Geometry walker = new Geometry("wakler", b);
    	Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        walker.setMaterial(mat);
//        269.5173, -284.07605, 87.175156 to 476.54184, -171.21466, 454.62576
        Entity e = placeObject(new Vector3f(270f,-280f,90f), new Vector3f(375f,-170f,455f));
        if(e != null){
        	e.attachChild(walker);
        	rootNode.attachChild(e);
        	this.walker = e;
        }

    	
    	Box b2 = new Box(.6f, .3f, .6f);
    	Geometry g2 = new Geometry("tgt", b2);
    	Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", ColorRGBA.Green);
        g2.setMaterial(mat2);
        Vector3f p = new Vector3f(375f,-170f,455f);
        enm.snapToGround(p);
        g2.setLocalTranslation(p);
    	rootNode.attachChild(g2);
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

//    	
    	t+=tpf;
    	if(t>=4f){
//
    		t=0f;
System.out.println("Cam@"+cam.getLocation());    
System.out.println("Walker@:"+walker.getLocalTranslation());
PositioningComponent pc = (PositioningComponent)em.getComponent(((Entity)walker).getId(), PositioningComponent.class);
if(pc != null){
	
		System.out.print("Ent 1@:"+pc.position+" path:"+pc.path);
		if(pc.nextWayPoint != null)
			System.out.println(" heading:"+pc.nextWayPoint.position);
		else
			System.out.println(" heading: not moving");
		
		if(pc.path != null && debugNodes.getQuantity()<=0)
			NavTestHelper.debugShowPath(assetManager, debugNodes, pc.path);
    		}
    	}
    }
    
    private Entity placeObject(final Vector3f position, final Vector3f destination/*can be null*/){
    	final Entity ent =  createEntity(position);

    	try {
			
			Thread t = new Thread(new Runnable() {
				
				@Override
				public void run() {
			        try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
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
					}
				}
			});
			t.start();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ent;
    	
    }
    static int id = 1;

	private Entity createEntity(Vector3f pos) {

		Entity e = em.createEntity(id++);
		if (e != null) {
			Cell c = enm.FindClosestCell(pos, false);
			pc = new PositioningComponent();
			if (c != null) {
				pc.cell = c.getId();
				c.MapVectorHeightToCell(pos);
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
