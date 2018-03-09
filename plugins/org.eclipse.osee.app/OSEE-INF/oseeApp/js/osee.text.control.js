
app.directive('oseeTextControl', function () {
    return {
        restrict: 'E',
        controller: ['BaseController', '$scope', 'OseeAppSchema', function (BaseController, $scope, OseeAppSchema) {
                var vm = this;
                if (!vm.isValid)
                    vm.isValid = OseeAppSchema.isValid;

                $scope.onNgBlur = function () {
                    if (OseeAppSchema.isValid(vm.resolvedData[vm.fragment], vm.uiSchema.options.required)) {
                        OseeAppSchema.doUpdate();
                    }
                }

                $scope.onNgChange = function () {
                    if (OseeAppSchema.isValid(vm.resolvedData[vm.fragment], vm.uiSchema.options.required)) {
                        vm.uiSchema.style = {
                            color: 'black'
                        };
                    } else {
                        vm.uiSchema.style = {
                            color: 'red'
                        };
                    }
                    OseeAppSchema.updateItem(vm.uiSchema, vm.resolvedData[vm.fragment]);
                }
                BaseController.call(vm, $scope, OseeAppSchema);
            }
        ],
        controllerAs: 'vm',
        link: function link(scope, element, attrs, ctrl) {
            if (ctrl.resolvedData) {
                if (!ctrl.isValid(ctrl.resolvedData[ctrl.fragment], ctrl.uiSchema.options.required)) {
                    console.log("invalid control data according to the uiSchema regex");
                    element.css({
                        color: 'red'
                    });
                }
            }
        },
        template: `
            <jsonforms-control id="{{vm.scope.$id}}" ng-style="vm.uiSchema.style">
                <input 
                    class="form-control jsf-control-string osee-text"
                    ng-model="vm.resolvedData[vm.fragment]"
                    ng-change="onNgChange()"
                    ng-blur="onNgBlur()"
                    ng-readonly="vm.uiSchema.readOnly">
                </input>
            </jsonforms-control>
        `
    };

}).run(['RendererService', 'JSONFormsTesters', function (RendererService, Testers) {
            RendererService.register('osee-text-control', Testers.and(

                    // Inherit this custom control from schema that call out the following using this single option:
                    Testers.optionIs('customControlName', 'oseeTextControl')), 10);
        }
    ]);
