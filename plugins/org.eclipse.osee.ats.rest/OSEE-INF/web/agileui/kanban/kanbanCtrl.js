/*
 * Copyright (c) 2015 Robert Bosch Engineering and Business Solutions Private Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

var _tasks;

/*
 * Registers controllers as a module
 */
angular
		.module('AgileApp')
		.controller(
				'KanbanCtrl',
				[
						'$scope',
						'AgileEndpoint',
						'Menu',
						'Global',
						'$routeParams',
						function($scope, AgileEndpoint, Menu, Global,
								$routeParams) {
							"use strict"
							$scope.team = {};
							$scope.team.id = $routeParams.team;
							if ($routeParams.rowType == "BY_STORY") {
								$scope.byStory = true;
								$scope.byAssignee = false;
							}else  {
								$scope.byStory = false;
								$scope.byAssignee = true;
							}
							$scope.nameFilter = null;
							$scope.tasks = {};

							/*
							 * Fetches userName for a userID
							 */
							$scope.userName = function(userId) {
								return getUserName(userId);
							}

							AgileEndpoint.getTeamToken($scope.team).$promise
									.then(function(data) {
										$scope.team.name = data.name;
									});

							$scope.openAssigneePane = function(event) {
								var id = event.currentTarget.id;
								$scope.selectedCard = $scope.kb.tasks[id];
								var assignees = [];
								if ($scope.selectedCard.attributeMap.AssigneesStr) {
									assignees = $scope.selectedCard.attributeMap.AssigneesStr.split('; ');
								}
								$scope.selectedTeamAssignees = assignees;
								$scope.assigneeSelected = null;
								$scope.assign = true;
							}

							$scope.isSelectedCardAssignee = function(user) {
								if ($scope.selectedCard != null) {
									var checked = $scope.selectedTeamAssignees.indexOf(user) != -1;
									return checked;
								}
								return false;
							}

							$scope.isInWork = function(taskId) {
								var task = $scope.kb.tasks[taskId];
								var stateType =  task.attributeMap['ats.Current State Type'];
								var isInWork = stateType == "Working";
								return isInWork;
							}


							function remove(array, element) {
							    const index = array.indexOf(element);
							    array.splice(index, 1);
							    return array;
							}

							$scope.handleAssigneeCheck = function(user) {
								if ($scope.selectedTeamAssignees.indexOf(user) == -1) {
									$scope.selectedTeamAssignees.push(user);
								}else {
									$scope.selectedTeamAssignees = remove($scope.selectedTeamAssignees,user);
								}
							}

							$scope.submitAssigneePane = function(event) {
								if ($scope.selectedTeamAssignees.length == 0) {
									alert("Must select assignee(s) or UnAssigned");
									return;
								}
								// change assignees here
								var data = {};
								data.ids = [];
								data.ids.push($scope.selectedCard.guid);
								 data.assigneesAccountIds = [];
								 data.setAssignees = true;
								for (var i = 0; i < $scope.selectedTeamAssignees.length; i++) {
									var userStr = $scope.selectedTeamAssignees[i];
									var id = $scope.kb.userNameToId[userStr];
									data.assigneesAccountIds.push(id);
								}

								/*
								 * On state transition updates the new states to
								 * the server.
								 */
								AgileEndpoint.updateStatus(data).$promise
										.then(
												function(data) {
													if (data.errors > 0) {
														alert(data.results.results);
													} else {
														// change selected
														// assignees on card

														var task = $scope.kb.tasks[$scope.selectedCard.guid];
														task.attributeMap['AssigneesStr'] = data.jaxAgileItem.assigneesStr;
														task.attributeMap['AssigneesStrShort'] = data.jaxAgileItem.assigneesStrShort;
														$scope.assign = false;
													}
												},
												function(reason) {
													alert("Error updating assignees "
															+ reason);
												});

							}

							$scope.closeAssigneePane = function(event) {
								$scope.assign = false;
							}

							$scope.openAction = function(event) {
								var id = event.currentTarget.id;
								var url = "/ats/ui/action/" + id;
								var win = window.open(url, '_blank');
								win.focus();
							}

							$scope.openEditPane = function(event) {
								var id = event.currentTarget.id;
								$scope.selectedCard = $scope.kb.tasks[id];
								$scope.edit = {};
								$scope.edit.task = {};
								$scope.edit.task.name = $scope.selectedCard.name;
								$scope.edit.task.description = $scope.selectedCard.attributeMap["ats.Description"];
								$scope.edit.task.points = $scope.selectedCard.attributeMap["ats.Points"];
								$scope.edit.task.workPackage = 15;
								// $scope.validWorkPackages = "[ { "id":"15", "name":"Add ARC 243 Radio"}, { "id":"15", "name":"Add ARC 243 Radio"} ]";
								$scope.edit = true;
							}
							
							$scope.saveEditPane = function(event) {
								alert("save");
								$scope.edit = false;
							}
							
							$scope.cancelEditPane = function(event) {
								$scope.edit = false;
							}

							/*
							 * State transition
							 */
							$scope.onDropComplete = function(data, evt,
									toState, toRow) {
								var fromState = data.taskState;
								var fromRowId = getRowId(data.row);
								var toRowId = getRowId(toRow);
								if (fromState !== toState
										|| fromRowId !== toRowId) {

									var _data = {};
									_data.ids = [ data.guid ];
									_data.toStateUsers = [ toRowId ];
									_data.toState = toState;

									updateForTransition(_data, evt, data.guid, fromState,
											toState);
									data.taskState = toState
									return;
								}
							}

							// Get tasks for selected sprint
							var getTasks = function() {
								if ($scope.selectedSprint) {
									AgileEndpoint.getSprintForKb($scope.team,
											$scope.selectedSprint, $scope.byStory).$promise
											.then(function(data) {
												_tasks = data;
												$scope.kb = _tasks;
												if (_tasks.statesToTaskIds.length == 0) {
													$scope.notasks = true;
													$scope.count = 0;
													$scope.rowtype = "";
												} else {
													$scope.notasks = false;
													$scope.availableStates = data["availableStates"];
													if (_tasks.rowType == "BY_STORY") {
														$scope.rowtype = "Story";
													} else {
														$scope.rowtype = "Assignee";
													}

													// process rowsToIds
													var rowNameToTasksIds = {};
													var count = 0;
													for (var i = 0; i < data.rowToTaskIds.length; i++) {
														var entry = data.rowToTaskIds[i];
														var name = data.rowIdToName[entry.rowId];
														rowNameToTasksIds[name] = entry.taskIds;
														count += entry.taskIds.length;
													}

													var rows = _
															.keys(rowNameToTasksIds);
													rows.sort();
													$scope.rows = rows;
													_tasks.rows = rowNameToTasksIds;

													// process statesToTaskIds
													var states = {};
													for (var i = 0; i < data.statesToTaskIds.length; i++) {
														var entry = data.statesToTaskIds[i];
														var name = entry.name;
														states[name] = entry.taskIds;
													}

													_tasks.states = states;
													$scope.teamMembersOrdered = $scope.kb.teamMembersOrdered;
													$scope.count = count;

												}
											});
								}
							}

							$scope
									.$watch(
											"bigCards",
											function() {
												if (_tasks && _tasks.tasks) {
													$scope.availableStates = $scope.availableStates;
												}
											});

							/*
							 * On state transition updates DOM and CSS for the
							 * card
							 */
							var updateForTransition = function(data, evt, taskId, fromState,
									toState) {
								var idDropedToTd = getRowName(data.toStateUsers[0])
										+ '-' + toState;
								var idDraggedCard = taskId;

								/*
								 * On state transition updates the new states to
								 * the server.
								 */
								AgileEndpoint.updateStatus(data).$promise
										.then(
												function(data) {
													if (data.errors > 0) {
														alert(data.results.results);
													} else {
														updateTask(taskId,
																fromState,
																toState);
														var toTd = document
																.getElementById(idDropedToTd);
														var card = document
																.getElementById(idDraggedCard);
														card
																.setAttribute(
																		"class",
																		"card "
																				+ toState)
														var attribute = card
																.getAttribute("ng-drag-data");
														var newAttr = attribute
																.replace(
																		fromState,
																		toState);
														card.setAttribute(
																"ng-drag-data",
																newAttr);
														toTd.appendChild(card);
													}
												},
												function(reason) {
													alert("Error updating status "
															+ reason);
												});
							}

							// populate model with sprints
							$scope
									.$watch(
											"team",
											function() {
												AgileEndpoint
														.getSprintsTokens($scope.team).$promise
														.then(function(data) {
															$scope.sprints = data;
															$scope.selectedSprint = $scope.sprints[0];
														});
											});

							// populate model with tasks
							$scope.$watch("selectedSprint", function() {
								getTasks();
							});

							// Copied through all controlers; ensure all are
							// same
							$scope.openBacklogForTeam = Menu.openBacklogForTeam;
							$scope.openSprintForTeam = Menu.openSprintForTeam;
							$scope.openKanbanForTeam = Menu.openKanbanForTeam;
							$scope.openNewTaskForTeam = Menu.openNewTaskForTeam;
							$scope.openBurndownForTeam = Menu.openBurndownForTeam;
							$scope.openBurnupForTeam = Menu.openBurnupForTeam;
							$scope.openSummaryForTeam = Menu.openSummaryForTeam;
							$scope.openDataForTeam = Menu.openDataForTeam;

						} ]);

