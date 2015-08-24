'use strict';

/**
 * @ngdoc function
 * @name monSiteApp.controller:AboutCtrl
 * @description
 * # AboutCtrl
 * Controller of the monSiteApp
 */
angular.module('VirtualClassRoomModule')

.controller("SingleClassRoomCtrl",function ($scope,singleClassRoomService,$location,$routeParams,remoteEventBus) {
	
	angularBeans.bind($scope,singleClassRoomService,["users"]);
		
	$scope.classRoomName=$routeParams.classRoomName;

	singleClassRoomService.getUsers($scope.classRoomName);
	
	
	$scope.busy=function(){
		remoteEventBus.unsubscribe("drawEvent");
	};
	

	$scope.ready=function(){
		remoteEventBus.subscribe("drawEvent");
	};
	
	//#1
	
	
//	singleClassRoomService.getUsers($scope.classRoomName).then(function(data){
//		$scope.users=data;
//	});
	
//	
//	$scope.$on("joinEvent",function(event,data){
//		
//		if(data.classRoom.name===$scope.classRoomName){
//			if(!(angular.toJson($scope.users).indexOf(angular.toJson(data.user)) != -1)){
//			$scope.users.push(data.user);
//			$scope.$apply();
//			}
//		}
//		
//		});
	
	
		    }); 

