
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
