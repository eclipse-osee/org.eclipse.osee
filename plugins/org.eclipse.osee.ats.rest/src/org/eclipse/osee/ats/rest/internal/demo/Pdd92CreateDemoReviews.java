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

package org.eclipse.osee.ats.rest.internal.demo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.review.DecisionReviewState;
import org.eclipse.osee.ats.api.review.IAtsDecisionReview;
import org.eclipse.osee.ats.api.review.IAtsPeerReviewRoleManager;
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
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Donald G. Dunne
 */
public class Pdd92CreateDemoReviews extends AbstractPopulateDemoDatabase {

   private ArtifactToken reviewArt3Tok;

   public Pdd92CreateDemoReviews(XResultData rd, AtsApi atsApi) {
      super(rd, atsApi);
   }

   @Override
   public void run() {
      rd.logf("Running [%s]...\n", getClass().getSimpleName());
      createPeerToPeerReviews1and2();
      createPeerToPeerReview3();
      createPeerToPeerReview3Roles();
      createPeerToPeerReview3Review();
      createPeerToPeerReview3Defects();
      createPeerToPeerReview3Complete();
      createDecisionReviews();
   }

   /**
    * Create Decision Reviews<br>
    * 1) ALREADY CREATED: Decision review created through the validation flag being set on a workflow<br>
    * 2) Decision in ReWork state w Joe Smith assignee and 2 reviewers<br>
    * 3) Decision in Complete state w Joe Smith assignee and completed<br>
    * <br>
    */
   public void createDecisionReviews() {
      IAtsChangeSet changes = atsApi.createChangeSet(getClass().getSimpleName());

      Date createdDate = new Date();
      AtsUser createdBy = atsApi.getUserService().getCurrentUser();

      // Create a Decision review and transition to ReWork
      IAtsDecisionReview review = atsApi.getReviewService().createValidateReview(
         DemoUtil.getButtonWDoesntWorkOnSituationPageWf(), true, createdDate, createdBy, changes);
      Result result = atsApi.getReviewService().transitionDecisionTo(review, DecisionReviewState.Followup, createdBy,
         false, changes);
      if (result.isFalse()) {
         throw new IllegalStateException("Failed transitioning review to Followup: " + result.getText());
      }
      changes.add(review);

      // Create a Decision review and transition to Completed
      review = atsApi.getReviewService().createValidateReview(DemoUtil.getProblemInTree_TeamWfWf(), true, createdDate,
         createdBy, changes);
      atsApi.getReviewService().transitionDecisionTo(review, DecisionReviewState.Completed, createdBy, false, changes);
      if (result.isFalse()) {
         throw new IllegalStateException("Failed transitioning review to Completed: " + result.getText());
      }
      changes.add(review);

      changes.execute();
   }

   /**
    * Create<br>
    * 1) PeerToPeer in Prepare state w UnAssigned assignee<br>
    * 2) PeerToPeer in Review state w Joe Smith assignee and 2 reviewers<br>
    * 3) PeerToPeer in Prepare state w Smith assignee and completed<br>
    * <br>
    */
   public void createPeerToPeerReviews1and2() {

      IAtsChangeSet changes = atsApi.createChangeSet("Populate Demo DB - PeerToPeer 1and2");

      IAtsTeamWorkflow firstCodeArt = DemoUtil.getSawCodeCommittedWf();

      // Create PeerToPeer review 1 and leave in Prepare state
      atsApi.getReviewService().createNewPeerToPeerReview(firstCodeArt, "1 - Peer Review first set of code changes",
         firstCodeArt.getCurrentStateName(), changes);

      // Create PeerToPeer review 2 and transition to Review state
      IAtsPeerToPeerReview peerRev = atsApi.getReviewService().createNewPeerToPeerReview(firstCodeArt,
         DemoArtifactToken.PeerReview2.getName(), firstCodeArt.getCurrentStateName(), changes);
      changes.setSoleAttributeValue(peerRev.getStoreObject(), AtsAttributeTypes.Description, "description");

      Result result1 = atsApi.getReviewService().transitionTo(peerRev, PeerToPeerReviewState.Review,
         Collections.emptyList(), null, atsApi.getUserService().getCurrentUser(), false, changes);

      Conditions.assertTrue(result1.getText().contains("minimum of 1 [Author]"), result1.getText());

      List<UserRole> roles = new ArrayList<>();
      roles.add(new UserRole(ReviewRole.Author, DemoUsers.Joe_Smith));
      roles.add(new UserRole(ReviewRole.Reviewer, DemoUsers.Kay_Jones));
      roles.add(new UserRole(ReviewRole.Reviewer, DemoUsers.Alex_Kay, 2.0, true));

      Result result2 = atsApi.getReviewService().transitionTo(peerRev, PeerToPeerReviewState.Review, roles, null,
         atsApi.getUserService().getCurrentUser(), false, changes);
      if (result2.isFalse()) {
         throw new IllegalStateException("Failed transitioning review to Review: " + result2.getText());
      }
      changes.add(peerRev);
      changes.execute();
   }

