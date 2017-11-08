
angular.module('AgileApp').controller(
		'NewTaskCtrl',
		[
				'$scope',
				'AgileEndpoint',
				'Menu',
				'Global',
				'$resource',
				'$window',
				'$filter',
				'$routeParams',
				'LayoutService',
				function($scope, AgileEndpoint, Menu, Global, $resource, $window,
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
							if ($scope.validAis.length == 1) {
								$scope.actionableItem = $scope.validAis[0].id;
							}
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
					AgileEndpoint.getTeamMembers($scope.team).$promise
					.then(function(data) {
						$scope.action.assigneesStr = "";
						$scope.action.originatorStr = "";
						$scope.users = data;
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
					
					Global.loadActiveProgsTeams($scope, AgileEndpoint);

					// Copied through all controlers; ensure all are same
					$scope.openBacklogForTeam = Menu.openBacklogForTeam;
					$scope.openSprintForTeam = Menu.openSprintForTeam;
					$scope.openKanbanForTeam = Menu.openKanbanForTeam;
					$scope.openNewTaskForTeam = Menu.openNewTaskForTeam;
					$scope.openBurndownForTeam = Menu.openBurndownForTeam;
					$scope.openBurnupForTeam = Menu.openBurnupForTeam;
					$scope.openSummaryForTeam = Menu.openSummaryForTeam;
					$scope.openDataForTeam = Menu.openDataForTeam;

				} ]);
