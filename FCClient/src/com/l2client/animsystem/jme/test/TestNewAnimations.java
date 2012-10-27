package com.l2client.animsystem.jme.test;


import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.jme3.input.RawInputListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.l2client.animsystem.jme.JMEAnimationController;
import com.l2client.animsystem.jme.gui.AnimationInputSwitchSidebar;
import com.l2client.app.Assembler2;
import com.l2client.app.ExtendedApplication;
import com.l2client.app.Singleton;
import com.l2client.util.PartSetManager;

public class TestNewAnimations extends ExtendedApplication {


	int MAX_NODES = 10;
	
	PartSetManager man = Singleton.get().getPartManager();
	
	Node[] nodes = new Node[MAX_NODES];
	
	int currentNode = 0;
	
    /*
     * (non-Javadoc)
     *
     * @see com.jme.app.SimpleGame#initGame()
     */
    public void simpleInitApp() {
    	
        //override mouse input, otherwise you will have a hard time switching to the menu
        //without rotating the model
 		mouseInput.setInputListener(new RawInputListener() {

 			@Override
 			public void onTouchEvent(TouchEvent evt) {
 			}

 			@Override
 			public void onMouseMotionEvent(MouseMotionEvent evt) {
 			}

 			@Override
 			public void onMouseButtonEvent(MouseButtonEvent evt) {
 			}

 			@Override
 			public void onKeyEvent(KeyInputEvent evt) {
 			}

 			@Override
 			public void onJoyButtonEvent(JoyButtonEvent evt) {
 			}

 			@Override
 			public void onJoyAxisEvent(JoyAxisEvent evt) {
 			}

 			@Override
 			public void endInput() {
 			}

 			@Override
 			public void beginInput() {
 			}
 		});
    	
    	man.loadParts("megaset.csv");
 
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f).normalizeLocal());
        rootNode.addLight(sun);
        
        //move cam a bit closer
        cam.setLocation(cam.getLocation().mult(0.5f));

 
        setupScene();

    }
 
    /**
     * add terrain
     */
    private void setupScene() {

    	//TODO could use getTemplates to switch through models..
    	Node n = Assembler2.getModel3("dwarfwarrior"); //humanhalberd"); //goblin");
    	JMEAnimationController con=null;
    	if(n != null){
    		con = n.getControl(JMEAnimationController.class);
    		rootNode.attachChild(n);
//    		n.updateGeometricState();
    	}
    	
    	if(con!= null ){
    		final JMEAnimationController conn = con;
    		SwingUtilities.invokeLater(new Runnable() {

    			public void run() {
    				AnimationInputSwitchSidebar bar = new AnimationInputSwitchSidebar();
    				bar.setTarget(conn);
    				JFrame frame = new JFrame();
    				frame.getContentPane().add(bar);
    				frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    				frame.pack();
    				frame.setVisible(true);
    			}
    		});   		
    	}
    }
    
	/**
     * Entry point
     */
    public static void main(String[] args) {
    	TestNewAnimations app = new TestNewAnimations();
        app.start();
    }

}
