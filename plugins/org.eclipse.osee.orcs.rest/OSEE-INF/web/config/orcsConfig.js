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
var app = angular.module('app', [ 'checklist-model', 'ngResource' ]);

app.controller("appCtrl", [
		'$scope',
		'$http',
		'$resource',
		function($scope, $http, $resource) {

			$scope.formData = {
				version : '',
				sheets : ''
			};
			$scope.message = '';

			$scope.execute = function() {
				$scope.run();
			}

			$scope.loadSheets = function() {
				$http.get('/orcs/types/config/sheet').then(function(response) {
					$scope.sheets = response.data;
				});
			}

			$scope.run = function() {
				$scope.message = '';
				var url = "/orcs/types/config/sheet";
				var data = {};
				if (!$scope.formData.version
						&& !$scope.formData.sheets.length > 0) {
					$scope.message = "ERROR: Must enter version and sheets";
				} else {
					$scope.message = "Processing...";
					data.versionNum = $scope.formData.version;
					data.sheets = [];
					var x = 0;
					for (x = 0; x < $scope.formData.sheets.length; x++) {
						data.sheets[x] = {};
						data.sheets[x].attrId = $scope.formData.sheets[x];
					}
					$http({
						method : 'POST',
						url : url,
						data : data,
						headers : {
							'Accept' : 'application/json',
							'Content-Type' : 'application/json'
						}
					}).success(function(data, status, headers, config) {
						$scope.message += '\n\nCompleted';
					}).error(function(data, status, headers, config) {
						$scope.message += '\n\n' + data.message;
					});
				}
			}

			$scope.loadSheets();

		} ]);
