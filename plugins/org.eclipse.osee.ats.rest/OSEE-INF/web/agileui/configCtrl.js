/**
 * Agile Config Controller
 */
angular
		.module('AgileApp')
		.controller(
				'ConfigCtrl',
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
							$scope.team.uuid = $routeParams.team;
							$scope.selectedTeam = {};
							$scope.selectedTeam.name = "";
							$scope.selectedTeam.description = "";
							$scope.selectedTeam.active = "";
							$scope.selectedTeam.backlog = "";
							$scope.isLoaded = "";

							// ////////////////////////////////////
							// Sprint Config Table
							// ////////////////////////////////////

							var deleteSprintImpl = '<button class="btn btn-default btn-sm" confirmed-click="deleteSprint(row.entity)" ng-confirm-click="Delete Sprint \"{{row.entity.name}}?\"">Delete</button>';

							$scope.sprintGridOptions = {
								data : 'sprints',
								enableHighlighting : true,
								enableColumnResize : true,
								showFilter : true,
								sortInfo : {
									fields : [ 'Name' ],
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
									field : 'active',
									displayName : 'Active',
									width : 60
								}, {
									field : "config",
									displayName : 'Delete',
									width : 60,
									cellTemplate : deleteSprintImpl
								} ]
							};

							$scope.updateSprints = function() {
								var team = $scope.selectedTeam;
								var toPost = {};
								toPost.uuid = team.uuid;
								$scope.sprints = [];
								AgileEndpoint.getSprints(toPost).$promise
										.then(function(data) {
											$scope.sprints = data;
											LayoutService
													.resizeElementHeight("sprintConfigTable");
											LayoutService.refresh();
										});
							}

							$scope.deleteTeam = function() {
								AgileEndpoint.deleteTeam($scope.team).$promise
										.then(function(data) {
											window.location.assign("main")
										});
							}

							$scope.deleteSprint = function(sprint) {
								AgileEndpoint.deleteSprint(sprint).$promise
										.then(function(data) {
											$scope.updateSprints();
										});
							}

							$scope.addNewSprint = function() {
								var modalInstance = $modal.open({
									templateUrl : 'addNewSprint.html',
									controller : AddNewSprintModalCtrl,
								});

								modalInstance.result
										.then(function(teamName) {
											AgileEndpoint.addNewSprint(
													$scope.selectedTeam,
													teamName).$promise
													.then(function(data) {
														$scope.updateSprints();
													});
										});
							}

							var AddNewSprintModalCtrl = function($scope,
									$modalInstance) {

								$scope.newSprint = {
									name : ""
								};

								$scope.ok = function() {
									$modalInstance.close($scope.newSprint.name);
								};

								$scope.cancel = function() {
									$modalInstance.dismiss('cancel');
								};
							};

							// ////////////////////////////////////
							// Feature Group Config Table
							// ////////////////////////////////////

							var deleteFeatureGroupImpl = '<button class="btn btn-default btn-sm" confirmed-click="deleteFeatureGroup(row.entity)" ng-confirm-click="Delete Feature Group \"{{row.entity.Name}}?\"">Delete</button>';

							$scope.featureGridOptions = {
								data : 'featureGroups',
								enableHighlighting : true,
								enableColumnResize : true,
								showFilter : true,
								sortInfo : {
									fields : [ 'Name' ],
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
									field : 'active',
									displayName : 'Active',
									width : 60
								}, {
									field : "config",
									displayName : 'Delete',
									width : 60,
									cellTemplate : deleteFeatureGroupImpl
								} ]
							};

							$scope.updateFeatureGroups = function() {
								var team = $scope.selectedTeam;
								var toPost = {};
								toPost.uuid = team.uuid;
								$scope.featureGroups = [];
								AgileEndpoint.getFeatureGroups(toPost).$promise
										.then(function(data) {
											$scope.featureGroups = data;
											LayoutService
													.resizeElementHeight("featureGroupConfigTable");
											LayoutService.refresh();
										});
							}

							$scope.deleteFeatureGroup = function(featureGroup) {
								AgileEndpoint.deleteFeatureGroup(featureGroup).$promise
										.then(function(data) {
											$scope.updateFeatureGroups();
										});
							}

							$scope.addNewFeatureGroup = function() {
								var modalInstance = $modal.open({
									templateUrl : 'addNewFeatureGroup.html',
									controller : AddNewFeatureGroupModalCtrl,
								});

								modalInstance.result
										.then(function(teamName) {
											AgileEndpoint.addNewFeatureGroup(
													$scope.selectedTeam,
													teamName).$promise
													.then(function(data) {
														$scope
																.updateFeatureGroups();
													});
										});
							}

							var AddNewFeatureGroupModalCtrl = function($scope,
									$modalInstance) {

								$scope.newFeatureGroup = {
									name : ""
								};

								$scope.ok = function() {
									$modalInstance
											.close($scope.newFeatureGroup.name);
								};

								$scope.cancel = function() {
									$modalInstance.dismiss('cancel');
								};
							};

							// ////////////////////////////////////
							// Agile Backlog
							// ////////////////////////////////////
							$scope.createBacklog = function() {
								var modalInstance = $modal.open({
									templateUrl : 'createBacklog.html',
									controller : CreateBacklogModalCtrl,
								});

								modalInstance.result
										.then(function(backlogName) {
											AgileEndpoint.createBacklog(
													$scope.selectedTeam,
													backlogName).$promise
													.then(function(data) {
														$scope.selectedTeam.backlog = data.name;
													});
										});
							}

							var CreateBacklogModalCtrl = function($scope,
									$modalInstance) {

								$scope.newBacklog = {
									name : ""
								};

								$scope.ok = function() {
									$modalInstance
											.close($scope.newBacklog.name);
								};

								$scope.cancel = function() {
									$modalInstance.dismiss('cancel');
								};
							};

							$scope.enterBacklog = function() {
								var modalInstance = $modal.open({
									templateUrl : 'enterBacklog.html',
									controller : CnterBacklogModalCtrl,
								});

								modalInstance.result
										.then(function(backlogUuid) {
											AgileEndpoint.enterBacklog(
													$scope.selectedTeam,
													backlogUuid).$promise
													.then(function(data) {
														$scope.selectedTeam.backlogUuid = data.uuid;
														$scope.selectedTeam.backlog = data.name;
													});
										});
							}

							var CnterBacklogModalCtrl = function($scope,
									$modalInstance) {

								$scope.backlog = {
									name : ""
								};

								$scope.ok = function() {
									$modalInstance.close($scope.backlog.uuid);
								};

								$scope.cancel = function() {
									$modalInstance.dismiss('cancel');
								};
							};

							$scope.refresh = function() {
								$scope.isLoaded = "";
								AgileEndpoint.getTeamSingle($scope.team).$promise
										.then(function(data) {
											$scope.selectedTeam = data;
											$scope.teamName = data.name;
											$scope.updateSprints();
											$scope.updateFeatureGroups();
											AgileEndpoint
													.getBacklog($scope.selectedTeam).$promise
													.then(function(data) {
														if (data && data.name) {
															$scope.selectedTeam.backlog = data.name;
															$scope.selectedTeam.backlogUuid = data.uuid;
														}
													});
											LayoutService
													.resizeElementHeight("sprintConfigTable");
											LayoutService
													.resizeElementHeight("featureGroupConfigTable");
											LayoutService.refresh();
											$scope.isLoaded = "true";
										});
							}

							$scope.refresh();

							// COMMON MENU COPIED TO ALL JS
							$scope.openConfigForTeam = function(team) {
								window.location.assign("main#/config?team="
										.concat($scope.team.id))
							}

							$scope.openKanbanForTeam = function(team) {
								window.location.assign("main#/kanban?team="
										.concat($scope.team.id))
							}

							$scope.openBurndownForTeam = function(team) {
								window.location
										.assign("main#/report?team="
												.concat($scope.team.id)
												.concat(
														"&reporttype=burndown&reportname=Burn-Down"))
							}

							$scope.openBurnupForTeam = function(team) {
								window.location
										.assign("main#/report?team="
												.concat($scope.team.id)
												.concat(
														"&reporttype=burnup&reportname=Burn-Up"))
							}

							$scope.openBacklogForTeam = function(team) {
								window.location.assign("main#/backlog?team="
										.concat($scope.team.id).concat(
												"&default=backlog"))
							}

							$scope.openNewTaskForTeam = function(team) {
								window.location.assign("main#/newTask?team="
										.concat($scope.team.id))
							}

							$scope.openSprintForTeam = function(team) {
								window.location.assign("main#/sprint?team="
										.concat($scope.team.id).concat(
												"&default=sprint"))
							}

							$scope.openSummaryForTeam = function(team) {
								window.location
										.assign("main#/report?team="
												.concat($scope.team.id)
												.concat(
														"&reporttype=summary&reportname=Summary"))
							}

							$scope.openDataForTeam = function(team) {
								window.location
										.assign("main#/report?team="
												.concat($scope.team.id)
												.concat(
														"&reporttype=data&reportname=Data"))
							}
							// COMMON MENU COPIED TO ALL JS

						} ]);
