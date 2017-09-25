package tum.cms.sim.momentum.utility.geometry;

import org.dyn4j.geometry.Ray;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class Ray2DTest {

    private double PRECISION = 1E-10;

    private Vector2D aStart, aDirection, bStart, bDirection, intersection, point;
    private Ray2D a, b;


    @Test
    public void intersectionPoint() throws Exception {

        // Parallel rays
        aStart = GeometryFactory.createVector(0,0);
        aDirection = GeometryFactory.createVector(1,0);
        a = GeometryFactory.createRay2D(aStart, aDirection);

        bStart = GeometryFactory.createVector(0,1);
        bDirection = GeometryFactory.createVector(1,0);
        b = GeometryFactory.createRay2D(bStart, bDirection);
        assertNull(a.intersectionPoint(b));

        // Identical rays
        aStart = GeometryFactory.createVector(0,0);
        aDirection = GeometryFactory.createVector(1,0);
        a = GeometryFactory.createRay2D(aStart, aDirection);
        b = GeometryFactory.createRay2D(aStart, aDirection);
        assertNull(a.intersectionPoint(b));

        // Overlapping rays
        aStart = GeometryFactory.createVector(1,1);
        aDirection = GeometryFactory.createVector(1,0);
        a = GeometryFactory.createRay2D(aStart, aDirection);

        bStart = GeometryFactory.createVector(2,1);
        bDirection = GeometryFactory.createVector(1,0);
        b = GeometryFactory.createRay2D(bStart, bDirection);
        assertNull(a.intersectionPoint(b));

        // Intersecting rays, 1
        aStart = GeometryFactory.createVector(5,0);
        aDirection = GeometryFactory.createVector(-1,0);
        bStart = GeometryFactory.createVector(0,5);
        bDirection = GeometryFactory.createVector(0,-1);
        a = GeometryFactory.createRay2D(aStart, aDirection);
        b = GeometryFactory.createRay2D(bStart, bDirection);

        intersection = a.intersectionPoint(b);
        Assert.assertEquals(0, intersection.getXComponent(), PRECISION);
        Assert.assertEquals(0, intersection.getXComponent(), PRECISION);

        // Intersecting rays, 2
        aStart = GeometryFactory.createVector(2,0);
        aDirection = GeometryFactory.createVector(1,2);
        bStart = GeometryFactory.createVector(0,2);
        bDirection = GeometryFactory.createVector(2,1);
        a = GeometryFactory.createRay2D(aStart, aDirection);
        b = GeometryFactory.createRay2D(bStart, bDirection);

        intersection = a.intersectionPoint(b);
        Assert.assertEquals(4, intersection.getXComponent(), PRECISION);
        Assert.assertEquals(4, intersection.getXComponent(), PRECISION);
    }

    @Test
    public void isParallel() throws Exception {
        aStart = GeometryFactory.createVector(0,0);
        aDirection = GeometryFactory.createVector(1,0);
        a = GeometryFactory.createRay2D(aStart, aDirection);

        bStart = GeometryFactory.createVector(1,1);
        bDirection = GeometryFactory.createVector(1,0);
        b = GeometryFactory.createRay2D(bStart, bDirection);

        assertTrue(a.isParallel(b));

        bStart = GeometryFactory.createVector(1,1);
        bDirection = GeometryFactory.createVector(1,1.0001);
        b = GeometryFactory.createRay2D(bStart, bDirection);
        assertFalse(a.isParallel(b));

        bStart = GeometryFactory.createVector(0,0);
        bDirection = GeometryFactory.createVector(0,1);
        b = GeometryFactory.createRay2D(bStart, bDirection);
        assertFalse(a.isParallel(b));
    }

    @Test
    public void equals() throws Exception {
        aStart = GeometryFactory.createVector(0,0);
        aDirection = GeometryFactory.createVector(1,0);
        a = GeometryFactory.createRay2D(aStart, aDirection);

        bStart = GeometryFactory.createVector(1,1);
        bDirection = GeometryFactory.createVector(1,0);
        b = GeometryFactory.createRay2D(bStart, bDirection);
        assertFalse(a.equals(b));

        bStart = GeometryFactory.createVector(0,0);
        bDirection = GeometryFactory.createVector(2,0);
        b = GeometryFactory.createRay2D(bStart, bDirection);
        assertTrue(a.equals(b));
    }

    @Test
    public void contains() throws Exception {
        // horizontal test
        aStart = GeometryFactory.createVector(5,5);
        aDirection = GeometryFactory.createVector(1,0);
        a = GeometryFactory.createRay2D(aStart, aDirection);

        point = GeometryFactory.createVector(7,0);
        assertFalse(a.contains(point));

        point = GeometryFactory.createVector(7,5);
        assertTrue(a.contains(point));

        point = GeometryFactory.createVector(4,5);
        assertFalse(a.contains(point));

        // vertical test
        aStart = GeometryFactory.createVector(0,0);
        aDirection = GeometryFactory.createVector(0,1);
        a = GeometryFactory.createRay2D(aStart, aDirection);

        point = GeometryFactory.createVector(0,1);
        assertTrue(a.contains(point));

        point = GeometryFactory.createVector(0,-2);
        assertFalse(a.contains(point));

        // slope test
        aStart = GeometryFactory.createVector(1,0);
        aDirection = GeometryFactory.createVector(1,1);
        a = GeometryFactory.createRay2D(aStart, aDirection);

        point = GeometryFactory.createVector(3,2);
        assertTrue(a.contains(point));

        point = GeometryFactory.createVector(0,-1);
        assertFalse(a.contains(point));
    }

    @Test
    public void intersectionRay() throws Exception {
        // intersecting rays, 1
        aStart = GeometryFactory.createVector(0,0);
        aDirection = GeometryFactory.createVector(1,0);
        a = GeometryFactory.createRay2D(aStart, aDirection);

        bStart = GeometryFactory.createVector(5,0);
        bDirection = GeometryFactory.createVector(1,0);
        b = GeometryFactory.createRay2D(bStart, bDirection);

        Ray2D intersectionRay = a.intersectionRay(b);
        assertTrue(intersectionRay.equals(b));
        intersectionRay = b.intersectionRay(a);
        assertTrue(intersectionRay.equals(b));

        // intersecting rays, 2
        aStart = GeometryFactory.createVector(0,0);
        aDirection = GeometryFactory.createVector(0,5);
        a = GeometryFactory.createRay2D(aStart, aDirection);

        bStart = GeometryFactory.createVector(0,3);
        bDirection = GeometryFactory.createVector(0,0.1);
        b = GeometryFactory.createRay2D(bStart, bDirection);

        intersectionRay = a.intersectionRay(b);
        assertTrue(intersectionRay.equals(b));
        intersectionRay = b.intersectionRay(a);
        assertTrue(intersectionRay.equals(b));

        // rays, which form a segment
        aStart = GeometryFactory.createVector(0,0);
        aDirection = GeometryFactory.createVector(1,0);
        a = GeometryFactory.createRay2D(aStart, aDirection);

        bStart = GeometryFactory.createVector(5,0);
        bDirection = GeometryFactory.createVector(-1,0);
        b = GeometryFactory.createRay2D(bStart, bDirection);
        assertNull(a.intersectionRay(b));

        // not parallel rays
        aStart = GeometryFactory.createVector(0,0);
        aDirection = GeometryFactory.createVector(1,0);
        a = GeometryFactory.createRay2D(aStart, aDirection);

        bStart = GeometryFactory.createVector(0,5);
        bDirection = GeometryFactory.createVector(0,-1);
        b = GeometryFactory.createRay2D(bStart, bDirection);
        assertNull(a.intersectionRay(b));
    }

    @Test
    public void intersectionSegment() throws Exception {
        // intersecting rays, 1
        aStart = GeometryFactory.createVector(0,0);
        aDirection = GeometryFactory.createVector(1,0);
        a = GeometryFactory.createRay2D(aStart, aDirection);

        bStart = GeometryFactory.createVector(5,0);
        bDirection = GeometryFactory.createVector(-1,0);
        b = GeometryFactory.createRay2D(bStart, bDirection);

        Segment2D intersectionSegment = a.intersectionSegment(b);
        Assert.assertEquals(0, intersectionSegment.getFirstPoint().getXComponent(), PRECISION);
        Assert.assertEquals(0, intersectionSegment.getFirstPoint().getYComponent(), PRECISION);
        Assert.assertEquals(5, intersectionSegment.getLastPoint().getXComponent(), PRECISION);
        Assert.assertEquals(0, intersectionSegment.getLastPoint().getYComponent(), PRECISION);

        // intersecting rays, 2
        aStart = GeometryFactory.createVector(0,0);
        aDirection = GeometryFactory.createVector(0,1);
        a = GeometryFactory.createRay2D(aStart, aDirection);

        bStart = GeometryFactory.createVector(0,7);
        bDirection = GeometryFactory.createVector(0,-0.1);
        b = GeometryFactory.createRay2D(bStart, bDirection);

        intersectionSegment = a.intersectionSegment(b);
        Assert.assertEquals(0, intersectionSegment.getFirstPoint().getXComponent(), PRECISION);
        Assert.assertEquals(0, intersectionSegment.getFirstPoint().getYComponent(), PRECISION);
        Assert.assertEquals(0, intersectionSegment.getLastPoint().getXComponent(), PRECISION);
        Assert.assertEquals(7, intersectionSegment.getLastPoint().getYComponent(), PRECISION);


        // not intersecting rays
        bStart = GeometryFactory.createVector(5,5);
        bDirection = GeometryFactory.createVector(-1,0);
        b = GeometryFactory.createRay2D(bStart, bDirection);

        intersectionSegment = a.intersectionSegment(b);
        assertNull(intersectionSegment);


    }

}