package com.l2client.util;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.WeakHashMap;
import java.util.logging.Logger;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.jme3.animation.AnimationProvider;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.l2client.app.Singleton;
import com.l2client.model.PartSet;


public final class AnimationManager {
	
	private HashMap<String, HashMap<String, PartSet>> animationParts = new HashMap<String, HashMap<String,PartSet>>();
	private static HashMap<String,PartSet> noAnims = new HashMap<String,PartSet>();

	class PartSetAnimationProvider extends AnimationProvider {
		HashMap<String,PartSet> anims = null;
		String defaultSet = null;
		
		PartSetAnimationProvider(){
		}
		/**
		 * Set a substitute anim set in case this one has only some anims
		 * @param set
		 */
		void setDefaultAnimSet(String set){
			this.defaultSet = set;
		}
		/**
		 * do nothing, animations are configured in the partset control
		 */
		@Override
		public void setAnimations(HashMap<String, Animation> animations) {
			//left empty by purpose
		}

		@Override
		public Animation getAnimation(String name, AnimControl control) {
			PartSet p = anims.get(name);
			Animation anim = null;
			if (p != null) {
				anim = Singleton.get().getAnimManager().getAnimation(
						p.getNext());
			}
			if(anim == null){
				//try default substitute
				if(defaultSet != null){
					AnimationProvider an = Singleton.get().getAnimManager().getAnimationProvider(defaultSet);
					anim = an.getAnimation(name, control);
				}
			}
			if (anim != null && control != null)
				anim.rewireBoneTracks(control.getSkeleton());
			return anim;
		}

		@Override
		public void removeAnimation(String name) {
			//do nothing
		}

		@Override
		public void addAnimation(Animation anim) {
			//do nothing
		}

		@Override
		public Collection<String> getAnimationNames() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void write(JmeExporter ex) throws IOException {
			//do nothing
		}

		@Override
		public void read(JmeImporter im) throws IOException {
			//do nothing
		}
		@Override
		public float getAnimationLength(String name) {
			// TODO Auto-generated method stub
			return 0;
		}

	}

	
	private static Logger logger = Logger.getLogger(AnimationManager.class.getName());
	
	public static String MANDATORY = "F";
	private static AnimationManager singleton = null;	
	private static WeakHashMap<String, PartSetAnimationProvider> loadedAnimations = new WeakHashMap<String, PartSetAnimationProvider>(); 
	private static PartSetAnimationProvider noAnimationsProvider = null;
	private static HashMap<String, String> defaultAnimations = new HashMap<String, String>();

	public final AnimationProvider getAnimationProvider(String animParts) {
		HashMap<String, PartSet> ret = animationParts.get(animParts);
		
		if(ret != null){
			PartSetAnimationProvider a = loadedAnimations.get(animParts);
			if(a != null)
				return a;
			else {
				a = new PartSetAnimationProvider();
				a.anims = ret;
				String def = defaultAnimations.get(animParts);
				if(def != null)
					a.defaultSet = def;
				loadedAnimations.put(animParts, a);
				return a;
			}
		}
		else {
			return noAnimationsProvider;
		}
	}

	/**
	 * singleton private constructor
	 */
	private AnimationManager() {
		singleton = this;
		noAnimationsProvider = new PartSetAnimationProvider();
		noAnimationsProvider.anims = (noAnims);
		logger.info("Singleton initialized");
	}

	/**
	 * Fetch the singleton instance (created in case not done so far)
	 * 
	 * @return The instance
	 */
	public static AnimationManager get() {
		if (singleton != null)
			return singleton;
		else {
			return new AnimationManager();
		}
	}
	
	/**
	 * Register a default substitute for an animation set.
	 * @param animset
	 * @param defaultset
	 */
	public void setDefault(String animset, String defaultset){
		defaultAnimations.put(animset, defaultset);
	}
	
	public Animation getAnimation(String path){
		Animation a ;
		//FIXME remove this !=!
		if(path.endsWith(".j3o"))
			a = (Animation) Singleton.get().getAssetManager().getJmeAssetMan().loadAsset(path);
		else
			a = (Animation) Singleton.get().getAssetManager().getJmeAssetMan().loadAsset(path+".j3o");	

		return (Animation)a;
	}

	public HashMap<String, PartSet> precacheAnimations(PartSet det) {
		HashMap<String, PartSet> set = animationParts.get(det.getName()) ;
		if(set != null) {
//			logger.severe("someone wants to recaches animparts for:"+det.getName());
			return set;
		}
			
		set = new HashMap<String, PartSet>();
		for(PartSet e : det.getParts()){
			set.put(e.getName(), e);
		}
		
		if(set.size() > 0){
			animationParts.put(det.getName(), set);
			return set;
		}
		else 
			return noAnims;
	}
}
