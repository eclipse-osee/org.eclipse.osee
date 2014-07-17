/**
 * Cpa app definition
 */
var app = angular.module('CpaApp', [ 'ngRoute', 'ngResource', 'ui.bootstrap', 'ngGrid' ]);

app.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/', {
		redirectTo : "/analyze",
	}).when('/analyze', {
		templateUrl : 'analyze.html',
		controller : 'AnalyzeCtrl'
	}).otherwise({
		redirectTo : "/analyze"
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