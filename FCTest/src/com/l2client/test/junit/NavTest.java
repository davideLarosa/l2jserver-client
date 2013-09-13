package com.l2client.test.junit;

import junit.framework.TestCase;

import com.jme3.math.Vector3f;
import com.l2client.app.Singleton;
import com.l2client.controller.area.IArea;
import com.l2client.navigation.EntityNavigationManager;
import com.l2client.navigation.NavTestHelper;
import com.l2client.navigation.Path;
import com.l2client.navigation.TiledNavMesh;

public class NavTest extends TestCase {

	public void testSelf(){
    	EntityNavigationManager em = Singleton.get().getNavManager();

        
        TiledNavMesh n1 = getNavMesh("0/0", Vector3f.ZERO);
        em.attachMesh(n1);
        TiledNavMesh n2 = getNavMesh(""+IArea.TERRAIN_SIZE+"/0", new Vector3f(IArea.TERRAIN_SIZE,0,0));
        em.attachMesh(n2);
        TiledNavMesh n3 = getNavMesh("0/-"+IArea.TERRAIN_SIZE, new Vector3f(0,0,-IArea.TERRAIN_SIZE));
        em.attachMesh(n3);        

        TiledNavMesh n11 = em.getNavMesh(new Vector3f(10,0,10));
        System.out.println("Navmesh with 10/10 in it is:"+n11+" ? 0/0 ?");
        assertEquals(n11, n1);
        TiledNavMesh n22 = em.getNavMesh(new Vector3f(IArea.TERRAIN_SIZE_HALF+10,0,10));
        System.out.println("Navmesh with "+(IArea.TERRAIN_SIZE_HALF+10)+"/10 in it is:"+n22+" ? "+IArea.TERRAIN_SIZE+"/0 ?");
        assertEquals(n22, n2);
        TiledNavMesh n33 = em.getNavMesh(new Vector3f(10,0,-IArea.TERRAIN_SIZE_HALF-10));
        System.out.println("Navmesh with 10/-"+(IArea.TERRAIN_SIZE_HALF+10)+" in it is:"+n33+" ? 0/-"+IArea.TERRAIN_SIZE+" ?");
        assertEquals(n33, n3);
        TiledNavMesh n44 = em.getNavMesh(new Vector3f(10,0,10));
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
    private TiledNavMesh getNavMesh(String name, Vector3f worldtrans){
    	TiledNavMesh m = new TiledNavMesh();
    	m.loadFromData(new Vector3f[] {new Vector3f(-IArea.TERRAIN_SIZE_HALF,0,IArea.TERRAIN_SIZE_HALF),
    			new Vector3f(-IArea.TERRAIN_SIZE_HALF,0,-IArea.TERRAIN_SIZE_HALF), 
    			new Vector3f(IArea.TERRAIN_SIZE_HALF,0,-IArea.TERRAIN_SIZE_HALF), 
    			new Vector3f(IArea.TERRAIN_SIZE_HALF,0,IArea.TERRAIN_SIZE_HALF)}, 
    			new short[][] {{0,2,1},{0,3,2}}, worldtrans);
    	return m;
    }

}
