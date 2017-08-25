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

package tum.cms.sim.momentum.utility.stateMachine;

import java.util.HashMap;
import java.util.List;

import de.xabsl.jxabsl.IntermediateCodeMalformedException;
import de.xabsl.jxabsl.TimeFunction;
import de.xabsl.jxabsl.engine.Engine;
import de.xabsl.jxabsl.engine.Symbols;
import de.xabsl.jxabsl.symbols.BooleanInputSymbol;
import de.xabsl.jxabsl.symbols.BooleanOutputSymbol;
import de.xabsl.jxabsl.symbols.DecimalInputSymbol;
import de.xabsl.jxabsl.symbols.DecimalOutputSymbol;
import de.xabsl.jxabsl.symbols.EnumeratedInputSymbol;
import de.xabsl.jxabsl.symbols.EnumeratedOutputSymbol;
import de.xabsl.jxabsl.symbols.Enumeration;
import de.xabsl.jxabsl.utils.DebugMessages;
import de.xabsl.jxabslx.utils.PrintStreamDebug;

public class MachineEngine {

	private HashMap<String, Engine> agentEngines = null;
	
	public Enumeration getEnumeration(String agentName, String simpleName) {

		return agentEngines.get(agentName).getEnumeration(simpleName);
	}

	public Symbols getSymbols(String agentName) {
	
		return agentEngines.get(agentName);
	}
	
	private TimeFunction timeFunction = new TimeFunction() {
		
		@Override
		public long getTime() {
		
			return System.currentTimeMillis();
		}
	};
	
	private MachineInputSource input = null;
	
	private DebugMessages debugMessages = new PrintStreamDebug(System.out, System.err);

	public DebugMessages getDebugMessages() {
		return debugMessages;
	}

	MachineEngine(MachineInputSource inputSource) {

		input = inputSource;
	}
	
	public boolean createEngine(String agentName) {
		
		boolean isCreated = false;
		
		if(!agentEngines.containsKey(agentName)) {
			
			Engine engine = new Engine(debugMessages, timeFunction);
			agentEngines.put(agentName, engine);
			isCreated = true;
		}
		
		return isCreated;
	}
	
	public boolean bootEngine(String agentName) {
		 
		boolean isBooted = false;
		
		if(agentEngines.containsKey(agentName)) {
			
			try {
				
				agentEngines.get(agentName).createOptionGraph(this.input.getInputSource());
				isBooted = true;
			} 
			catch (IntermediateCodeMalformedException e) {

			}
		}
		
		return isBooted;
	}
	
	public boolean shutdownEngine(String agentName) {
		
		boolean isShutdown = false;
		
		if(agentEngines.containsKey(agentName)) {
				
			agentEngines.remove(agentName);
			isShutdown = true;
		}
		
		return isShutdown;
	}

	public void selectTopMachine(String agentName, String topMachineName) {
		
		if(agentEngines.containsKey(agentName)) {
			
			agentEngines.get(agentName).setSelectedAgent(topMachineName);
		}
	}
	
	public void switchMachine(String agentName) {

		if(agentEngines.containsKey(agentName)) {
			
			agentEngines.get(agentName).execute();
		}
	}

	public void bindEnumerations(String agentName, List<EnumerationBinding> enumerationBindings) {
		
		if(agentEngines.containsKey(agentName)) {
			
			enumerationBindings.forEach(enumeration ->
			agentEngines.get(agentName).registerEnumeration(enumeration.getEnumeration()));
		}
	}
	
	public void bindInputSymbols(String agentName, List<InputSymbolBinding> inputSymbols) {
		
		if(agentEngines.containsKey(agentName)) {
			
			for(InputSymbolBinding inputSymbolBinding : inputSymbols) {
				
				switch(inputSymbolBinding.getSymbolType()) {
				
				case Boolean: 
					
					agentEngines.get(agentName).registerBooleanInputSymbol(inputSymbolBinding.getBindMethodName(), 
							(BooleanInputSymbol)inputSymbolBinding.getInputSymbol());
					break;
	
				case Enumeration: 
					
					agentEngines.get(agentName).registerEnumeratedInputSymbol(inputSymbolBinding.getBindMethodName(), 
							(EnumeratedInputSymbol)inputSymbolBinding.getInputSymbol());
					break;	
				
				default: 
				
					agentEngines.get(agentName).registerDecimalInputSymbol(inputSymbolBinding.getBindMethodName(), 
							(DecimalInputSymbol)inputSymbolBinding.getInputSymbol());
				}
			}
		}
	}

	public void bindOutputSymbols(String agentName, List<OutputSymbolBinding> outputSymbols) {
		
		if(agentEngines.containsKey(agentName)) {
			
			for(OutputSymbolBinding outputSymbolBinding : outputSymbols) {
				
				switch(outputSymbolBinding.getSymbolType()) {
	
				case Boolean: 
					
					agentEngines.get(agentName).registerBooleanOutputSymbol(outputSymbolBinding.getBindMethodName(), 
							(BooleanOutputSymbol)outputSymbolBinding.getOutputSymbol());
					break;
	
				case Enumeration: 
					
					agentEngines.get(agentName).registerEnumeratedOutputSymbol(outputSymbolBinding.getBindMethodName(), 
							(EnumeratedOutputSymbol)outputSymbolBinding.getOutputSymbol());
					break;	
				
				default: // decimal is a number in general, can be converted to i.e integer, long, float, and decimal
					
					agentEngines.get(agentName).registerDecimalOutputSymbol(outputSymbolBinding.getBindMethodName(), 
							(DecimalOutputSymbol)outputSymbolBinding.getOutputSymbol());
					break;
				}
			}
		}
	}

	public void bindBasicBehaviors(String agentName, List<BehaviorBinding> behaviors) {
		
		if(agentEngines.containsKey(agentName)) {
			
			behaviors.forEach(behaviorBinding -> agentEngines.get(agentName).registerBasicBehavior(behaviorBinding.getBehavior()));
		}
	}
}
