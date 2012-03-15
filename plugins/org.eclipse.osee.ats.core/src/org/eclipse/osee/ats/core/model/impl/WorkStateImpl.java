/*
 * Created on Mar 1, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.model.impl;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.core.model.IAtsUser;
import org.eclipse.osee.ats.core.model.WorkState;

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
   public void addAssignee(IAtsUser steve) {
      if (!assignees.contains(steve)) {
         assignees.add(steve);
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
