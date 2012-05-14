package com.l2client.navigation;

import java.io.IOException;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.math.Vector2f;

/**
 * 
 * Line2D represents a line in 2D space. Line data is held as a line segment having two 
 * endpoints and as a fictional 3D plane extending verticaly. The Plane is then used for
 * spanning and point clasification tests. A Normal vector is used internally to represent
 * the fictional plane.
 * 
 * Portions Copyright (C) Greg Snook, 2000
 * @author TR
 *
 */
public class Line2D implements Savable{

		enum POINT_CLASSIFICATION
		{
			ON_LINE,		// The point is on, or very near, the line
			LEFT_SIDE,		// looking from endpoint A to B, the test point is on the left
			RIGHT_SIDE		// looking from endpoint A to B, the test point is on the right
		};

		enum LINE_CLASSIFICATION
		{
			COLLINEAR,			// both lines are parallel and overlap each other
			LINES_INTERSECT,	// lines intersect, but their segments do not
			SEGMENTS_INTERSECT,	// both line segments bisect each other
			A_BISECTS_B,		// line segment B is crossed by line A
			B_BISECTS_A,		// line segment A is crossed by line B
			PARALELL			// the lines are paralell
		};

		private Vector2f m_PointA;	// Endpoint A of our line segment
		private Vector2f m_PointB;	// Endpoint B of our line segment

		volatile Vector2f m_Normal;	// 'normal' of the ray. 
									// a vector pointing to the right-hand side of the line
									// when viewed from PointA towards PointB
		volatile boolean m_NormalCalculated = false; // normals are only calculated on demand

	public Line2D(){}

	public Line2D( Vector2f PointA, Vector2f PointB)
	{
		m_PointA = PointA;
		m_PointB = PointB;
		m_NormalCalculated = false;
	}
	
	public Line2D(float x1, float y1, float x2, float y2){
		m_PointA = new Vector2f(x1,y1);
		m_PointB = new Vector2f(x2,y2);
		m_NormalCalculated = false;
	}

	public void SetEndPointA( Vector2f Point)
	{
		m_PointA = Point;
		m_NormalCalculated = false;
	}

	public void SetEndPointB(Vector2f Point)
	{
		m_PointB = Point;
		m_NormalCalculated = false;
	}

	Vector2f getNormal()
	{
		if (!m_NormalCalculated)
		{
			ComputeNormal();
		}

		return (m_Normal);
	}

	void SetPoints(Vector2f PointA, Vector2f PointB)
	{
		m_PointA = PointA;
		m_PointB = PointB;
		m_NormalCalculated = false;
	}


	void SetPoints(float PointAx, float PointAy, float PointBx, float PointBy)
	{
		m_PointA.x=PointAx;
		m_PointA.y=PointAy;
		m_PointB.x=PointBx;
		m_PointB.y=PointBy;
		m_NormalCalculated = false;
	}

	/**
	 * 
		Determines the signed distance from a point to this line. Consider the line as
		if you were standing on PointA of the line looking towards PointB. Posative distances
		are to the right of the line, negative distances are to the left.
	*/

	public float SignedDistance(Vector2f Point)
	{
		if (!m_NormalCalculated)
		{
			ComputeNormal();
		}

		Vector2f TestVector = new Vector2f(Point.subtract(m_PointA));
		
		return TestVector.dot(m_Normal); //.x*m_Normal.x + TestVector.y*m_Normal.y;//DotProduct(TestVector,m_Normal);

	}

	/**
	 * 
		Determines where a point lies in relation to this line. Consider the line as
		if you were standing on PointA of the line looking towards PointB. The incomming
		point is then classified as being on the Left, Right or Centered on the line.
	*/

	public POINT_CLASSIFICATION ClassifyPoint(Vector2f Point, float Epsilon) 
	{
	    POINT_CLASSIFICATION      Result = POINT_CLASSIFICATION.ON_LINE;
	    float          Distance = SignedDistance(Point);
	    
	    if (Distance > Epsilon)
	    {
	        Result = POINT_CLASSIFICATION.RIGHT_SIDE;
	    }
	    else if (Distance < -Epsilon)
	    {
	        Result = POINT_CLASSIFICATION.LEFT_SIDE;
	    }

	    return(Result);
	}
	
