/*
 * Created on Jun 3, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.config.demo.workflow;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.workflow.flow.TeamWorkflowDefinition;
import org.eclipse.osee.ats.workflow.page.AtsAnalyzeWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsAuthorizeWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsCancelledWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsCompletedWorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;

/**
 * @author Donald G. Dunne
 */
public class DemoTestWorkFlowDefinition extends TeamWorkflowDefinition {

   public static String ID = "demo.test";

   public DemoTestWorkFlowDefinition() {
      super("Demo Test Work Flow Definition", ID);
   }

   public static String ENDORSE_STATE_ID = ID + "." + DefaultTeamState.Endorse.name();
   public static String ANALYZE_STATE_ID = ID + "." + DefaultTeamState.Analyze.name();
   public static String AUTHORIZE_STATE_ID = ID + "." + DefaultTeamState.Authorize.name();
   public static String IMPLEMENT_STATE_ID = ID + "." + DefaultTeamState.Implement.name();
   public static String COMPLETED_STATE_ID = ID + "." + DefaultTeamState.Completed.name();
   public static String CANCELLED_STATE_ID = ID + "." + DefaultTeamState.Cancelled.name();

   public static List<WorkItemDefinition> getWorkItemDefinitions() {
      List<WorkItemDefinition> workItems = new ArrayList<WorkItemDefinition>();

      // Add Team Page and Workflow Definition
      workItems.add(new WorkPageDefinition(DefaultTeamState.Endorse.name(), ENDORSE_STATE_ID,
            AtsAnalyzeWorkPageDefinition.ID));
      workItems.add(new WorkPageDefinition(DefaultTeamState.Analyze.name(), ANALYZE_STATE_ID,
            AtsAnalyzeWorkPageDefinition.ID));
      workItems.add(new WorkPageDefinition(DefaultTeamState.Authorize.name(), AUTHORIZE_STATE_ID,
            AtsAuthorizeWorkPageDefinition.ID));
      workItems.add(new WorkPageDefinition(DefaultTeamState.Implement.name(), IMPLEMENT_STATE_ID,
            AtsAuthorizeWorkPageDefinition.ID));
      workItems.add(new WorkPageDefinition(DefaultTeamState.Completed.name(), COMPLETED_STATE_ID,
            AtsCompletedWorkPageDefinition.ID));
      workItems.add(new WorkPageDefinition(DefaultTeamState.Cancelled.name(), CANCELLED_STATE_ID,
            AtsCancelledWorkPageDefinition.ID));
      workItems.add(new DemoTestWorkFlowDefinition());

      return workItems;
   }

}
