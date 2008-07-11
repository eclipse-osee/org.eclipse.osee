/*
 * Created on May 15, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.config.demo.config;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.DecisionReviewArtifact;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.config.demo.OseeAtsConfigDemoPlugin;
import org.eclipse.osee.ats.config.demo.artifact.DemoTestTeamWorkflowArtifact;
import org.eclipse.osee.ats.config.demo.util.DemoUsers;
import org.eclipse.osee.ats.util.DefaultDecisionReviewWorkflowManager;
import org.eclipse.osee.ats.util.DefaultPeerToPeerReviewWorkflowManager;
import org.eclipse.osee.ats.util.widgets.defect.DefectItem;
import org.eclipse.osee.ats.util.widgets.defect.DefectItem.Disposition;
import org.eclipse.osee.ats.util.widgets.defect.DefectItem.InjectionActivity;
import org.eclipse.osee.ats.util.widgets.defect.DefectItem.Severity;
import org.eclipse.osee.ats.util.widgets.role.UserRole;
import org.eclipse.osee.ats.util.widgets.role.UserRole.Role;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Donald G. Dunne
 */
public class DemoDbReviews {

   public static void createReviews() throws Exception {
      createPeerToPeerReviews();
      createDecisionReviews();
   }

   /**
    * Create Decision Reviews<br>
    * 1) ALREADY CREATED: Decision review created through the validation flag being set on a workflow<br>
    * 2) Decision in ReWork state w Joe Smith assignee and 2 reviewers<br>
    * 3) Decision in Complete state w Joe Smith assignee and completed<br>
    * <br>
    * 
    * @param codeWorkflows
    * @throws Exception
    */
   public static void createDecisionReviews() throws Exception {

      OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Create Decision reviews", false);
      TeamWorkFlowArtifact firstTestArt = getSampleReviewTestWorkflows().get(0);
      TeamWorkFlowArtifact secondTestArt = getSampleReviewTestWorkflows().get(1);

      // Create a Decision review and transition to ReWork
      DecisionReviewArtifact reviewArt = firstTestArt.getSmaMgr().getReviewManager().createValidateReview(true);
      Result result =
            DefaultDecisionReviewWorkflowManager.transitionTo(reviewArt, DecisionReviewArtifact.StateNames.Followup,
                  SkynetAuthentication.getUser(), false);
      if (result.isFalse()) {
         throw new IllegalStateException("Failed transitioning review to Followup: " + result.getText());
      }
      reviewArt.persistAttributesAndRelations();

      // Create a Decision review and transition to Completed
      reviewArt = secondTestArt.getSmaMgr().getReviewManager().createValidateReview(true);
      DefaultDecisionReviewWorkflowManager.transitionTo(reviewArt, DecisionReviewArtifact.StateNames.Completed,
            SkynetAuthentication.getUser(), false);
      if (result.isFalse()) {
         throw new IllegalStateException("Failed transitioning review to Completed: " + result.getText());
      }
      reviewArt.persistAttributesAndRelations();

   }

   private static List<DemoTestTeamWorkflowArtifact> reviewTestArts;

