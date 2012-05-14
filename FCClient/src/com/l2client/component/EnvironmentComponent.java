package com.l2client.component;

import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * Could also be called Internal Vars, Perception, Mind, etc.
 *
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
	public boolean changed = false;
	public int movement = -1; //-1 no, 0 walk, 1 run
	
}
