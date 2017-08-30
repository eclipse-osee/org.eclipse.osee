app.directive('oseeCheckboxControl', function () {
    return {
        restrict: 'E',
        controller: ['BaseController', '$scope', '$routeParams', 'OseeAppSchema', function (
                BaseController, $scope, $routeParams, OseeAppSchema) {
                var vm = this;

                $scope.onNgBlur = function () {
                }

                $scope.onNgChanged = function (controlschema) {
                    OseeAppSchema.updateItem(controlschema);
                }
                BaseController.call(vm, $scope);

            }
        ],
        controllerAs: 'vm',
        template: `
            <jsonforms-control>
                <input id="{{vm.id}}"
                    type="checkbox"
                    class="form-control-boolean"
                    ng-style="{{vm.uiSchema.style}}"
                    ng-model="vm.resolvedData[vm.fragment]"
                    ng-change="onNgChanged('{{vm.uiSchema}}')"
                    ng-readonly="vm.uiSchema.readOnly"
                    ng-blur="onNgBlur()">
                </input>
            </jsonforms-control>
        `
    };

}).run(['RendererService', 'JSONFormsTesters', function (RendererService, Testers) {
            RendererService.register('osee-checkbox-control', Testers.and(
                    // Inherit this custom control from schema that call out the following using this single option:
                    Testers.optionIs('customControlName', 'oseeCheckboxControl')), 10);
        }]);
