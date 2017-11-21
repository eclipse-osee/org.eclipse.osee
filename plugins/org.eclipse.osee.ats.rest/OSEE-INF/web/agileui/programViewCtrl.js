/**
 * Uses Angular-Tree-Widget - http://www.bestjquery.com/?291XT9RI
 */
angular.module('AgileApp').controller(
		'ProgramViewCtrl',
		[
				'$scope',
				'AgileEndpoint',
				'Menu',
				'Global',
				'$resource',
				'$window',
				'$modal',
				'$filter',
				'$routeParams',
				function($scope, AgileEndpoint, Menu, Global, $resource,
						$window, $modal, $filter, $routeParams) {

					$scope.program = {};
					$scope.program.id = $routeParams.program;
					$scope.program.name = "";

					AgileEndpoint.getProgramToken($scope.program).$promise
					.then(function(data) {
						$scope.program.name = data.name;
					});

					AgileEndpoint.getProgramAtw($scope.program).$promise
					.then(function(data) {
						$scope.treeNodes = data;
					});

					Global.loadActiveProgsTeams($scope, AgileEndpoint, Menu);

				} ]);
