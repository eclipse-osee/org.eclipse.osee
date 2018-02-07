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
package org.eclipse.osee.ats.api.workflow.state;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.WorkState;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogItem;

/**
 * @author Donald G. Dunne
 */
public interface IAtsStateManager extends WorkStateFactory {

   String getCurrentStateName();

   IStateToken getCurrentState();

   StateType getCurrentStateType();

   void updateMetrics(IStateToken state, double additionalHours, int percentComplete, boolean logMetrics, IAtsUser user);

   void setMetrics(double hours, int percentComplete, boolean logMetrics, IAtsUser user, Date date);

   /**
    * Set metrics and log if changed
    *
    * @param changes JavaTip
    */
   void setMetrics(IStateToken state, double hours, int percentComplete, boolean logMetrics, IAtsUser user, Date date);

   StateType getStateType();

   void addAssignees(String stateName, Collection<? extends IAtsUser> assignees);

   String getHoursSpentStr(String stateName);

   void setAssignee(IAtsUser assignee);

   void setAssignees(Collection<? extends IAtsUser> assignees);

   /**
    * Sets the assignees as attributes and relations AND writes to store. Does not persist.
    */
   void setAssignees(String stateName, List<? extends IAtsUser> assignees);

   void transitionHelper(List<? extends IAtsUser> toAssignees, IStateToken fromStateName, IStateToken toStateName, String cancelReason);

   long getTimeInState();

   long getTimeInState(IStateToken state);

   void addAssignee(String stateName, IAtsUser assignee);

   void addState(String stateName, List<? extends IAtsUser> assignees, double hoursSpent, int percentComplete);

   boolean isDirty();

   List<IAtsUser> getAssignees(String stateName);

   List<IAtsUser> getAssigneesForState(String fromStateName);

   List<IAtsUser> getAssignees();

   void setCurrentStateName(String currentStateName);

   void addAssignee(IAtsUser assignee);

   void addState(String stateName, List<? extends IAtsUser> assignees);

   void setAssignees(List<? extends IAtsUser> assignees);

   void createState(String stateName);

   void setPercentComplete(String stateName, int percentComplete);

   void setHoursSpent(String stateName, double hoursSpent);

   double getHoursSpent(String stateName);

   int getPercentComplete(String stateName);

   List<String> getVisitedStateNames();

   void removeAssignee(String stateName, IAtsUser assignee);

   void setAssignee(IStateToken state, IAtsUser assignee);

   void createState(IStateToken state);

   boolean isUnAssignedSolely();

   String getAssigneesStr();

   void removeAssignee(IAtsUser assignee);

   boolean isUnAssigned();

   void clearAssignees();

   Collection<IAtsUser> getAssignees(IStateToken state);

   boolean isStateVisited(IStateToken state);

   String getAssigneesStr(int length);

   String getAssigneesStr(String stateName, int length);

   String getAssigneesStr(String stateName);

   void addAssignees(Collection<? extends IAtsUser> assignees);

   void setAssignee(String stateName, IAtsUser assignee);

   boolean isStateVisited(String stateName);

   @Override
   WorkState createStateData(String name, List<? extends IAtsUser> assignees);

   @Override
   WorkState createStateData(String name);

   @Override
   WorkState createStateData(String name, List<? extends IAtsUser> assignees, double hoursSpent, int percentComplete);

   void addState(WorkState workState);

   void validateNoBootstrapUser();

   @Override
   String getId();

   IAtsLogItem getStateStartedData(IStateToken state);

   IAtsLogItem getStateStartedData(String stateName);

   Collection<? extends IAtsUser> getAssigneesAdded();

   Integer getPercentCompleteValue();

   void setPercentCompleteValue(Integer percentComplete);

   WorkState getState(String string);

   boolean isInState(IStateToken state);

   void setAssignees(String stateName, StateType stateType, List<? extends IAtsUser> assignees);

}