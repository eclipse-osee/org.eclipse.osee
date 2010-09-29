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

package org.eclipse.osee.ats.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.log.LogItem;
import org.eclipse.osee.ats.artifact.log.LogType;
import org.eclipse.osee.ats.util.widgets.SMAState;
import org.eclipse.osee.ats.util.widgets.XCurrentStateDam;
import org.eclipse.osee.ats.util.widgets.XStateDam;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;

/**
 * @author Donald G. Dunne
 */
public class StateManager {

   private final XCurrentStateDam currentStateDam;
   private final XStateDam stateDam;
   private final AbstractWorkflowArtifact sma;

   public StateManager(AbstractWorkflowArtifact sma) throws OseeCoreException {
      super();
      this.sma = sma;
      currentStateDam = new XCurrentStateDam(sma);
      stateDam = new XStateDam(sma);
   }

   /**
    * Get state and create if not there.
    * 
    * @return state matching name
    */
   private SMAState getSMAState(String name, boolean create) throws OseeCoreException {
      if (currentStateDam.getState().getName().equals(name)) {
         return currentStateDam.getState();
      } else {
         return stateDam.getState(name, create);
      }
   }

   /**
    * Discouraged Access. This method should not normally be called except in cases were state data is being manually
    * created.
    */
   public void internalCreateIfNotExists(String name) throws OseeCoreException {
      if (isStateVisited(name)) {
         return;
      }
      SMAState smaState = getSMAState(name, true);
      putState(smaState);
   }

   /**
    * @return true if UnAssigned user is currently an assignee
    */
   public boolean isUnAssigned() throws OseeCoreException {
      return getAssignees().contains(UserManager.getUser(SystemUser.UnAssigned));
   }

   public boolean isUnAssignedSolely() throws OseeCoreException {
      return getAssignees().size() == 1 && isUnAssigned();
   }

   /**
    * Return Hours Spent for State
    * 
    * @return hours spent or 0 if none
    */
   public double getHoursSpent(String stateName) throws OseeCoreException {
      SMAState state = getSMAState(stateName, false);
      if (state == null) {
         return 0.0;
      }
      return state.getHoursSpent();
   }

   public double getHoursSpent() throws OseeCoreException {
      return getHoursSpent(getCurrentStateName());
   }

   /**
    * Return Percent Complete for State
    * 
    * @return percent complete or 0 if none
    */
   public int getPercentComplete(String stateName) throws OseeCoreException {
      if (stateName.equals(DefaultTeamState.Completed.name()) || stateName.equals(DefaultTeamState.Cancelled.name())) {
         return 100;
      }
      SMAState state = getSMAState(stateName, false);
      if (state == null) {
         return 0;
      }
      return state.getPercentComplete();

   }

   public int getPercentComplete() throws OseeCoreException {
      return getPercentComplete(getCurrentStateName());
   }

   public String getCurrentStateName() {
      try {
         return currentStateDam.getState().getName();
      } catch (OseeCoreException ex) {
         return ex.getLocalizedMessage();
      }
   }

   public String getAssigneesStr() throws OseeCoreException {
      return Artifacts.toString("; ", sma.getStateMgr().getAssignees());
   }

   public String getAssigneesStr(int length) throws OseeCoreException {
      String str = getAssigneesStr();
      if (str.length() > length) {
         return str.substring(0, length - 1) + "...";
      }
      return str;
   }

   public Collection<User> getAssignees() throws OseeCoreException {
      return getAssignees(getCurrentStateName());
   }

   public Collection<User> getAssignees(String stateName) throws OseeCoreException {
      SMAState state = getSMAState(stateName, false);
      if (state == null) {
         return Collections.emptyList();
      } else {
         return state.getAssignees();
      }
   }

   public void updateMetrics(double additionalHours, int percentComplete, boolean logMetrics) throws OseeCoreException {
      updateMetrics(getCurrentStateName(), additionalHours, percentComplete, logMetrics);
   }

   public void updateMetrics(String stateName, double additionalHours, int percentComplete, boolean logMetrics) throws OseeCoreException {
      if (stateName.equals(getCurrentStateName())) {
         currentStateDam.updateMetrics(additionalHours, percentComplete, logMetrics);
      } else {
         stateDam.updateMetrics(stateName, additionalHours, percentComplete, logMetrics);
      }
   }

   public void setMetrics(double hours, int percentComplete, boolean logMetrics) throws OseeCoreException {
      currentStateDam.setMetrics(hours, percentComplete, logMetrics);
   }

   /**
    * Sets the assignees as attributes and relations AND writes to SMA. Does not persist.
    */
   public void setAssignees(Collection<User> assignees) throws OseeCoreException {
      SMAState state = getSMAState(getCurrentStateName(), false);
      state.setAssignees(assignees);
      putState(state);
   }

   /**
    * Sets the assignee AND writes to SMA. Does not persist.
    */
   public void setAssignee(String stateName, User assignee) throws OseeCoreException {
      if (!isStateVisited(stateName)) {
         throw new OseeArgumentException("State [%s] does not exist.", stateName);
      }
      SMAState state = getSMAState(stateName, false);
      state.setAssignee(assignee);
      putState(state);
   }

