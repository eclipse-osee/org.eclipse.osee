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
app.directive('oseeCheckboxControl', function () {
    return {
        restrict: 'E',
        controller: ['BaseController', '$scope', '$routeParams', 'OseeAppSchema', function (
                BaseController, $scope, $routeParams, OseeAppSchema) {
                var vm = this;

                $scope.onNgChanged = function () {
                    OseeAppSchema.updateItem(vm.uiSchema, vm.resolvedData[vm.fragment]);
                    OseeAppSchema.doUpdate();
                }
                $scope.subLabelExists = function () {
                    if (vm.uiSchema.options.subLabel)
                        return true;
                    else
                        return false;
                }
                BaseController.call(vm, $scope);

            }
        ],
        controllerAs: 'vm',
        template: `
            <jsonforms-control ng-style="vm.uiSchema.style">
                <span class="osee-checkbox-label" ng-if="subLabelExists()" >{{vm.uiSchema.options.subLabel}}</span>
                <input id="{{vm.id}}"
                    type="checkbox"
                    class="form-control-boolean"
                    ng-model="vm.resolvedData[vm.fragment]"
                    ng-change="onNgChanged()"
                    ng-readonly="vm.uiSchema.readOnly">
                </input>
            </jsonforms-control>
        `
    };

}).run(['RendererService', 'JSONFormsTesters', function (RendererService, Testers) {
            RendererService.register('osee-checkbox-control', Testers.and(
                    // Inherit this custom control from schema that call out the following using this single option:
                    Testers.optionIs('customControlName', 'oseeCheckboxControl')), 10);
        }
    ]);
