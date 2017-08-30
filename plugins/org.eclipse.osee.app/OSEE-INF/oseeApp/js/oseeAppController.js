app.controller('oseeAppController', [
        'OseeAppSchema',
        '$route',
        '$scope',
        '$resource',
        function(OseeAppSchema, $route, $scope, $resource) {
            $route.current.params;
            var vm = this;
            vm.dataloaded = false;
            vm.appId = $route.current.params.uuid;
            vm.name = $route.current.params.name;
            vm.element = $route.current.params.element;
            vm.oseeAppData = {};
            vm.doItem = false;

            OseeAppSchema.get({
                appId : vm.appId
            }, function(data) {
                vm.oseeAppSchema = data.OseeApp;
                vm.dataloaded = true;
                vm.defaultUpdateURL = data.DefaultUpdateURL;
                vm.itemDataResource = $resource(data.ItemRestURL);
                $scope.schemaKey = data.SchemaKey;
                if (vm.element != undefined) {
                    vm.itemDataResource.query({
                        atsId : vm.element
                    }, function(data) {
                        vm.oseeAppData = data[0];
                        vm.doItem = true;
                    });
                }
            });

            OseeAppSchema.updateItem = function(controlschema) {
                var input = JSON.parse(controlschema);
                var changedItem = input.scope.$ref.substr(input.scope.$ref.lastIndexOf('/') + 1);
                var changedData = vm.oseeAppData[changedItem];
                var jsonData = JSON.stringify(changedData);
                vm.putResource = $resource(
                        vm.defaultUpdateURL, null, {
                            'update' : {
                                method : 'PUT',
                                headers: {
                                    'Content-Type': 'application/json'
                                }
                            }
                        });
                vm.putResource.update({
                    wfId : vm.element,
                    attrTypeId : changedItem
                }, "[" + jsonData + "]");
            }
        } ]);
