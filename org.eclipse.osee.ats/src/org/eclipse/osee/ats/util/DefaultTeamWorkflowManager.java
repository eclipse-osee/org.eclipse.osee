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
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * Methods in support of programatically transitioning the DefaultWorkFlow through it's states. Only to be used for the
 * DefaultTeamWorkflow of Endorse->Analyze->Auth->Implement->Complete
 * 
 * @author Donald G. Dunne
 */
public class DefaultTeamWorkflowManager {

   private final SMAManager smaMgr;
   private final TeamWorkFlowArtifact teamArt;

   public DefaultTeamWorkflowManager(TeamWorkFlowArtifact teamArt) {
      this.teamArt = teamArt;
      smaMgr = new SMAManager(teamArt);
   }

   /**
    * Quickly transition to a state with minimal metrics and data entered. Should only be used for automated
    * transitioning for things such as developmental testing and demos.
    * 
    * @param toState
    * @param user User to transition to OR null if should use user of current state
    * @param popup
    * @return
    * @throws Exception
    */
   public Result transitionTo(DefaultTeamState toState, User user, boolean popup) throws OseeCoreException, SQLException {
      Result result = setEndorseData(null, 100, .2);
      if (result.isFalse()) {
         if (popup) result.popup();
         return result;
      }
      result =
            smaMgr.transition(DefaultTeamState.Analyze.name(),
                  (user != null ? user : smaMgr.getStateMgr().getAssignees().iterator().next()), false);
      if (result.isFalse()) {
         if (popup) result.popup();
         return result;
      }
      if (toState == DefaultTeamState.Analyze) return Result.TrueResult;

      result = setAnalyzeData(null, null, 1, 100, .2);
      if (result.isFalse()) {
         if (popup) result.popup();
         return result;
      }
      result =
            smaMgr.transition(DefaultTeamState.Authorize.name(),
                  (user != null ? user : smaMgr.getStateMgr().getAssignees().iterator().next()), false);
      if (result.isFalse()) {
         if (popup) result.popup();
         return result;
      }
      if (toState == DefaultTeamState.Authorize) return Result.TrueResult;

      result = setAuthorizeData(100, .2);
      if (result.isFalse()) {
         if (popup) result.popup();
         return result;
      }

      result =
            smaMgr.transition(DefaultTeamState.Implement.name(),
                  (user != null ? user : smaMgr.getStateMgr().getAssignees().iterator().next()), false);
      if (result.isFalse()) {
         if (popup) result.popup();
         return result;
      }
      if (toState == DefaultTeamState.Implement) return Result.TrueResult;

      result =
            smaMgr.transition(DefaultTeamState.Completed.name(),
                  (user != null ? user : smaMgr.getStateMgr().getAssignees().iterator().next()), false);
      if (result.isFalse()) {
         if (popup) result.popup();
         return result;
      }
      return Result.TrueResult;

   }

   public Result setEndorseData(String propRes, int statePercentComplete, double stateHoursSpent) throws OseeCoreException, SQLException {
      if (!smaMgr.getStateMgr().getCurrentStateName().equals("Endorse")) return new Result(
            "Action not in Endorse state");
      if (propRes == null || propRes.equals(""))
         teamArt.setSoleAttributeValue(ATSAttributes.PROPOSED_RESOLUTION_OVERRIDE_ATTRIBUTE.getStoreName(), true);
      else
         teamArt.setSoleAttributeValue(ATSAttributes.PROPOSED_RESOLUTION_ATTRIBUTE.getStoreName(), propRes);
      smaMgr.getStateMgr().updateMetrics(stateHoursSpent, statePercentComplete, true);
      return Result.TrueResult;
   }

   public Result setAnalyzeData(String problem, String propRes, double hourEstimate, int statePercentComplete, double stateHoursSpent) throws OseeCoreException, SQLException {
      if (!smaMgr.getStateMgr().getCurrentStateName().equals("Analyze")) return new Result(
            "Action not in Analyze state");
      if (problem == null || problem.equals(""))
         teamArt.setSoleAttributeValue(ATSAttributes.PROBLEM_OVERRIDE_ATTRIBUTE.getStoreName(), true);
      else
         teamArt.setSoleAttributeValue(ATSAttributes.PROBLEM_ATTRIBUTE.getStoreName(), problem);
      if (propRes == null || propRes.equals(""))
         teamArt.setSoleAttributeValue(ATSAttributes.PROPOSED_RESOLUTION_OVERRIDE_ATTRIBUTE.getStoreName(), true);
      else
         teamArt.setSoleAttributeValue(ATSAttributes.PROPOSED_RESOLUTION_ATTRIBUTE.getStoreName(), propRes);
      teamArt.setSoleAttributeValue(ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE.getStoreName(), hourEstimate);
      smaMgr.getStateMgr().updateMetrics(stateHoursSpent, statePercentComplete, true);
      return Result.TrueResult;
   }

   public Result setAuthorizeData(int statePercentComplete, double stateHoursSpent) throws OseeCoreException, SQLException {
      if (!smaMgr.getStateMgr().getCurrentStateName().equals("Authorize")) return new Result(
            "Action not in Authorize state");
      smaMgr.getStateMgr().updateMetrics(stateHoursSpent, statePercentComplete, true);
      return Result.TrueResult;
   }

   public Result setImplementData(String resolution, int statePercentComplete, double stateHoursSpent) throws OseeCoreException, SQLException {
      if (!smaMgr.getStateMgr().getCurrentStateName().equals("Implement")) return new Result(
            "Action not in Implement state");
      if (resolution == null || resolution.equals(""))
         teamArt.setSoleAttributeValue(ATSAttributes.RESOLUTION_OVERRIDE_ATTRIBUTE.getStoreName(), true);
      else
         teamArt.setSoleAttributeValue(ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName(), resolution);
      smaMgr.getStateMgr().updateMetrics(stateHoursSpent, statePercentComplete, true);
      return Result.TrueResult;
   }

}
