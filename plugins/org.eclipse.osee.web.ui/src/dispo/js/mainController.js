app.controller("mainController", ["$scope", "$rootScope", "$localStorage",
	function($scope, $rootScope, $localStorage) {
		$scope.links = [ { ref: '', name: 'Home'}, { ref: 'user', name: 'User'}, { ref: 'admin', name: 'Admin'}];
		
		$rootScope.setUserName = function() {
			$rootScope.attempts = 0;
			while(($rootScope.cachedName == null || $rootScope.cachedName == "") && $rootScope.attempts < 5) {
				var nameEnter=prompt("Please enter your name","");
				$localStorage.cachedName = nameEnter;
				$rootScope.cachedName = nameEnter;
				$rootScope.attempts++;
			}
			if($rootScope>=5) {
				$rootScope.cachedName = "Need To Log In";
			}
			
		}
		
		$rootScope.cachedName = $localStorage.cachedName;
		$rootScope.setUserName();
		
		$rootScope.resetUserName = function() {
			$rootScope.cachedName = null;
			$rootScope.setUserName();
		}
	}]);