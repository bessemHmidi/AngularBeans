/*
 * AngularBeans, CDI-AngularJS bridge 
 *
 * Copyright (c) 2014, Bessem Hmidi. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 */

/**
 @author Bessem Hmidi
 */
package angularBeans.boot;

import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.javascript.jscomp.CheckLevel;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.Result;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.VariableRenamingPolicy;


@WebServlet(urlPatterns = "/angular-beans.js")
public class BootServlet extends HttpServlet {

	@Inject
	ModuleGenerator generator;
	@Inject
	Logger log;

	String jsContent;

	//String compressed;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		long startTime = System.currentTimeMillis();

		generator.setContextPath(req.getScheme() + "://" + req.getServerName()
				+ ":" + req.getServerPort()
				+ req.getServletContext().getContextPath() + "/");

		resp.setContentType("text/javascript");
		StringWriter stringWriter = new StringWriter();
		generator.getScript(stringWriter);
		jsContent = stringWriter.toString();

		 String compressed = getCompressedJavaScript(jsContent);
		resp.getWriter().write(compressed);

		long endTime = System.currentTimeMillis();
		
		log.info("Module generated successfully in " + (endTime - startTime)
				+ " ms");
		
		
		resp.getWriter().flush();
	} 

	 private String getCompressedJavaScript(String jsContent) {
		 
		 
		 
	 String compiled = jsContent;
	
	 try {
		 compiled = compile(jsContent, CompilationLevel.WHITESPACE_ONLY);
	} catch (Exception e) {
		log.log(Level.WARNING,"could not copmress JS, compression disabled, check for error or your guava library version");
	}
	 
	
	 // compiled=compile(compiled, CompilationLevel.SIMPLE_OPTIMIZATIONS);
	 return compiled;
	 } 
    
	 public static String compile(String code, CompilationLevel level) {
	 Compiler compiler = new Compiler();
	
	
	 
	 CompilerOptions options = new CompilerOptions();
	 

	 CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(options);
	
//	 options.inlineConstantVars=true;
//	 options.aggressiveVarCheck=CheckLevel.WARNING;
//	 options.checkUnreachableCode=CheckLevel.WARNING;
//	 options.aliasAllStrings=true;
//	 options.foldConstants=true;
//	 options.deadAssignmentElimination=true;
//	 options.inlineLocalFunctions=true;
//	 options.coalesceVariableNames=true;
//	 options.aliasKeywords=true;
//	 options.convertToDottedProperties=true;
//	 options.setShadowVariables(true);
//	 options.setChainCalls(true);
//	 options.setConvertToDottedProperties(true);
//	 options.setFoldConstants(true);
//	 options.setAggressiveRenaming(true);
//	
//	 options.optimizeArgumentsArray=true;
//	 options.optimizeCalls=true;
//	 options.optimizeParameters=true;
//	 options.optimizeReturns=true;
//	 
//	 options.setMoveFunctionDeclarations(true);
//	 options.setManageClosureDependencies(true);
//
//	 options.setDevirtualizePrototypeMethods(true);
	 
	 options.setVariableRenaming(VariableRenamingPolicy.OFF);
	 options.setAngularPass(false);
	 options.setTightenTypes(false);
	 options.prettyPrint=false;
	 
	 
	 compiler.disableThreads();
	 
	 
	 
	 
	 SourceFile extern = SourceFile.fromCode("externs.js",
	 "function alert(x) {}");
	 SourceFile input = SourceFile.fromCode("input.js", code);
	 Result result=compiler.compile(extern, input, options);
	 
	 
	 
	 return compiler.toSource();
	 }

}
