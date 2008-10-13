/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow.page;

import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.workflow.flow.DecisionWorkflowDefinition;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions.RuleWorkItemId;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;

/**
 * @author Donald G. Dunne
 */
public class AtsDecisionCompletedWorkPageDefinition extends WorkPageDefinition {

   public static String ID = DecisionWorkflowDefinition.ID + "." + DefaultTeamState.Completed.name();

   public AtsDecisionCompletedWorkPageDefinition() {
      this(DefaultTeamState.Completed.name(), ID, null);
   }

   public AtsDecisionCompletedWorkPageDefinition(String name, String pageId, String parentId) {
      super(name, pageId, parentId);
      addWorkItem(RuleWorkItemId.atsAddDecisionValidateBlockingReview.name());
      addWorkItem(AtsDecisionDecisionWorkPageDefinition.DECISION_QUESTION_LABEL);
      addWorkItem(AtsDecisionDecisionWorkPageDefinition.DECISION_ANSWER_LABEL);
      addWorkItem(ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName());
   }

}
