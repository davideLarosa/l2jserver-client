package com.l2client.component;

import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * A Component for internal Vars, Perception, Mind, etc.
 */
public class EnvironmentComponent implements Component {
	
	public Spatial currentTarget = null;
	public Spatial lastTarget = null;
	public double targetSeenTime;
	public boolean targetEnemy = false;
	public boolean hidden = false;
	public int lastWounded = 0;
	public int currentWounded = 0;
	public double woundedTime;
	public Vector3f woundedFrom = null;
	public int teamHealthPercent = -1;
	/**
	 * flag if something changed at all, should be reset by the environment system
	 */
	public boolean changed = false;
	/**
	 * -1 not moving, 0 walk, 1 run
	 */
	public int movement = -1; 
	
}
