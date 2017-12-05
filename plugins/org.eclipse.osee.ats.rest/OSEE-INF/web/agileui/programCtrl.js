/**
 * Uses Angular-Tree-Widget - http://www.bestjquery.com/?291XT9RI
 */
angular
		.module('AgileApp')
		.controller(
				'ProgramCtrl',
				[
						'$scope',
						'AgileEndpoint',
						'Menu',
						'Global',
						'LayoutService',
						'$resource',
						'$window',
						'$modal',
						'$filter',
						'$routeParams',
						'uiGridTreeViewConstants',
						'localStorageService',
						'$timeout',
						function($scope, AgileEndpoint, Menu, Global,
								LayoutService, $resource, $window, $modal,
								$filter, $routeParams, uiGridTreeViewConstants,
								localStorageService, $timeout) {

							$scope.program = {};
							$scope.program.id = $routeParams.program;
							$scope.program.name = "";

							$scope.itemsGridOptions = {
								data : 'data',
								enableHighlighting : true,
								enableGridMenu : true,
								enableColumnResize : true,
								enableColumnReordering : true,
								enableRowSelection : true,
								showTreeExpandNoChildren : false,
								enableRowHeaderSelection : false,
								modifierKeysToMultiSelect : true,
								enableFiltering : true,
								multiSelect : false,
								showFilter : true,
								programBacklogItemSectionOpen : false,
								onRegisterApi : function(gridApi) {
									$scope.gridApi = gridApi;
									gridApi.selection.on
											.rowSelectionChanged(
													$scope,
													function(rows) {
														if (gridApi.selection
																.getSelectedRows().length == 1) {
															$scope.selectedTask = gridApi.selection
																	.getSelectedRows()[0];
															updateEnablement();
														} else {
															$scope.selectedTask = null;
														}
													});
									// Setup events so we're notified when grid
									// state changes.
									$scope.gridApi.colMovable.on
											.columnPositionChanged($scope,
													saveState);
									$scope.gridApi.colResizable.on
											.columnSizeChanged($scope,
													saveState);
									$scope.gridApi.core.on
											.columnVisibilityChanged($scope,
													saveState);
									$scope.gridApi.core.on.filterChanged(
											$scope, saveState);
									$scope.gridApi.core.on.sortChanged($scope,
											saveState);

									// Restore previously saved state.
									restoreState();

								},
								columnDefs : [
										{
											field : 'image',
											displayName : ' ',
											enableSorting : false,
											width : 24,
											cellTemplate : "<img ng-src=\"{{grid.getCellValue(row, col)}}\" lazy-src>"
										}, {
											field : 'name',
											displayName : 'Title',
											width : 450
										}, {
											field : 'shortType',
											displayName : 'Type',
											width : 85
										}, {
											field : 'agilePoints',
											displayName : 'Points',
											width : 50
										}, {
											field : 'assigneesOrImplementers',
											displayName : 'Assignees',
											width : 160
										} ]
							};

							function saveState() {
								var state = $scope.gridApi.saveState.save();
								localStorageService.set('gridState', state);
							}

							function restoreState() {
								$timeout(function() {
									var state = localStorageService
											.get('gridState');
									if (state) {
										$scope.gridApi.saveState.restore(
												$scope, state);
									}
								});
							}

							AgileEndpoint.getProgramToken($scope.program).$promise
									.then(function(data) {
										$scope.program.name = data.name;
									});

							AgileEndpoint.getProgramItems($scope.program).$promise
									.then(function(data) {
										$scope.items = data;
									});

							var updateEnablement = function() {
								$scope.isPBacklog = false;
								$scope.isPBacklogItem = false;
								$scope.isPBacklogFeature = false;
								$scope.isStory = false;
								$scope.isTask = false;
								if ($scope.selectedTask) {
									var sel = $scope.selectedTask;
									if (sel.shortType == "Program Backlog") {
										$scope.isPBacklog = true;
									} else if (sel.shortType == "Program Backlog Item") {
										$scope.isPBacklogItem = true;
									} else if (sel.shortType == "Program Feature") {
										$scope.isPBacklogFeature = true;
									} else if (sel.shortType == "Story") {
										$scope.isStory = true;
									} else if (sel.shortType == "Task") {
										$scope.isTask = true;
									}
								}
							}

							var getItems = function() {
								
								AgileEndpoint.getImages().$promise
								.then(function(data) {
									$scope.artTypeNameToImageMap = {};
									for (var i = 0; i < data.length; i++) {
										var item = data[i];
										$scope.artTypeNameToImageMap[item.artifactTypeName] = item;
									}
								});
								
								AgileEndpoint.getProgramItems($scope.program).$promise
										.then(function(data) {
											var program = data;
											if (program.items
													&& program.items.length == 0) {
												$scope.noitems = true;
											} else {
												$scope.data = program.items;
												for (var i = 0; i < $scope.data.length; i++) {
													$scope.prepTaskForGrid($scope.data[i]);
												}
												LayoutService
														.resizeElementHeight("itemsTable");
												LayoutService.refresh();
											}
										});
							}

							$scope.prepTaskForGrid = function(task) {
								var level = task.tlevel;
								task.$$treeLevel = level;
								task.shortType = task.type
										.replace("Agile ", "");
								if (!task.image) {
									var image = $scope.artTypeNameToImageMap[task.artifactTypeName];
									if (image) {
										task.image = $scope.image;
									}
								}
							}

							$scope.onDblClick = function() {
								var selected = $scope.selectedTask;
								if (selected) {
									var url = selected.link;
									var win = window.open(url, '_blank');
									win.focus();
								}
							}

							$scope.openProgramBacklogItemSection = function(
									selectedTask) {
								$scope.programBacklogItemSectionOpen = true;
								$scope.item = {};
								$scope.item.location = "First";
							}
							$scope.closeProgramBacklogItemSection = function(
									selectedTask) {
								$scope.programBacklogItemSectionOpen = null;
							}
							$scope.addProgramBacklogItem = function(item) {
								var pbi = {};
								if (item.addAfter) {
									pbi.location = "AfterSelection";
								} else if ($scope.isPBacklogItem) {
									pbi.location = "Selection";
								} else {
									pbi.location = item.location;
								}
								pbi.type = "New";
								pbi.title = item.title;
								pbi.programId = $scope.program.id;
								pbi.selectedId = $scope.selectedTask.id;
								AgileEndpoint.updateProgramBacklogItem(pbi).$promise
										.then(function(data) {
											var result = data;
											if (result.errors) {
												alert(result.results);
												return;
											} else {
												var newItem = result.item;
												newItem.tlevel = 1;
												newItem.type = "Program Backlog Item";
												$scope.prepTaskForGrid(newItem);
												if ($scope.isPBacklog) {
													$scope
															.addAfterSelected(newItem);
												} else if ($scope.isPBacklogItem) {
													var index = $scope.data
															.lastIndexOf($scope.selectedTask);
													if (result.location == "AfterSelection") {
														index = index + 1;
													}
													$scope.data.splice(index,
															0, newItem);
												}
											}
										});
							}

							$scope.addAfterSelected = function(newItem) {
								var index = $scope.data
										.lastIndexOf($scope.selectedTask) + 1;
								$scope.data.splice(index, 0, newItem);
							}
							/**
							 * From startTask, find last sibling at that level
							 * or end if not found
							 */
							$scope.findLastOfLevel = function(startTask) {
								var selIndex = $scope.data
										.lastIndexOf(startTask);
								for (i = selIndex; i < $scope.data.length; i++) {
									var curr = $scope.data[i];
									if (curr.tLevel != startTask.tLevel) {
										return curr;
									}
								}
								return $scope.data.length - 1;
							}

							$scope.deleteProgramBacklogItem = function(item) {
								var dialog = confirm("Delete Program Backlog Item ["
										+ item.name
										+ "] and all related features and stories (tasks will not be deleted)?");
								if (dialog == true) {
									var pbi = {};
									pbi.type = "Delete";
									pbi.programId = $scope.program.id;
									pbi.backlogitemid = $scope.selectedTask.id;
									AgileEndpoint.deleteProgramBacklogItem(pbi).$promise
											.then(function(data) {
												var result = data;
												if (result.errors) {
													alert(result.results);
												} else {
													$scope.deleteSelected();
												}
											});
								}
							}

							$scope.deleteSelected = function() {
								angular.forEach($scope.gridApi.selection
										.getSelectedRows(), function(data,
										index) {
									$scope.data.splice($scope.data
											.lastIndexOf(data), 1);
								});
							}
							
							getItems();

							$scope
									.$watch(
											"selectedTask",
											function() {
												if ($scope.selectedTask == null) {
													$scope.programBacklogItemSectionOpen = null;
													$scope.isPBacklog = null;
													$scope.isPBacklogItem = null;
													$scope.isStory = null;
													$scope.isTask = null;
													$scope.isPBacklogFeature = null;
												}
											});

							Global.loadActiveProgsTeams($scope, AgileEndpoint,
									Menu);

						} ]);
