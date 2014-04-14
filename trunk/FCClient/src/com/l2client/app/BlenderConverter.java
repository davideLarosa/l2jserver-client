package com.l2client.app;

import java.io.File;
import java.io.FilenameFilter;

import com.jme3.asset.AssetManager;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.scene.Spatial;
import com.jme3.system.NanoTimer;
import com.jme3.system.Timer;
import com.l2client.asset.FullPathFileLocator;

/**
 * converts all .blender files in a directory to .j3o
 */
public class BlenderConverter {
	
	private static String fileEnding = ".blend";

	private static AssetManager assetMan = new DesktopAssetManager(true);

	private static FilenameFilter fileNameFilter = new FilenameFilter() {

		@Override
		public boolean accept(File dir, String name) {
			if (name.endsWith(fileEnding)) {
				return true;
			}

			return false;
		}
	};
	
	public BlenderConverter(){
	}


	/**
	 * @param args
	 *            directory of the exported files, TODO flags vsam for only
	 *            converting meshes, skeletons, anims, materials
	 * @throws Throwable
	 */
	public static void main(String[] args) throws Throwable {
		if (args != null && args.length != 1) {
			System.out
					.println("ERROR: compiler needs a directory or file to start converting "+fileEnding+" files");
			// return -10;
			return;
		}

		File file = new File(args[0]);
		
		assetMan.registerLocator(file.toString(), FullPathFileLocator.class);

		BlenderConverter com = new BlenderConverter();
		com.convert(file);
	}


	private void convert(File directory) throws Throwable {
		for (File ff : directory.listFiles(fileNameFilter))
			convertNav(ff);

		for (File fs : directory.listFiles())
			if (fs.isDirectory())
				convert(fs);
	}

	public int convertNav(File from) throws Throwable {
		Timer t = new NanoTimer();
		float time = 0f;

		// new loader each time, yes
		try {
			t.reset();
			Spatial n = (Spatial) assetMan.loadModel(from.getAbsolutePath());
			if(n != null) {
				String fName = from.getName().substring(0,
						from.getName().length() - fileEnding.length());
				fName = from.getParent()+"/"+fName+".j3o";
				n.updateModelBound();
				n.updateGeometricState();
				BinaryExporter.getInstance().save(n,new File(fName));
				time = t.getTimeInSeconds();
				System.out.println("File "+fName+".saved in "+ time +" seconds");
			} else {
				System.out.println("Failed to load model "+from);
			}

		} catch (Exception e) {
			System.out.println("Failed to convert File "+from+ " with exception "+e.getMessage());
		}
		return 0;
	}
}
