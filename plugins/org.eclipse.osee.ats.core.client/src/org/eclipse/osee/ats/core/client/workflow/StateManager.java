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

package org.eclipse.osee.ats.core.client.workflow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.WorkState;
import org.eclipse.osee.ats.api.workflow.WorkStateProvider;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogItem;
import org.eclipse.osee.ats.api.workflow.log.LogType;
import org.eclipse.osee.ats.core.AtsCore;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.notify.AtsNotificationManager;
import org.eclipse.osee.ats.core.client.notify.AtsNotifyType;
import org.eclipse.osee.ats.core.client.team.SimpleTeamState;
import org.eclipse.osee.ats.core.client.team.TeamState;
import org.eclipse.osee.ats.core.client.util.AtsUtilCore;
import org.eclipse.osee.ats.core.model.WorkStateFactory;
import org.eclipse.osee.ats.core.model.impl.WorkStateImpl;
import org.eclipse.osee.ats.core.model.impl.WorkStateProviderImpl;
import org.eclipse.osee.ats.core.notify.IAtsNotificationListener;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.QueryOptions;

/**
 * @author Donald G. Dunne
 */
public class StateManager implements IAtsNotificationListener, WorkStateProvider, WorkStateFactory {

   private final AbstractWorkflowArtifact awa;

   private static List<String> allValidStateNames = null;
   private WorkStateProvider stateProvider = null;
   private int loadTransactionNumber;

   public StateManager(AbstractWorkflowArtifact awa) {
      this.awa = awa;
   }

   public synchronized WorkStateProvider getStateProvider() throws OseeCoreException {
      // Refresh the StateProvider on first load or when artifact transaction number changes
      if (stateProvider == null || awa.getTransactionNumber() != loadTransactionNumber) {
         load();
      }
      return stateProvider;
   }

   public void reload() throws OseeCoreException {
      load();
   }

