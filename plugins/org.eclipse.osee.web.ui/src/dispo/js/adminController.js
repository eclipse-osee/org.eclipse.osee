app.controller('adminController', [
    '$scope',
    '$rootScope',
    '$modal',
    '$filter',
    'Program',
    'Set',
    'Report',
    'CopySet',
    'CopySetCoverage',
    'MultiItemEdit',
    'Config',
    'uiGridConstants',
        function($scope, $rootScope,  $modal, $filter, Program, Set, Report, CopySet, CopySetCoverage, MultiItemEdit, Config, uiGridConstants) {
            $scope.readOnly = true;
            $scope.programSelection = null;
            $scope.modalShown = false;
            $scope.primarySet = "";
            $scope.secondarySet = "";
            $scope.sets = [];
            $scope.addNew = false;
            $scope.newProgramName = ""
            $scope.selectedItems = [];
            $scope.isRunningOperation = false;
            $scope.cachedValue = "";
            $scope.types = [];
            $scope.isCoverage = $rootScope.type == 'codeCoverage';
            $scope.programs = Program.query();
            
            var isPrimary = function(importState) {
                return row.entity.importState != "Warnings" && row.entity.importState != "Failed";
        }
		        
        $scope.createNewProgram = function() {
            if ($scope.newProgramName != "") {
                var loadingModal = $scope.showLoadingModal();
                var newProgram = new Program;
                newProgram.name = $scope.newProgramName;
                newProgram.$save({
                    name: $scope.newProgramName,
                    userName: $rootScope.cachedName
                }, function() {
                    $scope.newProgramName = "";
                    $scope.addNew = false;
                    loadingModal.close();
                    $scope.programs = Program.query();
                }, function() {
                    loadingModal.close();
                    alert("Oops...Something went wrong");
                });
            }
        }
        
        $scope.toggleAddNew = function() {
            if($scope.addNew) {
                $scope.addNew = false;
            } else {
                $scope.addNew = true;
            }
        }
        
        $scope.gridOptions = {
            data: 'sets',
            selectedItems: $scope.selectedItems,
            showGroupPanel: false,
            enableGridMenu: false,
            enableCellEdit: true
        }

        var editCellTmpl = '<input editable="true" >'
        var dellCellTmpl = '<button width="50px" class="btn btn-danger btn-sm setDelete" ng-show="!readOnly" ng-click="grid.appScope.deleteSet(row.entity)">X</button>';
        var importCellTmpl = '<button width="50px" class="btn btn-primary" ng-disabled="row.entity.processingImport" ng-click="grid.appScope.importSet(row.entity)">Import</button>';
        var exportCellTmpl = '<button width="50px" class="btn btn-primary" ng-disabled="row.entity.processingImport" ng-click="grid.appScope.exportSet(row.entity)">Export</button>';
        var lastOperationCellTmpl = '<id="stateButton" button width="99%" ng-disabled="row.entity.processingImport || row.entity.gettingDetails" ng-class="{btn: true, \'btn-primary\': \'isPrimary(row.entity.importState)\',' +
        '\'btn-warning\': row.entity.importState == \'Warnings\', \'btn-danger\': row.entity.importState == \'Failed\', \'btn-success\': row.entity.importState == \'OK\'}" ng-click="grid.appScope.getSetImportDetails(row.entity)">{{row.entity.importState}}</button>';
         
        $scope.columnDefs1 = [{
            field: 'name',
            displayName: "Import",
            width: '7%',
            enableColumnMenu: false,
            enableCellEdit: false,
            cellTemplate: importCellTmpl
        }, {
            field: 'name',
            displayName: "Export",
            width: '7%',
            enableColumnMenu: false,
            enableCellEdit: false,
            cellTemplate: exportCellTmpl
        }, {
            field: 'name',
            displayName: "Last Operation",
            width: '10%',
            enableColumnMenu: false,
            enableCellEdit: false,
            cellTemplate: lastOperationCellTmpl
        }, {
		            field: 'time',
		            displayName: "Timestamp",
		            width: '15%',
		            enableColumnMenu: false,
		            enableCellEdit: false
		        }, {
            field: 'name',
            displayName: "Name",
            width: '12%',
            enableColumnMenu: false,
            enableCellEdit: false
        }, {
            field: 'importPath',
            displayName: "Path",
            enableColumnMenu: false,
            enableCellEdit: false
        }];

        $scope.columnDefs2 = [{
            field: 'name',
            displayName: "Import",
            width: '7%',
            enableColumnMenu: false,
            enableCellEdit: false,
            cellTemplate: importCellTmpl
        }, {
            field: 'name',
            displayName: "Export",
            width: '7%',
            enableColumnMenu: false,
            enableCellEdit: false,
            cellTemplate: exportCellTmpl
        }, {
        	field: 'name',
            displayName: "Last Operation",
            width: '10%',
            enableColumnMenu: false,
            enableCellEdit: false,
        	cellTemplate: lastOperationCellTmpl
        }, {
		            field: 'time',
		            displayName: "Timestamp",
		            width: '15%',
		            enableColumnMenu: false,
		            enableCellEdit: false
		        }, {
            field: 'name',
            displayName: "Name",
            width: '12%',
            enableColumnMenu: false,
            enableCellEdit: true,
        }, {
            field: 'importPath',
            displayName: "Path",
            enableColumnMenu: false,
            enableCellEdit: true,
        }, {
            field: 'name',
            displayName: "",
            width: '5%',
            enableColumnMenu: false,
            cellTemplate: dellCellTmpl
        }];
        
        $scope.gridOptions.columnDefs = $scope.columnDefs1;
        
        $scope.gridOptions.onRegisterApi = function(gridApi) {

            $scope.subGridApi = gridApi;

            gridApi.edit.on.afterCellEdit($scope, function(rowEntity, colDef, newValue, oldValue) {
                if (oldValue != newValue) {
                    $scope.editSet(rowEntity);
                }
            });

        };

        $scope.toggleModal = function() {
            $scope.modalShown = !$scope.modalShown
        };

        $scope.toggleReadOnly = function() {
            if ($scope.readOnly) {
                $scope.gridOptions.columnDefs = $scope.columnDefs2;
                $scope.readOnly = false;
            } else {
                $scope.gridOptions.columnDefs = $scope.columnDefs1;
                $scope.readOnly = true;
            }
        };

        $scope.generateReport = function() {
            var requst = [];
            requst.push(
              "/dispo/program/",
              $scope.programSelection,
              "/admin/report?primarySet=",
              $scope.primarySet,
              "&secondarySet=",
              $scope.secondarySet
              );
            var url = requst.join("");
            
            window.open(url);
        }
        
        $scope.getSetImportDetails = function(set) {
            $scope.setAnnotationsSummaryGrid();
            set.gettingDetails = true;
            Set.get({
                programId: $scope.programSelection,
                setId: set.guid
            }, function(data) {
                set.gettingDetails = false;
                $scope.operationSummary = data.operationSummary;
                $scope.summaryGrid.data = $scope.operationSummary.entries;
                set.importState = data.importState;
			        	set.time = data.time;
            }, function(data) {
                set.gettingDetails = false;
                alert("Could not update Set from Server");
            })
        }
        
        $scope.getCiSetDetails = function(set) {
            $scope.setConfigureCiSetSummaryGrid();
            set.gettingDetails = true;
            Set.get({
                programId: $scope.programSelection,
                setId: set.guid
            }, function(data) {
                set.gettingDetails = false;
                $scope.operationSummary = set.operationSummary;
                $scope.summaryGrid.data = set.operationSummary.entries;
                set.importState = data.importState;
            }, function(data) {
                set.gettingDetails = false;
                alert("Could not update Set from Server");
            })
        }
        
        $scope.getMassSendDispoItemStatus = function(set) {
            set.gettingDetails = true;
            Set.get({
                programId: $scope.programSelection,
                setId: set.guid
            }, function(data) {
                set.gettingDetails = false;
                $scope.operationSummary = data.operationSummary;
                $scope.summaryGrid.data = data.operationSummary.entries;
                set.importState = data.importState;
            }, function(data) {
                set.gettingDetails = false;
                alert("Could not update Set from Server");
            })
        }
        
        $scope.updateProgram = function updateProgram() {
            var loadingModal = $scope.showLoadingModal();
            $scope.loading = true;
            $scope.items = {};
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
			            Config.get({
                            programId: $scope.programSelection,
                            type: $rootScope.type
                        }, function(data) {
                       	      $scope.types = data.validResolutions;
                        }); 
        };

        $scope.editSet = function editSet(set) {
            Set.update({
                programId: $scope.programSelection,
                setId: set.guid,
                userName: $rootScope.cachedName
            }, set);
        };

        $scope.massAssignTeam = function(setId, team, namesList) {
            $scope.isRunningOperation = true;
            var loadingModal = $scope.showLoadingModal();
            var multiItemEditOp = new MultiItemEdit;
            multiItemEditOp.namesList = namesList;
            multiItemEditOp.team = team;
            multiItemEditOp.setId = setId;
            multiItemEditOp.userName = $rootScope.cachedName;
            
            multiItemEditOp.$save({
                programId: $scope.programSelection,
                userName: $rootScope.cachedName
            }, function(data) {
                $scope.isRunningOperation = false;
                loadingModal.close();
                $scope.getSetImportDetails($scope.getSetById(setId));
            }, function() {
                $scope.isRunningOperation = false;
                loadingModal.close();
                alert("Oops...Something went wrong");
                // boo
            })
        };
        
        $scope.getSetById = function(setId) {
            for(var i =0; i < $scope.sets.length; i++) {
                if($scope.sets[i].guid == setId) {
                    return $scope.sets[i];
                }
            }
            return null;
        }

        $scope.deleteSet = function deleteSet(set) {
            var loadingModal = $scope.openConfirmDeleteModal(set);
        }
        
        $scope.importSet = function importSet(set) {
            var newSet = new Set;
            newSet.operation = "Import";
            set.processingImport = true;
            Set.update({
                programId: $scope.programSelection,
                setId: set.guid,
                userName: $rootScope.cachedName
            }, newSet, function(data){
                set.processingImport = false;
		            	set.time = data.time;
                $scope.getSetImportDetails(set);
            }, function() {
                set.processingImport = false;
		            	set.time = new Date();
                $scope.getSetImportDetails(set);
            });
        };
        
        $scope.exportSet = function importSet(set) {
            var requst = [];
            requst.push(
              "/dispo/program/",
              $scope.programSelection,
              "/admin/export?primarySet=",
              set.guid,
              "&option=detailed"
              );
            var url = requst.join("");

            window.open(url);
        };

        $scope.createNewSet = function createNewSet(name, path) {
            if (name != "" && path != "") {
                var newSet = new Set;
                newSet.name = name;
                newSet.importPath = path;
                newSet.dispoType = $rootScope.type;
                newSet.$save({
                    programId: $scope.programSelection,
                    userName: $rootScope.cachedName
                }, function(data) {
		                	data.time = formatDate(data.time);
                    $scope.sets.push(data);
                });
            }
        };

        $scope.copySet = function(inputs)	 {			        		        	
            $scope.isRunningOperation = true;
            var destinationSet = $scope.getSetById(inputs.destinationSet);
            var copySetOp = new CopySet;
            copySetOp.annotationParam = inputs.annotationParam;
            copySetOp.categoryParam = inputs.categoryParam;
            copySetOp.assigneeParam = inputs.assigneeParam;
            copySetOp.noteParam = inputs.noteParam;
            copySetOp.sourceProgram = inputs.sourceProgram;		        	
            
            copySetOp.$save({
                programId: $scope.programSelection,
                destinationSet: inputs.destinationSet,
                sourceProgram: inputs.sourceProgram,
                sourceSet: inputs.sourceSet,
                userName: $rootScope.cachedName
            }, function(data) {
                $scope.isRunningOperation = false;
                $scope.getSetImportDetails($scope.getSetById(inputs.destinationSet));
            }, function(data) {
                $scope.isRunningOperation = false;
                $scope.getSetImportDetails($scope.getSetById(inputs.destinationSet));
            });
        };

        $scope.configureCiSet = function setCiSet(inputs) {		        			        	       	
            $scope.isRunningOperation = true;
            var localSet =  $scope.getSetById(inputs.ciDispositionSet);
            var ciSetWas = localSet.ciSet;
            if (inputs.ciSet != "") {
                localSet.ciSet = inputs.ciSet;
            }
            Set.update({
            programId: $scope.programSelection,
            setId: inputs.ciDispositionSet,
            userName: $rootScope.cachedName
            }, localSet, function(data) {
                var message = "";
                if (inputs.ciSet == "") {
                    message = "Nothing was entered for a new CI Set name.";
                    data.ciSet = ciSetWas;
                }
                else {
                    message = "New CI Set name was applied."
                }
                $scope.isRunningOperation = false;

                localSet.operationSummary.entries.length = 0;
                localSet.operationSummary.entries.push(
                        {"message":message,"ciSetIs":data.ciSet,"ciSetWas":ciSetWas,"dispoSet":localSet.name}
                );

                $scope.getCiSetDetails(localSet);
            });
        };
        		        		        
        $scope.massSendDispoItemStatus = function massSendDispoItemStatus (set) {
            $scope.setAnnotationsSummaryGrid();
            $scope.isRunningOperation = true;
            var newSet = $scope.getSetById(set.ciDispositionSet);
            newSet.operation = "MassSendDispoItemStatus";
            Set.update({
                programId: $scope.programSelection,
                setId: set.ciDispositionSet,
                userName: $rootScope.cachedName
            }, newSet, function(data) {            	
                $scope.isRunningOperation = false;		            			            	
                $scope.getMassSendDispoItemStatus(newSet);
            });
        };
        		        
		        $scope.rerunReportStatus = function rerunReportStatus (input) {
		        	var newSet = $scope.getSetById(input.rerunDispositionSet);
		        	var request = [];
		        	request.push(
		        	  "/dispo/program/",
		        	  $scope.programSelection,
		        	  "/admin/rerun?primarySet=",
		        	  newSet.guid
		        	  );
		        	var url = request.join("");
		            window.open(url);
		        };
		        
		        
		        
        // -------------------- Summary Grids ----------------------\\
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

        $scope.summaryGrid = {}	
        
        $scope.summaryGrid.onRegisterApi = function(gridApi) {
            $scope.subGridApi = gridApi;
        };	
        
        $scope.setConfigureCiSetSummaryGrid = function() {
            $scope.summaryGrid = { 
                configureCiSet: true,
                data: 'operationSummary.entries',
                enableHighlighting: true,
                enableColumnResize: true,
                multiSelect: false,
                showFilter: true,
                enableFiltering: true,
                headerRowHeight: 60, // give room for filter bar
            
                columnDefs : [{
                    field: "dispoSet",
                    displayName: "Disposition Set",
                    width: '15%',
                },{
                    field: "ciSetWas",
                    displayName: "CI Set Was",
                    width: '15%',
                },{
                    field: "ciSetIs",
                    displayName: "CI Set Is",
                    width: '15%',
                },{
                    field: "message",
                    displayName: "Message",
                }]
            }
        }
        
        $scope.setAnnotationsSummaryGrid = function () {
            $scope.summaryGrid = { 
                configureCiSet: true,
                data: 'operationSummary.entries',
                enableHighlighting: true,
                enableColumnResize: true,
                multiSelect: false,
                showFilter: true,
                enableFiltering: true,
                headerRowHeight: 60, // give room for filter bar
            
                columnDefs : [{
                    field: "severity",
                    displayName: "Severity",
                    width: '10%',
                },{
                    field: "name",
                    displayName: "Name",
                    width: '20%',
                },{
                    field: "message",
                    displayName: "Message",
                }]
            }		        	
        }

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
        
        $scope.copySetCoverage = function(inputs)	 {
            var copySetOp = new CopySetCoverage;
        	
            copySetOp.$save({
                programId: $scope.programSelection,
                destinationSet: inputs.destinationSet,
                sourceBranch: inputs.sourceBranch,
                sourcePackage: inputs.sourcePackage,
            }, function(data) {
                var reportUrl = data.operationStatus;
                window.open(reportUrl);
                console.log(data);
            });
        }

        // Create Set Modal
        $scope.createNewSetModal = function() {
            var modalInstance = $modal.open({
                templateUrl: 'popup.html',
                controller: CreateSetModalCtrl,
                size: 'sm',
                windowClass: 'createSetModal'
            });

            modalInstance.result.then(function(inputs) {
                $scope.createNewSet(inputs.name, inputs.path);
            });
        }

        var CreateSetModalCtrl = function($scope, $modalInstance) {
            $scope.setName = "";
            $scope.importPath = "";
            
            $scope.ok = function() {
                var inputs = {};
                inputs.name = this.setName;
                inputs.path = this.importPath;
                $modalInstance.close(inputs);
            };

            $scope.cancel = function() {
                $modalInstance.dismiss('cancel');
            };
        };
               
        // Mass Assign Modal
        $scope.openMassAssignTeamModal = function() {
            var modalInstance = $modal.open({
                templateUrl: 'massAssignTeam.html',
                controller: MassAssignTeamCtrl,
                size: 'lg',
                windowClass: 'massAssignTeamModal',
                resolve: {
                    sets: function() {
                        return $scope.sets;
                    },
                    gridSelectedSetId: function() {
                        if($scope.selectedItems.legnth > 0) {
                            return $scope.selectedItems[0].guid;
                        } else {
                            return null;
                        }
                    }
                }
            });

            modalInstance.result.then(function(inputs) {
                $scope.massAssignTeam(inputs.setId, inputs.team, inputs.nameList);
            });
        }

        var MassAssignTeamCtrl = function($scope, $modalInstance, gridSelectedSetId, sets) {	
            $scope.setsLocal = sets.slice();
            $scope.nameListAsString = "";
            $scope.team = "";
            $scope.setId = gridSelectedSetId;

            $scope.ok = function() {
                var inputs = {};
                inputs.nameList = this.nameListAsString.split(",");
                inputs.team = this.team;
                inputs.setId = this.setId;
                
                $modalInstance.close(inputs);
            };

            $scope.cancel = function() {
                $modalInstance.dismiss('cancel');
            };
        }
        
        // Copy Set Modal
        $scope.openCopySetModal = function() {		        			        	
            var modalInstance = $modal.open({
                templateUrl: 'copySets.html',
                controller: CopySetModalCtrl,
                size: 'md',
                windowClass: 'copySetModal',
                resolve: {
                    sets: function() {
                        return $scope.sets;
                    }, 
                    programs: function() {
                        return $scope.programs;
                    }, 
                    showLoadingModal: function() {
                        return $scope.showLoadingModal;
                    }, 
                    currentlySelectedProgram: function() {
                        return $scope.programSelection;
                    }
                }
            });

            modalInstance.result.then(function(inputs) {
                $scope.copySet(inputs);
            });
        }
        
        
        var CopySetModalCtrl = function($scope, $modalInstance, programs, currentlySelectedProgram, sets, showLoadingModal) {
            $scope.setsLocal = sets.slice();
            $scope.programsLocal = programs.slice();
            $scope.setsLocalSource = sets.slice();
            $scope.sourceProgram = currentlySelectedProgram;
            
            $scope.updateProgramLocal = function() {
                var loadingModal = showLoadingModal();
                $scope.loading = true;
                Set.query({
                    programId: $scope.sourceProgram,
                    type: $rootScope.type
                }, function(data) {
                    loadingModal.close();
                    $scope.setsLocalSource = data;
                }, function(data) {
                    loadingModal.close();
                    alert(data.statusText);
                });
            };
            
            $scope.annotationOptions = [{ value: 0, text: 'NONE'}, { value: 1, text: 'OVERRIDE'}];
            $scope.categoryOptions = [{ value: 0, text: 'NONE'}, { value: 1, text: 'OVERRIDE'}, { value: 2, text: 'ONLY COPY IF DEST IS EMPTY'}, { value: 3, text: 'MERGE DEST AND SOURCE'}];
            $scope.assigneeOptions = [{ value: 0, text: 'NONE'}, { value: 1, text: 'OVERRIDE'}, { value: 2, text: 'ONLY COPY IF DEST IS UNASSIGNED'}];
            $scope.noteOptions = [{ value: 0, text: 'NONE'}, { value: 1, text: 'OVERRIDE'}, { value: 2, text: 'ONLY COPY IF DEST IS EMPTY'}, { value: 3, text: 'MERGE DEST AND SOURCE'}];
            
            $scope.annotationParam = 0;
            $scope.categoryParam = 0;
            $scope.assigneeParam = 0;
            $scope.noteParam = 0;

            $scope.ok = function() {
                var inputs = {};
                inputs.destinationSet = this.destinationSet;
                inputs.sourceProgram = this.sourceProgram;
                inputs.sourceSet = this.sourceSet;
                inputs.annotationParam = this.annotationParam;
                inputs.categoryParam = this.categoryParam;
                inputs.noteParam = this.noteParam;
                inputs.assigneeParam = this.assigneeParam;
                
                $modalInstance.close(inputs);
            };

            $scope.cancel = function() {
                $modalInstance.dismiss('cancel');
            };
        };
        
        
        // Copy Coverage Modal
        $scope.openCopyCoverageModal = function() {
            var modalInstance = $modal.open({
                templateUrl: 'copySetCoverage.html',
                controller: CopyCoverageModalCtrl,
                size: 'md',
                windowClass: 'copyCoverageModal',
                resolve: {
                    sets: function() {
                        return $scope.sets;
                    }
                }
            });

            modalInstance.result.then(function(inputs) {
                $scope.copySetCoverage(inputs);
            });
        }
        
        
        var CopyCoverageModalCtrl = function($scope, $modalInstance, sets) {
            $scope.setsLocal = angular.copy(sets);
            
            $scope.ok = function() {
                var inputs = {};
                inputs.destinationSet = this.destinationSet;
                inputs.sourceBranch = this.sourceBranch;
                inputs.sourcePackage = this.sourcePackage;
                
                $modalInstance.close(inputs);
            };

            $scope.cancel = function() {
                $modalInstance.dismiss('cancel');
            };
        };
        
        // Confirm Delete Modal
        $scope.openConfirmDeleteModal = function(set) {
            var modalInstance = $modal.open({
                templateUrl: 'confirmDelete.html',
                controller: ConfirmDeleteCtrl,
                size: 'sm',
                windowClass: 'confirmDeleteModal',
                resolve: {
                    selectedProgram: function() {
                        return $scope.programSelection;
                    },
                    selectedSet: function() {
                        return set;
                    }              	
                }
            });

            modalInstance.result.then(function(inputs) {
                if(inputs.isConfirmed) {
                    Set.delete({
                        programId: inputs.program,
                        setId: inputs.set.guid,
                        userName: $rootScope.cachedName
                    }, function() {
                        var index = $scope.sets.indexOf(inputs.set);
                        if (index > -1) {
                            $scope.sets.splice(index, 1);
                        }
                    });
                }
            });
        }

        var ConfirmDeleteCtrl = function($scope, $modalInstance, selectedProgram, selectedSet) {
            $scope.text = "";
            
            $scope.ok = function() {
                var inputs = {};
                inputs.isConfirmed = false;
                inputs.program = selectedProgram;
                inputs.set = selectedSet;
                
                if(this.text.toUpperCase() == "DELETE") {
                    inputs.isConfirmed = true;
                }
                $modalInstance.close(inputs);
            };

            $scope.cancel = function() {
                $modalInstance.dismiss('cancel');
            };
        };
        
        // Configure/Set CI Set
        $scope.openConfigureCiSetModal = function() {
            var modalInstance = $modal.open({
                templateUrl: 'configureCiSet.html',
                controller: ConfigureCiSetCtrl,
                size: 'sm',
                windowClass: 'ConfigureCiSetModal',
                resolve: {
                    sets: function() {
                        return $scope.sets;
                    }
                }
            });

            modalInstance.result.then(function(inputs) {
                $scope.configureCiSet(inputs);
            });
        }
        
        var ConfigureCiSetCtrl = function($scope, $modalInstance, sets) {
            $scope.ciSet =  "";
            $scope.ciDispositionSet = "";
            $scope.setsLocal = angular.copy(sets);
            $scope.ok = function() {
                var inputs = {};
                inputs.ciSet = this.ciSet
                inputs.ciDispositionSet = this.dispositionSet;
                $modalInstance.close(inputs);
            };

            $scope.cancel = function() {
                $modalInstance.dismiss('cancel');
            };
        }
        
        // Mass Send Disposition Item Status
        $scope.openMassSendDispoItemStatusModal = function() {
            var modalInstance = $modal.open({
                templateUrl: 'massSendDispoItemStatus.html',
                controller: MassSendDispoItemStatusCtrl,
                size: 'sm',
                windowClass: 'MassSendDispoItemStatusModal',
                resolve: {
                    sets: function() {
                        return $scope.sets;
                    }
                }
            });

            modalInstance.result.then(function(inputs) {
                $scope.massSendDispoItemStatus(inputs);
            });
        }
        
        var MassSendDispoItemStatusCtrl = function($scope, $modalInstance, sets) {
            $scope.ciDispositionSet = "";
            $scope.setsLocal = angular.copy(sets);
            $scope.ok = function() {
                var inputs = {};
                inputs.ciDispositionSet = this.dispositionSet;
		                $modalInstance.close(inputs);
		            };

		            $scope.cancel = function() {
		                $modalInstance.dismiss('cancel');
		            };
		        }
		        
		        
		        // Report Reruns
		        $scope.openRerunReportStatusModal = function() {
		        	 var modalInstance = $modal.open({
			                templateUrl: 'rerunReportStatus.html',
			                controller: RerunReportStatusCtrl,
			                size: 'sm',
			                windowClass: 'rerunReportStatusModal',
			                resolve: {
			                	sets: function() {
			                		return $scope.sets;
			                	},
			                	types: function() {
			                		return $scope.types;
			                	}
			                }
			            });

			            modalInstance.result.then(function(inputs) {
			            	$scope.rerunReportStatus(inputs);
			            });
		        }
		        
		        var RerunReportStatusCtrl = function($scope, $modalInstance, sets, types) {
		        	$scope.rerunDispositionSet = "";
		        	$scope.setsLocal = angular.copy(sets);
		        	$scope.typesLocal = angular.copy(types);
		            $scope.ok = function() {
		                var inputs = {};
		                inputs.rerunDispositionSet = this.dispositionSet;
		                inputs.rerunResolutionTypes = this.resolutionTypes;
                $modalInstance.close(inputs);
            };

            $scope.cancel = function() {
                $modalInstance.dismiss('cancel');
            };
        }
		        
		        var formatDate = function(time) {
		        	return $filter('date')(new Date(time), 'EEE MMM dd HH:mm:ss UTC yyyy');
		        }
    }
]);
