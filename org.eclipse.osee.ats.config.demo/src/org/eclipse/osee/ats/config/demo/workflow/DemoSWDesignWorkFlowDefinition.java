/*
 * Created on Jun 3, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.config.demo.workflow;

import java.util.Arrays;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact.ReviewBlockType;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.workflow.flow.TeamWorkflowDefinition;
import org.eclipse.osee.ats.workflow.item.AtsStatePercentCompleteWeightDefaultWorkflowRule;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.ats.workflow.item.StateEventType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinitionFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;

/**
 * @author Donald G. Dunne
 */
public class DemoSWDesignWorkFlowDefinition extends TeamWorkflowDefinition {

   public static String ID = "demo.swdesign";

   public DemoSWDesignWorkFlowDefinition() {
      super(ID, ID, TeamWorkflowDefinition.ID);
      addWorkItem(AtsStatePercentCompleteWeightDefaultWorkflowRule.ID);
   }

   @Override
   public void config(WriteType writeType, XResultData xResultData) throws OseeCoreException {
      // Create decision and peer rules
      DemoAddDecisionReviewRule decisionTransitionToRule =
            new DemoAddDecisionReviewRule(DefaultTeamState.Analyze.name(), ReviewBlockType.None,
                  StateEventType.TransitionTo);
      DemoAddDecisionReviewRule decisionCreateBranchRule =
            new DemoAddDecisionReviewRule(DefaultTeamState.Implement.name(), ReviewBlockType.None,
                  StateEventType.CreateBranch);
      DemoAddPeerToPeerReviewRule peerTransitionToRule =
            new DemoAddPeerToPeerReviewRule(DefaultTeamState.Authorize.name(), ReviewBlockType.None,
                  StateEventType.TransitionTo);
      DemoAddPeerToPeerReviewRule peerCommitBranchRule =
            new DemoAddPeerToPeerReviewRule(DefaultTeamState.Implement.name(), ReviewBlockType.None,
                  StateEventType.CommitBranch);
      // Import decision and peer rules into database
      AtsWorkDefinitions.importWorkItemDefinitionsIntoDb(writeType, null, Arrays.asList(decisionTransitionToRule,
            decisionCreateBranchRule, peerTransitionToRule, peerCommitBranchRule));

      // Add Normal SW_Design workflows
      AtsWorkDefinitions.importWorkItemDefinitionsIntoDb(writeType, xResultData,
            TeamWorkflowDefinition.getWorkPageDefinitionsForId(getId()));
      AtsWorkDefinitions.importWorkItemDefinitionsIntoDb(writeType, xResultData, new DemoSWDesignWorkFlowDefinition());
      AtsWorkDefinitions.relatePageToBranchCommitRules(ID + "." + DefaultTeamState.Implement.name());

      // Add Non-blocking createBranch decision review to Implement state 
      WorkItemDefinitionFactory.relateWorkItemDefinitions(ID + "." + DefaultTeamState.Analyze.name(),
            decisionTransitionToRule.getId());
      WorkItemDefinitionFactory.relateWorkItemDefinitions(ID + "." + DefaultTeamState.Implement.name(),
            decisionCreateBranchRule.getId());

      // Add Non-blocking commitBranch peerToPeer review to Implement state 
      WorkItemDefinitionFactory.relateWorkItemDefinitions(ID + "." + DefaultTeamState.Authorize.name(),
            peerTransitionToRule.getId());
      WorkItemDefinitionFactory.relateWorkItemDefinitions(ID + "." + DefaultTeamState.Implement.name(),
            peerCommitBranchRule.getId());
   }
}
