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

package tum.cms.sim.momentum.model.output.writerFormats;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import tum.cms.sim.momentum.data.output.WriterData;
import tum.cms.sim.momentum.model.output.writerSources.WriterSource;

/**
 * WriterFormat which creates Microsoft Sql Server compliant SQL DDL statements for inserting
 * into a table.
 * 
 * @author Sven Lauterbach (sven.lauterbach@tum.de)
 *
 */
public class MsSqlWriterFormat extends WriterFormat {
	
	private String table;
	

	@Override
	public void initialize() {
		
		table = this.getPropertyBackPack().getStringProperty("table");
		
	}

	@Override
	public WriterData formatData(WriterSource writerSource) {
		
		WriterData writerData = new WriterData();
		
		LinkedHashSet<String> columns = new LinkedHashSet<>();
		List<String> rows = new LinkedList<>();
		
		/*
		 * iterates over the writer source data and fill the columns hashset containing the column identifier
		 * and the rows list which contains a string containing the values for each row in the format 
		 * "(value1, value2, ...., valueN)". This allows to add multiple rows with one insert statement.
		 */
		while(writerSource.hasNextSet()) {
		
			writerSource.loadSet();
			
			while(writerSource.hasNextSetItem()) {
				
				writerSource.loadSetItem();

				List<String> rowValues = new LinkedList<>();
 								
				for(String itemType : writerSource.getDataItemNames()) {
				
					String itemContent = writerSource.readSingleValue(itemType);
					
					if(itemContent != null && !itemContent.isEmpty()) {
						columns.add(itemType);
						rowValues.add(itemContent);
					}
				}			

				String currentRow = "(" + String.join(",", rowValues) + ")";
				rows.add(currentRow);
			}
			
		}
		
		if(columns.size() > 0 && rows.size() > 0) {

			/*
			 * Insert into <table> (column1, column2, ..., columnN) values (value1,..valueN), (value1, ...valueN)
			 * 
			 * This Syntax is MS SQL TSQL, but maybe could also work in postgres or other sql server.
			 */
			String sqlInsertCommand = "Insert Into " + table + " (" + String.join(",", columns) + ") Values" + String.join(",", rows) + ";";
					
			writerData.setData(sqlInsertCommand);
		}
		
		return writerData;
	}

}
