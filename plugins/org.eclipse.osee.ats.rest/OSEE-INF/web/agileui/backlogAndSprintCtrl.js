/**
 * Agile Config Controller
 */
angular
		.module('AgileApp')
		.controller(
				'BacklogAndSprintCtrl',
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
							$scope.backlog = {};
							$scope.sprint = {};
							$scope.team.id = $routeParams.team;
							$scope.defaultItem = $routeParams.default;
							$scope.count = "--";
							$scope.loadingImg = Global.loadingImg;
							
							// ////////////////////////////////////
							// Backlog and Sprint table
							// ////////////////////////////////////

							var atsIdCellTemplate = '<div class="ui-grid-cell-contents"><a href="/ats/ui/action/{{row.entity.atsId}}" ' +
								' target="_blank">{{row.entity.atsId}}</a></div>';

							$scope.tasksGridOptions = {
								data : 'tasks',
								enableHighlighting : true,
                                enableColumnResize : true,
								enableRowSelection: true,
								enableRowHeaderSelection: false,
								modifierKeysToMultiSelect: true,
								multiSelect: false,
								showFilter : true,
								onRegisterApi: function(gridApi){
								  $scope.gridApi = gridApi;
								  gridApi.selection.on.rowSelectionChanged($scope,function(rows){
								  	if (gridApi.selection.getSelectedRows().length == 1) {
								  		$scope.selectedTask = gridApi.selection.getSelectedRows()[0];
								  	}
								  });
								},
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
									field : 'agilePoints',
									displayName : 'Points',
									width : 50
								}, {
									field : 'assigneesOrImplementers',
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


							AgileEndpoint.getTeamToken($scope.team).$promise
									.then(function(data) {
										$scope.team.name = data.name;
									});


							var getTasks = function() {
								var selected = $scope.selectedItem;

								if (selected) {
									if (selected.isBacklog) {
										AgileEndpoint.getBacklogItems($scope.team).$promise
												.then(function(data) {
													if (data.tasks && data.tasks.length == 0) {
														$scope.notasks = true;
													} else {
														$scope.tasks = data;
														$scope.count = $scope.tasks.length;
														LayoutService.resizeElementHeight("taskTable");
														LayoutService.refresh();
													}
												});
									} else {
										AgileEndpoint.getSprintItems($scope.team, selected).$promise
											.then(function(data) {
												var result = data;
												if (result && result.length == 0) {
													$scope.notasks = true;
												} else {
													$scope.tasks = data;
													$scope.count = $scope.tasks.length;
													LayoutService.resizeElementHeight("taskTable");
													LayoutService.refresh();
												}
											});
										AgileEndpoint.getSprintConfig($scope.team, selected).$promise
										.then(function(data) {
											var config = {};
											config.startDate = new Date(data.startDate);
											config.endDate = new Date(data.endDate);
											config.plannedPoints = data.plannedPoints;
											config.unPlannedPoints = data.unPlannedPoints;
											$scope.sprint.config = config;
										});
									}
								}
							}

							// populate model with items
							$scope.$watch("selectedItem", function() {
								if ($scope.selectedItem) {
									$scope.notasks = "";
									$scope.tasks = null;
									getTasks();
								}
							});
							
							$scope.onDblClick = function() {
								var selected = $scope.selectedTask;
								if (selected) {
									var url = selected.link;
									var win = window.open(url, '_blank');
  									win.focus();
								}
							}

							// Only available and called when sprint is selected
							$scope.updateSprintConfig = function() {
								try {
								 $scope.sprint.id = $scope.selectedItem.id;
								 AgileEndpoint.updateSprintConfig($scope.team, $scope.sprint).$promise
										.then(function(data) {
											if (data.results.numErrors > 0) {
												alert(data.results.results);
											} else {
												$scope.sprint.config = data;
												alert("configuration saved");
											}
										}).catch((err) => {
											alert(err);
										});
								} catch (err) {
									alert(err);
								}
							};
							
							// add backlog and sprints to pulldown and set
							// default if specified as query parameter
							AgileEndpoint
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
											AgileEndpoint
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
													});

										}
									});
							
							// Copied through all controlers; ensure all are
							// same
							$scope.openBacklogForTeam = Menu.openBacklogForTeam;
							$scope.openSprintForTeam = Menu.openSprintForTeam;
							$scope.openKanbanForTeam = Menu.openKanbanForTeam;
							$scope.openNewTaskForTeam = Menu.openNewTaskForTeam;
							$scope.openBurndownForTeam = Menu.openBurndownForTeam;
							$scope.openBurnupForTeam = Menu.openBurnupForTeam;
							$scope.openSummaryForTeam = Menu.openSummaryForTeam;
							$scope.openDataForTeam = Menu.openDataForTeam;

						} ]);
