package com.l2client.asset;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLocator;
import com.jme3.asset.AssetManager;

/**
 * Files are fully quallified, root is not set in this case, or the root of the last loaded file is used if only a filename.
 *
 */
public class FullPathFileLocator implements AssetLocator {
	
		private File root;

	    public void setRootPath(String rootPath) {
	    	//ignore any roots
	    }

	    private static class AssetInfoFile extends AssetInfo {

	        private File file;

	        public AssetInfoFile(AssetManager manager, AssetKey key, File file){
	            super(manager, key);
	            this.file = file;
	        }

	        @Override
	        public InputStream openStream() {
	            try{
	                return new FileInputStream(file);
	            }catch (FileNotFoundException ex){
	                return null;
	            }
	        }
	    }

	    public AssetInfo locate(AssetManager manager, AssetKey key) {
	        File file = new File(key.getName());
	        if (file.exists() && file.isFile()){
	        	root = file.getParentFile();
	            return new AssetInfoFile(manager, key, file);
	        }else{
	        	//try on last root
	        	file = new File(root,key.getName());
	        	if(file.exists() && file.isFile())
	        		return new AssetInfoFile(manager, key, file);
	        }
	        return null;
	    }

	
}
