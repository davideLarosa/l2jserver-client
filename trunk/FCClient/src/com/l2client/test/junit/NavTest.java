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

package com.l2client.test.junit;

import junit.framework.TestCase;

import com.jme3.math.Vector3f;
import com.l2client.controller.area.IArea;
import com.l2client.navigation.EntityNavigationManager;
import com.l2client.navigation.NavTestHelper;
import com.l2client.navigation.NavigationMesh;
import com.l2client.navigation.Path;

public class NavTest extends TestCase {

	public void testSelf(){
    	EntityNavigationManager em = EntityNavigationManager.get();

        
        NavigationMesh n1 = getNavMesh("0/0", Vector3f.ZERO);
        em.attachMesh(n1);
        NavigationMesh n2 = getNavMesh(""+IArea.TERRAIN_SIZE+"/0", new Vector3f(IArea.TERRAIN_SIZE,0,0));
        em.attachMesh(n2);
        NavigationMesh n3 = getNavMesh("0/-"+IArea.TERRAIN_SIZE, new Vector3f(0,0,-IArea.TERRAIN_SIZE));
        em.attachMesh(n3);        

        NavigationMesh n11 = em.getNavMesh(new Vector3f(10,0,10));
        System.out.println("Navmesh with 10/10 in it is:"+n11+" ? 0/0 ?");
        assertEquals(n11, n1);
        NavigationMesh n22 = em.getNavMesh(new Vector3f(IArea.TERRAIN_SIZE_HALF+10,0,10));
        System.out.println("Navmesh with "+(IArea.TERRAIN_SIZE_HALF+10)+"/10 in it is:"+n22+" ? "+IArea.TERRAIN_SIZE+"/0 ?");
        assertEquals(n22, n2);
        NavigationMesh n33 = em.getNavMesh(new Vector3f(10,0,-IArea.TERRAIN_SIZE_HALF-10));
        System.out.println("Navmesh with 10/-"+(IArea.TERRAIN_SIZE_HALF+10)+" in it is:"+n33+" ? 0/-"+IArea.TERRAIN_SIZE+" ?");
        assertEquals(n33, n3);
        NavigationMesh n44 = em.getNavMesh(new Vector3f(10,0,10));
        assertEquals(n11, n44);

        Vector3f sPos = new Vector3f(10,0,10);
        Vector3f ePos = new Vector3f(30,0,30);
        Path p = null;
        p= NavTestHelper.findPath(em, sPos, ePos);
        assertNotNull(p);//path inside tile
        assertTrue(NavTestHelper.areCellsConnected(p));
        sPos = new Vector3f(-10,0,-10);
        ePos = new Vector3f(30,0,30);
        p =NavTestHelper.findPath(em, sPos, ePos);//path inside tile
        assertNotNull(p);
        assertTrue(NavTestHelper.areCellsConnected(p));
        assertNotNull(NavTestHelper.walkPath(em, new Vector3f(-2,0,0), new Vector3f(2,0,0), 4f));
        assertNull(NavTestHelper.findPath(em, sPos, new Vector3f(0,0,IArea.TERRAIN_SIZE-1)));//target is no tile
        assertNotNull(NavTestHelper.findPath(em, sPos, new Vector3f(0,0,-IArea.TERRAIN_SIZE-1)));//crossing from one to the other
        assertNotNull(NavTestHelper.walkPath(em, sPos,new Vector3f(0,0,-IArea.TERRAIN_SIZE-1), 0.25f*IArea.TERRAIN_SIZE_HALF));//crossing from one to the other
        assertNull(NavTestHelper.findPath(em, new Vector3f(IArea.TERRAIN_SIZE+30,0,0), new Vector3f(IArea.TERRAIN_SIZE_HALF-4,0,-IArea.TERRAIN_SIZE_HALF-4)));//crossing diagonal but not on the corner, so we have two cross points which are slightly off
        assertNull(NavTestHelper.findPath(em, new Vector3f(-IArea.TERRAIN_SIZE_HALF,0,0), new Vector3f(IArea.TERRAIN_SIZE_HALF,0,1)));//target further way than tile side length, should fail
    }

    /**
     * create a simple quad navmesh 
     * @param name			name of the mesh
     * @param worldtrans	center position of quad
     * @return
     */
    private NavigationMesh getNavMesh(String name, Vector3f worldtrans){
    	NavigationMesh m = new NavigationMesh();
    	m.loadFromData(new Vector3f[] {new Vector3f(-IArea.TERRAIN_SIZE_HALF,0,IArea.TERRAIN_SIZE_HALF),
    			new Vector3f(-IArea.TERRAIN_SIZE_HALF,0,-IArea.TERRAIN_SIZE_HALF), 
    			new Vector3f(IArea.TERRAIN_SIZE_HALF,0,-IArea.TERRAIN_SIZE_HALF), 
    			new Vector3f(IArea.TERRAIN_SIZE_HALF,0,IArea.TERRAIN_SIZE_HALF)}, 
    			new short[][] {{0,2,1},{0,3,2}}, worldtrans);
    	return m;
    }

}
