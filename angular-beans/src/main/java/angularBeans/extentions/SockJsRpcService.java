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
import javax.servlet.http.HttpServletRequest;

import angularBeans.boot.ModuleGenerator;

@NGExtention
public class SockJsRpcService implements Extention {

	@Inject
	ModuleGenerator generator;

	@Override
	public String render() {

		HttpServletRequest request = generator.getRequest();

		String serverName = (request.getServerName());
		String portNumber = (String.valueOf(request.getServerPort()));
		String contextPath = (request.getServletContext().getContextPath());

		String webSocketPath = ("http://" + serverName + ":" + portNumber
				+ contextPath + "/ws-service/");

		String result = "";
		
		result += "app.service('wsocketRPC',['logger','$rootScope','$http','responseHandler',function(logger,$rootScope,$http,responseHandler){\n";
//		result += "\nvar ws = new WebSocket(\"" + webSocketPath + "\");";
		
		

		result+="var sockjs_url =\"" + webSocketPath + "\";";
		result+="var ws = new SockJS(sockjs_url, undefined, {debug: true});";
		
		result += "\nthis.rootScope=$rootScope;";
		result += "\nvar wsocketRPC=this;";
		result += "\nvar reqId=0;";
		result += "\nvar scopes=[];";
		result += "\nvar refScope='';";
		result += "\nws.onopen = function (evt)";
		result += "\n{ console.log('session opened!!');";

		result += "\nvar message = {";
		result += "\n'reqId':0,";
		result += "\n'session': wsocketRPC.rootScope.sessionUID,";
		result += "\n'service': 'ping',";
		result += "\n'method': 'ping',";
		result += "\n'params': {'nada':'nada'}";
		result += "\n};";
		result += "\nwsocketRPC.send(message);};";

		result += "\nws.onmessage = function (evt)";
		result += "\n{";
		result += "\nvar msg=JSON.parse(evt.data);";
	//	result += "\nalert(JSON.stringify(msg));";
		//-----------------
		//
		
		//-----------------------
		//result +="scopes=sessionStorage.getItem(\"scopes\");";
		
		
	    //----------------	
		
		result += "\nlogger.log(msg.log);";
		
	//	result += "\nvar elementPos = scopes.map(function(x) {return x.id; }).indexOf(msg.reqId);";
		
		
		result += "\nfor (var rs in scopes){";

	result += 	"if(scopes[rs].id===msg.reqId){";
				
		result+="refScope=scopes[rs].scope;";
			
		
		
	
//		result += "if (!(typeof (scopes[elementPos]) === \"undefined\")){";
//		
//		
//		result += "\nrefScope = scopes[elementPos].scope;";
		
		//result += "\nrefScope.received=msg;";
		
//		result += "\nfor (var key in msg) {";
//		result += "\nrefScope[key]=\"\";}";

	//	result += "\nrefScope.$apply();\nrefScope.$digest();";
		
		result += "\nfor (var key in msg) {";
	
		
		//result += "\nif (refScope.hasOwnProperty(key)) {";
		//result+="refScope[key]='#';refScope.$digest();\nrefScope.$apply();";
		

		
		result += "\nif(key==='rootScope'){";
			
		result += "\nfor(var model in msg[key]){";
		
//		result +="console.log(JSON.stringify(msg['rootScope'][model]));";
		
		result += "\n$rootScope[model]=msg['rootScope'][model];";
				
		result +="console.log(JSON.stringify($rootScope[model]));";
		result += "\n}";
			
		result += "}";
		
		result+="else{";
		result += "\nrefScope[key]=msg[key]";
		result += "\n  }";
		result += "\n  }";
		
		result += "\nrefScope.$apply();"; 
		result +="\nrefScope.$digest();";
   result+="}";
        //
   
   
   
		result += "\nif(msg.isRPC){";
		
		//result += "\nrefScope.$digest();\nrefScope.$apply();";
		
	//	result += "\nwsocketRPC.unsubscribe(msg.reqId,refScope);";
		
		result += "\n}";
		
		result+="if(msg.hasOwnProperty('location')){window.location = msg.location;}";
		
		result += "\n }};"; 

		result += "\nthis.send = function(message) {";
		result += "\nws.send(JSON.stringify(message));";
		result += "\n};";

		result += "\nthis.subscribe=function(rfc,id,isRPC){";
		result += "\nrefScope=rfc;";
		
		
		
		result += "\nscopes.push({'id':id,'scope':rfc,'isRPC':isRPC});";
		//----------------
		//result += "\nsessionStorage.setItem(\"scopes\",scopes);";
		
		//--------------
		
		result += "\n}";

		result += "\nthis.unsubscribe=function(id,rfc){";

		result += "\nfor(var i = scopes.length - 1; i >= 0; i--) {";
		
		result += "\nif((scopes[i].id === id) && (scopes[i].scope === rfc)) {";
		result += "\nscopes.splice(i, 1);";
		result += "\n}}";
		//---------------
		result += "\nsessionStorage.setItem(\"scopes\",scopes);";
		result += "\n}";

		result += "\nthis.call=function(rfc,invockation,params){";
		result += "\nreqId++;";
		
		//result += "\nwsocketRPC.subscribe(rfc,reqId,true);";

		result += "\nvar message = {";
		result += "\n'reqId':reqId,";
		result += "\n'session': wsocketRPC.rootScope.sessionUID,";
		result += "\n'service': invockation.split(\".\")[0],";
		result += "\n'method': invockation.split(\".\")[1],";
		result += "\n'params': params";
		result += "\n};";
		result += "\nwsocketRPC.send(message);";
		result += "\n}}]);";

		return result;
	}

}
