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

package tum.cms.sim.momentum.utility.generic;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

import tum.cms.sim.momentum.configuration.generic.ComplexPropertyConfiguration;
import tum.cms.sim.momentum.configuration.generic.FormatString;
import tum.cms.sim.momentum.configuration.generic.PropertyContainerNode;
import tum.cms.sim.momentum.configuration.generic.SimplePropertyConfiguration;

public class PropertyBackPack extends Unique {

	private LinkedHashMap<String, String> stringProperties = new LinkedHashMap<String, String>();
	
	private LinkedHashMap<String, File> fileProperties = new LinkedHashMap<String, File>();
	
	private LinkedHashMap<String, Double> doubleProperties = new LinkedHashMap<String, Double>();
	
	private LinkedHashMap<String, Boolean> booleanProperties = new LinkedHashMap<String, Boolean>();
		
	private HashMap<String, Integer> integerProperties = new LinkedHashMap<String, Integer>();

	private LinkedHashMap<String, FormatString> formatProperties = new LinkedHashMap<String, FormatString>();

	private LinkedHashMap<String, ArrayList<Object>> listProperties = new LinkedHashMap<String, ArrayList<Object>>();
	
	private LinkedHashMap<String, ArrayList<ArrayList<Object>>> listOfListsProperty = new LinkedHashMap<String, ArrayList<ArrayList<Object>>>();
	
	private LinkedHashMap<String, LinkedHashMap<Integer, ArrayList<Object>>> matrixProperties = new LinkedHashMap<String, LinkedHashMap<Integer, ArrayList<Object>>>();

	private LinkedHashMap<String, LinkedHashMap<Integer, Object>> linkedHashMapProperties = new LinkedHashMap<>();
	
	private LinkedHashMap<Integer, PropertyBackPack> backPackContainer = new LinkedHashMap<Integer, PropertyBackPack>();
	
	public boolean addSimpleProperty(SimplePropertyConfiguration simplePropertyConfiguration) {
		
		boolean successfull = false;
		String propertyName = simplePropertyConfiguration.getName();
		
		if(!stringProperties.containsKey(propertyName) &&
		   !fileProperties.containsKey(propertyName) &&
		   !doubleProperties.containsKey(propertyName) &&
		   !integerProperties.containsKey(propertyName) &&
		   !formatProperties.containsKey(propertyName)) {
			
			switch(simplePropertyConfiguration.getType()) {
			
			case String:
				
				String propertyString = simplePropertyConfiguration.getValueAsString();
			
				if(propertyString != null) {
				
					stringProperties.put(propertyName, propertyString);
					successfull = true;
				}
				
				break;
				
			case File:
				
				File propertyFile = simplePropertyConfiguration.getValueAsFile();
			
				if(propertyFile != null) {
				
					fileProperties.put(propertyName, propertyFile);
					successfull = true;
				}
				
				break;
				
			case Double:
				
				Double propertyDouble = simplePropertyConfiguration.getValueAsDouble();
			
				if(propertyDouble != null) {
				
					doubleProperties.put(propertyName, propertyDouble);
					successfull = true;
				}
				
				break;
			case Boolean:
				
				Boolean propertyBoolean = simplePropertyConfiguration.getValueAsBoolean();
			
				if(propertyBoolean != null) {
				
					booleanProperties.put(propertyName, propertyBoolean);
					successfull = true;
				}
				
				break;
					
			case Integer:
				
				Integer propertyInteger = simplePropertyConfiguration.getValueAsInteger();
			
				if(propertyInteger != null) {
				
					integerProperties.put(propertyName, propertyInteger);
					successfull = true;
				}
				
				break;
				
			case Format:
				FormatString propertyFormat = simplePropertyConfiguration.getValueAsFormat();
				
				if(propertyFormat != null) {
				
					formatProperties.put(propertyName, propertyFormat);
					successfull = true;
				}	
				
				break;
				
			default:
				break;
			}
			
		}

		return successfull;
	}
	
