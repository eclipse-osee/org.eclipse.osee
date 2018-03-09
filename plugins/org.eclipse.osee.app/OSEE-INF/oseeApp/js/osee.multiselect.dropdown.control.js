
app.directive('oseeMultiselectDropdownControl', function () {
    return {
        restrict: 'E',
        controller: ['BaseController', '$scope', 'OseeAppSchema', 'OseeControlValues', '$route', function (BaseController, $scope, OseeAppSchema, OseeControlValues, $route) {
                var vm = this;
                vm.element = $route.current.params.element;

                $scope.settings = $scope.uischema.options.settings;
                if ($scope.settings.smartButtonTextConverter == true) {
                    $scope.settings.smartButtonTextConverter = function (itemText, originalItem) {
                        // This is where we can modify text, if needed.
                        return itemText;
                    }
                }

                $scope.onInit = function () {
                    console.log("in init");
                    if (!$scope.model) {
                        $scope.model = []; // This is where we can set default selections on initialization, if any.
                    } else {
                        vm.resolvedData.value = $scope.model["0"].label;
                        $scope.model = [];
                    }
                    var controlIdParam = {};
                    if ($scope.uischema.scope.getUrl.indexOf(":element") > 0) {
                        controlIdParam = OseeControlValues.getParameterFromString("element", vm.element);
                    } else if ($scope.uischema.scope.getUrl.indexOf(":attribute") > 0) {
                        controlIdParam = OseeControlValues.getParameterFromString("attribute", OseeControlValues.parseAttribute($scope.uischema.scope.$ref));
                    }
                    // choose parameters by either element id or attribute id
                    console.log("logging url and attribute id, url: [" + $scope.uischema.scope.getUrl + "] and control id: [" + controlIdParam + "]");
                    OseeControlValues.queryUrl($scope.uischema.scope.getUrl, true).query(controlIdParam, function (selections) {
                        var objects = [];
                        // if there are no objects, use the local enumeration
                        if (selections.length < 1) {
                            for (i = 0; i < vm.resolvedSchema.enum.length; ++i) {
                                selections[i] = vm.resolvedSchema.enum[i];
                            }
                        }
                        var found = false;
                        for (i = 0; i < selections.length; i++) {
                            objects[i] = {
                                id: i,
                                label: selections[i].toString()
                            };

                            if (vm.resolvedData) {
                                if (selections[i].toString() === vm.resolvedData.value) {
                                    console.log('selection[' + selections[i].toString() + ']data>' + vm.resolvedData.value + '<');
                                    $scope.model.push(objects[i]);
                                    found = true;
                                }
                            }
                        }
                        if (!found) {
                            if (vm.resolvedData.value) {
                                // put it into the list
                                objects[selections.length] = {
                                    id: selections.length,
                                    label: vm.resolvedData.value
                                }
                                $scope.model.push(objects[selections.length]);
                                vm.uiSchema.style = {
                                    color: 'black'
                                };
                            } else if ($scope.uischema.options.required) {
                                // value required, but not found
                                vm.uiSchema.style = {
                                    color: 'red'
                                };
                            }
                        } else {
                            vm.uiSchema.style = {
                                color: 'black'
                            };
                        }
                        $scope.data = objects;
                    });
                }
                $scope.multiselectEvents = {
                    onSelectionChanged: function () {
                        var content = $scope.getEffectedData($scope.model);
                        var parameter = OseeControlValues.getParametersFromURL($scope.uischema.scope.putUrl, $scope.vm.element,
                                OseeControlValues.parseAttribute($scope.vm.uiSchema.scope.$ref));
                        OseeControlValues.putUrl($scope.uischema.scope.putUrl, false).submit(parameter, content).$promise.then(
                            function (data) {
                            console.log("data put from multiselect dropdown:" + data);
                            $scope.onInit();
                            // will need to update the gamma here
                        }, function (response) {
                            vm.failed = true;
                            alert("Problem: " + response.message);
                        });
                    }
                };
                $scope.getEffectedData = function (selections) {
                    var toReturn = '[ ';
                    var first = true;
                    for (var item in selections) {
                        if (selections.hasOwnProperty(item)) {
                            if (first) {
                                toReturn += JSON.stringify(selections[item].label);
                                first = false;
                            } else {
                                toReturn += ', ' + JSON.stringify(selections[item].label);
                            }
                        }
                    }
                    return toReturn + " ]";
                }
                $scope.linkExists = function () {
                    if (vm.uiSchema.options.link)
                        return true;
                    else
                        return false;
                }

                BaseController.call(vm, $scope, OseeAppSchema, OseeControlValues);
            }
        ],
        controllerAs: 'vm',
        template: `
            <jsonforms-control id="{{vm.scope.$id}}" ng-style="vm.uiSchema.style">
                <span ng-if = "linkExists()">
                    <label>{{vm.uiSchema.options.subLabel}}</label>
                    <a href="{{vm.uiSchema.options.link}}" class="btn pull-right priority">{{vm.uiSchema.options.linkText}}</a>
                </span>
                <div ng-dropdown-multiselect="" 
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
            RendererService.register('osee-multiselect-dropdown-control', Testers.and(
                    // Inherit this custom control from schema that call out the following using this single option:
                    Testers.optionIs('customControlName', 'oseeMultiselectDropdownControl')), 10);
        }
    ]);
