/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.core.internal.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.jdk.core.type.NamedBase;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
class WorkState extends NamedBase {

   private final Pattern storagePattern = Pattern.compile("^(.*?);(.*?);(.*?);(.*?)$");
   private final Pattern userPattern = Pattern.compile("<(.*?)>");
   private final IAtsWorkItem workItem;
   private String name;
   private final Set<AtsUser> assignees = new HashSet<>();
   private double hoursSpent = 0;
   private int percentComplete = 0;
   private IAttribute<Object> attr;
   private final boolean loaded = false;
   private boolean currentState = false;

   private WorkState(IAtsWorkItem workItem, String name, Collection<AtsUser> assignees, double hoursSpent, int percentComplete, boolean currentState) {
      this.workItem = workItem;
      this.name = name;
      this.assignees.addAll(assignees);
      this.hoursSpent = hoursSpent;
      this.percentComplete = percentComplete;
      this.currentState = currentState;
   }

   public WorkState(IAtsWorkItem workItem, IAttribute<Object> attr, boolean currentState) {
      this.workItem = workItem;
      this.attr = attr;
      this.currentState = currentState;
   }

   public void setHoursSpent(double hoursSpent) {
      ensureLoaded();
      this.hoursSpent = hoursSpent;
   }

   public void setPercentComplete(int percentComplete) {
      ensureLoaded();
      this.percentComplete = percentComplete;
   }

   @Override
   public String getName() {
      ensureLoaded();
      return name;
   }

   public Collection<AtsUser> getAssignees() {
      ensureLoaded();
      return assignees;
   }

   public double getHoursSpent() {
      return hoursSpent;
   }

   public int getPercentComplete() {
      return percentComplete;
   }

   public void addAssignee(AtsUser user) {
      Conditions.checkNotNull(user, "user");
      assignees.add(user);
   }

   public void setAssignees(Collection<AtsUser> users) {
      assignees.clear();
      for (AtsUser user : users) {
         addAssignee(user);
      }
   }

   @Override
   public void setName(String name) {
      this.name = name;
   }

   public void removeAssignee(AtsUser assignee) {
      assignees.remove(assignee);
   }

   public void ensureLoaded() {
      if (!loaded) {
         fromStoreStr();
      }
   }

   public String getStoreStr() {
      if (!loaded && attr != null) {
         return (String) attr.getValue();
      }
      return String.format("%s;%s;;", name, AtsApiService.get().getUserService().getUserStorageString(assignees));
   }

   private void fromStoreStr() {
      if (attr != null) {
         String storeStr = (String) attr.getValue();
         if (Strings.isValid(storeStr)) {
            Matcher m = storagePattern.matcher(storeStr);
            if (m.find()) {
               setName(m.group(1));
               if (!m.group(3).equals("")) {
                  setHoursSpent(Float.valueOf(m.group(3)).doubleValue());
               }
               if (!m.group(4).equals("")) {
                  setPercentComplete(Integer.valueOf(m.group(4)).intValue());
               }
               String userStr = m.group(2);
               List<AtsUser> users = getUsers(userStr);
               setAssignees(users);
            } else {
               throw new OseeArgumentException("Can't unpack state data [%s]", storeStr);
            }
         }
      }
   }

   private List<AtsUser> getUsers(String storageString) {
      List<AtsUser> users = new ArrayList<>();
      Matcher m = userPattern.matcher(storageString);
      while (m.find()) {
         String userId = m.group(1);
         if (!Strings.isValid(userId)) {
            throw new IllegalArgumentException("Blank userId specified.");
         }
         try {
            String uId = m.group(1);
            AtsUser u = workItem.getAtsApi().getUserService().getUserByUserId(uId);
            Conditions.checkNotNull(u, "userById " + uId);
            users.add(u);
         } catch (Exception ex) {
            OseeLog.log(WorkState.class, Level.SEVERE, ex);
         }
      }
      return users;
   }

   public boolean isDirty() {
      if (attr == null) {
         return true;
      }
      if (!loaded) {
         return false;
      }
      if (attr != null) {
         String stored = (String) attr.getValue();
         String loaded = getStoreStr();
         if (!stored.equals(loaded)) {
            return true;
         }
      }
      return false;
   }

   @Override
   public String toString() {
      return name;
   }

   public static WorkState create(IAtsWorkItem workItem, String name, boolean currentState) {
      return new WorkState(workItem, name, Collections.emptyList(), 0, 0, currentState);
   }

   public static WorkState create(IAtsWorkItem workItem, String name, Collection<AtsUser> assignees,
      boolean currentState) {
      Conditions.checkNotNullOrContainNull(assignees, "assignees");
      return new WorkState(workItem, name, assignees, 0, 0, currentState);
   }

   public static WorkState create(IAtsWorkItem workItem, IAttribute<Object> attr, boolean currentState) {
      return new WorkState(workItem, attr, currentState);
   }

   public boolean isCurrentState() {
      return currentState;
   }

   public void setCurrentState(boolean currentState) {
      this.currentState = currentState;
   }

   public IAttribute<Object> getAttr() {
      return attr;
   }

}
