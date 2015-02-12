(function() {
	var app = angular.module('ScriptConsoleApp', []);

	app.controller("FormController", [ '$http', function($http) {
		var store = this;

		store.formData = {
			filename : 'output.xml'
		};
		store.message = '';

		this.processForm = function() {
			store.message = '';
			if (store.formData.excel) {
				console.log("Processing as Excel");
				var request = [];
				request.push("../../script.xml?", $.param(store.formData));
				var url = request.join("");

				window.open(url);

				store.message = 'Excel Output Generated';
			} else {
				console.log("Processing as Json");
				$http({
					method : 'POST',
					url : '../',
					data : $.param(store.formData),
					headers : {
						'Content-Type' : 'application/x-www-form-urlencoded',
						'Accept' : 'application/json'
					}
				}).success(function(data, status, headers, config) {
					console.log(data);
					store.message = angular.toJson(data, true);
				}).error(function(data, status, headers, config) {
					console.log(data);
					store.message = 'error - status: ' + status + ' ' + data;
				});
			}
		}
	} ]);
})();
