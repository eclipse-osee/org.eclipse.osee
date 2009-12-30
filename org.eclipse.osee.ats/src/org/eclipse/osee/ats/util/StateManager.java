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

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.artifact.LogItem;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.ATSLog.LogType;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.util.widgets.SMAState;
import org.eclipse.osee.ats.util.widgets.XCurrentStateDam;
import org.eclipse.osee.ats.util.widgets.XStateDam;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;

/**
 * @author Donald G. Dunne
 */
public class StateManager {

   private final XCurrentStateDam currentStateDam;
   private final XStateDam stateDam;
   private final StateMachineArtifact sma;

   public StateManager(StateMachineArtifact sma) throws OseeStateException {
      super();
      this.sma = sma;
      currentStateDam = new XCurrentStateDam(sma);
      stateDam = new XStateDam(sma);
   }

   /**
    * Get state and create if not there.
    * 
    * @param name
    * @param create
    * @return state matching name
    */
   private SMAState getSMAState(String name, boolean create) throws OseeCoreException {
      if (currentStateDam.getState().getName().equals(name))
         return currentStateDam.getState();
      else
         return (stateDam.getState(name, create));
   }

   /**
    * Discouraged Access. This method should not normally be called except in cases were state data is being manually
    * created.
    * 
    * @param name
    * @throws OseeCoreException
    */
   public void internalCreateIfNotExists(String name) throws OseeCoreException {
      if (isStateVisited(name)) {
         return;
      }
      SMAState smaState = getSMAState(name, true);
      putState(smaState);
   }

   /**
    * This method will create an assignee relation for each current assignee. Assignees are related to user artifacts to
    * speed up ATS searching. This does not persist the artifact.<br>
    * <br>
    * The "UnAssigned" user is no longer related due to the performance and event service issues with having a single
    * user related to > 5000 items. Since these relations are only used for searching, no need to have them for
    * "UnAssigned".
    */
   public static void updateAssigneeRelations(StateMachineArtifact sma) throws OseeCoreException {
      Collection<User> assignees = sma.getStateMgr().getAssignees();
      assignees.remove(UserManager.getUser(SystemUser.UnAssigned));
      sma.setRelations(CoreRelationTypes.Users_User, assignees);
   }

   /**
    * @return true if UnAssigned user is currently an assignee
    * @throws OseeCoreException
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
    * @param stateName
    * @return hours spent or 0 if none
    */
   public double getHoursSpent(String stateName) throws OseeCoreException {
      SMAState state = getSMAState(stateName, false);
      if (state == null) return 0.0;
      return state.getHoursSpent();
   }

   public double getHoursSpent() throws OseeCoreException {
      return getHoursSpent(getCurrentStateName());
   }

   /**
    * Return Percent Complete for State
    * 
    * @param stateName
    * @return percent complete or 0 if none
    */
   public int getPercentComplete(String stateName) throws OseeCoreException {
      if (stateName.equals(DefaultTeamState.Completed.name()) || stateName.equals(DefaultTeamState.Cancelled.name())) return 100;
      SMAState state = getSMAState(stateName, false);
      if (state == null) return 0;
      return state.getPercentComplete();

   }

   public int getPercentComplete() throws OseeCoreException {
      return getPercentComplete(getCurrentStateName());
   }

   public String getCurrentStateName() throws OseeCoreException {
      return currentStateDam.getState().getName();
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
      if (state != null)
         return state.getAssignees();
      else
         return Collections.emptyList();
   }

   public void updateMetrics(double additionalHours, int percentComplete, boolean logMetrics) throws OseeCoreException {
      updateMetrics(getCurrentStateName(), additionalHours, percentComplete, logMetrics);
   }

   public void updateMetrics(String stateName, double additionalHours, int percentComplete, boolean logMetrics) throws OseeCoreException {
      if (stateName.equals(getCurrentStateName()))
         currentStateDam.updateMetrics(additionalHours, percentComplete, logMetrics);
      else
         stateDam.updateMetrics(stateName, additionalHours, percentComplete, logMetrics);
   }

   public void setMetrics(double hours, int percentComplete, boolean logMetrics) throws OseeCoreException {
      currentStateDam.setMetrics(hours, percentComplete, logMetrics);
   }

   /**
    * Sets the assignees as attributes and relations AND writes to SMA. Does not persist.
    * 
    * @param assignees
    * @throws Exception
    */
   public void setAssignees(Collection<User> assignees) throws OseeCoreException {
      SMAState state = getSMAState(getCurrentStateName(), false);
      state.setAssignees(assignees);
      putState(state);
   }

   /**
    * Sets the assignee AND writes to SMA. Does not persist.
    * 
    * @param assignee
    * @throws Exception
    */
   public void setAssignee(String stateName, User assignee) throws OseeCoreException {
      if (!isStateVisited(stateName)) throw new IllegalArgumentException("State " + stateName + " does not exist.");
      SMAState state = getSMAState(stateName, false);
      state.setAssignee(assignee);
      putState(state);
   }

   /**
    * Sets the assignee AND writes to SMA. Does not persist.
    * 
    * @param assignee
    * @throws Exception
    */
   public void setAssignee(User assignee) throws OseeCoreException {
      SMAState state = getSMAState(getCurrentStateName(), false);
      state.setAssignee(assignee);
      putState(state);
   }

   /**
    * Removes the assignee from stateName state AND writes to SMA. Does not persist.
    * 
    * @param stateName
    * @param assignee
    * @throws Exception
    */
   public void removeAssignee(String stateName, User assignee) throws OseeCoreException {
      if (!isStateVisited(stateName)) return;
      SMAState state = getSMAState(stateName, false);
      state.removeAssignee(assignee);
      putState(state);
   }

   /**
    * Removes the assignee AND writes to SMA. Does not persist.
    * 
    * @param assignee
    * @throws Exception
    */
   public void removeAssignee(User assignee) throws OseeCoreException {
      SMAState state = getSMAState(getCurrentStateName(), false);
      state.removeAssignee(assignee);
      putState(state);
   }

   /**
    * Adds the assignee AND writes to SMA. Does not persist.
    * 
    * @param assignee
    * @throws Exception
    */
   public void addAssignee(User assignee) throws OseeCoreException {
      SMAState state = getSMAState(getCurrentStateName(), false);
      state.addAssignee(assignee);
      putState(state);
   }

   /**
    * Removes ALL assignees AND writes to SMA. Does not persist.
    * 
    * @param assignee
    * @throws Exception
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
      if (previousState != null) {
         if (toAssignees.size() > 0) previousState.setAssignees(toAssignees);
         currentStateDam.setState(previousState);
      } else {
         currentStateDam.setState(new SMAState(toStateName, toAssignees));
      }
   }

   /**
    * Initializes state machine and sets the current state to stateName
    * 
    * @param stateName
    * @throws Exception
    */
   public void initializeStateMachine(String stateName) throws OseeCoreException {
      initializeStateMachine(stateName, null);
   }

   /**
    * Initializes state machine and sets the current state to stateName
    * 
    * @param stateName
    * @param assignees
    * @throws Exception
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
      if (getCurrentStateName().equals(state.getName()))
         currentStateDam.setState(state);
      else
         stateDam.setState(state);
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
      if (state == null) return 0;
      LogItem logItem = sma.getLog().getLastEvent(LogType.StateEntered);
      if (logItem == null) return 0;
      return (new Date()).getTime() - logItem.getDate().getTime();
   }
}
