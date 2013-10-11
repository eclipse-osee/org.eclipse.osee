/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.core.model.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workflow.WorkState;
import org.eclipse.osee.ats.api.workflow.WorkStateProvider;
import org.eclipse.osee.ats.api.workflow.state.WorkStateFactory;
import org.eclipse.osee.ats.core.internal.Activator;
import org.eclipse.osee.ats.core.notify.IAtsNotificationListener;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class WorkStateProviderImpl implements WorkStateProvider {

   private final WorkStateFactory factory;
   private final List<WorkState> states = new LinkedList<WorkState>();

   private IAtsNotificationListener listener;
   private String currentStateName;

   /**
    * Creates a new StateData initialized with currentStateName as current state and sets assignees
    */
   public WorkStateProviderImpl(WorkStateFactory factory) {
      this(factory, null);
   }

   public WorkStateProviderImpl(WorkStateFactory factory, WorkState workState) {
      this.factory = factory;
      if (workState != null) {
         currentStateName = workState.getName();
         states.add(workState);
      }
   }

   public void setNotificationListener(IAtsNotificationListener listener) {
      this.listener = listener;
   }

   /**
    * @return true if UnAssigned user is currently an assignee
    */
   @Override
   public boolean isUnAssigned() throws OseeCoreException {
      return getAssignees().contains(AtsCoreUsers.UNASSIGNED_USER);
   }

   @Override
   public boolean isUnAssignedSolely() throws OseeCoreException {
      return getAssignees().size() == 1 && isUnAssigned();
   }

   @Override
   public String getAssigneesStr(String stateName) {
      return AtsObjects.toString("; ", getAssignees(stateName));
   }

   @Override
   public String getAssigneesStr(String stateName, int length) {
      String str = getAssigneesStr(stateName);
      if (str.length() > length) {
         return str.substring(0, length - 1) + "...";
      }
      return str;
   }

   @Override
   public String getAssigneesStr() {
      return getAssigneesStr(getCurrentStateName());
   }

   @Override
   public List<IAtsUser> getAssignees(String stateName) {
      return getAssigneesForState(stateName);
   }

   @Override
   public void addAssignees(Collection<? extends IAtsUser> assignees) throws OseeCoreException {
      addAssignees(getCurrentStateName(), assignees);
   }

   @Override
   public void addAssignees(String stateName, Collection<? extends IAtsUser> assignees) throws OseeCoreException {
      if (assignees == null || assignees.isEmpty()) {
         return;
      }
      for (IAtsUser assignee : assignees) {
         if (AtsCoreUsers.isGuestUser(assignee)) {
            throw new OseeArgumentException("Can not assign workflow to Guest");
         }
      }
      List<IAtsUser> notifyAssignees = new ArrayList<IAtsUser>();
      WorkState state = getState(stateName);

      List<IAtsUser> currentAssignees = state.getAssignees();
      for (IAtsUser assignee : assignees) {
         if (!currentAssignees.contains(assignee)) {
            notifyAssignees.add(assignee);
            state.addAssignee(assignee);
         }
      }
      if (listener != null) {
         listener.notifyAssigned(notifyAssignees);
      }
      if (getAssignees().size() > 1 && getAssignees().contains(AtsCoreUsers.UNASSIGNED_USER)) {
         removeAssignee(getCurrentStateName(), AtsCoreUsers.UNASSIGNED_USER);
      }
      if (getAssignees().size() > 1 && getAssignees().contains(AtsCoreUsers.SYSTEM_USER)) {
         removeAssignee(getCurrentStateName(), AtsCoreUsers.SYSTEM_USER);
      }
   }

   @Override
   public void setAssignee(IAtsUser assignee) throws OseeCoreException {
      if (assignee != null) {
         setAssignees(Arrays.asList(assignee));
      }
   }

   @Override
   public void setAssignee(String stateName, IAtsUser assignee) throws OseeCoreException {
      if (assignee != null) {
         setAssignees(stateName, Arrays.asList(assignee));
      }
   }

   @Override
   public void setAssignees(List<? extends IAtsUser> assignees) throws OseeCoreException {
      setAssignees(getCurrentStateName(), assignees);
   }

   /**
    * Set assignees for stateName and notify any newly added assignees
    */
   @Override
   public void setAssignees(String stateName, List<? extends IAtsUser> assignees) throws OseeCoreException {
      if (assignees == null) {
         return;
      }
      for (IAtsUser assignee : assignees) {
         if (AtsCoreUsers.isGuestUser(assignee)) {
            throw new OseeArgumentException("Can not assign workflow to Guest");
         }
      }

      // Note: current and next state could be same
      WorkState currState = getState(getCurrentStateName());
      List<IAtsUser> currAssignees = currState.getAssignees();

      WorkState nextState = getState(stateName);
      List<IAtsUser> nextAssignees = new ArrayList<IAtsUser>(assignees);

      List<IAtsUser> notifyNewAssignees = new ArrayList<IAtsUser>(nextAssignees);
      notifyNewAssignees.removeAll(currAssignees);

      //Update assignees for state
      nextState.setAssignees(nextAssignees);

      //Notify users who are being assigned to the state
      if (listener != null) {
         listener.notifyAssigned(notifyNewAssignees);
      }

      // Remove UnAssigned if part of assignees
      if (getAssignees().size() > 1 && getAssignees().contains(AtsCoreUsers.UNASSIGNED_USER)) {
         removeAssignee(getCurrentStateName(), AtsCoreUsers.UNASSIGNED_USER);
      }

   }

   @Override
   public void removeAssignee(IAtsUser assignee) throws OseeCoreException {
      removeAssignee(getCurrentStateName(), assignee);
   }

   @Override
   public void clearAssignees() throws OseeCoreException {
      setAssignees(getCurrentStateName(), new LinkedList<IAtsUser>());
   }

   @Override
   public boolean isStateVisited(String stateName) {
      return getVisitedStateNames().contains(stateName);
   }

   @Override
   public List<String> getVisitedStateNames() {
      List<String> stateNames = new LinkedList<String>();
      for (WorkState state : states) {
         stateNames.add(state.getName());
      }
      return stateNames;
   }

   @Override
   public void createState(String stateName) {
      WorkState state = getState(stateName);
      if (state == null) {
         state = factory.createStateData(stateName);
         addState(state);
      }
   }

   @Override
   public void addState(String name, List<? extends IAtsUser> assignees, double hoursSpent, int percentComplete) {
      addState(name, assignees, hoursSpent, percentComplete, true);
   }

   protected void addState(String name, List<? extends IAtsUser> assignees, double hoursSpent, int percentComplete, boolean logError) {
      if (getVisitedStateNames().contains(name)) {
         String errorStr = String.format("Error: Duplicate state [%s] for [%s]", name, factory.getId());
         if (logError) {
            OseeLog.log(Activator.class, Level.SEVERE, errorStr);
         }
         return;
      } else {
         addState(factory.createStateData(name, assignees, hoursSpent, percentComplete));
      }
   }

   @Override
   public void setCurrentStateName(String currentStateName) {
      this.currentStateName = currentStateName;
   }

   @Override
   public List<IAtsUser> getAssigneesForState(String stateName) {
      WorkState state = getState(stateName);
      if (state != null) {
         return state.getAssignees();
      }
      return Collections.emptyList();
   }

   @Override
   public List<IAtsUser> getAssignees() throws OseeStateException {
      WorkState state = getState(getCurrentStateName());
      if (state != null) {
         return state.getAssignees();
      } else {
         throw new OseeStateException("State [%s] not found", getCurrentStateName());
      }
   }

   @Override
   public void addAssignee(IAtsUser assignee) throws OseeCoreException {
      addAssignee(getCurrentStateName(), assignee);
   }

   @Override
   public void addAssignee(String stateName, IAtsUser assignee) throws OseeCoreException {
      addAssignees(stateName, Arrays.asList(assignee));
   }

   private WorkState getState(String string) {
      for (WorkState state : states) {
         if (state.getName().equals(string)) {
            return state;
         }
      }
      return null;
   }

   @Override
   public void addState(WorkState state) {
      addState(state, true);
   }

   protected void addState(WorkState state, boolean logError) {
      if (getVisitedStateNames().contains(state.getName())) {
         String errorStr = String.format("Error: Duplicate state [%s] for [%s]", state.getName(), factory.getId());
         if (logError) {
            OseeLog.log(Activator.class, Level.SEVERE, errorStr);
         }
         return;
      } else {
         states.add(state);
      }
   }

   @Override
   public void addState(String name, List<? extends IAtsUser> assignees) {
      addState(name, assignees, 0, 0);
   }

   @Override
   public String getCurrentStateName() {
      return currentStateName;
   }

   @Override
   public int getPercentComplete(String stateName) {
      WorkState state = getState(stateName);
      if (state != null) {
         return state.getPercentComplete();
      }
      return 0;
   }

   @Override
   public double getHoursSpent(String stateName) {
      WorkState state = getState(stateName);
      if (state != null) {
         return state.getHoursSpent();
      }
      return 0.0;
   }

   @Override
   public void setHoursSpent(String stateName, double hoursSpent) throws OseeStateException {
      WorkState state = getState(stateName);
      if (state != null) {
         state.setHoursSpent(hoursSpent);
      } else {
         throw new OseeStateException("State [%s] not found", stateName);
      }
   }

   @Override
   public void setPercentComplete(String stateName, int percentComplete) throws OseeStateException {
      WorkState state = getState(stateName);
      if (state != null) {
         state.setPercentComplete(percentComplete);
      } else {
         throw new OseeStateException("State [%s] not found", stateName);
      }
   }

   @Override
   public void removeAssignee(String stateName, IAtsUser assignee) throws OseeStateException {
      WorkState state = getState(stateName);
      if (state != null) {
         state.removeAssignee(assignee);
      } else {
         throw new OseeStateException("State [%s] not found", stateName);
      }
   }

   @Override
   public boolean isSame(WorkState workState) {
      WorkState thisState = getState(workState.getName());
      if (thisState == null) {
         return false;
      } else if (thisState.getHoursSpent() != workState.getHoursSpent()) {
         return false;
      } else if (thisState.getPercentComplete() != workState.getPercentComplete()) {
         return false;
      }
      return org.eclipse.osee.framework.jdk.core.util.Collections.isEqual(thisState.getAssignees(),
         workState.getAssignees());
   }
}
