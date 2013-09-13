package com.l2client.navigation;

import java.util.ArrayList;

import com.jme3.math.Vector3f;
import com.l2client.navigation.Line2D.LINE_CLASSIFICATION;

/**
 * NavigationPath is a collection of waypoints that define a movement path for
 * an Actor. This object is ownded by an Actor and filled by
 * NavigationMesh::BuildNavigationPath().
 * 
 * Portions Copyright (C) Greg Snook, 2000
 * 
 * @author TR
 * 
 */
public class Path {
	public class WAYPOINT {
		public Vector3f Position; // 3D position of waypoint
		public Cell Cell; // The cell which owns the waypoint
		public NavMesh mesh; // the mesh the cell belongs to
		boolean borderCell;

		@Override
		public String toString() {
			return "WAYPOINT: pos:" + Position.x + "/" + Position.z + " cell:"
					+ Cell;
		}
	};

//	Mesh m_Parent;
//	WAYPOINT m_StartPoint = new WAYPOINT();
//	WAYPOINT m_EndPoint = new WAYPOINT();
//	Iterator<WAYPOINT> m_EndID;
	public ArrayList<WAYPOINT> m_WaypointList = new ArrayList<WAYPOINT>();
	public ArrayList<WAYPOINT> m_OptimalWaypointList = null;

	// : Setup
	// ----------------------------------------------------------------------------------------
	//
	// Sets up a new path from StartPoint to EndPoint. It adds the StartPoint as
	// the first
	// waypoint in the list and waits for further calls to AddWayPoint and
	// EndPath to
	// complete the list
	//
	// -------------------------------------------------------------------------------------://
	void Setup(NavMesh Parent, Vector3f StartPoint, Cell StartCell){//,
//	}
//			Vector3f EndPoint, Cell EndCell) {
		m_WaypointList.clear();

//		m_Parent = Parent;
//		m_StartPoint.Position = StartPoint;
//		m_StartPoint.Cell = StartCell;
//		m_EndPoint.Position = EndPoint;
//		m_EndPoint.Cell = EndCell;
		WAYPOINT start = new WAYPOINT();
		start.Position = StartPoint;
		start.Cell = StartCell;
		start.mesh = Parent;

		// setup the waypoint list with our start and end points
		m_WaypointList.add(start);;
	}

	// : AddWayPoint
	// ----------------------------------------------------------------------------------------
	//
	// Adds a new waypoint to the end of the list
	//
	// -------------------------------------------------------------------------------------://
	void AddWayPoint(Vector3f Point, Cell Cell, NavMesh mesh) {
		WAYPOINT NewPoint = new WAYPOINT();

		NewPoint.Position = Point;
		NewPoint.Cell = Cell;
		NewPoint.mesh = mesh;

		m_WaypointList.add(NewPoint);
	}

//	// : EndPath
//	// ----------------------------------------------------------------------------------------
//	//
//	// Caps the end of the waypoint list by adding our final destination point
//	//
//	// -------------------------------------------------------------------------------------://
//	void EndPath() {
//		// cap the waypoint path with the last endpoint
//		if(m_EndPoint != null)
//			m_WaypointList.add(m_EndPoint);
//	}

//	Mesh Parent() {
//		return (m_Parent);
//	}

	public WAYPOINT StartPoint() {
		return (m_WaypointList.get(0));
	}

	public WAYPOINT EndPoint() {
		if(m_WaypointList.size() > 0)
			return (m_WaypointList.get(m_WaypointList.size()-1));
		return null;
	}

	public ArrayList<WAYPOINT> WaypointList() {
		return (m_WaypointList);
	}

