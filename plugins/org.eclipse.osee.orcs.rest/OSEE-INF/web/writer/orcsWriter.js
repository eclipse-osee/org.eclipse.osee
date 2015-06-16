var app = angular.module('OrcsWriterApp', []);

app.controller("FormController", function($scope, $http) {

	$scope.formData = {
		filename : '',
		json : ''
	};
	$scope.message = '';

	$scope.validate = function() {
		$scope.run(true);
	}

	$scope.execute = function() {
		$scope.run(false);
	}

	$scope.setFiles = function(element) {
		$scope.$apply(function() {
			console.log('files:', element.files);
			$scope.formData.filename = "";
			$scope.formData.asJson = false;
			if (element.files[0]) {
				$scope.formData.filename = element.files[0];
			}
		});
	};

	$scope.run = function(validate) {
		$scope.message = '';
		var url = "";
		if (validate) {
			url = "../../writer/validate";
		} else {
			url = "../../writer";
		}
		var data = {};
		if (!$scope.formData.json && !$scope.formData.filename) {
			$scope.message = "ERROR: Must select Excel or enter JSON";
		} else if ($scope.formData.json) {
			$scope.message = "Processing JSON";
			data = $scope.formData.json;
			$http({
				method : 'POST',
				url : url,
				data : data,
				headers : {
					'Accept' : 'application/json',
					'Content-Type' : 'application/json'
				}
			}).success(function(data, status, headers, config) {
				$scope.message += "\nValidation Passed";
			}).error(function(data, status, headers, config) {
				var message = 'error - status: ' + status + ' ' + data;
				if (data.exception) {
					message += ' Exception: ' + data.exception;
				}
				$scope.message += '\n' + message;
			});
		} else if ($scope.formData.filename) {
			$scope.message = "Processing Excel - NOT IMPLEMENTED YET";
			$scope.message += '\nExcel Output Generated';
		}
	}
});
