
angular.module('AgileApp').controller(
		'NewTaskCtrl',
		[
				'$scope',
				'AgileEndpoint',
				'Global',
				'$resource',
				'$window',
				'$filter',
				'$routeParams',
				'LayoutService',
				function($scope, AgileEndpoint, Global, $resource, $window,
						$filter, $routeParams, LayoutService) {

					$scope.team = {};
					$scope.team.id = $routeParams.team;
					$scope.team.backlog = "";
					$scope.team.sprint = "";
					$scope.action = {};
					$scope.loadingImg = Global.loadingImg;
					$scope.creating = false;
 
					AgileEndpoint.getTeamToken($scope.team).$promise
						.then(function(data) {
							$scope.team.name = data.name;
					});

					AgileEndpoint.getTeamAis($scope.team).$promise
						.then(function(data) {
							$scope.validAis = data;
							$scope.action.actionableItem = "";
					});
					AgileEndpoint.getSprintsTokens($scope.team).$promise
						.then(function(data) {
							$scope.validSprints = data;
							$scope.action.sprint = "";
					});
					AgileEndpoint.getFeatureGroups($scope.team).$promise
						.then(function(data) {
							$scope.validFeatureGroups = data;
							$scope.action.featureGroup = "";
					});
						AgileEndpoint.getWorkPackages($scope.team).$promise
						.then(function(data) {
							$scope.validWorkPackages = data;
							$scope.action.workPackage = "";
					});

					$scope.createItem = function() {
						$scope.creating = true;
						$scope.action.asUserId = 99999999;
						$scope.action.agileTeam = $scope.team.id;
						$scope.action.createdByUserId = 99999999;
						if ($scope.actionableItem) {
							$scope.action.aiIds = [];
							$scope.action.aiIds[0] = $scope.actionableItem;
						}
						AgileEndpoint.createTask($scope.action).$promise
								.then(function(data) {
									// open new tab to new action
									if (data.results.numErrors > 0) {
										alert(data.results.results);
										$scope.creating = false;
									} else {
										$scope.creating = false;
										var url = "/ats/ui/action/" + data.teamWfs[0];
										var win = window.open(url, '_blank');
										if (win) {
											// Browser has allowed it to be
											// opened
											win.focus();
										} else {
											// Browser has blocked it
											alert('Task Created');
										}
										$scope.creating = false;
									}
								}).catch((err) => {
									alert(err);
									$scope.creating = false;
								});
						
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

					$scope.openNewTaskForTeam = function(team) {
						window.location.assign("main#/newTask?team="
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
