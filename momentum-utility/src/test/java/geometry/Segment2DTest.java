package tum.cms.sim.momentum.utility.geometry;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

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