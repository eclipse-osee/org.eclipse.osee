var app = angular.module('oseeApp', [ 'ngRoute', 'ngStorage', 'ngCookies',
        'jsonforms', 'jsonforms-bootstrap', 'ui.bootstrap', 'ngResource' ]);

app.config([ '$routeProvider', function($routeProvider) {

    $routeProvider.when('/', {
        templateUrl : '/osee_app/views/start_view.html',
        controller : 'startController as sc'
    }).when('/osee_app', {
        templateUrl : '/osee_app/views/osee_app_view.html',
        controller : 'oseeAppController as oc'
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

app.directive('linkItemControl', function() {
    return {
        restrict: 'E',
        controller: ['BaseController', '$scope', '$route', function(BaseController, $scope, $route) {
            var vm = this;
            vm.uuid = $route.current.params.uuid;
            vm.name = $route.current.params.name;
            vm.schemaKey = $route.current.scope.schemaKey;
            BaseController.call(vm, $scope);
            vm.element = function() {
                if (vm.resolvedData[vm.schemaKey] !== undefined) {
                    return vm.resolvedData[vm.schemaKey];
                } else {
                    return 'undefined';
                }
            };
        }],
        controllerAs: 'vm',
        template: `
            <jsonforms-control>
                 <a href="#/osee_app?uuid={{vm.uuid}}&name={{vm.name}}&element={{vm.element()}}"><button>{{vm.element()}}</button></a>
            </jsonforms-control>
        `
    };
}).run(['RendererService', 'JSONFormsTesters', '$route', function(RendererService, Testers, $route) {
    RendererService.register('link-item-control', Testers.and(
        Testers.uiTypeIs('Control'),
        Testers.schemaTypeIs('integer'),
        Testers.schemaPropertyName('ISSUE_NUMBER')
    ), 10);
}]);

