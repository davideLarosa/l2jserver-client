package com.l2client.controller.entity;

import java.util.concurrent.ConcurrentHashMap;

import com.l2client.util.ConcurrentHashSet;


public class SpatialPointIndex {

	int scale = 10;
	ConcurrentHashMap<Integer, ConcurrentHashSet<ISpatialPointing>> bucketsX = new ConcurrentHashMap<Integer, ConcurrentHashSet<ISpatialPointing>>();
	ConcurrentHashMap<Integer, ConcurrentHashSet<ISpatialPointing>> bucketsZ = new ConcurrentHashMap<Integer, ConcurrentHashSet<ISpatialPointing>>();

	public SpatialPointIndex(int scaling){
		scale = scaling;
	}
	
	public void put(ISpatialPointing object){
		for(int i : getObjectsX(object.getX(), object.getSize())){
			if(bucketsX.get(i)!= null){
				bucketsX.get(i).add(object);
			} else {
				ConcurrentHashSet<ISpatialPointing> h = new ConcurrentHashSet<ISpatialPointing>();
				h.add(object);
				bucketsX.put(i, h);
			}
		}
		for(int i : getObjectsZ(object.getZ(), object.getSize())){
			if(bucketsZ.get(i)!= null){
				bucketsZ.get(i).add(object);
			} else {
				ConcurrentHashSet<ISpatialPointing> h = new ConcurrentHashSet<ISpatialPointing>();
				h.add(object);
				bucketsZ.put(i, h);
			}
		}
	}
	
	public void remove(ISpatialPointing object){
		for(int i : getObjectsX(object.getLastX(), object.getSize())){
			ConcurrentHashSet<ISpatialPointing> h = bucketsX.get(i);
			if(h != null){
				h.remove(object);
				if(h.size()<=0){
					bucketsX.remove(i);
					assert(bucketsX.get(i)==null);
					h=null;
				}
			} else {
				System.out.println("remove in X on null bucket");
			}
		}
		for(int i : getObjectsZ(object.getLastZ(), object.getSize())){
			ConcurrentHashSet<ISpatialPointing> h = bucketsZ.get(i);
			if(h != null){
				h.remove(object);
				if(h.size()<=0){
					bucketsZ.remove(i);
					assert(bucketsZ.get(i)==null);
					h=null;
				}
			} else {
				System.out.println("remove in Z on null bucket");
			}
		}
	}

	private int[] getObjectsX(int p, int size) {
		if(size!= 0)
			return getObjects(bucketsX,p, size);
		else 
			return new int[]{p/scale};
	}
	private int[] getObjectsZ(int p, int size) {
		if(size!= 0)
			return getObjects(bucketsZ,p, size);
		else 
			return new int[]{p/scale};
	}
	
	private int[] getObjects(ConcurrentHashMap<Integer, ConcurrentHashSet<ISpatialPointing>> bucketsZ2, int pos, int size) {
		int xs = (pos-size)/scale;
		int xe = (pos+size)/scale;

		if(xe != xs){
			if(xe != xs+1){
				int [] ret = null;
				ret = new int[xe-xs+1];
				for(int i=0;xs<=xe;xs++,i++)
					ret[i] = xs;
				return ret;
			} else
				return new int[]{xs,xe};
		}else
			return new int[]{xs};
	}

	public void update(ISpatialPointing object){
		remove(object);
		put(object);
		object.updateLast();
	}
	
	public ISpatialPointing[] getObjectsInRange(int posX, int posZ, int range){

		int[] x = getObjectsX(posX, range);
		int[] z = getObjectsZ(posZ, range);
		ConcurrentHashSet<ISpatialPointing> inter = new ConcurrentHashSet<ISpatialPointing>();
		ConcurrentHashSet<ISpatialPointing> ret = new ConcurrentHashSet<ISpatialPointing>();
		
		for(int i : x){
			ConcurrentHashSet<ISpatialPointing> h = bucketsX.get(i);
			if(h!= null){
				if(scale != 1){
					for(ISpatialPointing s : h)
						if(s.getX()>=posX-range&&s.getX()<=posX+range)
							inter.add(s);
				}
				else
					inter.addAll(h);
			}
		}
		
		for(int i : z){
			ConcurrentHashSet<ISpatialPointing> h = bucketsZ.get(i);
			if(h!= null)
				for(ISpatialPointing s : h)
					if(inter.contains(s)){
						if(scale != 1){
								if(s.getZ()>=posZ-range&&s.getZ()<=posZ+range)
									ret.add(s);
						}
						else
							ret.add(s);
					}
		}
		
		return ret.toArray(new ISpatialPointing[0]);
	}
}
