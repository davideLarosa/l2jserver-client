package com.l2client.test.junit;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import com.jme3.asset.DesktopAssetManager;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.export.binary.BinaryImporter;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.system.JmeSystem;
import com.jme3.system.Timer;
import com.l2client.navigation.NavigationMesh;

public class NavLoadingTest extends TestCase {

	public void testSelf(){
		
		NavigationMesh m = getNavMesh("0_0", Vector3f.ZERO);
		try {
			BinaryExporter.getInstance().save(m, new File("0_0.jnv"));
			m = (NavigationMesh) BinaryImporter.getInstance().load(new File("0_0.jnv"));
			DesktopAssetManager assetManager = new DesktopAssetManager(true);
			Geometry g00 = (Geometry) assetManager.loadAsset("grid.0_0.j3o");
			assertNotNull("Asset for grid 0_0 is missing", g00);
			m = new NavigationMesh();
			
			long eT = 0L;
	        long sT =  System.currentTimeMillis();	        
			m.loadFromGeom(g00);
			int cells = m.TotalCells();
			eT =System.currentTimeMillis();	        
	        System.out.println("Navmesh building from g00 took "+(eT-sT)+" milli seconds for "+cells+" cells");
	        BinaryExporter.getInstance().save(m, new File("0_0.jnv"));
	        sT = System.currentTimeMillis();
	        m = (NavigationMesh) BinaryImporter.getInstance().load(new File("0_0.jnv"));
	        eT = System.currentTimeMillis();
	        System.out.println("Navmesh loading from file took "+(eT-sT)+" milli seconds for "+cells+" cells");
	        assertNotNull(m);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    private NavigationMesh getNavMesh(String name, Vector3f worldtrans){
    	NavigationMesh m = new NavigationMesh();
    	m.loadFromData(new Vector3f[] {new Vector3f(-64,0,64),new Vector3f(-64,0,-64), new Vector3f(64,0,-64), new Vector3f(64,0,64)}, new short[][] {{0,2,1},{0,3,2}}, worldtrans);
    	return m;
    }

}
