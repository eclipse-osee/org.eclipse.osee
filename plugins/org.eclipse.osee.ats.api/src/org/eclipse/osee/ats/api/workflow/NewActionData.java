/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.api.workflow;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;

/**
 * @author Donald G. Dunne
 */
public class NewActionData {

   String asUserId;
   String title;
   String description;
   ChangeTypes changeType;
   String priority;
   boolean validationRequired;
   Collection<String> aiIds;
   String createdDateLong;
   String createdByUserId;
   String transactionComment;
   String needByDateLong;
   String needByDate;
   Map<String, String> attrValues = new HashMap<>();
   String points;
   boolean unplanned;
   String sprint;
   String agileTeam;
   String featureGroup;
   String workPackage;
   String originatorStr;
   String assigneeStr;
   ArtifactId versionId;

   public NewActionData() {
      // jax-rs
   }

   public NewActionData(String asUserUserId, String title, String desc, ChangeTypes changeType, String priority, boolean validationRequired, Date needByDate, Collection<String> aiIds, Date createdDate, String createdByUserId) {
      this.title = title;
      this.description = desc;
      this.changeType = changeType;
      this.priority = priority;
      this.validationRequired = validationRequired;
      this.needByDateLong = String.valueOf(needByDate.getTime());
      this.asUserId = asUserUserId;
      this.createdDateLong = String.valueOf(createdDate.getTime());
      this.createdByUserId = createdByUserId;
      this.aiIds = aiIds;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public ChangeTypes getChangeType() {
      return changeType;
   }

   public void setChangeType(ChangeTypes changeType) {
      this.changeType = changeType;
   }

   public String getPriority() {
      return priority;
   }

   public void setPriority(String priority) {
      this.priority = priority;
   }

   public boolean isValidationRequired() {
      return validationRequired;
   }

   public void setValidationRequired(boolean validationRequired) {
      this.validationRequired = validationRequired;
   }

   public String getTransactionComment() {
      return transactionComment;
   }

   public void setTransactionComment(String transactionComment) {
      this.transactionComment = transactionComment;
   }

   public String getNeedByDateLong() {
      return needByDateLong;
   }

   public void setNeedByDateLong(String needByDateLong) {
      this.needByDateLong = needByDateLong;
   }

   public String getAsUserId() {
      return asUserId;
   }

   public void setAsUserId(String asUserId) {
      this.asUserId = asUserId;
   }

   public String getCreatedByUserId() {
      return createdByUserId;
   }

   public void setCreatedByUserId(String createdByUserId) {
      this.createdByUserId = createdByUserId;
   }

   public Collection<String> getAiIds() {
      return aiIds;
   }

   public void setAiIds(Collection<String> aiIds) {
      this.aiIds = aiIds;
   }

   public String getCreatedDateLong() {
      return createdDateLong;
   }

   public void setCreatedDateLong(String createdDateLong) {
      this.createdDateLong = createdDateLong;
   }

   public Map<String, String> getAttrValues() {
      return attrValues;
   }

   public void setAttrValues(Map<String, String> attrValues) {
      this.attrValues = attrValues;
   }

   public void addAttrValue(AttributeTypeId type, String value) {
      attrValues.put(type.getIdString(), value);
   }

   public String getNeedByDate() {
      return needByDate;
   }

   public void setNeedByDate(String needByDate) {
      this.needByDate = needByDate;
   }

   public String getPoints() {
      return points;
   }

   public void setPoints(String points) {
      this.points = points;
   }

   public boolean isUnplanned() {
      return unplanned;
   }

   public void setUnplanned(boolean unplanned) {
      this.unplanned = unplanned;
   }

   public String getSprint() {
      return sprint;
   }

   public void setSprint(String sprint) {
      this.sprint = sprint;
   }

   public String getFeatureGroup() {
      return featureGroup;
   }

   public void setFeatureGroup(String featureGroup) {
      this.featureGroup = featureGroup;
   }

   public String getWorkPackage() {
      return workPackage;
   }

   public void setWorkPackage(String workPackage) {
      this.workPackage = workPackage;
   }

   public String getAgileTeam() {
      return agileTeam;
   }

   public void setAgileTeam(String agileTeam) {
      this.agileTeam = agileTeam;
   }

   public String getOriginatorStr() {
      return originatorStr;
   }

   /**
    * @param originatorStr - originator id (not userId)
    */
   public void setOriginatorStr(String originatorStr) {
      this.originatorStr = originatorStr;
   }

   public String getAssigneeStr() {
      return assigneeStr;
   }

   /**
    * @param assigneeStr - comma delimited assignee ids (not userId)
    */
   public void setAssigneeStr(String assigneeStr) {
      this.assigneeStr = assigneeStr;
   }

   public ArtifactId getVersionId() {
      return versionId;
   }

   public void setVersionId(ArtifactId versionId) {
      this.versionId = versionId;
   }
}
