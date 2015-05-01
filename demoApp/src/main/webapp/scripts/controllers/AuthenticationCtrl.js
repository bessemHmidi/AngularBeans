'use strict';

/**
 * @ngdoc function
 * @name monSiteApp.controller:AboutCtrl
 * @description
 * # AboutCtrl
 * Controller of the monSiteApp
 */
angular.module('VirtualClassRoomModule')

.controller("AuthenticationCtrl",function ($scope,authenticationService,$location) {
		 
	$scope.authenticationService=authenticationService;
		 $scope.authenticate=function(login,password){
			
			 authenticationService.login=login;
			 authenticationService.password=password;
			 
			 authenticationService.authenticate().then(function(data){
				 
				 $location.path(data);
				 
			 });
			 
		 };
		 
		 
		 
		    }); 

