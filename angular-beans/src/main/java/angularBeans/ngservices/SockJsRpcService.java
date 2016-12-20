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

package angularBeans.ngservices;

/**
 * A sockJs RPC service wrapper for AngularBeans
 * 
 * @author Bessem Hmidi
 *
 */

@NGExtension
public class SockJsRpcService implements NGService {

	@Override
	public String render() {

		String result = "";


		
		result += "app.provider('realtimeManager',function RTSrvc(){\n";

//********************		
//		var options = {
//		        protocols_whitelist : [ "websocket", "xhr-streaming", "xdr-streaming", "xhr-polling", "xdr-polling", "iframe-htmlfile", "iframe-eventsource", "iframe-xhr-polling" ],
//		        debug : false
//		    };
//***********************
		
		result+="var self=this;";
		result+="self.options={debug : false};";
		
		
		result+="self.setOptions=function(options){self.options=options;};";
		result+="self.webSocketEnabled=function(enabled){if(!enabled){WebSocket = undefined;}};";
		result+="self.$get=function($log,logger,$rootScope,$http,responseHandler,$q,$injector){";
		
		result += "var wsuri =sript_origin.replace(/^http(s?)/,'ws$1') +'rt-service/websocket';";

		result += "var sjsuri = sript_origin +'rt-service/';";
		result += "var ws={};";
		
		result+="if (('WebSocket' in window) && (WebSocket!=undefined)){ws = new WebSocket(wsuri);}else{";
		result += "if (!((typeof SockJS !=='undefined')&&(angular.isDefined(SockJS.constructor)))){"

				+ "console.warn('websocket not supported, use sockJs client to polyfill...');}";

		result += "else{ws = new SockJS(sjsuri, undefined, self.options);}};";

 
		result += "var rt={ready:false,onReadyState:function(fn){"
				
		+"setTimeout(listen,500);"
		+"function listen(){"
		 +"   if(ws.readyState==1){"
		 + "rt.ready=true;"

		       +"fn();"
		 +"   }"
		 +"   else"
		  +"      setTimeout(listen,500);"
		+"}"
				+ "}};";

		result += "\nrt.rootScope=$rootScope;";
		result += "\nvar reqId=0;";
		result += "\nvar callbacks={};";
		result += "\nvar caller='';";
		result += "\nws.onopen = function (evt)";
		result += "\n{ $log.log('%c>> CONNECTING...','color:blue;font-weight: bold');";

		result += "\nvar message = {";
		result += "\n'reqId':0,";
		result += "\n'session': rt.rootScope.sessionUID,";
		result += "\n'service': 'ping',";
		result += "\n'method': 'ping',";
		result += "\n'params': {'nada':'nada'}";
		result += "\n};";
		result += "\nrt.send(message);};";

		result += "\nws.onmessage = function (evt)";
		result += "\n{";
		result += "\nvar msg=angular.fromJson(evt.data);";

		result += "var REQ_ID=parseInt(msg.reqId);";

		result += " if (angular.isDefined(callbacks[REQ_ID])) {";
		result += "    var callback = callbacks[REQ_ID];";
		result += "delete callbacks[REQ_ID];";
		result += "callback.resolve(msg);";
		result += "  }";

		result += " if (angular.isDefined(msg.ngEvent)) {";
		result += "if(msg.ngEvent.name=='modelQuery'){"
				+ "var caller={};"
				+ "$injector.invoke([msg.ngEvent.data, function(icaller){caller=icaller;}]);"
				+ "responseHandler.handleResponse(msg,caller,false);}"
				+ "else{";
		result += "$rootScope.$broadcast(msg.ngEvent.name,msg.ngEvent.data);";
	
		result += " }";
		result+="if(!$rootScope.$$phase) {$rootScope. $digest ;$rootScope.$apply();}";
		result += "} }; ";

		result += "\nrt.sendAsync = function(message) {"
		
		+"setTimeout(listen,500);"
		+"function listen(){"
		 +"   if(ws.readyState==1){"
		+"\nws.send(angular.toJson(message));"
		 +"   }"
		 +"   else"
		  +"      setTimeout(listen,500);"
		+"}";
		
		
		result += "var deferred = $q.defer();";
		result += "callbacks[message.reqId] = deferred;";
		result += "return deferred.promise;";
		result += "\n};";

		result += "\nrt.send = function(message) {";

		result += "\nws.send(angular.toJson(message));";

		result += "\n};";

		result += "\nrt.call=function(caller,invockation,params){";
		result += "\nreqId++;";
		result += "\nvar message = {";
		result += "\n'reqId':reqId,";
		result += "\n'service': invockation.split(\".\")[0],";
		result += "\n'method': invockation.split(\".\")[1],";
		result += "\n'params': params";
		result += "\n};";
		result += "\nreturn rt.sendAsync(message);";
	
		result += "\n}; rt.onReadyState(function(){$log.log('%c>> REALTIME SESSION READY...','color:green;font-weight: bold');}) ;return rt; };});";
		return result;
	}

}
