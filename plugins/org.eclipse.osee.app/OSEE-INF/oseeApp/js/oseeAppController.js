app.controller('oseeAppController', [
        'OseeAppSchema',
        'OseeControlValues',
        '$route',
        '$scope',
        '$resource',
        function(OseeAppSchema, OseeControlValues, $route, $scope, $resource) {
            $route.current.params;
            var vm = this;
            vm.dataloaded = false;
            vm.loaded = false;
            vm.appId = $route.current.params.uuid;
            vm.name = $route.current.params.name;
            vm.element = $route.current.params.element;
            vm.oseeAppData = {};
            vm.useSubmit = false;

            OseeAppSchema.get({
                appId: vm.appId
            }, function (data) {
                vm.oseeAppSchema = data.OseeApp;
                vm.dataloaded = true;
                vm.defaultUpdateURL = data.ItemUpdateURL;
                if (data.ItemSubmitURL) {
                    vm.submitURL = data.ItemSubmitURL;
                    vm.useSubmit = true;
                }
                vm.itemDataResource = $resource(data.ItemGetURL);
                $scope.schemaGetKey = data.SchemaGetKey;
                if (vm.element !== undefined) {
                    if (data.SchemaType === 'isArray') {
                        vm.itemDataResource.query({
                            element: vm.element
                        }, function (data) {
                            vm.oseeAppData = data[0];
                            vm.loaded = true;
                        });
                    } else if (data.SchemaType === 'isItem') {
                        vm.itemDataResource.get({
                            element: vm.element
                        }, function (data) {
                            vm.oseeAppData = data;
                            vm.loaded = true;
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

            OseeAppSchema.updateItem = function (controlschema) {
                var input = JSON.parse(controlschema);
                var changedItem = input.scope.$ref.substr(input.scope.$ref.lastIndexOf('/') + 1);
                var changedData = vm.oseeAppData[changedItem];
                var jsonData = JSON.stringify(changedData);
                vm.putResource = $resource(
                        vm.defaultUpdateURL, null, {
                        'update': {
                            method: 'PUT',
                            headers: {
                                'Content-Type': 'application/json'
                            }
                        }
                    });
                vm.putResource.update({
                    element: vm.element,
                    updated: changedItem
                }, "[" + jsonData + "]");
            }
        }
    ]);
