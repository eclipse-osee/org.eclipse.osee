/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.rest.internal.demo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.review.IAtsPeerReviewDefectManager;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.review.PeerToPeerReviewState;
import org.eclipse.osee.ats.api.review.ReviewDefectItem;
import org.eclipse.osee.ats.api.review.ReviewDefectItem.Disposition;
import org.eclipse.osee.ats.api.review.ReviewDefectItem.InjectionActivity;
import org.eclipse.osee.ats.api.review.ReviewDefectItem.Severity;
import org.eclipse.osee.ats.api.review.ReviewRole;
import org.eclipse.osee.ats.api.review.UserRole;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * Creates SAW PL Requirements peer reviews with resolved/open defects so the directed "Open Defect Items" search (see
 * {@link org.eclipse.osee.ats.rest.internal.review.ReviewOperations}) has data for demo and test. A defect is returned
 * by that search only when its review is Working, the defect's userId matches, its resolution has text, and it is not
 * closed. This op seeds both matching and excluded defects for Joe Smith and Kay Wheeler.
 *
 * @author Donald G. Dunne
 */
public class Pdd97CreateDemoDefectReviews extends AbstractPopulateDemoDatabase {

   public static final String OPEN_REVIEW_NAME = "Defect Search - Open Peer Review";
   public static final String COMPLETED_REVIEW_NAME = "Defect Search - Completed Peer Review";

   public Pdd97CreateDemoDefectReviews(XResultData rd, AtsApi atsApi) {
      super(rd, atsApi);
   }

   @Override
   public void run() {
      rd.logf("Running [%s]...\n", getClass().getSimpleName());

      IAtsTeamWorkflow teamWf = createReqTeamWf();
      if (rd.isErrors()) {
         return;
      }

      createOpenReviewWithDefects(teamWf);
      createCompletedReviewWithDefects(teamWf);
   }

   private IAtsTeamWorkflow createReqTeamWf() {
      NewActionData data = atsApi.getActionService() //
         .createActionData(getClass().getSimpleName(),
            DemoArtifactToken.DefectSearchReviewChanges_TeamWf.getName(),
            "Workflow hosting peer reviews used by the Open Defect Items search") //
         .andAiAndToken(DemoArtifactToken.SAW_PL_Requirements_AI,
            DemoArtifactToken.DefectSearchReviewChanges_TeamWf) //
         .andChangeType(ChangeTypes.Improvement).andPriority("3");
      NewActionData newData = atsApi.getActionService().createAction(data);
      if (dataErrored(newData)) {
         return null;
      }
      if (!newData.getRd().isSuccess()) {
         rd.errorf("Create action failed for [%s]: %s\n", DemoArtifactToken.DefectSearchReviewChanges_TeamWf.getName(),
            newData.getRd().toString());
         return null;
      }
      return newData.getActResult().getAtsTeamWfs().iterator().next();
   }

   /**
    * Working peer review. Defects seeded:
    * <ul>
    * <li>Joe Smith - resolved, open -> MATCHES (x2)</li>
    * <li>Joe Smith - no resolution, open -> excluded (not resolved)</li>
    * <li>Joe Smith - resolved, closed -> excluded (closed)</li>
    * <li>Kay Wheeler - resolved, open -> MATCHES (x1)</li>
    * </ul>
    */
   private void createOpenReviewWithDefects(IAtsTeamWorkflow teamWf) {
      IAtsChangeSet changes = atsApi.createChangeSet(getClass().getSimpleName() + " - Open Review");
      AtsUser currentUser = atsApi.getUserService().getCurrentUser();

      IAtsPeerToPeerReview review = atsApi.getReviewService().createNewPeerToPeerReview(teamWf, OPEN_REVIEW_NAME,
         teamWf.getCurrentStateName(), changes);
      changes.setSoleAttributeValue(review.getStoreObject(), AtsAttributeTypes.Description, "Open defects demo review");

      List<UserRole> roles = new ArrayList<>();
      roles.add(new UserRole(ReviewRole.Author, DemoUsers.Kay_Jones, 1.0, true));
      roles.add(new UserRole(ReviewRole.Reviewer, DemoUsers.Joe_Smith, 1.0, false));
      roles.add(new UserRole(ReviewRole.Reviewer, DemoUsers.Kay_Wheeler, 1.0, false));

      Result result = atsApi.getReviewService().transitionTo(review, PeerToPeerReviewState.Review, roles, null,
         currentUser, false, changes);
      if (result.isFalse()) {
         rd.errorf("Failed transitioning [%s] to Review: %s\n", OPEN_REVIEW_NAME, result.getText());
         return;
      }

      String joeId = atsApi.getUserService().getUserById(DemoUsers.Joe_Smith).getUserId();
      String kayWheelerId = atsApi.getUserService().getUserById(DemoUsers.Kay_Wheeler).getUserId();

      List<ReviewDefectItem> defects = new ArrayList<>();
      defects.add(resolvedOpen(DemoUsers.Joe_Smith, "Joe - null check missing", kayWheelerId));
      defects.add(resolvedOpen(DemoUsers.Joe_Smith, "Joe - rename variable", kayWheelerId));
      defects.add(unresolvedOpen(DemoUsers.Joe_Smith, "Joe - needs discussion"));
      defects.add(resolvedClosed(DemoUsers.Joe_Smith, "Joe - already handled", kayWheelerId, joeId));
      defects.add(resolvedOpen(DemoUsers.Kay_Wheeler, "Kay - boundary condition", joeId));

      IAtsPeerReviewDefectManager defectManager = review.getDefectManager();
      for (ReviewDefectItem defect : defects) {
         defectManager.addOrUpdateDefectItem(defect);
      }
      defectManager.saveToArtifact(review, changes);

      changes.add(review);
      TransactionToken tx = changes.execute();
      if (tx.isInvalid()) {
         rd.errorf("Change set execute failed (invalid transaction) for [%s]\n", OPEN_REVIEW_NAME);
      }
   }

