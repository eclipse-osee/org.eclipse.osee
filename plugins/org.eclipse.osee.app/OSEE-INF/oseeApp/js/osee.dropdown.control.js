
app.directive('oseeDropdownControl', function () {
    return {
        restrict: 'E',
        controller: ['BaseController', '$scope', 'OseeAppSchema', 'OseeControlValues', function (BaseController, $scope, OseeAppSchema, OseeControlValues) {
                var vm = this;
                vm.isValid = OseeAppSchema.isValid;

                $scope.onInit = function () {
                    if ($scope.uischema.scope.getUrl) {
                        vm.possibleSelections = OseeControlValues.queryUrl($scope.uischema.scope.getUrl, true).query({
                                element: OseeAppSchema.getElement()
                            }, function (selections) {
                                vm.possibleSelections = selections;
                            });
                    } else
                        vm.possibleSelections = vm.resolvedSchema.enum;
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
                    if (vm.resolvedData.value) {
                        OseeAppSchema.updateItem(vm.uiSchema, vm.resolvedData.value);
                    }
                }
                $scope.onNgBlur = function () {
                    OseeAppSchema.doUpdate();
                }
                $scope.linkExists = function () {
                    if (vm.uiSchema.options.link)
                        return true;
                    else
                        return false;
                }
                BaseController.call(vm, $scope, OseeAppSchema);
            }
        ],
        controllerAs: 'vm',
        link: function link(scope, element, attrs, ctrl) {
            if (ctrl.resolvedData) {
                if (!ctrl.isValid(ctrl.resolvedData.value, ctrl.uiSchema.options.required)) {
                    console.log("invalid control data according to the uiSchema regex");
                    element.
                    css({
                        color: 'red'
                    });
                }
            }
        },
        template: `
            <jsonforms-control ng-style="vm.uiSchema.style">
                <span ng-if = "linkExists()">
                    <label>{{vm.uiSchema.options.subLabel}}</label>
                    <a href="{{vm.uiSchema.options.link}}" class="btn pull-right">{{vm.uiSchema.options.linkText}}</a>
                </span>
                    <select ng-options="option as option for option in vm.possibleSelections"
                        id="{{vm.id}}"
                        class="form-control jsf-control-enum"
                        ng-model="vm.resolvedData[vm.fragment]"
                        ng-change="onNgChange()"
                        ng-blur="onNgBlur()"
                        data-ng-init="onInit()">
                    </select>
               </jsonforms-control>
            `
    };
})
.run(['RendererService', 'JSONFormsTesters', function (RendererService, Testers) {
            RendererService.register('osee-dropdown-control', Testers.and(
                    // Inherit this custom control from schema that call out the following using this single option:
                    Testers.optionIs('customControlName', 'oseeDropdownControl')), 10);
        }
    ]);
