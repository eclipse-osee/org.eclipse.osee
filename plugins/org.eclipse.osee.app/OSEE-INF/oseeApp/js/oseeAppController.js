app.controller('oseeAppController', [ 'OseeAppSchema', '$route', '$scope',
		'$resource', function(OseeAppSchema, $route, $scope, $resource) {
			$route.current.params;

			var vm = this;
			vm.dataloaded = false;
			vm.appId = $route.current.params.uuid;
			vm.name = $route.current.params.name;
			vm.element = $route.current.params.element;
			vm.oseeAppData = {};
			vm.doItem = false;
			vm.doList = false;

			OseeAppSchema.get({
				appId : vm.appId
			}, function(data) {
				vm.oseeAppSchema = data.OseeApp;
				vm.dataloaded = true;

				vm.listDataResource = $resource(data.ListRestURL);
				vm.itemDataResource = $resource(data.ItemRestURL);
				$scope.schemaKey = data.SchemaKey;
				if (vm.element === undefined) {
					vm.listDataResource.get({}, function(data) {
						vm.oseeAppDataArray = data;
						vm.doList = true;
					});
				} else {
					vm.itemDataResource.get({
						atsId : vm.element
					}, function(data) {
						vm.oseeAppData = data;
						vm.doItem = true;
					});
				}
			});
		} ]);
