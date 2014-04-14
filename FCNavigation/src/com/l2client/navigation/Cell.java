package com.l2client.navigation;

import java.io.IOException;
import java.util.Random;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

/**
 * A Cell represents a single triangle within a NavigationMesh. It contains
 * functions for testing a path against the cell, and various ways to resolve
 * collisions with the cell walls. Portions of the A* path finding algorythm are
 * provided within this class as well, but the path finding process is managed
 * by the parent Navigation Mesh.
 * 
 * Portions Copyright (C) Greg Snook, 2000
 * 
 * @author TR
 * 
 */
public class Cell implements Savable{

	private final static int VERT_A = 0;
	private final static int VERT_B = 1;
	private final static int VERT_C = 2;
	public final static int SIDE_AB = 0;
	public final static int SIDE_BC = 1;
	public final static int SIDE_CA = 2;

	public enum PATH_RESULT {
		NO_RELATIONSHIP, // the path does not cross this cell
		ENDING_CELL, // the path ends in this cell
		EXITING_CELL
		// the path exits this cell through side X
	};

	public class ClassifyResult {
		PATH_RESULT result = PATH_RESULT.NO_RELATIONSHIP;
		int side = 0;
		Cell cell = null;
		Vector2f intersection = new Vector2f();

		@Override
		public String toString() {
			return result.toString() + " " + cell;
		}
	};
	
	int id = -1;
	Plane m_CellPlane = new Plane(); // A plane containing the cell triangle
	//TODO think of optimization, do we use the vertices at all? Most Stuff is based on lines, where two tris even share one line, remove after build..
	Vector3f m_Vertex[] = new Vector3f[3]; // pointers to the verticies of this
											// triangle held in the
											// NavigationMesh's vertex pool
	Vector3f m_CenterPoint = new Vector3f(); // The center of the triangle
	Line2D m_Side[] = new Line2D[3]; // a 2D line representing each cell Side
	Cell m_Link[] = new Cell[3];// pointers to cells that attach to this cell. A
								// NULL link denotes a solid edge.

	// Pathfinding Data...

	volatile int m_SessionID; // an identifier for the current pathfinding session.
	volatile float m_ArrivalCost; // total cost to use this cell as part of a path
	volatile float m_Heuristic; // our estimated cost to the goal from here
	volatile boolean m_Open; // are we currently listed as an Open cell to revisit and
					// test?
	volatile int m_ArrivalWall; // the side we arrived through.

	//TODO think about optimizing this out, used scarecly, so just compute on need....
	Vector3f m_WallMidpoint[] = new Vector3f[3]; // the pre-computed midpoint of
													// each wall.
	float m_WallDistance[] = new float[3]; // the distances between each wall
											// midpoint of sides (0-1, 1-2, 2-0)

	public void Initialize(int id, Vector3f PointA, Vector3f PointB, Vector3f PointC) {
		this.id = id;
		m_Vertex[VERT_A] = PointA;
		m_Vertex[VERT_B] = PointB;
		m_Vertex[VERT_C] = PointC;

		// object must be re-linked
		m_Link[SIDE_AB] = null;
		m_Link[SIDE_BC] = null;
		m_Link[SIDE_CA] = null;

		// now that the vertex pointers are set, compute additional data about
		// the Cell
		ComputeCellData();
	}
	
	public int getId(){
		return id;
	}

