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
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.notify.AtsNotificationManager;
import org.eclipse.osee.ats.core.client.notify.AtsNotifyType;
import org.eclipse.osee.ats.core.client.team.SimpleTeamState;
import org.eclipse.osee.ats.core.client.team.TeamState;
import org.eclipse.osee.ats.core.client.util.AtsUsersClient;
import org.eclipse.osee.ats.core.client.util.AtsUtilCore;
import org.eclipse.osee.ats.core.client.workdef.WorkDefinitionFactory;
import org.eclipse.osee.ats.core.client.workflow.log.LogItem;
import org.eclipse.osee.ats.core.client.workflow.log.LogType;
import org.eclipse.osee.ats.core.model.IAtsUser;
import org.eclipse.osee.ats.core.model.WorkState;
import org.eclipse.osee.ats.core.model.WorkStateFactory;
import org.eclipse.osee.ats.core.model.WorkStateProvider;
import org.eclipse.osee.ats.core.model.impl.WorkStateImpl;
import org.eclipse.osee.ats.core.model.impl.WorkStateProviderImpl;
import org.eclipse.osee.ats.core.notify.IAtsNotificationListener;
import org.eclipse.osee.ats.core.users.AtsUsers;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.ats.core.workdef.WorkDefinition;
import org.eclipse.osee.ats.core.workflow.IWorkPage;
import org.eclipse.osee.ats.core.workflow.WorkPageType;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class StateManager implements IAtsNotificationListener, WorkStateProvider, WorkStateFactory {

   private final AbstractWorkflowArtifact awa;
   private static List<String> allValidtateNames = null;
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

   public IWorkPage getCurrentState() {
      return new SimpleTeamState(getCurrentStateName(), getCurrentWorkPageType());
   }

   public WorkPageType getCurrentWorkPageType() {
      try {
         if (awa.isAttributeTypeValid(AtsAttributeTypes.CurrentStateType)) {
            // backward compatibility
            if (awa.getSoleAttributeValueAsString(AtsAttributeTypes.CurrentStateType, null) == null) {
               if (getCurrentStateName().equals(TeamState.Completed.getPageName())) {
                  return WorkPageType.Completed;
               } else if (getCurrentStateName().equals(TeamState.Cancelled.getPageName())) {
                  return WorkPageType.Cancelled;
               } else {
                  return WorkPageType.Working;
               }
            } else {
               return WorkPageType.valueOf(awa.getSoleAttributeValueAsString(AtsAttributeTypes.CurrentStateType, null));
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return null;
   }

   public void updateMetrics(IWorkPage state, double additionalHours, int percentComplete, boolean logMetrics) throws OseeCoreException {
      getStateProvider().setHoursSpent(state.getPageName(),
         getStateProvider().getHoursSpent(state.getPageName()) + additionalHours);
      if (awa.getWorkDefinition().isStateWeightingEnabled()) {
         getStateProvider().setPercentComplete(state.getPageName(), percentComplete);
      } else {
         awa.setSoleAttributeValue(AtsAttributeTypes.PercentComplete, percentComplete);
      }
      if (logMetrics) {
         logMetrics(awa.getStateMgr().getCurrentState(), AtsUsersClient.getUser(), new Date());
      }
      writeToArtifact();
   }

   protected void logMetrics(IWorkPage state, IAtsUser user, Date date) throws OseeCoreException {
      String hoursSpent = AtsUtilCore.doubleToI18nString(HoursSpentUtil.getHoursSpentTotal(awa));
      logMetrics(awa, PercentCompleteTotalUtil.getPercentCompleteTotal(awa) + "", hoursSpent, state, user, date);
   }

   public static void logMetrics(AbstractWorkflowArtifact sma, String percent, String hours, IWorkPage state, IAtsUser user, Date date) throws OseeCoreException {
      LogItem logItem =
         new LogItem(LogType.Metrics, date, user, state.getPageName(), String.format("Percent %s Hours %s", percent,
            hours), sma.getHumanReadableId());
      sma.getLog().addLogItem(logItem);
   }

   public void setMetrics(double hours, int percentComplete, boolean logMetrics, IAtsUser user, Date date) throws OseeCoreException {
      setMetrics(getCurrentState(), hours, percentComplete, logMetrics, user, date);
   }

   public void setMetrics(IWorkPage state, double hours, int percentComplete, boolean logMetrics, IAtsUser user, Date date) throws OseeCoreException {
      getStateProvider().setHoursSpent(state.getPageName(), hours);
      getStateProvider().setPercentComplete(state.getPageName(), percentComplete);
      if (logMetrics) {
         logMetrics(awa.getStateMgr().getCurrentState(), AtsUsersClient.getUser(), new Date());
      }
      writeToArtifact();
   }

   public WorkPageType getWorkPageType() {
      return awa.getStateDefinition().getWorkPageType();
   }

   @Override
   public void addAssignees(String stateName, Collection<? extends IAtsUser> assignees) throws OseeCoreException {
      if (assignees == null || assignees.isEmpty()) {
         return;
      } else if (getWorkPageType().isCompletedOrCancelledPage()) {
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
      setAssignees(getCurrentStateName(), AtsUsers.toList(assignees));
   }

   /**
    * Sets the assignees as attributes and relations AND writes to artifact. Does not persist.
    */
   @Override
   public void setAssignees(String stateName, List<? extends IAtsUser> assignees) throws OseeCoreException {
      if (assignees == null || assignees.isEmpty()) {
         return;
      } else if (getWorkPageType().isCompletedOrCancelledPage()) {
         throw new OseeStateException("Can't assign completed/cancelled states.");
      }
      getStateProvider().setAssignees(stateName, AtsUsers.toList(assignees));
      writeToArtifact();
   }

   public void transitionHelper(List<? extends IAtsUser> toAssignees, IWorkPage fromStateName, IWorkPage toStateName, String cancelReason) throws OseeCoreException {
      transitionHelper(toAssignees, fromStateName.getPageName(), toStateName.getPageName(), cancelReason);
      awa.setSoleAttributeValue(AtsAttributeTypes.CurrentStateType, toStateName.getWorkPageType().name());
      writeToArtifact();
   }

   public void transitionHelper(List<? extends IAtsUser> toAssignees, String fromStateName, String toStateName, String cancelReason) throws OseeCoreException {
      createState(toStateName);
      setCurrentStateName(toStateName);
      setAssignees(getCurrentStateName(), toAssignees);
      writeToArtifact();
   }

   /**
    * Initializes state machine and sets the current state to stateName
    */
   public void initializeStateMachine(IWorkPage workPage, List<? extends IAtsUser> assignees, IAtsUser currentUser) throws OseeCoreException {
      getStateProvider().createState(workPage.getPageName());
      getStateProvider().setCurrentStateName(workPage.getPageName());
      if (assignees == null) {
         assignees = new LinkedList<IAtsUser>();
      }
      if (workPage.isWorkingPage()) {
         if (assignees.isEmpty()) {
            setAssignees(Arrays.asList(currentUser));
         } else {
            setAssignees(assignees);
         }
      }
      if (awa.isAttributeTypeValid(AtsAttributeTypes.CurrentStateType)) {
         awa.setSoleAttributeValue(AtsAttributeTypes.CurrentStateType, workPage.getWorkPageType().name());
      }
      writeToArtifact();
   }

   public long getTimeInState() throws OseeCoreException {
      return getTimeInState(getCurrentState());
   }

   public long getTimeInState(IWorkPage state) throws OseeCoreException {
      if (state == null) {
         return 0;
      }
      LogItem logItem = awa.getStateStartedData(state);
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
         "%<" + userId + ">%", AtsUtilCore.getAtsBranch())) {
         if (clazz == null || clazz.isInstance(artifact)) {
            assigned.add(artifact);
         }
      }
      return assigned;

   }

   public static List<IAtsUser> getImplementersByState(AbstractWorkflowArtifact workflow, IWorkPage state) throws OseeCoreException {
      List<IAtsUser> users = new ArrayList<IAtsUser>();
      if (workflow.isCancelled()) {
         users.add(workflow.getCancelledBy());
      } else {
         for (IAtsUser user : workflow.getStateMgr().getAssignees(state.getPageName())) {
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
      if (allValidtateNames == null) {
         allValidtateNames = new ArrayList<String>();
         try {
            for (WorkDefinition workDef : WorkDefinitionFactory.loadAllDefinitions()) {
               for (StateDefinition state : workDef.getStates()) {
                  if (!allValidtateNames.contains(state.getName())) {
                     allValidtateNames.add(state.getName());
                  }
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
         Collections.sort(allValidtateNames);
      }
      return allValidtateNames;
   }

   public static String getCompletedDateByState(AbstractWorkflowArtifact awa, StateDefinition state) throws OseeCoreException {
      LogItem stateEvent = awa.getLog().getStateEvent(LogType.StateComplete, state.getPageName());
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

   public void setAssignee(IWorkPage state, IAtsUser assignee) throws OseeCoreException {
      getStateProvider().setAssignee(state.getPageName(), assignee);
      writeToArtifact();
   }

   public void createState(IWorkPage state) throws OseeCoreException {
      getStateProvider().createState(state.getPageName());
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

   public Collection<? extends IAtsUser> getAssignees(IWorkPage state) throws OseeCoreException {
      return getStateProvider().getAssignees(state.getPageName());
   }

   public String getAssigneesStr(SimpleTeamState state, int length) throws OseeCoreException {
      return getStateProvider().getAssigneesStr(state.getPageName(), length);
   }

   public boolean isStateVisited(IWorkPage state) throws OseeCoreException {
      return getStateProvider().isStateVisited(state.getPageName());
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

}
