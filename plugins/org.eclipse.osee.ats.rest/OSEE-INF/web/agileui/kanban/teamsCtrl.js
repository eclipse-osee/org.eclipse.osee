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
							var openKanbanImpl = '<button class="btn btn-default btn-sm" ng-click="openKanban(row.entity)">Kanban</button>';
							var openReportsImpl = '<button class="btn btn-default btn-sm" ng-click="openReports(row.entity)">Reports</button>';
							
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
									width : 290
								}, {
									field : "open",
									displayName : 'Open',
									width : 54,
									cellTemplate : openTeamTmpl
								}, {
									field : "backlog",
									displayName : 'Backlog',
									width : 70,
									cellTemplate : openBacklogImpl
								}, {
									field : "kanban",
									displayName : 'Kanban',
									width : 66,
									cellTemplate : openKanbanImpl
								}, {
									field : "reports",
									displayName : 'Reports',
									width : 70,
									cellTemplate : openReportsImpl
								}, {
									field : "config",
									displayName : 'Config',
									width : 60,
									cellTemplate : configTeamTmpl
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

							$scope.openReports = function(team) {
								window.location.assign("/ats/agile/team/"
										.concat(team.uuid).concat("/reports"))
							}

							$scope.openBacklog = function(team) {
								window.location.assign("main#/reports?team="
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
