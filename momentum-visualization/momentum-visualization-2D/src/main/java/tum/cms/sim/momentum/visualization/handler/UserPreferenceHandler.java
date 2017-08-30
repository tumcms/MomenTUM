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

package tum.cms.sim.momentum.visualization.handler;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import tum.cms.sim.momentum.visualization.enums.PropertyType;

public class UserPreferenceHandler {

	/**
	 * Returns default value if there is no value assigned to the {@link PropertyType}
	 * @param propertyType
	 * @return the property value
	 */
	public static String loadProperty(PropertyType propertyType) {
		Properties properties = loadPropertiesFromFile();
		if (properties.getProperty(propertyType.toString()).isEmpty()) {
			putProperty(propertyType, PropertyType.getDefaultValue(propertyType));
			return PropertyType.getDefaultValue(propertyType);
		}
		return properties.getProperty(propertyType.toString());
	}
	
	/**
	 * Stores a property in the properties file
	 * @param propertyType
	 * @param value
	 */
	public static void putProperty(PropertyType propertyType, String value) {
		
		Properties properties = loadPropertiesFromFile();
		properties.setProperty(propertyType.toString(), value);

		writeProperties(properties);
	}
	
	/**
	 * Load all stored properties. If there are no stored properties, returns a list with all 
	 * property keys with default values.
	 * @return
	 */
	public static Properties loadPropertiesFromFile() {
		Properties properties = new Properties();
		try {
			FileInputStream inputStream = new FileInputStream(System.getProperty("user.dir")+".properties");
			properties.load(inputStream);
			inputStream.close();
		}
		 catch (IOException e) {
			 
			for(PropertyType type : PropertyType.values()) {
				properties.setProperty(type.toString(), PropertyType.getDefaultValue(type));
				writeProperties(properties);
			}
		}
		
		return properties;
	}

	/**
	 * writes properties in properties file
	 * @param properties
	 */
	private static void writeProperties(Properties properties) {
		
		FileOutputStream outputStream;
		try {
			outputStream = new FileOutputStream(System.getProperty("user.dir")+".properties");
			properties.store(outputStream, null);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
