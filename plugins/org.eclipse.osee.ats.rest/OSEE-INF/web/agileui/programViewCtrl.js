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
/**
 * Uses Angular-Tree-Widget - http://www.bestjquery.com/?291XT9RI
 */
angular.module('AgileApp').controller(
		'ProgramViewCtrl',
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
				function($scope, AgileEndpoint, Menu, Global, $resource,
						$window, $modal, $filter, $routeParams) {

					$scope.program = {};
					$scope.program.id = $routeParams.program;
					$scope.program.name = "";

					AgileEndpoint.getProgramToken($scope.program).$promise
					.then(function(data) {
						$scope.program.name = data.name;
					});

					AgileEndpoint.getProgramAtw($scope.program).$promise
					.then(function(data) {
						$scope.treeNodes = data;
					});

					Global.loadActiveProgsTeams($scope, AgileEndpoint, Menu);

				} ]);
