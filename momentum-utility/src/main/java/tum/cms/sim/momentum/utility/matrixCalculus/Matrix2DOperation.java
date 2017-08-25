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

package tum.cms.sim.momentum.utility.matrixCalculus;

public class Matrix2DOperation {

	private Matrix2DOperation() {}
	
	public static Matrix2D multiplicate(Matrix2D leftMatrix, Matrix2D rightMatrix) {
		
		int leftRows = leftMatrix.getRowSize();
		int rightRows = rightMatrix.getRowSize();
		int leftColumns = leftMatrix.getColumnSize();
		int rightColumns = rightMatrix.getColumnSize();

		if(leftColumns != rightRows) {
			
			return null;
		}

		Matrix2D result = new Matrix2D(leftRows, rightColumns);
		
        for (int leftRowIndex = 0; leftRowIndex < leftRows; leftRowIndex++) { 
        	
            for (int rightColumnIndex = 0; rightColumnIndex < rightColumns; rightColumnIndex++) { 
            	
                for (int leftColumnIndex = 0; leftColumnIndex < leftColumns; leftColumnIndex++) {
                	
                	result.set(leftRowIndex, rightColumnIndex, 
                		result.get(leftRowIndex, rightColumnIndex) + 
                			leftMatrix.get(leftRowIndex, leftColumnIndex) * 
                				rightMatrix.get(leftColumnIndex, rightColumnIndex));
                }
            }
        }

		return result;
	}
	
	public static Matrix2D multiplicate(Matrix2D matrix, double skalar) {
	
		Matrix2D result = new Matrix2D(matrix.getRowSize(), matrix.getColumnSize());
		
	     for (int rowIndex = 0; rowIndex < matrix.getRowSize(); rowIndex++) { 
	        	
            for (int columnIndex = 0; columnIndex < matrix.getColumnSize(); columnIndex++) { 
            	
            	result.set(rowIndex, columnIndex, matrix.get(rowIndex, columnIndex) * skalar);
            }
	     }
	     
	     return result;
	}
	
	public static Matrix2D elementAddition(Matrix2D leftMatrix, Matrix2D rightMatrix) {
		
		int leftRows = leftMatrix.getRowSize();
		int rightRows = rightMatrix.getRowSize();
		int leftColumns = leftMatrix.getColumnSize();
		int rightColumns = rightMatrix.getColumnSize();

		if(leftRows != rightRows || leftColumns != rightColumns) {
			
			return null;
		}

		Matrix2D result = new Matrix2D(leftRows, rightColumns);
		
        for (int leftRowIndex = 0; leftRowIndex < leftRows; leftRowIndex++) { 
        	
            for (int leftColumnIndex = 0; leftColumnIndex < leftColumns; leftColumnIndex++) {
            	
            	result.set(leftRowIndex, leftColumnIndex, 
            		leftMatrix.get(leftRowIndex, leftColumnIndex) + rightMatrix.get(leftRowIndex, leftColumnIndex));
            }
        }

		return result;
	}
}
