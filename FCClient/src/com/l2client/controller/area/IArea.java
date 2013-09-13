package com.l2client.controller.area;

public interface IArea {
	/**
	 * The size of one terrain tile in x-z direction (y would be height) currently 256
	 */
	public static int TERRAIN_SIZE = 256;
	
	/**
	 * Half the size of one terrain tile in x-z direction (y would be height) currently 128
	 */
	public static int TERRAIN_SIZE_HALF = TERRAIN_SIZE/2;
	
	/**
	 * "tile_" this is used to find nav/ground tiles in the simple example
	 * @see GotoClickedInputAction.onAnalog()
	 */
	static final String TILE_PREFIX = "tile_";

}
