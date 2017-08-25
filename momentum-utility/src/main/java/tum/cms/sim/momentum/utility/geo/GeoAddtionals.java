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

package tum.cms.sim.momentum.utility.geo;

import java.util.ArrayList;
import org.apache.commons.math3.util.FastMath;
import tum.cms.sim.momentum.utility.geometry.Cycle2D;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Segment2D;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.geometry.operation.GeometryAdditionals;

public class GeoAddtionals {

	/**
	 * mHager 08-12-2012
	 * http://en.wikipedia.org/wiki/Haversine_formula
	 * Implementation
	 * @param lat1
	 * @param lng1
	 * @param lat2
	 * @param lng2
	 * @return
	 */
	public static double haverSineDistance(double lat1, double lng1, double lat2, double lng2)  {

	    // convert to radians
	    lat1 = FastMath.toRadians(lat1);
	    lng1 = FastMath.toRadians(lng1);
	    lat2 = FastMath.toRadians(lat2);
	    lng2 = FastMath.toRadians(lng2);

	    double dlon = lng2 - lng1;
	    double dlat = lat2 - lat1;

	    double a = FastMath.pow((FastMath.sin(dlat/2)),2) + FastMath.cos(lat1) * FastMath.cos(lat2) * FastMath.pow(FastMath.sin(dlon/2),2);

	    double c = 2 * FastMath.atan2(FastMath.sqrt(a), FastMath.sqrt(1-a));

	    return 6378137 * c;
	}   
	
    
    /**
     * Important the cycle position is in lat lon and the distances (radius) is computed
     * using the haverSine distance
     * @param cycleA
     * @param cycleB
     * @param cycleC
     * @return
     */
    public static Vector2D trilateration(Cycle2D cycleA, Cycle2D cycleB, Cycle2D cycleC) {
    
    	Vector2D trilaterationLonLat = null;
		ArrayList<Vector2D> intersectionAB = cycleA.getIntersection(cycleB);
		ArrayList<Vector2D> intersectionAC = cycleA.getIntersection(cycleC);
		ArrayList<Vector2D> intersectionBC = cycleB.getIntersection(cycleC);
		
		if(intersectionAB != null && intersectionAC != null && intersectionBC != null) {
			
			Segment2D abLine = GeometryFactory.createSegment(intersectionAB);
			Segment2D acLine = GeometryFactory.createSegment(intersectionAC);
			Segment2D bcLine = GeometryFactory.createSegment(intersectionBC);
			
			ArrayList<Vector2D> ab_ac_Lineintersection = abLine.getIntersection(acLine);
			ArrayList<Vector2D> ac_bc_Lineintersection = acLine.getIntersection(bcLine);
			ArrayList<Vector2D> bc_ab_Lineintersection = bcLine.getIntersection(abLine);
			
			ArrayList<Vector2D> lineIntersections = new ArrayList<>();
			lineIntersections.addAll(ab_ac_Lineintersection);
			lineIntersections.addAll(ac_bc_Lineintersection);
			lineIntersections.addAll(bc_ab_Lineintersection);
			
			trilaterationLonLat = GeometryAdditionals.calculateVectorCenter(lineIntersections);
		}
		
		return trilaterationLonLat;
    }
}
