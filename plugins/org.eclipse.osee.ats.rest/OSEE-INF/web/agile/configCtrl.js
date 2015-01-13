/**
 * Agile Config Controller
 */
angular
		.module('AgileApp')
		.controller(
				'ConfigCtrl',
				[
						'$scope',
						'AgileFactory',
						'$resource',
						'$window',
						'$modal',
						'$filter',
						'$routeParams',
						function($scope, AgileFactory, $resource, $window,
								$modal, $filter, $routeParams) {

							// ////////////////////////////////////
							// Loading Modal
							// ////////////////////////////////////
							$scope.showLoadingModal = function() {
								var modalInstance = $modal.open({
									templateUrl : 'loadingModal.html',
									size : 'sm',
									windowClass : 'needsRerunModal',
									backdrop : 'static'
								});

								return modalInstance;
							}

							// ////////////////////////////////////
							// Agile Team table
							// ////////////////////////////////////
							$scope.selectedTeams = [];

							var openTeamTmpl = '<button class="btn btn-default btn-sm" ng-click="selectTeam(row.entity)">Config</button>';
							var deleteTeamGroupImpl = '<button class="btn btn-default btn-sm" confirmed-click="deleteTeam(row.entity)" ng-confirm-click="Delete Agile Team?\n\nNOTE: This will delete all Agile Team\'s configuration items.">Delete</button>';

							$scope.teamGridOptions = {
								data : 'teams',
								enableHighlighting : true,
								enableColumnResize : true,
								multiSelect : false,
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
									field : 'Name',
									displayName : 'Name',
									width : 290
								}, {
									field : "config",
									displayName : 'Config',
									width : 60,
									cellTemplate : openTeamTmpl
								}, {
									field : "config",
									displayName : 'Delete',
									width : 60,
									cellTemplate : deleteTeamGroupImpl
								} ]
							};

							$scope.updateTeams = function() {
								$scope.teams = null;
								var loadingModal = $scope.showLoadingModal();
								AgileFactory.getTeams().$promise.then(function(
										data) {
									$scope.teams = data;
									loadingModal.close();
								});
							}

							$scope.selectTeam = function(team) {
								$scope.teams = null;
								var loadingModal = $scope.showLoadingModal();
								AgileFactory.getTeamSingle(team).$promise
										.then(function(data) {
											$scope.selectedTeam = data[0];
											$scope.featureGroups = $scope.selectedTeam.featureGroups;
											loadingModal.close();
										});
							}

							$scope.deleteTeam = function(team) {
								AgileFactory.deleteTeam(team).$promise
										.then(function(data) {
											$scope.updateTeams();
										});
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

							$scope.updateTeams();

							// ////////////////////////////////////
							// Agile Feature Group Table
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
									field : 'Name',
									displayName : 'Name',
									width : 300
								}, {
									field : "config",
									displayName : 'Delete',
									width : 60,
									cellTemplate : deleteFeatureGroupImpl
								} ]
							};

							$scope.updateFeatureGroups = function() {
								var team = $scope.selectedTeam;
								$scope.selectedTeam = null;
								$scope.selectTeam(team);
							}

							$scope.deleteFeatureGroup = function(featureGroup) {
								AgileFactory.deleteFeatureGroup(featureGroup).$promise
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
											AgileFactory.addNewFeatureGroup(
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

						} ]);
