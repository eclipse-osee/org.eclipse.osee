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
package org.eclipse.osee.ats.core.client.workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.core.client.util.AtsUsers;
import org.eclipse.osee.ats.core.client.util.AtsUtilCore;
import org.eclipse.osee.ats.core.client.util.UsersByIds;
import org.eclipse.osee.ats.core.model.IAtsUser;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.ats.core.workflow.IWorkPage;
import org.eclipse.osee.ats.core.workflow.WorkPageType;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;

public class SMAState {
   private String name;
   private List<IAtsUser> assignees = new ArrayList<IAtsUser>();
   private int percentComplete = 0;
   private double hoursSpent = 0;
   private WorkPageType workPageType;

   @Override
   public int hashCode() {
      int result = 17;
      result = result * 31 + name.hashCode();
      result = result * 31 + assignees.hashCode();

      return result;
   }

   public SMAState(IWorkPage stateDef, Collection<? extends IAtsUser> assignees) throws OseeCoreException {
      this(stateDef);
      if (assignees != null) {
         setAssignees(assignees);
      }
   }

   public SMAState(IWorkPage stateDef) {
      setName(stateDef.getPageName());
      this.workPageType = stateDef.getWorkPageType();
   }

   public SMAState(IWorkPage stateDef, String xml) throws OseeCoreException {
      this(stateDef);
      setFromXml(xml);
   }

   public SMAState(AbstractWorkflowArtifact awa, String xml) throws OseeCoreException {
      setFromXml(xml);
      String pageName = getName();
      StateDefinition stateDef = awa.getStateDefinitionByName(pageName);
      if (stateDef != null) {
         setWorkPageType(stateDef.getWorkPageType());
      } else {
         setWorkPageType(WorkPageType.Working);
      }
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
         if (!Collections.isEqual(state.assignees, this.assignees)) {
            return false;
         }
         return true;
      }
      return super.equals(obj);
   }

   public Collection<? extends IAtsUser> getAssignees() {
      return assignees;
   }

   /**
    * Sets the assignees but DOES NOT write to SMA. This method should NOT be called outside the StateMachineArtifact.
    */
   public void setAssignees(Collection<? extends IAtsUser> assignees) throws OseeCoreException {
      if (assignees != null) {
         if (assignees.contains(AtsUsers.getOseeSystemUser()) || assignees.contains(AtsUsers.getGuestUser())) {
            throw new OseeArgumentException("Can not assign workflow to OseeSystem or Guest");
         }
         if (assignees.size() > 1 && assignees.contains(AtsUsers.getUnAssigned())) {
            throw new OseeArgumentException("Can not assign to user and UnAssigned");
         }
         if (assignees.size() > 0 && workPageType.isCompletedOrCancelledPage()) {
            throw new OseeStateException("Can't assign completed/cancelled states.");
         }
      } else {
         assignees = new HashSet<IAtsUser>();
      }
      this.assignees.clear();
      for (IAtsUser assignee : assignees) {
         addAssignee(assignee);
      }

   }

   public void clearAssignees() {
      this.assignees.clear();
   }

   /**
    * Sets the assignees but DOES NOT write to SMA. This method should NOT be called outside the StateMachineArtifact.
    */
   public void setAssignee(IAtsUser assignee) throws OseeCoreException {
      if (assignee != null && workPageType.isCompletedOrCancelledPage()) {
         throw new OseeStateException("Can't assign completed/cancelled states.");
      }
      if (AtsUsers.isOseeSystemUser(assignee) || AtsUsers.isGuestUser(assignee)) {
         throw new OseeArgumentException("Can not assign workflow to OseeSystem or Guest");
      }
      this.assignees.clear();
      addAssignee(assignee);
   }

   public void addAssignee(IAtsUser assignee) throws OseeCoreException {
      if (assignee != null && workPageType.isCompletedOrCancelledPage()) {
         throw new OseeStateException("Can't assign completed/cancelled states.");
      }
      if (AtsUsers.isOseeSystemUser(assignee) || AtsUsers.isGuestUser(assignee)) {
         throw new OseeArgumentException("Can not assign workflow to OseeSystem or Guest");
      }
      if (assignee != null && !this.assignees.contains(assignee)) {
         this.assignees.add(assignee);
      }
   }

   public void removeAssignee(IAtsUser assignee) {
      if (assignee != null) {
         this.assignees.remove(assignee);
      }
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = Strings.intern(name);
   }

   public String toXml() throws OseeCoreException {
      StringBuffer sb = new StringBuffer(name);
      sb.append(";");
      sb.append(org.eclipse.osee.ats.core.client.util.UsersByIds.getStorageString(assignees));
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
      if (!Strings.isValid(xml)) {
         setName("Unknown");
         return;
      }
      Matcher m = storagePattern.matcher(xml);
      if (m.find()) {
         setName(m.group(1));
         if (!m.group(3).equals("")) {
            hoursSpent = new Float(m.group(3)).doubleValue();
         }
         if (!m.group(4).equals("")) {
            percentComplete = Integer.valueOf(m.group(4)).intValue();
         }
         assignees = UsersByIds.getUsers(m.group(2));
      } else {
         throw new OseeArgumentException("Can't unpack state data [%s]", xml);
      }
   }

   public double getHoursSpent() {
      return hoursSpent;
   }

   public String getHoursSpentStr() {
      return AtsUtilCore.doubleToI18nString(hoursSpent, true);
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

   public WorkPageType getWorkPageType() {
      return workPageType;
   }

   public void setWorkPageType(WorkPageType workPageType) {
      this.workPageType = workPageType;
   }

}
