/*********************************************************************
* Copyright (c) 2023 Boeing
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     Boeing - initial API and implementation
**********************************************************************/

app.directive('oseeTextareaControl', function () {
    return {
        restrict: 'E',
        controller: ['BaseController', '$scope', '$routeParams', 'OseeAppSchema', function (
                BaseController, $scope, $routeParams, OseeAppSchema) {
                var vm = this;
                vm.isValid = OseeAppSchema.isValid;

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
                $scope.onNgBlur = function () {
                    if (OseeAppSchema.isValid(vm.resolvedData[vm.fragment], vm.uiSchema.options.required)) {
                        OseeAppSchema.doUpdate();
                    }
                }
                BaseController.call(vm, $scope, OseeAppSchema);
                console.log(document.getElementById(vm.scope.$id));
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
            <jsonforms-control id="{{vm.scope.$id}}" ng-style="vm.uiSchema.style">
                <textarea
                    class="form-control jsf-control-string osee-textarea"
                    rows="{{vm.uiSchema.rows}}"
                    ng-model="vm.resolvedData[vm.fragment]"
                    ng-change="onNgChange()"
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
