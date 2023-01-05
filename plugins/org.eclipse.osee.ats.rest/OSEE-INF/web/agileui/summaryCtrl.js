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
				'SummaryCtrl',
				[
						'$scope',
						'AgileEndpoint',
						'$resource',
						'$window',
						'$modal',
						'$filter',
						'$routeParams',
						'LayoutService',
						function($scope, AgileEndpoint, $resource, $window,
								$modal, $filter, $routeParams, LayoutService) {

							$scope.team = {};
							$scope.team.id = $routeParams.team;
							$scope.reportname = "Sprint Summary";

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
																.concat("/summary?type=best\">");

														$("#b1").html(url);
													});
										});
							}

							$scope.updateReports();
							Global.loadActiveProgsTeams($scope, AgileEndpoint, Menu);

						} ]);
