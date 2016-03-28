'use strict';

/**
 * @ngdoc function
 * @name monSiteApp.controller:AboutCtrl
 * @description
 * # AboutCtrl
 * Controller of the monSiteApp
 */


angular.module('VirtualClassRoomModule').controller('canvasCtrl', function($scope, canvasService) {

    
	  angularBeans.bind($scope,canvasService);
	  
	  $scope.$on('x:updated', function(event,data){
		  $scope.x=data;
		  $scope.$apply();
		  });

	  $scope.$on('y:updated', function(event,data){
		  $scope.y=data;
		  $scope.$apply();
		  $scope.notifyAllCanvas($scope.x,$scope.y);
		  });
	  
	
	});
