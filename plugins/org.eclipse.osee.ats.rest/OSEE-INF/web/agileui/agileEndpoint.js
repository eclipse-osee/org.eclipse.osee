
angular.module('AgileApp').factory('AgileEndpoint',
		[ '$resource', function($resource) {

			var factory = {};

			var teamMembersResource = $resource('/ats/agile/team/:teamid/member');
			var teamResource = $resource('/ats/agile/team');
			var teamsTokenResource = $resource('/ats/agile/team/token');
			var teamTokenResource = $resource('/ats/agile/team/:teamid/token');
			var teamSingleResource = $resource('/ats/agile/team/:teamid');
			var teamAisResource = $resource('/ats/agile/team/:teamid/ai');
			var featuresResource = $resource('/ats/agile/team/:teamid/feature');
			var workPackageResource = $resource('/ats/agile/team/:teamid/workpackage');
			var featureSingleResource = $resource('/ats/agile/team/:teamid/feature/:featureid');
			var sprintResource = $resource('/ats/agile/team/:teamid/sprint');
			var sprintConfigResource = $resource('/ats/agile/team/:teamid/sprint/:sprintid/config');
			var sprintTokenResource = $resource('/ats/agile/team/:teamid/sprint/token');
			var sprintCurrentResource = $resource('/ats/agile/team/:teamid/sprintcurrent');
			var sprintSingleResource = $resource('/ats/agile/team/:teamid/sprint/:sprintid');
			var sprintForKbResource = $resource('/ats/agile/team/:teamid/sprint/:sprintid/kb');
			var sprintItemsResource = $resource('/ats/agile/team/:teamid/sprint/:sprintid/item');
			var sprintConfigResource = $resource('/ats/agile/team/:teamid/sprint/:sprintid/config');
			var backlogTokenResource = $resource('/ats/agile/team/:teamid/backlog/token');
			var backlogResource = $resource('/ats/agile/team/:teamid/backlog');
			var backlogItemsResource = $resource('/ats/agile/team/:teamid/backlog/item');
			var actionResource = $resource('/ats/action');
			var itemResource = $resource('/ats/agile/items/:itemid', 
					{}, { 'update': { method:'PUT' } });

			// ////////////////////////////////////
			// Agile Item
			// ////////////////////////////////////

			factory.updateStatus = function(data) {
				return itemResource.update(data);
			}
			
			factory.createTask = function(data) {
				return actionResource.save(data);
			}

			// ////////////////////////////////////
			// Agile Teams
			// ////////////////////////////////////
			factory.getTeams = function() {
				return teamResource.query();
			}

			factory.getTeamsTokens = function() {
				return teamsTokenResource.query();
			}

			factory.getTeamToken = function(team) {
				return teamTokenResource.get({teamid: team.id});
			}

			factory.getTeamSingle = function(team) {
				return teamSingleResource.get({teamid: team.id})
			}

			factory.getTeamAis = function(team) {
				return teamAisResource.query({teamid: team.id})
			}

			factory.deleteTeam = function(team) {
				return teamSingleResource.delete(team);
			}

			factory.addNewTeam = function(teamName) {
				var toPost = {};
				toPost.name = teamName;
				toPost.active = true;
				return teamResource.save(toPost);
			}

			factory.getWorkPackages = function(team) {
				return workPackageResource.query({teamid: team.id});
			}

			factory.getTeamMembers = function(team) {
				return teamMembersResource.query({teamid: team.id});
			}

			// ////////////////////////////////////
			// Agile Feature Groups
			// ////////////////////////////////////
			factory.getFeatureGroups = function(team) {
				return featuresResource.query({teamid: team.id});
			}
			
			factory.deleteFeatureGroup = function(team) {
				return featureSingleResource.delete(team);
			}
			
			factory.addNewFeatureGroup = function(team, featureGroupName) {
				var param = {'uuid': team.uuid};
				var toPost = {};
				var newGroup = {};
				newGroup.teamUuid = team.uuid;
				newGroup.name = featureGroupName;
				newGroup.active = true;
				return featuresResource.save(param, newGroup);
			}

			// ////////////////////////////////////
			// Agile Sprint
			// ////////////////////////////////////
			factory.getSprints = function(team) {
				return sprintResource.query({teamid: team.id});
			}
			
			factory.getSprintsTokens = function(team) {
				return sprintTokenResource.query({teamid: team.id});
			}
			
			factory.getSprintCurrent = function(team) {
				var param = {};
				param.uuid = team.uuid;
				return sprintCurrentResource.get(param);
			}
			
			factory.getSprintForKb = function(team, sprint) {
				return sprintForKbResource.get({teamid: team.id, sprintid:sprint.id});
			}
			
			factory.getSprint = function(teamId, sprintId) {
				return sprintSingleResource.get({teamid: teamId, sprintid:sprintId});
			}
			
			factory.deleteSprint = function(team) {
				return sprintSingleResource.delete(team);
			}
			
			factory.addNewSprint = function(team, sprintName) {
				var param = {};
				param.uuid = team.uuid;
				var newSprint = {};
				newSprint.teamUuid = team.uuid;
				newSprint.name = sprintName;
				newSprint.active = true;
				return sprintResource.save(param, newSprint);
			}

			factory.getSprintItems = function(team, sprint) {
				return sprintItemsResource.query({teamid: team.id, sprintid:sprint.id})
			}

			factory.getSprintConfig = function(team, sprint) {
				return sprintConfigResource.get({teamid: team.id, sprintid:sprint.id})
			}

			factory.updateSprint = function(team, sprint) {
				return sprintSingleResource.save({teamid:team.id, sprintid:sprint.id}, sprint)
			}

			factory.updateSprintConfig = function(team, sprint) {
				return sprintConfigResource.save({teamid:team.id, sprintid:sprint.id}, sprint.config)
			}

			// ////////////////////////////////////
			// Agile Backlog
			// ////////////////////////////////////
			factory.createBacklog = function(team, backlogName) {
				var param = {'uuid': team.uuid};
				var newBacklog = {};
				newBacklog.teamUuid = team.uuid;
				newBacklog.name = backlogName;
				newBacklog.active = true;
				return backlogResource.save(param, newBacklog);
			}

			factory.enterBacklog = function(team, backlogUuid) {
				var param = {};
				param.teamUuid = team.uuid;
				param.uuid = backlogUuid; 
				return backlogResource.save(param);
			}
			
			factory.getBacklog = function(team) {
				return backlogResource.get({teamid: team.id});
			}

			factory.getBacklogToken = function(team) {
				return backlogTokenResource.get({teamid: team.id});
			}

			factory.getBacklogItems = function(team) {
				return backlogItemsResource.query({teamid: team.id});
			}

			return factory;

		} ]);