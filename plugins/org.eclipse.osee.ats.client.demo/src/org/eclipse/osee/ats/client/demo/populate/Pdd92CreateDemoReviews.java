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
package org.eclipse.osee.ats.client.demo.populate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.IAtsDecisionReview;
import org.eclipse.osee.ats.api.review.Role;
import org.eclipse.osee.ats.api.review.UserRole;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.client.demo.DemoUtil;
import org.eclipse.osee.ats.client.demo.internal.AtsClientService;
import org.eclipse.osee.ats.workflow.review.DecisionReviewArtifact;
import org.eclipse.osee.ats.workflow.review.DecisionReviewManager;
import org.eclipse.osee.ats.workflow.review.DecisionReviewState;
import org.eclipse.osee.ats.workflow.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.workflow.review.PeerToPeerReviewManager;
import org.eclipse.osee.ats.workflow.review.PeerToPeerReviewState;
import org.eclipse.osee.ats.workflow.review.ReviewDefectItem;
import org.eclipse.osee.ats.workflow.review.ReviewDefectItem.Disposition;
import org.eclipse.osee.ats.workflow.review.ReviewDefectItem.InjectionActivity;
import org.eclipse.osee.ats.workflow.review.ReviewDefectItem.Severity;
import org.eclipse.osee.ats.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.UserManager;

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
      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());

      Date createdDate = new Date();
      IAtsUser createdBy = AtsClientService.get().getUserService().getCurrentUser();

      // Create a Decision review and transition to ReWork
      IAtsDecisionReview review = AtsClientService.get().getReviewService().createValidateReview(
         DemoUtil.getButtonWDoesntWorkOnSituationPageWf(), true, createdDate, createdBy, changes);
      Result result = DecisionReviewManager.transitionTo((DecisionReviewArtifact) review.getStoreObject(),
         DecisionReviewState.Followup, createdBy, false, changes);
      if (result.isFalse()) {
         throw new IllegalStateException("Failed transitioning review to Followup: " + result.getText());
      }
      changes.add(review);

      // Create a Decision review and transition to Completed
      review = AtsClientService.get().getReviewService().createValidateReview(
         DemoUtil.getProblemInDiagramTree_TeamWfWf(), true, createdDate, createdBy, changes);
      DecisionReviewManager.transitionTo((DecisionReviewArtifact) review.getStoreObject(),
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

      IAtsChangeSet changes = AtsClientService.get().createChangeSet("Populate Demo DB - Create PeerToPeer Reviews 1");

      TeamWorkFlowArtifact firstCodeArt = DemoUtil.getSawCodeCommittedWf();
      TeamWorkFlowArtifact secondCodeArt = DemoUtil.getSawCodeUnCommittedWf();

      // Create a PeerToPeer review and leave in Prepare state
      PeerToPeerReviewArtifact reviewArt = PeerToPeerReviewManager.createNewPeerToPeerReview(firstCodeArt,
         "Peer Review first set of code changes", firstCodeArt.getStateMgr().getCurrentStateName(), changes);

      // Create a PeerToPeer review and transition to Review state
      reviewArt = PeerToPeerReviewManager.createNewPeerToPeerReview(firstCodeArt, "Peer Review algorithm used in code",
         firstCodeArt.getStateMgr().getCurrentStateName(), changes);
      changes.setSoleAttributeValue((ArtifactId) reviewArt, AtsAttributeTypes.CSCI, "csci");
      changes.setSoleAttributeValue((ArtifactId) reviewArt, AtsAttributeTypes.Description, "description");
      List<UserRole> roles = new ArrayList<>();
      roles.add(new UserRole(Role.Author,
         AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Joe_Smith)));
      roles.add(new UserRole(Role.Reviewer,
         AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Kay_Jones)));
      roles.add(new UserRole(Role.Reviewer,
         AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Alex_Kay), 2.0, true));
      Result result = PeerToPeerReviewManager.transitionTo(reviewArt, PeerToPeerReviewState.Review, roles, null,
         AtsClientService.get().getUserService().getCurrentUser(), false, changes);
      if (result.isFalse()) {
         throw new IllegalStateException("Failed transitioning review to Review: " + result.getText());
      }
      changes.add(reviewArt);

      // Create a PeerToPeer review and transition to Completed
      reviewArt = PeerToPeerReviewManager.createNewPeerToPeerReview(secondCodeArt, "Review new logic",
         secondCodeArt.getStateMgr().getCurrentStateName(), new Date(),
         AtsClientService.get().getUserServiceClient().getUserFromOseeUser(UserManager.getUser(DemoUsers.Kay_Jones)),
         changes);
      changes.setSoleAttributeValue((ArtifactId) reviewArt, AtsAttributeTypes.CSCI, "csci");
      changes.setSoleAttributeValue((ArtifactId) reviewArt, AtsAttributeTypes.Description, "description");
      roles = new ArrayList<>();
      roles.add(new UserRole(Role.Author,
         AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Kay_Jones), 2.3, true));
      roles.add(new UserRole(Role.Reviewer,
         AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Joe_Smith), 4.5, true));
      roles.add(new UserRole(Role.Reviewer,
         AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Alex_Kay), 2.0, true));

      List<ReviewDefectItem> defects = new ArrayList<>();
      defects.add(new ReviewDefectItem(
         AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Alex_Kay), Severity.Issue,
         Disposition.Accept, InjectionActivity.Code, "Problem with logic", "Fixed", "Line 234", new Date()));
      defects.add(
         new ReviewDefectItem(AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Alex_Kay),
            Severity.Issue, Disposition.Accept, InjectionActivity.Code, "Using getInteger instead", "Fixed",
            "MyWorld.java:Line 33", new Date()));
      defects.add(
         new ReviewDefectItem(AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Alex_Kay),
            Severity.Major, Disposition.Reject, InjectionActivity.Code, "Spelling incorrect", "Is correct",
            "MyWorld.java:Line 234", new Date()));
      defects.add(new ReviewDefectItem(
         AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Joe_Smith), Severity.Minor,
         Disposition.Reject, InjectionActivity.Code, "Remove unused code", "", "Here.java:Line 234", new Date()));
      defects.add(new ReviewDefectItem(
         AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Joe_Smith), Severity.Major,
         Disposition.Accept, InjectionActivity.Code, "Negate logic", "Fixed", "There.java:Line 234", new Date()));
      for (ReviewDefectItem defect : defects) {
         defect.setClosed(true);
      }
      changes.execute();

      changes = AtsClientService.get().createChangeSet("Populate Demo DB - Create PeerToPeer Reviews 2");
      PeerToPeerReviewManager.setPrepareStateData(false, reviewArt, roles, "here", 100, 2.5, changes);
      changes.execute();

      result = PeerToPeerReviewManager.transitionTo(reviewArt, PeerToPeerReviewState.Completed, roles, defects,
         AtsClientService.get().getUserService().getCurrentUser(), false, changes);
      if (result.isTrue()) {
         changes.add(reviewArt);
      }
      if (result.isFalse()) {
         throw new IllegalStateException("Failed transitioning review to Completed: " + result.getText());
      }

      changes.execute();
   }
}
