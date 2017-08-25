/*******************************************************************************
 * Welcome to the pedestrian simulation framework MomenTUM. 
 * This file belongs to the MomenTUM version 2.0.2.
 * 
 * This software was developed under the lead of Dr. Peter M. Kielar at the
 * Chair of Computational Modeling and Simulation at the Technical University Munich.
 * 
 * All rights reserved. Copyright (C) 2017.
 * 
 * Contact: peter.kielar@tum.de, https://www.cms.bgu.tum.de/en/
 * 
 * Permission is hereby granted, free of charge, to use and/or copy this software
 * for non-commercial research and education purposes if the authors of this
 * software and their research papers are properly cited.
 * For citation information visit:
 * https://www.cms.bgu.tum.de/en/31-forschung/projekte/456-momentum
 * 
 * However, further rights are not granted.
 * If you need another license or specific rights, contact us!
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/

package tum.cms.sim.momentum.utility.geometry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.dyn4j.geometry.AABB;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Triangle;
import org.dyn4j.geometry.Vector2;

/**
 * All class and method descriptions have been adopted from the dyn4j library documentation 
 * http://docs.dyn4j.org/v3.1.10/.
 * 
 * Represents a convex polygon.
 * 
 * A Polygon2D must have at least 3 vertices where one of which is not colinear with the other two simultaneously. 
 * A Polygon2D must also be convex and have anti-clockwise winding of points.
 * 
 * A polygon cannot have coincident vertices. If the first and the last node is identical the last will be removed
 * during creation of a polygon.
 * 
 * @author berndtornede, pk
 *
 */
	
public class Polygon2D extends Geometry2D {
	private Polygon polygon = null;

	private ArrayList<Vector2D> vector2DList = null;
	
	private Segment2D polygonAsSegment = null;
	
	private ArrayList<Vector2D> normalVectorList = null;
	
	/**
	 * Default constructor for sub classes.
	 */
	protected Polygon2D() {
		this.polygon = new Polygon();
	}
	
	/**
	 * Full constructor.
	 * 
	 * @param nodeArray
	 */
	Polygon2D(Vector2D[] nodeArray) {	
		
		Vector2[] nodes = new Vector2[nodeArray.length];
		
		for (int i = 0; i < nodeArray.length; i++) {
			
			nodes[i] = nodeArray[i].getVector();
		}
		
		if(nodes[0].equals(nodes[nodeArray.length - 1])) {
			
			Vector2[] temp = nodes;
			nodes = new Vector2[nodeArray.length - 1];
			
			for (int i = 0; i < nodeArray.length - 1; i++) {
				
				nodes[i] = temp[i];
			}
		}
		
		this.polygon = new Polygon(nodes);
	}
	
	Polygon2D(Vector2D[] nodeArray, boolean inverse) {	
		
		Vector2[] nodes = new Vector2[nodeArray.length];
		
		for (int i = nodeArray.length - 1; -1 < i; i--) {
			
			nodes[i] = nodeArray[i].getVector();
		}
		
		if(nodes[0].equals(nodes[nodeArray.length - 1])) {
			
			Vector2[] temp = nodes;
			nodes = new Vector2[nodeArray.length - 1];
			
			for (int i = 0; i < nodeArray.length - 1; i++) {
				
				nodes[i] = temp[i];
			}
		}
		
		this.polygon = new Polygon(nodes);
	}
	
	public Polygon2D(ArrayList<Vector2D> nodeArray, boolean inverse) {
		
		Vector2[] nodes = new Vector2[nodeArray.size()];
		
		for (int i = nodeArray.size() - 1; -1 < i; i--) {
			
			nodes[i] = nodeArray.get(i).getVector();
		}
		
		if(nodes[0].equals(nodes[nodeArray.size() - 1])) {
			
			Vector2[] temp = nodes;
			nodes = new Vector2[nodeArray.size() - 1];
			
			for (int i = 0; i < nodeArray.size() - 1; i++) {
				
				nodes[i] = temp[i];
			}
		}
		
		this.polygon = new Polygon(nodes);
	}
	
	Polygon2D(List<Vector2D> nodeArray) {	
		
		Vector2[] nodes = new Vector2[nodeArray.size()];
		
		for (int i = 0; i < nodeArray.size(); i++) {
			
			nodes[i] = nodeArray.get(i).getVector();
		}
		
		if(nodes[0].equals(nodes[nodeArray.size() - 1])) {
			
			Vector2[] temp = nodes;
			nodes = new Vector2[nodeArray.size() - 1];
			
			for (int i = 0; i < nodeArray.size() - 1; i++) {
				
				nodes[i] = temp[i];
			}
		}
		
		this.polygon = new Polygon(nodes);
	}

