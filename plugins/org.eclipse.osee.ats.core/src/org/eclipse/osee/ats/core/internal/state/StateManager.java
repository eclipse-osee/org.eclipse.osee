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
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.WorkState;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogFactory;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogItem;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.api.workflow.state.WorkStateFactory;
import org.eclipse.osee.ats.core.model.impl.WorkStateImpl;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.util.HoursSpentUtil;
import org.eclipse.osee.ats.core.util.PercentCompleteTotalUtil;
import org.eclipse.osee.ats.core.workflow.state.SimpleTeamState;
import org.eclipse.osee.framework.core.enums.SystemUser;
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
   private final WorkStateFactory factory;
   private Integer percentCompleteValue = 0;
   private final List<IAtsUser> initialAssignees = new ArrayList<>();
   private boolean dirty = false;
   private final String instanceId;
   private final IAtsLogFactory logFactory;
   private final AtsApi atsApi;
   private StateType stateType;

   public StateManager(IAtsWorkItem workItem, IAtsLogFactory logFactory, AtsApi atsApi) {
      this.workItem = workItem;
      this.logFactory = logFactory;
      this.atsApi = atsApi;
      this.factory = this;
      this.instanceId = Lib.generateArtifactIdAsInt().toString();
   }

   public void setStateType(StateType stateType) {
      this.stateType = stateType;
   }

   @Override
   public String getCurrentStateName() {
      return currentStateName;
   }

   @Override
   public IStateToken getCurrentState() {
      return new SimpleTeamState(getCurrentStateName(), getCurrentStateType());
   }

   @Override
   public StateType getCurrentStateType() {
      return stateType;
   }

   @Override
   public void updateMetrics(IStateToken state, double additionalHours, int percentComplete, boolean logMetrics, IAtsUser user) {

      // get hours in current state, if additional hours + current hours < 0, walk other states subtracting difference
      double hoursInCurrentState = getHoursSpent(state.getName());
      double remaining = hoursInCurrentState + additionalHours;
      if (remaining < 0.0) {
         setHoursSpent(state.getName(), 0.0);
         for (String stateName : getVisitedStateNames()) {
            hoursInCurrentState = getHoursSpent(stateName);
            remaining += hoursInCurrentState;
            if (remaining < 0.0) {
               setHoursSpent(stateName, 0.0);
            } else {
               setHoursSpent(stateName, remaining);
               break;
            }
         }
      } else {
         setHoursSpent(state.getName(), remaining);
      }

      if (atsApi.getWorkDefinitionService().isStateWeightingEnabled(workItem.getWorkDefinition())) {
         setPercentComplete(state.getName(), percentComplete);
      } else {
         this.percentCompleteValue = percentComplete;
      }
      if (logMetrics) {
         logMetrics(workItem.getStateMgr().getCurrentState(), user, new Date());
      }
      setDirty(true);
   }

   protected void logMetrics(IStateToken state, IAtsUser user, Date date) {
      String hoursSpent = AtsUtilCore.doubleToI18nString(HoursSpentUtil.getHoursSpentTotal(workItem, atsApi));
      logMetrics(atsApi, logFactory, workItem, PercentCompleteTotalUtil.getPercentCompleteTotal(workItem, atsApi) + "",
         hoursSpent, state, user, date);
   }

   public static void logMetrics(AtsApi atsApi, IAtsLogFactory logFactory, IAtsWorkItem workItem, String percent, String hours, IStateToken state, IAtsUser user, Date date) {
      IAtsLogItem logItem =
         logFactory.newLogItem(LogType.Metrics, date, user, state.getName(), String.format("Percent %s Hours %s",
            PercentCompleteTotalUtil.getPercentCompleteTotal(workItem, atsApi), Double.parseDouble(hours)));
      workItem.getLog().addLogItem(logItem);
   }

   @Override
   public void setMetrics(double hours, int percentComplete, boolean logMetrics, IAtsUser user, Date date) {
      setMetrics(getCurrentState(), hours, percentComplete, logMetrics, user, date);
   }

   /**
    * @return true if hours difference is > than .01
    */
   protected boolean isHoursEqual(double hours1, double hours2) {
      return Math.abs(hours2 - hours1) < 0.01;
   }

   /**
    * Set metrics and log if changed
    */

   @Override
   public void setMetrics(IStateToken state, double hours, int percentComplete, boolean logMetrics, IAtsUser user, Date date) {
      boolean changed = setMetricsIfChanged(state, hours, percentComplete);
      if (changed) {
         if (logMetrics) {
            logMetrics(workItem.getStateMgr().getCurrentState(), user, new Date());
         }
         setDirty(true);
      }
   }

   protected boolean setMetricsIfChanged(IStateToken state, double hours, int percentComplete) {
      boolean changed = false;
      if (!isHoursEqual(getHoursSpent(state.getName()), hours)) {
         setHoursSpent(state.getName(), hours);
         changed = true;
      }
      if (percentComplete != getPercentComplete(state.getName())) {
         setPercentComplete(state.getName(), percentComplete);
         changed = true;
      }
      return changed;
   }

   @Override
   public StateType getStateType() {
      return stateType;
   }

   @Override
   public void addAssignees(String stateName, Collection<? extends IAtsUser> assignees) {
      if (assignees == null || assignees.isEmpty()) {
         return;
      }
      for (IAtsUser assignee : assignees) {
         if (AtsCoreUsers.isBootstrapUser(assignee)) {
            throw new OseeArgumentException("Can not assign workflow to Bootstrap");
         }
         if (AtsCoreUsers.isGuestUser(assignee)) {
            throw new OseeArgumentException("Can not assign workflow to Guest");
         }
         if (AtsCoreUsers.isSystemUser(assignee)) {
            throw new OseeArgumentException("Can not assign workflow to System User");
         }
      }
      WorkState state = getState(stateName);

      List<IAtsUser> currentAssignees = state.getAssignees();
      for (IAtsUser assignee : assignees) {
         if (!currentAssignees.contains(assignee)) {
            state.addAssignee(assignee);
         }
      }
      if (getAssignees().size() > 1 && getAssignees().contains(AtsCoreUsers.UNASSIGNED_USER)) {
         removeAssignee(getCurrentStateName(), AtsCoreUsers.UNASSIGNED_USER);
      }
      if (getAssignees().size() > 1 && getAssignees().contains(AtsCoreUsers.SYSTEM_USER)) {
         removeAssignee(getCurrentStateName(), AtsCoreUsers.SYSTEM_USER);
      }
      setDirty(true);
   }

   @Override
   public String getHoursSpentStr(String stateName) {
      return AtsUtilCore.doubleToI18nString(getHoursSpent(stateName), true);
   }

   @Override
   public void setAssignee(IAtsUser assignee) {
      if (assignee != null) {
         setAssignees(Arrays.asList(assignee));
      }
   }

   @Override
   public void setAssignees(Collection<? extends IAtsUser> assignees) {
      setAssignees(getCurrentStateName(), new LinkedList<IAtsUser>(assignees));
   }

   /**
    * Sets the assignees as attributes and relations AND writes to store. Does not persist.
    */

   @Override
   public void setAssignees(String stateName, List<? extends IAtsUser> assignees) {
      if (assignees == null) {
         return;
      }
      IAtsStateDefinition stateDef = workItem.getWorkDefinition().getStateByName(stateName);
      StateType stateType = stateDef.getStateType();
      setAssignees(stateName, stateType, assignees);
   }

   @Override
   public void setAssignees(String stateName, StateType stateType, List<? extends IAtsUser> assignees) {
      if (assignees == null) {
         return;
      }
      if (stateType.isCompletedOrCancelledState()) {
         if (assignees.isEmpty()) {
            return;
         }
         throw new OseeStateException("Can't assign completed/cancelled states.");
      }

      for (IAtsUser assignee : assignees) {
         if (AtsCoreUsers.isBootstrapUser(assignee)) {
            throw new OseeArgumentException("Can not assign workflow to Bootstrap");
         }
         if (AtsCoreUsers.isGuestUser(assignee)) {
            throw new OseeArgumentException("Can not assign workflow to Guest");
         }
         if (AtsCoreUsers.isSystemUser(assignee)) {
            throw new OseeArgumentException("Can not assign workflow to System User");
         }
      }

      // Note: current and next state could be same
      WorkState currState = getState(getCurrentStateName());
      List<IAtsUser> currAssignees = currState.getAssignees();

      WorkState nextState = getState(stateName);
      List<IAtsUser> nextAssignees = new ArrayList<>(assignees);

      List<IAtsUser> notifyNewAssignees = new ArrayList<>(nextAssignees);
      notifyNewAssignees.removeAll(currAssignees);

      //Update assignees for state
      nextState.setAssignees(nextAssignees);

      // Remove UnAssigned if part of assignees
      if (getAssignees().size() > 1 && getAssignees().contains(AtsCoreUsers.UNASSIGNED_USER)) {
         removeAssignee(getCurrentStateName(), AtsCoreUsers.UNASSIGNED_USER);
      }
      if (getAssignees().size() > 1 && getAssignees().contains(AtsCoreUsers.SYSTEM_USER)) {
         removeAssignee(getCurrentStateName(), AtsCoreUsers.SYSTEM_USER);
      }

      setDirty(true);
   }

   @Override
   public void transitionHelper(List<? extends IAtsUser> toAssignees, IStateToken fromState, IStateToken toState, String cancelReason) {
      createState(toState);
      setAssignees(toState.getName(), toAssignees);
      setCurrentStateName(toState.getName());
      stateType = toState.getStateType();
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
   public void addAssignee(String stateName, IAtsUser assignee) {
      addAssignees(stateName, Arrays.asList(assignee));
   }

   @Override
   public void addState(String stateName, List<? extends IAtsUser> assignees, double hoursSpent, int percentComplete) {
      addState(stateName, assignees, hoursSpent, percentComplete, true);
   }

   protected void addState(String name, List<? extends IAtsUser> assignees, double hoursSpent, int percentComplete, boolean logError) {
      if (getVisitedStateNames().contains(name)) {
         String errorStr = String.format("Error: Duplicate state [%s] for [%s]", name, factory.getId());
         if (logError) {
            OseeLog.log(StateManager.class, Level.SEVERE, errorStr);
         }
         return;
      } else {
         addState(factory.createStateData(name, assignees, hoursSpent, percentComplete));
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
   public List<IAtsUser> getAssignees(String stateName) {
      return getAssigneesForState(stateName);
   }

   @Override
   public List<IAtsUser> getAssigneesForState(String fromStateName) {
      WorkState state = getState(fromStateName);
      if (state != null) {
         return state.getAssignees();
      }
      return Collections.emptyList();
   }

   @Override
   public List<IAtsUser> getAssignees() {
      List<IAtsUser> assignees = new ArrayList<>();
      WorkState state = getState(getCurrentStateName());
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
   public void addAssignee(IAtsUser assignee) {
      addAssignee(getCurrentStateName(), assignee);
   }

   @Override
   public void addState(String stateName, List<? extends IAtsUser> assignees) {
      addState(stateName, assignees, 0, 0);
   }

   @Override
   public void setAssignees(List<? extends IAtsUser> assignees) {
      setAssignees(getCurrentStateName(), assignees);
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
   public void setPercentComplete(String stateName, int percentComplete) {
      WorkState state = getState(stateName);
      if (state != null) {
         state.setPercentComplete(percentComplete);
         setDirty(true);
      } else {
         throw new OseeStateException("State [%s] not found for %s", stateName, workItem.toStringWithId());
      }
   }

   @Override
   public void setHoursSpent(String stateName, double hoursSpent) {
      WorkState state = getState(stateName);
      if (state != null) {
         state.setHoursSpent(hoursSpent);
         setDirty(true);
      } else {
         throw new OseeStateException("State [%s] not found for %s", stateName, workItem.toStringWithId());
      }
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
   public int getPercentComplete(String stateName) {
      WorkState state = getState(stateName);
      if (state != null) {
         return state.getPercentComplete();
      }
      return 0;
   }

   @Override
   public List<String> getVisitedStateNames() {
      List<String> stateNames = new LinkedList<>();
      for (WorkState state : states) {
         stateNames.add(state.getName());
      }
      return stateNames;
   }

   @Override
   public void removeAssignee(String stateName, IAtsUser assignee) {
      WorkState state = getState(stateName);
      if (state != null) {
         state.removeAssignee(assignee);
         setDirty(true);
      } else {
         throw new OseeStateException("State [%s] not found for %s", stateName, workItem.toStringWithId());
      }
   }

   @Override
   public void setAssignee(IStateToken state, IAtsUser assignee) {
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
      return getAssigneesStr(getCurrentStateName());
   }

   @Override
   public void removeAssignee(IAtsUser assignee) {
      removeAssignee(getCurrentStateName(), assignee);
   }

   @Override
   public boolean isUnAssigned() {
      return getAssignees().contains(AtsCoreUsers.UNASSIGNED_USER);
   }

   @Override
   public void clearAssignees() {
      setAssignees(getCurrentStateName(), new LinkedList<IAtsUser>());
   }

   @Override
   public Collection<IAtsUser> getAssignees(IStateToken state) {
      return getAssignees(state.getName());
   }

   @Override
   public boolean isStateVisited(IStateToken state) {
      return isStateVisited(state.getName());
   }

   @Override
   public String getAssigneesStr(int length) {
      return getAssigneesStr(getCurrentStateName(), length);
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
   public void addAssignees(Collection<? extends IAtsUser> assignees) {
      addAssignees(getCurrentStateName(), assignees);
   }

   @Override
   public void setAssignee(String stateName, IAtsUser assignee) {
      if (assignee != null) {
         setAssignees(stateName, Arrays.asList(assignee));
      }
   }

   @Override
   public boolean isStateVisited(String stateName) {
      return getVisitedStateNames().contains(stateName);
   }

   @Override
   public WorkState createStateData(String name, List<? extends IAtsUser> assignees) {
      Conditions.checkNotNullOrContainNull(assignees, "assignees");
      return new WorkStateImpl(name, assignees);
   }

   @Override
   public WorkState createStateData(String name) {
      return new WorkStateImpl(name);
   }

   @Override
   public WorkState createStateData(String name, List<? extends IAtsUser> assignees, double hoursSpent, int percentComplete) {
      Conditions.checkNotNullOrContainNull(assignees, "assignees");
      return new WorkStateImpl(name, assignees, hoursSpent, percentComplete);
   }

   @Override
   public void addState(WorkState workState) {
      addState(workState, true);
   }

   protected void addState(WorkState state, boolean logError) {
      if (getVisitedStateNames().contains(state.getName())) {
         String errorStr = String.format("Error: Duplicate state [%s] for [%s]", state.getName(), factory.getId());
         if (logError) {
            OseeLog.log(StateManager.class, Level.SEVERE, errorStr);
         }
         return;
      } else {
         states.add(state);
         setDirty(true);
      }
   }

   @Override
   public void validateNoBootstrapUser() {
      for (IAtsUser user : getAssignees()) {
         if (SystemUser.BootStrap.getUserId().equals(user.getUserId())) {
            throw new OseeStateException("Assignee can't be bootstrap user for %s", workItem.toStringWithId());
         }
      }
   }

   @Override
   public String getId() {
      return workItem.getAtsId();
   }

   public void setDirty(boolean dirty) {
      this.dirty = dirty;
   }

   @Override
   public void setPercentCompleteValue(Integer percentComplete) {
      this.percentCompleteValue = percentComplete;
      setDirty(true);
   }

   @Override
   public Integer getPercentCompleteValue() {
      return this.percentCompleteValue;
   }

   public List<IAtsUser> getInitialAssignees() {
      return initialAssignees;
   }

   @Override
   public List<IAtsUser> getAssigneesAdded() {
      List<IAtsUser> added = new ArrayList<>();
      List<IAtsUser> current = getAssignees();
      for (IAtsUser user : current) {
         if (!initialAssignees.contains(user)) {
            added.add(user);
         }
      }
      return added;
   }

   public void clear() {
      initialAssignees.clear();
      states.clear();
      percentCompleteValue = null;
      currentStateName = null;
   }

   @Override
   public String toString() {
      return String.format("StateManager id[%s] for workitem [%s]", instanceId, workItem);
   }

   @Override
   public boolean isInState(IStateToken state) {
      return getCurrentStateName().equals(state.getName());
   }
}
