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

package org.eclipse.osee.ats.core.workflow.transition;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.team.ITeamWorkflowProvider;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.transition.IAtsTransitionManager;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workflow.TeamWorkflowProviders;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Methods in support of programatically transitioning the DefaultWorkFlow through it's states. Only to be used for the
 * DefaultTeamWorkflow of Endorse->Analyze->Auth->Implement->Complete
 *
 * @author Donald G. Dunne
 */
public class TeamWorkFlowManager {

   private final IAtsTeamWorkflow teamWf;
   private final TransitionOption[] transitionOptions;
   private final AtsApi atsApi;

   public TeamWorkFlowManager(IAtsTeamWorkflow teamWf, AtsApi atsApi, TransitionOption... transitionOptions) {
      this.teamWf = teamWf;
      this.atsApi = atsApi;
      this.transitionOptions = transitionOptions;
   }

   /**
    * Quickly transition to a state with minimal metrics and data entered. Should only be used for automated transition
    * for things such as developmental testing and demos.
    *
    * @param user Users to be assigned after transition and that did the current state work
    */
   public Result transitionTo(TeamState toState, IAtsUser user, boolean popup, IAtsChangeSet changes) {
      return transitionTo(toState, user, Arrays.asList(user), popup, changes);
   }

   /**
    * Quickly transition to a state with minimal metrics and data entered. Should only be used for automated transition
    * for things such as developmental testing and demos.
    *
    * @param transitionToAssignees Users to be assigned after transition
    * @param currentStateUser User that did work on current state
    */
   public Result transitionTo(TeamState toState, IAtsUser currentStateUser, Collection<IAtsUser> transitionToAssignees, boolean popup, IAtsChangeSet changes) {
      Conditions.checkNotNull(currentStateUser, "currentStateUser");
      Conditions.checkNotNullOrEmpty(transitionToAssignees, "transitionToAssignees");
      Date date = new Date();
      if (toState == TeamState.Endorse) {
         if (!teamWf.getStateMgr().getCurrentStateName().equals(TeamState.Endorse.getName())) {
            return new Result("Workflow current state [%s] past desired Endorse state",
               teamWf.getStateMgr().getCurrentStateName());
         }
         return Result.TrueResult;
      }
      if (teamWf.getStateMgr().isInState(TeamState.Endorse)) {
         Result result = processEndorseState(popup, teamWf, currentStateUser, transitionToAssignees, date, changes);
         if (result.isFalse()) {
            return result;
         }
      }
      if (toState == TeamState.Analyze) {
         return Result.TrueResult;
      }

      if (teamWf.getStateMgr().isInState(TeamState.Analyze)) {
         Result result = processAnalyzeState(popup, teamWf, currentStateUser, transitionToAssignees, date, changes);
         if (result.isFalse()) {
            return result;
         }
      }

      if (toState == TeamState.Authorize) {
         return Result.TrueResult;
      }

      if (teamWf.getStateMgr().isInState(TeamState.Authorize)) {
         Result result = processAuthorizeState(popup, teamWf, currentStateUser, transitionToAssignees, date, changes);
         if (result.isFalse()) {
            return result;
         }
      }

      if (toState == TeamState.Implement) {
         return Result.TrueResult;
      }

      if (teamWf.getStateMgr().isInState(TeamState.Implement)) {
         Result result = transitionToState(popup, teamWf, TeamState.Completed, transitionToAssignees, changes, atsApi);
         if (result.isFalse()) {
            return result;
         }
      }
      return Result.TrueResult;

   }

   private Result processAuthorizeState(boolean popup, IAtsTeamWorkflow teamWf, IAtsUser currentStateUser, Collection<IAtsUser> transitionToAssignees, Date date, IAtsChangeSet changes) {
      Result result = setAuthorizeData(popup, 100, .2, currentStateUser, date);
      if (result.isFalse()) {
         return result;
      }
      result = transitionToState(popup, teamWf, TeamState.Implement, transitionToAssignees, changes, atsApi);
      if (result.isFalse()) {
         return result;
      }
      return Result.TrueResult;
   }

