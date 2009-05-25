/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * Definition of WorkItem. Once created, nothing in this class, or any subclasses, should be modified as these
 * definitions are shared by all instantiations of pages, rules, workflows and widgets.
 * 
 * @author Donald G. Dunne
 */
public abstract class WorkItemDefinition {

   protected String id;
   protected String name;
   protected String parentId;
   protected String description;
   protected Map<String, String> workDataKeyValueMap = new HashMap<String, String>();
   private final Pattern keyValuePattern = Pattern.compile("^(.*?)=(.*)$", Pattern.MULTILINE);
   protected String type;
   public static enum WriteType {
      Update, New
   };

   /**
    * @return the workDataKeyValueMap
    */
   public Map<String, String> getWorkDataKeyValueMap() {
      return workDataKeyValueMap;
   }

   /**
    * @param workDataKeyValueMap the workDataKeyValueMap to set
    */
   public void setWorkDataKeyValueMap(Map<String, String> workDataKeyValueMap) {
      this.workDataKeyValueMap = workDataKeyValueMap;
   }

   /**
    * @param description the description to set
    */
   public void setDescription(String description) {
      this.description = description;
   }

   /**
    * @return the description
    */
   public String getDescription() {
      return description;
   }

   public WorkItemDefinition(String name, String id, String parentId) {
      this(name, id, parentId, null);
   }

   public WorkItemDefinition(String name, String id, String parentId, String type) {
      this(name, id, parentId, type, null);
   }

   public WorkItemDefinition(String name, String id, String parentId, String type, String description) {
      this.name = name;
      this.id = id;
      this.type = type;
      this.parentId = parentId;
      this.description = description;
      if (parentId != null && parentId.equals("")) throw new IllegalArgumentException(
            "parentId must either be null or a valid parent Id.  Invalid for WorkItemDefinition " + id);
      if (type != null && type.equals("")) {
         throw new IllegalArgumentException(
               "type must either be null or a value, not empty string.  Invalid for WorkItemDefinition " + id);
      }
      if (this.id == null || this.id.equals("")) {
         throw new IllegalArgumentException("id must be unique and non-null");
      }

   }

   /**
    * Determine if this workItemDefinition is or has a parent definition of pageId. This will walk up the tree of
    * definition inheritance to answer the question
    * 
    * @param pageId
    * @return boolean
    * @throws OseeCoreException TODO
    */
   public boolean isInstanceOfPage(String pageId, String... visitedPageIds) throws OseeCoreException {
      // Collect all ids already visited
      Set<String> visitedIds = new HashSet<String>();
      for (String visitedId : visitedPageIds)
         visitedIds.add(visitedId);

      // Check for circular dependency
      if (visitedIds.contains(getId())) throw new IllegalStateException(
            "Circular dependency detected.  Id already visited: " + getId());

      // Check for instanceof 
      if (getId().equals(pageId)) return true;

      // If parentId exists, check if it isInstanceOfPage
      if (getParentId() != null) {
         visitedIds.add(getId());
         WorkItemDefinition workItemDefinition = WorkItemDefinitionFactory.getWorkItemDefinition(getParentId());
         return workItemDefinition.isInstanceOfPage(pageId, visitedIds.toArray(new String[visitedIds.size()]));
      }
      return false;
   }

   public boolean hasParent() {
      return (getParentId() != null);
   }

   public WorkItemDefinition getParent() throws OseeCoreException {
      if (!hasParent()) return null;
      return WorkItemDefinitionFactory.getWorkItemDefinition(getParentId());
   }

   @Override
   public String toString() {
      return getArtifactTypeName() + ":    Name: \"" + name +
      //
      "\"    Id: \"" + id + "\"   " +
      //
      (parentId != null ? "   Parent: " + parentId : "");
   }

   /**
    * @return the id
    */
   public String getId() {
      return id;
   }

   /**
    * @param id the id to set
    */
   public void setId(String id) {
      this.id = id;
   }

   /**
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * @return the name
    */
   public String getType() {
      return type;
   }

   /**
    * @return the parentId
    */
   public String getParentId() {
      return parentId;
   }

   public Artifact toArtifact(WriteType writeType) throws OseeCoreException {
      Artifact artifact = WorkItemDefinitionFactory.getWorkItemDefinitionArtifact(getId());
      if (writeType == WriteType.New) {
         // Double-check that doesn't already exist in db.  If so, exception cause duplicates
         if (ArtifactQuery.getArtifactsFromAttribute(WorkItemAttributes.WORK_ID.getAttributeTypeName(), getId(),
               BranchManager.getCommonBranch()).size() > 0) {
            throw new IllegalStateException(
                  "WorkItemDefinition artifact creation failed.  \"" + getId() + "\" already exists.");
         }
      }
      if (artifact == null) {
         // Create new
         artifact = ArtifactTypeManager.addArtifact(getArtifactTypeName(), BranchManager.getCommonBranch());
      }
      //      if (!getId().equals("atsStatePercentCompleteWeight.DefaultWorkflow")) {
      //         System.err.println("Skipping all but atsStatePercentCompleteWeight.DefaultWorkflow - Remove This");
      //         return artifact;
      //      }
      artifact.setDescriptiveName(getName());
      if (getParentId() != null && !getParentId().equals("")) artifact.setSoleAttributeValue(
            WorkItemAttributes.WORK_PARENT_ID.getAttributeTypeName(), getParentId());
      if (getDescription() != null) artifact.setSoleAttributeValue(
            WorkItemAttributes.WORK_DESCRIPTION.getAttributeTypeName(), getDescription());
      artifact.setSoleAttributeValue(WorkItemAttributes.WORK_ID.getAttributeTypeName(), getId());
      if (getType() != null) artifact.setSoleAttributeValue(WorkItemAttributes.WORK_TYPE.getAttributeTypeName(),
            getType());
      if (workDataKeyValueMap.size() > 0) {
         Set<String> keyValues = new HashSet<String>();
         for (Entry<String, String> entry : workDataKeyValueMap.entrySet()) {
            keyValues.add(entry.getKey() + "=" + entry.getValue());
         }
         artifact.setAttributeValues(WorkItemAttributes.WORK_DATA.getAttributeTypeName(), keyValues);
      }
      WorkItemDefinitionFactory.cacheWorkItemDefinitionArtifact(writeType, this, artifact);
      return artifact;
   }

   public abstract String getArtifactTypeName();

   public void setType(String type) {
      this.type = type;
   }

   public void loadWorkDataKeyValueMap(Artifact artifact) throws OseeCoreException {
      for (String value : artifact.getAttributesToStringList(WorkItemAttributes.WORK_DATA.getAttributeTypeName())) {
         Matcher m = keyValuePattern.matcher(value);
         if (m.find()) {
            addWorkDataKeyValue(m.group(1), m.group(2));
         } else {
            throw new OseeStateException("Illegal value for WorkData; must be key=value");
         }
      }
   }

   public String getWorkDataValue(String key) {
      return workDataKeyValueMap.get(key);
   }

   public void addWorkDataKeyValue(String key, String value) {
      workDataKeyValueMap.put(key, value);
   }

   /**
    * @param name the name to set
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    * @param parentId the parentId to set
    */
   public void setParentId(String parentId) {
      this.parentId = parentId;
   }

}
