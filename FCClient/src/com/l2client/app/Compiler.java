package com.l2client.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import com.jme3.animation.Animation;
import com.jme3.animation.BoneAnimation;
import com.jme3.asset.AssetManager;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.material.Material;
import com.jme3.material.MaterialList;
import com.jme3.scene.Spatial;
import com.jme3.scene.plugins.ogre.AnimData;
import com.jme3.scene.plugins.ogre.SkeletonLoader;
import com.l2client.asset.FullPathFileLocator;
import com.l2client.model.WeaponSet;

/**
 * Compiles ogre files into split up jme file parts
 *
 */
public class Compiler {

	private static final String MEGASET_CSV = "megaset.csv";
	private static byte[] delim = ";".getBytes();
	private static AssetManager assetMan = new DesktopAssetManager(true);

	private static FilenameFilter skeletonFilter = new FilenameFilter() {

		@Override
		public boolean accept(File dir, String name) {
			if (name.endsWith(".skeleton.xml"))
				return true;

			return false;
		}
	};

	public static FilenameFilter meshFilter = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			if (name.endsWith(".mesh.xml"))
				return true;

			return false;
		}
	};

	public static FilenameFilter animFilter = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			if (name.endsWith(".anim.xml"))
				return true;

			return false;
		}
	};

	public static FilenameFilter materialFilter = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			if (name.endsWith(".material"))
				return true;

			return false;
		}
	};
	
	public static FilenameFilter imageFilter = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			name = name.toLowerCase();
			if (name.endsWith(".dds") || 
				name.endsWith(".tga") ||
				name.endsWith(".png") ||
				name.endsWith(".gif") ||
				name.endsWith(".bmp") )
				return true;

			return false;
		}
	};
	
	public static FilenameFilter weaponFilter = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			name = name.toLowerCase();
			if (name.endsWith(".weapon") )
				return true;

			return false;
		}
	};

	/**
	 * File to save a megaSet definition
	 */
	private OutputStream megaWriter;
	public Compiler(FileOutputStream megaFile) {
		megaWriter = megaFile;
	}

	/**
	 * @param args directory of the exported files, TODO flags vsam for only converting meshes, skeletons, anims, materials
	 */
	public static void main(String[] args) {
		if (args != null && args.length != 2) {
			System.out
					.println("ERROR: compiler needs a directory for files to convert as parameter, and a directory as target");
			//return -10;
			return;
		}
		

		File file = new File(args[0]);
		File target = new File(args[1]);

		if (!file.isDirectory()) {
			System.out
					.println("ERROR: compiler needs a directory for files to convert from");
			return;// -20;
		} else if (!target.isDirectory()) {
			System.out
			.println("ERROR: compiler needs a directory for files to convert to");
			return;// -20;
		}
		else {assetMan.registerLocator(file.toString(),
                FullPathFileLocator.class);
			FileOutputStream megaFile = createMegaSetFile(target.getAbsolutePath()+File.separatorChar);
			Compiler com = new Compiler(megaFile);
			for(File f : file.listFiles()){
				if(f.isDirectory())
				{
					if("meshes".equals(f.getName())){
						com.convertMeshesParent(f,target);
					} 
					else if("anims".equals(f.getName())) {
						com.convertAnimsParent(f,target);
					}
				}
			}
		}
	}

	private static FileOutputStream createMegaSetFile(String absolutePath) {
		File setFile = new File(absolutePath+ MEGASET_CSV);
		try {
			if(setFile.exists())
				setFile.delete();
			
			setFile.createNewFile();
			return 	new FileOutputStream(setFile);		
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}	
	}

	private void convertMeshesParent(File f, File target) {
		for(File ff : f.listFiles()){
			if(ff.isDirectory())
			{
				convertMaterials(ff, target);
				convertMeshes(ff, target);
				convertSkeletons(ff, target);
//				addAnims(ff,target);
				System.out.println("Processed "+ff.getName());
			}
		}
	}

