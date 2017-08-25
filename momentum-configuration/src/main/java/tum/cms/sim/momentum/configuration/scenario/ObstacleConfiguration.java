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

package tum.cms.sim.momentum.configuration.scenario;

import java.util.HashMap;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.converters.enums.EnumToStringConverter;

@XStreamAlias("obstacle")
public class ObstacleConfiguration extends GeometryConfiguration {

	public enum ObstacleType {
		
		Solid,
		Wall,
		OneWayWall,
		VirtualWall
	}
	
	@SuppressWarnings("rawtypes")
	public static EnumToStringConverter getTypeConverter() {
		
		HashMap<String, ObstacleType> map = new HashMap<>();
		map.put(ObstacleType.Solid.toString(), ObstacleType.Solid);
		map.put(ObstacleType.Wall.toString(), ObstacleType.Wall);
		map.put(ObstacleType.OneWayWall.toString(), ObstacleType.OneWayWall);
		map.put(ObstacleType.VirtualWall.toString(), ObstacleType.VirtualWall);
		
		return new EnumToStringConverter<>(ObstacleType.class, map);
	}
	
	@XStreamAsAttribute
	private ObstacleType type;

	public ObstacleType getType() {
		return type;
	}

	public void setType(ObstacleType type) {
		this.type = type;
	}
	
	/**
	 * Where the direction point is, is the area from which the pedestrian can slip through the one way walls
	 */
	private PointConfiguration direction;

	public PointConfiguration getDirection() {
		return direction;
	}

	public void setDirection(PointConfiguration direction) {
		this.direction = direction;
	}
}
