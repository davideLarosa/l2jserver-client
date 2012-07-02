package com.l2client.asset;

import java.io.IOException;
import java.util.concurrent.Future;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.scene.Node;

/**
 * Assets are nodes which define the location of the asset. The real asset 
 * (called baseasset) can be loaded threaded and will be set after load.
 * On attaching the scene node onAttach() can be called for scene 
 * attachment initialization (warning this could be called several times)
 * @author tmi
 *
 */
public class Asset extends Node{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String location = "";
	
	protected transient Object baseAsset = null;

	private transient Future<Object> loader;


	public Asset(){
		super();
	}
	
	public Asset(String location, String name){
		super(name);
		//FIXME why strip starting / who's not conform? from AssetFile inconsistecies?
		if(location.startsWith("/"))
			this.location = (String) location.subSequence(1, location.length());
		else
			this.location = location;
	}
	
	public String toString() {
		return "Asset location:"+location;
	}
	
	public Object getBaseAsset() {
		if(baseAsset != null)
			return baseAsset;
		else if(loader != null){
			try {
				baseAsset = loader.get();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			return baseAsset;
		} else
			return null;
	}
	
	public String getLocation() {
		return location;
	}
	
	public synchronized void setBaseAsset(Object n) {
		this.baseAsset = n;
//		if(n instanceof Node){
//
//                attachBaseasset();
//
//		}
	}
	
//	/**
//	 * only called for node types
//	 */
//	private void attachBaseasset() {
//		try{
//		if (baseAsset != null && !hasChild((Node)baseAsset)) {
//			//FIXME this will blow currently in JME3
//			attachChild((Node)baseAsset);
////			updateGeometricState();
//			
//		}
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//	}

	@Override
	public void read(JmeImporter im) throws IOException {
		super.read(im);
		InputCapsule capsule = im.getCapsule(this);
		location = capsule.readString("location", "");
	}
	@Override
	public void write(JmeExporter ex) throws IOException {
		super.write(ex);
		OutputCapsule capsule = ex.getCapsule(this);
		capsule.write(location, "location", "");
	}

	public void setFuture(Future<Object> ret) {
		this.loader = ret;
	}
	
	public void afterLoad(){}
	
	public void beforeUnload(){}

}
