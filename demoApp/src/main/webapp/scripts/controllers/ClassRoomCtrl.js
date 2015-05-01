'use strict';

/**
 * @ngdoc function
 * @name monSiteApp.controller:AboutCtrl
 * @description
 * # AboutCtrl
 * Controller of the monSiteApp
 */
angular.module('VirtualClassRoomModule')

.controller("ClassRoomsCtrl",function ($scope,classRoomsService,$location) {
		 
	$scope.classRoomsService=classRoomsService;
		 
	
	$scope.join=function(classroom){
		
		classRoomsService.join(classroom).then(function(data){
			
			$location.path(data+"/"+classroom.name);
		});
		
	}
	
	
		    }); 

