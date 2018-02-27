
app.directive('oseeInputControl', function () {
    return {
        restrict: 'E',
        controller: ['BaseController', '$scope', '$routeParams', 'OseeAppSchema', function (
                BaseController, $scope, $routeParams, OseeAppSchema) {
                var vm = this;

                $scope.onNgBlur = function () {
                    OseeAppSchema.doUpdate();
                }

                $scope.onNgChange = function (controlschema) {
                    OseeAppSchema.updateItem(controlschema, vm.resolvedData[vm.fragment]);
                }
                BaseController.call(vm, $scope, $routeParams, OseeAppSchema);

            }
        ],
        controllerAs: 'vm',
        template: `
            <jsonforms-control>
                <input id="{{vm.id}}"
                    class="form-control jsf-control-string"
                    ng-style="{{vm.uiSchema.style}}"
                    ng-model="vm.resolvedData[vm.fragment]"
                    ng-change="onNgChange('{{vm.uiSchema}}')"
                    ng-readonly="vm.uiSchema.readOnly"
                    ng-blur="onNgBlur()">
                </input>
            </jsonforms-control>
        `
    };

}).run(['RendererService', 'JSONFormsTesters', function (RendererService, Testers) {
            RendererService.register('osee-input-control', Testers.and(
                    // Inherit this custom control from schema that call out the following using this single option:
                    Testers.optionIs('customControlName', 'oseeInputControl')), 10);
        }
    ]);
