angular
		.module('AgileApp')
		.factory(
				'Menu',
				[ function() {
					return {
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