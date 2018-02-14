/**
 * Dispo app definition
 */
var app = angular.module('dispoApp', ['ngRoute', 'ngResource', 'ui.bootstrap', 'ngGrid', 'mc.resizer', 'ngStorage', ]);


app.config(['$routeProvider',
    function($routeProvider) {
        $routeProvider.when('/', {
            redirectTo: "/user",
        }).when('/user', {
            templateUrl: '/dispo/legacy/user.html',
            controller: 'userController'
        }).when('/admin', {
            templateUrl: '/dispo/legacy/admin.html',
            controller: 'adminController'
        }).when('/search', {
            templateUrl: '/dispo/legacy/user.html',
            controller: 'userController'
        }).otherwise({
            redirectTo: "/user"
        });
    }
]);


app.directive('focusMe', function($timeout) {
    return function(scope, element, attrs) {
        scope.$watch(attrs.focusMe, function() {
            $timeout(function() {
                element[0].focus();
            }, 20);
        });
    };
});

//http://stackoverflow.com/questions/11868393/angularjs-inputtext-ngchange-fires-while-the-value-is-changing
app.directive('ngModelOnblur', function() {
    return {
        restrict: 'A',
        require: 'ngModel',
        priority: 1, // needed for angular 1.2.x
        link: function(scope, elm, attr, ngModelCtrl) {
            if (attr.type === 'radio' || attr.type === 'checkbox') return;

            elm.unbind('input').unbind('keydown').unbind('change');
            elm.bind('blur', function() {
                scope.$apply(function() {
                    ngModelCtrl.$setViewValue(elm.val());
                });
            });
        }
    };
});

app.provider('Program', function() {
    this.$get = ['$resource',
        function($resource) {
            var Program = $resource('/dispo/program/:programId', {}, {});
            return Program;
        }
    ];
});

app.provider('Report', function() {
    this.$get = ['$resource',
        function($resource) {
            var Program = $resource('/dispo/program/:programId/admin/report', {}, {});
            return Program;
        }
    ];
});

app.provider('ExportSet', function() {
	    this.$get = ['$resource',
	        function($resource) {
	            var ExportSet = $resource('/dispo/program/:programId/admin/export', {}, {});
	            return ExportSet;
	        }
	    ];
	});
	
app.provider('CopySet', function() {
    this.$get = ['$resource',
        function($resource) {
            var CopySet = $resource('/dispo/program/:programId/admin/copy', {}, {});
            return CopySet;
        }
    ];
});

app.provider('MultiItemEdit', function() {
    this.$get = [
        '$resource',
        function($resource) {
            var MultiItemEdit = $resource(
                '/dispo/program/:programId/admin/multiItemEdit', {}, {
                });
            return MultiItemEdit;
        }
    ];
});


app.provider('Set', function() {
    this.$get = ['$resource',
        function($resource) {
            var Set = $resource('/dispo/program/:programId/set/:setId', {}, {
                update: {
                    method: 'PUT',
                    headers: {
                        "Accept": "application/json"
                    }
                }
            });
            return Set;
        }
    ];
});

app.provider('Config', function() {
    this.$get = ['$resource',
        function($resource) {
            var Config = $resource('/dispo/program/:programId/config', {}, {
            });
            return Config;
        }
    ];
});

app.provider('SourceFile', function() {
    this.$get = ['$resource',
        function($resource) {
            var SourceFile = $resource('/dispo/program/:programId/set/:setId/file/:fileName', {}, {});
            return SourceFile;
        }
    ];
});

app.provider('Item', function() {
    this.$get = [
        '$resource',
        function($resource) {
            var Item = $resource(
                '/dispo/program/:programId/set/:setId/item/:itemId', {}, {
                    update: {
                        method: 'PUT',
                        headers: {
                            "Accept": "application/json"
                        }
                    }
                });
            return Item;
        }
    ];
});

app.provider('SetSearch', function() {
    this.$get = [
        '$resource',
        function($resource) {
            var SetSearch = $resource(
                '/dispo/program/:programId/set/:setId/search/', {}, {
                });
            return SetSearch;
        }
    ];
});

app.provider('Annotation', function() {
    this.$get = [
        '$resource',
        function($resource) {
            var Annotation = $resource(
                '/dispo/program/:programId/set/:setId/item/:itemId/annotation/:annotationId', {}, {
                    update: {
                        method: 'PUT',
                        headers: {
                            "Accept": "application/json"
                        }
                    }
                });
            return Annotation;
        }
    ];
});