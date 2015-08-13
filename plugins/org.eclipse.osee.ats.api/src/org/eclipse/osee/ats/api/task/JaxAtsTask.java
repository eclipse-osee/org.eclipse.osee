/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.task;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.ats.api.config.JaxAtsObject;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class JaxAtsTask extends JaxAtsObject {

   private List<String> assigneeUserIds = new LinkedList<>();
   private Date createdDate;
   private String createdByUserId;
   private String relatedToState;
   Map<String, Object> attrTypeToObject;

   public JaxAtsTask() {
      attrTypeToObject = new HashMap<>();
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
      return "JaxAtsTask [title=" + getName() + ", createdDate=" + createdDate + ", createdByUuid=" + createdByUserId + ", assigneeUuids=" + assigneeUserIds + ", relatedToState=" + relatedToState + "]";
   }

   public List<String> getAssigneeUserIds() {
      return assigneeUserIds;
   }

   public void setAssigneeUserIds(List<String> assigneeUserIds) {
      this.assigneeUserIds = assigneeUserIds;
   }

   public Map<String, Object> getAttrTypeToObject() {
      return attrTypeToObject;
   }

   public void setAttrTypeToObject(Map<String, Object> attrTypeToObject) {
      this.attrTypeToObject = attrTypeToObject;
   }

}
