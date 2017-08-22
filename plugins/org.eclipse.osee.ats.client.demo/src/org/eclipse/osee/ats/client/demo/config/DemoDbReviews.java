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
package org.eclipse.osee.ats.client.demo.config;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.IAtsDecisionReview;
import org.eclipse.osee.ats.api.review.Role;
import org.eclipse.osee.ats.api.review.UserRole;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.client.demo.DemoUtil;
import org.eclipse.osee.ats.client.demo.internal.Activator;
import org.eclipse.osee.ats.client.demo.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.review.DecisionReviewArtifact;
import org.eclipse.osee.ats.core.client.review.DecisionReviewManager;
import org.eclipse.osee.ats.core.client.review.DecisionReviewState;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewManager;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewState;
import org.eclipse.osee.ats.core.client.review.defect.ReviewDefectItem;
import org.eclipse.osee.ats.core.client.review.defect.ReviewDefectItem.Disposition;
import org.eclipse.osee.ats.core.client.review.defect.ReviewDefectItem.InjectionActivity;
import org.eclipse.osee.ats.core.client.review.defect.ReviewDefectItem.Severity;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.demo.api.DemoArtifactTypes;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class DemoDbReviews {

   public static void createReviews(boolean DEBUG) throws Exception {
      createPeerToPeerReviews(DEBUG);
      IAtsChangeSet changes = AtsClientService.get().createChangeSet("Populate Demo DB - Create Decision Reviews");
      createDecisionReviews(DEBUG, changes);
      changes.execute();
   }

   /**
    * Create Decision Reviews<br>
    * 1) ALREADY CREATED: Decision review created through the validation flag being set on a workflow<br>
    * 2) Decision in ReWork state w Joe Smith assignee and 2 reviewers<br>
    * 3) Decision in Complete state w Joe Smith assignee and completed<br>
    * <br>
    */
   public static void createDecisionReviews(boolean DEBUG, IAtsChangeSet changes) throws Exception {

      Date createdDate = new Date();
      IAtsUser createdBy = AtsClientService.get().getUserService().getCurrentUser();

      if (DEBUG) {
         OseeLog.log(Activator.class, Level.INFO, "Create Decision reviews");
      }
      TeamWorkFlowArtifact firstTestArt = getSampleReviewTestWorkflows().get(0);
      TeamWorkFlowArtifact secondTestArt = getSampleReviewTestWorkflows().get(1);

      // Create a Decision review and transition to ReWork
      IAtsDecisionReview review = AtsClientService.get().getReviewService().createValidateReview(firstTestArt, true,
         createdDate, createdBy, changes);
      Result result = DecisionReviewManager.transitionTo((DecisionReviewArtifact) review.getStoreObject(),
         DecisionReviewState.Followup, createdBy, false, changes);
      if (result.isFalse()) {
         throw new IllegalStateException("Failed transitioning review to Followup: " + result.getText());
      }
      changes.add(review);

      // Create a Decision review and transition to Completed
      review = AtsClientService.get().getReviewService().createValidateReview(secondTestArt, true, createdDate,
         createdBy, changes);
      DecisionReviewManager.transitionTo((DecisionReviewArtifact) review.getStoreObject(),
         DecisionReviewState.Completed, createdBy, false, changes);
      if (result.isFalse()) {
         throw new IllegalStateException("Failed transitioning review to Completed: " + result.getText());
      }
      changes.add(review);

   }

   private static List<TeamWorkFlowArtifact> reviewTestArts;

   private static List<TeamWorkFlowArtifact> getSampleReviewTestWorkflows() throws Exception {
      if (reviewTestArts == null) {
         reviewTestArts = new ArrayList<>();
         for (String actionName : new String[] {"Button W doesn't work on", "Diagram Tree"}) {
            for (Artifact art : ArtifactQuery.getArtifactListFromName(actionName, AtsClientService.get().getAtsBranch(),
               EXCLUDE_DELETED, QueryOption.CONTAINS_MATCH_OPTIONS)) {
               if (art.isOfType(DemoArtifactTypes.DemoTestTeamWorkflow)) {
                  reviewTestArts.add((TeamWorkFlowArtifact) art);
               }
            }
         }
      }
      return reviewTestArts;
   }

   /**
    * Create<br>
    * 1) PeerToPeer in Prepare state w Joe Smith assignee<br>
    * 2) PeerToPeer in Review state w Joe Smith assignee and 2 reviewers<br>
    * 3) PeerToPeer in Prepare state w Joe Smith assignee and completed<br>
    * <br>
    */
   public static void createPeerToPeerReviews(boolean DEBUG) throws Exception {

      IAtsChangeSet changes = AtsClientService.get().createChangeSet("Populate Demo DB - Create PeerToPeer Reviews 1");

      if (DEBUG) {
         OseeLog.log(Activator.class, Level.INFO, "Create Peer To Peer reviews");
      }
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

      PeerToPeerReviewManager.setPrepareStateData(false, reviewArt, roles, "here", 100, 2.5, changes);
      changes.execute();

      changes = AtsClientService.get().createChangeSet("Populate Demo DB - Create PeerToPeer Reviews 2");

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
