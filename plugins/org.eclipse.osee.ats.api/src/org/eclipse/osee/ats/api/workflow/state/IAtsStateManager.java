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

package org.eclipse.osee.ats.api.workflow.state;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.WorkState;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogItem;

/**
 * @author Donald G. Dunne
 */
public interface IAtsStateManager {

   IStateToken getCurrentState();

   void addAssignees(String stateName, Collection<? extends AtsUser> assignees);

   void setAssignee(AtsUser assignee);

   void setAssignees(Collection<? extends AtsUser> assignees);

   /**
    * Sets the assignees as attributes and relations AND writes to store. Does not persist.
    */
   void setAssignees(String stateName, List<? extends AtsUser> assignees);

   void transitionHelper(List<? extends AtsUser> toAssignees, IStateToken fromStateName, IStateToken toStateName);

   long getTimeInState();

   long getTimeInState(IStateToken state);

   void addAssignee(String stateName, AtsUser assignee);

   boolean isDirty();

   List<AtsUser> getAssignees(String stateName);

   List<AtsUser> getAssigneesForState(String fromStateName);

   List<AtsUser> getAssignees();

   void setCurrentStateName(String currentStateName);

   void addAssignee(AtsUser assignee);

   void addState(String stateName, List<? extends AtsUser> assignees);

   void setAssignees(List<? extends AtsUser> assignees);

   WorkState createState(String stateName);

   List<String> getVisitedStateNames();

   void removeAssignee(String stateName, AtsUser assignee);

   void setAssignee(IStateToken state, AtsUser assignee);

   void createState(IStateToken state);

   boolean isUnAssignedSolely();

   String getAssigneesStr();

   void removeAssignee(AtsUser assignee);

   boolean isUnAssigned();

   void clearAssignees();

   Collection<AtsUser> getAssignees(IStateToken state);

   boolean isStateVisited(IStateToken state);

   String getAssigneesStr(int length);

   String getAssigneesStr(String stateName, int length);

   String getAssigneesStr(String stateName);

   void addAssignees(Collection<? extends AtsUser> assignees);

   void setAssignee(String stateName, AtsUser assignee);

   boolean isStateVisited(String stateName);

   void addState(WorkState workState);

   IAtsLogItem getStateStartedData(IStateToken state);

   IAtsLogItem getStateStartedData(String stateName);

   Collection<? extends AtsUser> getAssigneesAdded();

   WorkState getState(String string);

   boolean isInState(IStateToken state);

   void setAssignees(String stateName, StateType stateType, List<? extends AtsUser> assignees);

   void setCreatedBy(AtsUser user, boolean logChange, Date date, IAtsChangeSet changes);

   void internalSetCreatedBy(AtsUser user, IAtsChangeSet changes);

   String getCurrentStateNameFast();

   /**
    * Should not be called except by WorkItemService or in test
    */
   String getCurrentStateNameInternal();

}