	private void ComputeCellData() {
		// create 2D versions of our verticies
		Vector2f Point1 = new Vector2f(m_Vertex[VERT_A].x, m_Vertex[VERT_A].z);
		Vector2f Point2 = new Vector2f(m_Vertex[VERT_B].x, m_Vertex[VERT_B].z);
		Vector2f Point3 = new Vector2f(m_Vertex[VERT_C].x, m_Vertex[VERT_C].z);

		// innitialize our sides
		m_Side[SIDE_AB] = new Line2D(Point1, Point2); // line AB
		m_Side[SIDE_BC] = new Line2D(Point2, Point3); // line BC
		m_Side[SIDE_CA] = new Line2D(Point3, Point1); // line CA

		m_CellPlane.setPlanePoints(m_Vertex[VERT_A], m_Vertex[VERT_B],
				m_Vertex[VERT_C]);

		// compute midpoint as centroid of polygon
		m_CenterPoint.x = ((m_Vertex[VERT_A].x + m_Vertex[VERT_B].x + m_Vertex[VERT_C].x) / 3);
		m_CenterPoint.y = ((m_Vertex[VERT_A].y + m_Vertex[VERT_B].y + m_Vertex[VERT_C].y) / 3);
		m_CenterPoint.z = ((m_Vertex[VERT_A].z + m_Vertex[VERT_B].z + m_Vertex[VERT_C].z) / 3);

		// compute the midpoint of each cell wall
		m_WallMidpoint[0] = new Vector3f(
				(m_Vertex[VERT_A].x + m_Vertex[VERT_B].x) / 2.0f,
				(m_Vertex[VERT_A].y + m_Vertex[VERT_B].y) / 2.0f,
				(m_Vertex[VERT_A].z + m_Vertex[VERT_B].z) / 2.0f);
		m_WallMidpoint[1] = new Vector3f(
				(m_Vertex[VERT_C].x + m_Vertex[VERT_B].x) / 2.0f,
				(m_Vertex[VERT_C].y + m_Vertex[VERT_B].y) / 2.0f,
				(m_Vertex[VERT_C].z + m_Vertex[VERT_B].z) / 2.0f);

		m_WallMidpoint[2] = new Vector3f(
				(m_Vertex[VERT_C].x + m_Vertex[VERT_A].x) / 2.0f,
				(m_Vertex[VERT_C].y + m_Vertex[VERT_A].y) / 2.0f,
				(m_Vertex[VERT_C].z + m_Vertex[VERT_A].z) / 2.0f);

		// compute the distances between the wall midpoints
		Vector3f WallVector;
		WallVector = m_WallMidpoint[0].subtract(m_WallMidpoint[1]);
		m_WallDistance[0] = WallVector.length();

		WallVector = m_WallMidpoint[1].subtract(m_WallMidpoint[2]);
		m_WallDistance[1] = WallVector.length();

		WallVector = m_WallMidpoint[2].subtract(m_WallMidpoint[0]);
		m_WallDistance[2] = WallVector.length();

	}

	// : RequestLink
	// ----------------------------------------------------------------------------------------
	//
	// Navigation Mesh is created as a pool of raw cells. The cells are then
	// compared against
	// each other to find common edges and create links. This routine is called
	// from a
	// potentially adjacent cell to test if a link should exist between the two.
	//
	// -------------------------------------------------------------------------------------://
	boolean RequestLink(Vector3f PointA, Vector3f PointB, Cell Caller) {
		// return true if we share the two provided verticies with the calling
		// cell.
		if (m_Vertex[VERT_A].equals(PointA)) {
			if (m_Vertex[VERT_B].equals(PointB)) {
				m_Link[SIDE_AB] = Caller;
				return (true);
			} else if (m_Vertex[VERT_C].equals(PointB)) {
				m_Link[SIDE_CA] = Caller;
				return (true);
			}
		} else if (m_Vertex[VERT_B].equals(PointA)) {
			if (m_Vertex[VERT_A].equals(PointB)) {
				m_Link[SIDE_AB] = Caller;
				return (true);
			} else if (m_Vertex[VERT_C].equals(PointB)) {
				m_Link[SIDE_BC] = Caller;
				return (true);
			}
		} else if (m_Vertex[VERT_C].equals(PointA)) {
			if (m_Vertex[VERT_A].equals(PointB)) {
				m_Link[SIDE_CA] = Caller;
				return (true);
			} else if (m_Vertex[VERT_B].equals(PointB)) {
				m_Link[SIDE_BC] = Caller;
				return (true);
			}
		}

		// we are not adjacent to the calling cell
		return (false);
	}

