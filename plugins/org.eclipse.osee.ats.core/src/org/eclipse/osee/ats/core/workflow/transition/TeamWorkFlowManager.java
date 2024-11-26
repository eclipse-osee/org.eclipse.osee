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

package org.eclipse.osee.ats.core.workflow.transition;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.team.ITeamWorkflowProvider;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.internal.AtsApiService;
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
   public Result transitionTo(TeamState toState, AtsUser user, boolean popup, IAtsChangeSet changes) {
      return transitionTo(toState, user, Arrays.asList(user), popup, changes);
   }

   /**
    * Quickly transition to a state with minimal metrics and data entered. Should only be used for automated transition
    * for things such as developmental testing and demos.
    *
    * @param transitionToAssignees Users to be assigned after transition
    * @param currentStateUser User that did work on current state
    */
   public Result transitionTo(TeamState toState, AtsUser currentStateUser, Collection<AtsUser> transitionToAssignees,
      boolean popup, IAtsChangeSet changes) {
      Conditions.checkNotNull(currentStateUser, "currentStateUser");
      Conditions.checkNotNullOrEmpty(transitionToAssignees, "transitionToAssignees");
      Date date = new Date();
      StateDefinition toStateDef = teamWf.getWorkDefinition().getStateByName(toState.getName());
      StateDefinition endorseStateDef = teamWf.getWorkDefinition().getStateByName(TeamState.Endorse.getName());
      StateDefinition analyzeStateDef = teamWf.getWorkDefinition().getStateByName(TeamState.Analyze.getName());
      StateDefinition authStateDef = teamWf.getWorkDefinition().getStateByName(TeamState.Authorize.getName());
      StateDefinition implStateDef = teamWf.getWorkDefinition().getStateByName(TeamState.Implement.getName());
      if (toState == TeamState.Endorse) {
         if (!teamWf.getCurrentStateName().equals(TeamState.Endorse.getName())) {
            return new Result("Workflow current state [%s] past desired Endorse state", teamWf.getCurrentStateName());
         }
         return Result.TrueResult;
      }

      // Set Endorse state data and transition if applicable
      if (endorseStateDef != null && endorseStateDef.getOrdinal() <= toStateDef.getOrdinal()) {
         Result result = setEndorseData(popup, null, 100, .2, currentStateUser, date, changes);
         if (result.isFalse()) {
            return result;
         }
      }
      if (toState == TeamState.Analyze || (analyzeStateDef != null && analyzeStateDef.getOrdinal() <= toStateDef.getOrdinal())) {
         Result result = transitionToState(popup, teamWf, TeamState.Analyze, transitionToAssignees, changes, atsApi);
         if (result.isFalse()) {
            return result;
         }
      }

      // Set Analyze state data
      if (analyzeStateDef != null && analyzeStateDef.getOrdinal() <= toStateDef.getOrdinal()) {
         Result result = setAnalyzeData(popup, null, null, 100, .2, currentStateUser, date, changes);
         if (result.isFalse()) {
            return result;
         }
      }

      // Transition to Authorize if applicable
      if (toState == TeamState.Authorize || (authStateDef != null && authStateDef.getOrdinal() <= toStateDef.getOrdinal())) {
         Result result = transitionToState(popup, teamWf, TeamState.Authorize, transitionToAssignees, changes, atsApi);
         if (result.isFalse()) {
            return result;
         }
      }

      // Set Authorize state data
      if (authStateDef != null && authStateDef.getOrdinal() <= toStateDef.getOrdinal()) {
         Result result = setAuthorizeData(popup, 100, .2, currentStateUser, date, changes);
         if (result.isFalse()) {
            return result;
         }
      }

      // Transition to Implement if applicable
      if (toState == TeamState.Implement || (implStateDef != null && implStateDef.getOrdinal() <= toStateDef.getOrdinal())) {
         Result result = transitionToState(popup, teamWf, TeamState.Implement, transitionToAssignees, changes, atsApi);
         if (result.isFalse()) {
            return result;
         }
      }

      // Transition to Completed if applicable
      if (toState == TeamState.Completed) {
         Result result = transitionToState(popup, teamWf, TeamState.Completed, transitionToAssignees, changes, atsApi);
         if (result.isFalse()) {
            return result;
         }
      }

      return Result.TrueResult;
   }

   public Result transitionToState(boolean popup, IAtsTeamWorkflow teamWf, IStateToken toState,
      Collection<AtsUser> transitionToAssignees, IAtsChangeSet changes, AtsApi atsApi) {
      TransitionData transData = new TransitionData("Transition to " + toState.getName(), Arrays.asList(teamWf),
         toState.getName(), transitionToAssignees, null, changes, transitionOptions);
      TransitionManager transitionMgr = new TransitionManager(transData);
      TransitionResults results = transitionMgr.handleAll();
      if (results.isEmpty()) {
         return Result.TrueResult;
      }
      return new Result("Transition Error %s", results.toString());
   }

   public Result setEndorseData(boolean popup, String propRes, int statePercentComplete, double stateHoursSpent,
      AtsUser user, Date date, IAtsChangeSet changes) {
      if (!teamWf.isInState(TeamState.Endorse)) {
         Result result = new Result("Action not in Endorse state");
         if (result.isFalse() && popup) {
            return result;
         }
      }
      AtsApiService.get().getWorkItemMetricsService().setMetrics(teamWf, stateHoursSpent, statePercentComplete, true,
         user, date, changes);
      return Result.TrueResult;
   }

   public Result setAnalyzeData(boolean popup, String problem, String propRes, int statePercentComplete,
      double stateHoursSpent, AtsUser user, Date date, IAtsChangeSet changes) {
      if (!teamWf.isInState(TeamState.Analyze)) {
         Result result = new Result("Action not in Analyze state");
         if (result.isFalse() && popup) {
            return result;
         }
      }
      AtsApiService.get().getWorkItemMetricsService().setMetrics(teamWf, stateHoursSpent, statePercentComplete, true,
         user, date, changes);
      return Result.TrueResult;
   }

   public Result setAuthorizeData(boolean popup, int statePercentComplete, double stateHoursSpent, AtsUser user,
      Date date, IAtsChangeSet changes) {
      if (!teamWf.isInState(TeamState.Authorize)) {
         Result result = new Result("Action not in Authorize state");
         if (result.isFalse() && popup) {
            return result;
         }
      }
      AtsApiService.get().getWorkItemMetricsService().setMetrics(teamWf, stateHoursSpent, statePercentComplete, true,
         user, date, changes);
      return Result.TrueResult;
   }

   public Result setImplementData(boolean popup, String resolution, int statePercentComplete, double stateHoursSpent,
      AtsUser user, Date date, IAtsChangeSet changes) {
      if (!teamWf.isInState(TeamState.Implement)) {
         Result result = new Result("Action not in Implement state");
         if (result.isFalse() && popup) {
            return result;
         }
      }
      AtsApiService.get().getWorkItemMetricsService().setMetrics(teamWf, stateHoursSpent, statePercentComplete, true,
         user, date, changes);
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
