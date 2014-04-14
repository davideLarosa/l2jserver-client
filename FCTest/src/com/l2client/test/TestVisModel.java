package com.l2client.test;

import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.l2client.app.ExtendedApplication;
import com.l2client.app.Singleton;
import com.l2client.model.jme.VisibleModel;
import com.l2client.model.network.NewCharSummary;

public class TestVisModel extends ExtendedApplication {


    /*
     * (non-Javadoc)
     *
     * @see com.jme.app.SimpleGame#initGame()
     */
    public void simpleInitApp() {
        setupScene();
    }
 
    /**
     * add terrain
     */
    private void setupScene() {
    	Singleton.get().init(null);
    	
        DirectionalLight dr = new DirectionalLight();
        dr.setColor(ColorRGBA.White);
        dr.setDirection(new Vector3f(1, 0 , 1));
        
        AmbientLight am = new AmbientLight();
        am.setColor(ColorRGBA.White);
        rootNode.addLight(am);
        rootNode.addLight(dr);
    	NewCharSummary c = new NewCharSummary();
    	c.name = "Myname";
    	VisibleModel v = new VisibleModel(c);
    	rootNode.attachChild(v);
    	v.attachVisuals();
    	v.rotateUpTo(Vector3f.UNIT_Z.mult(-1f));
    	
    	printHierarchy(rootNode, "");
    }
 
 
    private void printHierarchy(Spatial n, String indent) {
		System.out.println(indent+n.getName()+":"+n.getClass());
		if(n instanceof Node)
			for(Spatial c : ((Node)n).getChildren())
				printHierarchy(c, indent+" ");
		
		for(int i=0;i< n.getNumControls();i++)
			System.out.println(indent+"Controller:"+n.getControl(i).getClass());
	}

	/**
     * Entry point
     */
    public static void main(String[] args) {
    	TestVisModel app = new TestVisModel();
        app.start();
    }
}