	// : SetLink
	// ----------------------------------------------------------------------------------------
	//
	// Sets a link to the calling cell on the enumerated edge.
	//
	// -------------------------------------------------------------------------------------://
	void SetLink(int Side, Cell Caller) {
		m_Link[Side] = Caller;
	}

	// : MapVectorHeightToCell
	// ----------------------------------------------------------------------------------------
	//
	// Uses the X and Z information of the vector to calculate Y on the cell
	// plane
	//
	// -------------------------------------------------------------------------------------://
	public void MapVectorHeightToCell(Vector3f MotionPoint) {
		MotionPoint.y = m_CellPlane.SolveForY(MotionPoint.x, MotionPoint.z);
	}

	// = ACCESSORS
	// ============================================================================

	// : IsPointInCellCollumn
	// ----------------------------------------------------------------------------------------
	//
	// Test to see if a 2D point is within the cell. There are probably better
	// ways to do
	// this, but this seems plenty fast for the time being.
	//
	// -------------------------------------------------------------------------------------://
	boolean IsPointInCellCollumn(Vector2f TestPoint) {
		// we are "in" the cell if we are on the right hand side of all edge
		// lines of the cell
		int InteriorCount = 0;

		for (int i = 0; i < 3; i++) {
			Line2D.POINT_CLASSIFICATION SideResult = m_Side[i].ClassifyPoint(
					TestPoint, 1.0e-6f);

			if (SideResult != Line2D.POINT_CLASSIFICATION.LEFT_SIDE) {
				InteriorCount++;
			}
		}
		// if(InteriorCount == 3)
		// System.out.println("Point "+TestPoint+" is in Cell:"+this);
		// else
		// System.out.println("Point "+TestPoint+" is NOT in Cell:"+this);
		return (InteriorCount == 3);
	}

	/**
	 * Test to see if a 3D point is within the cell by projecting it down to 2D and calling the above method.
	 * @param TestPoint	Point to test if it would be within this cell
	 * @return	true if the point would be in that cell, false otherwise
	 */
	public boolean IsPointInCellCollumn(Vector3f TestPoint) {
		return (IsPointInCellCollumn(new Vector2f(TestPoint.x, TestPoint.z)));
	}

	Vector3f Vertex(int Vert) {
		return (m_Vertex[Vert]);
	}

	public Vector3f CenterPoint() {
		return (m_CenterPoint);
	}

	Cell Link(int Side) {
		return (m_Link[Side]);
	}

	float ArrivalCost() {
		return (m_ArrivalCost);
	}

	float Heuristic() {
		return (m_Heuristic);
	}

	public float PathfindingCost() {
		return (m_ArrivalCost + m_Heuristic);
	}

	int ArrivalWall() {
		return (m_ArrivalWall);
	}

	Vector3f WallMidpoint(int Side) {
		return (m_WallMidpoint[Side]);
	}

