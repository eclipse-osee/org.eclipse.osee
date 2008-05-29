/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow.page;

import org.eclipse.osee.ats.util.DefaultTeamState;
import org.eclipse.osee.ats.workflow.flow.DefaultTeamWorkflowDefinition;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions.BooleanWorkItemId;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;

/**
 * @author Donald G. Dunne
 */
public class AtsCompletedWorkPageDefinition extends WorkPageDefinition {

   public static String ID = DefaultTeamWorkflowDefinition.ID + "." + DefaultTeamState.Completed.name();

   public AtsCompletedWorkPageDefinition() {
      this(DefaultTeamState.Completed.name(), ID, null);
   }

   public AtsCompletedWorkPageDefinition(String name, String pageId, String parentId) {
      super(name, pageId, null);
      addWorkItem(AtsWorkDefinitions.getWorkItemDefinition(BooleanWorkItemId.atsAddDecisionValidateBlockingReview.name()));
   }

}
