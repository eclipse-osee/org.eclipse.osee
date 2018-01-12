
app.directive('oseeMultiselectDropdownControl', function () {
    return {
        restrict: 'E',
        controller: ['BaseController', '$scope', 'OseeAppSchema', 'OseeControlValues', '$route', function (BaseController, $scope, OseeAppSchema, OseeControlValues, $route) {
                var vm = this;
                vm.element = $route.current.params.element;
                $scope.model = []; // This is where we can set default selections on initialization, if any.
                var controlIdValue = OseeControlValues.parseAttribute($scope.uischema.scope.$ref);
                console.log("logging url and control id, url: [" + $scope.uischema.scope.getUrl + "] and control id: [" + controlIdValue + "]");
                OseeControlValues.queryUrl($scope.uischema.scope.getUrl + '/:controlId').query({
                    controlId: controlIdValue
                }, function (selections) {
                    var objects = [];
                    // if there are no objects, use the local enumeration
                    if(selections.length < 1) {
                        for(i = 0; i < vm.resolvedSchema.enum.length; ++i) {
                            selections[i] = vm.resolvedSchema.enum[i];                           
                        }
                    }
                    for (i = 0; i < selections.length; i++) {
                        objects[i] = {
                            id: i,
                            label: selections[i].toString()
                        };

                        if (vm.resolvedData && vm.resolvedData[vm.fragment]) {                 
                            if (selections[i].toString() === vm.resolvedData.value) {
                                console.log('selection['+selections[i].toString()+']data>'+vm.resolvedData.value+'<');
                                $scope.model.push(objects[i]);
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
                        var content = $scope.getEffectedData($scope.model);
                        var parameter = $scope.getParameterFromString($scope.uischema.scope.putUrl, $scope.vm.element, 
                                        OseeControlValues.parseAttribute($scope.vm.uiSchema.scope.$ref));
                        OseeControlValues.putUrl($scope.uischema.scope.putUrl).submit(parameter, content);
                    }
                };
                $scope.getParameterFromString = function (url, action, attribute) {
                    var elements = url.match(/:[^\/]*/g);
                    return JSON.parse("{ \"" + elements[0].substr(1) + "\": \"" + action + "\", \"" + elements[1].substr(1) + "\": \"" + attribute + "\" }");
                }
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
            RendererService.register('osee-multiselect-dropdown-control', Testers.and(
                    // Inherit this custom control from schema that call out the following using this single option:
                    Testers.optionIs('customControlName', 'oseeMultiselectDropdownControl')), 10);
        }
    ]);
