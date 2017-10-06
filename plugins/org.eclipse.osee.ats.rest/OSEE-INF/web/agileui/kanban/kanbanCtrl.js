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
						'Global',
						'$routeParams',
						function($scope, AgileEndpoint, Global, $routeParams) {
							"use strict"
							$scope.team = {};
							$scope.team.id = $routeParams.team;
							$scope.nameFilter = null;
							$scope.tasks = {};
							$scope.loadingImg = Global.loadingImg;
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
									_data.uuid = data.guid;
									_data.uuids = [ data.guid ];
									_data.toStateUsers = [ toAssigneeId ];
									_data.toState = toState;

									update(_data, evt, data.guid, fromState,
											toState);
									data.taskState = toState
									return;

								}
							}

							// Get tasks for selected spring
							var getTasks = function() {
								if ($scope.selectedSprint) {
									AgileEndpoint.getSprintForKb($scope.team,
											$scope.selectedSprint).$promise
											.then(function(data) {
												_tasks = data;
												$scope.availableStates = data["availableStates"];

												// process assigneesToUuids
												var assigneeNameToTasksUuids = {};
												var count = 0;
												for (var i = 0; i < data.assigneesToTaskUuids.length; i++) {
													var entry = data.assigneesToTaskUuids[i];
													var name = data.userIdToName[entry.assigneeId];
													assigneeNameToTasksUuids[name] = entry.taskUuids;
													count += entry.taskUuids.length;
												}

												// Add implementers as assignees
												// even though completed
												for (var i = 0; i < data.implementersToTaskUuids.length; i++) {
													var entry = data.implementersToTaskUuids[i];
													var name = data.userIdToName[entry.assigneeId];
													var assigneeEntry = assigneeNameToTasksUuids[name];
													if (assigneeEntry) {
														entry.taskUuids = entry.taskUuids
																.concat(assigneeEntry);
													}
													assigneeNameToTasksUuids[name] = entry.taskUuids;
													count += entry.taskUuids.length;
												}

												var temp = _
														.keys(
																assigneeNameToTasksUuids)
														.sort();

												$scope.assignees = _
														.keys(assigneeNameToTasksUuids);
												_tasks.assignees = assigneeNameToTasksUuids;

												// process statesToTaskUuids
												var states = {};
												for (var i = 0; i < data.statesToTaskUuids.length; i++) {
													var entry = data.statesToTaskUuids[i];
													var name = entry.name;
													states[name] = entry.taskUuids;
												}

												_tasks.states = states;
												$scope.count = count;

											});
								}
							}

							$scope.$watch("bigCards", function() {
								if (_tasks && _tasks.tasks) {
									$scope.availableStates = null;
									getTasks();
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

							// COMMON MENU COPIED TO ALL JS
							$scope.openConfigForTeam = function(team) {
								window.location.assign("main#/config?team="
										.concat($scope.team.id))
							}

							$scope.openKanbanForTeam = function(team) {
								window.location.assign("main#/kanban?team="
										.concat($scope.team.id))
							}

							$scope.openBurndownForTeam = function(team) {
								window.location
										.assign("main#/report?team="
												.concat($scope.team.id)
												.concat(
														"&reporttype=burndown&reportname=Burn-Down"))
							}

							$scope.openBurnupForTeam = function(team) {
								window.location
										.assign("main#/report?team="
												.concat($scope.team.id)
												.concat(
														"&reporttype=burnup&reportname=Burn-Up"))
							}

							$scope.openBacklogForTeam = function(team) {
								window.location.assign("main#/backlog?team="
										.concat($scope.team.id).concat(
												"&default=backlog"))
							}

							$scope.openNewActionForTeam = function(team) {
								window.location.assign("main#/newAction?team="
										.concat($scope.team.id))
							}

							$scope.openSprintForTeam = function(team) {
								window.location.assign("main#/sprint?team="
										.concat($scope.team.id).concat(
												"&default=sprint"))
							}

							$scope.openSummaryForTeam = function(team) {
								window.location
										.assign("main#/report?team="
												.concat($scope.team.id)
												.concat(
														"&reporttype=summary&reportname=Summary"))
							}

							$scope.openDataForTeam = function(team) {
								window.location
										.assign("main#/report?team="
												.concat($scope.team.id)
												.concat(
														"&reporttype=data&reportname=Data"))
							}
							// COMMON MENU COPIED TO ALL JS

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
