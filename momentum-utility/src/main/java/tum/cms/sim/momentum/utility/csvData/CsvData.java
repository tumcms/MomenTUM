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

package tum.cms.sim.momentum.utility.csvData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.comparators.ComparatorChain;

public class CsvData {

	
	public enum CsvDataTypes {
		
		String,
		Integer,
		Double
	}
	
	private String delimiter = ";";
	
	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	private List<String> header = null;

	private List<ArrayList<String>> allData = new ArrayList<ArrayList<String>>();
	
	public CsvData(String fileName, boolean isFolder, boolean hasHeader) throws IOException {

		if(isFolder) {
			
			// Merge all statistics data
			for(Path nextStatisticsPath : Files.walk(Paths.get(fileName)).collect(Collectors.toList())) {
				
				if (Files.isRegularFile(nextStatisticsPath)) {
					
					CsvData dataSet = new CsvData(nextStatisticsPath.toAbsolutePath().toString(), false, true);
					this.addData(dataSet);
					this.addHeader(dataSet.getHeader());
				}
			}
		}
		else {
			
			this.fillFromFile(fileName);
			
			if(hasHeader) {
				
				header = allData.remove(0);
			}
		}
	}
	
	public CsvData() { }

		
	public void addHeader(List<String> header) {
	
		this.header = header;
	}
	
	public void addData(CsvData data) {
		
		for(int iter = 0; iter < data.size(); iter++) {
			
			allData.add(data.getStringRow(iter));
		}
	}

	public void addData(ArrayList<String> dataSet) {
	
		allData.add(dataSet);
	}
	
	public CsvData(List<ArrayList<String>> data, boolean hasHeader) {
		
		allData = data;
		
		if(hasHeader) {
			
			header = allData.remove(0);
		}
	}

	public void clear() {

		this.allData.clear();
		this.header.clear();
	}
	
	public List<String> getHeader() {
		return header;
	}
	
	public void addRow(ArrayList<String> row) {
		
		allData.add(row);
	}
	
	public ArrayList<String> getStringRow(int rowIndex) {
		
		return allData.get(rowIndex);
	}
	
	public ArrayList<String> getStringColumn(int columnIndex) {
	
		ArrayList<String> column = new ArrayList<>();
		
		allData.forEach(row -> column.add(row.get(columnIndex)));
		
		return column;
	}

	/**
	 * Checks for the combinations of index data (string concatenation with ;) and removes all
	 * data rows that are duplicates regarding the index list. Thus, the first appearance will 
	 * be included in the results.
	 * 
	 * This method creates a copy of the data and do not change the original csv data.
	 */
	public CsvData distinctIgnoreOnColumn(int... columnIndexList) {
		
		CsvData resultData = new CsvData();
		resultData.addHeader(this.header);

		ArrayList<Integer> columnIndexListToCheck = new ArrayList<>();
		
		for(int iter = 0; iter < columnIndexList.length; iter++) {
			
			columnIndexListToCheck.add(columnIndexList[iter]);
		}
		
		HashSet<String> knownDataForColumn = new HashSet<>();
		
		for(int iter = 0; iter < this.size(); iter++) {
			
			ArrayList<String> data = this.getStringRow(iter);
			StringBuilder dataIdentifier = new StringBuilder(); 
			
			columnIndexListToCheck.forEach(index -> {
				dataIdentifier.append(data.get(index));
				dataIdentifier.append(";");
			});
			
			String dataItem = dataIdentifier.toString();
			
			if(!knownDataForColumn.contains(dataItem)) {
				
				knownDataForColumn.add(dataItem);
				resultData.addData(data);
			}
		}
		
		return resultData;
	}
	
	public CsvData meanOnDistinctIgnoreOnColumn(int columnIndex) {
		
		CsvData resultData = new CsvData();
		resultData.addHeader(this.header);

		ArrayList<Double> dataSet = new ArrayList<>();
		
		for(int iter = 0; iter < this.size(); iter++) {
			
			dataSet.add(this.getDoubleData(iter, columnIndex));
		}
		
		
//		StringBuilder dataIdentifier = new StringBuilder(); 
//		
//		columnIndexListToCheck.forEach(index -> {
//			dataIdentifier.append(data.get(index));
//			dataIdentifier.append(";");
//		});
//		
//		String dataItem = dataIdentifier.toString();
//		
//		if(!knownDataForColumn.contains(dataItem)) {
//			
//			resultData.addData(data);
//		}
		return resultData;
	}
	
	
	public ArrayList<Integer> getIntegerColumn(int columnIndex) {
		
		ArrayList<Integer> column = new ArrayList<>();
		
		allData.forEach(row -> column.add(Integer.parseInt(row.get(columnIndex))));
		
		return column;
	}
	
