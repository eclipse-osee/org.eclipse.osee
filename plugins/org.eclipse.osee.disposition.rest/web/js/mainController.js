app.controller('mainController', [
		'$rootScope',
		'$cookieStore',

		function($rootScope, $cookieStore) {
			$rootScope.cachedName = "Need To Log In";
			
			$rootScope.cachedName = $cookieStore.get('cachedName');
			while($rootScope.cachedName == null || $rootScope.cachedName == "") {
				var nameEnter=prompt("Please enter your name","");
				$cookieStore.put('cachedName', nameEnter);
				$rootScope.cachedName = nameEnter;
			}
		}]);
		
		