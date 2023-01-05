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
var app = angular.module('BranchSelectorApp', []);

app.controller("FormController", function($scope, $http) {
	$scope.branches = [];
	$scope.selectedBranch = "0";
	$scope.formData = {
	};

	$http.get('../../branches').then(function(res) {
		$scope.branches = res.data;
	});

});
