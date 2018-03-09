app.controller('oseeAppController', [
        'OseeAppSchema',
        'OseeControlValues',
        '$route',
        '$scope',
        '$resource',
        function (OseeAppSchema, OseeControlValues, $route, $scope, $resource) {
            $route.current.params;
            var vm = this;
            vm.dataloaded = false;
            vm.loaded = false;
            vm.name = {};
            vm.appId = $route.current.params.uuid;
            OseeControlValues.setActiveApp(vm.appId);
            vm.element = $route.current.params.element;
            vm.oseeAppData = {};
            vm.useSubmit = false;
            $scope.$on('$locationChangeStart', function (event) {
                // make sure the last update is captured before leaving
                OseeAppSchema.doUpdate();
            });

            OseeAppSchema.get({
                appId: vm.appId
            }, function (data) {
                vm.oseeAppSchema = data.OseeApp;
                vm.dataloaded = true;
                vm.defaultUpdateURL = data.ItemUpdateURL;
                vm.baseName = data.BaseName;
                vm.identifier = data.NameContent;
                OseeControlValues.setCreateUrl(data.ItemCreateURL);
                if (data.ItemSubmitURL) {
                    vm.submitURL = data.ItemSubmitURL;
                    vm.useSubmit = true;
                }
                vm.itemDataResource = $resource(data.ItemGetURL);
                $scope.schemaGetKey = data.SchemaGetKey;
                if (vm.element !== undefined) {
                    if (data.SchemaType === 'isArray') {
                        OseeControlValues.queryUrl(data.ItemGetURL, true).query({
                            element: vm.element
                        }, function (data) {
                            vm.oseeAppData = data[0];
                            vm.name = vm.baseName + vm.oseeAppData[vm.identifier];
                            vm.loaded = true;
                        }, function (response) {
                            vm.failed = true;
                            alert("Problem: " + response.message);
                        });
                    } else if (data.SchemaType === 'isItem') {
                        OseeControlValues.queryUrl(data.ItemGetURL, false).query({
                            element: vm.element
                        }, function (data) {
                            vm.oseeAppData = data;
                            vm.name = vm.baseName + vm.oseeAppData[vm.identifier];
                            vm.loaded = true;
                        }, function (response) {
                            vm.failed = true;
                            alert("Problem: " + response.message);
                        });
                    }
                }
            });

            this.submitForm = function () {
                vm.itemSubmitResource = $resource(vm.submitURL, null, {
                        'submit': {
                            method: 'PUT',
                            headers: {
                                'Content-Type': 'application/json'
                            }
                        }
                    });
                if (vm.loaded === true) {
                    vm.itemSubmitResource.submit({
                        element: vm.element
                    }, JSON.stringify(vm.oseeAppData));
                } else {
                    alert("Data not ready to be submitted.");
                }
            };
            OseeAppSchema.getElement = function () {
                return vm.element;
            };

            OseeAppSchema.doUpdate = function () {
                if (this.changedItem) {
                    var jsonData;
                    if (this.changedData instanceof Date) {
                        jsonData = JSON.stringify(this.changedData.getTime());
                    } else {
                        jsonData = JSON.stringify(this.changedData);
                    }
                    var userId = OseeControlValues.getActiveUserId();
                    OseeControlValues.putUrl(vm.defaultUpdateURL, false).submit({
                        element: vm.element,
                        updated: this.changedItem
                    }, "[" + jsonData + "]").$promise.then(
                        function (data) {
                        console.log(data);
                        // will need to update the gamma here
                    }, function (response) {
                        vm.failed = true;
                        alert("Problem: " + response.message);
                    });
                    this.changedItem = null;
                }
            };
            OseeAppSchema.updateItem = function (uiSchema, change) {
                var intermediate = uiSchema.scope.$ref.substr(13);
                this.changedItem = intermediate.substring(0, intermediate.indexOf('/'));
                this.changedData = change;
            };
            OseeAppSchema.isValid = function (value, regex) {
                // the regex represents invalid strings
                if (!regex)
                    return true;
                if (!value)
                    return false;
                var regexp = RegExp(regex);
                if (regexp.test(value))
                    return false;
                return true;
            };
        }
    ]);
