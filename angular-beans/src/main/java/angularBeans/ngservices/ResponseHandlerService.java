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
package angularBeans.ngservices;

import javax.inject.Inject;

import angularBeans.api.NGRedirect;
import angularBeans.boot.ModuleGenerator;

@NGExtention
public class ResponseHandlerService implements NGService {

	@Inject
	ModuleGenerator generator;

	@Override
	public String render() {

		String result = "";

		result += "app.service('responseHandler',['logger','$rootScope','$location','$filter',function(logger,$rootScope,$location,$filter){\n";

		result += ("\nthis.handleResponse=function(msg,caller){");

		
		result+="var mainReturn={};";
		result += "var isRedirect=false;";
		result += "if(msg.hasOwnProperty('location')){isRedirect=true;}";

		// result += "\nfor (var rs in scopes){";

		result += ("\nif (isRedirect){");
		// result+=("\nlogger.log(msg.location);");
		result += ("$location.path(msg.location);");

		result += ("}");

		


		result += ("\nfor (var key in msg) {");
		result += ("\nif(key==='rootScope'){");

		result += ("\nfor(var model in msg[key]){");

		result += ("\n$rootScope[model]=msg['rootScope'][model];");

		result += ("\n}");
		result += ("}");

		// arrays

		// result+=("else{");

//		result += ("\nif(key==='arrays'){");
//
//		result += ("\nfor(var modelkey in msg[key]){");
//
//		// result+="alert(modelkey);";
//		//
//		// result+="alert(JSON.stringify(msg[key][modelkey]));";
//
//		result += "if (!caller.hasOwnProperty(modelkey)){"
//				+ "caller[modelkey]=[]; }";
//
//		result += "var tab=msg[key][modelkey];";
//		result += "for (var value in tab){";
//		result += "caller[modelkey].push(tab[value]);";
//		// result+="alert(tab[value]);";
//		result += ("\n}");
//
//		// result+="alert(JSON.stringify(scope[modelkey]));";
//		result += ("}");
//
//		result += ("\n  }");

		// --------------------------------------------------------------------

//		result += ("\nif(key==='rm'){");
//
//		result += ("\nfor(var modelkey in msg[key]){");
//
//		//
//		// result+="alert(JSON.stringify(msg[key][modelkey]));";
//
//		result += "if (!caller.hasOwnProperty(modelkey)){}";
//			//	+ "scope[modelkey]={}; }";
//
//		result += "var tab=msg[key][modelkey];";
//
//		result += "var equalsKey='';";
//
//		result += "for (var value in tab){";
//		result += "if (typeof tab[value] == 'string' || tab[value] instanceof String){";
//		result += "if(tab[value].indexOf('equalsKey:') > -1){tab[value]=tab[value].replace('equalsKey:','');equalsKey=tab[value];continue;}";
//		result += "};}";
//
//		result += "for (var value in tab){";
//		result += "if(tab[value]==equalsKey){continue;}";
//
//		
////		result += "scope[modelkey] = scope[modelkey].filter(function(it) {";
////		//result += "alert(angular.toJson(it[equalsKey]) );";
////		result += "    return !(it[equalsKey] === tab[value][equalsKey]);";
////		result += "});";
//	
//		result += "var criteria={};";
//		result += "criteria[equalsKey]='!'+tab[value][equalsKey];";
//		result += "caller[modelkey] = $filter('filter')(caller[modelkey], criteria);";
//
//		
//		result += ("\n}");
//
//		// result+="alert(JSON.stringify(scope[modelkey]));";
//		result += ("}");
//
//		result += ("\n  }");
		
		
		
		// result+="alert(JSON.stringify(msg));";
		
		
		//-------------------------------------------------------------
		
		result += ("\nif((key==='add')||(key==='rm')){");
		
		
		result += "var equalsKey='--';";
		
		result += ("\nfor(var modelkey in msg[key]){");
		//
		
	
		
		result += "if (!(angular.isDefined(caller[modelkey]))){"
				+ "caller[modelkey]=[]; }";

		result += "var tab=msg[key][modelkey];";

		

		result += "for (var value in tab){";
	
		result += "if (typeof tab[value] == 'string' || tab[value] instanceof String){";
		
		result += "if(tab[value].indexOf('equalsKey:') > -1){equalsKey=tab[value].replace('equalsKey:','');break;}}}";
		
		//
		result += "for (var value in tab){";
		
		result+="if(key==='rm'){";
		
		result+="if(equalsKey=='NAN'){";

		result += "for (var item in caller[modelkey]) {";
		result+="if(angular.toJson(caller[modelkey][item])==angular.toJson(tab[value])){caller[modelkey].splice(item, 1);}";
		result+="}";
				
		result+= "}else{";
		result += "var criteria={};";
		result += "criteria[equalsKey]='!'+tab[value][equalsKey];";
		result += "caller[modelkey] = $filter('filter')(caller[modelkey], criteria);";
		result += "}}";
		//
		
		
		result+="if(key==='add'){";
		result+="\n var found=false;";
		result += "for (var item in caller[modelkey]) {";
		result+="if(angular.toJson(caller[modelkey][item])==angular.toJson(tab[value])){ found=true;}";
		result+="}";
        result+="if(!(found)){";
		result += "caller[modelkey].push(tab[value]);";
		result += "}};"
	
				
				+ "}";

		result += ("}");

		result += ("\n  }");

		// --------------------------------------------------------------------

		result += ("if(!(key in ['rootScope','add','mainReturn','rm'])){");
		result += ("\ncaller[key]=msg[key];");
		result += ("\n  }");


		result+="if (key==='mainReturn'){"
				+"if(msg[key].hasOwnProperty('boundTo')){"
				+ "mainReturn=msg[msg[key].boundTo];"
				
				+"console.log(''+mainReturn);"
				+ "}else{"
				
				+ "mainReturn=msg[key];}}";
		
		// result+=("\n  }");

		result += ("\n  }");

		
		result += ("\nlogger.log(msg.log);");

		// -->

		
		result += ("return mainReturn;");
		
		
		result += ("};");

		result += ("}]);\n");

		return result;
	}

}
