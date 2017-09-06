/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workflow;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.framework.core.data.AttributeTypeId;

/**
 * @author Donald G. Dunne
 */
public class NewActionData {

   String asUserId;
   String title;
   String description;
   ChangeType changeType;
   String priority;
   boolean validationRequired;
   Collection<String> aiIds;
   String createdDateLong;
   String createdByUserId;
   String transactionComment;
   String needByDateLong;
   Map<String, String> attrValues = new HashMap<String, String>();

   public NewActionData() {
      // jas-rs
   }

   public NewActionData(String asUserUserId, String title, String desc, ChangeType changeType, String priority, boolean validationRequired, Date needByDate, Collection<String> aiIds, Date createdDate, String createdByUserId) {
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

   public ChangeType getChangeType() {
      return changeType;
   }

   public void setChangeType(ChangeType changeType) {
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
}
