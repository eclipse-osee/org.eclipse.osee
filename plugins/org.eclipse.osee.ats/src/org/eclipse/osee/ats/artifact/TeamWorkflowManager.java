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

import org.eclipse.osee.ats.util.TeamState;
import org.eclipse.osee.ats.util.TransitionOption;
import org.eclipse.osee.ats.workflow.TransitionManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IWorkPage;

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
   public Result transitionTo(TeamState toState, User user, boolean popup, SkynetTransaction transaction) throws OseeCoreException {
      if (teamArt.isInState(TeamState.Endorse)) {
         Result result = processEndorseState(popup, teamArt, user, transaction);
         if (result.isFalse()) {
            return result;
         }
      }
      if (toState == TeamState.Analyze) {
         return Result.TrueResult;
      }

      Result result = processAnalyzeState(popup, teamArt, user, transaction);
      if (result.isFalse()) {
         return result;
      }

      if (toState == TeamState.Authorize) {
         return Result.TrueResult;
      }

      result = processAuthorizeState(popup, teamArt, user, transaction);
      if (result.isFalse()) {
         return result;
      }

      if (toState == TeamState.Implement) {
         return Result.TrueResult;
      }

      result = transitionToState(popup, teamArt, TeamState.Completed, user, transaction);
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
      result = transitionToState(popup, teamArt, TeamState.Implement, user, transaction);
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
      result = transitionToState(popup, teamArt, TeamState.Authorize, user, transaction);
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
      result = transitionToState(popup, teamArt, TeamState.Analyze, user, transaction);
      if (result.isFalse()) {
         return result;
      }
      return Result.TrueResult;
   }

   private Result transitionToState(boolean popup, TeamWorkFlowArtifact teamArt, IWorkPage toState, User user, SkynetTransaction transaction) throws OseeCoreException {
      TransitionManager transitionMgr = new TransitionManager(teamArt);
      Result result =
         transitionMgr.transition(toState,
            (user == null ? teamArt.getStateMgr().getAssignees().iterator().next() : user), transaction,
            TransitionOption.None);
      if (result.isFalse() && popup) {
         result.popup();
      }
      return result;
   }

   public Result setEndorseData(boolean popup, String propRes, int statePercentComplete, double stateHoursSpent) throws OseeCoreException {
      if (!teamArt.isInState(TeamState.Endorse)) {
         Result result = new Result("Action not in Endorse state");
         if (result.isFalse() && popup) {
            result.popup();
            return result;
         }
      }
      teamArt.getStateMgr().setMetrics(TeamState.Endorse, stateHoursSpent, statePercentComplete, true);
      return Result.TrueResult;
   }

   public Result setAnalyzeData(boolean popup, String problem, String propRes, double hourEstimate, int statePercentComplete, double stateHoursSpent) throws OseeCoreException {
      if (!teamArt.isInState(TeamState.Analyze)) {
         Result result = new Result("Action not in Analyze state");
         if (result.isFalse() && popup) {
            result.popup();
            return result;
         }
      }
      teamArt.setSoleAttributeValue(AtsAttributeTypes.EstimatedHours, hourEstimate);
      teamArt.getStateMgr().setMetrics(TeamState.Analyze, stateHoursSpent, statePercentComplete, true);
      return Result.TrueResult;
   }

   public Result setAuthorizeData(boolean popup, int statePercentComplete, double stateHoursSpent) throws OseeCoreException {
      if (!teamArt.isInState(TeamState.Authorize)) {
         Result result = new Result("Action not in Authorize state");
         if (result.isFalse() && popup) {
            result.popup();
            return result;
         }
      }
      teamArt.getStateMgr().setMetrics(TeamState.Authorize, stateHoursSpent, statePercentComplete, true);
      return Result.TrueResult;
   }

   public Result setImplementData(boolean popup, String resolution, int statePercentComplete, double stateHoursSpent) throws OseeCoreException {
      if (!teamArt.isInState(TeamState.Implement)) {
         Result result = new Result("Action not in Implement state");
         if (result.isFalse() && popup) {
            result.popup();
            return result;
         }
      }
      teamArt.getStateMgr().setMetrics(TeamState.Implement, stateHoursSpent, statePercentComplete, true);
      return Result.TrueResult;
   }

}
