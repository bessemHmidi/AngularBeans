package org.espritjug.angularBridge.boot;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

/**
 * Error log class For more information visit
 * http://technology.amis.nl/blog/?p=2392
 * 
 * @author Jeroen van Wilgenburg, AMIS Services BV
 * @version 0.1a - 9 September 2007
 * 
 */
public class CompressorErrorReporter implements ErrorReporter {
	private static final Logger log = Logger
			.getLogger("CompressorFilterErrorReporter");

	public void warning(String message, String sourceName, int line,
			String lineSource, int lineOffset) {
		if (line < 0) {
			log.log(Level.WARNING, message);
		} else {
			log.log(Level.WARNING, line + ':' + lineOffset + ':' + message);
		}
	}

	public void error(String message, String sourceName, int line,
			String lineSource, int lineOffset) {
		if (line < 0) {
			log.log(Level.SEVERE, message);
		} else {
			log.log(Level.SEVERE, line + ':' + lineOffset + ':' + message);
		}
	}

	public EvaluatorException runtimeError(String message, String sourceName,
			int line, String lineSource, int lineOffset) {
		error(message, sourceName, line, lineSource, lineOffset);
		return new EvaluatorException(message);
	}
}