	private List<Polygon2D> triangulation = null;
	
	public List<Polygon2D> triangulate() {
		
		if(triangulation == null) {
			
			List<Polygon2D> createdPolygons = new ArrayList<Polygon2D>();
			
			List<Triangle> triangulatedPolygon = SweepLine2D.triangulate(this.getVertices());
			
			for (int i = 0; i < triangulatedPolygon.size(); i++) {
				
				Vector2[] currentTriangleVertices = triangulatedPolygon.get(i).getVertices();
				List<Vector2D> triangleVertices2D = new ArrayList<Vector2D>();
				
				for (int m = 0; m < currentTriangleVertices.length; m++) {
					triangleVertices2D.add(GeometryFactory.createVector(currentTriangleVertices[m].x, currentTriangleVertices[m].y));
				}
				
				createdPolygons.add(GeometryFactory.createPolygon(triangleVertices2D));
			}
			
			this.triangulation = createdPolygons;
		}
		
		return triangulation;
	}
	

	/**
	 * Returns the array of vertices this Polygon2D contains in local coordinates.
	 * 
	 * @return Vector2D[]
	 */
	@Override
	public synchronized List<Vector2D> getVertices() {
		
		if(vector2DList == null) {
			
			Vector2[] vector2List = this.polygon.getVertices();
			
			vector2DList = new ArrayList<Vector2D>();
			
			for (int i = 0; i < vector2List.length; i++) {
				
				vector2DList.add(new Vector2D(vector2List[i].x, vector2List[i].y));
			}
		}
		
		return vector2DList;
	}
	
	public int getLength() {
		
		return this.polygon.getVertices().length;
	}
	@Override
	public double getRadius(Vector2D vector) {
		return this.polygon.getRadius(vector.getVector());
	}
	
	@Override
	public double getRadius() {
		return this.polygon.getRadius();
	}
	
	@Override
	public Vector2D getCenter() {
		return new Vector2D(this.polygon.getCenter().x, this.polygon.getCenter().y);
	}

	@Override
	public boolean contains(Vector2D point) {
		
		// BUG in this.polygon.contains(point.getVector());

		double offsetX = 0.0;
		double offsetY = 0.0;
		
		if(point.getXComponent() < 0) {
			
			offsetX = -1.0 * point.getXComponent() + 1.0;
		}
	
		if(point.getYComponent() < 0) {
			
			offsetY = -1.0 * point.getYComponent() + 1.0;
		}
	
		boolean inside = false;
		Vector2D[] vertices = null;

		vertices = new Vector2D[this.getVertices().size() + 1];
		
		for(int iter = 0; iter < this.getVertices().size(); iter++) {
			
			if(this.getVertices().get(iter).getXComponent() < 0) {
				
				offsetX += -1.0 * this.getVertices().get(iter).getXComponent() + 1.0;
			}
		
			if(this.getVertices().get(iter).getYComponent() < 0) {
				
				offsetY += -1.0 * this.getVertices().get(iter).getYComponent() + 1.0;
			}
		}
		
		for(int iter = 0; iter < this.getVertices().size(); iter++) {
			
			vertices[iter] = GeometryFactory.createVector(this.getVertices().get(iter).getXComponent() + offsetX,
					this.getVertices().get(iter).getYComponent() + offsetY);
		}
		
		point = GeometryFactory.createVector(point.getXComponent() + offsetX, point.getYComponent() + offsetY);
		
		vertices[this.getVertices().size()] = GeometryFactory.createVector(this.getVertices().get(0).getXComponent() + offsetX,
				this.getVertices().get(0).getYComponent() + offsetY);
				
		for (int i = 0, j = vertices.length - 1; i < vertices.length; j = i++) {
			
			boolean logicAlpha = (vertices[i].getYComponent() >= point.getYComponent()) !=
					(vertices[j].getYComponent() >= point.getYComponent());
			
			if(logicAlpha) {
				
				boolean logicBeta = point.getXComponent() <= 
						(vertices[j].getXComponent() - vertices[i].getXComponent()) * 
						(point.getYComponent() - vertices[i].getYComponent()) /
							(vertices[j].getYComponent() - vertices[i].getYComponent())
							+ vertices[i].getXComponent();
				
			    if (logicBeta) {
			    	  
			    	  inside = !inside;
			      }
			}
		}
		
		return inside;
	}

