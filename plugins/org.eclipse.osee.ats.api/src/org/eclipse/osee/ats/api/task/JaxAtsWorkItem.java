/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Donald G. Dunne
 */
public class JaxAtsWorkItem extends JaxAtsObject {

   protected List<String> assigneeUserIds = new LinkedList<>();
   protected List<ArtifactId> assigneeAccountIds = new LinkedList<>();
   protected Date createdDate;
   protected String createdByUserId;
   protected String workDef;
   protected String title;
   protected String atsId;
   protected String currentState;
   protected StateType stateType;
   List<JaxAttribute> attributes;
   List<JaxRelation> relations;

   public JaxAtsWorkItem() {
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

   @Override
   public String toString() {
      return "JaxAtsTask [title=" + getName() + ", createdDate=" + createdDate + ", createdById=" + createdByUserId + ", assigneeIds=" + assigneeUserIds + "]";
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

   @JsonIgnore
   public ArtifactToken getToken() {
      return ArtifactToken.valueOf(ArtifactId.valueOf(getId()), CoreBranches.COMMON);
   }

   public String getWorkDef() {
      return workDef;
   }

   public void setWorkDef(String workDef) {
      this.workDef = workDef;
   }

   public String getAtsId() {
      return atsId;
   }

   public void setAtsId(String atsId) {
      this.atsId = atsId;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getCurrentState() {
      return currentState;
   }

   public void setCurrentState(String currentState) {
      this.currentState = currentState;
   }

   public StateType getStateType() {
      return stateType;
   }

   public void setStateType(StateType stateType) {
      this.stateType = stateType;
   }

}
