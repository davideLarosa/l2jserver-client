package com.l2client.controller.entity;


import junit.framework.TestCase;


public class SpatialPointIndexTest extends TestCase {

	public void testInsertRemoveSize1(){
		SpatialPointIndex sp = new SpatialPointIndex(1);
		ISpatialPointing p = getTestobject(2, 3, 0);
		sp.put(p);
		assertEquals(1,sp.bucketsX.size());
		assertEquals(1,sp.bucketsZ.size());
		sp.remove(p);
		assertEquals(0,sp.bucketsX.size());
		assertEquals(0,sp.bucketsZ.size());
	}
	
	public void testInsertMoveRemoveSize1(){
		SpatialPointIndex sp = new SpatialPointIndex(1);
		testClass p = (testClass)getTestobject(2, 3, 0);
		sp.put(p);
		assertEquals(1,sp.bucketsX.size());
		assertEquals(1,sp.bucketsZ.size());
		p.x = 9; p.z = 3;
		sp.update(p);
		assertEquals(1,sp.bucketsX.size());
		assertEquals(1,sp.bucketsZ.size());
		sp.remove(p);
		assertEquals(0,sp.bucketsX.size());
		assertEquals(0,sp.bucketsZ.size());
	}
	
	public void testInsertRemoveSize2(){
		SpatialPointIndex sp = new SpatialPointIndex(1);
		ISpatialPointing p = getTestobject(2, 3, 1);
		sp.put(p);
		assertEquals(3,sp.bucketsX.size());
		assertEquals(3,sp.bucketsZ.size());
		sp.remove(p);
		assertEquals(0,sp.bucketsX.size());
		assertEquals(0,sp.bucketsZ.size());
	}
	
	public void testInsertMoveRemoveSize2(){
		SpatialPointIndex sp = new SpatialPointIndex(5);
		testClass p = (testClass) getTestobject(2, 1, 1);
		sp.put(p);
		assertEquals(1,sp.bucketsX.size());
		assertEquals(1,sp.bucketsZ.size());
		p.x = 0; p.z = 2;
		sp.update(p);
		assertEquals(1,sp.bucketsX.size());
		assertEquals(1,sp.bucketsZ.size());
		sp.remove(p);
		assertEquals(0,sp.bucketsX.size());
		assertEquals(0,sp.bucketsZ.size());
	}
	public void testInsertMoveRemoveAddSize2(){
		SpatialPointIndex sp = new SpatialPointIndex(5);
		testClass p = (testClass) getTestobject(2, 1, 1);
		sp.put(p);
		assertEquals(1,sp.bucketsX.size());
		assertEquals(1,sp.bucketsZ.size());
		p.x = 0; p.z = 2;
		sp.update(p);
		assertEquals(1,sp.bucketsX.size());
		assertEquals(1,sp.bucketsZ.size());
		ISpatialPointing p2 = getTestobject(2, 1, 1);
		sp.put(p2);
		assertEquals(1,sp.bucketsX.size());
		assertEquals(1,sp.bucketsZ.size());
		p.x = 4; p.z = 6;
		sp.update(p);
		assertEquals(2,sp.bucketsX.size());
		assertEquals(2,sp.bucketsZ.size());
		p.x =0; p.z =6;
		sp.update(p);
		assertEquals(1,sp.bucketsX.size());
		assertEquals(2,sp.bucketsZ.size());
		sp.remove(p);
		sp.remove(p2);
		assertEquals(0,sp.bucketsX.size());
		assertEquals(0,sp.bucketsZ.size());
	}
	public void testLookup1(){
		SpatialPointIndex sp = new SpatialPointIndex(1);
		ISpatialPointing p1 = getTestobject(2, 1, 0);
		sp.put(p1);
		ISpatialPointing p2 = getTestobject(-2, 0, 0);
		sp.put(p2);
		ISpatialPointing p3 = getTestobject(4, 2, 0);
		sp.put(p3);
		ISpatialPointing p4 = getTestobject(3, 3, 0);
		sp.put(p4);
		ISpatialPointing[] ret = sp.getObjectsInRange(0, 0, 2);
		assertEquals(2,ret.length);
	}
	public void testLookup2(){
		SpatialPointIndex sp = new SpatialPointIndex(1);
		ISpatialPointing p1 = getTestobject(2, 1, 1);
		sp.put(p1);
		ISpatialPointing p2 = getTestobject(-2, 0, 1);
		sp.put(p2);
		ISpatialPointing p3 = getTestobject(4, 2, 1);
		sp.put(p3);
		ISpatialPointing p4 = getTestobject(3, 3, 1);
		sp.put(p4);
		ISpatialPointing[] ret = sp.getObjectsInRange(0, 0, 2);
		assertEquals(3,ret.length);
	}
	public void testLookup3(){
		SpatialPointIndex sp = new SpatialPointIndex(5);
		ISpatialPointing p1 = getTestobject(2, 1, 1);
		sp.put(p1);
		ISpatialPointing p2 = getTestobject(-2, 0, 1);
		sp.put(p2);
		ISpatialPointing p3 = getTestobject(4, 2, 1);
		sp.put(p3);
		ISpatialPointing p4 = getTestobject(3, 3, 1);
		sp.put(p4);
		ISpatialPointing[] ret = sp.getObjectsInRange(0, 0, 2);
		assertEquals(2,ret.length);
	}
	

	private class testClass implements ISpatialPointing{

		public int x,z,lastX,lastZ,size;
		
		public testClass(int nX, int nZ, int size){
			this.size = size;
			x=lastX=nX;
			z=lastZ=nZ;
		}
		public int getLastX() {
			return lastX;
		}

		public int getLastZ() {
			return lastZ;
		}

		public int getSize() {
			return size;
		}

		public int getX() {
			return x;
		}

		public int getZ() {
			return z;
		}
		
		public void updateLast(){
			lastX = x; lastZ = z;
		}
		
		public String toString(){
			return "testClass x:"+x+" z:"+z;
		}
	}
	public ISpatialPointing getTestobject(int x, int z, int size){
		return new testClass(x,z,size);
	}
}
