package com.l2client.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.nio.FloatBuffer;
import java.util.Locale;

import com.jme3.asset.AssetManager;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.mesh.IndexBuffer;
import com.jme3.system.NanoTimer;
import com.jme3.system.Timer;
import com.l2client.asset.FullPathFileLocator;
import com.l2client.navigation.TiledNavMesh;

/**
 * converts all .obj files in a directory
 * into a tiled navmesh (.jnv) the name of the .obj file must conform to x_y.obj
 * naming convention where x and y are the tile numbers in x, y direction l2j's
 * center is in x between region 19 (-32768) and 20 (+32768) and in y between
 * region 18 (+32768) and 17 (-32768) so the upper left corner in 0/0 world
 * coordinates is on tile 160, 144 regions are divided into 8x8 tiles so 20*8 is
 * 160 for the 0 tile with the 0 x coordinate
 */
public class NavConverter {
	
	private static String fileEnding = "nav.obj";

	private static AssetManager assetMan = new DesktopAssetManager(Thread
			.currentThread().getContextClassLoader()
			.getResource("com/l2client/asset/loader.cfg"));

	private static FilenameFilter navFileFilter = new FilenameFilter() {

		@Override
		public boolean accept(File dir, String name) {
			if (name.endsWith(fileEnding)) {
//				String[] xy = name.substring(0, name.length()-fileEnding.length()).split("_");
//				if (xy.length != 2) {
//					System.out.println("File " + name
//							+ " does not conform to x_y"+fileEnding+" file name convention");
//					return false;
//				}
				
//					try {
//						int i = Integer.parseInt(xy[0]);
//						i = Integer.parseInt(xy[1]);
//					} catch (NumberFormatException e) {
//						System.out.println("File " + name
//								+ " does not conform to x_y"+fileEnding+" file name convention");
//					}
				return true;
			}

			return false;
		}
	};
	
	public NavConverter(){
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

		if (!file.isDirectory()) {
			if(navFileFilter.accept(file.getParentFile(), file.getName())){
				NavConverter3 com = new NavConverter3();
				com.convertNav(file);
			} else {
				System.out.println("ERROR: compiler needs a directory or file for files to convert from");
				return;// -20;
			}
		} else {
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
			if (n instanceof Geometry) {
				g = (Geometry) n;
				n = new Node(g.getName());
				((Node)n).attachChild(g);
			} else if (n instanceof Node) {
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
					from.getName().length() - fileEnding.length());// without .nav
			g.setName(fName.toLowerCase());
			TiledNavMesh navMesh = new TiledNavMesh();

			{
			String[] xy = from.getParentFile().getName().split("_");
			// l2j's center is in x between region 19 (-32768) and 20 (+32768)
			// and in y between region 18 (+32768) and 17 (-32768)
			
			// minus 20*2048, 20 because region count starts with 0_0,
			// 2048 because one region consists of 8x8 tiles (each 256x256 long)
			int xd = (Integer.parseInt(xy[0]) * 256) - (20 * 2048);
			// minus 18*2048
			int yd = (Integer.parseInt(xy[1])) * 256 - (18 * 2048);
			
			// center is in top left corner for nav mesh to be consistent with
			// this for borders move x right, move y up by half size
			Vector3f offset = new Vector3f(xd + 128, 0, yd - 128);
			System.out.println("Offset for "+from.getParentFile().getName()+"/nav2.obj should be at:"+offset);
			}
			
			navMesh.loadFromMesh(g.getMesh(), Vector3f.ZERO, isMeshRelative);
			
			time = t.getTimeInSeconds();
			System.out.println("File " + from.getAbsolutePath()
					+ " converted in " + time + " seconds");
			String path = from.getAbsolutePath();
			String parent = from.getParent();
			// replace .nav with .jnv
			t.reset();
			BinaryExporter.getInstance().save(navMesh,
					new File(parent+"/nav.jnv"));
			time = t.getTimeInSeconds();
//			System.out.println("File "+parent+"/nav.jnv saved in "
//					+ time + " seconds");
//			t.reset();
//			writeMeshToObjFile(mesh,
//					new File(parent+"/nav.obj"));
//			time = t.getTimeInSeconds();
//			System.out.println("File "+parent+"/nav.obj saved in "
//					+ time + " seconds");
			n = null;
			navMesh = null;
			System.out.println("-------------------------------------------------------");
		} catch (Exception e) {
			System.out.println("Failed to create mesh from File "+from);
			e.printStackTrace();
			System.out.println("-------------------------------------------------------");
			return -300;
		}
		return 0;
	}


	private void writeMeshToObjFile(Mesh mesh, File file) {
		try {
			PrintWriter p = new PrintWriter(file);
			p.printf(Locale.ENGLISH,"#verts\n");
			//verts
			FloatBuffer bu = mesh.getFloatBuffer(Type.Position);
			bu.rewind();
			for(int i=0; i<bu.capacity(); i+=3) {
				p.printf(Locale.ENGLISH,"v %.4f %.4f %.4f\n", bu.get(), bu.get(), bu.get());
			}
			// tex
			bu = mesh.getFloatBuffer(Type.TexCoord);
			if(bu != null) {
				bu.rewind();
				for(int i=0; i<bu.capacity(); i+=3) {
					p.printf(Locale.ENGLISH,"vt %.4f %.4f %.4f\n", bu.get(), bu.get(), bu.get());
				}
			}
			// norm
			bu = mesh.getFloatBuffer(Type.Normal);
			if(bu != null) {
				bu.rewind();
				for(int i=0; i<bu.capacity(); i+=3) {
					p.printf(Locale.ENGLISH,"vn %.4f %.4f %.4f\n", bu.get(), bu.get(), bu.get());
				}
			}
			//indices
			IndexBuffer ib = mesh.getIndexBuffer();
			if(ib != null){
				p.printf(Locale.ENGLISH,"#faces\n");
				switch(mesh.getMode()){
				case Triangles: writeTriangleBuffer(ib,p); break;
				case TriangleStrip:  writeTriangleStripBuffer(ib,p); break;
//				case TriangleFan:  writeTriangleFanBuffer(ib,p);break;
				default: throw new RuntimeException("Mesh mode not supported:"+mesh.getMode());
				}
			}
			p.flush();			
			p.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private void writeTriangleStripBuffer(IndexBuffer ib, PrintWriter p) {
		int x = ib.get(0)+1;
		int y = ib.get(1)+1;
		int z = ib.get(2)+1;
		p.printf("f %d %d %d\n", x, y, z);
		for(int i=3; i<ib.size();i++) {
			x=y;y=z;z=ib.get(i)+1;
			p.printf("f %d %d %d\n", x, y, z);
		}
	}


	private void writeTriangleBuffer(IndexBuffer ib, PrintWriter p) {
		for(int i=0; i<ib.size();i+=3) {
			p.printf("f %d %d %d\n", ib.get(i)+1, ib.get(i+1)+1, ib.get(i+2)+1);
		}
	}
}
