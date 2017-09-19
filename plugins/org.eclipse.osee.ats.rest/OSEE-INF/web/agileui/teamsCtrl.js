/**
 * Agile Config Controller
 */
angular
		.module('AgileApp')
		.controller(
				'TeamsCtrl',
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

							// ////////////////////////////////////
							// Agile Team table
							// ////////////////////////////////////
							$scope.selectedTeams = [];

							var openTeamTmpl = '<button class="btn btn-default btn-sm" ng-click="openTeam(row.entity)">Open</button>';
							var configTeamTmpl = '<button class="btn btn-default btn-sm" ng-click="configTeam(row.entity)">Config</button>';
							var openBacklogImpl = '<button class="btn btn-default btn-sm" ng-click="openBacklog(row.entity)">Backlog</button>';
							var openSprintImpl = '<button class="btn btn-default btn-sm" ng-click="openSprint(row.entity)">Sprint</button>';
							var openKanbanImpl = '<button class="btn btn-default btn-sm" ng-click="openKanban(row.entity)">Kanban</button>';
							var openBurndownImpl = '<button class="btn btn-default btn-sm" ng-click="openBurndown(row.entity)">Burn-Down</button>';
							var openBurnupImpl = '<button class="btn btn-default btn-sm" ng-click="openBurnup(row.entity)">Burn-Up</button>';
							var openSummaryImpl = '<button class="btn btn-default btn-sm" ng-click="openSummary(row.entity)">Summary</button>';
							var openDataImpl = '<button class="btn btn-default btn-sm" ng-click="openData(row.entity)">Data</button>';
							
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
									field : 'uuid',
									displayName : 'Id',
									width : 50
								}, {
									field : 'name',
									displayName : 'Name',
									width : 150
								}, {
									field : "open",
									displayName : 'Open',
									width : 54,
									cellTemplate : openTeamTmpl,
									cellClass: 'grid-align'
								}, {
									field : "backlog",
									displayName : 'Backlog',
									width : 70,
									cellTemplate : openBacklogImpl,
									cellClass: 'grid-align'
								}, {
									field : "sprint",
									displayName : 'Sprint',
									width : 60,
									cellTemplate : openSprintImpl,
									cellClass: 'grid-align'
								}, {
									field : "kanban",
									displayName : 'Kanban',
									width : 66,
									cellTemplate : openKanbanImpl,
									cellClass: 'grid-align'
								}, {
									field : "burndown",
									displayName : 'Burn-Down',
									width : 80,
									
									cellTemplate : openBurndownImpl,
									cellClass: 'grid-align'
								}, {
									field : "burnup",
									displayName : 'Burn-Up',
									width : 74,
									cellTemplate : openBurnupImpl,
									cellClass: 'grid-align'
								}, {
									field : "summary",
									displayName : 'Summary',
									width : 79,
									cellTemplate : openSummaryImpl,
									cellClass: 'grid-align'
								}, {
									field : "data",
									displayName : 'Data',
									width : 60,
									cellTemplate : openDataImpl,
									cellClass: 'grid-align'
								}, {
									field : "config",
									displayName : 'Config',
									width : 60,
									cellTemplate : configTeamTmpl,
									cellClass: 'grid-align'
								} ]
							};

							$scope.updateTeams = function() {
								$scope.sheets = null;
								var loadingModal = PopupService
										.showLoadingModal();
								AgileFactory.getTeams().$promise.then(function(
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
										.concat(team.uuid))
							}

							$scope.configTeam = function(team) {
								window.location.assign("main#/config?team="
										.concat(team.uuid))
							}

							$scope.openKanban = function(team) {
								window.location.assign("main#/kanban?team="
										.concat(team.uuid))
							}

							$scope.openBurndown = function(team) {
								window.location.assign("main#/burndown?team="
										.concat(team.uuid))
							}

							$scope.openBurnup = function(team) {
								window.location.assign("main#/burnup?team="
										.concat(team.uuid))
							}

							$scope.openBacklog = function(team) {
								window.location.assign("main#/backlog?team="
										.concat(team.uuid).concat("&default=backlog"))
							}

							$scope.openSprint = function(team) {
								window.location.assign("main#/sprint?team="
										.concat(team.uuid).concat("&default=sprint"))
							}

							$scope.openSummary = function(team) {
								window.location.assign("main#/summary?team="
										.concat(team.uuid))
							}

							$scope.openData = function(team) {
								window.location.assign("main#/data?team="
										.concat(team.uuid))
							}

							$scope.addNewTeam = function() {
								var modalInstance = $modal.open({
									templateUrl : 'addNewTeam.html',
									controller : AddNewTeamModalCtrl,
								});

								modalInstance.result.then(function(teamName) {
									AgileFactory.addNewTeam(teamName).$promise
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

						} ]);
