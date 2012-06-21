package com.l2client.component;

import com.jme3.math.Vector3f;
import com.l2client.controller.entity.ISpatialPointing;
import com.l2client.navigation.Cell;
import com.l2client.navigation.EntityNavigationManager;
import com.l2client.navigation.NavigationMesh;
import com.l2client.navigation.Path;
import com.l2client.navigation.Path.WAYPOINT;

/**
 * Full blown positioning component based on nav mesh and waypoints
 *
 */
public class PositioningComponent implements Component, ISpatialPointing{
	//max acceleration per second
	public float maxAcc;
	//current acceleration
	public float acc;
	//height offset
	public float heightOffset;
	//max decceleration per second
	public float maxDcc;
	//max speed per second
	public float maxSpeed;
	//current speed per seccond
	public float speed;
	//current heading in radians
	public float heading;
	//the size (as a radius around center) in x/z
	public float size;
	//the current position in world coordinates
	public Vector3f position = new Vector3f();
	//the position in the last frame
	public Vector3f lastPosition = new Vector3f();
	//current direction (corresponding to heading) vector
	public Vector3f direction = new Vector3f();
	//the current path followed
	public Path path;
	//the next waypoint
	public Path.WAYPOINT nextWayPoint;
	//current NavMesh
	public NavigationMesh mesh;
	//current Cell
	public Cell cell;
	
	public void initByWayPoint(Path p){
		WAYPOINT start = p.StartPoint();
		path = p;
		mesh = (NavigationMesh) start.mesh;
		cell = start.Cell;
		position = start.Position;
		if(p.m_WaypointList.size()>1){
			if(EntityNavigationManager.USE_OPTIMZED_PATH)
				nextWayPoint = p.m_OptimalWaypointList.get(1);
			else
				nextWayPoint = p.m_WaypointList.get(1);
		}
		
	}

	@Override
	public int getSize() {
		return (int) size;
	}

	@Override
	public int getX() {
		return (int) position.x;
	}

	@Override
	public int getZ() {
		return (int) position.z;
	}

	@Override
	public int getLastX() {
		return (int) lastPosition.x;
	}

	@Override
	public int getLastZ() {
		return (int) lastPosition.z;
	}

	@Override
	public void updateLast() {
		lastPosition.set(position);
	}
}
