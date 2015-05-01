'use strict';

/**
 * @ngdoc function
 * @name monSiteApp.controller:AboutCtrl
 * @description
 * # AboutCtrl
 * Controller of the monSiteApp
 */
angular.module('VirtualClassRoomModule')

.controller("SingleClassRoomCtrl",function ($scope,singleClassRoomService,$location,$routeParams) {
		
	$scope.singleClassRoomService=singleClassRoomService;
	$scope.classRoomName=$routeParams.classRoomName;
	singleClassRoomService.getUsers($scope.classRoomName).then(function(data){
		$scope.users=data;
	});
	
	$scope.$on("joinEvent",function(event,data){
		
		if(data.classRoom.name===$scope.classRoomName){
			$scope.users.push(data.user);
			$scope.$apply();
		}
		
		});
		    }); 

