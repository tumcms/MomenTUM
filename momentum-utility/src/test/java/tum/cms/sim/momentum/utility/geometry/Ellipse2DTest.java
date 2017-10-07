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

public class Ellipse2DTest {

    private double PRECISION = 1E-10;

    private Vector2D center, direction, F1, F2, point, normal, closestPoint;
    private double majorAxis, minorAxis;
    private Ellipse2D ellipse;

    @Test
    public void closestPoint() throws Exception {
        F1 = GeometryFactory.createVector(0, 0);
        F2 = GeometryFactory.createVector(2, 0);
        minorAxis = 2;
        ellipse = GeometryFactory.createEllipse(F1, F2, minorAxis);
        point = GeometryFactory.createVector(1, 3);

        closestPoint = ellipse.closestPoint(point);
        Assert.assertEquals(1, closestPoint.getXComponent(), PRECISION);
        Assert.assertEquals(2, closestPoint.getYComponent(), PRECISION);


        ellipse = GeometryFactory.createEllipse(new Vector2D(10, 10),
                new Vector2D(1, 0), 1, 1);
        point = new Vector2D(12, 12);
        Assert.assertEquals(10+Math.sqrt(2)/2, ellipse.closestPoint(point).getXComponent(), PRECISION);
        Assert.assertEquals(10+Math.sqrt(2)/2, ellipse.closestPoint(point).getXComponent(), PRECISION);
    }

    @Test
    public void vectorBetween() throws Exception {
        Vector2D vectorBetween;

        F1 = GeometryFactory.createVector(0, 0);
        F2 = GeometryFactory.createVector(2, 0);
        minorAxis = 2;
        ellipse = GeometryFactory.createEllipse(F1, F2, minorAxis);

        point = GeometryFactory.createVector(1, 3);
        vectorBetween = ellipse.vectorBetween(point);
        Assert.assertEquals(0, vectorBetween.getXComponent(), PRECISION);
        Assert.assertEquals(1, vectorBetween.getYComponent(), PRECISION);


        ellipse = GeometryFactory.createEllipse(new Vector2D(10, 10),
                new Vector2D(1, 0), 1, 1);

        Assert.assertEquals(0, ellipse.getOrientation(), PRECISION);

        point = new Vector2D(12,12);
        closestPoint = ellipse.closestPoint(point);
        vectorBetween = ellipse.vectorBetween(point);
        Assert.assertEquals(2 - Math.sqrt(2)/2, vectorBetween.getXComponent(), PRECISION);
        Assert.assertEquals(2 - Math.sqrt(2)/2, vectorBetween.getYComponent(), PRECISION);
    }

    @Test
    public void normal() throws Exception {

        F1 = GeometryFactory.createVector(0, 0);
        F2 = GeometryFactory.createVector(2, 0);
        minorAxis = 2;
        ellipse = GeometryFactory.createEllipse(F1, F2, minorAxis);
        point = GeometryFactory.createVector(1, 3);
        normal = ellipse.normal(point);
        Assert.assertEquals(0, normal.getXComponent(), PRECISION);
        Assert.assertEquals(1, normal.getYComponent(), PRECISION);

        F1 = GeometryFactory.createVector(0, 0);
        F2 = GeometryFactory.createVector(2, 2);
        minorAxis = 1;
        ellipse = GeometryFactory.createEllipse(F1, F2, minorAxis);
        Assert.assertEquals(Math.sqrt(3), ellipse.getMajorAxis(), PRECISION);
        point = GeometryFactory.createVector(2.5, 2.5);
        normal = ellipse.normal(point);
        Assert.assertEquals(Math.sqrt(2)/2, normal.getXComponent(), PRECISION);
        Assert.assertEquals(Math.sqrt(2)/2, normal.getYComponent(), PRECISION);

        F1 = GeometryFactory.createVector(0, 0);
        F2 = GeometryFactory.createVector(2, 2);
        minorAxis = 1;
        ellipse = GeometryFactory.createEllipse(F1, F2, minorAxis);
        point = GeometryFactory.createVector(-1, -1);
        normal = ellipse.normal(point);
        Assert.assertEquals(-Math.sqrt(2)/2, normal.getXComponent(), PRECISION);
        Assert.assertEquals(-Math.sqrt(2)/2, normal.getYComponent(), PRECISION);


        center = GeometryFactory.createVector(1, 0);
        direction = GeometryFactory.createVector(1, 0);
        minorAxis = 2;
        majorAxis = 5;
        ellipse = GeometryFactory.createEllipse(center, direction, majorAxis, minorAxis);
        point = GeometryFactory.createVector(1, 5);
        normal = ellipse.normal(point);
        Assert.assertEquals(0, normal.getXComponent(), PRECISION);
        Assert.assertEquals(1, normal.getYComponent(), PRECISION);
    }


}