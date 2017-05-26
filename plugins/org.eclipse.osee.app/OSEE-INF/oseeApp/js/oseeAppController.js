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

				vm.dataResource = $resource(data.RestURL);
				$scope.schemaKey = data.SchemaKey;
				console.log(data.RestURL);
				console.log(data.SchemaKey);
				if (vm.element === undefined) {
					vm.dataResource.get({}, function(data) {
						vm.oseeAppDataArray = data;
						console.log(data);
						vm.doList = true;
					});
				} else {
					vm.dataResource.get({
						issueId : vm.element
					}, function(data) {
						vm.oseeAppData = data;
						vm.doItem = true;
					});
				}
			});
		} ]);
