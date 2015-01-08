package org.espritjug.angularBridge.extentions;

import org.espritjug.angularBridge.BridgeExtention;
import org.espritjug.angularBridge.Extention;

@BridgeExtention
public class BundleService implements Extention {

	@Override
	public String render() {
		String result = "";
		result += "app.service(\"bundleService\",function($http,$rootScope,$timeout){"
				+ "this.loadBundle=function(bundleName,aleas){"
				+ " $http.get('resources/'+bundleName).success(function(data){"
				+ " $rootScope[aleas]=data;" + "});};;});";
		return result;
	}

}
