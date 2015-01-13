/**
 * Agile app definition
 */
var app = angular.module('AgileApp', [ 'ngRoute', 'ngResource', 'ui.bootstrap',
		'ngGrid' ]);

app.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/', {
		redirectTo : "/config",
	}).when('/home', {
		redirectTo : "main.html",
	}).when('/config', {
		templateUrl : 'config.html',
		controller : 'ConfigCtrl'
	}).otherwise({
		redirectTo : "/config"
	});
} ]);

app.directive('focusMe', function($timeout) {
	return function(scope, element, attrs) {
		scope.$watch(attrs.focusMe, function() {
			$timeout(function() {
				element[0].focus();
			}, 20);
		});
	};
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
