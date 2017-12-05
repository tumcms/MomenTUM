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

public class Rectangle2DTest {

    double PRECISION = 1E-15;
    Vector2D center, direction, point;
    double width, height;
    Rectangle2D rectangle;

    @Test
    public void vectorBetween() throws Exception {

        center = GeometryFactory.createVector(0, 0);
        direction = GeometryFactory.createVector(1, 0);
        width = 2; height = 6;
        rectangle = new Rectangle2D(center, direction, width, height);

        point = GeometryFactory.createVector(3, 2);
        Assert.assertEquals(1, rectangle.distanceBetween(point), PRECISION);

        point = GeometryFactory.createVector(-4, 2);
        Assert.assertEquals(Math.sqrt(2), rectangle.distanceBetween(point), PRECISION);

        point = GeometryFactory.createVector(3, 2);
        Vector2D between = rectangle.vectorBetween(point);
        Assert.assertEquals(0, between.getXComponent(), PRECISION);
        Assert.assertEquals(-1, between.getYComponent(), PRECISION);
    }

}