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
package org.eclipse.osee.ats.core.review;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.core.internal.Activator;
import org.eclipse.osee.ats.core.team.TeamState;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.type.AtsRelationTypes;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.workdef.DecisionReviewOption;
import org.eclipse.osee.ats.core.workdef.ReviewBlockType;
import org.eclipse.osee.ats.core.workdef.RuleDefinitionOption;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.ats.core.workflow.HoursSpentUtil;
import org.eclipse.osee.ats.core.workflow.PercentCompleteTotalUtil;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.ats.core.workflow.transition.TransitionOption;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.util.IWorkPage;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.UsersByIds;

/**
 * @author Donald G. Dunne
 */
public class ReviewManager {

   private final static String VALIDATE_REVIEW_TITLE = "Is the resolution of this Action valid?";

   public ReviewManager() {
      super();
   }

   public static boolean isValidatePage(StateDefinition stateDefinition) {
      if (stateDefinition.hasRule(RuleDefinitionOption.AddDecisionValidateBlockingReview)) {
         return true;
      }
      if (stateDefinition.hasRule(RuleDefinitionOption.AddDecisionValidateNonBlockingReview)) {
         return true;
      }
      return false;
   }

   /**
    * Create a new decision review configured and transitioned to handle action validation
    * 
    * @param force will force the creation of the review without checking that a review should be created
    */
   public static DecisionReviewArtifact createValidateReview(TeamWorkFlowArtifact teamArt, boolean force, Date createdDate, User createdBy, SkynetTransaction transaction) throws OseeCoreException {
      // If not validate page, don't do anything
      if (!force && !isValidatePage(teamArt.getStateDefinition())) {
         return null;
      }
      // If validate review already created for this state, return
      if (!force && getReviewsFromCurrentState(teamArt).size() > 0) {
         for (AbstractReviewArtifact rev : getReviewsFromCurrentState(teamArt)) {
            if (rev.getName().equals(VALIDATE_REVIEW_TITLE)) {
               return null;
            }
         }
      }
      // Create validate review
      try {

         DecisionReviewArtifact decRev =
            ReviewManager.createNewDecisionReview(
               teamArt,
               isValidateReviewBlocking(teamArt.getStateDefinition()) ? ReviewBlockType.Transition : ReviewBlockType.None,
               true, createdDate, createdBy);
         decRev.setName(VALIDATE_REVIEW_TITLE);
         decRev.setSoleAttributeValue(AtsAttributeTypes.DecisionReviewOptions,
            "No;Followup;" + getValidateReviewFollowupUsersStr(teamArt) + "\n" + "Yes;Completed;");

         TransitionManager transitionMgr = new TransitionManager(decRev);
         transitionMgr.transition(DecisionReviewState.Decision, teamArt.getCreatedBy(), transaction,
            TransitionOption.Persist);

         return decRev;

      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return null;
   }

   public static boolean isValidateReviewBlocking(StateDefinition stateDefinition) {
      return stateDefinition.hasRule(RuleDefinitionOption.AddDecisionValidateBlockingReview);
   }

   public static DecisionReviewArtifact createNewDecisionReview(TeamWorkFlowArtifact teamArt, String reviewTitle, String description, String againstState, ReviewBlockType reviewBlockType, Collection<DecisionReviewOption> options, Collection<User> assignees, Date createdDate, User createdBy, SkynetTransaction transaction) throws OseeCoreException {
      DecisionReviewArtifact decRev =
         ReviewManager.createNewDecisionReview(teamArt, reviewBlockType, reviewTitle, againstState, description,
            options, assignees, createdDate, createdBy);
      return decRev;
   }

   public static DecisionReviewArtifact createNewDecisionReviewAndTransitionToDecision(TeamWorkFlowArtifact teamArt, String reviewTitle, String description, String againstState, ReviewBlockType reviewBlockType, Collection<DecisionReviewOption> options, Collection<User> assignees, Date createdDate, User createdBy, SkynetTransaction transaction) throws OseeCoreException {
      DecisionReviewArtifact decRev =
         ReviewManager.createNewDecisionReview(teamArt, reviewBlockType, reviewTitle, againstState, description,
            options, assignees, createdDate, createdBy);
      decRev.persist(transaction);

      TransitionManager transitionMgr = new TransitionManager(decRev);
      Result result =
         transitionMgr.transition(DecisionReviewState.Decision, assignees, transaction, TransitionOption.Persist,
            TransitionOption.OverrideAssigneeCheck);
      if (result.isFalse()) {
         throw new OseeStateException("Error auto-transitioning review %s to Decision state", decRev.toStringWithId());
      }
      return decRev;
   }

   public static PeerToPeerReviewArtifact createNewPeerToPeerReview(TeamWorkFlowArtifact teamArt, String reviewTitle, String againstState, SkynetTransaction transaction) throws OseeCoreException {
      return createNewPeerToPeerReview(teamArt, reviewTitle, againstState, new Date(), UserManager.getUser(),
         transaction);
   }

   public static PeerToPeerReviewArtifact createNewPeerToPeerReview(TeamWorkFlowArtifact teamArt, String reviewTitle, String againstState, Date createdDate, User createdBy, SkynetTransaction transaction) throws OseeCoreException {
      PeerToPeerReviewArtifact peerToPeerRev =
         (PeerToPeerReviewArtifact) ArtifactTypeManager.addArtifact(AtsArtifactTypes.PeerToPeerReview,
            AtsUtilCore.getAtsBranch(), reviewTitle == null ? "Peer to Peer Review" : reviewTitle);
      // Initialize state machine
      peerToPeerRev.initializeNewStateMachine(null, new Date(), createdBy);

      if (teamArt != null) {
         teamArt.addRelation(AtsRelationTypes.TeamWorkflowToReview_Review, peerToPeerRev);
         if (againstState != null) {
            peerToPeerRev.setSoleAttributeValue(AtsAttributeTypes.RelatedToState, againstState);
         }
      }
      peerToPeerRev.setSoleAttributeValue(AtsAttributeTypes.ReviewBlocks, ReviewBlockType.None.name());
      peerToPeerRev.setSoleAttributeValue(AtsAttributeTypes.ReviewFormalType, ReviewFormalType.InFormal.name());
      peerToPeerRev.persist(transaction);
      return peerToPeerRev;
   }

   /**
    * Return Remain Hours for all reviews
    */
   public static double getRemainHours(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      double hours = 0;
      for (AbstractReviewArtifact reviewArt : getReviews(teamArt)) {
         hours += reviewArt.getRemainHoursFromArtifact();
      }
      return hours;

   }

   /**
    * Return Estimated Review Hours of "Related to State" stateName
    * 
    * @param relatedToState state name of parent workflow's state
    */
   public static double getEstimatedHours(TeamWorkFlowArtifact teamArt, IWorkPage relatedToState) throws OseeCoreException {
      double hours = 0;
      for (AbstractReviewArtifact revArt : getReviews(teamArt, relatedToState)) {
         hours += revArt.getEstimatedHoursTotal();
      }
      return hours;
   }

   /**
    * Return Estimated Hours for all reviews
    */
   public static double getEstimatedHours(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      double hours = 0;
      for (AbstractReviewArtifact revArt : getReviews(teamArt)) {
         hours += revArt.getEstimatedHoursTotal();
      }
      return hours;

   }

   public static String getValidateReviewFollowupUsersStr(TeamWorkFlowArtifact teamArt) {
      try {
         return UsersByIds.getStorageString(getValidateReviewFollowupUsers(teamArt));
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return ex.getLocalizedMessage();
      }
   }

   public static Collection<User> getValidateReviewFollowupUsers(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      Collection<User> users = teamArt.getStateMgr().getAssignees(TeamState.Implement);
      if (users.size() > 0) {
         return users;
      }

      // Else if Team Workflow , return it to the leads of this team
      return teamArt.getTeamDefinition().getLeads();

   }

   public static Collection<AbstractReviewArtifact> getReviews(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      return teamArt.getRelatedArtifacts(AtsRelationTypes.TeamWorkflowToReview_Review, AbstractReviewArtifact.class);
   }

   public static Collection<AbstractReviewArtifact> getReviewsFromCurrentState(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      return getReviews(teamArt, teamArt.getStateMgr().getCurrentState());
   }

   public static Collection<AbstractReviewArtifact> getReviews(TeamWorkFlowArtifact teamArt, IWorkPage state) throws OseeCoreException {
      Set<AbstractReviewArtifact> arts = new HashSet<AbstractReviewArtifact>();
      for (AbstractReviewArtifact revArt : getReviews(teamArt)) {
         if (revArt.getSoleAttributeValue(AtsAttributeTypes.RelatedToState, "").equals(state.getPageName())) {
            arts.add(revArt);
         }
      }
      return arts;
   }

   public static boolean hasReviews(TeamWorkFlowArtifact teamArt) {
      return teamArt.getRelatedArtifactsCount(AtsRelationTypes.TeamWorkflowToReview_Review) > 0;
   }

   public static Result areReviewsComplete(TeamWorkFlowArtifact teamArt) {
      return areReviewsComplete(teamArt, true);
   }

   public static Result areReviewsComplete(TeamWorkFlowArtifact teamArt, boolean popup) {
      try {
         for (AbstractReviewArtifact reviewArt : getReviews(teamArt)) {
            if (!reviewArt.isCompleted() && reviewArt.isCancelled()) {
               return new Result("Not Complete");
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return Result.TrueResult;
   }

   /**
    * Return Hours Spent for Reviews of "Related to State" stateName
    * 
    * @param relatedToState state name of parent workflow's state
    */
   public static double getHoursSpent(TeamWorkFlowArtifact teamArt, IWorkPage relatedToState) throws OseeCoreException {
      double spent = 0;
      for (AbstractReviewArtifact reviewArt : getReviews(teamArt, relatedToState)) {
         spent += HoursSpentUtil.getHoursSpentTotal(reviewArt);
      }
      return spent;
   }

   /**
    * Return Total Percent Complete / # Reviews for "Related to State" stateName
    * 
    * @param relatedToState state name of parent workflow's state
    */
   public static int getPercentComplete(TeamWorkFlowArtifact teamArt, IWorkPage relatedToState) throws OseeCoreException {
      int spent = 0;
      Collection<AbstractReviewArtifact> reviewArts = getReviews(teamArt, relatedToState);
      for (AbstractReviewArtifact reviewArt : reviewArts) {
         spent += PercentCompleteTotalUtil.getPercentCompleteTotal(reviewArt);
      }
      if (spent == 0) {
         return 0;
      }
      return spent / reviewArts.size();
   }

   public static DecisionReviewArtifact createNewDecisionReview(TeamWorkFlowArtifact teamArt, ReviewBlockType reviewBlockType, boolean againstCurrentState, Date createdDate, User createdBy) throws OseeCoreException {
      return createNewDecisionReview(teamArt, reviewBlockType,
         "Should we do this?  Yes will require followup, No will not",
         againstCurrentState ? teamArt.getStateMgr().getCurrentStateName() : null,
         "Enter description of the decision, if any", getDefaultDecisionReviewOptions(), null, createdDate, createdBy);
   }

   public static List<DecisionReviewOption> getDefaultDecisionReviewOptions() throws OseeCoreException {
      List<DecisionReviewOption> options = new ArrayList<DecisionReviewOption>();
      options.add(new DecisionReviewOption("Yes", true, Arrays.asList(UserManager.getUser().getUserId())));
      options.add(new DecisionReviewOption("No", false, null));
      return options;
   }

   public static String getDecisionReviewOptionsString(Collection<DecisionReviewOption> options) {
      StringBuffer sb = new StringBuffer();
      for (DecisionReviewOption opt : options) {
         sb.append(opt.getName());
         sb.append(";");
         sb.append(opt.isFollowupRequired() ? "Followup" : "Completed");
         sb.append(";");
         for (String userId : opt.getUserIds()) {
            sb.append("<" + userId + ">");
         }
         sb.append("\n");
      }
      return sb.toString();
   }

   public static DecisionReviewArtifact createNewDecisionReview(TeamWorkFlowArtifact teamArt, ReviewBlockType reviewBlockType, String title, String relatedToState, String description, Collection<DecisionReviewOption> options, Collection<User> assignees, Date createdDate, User createdBy) throws OseeCoreException {
      DecisionReviewArtifact decRev =
         (DecisionReviewArtifact) ArtifactTypeManager.addArtifact(AtsArtifactTypes.DecisionReview,
            AtsUtilCore.getAtsBranch(), title);

      // Initialize state machine
      decRev.initializeNewStateMachine(assignees, createdDate, createdBy);

      if (teamArt != null) {
         teamArt.addRelation(AtsRelationTypes.TeamWorkflowToReview_Review, decRev);
      }
      if (Strings.isValid(relatedToState)) {
         decRev.setSoleAttributeValue(AtsAttributeTypes.RelatedToState, relatedToState);
      }
      if (Strings.isValid(description)) {
         decRev.setSoleAttributeValue(AtsAttributeTypes.Description, description);
      }
      decRev.setSoleAttributeValue(AtsAttributeTypes.DecisionReviewOptions, getDecisionReviewOptionsString(options));
      if (reviewBlockType != null) {
         decRev.setSoleAttributeFromString(AtsAttributeTypes.ReviewBlocks, reviewBlockType.name());
      }

      return decRev;
   }

   public static AbstractReviewArtifact cast(Artifact artifact) {
      if (artifact instanceof AbstractReviewArtifact) {
         return (AbstractReviewArtifact) artifact;
      }
      return null;
   }
}
