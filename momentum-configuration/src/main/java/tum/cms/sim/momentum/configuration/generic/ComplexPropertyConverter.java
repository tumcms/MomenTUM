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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import tum.cms.sim.momentum.configuration.generic.ComplexPropertyConfiguration.ComplexPropertyType;
import tum.cms.sim.momentum.configuration.generic.SimplePropertyConfiguration.SimplePropertyType;

public class ComplexPropertyConverter implements Converter {
	
	public ComplexPropertyConverter() { }
	
	private Boolean loadExternalFiles = true;
	
	public Boolean isLoadExternalFiles() {
		return this.loadExternalFiles;
	}
	
	public void setLoadExternalFiles(Boolean value) {
		this.loadExternalFiles = value;
	}
	
	public ComplexPropertyConverter(Boolean loadExternalFiles) {
		this.loadExternalFiles = loadExternalFiles;
	}

	@SuppressWarnings("rawtypes")
	public boolean canConvert(Class clazz) {
		return clazz.equals(ComplexPropertyConfiguration.class);
	}

	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {

		ComplexPropertyConfiguration comPro = (ComplexPropertyConfiguration) value;

		writer.addAttribute("name", comPro.getName());
		writer.addAttribute("type", comPro.getType().name());
		writer.addAttribute("valueType", comPro.getValueType().name());
		
		switch (comPro.getType()) {

		case Matrix:

			HashMap<Integer, ArrayList<Object>> matrix = comPro.getValueAsMatrix();

			for(int i = 0; i < matrix.size(); i++) {

				if(matrix.get(i) != null) {

					writer.startNode("row");
					writer.addAttribute("index", Integer.toString(i));

					for (int j = 0; j < matrix.get(i).size(); j++) {

						if(matrix.get(i).get(j) != null) {

							writer.startNode("column");
							writer.addAttribute("index", Integer.toString(j));
							writer.addAttribute("value", matrix.get(i).get(j).toString());
							writer.endNode();
						}
					}
					writer.endNode();
				}
			}

			break;

		case CsvList:
		case List:

			ArrayList<?> list = (ArrayList<?>) comPro.getValueAsList();

			for(int i = 0; i < list.size(); i++) {

				if(list.get(i) != null) {

					writer.startNode("entry");
					writer.addAttribute("index", Integer.toString(i));
					writer.addAttribute("value",list.get(i).toString());
					writer.endNode();
				}
			}

			break;
		case LinkedHashMap:
			LinkedHashMap<Integer,Object> linkedHashMap = (LinkedHashMap<Integer, Object>) comPro.getValueAsLinkedHashMap();

			int iter = 0;
			for(Entry<Integer, Object> keyValue : linkedHashMap.entrySet()) {
				
				if(keyValue != null && keyValue.getKey() != null && keyValue.getValue() != null) {
				
					writer.startNode("entry");
					writer.addAttribute("index", Integer.toString(iter++));
					writer.addAttribute("key", keyValue.getKey().toString());
					writer.addAttribute("value", keyValue.getValue().toString());
					writer.endNode();	
				}
			}

			break;
		case CsvMatrix:
			ArrayList<ArrayList<Object>> listOfLists = (ArrayList<ArrayList<Object>>) comPro.getValueAsListOfLists();

			for(int i = 0; i < listOfLists.size(); i++) {

				writer.startNode("entry");
				writer.addAttribute("index", Integer.toString(i));
				
				for(int j = 0; j < listOfLists.get(i).size(); j++) {

					if(listOfLists.get(i).get(j) != null) {
	
						writer.startNode("entry");
						writer.addAttribute("index", Integer.toString(j));
						writer.addAttribute("value", listOfLists.get(i).toString());
						writer.endNode();
					}
				}
				
				writer.endNode();
			}
		case Executable:
			// TODO
			break;
		default:
			//throw new Exception("Incorrect Complex Property: type " + comPro.getType() + ", name " + comPro.getName());
			break;
		}
	}

	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		
		ComplexPropertyConfiguration comPro = new ComplexPropertyConfiguration();
		comPro.setName(reader.getAttribute("name"));
		Object value = null;

