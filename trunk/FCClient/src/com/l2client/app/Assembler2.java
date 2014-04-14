package com.l2client.app;

import java.util.HashMap;
import java.util.logging.Logger;

import com.jme3.animation.Skeleton;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.l2client.animsystem.jme.JMEAnimationController;
import com.l2client.asset.Asset;
import com.l2client.model.PartSet;
import com.l2client.model.WeaponSet;


/**
 * A high level 3d model assembler, based on the @see PartSetManager model configuration.
 * Templates for 3d models are named uniquely then all parts are looked up, from which the
 * next possible permutation is chosen. The parts of this model are passed over to the
 * low level assembler @see Assembler to finally create a jme3 3d model. 
 * 
 * models are invoked by calling the different getModel() methods.
 */
public class Assembler2 {
	
	private static Logger log = Logger.getLogger(Assembler2.class.getName());

	/**
	 * returns a possible model identified by the template name. no batching and no 
	 * hw skinning is used. The model AnimationController is based on the l2client
	 * FCOgre Animation controller @see JMEAnimationController.
	 * 
	 * @param template	name of the model template from the partset configuration
	 * @return a @see Node below which the 3d model and the animation controller is present
	 */
	public static Node getModel(String template){
		Node n = getModelInternal(JMEAnimationController.class, template, false, false);
		return n;
	}

	/**
	 * returns a possible model identified by the template name. no batching and no 
	 * hw skinning is used. The model AnimationController is based on the l2client
	 * FCOgre Animation controller @see JMEAnimationController.
	 * The model is rotated by - PI/2 around the x-axis
	 * 
	 * @param template	name of the model template from the partset configuration
	 * @return a @see Node below which the 3d model and the animation controller is present
	 */
	public static Node getModel3(String template){
		Node n = getModelInternal(JMEAnimationController.class, template, false, false);
		n.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
		n.updateGeometricState();
		
		return n;
	}
	
	/**
	 * returns a possible model identified by the template name. no batching and no 
	 * hw skinning is used. The model AnimationController is based on the l2client
	 * FCOgre Animation controller @see JMEAnimationController.
	 * The model is rotated by - PI/2 around the x-axis
	 * 
	 * @param template	name of the model template from the partset configuration
	 * @param optimized combine all mesh parts into one mesh using batch factory (should use same material)
	 * @param hwSkinning hwSkinning desired or not
	 * @return a @see Node below which the 3d model and the animation controller is present
	 */
	public static Node getModel4(String template, boolean optimized, boolean hwSkinning){
		Node n = getModelInternal(JMEAnimationController.class, template, optimized, hwSkinning);
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
	
	/**
	 * Top of the template partset containing all sub parts
	 * @param id
	 * @return
	 */
	public static PartSet getTopPart(String id){
		PartSet ret = Singleton.get().getPartManager().getPart("entity");
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
			Singleton.get().getAssetManager().loadAsset(a, false);
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
//			Singleton.get().getAssetManager().loadAsset(a, false);
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
					Singleton.get().getAssetManager().loadAsset(a, false);
					meshArray.put(a,mat);
			}
		}
		
