/**
 * Agile Config Controller
 */
angular
		.module('AgileApp')
		.controller(
				'TeamsCtrl',
				[
						'$scope',
						'AgileEndpoint',
						'$resource',
						'$window',
						'$modal',
						'$filter',
						'$routeParams',
						'LayoutService',
						'PopupService',
						function($scope, AgileEndpoint, $resource, $window,
								$modal, $filter, $routeParams, LayoutService,
								PopupService) {

							// ////////////////////////////////////
							// Agile Team table
							// ////////////////////////////////////
							$scope.selectedTeams = [];

							var openTeamTmpl = '<button class="btn btn-default btn-sm" ng-click="openTeam(row.entity)">Open</button>';
							var configTeamTmpl = '<button class="btn btn-default btn-sm" ng-click="openConfigForTeam(row.entity)">Config</button>';
							var openBacklogImpl = '<button class="btn btn-default btn-sm" ng-click="openBacklogForTeam(row.entity)">Backlog</button>';
							var openSprintImpl = '<button class="btn btn-default btn-sm" ng-click="openSprintForTeam(row.entity)">Sprint</button>';
							var openKanbanImpl = '<button class="btn btn-default btn-sm" ng-click="openKanbanForTeam(row.entity)">Kanban</button>';
							var openBurndownImpl = '<button class="btn btn-default btn-sm" ng-click="openBurndownForTeam(row.entity)">Burn-Down</button>';
							var openBurnupImpl = '<button class="btn btn-default btn-sm" ng-click="openBurnupForTeam(row.entity)">Burn-Up</button>';
							var openSummaryImpl = '<button class="btn btn-default btn-sm" ng-click="openSummaryForTeam(row.entity)">Summary</button>';
							var openDataImpl = '<button class="btn btn-default btn-sm" ng-click="openDataForTeam(row.entity)">Data</button>';
							var openNewActionImpl = '<button class="btn btn-default btn-sm" ng-click="openNewActionForTeam(row.entity)">New Action</button>';

							$scope.teamGridOptions = {
								data : 'teams',
								enableHighlighting : true,
								enableColumnResize : true,
								multiSelect : false,
								showFilter : true,
								sortInfo : {
									fields : [ 'name' ],
									directions : [ 'asc' ]
								},
								columnDefs : [ {
									field : 'name',
									displayName : 'Name',
									width : 200
								}, {
									field : "backlog",
									displayName : 'Backlog',
									width : 70,
									cellTemplate : openBacklogImpl,
									cellClass : 'grid-align'
								}, {
									field : "sprint",
									displayName : 'Sprint',
									width : 60,
									cellTemplate : openSprintImpl,
									cellClass : 'grid-align'
								}, {
									field : "kanban",
									displayName : 'Kanban',
									width : 66,
									cellTemplate : openKanbanImpl,
									cellClass : 'grid-align'
								}, {
									field : "newAction",
									displayName : 'New Action',
									width : 85,
									cellTemplate : openNewActionImpl,
									cellClass: 'grid-align'
								}, {
									field : "burndown",
									displayName : 'Burn-Down',
									width : 80,

									cellTemplate : openBurndownImpl,
									cellClass : 'grid-align'
								}, {
									field : "burnup",
									displayName : 'Burn-Up',
									width : 74,
									cellTemplate : openBurnupImpl,
									cellClass : 'grid-align'
								}, {
									field : "summary",
									displayName : 'Summary',
									width : 79,
									cellTemplate : openSummaryImpl,
									cellClass : 'grid-align'
								}, {
									field : "data",
									displayName : 'Data',
									width : 60,
									cellTemplate : openDataImpl,
									cellClass : 'grid-align'
								}, {
									field : "config",
									displayName : 'Config',
									width : 60,
									cellTemplate : configTeamTmpl,
									cellClass : 'grid-align'
								} ]
							};

							$scope.updateTeams = function() {
								$scope.sheets = null;
								var loadingModal = PopupService
										.showLoadingModal();
								AgileEndpoint.getTeamsTokens().$promise.then(function(
										data) {
									$scope.teams = data;
									loadingModal.close();
									LayoutService
											.resizeElementHeight("teamTable");
									LayoutService.refresh();
								});
							}

							$scope.openTeam = function(team) {
								window.location.assign("main#/team?team="
										.concat(team.id))
							}

							$scope.addNewTeam = function() {
								var modalInstance = $modal.open({
									templateUrl : 'addNewTeam.html',
									controller : AddNewTeamModalCtrl,
								});

								modalInstance.result.then(function(teamName) {
									AgileEndpoint.addNewTeam(teamName).$promise
											.then(function(data) {
												$scope.updateTeams();
											});
								});
							}

							var AddNewTeamModalCtrl = function($scope,
									$modalInstance) {

								$scope.newTeam = {
									name : ""
								};

								$scope.ok = function() {
									$modalInstance.close($scope.newTeam.name);
								};

								$scope.cancel = function() {
									$modalInstance.dismiss('cancel');
								};
							};

							$scope.refresh = function() {
								$scope.updateTeams();
							}
							
							$scope.refresh();

							// NOT COMMON MENU, MUST REFERENCE team and not $scope
							$scope.openConfigForTeam = function(team) {
								window.location.assign("main#/config?team="
										.concat(team.id))
							}

							$scope.openKanbanForTeam = function(team) {
								window.location.assign("main#/kanban?team="
										.concat(team.id))
							}

							$scope.openBurndownForTeam = function(team) {
								window.location.assign("main#/report?team="
										.concat(team.id).concat("&reporttype=burndown&reportname=Burn-Down"))
							}

							$scope.openBurnupForTeam = function(team) {
								window.location.assign("main#/report?team="
										.concat(team.id).concat("&reporttype=burnup&reportname=Burn-Up"))
							}

							$scope.openBacklogForTeam = function(team) {
								window.location.assign("main#/backlog?team="
										.concat(team.id).concat("&default=backlog"))
							}

							$scope.openNewActionForTeam = function(team) {
								window.location.assign("main#/newAction?team="
										.concat(team.id))
							}

							$scope.openSprintForTeam = function(team) {
								window.location.assign("main#/sprint?team="
										.concat(team.id).concat("&default=sprint"))
							}

							$scope.openSummaryForTeam = function(team) {
								window.location.assign("main#/report?team="
										.concat(team.id).concat("&reporttype=summary&reportname=Summary"))
							}

							$scope.openDataForTeam = function(team) {
								window.location.assign("main#/report?team="
										.concat(team.id).concat("&reporttype=data&reportname=Data"))
							}

						} ]);
