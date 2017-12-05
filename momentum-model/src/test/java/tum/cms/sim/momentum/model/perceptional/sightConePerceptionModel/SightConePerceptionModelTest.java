package tum.cms.sim.momentum.model.perceptional.sightConePerceptionModel;

import org.junit.Before;
import org.junit.Test;
import tum.cms.sim.momentum.data.agent.pedestrian.Pedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.state.operational.WalkingState;
import tum.cms.sim.momentum.data.agent.pedestrian.state.other.StaticState;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Vector2D;

import static org.junit.Assert.*;

public class SightConePerceptionModelTest {

    Pedestrian currentPedestrian;
    Pedestrian otherPedestrian;

    @Before
    public void setUp() throws Exception {

        StaticState staticState = new StaticState(3, 2, 1.4, 1, 1, 2, 3, 1, 2);
        currentPedestrian = new Pedestrian(staticState);
        otherPedestrian = new Pedestrian(staticState);
    }


    @Test
    public void isVisible() throws Exception {
        SightConePerceptionModel sightConePerceptionModelModel = new SightConePerceptionModel();
        sightConePerceptionModelModel.setAngle(90);
        sightConePerceptionModelModel.setRadius(30);

        Vector2D curPedPos, curPedHead, otherPedPos, otherPedHead;

        curPedPos = GeometryFactory.createVector(0,0);
        curPedHead = GeometryFactory.createVector(1,0);
        otherPedPos = GeometryFactory.createVector(20,0);
        otherPedHead = GeometryFactory.createVector(1,0);
        currentPedestrian.setWalkingState(new WalkingState(curPedPos, curPedHead, curPedHead));
        otherPedestrian.setWalkingState(new WalkingState(otherPedPos, otherPedHead, otherPedHead));

        assertTrue(sightConePerceptionModelModel.isVisible(currentPedestrian, otherPedestrian));

        otherPedPos.set(30.1, 0);
        assertFalse(sightConePerceptionModelModel.isVisible(currentPedestrian, otherPedestrian));

        otherPedPos.set(20, 20);
        assertTrue(sightConePerceptionModelModel.isVisible(currentPedestrian, otherPedestrian));

        otherPedPos.set(0, 20);
        assertFalse(sightConePerceptionModelModel.isVisible(currentPedestrian, otherPedestrian));

        otherPedPos.set(Math.cos(Math.toRadians(-46)), Math.sin(Math.toRadians(-46)));
        assertFalse(sightConePerceptionModelModel.isVisible(currentPedestrian, otherPedestrian));

        otherPedPos.set(Math.cos(Math.toRadians(-44)), Math.sin(Math.toRadians(-44)));
        assertTrue(sightConePerceptionModelModel.isVisible(currentPedestrian, otherPedestrian));


        curPedPos.set(10,10);
        curPedHead.set(0,-1);

        otherPedPos.set(10, 11);
        assertFalse(sightConePerceptionModelModel.isVisible(currentPedestrian, otherPedestrian));

        otherPedPos.set(10, 11);
        assertFalse(sightConePerceptionModelModel.isVisible(currentPedestrian, otherPedestrian));

        otherPedPos.set(10, 9);
        assertTrue(sightConePerceptionModelModel.isVisible(currentPedestrian, otherPedestrian));

        otherPedPos.set(10 + Math.cos(-46), 10 + Math.sin(-46));
        assertTrue(sightConePerceptionModelModel.isVisible(currentPedestrian, otherPedestrian));

        otherPedPos.set(10 + Math.cos(-44.5)*5, 10 + Math.sin(-44.5)*5);
        assertFalse(sightConePerceptionModelModel.isVisible(currentPedestrian, otherPedestrian));

    }

}