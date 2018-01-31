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