var app = angular.module('app', [ 'checklist-model', 'ngResource', 'ui.grid',
		'ui.grid.resizeColumns' ]);

app
		.controller(
				"appCtrl",
				[
						'$scope',
						'$http',
						'$resource',
						function($scope, $http, $resource) {

							$scope.selectedBranch = {};
							$scope.branches = [];
							$scope.selectedBranch.id = getQueryParameterByName('branch');
							$scope.itemsGridOptions = [];
							$scope.itemsGridOptions.data = [];

							// //////////////////////////////////////
							// Load branch combo regardless of which "page"
							// //////////////////////////////////////
							$scope.loadBranches = function() {
								$http.get('/orcs/applicui/branches').then(
										function(response) {
											$scope.branches = response.data;
											$scope.message = '';
											$scope.setSelectedBranch();
										});
							}

							$scope.loadBranches();

							// //////////////////////////////////////
							// Set selected branch
							// //////////////////////////////////////
							$scope.setSelectedBranch = function() {
								if ($scope.selectedBranch.id) {
									for (x = 0; x < $scope.branches.length; x++) {
										var branch = $scope.branches[x];
										var id = branch.id;
										id = id.replace(/"/g,""); // remove all quotes
										if (id == $scope.selectedBranch.id) {
											$scope.selectedBranch = branch;
											break;
										}
									}
								}
							}

							// //////////////////////////////////////
							// Handle branch selection
							// //////////////////////////////////////
							$scope.handleBranchSelection = function() {
								if (!$scope.selectedBranch) {
									$scope.message = 'Must Select a Branch';
								} else {
									$scope.message = 'Selected branch '
											+ $scope.selectedBranch;
									var url = '/orcs/applicui/config/main.html?branch='
											+ $scope.selectedBranch.id;
									window.location.replace(url);
								}
							}

							// //////////////////////////////////////
							// Load Table if selectedBranch.id
							// //////////////////////////////////////
							if ($scope.selectedBranch.id) {

								$scope.loadTable = function() {
									$http
											.get(
													'/orcs/applicui/branch/'
															+ $scope.selectedBranch.id)
											.then(
													function(response) {
														$scope.config = response.data;
														$scope.message = '';
														var columnName = '';
														for (i = 0; i < $scope.config.variantsOrdered.length; i++) {
															columnName = $scope.config.variantsOrdered[i];
															$scope.columns
																	.push({
																		field : columnName
																				.toLowerCase(),
																		displayName : columnName,
																		enableSorting : false,
																		width : 125
																	});
														}
														$scope.itemsGridOptions.columnDefs = $scope.columns;
														$scope.gridApi.grid
																.refresh();
														$scope.data = $scope.config.featureToValueMaps;
													});
								}

								$scope.itemsGridOptions = {
									data : 'data',
									enableHighlighting : true,
									enableGridMenu : true,
									enableColumnResize : true,
									enableColumnReordering : true,
									enableRowSelection : true,
									showTreeExpandNoChildren : false,
									enableRowHeaderSelection : false,
									showFilter : true,
									columnDefs : $scope.columns,
									onRegisterApi : function(gridApi) {
										$scope.gridApi = gridApi;
									}
								};

								$scope.columns = [ {
									field : 'feature',
									displayName : 'Feature',
									enableSorting : false,
									width : 125
								} ];

								$scope.loadTable();

							}

							// //////////////////////////////////////
							// Utilities
							// //////////////////////////////////////
							$scope.home = function() {
								var url = '/orcs/applicui/main.html';
								window.location.replace(url);
							}

							function getQueryParameterByName(name, url) {
								if (!url)
									url = window.location.href;
								name = name.replace(/[\[\]]/g, '\\$&');
								var regex = new RegExp('[?&]' + name
										+ '(=([^&#]*)|&|#|$)'), results = regex
										.exec(url);
								if (!results)
									return null;
								if (!results[2])
									return '';
								return decodeURIComponent(results[2].replace(
										/\+/g, ' '));
							}
						} ]);
