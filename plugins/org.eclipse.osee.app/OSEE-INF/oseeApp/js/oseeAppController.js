app.controller('oseeAppController', [
        'OseeAppSchema',
        'OseeControlValues',
        '$route',
        '$scope',
        '$resource',
        function (OseeAppSchema, OseeControlValues, $route, $scope, $resource) {
            $route.current.params;
            var vm = this;
            vm.dataloaded = false;
            vm.loaded = false;
            vm.name = {};
            vm.appId = $route.current.params.uuid;
            OseeControlValues.setActiveApp(vm.appId);
            vm.element = $route.current.params.element;
            vm.oseeAppData = {};
            vm.useSubmit = false;
            $scope.$on('$locationChangeStart', function (event) {
                // make sure the last update is captured before leaving
                OseeAppSchema.doUpdate();
            });

            OseeAppSchema.get({
                appId: vm.appId
            }, function (data) {
                vm.oseeAppSchema = data.OseeApp;
                vm.dataloaded = true;
                vm.defaultUpdateURL = data.ItemUpdateURL;
                vm.baseName = data.BaseName;
                vm.identifier = data.NameContent;
                OseeControlValues.setCreateUrl(data.ItemCreateURL);
                if (data.ItemSubmitURL) {
                    vm.submitURL = data.ItemSubmitURL;
                    vm.useSubmit = true;
                }
                vm.itemDataResource = $resource(data.ItemGetURL);
                $scope.schemaGetKey = data.SchemaGetKey;
                if (vm.element !== undefined) {
                    if (data.SchemaType === 'isArray') {
                        OseeControlValues.queryUrl(data.ItemGetURL, true).query({
                            element: vm.element
                        }, function (data) {
                            vm.oseeAppData = data[0];
                            vm.name = vm.baseName + vm.oseeAppData[vm.identifier];
                            vm.loaded = true;
                        }, function (response) {
                            vm.failed = true;
                            alert("Problem: " + response.message);
                        });
                    } else if (data.SchemaType === 'isItem') {
                        OseeControlValues.queryUrl(data.ItemGetURL, false).query({
                            element: vm.element
                        }, function (data) {
                            vm.oseeAppData = data;
                            vm.name = vm.baseName + vm.oseeAppData[vm.identifier];
                            vm.loaded = true;
                        }, function (response) {
                            vm.failed = true;
                            alert("Problem: " + response.message);
                        });
                    }
                }
            });

            this.submitForm = function () {
                vm.itemSubmitResource = $resource(vm.submitURL, null, {
                        'submit': {
                            method: 'PUT',
                            headers: {
                                'Content-Type': 'application/json'
                            }
                        }
                    });
                if (vm.loaded === true) {
                    vm.itemSubmitResource.submit({
                        element: vm.element
                    }, JSON.stringify(vm.oseeAppData));
                } else {
                    alert("Data not ready to be submitted.");
                }
            };
            OseeAppSchema.getElement = function () {
                return vm.element;
            };

            OseeAppSchema.doUpdate = function () {
                if (this.changedItem) {
                    var jsonData;
                    if (this.changedData instanceof Date) {
                        jsonData = JSON.stringify(this.changedData.getTime());
                    } else {
                        jsonData = JSON.stringify(this.changedData);
                    }
                    var userId = OseeControlValues.getActiveUserId();
                    OseeControlValues.putUrl(vm.defaultUpdateURL, false).submit({
                        element: vm.element,
                        updated: this.changedItem
                    }, "[" + jsonData + "]").$promise.then(
                        function (data) {
                        console.log(data);
                        // will need to update the gamma here
                    }, function (response) {
                        vm.failed = true;
                        alert("Problem: " + response.message);
                    });
                    this.changedItem = null;
                }
            };
            OseeAppSchema.updateItem = function (uiSchema, change) {
                this.changedItem = OseeControlValues.parseAttribute(uiSchema.scope.$ref);
                this.changedData = change;
            };
            OseeAppSchema.isValid = function (value, regex) {
                // the regex represents invalid strings
                if (!regex)
                    return true;
                if (!value)
                    return false;
                var regexp = RegExp(regex);
                if (regexp.test(value))
                    return false;
                return true;
            };
            OseeAppSchema.parseHtmlForTableData = function (html, jsonData) {
                if (!html) {
                    return "";
                }
                var div = document.createElement('div');

                div.innerHTML = html;

                var artNodes = div.getElementsByTagName('A');
                var parsedTableData = "[  \n";
                for (i = 0; i < artNodes.length; ++i) {
                    var artName = artNodes[i].innerText;
                    var artId = "";
                    artNodes[i].href.split("&").forEach(function (part) {
                        var item = part.split("=");
                        if (item[0] === "id") {
                            artId = item[1];
                        }
                    });
                    if(!OseeAppSchema.contains(jsonData, artId))
                        jsonData.push({ rpcrUuid: artId, rpcrName: artName });
                }
                parsedTableData += "\n ]";
                return parsedTableData;
            };
            OseeAppSchema.contains = function (jsonData, artId) {
               var found = false;
                 for(var i = 0; i < jsonData.length; i++) {
                   if (jsonData[i].rpcrUuid == artId) {
                     found = true;
                   break;
                 }
               }
               return found;
            }

        }
    ]);
