package com.l2client.test;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.l2client.app.Assembler;
import com.l2client.app.ExtendedApplication;
import com.l2client.gui.AssemblerSideBar;
import com.l2client.util.PartSetManager;

public class TestAssembler2SideBar extends ExtendedApplication {


    private Assembler assembler;
	private double time;
	PartSetManager man = PartSetManager.get();

    public void simpleInitApp() {
    	
    	man.loadParts("megaset.csv");
         
        //move cam a bit closer
        cam.setLocation(cam.getLocation().mult(0.5f));
        inputManager.setCursorVisible(true);
        
        DirectionalLight dr = new DirectionalLight();
        dr.setColor(ColorRGBA.White);
        dr.setDirection(new Vector3f(1, 0 , 1));
        
        AmbientLight am = new AmbientLight();
        am.setColor(ColorRGBA.White);
        rootNode.addLight(am);
        rootNode.addLight(dr);

        setupScene();

    }
 
    /**
     * add terrain
     */
    private void setupScene() {
    	
        assembler = new Assembler();
        rootNode.attachChild(assembler.getModel());
 
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				AssemblerSideBar bar = new AssemblerSideBar(assembler);
//				bar.setInputHandler(input);
				JFrame frame = new JFrame();
				frame.getContentPane().add(bar);
				frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				frame.pack();
				frame.setVisible(true);
			}
		});

//    	printHierarchy(v, "");
		cam.setLocation(Vector3f.ZERO.add(0.34487608f, 3.5889f, 4.985938f));
		cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
		cam.update();
    }
 
 
    private void printHierarchy(Spatial n, String indent) {
		System.out.println(indent+n.getName()+":"+n.getClass());
		if(n instanceof Node)
			for(Spatial c : ((Node)n).getChildren())
				printHierarchy(c, indent+" ");
		
		for(int i=0;i<n.getNumControls();i++)
			System.out.println(indent+"Controller:"+n.getControl(i).getClass());
	}

	/**
     * Entry point
     */
    public static void main(String[] args) {
    	TestAssembler2SideBar app = new TestAssembler2SideBar();
        app.start();
    }
}

