package com.l2client.model.l2j;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;


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
	 * Scale a value to server coordinates
	 * @param f	a float client coordinate
	 * @return an int representing 
	 */
	public static int getServerCoordX(float f){
//		return  (-2048 + (int)(invertCoord*f));
		return  ((int)(invertCoord*f));
	}
	/**
	 * Pass in jme z coordinate (not height)
	 * @param f
	 * @return
	 */
	public static int getServerCoordY(float f){
//		return (2048 + (int)(invertCoord*f));
		return ((int)(invertCoord*f));
	}
	/**
	 * Pass in jme y (height) coordinate
	 * @param f
	 * @return
	 */
	public static int getServerCoordZ(float f){
//		return (-64 +(int)(invertCoord*f));
		return ((int)(invertCoord*f));
	}
	
	/**
	 * Scale a value to client coordinates 
	 * @param i an int in server cordinates
	 * @return afloat in client coordinates
	 */
	public static float getClientCoordX(int i){
//		return coordFactor*(i+2048);//we will be always be a bit off the position we might think
		return coordFactor*(i);
	}
	/**
	 * pass in server y value to receive jme z value
	 * @param i
	 * @return
	 */
	public static float getClientCoordZ(int i){
//		return coordFactor*(i-2048);//we will be always be a bit off the position we might think
		return coordFactor*(i);
	}
	/**
	 * Pass in server z value, to receive height in jme
	 * @param i
	 * @return
	 */
	public static float getClientCoordY(int i){
//		return coordFactor*(i+64);
		return coordFactor*(i);
	}
	
	public static float getClientScaled(int i){
		return coordFactor*(i);
	}
	
	/**
	 * 
	 * @param x	jme x coord
	 * @param y	jme y coord (will be l2j z coord)
	 * @param z jme z coord (will be l2j y coord)
	 * @return
	 */
	public static String getServerString(float x, float y, float z){
		return ""+
		ServerValues.getServerCoordX(x) +","+
		ServerValues.getServerCoordY(z) +","+
		ServerValues.getServerCoordZ(y);
	}


	/**
	 * converts server x, y, z to a string representing client x,y,z (internally z and y will be swapped)
	 * @param x	Server x value
	 * @param y	Server y value
	 * @param z Server z value (up)
	 * @return String of client coords separated by , like 123,456,789
	 */
	public static String getClientString(int x, int y, int z) {
		return ""+
				ServerValues.getClientCoordX(x) +","+
				ServerValues.getClientCoordY(z) +","+
				ServerValues.getClientCoordZ(y);
	}
	
	/**
	 * converts server x, y, z to a vector3f representing client x,y,z (internally z and y will be swapped)
	 * @param x	Server x value
	 * @param y	Server y value
	 * @param z Server z value (up)
	 * @return Vector3f of client coords
	 */
	public static Vector3f getClientCoords(int x, int y, int z){
		return new Vector3f(ServerValues.getClientCoordX(x), 
				ServerValues.getClientCoordY(z), 
				ServerValues.getClientCoordZ(y));
	}
	
}
