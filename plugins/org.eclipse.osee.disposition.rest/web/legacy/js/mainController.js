app.controller('mainController', [
		'$rootScope',
		'$localStorage',
	    '$modal',

		function($rootScope, $localStorage, $modal) {
			$rootScope.type = 'testScript';
			
			$rootScope.searchResults = [];
			
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
			
	        // Help Modal
			$rootScope.showHelpModal = function() {
	            var modalInstance = $modal.open({
	                templateUrl: 'helpModal.html',
	                size: 'large',
	                windowClass: 'needsRerunModal'
	            });
	        }
			
		}]);
		
		