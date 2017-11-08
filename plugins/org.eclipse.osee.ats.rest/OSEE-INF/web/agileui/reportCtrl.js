/**
 * Agile Reports Controller
 */
angular
		.module('AgileApp')
		.controller(
				'ReportCtrl',
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
						'LayoutService',
						function($scope, AgileEndpoint, Menu, Global, $resource, $window,
								$modal, $filter, $routeParams, LayoutService) {

							$scope.team = {};
							$scope.team.id = $routeParams.team;
							$scope.reporttype = $routeParams.reporttype;
							$scope.reportname = $routeParams.reportname;
							$scope.loadingImg = Global.loadingImg;

							$scope.updateReports = function() {
								AgileEndpoint.getTeamSingle($scope.team).$promise
										.then(function(data) {
											$scope.team.name = data.name;
											$scope.sprint = {};
											$scope.sprint.id = data.sprintId;
											$scope.loadingImg = Global.loadingImg;
		
											var htmlcontent = $('#b1 ');
											var url = "/ats/agile/team/";
											url = url
													.concat($scope.team.id);
											url = url
													.concat("/sprint/");
											url = url
													.concat($scope.sprint.id);
											if ($scope.reporttype == "burndown") {
												url = url
														.concat("/burndown/chart/ui?type=best");
											} else if ($scope.reporttype == "burnup") {
												url = url
														.concat("/burnup/chart/ui?type=best");
											} else if ($scope.reporttype == "data") {
												url = url
														.concat("/data/table?type=best");
											} else if ($scope.reporttype == "summary") {
												url = url
														.concat("/summary?type=best");
											}
											
											var ff =
												  navigator.userAgent.search("Firefox");
											if (ff > -1) {
												$scope.firefox = true;
												window.open(url, "reportFrame");
											} else {
												$scope.firefox = false;
												$("#reportDiv").html("<object data=\"" + url + "\">");
												document.getElementById("reportDiv").style.zoom = "400%"

											}
										    
											AgileEndpoint
													.getSprint(
															$scope.team.id,
															$scope.sprint.id).$promise
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
														$scope.loaded = true;
													}).catch((err) => {
														alert(err);
													});
										}).catch((err) => {
											alert(err);
										});
							}

							$scope.updateReports();
							Global.loadActiveProgsTeams($scope, AgileEndpoint);

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
