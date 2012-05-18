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
import com.l2client.component.JmeUpdateSystem;
import com.l2client.component.PositioningComponent;
import com.l2client.component.PositioningSystem;
import com.l2client.controller.SceneManager;
import com.l2client.controller.area.AreaTerrainManager;
import com.l2client.controller.entity.Entity;
import com.l2client.controller.entity.EntityManager;
import com.l2client.navigation.Cell;
import com.l2client.navigation.EntityNavigationManager;
import com.l2client.navigation.NavTestHelper;
import com.l2client.navigation.Path;

public class TestRegion4 extends SimpleApplication {
	
	static float upd = 0;
	static int run = 0;
	
	Node debugNodes = new Node("debugs");
	Node scene = new Node("scene");
	Node walker = null;

	EntityNavigationManager enm;
	AreaTerrainManager am;
	SceneManager sm;
	PositioningSystem ps;
	private EntityManager em;
	private JmeUpdateSystem js;
	private PositioningComponent pc;
    public static void main(String[] args){
        TestRegion4 app = new TestRegion4();
        app.start();
    }

    @Override
    public void simpleInitApp() {
    	cam.setLocation(new Vector3f(390f,10f,10f));
    	cam.setFrustumFar(1000f);
    	cam.setFrustumNear(1f);
    	cam.lookAt(new Vector3f(256f,0,0), Vector3f.UNIT_Y);
    	flyCam.setMoveSpeed(50f);
    	
		assetManager = AssetManager.getInstance().getJmeAssetMan();
    	enm = EntityNavigationManager.get();
    	em = EntityManager.get();
    	am = AreaTerrainManager.get();
    	sm = SceneManager.get();
    	js = JmeUpdateSystem.get();
    	ps = PositioningSystem.get();
		am.update(cam.getLocation());
    	rootNode.attachChild(debugNodes);

        DirectionalLight light = new DirectionalLight();
        light.setDirection((new Vector3f(-0.5f, -1f, -0.5f)).normalize());
        rootNode.addLight(light);

        AmbientLight ambLight = new AmbientLight();
        ambLight.setColor(new ColorRGBA(0.2f, 0.2f, 0.2f, 1f));
        rootNode.addLight(ambLight);
    	

    	
    	rootNode.attachChild(scene);
    	sm.setRoot(scene);

    	while(enm.getMeshCount()<8)
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
        Entity e = placeObject(new Vector3f(257f,2.2f,0f), new Vector3f(389f,0f,0f));
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
        Vector3f p = new Vector3f(256f,2.2f,0f);
        enm.snapToGround(p);
        g2.setLocalTranslation(p);
    	rootNode.attachChild(g2);
    }
    static float t=0f;
    
    @Override
	public void simpleUpdate(float tpf){
    	
    	
    	am.update(cam.getLocation());
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

		Entity e = EntityManager.get().createEntity(id++);
		if (e != null) {
			Cell c = EntityNavigationManager.get().FindClosestCell(pos, false);
			pc = new PositioningComponent();
			if (c != null) {
				pc.cell = c;
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