	/**
	 * this line A = x0, y0 and B = x1, y1
	 * other is A = x2, y2 and B = x3, y3
	 * @param other
	 * @param pIntersectPoint
	 * @return
	 */
	public LINE_CLASSIFICATION Intersection( Line2D other, Vector2f pIntersectPoint)
	{
		float denom = (other.m_PointB.y-other.m_PointA.y)*(this.m_PointB.x-this.m_PointA.x)
						-
					  (other.m_PointB.x-other.m_PointA.x)*(this.m_PointB.y-this.m_PointA.y);
		float u0 = (other.m_PointB.x-other.m_PointA.x)*(this.m_PointA.y-other.m_PointA.y)
					-
					(other.m_PointB.y-other.m_PointA.y)*(this.m_PointA.x-other.m_PointA.x);
		float u1 = (other.m_PointA.x-this.m_PointA.x)*(this.m_PointB.y-this.m_PointA.y)
					-
				   (other.m_PointA.y-this.m_PointA.y)*(this.m_PointB.x-this.m_PointA.x);
		
		//if parallel
		if(denom == 0.0f){
			//if collinear
			if(u0 == 0.0f && u1 == 0.0f)
				return LINE_CLASSIFICATION.COLLINEAR;
			else 
				return LINE_CLASSIFICATION.PARALELL;
		} else {
			//check if they intersect
			u0 = u0/denom;
			u1 = u1/denom;
			
			float x = this.m_PointA.x + u0*(this.m_PointB.x - this.m_PointA.x);
			float y = this.m_PointA.y + u0*(this.m_PointB.y - this.m_PointA.y);
			
			if (pIntersectPoint != null)
			{
				pIntersectPoint.x = x; //(m_PointA.x + (FactorAB * Bx_minus_Ax));
				pIntersectPoint.y = y; //(m_PointA.y + (FactorAB * By_minus_Ay));
			}
			
			// now determine the type of intersection
			if ((u0 >= 0.0f) && (u0 <= 1.0f) && (u1 >= 0.0f) && (u1 <= 1.0f))
			{
				return LINE_CLASSIFICATION.SEGMENTS_INTERSECT;
			}
			else if ((u1 >= 0.0f) && (u1 <= 1.0f))
			{
				return (LINE_CLASSIFICATION.A_BISECTS_B);
			}
			else if ((u0 >= 0.0f) && (u0 <= 1.0f))
			{
				return (LINE_CLASSIFICATION.B_BISECTS_A);
			}

			return LINE_CLASSIFICATION.LINES_INTERSECT;
			
		}
	}

	/**
	 * Determines if two segments intersect, and if so the point of intersection. The current
		member line is considered line AB and the incomming parameter is considered line CD for
		the purpose of the utilized equations.

		A = PointA of the member line
		B = PointB of the member line
		C = PointA of the provided line	
		D = PointB of the provided line	
	*/

	public LINE_CLASSIFICATION Intersection_OLD( Line2D Line, Vector2f pIntersectPoint)
	{
		float Ay_minus_Cy = m_PointA.y - Line.EndPointA().y;	
		float Dx_minus_Cx = Line.EndPointB().x - Line.EndPointA().x;	
		float Ax_minus_Cx = m_PointA.x - Line.EndPointA().x;	
		float Dy_minus_Cy = Line.EndPointB().y - Line.EndPointA().y;	
		float Bx_minus_Ax = m_PointB.x - m_PointA.x;	
		float By_minus_Ay = m_PointB.y - m_PointA.y;	
		

		java.awt.geom.Line2D l1 = new java.awt.geom.Line2D.Float(this.m_PointA.x, this.m_PointA.y, this.m_PointB.x, this.m_PointB.y);
		if(l1.intersectsLine(Line.EndPointA().x, Line.EndPointA().y, Line.EndPointB().x, Line.EndPointB().y))
//return LINE_CLASSIFICATION.LINES_INTERSECT;
			System.out.println("They intersect");
		else
//return LINE_CLASSIFICATION.COLLINEAR;
			System.out.println("They DO NOT intersect");			

		float Numerator = (Ay_minus_Cy * Dx_minus_Cx) - (Ax_minus_Cx * Dy_minus_Cy);
		float Denominator = (Bx_minus_Ax * Dy_minus_Cy) - (By_minus_Ay * Dx_minus_Cx);

		// if lines do not intersect, return now
		if (Denominator != 0.0f)
		{
			if (Numerator != 0.0f)
			{
System.out.println("App says They DO NOT intersect");
				return LINE_CLASSIFICATION.COLLINEAR;
			}
System.out.println("App says They DO NOT intersect");
			return LINE_CLASSIFICATION.PARALELL;
		}

		float FactorAB = Numerator / Denominator;
		float FactorCD = ((Ay_minus_Cy * Bx_minus_Ax) - (Ax_minus_Cx * By_minus_Ay)) / Denominator;

		// posting (hitting a vertex exactly) is not allowed, shift the results
		// if they are within a minute range of the end vertecies
//		if (fabs(FactorCD) < 1.0e-6f)
//		{
//			FactorCD = 1.0e-6f;
//		}
//		if (fabs(FactorCD - 1.0f) < 1.0e-6f)
//		{
//			FactorCD = 1.0f - 1.0e-6f;
//		}


		// if an interection point was provided, fill it in now
		if (pIntersectPoint != null)
		{
			pIntersectPoint.x = (m_PointA.x + (FactorAB * Bx_minus_Ax));
			pIntersectPoint.y = (m_PointA.y + (FactorAB * By_minus_Ay));
		}

System.out.println("App says They DO intersect");
		// now determine the type of intersection
		if ((FactorAB >= 0.0f) && (FactorAB <= 1.0f) && (FactorCD >= 0.0f) && (FactorCD <= 1.0f))
		{
			return LINE_CLASSIFICATION.SEGMENTS_INTERSECT;
		}
		else if ((FactorCD >= 0.0f) && (FactorCD <= 1.0f))
		{
			return (LINE_CLASSIFICATION.A_BISECTS_B);
		}
		else if ((FactorAB >= 0.0f) && (FactorAB <= 1.0f))
		{
			return (LINE_CLASSIFICATION.B_BISECTS_A);
		}

		return LINE_CLASSIFICATION.LINES_INTERSECT;

	}

