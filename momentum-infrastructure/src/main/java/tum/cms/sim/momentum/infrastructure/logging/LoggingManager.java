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

package tum.cms.sim.momentum.infrastructure.logging;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.ArrayList;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;

import tum.cms.sim.momentum.configuration.execution.LoggingConfiguration;
import tum.cms.sim.momentum.configuration.execution.LoggingStateConfiguration;
import tum.cms.sim.momentum.configuration.execution.LoggingStateConfiguration.LoggingLevel;
import tum.cms.sim.momentum.infrastructure.exception.BadConfigurationException;
import tum.cms.sim.momentum.utility.generic.IUnique;

/**
 * LoggingManager creates loggers and enables access to logging via static methods
 * There is always a console logger activate, if not defined in the console otherwise.
 * Multiple file loggers can be added.
 * 
 * Example from basicSetup.xml 
 * levels = Trace | Debug | User | None 
 * <logging>
 * 	<loggingState type="Console" level="User"/>
 * 	<loggingState type="File" level="Debug">
 * 		<folder name="C:\Users\Desktop\logs" />
 * 	</loggingState>
 * </logging>
 * 
 * @author Peter M. Kielar
 */
public class LoggingManager {

	private static ArrayList<Logger> loggers = new ArrayList<>();
	private LoggingManager() {}
	/**
	 * The logging manager uses the configuration to create loggers
	 * 
	 * @param loggingConfiguration
	 * @throws BadConfigurationException 
	 */
	public static void setupLoggingManager(LoggingConfiguration loggingConfiguration)
			throws BadConfigurationException {
	
		LoggingManager.logDebug(LoggerStrings.LogEntry);
		boolean doesConsoleLoggerExitsts = false;
	    int nameIter = 0;
	    
	    if(loggingConfiguration == null || loggingConfiguration.getLoggingStates().isEmpty()) {
	    	
	    	loggers.add(Logger.getRootLogger());
	    }
	    else {
	    	
	    	for(LoggingStateConfiguration loggingStateConfiguration : loggingConfiguration.getLoggingStates()) {
				
				switch(loggingStateConfiguration.getType()) {
				
				case File: // Create a file logger
					
					if(Files.exists(new File(loggingStateConfiguration.getFolder()).toPath(), LinkOption.NOFOLLOW_LINKS)) {
						
						throw new BadConfigurationException(String.format(LoggerStrings.BadConfigurationFolder,
								loggingStateConfiguration.getFolder()));
					}
					
					String name = String.valueOf(nameIter++) + LoggerStrings.FileLoggingName + LoggerStrings.FileLoggingEnd;
					FileAppender fileAppender = new FileAppender();
					fileAppender.setName(name);
					fileAppender.setFile(loggingStateConfiguration.getFolder() + File.pathSeparator + name);
					fileAppender.setLayout(new PatternLayout(LoggerStrings.LogPattern));
					fileAppender.setAppend(false);
					fileAppender.activateOptions();
					
					Logger.getRootLogger().addAppender(fileAppender);		
					LoggingManager.loggers.add(Logger.getLogger(name));
					LoggingManager.logUser(LoggerStrings.LogCreate, name);
					
					break;
					
				case Console: // Update the root logger (console logger)
				default:
					
					if(!doesConsoleLoggerExitsts) {
			
						Logger consoleAppender = Logger.getLogger(LoggerStrings.ConsoleLoggerName);
						consoleAppender.setLevel(translateLogLevel(loggingStateConfiguration.getLevel()));
						LoggingManager.logUser(LoggerStrings.LogCreate, LoggerStrings.ConsoleLoggerName);
						
						if(loggingStateConfiguration.getLevel() == LoggingLevel.None) {
							
							LoggingManager.logUser(LoggerStrings.LogOff);
						}
						
						// the logger is the root logger (stdout), see log4j.properties
						// We change the log level if a console tag exist in the simulator configuration
						// Only one console logger
						doesConsoleLoggerExitsts = false;
						LoggingManager.loggers.add(consoleAppender);
					}
			
					break;
				}

			}
	    }
	}

	/**
	 * Momentum provides three log levels.
	 * The log4j info level is the momentum user level.
	 * The log4j debug level is the momentum debug level.
	 * The log4j off level is the momentum none level.
	 * Because basic console info logging is always present, off is needed.
	 * 
	 * @param level, the momentumv2 logging level
	 * @return the log4j logging level
	 */
	private static Level translateLogLevel(LoggingLevel level) {
		
		Level levelLog4j = Level.INFO;
		
		if(level != null) {
			
			switch (level) {
			
			case Trace:
				levelLog4j = Level.TRACE;
				break;
				
			case Debug:
				levelLog4j = Level.DEBUG;
				break;
				
			case None:
				levelLog4j = Level.OFF;
				break;
				
			case User:
			default:
				levelLog4j = Level.INFO;
				break;
			}
		}
		return levelLog4j;
    }
	
	public static void logTrace(String message) {
		
		if(message != null) {
			
			LoggingManager.loggers.forEach(logger -> logger.trace(message));			
		}
	}
	
	public static void logTrace(String message, Throwable throwable) {
		
		if(message != null) {
			
			LoggingManager.loggers.forEach(logger -> logger.trace(message, throwable));			
		}
	}
	
