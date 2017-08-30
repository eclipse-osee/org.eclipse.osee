app.directive('oseeTextControl', function() {
    return {
        restrict: 'E',
        controller: ['BaseController', '$scope', 'OseeAppSchema', function(BaseController, $scope, OseeAppSchema) {
            var vm = this;

            $scope.onNgChange = function(controlschema) {
                OseeAppSchema.updateItem(controlschema);
            }

            $scope.onInit = function() {
            }
            return vm;
        }],
        controllerAs: 'vm',
        template: `
            <jsonforms-control>
                <textarea id="{{vm.id}}"
                    class="form-control jsf-control-string osee-text"
                    style="{{vm.uiSchema.style}}"
                    data-ng-init="onInit()"
                    ng-model="vm.resolvedData[vm.fragment]"
                    ng-change="onNgChange('{{vm.uiSchema}}')"
                    ng-readonly="vm.uiSchema.readOnly">
                </textarea>
            </jsonforms-control>
        `
    };

}).run(['RendererService', 'JSONFormsTesters', function(RendererService, Testers) {
    RendererService.register('osee-text-control', Testers.and(

        // Inherit this custom control from schema that call out the following using this single option:
        Testers.optionIs('customControlName', 'oseeTextControl')
    ), 10);
}]);


