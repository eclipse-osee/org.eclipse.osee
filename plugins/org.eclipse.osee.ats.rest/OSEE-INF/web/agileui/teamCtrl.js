/*********************************************************************
* Copyright (c) 2023 Boeing
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     Boeing - initial API and implementation
**********************************************************************/
angular
		.module('AgileApp')
		.controller(
				'TeamCtrl',
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

							$scope.team = {};
							$scope.team.id = $routeParams.team;

							AgileEndpoint.getTeamToken($scope.team).$promise
							.then(function(data) {
								$scope.team.name = data.name;
							});

							Global.loadActiveProgsTeams($scope, AgileEndpoint, Menu);

							// Copied through all controlers; ensure all are
							// same
							$scope.openBacklogForTeam = Menu.openBacklogForTeam;
							$scope.openSprintForTeam = Menu.openSprintForTeam;
							$scope.openKanbanForTeam = Menu.openKanbanForTeam;
							$scope.openNewTaskForTeam = Menu.openNewTaskForTeam;
							$scope.openBurndownForTeam = Menu.openBurndownForTeam;
							$scope.openBurnupForTeam = Menu.openBurnupForTeam;
							$scope.openSummaryForTeam = Menu.openSummaryForTeam;
							$scope.openDataForTeam = Menu.openDataForTeam;

						} ]);
