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

							/*
							 * State transition
							 */
							$scope.onDropComplete = function(data, evt,
									toState, toAssignees) {
								var fromState = data.taskState;
								var fromAssigneeId = getUserId(data.assignee);
								var toAssigneeId = getUserId(toAssignees);
								if (fromState !== toState
										|| fromAssigneeId !== toAssigneeId) {

									var _data = {};
									_data.ids = [ data.guid ];
									_data.toStateUsers = [ toAssigneeId ];
									_data.toState = toState;

									update(_data, evt, data.guid, fromState,
											toState);
									data.taskState = toState
									return;

								}
							}

							// Get tasks for selected sprint
							var getTasks = function() {
								if ($scope.selectedSprint) {
									AgileEndpoint.getSprintForKb($scope.team,
											$scope.selectedSprint).$promise
											.then(function(data) {
												_tasks = data;
												if (_tasks.statesToTaskIds.length == 0) {
													$scope.notasks = true;
													$scope.count = 0;
												} else {
													$scope.notasks = false;
													$scope.availableStates = data["availableStates"];

													// process assigneesToIds
													var assigneeNameToTasksIds = {};
													var count = 0;
													for (var i = 0; i < data.assigneesToTaskIds.length; i++) {
														var entry = data.assigneesToTaskIds[i];
														var name = data.userIdToName[entry.assigneeId];
														assigneeNameToTasksIds[name] = entry.taskIds;
														count += entry.taskIds.length;
													}

													// Add implementers as
													// assignees
													// even though completed
													for (var i = 0; i < data.implementersToTaskIds.length; i++) {
														var entry = data.implementersToTaskIds[i];
														var name = data.userIdToName[entry.assigneeId];
														var assigneeEntry = assigneeNameToTasksIds[name];
														if (assigneeEntry) {
															entry.taskIds = entry.taskIds
																	.concat(assigneeEntry);
														}
														assigneeNameToTasksIds[name] = entry.taskIds;
														count += entry.taskIds.length;
													}

													var assignees = _
															.keys(assigneeNameToTasksIds);
													assignees.sort();
													$scope.assignees = assignees;
													_tasks.assignees = assigneeNameToTasksIds;

													// process statesToTaskIds
													var states = {};
													for (var i = 0; i < data.statesToTaskIds.length; i++) {
														var entry = data.statesToTaskIds[i];
														var name = entry.name;
														states[name] = entry.taskIds;
													}

													_tasks.states = states;
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
							var update = function(data, evt, taskId, fromState,
									toState) {
								var idDropedToTd = getUserName(data.toStateUsers[0])
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
 * Returns Tasks for given Task state and assignee
 */
var getTasksFor = function(taskState, assignee) {
	var tasksObject = {};
	var tasksForState = _tasks.states[taskState];
	var tasksAssigned = _tasks.assignees[assignee];
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
 * Fetches assignees for a task
 */
var getAssigneesForTask = function(taskId) {
	var assigneesWithTask = []
	var assignees = _tasks.assignees;
	if (!_.isNull(assignees) && !_.isEmpty(assignees)) {
		_.map(assignees, function(value, key) {
			if (_.contains(value, taskId)) {
				assigneesWithTask.push(key);
			}
		})
	}
	return assigneesWithTask;
}

/*
 * Fetches userName for a userID
 */
var getUserName = function(userId) {
	return _tasks.userIdToName[userId];
}

var getUserId = function(userName) {
	var resultUserId = "";
	for ( var userId in _tasks.userIdToName) {
		var name = _tasks.userIdToName[userId];
		if (name === userName) {
			resultUserId = userId;
			break;
		}
	}
	return resultUserId;
}
