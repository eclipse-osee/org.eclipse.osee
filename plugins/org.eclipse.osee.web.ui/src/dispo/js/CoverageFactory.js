app.factory('CoverageFactory', function() {
	var CoverageFactory = {};
	
	var isCompleteCoverage = function(annotation) {
    	if(annotation.resolutionType === "" || annotation.resolution === "") {
    		return false;
    	} else {
    		return true;
    	}
    }
	
	CoverageFactory.getTextResolution = function(annotation) {
		if (annotation.isLeaf) {
            return annotation.resolution;
        } else {
        	if(annotation.isTopLevel) {
        		return annotation.percentCompleteStr;
        	} else {
        		return annotation.childMetadata.completeCount + " / " + annotation.childMetadata.totalCount;
        	}
        }
	}
	
	CoverageFactory.getLastTextResolution = function(annotation) {
		if (annotation.isLeaf && annotation.lastResolution!=annotation.resolution) {
			return annotation.lastResolution;
        } else {
        	return "";
        }
	}
	
	CoverageFactory.setTextForNonDefaultAnnotations = function(annotations, discrepancies) {
		for(var i = 0; i < annotations.length; i++) {
			if(!annotations[i].isDefault) { 
        		var coveredId = annotations[i].idsOfCoveredDiscrepancies[0]
        		
        		var discrepancyAddress = discrepancies[coveredId];
        		if(discrepancyAddress != null)
	        			return annotations[i].customerNotes;
	        		else 
	        			return annotations[i].customerNotes = "no text found";     		
    		} 
		}
	}
    
	var getReasonWhyIncomplete = function(annotation) {
		var toReturn = [];
    	if(annotation.resolutionType === "") {
    		toReturn.push("resolutionType");
    	} 
    	if(annotation.resolution === "") {
    	    toReturn.push("resolution");
    	} 
    	return toReturn;
    }
    
	var isColumnAffectCompleteness = function(col) { 
    	if(col.field == "resolutionType" || col.field == "resolution") {
    		return true;
    	} else {
    		return false;
    	}
    }
	
	CoverageFactory.writeoutNode = function(childArray, currentLevel, dataArray) {
    	var id = 0;
        childArray.forEach(function(childNode) {
            if (childNode.children.length > 0) {
                childNode.$$treeLevel = currentLevel;
                id = childNode.id;
            } else {
                if ((id != childNode.parentId) || (childNode.id == childNode.parentId)) {
                    childNode.$$treeLevel = currentLevel;
                }
            }
            dataArray.push(childNode);
            CoverageFactory.writeoutNode(childNode.children, currentLevel + 1, dataArray);
        });
    };
    
    CoverageFactory.treeAnnotations = function(annotations) {
        var annotationsStack = annotations.slice();
        var toReturn = [];

        while (annotationsStack.length > 0) {
            var annotation = annotationsStack[annotationsStack.length - 1] // peek
            if (annotation.name.match(/\d+\.\w+\..+/gi)) {
                var parentAnnotation = {};
                annotation.id = annotation.guid;
                var leadingLocRefs = annotation.locationRefs.split(".")[0];
                parentAnnotation.locationRefs = leadingLocRefs;
                parentAnnotation.id = annotation.guid;
                parentAnnotation.guid = annotation.guid;
                parentAnnotation.parentId = -1;

                var metadata = {};
                metadata.childrenComplete = true;
            	metadata.totalCount = 0;
            	metadata.completeCount = 0;
            	metadata.runningTotalCount = 0;
            	metadata.runningCompleteCount = 0;
            	parentAnnotation.isTopLevel = true;
                parentAnnotation.childMetadata = metadata;

                var childrenStack = getAnnotationsStartsWith(annotationsStack, leadingLocRefs, false, true);
                var childrenAsTree = createTreeAnnotations(annotationsStack, parentAnnotation, childrenStack, 1, metadata);
                parentAnnotation.children = childrenAsTree;
                parentAnnotation.$treeLevel = 0;
                populatePercentData(parentAnnotation);
                
                toReturn.push(parentAnnotation);
            } else {
                annotationsStack.pop();
                annotation.children = [];
                annotation.$$treeLevel = -1;
                annotation.isLeaf = true;

                toReturn.push(annotation);
            }
        }

        return toReturn;
    }
    
    // annotationsStack is the entire remaining stack of annotations
    // parent is the calling parent place holder annotation
    // childrenStack is the annotations that are to be under the parent
    // level is the current tree level
    // Metadata is used to pass information up to the calling parent annotation
    var createTreeAnnotations = function(annotationsStack, parent, childrenStack, level, metadata) {
        if (level >= 2) {
        	metadata.childrenComplete = true;
        	metadata.totalCount = 0;
        	metadata.completeCount = 0;
            var children = getAnnotationsStartsWith(childrenStack, parent.locationRefs, false, true);
            for (var i = 0; i < children.length; i++) {
            	metadata.totalCount++;
                children[i].$$treeLevel = level;

                children[i].children = [];
                children[i].parentId = parent.id;
                children[i].parentRef = parent;
                children[i].isLeaf = true;
                if (!children[i].isDefault && !isCompleteCoverage(children[i])) {
                	metadata.allComplete = false;
                } else {
                	metadata.completeCount++;
                }
                metadata.customerNotes = children[i].customerNotes;
            }
            return children;
        } else {
            var toReturn = [];
            while (childrenStack.length > 0) {
            	metadata.totalCount++;
                var annotation = childrenStack[0]
                var leadingLocRefs = createLocRef(annotation.locationRefs, level);

                var self = {};
                self.id = generateId();
                self.parentId = parent.id;
                self.parentRef = parent;
                self.locationRefs = leadingLocRefs;
                self.$$treeLevel = level;
                var childMetadata = {};
                self.children = createTreeAnnotations(annotationsStack, self, childrenStack, level + 1, childMetadata);
                self.childMetadata = childMetadata
                metadata.childrenComplete = metadata.childrenComplete && childMetadata.childMetadata;
                if(metadata.childrenComplete) {
                	metadata.completeCount++;
                }
                metadata.runningTotalCount += childMetadata.totalCount;
                metadata.runningCompleteCount += childMetadata.completeCount;
                self.customerNotes = childMetadata.customerNotes;
                
                toReturn.push(self);
            }
            return toReturn;
        }
    }
    
    CoverageFactory.updatePercent = function(colDef, oldValue, annotation) {
    	var possibleCountChange = 0;
    	
    	// We only need to update the count +/- 1 if the change is in one of the completeness fields 
    	if(isColumnAffectCompleteness(colDef)) {
    		// We checked in ui-grides call back (gridApi.edit.on.afterCellEdit) that the newValue is different than the old
    		// Thus if it was empty before and the annotation as a whole is complete then the current change did the trick and we can add 1 to the complete count 
    		if(oldValue == "" && isCompleteCoverage(annotation)) {
    			possibleCountChange = 1;        			
    		} else if(annotation[colDef.field] == "" && (getReasonWhyIncomplete(annotation).length == 1 && getReasonWhyIncomplete(annotation)[0] == colDef.field)) {
    			// If the new value is NOW empty we can assume it was not empty before so might need to subtract 1. 
    			// Before being sure we need to make sure the only reason this annotation is now incomplete is because of this column
    			possibleCountChange = -1;        			
    		}
    	} 
    	
    	
    	if(annotation.isLeaf) {
    		var parent = annotation.parentRef;
    		var topLevelParent = annotation;
    		while(parent != undefined && parent != null) {
    			topLevelParent = parent;
    			parent.childMetadata.completeCount += possibleCountChange;
    			parent = parent.parentRef;
    		}
    		if (topLevelParent.childMetadata) {
    			topLevelParent.childMetadata.runningCompleteCount += possibleCountChange;
    			populatePercentData(topLevelParent);
    		}
    	}
    }
    
    var populatePercentData = function(annotation) {
		var str = [];
		var pct = (annotation.childMetadata.runningCompleteCount / annotation.childMetadata.runningTotalCount);
		var pctStr = pct.toLocaleString("en", {style: "percent"});
		str.push(annotation.childMetadata.runningCompleteCount, "/", annotation.childMetadata.runningTotalCount, " (", pctStr, ")");
	    
	    annotation.percentCompleteStr = str.join("");
	    
	    annotation.isAllComplete = false;
	    annotation.isNoneComplete = false;
	    annotation.isSomeComplete = false;
	    annotation.isAlmostComplete = false;
	    	
	    if(pct < .33) {
    	    annotation.isNoneComplete = true;
	    } else if(pct > .33 && pct < .66) {
	    	annotation.isSomeComplete = true;
	    } else if(pct > .66 && pct < 1) {
    	    annotation.isAlmostComplete = true;
	    } else {
    	    annotation.isAllComplete = true;
	    }
    }

    
    var getAnnotationsStartsWith = function(annotationsStack, startsWith, isWholeString, isPop) {
        var searchStr = startsWith; 
    	if(!isWholeString) {
        	searchStr = startsWith + ".";
        }
        var annotationsStackOrig = annotationsStack.slice();
        var toReturn = [];
        var toDelete = [];
        for (var i = 0; i < annotationsStack.length; i++) {
            var annotation = annotationsStackOrig[i];
            if ((!isWholeString && annotation.locationRefs.startsWith(searchStr)) || (isWholeString && annotation.locationRefs == searchStr)) {
                toDelete.push(i);
                toReturn.push(annotation);
            }
        }
        if (isPop) {
            for (var i = toDelete.length - 1; i >= 0; i--) {
                annotationsStack.splice(toDelete[i], 1);
            }
        }
        return toReturn;
    }
    
    var createLocRef = function(fullLocRefs, level) {
        var workingGenLocationRefs = "";
        for (var i = 0; i <= level; i++) {
            if (i > 0) {
                workingGenLocationRefs += ".";
            }
            workingGenLocationRefs += fullLocRefs.split(".")[i];
        }
        return workingGenLocationRefs;
    }
    
    var generateId = function() {
        var text = "";
        var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for (var i = 0; i < 10; i++)
            text += possible.charAt(Math.floor(Math.random() * possible.length));

        return text;
    }
    
    var getText = function(annotation) {
    	if(annotation.customerNotes != undefined && annotation.customerNotes == "") { 
    		var discrepancies = $scope.selectedItem.discrepancies;
    		var covered = annotation.idsOfCoveredDiscrepancies[0]
    		
    		if(!discrepancies[covered] == null)
        			return discrepancies[covered].text;
        		else 
        			return "";     		
		} else {
			return annotation.customerNotes;
		}
    }
	
    
    return CoverageFactory;
});