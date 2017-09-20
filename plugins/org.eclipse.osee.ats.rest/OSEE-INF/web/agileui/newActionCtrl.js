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
					$scope.team.uuid = $routeParams.team;
					$scope.selectedTeam = {};
					$scope.selectedTeam.name = "";
					$scope.selectedTeam.backlog = "";
					$scope.selectedTeam.sprint = "";
					$scope.isLoaded = "";
					$scope.action = {};

					$scope.refresh = function() {
						$scope.isLoaded = "";
						var loadingModal = PopupService.showLoadingModal();
						AgileFactory.getTeamSingle($scope.team).$promise
								.then(function(data) {
									$scope.selectedTeam = data;
									$scope.teamName = data.name;
									loadingModal.close();
									$scope.isLoaded = "true";
								});
						AgileFactory.getTeamAis($scope.team).$promise
								.then(function(data) {
									$scope.validAis = data;
									$scope.actionableItem = "";
								});
					}

					$scope.reset = function() {
						$scope.action.title = "";
						$scope.action.actionableItems = "";
						$scope.action.description = "";
						$scope.action.changeType = "";
						$scope.action.priority = "";
						$scope.action.needByDate = "";
					}

					$scope.createItem = function() {
						var loadingModal = null;
						try {
						$scope.action.asUserId = 99999999;
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
											// Browser has allowed it to be opened
											win.focus();
										} else {
											// Browser has blocked it
											alert('Action Created; Please allow popups for this site.');
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
					$scope.refresh();

				} ]);
