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
import org.eclipse.osee.ats.api.workflow.WorkStateProvider;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsStateManager extends WorkStateProvider, WorkStateFactory {

   public abstract WorkStateProvider getStateProvider() throws OseeCoreException;

   public abstract void reload() throws OseeCoreException;

   public abstract void load() throws OseeCoreException;

   @Override
   public abstract String getCurrentStateName();

   public abstract IStateToken getCurrentState();

   public abstract StateType getCurrentStateType();

   public abstract void updateMetrics(IStateToken state, double additionalHours, int percentComplete, boolean logMetrics) throws OseeCoreException;

   /**
    * Set metrics and log if changed
    */
   public abstract void setMetrics(IStateToken state, double hours, int percentComplete, boolean logMetrics, IAtsUser user, Date date) throws OseeCoreException;

   public abstract StateType getStateType() throws OseeCoreException;

   @Override
   public abstract void addAssignees(String stateName, Collection<? extends IAtsUser> assignees) throws OseeCoreException;

   public abstract String getHoursSpentStr(String stateName) throws OseeCoreException;

   @Override
   public abstract void setAssignee(IAtsUser assignee) throws OseeCoreException;

   public abstract void setAssignees(Collection<? extends IAtsUser> assignees) throws OseeCoreException;

   /**
    * Sets the assignees as attributes and relations AND writes to store. Does not persist.
    */
   @Override
   public abstract void setAssignees(String stateName, List<? extends IAtsUser> assignees) throws OseeCoreException;

   public abstract void transitionHelper(List<? extends IAtsUser> toAssignees, IStateToken fromStateName, IStateToken toStateName, String cancelReason) throws OseeCoreException;

   public abstract void transitionHelper(List<? extends IAtsUser> toAssignees, String fromStateName, String toStateName, String cancelReason) throws OseeCoreException;

   /**
    * Initializes state machine and sets the current state to stateName
    */
   public abstract void initializeStateMachine(IStateToken workPage, List<? extends IAtsUser> assignees, IAtsUser currentUser) throws OseeCoreException;

   public abstract long getTimeInState() throws OseeCoreException;

   public abstract long getTimeInState(IStateToken state) throws OseeCoreException;

   @Override
   public abstract void addAssignee(String stateName, IAtsUser assignee) throws OseeCoreException;

   @Override
   public abstract void addState(String stateName, List<? extends IAtsUser> assignees, double hoursSpent, int percentComplete) throws OseeCoreException;

   public abstract boolean isDirty() throws OseeCoreException;

   public abstract Result isDirtyResult() throws OseeCoreException;

   public abstract void writeToStore() throws OseeCoreException;

   public abstract void notifyAssigned(List<IAtsUser> notifyAssignees) throws OseeCoreException;

   @Override
   public abstract List<IAtsUser> getAssignees(String stateName) throws OseeCoreException;

   @Override
   public abstract List<IAtsUser> getAssigneesForState(String fromStateName) throws OseeCoreException;

   @Override
   public abstract List<IAtsUser> getAssignees() throws OseeCoreException;

   @Override
   public abstract void setCurrentStateName(String currentStateName) throws OseeCoreException;

   @Override
   public abstract void addAssignee(IAtsUser assignee) throws OseeCoreException;

   @Override
   public abstract void addState(String stateName, List<? extends IAtsUser> assignees) throws OseeCoreException;

   @Override
   public abstract void setAssignees(List<? extends IAtsUser> assignees) throws OseeCoreException;

   @Override
   public abstract void createState(String stateName) throws OseeCoreException;

   @Override
   public abstract void setPercentComplete(String stateName, int percentComplete) throws OseeCoreException;

   @Override
   public abstract void setHoursSpent(String stateName, double hoursSpent) throws OseeCoreException;

   @Override
   public abstract double getHoursSpent(String stateName) throws OseeCoreException;

   @Override
   public abstract int getPercentComplete(String stateName) throws OseeCoreException;

   @Override
   public abstract List<String> getVisitedStateNames() throws OseeCoreException;

   @Override
   public abstract void removeAssignee(String stateName, IAtsUser assignee) throws OseeCoreException;

   public abstract void setAssignee(IStateToken state, IAtsUser assignee) throws OseeCoreException;

   public abstract void createState(IStateToken state) throws OseeCoreException;

   @Override
   public abstract boolean isUnAssignedSolely() throws OseeCoreException;

   @Override
   public abstract String getAssigneesStr() throws OseeCoreException;

   @Override
   public abstract void removeAssignee(IAtsUser assignee) throws OseeCoreException;

   @Override
   public abstract boolean isUnAssigned() throws OseeCoreException;

   @Override
   public abstract void clearAssignees() throws OseeCoreException;

   public abstract Collection<? extends IAtsUser> getAssignees(IStateToken state) throws OseeCoreException;

   public abstract boolean isStateVisited(IStateToken state) throws OseeCoreException;

   public abstract String getAssigneesStr(int length) throws OseeCoreException;

   @Override
   public abstract String getAssigneesStr(String stateName, int length) throws OseeCoreException;

   @Override
   public abstract String getAssigneesStr(String stateName) throws OseeCoreException;

   @Override
   public abstract void addAssignees(Collection<? extends IAtsUser> assignees) throws OseeCoreException;

   @Override
   public abstract void setAssignee(String stateName, IAtsUser assignee) throws OseeCoreException;

   @Override
   public abstract boolean isStateVisited(String stateName) throws OseeCoreException;

   @Override
   public abstract WorkState createStateData(String name, List<? extends IAtsUser> assignees);

   @Override
   public abstract WorkState createStateData(String name);

   @Override
   public abstract WorkState createStateData(String name, List<? extends IAtsUser> assignees, double hoursSpent, int percentComplete);

   @Override
   public abstract void addState(WorkState workState) throws OseeCoreException;

   public abstract void validateNoBootstrapUser() throws OseeCoreException;

   @Override
   public abstract boolean isSame(WorkState workState) throws OseeCoreException;

   @Override
   public abstract String getId();

}