/**
 * Dispo app definition
 */
var app = angular.module('dispoApp', ['oauth', 'ngRoute', 'ngResource', 'ui.bootstrap', 'ui.grid', 'ui.grid.selection', 'ui.grid.exporter', 'ui.grid.edit', 'ui.grid.resizeColumns', 'mc.resizer', 'ngCookies', 'ngStorage', 'ui.grid.autoResize', 'ui.grid.cellNav', 'ui.grid.treeView', 'ui.grid.grouping']);

app.config(['$routeProvider',
function($routeProvider) {
	
  $routeProvider.when('/', {
	  templateUrl: '/dispo/views/user.html',
      controller: 'userController'
  }).when('/user', {
	  templateUrl: '/dispo/views/user.html',
      controller: 'userController'
  }).when('/admin', {
	  templateUrl: '/dispo/views/admin.html',
      controller: 'adminController'
  }).when('/search', {
	  templateUrl: '/dispo/views/user.html',
      controller: 'userController'
  })
  .otherwise({
    redirectTo: "/"
    });
  }
]);

/**
 *  @ngdoc directive
 *  @name ui.grid.edit.directive:uiGridEditDropdown
 *  @element div
 *  @restrict A
 *
 *  @description dropdown editor for editable fields.
 *  Provides EndEdit and CancelEdit events
 *
 *  Events that end editing:
 *     blur and enter keydown, and any left/right nav
 *
 *  Events that cancel editing:
 *    - Esc keydown
 *
 */
app.directive('uiGridEditDropdownOsee',
  ['uiGridConstants', 'uiGridEditConstants',
    function (uiGridConstants, uiGridEditConstants) {
      return {
        require: ['?^uiGrid', '?^uiGridRenderContainer'],
        scope: true,
        compile: function () {
          return {
            pre: function ($scope, $elm, $attrs) {

            },
            post: function ($scope, $elm, $attrs, controllers) {
              var uiGridCtrl = controllers[0];
              var renderContainerCtrl = controllers[1];

              //set focus at start of edit
              $scope.$on(uiGridEditConstants.events.BEGIN_CELL_EDIT, function () {
                $elm[0].focus();
                $elm[0].style.width = ($elm[0].parentElement.offsetWidth - 1) + 'px';
                $elm.on('blur', function (evt) {
                  $scope.stopEdit(evt);
                });
                $elm.on('change', function (evt) {
                    $elm[0].blur();
                  });
              });


              $scope.stopEdit = function (evt) {
                // no need to validate a dropdown - invalid values shouldn't be
                // available in the list
                $scope.$emit(uiGridEditConstants.events.END_CELL_EDIT);
              };

              $elm.on('keydown', function (evt) {
                switch (evt.keyCode) {
                  case uiGridConstants.keymap.ESC:
                    evt.stopPropagation();
                    $scope.$emit(uiGridEditConstants.events.CANCEL_CELL_EDIT);
                    break;
                }
                if (uiGridCtrl && uiGridCtrl.grid.api.cellNav) {
                  evt.uiGridTargetRenderContainerId = renderContainerCtrl.containerId;
                  if (uiGridCtrl.cellNav.handleKeyDown(evt) !== null) {
                    $scope.stopEdit(evt);
                  }
                }
                else {
                  //handle enter and tab for editing not using cellNav
                  switch (evt.keyCode) {
                    case uiGridConstants.keymap.ENTER: // Enter (Leave Field)
                    case uiGridConstants.keymap.TAB:
                      evt.stopPropagation();
                      evt.preventDefault();
                      $scope.stopEdit(evt);
                      break;
                  }
                }
                return true;
              });
            }
          };
        }
      };
    }]);



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

app.provider('CopySetCoverage', function() {
    this.$get = ['$resource',
        function($resource) {
            var CopySetCoverage = $resource('/dispo/program/:programId/admin/copyCoverage', {}, {});
            return CopySetCoverage;
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

