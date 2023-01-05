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
/**
 * Agile Reports Controller
 */
angular
		.module('AgileApp')
		.controller(
				'BurnupCtrl',
				[
						'$scope',
						'AgileEndpoint',
						'Menu',
						'$resource',
						'$window',
						'$modal',
						'$filter',
						'$routeParams',
						'LayoutService',
						function($scope, AgileEndpoint, Menu, $resource, $window,
								$modal, $filter, $routeParams, LayoutService) {

							$scope.team = {};
							$scope.team.id = $routeParams.team;
							$scope.reportname = "Burn-Up";

							$scope.updateReports = function() {
								AgileEndpoint.getTeamSingle($scope.team).$promise
										.then(function(data) {
											$scope.selectedTeam = data;
											AgileEndpoint
													.getTeamSingle($scope.selectedTeam).$promise
													.then(function(data) {

														$scope.selectedTeam = data.name;
														$scope.team.sprintId = data.sprintId;

														var htmlcontent = $('#b1 ');
														var url = "<object data=\"/ats/agile/team/";
														url = url
																.concat($scope.team.id);
														url = url
																.concat("/sprint/");
														url = url
																.concat($scope.team.sprintId);
														url = url
																.concat("/burnup/chart/ui?type=best\">");

														$("#b1").html(url);
													});
										});
							}

							$scope.updateReports();
							Global.loadActiveProgsTeams($scope, AgileEndpoint, Menu);

							// Copied through all controlers; ensure all are same
							$scope.openBacklogForTeam = Menu.openBacklogForTeam;
							$scope.openSprintForTeam = Menu.openSprintForTeam;
							$scope.openKanbanForTeam = Menu.openKanbanForTeam;
							$scope.openNewTaskForTeam = Menu.openNewTaskForTeam;
							$scope.openBurndownForTeam = Menu.openBurndownForTeam;
							$scope.openBurnupForTeam = Menu.openBurnupForTeam;
							$scope.openSummaryForTeam = Menu.openSummaryForTeam;
							$scope.openDataForTeam = Menu.openDataForTeam;

						} ]);
