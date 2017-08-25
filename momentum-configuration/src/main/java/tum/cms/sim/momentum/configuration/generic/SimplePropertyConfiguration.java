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
import java.util.HashMap;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.converters.enums.EnumToStringConverter;

@XStreamAlias("property")
public class SimplePropertyConfiguration {
	
	public enum SimplePropertyType {
		
		Integer,
		Double,
		String,
		File,
		Format,
		Boolean
	}
	
	@SuppressWarnings("rawtypes")
	public static EnumToStringConverter getSimplePropertyTypeConverter() {
		
		HashMap<String, SimplePropertyType> map = new HashMap<>();
		map.put(SimplePropertyType.Integer.toString(), SimplePropertyType.Integer);
		map.put(SimplePropertyType.Double.toString(), SimplePropertyType.Double);
		map.put(SimplePropertyType.String.toString(), SimplePropertyType.String);
		map.put(SimplePropertyType.File.toString(), SimplePropertyType.File);
		map.put(SimplePropertyType.Format.toString(), SimplePropertyType.Format);
		map.put(SimplePropertyType.Boolean.toString(), SimplePropertyType.Boolean);
		
		return new EnumToStringConverter<>(SimplePropertyType.class, map);
	}
	
	@XStreamAsAttribute
	private String name = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@XStreamAsAttribute
	private SimplePropertyType type = null;

	public SimplePropertyType getType() {
		return type;
	}

	public void setType(SimplePropertyType type) {
		
		this.type = type;
	}
	
	@XStreamAsAttribute
	private String value = null;
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String getValueAsString() {
		
		String result = null;
		
		if(this.type == SimplePropertyType.String) {
			
			result = this.value;
		}
		
		return result;
	}
	
	public Integer getValueAsInteger() {
		
		Integer result = null;
		
		if(this.type == SimplePropertyType.Integer) {
			
			if(this.value.equals("Integer.MAX_VALUE")) {
				
				result = Integer.MAX_VALUE;
			}
			else {
				
				result = Integer.parseInt(this.value);
			}
		}
		
		return result;
	}
	
	public Double getValueAsDouble() {
		
		Double result = null;
		
		if(this.type == SimplePropertyType.Double) {
			
			if(this.value.equals("Double.MAX_VALUE")) {
				
				result = Double.MAX_VALUE;
			}
			else {
				
				result = Double.parseDouble(this.value);
			}
		}
		
		return result;
	}
	
	public File getValueAsFile() {
		
		File result = null;
		
		if(this.type == SimplePropertyType.File) {
			
			result = new File(value);
		}
		
		return result;
	}
	
	public FormatString getValueAsFormat() {
		
		FormatString format = null;
		
		if(this.type == SimplePropertyType.Format) {
			
			format = new FormatString(value);
		}
		
		return format;
	}
	
	public Boolean getValueAsBoolean() {
		
		Boolean result = null;
		
		if(this.type == SimplePropertyType.Boolean) {
			
			result = Boolean.parseBoolean(value);
		}
		
		return result;
	}
}
