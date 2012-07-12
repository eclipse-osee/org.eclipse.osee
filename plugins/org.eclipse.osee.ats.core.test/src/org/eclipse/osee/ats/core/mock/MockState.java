/*
 * Created on Mar 1, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.mock;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.core.model.IAtsUser;

/**
 * @author Donald G. Dunne
 */
public class MockState {

   private final String name;
   private final List<IAtsUser> assignees = new LinkedList<IAtsUser>();
   private double hoursSpent = 0;
   private int percentComplete = 0;

   public MockState(String name, List<? extends IAtsUser> assignees) {
      this(name, assignees, 0, 0);
   }

   public MockState(String name, List<? extends IAtsUser> assignees, double hoursSpent, int percentComplete) {
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

   public MockState(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public List<IAtsUser> getAssignees() {
      return assignees;
   }

   public double getHoursSpent() {
      return hoursSpent;
   }

   public int getPercentComplete() {
      return percentComplete;
   }

   public void addAssignee(IAtsUser steve) {
      if (!assignees.contains(steve)) {
         assignees.add(steve);
      }
   }

   public void setAssignees(List<? extends IAtsUser> users) {
      assignees.clear();
      assignees.addAll(users);
   }

}
