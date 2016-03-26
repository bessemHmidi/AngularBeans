angular.module('VirtualClassRoomModule')
.controller("GrowlCtrl",function($scope,notificationsService,GROWL){
	  
	$scope.notificationsService=notificationsService;

	// angularBeans.bind($scope,notificationService);
	  
	
	$scope.$on("notificationChannel",function(event,data){
		  
		 var message=data.message;
		  if (typeof message!= 'undefined'){
			//'https://randomuser.me/api/portraits/med/men/77.jpg'
		  if(message.type==='img'){
			  GROWL.withImage(message.title,message.body,message.image);
		  }
		  else{ 
			  
		  GROWL.simple(message.type,message.title,message.body);
		  } 
		  
		  
		  
		  }
		  
	  });

});
	
//	  $scope.$watch('notificationsService.message',function(){
//		  
//		  
//		  console.log('new Message..........');
//		  
//		 var message=notificationsService.message;
//		  if (typeof message!= 'undefined'){
//			//'https://randomuser.me/api/portraits/med/men/77.jpg'
//		  if(message.type==='img'){
//			  GROWL.withImage(message.title,message.body,message.image);
//		  }
//		  else{ 
//			  
//		  GROWL.simple(message.type,message.title,message.body);
//		  } 
//		  
//		  
//		  
//		  }
//		  
//	  });
//	  
//  });