   /**
    * Sets the assignee AND writes to SMA. Does not persist.
    */
   public void setAssignee(User assignee) throws OseeCoreException {
      SMAState state = getSMAState(getCurrentStateName(), false);
      state.setAssignee(assignee);
      putState(state);
   }

   /**
    * Removes the assignee from stateName state AND writes to SMA. Does not persist.
    */
   public void removeAssignee(String stateName, User assignee) throws OseeCoreException {
      if (!isStateVisited(stateName)) {
         return;
      }
      SMAState state = getSMAState(stateName, false);
      state.removeAssignee(assignee);
      putState(state);
   }

   /**
    * Removes the assignee AND writes to SMA. Does not persist.
    */
   public void removeAssignee(User assignee) throws OseeCoreException {
      SMAState state = getSMAState(getCurrentStateName(), false);
      state.removeAssignee(assignee);
      putState(state);
   }

   /**
    * Adds the assignee AND writes to SMA. Does not persist. Will remove UnAssigned user if another assignee exists.
    */
   public void addAssignee(User assignee) throws OseeCoreException {
      SMAState state = getSMAState(getCurrentStateName(), false);
      state.addAssignee(assignee);
      if (state.getAssignees().size() > 1 && state.getAssignees().contains(UserManager.getUser(SystemUser.UnAssigned))) {
         state.removeAssignee(UserManager.getUser(SystemUser.UnAssigned));
      }
      putState(state);
   }

   /**
    * Removes ALL assignees AND writes to SMA. Does not persist.
    */
   public void clearAssignees() throws OseeCoreException {
      SMAState state = getSMAState(getCurrentStateName(), false);
      state.clearAssignees();
      putState(state);
   }

   public boolean isStateVisited(String name) throws OseeCoreException {
      return getVisitedStateNames().contains(name);
   }

   public void transitionHelper(Collection<User> toAssignees, boolean persist, WorkPageDefinition fromPage, WorkPageDefinition toPage, String toStateName, String cancelReason) throws OseeCoreException {
      // Set XCurrentState info to XState
      stateDam.setState(currentStateDam.getState());

      // Set XCurrentState; If been to this state, copy state info from prev state; else create new
      SMAState previousState = stateDam.getState(toStateName, false);
      if (previousState == null) {
         currentStateDam.setState(new SMAState(toStateName, toAssignees));
      } else {
         if (toAssignees.size() > 0) {
            previousState.setAssignees(toAssignees);
         }
         currentStateDam.setState(previousState);
      }
   }

   /**
    * Initializes state machine and sets the current state to stateName
    */
   public void initializeStateMachine(String stateName) throws OseeCoreException {
      initializeStateMachine(stateName, null);
   }

   /**
    * Initializes state machine and sets the current state to stateName
    */
   public void initializeStateMachine(String stateName, Collection<User> assignees) throws OseeCoreException {
      SMAState smaState = null;
      if (getVisitedStateNames().contains(stateName)) {
         smaState = getSMAState(stateName, false);
      } else {
         if (assignees == null) {
            smaState = new SMAState(stateName, UserManager.getUser());
         } else {
            smaState = new SMAState(stateName, assignees);
         }
      }
      currentStateDam.setState(smaState);
   }

   private void putState(SMAState state) throws OseeCoreException {
      if (getCurrentStateName().equals(state.getName())) {
         currentStateDam.setState(state);
      } else {
         stateDam.setState(state);
      }
   }

   public Collection<String> getVisitedStateNames() throws OseeCoreException {
      Set<String> names = new HashSet<String>();
      for (SMAState state : stateDam.getStates()) {
         names.add(state.getName());
      }
      names.add(getCurrentStateName());
      return names;
   }

   public long getTimeInState() throws OseeCoreException {
      return getTimeInState(getCurrentStateName());
   }

   public long getTimeInState(String stateName) throws OseeCoreException {
      SMAState state = getSMAState(stateName, false);
      if (state == null) {
         return 0;
      }
      LogItem logItem = sma.getLog().getLastEvent(LogType.StateEntered);
      if (logItem == null) {
         return 0;
      }
      return new Date().getTime() - logItem.getDate().getTime();
   }

   /**
    * return currently assigned state machine artifacts
    */
   public static Set<Artifact> getAssigned(User user) throws OseeCoreException {
      return getAssigned(user, null);
   }

   /**
    * return currently assigned state machine artifacts that match clazz
    * 
    * @param clazz to match or all if null
    */
   public static Set<Artifact> getAssigned(User user, Class<?> clazz) throws OseeCoreException {
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
         "%<" + userId + ">%", AtsUtil.getAtsBranch())) {
         if (clazz == null || clazz.isInstance(artifact)) {
            assigned.add(artifact);
         }
      }
      return assigned;

   }

   public static Collection<User> getImplementersByState(AbstractWorkflowArtifact workflow, String stateName) throws OseeCoreException {
      if (workflow.isCancelled()) {
         return Arrays.asList(workflow.getLog().getCancelledLogItem().getUser());
      }
      Collection<User> users = new HashSet<User>(workflow.getStateMgr().getAssignees(stateName));
      LogItem item = workflow.getLog().getCompletedLogItem();
      if (item != null) {
         users.add(item.getUser());
      }
      return users;
   }

}
