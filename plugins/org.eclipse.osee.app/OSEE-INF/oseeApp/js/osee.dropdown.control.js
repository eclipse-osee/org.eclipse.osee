
app.directive('oseeDropdownControl', function () {
    return {
        restrict: 'E',
        controller: ['BaseController', '$scope', 'OseeAppSchema', 'OseeControlValues', function (BaseController, $scope, OseeAppSchema, OseeControlValues) {
                var vm = this;

                $scope.onInit = function () {
                    if ($scope.uischema.scope.getUrl) {
                        vm.possibleSelections = OseeControlValues.queryUrl($scope.uischema.scope.getUrl).query({
                                element: OseeAppSchema.getElement()
                            }, function (selections) {
                                vm.possibleSelections = selections;
                            });
                    } else
                        vm.possibleSelections = vm.resolvedSchema.enum;
                }
                $scope.onNgChange = function (controlschema) {
                    if (vm.resolvedData.value) {
                        OseeAppSchema.updateItem(controlschema, vm.resolvedData.value);
                    }
                }
                $scope.onNgBlur = function () {
                    OseeAppSchema.doUpdate();
                }
                $scope.linkExists = function () {
                    if (vm.uiSchema.options.link)
                        return true;
                    else
                        return false;
                }
                BaseController.call(vm, $scope, OseeAppSchema);
            }
        ],
        controllerAs: 'vm',
        template: `
            <jsonforms-control>
                <span ng-if = "linkExists()">
                    <label>{{vm.uiSchema.options.subLabel}}</label>
                    <a href="{{vm.uiSchema.options.link}}" class="btn pull-right">{{vm.uiSchema.options.linkText}}</a>
                </span>
                    <select ng-options="option as option for option in vm.possibleSelections"
                        id="{{vm.id}}"
                        class="form-control jsf-control-enum"
                        ng-model="vm.resolvedData[vm.fragment]"
                        ng-change="onNgChange('{{vm.uiSchema}}')"
                        ng-blur="onNgBlur()"
                        data-ng-init="onInit()">
                    </select>
               </jsonforms-control>
            `
    };
})
.run(['RendererService', 'JSONFormsTesters', function (RendererService, Testers) {
            RendererService.register('osee-dropdown-control', Testers.and(
                    // Inherit this custom control from schema that call out the following using this single option:
                    Testers.optionIs('customControlName', 'oseeDropdownControl')), 10);
        }
    ]);
