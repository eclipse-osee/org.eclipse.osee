app.controller('userController', [
    '$scope',
    '$modal',
    '$rootScope',
    'Program',
    'Set',
    'Item',
    'Annotation',
    'SetSearch',
    'SourceFile',
    'Config',
    'ColumnFactory',
    function($scope, $modal, $rootScope, Program, Set, Item, Annotation, SetSearch, SourceFile, Config, ColumnFactory) {
    	$scope.unselectingItem = false;
    	$scope.editItems = false;
    	$scope.selectedItems = [];
        $scope.programSelection = null;
        $scope.setSelection = null; 	
        $scope.lastFocused = null;
        $scope.isMulitEditRequest = false;
        $scope.loading = false;
		$scope.isSearchView = false;
		
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
        };
        
        // if this is a search result view, populate program, set and items from parent scope
        if(window.opener != null &&  window.opener.$windowScope != undefined) {
        	$scope.programs = window.opener.$windowScope.programs;
        	$scope.sets = window.opener.$windowScope.sets;
        	$scope.programSelection = window.opener.$windowScope.programSelection;
        	$scope.setSelection = window.opener.$windowScope.setSelection;
    		$scope.items = window.opener.$windowScope.searchData;
    		
    		$scope.isSearchView = true;
    		$scope.searchValue = window.opener.$windowScope.searchValue;
    		
    		$scope.type = window.opener.$windowScope.type;
    	} else {
            // Get programs from server
            Program.query(function(data) {
                $scope.programs = data;
            });
            $scope.getDispoType();
    	}
        
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
            }, function(data) {
            	loadingModal.close();
            	alert(data.statusText);
            });
            
            // Try to get custom config
            Config.get({
                programId: $scope.programSelection,
                type: $rootScope.type
            }, function(data) {
                $scope.coverageResolutionTypes = data.validResolutions;
            });
        };
        
        $scope.updateSet = function updateSet() {
        	var loadingModal = $scope.showLoadingModal();
        	$scope.items = {};
        	
        	if($scope.isSearchView) {
        		$scope.doAdvSearch($scope.searchValue, loadingModal);
        	} else {
                Item.query({
                    programId: $scope.programSelection,
                    setId: $scope.setSelection,
                    isDetailed: $rootScope.type == 'codeCoverage'
                }, function(data) {
                	loadingModal.close();
                    $scope.items = data;
                }, function(data) {
                	loadingModal.close();
                	alert("Ooops...Something went wrong");
                });
        	}
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
        
        $scope.saveLastFocused = function saveLastFocused(element) {
            $scope.lastFocused = element;
        }
        
        $scope.getSourceFlie = function () {
        	var requst = [];
        	requst.push(
        	  "program/",
        	  $scope.programSelection,
        	  "/set/",
        	  $scope.setSelection,
        	  "/file/",
        	  $scope.selectedItem.name
        	  );
        	var url = requst.join("");
            
            window.open(url);
        }
        
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
            }
        });
        
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
        
        var checkboxSorting = function checkboxSorting(itemA, itemB) {
            if(itemA == itemB) {
            	return 0;
            } else if (itemA) {
                return -1;
            } else if (itemB) {
                return 1;
            } 
        };
        
        $scope.checkEditable = function checkEditable(item) {
        	return  item.assignee != $rootScope.cachedName;
        }

        $scope.columns = ColumnFactory.getColumns($scope.type, window.innerWidth);
        
        var filterBarPlugin = {
                init: function(scope, grid) {
                    filterBarPlugin.scope = scope;
                    filterBarPlugin.grid = grid;
                    $scope.$watch(function() {
                        var searchQuery = "";
                        angular.forEach(filterBarPlugin.scope.columns, function(col) {
                            if (col.visible && col.filterText) {
                                var filterText = (col.filterText.indexOf('*') == 0 ? col.filterText.replace('*', '') : "^" + col.filterText) + ";";
                                searchQuery += col.displayName + ": " + filterText;
                            }
                        });
                        return searchQuery;
                    }, function(searchQuery) {
                        filterBarPlugin.scope.$parent.filterText = searchQuery;
                        filterBarPlugin.grid.searchProvider.evalFilter();
                    });
                },
                scope: undefined,
                grid: undefined,
            };
        
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
            columnDefs: 'columns',
            plugins: [filterBarPlugin],
            headerRowHeight: 60 // give room for filter bar
        };
        
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
            isDefault: true
        }, {
            text: "Exception_Handling",
            value: "Exception_Handling",
            isDefault: true
        }, {
            text: "Other",
            value: "other",
            isDefault: false
        }, {
            text: "Undetermined",
            value: "UNDETERMINED",
            isDefault: false
        }];
        
        
        $scope.searchAnnotations = function() {
        	Item.get({
        		    programId: $scope.programSelection,
                    setId: $scope.setSelection,
                    itemId: $scope.selectedItem.guid,
                    keyword: "text"
        	})
        }
        
        $scope.getResolutionTypes = function getResolutionTypes() {
        	if($scope.type == 'codeCoverage') {
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
        
        $scope.editAssignees = function(item) {
        	$scope.editItem(item, 'assignee');
        }
        
        $scope.editNotes = function (item) {
        	$scope.editItem(item, 'itemNotes');
        }
        
        $scope.editCategories = function (item) {
        	$scope.editItem(item, 'category');
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
        
        $scope.doAdvSearch = function(value, loadingModal) {
        	SetSearch.query({
                programId: $scope.programSelection,
                setId: $scope.setSelection,
                value: value,
                isDetailed: $rootScope.type == 'codeCoverage',
            }, function(data) {
            	if($scope.isSearchView) {
            		$scope.items = data;
            		if(loadingModal != null) {
            			loadingModal.close();
            		}
            	} else  {
                	$scope.searchData = data;
                	window.$windowScope = $scope;
                	$scope.searchValue = value;
                	window.open("/dispo/main.html#/search");
            	}
            }, function(data) {
            	if($scope.isSearchView) {
            		$scope.items = $scope.emptyItems;
            	} else {
                	$scope.searchData = $scope.emptyItems;
                	window.$windowScope = $scope;
                	window.open("/dispo/main.html#/search");
            	}
            	if(loadingModal != null) {
        			loadingModal.close();
        		}
            });
        }
        
        $scope.emptyItems = [{"name": "NONE FOUND"}]
        
        
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
	            		$scope.editAssignees($scope.selectedItems[i]);
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
	            		$scope.editCategories($scope.selectedItems[i]);
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
            			$scope.editNeedsRerun($scope.selectedItems[i]);
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
        
        
        
        // Advanced Serach Modal
        $scope.showAdvSearchModal = function() {
            var modalInstance = $modal.open({
                templateUrl: 'advSearchModal.html',
                controller: AdvSearchModalCtrl,
                size: 'md',
                windowClass: 'advSearch',
                resolve: {
                	value: function() {
                		return $scope.searchValue;
                	}
                }
            });

            modalInstance.result.then(function(inputs) {
            	var loadingModal = null;
            	if($scope.isSearchView) {
            		$scope.searchValue = inputs.value;
            		loadingModal = $scope.showLoadingModal();
            	}
            	$scope.doAdvSearch(inputs.value, loadingModal)
            });
        }
        
        var AdvSearchModalCtrl = function($scope, $modalInstance, value) {
        	$scope.searchValue = value;
            $scope.ok = function() {
                var inputs = {};
            	inputs.value = this.searchValue;
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
