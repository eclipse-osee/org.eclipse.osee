/**
 * Agile Config Controller
 */
angular
		.module('AgileApp')
		.controller(
				'BacklogCtrl',
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
							$scope.default = $routeParams.default;
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
										AgileFactory.getBacklogItems(selected).$promise
												.then(function(data) {
													$scope.tasks = data;
													$scope.count = $scope.tasks.length;
													LayoutService.resizeElementHeight("taskTable");
													LayoutService.refresh();
												});
									} else {
										AgileFactory.getSprintItems(selected).$promise
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

							// add backlog and sprints to pulldown and set default if specified as query parameter
							$scope.setupItemsPulldown = function() {
								var loadingModal = PopupService
										.showLoadingModal();
								AgileFactory.getTeamSingle($scope.team).$promise
										.then(function(data) {
											$scope.selectedTeam = data;
											var activeItems = [];
											AgileFactory
													.getBacklog($scope.selectedTeam).$promise
													.then(function(data) {
														if (data && data.name) {
															$scope.selectedTeam.backlog = data.name;
															$scope.selectedTeam.backlogUuid = data.uuid;
															data.isBacklog = true;
															// add backlog first
															var defaultBacklogItem = data;
															activeItems
																	.push(data);

															AgileFactory
																	.getSprints($scope.team).$promise
																	.then(function(
																			data) {
																		var defaultSprintItem = null;
																		for ( var index in data) {
																			var sprint = data[index];
																			if (sprint.active) {
																				if (!(defaultSprintItem)) {
																					defaultSprintItem = sprint;
																				}
																				sprint.isBacklog = false;
																				activeItems
																						.push(sprint);
																			}
																		}
																		$scope.activeItems = activeItems;

																		if ($scope.default == "sprint") {
																			$scope.selectedItem = defaultSprintItem;
																		}else if ($scope.default = "backlog") {
																			$scope.selectedItem = defaultBacklogItem;
																		}
																		loadingModal
																				.close();
																	});

														}
													});
										});
							}


							$scope.setupItemsPulldown();

						} ]);
