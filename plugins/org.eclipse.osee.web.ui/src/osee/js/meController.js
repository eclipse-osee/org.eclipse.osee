app.controller('meController', [
    '$scope',
    '$localStorage',
    '$modal',
    'Preferences',
    'uiGridConstants',
    function($scope, $localStorage, $modal, Preferences, uiGridConstants) {
    	$scope.editLinks = false;
    	
    	$scope.editItem = function(item, key) {
    		if(item.url.match(/http.*/) == null) {
    			item.url = "http://" + item.url;
    		}
    		Preferences.update({
                id: $localStorage.uuid,
                key: key,
                itemId: item.id,
            }, item, function() {
            }, function(data) {
                alert("Could not make change, please try refreshing");
            });
    	}
    	
    	
    	$scope.deleteLink = function(item) {
    		var newLink = {};
    		var key = "links";
    		Preferences.update({
                id: $localStorage.uuid,
                key: key,
                itemId: item.id,
            }, newLink, function() {
            	$scope.getPreferences();
            }, function(data) {
                alert("Could not make change, please try refreshing");
            });
    	}
    	
    	var clickCellTmpl = '<a href="{{ COL_FIELD }}" target="_blank">{{ COL_FIELD }}</a>'
        var dellCellTmpl = '<button width="35px" class="btn btn-default btn-sm setDelete" ng-show="!readOnly" ng-click="grid.appScope.deleteLink(row.entity)">X</button>';
    	
    	$scope.defaultGridOptions = {
    			data: 'defaultLinks',
    			columnDefs: [
    			     {field: 'name', displayName: 'Name'},
    			     {field: 'url', displayName: 'URL', cellTemplate: clickCellTmpl}
    			],
    			onRegisterApi: function( gridApi ) {
    			      $scope.gridApi = gridApi;
    			      gridApi.edit.on.afterCellEdit($scope,function(rowEntity, colDef, newValue, oldValue){
    			            this.grid.appScope.editItem(null);
    			          });
    			    }
    			
    	};
    	
    	$scope.personalColumns = [
    	         			     {field: 'name', displayName: 'Name', cellEditableCondition: function( $scope ) { 
    	        			    	 return $scope.$parent.$parent.grid.appScope.editLinks; }},
    	        			     {field: 'url', displayName: 'URL', cellTemplate: clickCellTmpl, cellEditableCondition: function( $scope ) { 
    	        			    	 return $scope.$parent.$parent.grid.appScope.editLinks; }},
	        			    	 {field: 'delete', width: 35, displayName: 'X', enableCellEdit: false, cellTemplate: dellCellTmpl, enableColumnMenu: false, visible: false}
    	        			];
    	
    	$scope.personalGridOptions = {
    			data: 'personalLinks',
    			columnDefs: $scope.personalColumns,
    			onRegisterApi: function( gridApi ) {
    			      $scope.gridApi = gridApi;
    			      gridApi.edit.on.afterCellEdit($scope,function(rowEntity, colDef, newValue, oldValue){
    			    	  	if(newValue != oldValue) {
    			    	  		this.grid.appScope.editItem(rowEntity, 'links');
    			    	  	}
    			          });
    			    }
    			
    	};
    	
    	$scope.toggleEditLinks = function() {
    		$scope.editLinks = !$scope.editLinks;
    		$scope.personalColumns[2].visible = !($scope.personalColumns[2].visible || $scope.personalColumns[2].visible === undefined);
    		$scope.gridApi.core.notifyDataChange(uiGridConstants.dataChange.COLUMN);
    	}
    	
    	
    	$scope.getPreferences = function() {
        	Preferences.get({
        		id: $localStorage.uuid
        	}, function(data2) {
        		$scope.personalLinks = Object.keys(data2.links).map(function (key) {return data2.links[key]});
        	});
    	}

    	
    	$scope.createNewLink = function(name, url) {
    		var newLink = {};
    		newLink.name = name;
    		if(url.match(/http.*/) == null) {
    			url = "http://" + url;
    		}
    		newLink.url = url;
    		var key = "links";
    		
    		Preferences.update({
                id: $localStorage.uuid,
                key: key,
                itemId: null,
            }, newLink, function() {
            	$scope.getPreferences();
            }, function(data) {
                alert("Could not make change, please try refreshing");
            });
    	}
    	
    	
        // Create Set Modal
        $scope.showCreateLinkModal = function() {
            var modalInstance = $modal.open({
                templateUrl: 'popup.html',
                controller: CreateSetModalCtrl,
                size: 'sm',
                windowClass: 'createSetModal'
            });

            modalInstance.result.then(function(inputs) {
                $scope.createNewLink(inputs.name, inputs.url);
            });
        }

        var CreateSetModalCtrl = function($scope, $modalInstance) {
            $scope.linkName = "";
            $scope.linkUrl = "";
            
            $scope.ok = function() {
                var inputs = {};
                inputs.name = this.linkName;
                inputs.url = this.linkUrl;
                $modalInstance.close(inputs);
            };

            $scope.cancel = function() {
                $modalInstance.dismiss('cancel');
            };
        };
    	$scope.$on("osee:userAuthenticated", function(event, token) {
        	$scope.getDefaultPreferences();
        	$scope.getPreferences();
    	});
    	
    }]);