	// : GetFurthestVisibleWayPoint
	// ----------------------------------------------------------------------------------------
	//
	// Find the furthest visible waypoint from the VantagePoint provided. This
	// is used to
	// smooth out irregular paths.
	//
	// -------------------------------------------------------------------------------------://
	public WAYPOINT GetFurthestVisibleWayPoint(WAYPOINT VantagePoint) {
		// see if we are already talking about the last waypoint
		if (VantagePoint == m_WaypointList.get(m_WaypointList.size() - 1)) {
			return (VantagePoint);
		}
		WAYPOINT Vantage = VantagePoint;
		int i = m_WaypointList.indexOf(VantagePoint);
		if(i < 0)
			return VantagePoint;
//		System.out.print("WAY IND:" + i);

		WAYPOINT TestWaypoint = VantagePoint;
		TestWaypoint = m_WaypointList.get(++i);

		if (TestWaypoint == m_WaypointList.get(m_WaypointList.size() - 1)) {
//			System.out.println(" WAY IND was last");
			return (TestWaypoint);
		}

		WAYPOINT VisibleWaypoint = TestWaypoint;
		// TestWaypoint = m_WaypointList.get(++i);

		while (TestWaypoint != m_WaypointList.get(m_WaypointList.size() - 1)) {
			WAYPOINT Test = TestWaypoint;
			if (!Vantage.mesh.LineOfSightTest(Vantage.Cell, Vantage.Position,
					Test.Position)) {
//				System.out.println(" WAY IND was:" + i);
				return (VisibleWaypoint);
			}
			VisibleWaypoint = TestWaypoint;
			TestWaypoint = m_WaypointList.get(++i);
		}
//		System.out.println(" WAY IND was:" + i);
		// the last
		return (TestWaypoint);
	}
	
	void optimize(){
		int size = m_WaypointList.size();
		
		int current = 0;
		int next = 1;
		ArrayList<WAYPOINT> newList = new ArrayList<WAYPOINT>();
		//add start to optimized one
		newList.add(m_WaypointList.get(0));
		//nothing to do ononly two points
		if(size <= 2) {
			newList.add(m_WaypointList.get(1));
			m_OptimalWaypointList = newList;
			return;	
		}
		
		size--;
		//try to optimize as long as we have not reached the last point
		while(next < size) {
			if(isEndVisible(current, next)){
				//go on to test the next point
				next++;
			} else {
				//if the last is not the start add the last one as the next waypoint as it was the last one visible
				if(next -1 != current) {
					newList.add(m_WaypointList.get(next - 1));
					//now go on with new current and next
					current = next -1;
					//next is already on the right spot
				} else {
					newList.add(m_WaypointList.get(next));
					current = next;//BOOOM something went wrong??!?!
					next = next+1;
				}
				
			}
		}
		//do we have the last one on it?
		if(m_WaypointList.get(size) != newList.get(newList.size()-1))
			newList.add(m_WaypointList.get(size));
		
		m_OptimalWaypointList = newList;			
	}
	
	private boolean isEndVisible(int from, int to){
		//is start at the end or even further?
		if(from>=to)
			return false;
		//out of bounds?
		if(from <0 || to > m_WaypointList.size())
			return false;
		
		WAYPOINT wFrom = m_WaypointList.get(from);
		WAYPOINT wTo = m_WaypointList.get(to);
		
		//early out if both are on the same cell (line will end on border or not even getting to the border at all)
		if(wFrom.Cell == wTo.Cell)
			return true;
		
		Line2D wayLine = new Line2D(wFrom.Position.x, wFrom.Position.z,wTo.Position.x, wTo.Position.z);
		
		WAYPOINT check = null;
		for(int i = from+1;i<=to;i++){
			check = m_WaypointList.get(i);
			int wall = check.Cell.ArrivalWall();
			Line2D wallLine = check.Cell.m_Side[wall];
			LINE_CLASSIFICATION result = wallLine.Intersection(wayLine, null);
			if(Line2D.LINE_CLASSIFICATION.SEGMENTS_INTERSECT != result && LINE_CLASSIFICATION.B_BISECTS_A != result){
//System.out.println("Optimization between "+from+" and "+ i+" result:"+result);
				return false;
			}
		}
		
		return true;
	}
	
}