	public boolean addComplexProperty(ComplexPropertyConfiguration complexPropertyConfiguration) {
		
		boolean successfull = false;
		String propertyName = complexPropertyConfiguration.getName();

		if(!matrixProperties.containsKey(propertyName) &&
		   !listProperties.containsKey(propertyName) &&
		   !listOfListsProperty.containsKey(propertyName)) {
			
			switch(complexPropertyConfiguration.getType()) {
			
			case Matrix:
				
				LinkedHashMap<Integer, ArrayList<Object>> propertyMatrix = complexPropertyConfiguration.getValueAsMatrix();
			
				if(propertyMatrix != null) {
				
					matrixProperties.put(propertyName, propertyMatrix);
					successfull = true;
				}	
				
				break;
			
			case CsvList:
			case List:
				
				ArrayList<Object> propertyList = complexPropertyConfiguration.getValueAsList();
			
				if(propertyList != null) {
					
					listProperties.put(propertyName, propertyList);
					successfull = true;
				}
				break;
			case LinkedHashMap:
				
				LinkedHashMap<Integer, Object> propertyLinkedMap = complexPropertyConfiguration.getValueAsLinkedHashMap();
				
				if(propertyLinkedMap != null) {
					
					linkedHashMapProperties.put(propertyName, propertyLinkedMap);
					successfull = true;
				}
			
				break;
			case CsvMatrix:
				ArrayList<ArrayList<Object>> propertyListOfLists = complexPropertyConfiguration.getValueAsListOfLists();
				
				if(propertyListOfLists != null) {
					
					listOfListsProperty.put(propertyName, propertyListOfLists);
					successfull = true;
				}
			default:
				break;
			}
		}
		return successfull;
	}
	
	public void addChildPackBackContainer(PropertyContainerNode childProperty) {
		
		PropertyBackPack childPack = PropertyBackPackFactory.fillProperties(childProperty);
		backPackContainer.put(childProperty.getId(), childPack);
	}

	public Collection<Integer> getChildPropertyBackPackIds() {
		
		return backPackContainer.keySet();
	}
	
	public Collection<PropertyBackPack> getChildPropertyBackPacks() {
	
		return backPackContainer.values();
	}
	
	public PropertyBackPack getChildPropertyBackPack(Integer id) {
		
		return backPackContainer.get(id);
	}
	
	public Collection<String> getStringNames() {
		
		return stringProperties.keySet();
	}
	
	public Collection<String> getStringProperties() {
	
		return stringProperties.values();
	}
	
	public String getStringProperty(String name) {
		
		return stringProperties.get(name);
	}
	
	public Collection<String> getFileNames() {
		
		return fileProperties.keySet();
	}	
	
	public Collection<File> getFileProperties() {
		
		return fileProperties.values();
	}	
	
	public File getFileProperty(String name) {
		
		return fileProperties.get(name);
	}
	
	public Collection<String> getDoubleNames() {
		
		return doubleProperties.keySet();
	}	

	public Collection<Double> getDoubleProperties() {
		
		return doubleProperties.values();
	}	
	
	public Double getDoubleProperty(String name) {
		
		return doubleProperties.get(name);
	}
	
	public Collection<String> getIntegerNames() {
		
		return integerProperties.keySet();
	}	

	public Collection<Integer> getIntegerProperties() {
		
		return integerProperties.values();
	}	
	
	public Integer getIntegerProperty(String name) {
		
		return integerProperties.get(name);
	}
		
	public Collection<String> getFormatNames() {
		
		return formatProperties.keySet();
	}	
	
	public Collection<FormatString> getFormatProperties() {
		
		return formatProperties.values();
	}	
	
	public FormatString getFormatProperty(String name) {
		
		return formatProperties.get(name);
	}
	
	public Collection<Boolean> getBooleanProperties() {
		
		return booleanProperties.values();
	}	
	
	public Boolean getBooleanProperty(String name) {
		
		return booleanProperties.get(name);
	}
	
	@SuppressWarnings("unchecked")
	public <T> ArrayList<T> getListProperty(String name) {
		
		ArrayList<Object> property = listProperties.get(name);
		
		if(property == null) {
			return null;
		}
		
		return (ArrayList<T>)property;
	}
	
	/**
	 * HashMap Key is rows and arraylist index the columns
	 */
	@SuppressWarnings("unchecked")
	public <T> HashMap<Integer, ArrayList<T>> getMatrixProperty(String name) {
		
		HashMap<Integer, ArrayList<Object>> matrix = matrixProperties.get(name);
		
		if(matrix == null) {
			return null;
		}
		
		LinkedHashMap<Integer, ArrayList<T>> resultMatrix = new LinkedHashMap<Integer, ArrayList<T>>();
		matrix.forEach((raw,column) -> resultMatrix.put(raw, (ArrayList<T>)column));
		
		return resultMatrix;
	}
	
	@SuppressWarnings("unchecked")
	public <T> ArrayList<ArrayList<T>> getListOfListsProperty(String name) {
		
		ArrayList<ArrayList<Object>> listOfLists = listOfListsProperty.get(name);
		
		if(listOfLists == null) {
			return null;
		}
		ArrayList<ArrayList<T>> resultListOfList = new ArrayList<ArrayList<T>>();
		listOfLists.forEach(list -> resultListOfList.add((ArrayList<T>)list));
		
		return resultListOfList;
	}
}
