package com.l2client.component;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;


public class TargetComponent implements Component {

	/**
	 * Constant no target at all: -1
	 */
	public static int NO_TARGET = -1;
	/**
	 * current target selected
	 */
	private int currentTargetID = NO_TARGET;
	/**
	 * last target selected
	 */
	private int lastTargetID = NO_TARGET;
	/**
	 * target position for conveninence
	 */
	public Vector3f pos = Vector3f.ZERO;
	/**
	 * target threat color
	 */
	public ColorRGBA color;
	
	public boolean hasTarget(){
		return currentTargetID != NO_TARGET;
	}

	public void setNoTarget(){
		setTarget(NO_TARGET);
	}
	
	public void setTarget(int id){
		lastTargetID = currentTargetID;
		currentTargetID = id;
	}
	
	public int getCurrentTarget(){
		return currentTargetID;
	}
	
	public int getLastTarget(){
		return lastTargetID;
	}
}
