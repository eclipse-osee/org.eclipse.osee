var app = angular.module('BranchSelectorApp', []);

app.controller("FormController", function($scope, $http) {
	$scope.branches = [];
	$scope.selectedBranch = "0";
	$scope.formData = {
	};

	$http.get('../../branches').then(function(res) {
		$scope.branches = res.data;
	});

});