	/*
	 * ClassifyPathToCell
	 * --------------------------------------------------------
	 * ----------------------------------
	 * 
	 * Classifies a Path in relationship to this cell. A path is represented by
	 * a 2D line where Point A is the start of the path and Point B is the
	 * desired position.
	 * 
	 * If the path exits this cell on a side which is linked to another cell,
	 * that cell index is returned in the NextCell parameter and SideHit
	 * contains the side number of the wall exited through.
	 * 
	 * If the path collides with a side of the cell which has no link (a solid
	 * edge), SideHit contains the side number (0-2) of the colliding wall.
	 * 
	 * In either case PointOfIntersection will contain the point where the path
	 * intersected with the wall of the cell if it is provided by the caller.
	 * 
	 * 
	 * --------------------------------------------------------------------------
	 * ----------------
	 */
	ClassifyResult ClassifyPathToCell(Line2D MotionPath) {
		// System.out.println("Cell:"+m_Vertex[0].toString()+" "+m_Vertex[1].toString()+" "+m_Vertex[2].toString());
		// System.out.println("     Path:"+MotionPath);
		int InteriorCount = 0;
		ClassifyResult result = new ClassifyResult();

		// Check our MotionPath against each of the three cell walls
		for (int i = 0; i < 3; ++i) {
			// Classify the MotionPath endpoints as being either ON_LINE,
			// or to its LEFT_SIDE or RIGHT_SIDE.
			// Since our triangle vertices are in clockwise order,
			// we know that points to the right of each line are inside the
			// cell.
			// Points to the left are outside.
			// We do this test using the ClassifyPoint function of Line2D

			// If the destination endpoint of the MotionPath
			// is Not on the right side of this wall...
			Line2D.POINT_CLASSIFICATION end = m_Side[i].ClassifyPoint(
					MotionPath.EndPointB(), 0.0f);
//			if (end != Line2D.POINT_CLASSIFICATION.RIGHT_SIDE) {
			if (end != Line2D.POINT_CLASSIFICATION.RIGHT_SIDE
					&& end != Line2D.POINT_CLASSIFICATION.ON_LINE) {
				// ..and the starting endpoint of the MotionPath
				// is Not on the left side of this wall...
				if (m_Side[i].ClassifyPoint(MotionPath.EndPointA(), 0.0f) != Line2D.POINT_CLASSIFICATION.LEFT_SIDE) {
					// Check to see if we intersect the wall
					// using the Intersection function of Line2D
					Line2D.LINE_CLASSIFICATION IntersectResult = MotionPath
							.Intersection(m_Side[i], result.intersection);

					if (IntersectResult == Line2D.LINE_CLASSIFICATION.SEGMENTS_INTERSECT || IntersectResult == Line2D.LINE_CLASSIFICATION.A_BISECTS_B) {
						// record the link to the next adjacent cell
						// (or NULL if no attachement exists)
						// and the enumerated ID of the side we hit.
						result.cell = m_Link[i];
						result.side = i;
						result.result = PATH_RESULT.EXITING_CELL;
						// System.out.println("exits this cell");
						return result;

						// pNextCell = m_Link[i];
						// Side = i;
						// return (PATH_RESULT.EXITING_CELL);
					}
				}
			} else {
				// The destination endpoint of the MotionPath is on the right
				// side.
				// Increment our InteriorCount so we'll know how many walls we
				// were
				// to the right of.
				InteriorCount++;
			}
		}

		// An InteriorCount of 3 means the destination endpoint of the
		// MotionPath
		// was on the right side of all walls in the cell.
		// That means it is located within this triangle, and this is our ending
		// cell.
		if (InteriorCount == 3) {
			// System.out.println(" ends within this cell");
			result.result = PATH_RESULT.ENDING_CELL;
			return result;
			// return (PATH_RESULT.ENDING_CELL);
		}
		// System.out.println("No intersection with this cell at all");
		// We only reach here is if the MotionPath does not intersect the cell
		// at all.
		return result;
		// return (PATH_RESULT.NO_RELATIONSHIP);
	}

	/*
	 * ProjectPathOnCellWall
	 * ----------------------------------------------------
	 * --------------------------------------
	 * 
	 * ProjectPathOnCellWall projects a path intersecting the wall with the wall
	 * itself. This can be used to convert a path colliding with a cell wall to
	 * a resulting path moving along the wall. The input parameter MotionPath
	 * MUST contain a starting point (EndPointA) which is the point of
	 * intersection with the path and cell wall number [SideNumber] and an
	 * ending point (EndPointB) which resides outside of the cell.
	 * 
	 * 
	 * --------------------------------------------------------------------------
	 * ----------------
	 */
	void ProjectPathOnCellWall(int SideNumber, Line2D MotionPath) {
		// compute the normalized vector of the cell wall in question
		Vector2f WallNormal = m_Side[SideNumber].EndPointB().subtract(
				m_Side[SideNumber].EndPointA());
		WallNormal = WallNormal.normalize();

		// determine the vector of our current movement
		Vector2f MotionVector = MotionPath.EndPointB().subtract(
				MotionPath.EndPointA());

		// compute dot product of our MotionVector and the normalized cell wall
		// this gives us the magnatude of our motion along the wall

		float DotResult = MotionVector.dot(WallNormal);

		// our projected vector is then the normalized wall vector times our new
		// found magnatude
		MotionVector = WallNormal.mult(DotResult);

		// redirect our motion path along the new reflected direction
		MotionPath.SetEndPointB(MotionPath.EndPointA().add(MotionVector));

		//
		// Make sure starting point of motion path is within the cell
		//
		Vector2f NewPoint = MotionPath.EndPointA();
		ForcePointToCellCollumn(NewPoint);
		MotionPath.SetEndPointA(NewPoint);

		//
		// Make sure destination point does not intersect this wall again
		//
		NewPoint = MotionPath.EndPointB();
		ForcePointToWallInterior(SideNumber, NewPoint);
		MotionPath.SetEndPointB(NewPoint);

	}

