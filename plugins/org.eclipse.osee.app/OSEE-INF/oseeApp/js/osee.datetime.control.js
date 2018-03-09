
app.directive('oseeDateTimeControl', function () {
    return {
        restrict: 'E',
        controller: ['BaseController', '$scope', '$routeParams', 'OseeAppSchema', function (
                BaseController, $scope, $routeParams, OseeAppSchema) {
                var vm = this;
                vm.isValid = OseeAppSchema.isValid;

                $scope.onNgBlur = function () {
                    if (OseeAppSchema.isValid(vm.resolvedData[vm.fragment], vm.uiSchema.options.required)) {
                        OseeAppSchema.doUpdate();
                    }
                }
                $scope.onNgChanged = function () {
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
                $scope.onInit = function () {
                    var given;
                    var intermediate = vm.uiSchema.scope.$ref.substr(13);

                    var dateIndex;
                    if (intermediate.indexOf('/') > 0) {
                        dateIndex = intermediate.substring(0, intermediate.indexOf('/'));
                        given = new Date(vm.data[dateIndex].value);
                        vm.data[dateIndex].value = given;
                    } else {
                        dateIndex = intermediate;
                        given = new Date(vm.data[dateIndex]);
                        vm.data[dateIndex] = given;
                    }
                }
                BaseController.call(vm, $scope);
            }
        ],
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
        controllerAs: 'vm',
        template: `<jsonforms-control id="{{vm.scope.$id}}" ng-style="vm.uiSchema.style">
                   <input type="date"
                     close-text="Close"
                     is-open="vm.isOpen"
                     class="osee-date-time form-control jsf-control-datetime"
                     ng-model="vm.resolvedData[vm.fragment]"
                     ng-change="onNgChanged()"
                     ng-readonly="vm.uiSchema.readOnly"
                     ng-blur="onNgBlur()"
                     data-ng-init="onInit()">
                   </input>
                   </jsonforms-control>`
    };
}).run(['RendererService', 'JSONFormsTesters', function (RendererService, Testers) {
            RendererService.register('osee-date-time-control', Testers.and(
                    // Inherit this custom control from schema that call out the following using this single option:
                    Testers.optionIs('customControlName', 'oseeDateTimeControl')), 10);
        }
    ]);
