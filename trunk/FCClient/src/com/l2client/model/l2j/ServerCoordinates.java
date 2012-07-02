package com.l2client.model.l2j;

import com.jme3.math.Vector3f;

/**
 * A central class for coordinates as understood by the server,
 * used for central coordinates scaling and conversion to the specific
 * game coordinates classes
 * L2J uses z as up
 */
public class ServerCoordinates {

	//FIXME scalefactor should be 1/16th or 0.0625 not 0.125 (1/8) as a cell is 16 units, not 8 units
	private static float scaleFactor = 0.0625f;
	public static Vector3f getJMEVector(float x, float y, float z){
		return new Vector3f(x*scaleFactor, y*scaleFactor, z*scaleFactor);
	}
	
	/**
	 * Sets the scalefactor the coordinates are scaled by (multiplied with)
	 * A scalefactor of 1/8 th would be 0.125 for f for example.
	 * @param f
	 */
	public static void setScaleFactor(float f){
		scaleFactor = f;
	}
	
	public static float getScaleFactor(){
		return scaleFactor;
	}
}
