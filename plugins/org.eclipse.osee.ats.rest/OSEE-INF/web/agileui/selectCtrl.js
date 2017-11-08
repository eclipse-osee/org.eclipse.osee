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
							$scope.loadingImg = Global.loadingImg;

							Global.loadActiveProgsTeams($scope, AgileEndpoint);

							$scope
									.$watch(
											"$parent.selectedItem",
											function() {
												var selected = $scope.$parent.selectedItem;

												if (selected) {
													if (selected.isProgram == false) {
														Menu
																.openTeamForTeam($scope.$parent.selectedItem);
													} else {
														Menu
														Menu
																.openProgram($scope.$parent.selectedItem);
													}
												}
											});

						} ]);
