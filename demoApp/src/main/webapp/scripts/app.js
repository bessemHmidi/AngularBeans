'use strict';

/**
 * @ngdoc overview
 * @name monSiteApp
 * @description
 * # monSiteApp
 *
 * Main module of the application.
 **/

angular
  .module('VirtualClassRoomModule', [
	'angularBeans',                          
    'ngAnimate',
    'ngCookies',
    'ngResource',
    'ngRoute',
    'ngSanitize',
    'ngTouch',
    'angularFileUpload'
  ])

angular.module('VirtualClassRoomModule')

.config(function ($routeProvider) {
    $routeProvider
      .when('/choice', {
        templateUrl: 'views/choice.html',
        controller: 'ClassRoomsCtrl'
      })
      .when('/about', {
        templateUrl: 'views/avatarUpload.html'
     //   controller: 'AboutCtrl'
      })
       .when('/login', {
        templateUrl: 'views/login.html',
        controller: 'AuthenticationCtrl'
      })
      
      .when('/listClassRooms', {
        templateUrl: 'views/classRoomsList.html',
        controller: 'ClassRoomsCtrl'
      })
       .when('/classRoom/:classRoomName', {
        templateUrl: 'views/classRoom.html',
        controller: 'SingleClassRoomCtrl'
      })
     
      
     
     
      .otherwise({
        redirectTo: '404.html'
      });
  });
 

angular.module('VirtualClassRoomModule').run(function($rootScope, $location) {
	    $rootScope.$on( "$routeChangeStart", function(event, next, current) {
	    	
	      if ($rootScope.GRANT_LOGIN==null) {
	    	
	        // no logged user, redirect to /login
	        if ( next.templateUrl === "views/login.html") {
	        } else {
	          $location.path("/login");
	        }
	      }else{
	    	  //console.log('ok');
	      }
	    });
	  });