	public ArrayList<Integer> getIntegerRow(int iter) {
		
		ArrayList<Integer> row = new ArrayList<>();
		
		allData.get(iter).forEach(item -> row.add(Integer.parseInt(item)));
		
		return row;
	}
	
	public ArrayList<Double> getDoubleRow(int iter) {
		
		ArrayList<Double> row = new ArrayList<>();
		
		allData.get(iter).forEach(item -> row.add(Double.parseDouble(item)));
		
		return row;
	}
	
	public ArrayList<Double> getDoubleColumn(int columnIndex) {
		
		ArrayList<Double> column = new ArrayList<>();
		
		allData.forEach(row -> column.add(Double.parseDouble(row.get(columnIndex))));
		
		return column;
	}
	
	public String getStringData(int rowIndex, int columnIndex) {
		
		return this.getStringRow(rowIndex).get(columnIndex);
	}
	
	public int getIntegerData(int rowIndex, int columnIndex) {
		
		return Integer.parseInt(this.getStringRow(rowIndex).get(columnIndex));
	}
	
	public double getDoubleData(int rowIndex, int columnIndex) {
		
		return Double.parseDouble(this.getStringRow(rowIndex).get(columnIndex));
	}
	
	public void fillFromFile(String fileName) throws IOException {
		
		Files.readAllLines(Paths.get(fileName)).forEach(dataLine -> {
			
			allData.add(new ArrayList<String>(Arrays.asList(dataLine.split(this.delimiter))));
		});
	}

	public void removeColumn(int... removeIndexColumn) {
		
		for(int iter = 0; iter < this.size(); iter++) {
			
			for(int columnIter = 0; columnIter < removeIndexColumn.length; columnIter++) {
				
				this.allData.get(iter).remove(removeIndexColumn[columnIter]);	
			}
		}
	}
	
	public int size() {
	
		return allData.size();
	}
	
	/**
	 * Creates an iterator which points to the next list of lists packed by the column index.
	 * For multiple column index, the combination of the content of the columns are the split point.
	 * 
	 * @param columnIndex index starting with 0 of a column which should be the splitter
	 * @return Iterator on list of list packed by column index
	 */
	public Iterator<CsvData> getSubListsIteratorBy(int... columnIndex) {

		PackedListIterator subListIterator = new PackedListIterator();
		subListIterator.setPackingIndex(columnIndex);
		
		return subListIterator;
	}
	
	public Iterator<CsvData> getSubListsIteratorBy(List<Integer> columnIndex) {

		PackedListIterator subListIterator = new PackedListIterator();
		int[] flatIndex = new int[columnIndex.size()];
		
		for(int iter = 0; iter < columnIndex.size(); iter++) {
			
			flatIndex[iter] = columnIndex.get(iter);
		}
		
		subListIterator.setPackingIndex(flatIndex);
		
		return subListIterator;
	}
	
	/** 
	 * Creates sub data sets which comprises list of lists packed by the column index list.
	 * For multiple column index, the combination of the content of the columns are the split point.
	 */
	public ArrayList<CsvData> splitToSubListsBy(int... columnIndex) {
	
		ArrayList<CsvData> splittedData = new ArrayList<>();
		
		PackedListIterator subListIterator = new PackedListIterator();
		subListIterator.setPackingIndex(columnIndex);
		
		while(subListIterator.hasNext()) {
			
			splittedData.add(subListIterator.next());
		}
		
		return splittedData;
	}
	
