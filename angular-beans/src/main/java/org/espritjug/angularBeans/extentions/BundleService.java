package org.espritjug.angularBeans.extentions;

import org.espritjug.angularBeans.BridgeExtention;
import org.espritjug.angularBeans.Extention;

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
