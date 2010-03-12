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
package org.eclipse.osee.ats.util.widgets;

import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.utility.UsersByIds;

public class SMAState {
   private String name;
   private Collection<User> assignees = new HashSet<User>();
   private int percentComplete = 0;
   private double hoursSpent = 0;

   @Override
   public int hashCode() {
      int result = 17;
      result = result * 31 + name.hashCode();
      result = result * 31 + assignees.hashCode();

      return result;
   }

   public SMAState(String name, Collection<User> assignees) {
      this.name = Strings.intern(name);
      if (assignees != null) {
         this.assignees = assignees;
      }
   }

   public SMAState(String name, User assignee) {
      this.name = Strings.intern(name);
      if (assignee != null) {
         this.assignees.add(assignee);
      }
   }

   public SMAState(String name) {
      this(name, (User) null);
   }

   public SMAState() {
      this("", (User) null);
   }

   @Override
   public String toString() {
      return name;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof SMAState) {
         SMAState state = (SMAState) obj;
         if (!state.name.equals(name)) {
            return false;
         }
         if (!state.assignees.equals(this.assignees)) {
            return false;
         }
         return true;
      }
      return super.equals(obj);
   }

   public Collection<User> getAssignees() {
      return assignees;
   }

   /**
    * Sets the assignees but DOES NOT write to SMA. This method should NOT be called outside the StateMachineArtifact.
    * 
    * @param assignees
    * @throws OseeCoreException
    */
   public void setAssignees(Collection<User> assignees) throws OseeCoreException {
      if (assignees != null) {
         if (assignees.contains(UserManager.getUser(SystemUser.OseeSystem)) || assignees.contains(UserManager.getUser(SystemUser.Guest))) {
            throw new OseeArgumentException("Can not assign workflow to OseeSystem or Guest");
         }
         if (assignees.size() > 1 && assignees.contains(UserManager.getUser(SystemUser.UnAssigned))) {
            throw new OseeArgumentException("Can not assign to user and UnAssigned");
         }
         if (assignees.size() > 0 && (name.equals(DefaultTeamState.Completed.name()) || name.equals(DefaultTeamState.Cancelled.name()))) {
            throw new OseeStateException("Can't assign completed/cancelled states.");
         }
      } else {
         assignees = new HashSet<User>();
      }
      this.assignees.clear();
      this.assignees.addAll(assignees);

   }

   public void clearAssignees() {
      this.assignees.clear();
   }

   /**
    * Sets the assignees but DOES NOT write to SMA. This method should NOT be called outside the StateMachineArtifact.
    * 
    * @param assignee
    * @throws OseeCoreException
    * @throws IllegalOseeArgumentException
    */
   public void setAssignee(User assignee) throws OseeCoreException {
      if (assignee != null && (name.equals(DefaultTeamState.Completed.name()) || name.equals(DefaultTeamState.Cancelled.name()))) {
         throw new OseeStateException("Can't assign completed/cancelled states.");
      }
      if (assignee == UserManager.getUser(SystemUser.OseeSystem) || assignee == UserManager.getUser(SystemUser.Guest)) {
         throw new OseeArgumentException("Can not assign workflow to OseeSystem or Guest");
      }
      this.assignees.clear();
      if (assignee != null) {
         this.assignees.add(assignee);
      }
   }

   /**
    * @param assignee
    */
   public void addAssignee(User assignee) throws OseeCoreException {
      if (assignee == UserManager.getUser(SystemUser.OseeSystem) || assignee == UserManager.getUser(SystemUser.Guest)) {
         throw new OseeArgumentException("Can not assign workflow to OseeSystem or Guest");
      }
      if (assignee != null) {
         this.assignees.add(assignee);
      }
   }

   public void removeAssignee(User assignee) {
      if (assignee != null) {
         this.assignees.remove(assignee);
      }
   }

   /**
    * @return Returns the name.
    */
   public String getName() {
      return name;
   }

   /**
    * @param name The name to set.
    */
   public void setName(String name) {
      this.name = name;
   }

   public String toXml() throws OseeCoreException {
      StringBuffer sb = new StringBuffer(name);
      sb.append(";");
      sb.append(UsersByIds.getStorageString(assignees));
      sb.append(";");
      if (hoursSpent > 0) {
         sb.append(getHoursSpentStr());
      }
      sb.append(";");
      if (percentComplete > 0) {
         sb.append(percentComplete);
      }
      return sb.toString();
   }

   public static Pattern storagePattern = Pattern.compile("^(.*?);(.*?);(.*?);(.*?)$");

   public void setFromXml(String xml) throws OseeCoreException {
      if (xml == null || xml.equals("")) {
         name = "Unknown";
         return;
      }
      Matcher m = storagePattern.matcher(xml);
      if (m.find()) {
         name = m.group(1);
         if (!m.group(3).equals("")) {
            hoursSpent = new Float(m.group(3)).doubleValue();
         }
         if (!m.group(4).equals("")) {
            percentComplete = new Integer(m.group(4)).intValue();
         }
         assignees = UsersByIds.getUsers(m.group(2));
      } else {
         throw new OseeArgumentException("Can't unpack state data => " + xml);
      }
   }

   public double getHoursSpent() {
      return hoursSpent;
   }

   public String getHoursSpentStr() {
      return String.valueOf(hoursSpent);
   }

   public void setHoursSpent(double hoursSpent) {
      this.hoursSpent = hoursSpent;
   }

   public int getPercentComplete() {
      return percentComplete;
   }

   public void setPercentComplete(int percentComplete) {
      this.percentComplete = percentComplete;
   }
}
