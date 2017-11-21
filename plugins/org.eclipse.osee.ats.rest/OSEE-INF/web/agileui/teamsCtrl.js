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
						'Menu',
						'Global',
						'$resource',
						'$window',
						'$modal',
						'$filter',
						'$routeParams',
						'LayoutService',
						function($scope, AgileEndpoint, Menu, Global, $resource,
								$window, $modal, $filter, $routeParams,
								LayoutService) {

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
							var openNewTaskImpl = '<button class="btn btn-default btn-sm" ng-click="openNewTaskForTeam(row.entity)">New Task</button>';

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
									field : "newTask",
									displayName : 'New Task',
									width : 85,
									cellTemplate : openNewTaskImpl,
									cellClass : 'grid-align'
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
								AgileEndpoint.getTeamsTokens().$promise
										.then(function(data) {
											$scope.teams = data;
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
