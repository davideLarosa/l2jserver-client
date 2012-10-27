package com.l2client.navigation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.l2client.app.Singleton;
import com.l2client.navigation.Cell.ClassifyResult;

/**
 * A NavigationMesh is a collection of NavigationCells used to control object
 * movement while also providing path finding line-of-sight testing. It serves
 * as a parent to all the Actor objects which exist upon it.
 * 
 * Portions Copyright (C) Greg Snook, 2000
 * 
 * @author TR
 * 
 */
public class Mesh implements Savable{

	ArrayList<Cell> m_CellArray = new ArrayList<Cell>(); // the cells that make
															// up this mesh

	// path finding data...
	volatile int m_PathSession = 0;
	volatile Heap m_NavHeap = new Heap();

	public class MotionResult {
		Cell cell = null;
		Vector3f pos = null;
	};

//	public void Clear() {
//		m_CellArray.clear();
//	}


	/**
	 * Add a new cell, defined by the three vertices in clockwise order, to this
	 * mesh
	 */
	public Cell AddCell(Vector3f PointA, Vector3f PointB, Vector3f PointC) {
		Cell NewCell = new Cell();
		NewCell.Initialize(PointA, PointB, PointC);
		m_CellArray.add(NewCell);
		return NewCell;
	}

//	// : Update
//	// ----------------------------------------------------------------------------------------
//	//
//	// Does noting at this point. Stubbed for future use in animating the mesh
//	//
//	// -------------------------------------------------------------------------------------://
//	void Update(float elapsedTime) {
//	}

	public int TotalCells() {
		return m_CellArray.size();
	}

	public Cell Cell(int index) {
		return (m_CellArray.get(index));
	}

	// : SnapPointToCell
	// ----------------------------------------------------------------------------------------
	//
	// Force a point to be inside the cell
	//
	// -------------------------------------------------------------------------------------://
	public Vector3f SnapPointToCell(Cell Cell, Vector3f Point) {
		Vector3f PointOut = Point;

		if (!Cell.IsPointInCellCollumn(PointOut)) {
			Cell.ForcePointToCellCollumn(PointOut);
		}

		Cell.MapVectorHeightToCell(PointOut);
		return (PointOut);
	}

//	// : SnapPointToMesh
//	// ----------------------------------------------------------------------------------------
//	//
//	// Force a point to be inside the nearest cell on the mesh
//	//
//	// -------------------------------------------------------------------------------------://
//	Vector3f SnapPointToMesh(Cell CellOut, Vector3f Point) {
//		Vector3f PointOut = Point;
//
//		CellOut = FindClosestCell(PointOut);
//
//		return (SnapPointToCell(CellOut, PointOut));
//	}

	/**
	 * Find the closest cell on the mesh to the given point
	 */
	public Cell FindClosestCell(Vector3f Point) {
		float ClosestDistance = 3.4E+38f;
		float ClosestHeight = 3.4E+38f;
		boolean FoundHomeCell = false;
		float ThisDistance;
		Cell ClosestCell = null;

		for (Cell pCell : m_CellArray) {
			if (pCell.IsPointInCellCollumn(Point)) {
				Vector3f NewPosition = new Vector3f(Point);
				pCell.MapVectorHeightToCell(NewPosition);

				ThisDistance = Math.abs(NewPosition.y - Point.y);

				if (FoundHomeCell) {
					if (ThisDistance < ClosestHeight) {
						ClosestCell = pCell;
						ClosestHeight = ThisDistance;
					}
				} else {
					ClosestCell = pCell;
					ClosestHeight = ThisDistance;
					FoundHomeCell = true;
				}
			}
			
			if (!FoundHomeCell) {
				Vector2f Start = new Vector2f(pCell.CenterPoint().x, pCell
						.CenterPoint().z);
				Vector2f End = new Vector2f(Point.x, Point.z);
				Line2D MotionPath = new Line2D(Start, End);

				ClassifyResult Result = pCell.ClassifyPathToCell(MotionPath);

				if (Result.result == Cell.PATH_RESULT.EXITING_CELL) {
					Vector3f ClosestPoint3D = new Vector3f(
							Result.intersection.x, 0.0f, Result.intersection.y);
					pCell.MapVectorHeightToCell(ClosestPoint3D);

					ClosestPoint3D = ClosestPoint3D.subtract(Point);

					ThisDistance = ClosestPoint3D.length();

					if (ThisDistance < ClosestDistance) {
						ClosestDistance = ThisDistance;
						ClosestCell = pCell;
					}
				}
			}
		}

		return (ClosestCell);	
	}

