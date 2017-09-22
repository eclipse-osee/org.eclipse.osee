var app = angular.module('oseeApp', [ 'ngRoute', 'ngStorage', 'ngCookies', 'angularjs-dropdown-multiselect', 
        'jsonforms', 'jsonforms-bootstrap', 'ui.bootstrap', 'ngResource', 'ui.grid', 'ui.grid.resizeColumns' ]);

app.config([ '$routeProvider', function($routeProvider) {

    $routeProvider.when('/', {
        templateUrl : '/osee_app/views/start_view.html',
        controller : 'startController as sc'
    }).when('/osee_app', {
        templateUrl : '/osee_app/views/osee_app_view.html',
        controller : 'oseeAppController as oc'
    }).when('/osee_list', {
        templateUrl : '/osee_app/views/osee_list_view.html',
        controller : 'oseeListController as lc'
    }).otherwise({
        redirectTo : "/"
    });
} ]);

app.provider('OseeAppSchema', function() {
    this.$get = [
       '$resource',
       function($resource) {
       var OseeAppSchema = $resource(
            '/apps/api/:appId', {}, {});
       return OseeAppSchema;
       } ];   
});

app.factory('OseeControlValues', function($resource) {    
    return $resource('/:url/:controlId', {}, {
        query : {
          method: 'GET',
          params: {},
          isArray: true
        }
    });
})