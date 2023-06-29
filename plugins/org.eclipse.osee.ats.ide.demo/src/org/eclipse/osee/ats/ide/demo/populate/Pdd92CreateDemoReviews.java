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

package org.eclipse.osee.ats.ide.demo.populate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.DecisionReviewState;
import org.eclipse.osee.ats.api.review.IAtsDecisionReview;
import org.eclipse.osee.ats.api.review.IAtsPeerReviewRoleManager;
import org.eclipse.osee.ats.api.review.PeerToPeerReviewState;
import org.eclipse.osee.ats.api.review.ReviewDefectItem;
import org.eclipse.osee.ats.api.review.ReviewDefectItem.Disposition;
import org.eclipse.osee.ats.api.review.ReviewDefectItem.InjectionActivity;
import org.eclipse.osee.ats.api.review.ReviewDefectItem.Severity;
import org.eclipse.osee.ats.api.review.ReviewRole;
import org.eclipse.osee.ats.api.review.UserRole;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.ide.demo.DemoUtil;
import org.eclipse.osee.ats.ide.demo.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.review.DecisionReviewArtifact;
import org.eclipse.osee.ats.ide.workflow.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.util.Result;

/**
 * @author Donald G. Dunne
 */
public class Pdd92CreateDemoReviews {

   private PeerToPeerReviewArtifact reviewArt3;
   public void run() {
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
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());

      Date createdDate = new Date();
      AtsUser createdBy = AtsApiService.get().getUserService().getCurrentUser();

      // Create a Decision review and transition to ReWork
      IAtsDecisionReview review = AtsApiService.get().getReviewService().createValidateReview(
         DemoUtil.getButtonWDoesntWorkOnSituationPageWf(), true, createdDate, createdBy, changes);
      Result result = AtsApiService.get().getReviewService().transitionDecisionTo(
         (DecisionReviewArtifact) review.getStoreObject(), DecisionReviewState.Followup, createdBy, false, changes);
      if (result.isFalse()) {
         throw new IllegalStateException("Failed transitioning review to Followup: " + result.getText());
      }
      changes.add(review);

      // Create a Decision review and transition to Completed
      review = AtsApiService.get().getReviewService().createValidateReview(DemoUtil.getProblemInDiagramTree_TeamWfWf(),
         true, createdDate, createdBy, changes);
      AtsApiService.get().getReviewService().transitionDecisionTo((DecisionReviewArtifact) review.getStoreObject(),
         DecisionReviewState.Completed, createdBy, false, changes);
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

      IAtsChangeSet changes = AtsApiService.get().createChangeSet("Populate Demo DB - PeerToPeer 1and2");

      TeamWorkFlowArtifact firstCodeArt = DemoUtil.getSawCodeCommittedWf();

      // Create PeerToPeer review 1 and leave in Prepare state
      AtsApiService.get().getReviewService().createNewPeerToPeerReview(firstCodeArt,
         "1 - Peer Review first set of code changes", firstCodeArt.getCurrentStateName(), changes);

      // Create PeerToPeer review 2 and transition to Review state
      PeerToPeerReviewArtifact reviewArt2 =
         (PeerToPeerReviewArtifact) AtsApiService.get().getReviewService().createNewPeerToPeerReview(firstCodeArt,
            "2 - Peer Review algorithm used in code", firstCodeArt.getCurrentStateName(), changes);
      changes.setSoleAttributeValue((ArtifactId) reviewArt2, AtsAttributeTypes.Description, "description");
      List<UserRole> roles = new ArrayList<>();
      roles.add(new UserRole(ReviewRole.Author, DemoUsers.Joe_Smith));
      roles.add(new UserRole(ReviewRole.Reviewer, DemoUsers.Kay_Jones));
      roles.add(new UserRole(ReviewRole.Reviewer, DemoUsers.Alex_Kay, 2.0, true));
      Result result = AtsApiService.get().getReviewService().transitionTo(reviewArt2, PeerToPeerReviewState.Review,
         roles, null, AtsApiService.get().getUserService().getCurrentUser(), false, changes);
      if (result.isFalse()) {
         throw new IllegalStateException("Failed transitioning review to Review: " + result.getText());
      }
      changes.add(reviewArt2);
      changes.execute();
   }

   public void createPeerToPeerReview3() {
      IAtsChangeSet changes = AtsApiService.get().createChangeSet("Populate Demo DB - PeerToPeer 3.1");
      TeamWorkFlowArtifact secondCodeArt = DemoUtil.getSawCodeUnCommittedWf();

      reviewArt3 =
         (PeerToPeerReviewArtifact) AtsApiService.get().getReviewService().createNewPeerToPeerReview(secondCodeArt,
            "3 - Review new logic", secondCodeArt.getCurrentStateName(), new Date(),
            AtsApiService.get().getUserService().getUserById(DemoUsers.Kay_Jones), changes);
      changes.setSoleAttributeValue((ArtifactId) reviewArt3, AtsAttributeTypes.Description, "description");
      changes.execute();
   }

   public void createPeerToPeerReview3Roles() {
      // reviewArt3 - Add Roles
      IAtsChangeSet changes = AtsApiService.get().createChangeSet("Populate Demo DB - PeerToPeer 3.2");

      List<UserRole> roles = new ArrayList<>();
      roles.add(new UserRole(ReviewRole.Author, DemoUsers.Kay_Jones, 2.3, false));
      roles.add(new UserRole(ReviewRole.Reviewer, DemoUsers.Joe_Smith, 4.5, false));
      roles.add(new UserRole(ReviewRole.Reviewer, DemoUsers.Alex_Kay, 2.0, false));
      AtsApiService.get().getReviewService().setPrepareStateData(false, reviewArt3, roles, "here", 100, 2.5, changes);
      changes.execute();
   }

   public void createPeerToPeerReview3Review() {
      IAtsChangeSet changes = AtsApiService.get().createChangeSet("Populate Demo DB - PeerToPeer 3.3");

      // reviewArt3 - Transition to Completed
      Result result = AtsApiService.get().getReviewService().transitionTo(reviewArt3, PeerToPeerReviewState.Review,
         null, null, AtsApiService.get().getUserService().getCurrentUser(), false, changes);
      if (result.isTrue()) {
         changes.add(reviewArt3);
      }
      if (result.isFalse()) {
         throw new IllegalStateException("Failed transitioning review to Review: " + result.getText());
      }
      changes.execute();
   }

   public void createPeerToPeerReview3Defects() {
      IAtsChangeSet changes = AtsApiService.get().createChangeSet("Populate Demo DB - PeerToPeer 3.4");

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
         defect.setClosedUserId(AtsApiService.get().getUserService().getCurrentUserId());
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
      IAtsChangeSet changes = AtsApiService.get().createChangeSet("Populate Demo DB - PeerToPeer 3.5");

      // reviewArt3 - Transition to Completed
      Result result = AtsApiService.get().getReviewService().transitionTo(reviewArt3, PeerToPeerReviewState.Completed,
         null, null, AtsApiService.get().getUserService().getCurrentUser(), false, changes);
      if (result.isTrue()) {
         changes.add(reviewArt3);
      }
      if (result.isFalse()) {
         throw new IllegalStateException("Failed transitioning review to Completed: " + result.getText());
      }
      changes.execute();
   }
}
