package com.l2client.controller.area;

import java.util.concurrent.ConcurrentHashMap;

import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.SkyFactory;
import com.l2client.app.Singleton;

/**
 * A simple terrain manager, storing definitions of terrain tiles, loaded tiles, etc.
 * The simple terrain manager is used for testing only and does not use the @see AssetManager for loading.
 * The terrain model used is kept simple, just 128 sized plain quads are used to pose as terrain.
 * The swapping of terrain is already demonstrated, but in contrast to the real terrain manager the loading is handled synchronously.
 * As the character moves on new tiles are attached to the scene, as older are flushed from the cache.
 *
 * Used as a singleton by calling SimpleTerrainManager.get()
 */
public final class SimpleTerrainManager {
	
	/**
	 * "tile_" this is used to find nav/ground tiles in the simple example
	 * @see GotoClickedInputAction.onAnalog()
	 */
	public static String TILE_PREFIX = "tile_";

	// private static int count = 99;
	// TerrainPatch load[] = new TerrainPatch[24];//7x7 -5x5 third ring low
	// detail in loading
	// TerrainPatch far[] = new TerrainPatch[16];//5x5 -3x3 second ring low
	// detail
	// TerrainPatch near[] = new TerrainPatch[9];//3x3 high detail
	/**
	 * Internal wrapper for a terrain patch, stores the coordinates of the
	 * patch, load information, detail information and after loading the finally
	 * loaded patch from the @see AssetManager
	 */
	//TODO getter/setter
	private class PatchInfo {
		public PatchInfo(int _x, int _y, boolean b) {
			x = _x;
			y = _y;
//			detail = b;
		}

		public int x;
		public int y;
//		public boolean detail;
		public Spatial patch =null;
	    public boolean equals(Object obj) {
			if (obj instanceof PatchInfo) {
				PatchInfo pt = (PatchInfo) obj;
				return (x == pt.y) && (y == pt.y);
			}
			return super.equals(obj);
		}

		public int hashCode() {
			return (x+","+y).hashCode();
			//this is bad, -1 -1 has the same as 1,1 -1,1 has the same as 1,-1
//			long bits = java.lang.Double.doubleToLongBits(x);
//			bits ^= java.lang.Double.doubleToLongBits(z) * 31;
//			return (((int) bits) ^ ((int) (bits >> 32)));
		}
	}

	/**
	 *  Loadqueue 1: the to be unloaded patches after a move
	 */
	ConcurrentHashMap<Integer, PatchInfo> unloadPatches = new ConcurrentHashMap<Integer, PatchInfo>();
	/**
	 * Loadqueue 2: the loaded patches
	 */
	ConcurrentHashMap<Integer, PatchInfo> loadedPatches = new ConcurrentHashMap<Integer, PatchInfo>();
	/**
	 * internal reference to the center tile
	 */
	private PatchInfo center = null;
	/**
	 * internal singleton reference
	 */
	private final static SimpleTerrainManager singleton = new SimpleTerrainManager();
	/**
	 * JME specific material to be used on the terrain tiles (only in simple) 
	 */
	private Material material = null;

	private Spatial sky;

	/**
	 * internal singleton constructor, initializes dummy textures for the simple demo
	 */
	private SimpleTerrainManager() {
	}

	/**
	 * Fetch the singleton instance, creating one of it has not happened so far.
	 * @return
	 */
	public static SimpleTerrainManager get() {
			return singleton;
	}
	
	/**
	 * creates dummy textures for quad based terrain standins
	 * creates a skydome
	 */
	public void initialize(){
		initDummyTexture();
	}
	
	public void update(Vector3f worldPosition) {
			setCenter((int) worldPosition.x / IArea.TERRAIN_SIZE,
					(int) worldPosition.z / IArea.TERRAIN_SIZE);
	}

	/**
	 * Sets the center coordinates. This will be used for calculation which tiles must be swapped. Early out if center has not changed.
	 * Expects the world coordinates/TERRAIN_SIZE as x and z
	 * 
	 * Should be called each render frame or at least once a second.
	 * 
	 * @param x	terrain coordinates in world coords/TERRAIN_SIZE
	 * @param y terrain coordinates in world coords/TERRAIN_SIZE
	 */
	public void setCenter(int x, int y) {

		if (center != null && center.x == x && center.y == y)
			return;
		
		System.out.println("Setting player center to new tile region x="+x+" y="+y);

		unloadPatches.putAll(loadedPatches);

		Singleton.get().getSceneManager().removeTerrains();

		loadedPatches.clear();

		initCenter(x, y);
		initFirstRing(x, y);
		initSecondRing(x, y);
		// initThirdRing(x,z);

		unloadPatches.clear();

	}

