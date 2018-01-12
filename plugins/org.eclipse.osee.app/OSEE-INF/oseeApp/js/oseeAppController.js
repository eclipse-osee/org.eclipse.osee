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
                vm.createURL = data.ItemCreateURL;
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
                        }, function (response) {
                            vm.failed = true;
                            alert("Problem: " + response.message);
                        });
                    } else if (data.SchemaType === 'isItem') {
                        vm.itemDataResource.get({
                            element: vm.element
                        }, function (data) {
                            vm.oseeAppData = data;
                            vm.loaded = true;
                        }, function (response) {
                            vm.failed = true;
                            alert("Problem: " + response.message);
                        });
                    }
                }
            });

            this.goBack = function () {
                history.back();
            };
            this.newAction = function () {
                vm.createAction = $resource(vm.createURL, null, {
                        'create': {
                            method: 'POST'
                        }
                    });

                vm.createAction.create()
                .$promise.then(
                    function (data) {
                    window.location.href = 'index.html#/osee_app?uuid=' + vm.appId +
                        '&name=' + vm.name + '&element=' + data.id;
                }, function (response) {
                    vm.failed = true;
                    alert("Problem: " + response.data);
                });
            };

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
            OseeAppSchema.doUpdate = function () {
                if (this.changedItem) {
                        var jsonData;
                        if(this.changedData.value instanceof Date)  { 
                            jsonData = JSON.stringify(this.changedData.value.getTime());
                        } else {
                            jsonData = JSON.stringify(this.changedData.value);
                        }
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
                        updated: this.changedItem
                    }, "[" + jsonData + "]").$promise.then(
                        function (data) {
                        // will need to update the gamma here
                    }, function (response) {
                        vm.failed = true;
                        alert("Problem: " + response.message);
                    });
                    this.changedItem = null;
                }
            };
            OseeAppSchema.updateItem = function (controlschema) {
                var input = JSON.parse(controlschema);
                var intermediate = input.scope.$ref.substr(13);
                this.changedItem = intermediate.substring(0, intermediate.indexOf('/'));
                this.changedData = vm.oseeAppData[this.changedItem];
            }
        }
    ]);
