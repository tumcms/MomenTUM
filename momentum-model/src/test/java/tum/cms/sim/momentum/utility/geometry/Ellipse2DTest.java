package tum.cms.sim.momentum.utility.geometry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

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