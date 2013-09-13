package com.l2client.model.l2j;

import com.jme3.math.FastMath;


/**
 * A central class for coordinates as understood by the server,
 * used for central coordinates scaling and conversion to the specific
 * game coordinates classes
 * L2J uses z as up
 * JME uses y as up (this is not shifted here)
 */
public class ServerValues {

	//scalefactor should be 1/16th or 0.0625 not 0.125 (1/8) as a cell is 16 units, not 8 units
	static float coordFactor = 0.0625f;//Server coordinates * coordFactor = ClientCoords
	static float invertCoord = 1f/coordFactor; //Client coords * invertcoords = Servercoords
	static float headingFactor = 1.0f/182.044444444f;//Server heading * headingFactor = ClientHEading
    static final float invertHeading = 182.044444444f; //Clientheading (in degree) * invertheading = server heading
	
	/**
	 * Scale a value to server heading (360 degrees * 182.0444)
	 * @param f	a float client heading in radians
	 * @return an int representing server heading in degrees
	 */
	public static int getServerHeading(float f){
		return (int)(invertHeading*f*FastMath.RAD_TO_DEG);
	}
	
	
	/**
	 * Scale a value to client heading in radians
	 * @param i an int in server heading degrees
	 * @return a float in client heading in radians
	 */
	public static float getClientHeading(int i){
		
		return headingFactor*i*FastMath.DEG_TO_RAD;
	}
	
	/**
	 * Scale a value to server coordinates, be sure to switch z and y before writing to server
	 * @param f	a float client coordinate
	 * @return an int representing 
	 */
	public static int getServerCoord(float f){
		return (int)(invertCoord*f);
	}
	
	/**
	 * Scale a value to client coordinates, be sure to switch z and y 
	 * @param i an int in server cordinates
	 * @return afloat in client coordinates
	 */
	public static float getClientCoord(int i){
		return coordFactor*i;
	}
	
	public static String getServerString(float x, float y, float z){
		return ""+
		ServerValues.getServerCoord(x) +","+
		ServerValues.getServerCoord(z) +","+
		ServerValues.getServerCoord(y);
	}
	
}