	public static void logTrace(String message, Object... formatContent) {
		
		if(message != null || formatContent != null) {
			
			LoggingManager.loggers.forEach(logger -> logger.trace(String.format(message, formatContent)));			
		}
	}
	
	/**
	 * Debug level logging
	 * 
	 * @param message
	 */
	public static void logDebug(String message) {
		
		if(message != null) {
			
			LoggingManager.loggers.forEach(logger -> logger.debug(message));			
		}
	}
	
	/**
	 * Debug level logging
	 * 
	 * @param message
	 */
	public static void logDebug(IUnique caller, String message) {
		
		if(caller != null &&message != null) {
			
			LoggingManager.loggers.forEach(logger -> logger.debug(
					caller.getClass().getSimpleName() + " " +
					caller.getName() + " " +
					caller.getId() + " " + message));			
		}
	}
	
	/**
	 * Debug level logging for exception messages.
	 * 
	 * @param message
	 * @param throwable, 
	 */
	public static void logDebug(String message, Throwable throwable) {
		
		if(message != null) {
			
			LoggingManager.loggers.forEach(logger -> logger.debug(message, throwable));			
		}
	}
	
	/**
	 * Debug level logging with format.
	 * 
	 * @param message, includes at least a single %s format element
	 * @param formatContent, text to put into message
	 */
	public static void logDebug(String message, Object... formatContent) {
		
		if(message != null && formatContent != null) {
			
			LoggingManager.loggers.forEach(logger -> logger.debug(String.format(message, formatContent)));			
		}
	}
	
	public static void logUser(String message) {
		
		if(message != null) {
			
			LoggingManager.loggers.forEach(logger -> logger.info(message));			
		}
	}
	/**
	 * Info level (user level) logging for model based messages.
	 * 
	 * @param message
	 * @param throwable, 
	 */
	public static void logUser(IUnique caller, String message) {
		
		if(caller != null && message != null) {
			
			LoggingManager.loggers.forEach(logger -> logger.info(
				caller.getClass().getSimpleName() + " " +
				caller.getName() + " " +
				caller.getId() + " " +
				message));
		}
	}
	/**
	 * Info level (user level) logging for generic exception messages.
	 * 
	 * @param message
	 * @param throwable, 
	 */
	public static void logUser(IUnique caller, Throwable throwable) {
		
		if(caller != null && throwable.getStackTrace() != null && throwable.getStackTrace().length > 0) {
			
			LoggingManager.loggers.forEach(logger -> logger.info(
					caller.getClass().getSimpleName() + " " +
					caller.getName() + " " +
					caller.getId() + " "));		
				
			LoggingManager.loggers.forEach(logger -> {
				for(int iter = 0; iter < throwable.getStackTrace().length; iter++) {
					logger.info(throwable.getStackTrace()[iter]);
				}
			});
		}
	}
	/**
	 * Info level (user level) logging for exception messages.
	 * 
	 * @param message
	 * @param throwable, 
	 */
	public static void logUser(String message, Throwable throwable) {
		
		if(message != null && throwable.getStackTrace() != null && throwable.getStackTrace().length > 0) {
			
			LoggingManager.loggers.forEach(logger -> logger.info(message));		
			LoggingManager.loggers.forEach(logger -> {
				for(int iter = 0; iter < throwable.getStackTrace().length; iter++) {
					logger.info(throwable.getStackTrace()[iter]);
				}
			});
		}
	}
	
	public static void logUser(Throwable throwable) {
		
		if(throwable.getStackTrace() != null && throwable.getStackTrace().length > 0) {
					
			LoggingManager.loggers.forEach(logger -> logger.info(throwable.getMessage()));
			LoggingManager.loggers.forEach(logger -> {
				for(int iter = 0; iter < throwable.getStackTrace().length; iter++) {
					logger.info(throwable.getStackTrace()[iter]);
				}
			});
		}
	}
	/**
	 * Info level (user level) logging with format.
	 * 
	 * @param message, includes at least a single %s format element
	 * @param formatContent, text to put into message
	 */
	public static void logUser(String message, Object... formatContent) {
		
		if(message != null || formatContent != null) {
			
			LoggingManager.loggers.forEach(logger -> logger.info(String.format(message, formatContent)));			
		}
	}

	/**
	 * Wraps system.out.print to decouple.
	 * 
	 * @param printText
	 */
	public static void print(String printText) {

		System.out.print(printText);
	}
	
	/**
	 * Wraps resources and print to decouple.
	 * 
	 * @param printText
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	public static void printResourceText(URL resourceURL) throws IOException, URISyntaxException {
		
		InputStream inputStream = resourceURL.openStream();
		BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		int result;
		
		while((result = bufferedInputStream.read()) != -1) {
			
			byteArrayOutputStream.write((byte) result);
		}
		
		String printText = byteArrayOutputStream.toString(StandardCharsets.UTF_8.name());
		System.out.println(printText);
	}
	
	/**
	 * Initialize basic logging
	 * @throws IOException 
	 */	
	public static void initialize(URL configurationFile) {
				
		PropertyConfigurator.configure(configurationFile);
	}

}
