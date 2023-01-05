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

app.controller('startController', ['OseeAppSchema', '$route', '$rootScope',
        function (OseeAppSchema, $route, $rootScope) {
            $route.current.params;

            var vm = this;
            vm.dataloaded = false;

            OseeAppSchema.query(function (data) {
                vm.startData = data;
                vm.dataloaded = true;
            });
        }
    ]);
