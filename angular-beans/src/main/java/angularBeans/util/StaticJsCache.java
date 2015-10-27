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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * A cache for the static (non beans instances dependent) angular-beans.js code
 * 
 * @author bassem Hmidi
 *
 */

public class StaticJsCache {

	final static String scriptDetection = "var sript_origin=((document.scripts[document.scripts.length-1].src).replace('angular-beans.js',''));";

	/**
	 * the angularBeansMainObject is the angularBeans object in the angularBeans
	 * javascript api
	 */
	public final static String angularBeansMainObject = scriptDetection
			+ "function AngularEvent(data,dataClass){"
		
		+"if(dataClass!='String' && dataClass){"
		+ "this.dataClass=dataClass;this.data=JSON.stringify(data);"
		+ "}"
		+"else{this.data=data}"
		+"};"
		
		+ "var angularBeans={ "

			// + "fire:function(eventName,data){"
			// + "scope[service.serviceID]=service;"
			// + "},"

			+ "bind:function(scope,service,modelsName){"

			+ "scope[service.serviceID]=service;"

			+ "for (i in modelsName){"

			+ "modelsName[i]=service.serviceID+'.'+modelsName[i];"
			+ "}"

			+ "scope.$watch(angular.toJson(modelsName).split('\\\"').join(''), function (newValue, oldValue) {"

			+ "for (i in modelsName){"
			+ "scope[modelsName[i].split(service.serviceID+'.')[1]]=newValue[i];} "

			+ "}, true);"

			+ "}"

			+ ",addMethod :function(object,name,fn){"

			+ "if (object['$ab_fn_cache']==null){object['$ab_fn_cache']=[]; }"

			+ "  if((object['$ab_fn_cache'][name])==undefined){object['$ab_fn_cache'][name]=[];}"

			+ " var index= object['$ab_fn_cache'][name].length;"

			+ "object['$ab_fn_cache'][name][index]=fn;"

			+ "object[name]=function  (){"

			+ "  for (index in object['$ab_fn_cache'][name]){"

			+ "      var actf=object['$ab_fn_cache'][name][index];"

			+ "     if(arguments.length==actf.length){"

			+ "        return actf.apply(object,arguments);"

			+ "      } }};"

			+ "}"
 
			// *
			+ ",isIn:function(array,elem){var found=false;"
			+ "for(item in array){"
			+ "if(this.isSame(array[item],elem)){found =true;return item;}"
			+ "}"
			+ "return -1;}"

			+ ",isSame:function(item1,item2){"

			+ "var same=true;" 

			
			+ "for(prop in item1){"

			+ "if(prop=='$$hashKey'){continue;}"
			+ "if (item1[prop] instanceof String){if(item1[prop].startsWith('lob/')){continue;}}"
			// typeof item1[prop] == 'string' ||

			+ "if(!(angular.toJson(item1[prop])===angular.toJson(item2[prop]))){"
			
			+ "same=false;break;}"

			+ "}"

			+ "return same;}"

			+ " };";

	public static StringBuilder CORE_SCRIPT = new StringBuilder();
	public static Map<Class, StringBuffer> CACHED_BEAN_STATIC_PART = new HashMap<Class, StringBuffer>();
	public static StringBuilder EXTENTIONS_SCRIPT = new StringBuilder();
	public static StringBuilder VALIDATION_SCRIPT = new StringBuilder();

}
