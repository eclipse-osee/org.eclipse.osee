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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.widgets.SMAState;
import org.eclipse.osee.ats.util.widgets.XCurrentStateDam;
import org.eclipse.osee.ats.util.widgets.XStateDam;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;

/**
 * @author Donald G. Dunne
 */
public class StateManager {

   private XCurrentStateDam currentStateDam;
   private XStateDam stateDam;
   private static Collection<User> EMPTY_USER_ARRAY = new ArrayList<User>(0);

   public StateManager(SMAManager smaMgr) {
      super();
      currentStateDam = new XCurrentStateDam(smaMgr.getSma());
      stateDam = new XStateDam(smaMgr.getSma());
   }

   /**
    * Return current or past state from name
    * 
    * @param name
    * @param create TODO
    * @return state matching name
    * @throws SQLException
    */
   private SMAState getSMAState(String name, boolean create) {
      if (currentStateDam.getState().getName().equals(name))
         return currentStateDam.getState();
      else
         return (stateDam.getState(name, create));
   }

   /**
    * Return Hours Spent for State
    * 
    * @param stateName
    * @return hours spent or 0 if none
    */
   public double getHoursSpent(String stateName) throws OseeCoreException, SQLException {
      SMAState state = getSMAState(stateName, false);
      if (state == null) return 0.0;
      return state.getHoursSpent();
   }

   public double getHoursSpent() throws OseeCoreException, SQLException {
      return getHoursSpent(getCurrentStateName());
   }

   /**
    * Return Percent Complete for State
    * 
    * @param stateName
    * @return percent complete or 0 if none
    */
   public int getPercentComplete(String stateName) throws OseeCoreException, SQLException {
      if (stateName.equals(DefaultTeamState.Completed) || stateName.equals(DefaultTeamState.Cancelled)) return 100;
      SMAState state = getSMAState(stateName, false);
      if (state == null) return 0;
      return state.getPercentComplete();

   }

   public int getPercentComplete() throws OseeCoreException, SQLException {
      return getPercentComplete(getCurrentStateName());
   }

   public String getCurrentStateName() {
      return currentStateDam.getState().getName();
   }

   public Collection<User> getAssignees() {
      return getAssignees(getCurrentStateName());
   }

   public Collection<User> getAssignees(String stateName) {
      SMAState state = getSMAState(stateName, false);
      if (state != null)
         return state.getAssignees();
      else
         return EMPTY_USER_ARRAY;
   }

   public void updateMetrics(double additionalHours, int percentComplete, boolean logMetrics) throws OseeCoreException, SQLException {
      updateMetrics(getCurrentStateName(), additionalHours, percentComplete, logMetrics);
   }

   public void updateMetrics(String stateName, double additionalHours, int percentComplete, boolean logMetrics) throws OseeCoreException, SQLException {
      if (stateName.equals(getCurrentStateName()))
         currentStateDam.updateMetrics(additionalHours, percentComplete, logMetrics);
      else
         stateDam.updateMetrics(stateName, additionalHours, percentComplete, logMetrics);
   }

   public void setMetrics(double hours, int percentComplete, boolean logMetrics) throws OseeCoreException, SQLException {
      currentStateDam.setMetrics(hours, percentComplete, logMetrics);
   }

   /**
    * Sets the assignees AND writes to SMA. Does not persist.
    * 
    * @param assignees
    * @throws Exception
    */
   public void setAssignees(Collection<User> assignees) throws OseeCoreException, SQLException {
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
   public void setAssignee(String stateName, User assignee) throws OseeCoreException, SQLException {
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
   public void setAssignee(User assignee) throws OseeCoreException, SQLException {
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
   public void removeAssignee(String stateName, User assignee) throws OseeCoreException, SQLException {
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
   public void removeAssignee(User assignee) throws OseeCoreException, SQLException {
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
   public void addAssignee(User assignee) throws OseeCoreException, SQLException {
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
   public void clearAssignees() throws OseeCoreException, SQLException {
      SMAState state = getSMAState(getCurrentStateName(), false);
      state.clearAssignees();
      putState(state);
   }

   public boolean isStateVisited(String name) {
      return getVisitedStateNames().contains(name);
   }

   public void transitionHelper(Collection<User> toAssignees, boolean persist, WorkPageDefinition fromPage, WorkPageDefinition toPage, String toStateName, String cancelReason) throws OseeCoreException, SQLException {
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
   public void initializeStateMachine(String stateName) throws OseeCoreException, SQLException {
      initializeStateMachine(stateName, null);
   }

   /**
    * Initializes state machine and sets the current state to stateName
    * 
    * @param stateName
    * @param assignees
    * @throws Exception
    */
   public void initializeStateMachine(String stateName, Collection<User> assignees) throws OseeCoreException, SQLException {
      SMAState smaState = null;
      if (getVisitedStateNames().contains(stateName)) {
         smaState = getSMAState(stateName, false);
      } else {
         if (assignees == null) {
            smaState = new SMAState(stateName, SkynetAuthentication.getUser());
         } else {
            smaState = new SMAState(stateName, assignees);
         }
      }
      currentStateDam.setState(smaState);
   }

   private void putState(SMAState state) throws OseeCoreException, SQLException {
      if (getCurrentStateName().equals(state.getName()))
         currentStateDam.setState(state);
      else
         stateDam.setState(state);
   }

   public Collection<String> getVisitedStateNames() {
      Set<String> names = new HashSet<String>();
      for (SMAState state : stateDam.getStates()) {
         names.add(state.getName());
      }
      names.add(getCurrentStateName());
      return names;
   }

}