		return meshArray;
	}
	
	/**
	 * Special handling of hair for player for example.
	 * @param top the top partset 
	 * @param i	  the nth hair style to chose
	 * @return	the asset representing the desired hairstyle
	 */
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
				Singleton.get().getAssetManager().loadAsset(ret, false);
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
		Singleton.get().getAssetManager().loadAsset(ws, true);
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
		Singleton.get().getAssetManager().loadAsset(prim, false);
		p = det.getPart(weapons.getOffhand());
		//FIXME optionality not done here so far
		if(p != null){
			s = p.getNext();
			Asset secnd = new Asset(s,p.getName());
			Singleton.get().getAssetManager().loadAsset(secnd, false);
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
			Asset prim = new Asset(set.getNext(), p.getName());
			Singleton.get().getAssetManager().loadAsset(prim, false);
			ret.put(prim, mat);
			}
			p = det.getPart(weapons.getOffhand());
			// FIXME optionality not done here so far
			if (p != null) {
				mat = p.getNext();
				for(PartSet set : p.getParts()) {
				Asset prim = new Asset(set.getNext(), p.getName());
				Singleton.get().getAssetManager().loadAsset(prim, false);
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
		PartSet parts = Singleton.get().getPartManager().getPart("anim");
		if(parts == null)
			return animParts;
		PartSet det = parts.getPart(animSet);
		if( det != null )
//			for(PartSet e : det.getParts()){
				animParts = Singleton.get().getAnimManager().precacheAnimations(det);
//			}
		if(defaultSet != null){
			det = parts.getPart(defaultSet);
			if(det != null){
				Singleton.get().getAnimManager().setDefault(animSet, defaultSet);
				Singleton.get().getAnimManager().precacheAnimations(det);
			}
		}
			
		return animParts;
	}

	public static Skeleton getSkeleton(PartSet top) {
		//get skeleton
		PartSet det = top.getPart("skel");
		//FIXME always the 1.0 or is this deprecated?
		return Singleton.get().getSkeletonManager().getSkeleton(det.getNext(), 1.0f);
	}

	/**
	 * internal orchestration of model parts lookup and assembly
	 * 
	 * @param controller
	 * @param template
	 * @param optimized
	 * @param hwSkinning
	 * @return
	 */
	private static Node getModelInternal(Class<?> controller, String template, boolean optimized, boolean hwSkinning){

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

		Node n = assembleModel(template,meshArray,materials,skel,weapons.getAnimSet(), optimized, hwSkinning);
				
		return n;
	}
	
	public static void addWeaponSetToMeshMap(PartSet top, WeaponSet weapons,
			HashMap<Asset, String> meshes) {
			HashMap<Asset, String> a = getWeaponGeoms(top, weapons);
			meshes.putAll(a);
	}



	/** pass it all over to the low level assebler
	 * 
	 * @param template
	 * @param meshArray
	 * @param materials
	 * @param skel
	 * @param animSet
	 * @param optimized
	 * @param hwSkinnig
	 * @return
	 */
	private static Node assembleModel(String template,
			HashMap<Asset, String> meshArray, HashMap<String, Asset> materials,
			Skeleton skel, String animSet, boolean optimized, boolean hwSkinnig) {
		Assembler as = new Assembler();
		as.setUseOptimization(optimized);
		as.setUseHWSkinning(hwSkinnig);
		as.setSkeleton(skel);
		unpackMeshesIntoAssembler(meshArray, materials, as);
			
		as.setAnimParts(animSet);
		Node n = as.getModel();
		n.setName(template);
		return n;
	}

	public static void unpackMeshesIntoAssembler(
			HashMap<Asset, String> meshArray, HashMap<String, Asset> materials,
			Assembler assembler) {
		for(Asset a : meshArray.keySet()){
			Object n = a.getBaseAsset();
			if(n != null){
				if(n instanceof Geometry){
					Asset mat = materials.get(meshArray.get(a));
					if(mat != null){
						Material m = (Material)mat.getBaseAsset();
						if(m != null){
							//prepare rim light shader for selection highlighting
							m.setColor("RimLighting", ColorRGBA.BlackNoAlpha);//new ColorRGBA(1f, 0f, 0f, 12f));//this was for a test had to set the value to 12 for a really good noticable look
							//this must be a clone for hardware skinning
							((Geometry)n).setMaterial(m.clone());
						} else
							log.severe("Material "+mat.getLocation()+" not found is missing for "+a.getLocation());
					}else
						log.severe("Material is missing for "+a.getLocation());
					
					assembler.addMesh(a.getName(), (Geometry)n , false);
				}
				else 
					log.severe("Loaded Mesh asset which is not a Mesh but a:"+n.getClass().getName());
			}
			else
				log.severe("BaseAsset not found:"+a.getName()+", "+a.getLocation());
		}
	}
}
