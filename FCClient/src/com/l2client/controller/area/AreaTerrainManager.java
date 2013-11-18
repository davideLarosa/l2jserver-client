package com.l2client.controller.area;

import java.util.concurrent.ConcurrentHashMap;

import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.util.SkyFactory;
import com.l2client.app.Singleton;
import com.l2client.controller.SceneManager.Action;

/**
 * A simple terrain manager, storing definitions of terrain tiles, loaded tiles, etc.
 * The simple terrain manager is used for testing only and does not use the @see AssetManager for loading.
 * The terrain model used is kept simple, just 128 sized plain quads are used to pose as terrain.
 * The swapping of terrain is already demonstrated, but in contrast to the real terrain manager the loading is handled synchronously.
 * As the character moves on new tiles are attached to the scene, as older are flushed from the cache.
 *
 * Used as a singleton by calling SimpleTerrainManager.get()
 */
public final class AreaTerrainManager implements ITileManager {

	/**
	 *  Loadqueue 1: the to be unloaded patches after a move
	 */
	ConcurrentHashMap<Integer, Area> unloadPatches = new ConcurrentHashMap<Integer, Area>();
	/**
	 * Loadqueue 2: the loaded patches
	 */
	ConcurrentHashMap<Integer, Area> loadedPatches = new ConcurrentHashMap<Integer, Area>();
	/**
	 * internal reference to the center tile
	 */
	private Area center = null;
	/**
	 * internal singleton reference
	 */
	private final static ITileManager singleton = new AreaTerrainManager();
	/**
	 * JME specific material to be used on the terrain tiles (only in simple) 
	 */
	private Material material = null;

	private Spatial sky;

	/**
	 * internal singleton constructor, initializes dummy textures for the simple demo
	 */
	private AreaTerrainManager() {
	}

	/**
	 * Fetch the singleton instance, creating one of it has not happened so far.
	 * @return
	 */
	public static ITileManager get() {
			return singleton;
	}
	
	/* (non-Javadoc)
	 * @see com.l2client.controller.area.ITileManager#initialize()
	 */
	@Override
	public void initialize(){
//		initDummyTexture();
//		addSkyDome();
	}
	
	/* (non-Javadoc)
	 * @see com.l2client.controller.area.ITileManager#update(com.jme3.math.Vector3f)
	 */
	@Override
	public void update(Vector3f worldPosition) {
		// int x = (int)worldPosition.x/IArea.TERRAIN_SIZE;
		// int z = (int)worldPosition.z/IArea.TERRAIN_SIZE;
		// setCenter(x, z);
		if (center != null) {
			//how far are we away from the center ?
			float dx = FastMath.abs(worldPosition.x-(center.x * IArea.TERRAIN_SIZE));
			float dz = FastMath.abs(worldPosition.z-(center.z * IArea.TERRAIN_SIZE));
			//if we are further away than 20% of a half tile (or 10% of a full) in x or z then initiate switch
			if (dz > 1.2f * IArea.TERRAIN_SIZE_HALF
					|| dx > 1.2f * IArea.TERRAIN_SIZE_HALF) {
				//add half the tilesize for correct tile computation
				setCenter(getTile(IArea.TERRAIN_SIZE_HALF+worldPosition.x), 
						getTile(IArea.TERRAIN_SIZE_HALF+ worldPosition.z));
				// System.out.println(xx+","+zz);
			}
		} else
			setCenter(getTile(worldPosition.x),
					getTile(worldPosition.z));
	}

	/**
	 * Sets the center coordinates. This will be used for calculation which tiles must be swapped. Early out if center has not changed.
	 * Expects the world coordinates/TERRAIN_SIZE as x and z
	 * 
	 * Should be called each render frame or at least once a second.
	 * 
	 * @param x	terrain coordinates in world coords/TERRAIN_SIZE
	 * @param z terrain coordinates in world coords/TERRAIN_SIZE
	 */
	private void setCenter(int x, int z) {

		if (center != null && center.x == x && center.z == z)
			return;

		unloadPatches.putAll(loadedPatches);

//		Singleton.get().getSceneManager().removeTerrains();

		loadedPatches.clear();

System.out.println("AreaTerrainManager setCenter:"+x+","+z);
		initCenter(x, z);
		initFirstRing(x, z);
		initSecondRing(x, z);
		// initThirdRing(x,z);
		
		for(Area a : unloadPatches.values()){
			a.unLoad();
		}

		unloadPatches.clear();
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
	private Area addLoadAll(Area p) {
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
	 * @return a @see Area for the tile to be loaded (or not loaded in case tile not present)
	 */
	private Area checkLoadPatch(int x, int z, boolean regNav) {
		Area ret = new Area(x, z, false);
		if (unloadPatches.contains(ret)){
System.out.println("AreaTerrainManager checkLoad found "+x+","+z);
			return unloadPatches.remove(ret.hashCode());
		}
		
		try {
			System.out.println("AreaTerrainManager checkLoad load "+x+","+z);
			ret.load();
		} catch (Exception e) {
			//TODO use logger & error handling on failed load of a tile (in which case is this ok?)
			e.printStackTrace();
		}

		return ret;
	}


	/* (non-Javadoc)
	 * @see com.l2client.controller.area.ITileManager#addSkyDome()
	 */
	@Override
	public void addSkyDome(){
		if(sky == null)
			sky = SkyFactory.createSky(Singleton.get().getAssetManager().getJmeAssetMan(),"models/textures/sky_povray1.jpg", true);
        Singleton.get().getSceneManager().changeCharNode(sky, Action.ADD);
	}
	
	/* (non-Javadoc)
	 * @see com.l2client.controller.area.ITileManager#removeSkyDome()
	 */
	@Override
	public void removeSkyDome(){
		if(sky != null)
			Singleton.get().getSceneManager().changeCharNode(sky, Action.REMOVE);
	}

	@Override
	public void prepareTeleport(Vector3f worldPos) {
		addLoadAll(checkLoadPatch(getTile(worldPos.x), getTile(worldPos.z), true));
	}

	public int getTile(float x) {
		return (int) x / IArea.TERRAIN_SIZE;
	}
	
}
