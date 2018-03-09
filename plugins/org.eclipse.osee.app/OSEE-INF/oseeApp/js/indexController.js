app.controller('indexController', ['Account', '$scope', 'OseeControlValues',
        function ( Account, $scope, OseeControlValues ) {
            var vm = this;
            var userName = Account.get({
                }, function(data){
                    $scope.userName = data.name;
                    OseeControlValues.setActiveUserId(data.userName);
            });
            $scope.createAction = function () {
                if(!$scope.createTitle) {
                    alert("Invalid input - must input title");
                }
                else {
                    OseeControlValues.createAction($scope.createTitle);
                    $scope.createTitle = null;
                }
            };
            $scope.findAction = function () {
                if(!$scope.element) {
                    alert("Invalid input - must input element");
                }
                else {
                    OseeControlValues.setWindowLocation($scope.element);               
                    $scope.element = null;
                }
            };
        }
    ]);