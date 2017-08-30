app.controller('oseeListController', ['OseeAppSchema', '$route', '$resource',
        function (OseeAppSchema, $route, $resource) {
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

            vm.startdataloaded = false;

            OseeAppSchema.query(function (data) {
                vm.startData = data;
                vm.datadataloaded = true;
            });

            OseeAppSchema.get({
                appId: vm.appId
            }, function (result) {
                vm.oseeAppSchema = result.OseeApp;
                vm.allGridSettings.columnDefs = result.UIGridColumns;
                vm.gridOptions = vm.allGridSettings;
                vm.listDataResource = $resource(result.ListRestURL);
                vm.listDataResource.query(function(listData) {
                    vm.allGridSettings.enableColumnResizing = true;
                    vm.allGridSettings.data = listData;
                    vm.gridOptions = vm.allGridSettings;
                    vm.loaded = true;
                });
            });
        }]);