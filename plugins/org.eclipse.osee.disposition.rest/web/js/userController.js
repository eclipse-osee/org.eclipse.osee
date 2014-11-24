app.controller('userController', [
    '$scope',
    '$modal',
    '$rootScope',
    '$cookieStore',
    'Program',
    'Set',
    'Item',
    'Annotation',

    function($scope, $modal, $rootScope, $cookieStore, Program, Set, Item, Annotation) {
    	$scope.unselectingItem = false;
    	$scope.editItems = false;
    	$scope.selectedItems = [];
        $scope.programSelection = null;
        $scope.setSelection = null; 	
        $scope.lastFocused = null;
        $scope.isMulitEditRequest = false;
        $scope.loading = false;
        $scope.spinneractive = false;
        
        
        $scope.getDispoType = function() {
        	if($rootScope.type == 'codeCoverage') {
        		$scope.annotationHeaders = {
        				'locationRefs': 'Code Line',
        				'resolutionType': 'Disposition Type',
        				'resolution': 'Resolution'}
        	} else if($rootScope.type == 'testScript') {
        		$scope.annotationHeaders = {
        				'locationRefs': 'Test Point(s)',
        				'resolutionType': 'PCR Type',
        				'resolution': 'PCR'}
        	}
        }
        
        $scope.getDispoType();
        
        // Get programs
        Program.query(function(data) {
            $scope.programs = data;
        });
        
        $scope.isDefaultResolution = function isDefaultResolution(annotation) {
        	var resolutionType = annotation.resolutionType;
        	return resolutionType == 'Test_Script' || resolutionType == 'Exception_Handling';
        }

        $scope.updateProgram = function updateProgram() {
        	var loadingModal = $scope.showLoadingModal();
        	$scope.items = {};
        	$scope.sets = {};
            Set.query({
                programId: $scope.programSelection,
                type: $rootScope.type
            }, function(data) {
            	loadingModal.close();
                $scope.sets = data;
            });
        };
        
        $scope.updateSet = function updateSet() {
        	var loadingModal = $scope.showLoadingModal();
        	$scope.items = {};
            Item.query({
                programId: $scope.programSelection,
                setId: $scope.setSelection
            }, function(data) {
            	loadingModal.close();
                $scope.items = data;
            }, function(data) {
            	loadingModal.close();
            	alert("Ooops...Something went wrong");
            });

            Set.get({
                programId: $scope.programSelection,
                setId: $scope.setSelection
            }, function(data) {
                $scope.set = data;
                $scope.dispoConfig = $scope.set.dispoConfig;
            });
        };

        $scope.getItemDetails = function getItemDetails(item, row) {
        	$scope.unselectingItem = true;
        	$scope.gridOptions.selectAll(false);
        	$scope.unselectingItem = false;
            $scope.selectedItem = item;
            Annotation.query({
                programId: $scope.programSelection,
                setId: $scope.setSelection,
                itemId: item.guid
            }, function(data) {
                $scope.annotations = data;
                var blankAnnotation = new Annotation();
                $scope.annotations.push(blankAnnotation);

                $scope.gridOptions.selectRow(row.rowIndex, true);
            });


        };

        $scope.$on('ngGridEventStartCellEdit', function(data) {
            var field = data.targetScope.col.field;
            $scope.cachedValue = data.targetScope.row.getProperty(field);
        });

        $scope.$on('ngGridEventEndCellEdit', function(data) {
            var field = data.targetScope.col.field;
            var newValue = data.targetScope.row.getProperty(field);

            if ($scope.cachedValue != newValue) {
                var object = data.targetScope.row.entity;
                $scope.editItem(data.targetScope.row.entity);
            }
        });
        
        $scope.toggleEditItems = function toggleEditItems() {
        	var size = $scope.selectedItems.length;
        	$scope.gridOptions.selectAll(false);
        	
        	// Why do this last? Good question, checkSeletable gets called by selectAll and needs editItems to be true so ng-grid can properly unselect the selected items withouth breaking it's 'watch' function
        	$scope.editItems = !$scope.editItems;
        	$scope.annotations.length = 0;
        }
        
        // Need this so that user clicks on grid rows doesn't automatically change the selected/highlighted row
        var checkSelectable = function checkSelectable(data) {
        	if($scope.editItems || $scope.unselectingItem){
        		return true;
        	} else {
        		return false;
        	}
        };
        
        $scope.$on('ngGridEventStartCellEdit', function (event) {
        	$scope.previousCellData = event.targetScope.row.entity[event.targetScope.col.field];
        });
        
        $scope.$on('ngGridEventEndCellEdit', function (event) {
            cellData = event.targetScope.row.entity[event.targetScope.col.field];
            if(cellData != $scope.previousCellData) {
//            	$scope.editItem(event.targetScope.row.entity);
            }
        });
        
        var checkboxSorting = function checkboxSorting(itemA, itemB) {
            if (itemA.needsRerun == itemA.needsRerun) {
                return itemA;
            } else if (itemA.needsRerun) {
                return itemA;
            } else if (itemB.needsRerun) {
                return itemB;
            } else {
                return itemA;
            }
        };
        
        $scope.checkEditable = function checkEditable(item) {
        	return  item.assignee != $rootScope.cachedName;
        }

        var origCellTmpl = '<div ng-dblclick="getItemDetails(row.entity, row)">{{row.entity.name}}</div>';
        var editCellTmpl = '<input ng-model="row.getProperty(col.field)" ng-model-onblur ng-change="editItem(row.entity);" value="row.getProperty(col.field);></input>';
        var cellEditNotes = '<input class="cellInput" ng-model="COL_FIELD" ng-disabled="checkEditable(row.entity);" ng-model-onblur ng-change="editNotes(row.entity)"/>'
        var chkBoxTemplate = '<input type="checkbox" class="form-control" ng-model="COL_FIELD" ng-change="editNeedsRerun(row.entity)"></input>';
        var assigneeCellTmpl = '<div ng-dblclick="stealItem(row.entity)">{{row.entity.assignee}}</div>';
        var dateCellTmpl = '<div>getReadableDate({{row.getProperty(col.field)}})</div>';
        
        
        $scope.smallColumns = [{
            field: 'name',
            displayName: 'Name',
            cellTemplate: origCellTmpl
        }, {
            field: 'status',
            displayName: 'Status',
        }, {
            field: 'totalPoints',
            displayName: 'Total',
        }, {
            field: 'failureCount',
            displayName: 'Failure Count',
        }, {
            field: 'discrepanciesAsRanges',
            displayName: 'Failed Points',
        }, {
            field: 'assignee',
            displayName: 'Assignee',
            enableCellEdit: false,
            cellTemplate: assigneeCellTmpl
        }, {
            field: 'itemNotes',
            displayName: 'Script Notes',
            cellTemplate: cellEditNotes
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
            visible: false
        }, {
            field: 'category',
            displayName: 'Category',
            enableCellEdit: true,
            visible: false
        }, {
            field: 'machine',
            displayName: 'Station',
            enableCellEdit: true,
            visible: false
        }, {
            field: 'elapsedTime',
            displayName: 'Elapsed Time',
            enableCellEdit: false,
            visible: false
        },{
            field: 'creationDate',
            displayName: 'Creation Date',
            enableCellEdit: false,
            visible: false
        },{
            field: 'aborted',
            displayName: 'Aborted',
            enableCellEdit: false,
            visible: false
            }, {
                field: 'version',
                displayName: 'Version',
                enableCellEdit: false,
                visible: false
        }];
        
        $scope.wideColumns = [{
            field: 'name',
            displayName: 'Name',
            width: 350,
            cellTemplate: origCellTmpl
        }, {
            field: 'status',
            displayName: 'Status',
            width: 100
        }, {
            field: 'totalPoints',
            displayName: 'Total',
            width: 100
        }, {
            field: 'failureCount',
            displayName: 'Failure Count',
        }, {
            field: 'discrepanciesAsRanges',
            displayName: 'Failed Points',
        }, {
            field: 'assignee',
            displayName: 'Assignee',
            enableCellEdit: false,
            cellTemplate: assigneeCellTmpl
        }, {
            field: 'itemNotes',
            displayName: 'Script Notes',
            cellTemplate: cellEditNotes
        },{
            field: 'needsRerun',
            displayName: 'Rerun?',
            enableCellEdit: false,
            cellTemplate: chkBoxTemplate,
            sortFn: checkboxSorting,
            width: 70
        },{
            field: 'lastUpdated',
            displayName: 'Last Ran',
            enableCellEdit: false,
            visible: false
        }, {
            field: 'category',
            displayName: 'Category',
            enableCellEdit: true,
            visible: false
        }, {
            field: 'machine',
            displayName: 'Station',
            enableCellEdit: true,
            visible: false
        }, {
            field: 'elapsedTime',
            displayName: 'Elapsed Time',
            enableCellEdit: false,
            visible: false
        },{
            field: 'creationDate',
            displayName: 'Creation Date',
            enableCellEdit: false,
            visible: false
        },{
            field: 'aborted',
            displayName: 'Aborted',
            enableCellEdit: false,
            visible: false
        },  {
                field: 'version',
                displayName: 'Version',
                enableCellEdit: false,
                visible: false
        }];
        
        if(window.innerWidth < 1000) {
        	$scope.columns = $scope.smallColumns;
        } else {
        	$scope.columns = $scope.wideColumns;
        }
        
        $scope.gridOptions = {
            data: 'items',
            enableHighlighting: true,
            enableColumnResize: true,
            enableRowReordering: true,
            multiSelect: true,
            showColumnMenu: true,
            selectedItems: $scope.selectedItems,
            beforeSelectionChange: checkSelectable,
            showGroupPanel: true,
            showFilter: true,
            noTabInterference: true,
            tabIndex: 0,
            columnDefs: 'columns'
        };
        
        
        $scope.saveLastFocused = function saveLastFocused(element) {
            $scope.lastFocused = element;
        }
        
        $scope.stealItem = function(item) {
            Item.get({
                programId: $scope.programSelection,
                setId: $scope.setSelection,
                itemId: item.guid
            }, function(data) {
            	$scope.updateItemFromServer(item, data);
            	$scope.askToSteal(item);
            });
        }
        
        $scope.askToSteal = function askToSteal(item) {
            if ($rootScope.cachedName != null) {
                if ($rootScope.cachedName != item.assignee) {
                	var confirmed = false;
                	if(item.assignee.toUpperCase() == 'UNASSIGNED'){
                		confirmed = true;
                	} else {
                		confirmed = window.confirm("Are you sure you want to steal this Item from " + item.assignee);
                	}
                    if (confirmed) {
                        item.assignee = $rootScope.cachedName;
                        $scope.editItem(item);
                    }
                }
            }
        }
        
        $scope.isDisabledOption = function isDisabledOption(option) {
        	var isSelectable = option.selectable;
        	return !isSelectable;
        }
        
        $scope.testResolutionTypes = [{
            text: "Code",
            value: "CODE"
        }, {
            text: "Test",
            value: "TEST"
        }, {
            text: "Requirement",
            value: "REQUIREMENT"
        }, {
            text: "Other",
            value: "OTHER"
        }, {
            text: "Undetermined",
            value: "UNDETERMINED"
        }];

        $scope.coverageResolutionTypes = [{
            text: "Test Script",
            value: "Test_Script",
            isinuse: true
        }, {
            text: "Exception_Handling",
            value: "Exception_Handling",
            isinuse: true
        }, {
            text: "Other",
            value: "other",
            isinuse: false
        }, {
            text: "Undetermined",
            value: "UNDETERMINED",
            isinuse: false
        }];
        
        $scope.getResolutionTypes = function getResolutionTypes() {
        	if($scope.set.dispoType == 'codeCoverage') {
        		return $scope.coverageResolutionTypes;
        	} else {
        		return $scope.testResolutionTypes;
        	}
        }

        $scope.deleteAnnotation = function deleteAnnotation(annotation) {
            Annotation.delete({
                programId: $scope.programSelection,
                setId: $scope.setSelection,
                itemId: $scope.selectedItem.guid,
                annotationId: annotation.guid,
                userName: $rootScope.cachedName,
            }, function() {
                var index = $scope.annotations.indexOf(annotation);
                if (index > -1) {
                    $scope.annotations.splice(index, 1);
                }
                Item.get({
                    programId: $scope.programSelection,
                    setId: $scope.setSelection,
                    itemId: $scope.selectedItem.guid
                }, function(data) {
                    $scope.updateItemFromServer($scope.selectedItem, data);
                });
            }, function(data) {
                alert("Could not make change, please try refreshing");
            });

        }
        
        $scope.editItem = function editItem(item) {
        	$scope.editItem(item, null);
        }
        
        $scope.editNotes = function (item) {
        	$scope.editItem(item, 'itemNotes');
        }
        
        $scope.editNeedsRerun = function (item) {
        	$scope.editItem(item, 'needsRerun');
        }

        $scope.editItem = function editItem(item, field) {
        	var newItem = new Item;
        	if(field == null) {
        		newItem = item;
        	} else if(field == 'itemNotes') {
        		newItem.itemNotes = item.itemNotes;
        	} else if(field == 'needsRerun') {
        		newItem.needsRerun = item.needsRerun;
        	}
        	
            Item.update({
                programId: $scope.programSelection,
                setId: $scope.setSelection,
                itemId: item.guid,
            }, newItem, function() {
            	if($scope.isMulitEditRequest) {
                	$scope.gridOptions.selectAll(false);
            		$scope.isMulitEditRequest=false;
            	}
            }, function(data) {
                alert("Could not make change, please try refreshing");
            });

        }

        $scope.getInvalidLocRefs = function getInvalidLocRefs(annotation) {
        	if(annotation.isConnected != null) {
        		return !annotation.isConnected && annotation.locationRefs != null;
        	} else {
        		return false;
        	}
        }

        $scope.getInvalidRes = function getInvalidRes(annotation) {
            return annotation.resolution != null && annotation.resolution != "" && !annotation.isResolutionValid;
        }
        
        $scope.editAnnotation = function editAnnotation(annotation) {
        	$scope.lastFocused;
            if (annotation.guid == null) {
            	if(/[^\s]+/.test(annotation.locationRefs)) {
            		$scope.createAnnotation(annotation);
            	}
            } else {
                Annotation.update({
                    programId: $scope.programSelection,
                    setId: $scope.setSelection,
                    itemId: $scope.selectedItem.guid,
                    annotationId: annotation.guid,
                    userName: $rootScope.cachedName,
                }, annotation, function(annot) {
                    // get latest Annotation version from Server
                    Annotation.get({
                        programId: $scope.programSelection,
                        setId: $scope.setSelection,
                        itemId: $scope.selectedItem.guid,
                        annotationId: annotation.guid
                    }, function(data) {
                        annotation.isConnected = data.isConnected;
                        annotation.isResolutionValid = data.isResolutionValid;
                    });

                    // Get new latest Item version from server
                    Item.get({
                        programId: $scope.programSelection,
                        setId: $scope.setSelection,
                        itemId: $scope.selectedItem.guid
                    }, function(data) {
                        $scope.updateItemFromServer($scope.selectedItem, data);
                    });
                }, function(data) {
                    alert("Could not make change, please try refreshing");
                });
            }
        }

        $scope.createAnnotation = function createAnnotation(annotation) {
            annotation.$save({
                programId: $scope.programSelection,
                setId: $scope.setSelection,
                itemId: $scope.selectedItem.guid,
                userName: $rootScope.cachedName,
            }, function() {
            	var nextFocused = $scope.lastFocused.$$prevSibling;
            	nextFocused.focus;
            	nextFocused.focusMe;
            	
                Item.get({
                    programId: $scope.programSelection,
                    setId: $scope.setSelection,
                    itemId: $scope.selectedItem.guid
                }, function(data) {
                    $scope.updateItemFromServer($scope.selectedItem, data);
                });

                var blankAnnotation = new Annotation();
                $scope.annotations.push(blankAnnotation);
            }, function(data) {
                alert("Could not make change, please try refreshing");
            });
        }

        $scope.expandAnnotations = function expandAnnotations(item) {
            $scope.selectedItem = item;
            if (item.annotations == null) {
                Annotation.query({
                    programId: $scope.programSelection,
                    setId: $scope.setSelection,
                    itemId: item.guid
                }, function(data) {
                    item.annotations = data;

                    var blankAnnotation = new Annotation;
                    item.annotations.push(blankAnnotation);
                    item.showDetails = (data.length > 0);
                });
            } else if (!item.showDetails) {
                item.showDetails = (item.annotations.length > 0);
            } else {
                item.showDetails = false;
            }
        };

        $scope.toggleDetails = function toggleDetails(annotation) {
            if (annotation.showDeets == null) {
                annotation.showDeets = true;
            } else if (annotation.showDeets) {
                annotation.showDeets = false;
            } else {
                annotation.showDeets = true;
            }
        }
        
        $scope.updateItemFromServer = function(oldItem, newItem) {
        	oldItem.assignee = newItem.assignee;
        	oldItem.scriptNotes = newItem.scriptNotes;
        	oldItem.status = newItem.status;
        }
        
        
        // MODALS -------------------------------------------------------------------------------------------------
        $scope.showAssigneeModal = function() {
        	$scope.isMulitEditRequest = true;
            var modalInstance = $modal.open({
                templateUrl: 'assigneeModal.html',
                controller: AssigneeModalCtrl,
                size: 'sm',
                windowClass: 'assigneeModal'
            });

            modalInstance.result.then(function(inputs) {
            	var size = $scope.selectedItems.length;
            	for(var i = 0; i < size; i++) {
            		if($scope.selectedItems[i].assignee != inputs.multiAssignee){
	            		$scope.selectedItems[i].assignee = inputs.multiAssignee;
	            		$scope.editItem($scope.selectedItems[i]);
            		}
            	}
            });
        }
        
        var AssigneeModalCtrl = function($scope, $modalInstance) {
            $scope.multiAssignee = "";

            $scope.ok = function() {
                var inputs = {};
                inputs.multiAssignee = this.multiAssignee;
                $modalInstance.close(inputs);
            };

            $scope.cancel = function() {
                $modalInstance.dismiss('cancel');
            };
        };
        
        // Category Modal
        $scope.showCategoryModal = function() {
        	$scope.isMulitEditRequest = true;
            var modalInstance = $modal.open({
                templateUrl: 'categoryModal.html',
                controller: CategoryModalCtrl,
                size: 'sm',
                windowClass: 'categoryModal'
            });

            modalInstance.result.then(function(inputs) {
            	var size = $scope.selectedItems.length;
            	for(var i = 0; i < size; i++) {
            		if($scope.selectedItems[i].category != inputs.category){
	            		$scope.selectedItems[i].category = inputs.category;
	            		$scope.editItem($scope.selectedItems[i]);
            		}
            	}
            });
        }
        
        var CategoryModalCtrl = function($scope, $modalInstance) {
            $scope.multiCategory = "";

            $scope.ok = function() {
                var inputs = {};
                inputs.category = this.multiCategory;
                $modalInstance.close(inputs);
            };

            $scope.cancel = function() {
                $modalInstance.dismiss('cancel');
            };
        };
        
        // Needs Rerun Modal
        $scope.showNeedsRerunModal = function() {
        	$scope.isMulitEditRequest = true;
            var modalInstance = $modal.open({
                templateUrl: 'needsRerunModal.html',
                controller: NeedsRerunModalCtrl,
                size: 'sm',
                windowClass: 'needsRerunModal'
            });

            modalInstance.result.then(function(inputs) {
            	var size = $scope.selectedItems.length;
            	for(var i = 0; i < size; i++) {
            		if($scope.selectedItems[i].needsRerun != inputs.needsRerun) {
            			$scope.selectedItems[i].needsRerun = inputs.needsRerun;
            			$scope.editItem($scope.selectedItems[i]);
            		}
            	}
            });
        }
        
        var NeedsRerunModalCtrl = function($scope, $modalInstance) {
            $scope.ok = function() {
                var inputs = {};
                if(this.formData == undefined) {
                	inputs.needsRerun = true;
                } else {
                	inputs.needsRerun = this.formData.multiNeedsRerun;
                }
                $modalInstance.close(inputs);
            };

            $scope.cancel = function() {
                $modalInstance.dismiss('cancel');
            };
        };
        
        // Loading Modal
        $scope.showLoadingModal = function() {
            var modalInstance = $modal.open({
                templateUrl: 'loadingModal.html',
                size: 'sm',
                windowClass: 'needsRerunModal',
                backdrop: 'static'
            });
            
            return modalInstance;
        }


    }
]);

