
app.directive('oseeMultiselectDropdownControl', function() {
    return {
        restrict: 'E',
        controller: ['BaseController', '$scope', 'OseeAppSchema', 'OseeControlValues', function(BaseController, $scope, OseeAppSchema, OseeControlValues) {
            var vm = this; 
                                       
            $scope.model = []; // This is where we can set default selections on initialization, if any.           
            OseeControlValues.query({ 
                url : $scope.uischema.scope.endpoint,
                controlId : $scope.uischema.scope.$ref.substring($scope.uischema.scope.$ref.lastIndexOf('/') + 1)
            }, function(selections) {
                  var objects = [];
                  for(i = 0; i < selections.length; i++) {
                      objects[i] = {
                          id : i,
                          label : selections[i].toString()
                      };
                      // Opportunity to set default(s) here ($scope.model)
                  }
                  $scope.data = objects;
            });                
            $scope.settings = $scope.uischema.options.settings;
            if ($scope.settings.smartButtonTextConverter == true) {
                $scope.settings.smartButtonTextConverter = function(itemText, originalItem) {
                    // This is where we can modify text, if needed.
                    return itemText;
                }
            }
    
            $scope.onInit = function() {}
            $scope.onNgChange = function(controlschema) {
                OseeAppSchema.updateItem(controlschema);
            }
                           
            BaseController.call(vm, $scope, OseeAppSchema, OseeControlValues);
        }],
        controllerAs: 'vm',
        template: `
            <jsonforms-control>
                <div id="{{vm.id}}"
                    ng-dropdown-multiselect="" 
                    options="data" 
                    selected-model="model" 
                    extra-settings="settings"
                    data-ng-init="onInit()">
                </div>
           </jsonforms-control>
        `
        };
    })
    .run(['RendererService', 'JSONFormsTesters', function(RendererService, Testers) {
        RendererService.register('osee-multiselect-dropdown-control', Testers.and(
            // Inherit this custom control from schema that call out the following using this single option:
            Testers.optionIs('customControlName', 'oseeMultiselectDropdownControl')
        ), 10);
}]);