/*
 * Copyright (c) 2009-2010 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
import com.l2client.asset.AssetManager;
import com.l2client.controller.SceneManager;
import com.l2client.controller.area.AreaTerrainManager;
import com.l2client.navigation.EntityNavigationManager;

public class TestRegion3 extends SimpleApplication {
	
	static float upd = 0;
	static int run = 0;
	
	Node debugNodes = new Node("debugs");
	Node scene = new Node("scene");

	EntityNavigationManager em;
	AreaTerrainManager am;
	SceneManager sm;
    public static void main(String[] args){
        TestRegion3 app = new TestRegion3();
        app.start();
    }

    @Override
    public void simpleInitApp() {
   	
		assetManager = AssetManager.getInstance().getJmeAssetMan();

    	em = EntityNavigationManager.get();
    	am = AreaTerrainManager.get();
    	sm = SceneManager.get();
    	rootNode.attachChild(debugNodes);

        DirectionalLight light = new DirectionalLight();
        light.setDirection((new Vector3f(-0.5f, -1f, -0.5f)).normalize());
        rootNode.addLight(light);

        AmbientLight ambLight = new AmbientLight();
        ambLight.setColor(new ColorRGBA(0.2f, 0.2f, 0.2f, 1f));
        rootNode.addLight(ambLight);
    	
    	cam.setLocation(cam.getLocation().add(0, 120, 50));
    	cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
    	cam.setFrustumFar(1000f);
    	cam.setFrustumNear(1f);
    	flyCam.setMoveSpeed(50f);
    	
    	Box b = new Box(1f, 2f, 1f);
    	Geometry g = new Geometry("RefBox", b);
    	Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        g.setMaterial(mat);
        g.setLocalTranslation(256f,2.2f,0f);
    	rootNode.attachChild(g);
    	
    	rootNode.attachChild(scene);
    	sm.setRoot(scene);
    }
    static float t=0f;
    
    @Override
	public void simpleUpdate(float tpf){
    	super.simpleUpdate(tpf);
    	am.update(cam.getLocation());
    	sm.update(tpf);

    	t+=tpf;
    	if(t>=10f){

    		t=0f;
System.out.println("Cam@"+cam.getLocation());    
//System.out.println("EntityNavigationManger:"+EntityNavigationManager.get().toString());
    	}
    }
}
