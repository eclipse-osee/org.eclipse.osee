/*********************************************************************
* Copyright (c) 2023 Boeing
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     Boeing - initial API and implementation
**********************************************************************/
var app = angular.module('OrcsWriterApp', [ 'ngFileUpload' ]);

app
		.controller(
				"FormController",
				[
						'$scope',
						'$http',
						'Upload',
						function($scope, $http, Upload) {

							$scope.formData = {
								filename : '',
								json : ''
							};
							$scope.message = '';

							$scope.validate = function() {
								$scope.run(true);
							}

							$scope.execute = function() {
								$scope.run(false);
							}

							$scope.run = function(validate) {
								$scope.message = '';
								var url = "";
								if (validate) {
									url = "../../writer/validate";
								} else {
									url = "../../writer";
								}
								var data = {};
								if (!$scope.formData.json && !$scope.file) {
									$scope.message = "ERROR: Must select Excel or enter JSON";
								} else if ($scope.formData.json) {
									$scope.message = "Processing JSON";
									data = $scope.formData.json;
									$http({
										method : 'POST',
										url : url,
										data : data,
										headers : {
											'Accept' : 'application/json',
											'Content-Type' : 'application/json'
										}
									}).success(
											function(data, status, headers,
													config) {
												$scope.message += '\n\n'
														+ data.message;
											}).error(
											function(data, status, headers,
													config) {
												$scope.message += '\n\n'
														+ data.message;
											});
								} else if ($scope.file) {
									$scope.message = "Processing EXCEL XML";
									$suffix = ".xml";
									if ($scope.file.name.indexOf($suffix,
											$scope.file.name.length
													- $suffix.length) == -1) {
										$scope.message += "\n\nError: File must be Excel XML 2003 format with .xml extension.";
									} else {
										Upload.upload({
											url : url + '/excel',
											file : $scope.file
										}).success(
												function(data, status, headers,
														config) {
													$scope.message += '\n\n'
															+ data.message;
												}).error(
												function(data, status, headers,
														config) {
													var errStr = data.message;
													if (typeof errStr == 'undefined') {
														errStr = data;
													}
													$scope.message += '\n\n' + errStr;
												});
									}
								}
							}
						} ]);
