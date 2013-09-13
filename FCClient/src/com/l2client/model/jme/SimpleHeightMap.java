package com.l2client.model.jme;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import com.jme3.terrain.heightmap.AbstractHeightMap;

/**
 * A simple height map which supports arbitrary sized maps
 */
@Deprecated //no longer used, prepare for removal
public class SimpleHeightMap extends AbstractHeightMap {

	private String filename ="";
	/**
	 * standard ctor
	 */
	public SimpleHeightMap() {
		super();
	}
	
	/**
	 * creates a height map of size * size dimension, the passed height values are 
	 * used if size*size corresponds to the count of entries in the heights array, 
	 * otherwise size*size height values between -1.0 and 4.0 are created
	 * @param size		the width and depth of the height map
	 * @param heights	optional height map values for storing in the map, otherwise size*size height values between -1.0 and 4.0 are created
	 */
	public SimpleHeightMap(int size, float[] heights) {	
		if(size > 0)
			this.size = size;
		else
			return;
		
		if(heights != null && heights.length > 0 && heights.length == (size*size))		
			this.heightData = heights;
		else
			this.heightData = createDummyTerrain(size, size);
	}
	
	@Override
	public boolean load() {
		DataInputStream in = null;
		try {
			//FIXME test this
			FileInputStream fs = new FileInputStream(filename);
			size = readInt(fs);
			if(heightData != null)
				heightData = null;
			
			int verts = size * size;
			heightData = new float[verts];
			//just read in the height values, nothing else!
			//for(int i = 0;i<size;i++){
			for(int i=size-1;i>=0;i--) {
				//for(int j=size-1;j>=0;j--) {
				for(int j=0;j<size;j++) {
					//j+(i*size)
					heightData[j+(i*size)]=readFloat(fs);
				}
			}
		} catch (Exception e) {
			size = 0;
			heightData=null;
			return false;
		}
		return true;
	}
	
	/**
	 * creates a map of 1-50f height values of _x * _y size
	 * @param _x	x size of map
	 * @param _y	y size of map
	 * @return		the final map
	 */
	private float[] createDummyTerrain(int _x, int _y) {
		float [] map = new float[_x*_y];
		int i = 0;
		Random rand = new Random();
		for(int x = 0;x<_x;x++){
			for(int y = 0;y<_y;y++,i++){
				if(x==0 || x== _x-1||
						y==0 || y == _y-1)
					map[i] = 1.0f;
				else
					map[i] = -1.0f + rand.nextFloat()*5.0f;
			}
					
		}
		return map;
	}

	
	public String getFilename() {
		return filename;
	}

	
	public void setFilename(String name) {
		filename = name;
	}
	
    private int readInt(InputStream in) throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        int ch3 = in.read();
        int ch4 = in.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new EOFException();
        return ((ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0));
    }
    private float readFloat(InputStream in) throws IOException {
    	return Float.intBitsToFloat(readInt(in));
    }

}