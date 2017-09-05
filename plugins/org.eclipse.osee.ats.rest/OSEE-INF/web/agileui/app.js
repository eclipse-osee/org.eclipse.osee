/**
 * Agile app definition
 */
var app = angular.module('AgileApp', [ 'ngRoute', 'ngResource', 'ui.bootstrap',
		'ngGrid' ]);

app.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/', {
		redirectTo : "/teams",
	}).when('/home', {
		redirectTo : "/teams",
	}).when('/backlog', {
		templateUrl : 'backlog.html',
		controller : 'BacklogCtrl'
	}).when('/teams', {
		templateUrl : 'teams.html',
		controller : 'TeamsCtrl'
	}).when('/config', {
		templateUrl : 'config.html',
		controller : 'ConfigCtrl'
	}).otherwise({
		redirectTo : "/teams"
	});
} ]);

app.factory("LayoutService", function() {
	return {
		resizeElementHeight : function(elementName) {
			var element = window.document.getElementById(elementName);
			var height = 0;
			var body = window.document.body;
			if (window.innerHeight) {
				height = window.innerHeight;
			} else if (body.parentElement.clientHeight) {
				height = body.parentElement.clientHeight;
			} else if (body && body.clientHeight) {
				height = body.clientHeight;
			}
			element.style.height = ((height - element.offsetTop - 120) + "px");
		},
		refresh : function() {
			setTimeout(function() {
				$(window).trigger('resize');
			}, 500);
		}
	}
});

app.factory("PopupService", function($modal) {
	return {
		showLoadingModal : function(elementName) {
			var modalInstance = $modal.open({
				templateUrl : 'loadingModal.html',
				size : 'sm',
				windowClass : 'needsRerunModal',
				backdrop : 'static'
			});

			return modalInstance;
		}
	}
});

app.directive('focusMe', function($timeout) {
	return function(scope, element, attrs) {
		scope.$watch(attrs.focusMe, function() {
			$timeout(function() {
				element[0].focus();
			}, 20);
		});
	};
});

app
		.directive(
				'resize',
				function($window) {
					return function(scope, element) {
						var w = angular.element($window);
						scope.getWindowDimensions = function() {
							return {
								'h' : w.height(),
								'w' : w.width()
							};
						};
						scope.$watch(scope.getWindowDimensions, function(
								newValue, oldValue) {
							scope.windowHeight = newValue.h;
							scope.windowWidth = newValue.w;

							scope.style = function() {
								return {
									'height' : (newValue.h - 100) + 'px',
									'width' : (newValue.w - 100) + 'px'
								};
							};

						}, true);

						w
								.bind(
										'resize',
										function(scope) {
											var window = scope.currentTarget;
											var elementNameArray = [
													"backlogTable",
													"sprintConfigTable",
													"featureGroupConfigTable",
													"teamTable" ];
											var offsetArray = [ 120, 50, 50, 80 ];
											var arrayLength = elementNameArray.length;
											for (var i = 0; i < arrayLength; i++) {
												var elementName = elementNameArray[i];
												var element = window.document
														.getElementById(elementName);
												if (element) {
													var height = 0;
													var body = window.document.body;
													if (window.innerHeight) {
														height = window.innerHeight;
													} else if (body.parentElement.clientHeight) {
														height = body.parentElement.clientHeight;
													} else if (body
															&& body.clientHeight) {
														height = body.clientHeight;
													}
													var offset = offsetArray[i];
													element.style.height = ((height
															- element.offsetTop - offset) + "px");
												}
											}
										});
					}
				});

app.directive('ngConfirmClick', [ function() {
	return {
		link : function(scope, element, attr) {
			var msg = attr.ngConfirmClick || "Are you sure?";
			var clickAction = attr.confirmedClick;
			element.bind('click', function(event) {
				if (window.confirm(msg)) {
					scope.$eval(clickAction)
				}
			});
		}
	};
} ])
