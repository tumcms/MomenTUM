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

package tum.cms.sim.momentum.model.operational.walking.socialForceModel.ParallelHelbingOperational;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.Pair;

import tum.cms.sim.momentum.data.agent.pedestrian.state.operational.WalkingState;
import tum.cms.sim.momentum.data.agent.pedestrian.state.tactical.TacticalState.Motoric;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IOperationalPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IPedestrianExtansion;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.data.layout.obstacle.Obstacle;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.infrastructure.execute.threading.NamedThreadFactory;
import tum.cms.sim.momentum.model.operational.walking.WalkingModel;
import tum.cms.sim.momentum.model.operational.walking.socialForceModel.SocialForce;
import tum.cms.sim.momentum.model.operational.walking.socialForceModel.SocialForcePedestrianExtension;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.spaceTree.QuadTree;
import tum.cms.sim.momentum.utility.spaceTree.QuadTreeNode;

/***
 * Walking Model which uses the social force model using the Barnes-Hut algorithms
 * to approximate forces between pedestrians. 
 * 
 * @author Sven Lauterbach (sven.lauterbach@tum.de)
 *
 */
public class BarnesHutParallelHelbingOperational extends WalkingModel {
	
	private SocialForce socialForce;
    private ConcurrentHashMap<IOperationalPedestrian, NodeData> bodies = new ConcurrentHashMap<>();
    private QuadTree<NodeData> quadTree;
    private double threshold = 0.3;
    private boolean parallelPhase2 = false;
    private int threads = 2;
    private ThreadPoolExecutor workerPool;

	@Override
	public IPedestrianExtansion onPedestrianGeneration(IRichPedestrian pedestrian) {

		return new SocialForcePedestrianExtension();
	}

	@Override
	public void onPedestrianRemoval(IRichPedestrian pedestrian) {
		
		// nothing to do
	}

	@Override
	public void callBeforeBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		
		if(pedestrians.size() == 0) {
            return;
        }

        final double maxX = this.scenarioManager.getScenarios().getMaxX();
        final double maxY = this.scenarioManager.getScenarios().getMaxY();

        final double minX = this.scenarioManager.getScenarios().getMinX();
        final double minY = this.scenarioManager.getScenarios().getMinY();

        final double width = maxX - minX;
        final double height = maxY - minY;

        quadTree = new QuadTree<NodeData>(minX, minY, width, height, new NodeDataFactory());

        //1. insert bodies into quadtree (sequential)
        for(IRichPedestrian ped : pedestrians)
        {
        	NodeData body = NodeData.createFromPedestrian(ped);
            quadTree.insert(body);
            bodies.put(ped, body);
        }