	Vector2f EndPointA()
	{
		return (m_PointA);
	}


	Vector2f EndPointB()
	{
		return (m_PointB);
	}


	public float Length()
	{
		float xdist = m_PointB.x-m_PointA.x;
		float ydist = m_PointB.y-m_PointA.y;

		xdist *= xdist;
		ydist *= ydist;

		
		return (float) Math.sqrt(xdist + ydist);
	}

	 public Vector2f GetDirection()
	{
		 Vector2f Direction = (m_PointB.subtract(m_PointA));
		return Direction.normalize();
//		return Direction;
	}

	void ComputeNormal()
	{
		//
		// Get Normailized direction from A to B
		//
		m_Normal = GetDirection();

		//
		// Rotate by -90 degrees to get normal of line
		//
		float OldYValue = m_Normal.y;
		m_Normal.y = -m_Normal.x;
		m_Normal.x = OldYValue;
		m_NormalCalculated = true;
		
	}
	
	public static void selfTest() {
		Line2D a = new Line2D(new Vector2f(-2,0), new Vector2f(2,0));
		Line2D b = new Line2D(new Vector2f(-2,1), new Vector2f(2,-1));
		Line2D.LINE_CLASSIFICATION res = a.Intersection(b, null);
		if(res == LINE_CLASSIFICATION.COLLINEAR || res == LINE_CLASSIFICATION.PARALELL)
			System.out.println("Failed intersection verrification");

		if(a.ClassifyPoint(new Vector2f(0,1), 0.0f) != POINT_CLASSIFICATION.LEFT_SIDE)
			System.out.println("Failed left test");

		if(a.ClassifyPoint(new Vector2f(0,-1), 0.0f) != POINT_CLASSIFICATION.RIGHT_SIDE)
			System.out.println("Failed right test");
		
		if(a.ClassifyPoint(new Vector2f(0,0), 0.0f) != POINT_CLASSIFICATION.ON_LINE)
			System.out.println("Failed on line test");
	}
	
	public String toString(){
		return "Line:"+m_PointA.x+"/"+m_PointA.y+" -> "+m_PointB.x+"/"+m_PointB.y;
	}
	
    public Class<? extends Line2D> getClassTag() {
        return this.getClass();
    }

	public void write(JmeExporter e) throws IOException {
		OutputCapsule capsule = e.getCapsule(this);
		capsule.write(m_PointA, "m_PointA", null);
		capsule.write(m_PointB, "m_PointB", null);
		capsule.write(m_Normal, "m_Normal", null);		
	}

	public void read(JmeImporter e) throws IOException {
		InputCapsule capsule = e.getCapsule(this);
		m_PointA = (Vector2f) capsule.readSavable("m_PointA", new Vector2f());
		m_PointB = (Vector2f) capsule.readSavable("m_PointB", new Vector2f());
		m_Normal = (Vector2f) capsule.readSavable("m_Normal", new Vector2f());
	}
}
