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

import java.util.logging.Logger;

import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.VariableRenamingPolicy;

/**
 * A closure compiler utility class used buy AngularBeans to "minify" the
 * generated angular-beans.js script
 * 
 * @author Bassem Hmidi
 * @author Aymen Naili
 *
 */
public final class ClosureCompiler {

	
	private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());
	private final CompilerOptions options;
	
	private static final Object lock = new Object();

	public ClosureCompiler() {
		//We have to ensure a safe concurrency as ModuleGenerator is session-scoped and
		//multiple sessions can generate the script at the same time.
		synchronized (lock) {
			this.options = new CompilerOptions();
			CompilationLevel.WHITESPACE_ONLY.setOptionsForCompilationLevel(options);
			options.setVariableRenaming(VariableRenamingPolicy.OFF);
			options.setAngularPass(true);
			options.setTightenTypes(false);
			options.prettyPrint = false;
		}
	}
	
	public ClosureCompiler(CompilerOptions options) {
		this.options = options;
	}

	public String getCompressedJavaScript(String jsContent) {
		String compiled = jsContent;
		try {
			compiled = compile(jsContent);
		} catch (Exception e) {
			logger.warning("could not compress JS, compression disabled, check for error or your guava library version. Cause:" + e.getMessage());
		}

		return compiled.replace("delete_to_replace_by_just_delete", "delete");
	}

	public String compile(String code) {
		Compiler compiler = new Compiler();
		compiler.disableThreads();
		
		SourceFile extern = SourceFile.fromCode("externs.js","function alert(x) {}");
		SourceFile input = SourceFile.fromCode("input.js", code);
		
		compiler.compile(extern, input, options);
		return compiler.toSource();
	}
}
