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

   public void run() {
      createPeerToPeerReviews();
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
    * 1) PeerToPeer in Prepare state w Joe Smith assignee<br>
    * 2) PeerToPeer in Review state w Joe Smith assignee and 2 reviewers<br>
    * 3) PeerToPeer in Prepare state w Joe Smith assignee and completed<br>
    * <br>
    */
   public void createPeerToPeerReviews() {

      IAtsChangeSet changes = AtsApiService.get().createChangeSet("Populate Demo DB - Create PeerToPeer Reviews 1");

      TeamWorkFlowArtifact firstCodeArt = DemoUtil.getSawCodeCommittedWf();
      TeamWorkFlowArtifact secondCodeArt = DemoUtil.getSawCodeUnCommittedWf();

      // Create a PeerToPeer review and leave in Prepare state
      PeerToPeerReviewArtifact reviewArt =
         (PeerToPeerReviewArtifact) AtsApiService.get().getReviewService().createNewPeerToPeerReview(firstCodeArt,
            "Peer Review first set of code changes", firstCodeArt.getCurrentStateName(), changes);

      // Create a PeerToPeer review and transition to Review state
      reviewArt =
         (PeerToPeerReviewArtifact) AtsApiService.get().getReviewService().createNewPeerToPeerReview(firstCodeArt,
            "Peer Review algorithm used in code", firstCodeArt.getCurrentStateName(), changes);
      changes.setSoleAttributeValue((ArtifactId) reviewArt, AtsAttributeTypes.Description, "description");
      List<UserRole> roles = new ArrayList<>();
      roles.add(new UserRole(ReviewRole.Author, DemoUsers.Joe_Smith));
      roles.add(new UserRole(ReviewRole.Reviewer, DemoUsers.Kay_Jones));
      roles.add(new UserRole(ReviewRole.Reviewer, DemoUsers.Alex_Kay, 2.0, true));
      Result result = AtsApiService.get().getReviewService().transitionTo(reviewArt, PeerToPeerReviewState.Review,
         roles, null, AtsApiService.get().getUserService().getCurrentUser(), false, changes);
      if (result.isFalse()) {
         throw new IllegalStateException("Failed transitioning review to Review: " + result.getText());
      }
      changes.add(reviewArt);

      // Create a PeerToPeer review and transition to Completed
      reviewArt =
         (PeerToPeerReviewArtifact) AtsApiService.get().getReviewService().createNewPeerToPeerReview(secondCodeArt,
            "Review new logic", secondCodeArt.getCurrentStateName(), new Date(),
            AtsApiService.get().getUserService().getUserById(DemoUsers.Kay_Jones), changes);
      changes.setSoleAttributeValue((ArtifactId) reviewArt, AtsAttributeTypes.Description, "description");
      roles = new ArrayList<>();
      roles.add(new UserRole(ReviewRole.Author, DemoUsers.Kay_Jones, 2.3, true));
      roles.add(new UserRole(ReviewRole.Reviewer, DemoUsers.Joe_Smith, 4.5, true));
      roles.add(new UserRole(ReviewRole.Reviewer, DemoUsers.Alex_Kay, 2.0, true));

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
      changes.execute();

      changes = AtsApiService.get().createChangeSet("Populate Demo DB - Create PeerToPeer Reviews 2");
      AtsApiService.get().getReviewService().setPrepareStateData(false, reviewArt, roles, "here", 100, 2.5, changes);
      changes.execute();

      result = AtsApiService.get().getReviewService().transitionTo(reviewArt, PeerToPeerReviewState.Completed, roles,
         defects, AtsApiService.get().getUserService().getCurrentUser(), false, changes);
      if (result.isTrue()) {
         changes.add(reviewArt);
      }
      if (result.isFalse()) {
         throw new IllegalStateException("Failed transitioning review to Completed: " + result.getText());
      }

      changes.execute();
   }
}