	// : BuildNavigationPath
	// ----------------------------------------------------------------------------------------
	//
	// Build a navigation path using the provided points and the A* method
	//
	// -------------------------------------------------------------------------------------://
	public boolean BuildNavigationPath(Path NavPath, Cell StartCell,
			Vector3f StartPos, Cell EndCell, Vector3f EndPos) {
		boolean FoundPath = false;
//System.out.println("-- looking for path from"+StartPos+" to "+EndPos);
		// Increment our path finding session ID
		// This Identifies each pathfinding session
		// so we do not need to clear out old data
		// in the cells from previous sessions.
		++m_PathSession;

		// load our data into the NavigationHeap object
		// to prepare it for use.
		m_NavHeap.Setup(m_PathSession, StartPos);

		// We are doing a reverse search, from EndCell to StartCell.
		// Push our EndCell onto the Heap at the first cell to be processed
		EndCell.QueryForPath(m_NavHeap, null, 0.0f);
		// process the heap until empty, or a path is found
		while (m_NavHeap.NotEmpty() && !FoundPath) {

			// pop the top cell (the open cell with the lowest cost) off the
			// Heap
			Node ThisNode = m_NavHeap.GetTop();

			// if this cell is our StartCell, we are done
			if (ThisNode.cell.equals(StartCell)) {
				FoundPath = true;
			} else {
				// Process the Cell, Adding it's neighbors to the Heap as needed
				ThisNode.cell.ProcessCell(m_NavHeap);
//System.out.println("-- processed Cell:"+ThisNode+" at:"+ThisNode.cell.m_CenterPoint+" heuristic:"+ThisNode.cell.m_Heuristic+" cost:"+ThisNode.cost+" arrivalCost:"+ThisNode.cell.m_ArrivalCost+" heapSize:"+m_NavHeap.size());
//for(Node n : m_NavHeap.getNodes())
//	System.out.println("----+ heap cost:"+n.cost+" cell:"+n.cell);
			}
		}

		// if we found a path, build a waypoint list
		// out of the cells on the path
		if (FoundPath) {
			Cell TestCell = StartCell;
			Vector3f NewWayPoint;

			// Setup the Path object, clearing out any old data
			NavPath.Setup(this, StartPos, StartCell);

			// Step through each cell linked by our A* algorythm
			// from StartCell to EndCell
			while (TestCell != null && TestCell != EndCell) {
				// add the link point of the cell as a way point (the exit
				// wall's center)
				int LinkWall = TestCell.ArrivalWall();

				NewWayPoint = TestCell.WallMidpoint(LinkWall);
				NewWayPoint = SnapPointToCell(TestCell, NewWayPoint); // just to be sure

				NavPath.AddWayPoint(NewWayPoint, TestCell, this);
//				NavPath.AddWayPoint(TestCell.m_CenterPoint, TestCell, this);

				// and on to the next cell
				TestCell = TestCell.Link(LinkWall);
			}

			// cap the end of the path.
			NavPath.AddWayPoint(EndPos, EndCell, this);
			return (true);
		}
		return (false);
	}

