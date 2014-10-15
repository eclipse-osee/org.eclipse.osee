		app.controller('adminController', ['$scope', '$modal', 'Program', 'Set', 'Report',
		    function($scope, $modal, Program, Set, Report) {
		        $scope.readOnly = true;
		        $scope.programSelection = null;
		        $scope.modalShown = false;
		        $scope.primarySet = "";
		        $scope.secondarySet = "";
		        $scope.sets = [];

		        $scope.cachedValue = "";

		        $scope.$on('ngGridEventStartCellEdit', function(data) {
		            var field = data.targetScope.col.field;
		            $scope.cachedValue = data.targetScope.row.getProperty(field);
		        });

		        $scope.$on('ngGridEventEndCellEdit', function(data) {
		            var field = data.targetScope.col.field;
		            var row = data.targetScope.row;
		            var newValue = row.getProperty(field);

		            if ($scope.cachedValue != newValue) {
		                $scope.editSet(row.entity);
		            }
		        });



		        $scope.gridOptions = {
		            data: 'sets',
		            enableHighlighting: true,
		            enableColumnResize: false,
		            enableRowReordering: true,
		            multiSelect: false,
		            columnDefs: 'columnDefs' // link to scope variable which we will define dynamically				
		        }

		        var editCellTmpl = '<input editable="true" >'
		        var dellCellTmpl = '<button width="50px" class="btn btn-default btn-sm setDelete" ng-show="!readOnly" ng-click="deleteSet(row.entity)">X</button>';
		        var importCellTmpl = '<button width="50px" class="btn btn-primary" ng-disabled="row.entity.processingImport" ng-click="importSet(row.entity)">Import</button>';
		        var exportCellTmpl = '<button width="50px" class="btn btn-primary" ng-disabled="row.entity.processingImport" ng-click="exportSet(row.entity)">Export</button>';

		        $scope.columnDefs1 = [{
		            field: "",
		            displayName: "Import",
		            width: 70,
		            enableCellEdit: false,
		            cellTemplate: importCellTmpl
		        }, {
		        	field: "",
		        	displayName: "Export",
		        	width: 70,
		        	cellTemplate: exportCellTmpl
		        }, {
		            field: "name",
		            displayName: "Name",
		            width: 200,
		            enableCellEdit: false
		        }, {
		            field: "importPath",
		            displayName: "Path",
		            width: 460,
		            enableCellEdit: false
		        }];

		        $scope.columnDefs2 = [{
		            field: "",
		            displayName: "Import",
		            width: 70,
		            enableCellEdit: false,
		            cellTemplate: importCellTmpl
		        }, {
		        	field: "",
		        	displayName: "Export",
		        	width: 70,
		        	cellTemplate: exportCellTmpl
		        },{
		            field: "name",
		            displayName: "Name",
		            width: 200,
		            enableCellEdit: true
		        }, {
		            field: "importPath",
		            displayName: "Path",
		            width: 413,
		            enableCellEdit: true
		        }, {
		            field: "delete",
		            displayName: "Delete",
		            width: 57,
		            cellTemplate: dellCellTmpl
		        }];

		        $scope.columnDefs = $scope.columnDefs1;

		        $scope.programs = Program.query();

		        $scope.toggleModal = function() {
		            $scope.modalShown = !$scope.modalShown
		        };

		        $scope.toggleReadOnly = function() {
		            if ($scope.readOnly) {
		                $scope.columnDefs = $scope.columnDefs2;
		                $scope.readOnly = false;
		            } else {
		                $scope.columnDefs = $scope.columnDefs1;
		                $scope.readOnly = true;
		            }

		        };

		        $scope.generateReport = function() {
		        	var requst = [];
		        	requst.push(
		        	  "program/",
		        	  $scope.programSelection,
		        	  "/admin/report?primarySet=",
		        	  $scope.primarySet,
		        	  "&secondarySet=",
		        	  $scope.secondarySet
		        	  );
		        	var url = requst.join("");
		            
		            window.open(url);
		        }
		        
		        
		        
		        $scope.updateProgram = function updateProgram() {
		            $scope.loading = true;
		        	$scope.items = {};
		        	$scope.sets = {};
		            Set.query({
		                programId: $scope.programSelection
		            }, function(data) {
		                $scope.loading = false;
		                $scope.sets = data;
		            });
		        };

		        $scope.editSet = function editSet(set) {
		            Set.update({
		                programId: $scope.programSelection,
		                setId: set.guid
		            }, set);
		        };

		        $scope.deleteSet = function deleteSet(set) {
		            Set.delete({
		                programId: $scope.programSelection,
		                setId: set.guid
		            }, function() {
		                var index = $scope.sets.indexOf(set);
		                if (index > -1) {
		                    $scope.sets.splice(index, 1);
		                }
		            });

		        }


		        $scope.importSet = function importSet(set) {
		            var newSet = new Set;
		            newSet.operation = "Import";
		            set.processingImport = true;
		            Set.update({
		                programId: $scope.programSelection,
		                setId: set.guid
		            }, newSet, function(){
		            	set.processingImport = false;
		            }, function() {
		            	set.processingImport = false;
		            	alert("Could not Import");
		            });
		        };
		        
		        $scope.exportSet = function importSet(set) {
		        	var requst = [];
		        	requst.push(
		        	  "program/",
		        	  $scope.programSelection,
		        	  "/admin/export?primarySet=",
		        	  set.guid,
		        	  "&option=detailed"
		        	  );
		        	var url = requst.join("");
		            
		            window.open(url);
		        };

		        $scope.createNewSet = function createNewSet(name, path, type) {
		            if (name != "" && path != "") {
		                var newSet = new Set;
		                newSet.name = name;
		                newSet.importPath = path;
		                newSet.dispoType = type;
		                newSet.$save({
		                    programId: $scope.programSelection
		                }, function() {
		                    $scope.sets.push(newSet);
		                });
		            }
		        };

		        $scope.createNewSetModal = function() {
		            var modalInstance = $modal.open({
		                templateUrl: 'popup.html',
		                controller: CreateSetModalCtrl,
		                size: 'sm',
		                windowClass: 'createSetModal'
		            });

		            modalInstance.result.then(function(inputs) {
		                $scope.createNewSet(inputs.name, inputs.path, inputs.dispoType);
		            });
		        }

		        var CreateSetModalCtrl = function($scope, $modalInstance) {
		            $scope.setName = "";
		            $scope.importPath = "";
		            $scope.dispoType = "";

		            $scope.ok = function() {
		                var inputs = {};
		                inputs.name = this.setName;
		                inputs.path = this.importPath;
		                inputs.dispoType = "testScripts";
		                $modalInstance.close(inputs);
		            };

		            $scope.cancel = function() {
		                $modalInstance.dismiss('cancel');
		            };
		        };

		    }
		]);