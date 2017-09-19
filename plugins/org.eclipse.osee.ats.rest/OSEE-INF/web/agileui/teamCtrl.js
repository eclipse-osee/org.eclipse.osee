/**
 * Agile Config Controller
 */
angular
		.module('AgileApp')
		.controller(
				'TeamCtrl',
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
							$scope.team = {};
							$scope.team.uuid = $routeParams.team;
							$scope.selectedTeam = {};
							$scope.selectedTeam.name = "";
							$scope.selectedTeam.backlog = "";
							$scope.selectedTeam.sprint = "";
							$scope.isLoaded = "";

							var atsIdCellTemplate = '<div class="ngCellText" ng-class="col.colIndex()">'
									+ '  <a href="/ats/ui/action/{{row.getProperty(col.field)}}">{{row.getProperty(col.field)}}</a>'
									+ '</div>';

							$scope.sprintGridOptions = {
								data : 'items',
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
									field : "atsId",
									displayName : 'ATS Id',
									width : 90,
									cellTemplate : atsIdCellTemplate
								} ]
							};

							$scope.openTeam = function(team) {
								window.location.assign("main#/team?team="
										.concat(team.uuid))
							}

							$scope.configTeam = function(team) {
								window.location.assign("main#/config?team="
										.concat(team.uuid))
							}

							$scope.openBacklog = function(team) {
								window.location.assign("main#/backlog?team="
										.concat(team.uuid))
							}

							$scope.refresh = function() {
								$scope.isLoaded = "";
								var loadingModal = PopupService
										.showLoadingModal();
								AgileFactory.getTeamSingle($scope.team).$promise
										.then(function(data) {
											$scope.selectedTeam = data;
											AgileFactory
													.getSprintCurrent($scope.selectedTeam).$promise
													.then(function(data) {
														if (data && data.name) {
															$scope.selectedTeam.sprint = data.name;
															$scope.selectedTeam.sprintUuid = data.uuid;
														}
													});
											AgileFactory
													.getSprintItems($scope.selectedTeam).$promise
													.then(function(data) {
														$scope.items = data;
														$scope.count = $scope.items.length;
														LayoutService
																.resizeElementHeight("sprintTable");
														LayoutService.refresh();
														loadingModal.close();
													});
											$scope.isLoaded = "true";
										});
							}

							$scope.refresh();

						} ]);
