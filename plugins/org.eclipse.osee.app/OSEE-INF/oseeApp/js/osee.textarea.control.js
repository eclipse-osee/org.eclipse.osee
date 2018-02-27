
app.directive('oseeTextareaControl', function () {
    return {
        restrict: 'E',
        controller: ['BaseController', '$scope', '$routeParams', 'OseeAppSchema', function (
                BaseController, $scope, $routeParams, OseeAppSchema) {
                var vm = this;

                $scope.onNgChange = function (controlschema) {
                    OseeAppSchema.updateItem(controlschema, vm.resolvedData[vm.fragment]);
                    console.log("got ng change");
                }
                $scope.onNgBlur = function () {
                    console.log("got ng blur");
                    OseeAppSchema.doUpdate();
                }
                BaseController.call(vm, $scope, OseeAppSchema);

            }
        ],
        controllerAs: 'vm',
        template: `
            <jsonforms-control>
                <textarea id="{{vm.id}}"
                    class="form-control jsf-control-string osee-textarea"
                    ng-style="{{vm.uiSchema.style}}"
                    rows="{{vm.uiSchema.rows}}"
                    ng-model="vm.resolvedData[vm.fragment]"
                    ng-change="onNgChange('{{vm.uiSchema}}')"
                    ng-blur="onNgBlur()"
                    ng-readonly="vm.uiSchema.readOnly">
                </textarea>
            </jsonforms-control>
        `
    };

}).run(['RendererService', 'JSONFormsTesters', function (RendererService, Testers) {
            RendererService.register('osee-textarea-control', Testers.and(

                    // Inherit this custom control from schema that call out the following using this single option:
                    Testers.optionIs('customControlName', 'oseeTextareaControl')), 10);
        }
    ]);
