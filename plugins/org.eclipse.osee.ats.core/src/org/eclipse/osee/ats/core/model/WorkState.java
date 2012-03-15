/*
 * Created on Mar 6, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.model;

import java.util.List;

/**
 * @author Donald G. Dunne
 */
public interface WorkState {

   public abstract void setHoursSpent(double hoursSpent);

   public abstract void setPercentComplete(int percentComplete);

   public abstract String getName();

   public abstract List<IAtsUser> getAssignees();

   public abstract double getHoursSpent();

   public abstract int getPercentComplete();

   public abstract void addAssignee(IAtsUser steve);

   public abstract void setAssignees(List<? extends IAtsUser> users);

   public abstract void setName(String name);

   public abstract void removeAssignee(IAtsUser assignee);

}