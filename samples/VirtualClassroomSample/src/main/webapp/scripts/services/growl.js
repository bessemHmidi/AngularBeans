'use strict';

angular
		.module('VirtualClassRoomModule')
		.factory(
				'GROWL',
				function() {
					var growl = {};

					growl.simple = function(type, title, message) {
						$.notify({
							title : '<strong>' + title + '</strong>',
							message : message
						}, {

							animate : {
								enter : 'animated rollIn',
								exit : 'animated rollOut'
							},
							type : type
						});
					};

					growl.withImage = function(title, message,icon) {
						$
								.notify(
										{
											icon : icon,
											title : '<strong>' + title + '</strong>',
											message : message

										},
										{
											delay : 5000,
											icon_type : 'image',
											template : '<div data-notify="container" class="col-xs-11 col-sm-3 alert alert-{0}"  role="alert">'
													+ '<img data-notify="icon" class="img-circle pull-left" height="10%" width="10%">'
													+ '<span data-notify="title">{1}</span>'
													+ '<span data-notify="message">{2}</span>'
													+ '</div>'
										});

					};

					return growl;
				});
