function oseeProgversingleControl() {
    return {
        restrict: 'E',
        controller: ['BaseController', '$scope', '$routeParams', 'OseeControlValues', 'OseeAppSchema', function (
                BaseController, $scope, $routeParams, OseeControlValues, OseeAppSchema) {
                var vm = this;
                vm.isValid = OseeAppSchema.isValid;
                vm.schemaElement = OseeAppSchema.getElement();
                vm.schemaAttr = OseeControlValues.parseAttribute($scope.uischema.scope.$ref);
                $scope.settings = $scope.uischema.options.settings;
                $scope.settings.closeOnSelect = true;
                $scope.settings.selectedToTop = true;
                $scope.settings.buttonClasses = "btn btn-default btn-block osee-multiselect-dropdown";
                $scope.settings.groupBy = "programName";
                $scope.settings.groupByTextProvider = function (groupValue) {
                    return "Program: " + groupValue;
                };
                $scope.versiontexts = [];
                $scope.versiontexts.buttonDefaultText = "Select Version";
                if ($scope.settings.smartButtonTextConverter == true) {
                    $scope.settings.smartButtonTextConverter = function (itemText, originalItem) {
                        // This is where we can modify text, if needed.
                        return originalItem.programName + " : " + originalItem.versionName;
                    }
                }
                $scope.existingVersion = {}; //existing at the time the control is initialized
                $scope.program = [];
                $scope.availablePrograms = OseeControlValues.queryUrl($scope.uischema.scope.getUrl, true).query({}, function (programs) {
                        $scope.availablePrograms = programs;
                        for (i = 0; i < programs.length; i++) {
                            if (programs[i].id == $scope.existingVersion.id) {
                                $scope.program = [];
                                $scope.program.push(programs[i]);
                            }
                        }
                    });

                $scope.checkValue = function (item, regex) {
                    if (OseeAppSchema.isValid(item, regex)) {
                        vm.uiSchema.style = {
                            color: 'black'
                        };
                    } else {
                        vm.uiSchema.style = {
                            color: 'red'
                        };
                    }
                }
                $scope.versionEvents = {
                    onSelectionChanged: function () {
                        if ($scope.program[0] && $scope.program[0].id) {
                            var content = "[ " + $scope.program[0].id + " ]";
                            var parameter = OseeControlValues.getParametersFromURL($scope.uischema.scope.putUrl, vm.schemaElement,
                                    vm.schemaAttr);
                            OseeControlValues.putUrl($scope.uischema.scope.putUrl, false).submit(parameter, content).$promise.then(
                                function (data) {
                                console.log("data put from program version dropdown:" + data);
                                // will need to update the gamma here
                            }, function (response) {
                                vm.failed = true;
                                alert("Problem: " + response.message);
                            });
                        }
                    }
                };
                $scope.onInit = function () {
                    if (vm.data[vm.schemaAttr] && vm.data[vm.schemaAttr].value) {
                        $scope.existingVersion = {
                            id: vm.data[vm.schemaAttr].value,
                            name: "na"
                        }
                    }
                }
                BaseController.call(vm, $scope, $routeParams, OseeAppSchema);
            }
        ],
        controllerAs: 'vm',
        link: function link(scope, element, attrs, ctrl) {
            if (ctrl.resolvedData) {
                if (!ctrl.isValid(ctrl.resolvedData.value, ctrl.uiSchema.options.required)) {
                    console.log("invalid control data according to the uiSchema regex");
                    element.
                    css({
                        color: 'red'
                    });
                }
            }
        },
        template: `
            <jsonforms-control id="{{vm.scope.$id}}" ng-style="vm.uiSchema.style">
                <div ng-dropdown-multiselect="" 
                      options = "availablePrograms"
                      translation-texts = "versiontexts"
                      selected-model="program" 
                      extra-settings="settings"
                      events="versionEvents"
                      data-ng-init = "onInit()"
                      >
                </div>
            </jsonforms-control>
        `
    };

}
app.directive('oseeProgversingleControl', oseeProgversingleControl).run(['RendererService', 'JSONFormsTesters', function (RendererService, Testers) {
            RendererService.register('osee-progversingle-control', Testers.and(
                    // Inherit this custom control from schema that call out the following using this single option:
                    Testers.optionIs('customControlName', 'oseeProgversingleControl')), 10);
        }
    ]);
