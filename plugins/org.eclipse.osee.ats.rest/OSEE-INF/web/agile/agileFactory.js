/**
 * Agile Factory
 */
angular.module('AgileApp').factory('AgileFactory',
		[ '$resource', function($resource) {

			var factory = {};

			var teamResource = $resource('/ats/agile/team');
			var teamSingleResource = $resource('/ats/agile/team/:uuid');
			var teamFeatureResource = $resource('/ats/agile/team/:uuid/feature');
			var featureResource = $resource('/ats/agile/team/feature/:uuid');

			// ////////////////////////////////////
			// Agile Teams
			// ////////////////////////////////////
			factory.getTeams = function() {
				return teamResource.query();
			}

			factory.getTeamSingle = function(team) {
				return teamSingleResource.query(team)
			}

			factory.deleteTeam = function(team) {
				return teamSingleResource.delete(team);
			}

			factory.addNewTeam = function(teamName) {
				var toPost = {};
				toPost.name = teamName;
				return teamResource.save(toPost);

			}

			// ////////////////////////////////////
			// Agile Feature Groups
			// ////////////////////////////////////
			factory.deleteFeatureGroup = function(team) {
				return featureResource.delete(team);
			}
			
			factory.addNewFeatureGroup = function(team, featureGroupName) {
				var toPost = {};
				toPost.teamUuid = team.uuid;
				toPost.name = featureGroupName;
				return teamFeatureResource.save(toPost);
			}

			return factory;

		} ]);