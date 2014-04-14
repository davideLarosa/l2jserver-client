
package com.l2client.navigation;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.logging.Logger;

import com.jme3.export.binary.BinaryImporter;
import com.jme3.math.Vector3f;
import com.l2client.controller.area.Tile;

public class NavManagerLoadTest {
	private static NavigationManager navMan = NavigationManager.get();
	private final static Logger log = Logger.getLogger( NavManagerLoadTest.class.getName());
	
	private static FilenameFilter navFileFilter = new FilenameFilter() {

		@Override
		public boolean accept(File dir, String name) {
			if (name.endsWith(".jnv")) {
				return true;
			}
			return false;
		}
	};
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//set the cache drastically low
		navMan.setCacheSize(10);
		Tile t = new Tile(9999, 9999);
		int code = t.hashCode();
		System.gc();
		long sTot, sFree, sMax;
		long dTot, dFree;
		sTot = Runtime.getRuntime().totalMemory();
		sFree = Runtime.getRuntime().freeMemory();
		sMax = Runtime.getRuntime().maxMemory();
		tryLoad();
		log.info("Loaded meshes:"+navMan.getMeshCount());
		dTot = Runtime.getRuntime().totalMemory();
		dFree = Runtime.getRuntime().freeMemory();
		log.info("Mem end Mbytes:        tot:"+dTot/1024/1024+" free:"+dFree/1024/1024+" max:"+sMax/1024/1024);
		System.gc();
		log.info("Loaded meshes:"+navMan.getMeshCount());
//		log.info(navMan.toString());
		
//		TiledNavMesh nm = navMan.getNavMesh(1536.0f, 768.0f);
//		int x = Tile.getTileFromWorldXPosition((int)1536.0f);
//		int z = Tile.getTileFromWorldZPosition((int)768.0f);
//		int hc1 = nm.hashCode();
//		t = new Tile(x,z);
//		int hc2 = t.hashCode();
//		TiledNavMesh nm2 = navMan.getNavMesh(101660147);
		
//		log.info(navMan.toString());
		navMan.getNavMesh(1536.0f, 768.0f);
//		log.info(navMan.toString());
		navMan.getNavMesh(-10240.0f,8448.0f);
		navMan.getNavMesh(-10240.0f,8448.0f);
		log.info(navMan.toString());
		dTot = Runtime.getRuntime().totalMemory();
		dFree = Runtime.getRuntime().freeMemory();		
		log.info("Mem end + gc Mbytes:   tot:"+dTot/1024/1024+" free:"+dFree/1024/1024+" max:"+sMax/1024/1024);
		log.info("Loaded meshes:"+navMan.getMeshCount());
		log.info(navMan.toString());
	}


	private static void tryLoad() {
		
		try {
			
			 Enumeration<URL> url = NavManagerLoadTest.class.getClassLoader().getResources("tile/");			 
			File startDir = new File(url.nextElement().toURI());
			File[] files = startDir.listFiles();
			for (File subdirectory : files)
			{
				for (File file : subdirectory.listFiles(navFileFilter))
				{
					
					TiledNavMesh nm;
					try {
						//load tiled namvmesh
						nm = (TiledNavMesh) BinaryImporter.getInstance().load(file);
						System.gc();
						//add it to the navmanager
						if(nm != null){
							//nav meshes are from 0/0 to tile.x/tile.y for conveniance so move it
							int xTile, zTile;
							String[] splits = file.getParentFile().getName().split("_");
							if(splits.length == 2){
								xTile = Integer.parseInt(splits[0]);
								zTile = Integer.parseInt(splits[1]);
								//nm.setPosition(new Vector3f(Tile.getWorldPositionOfXTile(x), 0f,Tile.getWorldPositionOfZTile(z)));
								nm.setPosition(new Vector3f((xTile-160)<<8, 0f, (zTile-144)<<8));
								navMan.addMesh(nm);
//								log.info(navMan.toString());
							}
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
