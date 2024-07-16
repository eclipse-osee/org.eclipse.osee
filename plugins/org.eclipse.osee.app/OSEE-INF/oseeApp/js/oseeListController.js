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

app.controller('oseeListController', ['OseeAppSchema', 'OseeControlValues', '$route', '$resource',
        function (OseeAppSchema, OseeControlValues, $route, $resource) {
            $route.current.params;

            var vm = this;
            vm.appId = $route.current.params.uuid;
            vm.name = $route.current.params.name;
            vm.splash = "Item List for: " + vm.name;
            vm.oseeAppData = {};
            vm.resultData = {};
            vm.loaded = false;
            vm.allGridSettings = {
                enableFiltering: true
            };
            vm.gridOptions = vm.allGridSettings;

            OseeAppSchema.get({
                appId: vm.appId
            }, function (result) {
                OseeAppSchema.query({
                    filter: result.QueryFilter
                }, function (data) {
                    vm.startData = data;
                });
                OseeControlValues.setActiveApp(result.ActiveAppId);
                vm.allGridSettings.columnDefs = result.UIGridColumns;
                vm.gridOptions = vm.allGridSettings;
                vm.listDataResource = $resource(result.ListRestURL);
                vm.listDataResource.query(function (listData) {
                    vm.allGridSettings.enableColumnResizing = true;
                    vm.allGridSettings.data = listData;
                    vm.gridOptions = vm.allGridSettings;
                    vm.loaded = true;
                }, function (response) {
                    vm.failed = true;
                    alert("Problem: " + response.message);
                });
            });
        }
    ]);
