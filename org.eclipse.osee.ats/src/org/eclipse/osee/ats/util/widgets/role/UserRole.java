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
package org.eclipse.osee.ats.util.widgets.role;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Donald G. Dunne
 */
public class UserRole {

   private Role role = Role.Reviewer;
   private User user;
   private Double hoursSpent = null;
   private String guid = GUID.generateGuidStr();
   private boolean completed = false;

   public static enum Role {
      Moderator, Reviewer, Author;
      public static Collection<String> strValues() {
         Set<String> values = new HashSet<String>();
         for (Enum<Role> e : values()) {
            values.add(e.name());
         }
         return values;
      }
   };

   public UserRole() {
      this(Role.Reviewer, SkynetAuthentication.getInstance().getAuthenticatedUser(), null);
   }

   public UserRole(Role role, User user) {
      this(role, user, 0.0);
   }

   public UserRole(Role role, User user, Double hoursSpent) {
      this.role = role;
      this.user = user;
      this.hoursSpent = hoursSpent;
   }

   public UserRole(String xml) {
      try {
         fromXml(xml);
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
      }
   }

   public void update(UserRole dItem) throws SQLException {
      fromXml(dItem.toXml());
   }

   public String toXml() {
      StringBuffer sb = new StringBuffer();
      sb.append(AXml.addTagData("role", role.name()));
      sb.append(AXml.addTagData("userId", user.getUserId()));
      sb.append(AXml.addTagData("hoursSpent", hoursSpent == null ? "" : String.valueOf(hoursSpent)));
      sb.append(AXml.addTagData("completed", String.valueOf(completed)));
      sb.append(AXml.addTagData("guid", guid));
      return sb.toString();
   }

   public void fromXml(String xml) throws SQLException {
      this.role = Role.valueOf(AXml.getTagData(xml, "role"));
      this.user = SkynetAuthentication.getInstance().getUserByIdWithError(AXml.getTagData(xml, "userId"));
      this.hoursSpent =
            AXml.getTagData(xml, "hoursSpent").equals("") ? null : Double.valueOf(AXml.getTagData(xml, "hoursSpent")).doubleValue();
      String completedStr = AXml.getTagData(xml, "completed");
      if (completedStr != null)
         this.completed = completedStr.equals("true");
      else
         this.completed = false;
      this.guid = AXml.getTagData(xml, "guid");
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof UserRole) {
         UserRole di = (UserRole) obj;
         return di.getGuid().equals(getGuid());
      }
      return false;
   }

   @Override
   public int hashCode() {
      return toString().hashCode();
   }

   public String toString() {
      return role + " - " + user + " - " + hoursSpent + " - " + (completed ? "Completed" : "InWork");
   }

   /**
    * @return the role
    */
   public Role getRole() {
      return role;
   }

   /**
    * @param role the role to set
    */
   public void setRole(Role role) {
      this.role = role;
   }

   /**
    * @return the user
    */
   public User getUser() {
      return user;
   }

   /**
    * @param user the user to set
    */
   public void setUser(User user) {
      this.user = user;
   }

   /**
    * @return the hoursSpent
    */
   public Double getHoursSpent() {
      return hoursSpent;
   }

   public String getHoursSpentStr() {
      return hoursSpent == null ? "" : AtsLib.doubleToStrString(hoursSpent, true);
   }

   /**
    * @param hoursSpent the hoursSpent to set
    */
   public void setHoursSpent(Double hoursSpent) {
      this.hoursSpent = hoursSpent;
   }

   /**
    * @return the guid
    */
   public String getGuid() {
      return guid;
   }

   /**
    * @param guid the guid to set
    */
   public void setGuid(String guid) {
      this.guid = guid;
   }

   /**
    * @return the completed
    */
   public boolean isCompleted() {
      return completed;
   }

   /**
    * @param completed the completed to set
    */
   public void setCompleted(boolean completed) {
      this.completed = completed;
   }

}
