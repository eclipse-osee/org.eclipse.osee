
app.directive('oseeTextControl', function() {
    return {
        restrict: 'E',
        controller: ['BaseController', '$scope', 'OseeAppSchema', function(BaseController, $scope, OseeAppSchema) {
            var vm = this;

            $scope.onNgBlur = function () {
                OseeAppSchema.doUpdate();
            }

            $scope.onNgChange = function(controlschema) {
                OseeAppSchema.updateItem(controlschema);
            }

            BaseController.call(vm, $scope, OseeAppSchema);
        }],
        controllerAs: 'vm',
        template: `
            <jsonforms-control>
                <input id="{{vm.id}}"
                    class="form-control jsf-control-string osee-text"
                    style="{{vm.uiSchema.style}}"
                    ng-model="vm.resolvedData[vm.fragment]"
                    ng-change="onNgChange('{{vm.uiSchema}}')"
                    ng-blur="onNgBlur()"
                    ng-readonly="vm.uiSchema.readOnly">
                </input>
            </jsonforms-control>
        `
    };

}).run(['RendererService', 'JSONFormsTesters', function(RendererService, Testers) {
    RendererService.register('osee-text-control', Testers.and(

        // Inherit this custom control from schema that call out the following using this single option:
        Testers.optionIs('customControlName', 'oseeTextControl')
    ), 10);
}]);


