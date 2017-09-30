package tum.cms.sim.momentum.utility.geometry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

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