package com.l2client.test;

import java.io.File;
import java.io.IOException;

import com.jme3.asset.DesktopAssetManager;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.export.binary.BinaryImporter;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.l2client.controller.area.IArea;
import com.l2client.navigation.NavigationMesh;

public class TestRegion {

	/**
	 * @param args
	 */
	public static void main(String[] args){

		try {
//	        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//	        mat.setColor("Color", ColorRGBA.LightGray);
//			grid.setMaterial(mat);
			@SuppressWarnings("deprecation")
			DesktopAssetManager assetManager = new DesktopAssetManager(true);
//			loadSave(assetManager, "4_0.obj", Vector3f.ZERO);
//			loadSave(assetManager, "4_1.obj", new Vector3f(0,0,IArea.TERRAIN_SIZE));
//			loadSave(assetManager, "4_2.obj", new Vector3f(0,0,2*IArea.TERRAIN_SIZE));
//			loadSave(assetManager, "4_3.obj", new Vector3f(0,0,3*IArea.TERRAIN_SIZE));
//			loadSave(assetManager, "5_0.obj", new Vector3f(IArea.TERRAIN_SIZE,0,0));
//			loadSave(assetManager, "5_1.obj", new Vector3f(IArea.TERRAIN_SIZE,0,IArea.TERRAIN_SIZE));
//			loadSave(assetManager, "5_2.obj", new Vector3f(IArea.TERRAIN_SIZE,0,2*IArea.TERRAIN_SIZE));
//			loadSave(assetManager, "5_3.obj", new Vector3f(IArea.TERRAIN_SIZE,0,3*IArea.TERRAIN_SIZE));
//			loadSave(assetManager, "6_0.obj", new Vector3f(2*IArea.TERRAIN_SIZE,0,0));
//			loadSave(assetManager, "6_1.obj", new Vector3f(2*IArea.TERRAIN_SIZE,0,IArea.TERRAIN_SIZE));
//			loadSave(assetManager, "6_2.obj", new Vector3f(2*IArea.TERRAIN_SIZE,0,2*IArea.TERRAIN_SIZE));
//			loadSave(assetManager, "6_3.obj", new Vector3f(2*IArea.TERRAIN_SIZE,0,3*IArea.TERRAIN_SIZE));
//			loadSave(assetManager, "7_0.obj", new Vector3f(3*IArea.TERRAIN_SIZE,0,0));
//			loadSave(assetManager, "7_1.obj", new Vector3f(3*IArea.TERRAIN_SIZE,0,IArea.TERRAIN_SIZE));
//			loadSave(assetManager, "7_2.obj", new Vector3f(3*IArea.TERRAIN_SIZE,0,2*IArea.TERRAIN_SIZE));
			loadSave(assetManager, "7_3.obj", new Vector3f(3*IArea.TERRAIN_SIZE,0,3*IArea.TERRAIN_SIZE));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	}

	private static void loadSave(DesktopAssetManager assetManager, String file, Vector3f transl)
			throws IOException {
		NavigationMesh m;
		Geometry g00 = (Geometry) assetManager.loadModel("export/"+file);
		if(g00 == null){
			System.out.println("Asset for "+file+"is missing");
			return;
		}

		g00.setLocalTranslation(transl);
		g00.updateGeometricState();
		m = new NavigationMesh();
		BinaryExporter.getInstance().save(g00, new File("data/export/"+file + ".j3o"));
		long eT = 0L;
		long sT =  System.currentTimeMillis();	        
		m.loadFromGeom(g00);
		int cells = m.TotalCells();
		eT =System.currentTimeMillis();	        
		System.out.println("Navmesh building from "+file+"took "+(eT-sT)+" milli seconds for "+cells+" cells");
		BinaryExporter.getInstance().save(m, new File("data/export/"+file+".jnv"));
		sT = System.currentTimeMillis();
		//FIXME why is the file created but not loadable thereafter?
		m = (NavigationMesh) BinaryImporter.getInstance().load(new File("data/export/"+file+".jnv"));
		eT = System.currentTimeMillis();
		System.out.println("Navmesh loading from file took "+(eT-sT)+" milli seconds for "+cells+" cells");
	}

	
//	private void addNavWithMesh(EntityNavigationManager em, String meshFile,
//			int x, int y, int z) {
//		try {
////			NavigationMesh m = getNavMesh(meshFile+".jnv", 128, x, y, z);
//			NavigationMesh m = getMeshNav(meshFile, 128, x, y, z);
//			em.attachMesh(m);
//			Geometry grid = (Geometry) assetManager.loadAsset(meshFile);
//			grid.setLocalTranslation(x, y, z);
//
//			grid.updateGeometricState();
//			rootNode.attachChild(grid);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

}
