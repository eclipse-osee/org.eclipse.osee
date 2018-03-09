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

app.provider('Account', function () {
    this.$get = ['$resource',
        function ($resource) {
            var Account = $resource('/accounts/user', {}, {});
            return Account;
        }
    ];
});

app.service('OseeControlValues', function ($resource) {

    this.queryUrl = function (url, useArray) {
        return $resource(url, {}, {
            query: {
                method: 'GET',
                params: {},
                isArray: useArray
            }
        });
    }
    this.putUrl = function (url, useArray) {
        return $resource(url, {}, {
            submit: {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'osee.account.id': this.userId
                },
                params: {},
                isArray: useArray
            }
        });
    }
    this.createUrl = function (url, params) {
        return $resource(url, params, {
            'create': {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'osee.account.id': this.userId
                },
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
    this.setActiveUserId = function (userId) {
        this.userId = userId;
    }
    this.getActiveUserId = function () {
        if (this.userId) {
            return this.userId;
        } else {
            return '99999999';
        }
    }
    this.setCreateUrl = function (url) {
        this.newCreateUrl = url;
    }
    this.getCreateUrl = function () {
        return this.newCreateUrl;
    }
    this.setActiveApp = function(appId) {
        this.activeApp = appId;
    }
    this.getActiveApp = function() {
        return this.activeApp;
    }
    this.createAction = function (title) {
        if (this.newCreateUrl) {
            var params = { userId: this.getActiveUserId(), title: title };
            var appId = this.getActiveApp();
            this.createUrl(this.getCreateUrl(), params).create()
            .$promise.then(
                function (data) {
                    window.location.href = 'index.html#/osee_app?uuid=' + appId + '&element=' + data.id;
            }, function (response) {
                alert("Problem: " + response.data);
            });
        } 
        else {
            alert("URL for creation not set");
        }
    }
    this.setWindowLocation = function (element) {
        if(!this.getActiveApp()) {
            alert("Active Application not yet set");
            return;
        }
        window.location.href = 'index.html#/osee_app?uuid=' + this.getActiveApp() + '&element=' + element;
    }
});
