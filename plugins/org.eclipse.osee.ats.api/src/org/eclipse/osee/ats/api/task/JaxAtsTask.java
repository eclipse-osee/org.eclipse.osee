/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.api.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.config.JaxAtsObject;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Donald G. Dunne
 */
public class JaxAtsTask extends JaxAtsObject {

   private List<String> assigneeUserIds = new LinkedList<>();
   private List<ArtifactId> assigneeAccountIds = new LinkedList<>();
   private Date createdDate;
   private String createdByUserId;
   private String relatedToState;
   private String taskWorkDef;
   List<JaxAttribute> attributes;
   List<JaxRelation> relations;
   private Double hoursSpent = 0.0;

   public JaxAtsTask() {
      attributes = new ArrayList<>();
   }

   public Date getCreatedDate() {
      return createdDate;
   }

   public void setCreatedDate(Date createdDate) {
      this.createdDate = createdDate;
   }

   public String getCreatedByUserId() {
      return createdByUserId;
   }

   public void setCreatedByUserId(String createdByUserId) {
      this.createdByUserId = createdByUserId;
   }

   public String getRelatedToState() {
      return relatedToState;
   }

   public void setRelatedToState(String relatedToState) {
      this.relatedToState = relatedToState;
   }

   @Override
   public String toString() {
      return "JaxAtsTask [title=" + getName() + ", createdDate=" + createdDate + ", createdById=" + createdByUserId + ", assigneeIds=" + assigneeUserIds + ", relatedToState=" + relatedToState + "]";
   }

   public List<String> getAssigneeUserIds() {
      return assigneeUserIds;
   }

   public void setAssigneeUserIds(List<String> assigneeUserIds) {
      Conditions.assertFalse(assigneeUserIds.contains(AtsCoreUsers.SYSTEM_USER.getUserId()),
         "Can't assign task to System User");
      this.assigneeUserIds = assigneeUserIds;
   }

   public void addAssigneeUserIds(String idString) {
      Conditions.assertFalse(idString.equals(AtsCoreUsers.SYSTEM_USER.getUserId()), "Can't assign task to System User");
      this.assigneeUserIds.add(idString);
   }

   public String getTaskWorkDef() {
      return taskWorkDef;
   }

   public void setTaskWorkDef(String taskWorkDef) {
      this.taskWorkDef = taskWorkDef;
   }

   public List<JaxAttribute> getAttributes() {
      return attributes;
   }

   public void setAttributes(List<JaxAttribute> attributes) {
      this.attributes = attributes;
   }

   public void addAttributes(AttributeTypeToken attrType, List<Object> values) {
      Conditions.assertNotNullOrEmpty(values, "Values can not be empty");
      JaxAttribute attr = new JaxAttribute();
      attr.setAttrType(attrType);
      attr.getValues().addAll(values);
      attributes.add(attr);
   }

   public JaxAttribute addAttribute(AttributeTypeToken attrType, Object value) {
      Conditions.assertNotNull(value, "Value can not be null");
      JaxAttribute attr = new JaxAttribute();
      attr.setAttrType(attrType);
      attr.getValues().add(value);
      attributes.add(attr);
      return attr;
   }

   public void addRelation(RelationTypeSide relationSide, long... relatedIds) {
      JaxRelation relation = new JaxRelation();
      relation.setRelationTypeName(relationSide.getName());
      relation.setSideA(relationSide.getSide().isSideA());
      for (long relatedId : relatedIds) {
         relation.getRelatedIds().add(relatedId);
      }
      getRelations().add(relation);
   }

   public List<JaxRelation> getRelations() {
      if (relations == null) {
         relations = new LinkedList<>();
      }
      return relations;
   }

   public void setRelations(List<JaxRelation> relations) {
      this.relations = relations;
   }

   public List<ArtifactId> getAssigneeAccountIds() {
      return assigneeAccountIds;
   }

   public void setAssigneeAccountIds(List<ArtifactId> assigneeAccountIds) {
      this.assigneeAccountIds = assigneeAccountIds;
   }

   public static JaxAtsTask createet(String title, AtsUser createdBy, Date createdDate) {
      JaxAtsTask task = new JaxAtsTask();
      task.setCreatedByUserId(createdBy.getUserId());
      task.setCreatedDate(createdDate);
      task.setName(title);
      return task;
   }

   public static JaxAtsTask create(NewTaskData newTaskData, String title, AtsUser createdBy, Date createdDate) {
      JaxAtsTask task = createet(title, createdBy, createdDate);
      newTaskData.getTasks().add(task);
      return task;
   }

   @JsonIgnore
   public ArtifactToken getToken() {
      return ArtifactToken.valueOf(getId(), atsApi.getAtsBranch());
   }

   public Double getHoursSpent() {
      return hoursSpent;
   }

   public void setHoursSpent(Double hoursSpent) {
      this.hoursSpent = hoursSpent;
   }
}