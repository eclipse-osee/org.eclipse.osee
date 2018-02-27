var app = angular.module('oseeApp', ['ngRoute', 'ngStorage', 'ngCookies', 'angularjs-dropdown-multiselect',
            'jsonforms', 'jsonforms-bootstrap', 'ui.bootstrap', 'ngResource', 'ui.grid', 'ui.grid.resizeColumns']);

app.config(
    ['$routeProvider', function ($routeProvider) {
            $routeProvider.when('/', {
                templateUrl: '/osee_app/views/start_view.html',
                controller: 'startController as sc'
            }).when('/osee_app', {
                templateUrl: '/osee_app/views/osee_app_view.html',
                controller: 'oseeAppController as oc'
            }).when('/osee_list', {
                templateUrl: '/osee_app/views/osee_list_view.html',
                controller: 'oseeListController as lc'
            }).otherwise({
                redirectTo: "/"
            });
        }
    ]);

app.provider('OseeAppSchema', function () {
    this.$get = [
        '$resource',
        function ($resource) {
            var OseeAppSchema = $resource(
                    '/apps/api/:appId', {}, {});
            return OseeAppSchema;
        }
    ];
});

app.service('OseeControlValues', function ($resource) {
    this.queryUrl = function (url) {
        return $resource(url, {}, {
            query: {
                method: 'GET',
                params: {},
                isArray: true
            }
        });
    }
    this.putUrl = function (url, useArray) {
        return $resource(url, {}, {
            submit: {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                params: {},
                isArray: useArray
            }
        });
    }
    this.parseAttribute = function (schemaRef) {
        var intermediate = schemaRef.substr(13);
        if (intermediate.indexOf('/') > 0) {
            return intermediate.substring(0, intermediate.indexOf('/'));
        }
        return intermediate;
    }
    this.getParametersFromURL = function (url, action, attribute) {
        var elements = url.match(/:[^\/]*/g);
        return JSON.parse("{ \"" + elements[0].substr(1) + "\": \"" + action + "\", \"" + elements[1].substr(1) + "\": \"" + attribute + "\" }");
    }
    this.getParameterFromString = function (item, value) {
        return JSON.parse("{ \"" + item + "\": \"" + value + "\" }");
    }
});
