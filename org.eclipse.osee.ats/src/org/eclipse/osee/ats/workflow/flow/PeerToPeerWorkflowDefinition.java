/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow.flow;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.workflow.page.AtsCancelledWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsCompletedWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsDecisionDecisionWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsDecisionFollowupWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsDecisionPrepareWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsPeerPrepareWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsPeerReviewWorkPageDefinition;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;

/**
 * @author Donald G. Dunne
 */
public class PeerToPeerWorkflowDefinition extends WorkFlowDefinition {

   public static String ID = "osee.ats.peerToPeerReview";
   public static String PEER_REVIEW_COMPLETED_STATE_ID = ID + "." + DefaultTeamState.Completed.name();
   public static String PEER_REVIEW_CANCELLED_STATE_ID = ID + "." + DefaultTeamState.Cancelled.name();

   public PeerToPeerWorkflowDefinition() {
      this("Peer To Peer Workflow Definition", ID);
      startPageId = AtsPeerPrepareWorkPageDefinition.ID;
   }

   public PeerToPeerWorkflowDefinition(Artifact artifact) throws Exception {
      super(artifact);
      throw new IllegalStateException("This constructor should never be used.");
   }

   public static List<WorkItemDefinition> getAtsWorkDefinitions() {
      List<WorkItemDefinition> workItems = new ArrayList<WorkItemDefinition>();

      // Add PeerToPeer Pages and Workflow Definition
      workItems.add(new AtsPeerPrepareWorkPageDefinition());
      workItems.add(new AtsPeerReviewWorkPageDefinition());
      workItems.add(new WorkPageDefinition(DefaultTeamState.Completed.name(),
            PeerToPeerWorkflowDefinition.PEER_REVIEW_COMPLETED_STATE_ID, AtsCompletedWorkPageDefinition.ID));
      workItems.add(new WorkPageDefinition(DefaultTeamState.Cancelled.name(),
            PeerToPeerWorkflowDefinition.PEER_REVIEW_CANCELLED_STATE_ID, AtsCancelledWorkPageDefinition.ID));
      workItems.add(new PeerToPeerWorkflowDefinition());

      // Add Decision Pages and Workflow Definition
      workItems.add(new AtsDecisionPrepareWorkPageDefinition());
      workItems.add(new AtsDecisionDecisionWorkPageDefinition());
      workItems.add(new AtsDecisionFollowupWorkPageDefinition());
      workItems.add(new WorkPageDefinition(DefaultTeamState.Completed.name(),
            DecisionWorkflowDefinition.DECISION_COMPLETED_STATE_ID, AtsCompletedWorkPageDefinition.ID));
      workItems.add(new WorkPageDefinition(DefaultTeamState.Cancelled.name(),
            DecisionWorkflowDefinition.DECISION_CANCELLED_STATE_ID, AtsCancelledWorkPageDefinition.ID));
      workItems.add(new DecisionWorkflowDefinition());

      return workItems;
   }

   /**
    * @param name
    * @param id
    * @param parentId
    */
   public PeerToPeerWorkflowDefinition(String name, String id) {
      super(name, id, null);
      // Add default transitions
      addPageTransition(AtsPeerPrepareWorkPageDefinition.ID, AtsPeerReviewWorkPageDefinition.ID,
            TransitionType.ToPageAsDefault);
      addPageTransition(AtsPeerReviewWorkPageDefinition.ID, PEER_REVIEW_COMPLETED_STATE_ID,
            TransitionType.ToPageAsDefault);

      // Add return transitions
      addPageTransition(PEER_REVIEW_COMPLETED_STATE_ID, AtsPeerReviewWorkPageDefinition.ID,
            TransitionType.ToPageAsReturn);
      addPageTransition(AtsPeerReviewWorkPageDefinition.ID, AtsPeerPrepareWorkPageDefinition.ID,
            TransitionType.ToPageAsReturn);
      addPageTransition(PEER_REVIEW_CANCELLED_STATE_ID, AtsPeerReviewWorkPageDefinition.ID,
            TransitionType.ToPageAsReturn);
      addPageTransition(PEER_REVIEW_CANCELLED_STATE_ID, AtsPeerPrepareWorkPageDefinition.ID,
            TransitionType.ToPageAsReturn);

      // Add cancelled transitions
      addPageTransitionToPageAndReturn(AtsPeerPrepareWorkPageDefinition.ID, PEER_REVIEW_CANCELLED_STATE_ID);
      addPageTransitionToPageAndReturn(AtsPeerReviewWorkPageDefinition.ID, PEER_REVIEW_CANCELLED_STATE_ID);
   }

}
