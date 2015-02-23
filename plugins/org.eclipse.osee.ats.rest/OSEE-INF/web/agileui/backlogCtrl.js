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
								$modal, $filter, $routeParams, LayoutService, PopupService) {

							$scope.team = {};
							$scope.team.uuid = $routeParams.team;
							$scope.count = "--";

							// ////////////////////////////////////
							// Backlog table
							// ////////////////////////////////////

							var atsIdCellTemplate = '<div class="ngCellText" ng-class="col.colIndex()">'
									+ '  <a href="/ats/ui/action/{{row.getProperty(col.field)}}">{{row.getProperty(col.field)}}</a>'
									+ '</div>';

							$scope.backlogGridOptions = {
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

							$scope.updateItems = function() {
								var loadingModal = PopupService.showLoadingModal();
								AgileFactory.getBacklogItems($scope.team).$promise
										.then(function(data) {
											$scope.items = data;
											$scope.count = $scope.items.length;
											LayoutService.resizeElementHeight("backlogTable");
											LayoutService.refresh();
											loadingModal.close();
										});
								AgileFactory.getTeamSingle($scope.team).$promise
										.then(function(data) {
											$scope.selectedTeam = data;
											AgileFactory.getBacklog($scope.selectedTeam).$promise
											.then(function(data) {
												if (data && data.name) {
													$scope.selectedTeam.backlog = data.name;
													$scope.selectedTeam.backlogUuid = data.uuid;
												}
											});
										});
							}

							$scope.updateItems();
							
						} ]);