	@Override
	public boolean contains(Vector2D point, Transform transform) {
		return this.polygon.contains(point.getVector(), transform);
	}

	
	@Override
	public Mass createMass(double density) {
		return this.polygon.createMass(density);
	}
	
	/**
	* Creates an AABB Axis Aligned BoundingBox from this Shape.
	*
	* @return AABB
	*/
	@Override
	AABB createAxisAlignedBoundingBox() {
		
		return this.polygon.createAABB();
	}
	
	/**
	 * Returns the point on this Polygon2D closest to the given point.
	 * The returned vector2d is a point which could be anywhere on the Polygon, created
	 * by the projection to on all subsegments and selecting the projection which is
	 * the closest to the point. Vector is directed from Point to Segment.
	 * @param point
	 * @return Vector2D
	 */
	public Vector2D getPointOnPolygonClosestToVector(Vector2D point) {
	
		return this.polygonAsSegments().getPointOnSegmentClosestToVector(point);
	}
	
	@Override
	public Vector2D getPointClosestToVector(Vector2D toVector) {
		
		return this.getPointOnPolygonClosestToVector(toVector);
	}
	
//	@Override
//	public Vector2D[] getAxes(Vector2D[] foci, Transform transform) {
//		
//		ArrayList<Vector2> foci2 = new ArrayList<Vector2>();
//		
//		for (int i = 0; i < foci.length; i++) {
//			
//			foci2.add(foci[i].getVector());
//		}
//		
//		Vector2[] vector2List = this.polygon.getAxes((Vector2[])foci2.toArray(), transform);
//		
//		Vector2D[] vector2DList = new Vector2D[vector2List.length];
//			
//		for (int i = 0; i < vector2List.length; i++) {
//			
//			vector2DList[i] = new Vector2D(vector2List[i].x, vector2List[i].y);
//		}
//		
//		return vector2DList;
//	}
	
//	@Override
//	public Edge getFarthestFeature(Vector2D point, Transform transform) {
//		return this.polygon.getFarthestFeature(point.getVector(), transform);
//	}
//
//	@Override
//	public Vector2D getFarthestPoint(Vector2D point, Transform transform) {
//		
//		Vector2 farthest = this.polygon.getFarthestPoint(point.getVector(), transform);
//		
//		return new Vector2D(farthest.x, farthest.y);
//	}
	
//	@Override
//	public Vector2D[] getFoci(Transform transform) {
//		
//		Vector2[] vector2List = this.polygon.getFoci(transform);
//		Vector2D[] vector2DList = new Vector2D[vector2List.length];
//		
//		for (int i = 0; i < vector2List.length; i++) {
//			
//			vector2DList[i] = new Vector2D(vector2List[i].x, vector2List[i].y);
//		}
//		
//		return vector2DList;
//	}
	
	public Pair<Double,Double> projectMinMax(Vector2D originVector, Vector2D normalizedAxis) {
		
		double min = normalizedAxis.dot(this.getVertices().get(0).subtract(originVector));
		double max = min;
		
		for (int i = 1; i < this.getVertices().size(); i++) {
			
			  // NOTE: the axis must be normalized to get accurate projections
			  double p = normalizedAxis.dot(this.getVertices().get(i).subtract(originVector));
			  
			  if (p < min) {
				  min = p;
			  } 
			  else if (p > max) {
				  max = p;
			  }
		}
		
		if(min > max) {
			
			return new ImmutablePair<Double,Double>(max, min);
		}
		
		return new ImmutablePair<Double,Double>(min, max);
	}
		
//	@Override
//	public Interval project(Vector2D vector, Transform transform) {
//		return this.polygon.project(vector.getVector(), transform);
//	}	

	@Override
	public void rotate(double theta, double x, double y) {
		this.polygon.rotate(theta, x, y);
	}
	
	@Override
	public void rotate(double theta) {
		this.polygon.rotate(theta);
	}

	@Override
	public void rotate(double theta, Vector2D point) {
		this.polygon.rotate(theta, point.getVector());
	}
		
	@Override
	public void translate(double x, double y) {
		this.polygon.translate(x, y);
	}
	
