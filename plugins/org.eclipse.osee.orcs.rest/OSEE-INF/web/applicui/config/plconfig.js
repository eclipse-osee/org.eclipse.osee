var app = angular.module('app', [ 'ngMessages', 'checklist-model', 'ngResource', 'ui.grid',
      'ui.grid.resizeColumns', 'ui.grid.selection', 'ui.grid.cellNav', 'ui.grid.edit' ]);

app
      .controller(
            "appCtrl",
            [
                  '$scope',
                  '$http',
                  '$resource',
                  '$timeout',
                  function($scope, $http, $resource, $timeout) {
                	  $scope.elem = {
                  	        top: 98,
                  	        left: 357
                  	      };
                	  $scope.branchQueryType = 'all';
                	  $scope.selectedViewOption = 'productAction';
                	  $scope.selectedFeatureOption = 'featureAction';
                	  $scope.selectedActionOption = 'actionAction';
                      $scope.teamWf = {};
                      $scope.events = [];
                      $scope.elements = [];
                     $scope.selectedBranch = {};
                     $scope.selectedVersion = {};
                     $scope.selectedAI = {};
                     $scope.selectedUser = {};
                     $scope.selectedToState = '';
                     $scope.transitionToStates = [];
                     $scope.users = [];
                     $scope.actionableitems = [];
                     $scope.versions = [];
                     $scope.branches = [];
                     $scope.selectedBranch.id = getQueryParameterByName('branch');
                     $scope.itemsGridOptions = [];
                     $scope.itemsGridOptions.data = [];
                     $scope.showAll = getQueryParameterByName('showAll'); 
                     $scope.feature = {};
                     $scope.view = {};
                     $scope.htmlFeatureColumns = false;
                     $scope.branchQueryType = getQueryParameterByName('branchQueryType');
                     // HTML boolean to show/hide elements
                     $scope.resetHtmlVarsAndFlags = function() {
                    	 $scope.elem.top = 98;
                    	 $scope.elem.left = 357;
                         $scope.edit = {};

                   	  $scope.selectedViewOption = 'productAction';
                   	  $scope.selectedFeatureOption = 'featureAction';
                	  $scope.selectedActionOption = 'actionAction';
                       //Create Action flags and vars
                         $scope.action = {};
                         $scope.htmlAISelect = false;
                         $scope.htmlUserSelect = false;
                         $scope.htmlVersionSelect = false;
                         $scope.htmlActionPane = false;
                         $scope.htmlActionAdd = false;
                         $scope.htmlActionSave = false;
                         $scope.htmlTransitionPane = false;
                         $scope.htmlTransition = false;
                         $scope.htmlTransitionSelect = false;
                         $scope.htmlTransitionSave = false;
                         $scope.htmlCommitWorkingBranchPane = false;
                         $scope.htmlCommitWorkingBranch = false;
                         $scope.htmlCommitWorkingBranchSave = false;
                         // ///// view flags and
                            // vars
                         $scope.view = {};
                         $scope.validatePatternView = '^[A-Za-z0-9 ]+$';
                         // Main Edit Container
                         $scope.htmlViewPane = false;
                         // For Add/Edit/Delete
                         $scope.htmlViewAction = null;
                         $scope.htmlViewEdit = false;
                         $scope.htmlViewAdd = false;
                         $scope.htmlViewDelete = false;
                         // Widgets
                         $scope.htmlViewSelect = false;
                         $scope.htmlViewSave = false;
                         $scope.htmlViewCopyFrom = false;
                         $scope.htmlViewAddTitle = false;
                         $scope.htmlViewEditTitle = false;
                         $scope.htmlViewDeleteTitle = false;
                         
                         // ///// FEATURE flags and
                            // vars
                         $scope.feature = {};
                         $scope.featureTitle = '';
                         $scope.validatePatternFeature = '^[A-Z0-9_]+$';
                         // Main Edit Container
                         $scope.htmlFeaturePane = false;
                         // For Add/EditDelete
                         $scope.htmlFeatureAction = null;
                         $scope.htmlFeatureEdit = false;
                         $scope.htmlFeatureAdd = false;
                         $scope.htmlFeatureDelete = false;
                         // Widgets
                         $scope.htmlFeatureSelect = false;
                         $scope.htmlFeatureSave = false;
                         $scope.htmlFeatureEditTitle = false;
                         $scope.htmlFeatureAddTitle = false;
                         $scope.htmlFeatureDeleteTitle = false;
                         //clear errors
                         $scope.showErrorLabel = false;
                         $scope.errorMsg = '';
                         
                     }
                     $scope.resetHtmlVarsAndFlags();

                     $scope.updateBranchQueryType = function() {
                    	 $scope.selectedBranch = {};
                    	 $scope.loadBranches();
                     }
                     // //////////////////////////////////////
                     // Load branch combo regardless of which "page"
                     // //////////////////////////////////////
                     $scope.loadBranches = function() {
                    	 if (!$scope.branchQueryType) {
                    		 $scope.branchQueryType = 'all';
                    	 }
                    	 if($scope.branchQueryType == 'all') {
                            $http.get('/orcs/applicui/branches').then(
                              function(response) {
                                 $scope.branches = response.data;
                                 $scope.message = '';
                                 $scope.setSelectedBranch();
                              });
                    	 } else {
                    		 $http.get('/orcs/applicui/branches/'+$scope.branchQueryType).then(
                                     function(response) {
                                        $scope.branches = response.data;
                                        $scope.message = '';
                                        $scope.setSelectedBranch();
                                     });
                    	 }
                         
                     }
                     $scope.loadBranches();

                     // //////////////////////////////////////
                     // Set selected branch
                     // //////////////////////////////////////
                     $scope.setSelectedBranch = function() {
                        if ($scope.selectedBranch.id) {
                           for (x = 0; x < $scope.branches.length; x++) {
                              var branch = $scope.branches[x];
                              var id = branch.id;
                              id = id.replace(/"/g, ""); // remove
                              // all
                              // quotes
                              if (id == $scope.selectedBranch.id) {
                                 $scope.selectedBranch = branch;
                                 break;
                              }
                           }
                        }
                     }

                     // //////////////////////////////////////
                     // Handle branch selection
                     // //////////////////////////////////////
                     $scope.handleBranchSelection = function() {
                        if (!$scope.selectedBranch) {
                           $scope.message = 'Must Select a Branch';
                        } else {
                           $scope.message = 'Selected branch '
                                 + $scope.selectedBranch;
//                           var url = '/orcs/applicui/config/plconfig.html?branch='
//                                 + $scope.selectedBranch.id;
//                           
//                           window.location.replace(url);
                             $scope.updateUrl();
                        }
                     }
                     
                     $scope.updateUrl = function () {
                    	 var url = '/orcs/applicui/config/plconfig.html';
                    	 
                    	 if ($scope.selectedBranch) {
                    		 url = url + '?branchQueryType='+$scope.branchQueryType+'&branch=' + $scope.selectedBranch.id
                    		 
                    		 if (getQueryParameterByName('showAll')) {
                    			 url = url + '&showAll=true';
                    		 }
                    	 };
                    	 window.location.replace(url);
                     }
                     
                  // //////////////////////////////////////
                     // Handle branch selection READ ONLY
                     // //////////////////////////////////////
                     $scope.handleBranchSelectionRO = function() {
                        if (!$scope.selectedBranch) {
                           $scope.message = 'Must Select a Branch';
                        } else {
                           $scope.message = 'Selected branch '
                                 + $scope.selectedBranch;
                           var url = '/orcs/applicui/config/plconfigro.html?branch='
                                 + $scope.selectedBranch.id;
                           window.location.replace(url);
                        }
                     }

                     // //////////////////////////////////////
                     // Determine branch access and refresh label
                     // //////////////////////////////////////
                     $scope.refreshAccess = function() {
                	 var url = '/orcs/branch/'
                              + $scope.selectedBranch.id
                              + '/applic/access';
                           $http.get(url) .then(
                               function(response) {
                                   if (response.data.errors) {
                                       //$scope.accessLevel = "(Read-Only)";
                                       $scope.isReadOnly = true;
                                       //document.getElementById("selectedBranchField").style.color = "yellow";
                                    } else {
                                       //$scope.accessLevel = "";
                                       $scope.isReadOnly = false;
                                       //document.getElementById("selectedBranchField").style.color = "blue";
                                    }
                               });
                     }
                     // //////////////////////////////////////
                     // Load users combo 
                     // //////////////////////////////////////
                     //http://<server>/ats/user
                     $scope.loadUsers = function() {
                        $http.get('/ats/user').then(
                              function(response) {
                                 $scope.users = response.data;
                                 $scope.message = '';
                              });
                     }
                     $scope.loadUsers();
                     // //////////////////////////////////////
                     // Handle User selection
                     // //////////////////////////////////////
                     $scope.handleUserSelection = function() {
                        if (!$scope.selectedUser) {
                           $scope.message = 'Must Select Originator';
                        } else {
                           $scope.message = 'Selected Originator: '
                                 + $scope.selectedUser;
                        }
                     }
                     // //////////////////////////////////////
                     // Load actionable item combo 
                     // //////////////////////////////////////
                     //http://<server>/ats/ui/action/actionableitems
                     $scope.loadAIs = function() {
                        $http.get('/ats/ai').then(
                              function(response) {
                                 $scope.actionableitems = response.data;
                                 $scope.message = '';
                              });
                     }
                     $scope.loadAIs();

                     // //////////////////////////////////////
                     // Handle AI selection
                     // //////////////////////////////////////
                     $scope.handleAISelection = function() {
                        if (!$scope.selectedAI) {
                           $scope.message = 'Must Select a Actionable Item';
                        } else {
                           $scope.message = 'Selected Actionable Item: '
                                 + $scope.selectedAI;
                           $scope.loadVersions();
                        }
                     }
        
                     
                  // //////////////////////////////////////
                     // Load version combo regardless of which "page"
                     // //////////////////////////////////////
                     //http://<server>/ats/teamwf/<actionableItemId>/version
                     $scope.loadVersions = function() {
                        $http.get('/ats/teamwf/'+$scope.selectedAI.id+'/version').then(
                              function(response) {
                                 $scope.versions = response.data;
                                 $scope.message = '';
                              });
                     }
                  // //////////////////////////////////////
                     // Load transitionTo
                     // //////////////////////////////////////
                     //http://localhost:8089/ats/action/201204/TransitionToStates
                     $scope.loadTransitionStates = function() {
                        $http.get('/ats/action/'+$scope.actionId+'/TransitionToStates').then(
                              function(response) {
                                 $scope.transitionToStates = response.data;
                                 $scope.message = '';
                              });
                     }
                     
                     // /////////////////////////////////////////////////////////////////////
                     // View METHODS
                     // /////////////////////////////////////////////////////////////////////

                     $scope.doSelectedViewAction = function() {
                 		switch($scope.selectedViewOption) {
                 		case "add":
                 			$scope.handleAddView();
                 			break;
                 		case "edit":
                 			$scope.handleEditView();
                 			break;
                 		case "delete":
                 			$scope.handleDeleteView();
                 			break;
                 		}
                 	}
                      
                     // //////////////////////////////////////
                     // Handle Add View
                     // //////////////////////////////////////
                     $scope.handleAddView = function() {
                    	 $scope.resetHtmlVarsAndFlags();
                    	 if (!$scope.selectedBranch.id) {
                    		 $scope.error("Must select a branch");
                    		 return;
                    	 }
                    	 if ($scope.isReadOnly) {
                    		 $scope.error("Selected Branch cannot be modified.  Choose a working branch or create one.");
                    		 return;
                    	 }
                    	 
                    	 $scope.htmlViewAction = "add";
                        $http
                        .get(
                              '/orcs/applicui/branch/'
                                    + $scope.selectedBranch.id)
                        .then(
                              function(response) {
                                 var config = response.data;
                                 $scope.htmlViewPane = true;
                                 $scope.htmlViewAdd = true;
                                 $scope.htmlViewCopyFrom = true;
                                 $scope.htmlViewSave = true;
                                 $scope.views = config.views;
                              });
                    	 
                     }

                     // //////////////////////////////////////
                     // Handle Edit view
                     // //////////////////////////////////////
                     $scope.handleEditView = function() {
                    	 $scope.resetHtmlVarsAndFlags();

                    	 if (!$scope.selectedBranch.id) {
                    		 $scope.error("Must select a branch");
                    		 return;
                    	 }
                    	 if ($scope.isReadOnly) {
                    		 $scope.error("Selected Branch cannot be modified.  Choose a working branch or create one.");
                    		 return;
                    	 }
                    	 $scope.htmlViewAction = "edit";
                        $http
                        .get(
                              '/orcs/applicui/branch/'
                                    + $scope.selectedBranch.id)
                        .then(
                              function(response) {
                            	  var config = response.data;
                            	  $scope.htmlViewPane = true;
                            	  $scope.htmlViewEdit = true;
                            	  if (!$scope.view.id) {
                            		  $scope.htmlViewSelect = true;
                            		  }
                            	  $scope.htmlViewEditTitle = true;
                            	  $scope.htmlViewSave = true;
                            	  $scope.htmlViewCopyFrom = true;
                            	  $scope.views = config.views;
                            	  }
                              );
                     }                     
                     // //////////////////////////////////////
                     // Handle Edit view - Values
                     //
                     // Open edit pane 
                     // //////////////////////////////////////
                     $scope.handleEditViewSelect = function() {
                     	// Same select widget used for both; return if deleting
                     	if ($scope.htmlViewAction == "delete") {
                     		return;
                     	}
                     	var view = $scope.view;
                        $scope.htmlViewPane = true;
                        $scope.htmlViewEditTitle = true;
                        $scope.htmlViewSelect = true;
                        $scope.htmlViewEdit = true;
                        $scope.htmlViewSave = true;
                        $scope.htmlCopyFrom=true;
                     }
                     // //////////////////////////////////////
                     // Cancel View Edit
                     // //////////////////////////////////////
                     $scope.cancelViewEdit = function() {
                    	 $scope.htmlViewAction = null;
                        $scope.resetHtmlVarsAndFlags();
                     }


                     // //////////////////////////////////////
                     // Save View Edit
                     // //////////////////////////////////////
                     $scope.saveViewEdit = function() {

                         if (!$scope.view.name) {
                         	$scope.error("View title is required")
                         	return;
                         }
                         if (!$scope.view.copyFrom) {
                         	$scope.error("Copy From is required");
                         	return;
                         }
                        var view = $scope.view;
                        var action = $scope.htmlViewAction;
                        var url = '/orcs/branch/'
                              + $scope.selectedBranch.id
                              + '/applic/view';
                        
                        var json = JSON.stringify(view); 
                        if ($scope.htmlViewAction == "add") {
	                        $http.post(url, json).then(function(response) {
	                            if (response.data.errors) {
	                               $scope.error(response.data.results);
	                            } else {
	                               $scope.loadTable();
	                            }
	                            $scope.resetHtmlVarsAndFlags();
	                         }) 
	                    } else {
	                        $http.put(url, json).then(function(response) {
	                           if (response.data.errors) {
	                              $scope.error(response.data.results);
	                           } else {
	                              $scope.loadTable();
	                           }
	                           $scope.resetHtmlVarsAndFlags();
	                        })
	                     }
                     }

                     // //////////////////////////////////////
                     // Handle Delete View
                     // //////////////////////////////////////
                     $scope.handleDeleteView = function(view) {
                    	 $scope.resetHtmlVarsAndFlags();

                    	 if (!$scope.selectedBranch.id) {
                    		 $scope.error("Must select a branch");
                    		 return;
                    	 }
                    	 if ($scope.isReadOnly) {
                    		 $scope.error("Selected Branch cannot be modified.  Choose a working branch or create one.");
                    		 return;
                    	 }
                    	 $scope.htmlViewAction = "delete";
                        $http
                        .get(
                              '/orcs/applicui/branch/'
                                    + $scope.selectedBranch.id)
                        .then(
                              function(response) {
                               if (response.data.errors) {
                                  $scope.error(response.data.results);
                               } else {
                                     var config = response.data;
                                     $scope.htmlViewPane = true;
                                     $scope.htmlViewSelect = true;
                                     $scope.htmlViewDelete = true;
                                     $scope.htmlViewAction = "delete";
                                     $scope.views = config.views;
                               }
                         });
                     }

                     // //////////////////////////////////////
                     // Save View Delete
                     // //////////////////////////////////////
                     $scope.saveViewDelete = function(view) {
                        if (!view || !view.id) {
                           view = $scope.view;
                        } 
                        if (!view.name) {
                           view.name = $scope.getViewById(view.id).name; 
                        }
                        if (confirm("Delete View ["+view.name+"]\n\nAre you sure?")) {
                           var url = '/orcs/branch/'
                              + $scope.selectedBranch.id
                              + '/applic/view/' + view.id;
                           $http.delete(url).then(
                                 function(response) {
                                    if (response.data.errors) {
                                       $scope.error(response.data.results);
                                    } else {
                                       $scope.loadTable();
                                    }
                                    $scope.resetHtmlVarsAndFlags();
                                 });
                        }
                     }

                  // /////////////////////////////////////////////////////////////////////
                     // Action METHODS
                     // /////////////////////////////////////////////////////////////////////
                     
                     $scope.doSelectedActionAction = function() {
                  		switch($scope.selectedActionOption) {
                  		case "create":
                  			$scope.handleActionAdd();
                  			break;
                  		case "transition":
                  			$scope.handleTransition();
                  			break;
                  		case "commitBranch":
                  			$scope.handleCommitWorkingBranch();
                  			break;
                  		}
                  	}
                     
                     $scope.handleActionAdd = function() {
                    	 $scope.resetHtmlVarsAndFlags();
                    	 if (!$scope.selectedBranch.id) {
                    		 $scope.error("Must select a branch.");
                    		 return;
                    	 }
                    	 if (!$scope.isReadOnly) {
                    		 $scope.error("This is a working branch, please choose a baseline branch.");
                    		 return;
                    	 }
                        $http
                        .get(
                              '/orcs/applicui/branch/'
                                    + $scope.selectedBranch.id)
                        .then(
                              function(response) {
                                 var config = response.data;
                                 $scope.htmlActionPane = true;
                                 $scope.htmlActionAdd = true;
                                 $scope.htmlActionSave = true;
                                 $scope.htmlAISelect = true;
                                 $scope.htmlUserSelect = true;
                                 $scope.htmlVersionSelect = true;
                              });
                     }
 
                     // //////////////////////////////////////
                     // Cancel Action Add 
                     // //////////////////////////////////////
                     $scope.cancelActionAdd = function() {
                    	 $scope.htmlActionAdd = null;
                        $scope.resetHtmlVarsAndFlags();
                     }


                     // //////////////////////////////////////
                     // Save Action Add
                     // //////////////////////////////////////
                     $scope.saveActionAdd = function() {
                        var action = $scope.action;
                        if (!$scope.selectedAI.id) {
                        	$scope.error("Actionable Item is required");
                        	return;
                        }
                        if (!$scope.action.title) {
                        	$scope.error("Action title is required");
                        	return;
                        }
                        if (!$scope.action.description) {
                        	$scope.error("Description is required");
                        	return;
                        }
                        if (!$scope.selectedUser.id) {
                        	$scope.error("User is required");
                        	return;
                        }
                        if (!$scope.selectedVersion.id) {
                        	$scope.error("Version is required");
                        	return;
                        }
                        var aiIdsParam = []; 
                        aiIdsParam.push($scope.selectedAI.id);
                        
                        var actionData = { title: $scope.action.title,
                        		           description: $scope.action.description,
                        		           aiIds: aiIdsParam,
                        		           asUserId: $scope.selectedUser.id,
                        		           createdByUserId: $scope.selectedUser.id,
                        		           versionId: $scope.selectedVersion.id }
                        var url = '/ats/action/branch';
                        
                        var json = JSON.stringify(actionData);
                        
                        $http.post(url, json).then(function(response) {
                           if (response.data.errors) {
                              $scope.error(response.data.results);
                           } else {
                              $scope.selectedBranch = response.data.workingBranchId;
                              $scope.branchQueryType = 'working';
                              $scope.handleBranchSelection();
                           }
                           $scope.resetHtmlVarsAndFlags();
                        });
                     }

			// /////////////////////////////////////////////////////////////////////
			// Transition METHODS
			// /////////////////////////////////////////////////////////////////////
                     
			$scope.handleTransition = function() {
			   $scope.resetHtmlVarsAndFlags();
           	   if (!$scope.selectedBranch.id) {
           		 $scope.error("Must select a branch");
           		 return;
           	   }
			   if ($scope.isReadOnly) {
				   $scope.error("Selected branch is read only");
				   return;
			   }
			   if (!$scope.actionId || $scope.actionId < 1) {
				   $scope.error("Selected branch does not have an associated action to transition");
				   return;
			   }
			   if ($scope.actionId) {
              	 $http.get('/ats/action/'+$scope.actionId)
              	 .then(
                       function(response) {
                              var rtn = response.data[0];
                              $scope.atsId = rtn.AtsId;
                       }
                       );
               }
			   $http
			   .get(
			         '/orcs/applicui/branch/'
			               + $scope.selectedBranch.id)
			   .then(
			         function(response) {
			            var config = response.data;
			            $scope.htmlTransitionPane = true;
			            $scope.htmlTransition = true;
			            $scope.htmlTransitionSave = true;
			            $scope.htmlTransitionSelect = true;
			            $scope.htmlUserSelect = true;
			         });
			   $scope.loadTransitionStates();
			}
			
			// //////////////////////////////////////
			// Cancel Transition 
			// //////////////////////////////////////
			$scope.cancelTransition = function() {
				 $scope.htmlTransition = null;
				 $scope.error("");
			   $scope.resetHtmlVarsAndFlags();
			}
			
			
			// //////////////////////////////////////
			// Save Transition
			// //////////////////////////////////////
			$scope.validateTransition = function() {
				if ($scope.selectedToState && $scope.actionId && $scope.selectedUser.id) {
					

					var workItemIdsParam = []; 
					var workItem = { id: $scope.actionId, name: ""};
	                workItemIdsParam.push(workItem);
					   var transitionData = { toStateName: $scope.selectedToState,
					   		           name: "Transition to " + $scope.selectedToState,
					   		           transitionUserArtId: $scope.selectedUser.accountId,
					   		           workItemIds: workItemIdsParam }
					   
					   var validateUrl = '/ats/action/transitionValidate';
					   var json = JSON.stringify(transitionData);
					   
					   $http.post(validateUrl, json).then(function(response) {
						      
						      if (response.data.empty) {
						    	  $scope.handleSaveTransition();
						    	  
						      } else {
						    	  var results = response.data.transitionWorkItems[0].results[0];
						    	  $scope.error(results.details);
						      }
						   });
				} else {
					   if (!$scope.selectedToState) {
						   $scope.error("Must selected a Transition To State");
					   }
					   if (!$scope.selectedUser.id) {
						   $scope.error("Must selected transition user");
					   }
					   if (!$scope.actionId) {
						   $scope.error("Error occurred with associated action Id.  Contact Admin");
					   }
				   }
			}
			$scope.handleSaveTransition = function() {
				

				var workItemIdsParam = []; 
				var workItem = { id: $scope.actionId, name: ""};
                workItemIdsParam.push(workItem);
				   var transitionData = { toStateName: $scope.selectedToState,
				   		           name: "Transition to " + $scope.selectedToState,
				   		           transitionUserArtId: $scope.selectedUser.accountId,
				   		           workItemIds: workItemIdsParam }
			   		  var json = JSON.stringify(transitionData);
					  var url = '/ats/action/transition';
					  $http.post(url, json).then(function(response) {
					      if (response.data.errors) {
					         $scope.error(response.data.results);
					      } else {
					   	   var results = response.data.results;
					      }
					      $scope.resetHtmlVarsAndFlags();
					   })
				  
			   
			}

					// /////////////////////////////////////////////////////////////////////
					// Commit Branch METHODS
					// /////////////////////////////////////////////////////////////////////
					         
					$scope.handleCommitWorkingBranch = function() {
					   $scope.resetHtmlVarsAndFlags();
						   if (!$scope.selectedBranch.id) {
							 $scope.error("Must select a branch");
							 return;
						   }
					   if ($scope.isReadOnly) {
						   $scope.error("Selected branch is read only");
						   return;
					   }
					   if (!$scope.config.parentBranch) {
						   $scope.error("Selected branch does not have a parent branch");
						   return;
					   }
					   $http
					   .get(
					         '/orcs/applicui/branch/'
					               + $scope.selectedBranch.id)
					   .then(
					         function(response) {
					            var config = response.data;
					            $scope.htmlCommitWorkingBranchPane = true;
					            $scope.htmlCommitWorkingBranch = true;
					            $scope.htmlCommitWorkingBranchSave = true;
					            $scope.htmlUserSelect = true;
					         });
					}
					
					// //////////////////////////////////////
					// Cancel Commit 
					// //////////////////////////////////////
					$scope.cancelCommitWorkingBranch = function() {
						 $scope.htmlCommitWorkingBranch = null;
						 $scope.error("");
					   $scope.resetHtmlVarsAndFlags();
					}
					
					
					// //////////////////////////////////////
					// Save Commit Working Branch
					// //////////////////////////////////////
					$scope.handleSaveCommitWorkingBranch = function() {
					   if ($scope.selectedBranch.id && $scope.config.parentBranch.id && $scope.selectedUser.id) {
						   var branchCommitOptions = { committer: $scope.selectedUser.id,
						   		           archive: false}
						   var url = '/orcs/branches/'+$scope.selectedBranch.id+'/commit/'+$scope.config.parentBranch.id;
						   
						   var json = JSON.stringify(branchCommitOptions);
						   var branchCommitted = false;
						   $http.post(url, json).then(function(response) {
						      if (response.data == null) {
						         $scope.error("Branch Commit Failed");
						         
						      } else {
						   	     branchCommitted = true;
						   	     $scope.selectedBranch.id = response.data.branchId;
	                             $scope.branchQueryType = 'baseline';
	                             $scope.handleBranchSelection();
						      }
						      $scope.resetHtmlVarsAndFlags();
						   });
						   if (branchCommitted) {
							   $scope.selectedToState = 'Completed';
							   $scope.handleSaveTransition();
						   }
					   } else {
						   
						   if (!$scope.selectedUser.id) {
							   $scope.error("Must selected commit user");
						   }
						   
					   }
					}
                     
                     // /////////////////////////////////////////////////////////////////////
                     // FEATURE METHODS
                     // /////////////////////////////////////////////////////////////////////

					$scope.doSelectedFeatureAction = function() {
						switch($scope.selectedFeatureOption) {
						case "add":
							$scope.handleAddFeature();
							break;
						case "edit":
							$scope.handleEditFeature();
							break;
						case "delete":
							$scope.handleDeleteFeature();
							break;
						}
					}
                     // //////////////////////////////////////
                     // Handle Add Feature
                     // //////////////////////////////////////
                     $scope.handleAddFeature = function() {
                    	 $scope.resetHtmlVarsAndFlags();

                    	 if (!$scope.selectedBranch.id) {
                    		 $scope.error("Must select a branch");
                    		 return;
                    	 }
                    	 if ($scope.isReadOnly) {
                    		 $scope.error("Selected Branch cannot be modified.  Choose a working branch or create one.");
                    		 return;
                    	 }
                     	 $scope.htmlFeatureAction = "add";
                    	 $http
                    	 .get(
                    			 '/orcs/applicui/branch/'+$scope.selectedBranch.id)
                    	 .then( 
                    		   function(response) {
                    		     var config = response.data;
                        	     $scope.htmlFeaturePane = true;
                        	     $scope.htmlFeatureEdit = true;
                        	     $scope.htmlFeatureAddTitle = true;
                        	     $scope.htmlFeatureSave = true;
                        	     $scope.htmlFeatureSelect = false;
                    	 })
                     }

                     // //////////////////////////////////////
                     // Handle Edit Feature - Select
                     //
                     // Open edit pane with select and populate with features
                     // //////////////////////////////////////
                     
                     $scope.handleEditFeatureSelect = function() {

                    	$scope.htmlFeatureAction = "edit";
                        $scope.feature.valueStr = $scope.feature.values.join();
                     }
                     
                     // //////////////////////////////////////
                     // Handle Edit Feature
                     //
                     // Open edit pane and populate with selected feature
                     // //////////////////////////////////////
                     $scope.handleEditFeature = function() {
                     	$scope.resetHtmlVarsAndFlags();

                    	 if (!$scope.selectedBranch.id) {
                    		 $scope.error("Must select a branch");
                    		 return;
                    	 }
                    	 if ($scope.isReadOnly) {
                    		 $scope.error("Selected Branch cannot be modified.  Choose a working branch or create one.");
                    		 return;
                    	 }                        
                      	 $scope.htmlFeaturePane = true;
                         $scope.htmlFeatureEditTitle = true;
                         $scope.htmlFeatureSelect = true;
                         $scope.htmlFeatureEdit = true;
                         $scope.htmlFeatureSave = true;
                     	if ($scope.htmlFeatureAction == "delete") {
                     		return;
                     	}
                     	if ($scope.feature.id) {
                        $http
                              .get(
                                    '/orcs/branch/'
                                          + $scope.selectedBranch.id
                                          + '/applic/feature/'
                                          + feature.id)
                              .then(
                                    function(response) {

                                       
                                       $scope.feature = response.data;
                                       $scope.feature.valueStr = feature.values.join();
                                       
                                    });
                     	} else {
                     		$scope.features = $scope.config.features;
                     	}
                    	 
                     }
                     
                     // //////////////////////////////////////
                     // Save Feature Edit
                     // //////////////////////////////////////
                     $scope.saveFeatureEdit = function() {

                         var feature = $scope.feature;
                    	if (!$scope.feature.name) {
                        	$scope.error("Feature title is required");
                        	return;
                        }
                        if (!$scope.feature.description) {
                        	$scope.error("Feature Description is required");
                        	return;
                        }
                        if (!$scope.feature.valueStr) {
                        	$scope.error("Values are required");
                        	return;
                        }
                        if (!$scope.feature.defaultValue) {
                        	$scope.error("Default_value is required");
                        	return;
                        }
                        if (!$scope.feature.valueType) {
                        	$scope.error("Value Type is required");
                        	return;
                        }
                        
                        if (feature.valueStr) {
                           feature.values = feature.valueStr
                                 .split(",");
                           //feature.valueStr = "";
                        }

                        var url = '/orcs/branch/'
                              + $scope.selectedBranch.id
                              + '/applic/feature';
                        var json = JSON.stringify(feature);
                        if ($scope.htmlFeatureAction == "add") {
                        	$http.post(url, json).then(function(response) {
                                if (response.data.errors) {
                                    $scope.error(response.data.results);
                                } else {
                                    $scope.loadTable();
                                    alert("Saved new Feature.  Add another or press Cancel");
                                }
                             })
                     	} else {
                        $http.put(url, json).then(function(response) {
                           if (response.data.errors) {
                               $scope.error(response.data.results);
                           } else {
                               $scope.loadTable();
                               alert("Saved edited Feature.  Edit another or press Cancel");
                           }
                        })};
                    	 
                     }

                     // //////////////////////////////////////
                     // Cancel Feature
                     // //////////////////////////////////////
                     $scope.cancelFeatureEdit = function() {
                        $scope.resetHtmlVarsAndFlags();
                     }
                    
                     // //////////////////////////////////////
                     // Handle Delete Feature
                     // //////////////////////////////////////
                     $scope.handleDeleteFeature = function(view) {
                    	 $scope.resetHtmlVarsAndFlags();

                    	 if (!$scope.selectedBranch.id) {
                    		 $scope.error("Must select a branch");
                    		 return;
                    	 }
                    	 if ($scope.isReadOnly) {
                    		 $scope.error("Selected Branch cannot be modified.  Choose a working branch or create one.");
                    	 } else {
                    	 
                        $http
                        .get(
                              '/orcs/applicui/branch/'
                                    + $scope.selectedBranch.id)
                        .then(
                              function(response) {
                                   if (response.data.errors) {
                                      $scope.error(response.data.results);
                                   } else {
                                         var config = response.data;
                                         $scope.htmlFeaturePane = true;
                                         $scope.htmlFeatureSelect = true;
                                         $scope.htmlFeatureDelete = true;
                                         $scope.htmlFeatureAction = "delete";
                                         $scope.htmlFeatureDeleteTitle = true;
                                         $scope.features = config.features;
                                   }
                              });
                    	 }
                     }

                     // //////////////////////////////////////
                     // Save Feature Delete
                     // //////////////////////////////////////
                     $scope.saveFeatureDelete = function() {
                       var feature = $scope.feature;
                       if (confirm("Delete Feature ["+feature.name+"]\n\nAre you sure?")) {
                          $http
                          .delete(
                                '/orcs/branch/'
                                      + $scope.selectedBranch.id
                                      + '/applic/feature/'
                                      + feature.id)
                          .then(
                                function(response) {
                                   if (response.data.errors) {
                                      $scope.error(response.data.results);
                                   } else {
                                      $scope.loadTable();
                                   }
                                });
                                  $scope.resetHtmlVarsAndFlags();
                       }
                        $scope.resetHtmlVarsAndFlags();
                     }
                     
                     // //////////////////////////////////////
                     // Handle Show All Feature Columns
                     // //////////////////////////////////////
                     $scope.handleShowAllFeatureColumns = function() {
                	 if ($scope.showAll) {
                	     $scope.showAll = false;
                	 } else {
                	     $scope.showAll = true;
                	 }
                	 var url = window.location.href;
                	 if ($scope.showAll) {
                            url = window.location.href+"&showAll=true"
                	 } else {
                	    url = url.replace("&showAll=true",""); 
                	 }
                         window.location.replace(url);
                     }

                     $scope.refreshShowAllLabel = function() {
                	 if ($scope.showAll) {
                	     $scope.showAllLabel = "Hide Extra Feature Columns";
                	 } else {
                	     $scope.showAllLabel = "Show All Feature Columns";
                	 }
                     }

                     // //////////////////////////////////////
                     // Load Table if selectedBranch.id
                     // //////////////////////////////////////
                     if ($scope.selectedBranch.id) {
                        
                        $scope.loadTable = function() {
                           var url = null;
                           if ($scope.showAll) {
                              url = '/orcs/applicui/branch/'
                                  + $scope.selectedBranch.id  
                                  + '?showAll=true';
                           } else {
                              url = '/orcs/applicui/branch/'
                                  + $scope.selectedBranch.id;
                           }
                           $http.get(url)
                             .then(
                                   function(response) {
                                      $scope.config = response.data;
                                      $scope.actionId = $scope.config.associatedArtifactId;
                                      $scope.message = '';
                                      $scope.htmlFeatureColumns = true;
                                      
                                      $scope.columns = [ {
                                         field : 'feature',
                                         displayName : 'Feature',
                                         enableSorting : true,
                                         enableCellEdit:false,
                                         enableFiltering: true,
                                         width : 125
                                      } ];

                                      $scope.createFeatureColumns();
                                      $scope.createViewColumns();
                                      $scope.refreshAccess();
                                      $scope.refreshShowAllLabel();
                                      
                                      $scope.itemsGridOptions.columnDefs = $scope.columns;
                                      $scope.gridApi.grid
                                            .refresh();
                                      $scope.data = $scope.config.featureToValueMaps;
                                   });
                        }

                        $scope.itemsGridOptions = {
                           data : 'data',
                           enableHighlighting : true,
                           enableGridMenu : true,
                           enableColumnResize : true,
                           enableColumnReordering : true,
                           enableRowSelection : true,
                           enableSelectAll : false,
                           showTreeExpandNoChildren : false,
                           enableRowHeaderSelection : false,
                           showFilter : true,
                           enableFiltering: true,
                           multiSelect : false,
                           columnDefs : $scope.columns,
                           onRegisterApi : function(gridApi) {
                              $scope.gridApi = gridApi;

                              $scope.gridApi.selection.on
                                    .rowSelectionChanged(
                                          $scope,
                                          function(row) {
                                             if (row.isSelected) {
                                                $scope.selectedRow = row.entity;
                                             } else {
                                                $scope.selectedRow = null;
                                             }
                                          });
                                          
                              $scope.gridApi.edit.on.afterCellEdit($scope, function(rowEntity, colDef, newValue, oldValue) {
                                 $scope.updateValue(rowEntity, colDef, newValue, oldValue);
                                 $scope.$apply();
                              });

                           }
                        };

                        $scope.loadTable();

                     }

                     $scope.updateValue = function(rowEntity, colDef, newValue, oldValue) {
                	 var featureId = rowEntity.id;
                	 var newApplic = rowEntity.feature + " = " + newValue;
                	 var viewId = colDef.id;
                     var url = '/orcs/branch/' + $scope.selectedBranch.id  + '/applic/view/' + viewId + '/applic';
                         $http.put(url,newApplic).then(function(response) {
                        	 if (response.data.errors) {
                                 $scope.error(response.data.results);
                              } else {
                                 $scope.loadTable();
                              }
                         });
                     }

                     $scope.createFeatureColumns = function() {
                        $scope.columns
                        .push({
                           field : "description",
                           displayName : "Description",
                           enableSorting : false,
                           enableCellEdit:false,
                           width : 125
                        });
                        if ($scope.showAll) {
                            $scope.columns
                            .push({
                               field : "valueType",
                               displayName : "Value Type",
                               enableSorting : true,
                               width : 125
                            });
                            $scope.columns
                            .push({
                               field : "values",
                               displayName : "Values",
                               enableSorting : true,
                               width : 125
                            });
                            $scope.columns
                            .push({
                               field : "defaultValue",
                               displayName : "Default Value",
                               enableSorting : true,
                               width : 125
                            });
                            $scope.columns
                            .push({
                               field : "multiValued",
                               displayName : "Multi Valued",
                               enableSorting : true,
                               width : 60
                            });
                        }
                     }
                     
                     // //////////////////////////////////////
                     // Dynamically create columns based on views from
					 // configs
                     // //////////////////////////////////////
                     $scope.createViewColumns = function() {
                        for (i = 0; i < $scope.config.views.length; i++) {
                           var view = $scope.config.views[i];
                           if (view.id == null || view.id <=0) {
                              $scope.error("view Id is invalid in "+$scope.views);
                              $return;
                           } else if (!view.name) {
                              $scope.error("view Name is invalid in "+$scope.views);
                              return;
                           }
                           var columnName = view.name;
                           $scope.columns
                                 .push({
                                    field : columnName
                                          .toLowerCase(),
                                    displayName : columnName,
                                    id : view.id,
                                    enableSorting : true,
                                    width : 125,
                                    enableCellEdit: true,
                                    editDropdownValueLabel: 'value', 
                                    editDropdownIdLabel: 'value',
                                    editableCellTemplate: 'ui-grid/dropdownEditor',
                                     editDropdownOptionsFunction: function(rowEntity, colDef) {
                                        var feature = $scope.config.featureIdToFeature[rowEntity.id];
                                        var values = [];
                                        for (var i=0; i<feature.values.length; i++) {
                                           values.push({value: feature.values[i]});
                                        }
                                        return $timeout(function() {
                                             return values;
                                           }, 100);
                                    },
                                    menuItems: [
                                                {
                                                  title: 'Edit View',
                                                  icon: 'glyphicon glyphicon-pencil',
                                                  context: {scope: $scope, view: view},
                                                  action: function($event) {
                                                     this.context.scope.view = this.context.view;
                                                     this.context.scope.handleEditView();
                                                  }
                                                },
                                                {
                                                  title: 'Delete view',
                                                  icon: 'glyphicon glyphicon-remove',
                                                  context: {scope: $scope, view: view},
                                                  action: function($event) {
                                                     this.context.scope.view = this.context.view;
                                                     this.context.scope.saveViewDelete();
                                                  }
                                                }
                                              ]
                                 });
                        }

                     }
                     
                     // //////////////////////////////////////
                     // Utilities
                     // //////////////////////////////////////
                     

                     
                     
                     
                     $scope.home = function() {
                        var url = '/orcs/applicui/plconfigro.html';
                        window.location.replace(url);
                     }

                     $scope.getViewById = function (id) {
                        for (i = 0; i < $scope.config.views.length; i++) {
                           var view = $scope.config.views[i];
                           if (view.id == id) {
                              return view
                           }
                        }
                     }
                     
                     $scope.error = function(msg) {
                        if (msg) {
                           $scope.showErrorLabel = true;
                           $scope.errorMsg = msg;
                        }
                        else {
                           $scope.showErrorLabel = false;
                           $scope.errorMsg = "";
                        }
                     }
                     
                     function getQueryParameterByName(name, url) {
                        if (!url)
                           url = window.location.href;
                        name = name.replace(/[\[\]]/g, '\\$&');
                        var regex = new RegExp('[?&]' + name
                              + '(=([^&#]*)|&|#|$)'), results = regex
                              .exec(url);
                        if (!results)
                           return null;
                        if (!results[2])
                           return '';
                        return decodeURIComponent(results[2].replace(
                              /\+/g, ' '));
                     }
                  } ]);

app.directive("draggable", function($document) {
    return function(scope, element, attr) {
      var container, mousemove, mouseup, startX, startY, x, y, _ref;
      _ref = [null, null, null, null, null], x = _ref[0], y = _ref[1], container = _ref[2], startX = _ref[3], startY = _ref[4];
      mousemove = function(event) {
        y = event.pageY - startY;
        x = event.pageX - startX;
        if (x < 0) {
          x = 0;
        }
        if (y < 0) {
          y = 0;
        }
        scope.$apply(function() {
          return scope.$parent.events.push({
            mousemove: {
              x: x,
              y: y,
              pageX: event.pageX,
              pageY: event.pageY,
              startY: startY,
              startX: startX
            }
          });
        });
        return container.css({
          top: y + "px",
          left: x + "px"
        });
      };
      mouseup = function() {
        $document.unbind("mousemove", mousemove);
        $document.unbind("mouseup", mouseup);
        scope.elem.top = y;
        scope.elem.left = x;
        console.log(element);
        return scope.$apply(function() {
          return scope.$parent.events.push({
            mouseup: {
              x: x,
              y: y
            }
          });
        });
      };
      startX = 0;
      startY = 0;
      x = scope.elem.left;
      y = scope.elem.top;
      container = null;
      element.css({
        position: "relative",
        cursor: "pointer"
      });
      return element.on("mousedown", function(event) {
        if (event.which !== 1) {
          return;
        }
        event.preventDefault();
        console.log('mousedown');
        console.log(event);
        console.log(element);
        container = attr.$$element.parent();
        console.log(container);
        scope.$apply(function() {
          return scope.$parent.events = [
            {
              "mousedown": {
                x: x,
                y: y,
                pageX: event.pageX,
                pageY: event.pageY,
                startY: startY,
                startX: startX
              }
            }
          ];
        });
        startX = event.pageX - x;
        startY = event.pageY - y;
        $document.on("mousemove", mousemove);
        return $document.on("mouseup", mouseup);
      });
    };
  });