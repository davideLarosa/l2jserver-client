package com.l2client.navigation;

import junit.framework.TestCase;

import com.jme3.math.Vector3f;

public class CellTest extends TestCase {

	public void testSelf(){
		
//		Vector3f[] vs = {new Vector3f(0,0,0),new Vector3f(1,0,0),new Vector3f(0,0,1),new Vector3f(1,0,1)};
		TiledNavMesh m = new TiledNavMesh();
    	m.loadFromData(new Vector3f[] {new Vector3f(-1,0,1),new Vector3f(-1,0,-1), new Vector3f(1,0,-1), new Vector3f(1,0,1)}, new short[][] {{0,2,1},{0,3,2}}, Vector3f.ZERO);
    	assertTrue(m.getCell(0).m_Link[0]!= null);
    	assertTrue(m.getCell(0).m_WallMidpoint[0].equals(m.getCell(1).m_WallMidpoint[2]));
    	assertTrue(m.getCell(0).IsPointInCellCollumn(new Vector3f(-0.2f,0,-0.8f)));
    	assertTrue(m.getCell(1).IsPointInCellCollumn(new Vector3f(0.2f,0,0.8f)));
	}

}
