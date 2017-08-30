app.directive('oseeDateTimeControl', function() {
    return {
        restrict: 'E',
        controller: ['BaseController', '$scope', '$routeParams', 'OseeAppSchema', function(
                      BaseController, $scope, $routeParams, OseeAppSchema) {
            var vm = this;

            $scope.onNgBlur = function() {
            }
            $scope.onNgChanged = function(controlschema) {
                OseeAppSchema.updateItem(controlschema);
            }
            $scope.onInit = function() {
                var given = vm.data[vm.fragment];
                if (given) {
                    var date = new Date(given);
                    console.log("given date was: " + given + ", now " + date);
                    vm.data[vm.fragment] = date;
                }
                console.log("fragment = " + vm.fragment);
            }
            BaseController.call(vm, $scope);
        }],
        controllerAs: 'vm',
        template: `<jsonforms-control>
                   <input type="date"
                     close-text="Close"
                     is-open="vm.isOpen"
                     id="{{vm.id}}"
                     class="osee-date-time form-control jsf-control-datetime"
                     ng-model="vm.resolvedData[vm.fragment]"
                     ng-change="onNgChanged('{{vm.uiSchema}}')"
                     ng-readonly="vm.uiSchema.readOnly"
                     data-ng-init="onInit()">
                   </input>
                   </jsonforms-control>`
    };
}).run(['RendererService', 'JSONFormsTesters', function(RendererService, Testers) {
    RendererService.register('osee-date-time-control', Testers.and(
        // Inherit this custom control from schema that call out the following using this single option:
        Testers.optionIs('customControlName', 'oseeDateTimeControl')
    ), 10);
}]);
