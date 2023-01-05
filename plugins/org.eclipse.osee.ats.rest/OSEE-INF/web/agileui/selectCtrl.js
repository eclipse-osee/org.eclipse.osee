/*********************************************************************
* Copyright (c) 2023 Boeing
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     Boeing - initial API and implementation
**********************************************************************/
angular
		.module('AgileApp')
		.controller(
				'SelectCtrl',
				[
						'$scope',
						'AgileEndpoint',
						'Menu',
						'Global',
						'$resource',
						'$window',
						'$modal',
						'$filter',
						'$routeParams',
						function($scope, AgileEndpoint, Menu, Global,
								$resource, $window, $modal, $filter,
								$routeParams) {

							$scope.selectedTeams = [];
							$scope.activeProgsTeams = [];

							Global.loadActiveProgsTeams($scope, AgileEndpoint, Menu);


						} ]);
