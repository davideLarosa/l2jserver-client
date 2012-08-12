package com.l2client.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import com.jme3.animation.Skeleton;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.l2client.animsystem.jme.JMEAnimationController;
import com.l2client.asset.Asset;
import com.l2client.asset.AssetManager;
import com.l2client.model.PartSet;
import com.l2client.model.WeaponSet;
import com.l2client.util.AnimationManager;
import com.l2client.util.PartSetManager;
import com.l2client.util.SkeletonManger;


public class Assembler2 {
	
	private static Logger log = Logger.getLogger(Assembler2.class.getName());

	//give me the model defined by an array of partsets
	//skeletonmanager get an instance of an skeleton
	//partsetmanager get an instance of animpartset 
	public static Node getModel(String template){
		Node n = getModelInternal(JMEAnimationController.class, template);
		return n;
	}

	public static Node getModel3(String template){
		Node n = getModelInternal(JMEAnimationController.class, template);
		n.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
		n.updateGeometricState();
		
		return n;
	}
		
//	private static void addController(Node model, Mesh [] meshes,
//			Skeleton skeleton, String animSet) {
//			//TODO move this out into AnimationManger
//			AnimControl c = new AnimControl(skeleton);//skeleton, animParts, template
//			c.createChannel();
//			c.setAnimationProvider(AnimationManager.get().getAnimationProvider(animSet));
//
//            model.addControl(new JMEAnimationController(c, animSet));
//            SkeletonControl skeletonControl = new SkeletonControl(meshes, skeleton);
//            model.addControl(skeletonControl);
//	}
	
	public static PartSet getTopPart(String id){
		PartSet ret = PartSetManager.get().getPart("entity");
		if(ret != null)
			return ret.getPart(id);
		else return null;
	}
	
	/**
	 * Retrieves from an entity the next material instances
	 * @param top the top partset of this entity
	 * @return	Hashmap of material names from this entity and the asset of the mat instance
	 */
	public static HashMap<String, Asset> getMaterials(PartSet top){
		HashMap<String, Asset> materials = new HashMap<String, Asset>();
		PartSet det = top.getPart("mat");
		if(det != null){
		for( PartSet m : det.getParts()){
			String next = m.getNext();
			Asset a = new Asset(next,m.getName());
			AssetManager.getInstance().loadAsset(a, false);
			materials.put(m.getName(),a);
		}
		} //FIXME else log waring!!!
//		} else
			
		return materials;
	}
	
//	public static ArrayList<Asset> getMeshes(PartSet top){
//		//get meshes (w.o. weapon sets)
//		ArrayList<Asset> meshArray = new ArrayList<Asset>();
//		PartSet det = top.getPart("mesh");
//		for( PartSet oSet : det.getParts()){
//			String name = oSet.getName();
//			if(name.startsWith("weapon") || name.startsWith("shield"))//skip weapon+shield model
//				continue;
//			String next = oSet.getNext();
//			Asset a = new Asset(next,name);
//			AssetManager.getInstance().loadAsset(a, false);
//			meshArray.add(a);
//		}
//		
//		return meshArray;
//	}
//	
	
	/**
	 * Retrieves one next instances with material information 
	 * @param top
	 * @return
	 */
	public static HashMap<Asset,String> getMeshesWithMaterial(PartSet top){
		//get meshes (w.o. weapon sets)
		HashMap<Asset,String> meshArray = new HashMap<Asset,String>();
		//mesh
		PartSet det = top.getPart("mesh");
			for( PartSet oSet : det.getParts()){
		//mesh part
				String name = oSet.getName();
				if(name.startsWith("weapon") || name.startsWith("shield"))//skip weapon+shield model
					continue;
		//materials
				for(PartSet matSet :oSet.getParts()){ 
					String mat = matSet.getName();
		//next mesh 
					String next = matSet.getNext();
					Asset a = new Asset(next,name);
					AssetManager.getInstance().loadAsset(a, false);
					meshArray.put(a,mat);
			}
		}
		
		return meshArray;
	}
	
	public static Asset getHair(PartSet top, int i){
		Asset ret = null;
		PartSet det = top.getPart("mesh");
		if(det != null)
			det = det.getPart("hair");
		if(det != null){			
			String hair = null;
			if(i>=0){
				hair = det.get(i);
			}else{
				hair = det.getNext();
			}
			if(hair != null && !"".equals(hair)){
				ret = new Asset(hair,"hair");
				AssetManager.getInstance().loadAsset(ret, false);
			}
		}
		return ret;
			
	}
	
	/**
	 * Retrieves weapon configs for an entity
	 * @param top entity (e.g. DwarfWarrior etc.)
	 * @return the next weapon configuration
	 */
	public static WeaponSet getWeaponset(PartSet top){
		PartSet det = top.getPart("weapon");
		if(det != null){
		String weaponSets = det.getNext();
//		String weaponSet = det.getPart(weaponSets).getNext();
		Asset ws = new Asset(weaponSets,det.getName());
		AssetManager.getInstance().loadAsset(ws, true);
		//add weaponset meshes
		return (WeaponSet)ws.getBaseAsset();
		} else
			return null;
	}
	
