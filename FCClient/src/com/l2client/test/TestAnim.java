package com.l2client.test;

import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.l2client.app.Assembler2;
import com.l2client.app.ExtendedApplication;
import com.l2client.app.Singleton;
import com.l2client.component.AnimationSystem;
import com.l2client.component.EnvironmentComponent;
import com.l2client.component.VisualComponent;
import com.l2client.controller.entity.Entity;
import com.l2client.controller.entity.EntityManager;
import com.l2client.model.PartSet;
import com.l2client.util.PartSetManager;

public class TestAnim extends ExtendedApplication {
	
	private static final int _1000 = 1000;

	PartSetManager man = Singleton.get().getPartManager();
	
	
	static float time = 0f;
	static int step = 0;
	
	private PartSet con;

	private AnimationSystem as;

	private EntityManager em;

	private Entity ent;

    public void simpleInitApp() {
    	
    	man.loadParts("megaset.csv");
         
        //move cam a bit closer
        cam.setLocation(cam.getLocation().mult(0.5f));
        inputManager.setCursorVisible(true);
        flyCam.setEnabled(false);
        
        DirectionalLight dr = new DirectionalLight();
        dr.setColor(ColorRGBA.White);
        dr.setDirection(new Vector3f(1, 0 , 1));
        
        AmbientLight am = new AmbientLight();
        am.setColor(ColorRGBA.White);
        rootNode.addLight(am);
        rootNode.addLight(dr);

        setupGUI();
        setupScene();

    }
 
    private void setupGUI() {
    	em = Singleton.get().getEntityManager();
    	as = Singleton.get().getAnimSystem();
	}

    private void setupScene() {

    	//TODO could use getTemplates to switch through models..
    	Node n = Assembler2.getModel3("dwarfwarrior"); //"pelffwarrior");//humanhalberd"); //goblin");

    	if(n != null){	
    		ent = em.createEntity(_1000);
    		
    		Node nn = new Node("intermed");
    		nn.attachChild(n);
    		ent.attachChild(nn);
    		
    		rootNode.attachChild(ent);
    		VisualComponent vis = new VisualComponent();
    		EnvironmentComponent env = new EnvironmentComponent();
    		
    		em.addComponent(ent.getId(), env);
    		em.addComponent(ent.getId(), vis);
    		
    		vis.vis = n;

    		as.addComponentForUpdate(env);
    	}
    }
    
    public void simpleUpdate(float tpf) {
    	time += tpf;
    	if(time > 1.5f && time < 2.0f)
    		do5Update();
    	else if (time > 4f && time < 4.5f)
    		do9Update();
    	else if (time > 15f && time < 15.3f)
    		do12Update();
    	else if (time > 20f && time < 20.6f)
    		do20Update();
    	else if (time > 29f && time < 31.11f)
    		do31Update();
    	else if (time < 60f)
    		do60Update();
    	
    	as.update(tpf);
    }
    
	private void do5Update() {
		if(step != 0)
			return;
		EnvironmentComponent env = (EnvironmentComponent) EntityManager
				.get().getComponent(_1000, EnvironmentComponent.class);
		if (env != null) {
			env.movement = 0;
			env.changed = true;
System.out.println(time+" SHOULD walk");
		}
		step++;
	}
	
	private void do9Update() {
		if(step != 1)
			return;
		step++;
		EnvironmentComponent env = (EnvironmentComponent) EntityManager
				.get().getComponent(_1000, EnvironmentComponent.class);
		if (env != null) {
			env.movement = -1;
			env.changed = true;
System.out.println(time+" SHOULD STOOOOOOOOPPPPP");
		}
	}

	private void do12Update() {
		if(step != 2)
			return;
		step++;
		EnvironmentComponent env = (EnvironmentComponent) EntityManager
				.get().getComponent(_1000, EnvironmentComponent.class);
		if (env != null) {
			env.movement = 1;
			env.changed = true;
System.out.println(time+" SHOULD ruuuuunn");
		}
	}
	
	private void do20Update() {
		if(step != 3)
			return;
		step++;
		EnvironmentComponent env = (EnvironmentComponent) EntityManager
				.get().getComponent(_1000, EnvironmentComponent.class);
		if (env != null) {
			env.movement = -1;
			env.changed = true;
System.out.println(time+" SHOULD STOOOOOOOOPPPPP");
		}
	}
	
	private void do31Update() {
		if(step != 4)
			return;
		step++;
		EnvironmentComponent env = (EnvironmentComponent) EntityManager
				.get().getComponent(_1000, EnvironmentComponent.class);
		if (env != null) {
			env.movement = -1;
			env.changed = true;
System.out.println(time+" SHOULD STOOOOOOOOPPPPP");
		}
	}
	
	private void do60Update() {
		if(step != 5)
			return;
		step++;
		EnvironmentComponent env = (EnvironmentComponent) EntityManager
				.get().getComponent(_1000, EnvironmentComponent.class);
		if (env != null) {
			env.movement = -1;
			env.changed = true;
System.out.println(time+" SHOULD STOOOOOOOOPPPPP");
		}
	}
	
	/**
     * Entry point
     */
    public static void main(String[] args) {
    	TestAnim app = new TestAnim();
        app.start();
    }
   
}
