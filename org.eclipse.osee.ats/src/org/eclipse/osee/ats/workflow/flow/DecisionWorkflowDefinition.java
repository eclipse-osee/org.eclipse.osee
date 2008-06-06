/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow.flow;

import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.workflow.page.AtsDecisionDecisionWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsDecisionFollowupWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsDecisionPrepareWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsPeerPrepareWorkPageDefinition;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;

/**
 * @author Donald G. Dunne
 */
public class DecisionWorkflowDefinition extends WorkFlowDefinition {

   public static String ID = "osee.ats.decisionReview";
   public static String DECISION_COMPLETED_STATE_ID = ID + "." + DefaultTeamState.Completed.name();
   public static String DECISION_CANCELLED_STATE_ID = ID + "." + DefaultTeamState.Cancelled.name();

   public DecisionWorkflowDefinition() {
      this("Decision Workflow Definition", ID);
      startPageId = AtsDecisionPrepareWorkPageDefinition.ID;
   }

   public DecisionWorkflowDefinition(Artifact artifact) throws Exception {
      super(artifact);
      throw new IllegalStateException("This constructor should never be used.");
   }

   /**
    * @param name
    * @param id
    * @param parentId
    */
   public DecisionWorkflowDefinition(String name, String id) {
      super(name, id, null);
      // Add Prepare Transitions
      addPageTransition(AtsDecisionPrepareWorkPageDefinition.ID, AtsDecisionDecisionWorkPageDefinition.ID,
            TransitionType.ToPageAsDefault);
      addPageTransitionToPageAndReturn(AtsPeerPrepareWorkPageDefinition.ID, DECISION_CANCELLED_STATE_ID);

      // Add Decision Transitions
      addPageTransition(AtsDecisionDecisionWorkPageDefinition.ID, DECISION_COMPLETED_STATE_ID,
            TransitionType.ToPageAsDefault);
      addPageTransition(AtsDecisionDecisionWorkPageDefinition.ID, AtsDecisionFollowupWorkPageDefinition.ID,
            TransitionType.ToPage);
      addPageTransition(AtsDecisionDecisionWorkPageDefinition.ID, AtsDecisionPrepareWorkPageDefinition.ID,
            TransitionType.ToPageAsReturn);

      // Add Followup Transitions
      addPageTransition(AtsDecisionFollowupWorkPageDefinition.ID, AtsDecisionDecisionWorkPageDefinition.ID,
            TransitionType.ToPageAsReturn);
      addPageTransition(AtsDecisionFollowupWorkPageDefinition.ID, DECISION_COMPLETED_STATE_ID,
            TransitionType.ToPageAsReturn);
      addPageTransitionToPageAndReturn(AtsDecisionFollowupWorkPageDefinition.ID, DECISION_CANCELLED_STATE_ID);

      // Add Completed Transitions
      addPageTransition(DECISION_COMPLETED_STATE_ID, AtsDecisionDecisionWorkPageDefinition.ID,
            TransitionType.ToPageAsReturn);
      addPageTransition(DECISION_COMPLETED_STATE_ID, AtsDecisionFollowupWorkPageDefinition.ID,
            TransitionType.ToPageAsReturn);

      // Add Cancelled Transitions
      addPageTransition(DECISION_CANCELLED_STATE_ID, AtsDecisionPrepareWorkPageDefinition.ID,
            TransitionType.ToPageAsReturn);
      addPageTransition(DECISION_CANCELLED_STATE_ID, AtsDecisionDecisionWorkPageDefinition.ID,
            TransitionType.ToPageAsReturn);
      addPageTransition(DECISION_CANCELLED_STATE_ID, AtsDecisionFollowupWorkPageDefinition.ID,
            TransitionType.ToPageAsReturn);

   }

}