	// : ForcePointToWallInterior
	// ----------------------------------------------------------------------------------------
	//
	// Force a 2D point to the interior side of the specified wall.
	//
	// -------------------------------------------------------------------------------------://
	boolean ForcePointToWallInterior(int SideNumber, Vector2f TestPoint) {
		float Distance = m_Side[SideNumber].SignedDistance(TestPoint);
		float Epsilon = 0.001f;

		if (Distance <= Epsilon) {
			if (Distance <= 0.0f) {
				Distance -= Epsilon;
			}

			Distance = Math.abs(Distance);
			Distance = (Epsilon > Distance ? Epsilon : Distance);

			// this point needs adjustment
			Vector2f Normal = m_Side[SideNumber].getNormal();
			Normal = Normal.mult(Distance);
			TestPoint.x += Normal.x;
			TestPoint.y += Normal.y;
			return (true);
		}
		return (false);
	}

	// : ForcePointToWallInterior
	// ----------------------------------------------------------------------------------------
	//
	// Force a 3D point to the interior side of the specified wall.
	//
	// -------------------------------------------------------------------------------------://
	boolean ForcePointToWallInterior(int SideNumber, Vector3f TestPoint) {
		Vector2f TestPoint2D = new Vector2f(TestPoint.x, TestPoint.z);
		boolean PointAltered = ForcePointToWallInterior(SideNumber, TestPoint2D);

		if (PointAltered) {
			TestPoint.x = TestPoint2D.x;
			TestPoint.z = TestPoint2D.y;
		}

		return (PointAltered);
	}

	// : ForcePointToCellCollumn
	// ----------------------------------------------------------------------------------------
	//
	// Force a 2D point to the interior cell by forcing it to the interior of
	// each wall
	//
	// -------------------------------------------------------------------------------------://

	boolean ForcePointToCellCollumn(Vector2f TestPoint) {
		// create a motion path from the center of the cell to our point
		Line2D TestPath = new Line2D(new Vector2f(m_CenterPoint.x,
				m_CenterPoint.z), TestPoint);

		ClassifyResult result = ClassifyPathToCell(TestPath);
		// compare this path to the cell.

		if (result.result == PATH_RESULT.EXITING_CELL) {
			Vector2f PathDirection = new Vector2f(result.intersection.x
					- m_CenterPoint.x, result.intersection.y - m_CenterPoint.z);

			PathDirection = PathDirection.mult(0.9f);

			TestPoint.x = m_CenterPoint.x + PathDirection.x;
			TestPoint.y = m_CenterPoint.z + PathDirection.y;
			return (true);
		} else if (result.result == PATH_RESULT.NO_RELATIONSHIP) {
			TestPoint.x = m_CenterPoint.x;
			TestPoint.y = m_CenterPoint.z;
			return (true);
		}

		return (false);
	}

	// : ForcePointToCellCollumn
	// ----------------------------------------------------------------------------------------
	//
	// Force a 3D point to the interior cell by forcing it to the interior of
	// each wall
	//
	// -------------------------------------------------------------------------------------://
	boolean ForcePointToCellCollumn(Vector3f TestPoint) {
		Vector2f TestPoint2D = new Vector2f(TestPoint.x, TestPoint.z);
		boolean PointAltered = ForcePointToCellCollumn(TestPoint2D);

		if (PointAltered) {
			TestPoint.x = TestPoint2D.x;
			TestPoint.z = TestPoint2D.y;
		}
		return (PointAltered);
	}

