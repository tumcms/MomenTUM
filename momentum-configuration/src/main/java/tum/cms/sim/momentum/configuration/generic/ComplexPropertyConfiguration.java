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

package tum.cms.sim.momentum.configuration.generic;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ArrayList;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.converters.enums.EnumToStringConverter;

import tum.cms.sim.momentum.configuration.generic.SimplePropertyConfiguration.SimplePropertyType;

@XStreamAlias("complexProperty")
public class ComplexPropertyConfiguration {

	public enum ComplexPropertyType {
		
		Matrix,
		List,
		LinkedHashMap,
		CsvList,
		CsvMatrix,
		Executable
	}
	
	@SuppressWarnings("rawtypes")
	public static EnumToStringConverter getComplexPropertyTypeConverter() {
		
		HashMap<String, ComplexPropertyType> map = new HashMap<>();
		map.put(ComplexPropertyType.Matrix.toString(), ComplexPropertyType.Matrix);
		map.put(ComplexPropertyType.List.toString(), ComplexPropertyType.List);
		map.put(ComplexPropertyType.CsvList.toString(), ComplexPropertyType.CsvList);
		map.put(ComplexPropertyType.CsvMatrix.toString(), ComplexPropertyType.CsvMatrix);
		map.put(ComplexPropertyType.LinkedHashMap.toString(), ComplexPropertyType.LinkedHashMap);
		map.put(ComplexPropertyType.Executable.toString(), ComplexPropertyType.Executable);
		
		return new EnumToStringConverter<>(ComplexPropertyType.class, map);
	}
	
	private String name = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	private ComplexPropertyType type = null;

	public ComplexPropertyType getType() {
		
		return type;
	}

	public void setType(ComplexPropertyType type) {

		this.type = type;
	}
	
	private SimplePropertyType valueType = null;
	
	public SimplePropertyType getValueType() {
		return valueType;
	}

	public void setValueType(SimplePropertyType valueType) {
		this.valueType = valueType;
	}
	
	private Object value = null;

	public void setValue(Object value) {
		this.value = value;
	}
	
	public LinkedHashMap<Integer, ArrayList<Object>> getValueAsMatrix() {
		
		LinkedHashMap<Integer, ArrayList<Object>> result = null;
		
		if(this.type == ComplexPropertyType.Matrix) {
			
			result = new LinkedHashMap<Integer, ArrayList<Object>>();
			
			MatrixConfiguration matrix = (MatrixConfiguration) this.value;
			
			if(matrix.getMatrix() != null) {
				
				for(int iter = 0; iter < matrix.getMatrix().size(); iter++) {
					
					result.put(iter, new ArrayList<Object>());		
						
					if(matrix.getMatrix().get(iter) != null) {
							
						result.get(iter).addAll((ArrayList<Object>)matrix.getMatrix().get(iter));
					}
				}
			}
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Object> getValueAsList() {
		
		ArrayList<Object> result = null;
		
		if(this.type == ComplexPropertyType.List) {
			
			result = (ArrayList<Object>) this.value;
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public LinkedHashMap<Integer, Object> getValueAsLinkedHashMap() {
		
		LinkedHashMap<Integer,Object> result = null;
		
		if(this.type == ComplexPropertyType.LinkedHashMap) {
			
			result = (LinkedHashMap<Integer,Object>) this.value;
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<ArrayList<Object>> getValueAsListOfLists() {
		
		ArrayList<ArrayList<Object>> result = null;
		
		if(this.type == ComplexPropertyType.List) {
			
			result = (ArrayList<ArrayList<Object>>) this.value;
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<String, String> getValueAsXmlFileProperties() {
		
		HashMap<String, String> result = null;
		
		if(this.type == ComplexPropertyType.CsvList || this.type == ComplexPropertyType.CsvMatrix) {
			
			result = (HashMap<String, String>) this.value;
		}
		
		return result;
	}
}
