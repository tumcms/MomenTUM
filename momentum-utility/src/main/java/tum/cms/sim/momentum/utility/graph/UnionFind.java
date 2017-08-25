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

package tum.cms.sim.momentum.utility.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Union find implementation along <a href = "http://www.mathblog.dk/disjoint-set-data-structure/">
 * http://www.mathblog.dk/disjoint-set-data-structure/</a>
 * @author qa
 * @param <T>
 */
public class UnionFind<T> {
	
	private Map<T, T> representativeMap;
	private Map<T, Integer> rankMap;
	
	public UnionFind(Collection<T> elements) {
		representativeMap = new HashMap<T, T>();
		rankMap = new HashMap<T, Integer>();
		
		for(T element : elements) {
			representativeMap.put(element, element);
			rankMap.put(element, 0);
		}
	}
	
	/**
	 * Returns representative element (i. e. element on the top of the tree) of the set the element is in
	 * @param element
	 * @return
	 */
	public T find(T element) {
		if(!representativeMap.containsKey(element)) {
			throw new IllegalArgumentException("Element not found");
		}
		T representativeElement = representativeMap.get(element);
		if(representativeElement.equals(element)) {
			return representativeElement;
		}
		else {
			//find representative element and link it to the element
			T newRepresentative = find(representativeElement);
			//path compression:
			representativeMap.put(element, newRepresentative);
			return newRepresentative;
		}
	}
	
	public void union(T element1, T element2) {
		if(!representativeMap.containsKey(element1) || 
				!representativeMap.containsKey(element2)) {
			throw new IllegalArgumentException("Elements not found");
		}
		
		T representative1 = find(element1);
		T representative2 = find(element2);
		int rank1 = rankMap.get(representative1);
		int rank2 = rankMap.get(representative2);
		
		if(representative1.equals(representative2)) {
			return;
		}
		
		if(rank1 < rank2) {
			representativeMap.put(representative1, representative2);
		}
		else if(rank2 < rank1) {
			representativeMap.put(representative2, representative1);
		}
		else {
			representativeMap.put(representative1, representative2);
			rankMap.put(representative2, rank2+1);
		}
	}

}
