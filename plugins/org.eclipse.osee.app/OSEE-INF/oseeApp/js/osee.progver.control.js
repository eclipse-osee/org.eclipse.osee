function oseeProgverControl() {
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
                $scope.programtexts = [];
                $scope.programtexts.buttonDefaultText = "Select Program";
                $scope.versiontexts = [];
                $scope.versiontexts.buttonDefaultText = "Select Version";
                if ($scope.settings.smartButtonTextConverter == true) {
                    $scope.settings.smartButtonTextConverter = function (itemText, originalItem) {
                        // This is where we can modify text, if needed.
                        return originalItem.name;
                    }
                }
                $scope.existingProgram = {}; //existing at the time the control is initialized
                $scope.existingVersion = {};
                $scope.program = [];
                $scope.version = [];
                $scope.availablePrograms = OseeControlValues.queryUrl($scope.uischema.scope.getProgramUrl, true).query({}, function (programs) {
                        $scope.availablePrograms = programs;
                        if ($scope.existingProgram.$promise) {
                            $scope.existingProgram.$promise.then(function (program) {
                                for (i = 0; i < programs.length; i++) {
                                    if (programs[i].id == $scope.existingProgram.id) {
                                        $scope.program = [];
                                        $scope.program.push(programs[i]);
                                        $scope.programEvents.onSelectionChanged();
                                    }
                                }
                            }, function (response) {
                                alert("Problem: " + response.message);
                            });
                        }
                    });

                $scope.getProgram = function () {
                    if ($scope.program[0] && $scope.program[0].id) {
                        return $scope.program[0].id;
                    }
                    return null;
                }
                $scope.availableVersions = [];
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
                $scope.programEvents = {
                    onSelectionChanged: function () {
                        var prog = $scope.getProgram();
                        $scope.availableVersions = OseeControlValues.queryUrl($scope.uischema.scope.getVersionUrl, true).query({
                                program: prog,
                                active: true
                            },
                                function (versions) {
                                $scope.version = [];
                                $scope.availableVersions = versions;
                                if ($scope.existingVersion) {
                                    for (i = 0; i < versions.length; ++i) {
                                        if (versions[i].id == $scope.existingVersion.id) {
                                            $scope.version.push(versions[i]);
                                        }
                                    }
                                }
                            }, function (response) {
                                $scope.version = [];
                                $scope.availableVersions = [];
                            });

                    }
                };
                $scope.versionEvents = {
                    onSelectionChanged: function () {
                        if ($scope.version[0] && $scope.version[0].id) {
                            var content = "[ " + $scope.version[0].id + " ]";
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
                        console.log("found");

                        $scope.existingVersion = {
                            id: vm.data[vm.schemaAttr].value,
                            name: "na"
                        }
                        $scope.existingProgram = OseeControlValues.queryUrl($scope.uischema.scope.getProgramVersionUrl).query({
                                version: vm.data[vm.schemaAttr].value
                            },
                                function (program) {
                                $scope.existingProgram = program;
                            }, function (response) {
                                alert("program not found for version:" + vm.data[vm.schemaAttr].value);
                            });
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
                <div class="panel panel-default">
                    <div class="panel-body"> 
                        <form class="form-inline">
                            <div class="programsel pull-left" ng-dropdown-multiselect="" 
                                options = "availablePrograms"
                                translation-texts = "programtexts"
                                selected-model="program" 
                                extra-settings="settings"
                                events="programEvents"
                                data-ng-init = "onInit()"
                            >
                            </div>
                            <div class="versionsel pull-right" ng-dropdown-multiselect="" 
                                options = "availableVersions"
                                translation-texts = "versiontexts"
                                selected-model="version" 
                                extra-settings="settings"
                                events="versionEvents"
                            >
                            </div>
                       </form>
                    </div>
                </div>
            </jsonforms-control>
        `
    };

}
app.directive('oseeProgverControl', oseeProgverControl).run(['RendererService', 'JSONFormsTesters', function (RendererService, Testers) {
            RendererService.register('osee-progver-control', Testers.and(
                    // Inherit this custom control from schema that call out the following using this single option:
                    Testers.optionIs('customControlName', 'oseeProgverControl')), 10);
        }
    ]);