//	private int addAnims(File ff, File target) {
//		for (File name : ff.listFiles(animFilter)) {
//			try {
//				String anim = (new java.io.BufferedReader(new FileReader(name))).readLine();
//				if(anim != null){
//					String finName = name.getName();
//					finName = finName.substring(0, finName.length()-9);//truncate .anim.xml
//					appendMegaSet(new String[] {"entity", ff.getName(),"anim",finName,anim});
//				}
//
//			} catch (Exception e) {
//				e.printStackTrace();
//				return -100;
//			}
//		}
//		
//		return 0;
//	}

	private void convertAnimsParent(File f, File target) {
		for(File ff : f.listFiles()){
			if(ff.isDirectory())
			{
				convertAnims(ff, target);
				System.out.println("Processed "+ff.getName());
			}
		}
	}

	private int convertMeshes(File file, File target) {
		//target directory (exporters create them directly
		String path = target.getAbsolutePath() + File.separatorChar + "meshes";

		//weapon definitions are just copied
		copyAllWeaponFilesToTarget(file, target);
		
		//spool all anims
		for (final File name : file.listFiles(meshFilter)) {
			//new loader each time, yes
			try {
				String fName = name.getName().substring(0,name.getName().length()-".mesh.xml".length());
				Spatial n = (Spatial) assetMan.loadAsset(name.getAbsolutePath());
				n.setName(fName);
					BinaryExporter.getInstance().save(n,
							new File(path + File.separatorChar + file.getName()+ File.separatorChar + fName+ ".j3o"));
					appendMegaSet(new String[] {"entity",file.getName(),"mesh",truncateEndNumbers(fName),"meshes/"+file.getName()+"/"+fName + ".j3o"});

			} catch (Exception e) {
				e.printStackTrace();
				return -100;
			}
		}
		
		return 0;
	}

	private int convertSkeletons(File file, File target) {
		//target directory (exporters create them directly
		String path = target.getAbsolutePath() + File.separatorChar + "skeleton";

		//spool all anims
		for (File name : file.listFiles(skeletonFilter)) {
			//new loader each time, yes
//			SkeletonLoader loader;
			try {
				SkeletonLoader loader = new SkeletonLoader();
				AnimData ad = (AnimData)loader.load(name.toURI().toURL().openStream());
				if(ad != null && ad.skeleton != null){
					String fName = name.getName().substring(0,name.getName().length()-".xml".length());
					
					BinaryExporter.getInstance().save(ad.skeleton,
							new File(path + File.separatorChar + fName+ ".j3o"));
					appendMegaSet(new String[] {"entity", file.getName(),"skel","skeleton/"+fName+ ".j3o"});
				}

			} catch (Exception e) {
				e.printStackTrace();
				return -100;
			}
		}
		
		return 0;
	}
	
	private int convertAnims(File file, File target) {
		//target directory (exporters create them directly
		String path = target.getAbsolutePath() + File.separatorChar + "anims";

		//spool all anims
		for (File name : file.listFiles(animFilter)) {
			//new loader each time, yes
			com.jme3.scene.plugins.ogre.SkeletonLoader loader;
			try {
				loader = new com.jme3.scene.plugins.ogre.SkeletonLoader();
				AnimData ad = (AnimData) loader.load(name.toURI().toURL().openStream());
				//special case we just have only singled out anims
				if(ad.anims.size() == 1 ){
					Animation anim = ad.anims.get(0);
					BinaryExporter.getInstance().save(anim,
							new File(path + File.separatorChar + file.getName() + File.separatorChar + anim.getName().toLowerCase() + ".j3o"));
					appendMegaSet(new String[]{"anim",file.getName(),truncateEndNumbers(anim.getName().toLowerCase()),"anims/"+file.getName()+"/"+anim.getName().toLowerCase()+ ".j3o"});
				}

			} catch (Exception e) {
				e.printStackTrace();
				return -100;
			}
		}
		
		return 0;
	}

	//FIXME this must be done after the textures are copied to target, otherwise Texture will store _raw URL of image file :-(
	private int convertMaterials(File file, File target) {
		
		//target directory (exporters create them directly
		String path = target.getAbsolutePath() + File.separatorChar + "mats";
		//first copy all images so materials will save the new path not the old
		//we assume images are stored in the same directory as the material that uses them
		final File fpath = copyAllImageFilesToTarget(file, target);

//		fpath.mkdir();
		File[] fList = file.listFiles(materialFilter);
		//spool all materials
		for (File name : fList) {
			try {
				
				MaterialList mats = (MaterialList) assetMan.loadAsset(name.getAbsolutePath());
				for(String id : mats.keySet()){
					Material m = mats.get(id);
					String ass = "mats/"+file.getName()+"/"+id+ ".j3m";
//					m.setAssetName(ass);
				BinaryExporter.getInstance().save(m,
						new File(path + File.separatorChar + file.getName() + File.separatorChar + id+ ".j3m"));
				//Special handling of onyl one material! this one must be named "default"!
				if(fList.length > 1){
					appendMegaSet(new String[]{"entity",file.getName(),"mat",id,ass});
				}
				else {
					appendMegaSet(new String[]{"entity",file.getName(),"mat","default",ass});

				}
				}
			} catch (Exception e) {
				e.printStackTrace();
//				return -100;
			}
		}
		return 0;
	}
	
	private void appendMegaSet(String [] values) {
		if(megaWriter != null && values != null && values.length > 0){
			try {
				for(int i = 0; i<values.length; i++){
					
						megaWriter.write(values[i].getBytes());
					if(i<values.length-1)
						megaWriter.write(delim);
				}
				megaWriter.write('\n');
				megaWriter.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private String truncateEndNumbers(String in){
		if(in.matches("[a-z_A-Z]*_[0-9][0-9]"))
			return in.substring(0,in.length()-3);
		else 
			if(in.matches("[a-z_A-Z]*_[0-9]") || in.matches("[a-z_A-Z]*[0-9][0-9]"))
				return in.substring(0,in.length()-2);
			else
				if(in.matches("[a-z_A-Z]*[0-9]"))
					return in.substring(0,in.length()-1);
		
		return in;
	}
	
	private File copyAllImageFilesToTarget(File sourceDir, File targetDir){
		File target = new File(targetDir.getAbsolutePath()+File.separatorChar+"textures"+File.separatorChar+"entity");
		target.mkdirs();
		
		for(File f : sourceDir.listFiles(imageFilter))
			try {
				Compiler.copyFile(f, new File(target.getAbsolutePath()+File.separator+f.getName()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return target;
		
	}
	
	public static void copyFile(File sourceFile, File destFile) throws IOException {
		 if(!destFile.getParentFile().exists()) {
			 //create any dirs inbetween or we will get an ioexception
			 destFile.getParentFile().mkdirs();
			 //destFile.createNewFile();
		 }
		 
		 FileChannel source = null;
		 FileChannel destination = null;
		 try {
		  source = new FileInputStream(sourceFile).getChannel();
		  destination = new FileOutputStream(destFile).getChannel();
		  destination.transferFrom(source, 0, source.size());
		 }
		 finally {
		  if(source != null) {
		   source.close();
		  }
		  if(destination != null) {
		   destination.close();
		  }
		}
	}
	
	private File copyAllWeaponFilesToTarget(File sourceDir, File targetDir){
		File target = new File(targetDir.getAbsolutePath()+File.separatorChar+"weapons"+File.separatorChar+sourceDir.getName());
		target.mkdirs();

		for(File f : sourceDir.listFiles(weaponFilter))
		{
			WeaponSet s = new WeaponSet();
			if(s.readFromTextfile(f.getAbsolutePath())){
				try {
					BinaryExporter.getInstance().save(s,new File(target.getAbsolutePath()+File.separator+f.getName()+ ".j3o"));
					appendMegaSet(new String[] {"entity",sourceDir.getName(),"weapon","Weapon","weapons/"+sourceDir.getName()+"/"+f.getName()+ ".j3o"});
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return target;
		
	}

}
