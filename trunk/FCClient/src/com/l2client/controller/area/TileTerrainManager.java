package com.l2client.controller.area;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.jme3.asset.AssetManager;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.util.SkyFactory;
import com.l2client.app.Singleton;
import com.l2client.controller.SceneManager.Action;

/**
 * 
 */
public final class TileTerrainManager implements ITileManager {
	
	private static Logger log = Logger.getLogger(TileTerrainManager.class.getName());

	private static final int CROSSING_DISTANCE = (int)(IArea.TERRAIN_SIZE_HALF*1.1f);
	
//	private float lastupdate = 0f;

	/**
	 *  Loadqueue 1: the to be unloaded patches after a move
	 */
	ConcurrentHashMap<Integer, Tile> unloadPatches = new ConcurrentHashMap<Integer, Tile>();
	/**
	 * Loadqueue 2: the loaded patches
	 */
	ConcurrentHashMap<Integer, Tile> loadedPatches = new ConcurrentHashMap<Integer, Tile>();
	/**
	 * internal reference to the center tile
	 */
	private Tile center = null;
	/**
	 * internal singleton reference
	 */
	private final static TileTerrainManager singleton = new TileTerrainManager();
	/**
	 * JME specific material to be used on the terrain tiles (only in simple) 
	 */
	private Material material = null;

	private Spatial sky;
	
	private DirectionalLight skyLight;

	/**
	 * internal singleton constructor, initializes dummy textures for the simple demo
	 */
	private TileTerrainManager() {
	}

	/**
	 * Fetch the singleton instance, creating one of it has not happened so far.
	 * @return
	 */
	public static TileTerrainManager get() {
			return singleton;
	}
	
	/**
	 * creates dummy textures for quad based terrain standins
	 * creates a skydome
	 */
	public void initialize(){
//		initDummyTexture();
//		addSkyDome();
	}

	/**
	 * Set center tile of interest
	 * @param x	tile in x direction (east west)
	 * @param z tile in z direction (north south)
	 */
	private void setCenter(int x, int z) {

		if (center != null && center.x == x && center.z == z)
			return;
		
		unloadPatches.putAll(loadedPatches);

//		Singleton.get().getSceneManager().removeTerrains();

		loadedPatches.clear();

		log.fine("TileTerrainManager setCenter:"+x+","+z);
		initCenter(x, z);
		initFirstRing(x, z);
		initSecondRing(x, z);
		initThirdRing(x,z);
		
		for(Tile a : unloadPatches.values()){
			a.unLoad();
		}

		unloadPatches.clear();
		log.finer("TileTerrainManager loadedPatches entries:"+loadedPatches.size());
	}
	
	private void initThirdRing(int x, int y) {
		addLoadAll(checkLoadPatch(x-3,y-3, false));
		addLoadAll(checkLoadPatch(x-2,y-3, false));
		addLoadAll(checkLoadPatch(x-1,y-3, false));
		addLoadAll(checkLoadPatch(x,y-3, false));
		addLoadAll(checkLoadPatch(x+1,y-3, false));
		addLoadAll(checkLoadPatch(x+2,y-3, false));
		addLoadAll(checkLoadPatch(x+3,y-3, false));
		addLoadAll(checkLoadPatch(x+3,y-2, false));
		addLoadAll(checkLoadPatch(x+3,y-1, false));
		addLoadAll(checkLoadPatch(x+3,y, false));
		addLoadAll(checkLoadPatch(x+3,y+1, false));
		addLoadAll(checkLoadPatch(x+3,y+2, false));
		addLoadAll(checkLoadPatch(x+3,y+3, false));
		addLoadAll(checkLoadPatch(x+2,y+3, false));	
		addLoadAll(checkLoadPatch(x+1,y+3, false));
		addLoadAll(checkLoadPatch(x,y+3, false));
		addLoadAll(checkLoadPatch(x-1,y+3, false));
		addLoadAll(checkLoadPatch(x-2,y+3, false));		
		addLoadAll(checkLoadPatch(x-3,y+3, false));
		addLoadAll(checkLoadPatch(x-3,y+2, false));
		addLoadAll(checkLoadPatch(x-3,y+1, false));
		addLoadAll(checkLoadPatch(x-3,y, false));
		addLoadAll(checkLoadPatch(x-3,y-1, false));
		addLoadAll(checkLoadPatch(x-3,y-2, false));		
	}

