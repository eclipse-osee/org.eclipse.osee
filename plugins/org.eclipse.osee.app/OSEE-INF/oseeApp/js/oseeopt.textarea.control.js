app.directive('oseeoptTextareaControl', function() {
        return {
            restrict: 'E',
            controller: ['BaseController', '$scope', function(BaseController, $scope) {
                var vm = this;
                BaseController.call(vm, $scope);
            }],
            controllerAs: 'vm',
            template: `
                <jsonforms-control>
                    <textarea rows="2" id="{{vm.id}}"
                                class="oseeopt-textarea form-control jsf-control-string"
                                ng-model="vm.resolvedData[vm.fragment]"
                                ng-change='vm.triggerChangeEvent()'
                                ng-readonly="vm.uiSchema.readOnly"/>
                </jsonforms-control>
            `
        };
    })
    .run(['RendererService', 'JSONFormsTesters', function(RendererService, Testers) {
        RendererService.register('oseeopt-textarea-control', Testers.and(
            Testers.uiTypeIs('Control'),
            Testers.schemaTypeIs('string'),
            Testers.optionIs('multi', false)
        ), 10);
    }]);