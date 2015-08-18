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

@NGExtension
public class ResponseHandlerService implements NGService {

	@Override
	public String render() {

		String result = "";

		result += "app.service('responseHandler',['logger','$rootScope','$filter',function(logger,$rootScope,$filter){\n";

		result += ("\nthis.handleResponse=function(msg,caller,isRPC){");

		result += "var mainReturn={};";

		result += ("\nfor (var key in msg) {");
		result += ("\nif(key==='rootScope'){");

		result += ("\nfor(var model in msg[key]){");

		result += ("\n$rootScope[model]=msg['rootScope'][model];");

		result += ("\n}");
		result += ("}");

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

		result += "if(key==='rm'){";

		result += "if(equalsKey=='NAN'){";

		result += "for (var item in caller[modelkey]) {";

		result += "";
		result += "if(angularBeans.isIn(caller[modelkey],tab[value])){caller[modelkey].splice(item, 1);}";
		result += "}";

		result += "}else{";
		result += "var criteria={};";
		result += "criteria[equalsKey]='!'+tab[value][equalsKey];";
		result += "caller[modelkey] = $filter('filter')(caller[modelkey], criteria);";
		result += "}}";

		result += "if(key==='add'){";
		result += "\n var found=false; ";
		// result += "for(item in caller[modelkey]) {";
		result += "if(angularBeans.isIn(caller[modelkey],tab[value])){ found=true;}";
		// result += "};";
		result += "if(!(found)){  ";
		result += "caller[modelkey].push(tab[value]);";
		result += "}};"

		+ "}";

		result += ("}");

		result += ("\n  }");

		// --------------------------------------------------------------------

		result += ("if(!(key in ['rootScope','add','mainReturn','rm'])){");

		result += ("\ncaller[key]=msg[key];");

		result += ("\n  }");

		result += "if ((key==='mainReturn')&&(msg[key])){"
				+ "if(msg[key].hasOwnProperty('boundTo')){"
				+ "mainReturn=msg[msg[key].boundTo];"

				+ "}else{"

				+ "mainReturn=msg[key];}}";

		result += ("\n  }");

		result += ("\nlogger.log(msg.log);");

		// -->

		// result+="if(isRT){$rootScope.$apply();}";
		result += "if(!isRPC){$rootScope.$digest();$rootScope.$apply();}";

		result += ("return mainReturn;");

		result += ("};");

		result += ("}]);\n");

		return result;
	}

}