	// : ResolveMotionOnMesh
	// ----------------------------------------------------------------------------------------
	//
	// Resolve a movement vector on the mesh
	//
	// -------------------------------------------------------------------------------------://
	public Cell ResolveMotionOnMesh(Vector3f StartPos, Cell StartCell,	Vector3f EndPos) {
		// create a 2D motion path from our Start and End positions, tossing out
		// their Y values to project them
		// down to the XZ plane.
		Line2D MotionPath = new Line2D(new Vector2f(StartPos.x, StartPos.z),
				new Vector2f(EndPos.x, EndPos.z));

		// these three will hold the results of our tests against the cell walls
		ClassifyResult Result = null;

		// TestCell is the cell we are currently examining.
		Cell TestCell = StartCell;

//		do {
//			i++;
			// use NavigationCell to determine how our path and cell interact
//			 if(TestCell.IsPointInCellCollumn(MotionPath.EndPointA()))
//			 System.out.println("Start is in cell:"+TestCell);
//			 else
//			 System.out.println("Start is NOT in cell:"+TestCell);
//			 if(TestCell.IsPointInCellCollumn(MotionPath.EndPointB()))
//			 System.out.println("End is in cell:"+TestCell);
//			 else
//			 System.out.println("End is NOT in cell:"+TestCell);
			Result = TestCell.ClassifyPathToCell(MotionPath);

			// if exiting the cell...
			if (Result.result == Cell.PATH_RESULT.EXITING_CELL) {
				// Set if we are moving to an adjacent cell or we have hit a
				// solid (unlinked) edge
				if (Result.cell != null) {
					// moving on. Set our motion origin to the point of
					// intersection with this cell
					// and continue, using the new cell as our test cell.
					MotionPath.SetEndPointA(Result.intersection);
					TestCell = Result.cell;
				} else {
					//FIXME this could also be the case of switching meshes :-< check this !!
					Cell c = Singleton.get().getNavManager().FindClosestCell(EndPos, true);
					if(c!= null && c != TestCell){
//System.out.println("Mesh switching");
							TestCell =c;
					}
					else{
						//FIXME thid should push the entity more away from a wall than it does at the moment and make it move more perpendicular to the wall
//System.out.println("Hitting a wall!");
						// we have hit a solid wall. Resolve the collision and
						// correct our path.
						MotionPath.SetEndPointA(Result.intersection);
						TestCell.ProjectPathOnCellWall(Result.side, MotionPath);
	
						// add some friction to the new MotionPath since we are
						// scraping against a wall.
						// we do this by reducing the magnatude of our motion by 10%
						Vector2f Direction = MotionPath.EndPointB().subtract(
								MotionPath.EndPointA()).mult(0.9f);
						// Direction.mult(0.9f);
						MotionPath.SetEndPointB(MotionPath.EndPointA().add(
								Direction));
					}
				}
			} else if (Result.result == Cell.PATH_RESULT.NO_RELATIONSHIP) {
//System.out.println("NO RELATION");
				//FIXME this could also be the case of optimized meshes
				Cell c =Singleton.get().getNavManager().FindClosestCell(EndPos, true);
				if(c!= null && c != TestCell){
						TestCell =c;
				} else {
				// Although theoretically we should never encounter this case,
				// we do sometimes find ourselves standing directly on a vertex
				// of the cell.
				// This can be viewed by some routines as being outside the
				// cell.
				// To accomodate this rare case, we can force our starting point
				// to be within
				// the current cell by nudging it back so we may continue.
				Vector2f NewOrigin = MotionPath.EndPointA();
				TestCell.ForcePointToCellCollumn(NewOrigin);
//				MotionPath.SetEndPointA(NewOrigin);
				//we do not want to iterate we just want them to stop at the wall and not cet out
				MotionPath.SetEndPointB(NewOrigin);
				}
			}
//		}//
		// Keep testing until we find our ending cell or stop moving due to
		// friction
		//
//		while ((Result.result != Cell.PATH_RESULT.ENDING_CELL)
//				&& (Math.abs(MotionPath.EndPointA().x -MotionPath.EndPointB().x)>0.01f 
//						&& Math.abs(MotionPath.EndPointA().y -MotionPath.EndPointB().y)>0.01f) && i < 50);
//////				&& (MotionPath.EndPointA().x != MotionPath.EndPointB().x && MotionPath
//////						.EndPointA().y != MotionPath.EndPointB().y) && i < 5000);
////		//		
//		 if(i >= 50)
//		 System.out.println("Loop detected in ResolveMotionOnMesh");
		// // we now have our new host cell
		// EndCell = TestCell;

		// Update the new control point position,
		// solving for Y using the Plane member of the NavigationCell
		EndPos.x = MotionPath.EndPointB().x;
		EndPos.y = 0.0f;
		EndPos.z = MotionPath.EndPointB().y;
		TestCell.MapVectorHeightToCell(EndPos);

		// return EndCell;
		return TestCell;
	}

