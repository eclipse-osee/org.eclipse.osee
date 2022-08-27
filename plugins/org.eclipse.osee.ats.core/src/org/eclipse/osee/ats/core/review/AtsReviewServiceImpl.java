/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.core.review;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.review.DecisionReviewState;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.review.IAtsDecisionReview;
import org.eclipse.osee.ats.api.review.IAtsPeerReviewDefectManager;
import org.eclipse.osee.ats.api.review.IAtsPeerReviewRoleManager;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.review.IAtsReviewService;
import org.eclipse.osee.ats.api.review.PeerToPeerReviewState;
import org.eclipse.osee.ats.api.review.ReviewDefectItem;
import org.eclipse.osee.ats.api.review.ReviewFormalType;
import org.eclipse.osee.ats.api.review.UserRole;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionTokens;
import org.eclipse.osee.ats.api.workdef.IAtsDecisionReviewOption;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsReviewHook;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.workdef.SimpleDecisionReviewOption;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
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
   private static Set<IAtsReviewHook> reviewHooks = new HashSet<>();

   public void addReviewHook(IAtsReviewHook hook) {
      reviewHooks.add(hook);
   }

   public AtsReviewServiceImpl() {
      this(null);
      // for osgi
   }

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
   public IAtsDecisionReview createValidateReview(IAtsTeamWorkflow teamWf, boolean force, Date transitionDate, AtsUser transitionUser, IAtsChangeSet changes) {
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
         TransitionManager transitionMgr = new TransitionManager(helper);
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
         Collection<AtsUser> users = getValidateReviewFollowupUsers(teamWf);
         return atsApi.getWorkStateFactory().getStorageString(users);
      } catch (Exception ex) {
         OseeLog.log(AtsReviewServiceImpl.class, Level.SEVERE, ex);
         return ex.getLocalizedMessage();
      }
   }

   public boolean isValidateReviewBlocking(StateDefinition stateDefinition) {
      return stateDefinition.hasRule(RuleDefinitionOption.AddDecisionValidateBlockingReview.name());
   }

   public Collection<AtsUser> getValidateReviewFollowupUsers(IAtsTeamWorkflow teamWf) {
      Collection<AtsUser> users = new HashSet<>();
      users.addAll(teamWf.getStateMgr().getAssignees(TeamState.Implement));
      if (users.size() > 0) {
         return users;
      }

      // Else if Team Workflow , return it to the leads of this team
      users.addAll(atsApi.getTeamDefinitionService().getLeads(teamWf.getTeamDefinition()));
      return users;
   }

   @Override
   public IAtsDecisionReview createNewDecisionReviewAndTransitionToDecision(IAtsTeamWorkflow teamWf, String reviewTitle, String description, String againstState, ReviewBlockType reviewBlockType, Collection<IAtsDecisionReviewOption> options, List<AtsUser> assignees, Date createdDate, AtsUser createdBy, IAtsChangeSet changes) {
      IAtsDecisionReview decRev = createNewDecisionReview(teamWf, reviewBlockType, reviewTitle, againstState,
         description, options, assignees, createdDate, createdBy, changes);
      changes.add(decRev);

      // transition to decision
      TransitionHelper helper =
         new TransitionHelper("Transition to Decision", Arrays.asList(decRev), DecisionReviewState.Decision.getName(),
            assignees, null, changes, atsApi, TransitionOption.OverrideAssigneeCheck);
      TransitionManager transitionMgr = new TransitionManager(helper);
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
   public IAtsDecisionReview createNewDecisionReview(IAtsTeamWorkflow teamWf, ReviewBlockType reviewBlockType, boolean againstCurrentState, Date createdDate, AtsUser createdBy, IAtsChangeSet changes) {
      return createNewDecisionReview(teamWf, reviewBlockType,
         "Should we do this?  Yes will require followup, No will not",
         againstCurrentState ? teamWf.getStateMgr().getCurrentStateName() : null,
         "Enter description of the decision, if any", getDefaultDecisionReviewOptions(), null, createdDate, createdBy,
         changes);
   }

   @Override
   public IAtsDecisionReview createNewDecisionReview(IAtsTeamWorkflow teamWf, ReviewBlockType reviewBlockType, String title, String relatedToState, String description, Collection<IAtsDecisionReviewOption> options, List<? extends AtsUser> assignees, Date createdDate, AtsUser createdBy, IAtsChangeSet changes) {
      ArtifactToken decRevArt = changes.createArtifact(AtsArtifactTypes.DecisionReview, title);
      IAtsDecisionReview decRev = (IAtsDecisionReview) atsApi.getWorkItemService().getReview(decRevArt);

      changes.relate(teamWf, AtsRelationTypes.TeamWorkflowToReview_Review, decRev);
      atsApi.getActionService().setAtsId(decRev, decRev.getParentTeamWorkflow().getTeamDefinition(), null, changes);

      WorkDefinition workDefinition =
         atsApi.getWorkDefinitionService().getWorkDefinition(AtsWorkDefinitionTokens.WorkDef_Review_Decision);
      atsApi.getWorkDefinitionService().setWorkDefinitionAttrs(decRev, workDefinition, changes);

      // Initialize state machine
      atsApi.getActionService().initializeNewStateMachine(decRev, null, createdDate, createdBy, workDefinition,
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

   public static boolean isValidatePage(StateDefinition stateDefinition) {
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
   public boolean isStandAloneReview(Object obj) {
      if (obj instanceof IAtsPeerToPeerReview) {
         return atsApi.getAttributeResolver().getAttributeCount((IAtsPeerToPeerReview) obj,
            AtsAttributeTypes.ActionableItemReference) > 0;
      }
      return false;
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

   @Override
   public boolean hasReviews(IAtsTeamWorkflow teamWf) {
      return !getReviews(teamWf).isEmpty();
   }

   @Override
   public Collection<IAtsAbstractReview> getReviews(IAtsTeamWorkflow teamWf, IStateToken state) {
      Set<IAtsAbstractReview> reviews = new HashSet<>();
      for (IAtsAbstractReview review : getReviews(teamWf)) {
         if (atsApi.getAttributeResolver().getSoleAttributeValue(review, AtsAttributeTypes.RelatedToState, "").equals(
            state.getName())) {
            reviews.add(review);
         }
      }
      return reviews;
   }

   @Override
   public String getDefaultPeerReviewTitle(IAtsTeamWorkflow teamWf) {
      return "Review \"" + teamWf.getArtifactTypeName() + "\" titled \"" + teamWf.getName() + "\"";
   }

   @Override
   public IAtsPeerToPeerReview createNewPeerToPeerReview(IAtsTeamWorkflow teamWf, String reviewTitle, String againstState, IAtsChangeSet changes) {
      return createNewPeerToPeerReview(teamWf, reviewTitle, againstState, new Date(),
         atsApi.getUserService().getCurrentUser(), changes);
   }

   @Override
   public IAtsPeerToPeerReview createNewPeerToPeerReview(WorkDefinition workDefinition, IAtsTeamWorkflow teamWf, String reviewTitle, String againstState, IAtsChangeSet changes) {
      return createNewPeerToPeerReview(workDefinition, teamWf, teamWf.getTeamDefinition(), reviewTitle, againstState,
         new Date(), atsApi.getUserService().getCurrentUser(), changes);
   }

   @Override
   public IAtsPeerToPeerReview createNewPeerToPeerReview(IAtsTeamWorkflow teamWF, String reviewTitle, String againstState, Date createdDate, AtsUser createdBy, IAtsChangeSet changes) {
      return createNewPeerToPeerReview(
         atsApi.getWorkDefinitionService().getWorkDefinitionForPeerToPeerReviewNotYetCreated(teamWF), teamWF,
         teamWF.getTeamDefinition(), reviewTitle, againstState, createdDate, createdBy, changes);
   }

   @Override
   public IAtsPeerToPeerReview createNewPeerToPeerReview(IAtsActionableItem actionableItem, String reviewTitle, String againstState, Date createdDate, AtsUser createdBy, IAtsChangeSet changes) {
      IAtsTeamDefinition teamDef =
         actionableItem.getAtsApi().getActionableItemService().getTeamDefinitionInherited(actionableItem);
      WorkDefinition workDefinition =
         atsApi.getWorkDefinitionService().getWorkDefinitionForPeerToPeerReviewNotYetCreatedAndStandalone(
            actionableItem);
      IAtsPeerToPeerReview peerArt = createNewPeerToPeerReview(workDefinition, null, teamDef, reviewTitle, againstState,
         createdDate, createdBy, changes);
      atsApi.getActionableItemService().addActionableItem(peerArt, actionableItem, changes);
      return peerArt;
   }

   private IAtsPeerToPeerReview createNewPeerToPeerReview(WorkDefinition workDefinition, IAtsTeamWorkflow teamWf, IAtsTeamDefinition teamDef, String reviewTitle, String againstState, Date createdDate, AtsUser createdBy, IAtsChangeSet changes) {
      Conditions.assertNotNull(workDefinition, "WorkDefinition");
      ArtifactTypeToken reviewArtType = workDefinition.getArtType();
      IAtsPeerToPeerReview peerRev = (IAtsPeerToPeerReview) changes.createArtifact(reviewArtType,
         reviewTitle == null ? "Peer to Peer Review" : reviewTitle);

      if (teamWf != null) {
         changes.relate(teamWf, AtsRelationTypes.TeamWorkflowToReview_Review, peerRev);
      }

      atsApi.getActionService().setAtsId(peerRev, teamDef, null, changes);
      atsApi.getWorkDefinitionService().setWorkDefinitionAttrs(peerRev, workDefinition, changes);
      atsApi.getActionService().initializeNewStateMachine(peerRev, null, createdDate, createdBy, workDefinition,
         changes);

      if (teamWf != null && againstState != null) {
         changes.setSoleAttributeValue(peerRev, AtsAttributeTypes.RelatedToState, againstState);
      }

      changes.setSoleAttributeValue(peerRev, AtsAttributeTypes.ReviewBlocks, ReviewBlockType.None.name());
      changes.add(peerRev);
      return peerRev;
   }

   @Override
   public Set<IAtsReviewHook> getReviewHooks() {
      return reviewHooks;
   }

   /**
    * Quickly transition to a state with minimal metrics and data entered. Should only be used for automated
    * transitioning for things such as developmental testing and demos.
    *
    * @param user User to transition to OR null if should use user of current state
    */
   @Override
   public Result transitionDecisionTo(IAtsDecisionReview decRev, DecisionReviewState toState, AtsUser user, boolean popup, IAtsChangeSet changes) {
      Result result = Result.TrueResult;
      // If in Prepare state, set data and transition to Decision
      if (decRev.isInState(DecisionReviewState.Prepare)) {
         result = setDecisionPrepareStateData(popup, decRev, 100, 3, .2, changes);
         if (result.isFalse()) {
            return result;
         }
         result = transitionDecisionToState(toState.getStateType(), popup, DecisionReviewState.Decision, decRev, user,
            changes);
         if (result.isFalse()) {
            return result;
         }
      }
      if (toState == DecisionReviewState.Decision) {
         return Result.TrueResult;
      }

      // If desired to transition to follow-up, then decision is false
      boolean decision = toState != DecisionReviewState.Followup;

      result = setDecisionStateData(popup, decRev, decision, 100, .2, changes);
      if (result.isFalse()) {
         return result;
      }

      result = transitionDecisionToState(toState.getStateType(), popup, toState, decRev, user, changes);
      if (result.isFalse()) {
         return result;
      }
      return Result.TrueResult;
   }

   public Result setDecisionPrepareStateData(boolean popup, IAtsDecisionReview decRev, int statePercentComplete, double estimateHours, double stateHoursSpent, IAtsChangeSet changes) {
      if (!decRev.isInState(DecisionReviewState.Prepare)) {
         Result result = new Result("Action not in Prepare state");
         if (result.isFalse() && popup) {
            return result;
         }
      }
      changes.setSoleAttributeValue(decRev, AtsAttributeTypes.EstimatedHours, estimateHours);
      decRev.getStateMgr().updateMetrics(decRev.getStateDefinition(), stateHoursSpent, statePercentComplete, true,
         atsApi.getUserService().getCurrentUser());
      return Result.TrueResult;
   }

   public Result transitionDecisionToState(StateType StateType, boolean popup, IStateToken toState, IAtsDecisionReview decRev, AtsUser user, IAtsChangeSet changes) {
      TransitionHelper helper = new TransitionHelper("Transition to " + toState.getName(), Arrays.asList(decRev),
         toState.getName(), Arrays.asList(user == null ? decRev.getStateMgr().getAssignees().iterator().next() : user),
         null, changes, atsApi, TransitionOption.OverrideAssigneeCheck);
      TransitionManager transitionMgr = new TransitionManager(helper);
      TransitionResults results = transitionMgr.handleAll();
      if (results.isEmpty()) {
         return Result.TrueResult;
      }
      return new Result("Transition Error %s", results.toString());
   }

   public Result setDecisionStateData(boolean popup, IAtsDecisionReview decRev, boolean decision, int statePercentComplete, double stateHoursSpent, IAtsChangeSet changes) {
      if (!decRev.isInState(DecisionReviewState.Decision)) {
         Result result = new Result("Action not in Decision state");
         if (result.isFalse() && popup) {
            return result;
         }
      }
      changes.setSoleAttributeValue(decRev, AtsAttributeTypes.Decision, decision ? "Yes" : "No");

      decRev.getStateMgr().updateMetrics(decRev.getStateDefinition(), stateHoursSpent, statePercentComplete, true,
         atsApi.getUserService().getCurrentUser());
      return Result.TrueResult;
   }

   @Override
   public String getDefaultReviewTitle(IAtsTeamWorkflow teamWf) {
      return atsApi.getReviewService().getDefaultPeerReviewTitle(teamWf);
   }

   /**
    * Quickly transition to a state with minimal metrics and data entered. Should only be used for automated transition
    * for things such as developmental testing and demos.
    *
    * @param user User to transition to OR null if should use user of current state
    */
   @Override
   public Result transitionTo(IAtsPeerToPeerReview peerRev, PeerToPeerReviewState toState, Collection<UserRole> roles, Collection<ReviewDefectItem> defects, AtsUser user, boolean popup, IAtsChangeSet changes) {
      Result result = setPrepareStateData(popup, peerRev, roles, "DoThis.java", 100, .2, changes);
      if (result.isFalse()) {
         return result;
      }
      result = transitionToState(PeerToPeerReviewState.Review.getStateType(), popup, peerRev,
         PeerToPeerReviewState.Review, changes);
      if (result.isFalse()) {
         return result;
      }
      if (toState == PeerToPeerReviewState.Review) {
         return Result.TrueResult;
      }

      result = setReviewStateData(peerRev, roles, defects, 100, .2, changes);
      if (result.isFalse()) {
         return result;
      }

      result = transitionToState(PeerToPeerReviewState.Completed.getStateType(), popup, peerRev,
         PeerToPeerReviewState.Completed, changes);
      if (result.isFalse()) {
         return result;
      }
      return Result.TrueResult;
   }

   private Result transitionToState(StateType StateType, boolean popup, IAtsPeerToPeerReview peerRev, IStateToken toState, IAtsChangeSet changes) {
      TransitionHelper helper = new TransitionHelper("Transition to " + toState.getName(), Arrays.asList(peerRev),
         toState.getName(), Arrays.asList(peerRev.getStateMgr().getAssignees().iterator().next()), null, changes,
         atsApi, TransitionOption.OverrideAssigneeCheck);
      TransitionManager transitionMgr = new TransitionManager(helper);
      TransitionResults results = transitionMgr.handleAll();
      if (results.isEmpty()) {
         return Result.TrueResult;
      }
      return new Result("Error transitioning [%s]", results);
   }

   @Override
   public Result setPrepareStateData(boolean popup, IAtsPeerToPeerReview peerRev, Collection<UserRole> roles, String reviewMaterials, int statePercentComplete, double stateHoursSpent, IAtsChangeSet changes) {
      if (!peerRev.isInState(PeerToPeerReviewState.Prepare)) {
         Result result = new Result("Action not in Prepare state");
         if (result.isFalse() && popup) {
            return result;
         }

      }
      if (roles != null) {
         IAtsPeerReviewRoleManager roleMgr = peerRev.getRoleManager();
         for (UserRole role : roles) {
            roleMgr.addOrUpdateUserRole(role);
         }
         roleMgr.saveToArtifact(changes);
      }
      changes.setSoleAttributeValue(peerRev, AtsAttributeTypes.Location, reviewMaterials);
      changes.setSoleAttributeValue(peerRev, AtsAttributeTypes.ReviewFormalType, ReviewFormalType.InFormal.name());
      peerRev.getStateMgr().updateMetrics(peerRev.getStateDefinition(), stateHoursSpent, statePercentComplete, true,
         atsApi.getUserService().getCurrentUser());
      return Result.TrueResult;
   }

   @Override
   public Result setReviewStateData(IAtsPeerToPeerReview peerRev, Collection<UserRole> roles, Collection<ReviewDefectItem> defects, int statePercentComplete, double stateHoursSpent, IAtsChangeSet changes) {
      if (roles != null) {
         IAtsPeerReviewRoleManager roleMgr = peerRev.getRoleManager();
         for (UserRole role : roles) {
            roleMgr.addOrUpdateUserRole(role);
         }
         roleMgr.saveToArtifact(changes);
      }
      if (defects != null) {
         IAtsPeerReviewDefectManager defectManager = peerRev.getDefectManager();
         for (ReviewDefectItem defect : defects) {
            defectManager.addOrUpdateDefectItem(defect);
         }
         defectManager.saveToArtifact(peerRev, changes);
      }
      peerRev.getStateMgr().updateMetrics(peerRev.getStateDefinition(), stateHoursSpent, statePercentComplete, true,
         atsApi.getUserService().getCurrentUser());
      return Result.TrueResult;
   }

   @Override
   public ReviewDefectItem getDefectItem(String xml, IAtsPeerToPeerReview review) {
      return new ReviewDefectItem(xml, false, review);
   }

   @Override
   public IAtsAbstractReview getReview(ArtifactToken artifact) {
      IAtsAbstractReview review = null;
      if (artifact instanceof IAtsAbstractReview) {
         review = (IAtsAbstractReview) artifact;
      } else if (artifact.isOfType(AtsArtifactTypes.PeerToPeerReview)) {
         review = new PeerToPeerReview(atsApi.getLogger(), atsApi, artifact);
      } else if (artifact.isOfType(AtsArtifactTypes.DecisionReview)) {
         review = new DecisionReview(atsApi.getLogger(), atsApi, artifact);
      } else {
         throw new OseeArgumentException("Artifact %s must be of type Review", artifact.toStringWithId());
      }
      return review;
   }

}