	/**
	 * Removes all keys that are not in the notToRemoveKeys list on index position keyIndex.
	 */
	public void removeNotKeys(List<String> notToRemoveKeys, Integer keyIndex) {
		
		for(int iter = 0; iter < allData.size(); iter++) {
			
			boolean found = false;
			
			for(String notToRemoveKey : notToRemoveKeys) {
				
				if(this.getStringRow(iter).get(keyIndex).equals(notToRemoveKey)) {
					
					found = true;
				}
			}
		
			if(!found) {
			
				allData.remove(iter--);
			}
		}
	}
	
	public void sortAlphanumeric(List<Integer> sortIndices, List<CsvDataTypes> dataTypeForIndex) {

		ComparatorChain<ArrayList<String>> compareChain = new ComparatorChain<>();
		
		for(int iter = 0; iter < sortIndices.size(); iter++) {
			
			CsvDataComperator comperator = new CsvDataComperator();
			comperator.setIndex(sortIndices.get(iter)); 
			comperator.setDataType(dataTypeForIndex.get(iter));
			compareChain.addComparator(comperator);
		}
		
		Collections.sort(allData, compareChain);
	}
	

	public void sortAllForHeader() {
		
		ComparatorChain<ArrayList<String>> compareChain = new ComparatorChain<>();
		
		for(int iter = 0; iter < header.size(); iter++) {
			
			CsvDataComperator comperator = new CsvDataComperator();
			comperator.setIndex(iter); 
			comperator.setDataType(CsvDataTypes.String);
			compareChain.addComparator(comperator);
		}
		
		Collections.sort(allData, compareChain);
	}
	
	/** 
	 * fins first for search String in searchInColumnIndex
	 * 
	 * @param searchString
	 * @param searchInColumnIndex
	 * @return
	 */
	public ArrayList<String> findRowFor(String searchString, int searchInColumnIndex) {
		
		ArrayList<String> foundData = null;
		
		for(ArrayList<String> data : allData) {
			
			if(data.get(searchInColumnIndex).equals(searchString)) {
		
				foundData = data;
				break;
			}
		}
		
		return foundData;
	};
	
	private class CsvDataComperator implements Comparator<ArrayList<String>> {
		
		private int index = 0;
		
		public void setIndex(int index) {
			
			this.index = index;
		}

		public CsvDataTypes dataType = CsvDataTypes.String;
		
		public void setDataType(CsvDataTypes dataType) {
			this.dataType = dataType;
		}

		@Override
		public int compare(ArrayList<String> dataRow, ArrayList<String> dataRowOther) {

			switch(dataType) {
			
			case Double:
				return Double.compare(Double.parseDouble(dataRow.get(index)), Double.parseDouble(dataRowOther.get(index)));
			case Integer:
				return Integer.compare(Integer.parseInt(dataRow.get(index)), Integer.parseInt(dataRowOther.get(index)));
			case String:
			default:
				return dataRow.get(index).compareTo(dataRowOther.get(index));
			}	
		}
	}
	
	private class PackedListIterator implements Iterator<CsvData> {
		
		private ArrayList<Integer> packingIndex = new ArrayList<>();
		
		public void setPackingIndex(int... packingIndex) {
			
			for(int iter = 0; iter < packingIndex.length; iter++) {
				
				this.packingIndex.add(packingIndex[iter]);
			}
		}
		
		private int currentIndex = 0;
		

		@Override
		public CsvData next() {
			
			String packingPoint = this.computePackingCode(currentIndex);
			
			int startIndex = currentIndex;
			int endIndex = currentIndex;
			
			while(this.hasNext() && this.computePackingCode(++currentIndex).equals(packingPoint)) {
				
				endIndex++;
			}
			
			List<ArrayList<String>> subDataRaw = CsvData.this.allData.subList(startIndex, endIndex + 1);
			CsvData subData = new CsvData(subDataRaw, false);
			
			return subData;
		}
		
		@Override
		public boolean hasNext() {
		
			return CsvData.this.allData.size() > currentIndex + 1;
		}
		
		private String computePackingCode(int currentIndex) {
			
			ArrayList<String> packingIndicator = CsvData.this.allData.get(currentIndex);
			StringBuilder dataIdentifier = new StringBuilder(); 
			
			packingIndex.forEach(index -> {
				dataIdentifier.append(packingIndicator.get(index));
				dataIdentifier.append(";");
			});
			
			return dataIdentifier.toString();
		}
	}
}
