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

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.util.List;

import de.xabsl.jxabsl.behavior.BasicBehavior;
import de.xabsl.jxabsl.symbols.Enumeration;
import de.xabsl.jxabslx.conversions.Conversions;
import de.xabsl.jxabslx.io.Input;
import de.xabsl.jxabslx.io.InputFromMethod;
import de.xabsl.jxabslx.io.MethodBasicBehavior;
import de.xabsl.jxabslx.io.Output;
import de.xabsl.jxabslx.io.OutputToSetterMethod;
import de.xabsl.jxabslx.symbols.BooleanInputSymbolImpl;
import de.xabsl.jxabslx.symbols.BooleanOutputSymbolImpl;
import de.xabsl.jxabslx.symbols.DecimalInputSymbolImpl;
import de.xabsl.jxabslx.symbols.DecimalOutputSymbolImpl;
import de.xabsl.jxabslx.symbols.EnumeratedInputSymbolImpl;
import de.xabsl.jxabslx.symbols.EnumeratedOutputSymbolImpl;
import de.xabsl.jxabslx.symbols.InputSymbolImpl;
import de.xabsl.jxabslx.symbols.JavaEnumeration;
import de.xabsl.jxabslx.symbols.OutputSymbolImpl;
import tum.cms.sim.momentum.utility.stateMachine.XabslConstant.SymbolType;

public class StateMachineFactory {

	private final static String illegalArgumentMessage = "Return type of given method is unkown: ";
	
	public static MachineInputSource createMachineInputSource(File inputfile,
			int numberOfTopMachines, 
			List<String> topMachineNames,
			String rootOptionName) throws FileNotFoundException {
		
		return new MachineInputSource(inputfile, numberOfTopMachines, topMachineNames, rootOptionName);
	}
	
	public static MachineEngine createMachineEngine(MachineInputSource inputSource) {
	
		return new MachineEngine(inputSource);
	}
	
	public static <T> EnumerationBinding createEnumeration(Class<T> enumClass, 
			String agentName,
			MachineEngine container) {
		
		Enumeration enumeration = container.getEnumeration(agentName,
				enumClass.getSimpleName());
		
		if(enumeration == null) {
			
			enumeration = new JavaEnumeration(enumClass.getSimpleName(), enumClass, container.getDebugMessages());
		}
		
		return new EnumerationBinding(enumeration);
	}
	
	public static InputSymbolBinding createInputSymbolBinding(Object inputMethodSoure,
			String inputMethodName,
			String stateMachineMethodName,
			String agentName,
			MachineEngine container) throws NoSuchMethodException, SecurityException, IllegalArgumentException {
		
		Method inputMethod = inputMethodSoure.getClass().getMethod(inputMethodName);
		Input input = new InputFromMethod(inputMethod, inputMethodSoure);
		
		InputSymbolImpl inputSymbol = null;
		SymbolType symbolType = SymbolType.Unkown;
		
		if(inputMethod.getReturnType().equals(Boolean.class)) {
				
			symbolType = SymbolType.Boolean;
			inputSymbol = new BooleanInputSymbolImpl(input,
				Conversions.getBooleanConversion(Boolean.class),
				new String[] { },
				container.getSymbols(agentName),
				container.getDebugMessages());
						
		}
		else if(inputMethod.getReturnType().equals(Double.class)) {
			
			symbolType = SymbolType.Decimal;
			inputSymbol = new DecimalInputSymbolImpl(input,
				Conversions.getDecimalConversion(Double.class),
				new String[] { },
				container.getSymbols(agentName),
				container.getDebugMessages());
		}
		else if(inputMethod.getReturnType().equals(Integer.class)) {
			
			symbolType = SymbolType.Integer;
			inputSymbol = new DecimalInputSymbolImpl(input,
				Conversions.getDecimalConversion(Integer.class),
				new String[] { },
				container.getSymbols(agentName),
				container.getDebugMessages());
		}		
		else if(inputMethod.getReturnType().equals(Float.class)) {
			
			symbolType = SymbolType.Float;
			inputSymbol = new DecimalInputSymbolImpl(input,
				Conversions.getDecimalConversion(Float.class),
				new String[] { },
				container.getSymbols(agentName),
				container.getDebugMessages());
		}
		else if(inputMethod.getReturnType().equals(Long.class)) {
			
			symbolType = SymbolType.Long;
			inputSymbol = new DecimalInputSymbolImpl(input,
				Conversions.getDecimalConversion(Long.class),
				new String[] { },
				container.getSymbols(agentName),
				container.getDebugMessages());
		}
		else { // enum or shit
			
			Enumeration enumeration = container.getEnumeration(agentName,
					inputMethod.getReturnType().getSimpleName());
			
			if(enumeration != null) {
				
				symbolType = SymbolType.Enumeration;
				inputSymbol = new EnumeratedInputSymbolImpl(enumeration, 
					input,
					Conversions.getEnumeratedConversion(inputMethod.getReturnType()),
					new String[] { },
					container.getSymbols(agentName),
					container.getDebugMessages());
			}
			else {
				
				throw new IllegalArgumentException(illegalArgumentMessage + inputMethod.getReturnType().getSimpleName());
			}
		}

		return new InputSymbolBinding(stateMachineMethodName, inputSymbol, symbolType);
	}
		
