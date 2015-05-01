'use strict';

/**
 * @ngdoc function
 * @name monSiteApp.controller:AboutCtrl
 * @description
 * # AboutCtrl
 * Controller of the monSiteApp
 */
angular.module('VirtualClassRoomModule')

.controller("LanguageCtrl",function ($scope,bundleService) {
		 bundleService.loadBundle("tr_en","tr");
		 
		 $scope.changeLang=function(lang){
			bundleService.loadBundle("tr_"+lang,"tr");
	
		 };
		    }); 
