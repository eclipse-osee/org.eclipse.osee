/*
 * Created on Jun 3, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.config.demo.workflow;

import java.sql.SQLException;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.workflow.flow.TeamWorkflowDefinition;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;

/**
 * @author Donald G. Dunne
 */
public class DemoReqWorkFlowDefinition extends TeamWorkflowDefinition {

   public static String ID = "demo.req";

   public DemoReqWorkFlowDefinition() {
      super(ID, ID, TeamWorkflowDefinition.ID);
   }

   public void config(WriteType writeType, XResultData xResultData) throws OseeCoreException, SQLException {
      AtsWorkDefinitions.importWorkItemDefinitionsIntoDb(writeType, xResultData,
            TeamWorkflowDefinition.getWorkPageDefinitionsForId(getId()));
      AtsWorkDefinitions.importWorkItemDefinitionsIntoDb(writeType, xResultData, new DemoReqWorkFlowDefinition());
      AtsWorkDefinitions.relatePageToBranchCommitRules(ID + "." + DefaultTeamState.Implement.name());
   }

}
