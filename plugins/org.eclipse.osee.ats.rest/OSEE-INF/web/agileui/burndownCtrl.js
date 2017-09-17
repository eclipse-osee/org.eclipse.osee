/**
 * Agile Reports Controller
 */
angular
		.module('AgileApp')
		.controller(
				'BurndownCtrl',
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
							$scope.reportname = "Burn-Down";

							$scope.updateReports = function() {
								AgileFactory.getTeamSingle($scope.team).$promise
										.then(function(data) {
											$scope.selectedTeam = data;
											AgileFactory
													.getTeamSingle($scope.selectedTeam).$promise
													.then(function(data) {

														$scope.selectedTeam = data.name;
														$scope.team.sprintId = data.sprintId;

														var htmlcontent = $('#b1 ');
														var url = "<object data=\"/ats/agile/team/";
														url = url
																.concat($scope.team.uuid);
														url = url
																.concat("/sprint/");
														url = url
																.concat($scope.team.sprintId);
														url = url
																.concat("/burndown/chart/ui?type=best\">");

														$("#b1").html(url);
													});
										});
							}

							$scope.updateReports();

						} ]);
