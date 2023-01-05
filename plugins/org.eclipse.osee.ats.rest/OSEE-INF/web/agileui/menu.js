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
angular
		.module('AgileApp')
		.factory(
				'Menu',
				[ function() {
					return {
						openProgram : function(program) {
							window.location.assign("main#/program?program="
									.concat(program.id))
						},
						
						openTeamForTeam : function(team) {
							window.location.assign("main#/team?team="
									.concat(team.id))
						},

						openConfigForTeam : function(team) {
							window.location.assign("main#/config?team="
									.concat(team.id))
						},

						openKanbanForTeam : function(team) {
							window.location.assign("main#/kanban?team="
									.concat(team.id))
						},

						openBurndownForTeam : function(team) {
							window.location
									.assign("main#/report?team="
											.concat(team.id)
											.concat(
													"&reporttype=burndown&reportname=Burn-Down"))
						},

						openBurnupForTeam : function(team) {
							window.location.assign("main#/report?team=".concat(
									team.id).concat(
									"&reporttype=burnup&reportname=Burn-Up"))
						},

						openBacklogForTeam : function(team) {
							window.location
									.assign("main#/backlog?team=".concat(
											team.id).concat("&default=backlog"))
						},

						openNewTaskForTeam : function(team) {
							window.location.assign("main#/newTask?team="
									.concat(team.id))
						},

						openSprintForTeam : function(team) {
							window.location.assign("main#/sprint?team=".concat(
									team.id).concat("&default=sprint"))
						},

						openSummaryForTeam : function(team) {
							window.location.assign("main#/report?team=".concat(
									team.id).concat(
									"&reporttype=summary&reportname=Summary"))
						},

						openDataForTeam : function(team) {
							window.location.assign("main#/report?team=".concat(
									team.id).concat(
									"&reporttype=data&reportname=Data"))
						}
					}
				} ]);