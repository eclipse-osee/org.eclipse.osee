/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.core.internal.state;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workflow.WorkState;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogItem;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.core.workflow.state.SimpleTeamState;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class StateManager implements IAtsStateManager {

   private final IAtsWorkItem workItem;
   private String currentStateName;
   private final List<WorkState> states = new CopyOnWriteArrayList<>();
   private final List<AtsUser> initialAssignees = new ArrayList<>();
   private boolean dirty = false;
   private final String instanceId;
   private final AtsApi atsApi;

   public StateManager(IAtsWorkItem workItem, AtsApi atsApi) {
      this.workItem = workItem;
      this.atsApi = atsApi;
      this.instanceId = Lib.generateArtifactIdAsInt().toString();
   }

   @Override
   public String getCurrentStateNameInternal() {
      return currentStateName;
   }

   @Override
   public IStateToken getCurrentState() {
      return new SimpleTeamState(getCurrentStateNameInternal(), workItem.getCurrentStateType());
   }

   @Override
   public void addAssignees(String stateName, Collection<? extends AtsUser> assignees) {
      if (assignees == null || assignees.isEmpty()) {
         return;
      }
      for (AtsUser assignee : assignees) {
         if (AtsCoreUsers.isSystemUser(assignee)) {
            throw new OseeArgumentException("Can not assign workflow to System User");
         }
      }
      WorkState state = getState(stateName);
      if (state != null) {
         List<AtsUser> currentAssignees = state.getAssignees();
         for (AtsUser assignee : assignees) {
            if (!currentAssignees.contains(assignee)) {
               state.addAssignee(assignee);
            }
         }
      }
      if (getAssignees().size() > 1 && getAssignees().contains(AtsCoreUsers.UNASSIGNED_USER)) {
         removeAssignee(getCurrentStateNameInternal(), AtsCoreUsers.UNASSIGNED_USER);
      }
      if (getAssignees().size() > 1 && getAssignees().contains(AtsCoreUsers.SYSTEM_USER)) {
         removeAssignee(getCurrentStateNameInternal(), AtsCoreUsers.SYSTEM_USER);
      }
      setDirty(true);
   }

   @Override
   public void setAssignee(AtsUser assignee) {
      if (assignee != null) {
         setAssignees(Arrays.asList(assignee));
      }
   }

   @Override
   public void setAssignees(Collection<? extends AtsUser> assignees) {
      setAssignees(getCurrentStateNameInternal(), new LinkedList<AtsUser>(assignees));
   }

   /**
    * Sets the assignees as attributes and relations AND writes to store. Does not persist.
    */

   @Override
   public void setAssignees(String stateName, List<? extends AtsUser> assignees) {
      if (assignees == null) {
         return;
      }
      StateDefinition stateDef = workItem.getWorkDefinition().getStateByName(stateName);
      StateType stateType = stateDef.getStateType();
      setAssignees(stateName, stateType, assignees);
   }

   @Override
   public void setAssignees(String stateName, StateType stateType, List<? extends AtsUser> assignees) {
      if (assignees == null) {
         return;
      }
      if (stateType.isCompletedOrCancelled()) {
         if (assignees.isEmpty()) {
            return;
         }
         throw new OseeStateException("Can't assign completed/cancelled states.");
      }

      for (AtsUser assignee : assignees) {
         if (AtsCoreUsers.isSystemUser(assignee)) {
            throw new OseeArgumentException("Can not assign workflow to System User");
         }
      }

      // Note: current and next state could be same
      WorkState currState = getState(getCurrentStateNameInternal());
      if (currState != null) {
         List<AtsUser> currAssignees = currState.getAssignees();
         WorkState nextState = getState(stateName);
         List<AtsUser> nextAssignees = new ArrayList<>(assignees);

         List<AtsUser> notifyNewAssignees = new ArrayList<>(nextAssignees);
         notifyNewAssignees.removeAll(currAssignees);

         //Update assignees for state
         if (nextState != null) {
            nextState.setAssignees(nextAssignees);
         }
      }

      // Remove UnAssigned if part of assignees
      if (getAssignees().size() > 1 && getAssignees().contains(AtsCoreUsers.UNASSIGNED_USER)) {
         removeAssignee(getCurrentStateNameInternal(), AtsCoreUsers.UNASSIGNED_USER);
      }
      if (getAssignees().size() > 1 && getAssignees().contains(AtsCoreUsers.SYSTEM_USER)) {
         removeAssignee(getCurrentStateNameInternal(), AtsCoreUsers.SYSTEM_USER);
      }

      setDirty(true);
   }

   @Override
   public void transitionHelper(List<? extends AtsUser> toAssignees, IStateToken fromState, IStateToken toState) {
      createState(toState);
      setAssignees(toState.getName(), toAssignees);
      setCurrentStateName(toState.getName());
      setDirty(true);
   }

   @Override
   public long getTimeInState() {
      return getTimeInState(getCurrentState());
   }

   @Override
   public long getTimeInState(IStateToken state) {
      if (state == null) {
         return 0;
      }
      IAtsLogItem logItem = getStateStartedData(state);
      if (logItem == null) {
         return 0;
      }
      return new Date().getTime() - logItem.getDate().getTime();
   }

   @Override
   public IAtsLogItem getStateStartedData(IStateToken state) {
      return getStateStartedData(state.getName());
   }

   @Override
   public IAtsLogItem getStateStartedData(String stateName) {
      return workItem.getLog().getStateEvent(LogType.StateEntered, stateName);
   }

   @Override
   public void addAssignee(String stateName, AtsUser assignee) {
      addAssignees(stateName, Arrays.asList(assignee));
   }

   protected void addState(String name, List<? extends AtsUser> assignees, boolean logError) {
      if (getVisitedStateNames().contains(name)) {
         String errorStr = String.format("Error: Duplicate state [%s] for [%s]", name, workItem.getAtsId());
         if (logError) {
            OseeLog.log(StateManager.class, Level.SEVERE, errorStr);
         }
         return;
      } else {
         addState(WorkState.create(name, assignees));
      }
   }

   @Override
   public boolean isDirty() {
      return dirty;
   }

   @Override
   public WorkState getState(String string) {
      for (WorkState state : states) {
         if (state.getName().equals(string)) {
            return state;
         }
      }
      return null;
   }

   @Override
   public List<AtsUser> getAssignees(String stateName) {
      return getAssigneesForState(stateName);
   }

   @Override
   public List<AtsUser> getAssigneesForState(String fromStateName) {
      WorkState state = getState(fromStateName);
      if (state != null) {
         return state.getAssignees();
      }
      return Collections.emptyList();
   }

   @Override
   public List<AtsUser> getAssignees() {
      List<AtsUser> assignees = new ArrayList<>();
      WorkState state = getState(getCurrentStateNameInternal());
      if (state != null) {
         assignees.addAll(state.getAssignees());
      } else {
         throw new OseeStateException("State not found for %s", workItem.toStringWithId());
      }
      return assignees;
   }

   @Override
   public void setCurrentStateName(String currentStateName) {
      Conditions.assertNotNull(currentStateName, "currentStateName");
      this.currentStateName = currentStateName;
      setDirty(true);
   }

   @Override
   public void addAssignee(AtsUser assignee) {
      addAssignee(getCurrentStateNameInternal(), assignee);
   }

   @Override
   public void addState(String stateName, List<? extends AtsUser> assignees) {
      WorkState state = createState(stateName);
      state.setAssignees(assignees);
   }

   @Override
   public void setAssignees(List<? extends AtsUser> assignees) {
      setAssignees(getCurrentStateNameInternal(), assignees);
   }

   @Override
   public WorkState createState(String stateName) {
      WorkState state = getState(stateName);
      if (state == null) {
         state = WorkState.create(stateName);
         addState(state);
      }
      return state;
   }

   @Override
   public List<String> getVisitedStateNames() {
      return Named.getNames(states);
   }

   @Override
   public void removeAssignee(String stateName, AtsUser assignee) {
      WorkState state = getState(stateName);
      if (state != null) {
         state.removeAssignee(assignee);
         setDirty(true);
      } else {
         throw new OseeStateException("State [%s] not found for %s", stateName, workItem.toStringWithId());
      }
   }

   @Override
   public void setAssignee(IStateToken state, AtsUser assignee) {
      setAssignee(state.getName(), assignee);
   }

   @Override
   public void createState(IStateToken state) {
      createState(state.getName());
   }

   @Override
   public boolean isUnAssignedSolely() {
      return getAssignees().size() == 1 && isUnAssigned();
   }

   @Override
   public String getAssigneesStr() {
      return getAssigneesStr(getCurrentStateNameInternal());
   }

   @Override
   public void removeAssignee(AtsUser assignee) {
      removeAssignee(getCurrentStateNameInternal(), assignee);
   }

   @Override
   public boolean isUnAssigned() {
      return getAssignees().contains(AtsCoreUsers.UNASSIGNED_USER);
   }

   @Override
   public void clearAssignees() {
      setAssignees(getCurrentStateNameInternal(), new LinkedList<AtsUser>());
   }

   @Override
   public Collection<AtsUser> getAssignees(IStateToken state) {
      return getAssignees(state.getName());
   }

   @Override
   public boolean isStateVisited(IStateToken state) {
      return isStateVisited(state.getName());
   }

   @Override
   public String getAssigneesStr(int length) {
      return getAssigneesStr(getCurrentStateNameInternal(), length);
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
   public String getAssigneesStr(String stateName) {
      return AtsObjects.toString("; ", getAssignees(stateName));
   }

   @Override
   public void addAssignees(Collection<? extends AtsUser> assignees) {
      addAssignees(getCurrentStateNameInternal(), assignees);
   }

   @Override
   public void setAssignee(String stateName, AtsUser assignee) {
      if (assignee != null) {
         setAssignees(stateName, Arrays.asList(assignee));
      }
   }

   @Override
   public boolean isStateVisited(String stateName) {
      return getVisitedStateNames().contains(stateName);
   }

   @Override
   public void addState(WorkState workState) {
      addState(workState, true);
   }

   protected void addState(WorkState state, boolean logError) {
      if (getVisitedStateNames().contains(state.getName())) {
         String errorStr = String.format("Error: Duplicate state [%s] for [%s]", state.getName(), workItem.getAtsId());
         if (logError) {
            OseeLog.log(StateManager.class, Level.SEVERE, errorStr);
         }
         return;
      } else {
         states.add(state);
         setDirty(true);
      }
   }

   public void setDirty(boolean dirty) {
      this.dirty = dirty;
   }

   public List<AtsUser> getInitialAssignees() {
      return initialAssignees;
   }

   @Override
   public List<AtsUser> getAssigneesAdded() {
      List<AtsUser> added = new ArrayList<>();
      List<AtsUser> current = getAssignees();
      for (AtsUser user : current) {
         if (!initialAssignees.contains(user)) {
            added.add(user);
         }
      }
      return added;
   }

   public void clear() {
      initialAssignees.clear();
      states.clear();
      currentStateName = null;
   }

   @Override
   public String toString() {
      return String.format("StateManager id[%s] for workitem [%s]", instanceId, workItem);
   }

   @Override
   public boolean isInState(IStateToken state) {
      return getCurrentStateNameInternal().equals(state.getName());
   }

   @Override
   public void setCreatedBy(AtsUser user, boolean logChange, Date date, IAtsChangeSet changes) {
      if (logChange) {
         logCreatedByChange(workItem, user);
      }
      if (changes == null) {
         if (atsApi.getStoreService().isAttributeTypeValid(workItem, AtsAttributeTypes.CreatedBy)) {
            atsApi.getAttributeResolver().setSoleAttributeValue(workItem, AtsAttributeTypes.CreatedBy,
               user.getUserId());
         }
         if (date != null && atsApi.getStoreService().isAttributeTypeValid(workItem, AtsAttributeTypes.CreatedDate)) {
            atsApi.getAttributeResolver().setSoleAttributeValue(workItem, AtsAttributeTypes.CreatedDate, date);
         }
      } else {
         if (atsApi.getStoreService().isAttributeTypeValid(workItem, AtsAttributeTypes.CreatedBy)) {
            atsApi.getAttributeResolver().setSoleAttributeValue(workItem, AtsAttributeTypes.CreatedBy,
               user.getUserId());
         }
         if (date != null && atsApi.getStoreService().isAttributeTypeValid(workItem, AtsAttributeTypes.CreatedDate)) {
            changes.setSoleAttributeValue(workItem, AtsAttributeTypes.CreatedDate, date);
         }
      }

   }

   @Override
   public void internalSetCreatedBy(AtsUser user, IAtsChangeSet changes) {
      if (changes.isAttributeTypeValid(workItem, AtsAttributeTypes.CreatedBy)) {
         changes.setSoleAttributeValue(workItem, AtsAttributeTypes.CreatedBy, user.getUserId());
      }
   }

   private void logCreatedByChange(IAtsWorkItem workItem, AtsUser user) {
      if (atsApi.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.CreatedBy, null) == null) {
         workItem.getLog().addLog(LogType.Originated, "", "", new Date(), user.getUserId());
      } else {
         workItem.getLog().addLog(LogType.Originated, "",
            "Changed by " + atsApi.getUserService().getCurrentUser().getName(), new Date(), user.getUserId());
      }
   }

   @Override
   public String getCurrentStateNameFast() {
      String currState =
         atsApi.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.CurrentState, "");
      currState = currState.replaceFirst(";.*$", "");
      return currState;
   }

}
