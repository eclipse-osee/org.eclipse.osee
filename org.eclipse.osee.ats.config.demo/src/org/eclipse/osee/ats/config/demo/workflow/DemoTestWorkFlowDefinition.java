/*
 * Created on Jun 3, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.config.demo.workflow;

import org.eclipse.osee.ats.workflow.flow.TeamWorkflowDefinition;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;

/**
 * @author Donald G. Dunne
 */
public class DemoTestWorkFlowDefinition extends TeamWorkflowDefinition {

   public static String ID = "demo.test";

   public DemoTestWorkFlowDefinition() {
      super("Demo Test Work Flow Definition", ID, TeamWorkflowDefinition.ID);
   }

   public void config(WriteType writeType, XResultData xResultData) throws Exception {
      AtsWorkDefinitions.importWorkItemDefinitionsIntoDb(writeType, xResultData,
            TeamWorkflowDefinition.getWorkPageDefinitionsForId(getId()));
      AtsWorkDefinitions.importWorkItemDefinitionsIntoDb(writeType, xResultData, new DemoTestWorkFlowDefinition());
   }

}
