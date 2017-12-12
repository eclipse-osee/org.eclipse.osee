angular
		.module('AgileApp')
		.controller(
				'SelectCtrl',
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
						function($scope, AgileEndpoint, Menu, Global,
								$resource, $window, $modal, $filter,
								$routeParams) {

							$scope.selectedTeams = [];
							$scope.activeProgsTeams = [];

							Global.loadActiveProgsTeams($scope, AgileEndpoint, Menu);


						} ]);
