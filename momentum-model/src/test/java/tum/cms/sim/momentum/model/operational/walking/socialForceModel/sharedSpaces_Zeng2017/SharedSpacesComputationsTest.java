package tum.cms.sim.momentum.model.operational.walking.socialForceModel.sharedSpaces_Zeng2017;

import org.junit.Assert;
import org.junit.Test;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Vector2D;

import static org.junit.Assert.*;

public class SharedSpacesComputationsTest {

    private static double PRECISION = 1E-15;


    @Test
    public void calculateTimeToConflictPoint() throws Exception {

        Vector2D curPosition, curVelocity, othPosition, othVelocity;

        // vertical velocity test
        curPosition = GeometryFactory.createVector(0, 0);
        curVelocity = GeometryFactory.createVector(0, 1);
        othPosition = GeometryFactory.createVector(2, 2);
        othVelocity = GeometryFactory.createVector(-1, 0);
        Assert.assertEquals(0.0, SharedSpacesComputations.calculateTimeToConflictPoint(curPosition, curVelocity,
                othPosition, othVelocity), PRECISION);

        curPosition = GeometryFactory.createVector(0, 0);
        curVelocity = GeometryFactory.createVector(1, 1);
        othPosition = GeometryFactory.createVector(2, 0);
        othVelocity = GeometryFactory.createVector(-1, 1);
        Assert.assertEquals(0.0, SharedSpacesComputations.calculateTimeToConflictPoint(curPosition, curVelocity,
                othPosition, othVelocity), PRECISION);

        curPosition = GeometryFactory.createVector(0, 0);
        curVelocity = GeometryFactory.createVector(0, 1);
        othPosition = GeometryFactory.createVector(1, 0);
        othVelocity = GeometryFactory.createVector(-1, -1);
        Assert.assertEquals(Double.POSITIVE_INFINITY, SharedSpacesComputations.calculateTimeToConflictPoint(curPosition, curVelocity,
                othPosition, othVelocity), PRECISION);

        // collision point, which you reach after 1 and i reach after 11
        curPosition = GeometryFactory.createVector(0, 0);
        curVelocity = GeometryFactory.createVector(1, 0);
        othPosition = GeometryFactory.createVector(11, 1);
        othVelocity = GeometryFactory.createVector(0, -1);
        Assert.assertEquals(10.0, SharedSpacesComputations.calculateTimeToConflictPoint(curPosition, curVelocity,
                othPosition, othVelocity), PRECISION);

        curPosition = GeometryFactory.createVector(0, 0);
        curVelocity = GeometryFactory.createVector(0, 1);
        othPosition = GeometryFactory.createVector(0, 10);
        othVelocity = GeometryFactory.createVector(0, -1);
        Assert.assertEquals(0.0, SharedSpacesComputations.calculateTimeToConflictPoint(curPosition, curVelocity,
                othPosition, othVelocity), PRECISION);
    }

}