package com.l2client.app;

import java.io.File;
import java.io.FilenameFilter;

import com.jme3.asset.AssetManager;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.NanoTimer;
import com.jme3.system.Timer;
import com.l2client.asset.FullPathFileLocator;
import com.l2client.navigation.TiledNavMesh;

/**
 * converts all .nav files (simple .obj file with a mesh only) in a directory
 * into a tiled navmesh (.jnv) the name of the .nav file must conform to x_y.nav
 * naming convention where x and y are the tile numbers in x, y direction l2j's
 * center is in x between region 19 (-32768) and 20 (+32768) and in y between
 * region 18 (+32768) and 17 (-32768) so the upper left corner in 0/0 world
 * coordinates is on tile 160, 144 regions are divided into 8x8 tiles so 20*8 is
 * 160 for the 0 tile with the 0 x coordinate
 */
public class NavConverter {

	private static AssetManager assetMan = new DesktopAssetManager(Thread
			.currentThread().getContextClassLoader()
			.getResource("com/l2client/asset/loader.cfg"));

	private static FilenameFilter navFileFilter = new FilenameFilter() {

		@Override
		public boolean accept(File dir, String name) {
			if (name.endsWith(".nav"))
				return true;

			return false;
		}
	};

	/**
	 * @param args
	 *            directory of the exported files, TODO flags vsam for only
	 *            converting meshes, skeletons, anims, materials
	 * @throws Throwable
	 */
	public static void main(String[] args) throws Throwable {
		if (args != null && args.length != 1) {
			System.out
					.println("ERROR: compiler needs a directory to start converting .nav files");
			// return -10;
			return;
		}

		File file = new File(args[0]);

		if (!file.isDirectory()) {
			System.out
					.println("ERROR: compiler needs a directory for files to convert from");
			return;// -20;
		} else {
			assetMan.registerLocator(file.toString(), FullPathFileLocator.class);

			NavConverter com = new NavConverter();
//			com.setMeshRelative(false);
			com.convert(file);
		}
	}

	/**
	 * should be true if your model is located at 0/0 and must be moved to its
	 * offset false if your model is already in/at world coordinates
	 */
	private boolean isMeshRelative = true;

	/**
	 * @param isMeshRelative
	 *            the isMeshRelative to set
	 */
	public void setMeshRelative(boolean isMeshRelative) {
		this.isMeshRelative = isMeshRelative;
	}

	private void convert(File directory) throws Throwable {
		for (File ff : directory.listFiles(navFileFilter))
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
			Geometry g;
			// need to load vial loadModel, JME does not see obj, etc as
			// Assets..
			t.reset();
			Spatial n = (Spatial) assetMan.loadModel(from.getAbsolutePath());
			time = t.getTimeInSeconds();
			System.out.println("File " + from.getAbsolutePath() + " loaded in "
					+ time + " seconds");
			if (n instanceof Geometry)
				g = (Geometry) n;
			else if (n instanceof Node) {
				if (((Node) n).getChildren().size() > 1)
					throw new Throwable(
							"Mesh with more children detected than one on "
									+ from.getName());
				g = (Geometry) ((Node) n).getChild(0);
			} else
				throw new Throwable("Spatial loaded was unexpected type "
						+ n.getClass());
			// jme fucked up the model names, and ignores any object name
			// entries so we fix a bit
			String fName = from.getName().substring(0,
					from.getName().length() - 4);// without .nav
			g.setName(fName.toLowerCase());
			TiledNavMesh navMesh = new TiledNavMesh();

			String[] xy = fName.split("_");
			if (xy.length < 2) {
				System.out.println("File " + from.getAbsolutePath()
						+ " does not conform to x_y.nav file name convention");
				return -100;
			}
			// l2j's center is in x between region 19 (-32768) and 20 (+32768)
			// and in y between region 18 (+32768) and 17 (-32768)
			int xd = (Integer.parseInt(xy[0]) * 256) - (20 * 2048);// minus
																	// 20*2048,
																	// 20
																	// because
																	// region
																	// count
																	// starts
																	// with 0_0,
																	// 2048
																	// because
																	// one
																	// region
																	// consists
																	// of 8x8
																	// tiles
																	// (each
																	// 256x256
																	// long)
			int yd = (Integer.parseInt(xy[1])) * 256 - (18 * 2048);// minus
																	// 18*2048
			// center is in top left corner for nav mesh to be consistent with
			// this for borders move x right, move y up by half size
			Vector3f offset = new Vector3f(xd + 128, 0, yd - 128);
			t.reset();
			navMesh.loadFromMesh(g.getMesh(), offset, isMeshRelative);
			time = t.getTimeInSeconds();
			System.out.println("File " + from.getAbsolutePath()
					+ " converted in " + time + " seconds");
			String path = from.getAbsolutePath();
			// replace .nav with .jnv
			t.reset();
			BinaryExporter.getInstance().save(navMesh,
					new File(path.substring(0, path.length() - 4) + ".jnv"));
			time = t.getTimeInSeconds();
			System.out.println("File " + from.getAbsolutePath() + " saved in "
					+ time + " seconds");
			System.out.println("-------------------------------------------------------");
			n = null;
//			// DEBUG try to find the terrain
//			try {
//				t.reset();
//				Spatial s = (Spatial) assetMan.loadModel(from.getAbsolutePath()
//						.substring(0, from.getAbsolutePath().length() - 4)
//						+ ".obj");
//				time = t.getTimeInSeconds();
//				System.out.println("File " + from.getAbsolutePath()
//						+ ".obj loaded in " + time + " seconds");
//				if (s instanceof Geometry)
//					g = (Geometry) s;
//				else if (s instanceof Node) {
//					if (((Node) n).getChildren().size() > 1)
//						throw new Throwable(
//								"Mesh with more children detected than one on "
//										+ from.getName());
//					g = (Geometry) ((Node) s).getChild(0);
//				} else
//					throw new Throwable("Spatial loaded was unexpected type "
//							+ s.getClass());
//			} catch (Exception e) {
//				e.printStackTrace();
//				return -200;
//			}
			
////			// FIXME currently just a dummy terrain
////			if (isMeshRelative)
////				g.setLocalTranslation(offset);
//			t.reset();
//			BinaryExporter.getInstance().save(g,
//					new File(path.substring(0, path.length() - 4) + ".j3o"));
//			time = t.getTimeInSeconds();
//			System.out.println("File " + from.getAbsolutePath()
//					+ ".j3o saved in " + time + " seconds");
		} catch (Exception e) {
			e.printStackTrace();
			return -300;
		}
		return 0;
	}
}