   /**
    * Completed peer review. All defects are closed so the review can complete; because the review is Completed (not
    * Working) none of its defects are returned by the Open Defect Items search.
    */
   private void createCompletedReviewWithDefects(IAtsTeamWorkflow teamWf) {
      IAtsChangeSet changes = atsApi.createChangeSet(getClass().getSimpleName() + " - Completed Review");
      AtsUser currentUser = atsApi.getUserService().getCurrentUser();

      IAtsPeerToPeerReview review = atsApi.getReviewService().createNewPeerToPeerReview(teamWf, COMPLETED_REVIEW_NAME,
         teamWf.getCurrentStateName(), changes);
      changes.setSoleAttributeValue(review.getStoreObject(), AtsAttributeTypes.Description,
         "Completed defects demo review");

      List<UserRole> roles = new ArrayList<>();
      roles.add(new UserRole(ReviewRole.Author, DemoUsers.Kay_Jones, 1.0, true));
      roles.add(new UserRole(ReviewRole.Reviewer, DemoUsers.Joe_Smith, 1.0, true));

      Result toReview = atsApi.getReviewService().transitionTo(review, PeerToPeerReviewState.Review, roles, null,
         currentUser, false, changes);
      if (toReview.isFalse()) {
         rd.errorf("Failed transitioning [%s] to Review: %s\n", COMPLETED_REVIEW_NAME, toReview.getText());
         return;
      }

      String currentUserId = currentUser.getUserId();
      ReviewDefectItem defect = resolvedClosed(DemoUsers.Joe_Smith, "Joe - fixed before completion", currentUserId,
         currentUserId);
      IAtsPeerReviewDefectManager defectManager = review.getDefectManager();
      defectManager.addOrUpdateDefectItem(defect);
      defectManager.saveToArtifact(review, changes);
      changes.add(review);
      TransactionToken tx = changes.execute();
      if (tx.isInvalid()) {
         rd.errorf("Change set execute failed (invalid transaction) for [%s]\n", COMPLETED_REVIEW_NAME);
         return;
      }

      // Transition to Completed; requires all defects closed (enforced by TransitionManager)
      review = (IAtsPeerToPeerReview) atsApi.getReviewService().getReview(review.getArtifactToken());
      IAtsChangeSet completeChanges = atsApi.createChangeSet(getClass().getSimpleName() + " - Complete Review");
      Result toCompleted = atsApi.getReviewService().transitionTo(review, PeerToPeerReviewState.Completed, null, null,
         currentUser, false, completeChanges, TransitionOption.OverrideAssigneeCheck,
         TransitionOption.OverrideTransitionValidityCheck);
      if (toCompleted.isFalse()) {
         rd.errorf("Failed transitioning [%s] to Completed: %s\n", COMPLETED_REVIEW_NAME, toCompleted.getText());
         return;
      }
      completeChanges.add(review);
      TransactionToken completeTx = completeChanges.execute();
      if (completeTx.isInvalid()) {
         rd.errorf("Change set execute failed (invalid transaction) completing [%s]\n", COMPLETED_REVIEW_NAME);
      }
   }

   private ReviewDefectItem resolvedOpen(UserToken creator, String description, String resolvedByUserId) {
      ReviewDefectItem defect = new ReviewDefectItem(creator, Severity.Minor, Disposition.Accept, InjectionActivity.Code,
         description, "Fixed", "SomeFile.java", new Date(), "");
      defect.setResolvedUserId(resolvedByUserId);
      defect.setClosed(false);
      return defect;
   }

   private ReviewDefectItem unresolvedOpen(UserToken creator, String description) {
      ReviewDefectItem defect = new ReviewDefectItem(creator, Severity.Minor, Disposition.None, InjectionActivity.Code,
         description, "", "SomeFile.java", new Date(), "");
      defect.setClosed(false);
      return defect;
   }

   private ReviewDefectItem resolvedClosed(UserToken creator, String description, String resolvedByUserId,
      String closedByUserId) {
      ReviewDefectItem defect = new ReviewDefectItem(creator, Severity.Minor, Disposition.Accept, InjectionActivity.Code,
         description, "Fixed", "SomeFile.java", new Date(), "");
      defect.setResolvedUserId(resolvedByUserId);
      defect.setClosed(true);
      defect.setClosedUserId(closedByUserId);
      return defect;
   }

}
