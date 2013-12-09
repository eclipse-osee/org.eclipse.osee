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
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsStateManager extends WorkStateFactory {

   String getCurrentStateName();

   IStateToken getCurrentState();

   StateType getCurrentStateType();

   void updateMetrics(IStateToken state, double additionalHours, int percentComplete, boolean logMetrics, IAtsUser user) throws OseeCoreException;

   void setMetrics(double hours, int percentComplete, boolean logMetrics, IAtsUser user, Date date) throws OseeCoreException;

   /**
    * Set metrics and log if changed
    * 
    * @param changes JavaTip
    */
   void setMetrics(IStateToken state, double hours, int percentComplete, boolean logMetrics, IAtsUser user, Date date) throws OseeCoreException;

   StateType getStateType() throws OseeCoreException;

   void addAssignees(String stateName, Collection<? extends IAtsUser> assignees) throws OseeCoreException;

   String getHoursSpentStr(String stateName) throws OseeCoreException;

   void setAssignee(IAtsUser assignee) throws OseeCoreException;

   void setAssignees(Collection<? extends IAtsUser> assignees) throws OseeCoreException;

   /**
    * Sets the assignees as attributes and relations AND writes to store. Does not persist.
    */
   void setAssignees(String stateName, List<? extends IAtsUser> assignees) throws OseeCoreException;

   void transitionHelper(List<? extends IAtsUser> toAssignees, IStateToken fromStateName, IStateToken toStateName, String cancelReason) throws OseeCoreException;

   long getTimeInState() throws OseeCoreException;

   long getTimeInState(IStateToken state) throws OseeCoreException;

   void addAssignee(String stateName, IAtsUser assignee) throws OseeCoreException;

   void addState(String stateName, List<? extends IAtsUser> assignees, double hoursSpent, int percentComplete) throws OseeCoreException;

   boolean isDirty() throws OseeCoreException;

   List<IAtsUser> getAssignees(String stateName) throws OseeCoreException;

   List<IAtsUser> getAssigneesForState(String fromStateName) throws OseeCoreException;

   List<IAtsUser> getAssignees() throws OseeCoreException;

   void setCurrentStateName(String currentStateName) throws OseeCoreException;

   void addAssignee(IAtsUser assignee) throws OseeCoreException;

   void addState(String stateName, List<? extends IAtsUser> assignees) throws OseeCoreException;

   void setAssignees(List<? extends IAtsUser> assignees) throws OseeCoreException;

   void createState(String stateName) throws OseeCoreException;

   void setPercentComplete(String stateName, int percentComplete) throws OseeCoreException;

   void setHoursSpent(String stateName, double hoursSpent) throws OseeCoreException;

   double getHoursSpent(String stateName) throws OseeCoreException;

   int getPercentComplete(String stateName) throws OseeCoreException;

   List<String> getVisitedStateNames() throws OseeCoreException;

   void removeAssignee(String stateName, IAtsUser assignee) throws OseeCoreException;

   void setAssignee(IStateToken state, IAtsUser assignee) throws OseeCoreException;

   void createState(IStateToken state) throws OseeCoreException;

   boolean isUnAssignedSolely() throws OseeCoreException;

   String getAssigneesStr() throws OseeCoreException;

   void removeAssignee(IAtsUser assignee) throws OseeCoreException;

   boolean isUnAssigned() throws OseeCoreException;

   void clearAssignees() throws OseeCoreException;

   Collection<? extends IAtsUser> getAssignees(IStateToken state) throws OseeCoreException;

   boolean isStateVisited(IStateToken state) throws OseeCoreException;

   String getAssigneesStr(int length) throws OseeCoreException;

   String getAssigneesStr(String stateName, int length) throws OseeCoreException;

   String getAssigneesStr(String stateName) throws OseeCoreException;

   void addAssignees(Collection<? extends IAtsUser> assignees) throws OseeCoreException;

   void setAssignee(String stateName, IAtsUser assignee) throws OseeCoreException;

   boolean isStateVisited(String stateName) throws OseeCoreException;

   @Override
   WorkState createStateData(String name, List<? extends IAtsUser> assignees);

   @Override
   WorkState createStateData(String name);

   @Override
   WorkState createStateData(String name, List<? extends IAtsUser> assignees, double hoursSpent, int percentComplete);

   void addState(WorkState workState) throws OseeCoreException;

   void validateNoBootstrapUser() throws OseeCoreException;

   @Override
   String getId();

   IAtsLogItem getStateStartedData(IStateToken state) throws OseeCoreException;

   IAtsLogItem getStateStartedData(String stateName) throws OseeCoreException;

   Collection<? extends IAtsUser> getAssigneesAdded() throws OseeCoreException;

   Integer getPercentCompleteValue();

   void setPercentCompleteValue(Integer percentComplete);

   WorkState getState(String string);

}