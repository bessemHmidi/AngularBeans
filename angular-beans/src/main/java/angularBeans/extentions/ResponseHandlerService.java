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
package angularBeans.extentions;

import javax.inject.Inject;

import angularBeans.api.NGRedirect;
import angularBeans.boot.ModuleGenerator;

@NGExtention
public class ResponseHandlerService implements Extention {

	@Inject
	ModuleGenerator generator;

	@Override
	public String render() {

		
		String result = "";
		
		result += "app.service('responseHandler',['logger','$rootScope',function(logger,$rootScope){\n";

		
		result +=("\nthis.handleResponse=function(msg,scope,redirect){");
		result +=("\nfor (var key in msg) {");
		result+=("\nif(key==='rootScope'){");

		result+=("\nfor(var model in msg[key]){");

		result+=("\n$rootScope[model]=msg['rootScope'][model];");

		result+=("\n}");
		result+=("}");
		result+=("else{");
		result+=("\nscope[key]=msg[key]");
		result+=("\n  }");
		result+=("\n  }");

		// result+=("\n$scope.$apply();");

		result+=("\nlogger.log(msg.log);");
		
		result+=("\nif (redirect){");
			//result+=("\nlogger.log(msg.location);");
			result+=("window.location = msg.location;");
			
			result+=("}");
			
		result+=("};");

		result+=("}]);\n");

		return result;
	}

}