		switch(reader.getAttribute("type")) {

		case "Matrix":

			comPro.setType(ComplexPropertyType.Matrix);

				comPro.setValueType(SimplePropertyType.valueOf(reader.getAttribute("valueType")));
				MatrixConfiguration matrix = new MatrixConfiguration();

				while(reader.hasMoreChildren()) {

					reader.moveDown();
					int rowIndex = Integer.parseInt(reader.getAttribute("index"));

					while(reader.hasMoreChildren()) {

						reader.moveDown();
						int columnIndex = Integer.parseInt(reader.getAttribute("index"));
						switch(comPro.getValueType()) {
						
						case Double: 
							
							value = Double.parseDouble(reader.getAttribute("value"));
							break;
				
						case Integer: 
							
							value = Integer.parseInt(reader.getAttribute("value"));
							break;
		
						case String:
						default:
								
							value = reader.getAttribute("value");
							break;				
						}
						
						matrix.setValue(rowIndex, columnIndex, value);
						reader.moveUp();
					}
					reader.moveUp();
				}
				comPro.setValue(matrix);
				
			break;

		case "LinkedHashMap":

			comPro.setType(ComplexPropertyType.LinkedHashMap);
			comPro.setValueType(SimplePropertyType.valueOf(reader.getAttribute("valueType")));
			LinkedHashMap<Integer, Object> linkedHashMap = new LinkedHashMap<Integer, Object>();
			ArrayList<Integer> indexList = new ArrayList<Integer>();
			ArrayList<Integer> keyList = new ArrayList<Integer>();
			ArrayList<String> dataHashMapList = new ArrayList<String>();
			
			while(reader.hasMoreChildren()) {
	
				reader.moveDown();
				indexList.add(Integer.parseInt(reader.getAttribute("index")));
				keyList.add(Integer.parseInt(reader.getAttribute("key")));
				dataHashMapList.add(reader.getAttribute("value"));
				reader.moveUp();
			}
			
			int compareIter = 0;
			int targetIndex = 0;
			while(compareIter < indexList.size()) {
				
				for(int indexIter = 0; indexIter < indexList.size(); indexIter++) {
					
					if(compareIter == indexList.get(indexIter)) {
						
						targetIndex = compareIter;
					}
				}
				
				switch(comPro.getValueType()) {
				
				case Double: 
					
					value = Double.parseDouble(dataHashMapList.get(targetIndex));
					break;
		
				case Integer: 
					
					value = Integer.parseInt(dataHashMapList.get(targetIndex));
					break;

				case String:
				default:
						
					value = reader.getAttribute(dataHashMapList.get(targetIndex));
					break;				
				}
				
				linkedHashMap.put(keyList.get(targetIndex), value);
				compareIter++;
			}
			
			comPro.setValue(linkedHashMap);
			break;
			
		case "List":

			comPro.setType(ComplexPropertyType.List);
			comPro.setValueType(SimplePropertyType.valueOf(reader.getAttribute("valueType")));
			ArrayList <Object> list = new ArrayList<Object>();
	
			while(reader.hasMoreChildren()) {
	
				reader.moveDown();
				int index = Integer.parseInt(reader.getAttribute("index"));
				
				switch(comPro.getValueType()) {
				case Boolean:
					
					value = Boolean.parseBoolean(reader.getAttribute("value"));
					break;
					
				case Double: 
					
					value = Double.parseDouble(reader.getAttribute("value"));
					break;
		
				case Integer: 
					
					value = Integer.parseInt(reader.getAttribute("value"));
					break;
	
				case String:
				default:
						
					value = reader.getAttribute("value");
					break;				
				}
				
				setValueInList(list, index, value);
				reader.moveUp();
			}
			
			comPro.setValue(list);
			break;
		case "CsvList": 
			
			if(loadExternalFiles) {
				comPro.setType(ComplexPropertyType.List); // thats is correct!
				comPro.setValueType(SimplePropertyType.valueOf(reader.getAttribute("valueType")));
				
				reader.moveDown();
				
				String fileName = reader.getAttribute("file");
				String separator = reader.getAttribute("separator");
				File csvFile = null;
				ArrayList<Object> dataList = new ArrayList<Object>();
				
				
				try {
	
					csvFile = new File(fileName);
					List<String> input = Files.readAllLines(csvFile.toPath());
					
					StringBuilder completeInput = new StringBuilder();
					input.stream().forEach(line -> completeInput.append(line));
					String [] items = completeInput.toString().split(separator);		
					String itemValue = null;
					
					for(int iter = 0; iter < items.length; iter++) {
						
						itemValue = items[iter];
						switch(comPro.getValueType()) {
						
						case Boolean:
							
							value = Boolean.parseBoolean(reader.getAttribute("value"));
							break;
							
						case Double: 
							
							value = Double.parseDouble(itemValue);
							break;
				
						case Integer: 
							
							value = Integer.parseInt(itemValue);
							break;
			
						case String:
						default:
								
							value = reader.getAttribute(itemValue);
							break;				
						}
						setValueInList(dataList, iter, value);
					}				
				}
				catch (IOException e) {
					
					e.printStackTrace();
				}
				reader.moveUp();
				comPro.setValue(dataList);
				
			} else {
				
				comPro.setName(reader.getAttribute("name"));
				comPro.setType(ComplexPropertyType.CsvList); 
				comPro.setValueType(SimplePropertyType.valueOf(reader.getAttribute("valueType")));
				
				reader.moveDown();
				
				String listFileName = reader.getAttribute("file");
				String listSeparator = reader.getAttribute("separator");
				
				HashMap<String, String> listFileData = new HashMap<>();
				listFileData.put("file", listFileName);
				listFileData.put("separator", listSeparator);

				reader.moveUp();
				comPro.setValue(listFileData);
				
			}
			
			break;
			
		case "CsvMatrix":
			
			if(loadExternalFiles) {
			
				comPro.setType(ComplexPropertyType.Matrix); // thats is correct!
				comPro.setValueType(SimplePropertyType.valueOf(reader.getAttribute("valueType")));
				ArrayList<ArrayList<Object>> dataListsOfList = new ArrayList<ArrayList<Object>>();
				MatrixConfiguration matrixCsv = new MatrixConfiguration();
				
				while(reader.hasMoreChildren()) {
	
					reader.moveDown();
					ArrayList<Object> dataCsvList = null;
					
					String subFileName = reader.getAttribute("file");
					String subSeparator = reader.getAttribute("separator");
							
					File subCsvFile = null;
					
					try {
						
						subCsvFile = new File(subFileName);
						List<String> input = Files.readAllLines(subCsvFile.toPath());
						
						for(String line : input) {	
							
							String [] items = line.toString().split(subSeparator);		
							String itemValue = null;
							dataCsvList = new ArrayList<Object>();
							
							for(int iter = 0; iter < items.length; iter++) {
		
								itemValue = items[iter];

								if(!itemValue.isEmpty()) {
								
									switch(comPro.getValueType()) {
									
									case Double: 
										
										value = Double.parseDouble(itemValue);
										break;
							
									case Integer: 
										
										value = Integer.parseInt(itemValue);
										break;
						
									case String:
									default:
											
										value = reader.getAttribute(itemValue);
										break;				
									}
								}
							
								dataCsvList.add(value);
							}
							dataListsOfList.add(dataCsvList);
						}				
					}
					catch (IOException e) {
						
						e.printStackTrace();
					}
		
					matrixCsv.setMatrix(dataListsOfList);
	//				for(int row = 0; row < dataListsOfList.size(); row++) {
	//					
	//					if(row == 0 && hasHeader != null && hasHeader) {
	//						
	//						continue;
	//					}
	//					
	//					for(int column = 0; column < dataListsOfList.get(row).size(); column++) {
	//						
	//						if(column == 0 && hasHeader != null && hasHeader) {
	//							
	//							continue;
	//						}
	//						
	//						Integer matrixRow = ((Double) dataListsOfList.get(row).get(0)).intValue();
	//						Integer matrixColumn = ((Double)dataListsOfList.get(0).get(column)).intValue();
	//						matrixCsv.setValue(matrixRow, matrixColumn, dataListsOfList.get(row).get(column));
	//					}
	//				}
					
					reader.moveUp();
				}
	
				comPro.setValue(matrixCsv);
				
			} else {
				
				comPro.setName(reader.getAttribute("name"));
				comPro.setType(ComplexPropertyType.CsvMatrix); 
				comPro.setValueType(SimplePropertyType.valueOf(reader.getAttribute("valueType")));
				
				reader.moveDown();
				
				String matrixFileName = reader.getAttribute("file");
				String matrixSeparator = reader.getAttribute("separator");
				
				HashMap<String, String> matrixFileData = new HashMap<>();
				matrixFileData.put("file", matrixFileName);
				matrixFileData.put("separator", matrixSeparator);
				
				reader.moveUp();
				comPro.setValue(matrixFileData);
//				break;
			}
			break;
		}
			
		return comPro;	
	}
	
	/*
	 * Assures that the value is set in the list at the correct Position!
	 * The List needn't to start at index = 0
	 */
	private void setValueInList(ArrayList<Object> list, int index, Object value) {

		if(list.size() < index) {

			list.ensureCapacity(index);
			while (list.size() < index) {
				list.add(null);
			}
		}
		if(list.size() == index){

			list.add(index, value);
		}
		else if(list.size() > index){

			list.set(index, value);
		}
	}
}
