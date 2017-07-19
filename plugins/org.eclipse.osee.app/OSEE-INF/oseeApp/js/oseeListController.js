app.controller('oseeListController', [ 'OseeAppSchema', '$route', '$resource',
		function(OseeAppSchema, $route, $resource) {
			$route.current.params;

			var vm = this;
			vm.appId = $route.current.params.uuid;
			vm.name = $route.current.params.name;
			vm.splash = "Item List for: " + vm.name;
			vm.oseeAppData = {};
			vm.resultData = {};
			vm.loaded = false;
			vm.gridOptions = {
				enableFiltering : true
			};
			OseeAppSchema.get({
				appId : vm.appId
			}, function(result) {
				vm.oseeAppSchema = result.OseeApp;
				vm.gridOptions = {
					columnDefs : result.UIGridColumns
				};
				vm.listDataResource = $resource(result.ListRestURL);
				vm.listDataResource.query(function(listData) {
					vm.gridOptions = {
						enableColumnResizing : true,
						data : listData
					};
					vm.loaded = true;
				});
			});
		} ]);