//http://stackoverflow.com/questions/11868393/angularjs-inputtext-ngchange-fires-while-the-value-is-changing
app.directive('ngModelOnblur', function() {
    return {
        restrict: 'A',
        require: 'ngModel',
        priority: 1, // needed for angular 1.2.x
        link: function(scope, elm, attr, ngModelCtrl) {
            if (attr.type === 'radio' || attr.type === 'checkbox') return;

            elm.unbind('input').unbind('keydown').unbind('change');
            elm.bind('blur', function() {
                scope.$apply(function() {
                    ngModelCtrl.$setViewValue(elm.val());
                });
            });
        }
    };
});

// http://stackoverflow.com/questions/18398472/disabled-text-box-accessible-using-tab-key
app.directive('focusMe', function($timeout) {
	  return {
	    scope: { trigger: '=focusMe' },
	    link: function(scope, element) {
	      scope.$watch('trigger', function(value) {
	        if(value === true) { 
	            element[0].focus();
	        }
	      });
	    }
	  };
	});

//http://stackoverflow.com/questions/16202254/ng-options-with-disabled-rows
app.directive('optionsDisabled', function($parse) {
    var disableOptions = function(scope, attr, element, data, fnDisableIfTrue) {
        // refresh the disabled options in the select element.
        $("option[value!='?']", element).each(function(i, e) {
            var locals = {};
            locals[attr] = data[i];
            $(this).attr("disabled", fnDisableIfTrue(scope, locals));
        });
    };
    return {
        priority: 0,
        require: 'ngModel',
        link: function(scope, iElement, iAttrs, ctrl) {
            // parse expression and build array of disabled options
            var expElements = iAttrs.optionsDisabled.match(/^\s*(.+)\s+for\s+(.+)\s+in\s+(.+)?\s*/);
            var attrToWatch = expElements[3];
            var fnDisableIfTrue = $parse(expElements[1]);
            scope.$watch(attrToWatch, function(newValue, oldValue) {
                if(newValue)
                    disableOptions(scope, expElements[2], iElement, newValue, fnDisableIfTrue);
            }, true);
            // handle model updates properly
            scope.$watch(iAttrs.ngModel, function(newValue, oldValue) {
                var disOptions = $parse(attrToWatch)(scope);
                if(newValue)
                    disableOptions(scope, expElements[2], iElement, disOptions, fnDisableIfTrue);
            });
        }
    };
});
