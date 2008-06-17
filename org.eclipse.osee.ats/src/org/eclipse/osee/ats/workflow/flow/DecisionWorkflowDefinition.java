/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow.flow;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.ats.workflow.page.AtsCancelledWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsCompletedWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsDecisionDecisionWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsDecisionFollowupWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsDecisionPrepareWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsPeerPrepareWorkPageDefinition;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;

/**
 * @author Donald G. Dunne
 */
public class DecisionWorkflowDefinition extends WorkFlowDefinition {

   public static String ID = "osee.ats.decisionReview";
   public static String DECISION_COMPLETED_STATE_ID = ID + "." + DefaultTeamState.Completed.name();
   public static String DECISION_CANCELLED_STATE_ID = ID + "." + DefaultTeamState.Cancelled.name();

   public DecisionWorkflowDefinition() {
      super(ID, ID, null);
      addTransitions();
      startPageId = AtsDecisionPrepareWorkPageDefinition.ID;
   }

   public DecisionWorkflowDefinition(Artifact artifact)throws OseeCoreException, SQLException{
      super(artifact);
      throw new IllegalStateException("This constructor should never be used.");
   }

   public void config(WriteType writeType, XResultData xResultData)throws OseeCoreException, SQLException{
      AtsWorkDefinitions.importWorkItemDefinitionsIntoDb(writeType, xResultData, getAtsWorkDefinitions());
   }

   public static List<WorkItemDefinition> getAtsWorkDefinitions() {
      List<WorkItemDefinition> workItems = new ArrayList<WorkItemDefinition>();

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

   private void addTransitions() {
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
