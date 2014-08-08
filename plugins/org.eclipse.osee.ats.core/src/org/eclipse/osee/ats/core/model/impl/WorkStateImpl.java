/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.model.impl;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workflow.WorkState;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Donald G. Dunne
 */
public class WorkStateImpl implements WorkState {

   private String name;
   private final List<IAtsUser> assignees = new LinkedList<IAtsUser>();
   private double hoursSpent = 0;
   private int percentComplete = 0;

   public WorkStateImpl(String name, List<? extends IAtsUser> assignees) {
      this(name, assignees, 0, 0);
   }

   public WorkStateImpl(String name, List<? extends IAtsUser> assignees, double hoursSpent, int percentComplete) {
      this.name = name;
      this.assignees.addAll(assignees);
      this.hoursSpent = hoursSpent;
      this.percentComplete = percentComplete;
   }

   @Override
   public void setHoursSpent(double hoursSpent) {
      this.hoursSpent = hoursSpent;
   }

   @Override
   public void setPercentComplete(int percentComplete) {
      this.percentComplete = percentComplete;
   }

   public WorkStateImpl(String name) {
      this.name = name;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public List<IAtsUser> getAssignees() {
      return assignees;
   }

   @Override
   public double getHoursSpent() {
      return hoursSpent;
   }

   @Override
   public int getPercentComplete() {
      return percentComplete;
   }

   @Override
   public void addAssignee(IAtsUser user) {
      Conditions.checkNotNull(user, "user");
      if (!assignees.contains(user)) {
         assignees.add(user);
      }
   }

   @Override
   public void setAssignees(List<? extends IAtsUser> users) {
      assignees.clear();
      for (IAtsUser user : users) {
         addAssignee(user);
      }
   }

   @Override
   public void setName(String name) {
      this.name = name;
   }

   @Override
   public void removeAssignee(IAtsUser assignee) {
      assignees.remove(assignee);
   }

}
