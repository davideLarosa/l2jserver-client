package com.l2client.test;

import jme3test.post.SSAOUI;

import com.jme3.light.AmbientLight;
import com.jme3.light.Light;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.l2client.app.Assembler2;
import com.l2client.app.ExtendedApplication;
import com.l2client.util.PartSetManager;

public class TestCreateScene extends ExtendedApplication {

    public static void main(String[] args){
    	TestCreateScene app = new TestCreateScene();
        app.start();
    }
	
	public void simpleInitApp() {
		
		

		FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
		SSAOFilter ssaoFilter = new SSAOFilter(12.940201f, 43.928635f,
				0.32999992f, 0.6059958f);
		fpp.addFilter(ssaoFilter);
		SSAOUI ui = new SSAOUI(inputManager, ssaoFilter);
		viewPort.addProcessor(fpp);
		
		Spatial n = assetManager
				.loadModel("scenes/create/create.j3o");
		rootNode.attachChild(n);

		for(Light l :n.getLocalLightList())
			if(l instanceof AmbientLight)
				l.setColor(new ColorRGBA(0.5f,0.5f,0.5f,1.0f));
		
		PartSetManager man = PartSetManager.get();
		man.loadParts("dwarf.csv");
		Spatial n2 = Assembler2.getModel3("DwarfWarriorM");
//		n.setLocalTranslation(.126f,-.1224f, 5.76f);
		n2.setLocalTranslation(.126f, -0.1224f, 7.76f);
		((Node)n).attachChild(n2);
		
		/**
		 * Camera Position: (2.1353703, 0.10786462, 14.364603)
Camera Rotation: (0.0127822235, 0.98611915, 0.13812076, -0.091258995)
Camera Direction: (-0.1764535, 0.27474004, -0.94518876)
		 */
		
		cam.setLocation(new Vector3f(2.1353703f, 0.10786462f, 14.364603f));
		cam.lookAtDirection(new Vector3f(-0.1764535f, 0.27474004f, -0.94518876f), Vector3f.UNIT_Y);
	}

}
