/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow.page;

import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.DecisionReviewArtifact;
import org.eclipse.osee.ats.workflow.flow.DecisionWorkflowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;

/**
 * @author Donald G. Dunne
 */
public class AtsDecisionFollowupWorkPageDefinition extends WorkPageDefinition {

   public static String ID = DecisionWorkflowDefinition.ID + "." + DecisionReviewArtifact.DecisionReviewState.Followup.name();

   public AtsDecisionFollowupWorkPageDefinition() {
      this(DecisionReviewArtifact.DecisionReviewState.Followup.name(), ID, null);
   }

   public AtsDecisionFollowupWorkPageDefinition(String name, String pageId, String parentId) {
      super(name, pageId, parentId);
      addWorkItem(ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName());
   }

}
