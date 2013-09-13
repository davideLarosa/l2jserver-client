package com.l2client.test;

import java.io.File;
import java.io.IOException;

import com.jme3.asset.DesktopAssetManager;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.export.binary.BinaryImporter;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.l2client.navigation.TiledNavMesh;

public class TestNavLoading {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TiledNavMesh m;
		try {
			DesktopAssetManager assetManager = new DesktopAssetManager(true);
			loadSave(assetManager, "grid.0_0.j3o", Vector3f.ZERO);
			loadSave(assetManager, "grid.0_1.j3o", new Vector3f(0,0,-128));
			loadSave(assetManager, "grid.1_0.j3o", new Vector3f(128,0,0));
			loadSave(assetManager, "grid.1_1.j3o", new Vector3f(128,0,-128));
			loadSave(assetManager, "grid_big.j3o", Vector3f.ZERO);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	}

	public static void loadSave(DesktopAssetManager assetManager, String file, Vector3f transl)
			throws IOException {
		TiledNavMesh m;
		Geometry g00 = (Geometry) assetManager.loadAsset(file);
		if(g00 == null){
			System.out.println("Asset for "+file+"is missing");
			return;
		}
		g00.setLocalTranslation(transl);
		g00.updateGeometricState();
		m = new TiledNavMesh();
		
		long eT = 0L;
		long sT =  System.currentTimeMillis();	        
		m.loadFromGeom(g00);
		int cells = m.TotalCells();
		eT =System.currentTimeMillis();	        
		System.out.println("Navmesh building from "+file+"took "+(eT-sT)+" milli seconds for "+cells+" cells");
		BinaryExporter.getInstance().save(m, new File(file+".jnv"));
		sT = System.currentTimeMillis();
		m = (TiledNavMesh) BinaryImporter.getInstance().load(new File(file+".jnv"));
		eT = System.currentTimeMillis();
		System.out.println("Navmesh loading from file took "+(eT-sT)+" milli seconds for "+cells+" cells");
	}

}
