package com.l2client.controller.area;

import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

public interface ITileManager {

	/**
	 * creates dummy textures for quad based terrain standins
	 * creates a skydome
	 */
	public abstract void initialize();

	/**
	 * update and check for updates, with center of interest being at worldposition
	 * @param worldPosition position wich is the center of current tile loading in jme world coordinates
	 */
	public abstract void update(Vector3f worldPosition);

	/**
	 * Adds a sky node to the char
	 */
	public abstract void addSkyDome();

	public abstract void removeSkyDome();

	/**
	 * chance for the tile managers to pre-load a tile which soon will be a new center
	 * @param worldPosition	a position on the tile in world coordinates
	 */
	public abstract void prepareTeleport(Vector3f worldPosition);

	/**
	 * adds a skydome to the char node
	 * @param cam			camera used for sky rendering
	 * @param timeOffset	timeoffset for starting time
	 */
	public abstract void addSkyDome(Camera cam, int timeOffset);

}