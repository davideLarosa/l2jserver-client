package com.l2client.component;

import com.jme3.math.Vector3f;

/**
 * Simple position component, just the target and current positions and heading nothing fancy
 *
 */
public class SimplePositionComponent implements Component {
	
	public Vector3f startPos = new Vector3f();
	public Vector3f currentPos = new Vector3f();
	public Vector3f goalPos = new Vector3f();
	public float walkSpeed = 0f;
	public float runSpeed = 0f;
	public boolean running = false;
	//current heading in radians
	public float heading = 0;
	// in radians
	public float targetHeading = heading;
}
