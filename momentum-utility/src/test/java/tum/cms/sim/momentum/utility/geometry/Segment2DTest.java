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

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;


public class Segment2DTest {

    private double PRECISION = 1E-10;

    @Test
    public void getSegmentSplittedByPolygon() throws Exception {

        Vector2D start = GeometryFactory.createVector(-5,0);
        Vector2D end = GeometryFactory.createVector(5,0);
        Segment2D segment = GeometryFactory.createSegment(start, end);

        ArrayList<Vector2D> nodeArray = new ArrayList<>();
        nodeArray.add(GeometryFactory.createVector(-2, -2));
        nodeArray.add(GeometryFactory.createVector(2, -2));
        nodeArray.add(GeometryFactory.createVector(2, 2));
        nodeArray.add(GeometryFactory.createVector(-2, 2));
        Polygon2D polygon = GeometryFactory.createPolygon(nodeArray);

        ArrayList<Segment2D> splittedSegment = segment.getSegmentSplittedByPolygon(polygon);

        assertEquals(3, splittedSegment.size());

        assertEquals(-5, splittedSegment.get(0).getFirstPoint().getXComponent(), PRECISION);
        assertEquals(0, splittedSegment.get(0).getFirstPoint().getYComponent(), PRECISION);
        assertEquals(-2, splittedSegment.get(0).getLastPoint().getXComponent(), PRECISION);
        assertEquals(0, splittedSegment.get(0).getLastPoint().getYComponent(), PRECISION);

        assertEquals(-2, splittedSegment.get(1).getFirstPoint().getXComponent(), PRECISION);
        assertEquals(0, splittedSegment.get(1).getFirstPoint().getYComponent(), PRECISION);
        assertEquals(2, splittedSegment.get(1).getLastPoint().getXComponent(), PRECISION);
        assertEquals(0, splittedSegment.get(1).getLastPoint().getYComponent(), PRECISION);

        assertEquals(2, splittedSegment.get(2).getFirstPoint().getXComponent(), PRECISION);
        assertEquals(0, splittedSegment.get(2).getFirstPoint().getYComponent(), PRECISION);
        assertEquals(5, splittedSegment.get(2).getLastPoint().getXComponent(), PRECISION);
        assertEquals(0, splittedSegment.get(2).getLastPoint().getYComponent(), PRECISION);

        System.out.println(splittedSegment.toString());

    }

}