	/**
	 * initializes the second ring around the center tile
	 * @param x tile # (coords/tile_size) in x 
	 * @param z tile # (coords/tile_size) in y
	 */
	private void initSecondRing(int x, int z) {
		addLoadAll(checkLoadPatch(x - 2, z - 2,false));
		addLoadAll(checkLoadPatch(x - 1, z - 2,false));
		addLoadAll(checkLoadPatch(x, z - 2,false));
		addLoadAll(checkLoadPatch(x + 1, z - 2,false));
		addLoadAll(checkLoadPatch(x + 2, z - 2,false));
		addLoadAll(checkLoadPatch(x + 2, z - 1,false));
		addLoadAll(checkLoadPatch(x + 2, z,false));
		addLoadAll(checkLoadPatch(x + 2, z + 1,false));
		addLoadAll(checkLoadPatch(x + 2, z + 2,false));
		addLoadAll(checkLoadPatch(x + 1, z + 2,false));
		addLoadAll(checkLoadPatch(x, z + 2,false));
		addLoadAll(checkLoadPatch(x - 1, z + 2,false));
		addLoadAll(checkLoadPatch(x - 2, z + 2,false));
		addLoadAll(checkLoadPatch(x - 2, z + 1,false));
		addLoadAll(checkLoadPatch(x - 2, z,false));
		addLoadAll(checkLoadPatch(x - 2, z - 1,false));
	}
	
	/**
	 * initializes the first ring around the center tile
	 * @param x tile # (coords/tile_size) in x 
	 * @param z tile # (coords/tile_size) in y
	 */
	private void initFirstRing(int x, int z) {
		addLoadAll(checkLoadPatch(x - 1, z - 1,true));
		addLoadAll(checkLoadPatch(x, z - 1,true));
		addLoadAll(checkLoadPatch(x + 1, z - 1,true));
		addLoadAll(checkLoadPatch(x + 1, z,true));
		addLoadAll(checkLoadPatch(x + 1, z + 1,true));
		addLoadAll(checkLoadPatch(x, z + 1,true));
		addLoadAll(checkLoadPatch(x - 1, z + 1,true));
		addLoadAll(checkLoadPatch(x - 1, z,true));
	}

	/**
	 * start loading all and attach base + detail
	 * 
	 * @param p
	 * @return
	 */
	private Tile addLoadAll(Tile p) {
		loadedPatches.put(p.hashCode(), p);
		return p;
	}

	/**
	 * initializes the center tile
	 * @param x tile # (coords/tile_size) in x 
	 * @param z tile # (coords/tile_size) in y
	 */
	private void initCenter(int x, int y) {
		center = addLoadAll(checkLoadPatch(x, y,true));
	}

	/**
	 * Checks if the to be loaded tile is present in the unloaded cache and eventually revives it,
	 * otherwise initializes loading of the tile in asynchronous mode via the @see AssetManager
	 *
	 * the simple version just creates quads synchronously
	 * 
	 * @param x tile # (coords/tile_size) in x 
	 * @param z tile # (coords/tile_size) in z
	 * @return a @see Tile for the tile to be loaded (or not loaded in case tile not present)
	 */
	private Tile checkLoadPatch(int x, int z, boolean prioLoadNav) {
		Tile ret = new Tile(x, z);
		if (unloadPatches.contains(ret)){
			log.finest("TileTerrainManager unload queue found "+x+","+z);
			//setCenter case put all in unloaded then revive all from unload queue rest gets emptied, so here we remove
			return unloadPatches.remove(ret.hashCode());
		}
		if (loadedPatches.contains(ret)){
			log.finest("TileTerrainManager unload queue found "+x+","+z);
			//do not remove it from the already loaded (case of teleport back and fro for example)
			return loadedPatches.get(ret.hashCode());
		}
		
		try {
			log.finer("TileTerrainManager not found so load requested of "+x+","+z);
			ret.load(prioLoadNav);
		} catch (Exception e) {
			//TODO use logger & error handling on failed load of a tile (in which case is this ok?)
			e.printStackTrace();
		}

		return ret;
	}