   private static List<DemoTestTeamWorkflowArtifact> getSampleReviewTestWorkflows() throws Exception {
      if (reviewTestArts == null) {
         reviewTestArts = new ArrayList<DemoTestTeamWorkflowArtifact>();
         for (String actionName : new String[] {"Button W doesn't work on%", "%Diagram Tree"}) {
            DemoTestTeamWorkflowArtifact testArt = null;
            for (Artifact art : ArtifactQuery.getArtifactsFromName(actionName, AtsPlugin.getAtsBranch())) {
               if (art instanceof DemoTestTeamWorkflowArtifact) {
                  testArt = (DemoTestTeamWorkflowArtifact) art;
                  reviewTestArts.add(testArt);
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
    * 
    * @param codeWorkflows
    * @throws Exception
    */
   public static void createPeerToPeerReviews() throws Exception {

      OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Create Peer To Peer reviews", false);
      TeamWorkFlowArtifact firstCodeArt = DemoDbUtil.getSampleCodeWorkflows().get(0);
      TeamWorkFlowArtifact secondCodeArt = DemoDbUtil.getSampleCodeWorkflows().get(1);

      // Create a PeerToPeer review and leave in Prepare state
      PeerToPeerReviewArtifact reviewArt =
            firstCodeArt.getSmaMgr().getReviewManager().createNewPeerToPeerReview(
                  "Peer Review first set of code changes", firstCodeArt.getSmaMgr().getStateMgr().getCurrentStateName());
      reviewArt.persistAttributesAndRelations();

      // Create a PeerToPeer review and transition to Review state
      reviewArt =
            firstCodeArt.getSmaMgr().getReviewManager().createNewPeerToPeerReview("Peer Review algorithm used in code",
                  firstCodeArt.getSmaMgr().getStateMgr().getCurrentStateName());
      List<UserRole> roles = new ArrayList<UserRole>();
      roles.add(new UserRole(Role.Author, DemoUsers.getDemoUser(DemoUsers.Joe_Smith)));
      roles.add(new UserRole(Role.Reviewer, DemoUsers.getDemoUser(DemoUsers.Kay_Jones)));
      roles.add(new UserRole(Role.Reviewer, DemoUsers.getDemoUser(DemoUsers.Alex_Kay), 2.0, true));
      Result result =
            DefaultPeerToPeerReviewWorkflowManager.transitionTo(reviewArt, PeerToPeerReviewArtifact.State.Review,
                  roles, null, SkynetAuthentication.getUser(), false);
      if (result.isFalse()) {
         throw new IllegalStateException("Failed transitioning review to Review: " + result.getText());
      }
      reviewArt.persistAttributesAndRelations();

      // Create a PeerToPeer review and transition to Completed
      reviewArt =
            secondCodeArt.getSmaMgr().getReviewManager().createNewPeerToPeerReview("Review new logic",
                  firstCodeArt.getSmaMgr().getStateMgr().getCurrentStateName(),
                  DemoUsers.getDemoUser(DemoUsers.Kay_Jones), new Date());
      roles = new ArrayList<UserRole>();
      roles.add(new UserRole(Role.Author, DemoUsers.getDemoUser(DemoUsers.Kay_Jones), 2.3, true));
      roles.add(new UserRole(Role.Reviewer, DemoUsers.getDemoUser(DemoUsers.Joe_Smith), 4.5, true));
      roles.add(new UserRole(Role.Reviewer, DemoUsers.getDemoUser(DemoUsers.Alex_Kay), 2.0, true));

      List<DefectItem> defects = new ArrayList<DefectItem>();
      defects.add(new DefectItem(DemoUsers.getDemoUser(DemoUsers.Alex_Kay), Severity.Issue, Disposition.Accept,
            InjectionActivity.Code, "Problem with logic", "Fixed", "Line 234", new Date()));
      defects.add(new DefectItem(DemoUsers.getDemoUser(DemoUsers.Alex_Kay), Severity.Issue, Disposition.Accept,
            InjectionActivity.Code, "Using getInteger instead", "Fixed", "MyWorld.java:Line 33", new Date()));
      defects.add(new DefectItem(DemoUsers.getDemoUser(DemoUsers.Alex_Kay), Severity.Major, Disposition.Reject,
            InjectionActivity.Code, "Spelling incorrect", "Is correct", "MyWorld.java:Line 234", new Date()));
      defects.add(new DefectItem(DemoUsers.getDemoUser(DemoUsers.Joe_Smith), Severity.Minor, Disposition.Reject,
            InjectionActivity.Code, "Remove unused code", "", "Here.java:Line 234", new Date()));
      defects.add(new DefectItem(DemoUsers.getDemoUser(DemoUsers.Joe_Smith), Severity.Major, Disposition.Accept,
            InjectionActivity.Code, "Negate logic", "Fixed", "There.java:Line 234", new Date()));
      result =
            DefaultPeerToPeerReviewWorkflowManager.transitionTo(reviewArt, PeerToPeerReviewArtifact.State.Completed,
                  roles, defects, SkynetAuthentication.getUser(), false);
      reviewArt.persistAttributesAndRelations();
      if (result.isFalse()) {
         throw new IllegalStateException("Failed transitioning review to Completed: " + result.getText());
      }
   }
}