	@Override
	public void translate(Vector2D vector) {
		this.polygon.translate(vector.getVector());
	}
	
	@Override
	public double distanceBetween(Vector2D point) {
		
		return this.polygonAsSegments().distanceBetween(point);
	}
		
	@Override
	public double minimalDistanceBetween(List<Vector2D> points) {
	
		double minimalDistance = Double.MAX_VALUE;
		
		for(Vector2D point : points) {
			
			minimalDistance = FastMath.min(this.distanceBetween(point), minimalDistance);
		}
		
		return minimalDistance;
	}
	
	public Vector2D vectorBetween(Vector2D point) {
		
		return this.polygonAsSegments().vectorBetween(point);
	}

	/**
	 * Separates this polygon into Segment2Ds (each segment represents an individual edge of the Polygon2D).
	 * 
	 * @param polygon
	 * @return Collection<Segment2D>
	 */
	public synchronized Segment2D polygonAsSegments() {
		
		if(polygonAsSegment == null) {

			ArrayList<Vector2D> segmentCorners = new ArrayList<Vector2D>(this.getVertices());
			segmentCorners.add(segmentCorners.get(0));
			this.polygonAsSegment = GeometryFactory.createSegment(segmentCorners);
		}
		
		return this.polygonAsSegment;
	}
	
	@Override
	public List<Segment2D> getSegments() {
		
		if(polygonAsSegment == null) {
		
			this.polygonAsSegments();
		}
		
		return this.polygonAsSegment.getLineSegments();
	}
	
	public boolean isOrthogonal() {
		
		Segment2D polySegments = this.polygonAsSegments();
		boolean orthogonal = false;
		
		Vector2D segmentVector1 = polySegments.getLineSegments().get(polySegments.getLineSegments().size() - 1).getFirstPoint()
				.difference(polySegments.getLineSegments().get(polySegments.getLineSegments().size() - 1).getLastPoint());
		
		Vector2D segmentVector2 = polySegments.getLineSegments().get(0).getFirstPoint()
				.difference(polySegments.getLineSegments().get(0).getLastPoint());
		
		if (segmentVector1.dot(segmentVector2) == 0) {
			orthogonal = true;
		}

		for (int i = 0; i < polySegments.getLineSegments().size() - 1; i++) {
			
			segmentVector1 = polySegments.getLineSegments().get(i).getFirstPoint()
					.difference(polySegments.getLineSegments().get(i).getLastPoint());
			
			segmentVector2 = polySegments.getLineSegments().get(i + 1).getFirstPoint()
					.difference(polySegments.getLineSegments().get(i + 1).getLastPoint());
			
			if (segmentVector1.dot(segmentVector2) == 0) {
				orthogonal = true;
			}
		}

		return orthogonal;
	}

	@Override
	public ArrayList<Vector2D> getIntersection(Segment2D segment) {
	
		return this.polygonAsSegments().getIntersection(segment);
	}

	@Override
	public ArrayList<Vector2D> getIntersection(Polygon2D polygon) {
	
		return this.polygonAsSegments().getIntersection(polygon.polygonAsSegments());
	}

