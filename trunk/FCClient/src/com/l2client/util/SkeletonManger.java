package com.l2client.util;

import java.util.concurrent.ConcurrentHashMap;

import com.jme3.animation.Skeleton;
import com.l2client.asset.Asset;
import com.l2client.asset.AssetManager;


public final class SkeletonManger {
	private static SkeletonManger singleton = null;
	
	private ConcurrentHashMap<String, Skeleton> skeletons = new ConcurrentHashMap<String, Skeleton>();
	
	/**
	 * singleton private constructor
	 */
	private SkeletonManger() {
		singleton = this;
	}

	/**
	 * Fetch the singleton instance (created in case not done so far)
	 * 
	 * @return The instance
	 */
	public static SkeletonManger get() {
		if (singleton != null)
			return singleton;
		else {
			return new SkeletonManger();
		}
	}
	
	/**
	 * Returns an instance of the specified skeleton
	 * @param name	Name of the file to be loaded
	 * @param scale Scale of the skeleton
	 * @return
	 */
	public Skeleton getSkeleton(String name , float scale){
		
		String scala = String.valueOf(scale);
		//create name (name+scale)
		String cName = name + scala;
		//lookup in cache or load it
		Skeleton s = skeletons.get(cName);
		if(s == null){
			Asset a = new Asset(name,cName);
			AssetManager.getInstance().loadAsset(a, true);
			s = (Skeleton)a.getBaseAsset();
		}
		if(s!= null){
			return s;
		}
		else {
			skeletons.put(cName, s);
			return null;
		}
	}
}
