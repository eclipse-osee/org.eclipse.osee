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
						'AgileFactory',
						'$routeParams',
						function($scope, AgileFactory, $routeParams) {
							"use strict"
							$scope.team = {};
							$scope.nameFilter = null;
							$scope.tasks = {};
							/*
							 * Fetches userName for a userID
							 */
							$scope.userName = function(userID) {
								return getUserName(userID);
							}

							AgileFactory.getTeams().$promise
									.then(function(data) {
										$scope.teams = data;
									});

							if ($routeParams.team) {
								$scope.team.uuid = $routeParams.team;
								AgileFactory.getTeamSingle($scope.team).$promise
										.then(function(data) {
											$scope.team = data;
										});
							}

							/*
							 * State transition
							 */
							$scope.onDropComplete = function(data, evt, toState, toAssignees) {
								var fromState = data.taskState;
								var fromAssigneeId = getUserId(data.assignee);
								var toAssigneeId = getUserId(toAssignees);
								if (fromState !== toState || fromAssigneeId !== toAssigneeId) {

									var _data = {};
									_data.uuid = data.guid;
									_data.uuids = [data.guid];
									_data.toStateUsers = [toAssigneeId];
									_data.toState = toState;

									update(_data, evt, data.guid, fromState,
											toState);
									data.taskState = toState
									return;

								}
							}

							/*
							 * Fetches tasks for selected combination of team
							 * and sprint
							 */
							var getTasks = function() {
								var selectedSprint = $scope.sprint;

								if (!_.isNull(selectedSprint)
										&& !_.isEmpty(selectedSprint)) {
									AgileFactory.getSprintForKb(selectedSprint).$promise
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

												// Add implementers as assignees even though completed
												for (var i = 0; i < data.implementersToTaskUuids.length; i++) {
													var entry = data.implementersToTaskUuids[i];
													var name = data.userIdToName[entry.assigneeId];
													var assigneeEntry = assigneeNameToTasksUuids[name];
													if (assigneeEntry) {
														entry.taskUuids = entry.taskUuids.concat(assigneeEntry);
													 }
													assigneeNameToTasksUuids[name] = entry.taskUuids;
													count += entry.taskUuids.length;
												}

												var temp = _.keys(assigneeNameToTasksUuids)
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

							/*
							 * On state transition updates DOM and CSS for the
							 * card
							 */
							var update = function(data, evt, taskId, fromState,
									toState) {
								var idDropedToTd = getUserName(data.toStateUsers[0]) + '-'
										+ toState;
								var idDraggedCard = taskId;

								/*
								 * On state transition updates the new states to
								 * the server.
								 */
								AgileFactory.updateStatus(data).$promise
										.then(function(data) {
											updateTask(taskId, fromState,
													toState);
											var toTd = document
													.getElementById(idDropedToTd);
											var card = document
													.getElementById(idDraggedCard);
											card.setAttribute("class", "card "
													+ toState)
											var attribute = card
													.getAttribute("ng-drag-data");
											var newAttr = attribute.replace(
													fromState, toState);
											card.setAttribute("ng-drag-data",
													newAttr);
											toTd.appendChild(card);
										}, function(reason) {
										    alert("Error updating status "+reason);
										});
							}

							// populate model with teams
							$scope.$watch("team", function() {
								AgileFactory.getSprints($scope.team).$promise
										.then(function(data) {
											$scope.sprints = data;
											var activeSprints = [];
											for (var index in $scope.sprints) {
											   var sprint = $scope.sprints[index];
											   if (sprint.active) {
											      activeSprints.push(sprint);
											   }
											}
											$scope.activeSprints = activeSprints;
										});
							});

							// populate model with tasks
							$scope.$watch("sprint", function() {
								getTasks();
							});

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
	for (var userId in _tasks.userIdToName) {
		var name = _tasks.userIdToName[userId];
		if (name === userName) {
		   resultUserId = userId;
		   break;
		}
   }
	return resultUserId;
}