/*
 * Returns Tasks for given Task state and row
 */
var getTasksFor = function(taskState, row) {
	var tasksObject = {};
	var tasksForState = _tasks.states[taskState];
	var tasksAssigned = _tasks.rows[row];
	if (!_.isNull(tasksForState) && _.isArray(tasksForState)
			&& _.isArray(tasksAssigned)) {
		_.map(_.intersection(tasksForState, tasksAssigned), function(taskId) {
			tasksObject[taskId] = _tasks.tasks[taskId];
		});
	}
	return tasksObject;
}

/*
 * Updates model
 */
var updateTask = function(taskId, oldState, newState) {

	var tasksForOldState = _tasks.states[oldState];
	var index = tasksForOldState.indexOf(taskId);
	if (!(newState in _tasks.states)) {
		_tasks.states[newState] = [];
	}
	_tasks.states[oldState].splice(index, 1)
	_tasks.states[newState].push(taskId);

}

/*
 * Fetches rows for a task
 */
var getRowsForTask = function(taskId) {
	var rowsWithTask = []
	var rows = _tasks.rows;
	if (!_.isNull(rows) && !_.isEmpty(rows)) {
		_.map(rows, function(value, key) {
			if (_.contains(value, taskId)) {
				rowsWithTask.push(key);
			}
		})
	}
	return rowsWithTask;
}

/*
 * Fetches rowName for a rowId
 */
var getRowName = function(userId) {
	return _tasks.rowIdToName[userId];
}

var getRowId = function(rowName) {
	var resultRowId = "";
	for ( var rowId in _tasks.rowIdToName) {
		var name = _tasks.rowIdToName[rowId];
		if (name === rowName) {
			resultRowId = rowId;
			break;
		}
	}
	return resultRowId;
}