   public void createPeerToPeerReview3() {
      IAtsChangeSet changes = atsApi.createChangeSet("Populate Demo DB - PeerToPeer 3.1");
      IAtsTeamWorkflow secondCodeArt = DemoUtil.getSawCodeUnCommittedWf();

      IAtsPeerToPeerReview reviewArt3 = atsApi.getReviewService().createNewPeerToPeerReview(secondCodeArt,
         "3 - Review new logic", secondCodeArt.getCurrentStateName(), new Date(),
         atsApi.getUserService().getUserById(DemoUsers.Kay_Jones), changes);
      changes.setSoleAttributeValue(reviewArt3.getStoreObject(), AtsAttributeTypes.Description, "description");
      changes.execute();
      this.reviewArt3Tok = reviewArt3.getArtifactToken();
   }

   public void createPeerToPeerReview3Roles() {

      IAtsPeerToPeerReview reviewArt3 = (IAtsPeerToPeerReview) atsApi.getReviewService().getReview(reviewArt3Tok);

      // reviewArt3 - Add Roles
      IAtsChangeSet changes = atsApi.createChangeSet("Populate Demo DB - PeerToPeer 3.2");

      List<UserRole> roles = new ArrayList<>();
      roles.add(new UserRole(ReviewRole.Author, DemoUsers.Kay_Jones, 2.3, false));
      roles.add(new UserRole(ReviewRole.Reviewer, DemoUsers.Joe_Smith, 4.5, false));
      roles.add(new UserRole(ReviewRole.Reviewer, DemoUsers.Alex_Kay, 2.0, false));
      atsApi.getReviewService().setPrepareStateData(false, reviewArt3, roles, "review materials", 100, 2.5, changes);
      changes.execute();
   }

   public void createPeerToPeerReview3Review() {

      IAtsPeerToPeerReview reviewArt3 = (IAtsPeerToPeerReview) atsApi.getReviewService().getReview(reviewArt3Tok);

      reviewArt3 = (IAtsPeerToPeerReview) atsApi.getReviewService().getReview(reviewArt3.getArtifactToken());
      IAtsChangeSet changes = atsApi.createChangeSet("Populate Demo DB - PeerToPeer 3.3");

      reviewArt3 = (IAtsPeerToPeerReview) atsApi.getWorkItemService().getWorkItem(reviewArt3.getId());
      // reviewArt3 - Transition to Completed
      Result result = atsApi.getReviewService().transitionTo(reviewArt3, PeerToPeerReviewState.Review, null, null,
         atsApi.getUserService().getCurrentUser(), false, changes);
      if (result.isTrue()) {
         changes.add(reviewArt3);
      }
      if (result.isFalse()) {
         throw new IllegalStateException("Failed transitioning review to Review: " + result.getText());
      }
      changes.execute();
   }

   public void createPeerToPeerReview3Defects() {

      IAtsPeerToPeerReview reviewArt3 = (IAtsPeerToPeerReview) atsApi.getReviewService().getReview(reviewArt3Tok);

      IAtsChangeSet changes = atsApi.createChangeSet("Populate Demo DB - PeerToPeer 3.4");

      // Add defects
      List<ReviewDefectItem> defects = new ArrayList<>();
      defects.add(new ReviewDefectItem(DemoUsers.Alex_Kay, Severity.Issue, Disposition.Accept, InjectionActivity.Code,
         "Problem with logic", "Fixed", "Line 234", new Date(), ""));
      defects.add(new ReviewDefectItem(DemoUsers.Alex_Kay, Severity.Issue, Disposition.Accept, InjectionActivity.Code,
         "Using getInteger instead", "Fixed", "MyWorld.java:Line 33", new Date(), ""));
      defects.add(new ReviewDefectItem(DemoUsers.Alex_Kay, Severity.Major, Disposition.Reject, InjectionActivity.Code,
         "Spelling incorrect", "Is correct", "MyWorld.java:Line 234", new Date(), ""));
      defects.add(new ReviewDefectItem(DemoUsers.Joe_Smith, Severity.Minor, Disposition.Reject, InjectionActivity.Code,
         "Remove unused code", "", "Here.java:Line 234", new Date(), ""));
      defects.add(new ReviewDefectItem(DemoUsers.Joe_Smith, Severity.Major, Disposition.Accept, InjectionActivity.Code,
         "Negate logic", "Fixed", "There.java:Line 234", new Date(), ""));
      for (ReviewDefectItem defect : defects) {
         defect.setClosed(true);
         defect.setClosedUserId(atsApi.getUserService().getCurrentUserId());
      }

      // reviewArt3 = Complete roles
      IAtsPeerReviewRoleManager roleManager = reviewArt3.getRoleManager();
      for (UserRole role : roleManager.getUserRoles()) {
         role.setCompleted(true);
      }
      roleManager.saveToArtifact(changes);
      changes.execute();
   }

   public void createPeerToPeerReview3Complete() {
      IAtsPeerToPeerReview reviewArt3 = (IAtsPeerToPeerReview) atsApi.getReviewService().getReview(reviewArt3Tok);

      IAtsChangeSet changes = atsApi.createChangeSet("Populate Demo DB - PeerToPeer 3.5");

      // reviewArt3 - Transition to Completed
      Result result = atsApi.getReviewService().transitionTo(reviewArt3, PeerToPeerReviewState.Completed, null, null,
         atsApi.getUserService().getCurrentUser(), false, changes, TransitionOption.OverrideAssigneeCheck,
         TransitionOption.OverrideTransitionValidityCheck);
      if (result.isTrue()) {
         changes.add(reviewArt3);
      }
      if (result.isFalse()) {
         throw new IllegalStateException("Failed transitioning review to Completed: " + result.getText());
      }
      changes.execute();
   }
}
