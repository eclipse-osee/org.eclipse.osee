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

package org.eclipse.osee.ats.api.workflow;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.framework.jdk.core.type.NamedBase;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Donald G. Dunne
 */
public class WorkState extends NamedBase {

   private String name;
   private final List<AtsUser> assignees = new LinkedList<>();
   private double hoursSpent = 0;
   private int percentComplete = 0;

   private WorkState(String name, List<? extends AtsUser> assignees, double hoursSpent, int percentComplete) {
      this.name = name;
      this.assignees.addAll(assignees);
      this.hoursSpent = hoursSpent;
      this.percentComplete = percentComplete;
   }

   public void setHoursSpent(double hoursSpent) {
      this.hoursSpent = hoursSpent;
   }

   public void setPercentComplete(int percentComplete) {
      this.percentComplete = percentComplete;
   }

   @Override
   public String getName() {
      return name;
   }

   public List<AtsUser> getAssignees() {
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
      if (!assignees.contains(user)) {
         assignees.add(user);
      }
   }

   public void setAssignees(List<? extends AtsUser> users) {
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

   public static WorkState create(String name, List<? extends AtsUser> assignees, double hoursSpent, int percentComplete) {
      return new WorkState(name, assignees, hoursSpent, percentComplete);
   }

   public static WorkState create(String name) {
      return new WorkState(name, Collections.emptyList(), 0, 0);
   }

   public static WorkState create(String name, List<? extends AtsUser> assignees) {
      return new WorkState(name, assignees, 0, 0);
   }

}
