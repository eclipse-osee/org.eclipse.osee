angular
		.module('AgileApp')
		.controller(
				'ProgramCtrl',
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
							$scope.loadingImg = Global.loadingImg;

							Global.loadActiveProgsTeams($scope, AgileEndpoint);

						} ]);
