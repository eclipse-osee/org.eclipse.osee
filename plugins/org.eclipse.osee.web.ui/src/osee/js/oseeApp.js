var app = angular.module('oseeApp', ['oauth', 'oseeProvider', 'ngRoute', 'aLinkRewrite']);

+app.config(['$routeProvider',
                         function($routeProvider) {
                         	
                           $routeProvider.when('/', {
                         	  templateUrl: '../views/home.html',
                               controller: 'indexController'
                           }).when('/wiki', {
                         	  templateUrl: '../views/wiki.html',
                               controller: 'indexController'
                           }).when('/about', {
                         	  templateUrl: '../views/about.html',
                               controller: 'indexController'
                           }).when('/contactUs', {
                         	  templateUrl: '../views/contactUs.html',
                               controller: 'indexController'
                           }).when('/redirect', {
                         	  templateUrl: '../views/home.html',
                               controller: 'indexController'
                           })
                           .otherwise({
                             redirectTo: "/"
                             });
                           }
                         ]);