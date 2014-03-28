/**
 * 
 */

var commonApp = angular.module('common', []);

commonApp.directive('goClick', function($location) {
	return function(scope, element, attrs) {
		var path;

		attrs.$observe('goClick', function(val) {
			path = val;
		});

		element.bind('click', function() {
			scope.$apply(function() {
				console.log('clicked: ' + path);
				$location.path(path);
			});
		});
	};
});
