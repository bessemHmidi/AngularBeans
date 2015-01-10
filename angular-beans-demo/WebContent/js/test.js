var app=angular.module("angularApplication",[]).run(function($rootScope){$rootScope.sessionUID="b112249a-0fc6-4dff-830a-49f97a4fd2b9";
});app.controller("booksController",function($rootScope,$scope,$http,$location,logger,wsocketRPC){wsocketRPC.subscribe($scope,"channel1");
$scope.otherBook={isbn:"001",title:"blablabla",free:true,pages:0};$scope.categories=["History","Science"];
$scope.allBooks=[];$scope.pages=0;$scope.clear=function(){var params={};params.title=$scope.title;
params.isbn=$scope.isbn;params.free=$scope.free;params.pages=$scope.pages;params.selectedCategorie=$scope.selectedCategorie;
params.otherBook=$scope.otherBook;$http.get("./rest/invoke/service/booksController/clear/json?params="+encodeURI(JSON.stringify(params))).success(function(data){$scope.allBooks=data[1];
logger.log(data[0]);});};$scope.findByIsbn=function(){var params={};params.isbn=$scope.isbn;
$http.get("./rest/invoke/service/booksController/findByIsbn/json?params="+encodeURI(JSON.stringify(params))).success(function(data){$scope.bookFound=data[1];
$scope.title=data[2];$scope.pages=data[3];$scope.free=data[4];$scope.selectedCategorie=data[5];
$scope.allBooks=data[6];logger.log(data[0]);});};$scope.testSock=function(){var params={};
params.title=$scope.title;params.isbn=$scope.isbn;params.free=$scope.free;params.pages=$scope.pages;
params.selectedCategorie=$scope.selectedCategorie;params.otherBook=$scope.otherBook;
wsocketRPC.call($scope,"booksController.testSock",params);};$scope.about=function(){var params={};
$http.get("./rest/invoke/service/booksController/about/json?params="+encodeURI(JSON.stringify(params))).success(function(data){window.location=data[1];
});};$scope.addBook=function(){var params={};params.title=$scope.title;params.isbn=$scope.isbn;
params.free=$scope.free;params.pages=$scope.pages;params.selectedCategorie=$scope.selectedCategorie;
params.otherBook=$scope.otherBook;$http.get("./rest/invoke/service/booksController/addBook/json?params="+encodeURI(JSON.stringify(params))).success(function(data){$scope.allBooks=data[1];
$scope.title=data[2];logger.log(data[0]);});};});app.controller("chatController",function($rootScope,$scope,$http,$location,logger,wsocketRPC){wsocketRPC.subscribe($scope,"chatChannel");
$scope.receivedMesssage="";$scope.newMessage="";$scope.send=function(){var params={};
params.newMessage=$scope.newMessage;wsocketRPC.call($scope,"chatController.send",params);
};});app.service("wsocketRPC",function(logger,$rootScope){var ws=new WebSocket("ws://localhost:8080/angular-bridge-demo/ws-service");
this.rootScope=$rootScope;var wsocketRPC=this;var reqId=0;var scopes=[];var refScope="";

ws.onopen=function(evt){var message={reqId:0,session:wsocketRPC.rootScope.sessionUID,service:"ping",method:"ping",params:"nada"};
wsocketRPC.send(message);};
ws.onmessage=function(evt){var msg=JSON.parse(evt.data);
logger.log(msg.log);var elementPos=scopes.map(function(x){return x.id;}).indexOf(msg.reqId);
refScope=scopes[elementPos].scope;refScope.received=msg;for(var key in msg){if(refScope.hasOwnProperty(key)){refScope[key]=msg[key];
}}refScope.$apply();if(msg.isRPC){wsocketRPC.unsubscribe(msg.reqId,refScope);}};




this.send=function(message){ws.send(JSON.stringify(message));
};this.subscribe=function(rfc,id,isRPC){refScope=rfc;scopes.push({id:id,scope:rfc,isRPC:isRPC});
};this.unsubscribe=function(id,rfc){for(var i=scopes.length-1;i>=0;i--){if((scopes[i].id===id)&&(scopes[i].scope===rfc)){scopes.splice(i,1);
}}};this.call=function(rfc,invockation,params){reqId++;wsocketRPC.subscribe(rfc,reqId,true);
var message={reqId:reqId,session:wsocketRPC.rootScope.sessionUID,service:invockation.split(".")[0],method:invockation.split(".")[1],params:params};
wsocketRPC.send(message);};});app.service("logger",function(){this.log=function(logMessages){for(var i in logMessages){var message=logMessages[i].message;
var level=logMessages[i].level;if(level==="info"){console.info(message);}if(level==="error"){console.error(message);
}if(level==="warn"){console.warn(message);}if(level==="debug"){console.debug(message);
}}};});app.directive("uiTemplate",function(){return{compile:function(tElem,attrs){return function(scope,elem,attrs){sessionStorage.setItem("URL",document.URL);
window.location=attrs.uiTemplate;};}};}).directive("uiInsert",function($compile){return{compile:function(tElem,attrs){return function(scope,elem,attrs,compile){elem.html("");
var addon=sessionStorage.getItem(attrs.uiInsert);elem.append($compile(addon)(scope));
window.history.pushState("","",localStorage.getItem("URL"));};}};}).directive("uiDefine",function(){return{compile:function(tElem,attrs){tElem.attr("hidden","true");
return function(scope,elem,attrs){localStorage.setItem(attrs.uiDefine,elem.html());
};}};});app.service("bundleService",function($http,$rootScope,$timeout){this.loadBundle=function(bundleName,aleas){$http.get("resources/"+bundleName).success(function(data){$rootScope[aleas]=data;
});};});