app.factory('ColumnFactory', function() {
	var ColumnFactory = {};
	
	ColumnFactory.getColumns = function(type, width) {
		if(type == 'testScript'){
			return columnsTestScript;
		} else {
			return columnsCoverage;
		}
	}
	
	ColumnFactory.setResolutionTypeArray = function(type, array) {
		if(type == 'testScript') {
			for(var i = 0; i < subGridColumnsTestScript.length; i++) {
				if(subGridColumnsTestScript[i].field == 'resolutionType') {
					subGridColumnsTestScript[i].editDropdownOptionsArray = array;
					return;
				}
			}
		} else {
			for(var i = 0; i < subGridColumnsCoverage.length; i++) {
				if(subGridColumnsCoverage[i].field == 'resolutionType') {
					subGridColumnsCoverage[i].editDropdownOptionsArray = array;
					return;
				}
			}
		}
	}
	
	ColumnFactory.getSubGridColumns = function(type) {
		if(type == 'testScript'){
			return subGridColumnsTestScript;
		} else {
			return subGridColumnsCoverage;
		}
	}
	
    var usePureRegex = function(searchTerm, cellValue) {
    	var escapeRegex = new RegExp("\\\\", "img");
    	var strippedSearchTerm = searchTerm.replace(escapeRegex, "");
    	var regex = new RegExp(strippedSearchTerm,"img");
    	return cellValue.match(regex) != null;
    };
    
    var findPointInRanges = function(searchTerm, cellValue) {
    	var isFound = false;
    	var searchTermAsInt = parseInt(searchTerm);
    	var ranges = cellValue.split(",");
    	
    	for(var i = 0; i < ranges.length; i ++) {
    		var range = ranges[i];
    		
    		if(range.indexOf("-") > -1) {
    			var limits = range.split("-");
    			var endPoint = parseInt(limits[1]);
    			var starPoint = parseInt(limits[0]);

        		if(searchTermAsInt <= endPoint && starPoint <= searchTermAsInt) {
        			isFound = true;
        		}
    		} else {
    			var singlePoint = parseInt(range);
    			isFound = singlePoint == searchTermAsInt;
    		}
    		
    		if(isFound) {
    			return true;
    		}
    	}
    	
    	return false;
    }
	
    var checkboxSorting = function checkboxSorting(itemA, itemB) {
        if(itemA == itemB) {
        	return 0;
        } else if (itemA) {
            return -1;
        } else if (itemB) {
            return 1;
        } 
    };
    
    var dateSorting = function (itemA, itemB) {
    	var DateA = new Date(itemA);
    	var DateB = new Date(itemB);
    	
        if (DateA < DateB) {
            return -1;
        } else if (DateB < DateA) {
            return 1;
        } else {
        	return 0;
        }
    };

    var origCellTmpl = '<div ng-dblclick="grid.appScope.getItemDetails(row.entity, row, grid)" class="ui-grid-cell-contents" title="TOOLTIP">{{row.entity.name}}</div>';
    var chkBoxTemplate = '<input type="checkbox" class="form-control needsRerunBox" ng-model="row.entity.needsRerun" ng-change="grid.appScope.editNeedsRerun(row.entity)"></input>';
    var assigneeCellTmpl = '<div ng-dblclick="grid.appScope.stealItem(row.entity, row)">{{row.entity.assignee}}</div>';
    var deleteCellTmpl = '<div><button ng-class="{annotationDelete: true, \'btn btn-danger\': true}" ng-disabled="row.entity.guid == null" ng-click="grid.appScope.deleteAnnotation(row.entity)">X</button></div>';


    var columnsCoverage = [{
        field: 'name',
        displayName: 'Name',
        cellTemplate: origCellTmpl,
        filter: {
            condition: usePureRegex
        }
    }, {
        field: 'status',
        displayName: 'Status',
        width: '6%',
        filter: {
            condition: usePureRegex
        }
    }, 
    {
        field: 'totalPoints',
        displayName: 'Total',
        width: '4%',
        filter: {
            condition: usePureRegex
        }
    }, 
    {
        field: 'failureCount',
        displayName: 'Failure Count',
        width: '4%',
        filter: {
            condition: usePureRegex
        }
    }, {
        field: 'discrepanciesAsRanges',
        displayName: 'Failed Points',
        filter: {
            condition: findPointInRanges
        }
    }, {
        field: 'assignee',
        displayName: 'Assignee',
        width: '10%',
        enableCellEdit: false,
        cellTemplate: assigneeCellTmpl,
        filter: {
            condition: usePureRegex
        }
    } ,
    {
        field: 'team',
        displayName: 'Team',
        width: '5%',
        enableCellEdit: true,
        filter: {
            condition: usePureRegex
        }
    }, {
        field: 'itemNotes',
        width: '10%',
        displayName: 'Item Notes',
        enableCellEdit: true,
        filter: {
            condition: usePureRegex
        }
    },{
        field: 'needsRerun',
        width: '4%',
        displayName: 'Rerun?',
        type: 'boolean',
        filter: {
        },
        cellTemplate: chkBoxTemplate,
        enableCellEdit: true
    },{
        field: 'lastUpdated',
        displayName: 'Last Ran',
        enableCellEdit: false,
        filter: {
            condition: usePureRegex
        },
        sortingAlgorithm: dateSorting
    }, {
        field: 'category',
        displayName: 'Category',
        enableCellEdit: true,
        visible: false,
        filter: {
            condition: usePureRegex
        }
    }, {
        field: 'machine',
        displayName: 'Station',
        enableCellEdit: true,
        visible: false,
        filter: {
            condition: usePureRegex
        }
    },{
        field: 'creationDate',
        displayName: 'Creation Date',
        enableCellEdit: false,
        visible: false,
        filter: {
            condition: usePureRegex
        },
        sortingAlgorithm: dateSorting
    },{
            field: 'version',
            displayName: 'Version',
            enableCellEdit: false,
            visible: false,
            filter: {
                condition: usePureRegex
            }
    }, {
        field: 'methodNumber',
        displayName: 'Method Number',
        enableCellEdit: false,
        visible: false,
        filter: {
            condition: usePureRegex
        }
    }, {
        field: 'fileNumber',
        displayName: 'File Number',
        enableCellEdit: false,
        visible: false,
        filter: {
            condition: usePureRegex
        }
    } ];
    
    var columnsTestScript = [{
        field: 'name',
        displayName: 'Name',
        cellTemplate: origCellTmpl,
        filter: {
            condition: usePureRegex
        }
    }, {
        field: 'status',
        displayName: 'Status',
        width: '6%',
        filter: {
            condition: usePureRegex
        }
    }, 
    {
        field: 'totalPoints',
        displayName: 'Total',
        width: '4%',
        filter: {
            condition: usePureRegex
        }
    }, 
    {
        field: 'failureCount',
        displayName: 'Failure Count',
        width: '4%',
        filter: {
            condition: usePureRegex
        }
    }, {
        field: 'discrepanciesAsRanges',
        displayName: 'Failed Points',
        filter: {
            condition: findPointInRanges
        }
    }, {
        field: 'assignee',
        displayName: 'Assignee',
        width: '10%',
        enableCellEdit: false,
        cellTemplate: assigneeCellTmpl,
        filter: {
            condition: usePureRegex
        }
    } ,
    {
        field: 'team',
        displayName: 'Team',
        width: '5%',
        enableCellEdit: true,
        filter: {
            condition: usePureRegex
        }
    }, {
        field: 'itemNotes',
        width: '10%',
        displayName: 'Item Notes',
        enableCellEdit: true,
        filter: {
            condition: usePureRegex
        }
    },{
        field: 'needsRerun',
        width: '4%',
        displayName: 'Rerun?',
        type: 'boolean',
        filter: {
        },
        cellTemplate: chkBoxTemplate,
        enableCellEdit: true
    },{
        field: 'lastUpdated',
        displayName: 'Last Ran',
        enableCellEdit: false,
        filter: {
            condition: usePureRegex
        },
        sortingAlgorithm: dateSorting
    },{
        field: 'aborted',
        width: '4%',
        displayName: 'Aborted',
        enableCellEdit: false,
        filter: {
            condition: usePureRegex
        },
    }, {
        field: 'category',
        displayName: 'Category',
        enableCellEdit: true,
        visible: false,
        filter: {
            condition: usePureRegex
        }
    }, {
        field: 'machine',
        displayName: 'Station',
        enableCellEdit: true,
        visible: false,
        filter: {
            condition: usePureRegex
        }
    },{
        field: 'elapsedTime',
        displayName: 'Elapsed Time',
        enableCellEdit: false,
        visible: false,
        filter: {
            condition: usePureRegex
        },
        sortingAlgorithm: dateSorting
    },{
        field: 'creationDate',
        displayName: 'Creation Date',
        enableCellEdit: false,
        visible: false,
        filter: {
            condition: usePureRegex
        },
        sortingAlgorithm: dateSorting
    },{
            field: 'version',
            displayName: 'Version',
            enableCellEdit: false,
            visible: false,
            filter: {
                condition: usePureRegex
            }
    } ];
    
    var textCoverageTmpl = '<div class="ui-grid-cell-contents" title="TOOLTIP">{{ grid.appScope.getTextCoverage(row.entity) }}</div>';
    var resolutionCoverageTmpl = '<div class="ui-grid-cell-contents placeholder" ng-class="{\'placeholder-parent\': !row.entity.isLeaf, \'complete-parent\': row.entity.isAllComplete, \'none-complete-parent\': row.entity.isNoneComplete, \'some-complete-parent\': row.entity.isSomeComplete, \'almost-complete-parent\': row.entity.isAlmostComplete,  \'causing-invalid\': grid.appScope.isCausingInvalid(row.entity)}" title="TOOLTIP">{{ grid.appScope.getTextResolution(row.entity) }}</div>';
    var resolutionTypeTmpl = '<div class="ui-grid-cell-contents" ng-class="{\'placeholder-parent\': true}" title="TOOLTIP">{{ grid.appScope.getTextResolutionType(row.entity) }}</div>';
    var subGridOrigTmpl = '<div ng-class="{\'ui-grid-cell-contents\': true, annotationInput: true, invalid: grid.appScope.getInvalidLocRefs(row.entity), details: annotation.showDeets}" title="TOOLTIP">{{row.entity.locationRefs}}</div>';
    var codeTemplate = '<div class="method-number">{{ grid.appScope.selectedItem.methodNumber }}</div>';
    
   var getInvalidRes = function getInvalidRes(annotation) {
        return annotation.resolution != null && annotation.resolution != "" && !annotation.isResolutionValid;
    }

    
    var subGridColumnsCoverage = [
	{
	    field: 'blank',
	    displayName: 'Method',
        cellTemplate: codeTemplate,
	    enableCellEdit: false,
	    width: '4%',
	},
    {
        field: 'locationRefs',
        displayName: 'Code Line',
        enableCellEdit: false,
        width: '5%',
    },
    {
        field: 'resolutionType',
        displayName: 'Resolution Type',
        editableCellTemplate: '/dispo/views/dropdown.html',
        width: '7%',
        cellTemplate: resolutionTypeTmpl,
        editDropdownIdLabel: 'text',
        cellEditableCondition: function($scope) {
            return $scope.row.entity.isLeaf && !$scope.row.entity.isDefault;
        }
    }, {
        field: 'resolution',
        displayName: 'Resolution',
        width: '15%',
        cellTemplate: resolutionCoverageTmpl,
        cellEditableCondition: function($scope) {
            return $scope.row.entity.isLeaf && !$scope.row.entity.isDefault
        }

    }, {
        field: 'developerNotes',
        displayName: 'Developer Notes',
        cellEditableCondition: function($scope) {
            return $scope.row.entity.isLeaf
        }
    }, {
        field: 'customerNotes',
        displayName: 'Text',
        cellTemplate: textCoverageTmpl,
        enableCellEdit: false,
    }
];
    var resolutionTmpl = '<div class="ui-grid-cell-contents placeholder" ng-class="{\'causing-invalid\': grid.appScope.isCausingInvalid(row.entity)}" title="TOOLTIP">{{ grid.appScope.getTextResolution(row.entity) }}</div>';

    var subGridColumnsTestScript = [{
        field: 'locationRefs',
        displayName: 'Test Point(s)',
        enableCellEdit: true,
        cellTemplate: subGridOrigTmpl
    },
    {
        field: 'resolutionType',
        displayName: 'PCR Type',
        editableCellTemplate: '/dispo/views/dropdown.html',
        editDropdownIdLabel: 'text',
        cellEditableCondition: function($scope) {
            return $scope.row.entity.guid != null;
        }
    }, {
        field: 'resolution',
        displayName: 'PCR',
        cellTemplate: resolutionTmpl,
        cellEditableCondition: function($scope) {
            return $scope.row.entity.guid != null;
        }
    }, {
        field: 'developerNotes',
        displayName: 'Developer Notes',
        cellEditableCondition: function($scope) {
            return $scope.row.entity.guid != null;
        }
    }, {
        field: 'customerNotes',
        displayName: 'Customer Notes',
        cellEditableCondition: function($scope) {
            return $scope.row.entity.guid != null;
        },
    } , {
        field: 'name',
        displayName: 'Delete',
        enableCellEdit: false,
        width: '5%',
        cellTemplate: deleteCellTmpl,
    }
];
    
	return ColumnFactory;
})