	public static OutputSymbolBinding createOutputSymbolBinding(Object outputInputMethodSoure,	
			String outputMethodName,
			String inputMethodName,
			String stateMachineMethodName,
			String agentName,
			MachineEngine container) throws NoSuchMethodException, SecurityException, IllegalArgumentException {
		
		Method inputMethod = outputInputMethodSoure.getClass().getMethod(inputMethodName);
		Input input = new InputFromMethod(inputMethod, outputInputMethodSoure);
		
		Method outputMethod = outputInputMethodSoure.getClass().getMethod(outputMethodName);
		Output output = new OutputToSetterMethod(outputMethod, outputInputMethodSoure);
			
		OutputSymbolImpl outputSymbol = null;
		SymbolType symbolType = SymbolType.Unkown;
		
		if(inputMethod.getReturnType().equals(Boolean.class)) {
				
			symbolType = SymbolType.Boolean;
			outputSymbol = new BooleanOutputSymbolImpl(output,
				input,
				Conversions.getBooleanConversion(Boolean.class),
				container.getDebugMessages());
						
		}
		else if(inputMethod.getReturnType().equals(Double.class)) {
			
			symbolType = SymbolType.Decimal;
			outputSymbol = new DecimalOutputSymbolImpl(output,
				input,
				Conversions.getDecimalConversion(Double.class),
				container.getDebugMessages());
		}
		else if(inputMethod.getReturnType().equals(Integer.class)) {
			
			symbolType = SymbolType.Integer;
			outputSymbol = new DecimalOutputSymbolImpl(output,
				input,
				Conversions.getDecimalConversion(Integer.class),
				container.getDebugMessages());
		}		
		else if(inputMethod.getReturnType().equals(Float.class)) {
			
			symbolType = SymbolType.Float;
			outputSymbol = new DecimalOutputSymbolImpl(output,
				input,
				Conversions.getDecimalConversion(Float.class),
				container.getDebugMessages());
		}
		else if(inputMethod.getReturnType().equals(Long.class)) {
			
			symbolType = SymbolType.Long;
			outputSymbol = new DecimalOutputSymbolImpl(output,
				input,
				Conversions.getDecimalConversion(Long.class),
				container.getDebugMessages());
		}
		else { // enum or shit
			
			Enumeration enumeration = container.getEnumeration(agentName,
					inputMethod.getReturnType().getSimpleName());
			
			if(enumeration != null) {
				
				symbolType = SymbolType.Enumeration;
				outputSymbol = new EnumeratedOutputSymbolImpl(enumeration, 
					output,
					input,
					Conversions.getEnumeratedConversion(inputMethod.getReturnType()),
					container.getDebugMessages());
			}
			else {
				
				throw new IllegalArgumentException(illegalArgumentMessage + inputMethod.getReturnType().getSimpleName());
			}
		}

		return new OutputSymbolBinding(stateMachineMethodName, outputSymbol, symbolType);
	}

	public static BehaviorBinding createBehaviorBinding(Object behaviorMethodSource,
			String behaviorMethodName,
			String stateMachineMethodName,
			String agentName,
			MachineEngine container) throws NoSuchMethodException, SecurityException {
		
		Method behaviorMethod = behaviorMethodSource.getClass().getMethod(behaviorMethodName);

		BasicBehavior behavior = new MethodBasicBehavior(stateMachineMethodName,
				behaviorMethod,
				new String[] { },
				behaviorMethodSource,
				container.getSymbols(agentName),
				container.getDebugMessages());
		
		return new BehaviorBinding(behavior);
	}
}