	/**
	 * initializes the second ring around the center tile
	 * @param x tile # (coords/tile_size) in x 
	 * @param z tile # (coords/tile_size) in y
	 */
	private void initSecondRing(int x, int z) {
		addLoadAll(checkLoadPatch(x - 2, z - 2));
		addLoadAll(checkLoadPatch(x - 1, z - 2));
		addLoadAll(checkLoadPatch(x, z - 2));
		addLoadAll(checkLoadPatch(x + 1, z - 2));
		addLoadAll(checkLoadPatch(x + 2, z - 2));
		addLoadAll(checkLoadPatch(x + 2, z - 1));
		addLoadAll(checkLoadPatch(x + 2, z));
		addLoadAll(checkLoadPatch(x + 2, z + 1));
		addLoadAll(checkLoadPatch(x + 2, z + 2));
		addLoadAll(checkLoadPatch(x + 1, z + 2));
		addLoadAll(checkLoadPatch(x, z + 2));
		addLoadAll(checkLoadPatch(x - 1, z + 2));
		addLoadAll(checkLoadPatch(x - 2, z + 2));
		addLoadAll(checkLoadPatch(x - 2, z + 1));
		addLoadAll(checkLoadPatch(x - 2, z));
		addLoadAll(checkLoadPatch(x - 2, z - 1));
	}
	
	/**
	 * initializes the first ring around the center tile
	 * @param x tile # (coords/tile_size) in x 
	 * @param z tile # (coords/tile_size) in y
	 */
	private void initFirstRing(int x, int z) {
		addLoadAll(checkLoadPatch(x - 1, z - 1));
		addLoadAll(checkLoadPatch(x, z - 1));
		addLoadAll(checkLoadPatch(x + 1, z - 1));
		addLoadAll(checkLoadPatch(x + 1, z));
		addLoadAll(checkLoadPatch(x + 1, z + 1));
		addLoadAll(checkLoadPatch(x, z + 1));
		addLoadAll(checkLoadPatch(x - 1, z + 1));
		addLoadAll(checkLoadPatch(x - 1, z));
	}

	/**
	 * start loading all and attach base + detail
	 * 
	 * @param p
	 * @return
	 */
	private PatchInfo addLoadAll(PatchInfo p) {
		loadedPatches.put(p.hashCode(), p);
		Singleton.get().getSceneManager().changeTerrainNode(p.patch,0);
		return p;
	}

	/**
	 * initializes the center tile
	 * @param x tile # (coords/tile_size) in x 
	 * @param z tile # (coords/tile_size) in y
	 */
	private void initCenter(int x, int y) {
		center = addLoadAll(checkLoadPatch(x, y));
	}

	/**
	 * Checks if the to be loaded tile is present in the unloaded cache and eventually revives it,
	 * otherwise initializes loading of the tile in asynchronous mode via the @see AssetManager
	 *
	 * the simple version just creates quads synchronously
	 * 
	 * @param x tile # (coords/tile_size) in x 
	 * @param z tile # (coords/tile_size) in y
	 * @return a @see PatchInfo for the tile to be loaded (or not loaded in case tile not present)
	 */
	private PatchInfo checkLoadPatch(int x, int y) {
		PatchInfo ret = new PatchInfo(x, y, false);
		if (unloadPatches.contains(ret)){
			ret = unloadPatches.remove(ret.hashCode());
			if(ret != null)
				return ret;
			else
				ret = new PatchInfo(x, y, false);//FIXME how can this happen?
		}
		try {
			Quad q = new Quad(1f * IArea.TERRAIN_SIZE, 1f * IArea.TERRAIN_SIZE);
			//this is the same as in GotoClickedInputAction
			Geometry n = new Geometry(TILE_PREFIX + x + " " + y,q);
			n.setMaterial(material);
			n.setLocalTranslation(x * IArea.TERRAIN_SIZE, 0f,y * IArea.TERRAIN_SIZE);
			ret.patch = n;
			n.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
		} catch (Exception e) {
			//TODO use logger & error handling on failed load of a tile (in which case is this ok?)
			e.printStackTrace();
		}

		return ret;
	}

	/**
	 * Initializes a dummy texture to be used by ALL tiles, this is just for the demo.
	 * Normally a tile already contains complete culling and rendering information, so this is not needed at all.
	 * JME specific
	 */
	private void initDummyTexture() {
		com.jme3.asset.AssetManager assetManager = Singleton.get().getAssetManager().getJmeAssetMan();
		// TERRAIN TEXTURE material
		material = new Material(assetManager, "Common/MatDefs/Terrain/Terrain.j3md");
        material.setBoolean("useTriPlanarMapping", false);

		// ALPHA map (for splat textures)
		material.setTexture("Alpha", assetManager.loadTexture("Textures/Terrain/splat/alphamap.png"));

		// HEIGHTMAP image (for the terrain heightmap)
		Texture heightMapImage = assetManager.loadTexture("Textures/Terrain/splat/mountains512.png");
        
		// GRASS texture
		Texture grass = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
		grass.setWrap(WrapMode.Repeat);
		material.setTexture("Tex1", grass);
		material.setFloat("Tex1Scale", 64f);

		// DIRT texture
		Texture dirt = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
		dirt.setWrap(WrapMode.Repeat);
		material.setTexture("Tex2", dirt);
		material.setFloat("Tex2Scale", 16f);

		// ROCK texture
		Texture rock = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
		rock.setWrap(WrapMode.Repeat);
		material.setTexture("Tex3", rock);
		material.setFloat("Tex3Scale", 128f);
	}

	public void addSkyDome(){
		if(sky == null)
			sky = SkyFactory.createSky(Singleton.get().getAssetManager().getJmeAssetMan(),"models/textures/sky_povray1.jpg", true);
        Singleton.get().getSceneManager().changeCharNode(sky,0);
	}
	
	public void removeSkyDome(){
		if(sky != null)
			Singleton.get().getSceneManager().changeCharNode(sky,1);
	}
	
}
