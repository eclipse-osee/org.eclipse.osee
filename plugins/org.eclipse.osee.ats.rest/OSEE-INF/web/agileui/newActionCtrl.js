/**
 * Agile Config Controller
 */
angular.module('AgileApp').controller(
		'NewActionCtrl',
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
				function($scope, AgileFactory, $resource, $window, $modal,
						$filter, $routeParams, LayoutService, PopupService) {

					$scope.team = {};
					$scope.team.id = $routeParams.team;
					$scope.team.backlog = "";
					$scope.team.sprint = "";
					$scope.action = {};
 
					AgileFactory.getTeamToken($scope.team).$promise
						.then(function(data) {
							$scope.team.name = data.name;
					});

					AgileFactory.getTeamAis($scope.team).$promise
						.then(function(data) {
							$scope.validAis = data;
							$scope.action.actionableItem = "";
					});
					AgileFactory.getSprintsTokens($scope.team).$promise
						.then(function(data) {
							$scope.validSprints = data;
							$scope.action.sprint = "";
					});
					AgileFactory.getFeatureGroups($scope.team).$promise
						.then(function(data) {
							$scope.validFeatureGroups = data;
							$scope.action.featureGroup = "";
					});
						AgileFactory.getWorkPackages($scope.team).$promise
						.then(function(data) {
							$scope.validWorkPackages = data;
							$scope.action.workPackage = "";
					});

					$scope.createItem = function() {
						var loadingModal = null;
						try {
						$scope.action.asUserId = 99999999;
						$scope.action.agileTeam = $scope.team.id;
						$scope.action.createdByUserId = 99999999;
						if ($scope.actionableItem) {
							$scope.action.aiIds = [];
							$scope.action.aiIds[0] = $scope.actionableItem;
						}
						 loadingModal = PopupService.showLoadingModal(); 
						AgileFactory.createItem($scope.action).$promise
								.then(function(data) {
									// open new tab to new action
									if (data.results.numErrors > 0) {
										alert(data.results.results);
									} else {
										var url = "/ats/ui/action/" + data.teamWfs[0];
										var win = window.open(url, '_blank');
										if (win) {
											// Browser has allowed it to be
											// opened
											win.focus();
										} else {
											// Browser has blocked it
											alert('Action Created');
										}
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
