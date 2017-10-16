/**
 * Agile app definition
 */
var app = angular.module('AgileApp', [ 'ngRoute', 'ngResource', 'ui.bootstrap',
		'ngGrid', 'ngDraggable' ]);

app.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/', {
		redirectTo : "/teams",
	}).when('/home', {
		redirectTo : "/teams",
	}).when('/kanban', {
		templateUrl : 'kanban/kanban.html',
		controller : 'KanbanCtrl'
	}).when('/backlog', {
		templateUrl : 'backlog.html',
		controller : 'BacklogAndSprintCtrl'
	}).when('/sprint', {
		templateUrl : 'sprint.html',
		controller : 'BacklogAndSprintCtrl'
	}).when('/teams', {
		templateUrl : 'teams.html',
		controller : 'TeamsCtrl'
	}).when('/config', {
		templateUrl : 'config.html',
		controller : 'ConfigCtrl'
	}).when('/report', {
		templateUrl : 'report.html',
		controller : 'ReportCtrl'
	}).when('/newAction', {
		templateUrl : 'newAction.html',
		controller : 'NewActionCtrl',
		caseInsensitiveMatch : true
	}).otherwise({
		redirectTo : "/teams"
	});
} ]);

app.factory("Global", function() {
	var global = {};
	global.loadingImg = "/ajax/libs/images/loading.gif";
	return global;
});

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

app.directive('sprintconfig', function() {
	return {
		templateUrl : 'sprintConfig.html'
	};
});

app.directive('menu', function() {
	return {
		templateUrl : 'menu.html'
	};
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

app.directive('resize', function($window) {
	return function(scope, element) {
		var w = angular.element($window);
		scope.getWindowDimensions = function() {
			return {
				'h' : w.height(),
				'w' : w.width()
			};
		};
		scope.$watch(scope.getWindowDimensions, function(newValue, oldValue) {
			scope.windowHeight = newValue.h;
			scope.windowWidth = newValue.w;

			scope.style = function() {
				return {
					'height' : (newValue.h - 100) + 'px',
					'width' : (newValue.w - 100) + 'px'
				};
			};

		}, true);
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

app.directive(
// Directive for cards element
'cards', function() {
	return {
		restrict : 'EA',
		link : function(scope, element, attrs) {
			var tasksAsCards = getTasksFor(scope.state.name, scope.assignee);
			scope.tasks = tasksAsCards;
		},
		replace : true,
		template : '<ng-include src="getTemplateUrl()"/>',
		controller : function($scope) {
			// function used on the ng-include to resolve the template
			$scope.getTemplateUrl = function() {
				// basic handling
				if ($scope.bigCards) {
					return "kanban/kanbanCard.html";
				}
				return "kanban/kanbanCardSm.html";
			}
		}
	}
});

app.filter(
// Truncate characters for long text
'truncate', function() {
	return function(text, length, end) {
		if (isNaN(length))
			length = 10;

		if (end === undefined)
			end = "...";

		if (text.length <= length || text.length - end.length <= length) {
			return text;
		} else {
			return String(text).substring(0, length - end.length) + end;
		}

	};
});
