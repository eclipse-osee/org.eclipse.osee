
app.directive('oseeInputControl', function () {
    return {
        restrict: 'E',
        controller: ['BaseController', '$scope', '$routeParams', 'OseeControlValues', 'OseeAppSchema', function (
                BaseController, $scope, $routeParams, OseeControlValues, OseeAppSchema) {
                var vm = this;
                vm.isValid = OseeAppSchema.isValid;
                vm.parseHtmlForTableData = OseeAppSchema.parseHtmlForTableData;
                vm.gridOptions = {
                    enableColumnResizing : true,
                    columnDefs: [ { name: " ", 
                                    enableColumnMenu: false,
                                    width: "10%", 
                                    cellTemplate: '<div style="padding:5px"><button class="btn btn-xs" ng-click="grid.appScope.deleteRow(row)"><i class="glyphicon glyphicon-remove"></i></button></div>' },
                                  { name: "Rpcr Uuid", field: "rpcrUuid", visible: false },
                                  { name: "Rpcr Name", width: "90%", field: "rpcrName"} ],
                    data: []
                };   
                var schemaElement = OseeAppSchema.getElement(); 
                var schemaAttr = OseeControlValues.parseAttribute($scope.uischema.scope.$ref);            
                OseeControlValues.queryUrl($scope.uischema.options.getUrl, true).query({ element: schemaElement, attribute: schemaAttr }, function (selections) {
                    for (i = 0; i < selections.length; i++) {
                        vm.gridOptions.data[i] = JSON.parse(selections[i]);
                    }
                });
                $scope.checkValue = function (item, regex) {
                    if (OseeAppSchema.isValid(item, regex)) {
                        vm.uiSchema.style = {
                            color: 'black'
                        };
                    } else {
                        vm.uiSchema.style = {
                            color: 'red'
                        };
                    }
                }
                $scope.onNgChange = function () {  
                    var putData = "[ " + schemaAttr; 
                    for(var i = 0; i < vm.gridOptions.data.length; ++i) {
                       console.log(vm.gridOptions.data[i].rpcrUuid); 
                       putData += ", " + vm.gridOptions.data[i].rpcrUuid;
                    }
                    putData += " ]";
                    OseeControlValues.putUrl($scope.uischema.options.putUrl, false).submit({
                        element: schemaElement
                    }, putData).$promise.then(
                        function (data) {
                        console.log(data);
                        // will need to update the gamma here
                    }, function (response) {
                        vm.failed = true;
                        alert("Problem: " + response.message);
                    });                    
                }
                BaseController.call(vm, $scope, $routeParams, OseeAppSchema);
                $scope.deleteRow = function(row) {
                    var index = $scope.vm.gridOptions.data.indexOf(row.entity);
                    $scope.vm.gridOptions.data.splice(index, 1);
                    $scope.onNgChange();
                };              
            }
        ],
        controllerAs: 'vm',
        link: function link(scope, element, attrs, ctrl) {
            if (ctrl.resolvedData) {
                if (!ctrl.isValid(ctrl.resolvedData.value, ctrl.uiSchema.options.required)) {
                    console.log("invalid control data according to the uiSchema regex");
                    element.
                    css({
                        color: 'red'
                    });
                }
            }
            element.on('dragover', function(event) {
               event.preventDefault();
            });
            element.on('drop', function (event) {
                event.preventDefault();
                event.dataTransfer = event.originalEvent.dataTransfer;
                ctrl.parseHtmlForTableData(event.dataTransfer.getData("text/html"), ctrl.gridOptions.data);
                scope.$apply();
                scope.onNgChange();
            });
        },
        template: `
            <jsonforms-control>
                <div id="{{vm.scope.$id}}" ui-grid="vm.gridOptions" class="gridjsform"
                    ui-grid-auto-resize 
                    ui-grid-resize-columns
                    ng-style="{{vm.uiSchema.style}}"
                    ng-readonly="vm.uiSchema.readOnly"                
                    >
                </div>
            </jsonforms-control>
        `
    };

}).run(['RendererService', 'JSONFormsTesters', function (RendererService, Testers) {
            RendererService.register('osee-input-control', Testers.and(
                    // Inherit this custom control from schema that call out the following using this single option:
                    Testers.optionIs('customControlName', 'oseeInputControl')), 10);
        }
    ]);
