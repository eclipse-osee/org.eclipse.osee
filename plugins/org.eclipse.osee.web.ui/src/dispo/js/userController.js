app.controller('userController', [
    '$scope',
    '$modal',
    '$rootScope',
    'Program',
    'Set',
    'MassDisposition',
    'Item',
    'Annotation',
    'SetSearch',
    'SourceFile',
    'Config',
    'uiGridConstants',
    'uiGridTreeViewConstants',
    'ColumnFactory',
    'CoverageFactory',
    function($scope, $modal, $rootScope, Program, Set, MassDisposition, Item, Annotation, SetSearch, SourceFile, Config, uiGridConstants, uiGridTreeViewConstants, ColumnFactory, CoverageFactory) {
        $scope.editItems = false;
        $scope.selectedItems = [];
        $scope.programSelection = null;
        $scope.setSelection = null;
        $scope.isMulitEditRequest = false;
        $scope.loading = false;
        $scope.isSearchView = false;
        $scope.isMultiEditView = false;
        $scope.isFirstSplit = false;
        $scope.isCoverage = $rootScope.type == 'codeCoverage';
        
        function split() {
            Split(['#itemsGridDiv', '#subGridDiv'], {
                direction: 'vertical',
                sizes: [75, 25],
                minSize: [200, 200],
                gutterSize: 10,
                cursor: 'row-resize'
            });
        }
        // if this is a search result view, populate program, set and items from parent scope
        if (window.opener != undefined && window.opener != null && window.parentScope != undefined) {
            if(!$scope.isFirstSplit) {
            	setTimeout(split, 100);
            	$scope.isFirstSplit = true;
            } 
            $scope.programs = window.parentScope.programs;
            $scope.sets = window.parentScope.sets;
            $scope.programSelection = window.parentScope.programSelection;
            $scope.setSelection = window.parentScope.setSelection;
            $scope.items = window.parentScope.searchData;

            $scope.isSearchView = true;
            $scope.searchValue = window.parentScope.searchValue;

            $scope.type = window.parentScope.type;
        } else {
            // Get programs from server
            Program.query(function(data) {
                $scope.programs = data;
            });
        }

        $scope.updateProgram = function updateProgram() {
            var loadingModal = $scope.showLoadingModal();
            $scope.items = [];
            $scope.sets = [];
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

            if(!$scope.isCoverage) {
                $scope.validResolutions = [
                {
                    text: "",
                    value: ""
                },{
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
                ColumnFactory.setResolutionTypeArray($rootScope.type, $scope.validResolutions);
            } else {
                Config.get({
                    programId: $scope.programSelection,
                    type: $rootScope.type
                }, function(data) {
                    $scope.resolutionTypes = data.validResolutions;
                    ColumnFactory.setResolutionTypeArray($rootScope.type, $scope.resolutionTypes);
                });
            }

        };

        $scope.updateSet = function updateSet() {
            var loadingModal = $scope.showLoadingModal();

            if ($scope.isSearchView) {
                $scope.doAdvSearch($scope.searchValue, loadingModal);
            } else {
                Item.query({
                    programId: $scope.programSelection,
                    setId: $scope.setSelection,
                    isDetailed: $scope.isCoverage
                }, function(data) {
                    loadingModal.close();
                    $scope.items = data;
                    if(!$scope.isFirstSplit) {
                    	setTimeout(split, 100);
                    	$scope.isFirstSplit = true;
                    } 
                }, function(data) {
                    loadingModal.close();
                    alert("Ooops...Something went wrong");
                });
            }
        };

        var sortStuff = function(a, b) {
            if (a.locationRefs == undefined) {
                return 1;
            }
            if (b.locationRefs == undefined) {
                return -1;
            }
            if (!isNaN(parseFloat(a)) && isFinite(a)) {
                return b.locationRefs - a.locationRefs
            } else {
                var aSplit = a.locationRefs.split(".");
                var bSplit = b.locationRefs.split(".");

                var delta = bSplit[0] - aSplit[0];
                if (delta == 0) {
                    if (isValidSize(aSplit, bSplit) && bSplit[1].match("RESULT") && !aSplit[1].match("RESULT")) {
                        return -1;
                    } else if (isValidSize(aSplit, bSplit) && !bSplit[1].match("RESULT") && aSplit[1].match("RESULT")) {
                        return 1;
                    } else {
                        return a.locationRefs.localeCompare(b.locationRefs);
                    }
                } else {
                    return delta;
                }
            }
        }
        
        var isValidSize = function (a, b) {
        	return (a.size > 1 && b.size > 1);
        }

        $scope.getSourceFlie = function() {
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
            $scope.isMultiEditView = !$scope.isMultiEditView;
            console.log($scope.gridOptions.enableRowSelection);

            $scope.gridApi.selection.clearSelectedRows();
            $scope.gridOptions.enableRowSelection = $scope.isMultiEditView;
            $scope.gridApi.selection.setMultiSelect($scope.isMultiEditView);

            $scope.gridOptions.enableFullRowSelection = $scope.isMultiEditView;
            $scope.gridOptions.enableRowHeaderSelection = $scope.isMultiEditView;

            $scope.gridApi.core.notifyDataChange(uiGridConstants.dataChange.OPTIONS);

            $scope.annotations = [];
            $scope.subGridOptions.data = [];
        }

        $scope.stealItem = function(item, row) {
            Item.get({
                programId: $scope.programSelection,
                setId: $scope.setSelection,
                itemId: item.guid
            }, function(data) {
                $scope.updateItemFromServer(item, data);
                $scope.askToSteal(item);
            });
        };


        $scope.askToSteal = function askToSteal(item) {
            if ($rootScope.cachedName != null) {
                if ($rootScope.cachedName != item.assignee) {
                    var confirmed = false;
                    if (item.assignee.toUpperCase() == 'UNASSIGNED') {
                        confirmed = true;
                    } else {
                        confirmed = window.confirm("Are you sure you want to steal this Item from " + item.assignee);
                    }
                    if (confirmed) {
                        item.assignee = $rootScope.cachedName;
                        $scope.editAssignees(item);
                    }
                }
            }
        }

        var dateSorting = function(itemA, itemB) {
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
            if (itemA == itemB) {
                return 0;
            } else if (itemA) {
                return -1;
            } else if (itemB) {
                return 1;
            }
        };

        $scope.checkEditable = function checkEditable(item) {
            return item.assignee != $rootScope.cachedName;
        }

        $scope.uiGridConstants = uiGridConstants;

        $scope.gridOptions = {
            data: 'items',
            enableCellEdit: false,
            enableHighlighting: true,
            enableColumnResize: true,
            multiSelect: false,
            showColumnMenu: true,
            selectedItems: $scope.selectedItems,
            showGroupPanel: true,
            showFilter: true,
            noTabInterference: true,
            enableGridMenu: true,
            tabIndex: 0,
            enableFiltering: true,
            enableFullRowSelection: true,
            showGridFooter: true,
            exporterMenuPdf: false,
        };
        $scope.gridOptions.enableRowSelection = false;
        $scope.gridOptions.enableRowHeaderSelection = false;

        $scope.gridOptions.onRegisterApi = function(gridApi) {

            $scope.gridApi = gridApi;

            gridApi.edit.on.afterCellEdit($scope, function(rowEntity, colDef, newValue, oldValue) {
                if (oldValue != newValue) {
                    $scope.editItem(rowEntity);
                }
            });

            gridApi.core.on.filterChanged($scope, function(ffff) {
                var grid = this.grid;
            })

            gridApi.selection.on.rowSelectionChanged($scope, function(row, event) {
                $scope.selectedItems = gridApi.selection.getSelectedRows();
            });

            gridApi.selection.on.rowSelectionChangedBatch($scope, function(row, event) {
                $scope.selectedItems = gridApi.selection.getSelectedRows();
            });
        };

        $scope.gridOptions.columnDefs = ColumnFactory.getColumns($scope.type, window.innerWidth);

        $scope.getTextResolutionType = function(annotation) {
            if (annotation.isLeaf) {
                return annotation.resolutionType;
            } else {
                return "-----------------";
            }
        }
        
        $scope.getTextResolution = function(annotation) {
        	if($scope.isCoverage) {
        		return CoverageFactory.getTextResolution(annotation);
        	} else {
        		return annotation.resolution;
        	}
        }
        
        $scope.getLastTextResolution = function(annotation) {
        	if($scope.isCoverage) {
        		return CoverageFactory.getLastTextResolution(annotation);
        	} else {
        		return annotation.lastResolution;
        	}
        }
        
        $scope.getTextCoverage = function(annotation) {
        	if(annotation.isLeaf) {
        		return annotation.customerNotes;
        	} else {
        		return "";
        	}
        }
        
        $scope.subGridOptions = {
            data: 'annotations',
            enableHighlighting: true,
            enableCellEdit: true,
            enableCellEditOnFocus: true,
            enableColumnResize: true,
            multiSelect: false,
            showColumnMenu: true,
            showFilter: true,
            noTabInterference: true,
            enableGridMenu: true,
            tabIndex: 0,
            enableFiltering: true,
            showTreeExpandNoChildren: false,
            enableFullRowSelection: true,
        };

        $scope.subGridOptions.enableRowSelection = false;
        $scope.subGridOptions.enableRowHeaderSelection = false;
        $scope.subGridOptions.onRegisterApi = function(gridApi) {

            $scope.subGridApi = gridApi;

            gridApi.edit.on.afterCellEdit($scope, function(rowEntity, colDef, newValue, oldValue) {
                if (oldValue != newValue) {
                    $scope.editAnnotation(colDef, oldValue, rowEntity);
                }
            });

        };

        $scope.subGridOptions.data = [];
        $scope.subGridOptions.columnDefs = ColumnFactory.getSubGridColumns($rootScope.type);
        
        $scope.getItemDetails = function(item, row) {
            if (!$scope.isMultiEditView) {
                $scope.selectedItem = item;
                Annotation.query({
                    programId: $scope.programSelection,
                    setId: $scope.setSelection,
                    itemId: item.guid
                }, function(data) {
                    $scope.subGridOptions.data = [];
                    $scope.annotations = data;
                    if ($scope.isCoverage) {
                        $scope.annotations.sort(sortStuff);
                        CoverageFactory.setTextForNonDefaultAnnotations($scope.annotations, $scope.selectedItem.discrepanciesList);
                        $scope.annotations = CoverageFactory.treeAnnotations(data);
                        CoverageFactory.writeoutNode($scope.annotations, 0, $scope.subGridOptions.data);
                    } else {
                        var blankAnnotation = new Annotation();
                        $scope.annotations.push(blankAnnotation);
                        $scope.subGridOptions.data = $scope.annotations;
                    }

                    $scope.gridApi.selection.selectRow(row.entity, $scope.gridApi.grid);
                });
            }
        }
        
        $scope.searchAnnotations = function() {
            Item.get({
                programId: $scope.programSelection,
                setId: $scope.setSelection,
                itemId: $scope.selectedItem.guid,
                keyword: "text"
            })
        }

        $scope.deleteAnnotation = function deleteAnnotation(annotation) {
            if ($scope.selectedItem.assignee != $rootScope.cachedName) {
            	alert("You are not assigned to this Item. Double click on the assignee field for this item to steal it and make changes");
            } else { 
	            Annotation.delete({
	                programId: $scope.programSelection,
	                setId: $scope.setSelection,
	                itemId: $scope.selectedItem.guid,
	                annotationId: annotation.guid,
	                userName: $rootScope.cachedName
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
        }

        $scope.massDisposition = function(itemIds, resolutionType, resolution) {
        	var loadingModal = $scope.showLoadingModal();
        	
        	MassDisposition.save({
                programId: $scope.programSelection,
                setId: $scope.setSelection,
                resolutionType: resolutionType,
                resolution: resolution,
                userName: $rootScope.cachedName
            }, itemIds, function(data){
            	$scope.items = {};
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
            }, function() {
            	alert("Ooops...Something went wrong");
            	loadingModal.close();
            });
        }
        
        $scope.editItem = function editItem(item) {
            $scope.editItem(item, null);
        }

        $scope.editAssignees = function(item) {
            $scope.editItem(item, 'assignee');
        }

        $scope.editItemNotes = function(item) {
            $scope.editItem(item, 'itemNotes');
        }

        $scope.editCategories = function(item) {
            $scope.editItem(item, 'category');
        }

        $scope.editNeedsRerun = function(item) {
            $scope.editItem(item, 'needsRerun');
        }

        $scope.editItem = function editItem(item, field) {
            var newItem = new Item();
            if (field == null) {
                newItem = item;
            } else if (field == 'itemNotes') {
                newItem.itemNotes = item.itemNotes;
            } else if (field == 'needsRerun') {
                newItem.needsRerun = item.needsRerun;
            } else if (field == 'category') {
                newItem.category = item.category;
            } else if (field == 'assignee') {
                newItem.assignee = item.assignee;
            }

            Item.update({
                programId: $scope.programSelection,
                setId: $scope.setSelection,
                itemId: item.guid,
                userName: $rootScope.cachedName,
                assignUser: field == 'assignee' ? true : false
            }, newItem, function() {
                if ($scope.isMulitEditRequest) {
                    $scope.gridApi.selection.clearSelectedRows();
                    $scope.isMulitEditRequest = false;
                }
            }, function(data) {
                alert("Could not make change, please try refreshing");
            });

        }

        $scope.getInvalidLocRefs = function getInvalidLocRefs(annotation) {
            if (annotation.isConnected != null) {
                return !annotation.isConnected && annotation.locationRefs != null;
            } else {
                return false;
            }
        }

        $scope.getInvalidRes = function getInvalidRes(annotation) {
            return annotation.resolution != null && annotation.resolution != "" && !annotation.isResolutionValid;
        }

        $scope.editAnnotation = function editAnnotation(colDef, oldValue, annotation) {
            if ($scope.selectedItem.assignee == $rootScope.cachedName) {
                $scope.editAnnotationServerCall(colDef, oldValue, annotation);
            } else if ($scope.selectedItem.assignee == "UnAssigned") {
                var newItem = new Item();
                newItem.assignee = $rootScope.cachedName;
                Item.get({
                    programId: $scope.programSelection,
                    setId: $scope.setSelection,
                    itemId: $scope.selectedItem.guid
                }, function(data) {
                    if (data.assignee == "UnAssigned") {
                        Item.update({
                            programId: $scope.programSelection,
                            setId: $scope.setSelection,
                            itemId: $scope.selectedItem.guid,
                            userName: $rootScope.cachedName
                        }, newItem, function() {
                            $scope.selectedItem.assignee = $rootScope.cachedName;
                            $scope.editAnnotationServerCall(colDef, oldValue, annotation)
                        }, function(data) {
                        	annotation[colDef.name] = oldValue;
                            alert("Could not make change, please try refreshing");
                        });
                    } else {
                    	annotation[colDef.name] = oldValue;
                        $scope.selectedItem.assignee = data.assignee;
                        alert("This item was taken while you weren't looking. Double click on the assignee field for this item to steal it and make changes");
                    }
                });
            } else {
            	annotation[colDef.name] = oldValue;
                alert("You are not assigned to this Item. Double click on the assignee field for this item to steal it and make changes");
            }
        }

        $scope.editAnnotationServerCall = function(colDef, oldValue, annotation) {
            if (annotation.guid == null) {
                if (/[^\s]+/.test(annotation.locationRefs)) {
                    $scope.createAnnotation(annotation);
                }
            } else {
            	// We do a clone for two reasons 
            	// 1. this is a PUT and not a PATCH we send the entire object as is not just the single field that was updated
            	// 2. we need to strip fields before sending so we don't want to change the annotation being rendered
            	var newAnnotation = $scope.cloneObj(annotation);
            	newAnnotation.parentRef = null;
            	// remove any field used strictly for UI Tree rendering purposes - server side parser will throw unknown field exception 
            	// since those fields are not present in the java object DispoAnnotationData
            	delete newAnnotation['children'];
            	delete newAnnotation['isLeaf'];
            	delete newAnnotation['$resolved'];
            	delete newAnnotation['parentId'];
            	// Removing the parent ref is extra important because JSON Stringify cannot handle circular references
            	// Stringify gets called because the payload needs to be parsed on transmit
            	delete newAnnotation['parentRef'];
            	
                Annotation.update({
                    programId: $scope.programSelection,
                    setId: $scope.setSelection,
                    itemId: $scope.selectedItem.guid,
                    annotationId: annotation.guid,
                    userName: $rootScope.cachedName,
                }, newAnnotation, function(annot) {
                    // get latest Annotation version from Server
                    Annotation.get({
                        programId: $scope.programSelection,
                        setId: $scope.setSelection,
                        itemId: $scope.selectedItem.guid,
                        annotationId: annotation.guid
                    }, function(data) {
                    	// These two fields are updated on the server when editing an Annotation thus we need to update our copy of them
                        annotation.isConnected = data.isConnected;
                        annotation.isResolutionValid = data.isResolutionValid;
                        CoverageFactory.updatePercent(colDef, oldValue, annotation);
                        if ($scope.getInvalidRes(annotation)) {
                        	alert("Your resolution is not valid. \nPlease make sure your PCR # is correct.");
                        }
                    }, function(data) {
                    	alert
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

                if ($scope.isCoverage) {
                    $scope.annotations.sort(sortStuff);
                }
            }
        }

        $scope.createAnnotation = function createAnnotation(annotation) {
            annotation.$save({
                programId: $scope.programSelection,
                setId: $scope.setSelection,
                itemId: $scope.selectedItem.guid,
                userName: $rootScope.cachedName,
            }, function() {
                Item.get({
                    programId: $scope.programSelection,
                    setId: $scope.setSelection,
                    itemId: $scope.selectedItem.guid
                }, function(data) {
                    $scope.updateItemFromServer($scope.selectedItem, data);
                });

                var blankAnnotation = new Annotation();
                $scope.annotations.push(blankAnnotation);

                if ($scope.isCoverage) {
                    $scope.annotations.sort(sortStuff);
                }
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
            oldItem.itemNotes = newItem.itemNotes;
            oldItem.status = newItem.status;
        }

        $scope.doAdvSearch = function(value, loadingModal) {
            SetSearch.query({
                programId: $scope.programSelection,
                setId: $scope.setSelection,
                value: value,
                isDetailed: $scope.isCoverage
            }, function(data) {
                if ($scope.isSearchView) {
                    $scope.items = data;
                    if (loadingModal != null) {
                        loadingModal.close();
                    }
                } else {
                    $scope.searchData = data;
                    $scope.searchValue = value;
                    var popupWindow = window.open("/coverage/ui/index.html#/user");
                    popupWindow.parentScope = $scope;

                }
            }, function(data) {
                if ($scope.isSearchView) {
                    $scope.items = $scope.emptyItems;
                } else {
                    $scope.searchData = $scope.emptyItems;
                    window.$windowScope = $scope;
                    window.open("/dispo/main.html#/search");
                }
                if (loadingModal != null) {
                    loadingModal.close();
                }
            });
        }
        
        $scope.getSourceFile = function () {
    		if($scope.isCoverage) {
         	var requst = [];
         	requst.push(
         	  "/dispo/",
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
     }

        $scope.emptyItems = [{
            "name": "NONE FOUND"
        }];

        
        // Utils **************************************************************************
        $scope.cloneObj = function(obj) {
        	var toReturn = {};
        	var keys = Object.keys(obj);
        	for(var i = 0; i < keys.length; i++) {
        		var key = keys[i];
        		toReturn[key] = obj[key];
        	}
        	return toReturn;
        }
        
        $scope.isCausingInvalid = function(annotation) {
        	if($scope.isCoverage) {
        		// Right now an invalid resolution is pretty straight forward and it's the same for coverage and test script dispo
        		// If Resolution is valid but resolutionType is not then return true
        		// In future, there might be more complicated reasons for invalid so putting a placeholder condition now
        		return annotation.isLeaf && annotation.resolutionType != "" && annotation.resolution == "";
        	} else {
        		return annotation.resolutionType != "" && annotation.resolution == "";
        	}
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
                for (var i = 0; i < size; i++) {
                    if ($scope.selectedItems[i].assignee != inputs.multiAssignee) {
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
                for (var i = 0; i < size; i++) {
                    if ($scope.selectedItems[i].category != inputs.category) {
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
                for (var i = 0; i < size; i++) {
                    if ($scope.selectedItems[i].needsRerun != inputs.needsRerun) {
                        $scope.selectedItems[i].needsRerun = inputs.needsRerun;
                        $scope.editNeedsRerun($scope.selectedItems[i]);
                    }
                }
            });
        }

        var NeedsRerunModalCtrl = function($scope, $modalInstance) {
            $scope.ok = function() {
                var inputs = {};
                if (this.formData == undefined) {
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


        // Mass Disposition Modal
        $scope.showMassDispositionModal = function() {
        	$scope.isMulitEditRequest = true;
            var modalInstance = $modal.open({
                templateUrl: 'massDisposition.html',
                controller: MassDispositionCtrl,
                size: 'sm',
                windowClass: 'massDispositionModal',
                resolve: {
                	coverageResolutionTypes: function() {
                		return $scope.coverageResolutionTypes;
                	}
                }
            });

            modalInstance.result.then(function(inputs) {
            	var newSet = new Set;
            	var operation = {};
            	var parameters = {};
            	operation.operationName = "Mass_Disposition";
            	var itemIds = [];
            	var size = $scope.selectedItems.length;
            	for(var i = 0; i < size; i++) {
            		itemIds.push($scope.selectedItems[i].guid);
            	}
            	$scope.massDisposition(itemIds, inputs.resolutionType, inputs.resolution);
            });
        }
        
        var MassDispositionCtrl = function($scope, $modalInstance, coverageResolutionTypes) {
        	$scope.typesLocal = coverageResolutionTypes.slice();
            $scope.ok = function() {
                var inputs = {};
                inputs.resolutionType = this.resolutionType;
                inputs.resolution = this.resolution;
                $modalInstance.close(inputs);
            };

            $scope.cancel = function() {
                $modalInstance.dismiss('cancel');
            };
        };
        
        
        // Advanced Search Modal
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
                if ($scope.isSearchView) {
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

        // Item Notes Modal
        $scope.showItemNotesModal = function() {
            $scope.isMulitEditRequest = true;
            var modalInstance = $modal.open({
                templateUrl: 'itemNotesModal.html',
                controller: ItemNotesModalCtrl,
                size: 'md',
                windowClass: 'itemNotesModal'
            });

            modalInstance.result.then(function(inputs) {
                var size = $scope.selectedItems.length;
                for (var i = 0; i < size; i++) {
                    if ($scope.selectedItems[i].itemNotes != inputs.itemNotes) {
                        $scope.selectedItems[i].itemNotes = inputs.itemNotes;
                        $scope.editItemNotes($scope.selectedItems[i]);
                    }
                }
            });
        }

        var ItemNotesModalCtrl = function($scope, $modalInstance) {
            $scope.itemNotes = "";

            $scope.ok = function() {
                var inputs = {};
                inputs.itemNotes = this.itemNotes;
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
        
        
        // Show All Failures Modal
        $scope.showAllFailuresModal = function() {
            var modalInstance = $modal.open({
                templateUrl: 'showAllFailuresModal.html',
                controller: ShowAllFailuresCtrl,
                size: 'lg',
                windowClass: 'showAllFailures',
                resolve: {
                    item: function() {
                        return $scope.selectedItem;
                    }
                }
            });
        }

        var ShowAllFailuresCtrl = function($scope, $modalInstance, item) {
        	$scope.item = item;
            $scope.close = function() {
                $modalInstance.dismiss('cancel');
            };
        };

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
        scope: {
            trigger: '=focusMe'
        },
        link: function(scope, element) {
            scope.$watch('trigger', function(value) {
                if (value === true) {
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
                if (newValue)
                    disableOptions(scope, expElements[2], iElement, newValue, fnDisableIfTrue);
            }, true);
            // handle model updates properly
            scope.$watch(iAttrs.ngModel, function(newValue, oldValue) {
                var disOptions = $parse(attrToWatch)(scope);
                if (newValue)
                    disableOptions(scope, expElements[2], iElement, disOptions, fnDisableIfTrue);
            });
        }
    };
});