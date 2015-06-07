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
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.SourceFile;

@WebServlet(urlPatterns = "/angular-beans.js")
public class BootServlet extends HttpServlet {

	@Inject
	ModuleGenerator generator;
	@Inject
	Logger log;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		generator.setHTTPRequest(req);
		long startTime = System.currentTimeMillis();
		resp.setContentType("text/javascript");
		StringWriter stringWriter = new StringWriter();
		generator.getScript(stringWriter);

		String jsContent = stringWriter.toString();

		String compressed = getCompressedJavaScript(jsContent);
		resp.getWriter().write(compressed);

		long endTime = System.currentTimeMillis();
		log.info("Module generated successfully in "+(endTime-startTime)+" ms");
		resp.getWriter().flush();
	}

	private String getCompressedJavaScript(String jsContent) {
		String compiled = "";

		compiled = compile(jsContent, CompilationLevel.SIMPLE_OPTIMIZATIONS);

		// compiled=compile(compiled, CompilationLevel.SIMPLE_OPTIMIZATIONS);
		return compiled;
	}

	public static String compile(String code, CompilationLevel level) {
		Compiler compiler = new Compiler();

		CompilerOptions options = new CompilerOptions();

		options.setAngularPass(true);

		SourceFile extern = SourceFile.fromCode("externs.js",
				"function alert(x) {}");
		SourceFile input = SourceFile.fromCode("input.js", code);
		compiler.compile(extern, input, options);
		return compiler.toSource();
	}

}
