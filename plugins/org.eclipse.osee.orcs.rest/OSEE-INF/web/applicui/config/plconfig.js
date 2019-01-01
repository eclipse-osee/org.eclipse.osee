var app = angular.module('app', [ 'checklist-model', 'ngResource', 'ui.grid',
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

                     $scope.selectedBranch = {};
                     $scope.branches = [];
                     $scope.selectedBranch.id = getQueryParameterByName('branch');
                     $scope.itemsGridOptions = [];
                     $scope.itemsGridOptions.data = [];
                     $scope.showAll = getQueryParameterByName("showAll"); 

                     // HTML boolean to show/hide elements
                     $scope.resetHtmlVarsAndFlags = function() {
                         $scope.edit = {};

                         // ///// VARIANT flags and
                            // vars
                         $scope.variant = {};
                         // Main Edit Container
                         $scope.htmlVariantPane = false;
                         // For Add and Edit
                         $scope.htmlVariantEdit = false;
                         // For Delete
                         $scope.htmlVariantDelete = false;
                         // Widgets
                         $scope.htmlVariantSelect = false;
                         $scope.htmlVariantCopyFrom = false;
                         $scope.htmlVariantTitle = false;
                         $scope.htmlVariantSave = false;
                         $scope.htmlVariantDelete = false;
                         
                         // ///// FEATURE flags and
                            // vars
                         $scope.feature = {};
                         $scope.deleting = false;
                         // Main Edit Container
                         $scope.htmlFeaturePane = false;
                         // For Add and Edit
                         $scope.htmlFeatureEdit = false;
                         // Widgets
                         $scope.htmlFeatureSelect = false;
                         $scope.htmlFeatureSave = false;
                         $scope.htmlFeatureDelete = false;
                         $scope.htmlFeatureEditTitle = false;
                     }
                     $scope.resetHtmlVarsAndFlags();

                     // //////////////////////////////////////
                     // Load branch combo regardless of which "page"
                     // //////////////////////////////////////
                     $scope.loadBranches = function() {
                        $http.get('/orcs/applicui/branches').then(
                              function(response) {
                                 $scope.branches = response.data;
                                 $scope.message = '';
                                 $scope.setSelectedBranch();
                              });
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
                           var url = '/orcs/applicui/config/plconfig.html?branch='
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
                                       $scope.accessLevel = "(Read-Only)";
                                       $scope.isReadOnly = true;
                                    } else {
                                       $scope.accessLevel = "";
                                       $scope.isReadOnly = false;
                                    }
                               });
                     }
                     
                     // /////////////////////////////////////////////////////////////////////
                     // VARIANT METHODS
                     // /////////////////////////////////////////////////////////////////////
                     
                     // //////////////////////////////////////
                     // Handle Add Variant
                     // //////////////////////////////////////
                     $scope.handleAddVariant = function() {
                        $http
                        .get(
                              '/orcs/applicui/branch/'
                                    + $scope.selectedBranch.id)
                        .then(
                              function(response) {
                                 var config = response.data;
                                 $scope.htmlVariantPane = true;
                                 $scope.htmlVariantEdit = true;
                                 $scope.htmlVariantTitle = true;
                                 $scope.htmlVariantCopyFrom = true;
                                 $scope.htmlVariantSave = true;
                                 $scope.variants = config.variants;
                              });
                     }

                     // //////////////////////////////////////
                     // Handle Edit Variant
                     // //////////////////////////////////////
                     $scope.handleEditVariant = function() {
                        $http
                        .get(
                              '/orcs/applicui/branch/'
                                    + $scope.selectedBranch.id)
                        .then(
                              function(response) {
                                 var config = response.data;
                                 $scope.htmlVariantPane = true;
                                 $scope.htmlVariantEdit = true;
                                 if (!$scope.variant.id) {
                                    $scope.htmlVariantSelect = true;
                                 }
                                 $scope.htmlVariantTitle = true;
                                 $scope.htmlVariantSave = true;
                                 $scope.variants = config.variants;
                              });
                     }
                     
                     // //////////////////////////////////////
                     // Cancel Variant Edit
                     // //////////////////////////////////////
                     $scope.cancelVariantEdit = function() {
                        $scope.resetHtmlVarsAndFlags();
                     }


                     // //////////////////////////////////////
                     // Save Variant Edit
                     // //////////////////////////////////////
                     $scope.saveVariantEdit = function() {
                        var variant = $scope.variant;
                        var url = '/orcs/branch/'
                              + $scope.selectedBranch.id
                              + '/applic/variant/';
                        var json = JSON.stringify(variant);
                        $http.put(url, json).then(function(response) {
                           if (response.data.errors) {
                              $scope.error(response.data.results);
                           } else {
                              $scope.loadTable();
                           }
                           $scope.resetHtmlVarsAndFlags();
                        });
                     }

                     // //////////////////////////////////////
                     // Handle Delete Variant
                     // //////////////////////////////////////
                     $scope.handleDeleteVariant = function(variant) {
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
                                     $scope.htmlVariantPane = true;
                                     $scope.htmlVariantSelect = true;
                                     $scope.htmlVariantDelete = true;
                                     $scope.variants = config.variants;
                               }
                         });
                     }

                     // //////////////////////////////////////
                     // Save Variant Delete
                     // //////////////////////////////////////
                     $scope.saveVariantDelete = function(variant) {
                        if (!variant || !variant.id) {
                           variant = $scope.variant;
                        } 
                        if (!variant.name) {
                           variant.name = $scope.getVariantById(variant.id).name; 
                        }
                        if (confirm("Delete Variant ["+variant.name+"]\n\nAre you sure?")) {
                           var url = '/orcs/branch/'
                              + $scope.selectedBranch.id
                              + '/applic/variant/' + variant.id;
                           $http.delete(url).then(
                                 function(response) {
                                    if (response.errors > 0) {
                                       $scope.error(response.results.results);
                                    } else {
                                       $scope.loadTable();
                                    }
                                    $scope.resetHtmlVarsAndFlags();
                                 });
                        }
                     }
                     
                     // /////////////////////////////////////////////////////////////////////
                     // FEATURE METHODS
                     // /////////////////////////////////////////////////////////////////////

                     // //////////////////////////////////////
                     // Handle Add Feature
                     // //////////////////////////////////////
                     $scope.handleAddFeature = function() {
                	 $scope.htmlFeaturePane = true;
                	 $scope.htmlFeatureEdit = true;
                	 $scope.htmlFeatureEditTitle = true;
                	 $scope.htmlFeatureSave = true;
                     }

                     // //////////////////////////////////////
                     // Handle Edit Feature - Select
                     //
                     // Open edit pane with select and populate with features
                     // //////////////////////////////////////
                     $scope.handleEditFeatureSelect = function() {

                        $http
                              .get(
                                    '/orcs/applicui/branch/'
                                          + $scope.selectedBranch.id)
                              .then(
                                    function(response) {
                                       $scope.htmlFeaturePane = true;
                                       $scope.htmlFeatureSelect = true;
                                       $scope.htmlFeatureEditTitle = true;
                                       $scope.config = response.data;
                                       $scope.features = $scope.config.features;
                                    });
                     }
                     
                     // //////////////////////////////////////
                     // Handle Edit Feature - Values
                     //
                     // Open edit pane and populate with selected feature
                     // //////////////////////////////////////
                     $scope.handleEditFeatureValues = function() {
                     	// Same select widget used for both; return if deleting
                     	if ($scope.deleting) {
                     		return;
                     	}
                     	var feature = $scope.feature;
                	    $scope.resetHtmlVarsAndFlags();
                        $http
                              .get(
                                    '/orcs/branch/'
                                          + $scope.selectedBranch.id
                                          + '/applic/feature/'
                                          + feature.id)
                              .then(
                                    function(response) {
                                       $scope.htmlFeaturePane = true;
                                       $scope.htmlFeatureEdit = true;
                                       $scope.htmlFeatureEditTitle = true;
                                       $scope.htmlFeatureSave = true;
                                       $scope.feature = response.data;
                                       $scope.feature.valueStr = feature.values
                                             .join(";");
                                    });
                     }
                     
                     // //////////////////////////////////////
                     // Save Feature Edit
                     // //////////////////////////////////////
                     $scope.saveFeatureEdit = function() {
                        var feature = $scope.feature;
                        if (feature.valueStr) {
                           feature.values = feature.valueStr
                                 .split(";");
                           feature.valueStr = "";
                        }

                        var url = '/orcs/branch/'
                              + $scope.selectedBranch.id
                              + '/applic/feature/';
                        var json = JSON.stringify(feature);
                        $http.put(url, json).then(function(response) {
                           if (response.errors > 0) {
                              $scope.error(response.results.results);
                           } else {
                              $scope.loadTable();
                              alert("Saved new Feature.  Add another or press Cancel");
                           }
                        });
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
                     $scope.handleDeleteFeature = function(variant) {
                        $http
                        .get(
                              '/orcs/applicui/branch/'
                                    + $scope.selectedBranch.id)
                        .then(
                              function(response) {
                                   if (response.errors > 0) {
                                      $scope.error(response.results.results);
                                   } else {
                                         var config = response.data;
                                         $scope.htmlFeaturePane = true;
                                         $scope.htmlFeatureSelect = true;
                                         $scope.htmlFeatureDelete = true;
                                         $scope.deleting = true;
                                         $scope.features = config.features;
                                   }
                              });
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
                                   if (response.errors > 0) {
                                      $scope.error(response.results.results);
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
                                      $scope.message = '';
                                      
                                      $scope.columns = [ {
                                         field : 'feature',
                                         displayName : 'Feature',
                                         enableSorting : true,
                                         width : 125
                                      } ];

                                      $scope.createFeatureColumns();
                                      $scope.createVariantColumns();
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
                	 var variantId = colDef.id;
                         var url = '/orcs/branch/' + $scope.selectedBranch.id  + '/applic/variant/' + variantId + '/feature/' + featureId + '/applic/' + newValue;
                         $http.put(url).then(function(response) {
                   			var result = response.data;
                            if (result.errors > 0) {
                               $scope.error(result.results);
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
                     // Dynamically create columns based on variants from
					 // configs
                     // //////////////////////////////////////
                     $scope.createVariantColumns = function() {
                        for (i = 0; i < $scope.config.variants.length; i++) {
                           var variant = $scope.config.variants[i];
                           if (variant.id == null || variant.id <=0) {
                              $scope.error("Variant Id is invalid in "+$scope.variants);
                              $return;
                           } else if (!variant.name) {
                              $scope.error("Variant Name is invalid in "+$scope.variants);
                              return;
                           }
                           var columnName = variant.name;
                           $scope.columns
                                 .push({
                                    field : columnName
                                          .toLowerCase(),
                                    displayName : columnName,
                                    id : variant.id,
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
                                                  title: 'Edit Varient',
                                                  icon: 'glyphicon glyphicon-pencil',
                                                  context: {scope: $scope, variant: variant},
                                                  action: function($event) {
                                                     this.context.scope.variant = this.context.variant;
                                                     this.context.scope.handleEditVariant();
                                                  }
                                                },
                                                {
                                                  title: 'Delete Varient',
                                                  icon: 'glyphicon glyphicon-remove',
                                                  context: {scope: $scope, variant: variant},
                                                  action: function($event) {
                                                     this.context.scope.variant = this.context.variant;
                                                     this.context.scope.saveVariantDelete();
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
                        var url = '/orcs/applicui/plconfig.html';
                        window.location.replace(url);
                     }

                     $scope.getVariantById = function (id) {
                        for (i = 0; i < $scope.config.variants.length; i++) {
                           var variant = $scope.config.variants[i];
                           if (variant.id == id) {
                              return variant
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
                  } ])
                  
;
