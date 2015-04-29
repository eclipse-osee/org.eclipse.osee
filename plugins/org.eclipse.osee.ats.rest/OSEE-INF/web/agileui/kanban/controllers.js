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
		.module('kanbanApp.controllers', [ 'ngDraggable' ])
		.controller(
				'tasksController',
				function($scope, taskCreator) {
					"use strict"
					$scope.nameFilter = null;
					$scope.tasks = {};
					/*
					 * Fetches userName for a userID
					 */
					$scope.userName = function(userID) {
						return getUserName(userID);
					}

					taskCreator.getProjects().success(function(response) {
						$scope.projects = response;
					});

					/*
					 * State transition
					 */
					$scope.onDropComplete = function(data, evt, toState) {
						var fromState = data.taskState;
						if (fromState !== toState) {

							var _data = [];
							_data.guid = data.guid;
							_data.toStateUsers = getAssigneesForTask(data.guid)
							_data.toState = toState;

							update(_data, evt, data.guid, fromState, toState);
							data.taskState = toState
							return;

						}
					}

					/*
					 * Fetches tasks for selected combination of project and
					 * team
					 */
					var getTasks = function() {
						var selectedProject = _
								.filter(
										$scope.projects,
										function(_project, key) {
											return _project.name === $scope.project.name;
										});
						var selectedTeam = _.filter($scope.teams, function(
								_team, key) {
							return _team.name === $scope.team.name;
						});

						if (!_.isNull(selectedProject)
								&& !_.isEmpty(selectedProject)
								&& !_.isNull(selectedTeam)
								&& !_.isEmpty(selectedTeam)) {
							taskCreator
									.getTasks(selectedTeam[0].uuid,
											selectedProject[0].guid)
									.success(
											function(response) {
												_tasks = response;
												$scope.availableStates = response["availableStates"];
												
												// process assigneesToUuids
												var assignees = {};
												for (var i = 0; i < response.assigneesToTaskUuids.length; i++) {
													var entry = response.assigneesToTaskUuids[i];
													var name = response.userIdToName[entry.assigneeId];
													assignees[name] = entry.taskUuids;
												}

												var temp = _.keys(
														assignees)
														.sort();

												$scope.assignees = _
														.keys(assignees);
												_tasks.assignees = assignees;
												
												// process assigneesToUuids
												var states = {};
												for (var i = 0; i < response.statesToTaskUuids.length; i++) {
													var entry = response.statesToTaskUuids[i];
													var name = entry.name;
													states[name] = entry.taskUuids;
												}

												_tasks.states = states;

											});
						}
					}

					/*
					 * Sets teams in scope for given project
					 */
					var getTeams = function() {
						var selectedProject = _
								.filter(
										$scope.projects,
										function(_project, key) {
											return _project.name === $scope.project.name;
										});
						if (!_.isNull(selectedProject)
								&& !_.isEmpty(selectedProject)) {
							taskCreator.getTeams(selectedProject[0].guid)
									.success(function(response) {
										$scope.teams = response;
									});
						}

					}

					/*
					 * On state transition updates DOM and CSS for the card
					 */
					var update = function(data, evt, taskId, fromState, toState) {
						var idDropedToTd = data.toStateUsers[0] + '-' + toState;
						var idDraggedCard = taskId;

						/*
						 * On state transition updates the new states to the
						 * server Note: Currently commented in this sample code.
						 * Please enable when required.
						 */
						// taskCreator
						// .updateStatus(
						// data,
						// function(response) {
						// if (response.status === "success") {
						updateTask(taskId, fromState, toState);
						var toTd = document.getElementById(idDropedToTd);
						var card = document.getElementById(idDraggedCard);
						card.setAttribute("class", "card " + toState)
						var attribute = card.getAttribute("ng-drag-data");
						var newAttr = attribute.replace(fromState, toState);
						card.setAttribute("ng-drag-data", newAttr);
						toTd.appendChild(card);
						// }
						// });
					}

					// populate model with teams
					$scope.$watch("project", function() {
						getTeams();
					});

					// populate model with tasks
					$scope.$watch("team", function() {
						getTasks();
					});

				}).directive(
				// Directive for cards element
				'cards',
				function() {
					return {
						restrict : 'EA',
						link : function(scope, element, attrs) {
							var tasksAsCards = getTasksFor(scope.state.name,
									scope.assignee);
							scope.tasks = tasksAsCards;

						},
						replace : true,
						templateUrl : 'kanban/kanbanCard.html',
					}
				}).filter(
				// Truncate characters for long text
				'truncate',
				function() {
					return function(text, length, end) {
						if (isNaN(length))
							length = 10;

						if (end === undefined)
							end = "...";

						if (text.length <= length
								|| text.length - end.length <= length) {
							return text;
						} else {
							return String(text).substring(0,
									length - end.length)
									+ end;
						}

					};
				});

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
var getUserName = function(userID) {
	return _tasks.userIdToName[userID];
}