   public synchronized void load() throws OseeCoreException {
      String currentStateXml = awa.getSoleAttributeValue(AtsAttributeTypes.CurrentState, "");
      if (Strings.isValid(currentStateXml)) {
         WorkStateImpl currentState = AtsWorkStateFactory.getFromXml(currentStateXml);
         stateProvider = new WorkStateProviderImpl(this, currentState);
         for (String stateXml : awa.getAttributesToStringList(AtsAttributeTypes.State)) {
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
      loadTransactionNumber = awa.getTransactionNumber();
   }

   @Override
   public String getCurrentStateName() {
      try {
         return getStateProvider().getCurrentStateName();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return "";
   }

   public IStateToken getCurrentState() {
      return new SimpleTeamState(getCurrentStateName(), getCurrentStateType());
   }

   public StateType getCurrentStateType() {
      try {
         if (awa.isAttributeTypeValid(AtsAttributeTypes.CurrentStateType)) {
            // backward compatibility
            if (awa.getSoleAttributeValueAsString(AtsAttributeTypes.CurrentStateType, null) == null) {
               if (getCurrentStateName().equals(TeamState.Completed.getName())) {
                  return StateType.Completed;
               } else if (getCurrentStateName().equals(TeamState.Cancelled.getName())) {
                  return StateType.Cancelled;
               } else {
                  return StateType.Working;
               }
            } else {
               return StateType.valueOf(awa.getSoleAttributeValueAsString(AtsAttributeTypes.CurrentStateType, null));
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return null;
   }

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

      if (AtsClientService.get().getWorkDefinitionAdmin().isStateWeightingEnabled(awa.getWorkDefinition())) {
         provider.setPercentComplete(state.getName(), percentComplete);
      } else {
         awa.setSoleAttributeValue(AtsAttributeTypes.PercentComplete, percentComplete);
      }
      if (logMetrics) {
         logMetrics(awa.getStateMgr().getCurrentState(), AtsClientService.get().getUserAdmin().getCurrentUser(),
            new Date());
      }
      writeToArtifact();
   }

   protected void logMetrics(IStateToken state, IAtsUser user, Date date) throws OseeCoreException {
      String hoursSpent = AtsUtilCore.doubleToI18nString(HoursSpentUtil.getHoursSpentTotal(awa));
      logMetrics(awa, PercentCompleteTotalUtil.getPercentCompleteTotal(awa) + "", hoursSpent, state, user, date);
   }

   public static void logMetrics(AbstractWorkflowArtifact sma, String percent, String hours, IStateToken state, IAtsUser user, Date date) throws OseeCoreException {
      IAtsLogItem logItem =
         AtsCore.getLogFactory().newLogItem(LogType.Metrics, date, user, state.getName(),
            String.format("Percent %s Hours %s", percent, hours), sma.getAtsId());
      sma.getLog().addLogItem(logItem);
   }

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
   public void setMetrics(IStateToken state, double hours, int percentComplete, boolean logMetrics, IAtsUser user, Date date) throws OseeCoreException {
      boolean changed = setMetricsIfChanged(state, hours, percentComplete);
      if (changed) {
         if (logMetrics) {
            logMetrics(awa.getStateMgr().getCurrentState(), AtsClientService.get().getUserAdmin().getCurrentUser(),
               new Date());
         }
         writeToArtifact();
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

   public StateType getStateType() {
      return awa.getStateDefinition().getStateType();
   }

   @Override
   public void addAssignees(String stateName, Collection<? extends IAtsUser> assignees) throws OseeCoreException {
      if (assignees == null || assignees.isEmpty()) {
         return;
      } else if (getStateType().isCompletedOrCancelledState()) {
         throw new OseeStateException("Can't assign completed/cancelled states.");
      }
      getStateProvider().addAssignees(stateName, assignees);
      writeToArtifact();
   }

   public String getHoursSpentStr(String stateName) throws OseeCoreException {
      return AtsUtilCore.doubleToI18nString(getHoursSpent(stateName), true);
   }

   @Override
   public void setAssignee(IAtsUser assignee) throws OseeCoreException {
      setAssignees(Arrays.asList(assignee));
   }

   public void setAssignees(Collection<? extends IAtsUser> assignees) throws OseeCoreException {
      setAssignees(getCurrentStateName(), new LinkedList<IAtsUser>(assignees));
   }

   /**
    * Sets the assignees as attributes and relations AND writes to artifact. Does not persist.
    */
   @Override
   public void setAssignees(String stateName, List<? extends IAtsUser> assignees) throws OseeCoreException {
      if (assignees == null || assignees.isEmpty()) {
         return;
      } else {
         IAtsStateDefinition stateDef = awa.getStateDefinitionByName(stateName);
         StateType stateType = stateDef.getStateType();
         if (stateType.isCompletedOrCancelledState()) {
            throw new OseeStateException("Can't assign completed/cancelled states.");
         }
      }
      getStateProvider().setAssignees(stateName, new LinkedList<IAtsUser>(assignees));
      writeToArtifact();
   }

   public void transitionHelper(List<? extends IAtsUser> toAssignees, IStateToken fromStateName, IStateToken toStateName, String cancelReason) throws OseeCoreException {
      transitionHelper(toAssignees, fromStateName.getName(), toStateName.getName(), cancelReason);
      awa.setSoleAttributeValue(AtsAttributeTypes.CurrentStateType, toStateName.getStateType().name());
      writeToArtifact();
   }

   public void transitionHelper(List<? extends IAtsUser> toAssignees, String fromStateName, String toStateName, String cancelReason) throws OseeCoreException {
      createState(toStateName);
      setAssignees(toStateName, toAssignees);
      setCurrentStateName(toStateName);
      writeToArtifact();
   }

   /**
    * Initializes state machine and sets the current state to stateName
    */
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
      if (awa.isAttributeTypeValid(AtsAttributeTypes.CurrentStateType)) {
         awa.setSoleAttributeValue(AtsAttributeTypes.CurrentStateType, workPage.getStateType().name());
      }
      writeToArtifact();
   }

   public long getTimeInState() throws OseeCoreException {
      return getTimeInState(getCurrentState());
   }

   public long getTimeInState(IStateToken state) throws OseeCoreException {
      if (state == null) {
         return 0;
      }
      IAtsLogItem logItem = awa.getStateStartedData(state);
      if (logItem == null) {
         return 0;
      }
      return new Date().getTime() - logItem.getDate().getTime();
   }

   /**
    * return currently assigned state machine artifacts
    */
   public static Set<Artifact> getAssigned(IAtsUser user) throws OseeCoreException {
      return getAssigned(user, null);
   }

   /**
    * return currently assigned state machine artifacts that match clazz
    * 
    * @param clazz to match or all if null
    */
   public static Set<Artifact> getAssigned(IAtsUser user, Class<?> clazz) throws OseeCoreException {
      return getAssigned(user.getUserId(), clazz);
   }

   /**
    * return currently assigned state machine artifacts that match clazz
    * 
    * @param clazz to match or all if null
    */
   public static Set<Artifact> getAssigned(String userId, Class<?> clazz) throws OseeCoreException {
      Set<Artifact> assigned = new HashSet<Artifact>();
      for (Artifact artifact : ArtifactQuery.getArtifactListFromAttribute(AtsAttributeTypes.CurrentState,
         "<" + userId + ">", AtsUtilCore.getAtsBranch(), QueryOptions.CONTAINS_MATCH_OPTIONS)) {
         if (clazz == null || clazz.isInstance(artifact)) {
            assigned.add(artifact);
         }
      }
      return assigned;

   }

   public static List<IAtsUser> getImplementersByState(AbstractWorkflowArtifact workflow, IStateToken state) throws OseeCoreException {
      List<IAtsUser> users = new ArrayList<IAtsUser>();
      if (workflow.isCancelled()) {
         users.add(workflow.getCancelledBy());
      } else {
         for (IAtsUser user : workflow.getStateMgr().getAssignees(state.getName())) {
            if (!users.contains(user)) {
               users.add(user);
            }
         }
         if (workflow.isCompleted()) {
            IAtsUser user = workflow.getCompletedBy();
            if (user != null && !users.contains(user)) {
               users.add(user);
            }
         }
      }
      return users;
   }

   /**
    * Returns all valid state names for all work definitions in the system
    */
   public synchronized static Collection<? extends String> getAllValidStateNames() {
      if (allValidStateNames == null) {
         allValidStateNames = new ArrayList<String>();
         try {
            Artifact artifact = null;
            try {
               artifact =
                  ArtifactQuery.getArtifactFromToken(AtsArtifactToken.WorkDef_State_Names,
                     AtsUtilCore.getAtsBranchToken());
            } catch (ArtifactDoesNotExist ex) {
               // do nothing
            }
            if (artifact != null) {
               for (String value : artifact.getSoleAttributeValue(CoreAttributeTypes.GeneralStringData, "").split(",")) {
                  allValidStateNames.add(value);
               }
            } else {
               OseeLog.logf(Activator.class, Level.INFO,
                  "ATS Valid State Names: Missing [%s] Artifact; Falling back to loadAddDefinitions",
                  AtsArtifactToken.WorkDef_State_Names.getName());
               try {
                  for (IAtsWorkDefinition workDef : AtsClientService.get().getWorkDefinitionAdmin().loadAllDefinitions()) {
                     for (String stateName : AtsClientService.get().getWorkDefinitionAdmin().getStateNames(workDef)) {
                        if (!allValidStateNames.contains(stateName)) {
                           allValidStateNames.add(stateName);
                        }
                     }
                  }
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
            Collections.sort(allValidStateNames);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return allValidStateNames;
   }

   public static String getCompletedDateByState(AbstractWorkflowArtifact awa, IAtsStateDefinition state) throws OseeCoreException {
      IAtsLogItem stateEvent = awa.getLog().getStateEvent(LogType.StateComplete, state.getName());
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
      writeToArtifact();
   }

   public boolean isDirty() throws OseeCoreException {
      return isDirtyResult().isTrue();
   }

   public Result isDirtyResult() throws OseeCoreException {
      if (awa.getAttributeCount(AtsAttributeTypes.CurrentState) == 0) {
         return new Result(true, "StateManager: Current State new");
      }
      if (!AtsWorkStateFactory.toXml(this, getCurrentStateName()).equals(
         awa.getSoleAttributeValue(AtsAttributeTypes.CurrentState, null))) {
         return new Result(true, "StateManager: Current State modified");
      }
      for (String stateName : getStateProvider().getVisitedStateNames()) {
         if (!stateName.equals(getCurrentStateName())) {
            boolean found = false;
            // Update attribute if it already exists
            Collection<Attribute<String>> attrs = awa.getAttributes(AtsAttributeTypes.State);
            for (Attribute<String> attr : attrs) {
               String attrValue = attr.getValue();
               WorkStateImpl storedState = AtsWorkStateFactory.getFromXml(attrValue);
               if (stateName.equals(storedState.getName())) {
                  found = true;
                  if (!awa.getStateMgr().getStateProvider().isSame(storedState)) {
                     return new Result(true, String.format("StateManager: State [%s] modified was [%s] is [%s]",
                        stateName, attrValue, AtsWorkStateFactory.toXml(awa.getStateMgr(), stateName)));
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

   public void writeToArtifact() throws OseeCoreException {
      awa.setSoleAttributeValue(AtsAttributeTypes.CurrentState, AtsWorkStateFactory.toXml(this, getCurrentStateName()));
      removeCurrentStateAttributeIfExists(getCurrentStateName());
      writeStatesToArtifact();
   }

   private void writeStatesToArtifact() throws OseeCoreException {
      for (String stateName : getStateProvider().getVisitedStateNames()) {
         if (!stateName.equals(getCurrentStateName())) {
            boolean updated = updateStateAttributeIfExsists(stateName);
            // Else, doesn't exist yet, create
            if (!updated) {
               awa.addAttribute(AtsAttributeTypes.State, AtsWorkStateFactory.toXml(awa.getStateMgr(), stateName));
            }
         }
      }
   }

   private void removeCurrentStateAttributeIfExists(String stateName) throws OseeCoreException {
      Collection<Attribute<String>> attrs = awa.getAttributes(AtsAttributeTypes.State);
      for (Attribute<String> attr : attrs) {
         WorkStateImpl storedState = AtsWorkStateFactory.getFromXml(attr.getValue());
         if (stateName.equals(storedState.getName())) {
            attr.delete();
         }
      }
   }

   private boolean updateStateAttributeIfExsists(String stateName) throws OseeCoreException {
      // Update attribute if it already exists
      Collection<Attribute<String>> attrs = awa.getAttributes(AtsAttributeTypes.State);
      for (Attribute<String> attr : attrs) {
         WorkStateImpl storedState = AtsWorkStateFactory.getFromXml(attr.getValue());
         if (stateName.equals(storedState.getName())) {
            attr.setValue(AtsWorkStateFactory.toXml(awa.getStateMgr(), stateName));
            return true;
         }
      }
      return false;
   }

   @Override
   public void notifyAssigned(List<IAtsUser> notifyAssignees) throws OseeCoreException {
      AtsNotificationManager.notify(awa, notifyAssignees, AtsNotifyType.Assigned);
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
      writeToArtifact();
   }

   @Override
   public void addAssignee(IAtsUser assignee) throws OseeCoreException {
      addAssignees(getCurrentStateName(), Arrays.asList(assignee));
   }

   @Override
   public void addState(String stateName, List<? extends IAtsUser> assignees) throws OseeCoreException {
      getStateProvider().addState(stateName, assignees);
      writeToArtifact();
   }

   @Override
   public void setAssignees(List<? extends IAtsUser> assignees) throws OseeCoreException {
      setAssignees(getCurrentStateName(), assignees);
   }

   @Override
   public void createState(String stateName) throws OseeCoreException {
      getStateProvider().createState(stateName);
      writeToArtifact();
   }

   @Override
   public void setPercentComplete(String stateName, int percentComplete) throws OseeCoreException {
      getStateProvider().setPercentComplete(stateName, percentComplete);
      writeToArtifact();
   }

   @Override
   public void setHoursSpent(String stateName, double hoursSpent) throws OseeCoreException {
      getStateProvider().setHoursSpent(stateName, hoursSpent);
      writeToArtifact();
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
      writeToArtifact();
   }

   public void setAssignee(IStateToken state, IAtsUser assignee) throws OseeCoreException {
      getStateProvider().setAssignee(state.getName(), assignee);
      writeToArtifact();
   }

   public void createState(IStateToken state) throws OseeCoreException {
      getStateProvider().createState(state.getName());
      writeToArtifact();
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
      writeToArtifact();
   }

   @Override
   public boolean isUnAssigned() throws OseeCoreException {
      return getStateProvider().isUnAssigned();
   }

   @Override
   public void clearAssignees() throws OseeCoreException {
      getStateProvider().clearAssignees();
      writeToArtifact();
   }

   public Collection<? extends IAtsUser> getAssignees(IStateToken state) throws OseeCoreException {
      return getStateProvider().getAssignees(state.getName());
   }

   public String getAssigneesStr(SimpleTeamState state, int length) throws OseeCoreException {
      return getStateProvider().getAssigneesStr(state.getName(), length);
   }

   public boolean isStateVisited(IStateToken state) throws OseeCoreException {
      return getStateProvider().isStateVisited(state.getName());
   }

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
      writeToArtifact();
   }

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
      return awa.getAtsId();
   }

}
