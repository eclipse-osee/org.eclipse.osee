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
app.controller("mainController", ["$scope", "$rootScope", "$localStorage","$location", "$window","Account",
	function($scope, $rootScope, $localStorage, $location, $window, Account) {
		$scope.links = [ { ref: '', name: 'Home'}, { ref: 'user', name: 'User'}, { ref: 'admin', name: 'Admin'}];
		
		Account.get({
		}, function(data){
				$localStorage.cachedName = data.name;
				$rootScope.cachedName = data.name;
		});		
		
		$rootScope.cachedName = $localStorage.cachedName;
	}]);