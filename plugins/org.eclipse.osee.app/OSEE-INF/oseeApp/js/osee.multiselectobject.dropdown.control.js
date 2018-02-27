
app.directive('oseeMultiselectobjectDropdownControl', function () {
    return {
        restrict: 'E',
        controller: ['BaseController', '$scope', 'OseeControlValues', '$route', function (BaseController, $scope, OseeControlValues, $route) {
                var vm = this;
                vm.element = $route.current.params.element;
                $scope.model = []; // This is where we can set default selections on initialization, if any.
                var controlIdValue = OseeControlValues.parseAttribute($scope.uischema.scope.$ref);
                console.log("logging url and control id, url: [" + $scope.uischema.scope.getUrl + "] and control id: [" + controlIdValue + "]");
                OseeControlValues.queryUrl($scope.uischema.scope.getUrl).query({}, function (selections) {
                    var objects = [];
                    // if there are no objects, use the local enumeration
                    if (selections.length < 1) {
                        for (i = 0; i < vm.resolvedSchema.enum.length; ++i) {
                            selections[i] = vm.resolvedSchema.enum[i];
                        }
                    }
                    for (i = 0; i < selections.length; i++) {
                        objects[i] = {
                            id: selections[i].id,
                            label: selections[i].name
                        };

                        if (vm.resolvedData && vm.resolvedData[vm.fragment]) {
                            for (j = 0; j < vm.resolvedData[vm.fragment].length; j++) {
                                if (vm.resolvedData[vm.fragment][j].id === selections[i].id) {
                                    $scope.model.push(objects[i]);
                                    console.log("selecting object for multiselect: " + objects[i].label + ", with id: " + objects[i].id);
                                }
                            }
                        }
                    }
                    $scope.data = objects;
                });
                $scope.settings = $scope.uischema.options.settings;
                if ($scope.settings.smartButtonTextConverter == true) {
                    $scope.settings.smartButtonTextConverter = function (itemText, originalItem) {
                        // This is where we can modify text, if needed.
                        return itemText;
                    }
                }

                $scope.onInit = function () {}
                $scope.multiselectEvents = {
                    onSelectionChanged: function () {
                        var content = $scope.getMyEffectedData($scope.model);
                        var parameter = $scope.getParameterFromString($scope.uischema.scope.putUrl, $scope.vm.element);
                        OseeControlValues.putUrl($scope.uischema.scope.putUrl, true).submit(parameter, content);
                    }
                };
                $scope.getParameterFromString = function (url, action) {
                    var elements = url.match(/:[^\/]*/g);
                    return JSON.parse("{ \"" + elements[0].substr(1) + "\": \"" + action + "\" }");
                }
                $scope.getMyEffectedData = function (selections) {
                    var objects = [];
                    for (i = 0; i < selections.length; i++) {
                        objects[i] = {
                            id: selections[i].id,
                            name: selections[i].label
                        };
                    }
                    return JSON.stringify(objects);
                }
                $scope.linkExists = function () {
                    if (vm.uiSchema.options.link)
                        return true;
                    else
                        return false;
                }

                BaseController.call(vm, $scope, OseeControlValues);
            }
        ],
        controllerAs: 'vm',
        template: `
            <jsonforms-control>
                <span ng-if = "linkExists()">
                    <label>{{vm.uiSchema.options.subLabel}}</label>
                    <a href="{{vm.uiSchema.options.link}}" class="btn pull-right priority">{{vm.uiSchema.options.linkText}}</a>
                </span>
                <div id="{{vm.id}}"
                    ng-dropdown-multiselect="" 
                    options="data" 
                    selected-model="model" 
                    extra-settings="settings"
                    events="multiselectEvents"
                    data-ng-init="onInit()">
                </div>
           </jsonforms-control>
        `
    };
})
.run(['RendererService', 'JSONFormsTesters', function (RendererService, Testers) {
            RendererService.register('osee-multiselectobject-dropdown-control', Testers.and(
                    // Inherit this custom control from schema that call out the following using this single option:
                    Testers.optionIs('customControlName', 'oseeMultiselectobjectDropdownControl')), 10);
        }
    ]);