	// : ProcessCell
	// ----------------------------------------------------------------------------------------
	//
	// Process this cells neighbors using A*
	//
	// -------------------------------------------------------------------------------------://
	boolean ProcessCell(Heap pHeap) {
		if (m_SessionID == pHeap.SessionID()) {
			// once we have been processed, we are closed
			m_Open = false;

			// querry all our neigbors to see if they need to be added to the
			// Open heap
			for (int i = 0; i < 3; ++i) {
				if (m_Link[i] != null) {
					// abs(i-m_ArrivalWall) is a formula to determine which
					// distance measurement to use.
					// The Distance measurements between the wall midpoints of
					// this cell
					// are held in the order ABtoBC, BCtoCA and CAtoAB.
					// We add this distance to our known m_ArrivalCost to
					// compute
					// the total cost to reach the next adjacent cell.
					m_Link[i].QueryForPath(pHeap, this, m_ArrivalCost
//							+ this.m_CenterPoint.distance(m_Link[i].m_CenterPoint));
							+ this.m_WallMidpoint[m_ArrivalWall].distance(pHeap.Goal()));//produces straighter paths on regular
//							+ m_WallDistance[Math.abs(i - m_ArrivalWall)]);//produces more cornered paths on regular
				}
			}
			return (true);
		}
		return (false);
	}

	// : QueryForPath
	// ----------------------------------------------------------------------------------------
	//
	// Process this cell using the A* heuristic
	//
	// -------------------------------------------------------------------------------------://
	boolean QueryForPath(Heap pHeap, Cell Caller, float arrivalcost) {
		if (m_SessionID != pHeap.SessionID()) {
			// this is a new session, reset our internal data
			m_SessionID = pHeap.SessionID();

			if (Caller != null) {
				m_Open = true;
				ComputeHeuristic(pHeap.Goal());
				setArrivalCost(arrivalcost);

				// remember the side this caller is entering from
				if (Caller.equals(m_Link[0])) {
					m_ArrivalWall = 0;
				} else if (Caller.equals(m_Link[1])) {
					m_ArrivalWall = 1;
				} else if (Caller.equals(m_Link[2])) {
					m_ArrivalWall = 2;
				}
			} else {
				// we are the cell that contains the starting location
				// of the A* search.
				m_Open = false;
				m_ArrivalCost = 0;
				m_Heuristic = 0;
				m_ArrivalWall = 0;
			}
			// add this cell to the Open heap
			pHeap.AddCell(this);
			return (true);
		} else if (m_Open) {
			// m_Open means we are already in the Open Heap.
			// If this new caller provides a better path, adjust our data
			// Then tell the Heap to resort our position in the list.
			if ((arrivalcost + m_Heuristic) < (m_ArrivalCost + m_Heuristic)) {
				setArrivalCost(arrivalcost);

				// remember the side this caller is entering from
				if (Caller.equals(m_Link[0])) {
					m_ArrivalWall = 0;
				} else if (Caller.equals(m_Link[1])) {
					m_ArrivalWall = 1;
				} else if (Caller.equals(m_Link[2])) {
					m_ArrivalWall = 2;
				}
				// ask the heap to resort our position in the priority heap
				pHeap.AdjustCell(this);
				return (true);
			}
		}
		// this cell is closed
		return (false);
	}

	// : ComputeHeuristic
	// ----------------------------------------------------------------------------------------
	//
	// Compute the A* Heuristic for this cell given a Goal point
	//
	// -------------------------------------------------------------------------------------://
	void ComputeHeuristic(Vector3f Goal) {
		// our heuristic is the estimated distance (using the longest axis
		// delta) between our
		// cell center and the goal location

//		float XDelta = Math.abs(Goal.x - m_CenterPoint.x);
//		float YDelta = Math.abs(Goal.y - m_CenterPoint.y);
//		float ZDelta = Math.abs(Goal.z - m_CenterPoint.z);
//
//		m_Heuristic = Math.max(Math.max(XDelta, YDelta), ZDelta);
		m_Heuristic = Goal.distance(m_CenterPoint);
//		m_Heuristic = 0;
	}

