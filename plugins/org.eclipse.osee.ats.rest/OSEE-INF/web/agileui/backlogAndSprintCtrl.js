/**
 * Agile Config Controller
 */
angular
		.module('AgileApp')
		.controller(
				'BacklogAndSprintCtrl',
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
							$scope.backlog = {};
							$scope.sprint = {};
							$scope.team.id = $routeParams.team;
							$scope.defaultItem = $routeParams.default;
							$scope.count = "--";

							
							// ////////////////////////////////////
							// Backlog table
							// ////////////////////////////////////

							var atsIdCellTemplate = '<div class="ngCellText" ng-class="col.colIndex()">'
									+ '  <a href="/ats/ui/action/{{row.getProperty(col.field)}}">{{row.getProperty(col.field)}}</a>'
									+ '</div>';

							$scope.tasksGridOptions = {
								data : 'tasks',
								enableHighlighting : true,
								enableColumnResize : true,
								multiSelect : false,
								showFilter : true,
								sortInfo : {
									fields : [ 'order' ],
									directions : [ 'asc' ]
								},
								columnDefs : [ {
									field : 'order',
									displayName : 'Order',
									width : 50
								}, {
									field : 'state',
									displayName : 'State',
									width : 85
								}, {
									field : 'name',
									displayName : 'Name',
									width : 310
								}, {
									field : 'changeType',
									displayName : 'Change Type',
									width : 50
								}, {
									field : 'assignees',
									displayName : 'Assignees',
									width : 160
								}, {
									field : 'featureGroups',
									displayName : 'Feature Group',
									width : 150
								}, {
									field : "sprint",
									displayName : 'Sprint',
									width : 60,
								}, {
									field : "unPlannedWork",
									displayName : 'UnPlanned',
									width : 20,
								}, {
									field : "backlog",
									displayName : 'Backlog',
									width : 60,
								}, {
									field : "version",
									displayName : 'Version',
									width : 60,
								}, {
									field : "atsId",
									displayName : 'ATS Id',
									width : 90,
									cellTemplate : atsIdCellTemplate
								}, {
									field : "createDate",
									displayName : 'Created Date',
									width : 90,
								}, {
									field : "compCancelDate",
									displayName : 'Comp/Cancel Date',
									width : 90,
								}, {
									field : "notes",
									displayName : 'Notes',
									width : 90,
								} ]
							};


							var getTasks = function() {
								var selected = $scope.selectedItem;

								if (selected) {
									if (selected.isBacklog) {
										AgileFactory.getBacklogItems($scope.team).$promise
												.then(function(data) {
													$scope.tasks = data;
													$scope.count = $scope.tasks.length;
													LayoutService.resizeElementHeight("taskTable");
													LayoutService.refresh();
												});
									} else {
										AgileFactory.getSprintItems($scope.team, selected).$promise
												.then(function(data) {
													$scope.tasks = data;
													$scope.count = $scope.tasks.length;
													LayoutService.resizeElementHeight("taskTable");
													LayoutService.refresh();
												});
									}
								}
							}

							// populate model with items
							$scope.$watch("selectedItem", function() {
								if ($scope.selectedItem) {
									getTasks();
								}
							});

							// Only available and called when sprint is selected
							$scope.updateSprint = function() {
								var loadingModal = null;
								try {
								 loadingModal = PopupService.showLoadingModal(); 
								 $scope.sprint.id = $scope.selectedItem.id;
								 AgileFactory.updateSprint($scope.team, $scope.sprint).$promise
										.then(function(data) {
											// open new tab to new action
											if (data.results.numErrors > 0) {
												alert(data.results.results);
											} 
											loadingModal.close();
										}).catch((err) => {
											loadingModal.close();
											alert(err);
										});
								} finally {
									if(loadingModal) {
										loadingModal.close();
									}
								}
							};
							
							// add backlog and sprints to pulldown and set
							// default if specified as query parameter
							$scope.setupItemsPulldown = function() {
								var loadingModal = PopupService
										.showLoadingModal();
								AgileFactory
										.getBacklogToken($scope.team).$promise
										.then(function(data) {
											if (data && data.name) {
												$scope.backlog.name = data.name;
												$scope.backlog.id = data.id;
												var item = $scope.backlog;
												item.isBacklog = true;
												// add backlog first
												var defaultBacklogItem = item;
												var activeItems = [];
												activeItems
														.push(item);

												// get active sprints
												AgileFactory
														.getSprintsTokens($scope.team).$promise
														.then(function(
																data) {
															var defaultSprintItem = null;
															for (i = 0; i < data.length; i++) { 
																var sprint = data[i];
																if (!defaultSprintItem) {
																	defaultSprintItem = sprint;
																}
																sprint.isBacklog = false;
																activeItems
																		.push(sprint);
															}
															$scope.activeItems = activeItems;

															if ($scope.defaultItem == "sprint") {
																$scope.selectedItem = defaultSprintItem;
															}else if ($scope.defaultItem = "backlog") {
																$scope.selectedItem = defaultBacklogItem;
															}
															loadingModal
																	.close();
														});

											}
										});
							}


							$scope.setupItemsPulldown();

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
								window.location.assign("main#/report?team="
										.concat($scope.team.id).concat("&reporttype=burndown&reportname=Burn-Down"))
							}

							$scope.openBurnupForTeam = function(team) {
								window.location.assign("main#/report?team="
										.concat($scope.team.id).concat("&reporttype=burnup&reportname=Burn-Up"))
							}

							$scope.openBacklogForTeam = function(team) {
								window.location.assign("main#/backlog?team="
										.concat($scope.team.id).concat("&default=backlog"))
							}

							$scope.openNewActionForTeam = function(team) {
								window.location.assign("main#/newAction?team="
										.concat($scope.team.id))
							}

							$scope.openSprintForTeam = function(team) {
								window.location.assign("main#/sprint?team="
										.concat($scope.team.id).concat("&default=sprint"))
							}

							$scope.openSummaryForTeam = function(team) {
								window.location.assign("main#/report?team="
										.concat($scope.team.id).concat("&reporttype=summary&reportname=Summary"))
							}

							$scope.openDataForTeam = function(team) {
								window.location.assign("main#/report?team="
										.concat($scope.team.id).concat("&reporttype=data&reportname=Data"))
							}
							// COMMON MENU COPIED TO ALL JS

						} ]);