	/**
	 * Retrieves the next weapon meshes for an entity
	 * @param top	the top entity (e.g. DwarfWarrior etc PartSet)
	 * @param weapons	the waeponset to use for loading
	 * @return
	 */
	private static Asset[] getWeapons(PartSet top, WeaponSet weapons){
		if(weapons != null){
		PartSet det = top.getPart("mesh");
		PartSet p = det.getPart(weapons.getPrimhand());
		if(p == null)
			return null;
		String s = p.getNext();
		Asset prim = new Asset(s,p.getName());
		AssetManager.getInstance().loadAsset(prim, false);
		p = det.getPart(weapons.getOffhand());
		//FIXME optionality not done here so far
		if(p != null){
			s = p.getNext();
			Asset secnd = new Asset(s,p.getName());
			AssetManager.getInstance().loadAsset(secnd, false);
			return new Asset[]{prim,secnd};
		} else 
			return new Asset []{prim};
		
		} else return null;
	}
	
	private static HashMap<Asset, String> getWeaponGeoms(PartSet top,
			WeaponSet weapons) {
		HashMap<Asset, String> ret = new HashMap<Asset, String>();

		if (weapons != null) {
			PartSet det = top.getPart("mesh");
			PartSet p = det.getPart(weapons.getPrimhand());
			if (p == null) {
				log.severe("Failed to load any weapons for "+top.getName()+" and weaponset "+weapons);
				return ret;
			}
			String mat = p.getNext();
			for(PartSet set : p.getParts()) {
			Asset prim = new Asset(set.getNext(), set.getName());
			AssetManager.getInstance().loadAsset(prim, false);
			ret.put(prim, mat);
			}
			p = det.getPart(weapons.getOffhand());
			// FIXME optionality not done here so far
			if (p != null) {
				mat = p.getNext();
				for(PartSet set : p.getParts()) {
				Asset prim = new Asset(set.getNext(), set.getName());
				AssetManager.getInstance().loadAsset(prim, false);
				ret.put(prim, mat);
				}
			}
		}
		if(ret.size() <=0)
			log.severe("Failed to load any weapons for "+top.getName()+" and weaponset "+weapons);
		return ret;
	}

	public static HashMap<String, PartSet> getAnimations(/*PartSet top, */String animSet, String defaultSet) {
		HashMap<String, PartSet> animParts = new HashMap<String, PartSet>();
		PartSet parts = PartSetManager.get().getPart("anim");
		if(parts == null)
			return animParts;
		PartSet det = parts.getPart(animSet);
		if( det != null )
//			for(PartSet e : det.getParts()){
				animParts = AnimationManager.get().precacheAnimations(det);
//			}
		if(defaultSet != null){
			det = parts.getPart(defaultSet);
			if(det != null){
				AnimationManager.get().setDefault(animSet, defaultSet);
				AnimationManager.get().precacheAnimations(det);
			}
		}
			
		return animParts;
	}

	public static Skeleton getSkeleton(PartSet top) {
		//get skeleton
		PartSet det = top.getPart("skel");
		//FIXME always the 1.0 or is this deprecated?
		return SkeletonManger.get().getSkeleton(det.getNext(), 1.0f);
	}

	private static Node getModelInternal(Class<?> controller, String template){

		PartSet top = getTopPart(template);
		//FIMXE error handling
		if(top == null) {
			new Exception("Failed to find temple <"+template+"> in megaset").printStackTrace();
			return new Node("NULL");
		}
		
		HashMap<String, Asset> materials = getMaterials(top);

		WeaponSet weapons = getWeaponset(top);

		HashMap<Asset, String> meshArray = getMeshesWithMaterial(top);
		
		addWeaponSetToMeshMap(top, weapons, meshArray);

		Skeleton skel = getSkeleton(top);

		getAnimations(/*top, */weapons.getAnimSet(), weapons.getDefaultSet());

		Node n = assembleModel(template,meshArray,materials,skel,weapons.getAnimSet());
				
		return n;
	}
	
	public static void addWeaponSetToMeshMap(PartSet top, WeaponSet weapons,
			HashMap<Asset, String> meshes) {
			HashMap<Asset, String> a = getWeaponGeoms(top, weapons);
			meshes.putAll(a);
	}



	private static Node assembleModel(String template,
			HashMap<Asset, String> meshArray, HashMap<String, Asset> materials,
			Skeleton skel, 
			String animSet) {
		Assembler as = new Assembler();
		as.setSkeleton(skel);
		for(Asset a : meshArray.keySet()){
			Object n = a.getBaseAsset();
			if(n != null){
				if(n instanceof Geometry){
					Asset mat = materials.get(meshArray.get(a));
					if(mat != null){
						Material m = (Material)mat.getBaseAsset();
						if(m != null){
							((Geometry)n).setMaterial(m);
						} else
							log.severe("Material "+mat.getLocation()+" not found is missing for "+a.getLocation());
					}else
						log.severe("Material is missing for "+a.getLocation());
					
					as.addMesh(a.getName(), (Geometry)n , false);
				}
				else 
					log.severe("Loaded Mesh asset which is not a Mesh but a:"+n.getClass().getName());
			}
			else
				log.severe("BaseAsset not found:"+a.getName()+", "+a.getLocation());
		}
			
		as.setAnimParts(animSet);
		Node n = as.getModel();
		n.setName(template);
		return n;
	}
}
