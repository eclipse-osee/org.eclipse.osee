/**
 *  Cpa Analyze Controller
 */
angular.module('CpaApp').controller('AnalyzeCtrl',
		[ '$scope', 'CpaFactory', '$resource', '$window', '$modal', '$filter', function($scope, CpaFactory, $resource, $window, $modal, $filter) {

			CpaFactory.getConfig().$promise.then(function(data) {
				$scope.applicabilityEnum = [''].concat(data.applicabilityOptions);
			});
			
			$scope.projects = CpaFactory.getProjects();
			
			var idCellTmpl = '<button class="btn btn-default btn-sm" ng-disabled="!row.entity.decisionLocation" ng-click="openLink(row.entity.decisionLocation)">{{row.getProperty(col.field)}}</button>';
			
			var origCellTmpl = '<button class="btn btn-default btn-sm" ng-disabled="!row.entity.origPcrLocation" ng-click="openLink(row.entity.origPcrLocation)">{{row.getProperty(col.field)}}</button>';
				
			var dupCellTmpl = '<button class="btn btn-default btn-sm" ng-show="row.entity.duplicatedPcrLocation" ng-disabled="!row.entity.duplicatedPcrLocation" ng-click="openLink(row.entity.duplicatedPcrLocation)">{{row.getProperty(col.field)}}</button>';
				
			var applCellTmpl = '<div class="ngCellText" ng-class="col.colIndex()"><select ng-change="updateApplicability(row.entity)" ng-model="COL_FIELD" ng-options="o as o for o in applicabilityEnum"></select></div>';
			
			$scope.selectedItems = [];
			
			$scope.gridOptions = {data: 'items',
					enableHighlighting: true,
					selectedItems: $scope.selectedItems,
					showSelectionCheckbox: true,
					selectWithCheckboxOnly: true,
					enableColumnResize: true,
					showFilter: true,
					sortInfo: {fields: ['id'], directions: ['asc']},
					columnDefs: [{field: 'id', displayName: 'Id', width: 85, cellTemplate: idCellTmpl}, 
					             {field: 'pcrSystem', displayName: 'Type', width: 60},
					             {field: 'originatingPcr.programName', displayName: 'Original Project', width: 100, cellTemplate: origCellTmpl},
					             {field: 'originatingPcr.priority', displayName: 'Priority', width: 40},
					             {field: 'originatingPcr.title', displayName: 'Title', width: 300},
					             {field: 'applicability', displayName: 'Applicability', width: 70, cellTemplate: applCellTmpl},
					             {field: 'assignees', displayName: 'Assignees', width: 150},
					             {field: 'rationale', displayName: 'Rationale'},
					             {field: 'duplicatedPcrId', displayName: 'Duplicate PCR', width: 70, enableCellEdit: true, cellTemplate: dupCellTmpl},
					             {field: 'completedDate', displayName: 'Completed Date', width: 90},
					             {field: 'completedBy', displayName: 'Completed By', width: 100}]
			};
			
			$scope.$on('ngGridEventEndCellEdit', function(evt){
				// place holder for updating duplicated pcr id
			});
			
			$scope.updateProject = function() {
				if($scope.selectedProject) {
					$scope.items = null;
					var loadingModal = $scope.showLoadingModal();
					CpaFactory.getProjectCpas($scope.selectedProject).$promise.then(function(data){
						$scope.items = data;
						loadingModal.close();
					});
					CpaFactory.getVersions($scope.selectedProject).$promise.then(function(data){
						$scope.versions = data;
					});
				}
			}
			
			$scope.openLink = function(url) {
				$window.open(url);
			}
			
			 $scope.itemsSelected = function() {
				 return $scope.selectedItems.length;
			 }
			 
			 $scope.numAnalyzed = function() {
				 var count = 0;
				 angular.forEach($scope.items, function(item) {
					 count += item.applicability ? 1 : 0;
				 });
				 return count;
			 }
			 
			 $scope.getSelected = function(includeCompleted) {
				 var toUpdate = [];
				 for(var i = 0; i < $scope.selectedItems.length; i++) {
						 if(!$scope.selectedItems[i].applicability || (includeCompleted && $scope.selectedItems[i].applicability)) {
							 toUpdate.push($scope.selectedItems[i]);
						 } 
				 }
				 return toUpdate;
			 }
			 
			 $scope.updateApplicability = function(item) {
				 item.$selected = item.$selected && !item.applicability;
				 CpaFactory.updateApplicability(item, item.applicability);
			 }
			 
			 $scope.updateAssignees = function() {
				 var modalInstance = $modal.open({
				      templateUrl: 'editAssignees.html',
				      controller: AssigneeModalCtrl,
				      size: 'sm',
				      resolve: {
				        items: function () {
				          return CpaFactory.getUsers();
				        }
				      }
				    });
				 
				 modalInstance.result.then(function (selected) {
					 var toUpdate = $scope.getSelected();
				      CpaFactory.updateAssignees(toUpdate, selected);
				    });
			 }
			 
			 var AssigneeModalCtrl = function ($scope, $modalInstance, items) {
				  $scope.items = items;
				  
				  $scope.ok = function () {
					  var selected = [];
					  for(var i = 0; i < $scope.items.length; i++) {
						  if($scope.items[i].$selected) {
							  selected.push($scope.items[i]);
						  }
					  }
				    $modalInstance.close(selected);
				  };

				  $scope.cancel = function () {
				    $modalInstance.dismiss('cancel');
				  };
				};
				
				 $scope.updateRationale = function() {
					 var toUpdate = $scope.getSelected();
					 var existing = "";
					 if(toUpdate.length === 1) {
						 existing = toUpdate[0].rationale;
					 }
					 var modalInstance = $modal.open({
					      templateUrl: 'editRationale.html',
					      controller: RationaleModalCtrl,
					      resolve: {
					    	  existing: function () {
						          return existing;
						        }
						      }
					    });
					 
					 modalInstance.result.then(function (rationale) {
					     CpaFactory.updateRationale(toUpdate, rationale);
					    });
				 }
				 
				 var RationaleModalCtrl = function ($scope, $modalInstance, existing) {
					 
					 $scope.newRationale = {rationale: existing};

					  $scope.ok = function () {
					    $modalInstance.close($scope.newRationale.rationale);
					  };

					  $scope.cancel = function () {
					    $modalInstance.dismiss('cancel');
					  };
					};
					
				  $scope.updateApplicabilities = function() {
					  var toUpdate = $scope.getSelected(true);
						 var existing = "";
						 if(toUpdate.length === 1) {
							 existing = toUpdate[0].applicability;
						 }
						 var modalInstance = $modal.open({
						      templateUrl: 'editApplicability.html',
						      controller: ApplicabilityModalCtrl,
						      size: 'sm',
						      resolve: {
						    	  existing: function () {
							          return existing;
							        },
							        applicabilityEnum: function() {
							        	return $scope.applicabilityEnum;
							        }
							      }
						    });
						 
						 modalInstance.result.then(function (applicability) {
						     CpaFactory.updateApplicability(toUpdate, applicability);
						  });
				  };
				  
				  var ApplicabilityModalCtrl = function ($scope, $modalInstance, existing, applicabilityEnum) {
						 
						 $scope.applicabilityEnum = applicabilityEnum;
						 $scope.selected = {applicability: existing};

						  $scope.ok = function () {
						    $modalInstance.close($scope.selected.applicability);
						  };

						  $scope.cancel = function () {
						    $modalInstance.dismiss('cancel');
						  };
						};
						
				$scope.duplicate = function(templateUrl) {
							  var toUpdate = $scope.getSelected(true);
							  var toSend = [];
							  var ids = '';
							  var alreadySet = '';
							  for(var i = 0; i < toUpdate.length; i++) {
								  toSend.push(toUpdate[i]);
								  ids += toUpdate[i].id;
								  ids += ', ';
							  }
							  ids = ids.slice(0, -2);
							  alreadySet = alreadySet.slice(0, -2);
							  
								var modalInstance = $modal.open({
									templateUrl : templateUrl,
									controller : DuplicateModalCtrl,
									size : 'md',
									resolve : {
										ids : function() {
											return ids;
										},
										alreadySet : function() {
											return alreadySet;
										},
										versions: function() {
											return $scope.versions;
										}
									}
								});

							modalInstance.result.then(function(retVal) {
								if(templateUrl === 'enterPcr.html') {
								   CpaFactory.updateDuplicatedPcrId(toSend, retVal);
								} else {
									CpaFactory.duplicateIssue(toSend, $scope.selectedProject, retVal);
								}
							  });
							};

					var DuplicateModalCtrl = function($scope,
									$modalInstance, ids, alreadySet, versions) {
								$scope.message = ids;
								$scope.alreadySet = alreadySet;
								$scope.pcrInput = {value: ''};
								$scope.versions = versions;
								
								$scope.updateVersion = function(version) {
									$scope.version = version;
								}

								$scope.ok = function() {
									var retVal = $scope.version;
									if(!retVal) {
										retVal = $scope.pcrInput.value;
									}
									console.log(retVal);
									$modalInstance
											.close(retVal);
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
			
		} ]);