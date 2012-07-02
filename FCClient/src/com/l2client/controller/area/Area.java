package com.l2client.controller.area;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;
import com.l2client.asset.Asset;
import com.l2client.asset.AssetManager;
import com.l2client.controller.SceneManager;
import com.l2client.navigation.EntityNavigationManager;
import com.l2client.navigation.NavigationMesh;

public class Area {
	static final String loadPath = "/area/";
	String areaPath = "";
	int x,z;
	Asset base = null;
	Asset detail1 = null;
	Asset nav = null;
	
	public Area(int _x, int _z, boolean b) {
		x = _x;
		z = _z;		
		areaPath = loadPath+x+"_"+z+"/";
	}
	
    public boolean equals(Object obj) {
		if (obj instanceof Area) {
			Area pt = (Area) obj;
			return (x == pt.x) && (z == pt.z);
		}
		return super.equals(obj);
	}

	public int hashCode() {
		return (x+","+z).hashCode();
		//this is bad, -1 -1 has the same as 1,1 -1,1 has the same as 1,-1
//		long bits = java.lang.Double.doubleToLongBits(x);
//		bits ^= java.lang.Double.doubleToLongBits(z) * 31;
//		return (((int) bits) ^ ((int) (bits >> 32)));
	}
	
	class DeferredTerrainAsset extends Asset{
		public DeferredTerrainAsset(String loc){
			super(loc, loc);
		}
		@Override
		public void afterLoad(){
			if(baseAsset != null && baseAsset instanceof Spatial){
				//FIXME just for dummy tests
				Spatial n = (Spatial)baseAsset;
				Material mat = new Material(AssetManager.getInstance().getJmeAssetMan(), "Common/MatDefs/Misc/Unshaded.j3md");
		        mat.setColor("Color", ColorRGBA.randomColor());
		        n.setMaterial(mat);
		        n.updateModelBound();
				SceneManager.get().changeTerrainNode(n,0);
System.out.println("DeferredTerrainAsset afterLoad of:"+this.name);
			}
		}
		
		@Override
		public void beforeUnload(){
			if(baseAsset != null && baseAsset instanceof Spatial){
System.out.println("DeferredTerrainAsset beforeUnload of:"+this.name);
				SceneManager.get().changeTerrainNode((Spatial)baseAsset,1);
				baseAsset = null;
			}
		}
	}
	
	class NavAsset extends Asset{
		public NavAsset(String loc){
			super(loc, loc);
		}
		@Override
		public void afterLoad(){
			if(baseAsset != null && baseAsset instanceof NavigationMesh )
				EntityNavigationManager.get().attachMesh((NavigationMesh) baseAsset);
//			System.out.println("NavAsset afterLoad of:"+this.name);
		}
		
		@Override
		public void beforeUnload(){
			if(baseAsset != null && baseAsset instanceof NavigationMesh ){
				EntityNavigationManager.get().detachMesh((NavigationMesh) baseAsset);
				baseAsset = null;
//				System.out.println("NavAsset beforeUnload of:"+this.name);
			}
		}
	}
	
	void load(){
		nav = new NavAsset(areaPath+"nav.jnv");
		AssetManager.getInstance().loadAsset(nav, true);
		base = new DeferredTerrainAsset(areaPath+"base.j3o");
		AssetManager.getInstance().loadAsset(base, false);
		detail1 = new DeferredTerrainAsset(areaPath+"detail1.j3o");
		AssetManager.getInstance().loadAsset(detail1, false);
		
		/*
			Quad q = new Quad(1f * TERRAIN_SIZE, 1f * TERRAIN_SIZE);
			Geometry n = new Geometry(x + " " + y,q);
			n.setMaterial(material);
			n.setLocalTranslation(x * TERRAIN_SIZE, 0f,y * TERRAIN_SIZE);
			ret.patch = n;
			n.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
			SceneManager.get().changeTerrainNode(n,0);
		 * */
	}

	void unLoad(){
		if(nav != null){
			nav.beforeUnload();
			nav = null;
		}
		if(base != null){
			base.beforeUnload();
			base = null;
		}
		if(detail1 != null){
			detail1.beforeUnload();
			detail1 = null;
		}
	}
}