	// : LineOfSightTest
	// ----------------------------------------------------------------------------------------
	//
	// Test to see if two points on the mesh can view each other
	//
	// -------------------------------------------------------------------------------------://
	// FIXME EndCell is the last visible cell?
	boolean LineOfSightTest(Cell StartCell, Vector3f StartPos, Vector3f EndPos) {
		Line2D MotionPath = new Line2D(new Vector2f(StartPos.x, StartPos.z),
				new Vector2f(EndPos.x, EndPos.z));

		Cell testCell = StartCell;
		Cell.ClassifyResult result = testCell.ClassifyPathToCell(MotionPath);
		;
		while (result.result == Cell.PATH_RESULT.EXITING_CELL) {
			//FIXME the test is not correct, as it should check if two POINTS ON THE MESH can view each other, not if the two points are in the same cell!!!
			if (result.cell == null)// hit a wall, so the point is not visible
				return false;
			result = result.cell.ClassifyPathToCell(MotionPath);

		}

		return (result.result == Cell.PATH_RESULT.ENDING_CELL);
	}
	
//	public boolean LineOfSightTestExtern(Cell StartCell, Vector3f StartPos, Vector3f EndPos) {
//		if(StartCell != null)
//			return LineOfSightTest(StartCell, StartPos, EndPos);
//		else
//			return false;
//	}

	// : LinkCells
	// ----------------------------------------------------------------------------------------
	//
	// Link all the cells that are in our pool
	//
	// -------------------------------------------------------------------------------------://
	public void LinkCells() {
		for (Cell pCellA : m_CellArray) {
			for (Cell pCellB : m_CellArray) {
				if (pCellA != pCellB) {
					pCellA.checkAndLink(pCellB);
				}
			}
		}
	}
//
//	// FIXME load from file
//	public void loadFromFile(String filename) {
//		m_CellArray.clear();
//		DataInputStream in;
//		File f = new File(filename);
//		if (f.isFile()) {
//			try {
//				Vector3f a = new Vector3f();
//				Vector3f b = new Vector3f();
//				Vector3f c = new Vector3f();
//				in = new DataInputStream(new FileInputStream(filename));
//				while (in.available() > 0) {
//					a.set(in.readFloat(), in.readFloat(), in.readFloat());
//					b.set(in.readFloat(), in.readFloat(), in.readFloat());
//					c.set(in.readFloat(), in.readFloat(), in.readFloat());
//					addFace(a, b, c);
//				}
//
//				in.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				m_CellArray.clear();
//			}
//		}
//		LinkCells();
//	}

//	private void addFace(Vector3f vertA, Vector3f vertB, Vector3f vertC) {
//		// some art programs can create linear polygons which have two or more
//		// identical vertices. This creates a poly with no surface area,
//		// which will wreak havok on our navigation mesh algorythms.
//		// We only except polygons with unique vertices.
//		if ((vertA != vertB) && (vertB != vertC) && (vertC != vertA)) {
//			AddCell(vertA, vertB, vertC);
//		}
//	}

//    public Class<? extends Mesh> getClassTag() {
//        return this.getClass();
//    }
    
	public void write(JmeExporter e) throws IOException {
		OutputCapsule capsule = e.getCapsule(this);
		capsule.writeSavableArrayList(m_CellArray, "cellarray", null);	
		HashMap<Cell, Integer> tmp = new HashMap<Cell, Integer>();
		
		int[] links = new int[m_CellArray.size()*3];
		
		for(int i =0; i<m_CellArray.size(); i++)
			tmp.put(m_CellArray.get(i), i);
		
		Cell c = null;
		for(int i =0,j=0; i<m_CellArray.size(); i++){
			c = m_CellArray.get(i); 
			links[j++]= (c.m_Link[0] != null?tmp.get(c.m_Link[0]):-1);
			links[j++]= (c.m_Link[1] != null?tmp.get(c.m_Link[1]):-1);
			links[j++]= (c.m_Link[2] != null?tmp.get(c.m_Link[2]):-1);
		}
		capsule.write(links, "links", null);
		tmp.clear();
	}

	@SuppressWarnings("unchecked")
	public void read(JmeImporter e) throws IOException {
		InputCapsule capsule = e.getCapsule(this);
		m_CellArray = (ArrayList<Cell>) capsule.readSavableArrayList("cellarray", new ArrayList<Cell>());
		int[] links = capsule.readIntArray("links", null);
		if(links != null && links.length == m_CellArray.size()*3){
			Cell c = null;
			int l = -1;
			for(int i =0,j=0; i<m_CellArray.size(); i++){
				c = m_CellArray.get(i); 
				l = links[j++];
				c.m_Link[0] = l>=0?m_CellArray.get(l):null;
				l = links[j++];
				c.m_Link[1] = l>=0?m_CellArray.get(l):null;
				l = links[j++];
				c.m_Link[2] = l>=0?m_CellArray.get(l):null;
			}
		}
	}
}
