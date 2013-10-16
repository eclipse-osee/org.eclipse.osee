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

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.notify.AtsNotifyType;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.IAttribute;
import org.eclipse.osee.ats.api.workflow.WorkState;
import org.eclipse.osee.ats.api.workflow.WorkStateProvider;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogItem;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.core.AtsCore;
import org.eclipse.osee.ats.core.model.impl.WorkStateImpl;
import org.eclipse.osee.ats.core.model.impl.WorkStateProviderImpl;
import org.eclipse.osee.ats.core.notify.IAtsNotificationListener;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.util.HoursSpentUtil;
import org.eclipse.osee.ats.core.util.PercentCompleteTotalUtil;
import org.eclipse.osee.ats.core.workflow.state.AtsWorkStateFactory;
import org.eclipse.osee.ats.core.workflow.state.SimpleTeamState;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class StateManager implements IAtsNotificationListener, IAtsStateManager {

   private final IAtsWorkItem workItem;
   private WorkStateProvider stateProvider = null;
   private int loadTransactionNumber;

   public StateManager(IAtsWorkItem workItem) {
      this.workItem = workItem;
   }

   @Override
   public synchronized WorkStateProvider getStateProvider() throws OseeCoreException {
      // Refresh the StateProvider on first load or when workItem transaction number changes
      if (stateProvider == null || AtsCore.getWorkItemService().getTransactionNumber(workItem) != loadTransactionNumber) {
         load();
      }
      return stateProvider;
   }

   @Override
   public void reload() throws OseeCoreException {
      load();
   }

   @Override
   public synchronized void load() throws OseeCoreException {
      String currentStateXml =
         AtsCore.getAttrResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.CurrentState, "");
      if (Strings.isValid(currentStateXml)) {
         WorkStateImpl currentState = AtsWorkStateFactory.getFromXml(currentStateXml);
         stateProvider = new WorkStateProviderImpl(this, currentState);
         for (String stateXml : AtsCore.getAttrResolver().getAttributesToStringList(workItem, AtsAttributeTypes.State)) {
            WorkStateImpl state = AtsWorkStateFactory.getFromXml(stateXml);
            if (!state.getName().equals(currentState.getName())) {
               if (!state.getName().equals(currentState.getName())) {
                  stateProvider.addState(state);
               }
            }
         }
      } else {
         stateProvider = new WorkStateProviderImpl(this);
      }
      ((WorkStateProviderImpl) stateProvider).setNotificationListener(this);
      loadTransactionNumber = AtsCore.getWorkItemService().getTransactionNumber(workItem);
   }

   @Override
   public String getCurrentStateName() {
      try {
         return getStateProvider().getCurrentStateName();
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsCore.class, Level.SEVERE, ex);
      }
      return "";
   }

   @Override
   public IStateToken getCurrentState() {
      return new SimpleTeamState(getCurrentStateName(), getCurrentStateType());
   }

   @Override
   public StateType getCurrentStateType() {
      try {
         if (AtsCore.getAttrResolver().isAttributeTypeValid(workItem, AtsAttributeTypes.CurrentStateType)) {
            // backward compatibility
            if (AtsCore.getAttrResolver().getSoleAttributeValueAsString(workItem, AtsAttributeTypes.CurrentStateType,
               null) == null) {
               if (getCurrentStateName().equals(TeamState.Completed.getName())) {
                  return StateType.Completed;
               } else if (getCurrentStateName().equals(TeamState.Cancelled.getName())) {
                  return StateType.Cancelled;
               } else {
                  return StateType.Working;
               }
            } else {
               return StateType.valueOf(AtsCore.getAttrResolver().getSoleAttributeValueAsString(workItem,
                  AtsAttributeTypes.CurrentStateType, null));
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsCore.class, Level.SEVERE, ex);
      }
      return null;
   }

   @Override
   public void updateMetrics(IStateToken state, double additionalHours, int percentComplete, boolean logMetrics) throws OseeCoreException {
      WorkStateProvider provider = getStateProvider();

      // get hours in current state, if additional hours + current hours < 0, walk other states subtracting difference
      double hoursInCurrentState = provider.getHoursSpent(state.getName());
      double remaining = hoursInCurrentState + additionalHours;
      if (remaining < 0.0) {
         provider.setHoursSpent(state.getName(), 0.0);
         for (String stateName : provider.getVisitedStateNames()) {
            hoursInCurrentState = provider.getHoursSpent(stateName);
            remaining += hoursInCurrentState;
            if (remaining < 0.0) {
               provider.setHoursSpent(stateName, 0.0);
            } else {
               provider.setHoursSpent(stateName, remaining);
               break;
            }
         }
      } else {
         provider.setHoursSpent(state.getName(), remaining);
      }

      if (AtsCore.getWorkDefService().isStateWeightingEnabled(workItem.getWorkDefinition())) {
         provider.setPercentComplete(state.getName(), percentComplete);
      } else {
         AtsCore.getAttrResolver().setSoleAttributeValue(workItem, AtsAttributeTypes.PercentComplete, percentComplete);
      }
      if (logMetrics) {
         logMetrics(workItem.getStateMgr().getCurrentState(), AtsCore.getUserService().getCurrentUser(), new Date());
      }
      writeToStore();
   }

   protected void logMetrics(IStateToken state, IAtsUser user, Date date) throws OseeCoreException {
      String hoursSpent = AtsUtilCore.doubleToI18nString(HoursSpentUtil.getHoursSpentTotal(workItem));
      logMetrics(workItem, PercentCompleteTotalUtil.getPercentCompleteTotal(workItem) + "", hoursSpent, state, user,
         date);
   }

   public static void logMetrics(IAtsWorkItem workItem, String percent, String hours, IStateToken state, IAtsUser user, Date date) throws OseeCoreException {
      IAtsLogItem logItem =
         AtsCore.getLogFactory().newLogItem(LogType.Metrics, date, user, state.getName(),
            String.format("Percent %s Hours %s", percent, hours), workItem.getAtsId());
      workItem.getLog().addLogItem(logItem);
   }

   @Override
   public void setMetrics(double hours, int percentComplete, boolean logMetrics, IAtsUser user, Date date) throws OseeCoreException {
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
   public void setMetrics(IStateToken state, double hours, int percentComplete, boolean logMetrics, IAtsUser user, Date date) throws OseeCoreException {
      boolean changed = setMetricsIfChanged(state, hours, percentComplete);
      if (changed) {
         if (logMetrics) {
            logMetrics(workItem.getStateMgr().getCurrentState(), AtsCore.getUserService().getCurrentUser(), new Date());
         }
         writeToStore();
      }
   }

   protected boolean setMetricsIfChanged(IStateToken state, double hours, int percentComplete) throws OseeCoreException {
      boolean changed = false;
      if (!isHoursEqual(getStateProvider().getHoursSpent(state.getName()), hours)) {
         getStateProvider().setHoursSpent(state.getName(), hours);
         changed = true;
      }
      if (percentComplete != getStateProvider().getPercentComplete(state.getName())) {
         getStateProvider().setPercentComplete(state.getName(), percentComplete);
         changed = true;
      }
      return changed;
   }

   @Override
   public StateType getStateType() throws OseeCoreException {
      return workItem.getStateDefinition().getStateType();
   }

   @Override
   public void addAssignees(String stateName, Collection<? extends IAtsUser> assignees) throws OseeCoreException {
      if (assignees == null || assignees.isEmpty()) {
         return;
      } else if (getStateType().isCompletedOrCancelledState()) {
         throw new OseeStateException("Can't assign completed/cancelled states.");
      }
      getStateProvider().addAssignees(stateName, assignees);
      writeToStore();
   }

   @Override
   public String getHoursSpentStr(String stateName) throws OseeCoreException {
      return AtsUtilCore.doubleToI18nString(getHoursSpent(stateName), true);
   }

   @Override
   public void setAssignee(IAtsUser assignee) throws OseeCoreException {
      setAssignees(Arrays.asList(assignee));
   }

   @Override
   public void setAssignees(Collection<? extends IAtsUser> assignees) throws OseeCoreException {
      setAssignees(getCurrentStateName(), new LinkedList<IAtsUser>(assignees));
   }

   /**
    * Sets the assignees as attributes and relations AND writes to store. Does not persist.
    */

   @Override
   public void setAssignees(String stateName, List<? extends IAtsUser> assignees) throws OseeCoreException {
      if (assignees == null || assignees.isEmpty()) {
         return;
      } else {
         IAtsStateDefinition stateDef = workItem.getWorkDefinition().getStateByName(stateName);
         StateType stateType = stateDef.getStateType();
         if (stateType.isCompletedOrCancelledState()) {
            throw new OseeStateException("Can't assign completed/cancelled states.");
         }
      }
      getStateProvider().setAssignees(stateName, new LinkedList<IAtsUser>(assignees));
      writeToStore();
   }

   @Override
   public void transitionHelper(List<? extends IAtsUser> toAssignees, IStateToken fromStateName, IStateToken toStateName, String cancelReason) throws OseeCoreException {
      transitionHelper(toAssignees, fromStateName.getName(), toStateName.getName(), cancelReason);
      AtsCore.getAttrResolver().setSoleAttributeValue(workItem, AtsAttributeTypes.CurrentStateType,
         toStateName.getStateType().name());
      writeToStore();
   }

   @Override
   public void transitionHelper(List<? extends IAtsUser> toAssignees, String fromStateName, String toStateName, String cancelReason) throws OseeCoreException {
      createState(toStateName);
      setAssignees(toStateName, toAssignees);
      setCurrentStateName(toStateName);
      writeToStore();
   }

   /**
    * Initializes state machine and sets the current state to stateName
    */

   @Override
   public void initializeStateMachine(IStateToken workPage, List<? extends IAtsUser> assignees, IAtsUser currentUser) throws OseeCoreException {
      getStateProvider().createState(workPage.getName());
      getStateProvider().setCurrentStateName(workPage.getName());
      if (assignees == null) {
         assignees = new LinkedList<IAtsUser>();
      }
      if (workPage.getStateType().isWorkingState()) {
         if (assignees.isEmpty()) {
            setAssignees(Arrays.asList(currentUser));
         } else {
            setAssignees(assignees);
         }
      }
      if (AtsCore.getAttrResolver().isAttributeTypeValid(workItem, AtsAttributeTypes.CurrentStateType)) {
         AtsCore.getAttrResolver().setSoleAttributeValue(workItem, AtsAttributeTypes.CurrentStateType,
            workPage.getStateType().name());
      }
      writeToStore();
   }

   @Override
   public long getTimeInState() throws OseeCoreException {
      return getTimeInState(getCurrentState());
   }

   @Override
   public long getTimeInState(IStateToken state) throws OseeCoreException {
      if (state == null) {
         return 0;
      }
      IAtsLogItem logItem = workItem.getStateStartedData(state);
      if (logItem == null) {
         return 0;
      }
      return new Date().getTime() - logItem.getDate().getTime();
   }

   public static String getCompletedDateByState(IAtsWorkItem workItem, IAtsStateDefinition state) throws OseeCoreException {
      IAtsLogItem stateEvent = workItem.getLog().getStateEvent(LogType.StateComplete, state.getName());
      if (stateEvent != null && stateEvent.getDate() != null) {
         return DateUtil.getMMDDYYHHMM(stateEvent.getDate());
      }
      return "";
   }

   @Override
   public void addAssignee(String stateName, IAtsUser assignee) throws OseeCoreException {
      addAssignees(stateName, Arrays.asList(assignee));
   }

   @Override
   public void addState(String stateName, List<? extends IAtsUser> assignees, double hoursSpent, int percentComplete) throws OseeCoreException {
      getStateProvider().addState(stateName, assignees, hoursSpent, percentComplete);
      writeToStore();
   }

   @Override
   public boolean isDirty() throws OseeCoreException {
      return isDirtyResult().isTrue();
   }

   @Override
   public Result isDirtyResult() throws OseeCoreException {
      if (AtsCore.getAttrResolver().getAttributeCount(workItem, AtsAttributeTypes.CurrentState) == 0) {
         return new Result(true, "StateManager: Current State new");
      }
      if (!AtsWorkStateFactory.toXml(this, getCurrentStateName()).equals(
         AtsCore.getAttrResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.CurrentState, null))) {
         return new Result(true, "StateManager: Current State modified");
      }
      for (String stateName : getStateProvider().getVisitedStateNames()) {
         if (!stateName.equals(getCurrentStateName())) {
            boolean found = false;
            // Update attribute if it already exists
            Collection<IAttribute<String>> attrs =
               AtsCore.getAttrResolver().getAttributes(workItem, AtsAttributeTypes.State);
            for (IAttribute<String> attr : attrs) {
               String attrValue = attr.getValue();
               WorkStateImpl storedState = AtsWorkStateFactory.getFromXml(attrValue);
               if (stateName.equals(storedState.getName())) {
                  found = true;
                  if (!workItem.getStateMgr().getStateProvider().isSame(storedState)) {
                     return new Result(true, String.format("StateManager: State [%s] modified was [%s] is [%s]",
                        stateName, attrValue, AtsWorkStateFactory.toXml(workItem.getStateMgr(), stateName)));
                  }
               }
            }
            // Else, doesn't exist yet so it's dirty
            if (!found) {
               return new Result(true, String.format("StateManager: State [%s] added", stateName));
            }
         }
      }
      return Result.FalseResult;
   }

   @Override
   public void writeToStore() throws OseeCoreException {
      AtsCore.getAttrResolver().setSoleAttributeValue(workItem, AtsAttributeTypes.CurrentState,
         AtsWorkStateFactory.toXml(this, getCurrentStateName()));
      removeCurrentStateAttributeIfExists(getCurrentStateName());
      writeStatesToStore();
   }

   private void writeStatesToStore() throws OseeCoreException {
      for (String stateName : getStateProvider().getVisitedStateNames()) {
         if (!stateName.equals(getCurrentStateName())) {
            boolean updated = updateStateAttributeIfExsists(stateName);
            // Else, doesn't exist yet, create
            if (!updated) {
               AtsCore.getAttrResolver().addAttribute(workItem, AtsAttributeTypes.State,
                  AtsWorkStateFactory.toXml(workItem.getStateMgr(), stateName));
            }
         }
      }
   }

   private void removeCurrentStateAttributeIfExists(String stateName) throws OseeCoreException {
      Collection<IAttribute<String>> attrs = AtsCore.getAttrResolver().getAttributes(workItem, AtsAttributeTypes.State);
      for (IAttribute<String> attr : attrs) {
         WorkStateImpl storedState = AtsWorkStateFactory.getFromXml(attr.getValue());
         if (stateName.equals(storedState.getName())) {
            attr.delete();
         }
      }
   }

   private boolean updateStateAttributeIfExsists(String stateName) throws OseeCoreException {
      // Update attribute if it already exists
      Collection<IAttribute<String>> attrs = AtsCore.getAttrResolver().getAttributes(workItem, AtsAttributeTypes.State);
      for (IAttribute<String> attr : attrs) {
         WorkStateImpl storedState = AtsWorkStateFactory.getFromXml(attr.getValue());
         if (stateName.equals(storedState.getName())) {
            attr.setValue(AtsWorkStateFactory.toXml(workItem.getStateMgr(), stateName));
            return true;
         }
      }
      return false;
   }

   @Override
   public void notifyAssigned(List<IAtsUser> notifyAssignees) throws OseeCoreException {
      AtsCore.getNotifyService().notify(workItem, notifyAssignees, AtsNotifyType.Assigned);
   }

   @Override
   public List<IAtsUser> getAssignees(String stateName) throws OseeCoreException {
      return getStateProvider().getAssignees(stateName);
   }

   @Override
   public List<IAtsUser> getAssigneesForState(String fromStateName) throws OseeCoreException {
      return getStateProvider().getAssigneesForState(fromStateName);
   }

   @Override
   public List<IAtsUser> getAssignees() throws OseeCoreException {
      return getStateProvider().getAssignees();
   }

   @Override
   public void setCurrentStateName(String currentStateName) throws OseeCoreException {
      getStateProvider().setCurrentStateName(currentStateName);
      writeToStore();
   }

   @Override
   public void addAssignee(IAtsUser assignee) throws OseeCoreException {
      addAssignees(getCurrentStateName(), Arrays.asList(assignee));
   }

   @Override
   public void addState(String stateName, List<? extends IAtsUser> assignees) throws OseeCoreException {
      getStateProvider().addState(stateName, assignees);
      writeToStore();
   }

   @Override
   public void setAssignees(List<? extends IAtsUser> assignees) throws OseeCoreException {
      setAssignees(getCurrentStateName(), assignees);
   }

   @Override
   public void createState(String stateName) throws OseeCoreException {
      getStateProvider().createState(stateName);
      writeToStore();
   }

   @Override
   public void setPercentComplete(String stateName, int percentComplete) throws OseeCoreException {
      getStateProvider().setPercentComplete(stateName, percentComplete);
      writeToStore();
   }

   @Override
   public void setHoursSpent(String stateName, double hoursSpent) throws OseeCoreException {
      getStateProvider().setHoursSpent(stateName, hoursSpent);
      writeToStore();
   }

   @Override
   public double getHoursSpent(String stateName) throws OseeCoreException {
      return getStateProvider().getHoursSpent(stateName);
   }

   @Override
   public int getPercentComplete(String stateName) throws OseeCoreException {
      return getStateProvider().getPercentComplete(stateName);
   }

   @Override
   public List<String> getVisitedStateNames() throws OseeCoreException {
      return getStateProvider().getVisitedStateNames();
   }

   @Override
   public void removeAssignee(String stateName, IAtsUser assignee) throws OseeCoreException {
      getStateProvider().removeAssignee(stateName, assignee);
      writeToStore();
   }

   @Override
   public void setAssignee(IStateToken state, IAtsUser assignee) throws OseeCoreException {
      getStateProvider().setAssignee(state.getName(), assignee);
      writeToStore();
   }

   @Override
   public void createState(IStateToken state) throws OseeCoreException {
      getStateProvider().createState(state.getName());
      writeToStore();
   }

   @Override
   public boolean isUnAssignedSolely() throws OseeCoreException {
      return getStateProvider().isUnAssignedSolely();
   }

   @Override
   public String getAssigneesStr() throws OseeCoreException {
      return getStateProvider().getAssigneesStr();
   }

   @Override
   public void removeAssignee(IAtsUser assignee) throws OseeCoreException {
      getStateProvider().removeAssignee(assignee);
      writeToStore();
   }

   @Override
   public boolean isUnAssigned() throws OseeCoreException {
      return getStateProvider().isUnAssigned();
   }

   @Override
   public void clearAssignees() throws OseeCoreException {
      getStateProvider().clearAssignees();
      writeToStore();
   }

   @Override
   public Collection<? extends IAtsUser> getAssignees(IStateToken state) throws OseeCoreException {
      return getStateProvider().getAssignees(state.getName());
   }

   @Override
   public boolean isStateVisited(IStateToken state) throws OseeCoreException {
      return getStateProvider().isStateVisited(state.getName());
   }

   @Override
   public String getAssigneesStr(int length) throws OseeCoreException {
      return getStateProvider().getAssigneesStr(getCurrentStateName(), length);
   }

   @Override
   public String getAssigneesStr(String stateName, int length) throws OseeCoreException {
      return getStateProvider().getAssigneesStr(stateName, length);
   }

   @Override
   public String getAssigneesStr(String stateName) throws OseeCoreException {
      return getStateProvider().getAssigneesStr(stateName);
   }

   @Override
   public void addAssignees(Collection<? extends IAtsUser> assignees) throws OseeCoreException {
      addAssignees(getCurrentStateName(), assignees);
   }

   @Override
   public void setAssignee(String stateName, IAtsUser assignee) throws OseeCoreException {
      setAssignees(stateName, Arrays.asList(assignee));
   }

   @Override
   public boolean isStateVisited(String stateName) throws OseeCoreException {
      return getStateProvider().isStateVisited(stateName);
   }

   @Override
   public WorkState createStateData(String name, List<? extends IAtsUser> assignees) {
      return new WorkStateImpl(name, assignees);
   }

   @Override
   public WorkState createStateData(String name) {
      return new WorkStateImpl(name);
   }

   @Override
   public WorkState createStateData(String name, List<? extends IAtsUser> assignees, double hoursSpent, int percentComplete) {
      return new WorkStateImpl(name, assignees, hoursSpent, percentComplete);
   }

   @Override
   public void addState(WorkState workState) throws OseeCoreException {
      getStateProvider().addState(workState);
      writeToStore();
   }

   @Override
   public void validateNoBootstrapUser() throws OseeCoreException {
      for (IAtsUser user : getAssignees()) {
         if (SystemUser.BootStrap.getUserId().equals(user.getUserId())) {
            throw new OseeStateException("Assignee can't be bootstrap user");
         }
      }
   }

   @Override
   public boolean isSame(WorkState workState) throws OseeCoreException {
      return getStateProvider().isSame(workState);
   }

   @Override
   public String getId() {
      return workItem.getAtsId();
   }

}