	@Override
	public String toString() {
		return "Cell: " + m_CenterPoint.x + "," + m_CenterPoint.z+" id:"+id;
	}
	
	public Vector3f getNormal(){
		return this.m_CellPlane.getNormal();
	}

	public Vector3f getRandomPoint() {
		Random rand = new Random();
		Vector2f ret =
		this.m_Side[0].EndPointA().add(this.m_Side[0].GetDirection().mult(rand.nextFloat()).add(
				this.m_Side[1].GetDirection().mult(rand.nextFloat())));
		ForcePointToCellCollumn(ret);
		Vector3f vec = new Vector3f(ret.x, 0, ret.y);
		MapVectorHeightToCell(vec);
		return vec;
	}

    public Class<? extends Cell> getClassTag() {
        return this.getClass();
    }
    
	public void write(JmeExporter e) throws IOException {
		OutputCapsule capsule = e.getCapsule(this);
		capsule.write(id, "id", -1);
		capsule.write(m_CellPlane, "cellplane", null);
		capsule.write(m_Vertex, "vertex", null);
		capsule.write(m_CenterPoint, "center", null);
		capsule.write(m_Side, "sides", null);
//links are handled one level above in mesh
//		capsule.write(m_Link, "links", null);
		capsule.write(m_WallMidpoint, "midpoints", null);
		capsule.write(m_WallDistance, "distances", null);	
	}

	public void read(JmeImporter e) throws IOException {
		InputCapsule capsule = e.getCapsule(this);
		id = capsule.readInt("id", -1);
		m_CellPlane = (Plane) capsule.readSavable("cellplane", new Plane());
		Savable[] sav = capsule.readSavableArray("vertex", new Vector3f[3]);
        for (int i = 0; i < sav.length; i++){
            m_Vertex[i] = (Vector3f) sav[i];
        }
		m_CenterPoint = (Vector3f) capsule.readSavable("center", new Vector3f());
		sav = capsule.readSavableArray("sides", new Line2D[3]);
        for (int i = 0; i < sav.length; i++){
            m_Side[i] = (Line2D) sav[i];
        }
		//done one level above in mesh
//		m_Link = (Cell[]) capsule.readSavableArray("links", new Cell[3]);
		sav = capsule.readSavableArray("midpoints", new Vector3f[3]);
        for (int i = 0; i < sav.length; i++){
            m_WallMidpoint[i] = (Vector3f) sav[i];
        }
		m_WallDistance = capsule.readFloatArray("distances", new float[3]);	
	}

	//TODO RequestLink already sets the link
	public void checkAndLink(Cell cellB) {
		if (Link(Cell.SIDE_AB) == null
				&& cellB.RequestLink(Vertex(0), Vertex(1), this)) {
			SetLink(Cell.SIDE_AB, cellB);
		} else if (Link(Cell.SIDE_BC) == null
				&& cellB.RequestLink(Vertex(1), Vertex(2), this)) {
			SetLink(Cell.SIDE_BC, cellB);
		} else if (Link(Cell.SIDE_CA) == null
				&& cellB.RequestLink(Vertex(2), Vertex(0), this)) {
			SetLink(Cell.SIDE_CA, cellB);
		}
	}
	
	public void unLink(Cell c){
		if(c==m_Link[0]){
			m_Link[0] = null;
			c.unLink(this);
		} else if(c==m_Link[1]){
			m_Link[1] = null;
			c.unLink(this);
		} else if(c==m_Link[2]){
			m_Link[2] = null;
			c.unLink(this);
		}
	}
	
	private void setArrivalCost(float cost){
		m_ArrivalCost = cost;
//		m_ArrivalCost = 0.1f;
	}
}
