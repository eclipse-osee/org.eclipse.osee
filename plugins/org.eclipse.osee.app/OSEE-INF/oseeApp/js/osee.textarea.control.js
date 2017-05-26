app.directive('oseeTextareaControl', function() {
        return {
            restrict: 'E',
            controller: ['BaseController', '$scope', function(BaseController, $scope) {
                var vm = this;
                BaseController.call(vm, $scope);
            }],
            controllerAs: 'vm',
            template: `
                <jsonforms-control>
                    <textarea rows="6" id="{{vm.id}}"
                                class="osee-textarea form-control jsf-control-string"
                                ng-model="vm.resolvedData[vm.fragment]"
                                ng-change='vm.triggerChangeEvent()'
                                ng-readonly="vm.uiSchema.readOnly"/>
                </jsonforms-control>
            `
        };
    })
    .run(['RendererService', 'JSONFormsTesters', function(RendererService, Testers) {
        RendererService.register('osee-textarea-control', Testers.and(
            Testers.uiTypeIs('Control'),
            Testers.schemaTypeIs('string'),
            Testers.optionIs('multi', true)
        ), 10);
    }]);