        //2. update inner nodes
        if(parallelPhase2) {

            Map<Integer, List<QuadTreeNode<NodeData>>> quadtreelevels = quadTree.getTreeLevels();

            int maxLevel = quadtreelevels.keySet()
                    .stream()
                    .max(Integer::compare)
                    .get();

            /*
             * iterate from the last tree level of the quadtree bottom up to the top
             * and compute for each node in the current level i the center of mass, velocity,
             * heading etc.
             */
            for (int i = maxLevel; i >= 0; i--) {

                List<QuadTreeNode<NodeData>> nodes = quadtreelevels.get(i);
                int size = nodes.size();

                //we only update in parallel if there are enough nodes per thread
                if(size > this.threads * 20) {               	

                    int batchSize = (int) Math.floor(size / threads);

                    List<List<QuadTreeNode<NodeData>>> batches = chopped(nodes, batchSize);

                    CountDownLatch latch = new CountDownLatch(batches.size());
                    for(List<QuadTreeNode<NodeData>> batch : batches) {
                        workerPool.submit(() -> {
                            for(QuadTreeNode<NodeData> node : batch) {
                                computeCenterOfMass(node, false);
                            }
                            latch.countDown();
                            return 0;
                        });
                    }

                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } 
                }
                else {
                    for(QuadTreeNode<NodeData> node : nodes) {
                        computeCenterOfMass(node, false);
                    }
                }
            }
        }
        else {
        	QuadTreeNode<NodeData> root = quadTree.getRoot();
            computeCenterOfMass(root, true);
        }
		
	}
	
	@Override
	public void callAfterBehavior(SimulationState simulationState, Collection<IRichPedestrian> pedestrians) {
		
	}

	@Override
	public void callPedestrianBehavior(IOperationalPedestrian pedestrian, SimulationState simulationState) {		

		QuadTreeNode<NodeData> root = quadTree.getRoot();
		
		//we traverse the quadtree and retrieve all nodes which should be used to calculate the repulsive interaction force.
		List<IPedestrian> otherPedestrians = computePedestrianInteractionForcePartner(root, bodies.get(pedestrian), threshold);
		
		List<Obstacle> obstacles = this.scenarioManager.getObstacles()
				.stream()
				.filter(obstacle -> obstacle.getGeometry().distanceBetween(pedestrian.getPosition()) < 5.0)
				.collect(Collectors.toList());
		
		Vector2D acceleration = socialForce.computeNewAcceleration(pedestrian, 
																	otherPedestrians,
																	obstacles);
		
		Vector2D deltaVelocity = acceleration.multiply(simulationState.getTimeStepDuration());
		Vector2D velocity = pedestrian.getVelocity().sum(deltaVelocity);

		if(velocity.getMagnitude() > pedestrian.getMaximalVelocity() ) {
		
			velocity = velocity.getNormalized()
					.multiply(pedestrian.getMaximalVelocity());
		}
		
		Vector2D deltaPosition = velocity.multiply(simulationState.getTimeStepDuration());
		Vector2D position = pedestrian.getPosition().sum(deltaPosition);
		
		Vector2D heading = this.computeHeading(pedestrian, pedestrian.getNextWalkingTarget());
		WalkingState novelState = new WalkingState(position, velocity, heading);
		
		pedestrian.setWalkingState(novelState);
	}

	@Override
	public void callPreProcessing(SimulationState simulationState) {
		
		this.socialForce = new SocialForce(this);
        Optional<Integer> threads = Optional.ofNullable(this.properties.getIntegerProperty("threads"));
        Optional<Double> possibleTheshold = Optional.ofNullable(this.properties.getDoubleProperty("mac_threshold"));
        Optional<Boolean> parallelPhase2 = Optional.ofNullable(this.properties.getBooleanProperty("parallelPhase2"));

        if(possibleTheshold.isPresent()) {
            this.threshold = possibleTheshold.get();
        }

        if(parallelPhase2.isPresent()) {
            this.parallelPhase2 = parallelPhase2.get();
        }

        if(threads.isPresent()) {
            this.threads = threads.get();
        }
		else {
			
			this.threads = simulationState.getNumberOfThreads();
		}
        
        workerPool = new ThreadPoolExecutor(this.threads, this.threads, 1000 * 3, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory("BarnesHutParallelHelbingOperational"));
		
	}

	@Override
	public void callPostProcessing(SimulationState simulationState) {
		workerPool.shutdown();		
	}
	
	private void computeCenterOfMass(QuadTreeNode<NodeData> rootNode, boolean recursive) {

        NodeData root = rootNode.getData();

        double currentNodeMass = 0.0;
        double currentMass = 0.0;
        Vector2D velocity = GeometryFactory.createVector(0, 0);
        Vector2D centerOfMass = GeometryFactory.createVector(0, 0);
        List<Pair<Double, Vector2D>> positions = new ArrayList<Pair<Double, Vector2D>>(4);
        HashMap<Motoric, Integer> motorikTasks = new HashMap<Motoric, Integer>();
        HashMap<Double, Vector2D> headings = new HashMap<>();
        double maxDistance = 0.0;

        /*
         * iterate over a childs and aggregate:
         * - overall mass
         * - center of mass
         * - overall velocity
         * - overall heading
         * - overall motoric task
         */
        for (int i = 0; i < 4; i++) {
        	@SuppressWarnings("unchecked")
			QuadTreeNode<NodeData> childNode = (QuadTreeNode<NodeData>) rootNode.getChild(i);

            if (childNode != null) {

                NodeData child = childNode.getData();

                if (!childNode.isLeaf() && recursive)  {
                    computeCenterOfMass(childNode, recursive);
                }
                currentNodeMass = child.getMass();

                currentMass += currentNodeMass;
                centerOfMass = centerOfMass.sum(child.getCenterOfMass().multiply(currentNodeMass));
                velocity = velocity.sum(child.getVelocity().multiply(currentNodeMass));                
                positions.add(new Pair<Double, Vector2D>(currentNodeMass, child.getPosition()));
                headings.put(currentNodeMass, child.getHeading());
                motorikTasks.computeIfPresent(child.getMotoricTask(), (k,v) -> v+1);
                motorikTasks.computeIfAbsent(child.getMotoricTask(), k -> 1);
            }
        }
        
        /*
         * After aggregation we use the aggregated values to calculate average values, which should
         * form the properties of the inner node
         */

        Vector2D currrentVelocity = GeometryFactory.createVector(velocity.getXComponent() / currentMass,
                velocity.getYComponent() / currentMass);

        Vector2D currentCenterOfMass = GeometryFactory.createVector(centerOfMass.getXComponent() / currentMass,
                centerOfMass.getYComponent() / currentMass);
        
        /*
         * We use the motoric task which occured most offen.
         */
        Motoric averageMotoricTask = motorikTasks.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .findFirst()
                .get()
                .getKey();
        
        /*
         * We use the heading of the child with the largest mass
         */
        Vector2D heading = headings.entrySet()
        		.stream()
        		.max(Map.Entry.comparingByKey(Double::compare))
        		.get()
        		.getValue();

        /*
         * To calculate the radius of the inner node we use the distance between
         * the center of mass and the pedestrian with the highest mass, which can 
         * also be a other inner node.
         */
        if(positions.size() > 1) {
            double maxMass = 0.0d;
            for (Pair<Double, Vector2D> position : positions) {
                double currentPositionMass = (double) position.getFirst();
                if(currentPositionMass >= maxMass) {
                    maxDistance = Math.max(maxDistance, currentCenterOfMass.distance((Vector2D)position.getSecond()));
                    maxMass = currentPositionMass;
                }
            }
        }
        
        root.setBodyRadius(maxDistance);
        root.setMotoricTask(averageMotoricTask);
        root.setCenterOfMass(currentCenterOfMass);
        root.setMass(currentMass);
        root.setVelocity(currrentVelocity);
        root.setHeading(heading);
    }
	
	private List<IPedestrian> computePedestrianInteractionForcePartner(QuadTreeNode<NodeData> rootNode, NodeData body, double threshold)
    {
        NodeData root = rootNode.getData();
        Vector2D centerOfMass = root.getCenterOfMass();
        Vector2D bodyPosition = body.getPosition();
        double distance = centerOfMass.distance(bodyPosition);
        List<IPedestrian> result = new LinkedList<>();

        //if the current node is a other pedestrian we add him/her to the interaction partners
        if(rootNode.isLeaf())
        {
        	//.....but only if its not the same pedestrian we are looking for interaction partners for
            if(root != body)
            {
            	result.add(root);
            }
        }
        else
        {
        	//...if its a inner node which is far away we use the inner node as interaction partner
            double radius = Math.min(rootNode.getRadiusX(), rootNode.getRadiusY());
            double boxWidth = radius * 2;
            if((boxWidth / distance) < threshold)
            {
                //use internal node
            	result.add(root);
            }
            else {
                //....if its to close we traverse the tree
                for(int i = 0; i < 4; i++)
                {
                	@SuppressWarnings("unchecked")
					QuadTreeNode<NodeData> childNode =  (QuadTreeNode<NodeData>) rootNode.getChild(i);

                    if(childNode != null)
                    {
                        if(childNode.isLeaf())
                        {
                        	NodeData other = childNode.getData();
                            result.add(other);
                        }
                        else
                        {
                        	result.addAll(computePedestrianInteractionForcePartner(childNode, body, threshold));
                        }
                    }
                }
            }
        }

        return result;
    }
	
	private Vector2D computeHeading(IOperationalPedestrian me, Vector2D target) {
		
		return target.subtract(me.getPosition()).getNormalized();
	}
	
	public <T> List<List<T>> chopped(List<T> list, final int L) {
		List<List<T>> parts = new ArrayList<List<T>>();
		final int N = list.size();
		for (int i = 0; i < N; i += L) {
			parts.add(new ArrayList<T>(
					list.subList(i, Math.min(N, i + L)))
			);
		}
		return parts;
	}

}
