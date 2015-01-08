package org.espritjug.angularBeans.extentions;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.espritjug.angularBeans.Extention;
import org.espritjug.angularBeans.NGExtention;
import org.espritjug.angularBeans.boot.JavaScriptGenerator;

@NGExtention
public class WSocketRPCService implements Extention {

	@Inject
	JavaScriptGenerator generator;

	@Override
	public String render() {

		HttpServletRequest request = generator.getRequest();

		String serverName = (request.getServerName());
		String portNumber = (String.valueOf(request.getServerPort()));
		String contextPath = (request.getServletContext().getContextPath());

		String webSocketPath = ("ws://" + serverName + ":" + portNumber
				+ contextPath + "/ws-service");

		String result = "";
		
		result += "app.service('wsocketRPC', function(logger,$rootScope,$http){\n";
		result += "\nvar ws = new WebSocket(\"" + webSocketPath + "\");";
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
		
		result += "\nrefScope.received=msg;";
		
		

		result += "\nfor (var key in msg) {";

		result += "\nif (refScope.hasOwnProperty(key)) {";

		result += "\nrefScope[key]=msg[key];";
		result += "\n  }}";
		result += "\nrefScope.$apply();";
        result+="}";
        //
		result += "\nif(msg.isRPC){";
		result += "\nwsocketRPC.unsubscribe(msg.reqId,refScope);";
		result += "\n}";

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
		result += "\nwsocketRPC.subscribe(rfc,reqId,true);";

		result += "\nvar message = {";
		result += "\n'reqId':reqId,";
		result += "\n'session': wsocketRPC.rootScope.sessionUID,";
		result += "\n'service': invockation.split(\".\")[0],";
		result += "\n'method': invockation.split(\".\")[1],";
		result += "\n'params': params";
		result += "\n};";
		result += "\nwsocketRPC.send(message);";
		result += "\n}});";

		return result;
	}

}
