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
package angularBeans.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.Result;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.VariableRenamingPolicy;

/**
 * A closure compiler utility class used buy AngularBeans to "minify" the
 * generated angular-beans.js script
 * 
 * @author Bassem Hmidi
 *
 */
public class ClosureCompiler {

	Logger logger = Logger.getLogger(ClosureCompiler.class.getSimpleName());

	CompilerOptions options;

	private ClosureCompiler() {

		options = new CompilerOptions();

		CompilationLevel.WHITESPACE_ONLY.setOptionsForCompilationLevel(options);

		// options.inlineConstantVars=true;
		// options.aggressiveVarCheck=CheckLevel.WARNING;
		// options.checkUnreachableCode=CheckLevel.WARNING;
		// options.aliasAllStrings=true;
		// options.foldConstants=true;
		// options.deadAssignmentElimination=true;
		// options.inlineLocalFunctions=true;
		// options.coalesceVariableNames=true;
		// options.aliasKeywords=true;
		// options.convertToDottedProperties=true;
		// options.setShadowVariables(true);
		// options.setChainCalls(true);
		// options.setConvertToDottedProperties(true);
		// options.setFoldConstants(true);
		// options.setAggressiveRenaming(true);
		//
		// options.optimizeArgumentsArray=true;
		// options.optimizeCalls=true;
		// options.optimizeParameters=true;
		// options.optimizeReturns=true;
		//
		// options.setMoveFunctionDeclarations(true);
		// options.setManageClosureDependencies(true);
		//
		// options.setDevirtualizePrototypeMethods(true);

		options.setVariableRenaming(VariableRenamingPolicy.OFF);
		options.setAngularPass(true);
		options.setTightenTypes(false);
		options.prettyPrint = false;

	}

	public String getCompressedJavaScript(String jsContent) {

		String compiled = jsContent;

		try {
			compiled = compile(jsContent);
		} catch (Exception e) {
			logger.log(
					Level.WARNING,
					"could not compress JS, compression disabled, check for error or your guava library version");

			e.printStackTrace();

		}

		// compiled=compile(compiled, CompilationLevel.SIMPLE_OPTIMIZATIONS);
		return compiled.replace("delete_to_replace_by_just_delete", "delete");
	}

	public String compile(String code) {

		Compiler compiler = new Compiler();
		compiler.disableThreads();

		SourceFile extern = SourceFile.fromCode("externs.js",
				"function alert(x) {}");
		SourceFile input = SourceFile.fromCode("input.js", code);
		Result result = compiler.compile(extern, input, options);

		return compiler.toSource();
	}

	private static ClosureCompiler INSTANCE = new ClosureCompiler();

	public static ClosureCompiler getINSTANCE() {
		return INSTANCE;
	}

}
