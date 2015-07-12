package angularBeans.util;

import java.util.HashMap;
import java.util.Map;

public class StaticJs {

	final static String scriptDetection="var sript_origin=((document.scripts[document.scripts.length-1].src).replace('angular-beans.js',''));";
	public final static String angularBeanMainFunction =scriptDetection+"var angularBeans={ "


//			+ "fire:function(eventName,data){"
//			+ "scope[service.serviceID]=service;"
//			+ "},"
			
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
			+ "if(this.isSame(array[item],elem)){found =true;break;}"
			+ "}"

			+ "return found;}"

			+ ",isSame:function(item1,item2){"

			+ "var same=true;"
			
			+ "for(prop in item1){"
		

			+ "if(prop=='$$hashKey'){continue;}"
			+ "if (item1[prop] instanceof String){if(item1[prop].startsWith('lob/')){continue;}}"
			//typeof item1[prop] == 'string' ||
	
			
			+ "if(!(angular.toJson(item1[prop])==angular.toJson(item2[prop]))){same=false;}"

			+ "}"

			+ "return same;}"

			+ " };";

	
	public static StringBuilder CORE_SCRIPT=new StringBuilder();
	public static Map<Class, StringBuffer> CACHED_BEAN_STATIC_PART = new HashMap<Class, StringBuffer>();
	public static StringBuilder EXTENTIONS_SCRIPT=new StringBuilder();
	public static StringBuilder VLIDATION_SCRIPT=new StringBuilder();
	

	
}
