/**
 * Agile Reports Controller
 */
angular
		.module('AgileApp')
		.controller(
				'ReportCtrl',
				[
						'$scope',
						'AgileFactory',
						'$resource',
						'$window',
						'$modal',
						'$filter',
						'$routeParams',
						'LayoutService',
						'PopupService',
						function($scope, AgileFactory, $resource, $window,
								$modal, $filter, $routeParams, LayoutService,
								PopupService) {

							$scope.team = {};
							$scope.team.uuid = $routeParams.team;
							$scope.reporttype = $routeParams.reporttype;
							$scope.reportname = $routeParams.reportname;

							$scope.updateReports = function() {
								AgileFactory.getTeamSingle($scope.team).$promise
										.then(function(data) {
											$scope.selectedTeam = data;
											$scope.teamName = data.name;
											$scope.team.sprintId = data.sprintId;
		
											var htmlcontent = $('#b1 ');
											var url = "<object data=\"/ats/agile/team/";
											url = url
													.concat($scope.team.uuid);
											url = url
													.concat("/sprint/");
											url = url
													.concat($scope.team.sprintId);
											if ($scope.reporttype == "burndown") {
												url = url
														.concat("/burndown/chart/ui?type=best\">");
											} else if ($scope.reporttype == "burnup") {
												url = url
														.concat("/burnup/chart/ui?type=best\">");
											} else if ($scope.reporttype == "data") {
												url = url
														.concat("/data/table?type=best\">");
											} else if ($scope.reporttype == "summary") {
												url = url
														.concat("/summary?type=best\">");
											}
		
											$("#b1").html(url);
		
											AgileFactory
													.getSprint(
															$scope.team.uuid,
															$scope.team.sprintId).$promise
													.then(function(
															data) {
														$scope.reportname = data.name
																.concat(
																		" - ")
																.concat(
																		$scope.reportname)
																.concat(
																		" - ")
																.concat(
																		new Date()
																				.toLocaleString());
													}).catch((err) => {
														alert(err);
													});
										}).catch((err) => {
											alert(err);
										});
							}

							$scope.updateReports();

							// COMMON MENU COPIED TO ALL JS
							$scope.openConfigForTeam = function(team) {
								window.location.assign("main#/config?team="
										.concat($scope.team.uuid))
							}

							$scope.openKanbanForTeam = function(team) {
								window.location.assign("main#/kanban?team="
										.concat($scope.team.uuid))
							}

							$scope.openBurndownForTeam = function(team) {
								window.location.assign("main#/report?team="
										.concat($scope.team.uuid).concat("&reporttype=burndown&reportname=Burn-Down"))
							}

							$scope.openBurnupForTeam = function(team) {
								window.location.assign("main#/report?team="
										.concat($scope.team.uuid).concat("&reporttype=burnup&reportname=Burn-Up"))
							}

							$scope.openBacklogForTeam = function(team) {
								window.location.assign("main#/backlog?team="
										.concat($scope.team.uuid).concat("&default=backlog"))
							}

							$scope.openNewActionForTeam = function(team) {
								window.location.assign("main#/newAction?team="
										.concat($scope.team.uuid))
							}

							$scope.openSprintForTeam = function(team) {
								window.location.assign("main#/sprint?team="
										.concat($scope.team.uuid).concat("&default=sprint"))
							}

							$scope.openSummaryForTeam = function(team) {
								window.location.assign("main#/report?team="
										.concat($scope.team.uuid).concat("&reporttype=summary&reportname=Summary"))
							}

							$scope.openDataForTeam = function(team) {
								window.location.assign("main#/report?team="
										.concat($scope.team.uuid).concat("&reporttype=data&reportname=Data"))
							}
							// COMMON MENU COPIED TO ALL JS

						} ]);
