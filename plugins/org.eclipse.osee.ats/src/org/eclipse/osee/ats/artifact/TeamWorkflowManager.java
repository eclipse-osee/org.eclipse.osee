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

package org.eclipse.osee.ats.artifact;

import org.eclipse.osee.ats.util.DefaultTeamState;
import org.eclipse.osee.ats.util.TransitionOption;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * Methods in support of programatically transitioning the DefaultWorkFlow through it's states. Only to be used for the
 * DefaultTeamWorkflow of Endorse->Analyze->Auth->Implement->Complete
 * 
 * @author Donald G. Dunne
 */
public class TeamWorkflowManager {

   private final TeamWorkFlowArtifact teamArt;

   public TeamWorkflowManager(TeamWorkFlowArtifact teamArt) {
      this.teamArt = teamArt;
   }

   /**
    * Quickly transition to a state with minimal metrics and data entered. Should only be used for automated transition
    * for things such as developmental testing and demos.
    * 
    * @param user User to transition to OR null if should use user of current state
    */
   public Result transitionTo(DefaultTeamState toState, User user, boolean popup, SkynetTransaction transaction) throws OseeCoreException {
      if (teamArt.getStateMgr().getCurrentStateName().equals(DefaultTeamState.Endorse.name())) {
         Result result = processEndorseState(popup, teamArt, user, transaction);
         if (result.isFalse()) {
            return result;
         }
      }
      if (toState == DefaultTeamState.Analyze) {
         return Result.TrueResult;
      }

      Result result = processAnalyzeState(popup, teamArt, user, transaction);
      if (result.isFalse()) {
         return result;
      }

      if (toState == DefaultTeamState.Authorize) {
         return Result.TrueResult;
      }

      result = processAuthorizeState(popup, teamArt, user, transaction);
      if (result.isFalse()) {
         return result;
      }

      if (toState == DefaultTeamState.Implement) {
         return Result.TrueResult;
      }

      result = transitionToState(popup, teamArt, DefaultTeamState.Completed.name(), user, transaction);
      if (result.isFalse()) {
         return result;
      }
      return Result.TrueResult;

   }

   private Result processAuthorizeState(boolean popup, TeamWorkFlowArtifact teamArt, User user, SkynetTransaction transaction) throws OseeCoreException {
      Result result = setAuthorizeData(popup, 100, .2);
      if (result.isFalse()) {
         return result;
      }
      result = transitionToState(popup, teamArt, DefaultTeamState.Implement.name(), user, transaction);
      if (result.isFalse()) {
         return result;
      }
      return Result.TrueResult;
   }

   private Result processAnalyzeState(boolean popup, TeamWorkFlowArtifact teamArt, User user, SkynetTransaction transaction) throws OseeCoreException {
      Result result = setAnalyzeData(popup, null, null, 1, 100, .2);
      if (result.isFalse()) {
         return result;
      }
      result = transitionToState(popup, teamArt, DefaultTeamState.Authorize.name(), user, transaction);
      if (result.isFalse()) {
         return result;
      }
      return Result.TrueResult;
   }

   private Result processEndorseState(boolean popup, TeamWorkFlowArtifact teamArt, User user, SkynetTransaction transaction) throws OseeCoreException {
      Result result = setEndorseData(popup, null, 100, .2);
      if (result.isFalse()) {
         return result;
      }
      result = transitionToState(popup, teamArt, DefaultTeamState.Analyze.name(), user, transaction);
      if (result.isFalse()) {
         return result;
      }
      return Result.TrueResult;
   }

   private Result transitionToState(boolean popup, TeamWorkFlowArtifact teamArt, String toState, User user, SkynetTransaction transaction) throws OseeCoreException {
      Result result =
         teamArt.transition(toState, (user == null ? teamArt.getStateMgr().getAssignees().iterator().next() : user),
            transaction, TransitionOption.None);
      if (result.isFalse() && popup) {
         result.popup();
      }
      return result;
   }

   public Result setEndorseData(boolean popup, String propRes, int statePercentComplete, double stateHoursSpent) throws OseeCoreException {
      if (!teamArt.getStateMgr().getCurrentStateName().equals(DefaultTeamState.Endorse.name())) {
         Result result = new Result("Action not in Endorse state");
         if (result.isFalse() && popup) {
            result.popup();
            return result;
         }
      }
      teamArt.getStateMgr().setMetrics(DefaultTeamState.Endorse.name(), stateHoursSpent, statePercentComplete, true);
      return Result.TrueResult;
   }

   public Result setAnalyzeData(boolean popup, String problem, String propRes, double hourEstimate, int statePercentComplete, double stateHoursSpent) throws OseeCoreException {
      if (!teamArt.getStateMgr().getCurrentStateName().equals(DefaultTeamState.Analyze.name())) {
         Result result = new Result("Action not in Analyze state");
         if (result.isFalse() && popup) {
            result.popup();
            return result;
         }
      }
      teamArt.setSoleAttributeValue(AtsAttributeTypes.EstimatedHours, hourEstimate);
      teamArt.getStateMgr().setMetrics(DefaultTeamState.Analyze.name(), stateHoursSpent, statePercentComplete, true);
      return Result.TrueResult;
   }

   public Result setAuthorizeData(boolean popup, int statePercentComplete, double stateHoursSpent) throws OseeCoreException {
      if (!teamArt.getStateMgr().getCurrentStateName().equals(DefaultTeamState.Authorize.name())) {
         Result result = new Result("Action not in Authorize state");
         if (result.isFalse() && popup) {
            result.popup();
            return result;
         }
      }
      teamArt.getStateMgr().setMetrics(DefaultTeamState.Authorize.name(), stateHoursSpent, statePercentComplete, true);
      return Result.TrueResult;
   }

   public Result setImplementData(boolean popup, String resolution, int statePercentComplete, double stateHoursSpent) throws OseeCoreException {
      if (!teamArt.getStateMgr().getCurrentStateName().equals(DefaultTeamState.Implement.name())) {
         Result result = new Result("Action not in Implement state");
         if (result.isFalse() && popup) {
            result.popup();
            return result;
         }
      }
      teamArt.getStateMgr().setMetrics(DefaultTeamState.Implement.name(), stateHoursSpent, statePercentComplete, true);
      return Result.TrueResult;
   }

}