	public void addSkyDome(){
		AssetManager assetManager = Singleton.get().getAssetManager().getJmeAssetMan();
		
		if(sky == null)
			sky = SkyFactory.createSky(Singleton.get().getAssetManager().getJmeAssetMan(),"models/textures/sky_povray1.jpg", true);
        Singleton.get().getSceneManager().changeCharNode(sky,Action.ADD);
        
        skyLight = new DirectionalLight();
        skyLight.setColor(new ColorRGBA(0.8f, 0.75f, 0.8f, 1f));
		Singleton.get().getSceneManager().changeRootLight(skyLight, Action.ADD);
		
//		//TODO should be not here
//        DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, 2048, 3);
//        dlsf.setLight(dl);
//        dlsf.setLambda(0.65f);
//        dlsf.setShadowIntensity(0.8f);
//        dlsf.setEdgeFilteringMode(EdgeFilteringMode.Dither);
//
//        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
//        fpp.addFilter(dlsf);
//		Singleton.get().getSceneManager().changePostProcessor(fpp, Action.ADD);
        DirectionalLightShadowRenderer dlsf = new DirectionalLightShadowRenderer(assetManager, 2048, 3);
        dlsf.setLight(skyLight);
        dlsf.setLambda(0.65f);
        dlsf.setShadowIntensity(0.8f);
        dlsf.setEdgeFilteringMode(EdgeFilteringMode.Dither);

		Singleton.get().getSceneManager().changePostProcessor(dlsf, Action.ADD);	
		
	}
	
	public void addSkyDome(Camera cam, int timeOffset){
		if(sky == null)
			sky = new Node("Sky");
		
		skyLight = new DirectionalLight();
		skyLight.setColor(new ColorRGBA(1f, 1f, 1f, 1f));
		Singleton.get().getSceneManager().changeRootLight(skyLight, Action.ADD);
		
		AssetManager assetManager = Singleton.get().getAssetManager().getJmeAssetMan();
		com.l2client.util.SkyDome skyControl = new com.l2client.util.SkyDome(assetManager, cam);
		skyControl.setUseCalendar(false);
		skyControl.setSun(skyLight);
		skyControl.setControlSun(true);
		skyControl.setTimeOffset(timeOffset);
		skyControl.setDaySkyColor(new ColorRGBA(0.75f, 0.75f, 1f, 1f));
		skyControl.setSkyNightColor(new ColorRGBA(0.2f, 0.15f, 0.25f, 1f));
		skyControl.setSunDayLight(new ColorRGBA(0.7f, 0.7f, 0.7f, 1f));
		skyControl.setSunNightLight(new ColorRGBA(0.05f, 0.05f, 0.1f, 1f));
		skyControl.initializeCalendar(1, 1, 24, 7, 4, 24);
		sky.addControl(skyControl);
		sky.setQueueBucket(Bucket.Sky);
		Singleton.get().getSceneManager().changeCharNode(sky,Action.ADD);
		//TODO should be not here
        DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, 2048, 3);
        dlsf.setLight(skyLight);
        dlsf.setLambda(0.65f);
        dlsf.setShadowIntensity(0.8f);
        dlsf.setEdgeFilteringMode(EdgeFilteringMode.Dither);
        dlsf.setEnabled(true);
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        fpp.addFilter(dlsf);
		Singleton.get().getSceneManager().changePostProcessor(fpp, Action.ADD);
		
//        DirectionalLightShadowRenderer dlsf = new DirectionalLightShadowRenderer(assetManager, 2048, 3);
//        dlsf.setLight(dl);
//        dlsf.setLambda(0.65f);
//        dlsf.setShadowIntensity(0.8f);
//        dlsf.setEdgeFilteringMode(EdgeFilteringMode.Dither);
//
//		Singleton.get().getSceneManager().changePostProcessor(dlsf, Action.ADD);		
	}
	
	public void removeSkyDome(){
		if(sky != null)
			Singleton.get().getSceneManager().changeCharNode(sky,Action.REMOVE);
		if(skyLight != null){
			Singleton.get().getSceneManager().changeRootLight(skyLight, Action.REMOVE);
		}
	}
	
	/**
	 * @return the loadedAtOrigin
	 */
	public boolean isLoadedAtOrigin() {
		return !Tile.moveNonNavToOrigin;
	}

	/**
	 * @param loadedAtOrigin true if tiles in local coordinates (all at origin), false if already at world position
	 */
	public void setLoadedAtOrigin(boolean loadedAtOrigin) {
		Tile.moveNonNavToOrigin = loadedAtOrigin;
	}

	@Override
	public void update(Vector3f worldPosition) {
		int x = (int)worldPosition.x;
		int z = (int)worldPosition.z;
		
		setCenter(Tile.getTileFromWorldXPosition(x), Tile.getTileFromWorldZPosition(z));
	}

	@Override
	public void prepareTeleport(Vector3f tPos) {
		addLoadAll(checkLoadPatch(Tile.getTileFromWorldXPosition((int) tPos.x), Tile.getTileFromWorldZPosition((int) tPos.z),true));
	}
	
}
