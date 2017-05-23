app.factory('ColumnFactory', function() {
	var ColumnFactory = {};
	
	ColumnFactory.getColumns = function(dispoType, width) {
		var toReturn; 
        if(width < 1000) {
        	if(dispoType = '') {
        		toReturn = smallColumnsTestScript;
        	} else {
        		toReturn = smallColumnsCoverage
        	}
        } else {
        	if(dispoType = '') {
        		toReturn = wideColumnsTestScript;
        	} else {
        		toReturn = wideColumnsCoverage
        	}
        }
		
		return toReturn;
	}
	
    var origCellTmpl = '<div ng-dblclick="getItemDetails(row.entity, row)">{{row.entity.name}}</div>';
    var editCellTmpl = '<input ng-model="row.getProperty(col.field)" ng-model-onblur ng-change="editItem(row.entity);" value="row.getProperty(col.field);></input>';
    var cellEditNotes = '<input class="cellInput" ng-model="COL_FIELD" ng-disabled="checkEditable(row.entity);" ng-model-onblur ng-change="editNotes(row.entity)"/>'
    var chkBoxTemplate = '<input type="checkbox" class="form-control" ng-model="COL_FIELD" ng-change="editNeedsRerun(row.entity)"></input>';
    var assigneeCellTmpl = '<div ng-dblclick="stealItem(row.entity)">{{row.entity.assignee}}</div>';
    var dateCellTmpl = '<div>getReadableDate({{row.getProperty(col.field)}})</div>';
    
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
	
	var smallColumnsTestScript = [{
        field: 'name',
        displayName: 'Name',
        cellTemplate: origCellTmpl,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    }, {
        field: 'status',
        displayName: 'Status',
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    }, {
        field: 'totalPoints',
        displayName: 'Total',
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    }, {
        field: 'failureCount',
        displayName: 'Failure Count',
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    }, {
        field: 'discrepanciesAsRanges',
        displayName: 'Failed Points',
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    }, {
        field: 'assignee',
        displayName: 'Assignee',
        enableCellEdit: false,
        cellTemplate: assigneeCellTmpl,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    }, 
    {
        field: 'team',
        displayName: 'Team',
        enableCellEdit: false,
        visible: true,
        headerCellTemplate: '/dispo/legacy/templates/nameFilterTmpl.html'
    },{
        field: 'itemNotes',
        displayName: 'Script Notes',
        cellTemplate: cellEditNotes,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    },{
        field: 'needsRerun',
        displayName: 'Rerun?',
        enableCellEdit: false,
        cellTemplate: chkBoxTemplate,
        sortFn: checkboxSorting
    },{
        field: 'lastUpdated',
        displayName: 'Last Ran',
        enableCellEdit: false,
        visible: false,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html',
        sortFn: dateSorting
    }, {
        field: 'category',
        displayName: 'Category',
        enableCellEdit: true,
        visible: false,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    }, {
        field: 'machine',
        displayName: 'Station',
        enableCellEdit: true,
        visible: false,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    }, {
        field: 'elapsedTime',
        displayName: 'Elapsed Time',
        enableCellEdit: false,
        visible: false,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    },{
        field: 'creationDate',
        displayName: 'Creation Date',
        enableCellEdit: false,
        visible: false,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html',
        sortFn: dateSorting
    },{
        field: 'aborted',
        displayName: 'Aborted',
        enableCellEdit: false,
        visible: false,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
        }, {
            field: 'version',
            displayName: 'Version',
            enableCellEdit: false,
            visible: false,
            headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    }];

	var wideColumnsTestScript = [{
        field: 'name',
        displayName: 'Name',
        width: '22%',
        cellTemplate: origCellTmpl,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    }, {
        field: 'status',
        displayName: 'Status',
        width: '10%',
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    }, {
        field: 'totalPoints',
        displayName: 'Total',
        width: '10%',
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    }, {
        field: 'failureCount',
        displayName: 'Failure Count',
        width: '7%',
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    }, {
        field: 'discrepanciesAsRanges',
        displayName: 'Failed Points',
        width: '15%',
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    }, {
        field: 'assignee',
        displayName: 'Assignee',
        enableCellEdit: false,
        cellTemplate: assigneeCellTmpl,
        width: '12%',
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    }, 
    {
        field: 'team',
        displayName: 'Team',
        enableCellEdit: false,
        visible: true,
        width: '7%',
        headerCellTemplate: '/dispo/legacy/templates/nameFilterTmpl.html'
    },{
        field: 'itemNotes',
        displayName: 'Script Notes',
        cellTemplate: cellEditNotes,
        width: '12%',
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    },{
        field: 'needsRerun',
        displayName: 'Rerun?',
        enableCellEdit: false,
        cellTemplate: chkBoxTemplate,
        sortFn: checkboxSorting,
        width: '10%',
    },{
        field: 'lastUpdated',
        displayName: 'Last Ran',
        enableCellEdit: false,
        visible: false,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html',
        sortFn: dateSorting
    }, {
        field: 'category',
        displayName: 'Category',
        enableCellEdit: true,
        visible: false,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    }, {
        field: 'machine',
        displayName: 'Station',
        enableCellEdit: true,
        visible: false,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    }, {
        field: 'elapsedTime',
        displayName: 'Elapsed Time',
        enableCellEdit: false,
        visible: false,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    },{
        field: 'creationDate',
        displayName: 'Creation Date',
        enableCellEdit: false,
        visible: false,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html',
        sortFn: dateSorting
    },{
        field: 'aborted',
        displayName: 'Aborted',
        enableCellEdit: false,
        visible: false,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    },  {
            field: 'version',
            displayName: 'Version',
            enableCellEdit: false,
            visible: false,
            headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    }];
	
	var smallColumnsCoverage = [{
        field: 'name',
        displayName: 'Name',
        cellTemplate: origCellTmpl,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    }, {
        field: 'status',
        displayName: 'Status',
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    }, {
        field: 'totalPoints',
        displayName: 'Total',
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    }, {
        field: 'failureCount',
        displayName: 'Failure Count',
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    }, {
        field: 'discrepanciesAsRanges',
        displayName: 'Failed Points',
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    }, {
        field: 'assignee',
        displayName: 'Assignee',
        enableCellEdit: false,
        cellTemplate: assigneeCellTmpl,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    },
    {
        field: 'team',
        displayName: 'Team',
        enableCellEdit: false,
        visible: true,
        headerCellTemplate: '/dispo/legacy/templates/nameFilterTmpl.html'
    },{
        field: 'itemNotes',
        displayName: 'Script Notes',
        cellTemplate: cellEditNotes,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    },{
        field: 'needsRerun',
        displayName: 'Rerun?',
        enableCellEdit: false,
        cellTemplate: chkBoxTemplate,
        sortFn: checkboxSorting
    },{
        field: 'lastUpdated',
        displayName: 'Last Ran',
        enableCellEdit: false,
        visible: false,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html',
        sortFn: dateSorting
    }, {
        field: 'category',
        displayName: 'Category',
        enableCellEdit: true,
        visible: false,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    }, {
        field: 'machine',
        displayName: 'Station',
        enableCellEdit: true,
        visible: false,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    }, {
        field: 'elapsedTime',
        displayName: 'Elapsed Time',
        enableCellEdit: false,
        visible: false,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    },{
        field: 'creationDate',
        displayName: 'Creation Date',
        enableCellEdit: false,
        visible: false,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html',
        sortFn: dateSorting
    },{
        field: 'aborted',
        displayName: 'Aborted',
        enableCellEdit: false,
        visible: false,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    }, {
        field: 'version',
        displayName: 'Version',
        enableCellEdit: false,
        visible: false,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    },{
        field: 'fileNumber',
        displayName: 'File Number',
        enableCellEdit: false,
        visible: false,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    },{
        field: 'methodNumber',
        displayName: 'Method Number',
        enableCellEdit: false,
        visible: false,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    }];

	var wideColumnsCoverage = [{
        field: 'name',
        displayName: 'Name',
        width: '22%',
        cellTemplate: origCellTmpl,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    }, {
        field: 'status',
        displayName: 'Status',
        width: '10%',
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    }, {
        field: 'totalPoints',
        displayName: 'Total',
        width: '10%',
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    }, {
        field: 'failureCount',
        displayName: 'Failure Count',
        width: '7%',
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    }, {
        field: 'discrepanciesAsRanges',
        displayName: 'Failed Points',
        width: '15%',
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    }, {
        field: 'assignee',
        displayName: 'Assignee',
        enableCellEdit: false,
        cellTemplate: assigneeCellTmpl,
        width: '12%',
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    }, 
    {
        field: 'team',
        displayName: 'Team',
        enableCellEdit: false,
        visible: true,
        width: '7%',
        headerCellTemplate: '/dispo/legacy/templates/nameFilterTmpl.html'
    }, {
        field: 'itemNotes',
        displayName: 'Script Notes',
        cellTemplate: cellEditNotes,
        width: '10%',
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    },{
        field: 'needsRerun',
        displayName: 'Rerun?',
        enableCellEdit: false,
        cellTemplate: chkBoxTemplate,
        sortFn: checkboxSorting,
        width: '5%',
    },{
        field: 'lastUpdated',
        displayName: 'Last Ran',
        enableCellEdit: false,
        visible: false,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html',
        sortFn: dateSorting
    }, {
        field: 'category',
        displayName: 'Category',
        enableCellEdit: true,
        visible: false,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    }, {
        field: 'machine',
        displayName: 'Station',
        enableCellEdit: true,
        visible: false,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    }, {
        field: 'elapsedTime',
        displayName: 'Elapsed Time',
        enableCellEdit: false,
        visible: false,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    },{
        field: 'creationDate',
        displayName: 'Creation Date',
        enableCellEdit: false,
        visible: false,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html',
        sortFn: dateSorting
    },{
        field: 'aborted',
        displayName: 'Aborted',
        enableCellEdit: false,
        visible: false,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    },{
        field: 'version',
        displayName: 'Version',
        enableCellEdit: false,
        visible: false,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    },{
        field: 'fileNumber',
        displayName: 'File Number',
        enableCellEdit: false,
        visible: false,
        width: 75,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    },{
        field: 'methodNumber',
        displayName: 'Method Number',
        enableCellEdit: false,
        visible: false,
        width: 75,
        headerCellTemplate: '/dispo/views/nameFilterTmpl.html'
    }];
	
	return ColumnFactory;
})