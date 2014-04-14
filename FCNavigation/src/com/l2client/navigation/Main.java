package com.l2client.navigation;

import java.io.File;
import java.io.IOException;

import com.jme3.export.binary.BinaryImporter;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NavigationManager nav = NavigationManager.get();
		System.out.println("Meshcount = 0?:"+nav.getMeshCount());
		try {
			TiledNavMesh nm = (TiledNavMesh) BinaryImporter.getInstance().load(new File("tile/120_177/nav.jnv"));
			nm.getPosition();
			nav.addMesh(nm);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
