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
app.controller('indexController', ['Account', '$scope', 'OseeControlValues',
        function ( Account, $scope, OseeControlValues ) {
            var vm = this;
            var userName = Account.get({
                }, function(data){
                    $scope.userName = data.name;
                    OseeControlValues.setActiveUserId(data.userName);
            });
            $scope.createAction = function () {
                if(!$scope.createTitle) {
                    alert("Invalid input - must input title");
                }
                else {
                    OseeControlValues.createAction($scope.createTitle);
                    $scope.createTitle = null;
                }
            };
            $scope.findAction = function () {
                if(!$scope.element) {
                    alert("Invalid input - must input element");
                }
                else {
                    OseeControlValues.setWindowLocation($scope.element);               
                    $scope.element = null;
                }
            };
        }
    ]);