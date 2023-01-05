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
var app = angular.module('oseeProvider', [ 'osee.directive' ]);

// ----------------------------------------------------------------------------------------------------

'use strict'

var directives = angular.module('osee.directive', []);

directives.directive('osee', [
		'$rootScope',
		'$compile',
		'$http',
		'$location',
		'$templateCache',
		'AccessToken',
		'Profile',
		'Endpoint',
		'$localStorage',
		'$sessionStorage',
		function($rootScope, $compile, $http, $location, $templateCache, AccessToken, Profile, Endpoint, $localStorage, $sessionStorage) {
			var definition = {
				restrict : 'E',
				replace : true,
				scope : {
					site : '@',
					redirectUri : '@',
					links : '='
				}
			};

			definition.link = function postLink(scope, element, attrs, controller) {
				scope.$watch('client', function(value) {
					init(); // sets defaults
					compile(); // compiles the desired
				});

				var init = function() {
					scope.anonymousUser = {
							email : 'anonymous@anonymous.com',
							name : 'Anonymous'
						};
					scope.profileUri ="/accounts/self";
					scope.show = 'none';
					scope.template = '/libs/js/oseeAuth/views/header.html';
					scope.profile = scope.anonymousUser;
					
					scope.getNgClass = function(link) {
						var toReturn = {
							active : scope.isActive(link)
						};
						return toReturn;
					}
					
					scope.isDisabled = function(link) {
						if(link.roles) {
							for(var i =0; i < link.roles.length; i++) {
								var role = link.roles[i];
								if(role == 'all' || $.inArray(role, scope.profile.roles) > 0) {
									return false;
								}
							}
						} else {
							return false;
						}
						
						return true;
					}
					
					scope.isActive = function(viewLocation) {
						var toCompare = "/" + viewLocation.ref;
						return toCompare === $location.path();
					};
				}
				
				var compile = function() {
					$http.get(scope.template, {
						cache : $templateCache
					}).success(function(html) {
						element.html(html);
						$compile(element.contents())(scope);
					});
				};
				
				// Stop user from bring up a view other than "home" if they're not logged in
				// Either the page is initialized with a path other than home (case 1) or user tried to change the view (case 2)
				// case 1
				// Check if we're coming from a redirect from Oauth
				if($location.absUrl().indexOf('#!#') >  -1){
					scope.paramMap = {};
					var splitted = $location.absUrl().split('#!#');
					var queryParams = splitted[1];
					
					if(queryParams )
					var params = queryParams.split('&');
					
					
					for(var i = 0 ; i < params.length; i++) {
						var paramPair = params[i].split('=');
						scope.paramMap[paramPair[0]] = decodeURIComponent(paramPair[1] || '');
					}
				} else if(!$sessionStorage.token) {
					// If we're not coming from an oAuth redirect check if the user is trying to access anything other than the home page of the application
					var continueTo = $location.path();
					if(continueTo.match("(^$|^\/$|^/index.html)")) {
						// Do nothing
					} else {
						$localStorage.continueTo = $location.path();
						$sessionStorage.needToLogin = true;
					}
				} 
				
				// case 2 router event
				$rootScope.$on('$routeChangeStart', function(event, next, current) {
					if (!$sessionStorage.token) {
						var nextRoute = next.$$route;
						// route to default page
						if(nextRoute === undefined) {
							// Do nothing
						} else if(nextRoute.originalPath.match("^\/$") ||(nextRoute.redirectTo && nextRoute.redirectTo.match("^\/$"))) {
							// Do nothing
						} else {
							// save the view they wanted to get to 
							$localStorage.continueTo = $location.path();
							$sessionStorage.needToLogin = true;
							if(Endpoint.get()) {
								delete $sessionStorage.needToLogin;
								Endpoint.redirect();
							} //else watch Endpoint.get() and kickoff login once it's populated
						}
					}
				});
				
				// Have to wait for Endpoint url to get populated 
				scope.$watch(function() {
						return Endpoint.get()
					}, function() {
						if($sessionStorage.needToLogin && !(Endpoint.get() === undefined)) {
							// Kick off oauth flow which will prompt login
							delete $sessionStorage.needToLogin;
							Endpoint.redirect();
					}
				});
				// END
				
				scope.$on("oauth:authorized", function(event, token) {
					Profile.find(scope.profileUri).success(function(response) {
						$localStorage.uuid = response.accountId;
						$localStorage.guid = response.guid;
						scope.profile = response;
					    $rootScope.$broadcast('osee:userAuthenticated');
					});
					if($localStorage.continueTo) {
						// Change the state to the continueTo we caught when User first tried to get into page
						$location.path($localStorage.continueTo);
						delete $localStorage.continueTo;
					}
				})
				scope.$on("oauth:logout", function() {
					$localStorage.uuid = 1896;
					scope.profile = scope.anonymousUser;
					AccessToken.destroy();
					$location.path("/");
				})
				scope.$on("oauth:denied", function(event, token) {
					$localStorage.uuid = 1896;
					scope.profile = scope.anonymousUser;
					AccessToken.destroy();
				});

			}

			return definition;

		} ]);