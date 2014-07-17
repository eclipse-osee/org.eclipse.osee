/**
 *  Cpa Factory
 */

angular.module('CpaApp').factory('CpaFactory', ['$resource', function($resource) {
	
	var factory = {};
	
	var projectResource = $resource('/ats/cpa/program');
	var projectCpaResource = $resource('/ats/cpa/program/:uuid');
	var userResource = $resource('/ats/user', {active: 'Active'});
	var configResource = $resource('/ats/cpa/config');
	var decisionResource = $resource('/ats/cpa/decision');
	
	var users = userResource.query();
	var config = configResource.get();
	
	factory.getConfig = function() {
		return config;
	}
	
	factory.getProjects = function() {
		return projectResource.query();
	}
	
	factory.getProjectCpas = function(project) {
		return projectCpaResource.query(project)
	}
	
	factory.getUsers = function() {
		return users;
	}
	
	factory.updateAssignees = function(cpas, newAssignees) {
		var cpaArr = cpas instanceof Array ? cpas : [cpas];
		var assigneeArr = newAssignees instanceof Array ? newAssignees : [newAssignees];
		
		var toPost = {};
		toPost.uuids = [];
		toPost.assignees = [];

		var assigneeStr = "";
		for(var i = 0; i < newAssignees.length; i++) {
			assigneeStr += newAssignees[i].name + ";";
			toPost.assignees.push(newAssignees[i].uuid);
		}
		assigneeStr = assigneeStr.slice(0, -1);
		
		for(var i = 0; i < cpas.length; i++) {
			toPost.uuids.push(cpas[i].uuid);
		}

		decisionResource.save(toPost, function() {
			for(var i = 0; i < cpas.length; i++) {
				cpas[i].assignees = assigneeStr;
			}
		});
		
	}
	
	factory.updateRationale = function(cpas, newRationale) {
		var cpaArr = cpas instanceof Array ? cpas : [cpas];
		
		var toPost = {};
		toPost.uuids = [];
		toPost.rationale = newRationale;
		
		for(var i = 0; i < cpas.length; i++) {
			toPost.uuids.push(cpas[i].uuid);
		}
		
		decisionResource.save(toPost, function() {
			for(var i = 0; i < cpas.length; i++) {
				cpas[i].rationale = newRationale;
			}
		}, function(err){
			for(var i = 0; i < cpas.length; i++) {
				cpas[i].rationale = "Err - Refresh Table";
			}
		});
		
	}
	
	factory.updateApplicability = function(item, applicability) {
		var items = item instanceof Array ? item : [item];
		var toPost = {};
		toPost.uuids = [];
		toPost.applicability = applicability; 

		for(var i = 0; i < items.length; i++) {
			toPost.uuids.push(items[i].uuid);
		}
		
		decisionResource.save(toPost, function() {
			for(var i = 0; i < items.length; i++) {
				items[i].applicability = applicability;
			}
		}, function(err){
			for(var i = 0; i < items.length; i++) {
				items[i].applicability = "Err - Refresh Table";
			}
		});
		
	}
	
	return factory;
	
}]);