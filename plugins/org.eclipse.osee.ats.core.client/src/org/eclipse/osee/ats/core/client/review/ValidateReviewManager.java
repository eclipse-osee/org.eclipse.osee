/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.review;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.ReviewBlockType;
import org.eclipse.osee.ats.api.workdef.RuleDefinitionOption;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.core.client.workflow.transition.TransitionManager;
import org.eclipse.osee.ats.core.client.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.core.client.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * Convenience methods used to create a validation decision review if so selected on the new action wizard
 * 
 * @author Donald G. Dunne
 */
public class ValidateReviewManager {

   private final static String VALIDATE_REVIEW_TITLE = "Is the resolution of this Action valid?";

   public static boolean isValidatePage(IAtsStateDefinition stateDefinition) {
      if (stateDefinition.hasRule(RuleDefinitionOption.AddDecisionValidateBlockingReview.name())) {
         return true;
      }
      if (stateDefinition.hasRule(RuleDefinitionOption.AddDecisionValidateNonBlockingReview.name())) {
         return true;
      }
      return false;
   }

   /**
    * Create a new decision review configured and transitioned to handle action validation
    * 
    * @param force will force the creation of the review without checking that a review should be created
    */
   public static DecisionReviewArtifact createValidateReview(TeamWorkFlowArtifact teamArt, boolean force, Date createdDate, IAtsUser createdBy, SkynetTransaction transaction) throws OseeCoreException {
      // If not validate page, don't do anything
      if (!force && !isValidatePage(teamArt.getStateDefinition())) {
         return null;
      }
      // If validate review already created for this state, return
      if (!force && ReviewManager.getReviewsFromCurrentState(teamArt).size() > 0) {
         for (AbstractReviewArtifact rev : ReviewManager.getReviewsFromCurrentState(teamArt)) {
            if (rev.getName().equals(VALIDATE_REVIEW_TITLE)) {
               return null;
            }
         }
      }
      // Create validate review
      try {

         DecisionReviewArtifact decRev =
            DecisionReviewManager.createNewDecisionReview(
               teamArt,
               isValidateReviewBlocking(teamArt.getStateDefinition()) ? ReviewBlockType.Transition : ReviewBlockType.None,
               true, createdDate, createdBy);
         decRev.setName(VALIDATE_REVIEW_TITLE);
         decRev.setSoleAttributeValue(AtsAttributeTypes.DecisionReviewOptions,
            "No;Followup;" + getValidateReviewFollowupUsersStr(teamArt) + "\n" + "Yes;Completed;");

         TransitionHelper helper =
            new TransitionHelper("Transition to Decision", Arrays.asList(decRev),
               DecisionReviewState.Decision.getName(), Arrays.asList(teamArt.getCreatedBy()), null,
               TransitionOption.None);
         TransitionManager transitionMgr = new TransitionManager(helper, transaction);
         TransitionResults results = transitionMgr.handleAll();
         if (!results.isEmpty()) {
            OseeLog.logf(Activator.class, OseeLevel.SEVERE_POPUP,
               "Error transitioning Decision review [%s] to Decision %s", decRev.toStringWithId(), results);
         }

         return decRev;

      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return null;
   }

   public static boolean isValidateReviewBlocking(IAtsStateDefinition stateDefinition) {
      return stateDefinition.hasRule(RuleDefinitionOption.AddDecisionValidateBlockingReview.name());
   }

   public static String getValidateReviewFollowupUsersStr(TeamWorkFlowArtifact teamArt) {
      try {
         return org.eclipse.osee.ats.core.users.UsersByIds.getStorageString(getValidateReviewFollowupUsers(teamArt));
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return ex.getLocalizedMessage();
      }
   }

   public static Collection<IAtsUser> getValidateReviewFollowupUsers(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      Collection<IAtsUser> users = new HashSet<IAtsUser>();
      users.addAll(teamArt.getStateMgr().getAssignees(TeamState.Implement));
      if (users.size() > 0) {
         return users;
      }

      // Else if Team Workflow , return it to the leads of this team
      users.addAll(teamArt.getTeamDefinition().getLeads());
      return users;
   }

}
