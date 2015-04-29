app.controller("mainController", ["$scope", "$rootScope",
	function($scope, $rootScope) {
		$scope.links = [ { ref: '', name: 'Home'}, { ref: 'user', name: 'User'}, { ref: 'admin', name: 'Admin'}];
		$rootScope.type = "test_script";
	}]);