
angular.module('AgileApp').factory('AgileEndpoint',
		[ '$resource', function($resource) {

			var factory = {};

			var imagesResource = $resource('/ats/config/image');

			// programs
			var activeProgramsTokenResource = $resource('/ats/agile/program/token?active=true');
			var programTokenResource = $resource('/ats/agile/program/:programid/token');
			var programAtwResource = $resource('/ats/agile/program/:programid/atw');
			var programUiGridResource = $resource('/ats/agile/program/:programid/uigrid');
			
			// teams
			var activeTeamsTokenResource = $resource('/ats/agile/team/token?active=true');
			var teamMembersResource = $resource('/ats/agile/team/:teamid/member');
			var teamResource = $resource('/ats/agile/team');
			var teamsTokenResource = $resource('/ats/agile/team/token');
			var teamTokenResource = $resource('/ats/agile/team/:teamid/token');
			var teamSingleResource = $resource('/ats/agile/team/:teamid');
			var teamAisResource = $resource('/ats/agile/team/:teamid/ai');
			
			// program backlog item
			var programProgramBacklogItemResource = $resource('/ats/agile/program/:programid/backlogitem');
			var programBacklogItemResource = $resource('/ats/agile/programbacklogitem/:programbacklogitemid');

			// program feature
			var programProgramFeatureResource = $resource('/ats/agile/program/:programid/feature');
			var programBacklogItemResource = $resource('/ats/agile/programfeature/:programfeatureid');

			// feature group resource
			var featuresResource = $resource('/ats/agile/team/:teamid/feature');
			var featureSingleResource = $resource('/ats/agile/team/:teamid/feature/:featureid');
			
			// work package resource
			var workPackageResource = $resource('/ats/agile/team/:teamid/workpackage');
			
			// sprint
			var sprintResource = $resource('/ats/agile/team/:teamid/sprint');
			var sprintConfigResource = $resource('/ats/agile/team/:teamid/sprint/:sprintid/config');
			var sprintTokenResource = $resource('/ats/agile/team/:teamid/sprint/token');
			var sprintCurrentResource = $resource('/ats/agile/team/:teamid/sprintcurrent');
			var sprintSingleResource = $resource('/ats/agile/team/:teamid/sprint/:sprintid');
			var sprintForKbResource = $resource('/ats/agile/team/:teamid/sprint/:sprintid/kb');
			var sprintItemsResource = $resource('/ats/agile/team/:teamid/sprint/:sprintid/item');
			var sprintConfigResource = $resource('/ats/agile/team/:teamid/sprint/:sprintid/config');
			
			// program backlog
			var backlogTokenResource = $resource('/ats/agile/team/:teamid/backlog/token');
			var backlogResource = $resource('/ats/agile/team/:teamid/backlog');
			var backlogItemsResource = $resource('/ats/agile/team/:teamid/backlog/item');
			
			// action
			var actionResource = $resource('/ats/action');
			
			// task
			var itemResource = $resource('/ats/agile/items/:itemid', 
					{}, { 'update': { method:'PUT' } });

			// ////////////////////////////////////
			// Config 
			// ////////////////////////////////////
			factory.getImages = function() {
				return imagesResource.query();
			}

			// ////////////////////////////////////
			// Programs 
			// ////////////////////////////////////
			factory.getActiveProgramsTokens = function() {
				return activeProgramsTokenResource.query();
			}

			factory.getProgramToken = function(program) {
				return programTokenResource.get({programid: program.id});
			}

			factory.getProgramAtw = function(program) {
				return programAtwResource.query({programid: program.id});
			}

			factory.getProgramItems = function(program) {
				return programUiGridResource.get({programid: program.id});
			}

			// ////////////////////////////////////
			// Program Backlog Items
			// ////////////////////////////////////

			factory.updateProgramBacklogItem = function(item) {	
				return programProgramBacklogItemResource.save({'programid': item.programId}, item);
			}
			
			factory.deleteProgramBacklogItem = function(item) {	
				return programBacklogItemResource.delete({'programbacklogitemid': item.backlogitemid});
			}
			
			// ////////////////////////////////////
			// Program Feature
			// ////////////////////////////////////

			factory.updateProgramFeature = function(item) {	
				return programProgramFeatureResource.save({'programid': item.programId}, item);
			}
			
			factory.deleteProgramFeature = function(item) {	
				return programFeatureResource.delete({'programfeatureitemid': item.featureid});
			}
			
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
			factory.getActiveTeamsTokens = function() {
				return activeTeamsTokenResource.query();
			}

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
				var param = {'id': team.id};
				var toPost = {};
				var newGroup = {};
				newGroup.teamId = team.id;
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
				param.id = team.id;
				return sprintCurrentResource.get(param);
			}
			
			factory.getSprintForKb = function(team, sprint) {
				return sprintForKbResource.get({teamid: team.id, sprintid:sprint.id});
			}
			
			factory.getSprint = function(team, sprint) {
				return sprintSingleResource.get({teamid: team.id, sprintid:sprint.id});
			}
			
			factory.deleteSprint = function(team) {
				return sprintSingleResource.delete(team);
			}
			
			factory.addNewSprint = function(team, sprintName) {
				var param = {};
				param.id = team.id;
				var newSprint = {};
				newSprint.teamId = team.id;
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
				var param = {'id': team.id};
				var newBacklog = {};
				newBacklog.teamId = team.id;
				newBacklog.name = backlogName;
				newBacklog.active = true;
				return backlogResource.save(param, newBacklog);
			}

			factory.enterBacklog = function(team, backlogId) {
				var param = {};
				param.teamId = team.id;
				param.id = backlogId; 
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