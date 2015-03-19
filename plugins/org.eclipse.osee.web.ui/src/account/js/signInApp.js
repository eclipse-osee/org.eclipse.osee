(function() {

	var app = angular.module('signInApp', [ 'ngRoute' ]);

	app.controller('signInController', [
			'$http',
			'$location',
			function($http, $location) {
				var store = this;
				
				store.formData = {};
				store.params = {};
				
				var getUrl = function() {
					var splitted = $location.absUrl().split('?');
					var url = splitted[0];
					var continueTo = decodeURIComponent(splitted[1]);

					if (splitted.length > 2) {
						var paramsForContinue = decodeURIComponent("?"
								+ splitted[2]);
						continueTo += paramsForContinue;
					}

					return continueTo.replace("continueTo=","");
				}
				
				store.continueTo = getUrl();

				this.submit = function() {
					var header = {
						Accept : 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
						Authorization : 'Basic '
								+ btoa(store.formData.username + ':' + store.formData.password)
					}
					$http({
						url : store.continueTo,
						method : 'GET',
						headers : header
					}).success(function(data, status, headers, config) {
						location.assign(store.continueTo);
					}).error(function(data, status, headers, config) {
						alert("Failed to Log in")
					});
				};

			} ]);
})();