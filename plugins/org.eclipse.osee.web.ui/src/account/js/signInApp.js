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
						if(data.startsWith('<!--ERROR PAGE-->', 0)) {
							var currentUrl = window.location.href;
							var errorPage = currentUrl.replace("login", "loginError");
							location.assign(errorPage);
						} else {
							location.assign(store.continueTo);
						}
						
					}).error(function(data, status, headers, config) {
						alert("Failed to Log in")
					});
				};

			} ]);
})();