   private Result processAnalyzeState(boolean popup, IAtsTeamWorkflow teamWf, IAtsUser currentStateUser, Collection<IAtsUser> transitionToAssignees, Date date, IAtsChangeSet changes) {
      Result result = setAnalyzeData(popup, null, null, 100, .2, currentStateUser, date);
      if (result.isFalse()) {
         return result;
      }
      result = transitionToState(popup, teamWf, TeamState.Authorize, transitionToAssignees, changes, atsApi);
      if (result.isFalse()) {
         return result;
      }
      return Result.TrueResult;
   }

   private Result processEndorseState(boolean popup, IAtsTeamWorkflow teamWf, IAtsUser currentStateUser, Collection<IAtsUser> transitionToAssignees, Date date, IAtsChangeSet changes) {
      Result result = setEndorseData(popup, null, 100, .2, currentStateUser, date);
      if (result.isFalse()) {
         return result;
      }
      result = transitionToState(popup, teamWf, TeamState.Analyze, transitionToAssignees, changes, atsApi);
      if (result.isFalse()) {
         return result;
      }
      return Result.TrueResult;
   }

   public Result transitionToState(boolean popup, IAtsTeamWorkflow teamWf, IStateToken toState, Collection<IAtsUser> transitionToAssignees, IAtsChangeSet changes, AtsApi atsApi) {
      TransitionHelper helper = new TransitionHelper("Transition to " + toState.getName(), Arrays.asList(teamWf),
         toState.getName(), transitionToAssignees, null, changes, atsApi, transitionOptions);
      IAtsTransitionManager transitionMgr = TransitionFactory.getTransitionManager(helper);
      TransitionResults results = transitionMgr.handleAll();
      if (results.isEmpty()) {
         return Result.TrueResult;
      }
      return new Result("Transition Error %s", results.toString());
   }

   public Result setEndorseData(boolean popup, String propRes, int statePercentComplete, double stateHoursSpent, IAtsUser user, Date date) {
      if (!teamWf.getStateMgr().isInState(TeamState.Endorse)) {
         Result result = new Result("Action not in Endorse state");
         if (result.isFalse() && popup) {
            return result;
         }
      }
      teamWf.getStateMgr().setMetrics(TeamState.Endorse, stateHoursSpent, statePercentComplete, true, user, date);
      return Result.TrueResult;
   }

   public Result setAnalyzeData(boolean popup, String problem, String propRes, int statePercentComplete, double stateHoursSpent, IAtsUser user, Date date) {
      if (!teamWf.getStateMgr().isInState(TeamState.Analyze)) {
         Result result = new Result("Action not in Analyze state");
         if (result.isFalse() && popup) {
            return result;
         }
      }
      teamWf.getStateMgr().setMetrics(TeamState.Analyze, stateHoursSpent, statePercentComplete, true, user, date);
      return Result.TrueResult;
   }

   public Result setAuthorizeData(boolean popup, int statePercentComplete, double stateHoursSpent, IAtsUser user, Date date) {
      if (!teamWf.getStateMgr().isInState(TeamState.Authorize)) {
         Result result = new Result("Action not in Authorize state");
         if (result.isFalse() && popup) {
            return result;
         }
      }
      teamWf.getStateMgr().setMetrics(TeamState.Authorize, stateHoursSpent, statePercentComplete, true, user, date);
      return Result.TrueResult;
   }

   public Result setImplementData(boolean popup, String resolution, int statePercentComplete, double stateHoursSpent, IAtsUser user, Date date) {
      if (!teamWf.getStateMgr().isInState(TeamState.Implement)) {
         Result result = new Result("Action not in Implement state");
         if (result.isFalse() && popup) {
            return result;
         }
      }
      teamWf.getStateMgr().setMetrics(TeamState.Implement, stateHoursSpent, statePercentComplete, true, user, date);
      return Result.TrueResult;
   }

   public static String getArtifactTypeShortName(IAtsWorkItem teamWf) {
      for (ITeamWorkflowProvider atsTeamWorkflow : TeamWorkflowProviders.getTeamWorkflowProviders()) {
         String typeName = atsTeamWorkflow.getArtifactTypeShortName((IAtsTeamWorkflow) teamWf.getStoreObject());
         if (Strings.isValid(typeName)) {
            return typeName;
         }
      }
      return null;
   }

}
