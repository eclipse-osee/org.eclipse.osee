/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.api.review;

import java.text.NumberFormat;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class UserRole {
   private ReviewRole role;
   private String userId;
   private Double hoursSpent = null;
   private String guid = GUID.create();
   private Boolean completed = false;
   private Boolean duplicateUser = false;

   public UserRole(ReviewRole role, UserToken user) {
      this(role, user.getUserId());
   }

   public UserRole(ReviewRole role, String userId) {
      this(role, userId, 0.0, false);
   }

   public UserRole(ReviewRole role, UserToken user, Double hoursSpent, Boolean completed) {
      this(role, user.getUserId(), hoursSpent, completed);
   }

   public UserRole(ReviewRole role, String userId, Double hoursSpent, Boolean completed) {
      this.role = role;
      this.userId = userId;
      this.hoursSpent = hoursSpent;
      this.completed = completed;
   }

   public UserRole(String xml, WorkDefinition workDefinition) {
      fromXml(xml, workDefinition);
   }

   public void update(UserRole dItem, WorkDefinition workDefinition) {
      fromXml(dItem.toXml(), workDefinition);
   }

   public String toXml() {
      StringBuffer sb = new StringBuffer();
      sb.append(AXml.addTagData("role", role.getName()));
      sb.append(AXml.addTagData("userId", userId));
      sb.append(AXml.addTagData("hoursSpent", hoursSpent == null ? "" : String.valueOf(hoursSpent)));
      sb.append(AXml.addTagData("completed", String.valueOf(completed)));
      sb.append(AXml.addTagData("guid", guid));
      return sb.toString();
   }

   private void fromXml(String xml, WorkDefinition workDefinition) {
      try {
         this.role = workDefinition.fromName(AXml.getTagData(xml, "role"));
         this.userId = AXml.getTagData(xml, "userId");
         String hoursSpent = AXml.getTagData(xml, "hoursSpent");
         if (Strings.isValid(hoursSpent)) {
            this.hoursSpent = NumberFormat.getInstance().parse(hoursSpent).doubleValue();
         } else {
            this.hoursSpent = null;
         }
         String completedStr = AXml.getTagData(xml, "completed");
         if (Strings.isValid(completedStr)) {
            this.completed = completedStr.equals("true");
         } else {
            this.completed = false;
         }
         this.guid = AXml.getTagData(xml, "guid");
      } catch (Exception ex) {
         throw new OseeCoreException("Can't parse User Role", ex);
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof UserRole) {
         UserRole userRole = (UserRole) obj;
         return userRole.getGuid().equals(getGuid());
      }
      return false;
   }

   @Override
   public int hashCode() {
      return getGuid().hashCode();
   }

   @Override
   public String toString() {
      return role + " - " + userId + " - " + hoursSpent + " - " + (completed ? "Completed" : "InWork");
   }

   public ReviewRole getRole() {
      return role;
   }

   public void setRole(ReviewRole role) {
      this.role = role;
   }

   public Double getHoursSpent() {
      return hoursSpent;
   }

   public void setHoursSpent(Double hoursSpent) {
      this.hoursSpent = hoursSpent;
   }

   public String getGuid() {
      return guid;
   }

   public void setGuid(String guid) {
      this.guid = guid;
   }

   public boolean isCompleted() {
      return completed;
   }

   public void setCompleted(boolean completed) {
      this.completed = completed;
   }

   public String getUserId() {
      return userId;
   }

   public void setUserId(String userId) {
      this.userId = userId;
   }

   public Boolean isDuplicateUser() {
      return duplicateUser;
   }

   public void setDuplicateUser(Boolean duplicateUser) {
      this.duplicateUser = duplicateUser;
   }

   public void addHoursSpent(double additionalHoursSpent) {
      setHoursSpent(this.hoursSpent + additionalHoursSpent);
   }
}