	@Override
	public ArrayList<Vector2D> getIntersection(Cycle2D cylce) {
		
		return cylce.getIntersection(this);
	}

    
	/**
	 * Checks if a vector is on the polygon, this will return false if if the vector is a polygon corner point.
	 * @param polygon
	 * @param vector
	 * @return
	 */
	@Override
	public boolean isOnLines(Vector2D vector, double precision) {
		
		if(!this.isOnCorners(vector, precision)) {
			
			if(this.polygonAsSegments().getPointOnSegmentClosestToVector(vector).getMagnitude() < precision) {
				
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Normals for each line of the polygon
	 * @return
	 */
	public List<Vector2D> getNormals() {
		
		if(normalVectorList == null) {
				
			normalVectorList = new ArrayList<Vector2D>();
			Vector2[] normals = this.polygon.getNormals();
			
			for (int i = 0; i < normals.length; i++) {
				
				normalVectorList.add(new Vector2D(normals[i]));
			}
		}
		
		return normalVectorList;
	}

	/**
	 * Checks if a vector is a segment corner point
	 * @param polygon
	 * @param vector
	 * @return
	 */
	@Override
	public boolean isOnCorners(Vector2D vector, double precision) {
		
		for(Vector2D onPolyPoint : this.getVertices()) {
			
			if(vector.distance(onPolyPoint) < precision) {
				
				return true;
			}
		}

		return false;	
	}
	
//	public Polygon2D shrinkOrthogonalPolygon(double shrinkDistance, double precision) {
//		
//		if (this.isOrthogonal() == true) {
//			
//			List<Segment2D> polygonEdges = this.polygonAsSegments().getLineSegments();
//			ArrayList<Vector2D> results = new ArrayList<Vector2D>();
//			
//			for (int i = 0; i < polygonEdges.size() - 1; i++) {
//				
//				Vector2D segmentsDirectionVector = polygonEdges.get(i).getLastPoint().difference(polygonEdges.get(i).getFirstPoint());
//				double nextSegmentsDirection = polygonEdges.get(i + 1).getLastPoint().difference(polygonEdges.get(i + 1)
//												.getFirstPoint()).getDirection();
//				Vector2D chosenNormal = null;
//		
//				if (FastMath.abs(polygonEdges.get(i).getNormals().get(0).getDirection() - nextSegmentsDirection) < precision) {
//					chosenNormal = polygonEdges.get(i).getNormals().get(0);
//				}
//				else {
//					chosenNormal = polygonEdges.get(i).getNormals().get(1);
//				}
//				
//				results.add(polygonEdges.get(i).getFirstPoint()
//					.sum(chosenNormal.scale(shrinkDistance))
//					.sum(segmentsDirectionVector.scale(shrinkDistance)));
//
//				if(i == polygonEdges.size() - 2) {
//					
//					results.add(polygonEdges.get(i).getLastPoint()
//						.sum(chosenNormal.scale(shrinkDistance))
//						.sum(segmentsDirectionVector.scale(shrinkDistance).negate()));
//					
//				}
//			}
//			
//			Polygon2D shrinkPolygon = null;
//					
//			try {
//				
//				shrinkPolygon = GeometryFactory.createPolygon(results);
//				
//			} catch (Exception e) { }
//			
//			return shrinkPolygon;
//		}
//		else {
//			return null;
//		}
//	}

	public Double getSmallestVertixValueOfX() {

		return (Double) this.getVertices().stream()
				.mapToDouble(vertex -> vertex.getXComponent())
				.min()
				.getAsDouble();
	}
	
	public Double getSmallestVertixValueOfY() {

		return (Double) this.getVertices().stream()
				.mapToDouble(vertex -> vertex.getYComponent())
				.min()
				.getAsDouble();
	}
	
	public Double getLargestVertixValueOfX() {

		return (Double) this.getVertices().stream()
				.mapToDouble(vertex -> vertex.getXComponent())
				.max()
				.getAsDouble();
	}
	
	public Double getLargestVertixValueOfY() {

		return (Double) this.getVertices().stream()
				.mapToDouble(vertex -> vertex.getYComponent())
				.max()
				.getAsDouble();
	}
	
	public Polygon2D scale(double scale) {
		
		Polygon scaled = Geometry.scale(this.polygon, scale);
		
		Vector2[] vector2List = scaled.getVertices();
		
		ArrayList<Vector2D> vector2DList = new ArrayList<Vector2D>();
		
		for (int i = 0; i < vector2List.length; i++) {
			
			vector2DList.add(new Vector2D(vector2List[i].x, vector2List[i].y));
		}
		
		return GeometryFactory.createPolygon(vector2DList);
	}

	@Override
	public double area() {
		
		return this.polygonAsSegments().area();
	}
	
	@Override
	public String toString() {
		
		 String stringOfPolygon = this.getVertices().stream()
				 .map(vec -> vec.toString())
				 .collect(Collectors.joining());

		return stringOfPolygon;
	}

	
//	@Override
//	public Vector2D[] intersections(Segment2D intersectingLine) {
//		
//		return (Vector2D[])polygonAsSegments().stream()
//				.map(edge -> edge.getSegmentIntersection(intersectingLine))
//				.collect(Collectors.toList())
//				.toArray();
//	}

	// Not used atm
	//	@Override
	//	public AABB createAABB(Transform transform) {
	//		if(this.polygon != null) {
	//			return this.polygon.createAABB(transform);
	//		}
	//		else {
	//			return this.segment.createAABB(transform);
	//		}
	//	}
	//
	//	@Override
	//	public AABB createAABB() {
	//		if(this.polygon != null) {
	//			return this.polygon.createAABB();
	//		}
	//		else {
	//			return this.segment.createAABB();
	//		}
	//	}
}
