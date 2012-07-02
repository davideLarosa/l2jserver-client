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

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.export.binary.BinaryImporter;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.l2client.navigation.EntityNavigationManager;
import com.l2client.navigation.NavTestHelper;
import com.l2client.navigation.NavigationMesh;
import com.l2client.navigation.Path;

/**
 * A visualization of pathfinding on a navmesh. numbers in the triangles are the path costs.
 * Red, green, blue lines are the cell links. Green box is the start point, the red boy simulates the goal.
 * The goal will walk the path each 5 sec. showing the way to go until the final destination is reached.
 * The simulation from start to end is looping endlessly.
 * 
 * @author tmi
 *
 */
public class TestNav extends SimpleApplication {
	
	static float upd = 0;
	static int run = 0;
	
	Node debugNodes = new Node("debugs");

	EntityNavigationManager em;
    public static void main(String[] args){
        TestNav app = new TestNav();
        app.start();
    }

    @Override
    public void simpleInitApp() {
    	flyCam.setMoveSpeed(20);
    	assetManager = new DesktopAssetManager(true);
    	em = EntityNavigationManager.get();
    	rootNode.attachChild(debugNodes);
    	cam.setLocation(cam.getLocation().add(0, 50, 50));
    	
    	addNavWithMesh(em,"navtest/grid.0_0.j3o",0, 0, 0);


        rootNode.attachChild(NavTestHelper.getText(assetManager, "0/0", 0, 5f, 0, 2f));
    }
    
    @Override
	public void simpleUpdate(float tpf){
    	
    	if(upd <0f){
    		enqueue(new Callable<Object>() {

				@Override
				public Object call() throws Exception {
					
					debugNodes.detachAllChildren();
		    		Path p = new Path();
					p = NavTestHelper.findPath(em, new Vector3f(1.1f,0,1f), new Vector3f(3.1f,0,3f));
					if(p != null){
					int s = p.m_WaypointList.size();
					for(int i =s-1;i>run;i--)
						p.m_WaypointList.remove(i);
					NavTestHelper.debugShowCost(assetManager, debugNodes, p, ColorRGBA.Cyan);
					NavTestHelper.debugShowPath(assetManager, debugNodes, p);
					NavTestHelper.areCellsConnected(p);
					NavTestHelper.arePointsInCells(p);
					run++;
					if(run > s)
						run = 0;
					}
					return null;
				}
    			
			});

    		upd =5f;
    	}
    	upd -=tpf;
    }

	private void addNavWithMesh(EntityNavigationManager em, String meshFile,
			int x, int y, int z) {
		try {
//			NavigationMesh m = getNavMesh(meshFile+".jnv", 128, x, y, z);
			NavigationMesh m = getMeshNav(meshFile, 128, x, y, z);
			em.attachMesh(m);
			Geometry grid = (Geometry) assetManager.loadAsset(meshFile);
			grid.setLocalTranslation(x, y, z);
//	        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//	        mat.setColor("Color", ColorRGBA.LightGray);
//			grid.setMaterial(mat);
			grid.updateGeometricState();
			rootNode.attachChild(grid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    
    private Spatial getQuad(float size, float x, float y, float z){
    	Quad q = new Quad(size,size,true);
        //Box b = new Box(Vector3f.ZERO, 1, 1, 1);
        Geometry geom = new Geometry("Box", q);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", assetManager.loadTexture("com/l2client/test/material/chterraingrass01_n.dds"));
        geom.setMaterial(mat);
//        geom.setLocalTranslation((-0.5f*size)+x,(-0.5f*size)+y,z);  
        geom.rotate(-FastMath.HALF_PI, 0, 0);
        geom.setLocalTranslation((-0.5f*size)+x,y,(0.5f*size)+z);
        
        return geom;
    }
    
    private NavigationMesh getNavMesh(String file, float size, float x, float y, float z) throws IOException{
    	return (NavigationMesh) BinaryImporter.getInstance().load(new File(file));
    }
    
    private NavigationMesh getMeshNav(String file, float size, float x, float y, float z){
		NavigationMesh m;
		Geometry g00 = (Geometry) assetManager.loadAsset(file);
		if(g00 == null){
			System.out.println("Asset for "+file+"is missing");
			return null;
		}
		g00.setLocalTranslation(x,y,z);
		g00.updateGeometricState();
		m = new NavigationMesh();
        
		m.loadFromGeom(g00);
		return m;
    }
    
    private NavigationMesh getNavMesh(String name, Vector3f worldtrans){
    	NavigationMesh m = new NavigationMesh();
    	m.loadFromData(new Vector3f[] {new Vector3f(-64,0,64),new Vector3f(-64,0,-64), new Vector3f(64,0,-64), new Vector3f(64,0,64)}, new short[][] {{0,2,1},{0,3,2}}, worldtrans);
    	return m;
    }

}
