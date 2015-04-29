/**
 * Agile Factory
 */
angular.module('AgileApp').factory('AgileFactory',
		[ '$resource', function($resource) {

			var factory = {};

			var teamResource = $resource('/ats/agile/team');
			var teamSingleResource = $resource('/ats/agile/team/:uuid');
			var featuresResource = $resource('/ats/agile/team/:uuid/feature');
			var featureSingleResource = $resource('/ats/agile/team/:teamUuid/feature/:uuid');
			var sprintResource = $resource('/ats/agile/team/:uuid/sprint');
			var sprintCurrentResource = $resource('/ats/agile/team/:uuid/sprintcurrent');
			var sprintSingleResource = $resource('/ats/agile/team/:teamUuid/sprint/:uuid');
			var sprintForKbResource = $resource('/ats/agile/team/:teamUuid/sprint/:uuid/kb');
			var backlogResource = $resource('/ats/agile/team/:uuid/backlog');
			var backlogItemsResource = $resource('/ats/agile/team/:uuid/backlog/item');
			var itemResource = $resource('/ats/agile/items/:uuid', 
					{}, { 'update': { method:'PUT' } });

			// ////////////////////////////////////
			// Agile Item
			// ////////////////////////////////////

			factory.updateStatus = function(data) {
				return itemResource.update(data);
			}

			// ////////////////////////////////////
			// Agile Teams
			// ////////////////////////////////////
			factory.getTeams = function() {
				return teamResource.query();
			}

			factory.getTeamSingle = function(team) {
				return teamSingleResource.get(team)
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

			// ////////////////////////////////////
			// Agile Feature Groups
			// ////////////////////////////////////
			factory.getFeatureGroups = function(team) {
				return featuresResource.query(team);
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
				return sprintResource.query(team);
			}
			
			factory.getSprintCurrent = function(team) {
				var param = {};
				param.uuid = team.uuid;
				return sprintCurrentResource.get(param);
			}
			
			factory.getSprintForKb = function(sprint) {
				return sprintForKbResource.get(team, sprint);
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
				var param = {};
				param.uuid = team.uuid;
				return backlogResource.get(param);
			}

			factory.getBacklogItems = function(team) {
				return backlogItemsResource.query(team)
			}

			return factory;

		} ]);