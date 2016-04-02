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
package angularBeans.js.cache;

import java.util.HashMap;
import java.util.Map;

import angularBeans.util.ClosureCompiler;

/**
 * <p>
 * A cache for the static (non beans instances dependent) angular-beans.js code
 * The content of this cache can be loaded by an implementation of a {@link StaticJsLoader}.
 * <b>It's mandatory to first load the content of this class before any bean generation. </b>
 * </p>
 *
 * @author bassem Hmidi
 * @author Michael Kulla <info@michael-kulla.com>
 * @author Aymen Naili
 * 
 * @see angularBeans.js.cache.StaticJsLoader
 * @see angularBeans.js.cache.StaticJsCacheFactory
 * @see angularBeans.js.cache.DefaultStaticJsCacheLoader
 */
public class StaticJsCache {

	/**
	 * the angularBeansMainObject is the angularBeans object in the angularBeans
	 * javascript api
	 */
	
	public static void appendToCore(String str){
		CORE_SCRIPT.append(str);
	}
	
	public static void appendToExtensions(String str){
		EXTENTIONS_SCRIPT.append(str);
	}
	
	public static void appendToValidation(String str){
		VALIDATION_SCRIPT.append(str);
	}
	
	public static void Compress(){
		String compressedCoreScript = new ClosureCompiler().getCompressedJavaScript(CORE_SCRIPT.toString());
		String compressedExtensions = new ClosureCompiler().getCompressedJavaScript(EXTENTIONS_SCRIPT.toString());
		String compressedValidation = new ClosureCompiler().getCompressedJavaScript(VALIDATION_SCRIPT.toString());
		CORE_SCRIPT = new StringBuilder(compressedCoreScript);
		EXTENTIONS_SCRIPT = new StringBuilder(compressedExtensions);
		VALIDATION_SCRIPT = new StringBuilder(compressedValidation);
	}


	//TODO clean up dirty naming
	public static StringBuilder CORE_SCRIPT = new StringBuilder();
	public static Map<Class, StringBuffer> CACHED_BEAN_STATIC_PART = new HashMap<>();
	public static StringBuilder EXTENTIONS_SCRIPT = new StringBuilder();
	public static StringBuilder VALIDATION_SCRIPT = new StringBuilder();

}
