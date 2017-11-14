/**
 *  Cpa Factory
 */

angular.module('CpaApp').factory('CpaFactory', ['$resource', function($resource) {
	
	var factory = {};
	
	var projectResource = $resource('/ats/cpa/program');
	var projectCpaResource = $resource('/ats/cpa/program/:id');
	var userResource = $resource('/ats/user', {active: 'Active'});
	var configResource = $resource('/ats/cpa/config');
	var decisionResource = $resource('/ats/cpa/decision', {}, {'save2' : {method: 'POST', isArray:true}});
	var buildsResource = $resource('/ats/cpa/program/:id/build');
	var duplicateResource = $resource('/ats/cpa/duplicate');
	
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
		toPost.ids = [];
		toPost.assignees = [];

		var assigneeStr = "";
		for(var i = 0; i < newAssignees.length; i++) {
			assigneeStr += newAssignees[i].name + ";";
			toPost.assignees.push(newAssignees[i].id);
		}
		assigneeStr = assigneeStr.slice(0, -1);
		
		for(var i = 0; i < cpas.length; i++) {
			toPost.ids.push(cpas[i].id);
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
		toPost.ids = [];
		toPost.rationale = newRationale;
		
		for(var i = 0; i < cpas.length; i++) {
			toPost.ids.push(cpas[i].id);
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
		toPost.ids = [];
		toPost.applicability = applicability; 

		for(var i = 0; i < items.length; i++) {
			toPost.ids.push(items[i].id);
		}
		
		decisionResource.save2(toPost, function() {
			for(var i = 0; i < items.length; i++) {
				items[i].applicability = applicability;
			}
		}, function(err){
			for(var i = 0; i < items.length; i++) {
				items[i].applicability = "Err - Refresh Table";
			}
		});
		
	}
	
	factory.updateDuplicatedPcrId = function(item, pcrId) {
		var items = item instanceof Array ? item : [item];
		var toPost = {};
		toPost.ids = [];
		toPost.duplicatedPcrId = pcrId; 

		for(var i = 0; i < items.length; i++) {
			toPost.ids.push(items[i].id);
		}
		
		decisionResource.save2(toPost).$promise.then(function(value) {
			for(var i = 0; i < items.length; i++) {
				items[i].duplicatedPcrId = pcrId;
				items[i].duplicatedPcrLocation = null;
				if(items[i].id != value[i].id) {
					for(var j = 0; j < value.length; j++) {
						if(items[i].id != value[j].id) {
							items[i].duplicatedPcrLocation = value[j].duplicatedPcrLocation;
							break;
						}
					}
				} else {
					items[i].duplicatedPcrLocation = value[i].duplicatedPcrLocation;
				}
			}
		}).catch(function(err) {
			for(var i = 0; i < items.length; i++) {
				items[i].duplicatedPcrId = "Err - Refresh Table";
				items[i].duplicatedPcrLocation = "Err - Refresh Table";
			}
		});
		
	}
	
	factory.getVersions = function(build) {
		return buildsResource.query(build);
	}
	
	// called when creating new pcr for issue
	factory.duplicatePcr = function(item, pcrId) {
		var items = item instanceof Array ? item : [item];
		var toPost = {};
		toPost.ids = [];

		for(var i = 0; i < items.length; i++) {
			toPost.ids.push(items[i].id);
		}
		
		decisionResource.save2(toPost).$promise.then(function(value) {
			for(var i = 0; i < items.length; i++) {
				items[i].duplicatedPcrId = pcrId;
				items[i].duplicatedPcrLocation = null;
				if(items[i].id != value[i].id) {
					for(var j = 0; j < value.length; j++) {
						if(items[i].id != value[j].id) {
							items[i].duplicatedPcrLocation = value[j].duplicatedPcrLocation;
							break;
						}
					}
				} else {
					items[i].duplicatedPcrLocation = value[i].duplicatedPcrLocation;
				}
			}
		}).catch(function(err) {
			for(var i = 0; i < items.length; i++) {
				items[i].duplicatedPcrId = "Err - Refresh Table";
				items[i].duplicatedPcrLocation = "Err - Refresh Table";
			}
		});
		
	}
	
	factory.duplicateIssue = function(item, selectedProgram, version) {
		var items = item instanceof Array ? item : [item];
		for(var i = 0; i < items.length; i++) {
			duplicate(items[i], selectedProgram, version);
		}
	}
	
	function duplicate(item, selectedProgram, version) {
		var toPost = {cpaId: item.id, programId: selectedProgram.id, versionId: version.id};
		duplicateResource.save(toPost, function(data) {
			item.duplicatedPcrId = data.duplicatedPcrId;
			item.duplicatedPcrLocation = data.duplicatedPcrLocation;
		}, function(err){
			item.duplicatedPcrId = "Err - Refresh Table";
			item.duplicatedPcrLocation = "Err - Refresh Table";
		});
	}
	
	return factory;
	
}]);