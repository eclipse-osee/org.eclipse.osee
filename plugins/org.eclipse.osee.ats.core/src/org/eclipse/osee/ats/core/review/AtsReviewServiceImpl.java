/*******************************************************************************
 * Copyright (c) 2013 Boeing.
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
import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.review.IAtsDecisionReview;
import org.eclipse.osee.ats.api.review.IAtsPeerReviewRoleManager;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.review.IAtsReviewService;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsDecisionReviewOption;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.transition.IAtsTransitionManager;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workdef.SimpleDecisionReviewOption;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TransitionFactory;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class AtsReviewServiceImpl implements IAtsReviewService {

   private final AtsApi atsApi;
   private final static String VALIDATE_REVIEW_TITLE = "Is the resolution of this Action valid?";

   public AtsReviewServiceImpl(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public boolean isValidationReviewRequired(IAtsWorkItem workItem) {
      boolean required = false;
      if (workItem.isTeamWorkflow()) {
         required =
            atsApi.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.ValidationRequired, false);
      }
      return required;
   }

   @Override
   public IAtsDecisionReview createValidateReview(IAtsTeamWorkflow teamWf, boolean force, Date transitionDate, IAtsUser transitionUser, IAtsChangeSet changes) {
      // If not validate page, don't do anything
      if (!force && !isValidatePage(teamWf.getStateDefinition())) {
         return null;
      }
      // If validate review already created for this state, return
      if (!force && getReviewsFromCurrentState(teamWf).size() > 0) {
         for (IAtsAbstractReview review : getReviewsFromCurrentState(teamWf)) {
            if (review.getName().equals(VALIDATE_REVIEW_TITLE)) {
               return null;
            }
         }
      }
      // Create validate review
      try {

         IAtsDecisionReview decRev = createNewDecisionReview(teamWf,
            isValidateReviewBlocking(teamWf.getStateDefinition()) ? ReviewBlockType.Transition : ReviewBlockType.None,
            true, new Date(), atsApi.getUserService().getCurrentUser(), changes);
         changes.setName(decRev, VALIDATE_REVIEW_TITLE);
         changes.setSoleAttributeValue(decRev, AtsAttributeTypes.DecisionReviewOptions,
            "No;Followup;" + getValidateReviewFollowupUsersStr(teamWf) + "\n" + "Yes;Completed;");

         TransitionHelper helper = new TransitionHelper("Transition to Decision", Arrays.asList(decRev),
            DecisionReviewState.Decision.getName(), Arrays.asList(teamWf.getCreatedBy()), null, changes, atsApi,
            TransitionOption.None);
         IAtsTransitionManager transitionMgr = TransitionFactory.getTransitionManager(helper);
         TransitionResults results = transitionMgr.handleAll();
         if (!results.isEmpty()) {
            OseeLog.logf(AtsReviewServiceImpl.class, OseeLevel.SEVERE_POPUP,
               "Error transitioning Decision review [%s] to Decision %s", decRev.toStringWithId(), results);
         }

         return decRev;

      } catch (Exception ex) {
         OseeLog.log(AtsReviewServiceImpl.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return null;
   }

   public String getValidateReviewFollowupUsersStr(IAtsTeamWorkflow teamWf) {
      try {
         Collection<IAtsUser> users = getValidateReviewFollowupUsers(teamWf);
         return atsApi.getWorkStateFactory().getStorageString(users);
      } catch (Exception ex) {
         OseeLog.log(AtsReviewServiceImpl.class, Level.SEVERE, ex);
         return ex.getLocalizedMessage();
      }
   }

   public boolean isValidateReviewBlocking(IAtsStateDefinition stateDefinition) {
      return stateDefinition.hasRule(RuleDefinitionOption.AddDecisionValidateBlockingReview.name());
   }

   public Collection<IAtsUser> getValidateReviewFollowupUsers(IAtsTeamWorkflow teamWf) {
      Collection<IAtsUser> users = new HashSet<>();
      users.addAll(teamWf.getStateMgr().getAssignees(TeamState.Implement));
      if (users.size() > 0) {
         return users;
      }

      // Else if Team Workflow , return it to the leads of this team
      users.addAll(teamWf.getTeamDefinition().getLeads());
      return users;
   }

   @Override
   public IAtsDecisionReview createNewDecisionReviewAndTransitionToDecision(IAtsTeamWorkflow teamWf, String reviewTitle, String description, String againstState, ReviewBlockType reviewBlockType, Collection<IAtsDecisionReviewOption> options, List<? extends IAtsUser> assignees, Date createdDate, IAtsUser createdBy, IAtsChangeSet changes) {
      IAtsDecisionReview decRev = createNewDecisionReview(teamWf, reviewBlockType, reviewTitle, againstState,
         description, options, assignees, createdDate, createdBy, changes);
      changes.add(decRev);

      // transition to decision
      TransitionHelper helper =
         new TransitionHelper("Transition to Decision", Arrays.asList(decRev), DecisionReviewState.Decision.getName(),
            assignees, null, changes, atsApi, TransitionOption.OverrideAssigneeCheck);
      IAtsTransitionManager transitionMgr = TransitionFactory.getTransitionManager(helper);
      TransitionResults results = transitionMgr.handleAll();

      if (!results.isEmpty()) {
         throw new OseeStateException("Error auto-transitioning review %s to Decision state. Results [%s]",
            decRev.toStringWithId(), results.toString());
      }
      // ensure assignees are as requested
      decRev.getStateMgr().setAssignees(assignees);
      changes.add(decRev);
      return decRev;
   }

   @Override
   public IAtsDecisionReview createNewDecisionReview(IAtsTeamWorkflow teamWf, ReviewBlockType reviewBlockType, boolean againstCurrentState, Date createdDate, IAtsUser createdBy, IAtsChangeSet changes) {
      return createNewDecisionReview(teamWf, reviewBlockType,
         "Should we do this?  Yes will require followup, No will not",
         againstCurrentState ? teamWf.getStateMgr().getCurrentStateName() : null,
         "Enter description of the decision, if any", getDefaultDecisionReviewOptions(), null, createdDate, createdBy,
         changes);
   }

   @Override
   public IAtsDecisionReview createNewDecisionReview(IAtsTeamWorkflow teamWf, ReviewBlockType reviewBlockType, String title, String relatedToState, String description, Collection<IAtsDecisionReviewOption> options, List<? extends IAtsUser> assignees, Date createdDate, IAtsUser createdBy, IAtsChangeSet changes) {
      ArtifactToken decRevArt = changes.createArtifact(AtsArtifactTypes.DecisionReview, title);
      IAtsDecisionReview decRev = (IAtsDecisionReview) atsApi.getWorkItemService().getReview(decRevArt);

      changes.relate(teamWf, AtsRelationTypes.TeamWorkflowToReview_Review, decRev);
      atsApi.getActionFactory().setAtsId(decRev, decRev.getParentTeamWorkflow().getTeamDefinition(), changes);

      IAtsWorkDefinition workDefinition =
         atsApi.getWorkDefinitionService().computeAndSetWorkDefinitionAttrs(decRev, null, changes);
      Conditions.assertNotNull(workDefinition, "Work Definition can not be null for %s", decRev.toStringWithId());

      // Initialize state machine
      atsApi.getActionFactory().initializeNewStateMachine(decRev, null, createdDate, createdBy, workDefinition,
         changes);
      decRev.getStateMgr().setAssignees(workDefinition.getStartState().getName(), StateType.Working, assignees);

      if (Strings.isValid(relatedToState)) {
         changes.setSoleAttributeValue(decRev, AtsAttributeTypes.RelatedToState, relatedToState);
      }
      if (Strings.isValid(description)) {
         changes.setSoleAttributeValue(decRev, AtsAttributeTypes.Description, description);
      }
      changes.setSoleAttributeValue(decRev, AtsAttributeTypes.DecisionReviewOptions,
         getDecisionReviewOptionsString(options));
      if (reviewBlockType != null) {
         changes.setSoleAttributeFromString(decRev, AtsAttributeTypes.ReviewBlocks, reviewBlockType.name());
      }
      changes.add(decRev);
      return decRev;
   }

   @Override
   public String getDecisionReviewOptionsString(Collection<IAtsDecisionReviewOption> options) {
      StringBuffer sb = new StringBuffer();
      for (IAtsDecisionReviewOption opt : options) {
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

   public static boolean isValidatePage(IAtsStateDefinition stateDefinition) {
      if (stateDefinition.hasRule(RuleDefinitionOption.AddDecisionValidateBlockingReview.name())) {
         return true;
      }
      if (stateDefinition.hasRule(RuleDefinitionOption.AddDecisionValidateNonBlockingReview.name())) {
         return true;
      }
      return false;
   }

   @Override
   public List<IAtsDecisionReviewOption> getDefaultDecisionReviewOptions() {
      List<IAtsDecisionReviewOption> options = new ArrayList<>();
      options.add(new SimpleDecisionReviewOption("Yes", true,
         Arrays.asList(atsApi.getUserService().getCurrentUser().getUserId())));
      options.add(new SimpleDecisionReviewOption("No", false, null));
      return options;
   }

   @Override
   public Collection<IAtsAbstractReview> getReviewsFromCurrentState(IAtsTeamWorkflow teamWf) {
      return atsApi.getWorkItemService().getReviews(teamWf, teamWf.getStateMgr().getCurrentState());
   }

   @Override
   public ReviewBlockType getReviewBlockType(IAtsAbstractReview review) {
      String blockStr = atsApi.getAttributeResolver().getSoleAttributeValueAsString(review,
         AtsAttributeTypes.ReviewBlocks, ReviewBlockType.None.name());
      return ReviewBlockType.valueOf(blockStr);
   }

   @Override
   public boolean isStandAloneReview(IAtsAbstractReview review) {
      return atsApi.getAttributeResolver().getAttributeCount(review, AtsAttributeTypes.ActionableItemReference) > 0;
   }

   @Override
   public Collection<IAtsAbstractReview> getReviews(IAtsTeamWorkflow teamWf) {
      List<IAtsAbstractReview> reviews = new ArrayList<>();

      for (ArtifactToken reviewArt : atsApi.getRelationResolver().getRelated(teamWf,
         AtsRelationTypes.TeamWorkflowToReview_Review)) {
         reviews.add(atsApi.getWorkItemService().getReview(reviewArt));
      }
      return reviews;
   }

   @Override
   public IAtsPeerReviewRoleManager createPeerReviewRoleManager(IAtsPeerToPeerReview peerRev) {
      return new UserRoleManager(peerRev, atsApi);
   }

}
