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

package tum.cms.sim.momentum.utility.voronoi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import delaunay.Pnt;
import delaunay.Triangle;
import delaunay.Triangulation;
import tum.cms.sim.momentum.utility.geometry.AxisAlignedBoundingBox2D;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Segment2D;
import tum.cms.sim.momentum.utility.geometry.Vector2D;

/**
 * Source code for function List<Segment2D> createVoronoiSegments is from taken and modified
 * from DelaunaySourceCodeJava60.jar by Paul Chew, chew@cs.cornell.edu
 * @author qa
 *
 */
public class VoronoiTheoryFactory {
	
	private VoronoiTheoryFactory() { }
	
	/**
	 * Returns a list of the segments of all Voronoi polygons based on the provided list of
	 * Voronoi points. Based on DelaunaySourceCodeJava60.jar by Paul Chew, chew@cs.cornell.edu
	 * @param voronoiPoints
	 * @param boundingBox 
	 * @return
	 */
	public static List<Segment2D> createVoronoiSegments(List<Vector2D> voronoiPoints,
			AxisAlignedBoundingBox2D boundingBox) {
		
		List<Segment2D> voronoiSegments = new ArrayList<Segment2D>();
		

		//Create the triangulation, initial triangle defined clockwise. Everything happens inside this
		//initial triangle.
		//The offset is used, because the algorithm needs some space between the edges of the bounding box
		//and the initial triangle.

		
		double offset = 1.;
		
		Pnt p1 = new Pnt(boundingBox.getMinX() - offset, boundingBox.getMinY() - offset);
		Pnt p2 = new Pnt(boundingBox.getMinX() - offset, 2 * (boundingBox.getMaxY() + offset));
		Pnt p3 = new Pnt(2 * (boundingBox.getMaxX() + offset), boundingBox.getMinY() - offset);
		
		Triangle initialTriangle = new Triangle(p1, p2, p3);
		
		Triangulation dt = new Triangulation(initialTriangle);
		
		//Add all Voronoi points to the triangulation and create segments from the triangulation
		for(Vector2D pnt : voronoiPoints) {
			dt.delaunayPlace(convertVector2DToPnt(pnt));
		}
		
		voronoiSegments = extractVoronoiSegments(dt, initialTriangle);
		
		return voronoiSegments;
	}
	
	/**
	 * Extracts all segments of the Voronoi polygons from a Delaunay triangulation. Based on 
	 * DelaunaySourceCodeJava60.jar by Paul Chew, chew@cs.cornell.edu
	 * @param dt
	 * @param initialTriangle
	 * @return
	 */
	private static List<Segment2D> extractVoronoiSegments(Triangulation dt, Triangle initialTriangle) {
		List<Segment2D> voronoiSegments = new ArrayList<Segment2D>();
		List<Vector2D> vertices = new ArrayList<Vector2D>();
		HashSet<Pnt> done = new HashSet<Pnt>(initialTriangle);
		
		//Extract the vertices of the Voronoi polygons from the Delaunay triangulation
		for (Triangle triangle : dt)
				for (Pnt site: triangle) {
				if (done.contains(site)) continue;
				done.add(site);
				List<Triangle> list = dt.surroundingTriangles(site, triangle);
				vertices.clear();
				for (Triangle tri: list) {
					vertices.add(convertPntToVector2D(tri.getCircumcenter()));
				}

				//Create segments from the vertices
				Vector2D[] vertexArray = new Vector2D[vertices.size()];
				vertices.toArray(vertexArray);
        		for(int i=0; i<vertexArray.length-1; i++) {
        			if(vertexArray[i].equals(vertexArray[i+1])) continue;
        			voronoiSegments.add(GeometryFactory.createSegment(vertexArray[i], vertexArray[i+1]));
        		}
        		if(!vertexArray[0].equals(vertexArray[vertexArray.length-1])) {
        			voronoiSegments.add(GeometryFactory.createSegment(vertexArray[0], vertexArray[vertexArray.length-1]));
        		}

            }
        
        return voronoiSegments;
	}
	
	/**
	 * Converts a vector2D to the pnt class to be used with DelaunaySourceCodeJava60.jar 
	 * by Paul Chew
	 * @param vector
	 * @return
	 */
	private static Pnt convertVector2DToPnt(Vector2D vector) {
		return new Pnt(vector.getXComponent(), vector.getYComponent());
	}
	
	/** 
	 * Converts a pnt from DelaunaySourceCodeJava60.jar to a vector2D
	 * @param point
	 * @return
	 */
	private static Vector2D convertPntToVector2D(Pnt point) {
		return GeometryFactory.createVector(point.coord(0), point.coord(1));
	}

}
