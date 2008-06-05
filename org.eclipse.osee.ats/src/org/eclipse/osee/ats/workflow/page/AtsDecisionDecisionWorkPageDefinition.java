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
public class AtsDecisionDecisionWorkPageDefinition extends WorkPageDefinition {

   public static String ID = DecisionWorkflowDefinition.ID + "." + DecisionReviewArtifact.StateNames.Decision.name();
   public static String DECISION_XWIDGET_ID = "ats.Decision Question";

   public AtsDecisionDecisionWorkPageDefinition() {
      this(DecisionReviewArtifact.StateNames.Decision.name(), ID, null);
   }

   public AtsDecisionDecisionWorkPageDefinition(String name, String pageId, String parentId) {
      super(name, pageId, parentId);
      addWorkItem(AtsDecisionDecisionWorkPageDefinition.DECISION_XWIDGET_ID);
      addWorkItem(ATSAttributes.DECISION_ATTRIBUTE.getStoreName());
      addWorkItem(ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName());
   }

}
