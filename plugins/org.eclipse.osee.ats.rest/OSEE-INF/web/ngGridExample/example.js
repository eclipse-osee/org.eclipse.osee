var app = angular.module('ExampleApp', [ 'ngGrid' ]);

function ExampleCtrl($scope, $http, $window) {

	var $url = 'http://localhost:8089/ats/action/ATS12,ATS13,ATS11,ATS6,ATS3,ATS7,ATS4/details';
	$http.get($url).success(function(data) {
		$scope.items = data;
		$scope.gridOptions.sortBy('AtsId');
	});

	function dateSort(aDate, bDate) {
		var a = new Date(aDate);
		var b = new Date(bDate);
		if (a < b) {
			return -1;
		} else if (a > b) {
			return 1;
		} else {
			return 0;
		}
	}

	var idCellTmpl = '<button class="btn btn-default btn-sm" ng-enabled="!row.entity.actionLocation" ng-click="openLink(row.entity.actionLocation)">{{row.getProperty(col.field)}}</button>';

	$scope.gridOptions = {
		data : 'items',
		enableHighlighting : true,
		selectedItems : $scope.selectedItems,
		enableColumnResize : true,
		showFilter : true,
		sortInfo : {
			fields : [ 'AtsId' ],
			directions : [ 'asc' ]
		},
		columnDefs : [ {
			field : 'AtsId',
			displayName : 'ATS ID',
			width : 85,
			cellTemplate : idCellTmpl
		}, {
			field : 'TeamName',
			displayName : 'Team',
			width : 100
		}, {
			field : 'Priority',
			displayName : 'Priority',
			width : 20
		}, {
			field : 'ChangeType',
			displayName : 'Change Type',
			width : 35
		}, {
			field : 'State',
			displayName : 'State',
			width : 100
		}, {
			field : 'Name',
			displayName : 'Title',
			width : 400
		}, {
			field : 'Assignees',
			displayName : 'Assignees',
			width : 100
		}, {
			field : 'CreatedBy',
			displayName : 'Created By',
			width : 100
		}, {
			field : 'CreatedDate',
			displayName : 'Created Date',
			width : 100
		} ]
	};

	$scope.openLink = function(url) {
		$window.open(url);
	}

}
