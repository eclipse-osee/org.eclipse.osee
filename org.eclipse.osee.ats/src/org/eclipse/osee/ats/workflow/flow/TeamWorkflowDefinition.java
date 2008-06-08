/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow.flow;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.workflow.page.AtsAnalyzeWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsAuthorizeWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsCancelledWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsCompletedWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsEndorseWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsImplementWorkPageDefinition;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition;

/**
 * @author Donald G. Dunne
 */
public class TeamWorkflowDefinition extends WorkFlowDefinition {

   public static String ID = "osee.ats.teamWorkflow";

   public TeamWorkflowDefinition() {
      this("Team Work Flow Definition", ID);
   }

   public TeamWorkflowDefinition(Artifact artifact) throws Exception {
      super(artifact);
      throw new IllegalStateException("This constructor should never be used.");
   }

   /**
    * Instantiate workflow as inherited from parentWorkflowId. Default transitions and startPageId are not set as they
    * will most likely come from parent.
    * 
    * @param name
    * @param workflowId
    * @param parentWorkflowId
    */
   public TeamWorkflowDefinition(String name, String workflowId, String parentWorkflowId) {
      super(name, workflowId, parentWorkflowId);
   }

   /**
    * Instantiate workflow as a TeamWorkflowDefinition with default transitions and startPageId set.
    * 
    * @param name
    * @param id
    * @param parentId
    */
   public TeamWorkflowDefinition(String name, String workflowId) {
      super(name, workflowId, null);
      addDefaultTransitions(this, workflowId);
      startPageId = DefaultTeamState.Endorse.name();
   }

   public static List<WorkItemDefinition> getAtsWorkDefinitions() {
      List<WorkItemDefinition> workItems = new ArrayList<WorkItemDefinition>();

      // Add Team Page and Workflow Definition
      workItems.add(new AtsEndorseWorkPageDefinition());
      workItems.add(new AtsAnalyzeWorkPageDefinition());
      workItems.add(new AtsAuthorizeWorkPageDefinition());
      workItems.add(new AtsImplementWorkPageDefinition());
      workItems.add(new AtsCompletedWorkPageDefinition());
      workItems.add(new AtsCancelledWorkPageDefinition());
      workItems.add(new TeamWorkflowDefinition());

      return workItems;
   }

   public static void addDefaultTransitions(WorkFlowDefinition teamWorkflowDefinition, String workflowId) {
      teamWorkflowDefinition.addPageTransition(DefaultTeamState.Endorse.name(), DefaultTeamState.Analyze.name(),
            TransitionType.ToPageAsDefault);
      teamWorkflowDefinition.addPageTransition(DefaultTeamState.Analyze.name(), DefaultTeamState.Authorize.name(),
            TransitionType.ToPageAsDefault);
      teamWorkflowDefinition.addPageTransition(DefaultTeamState.Authorize.name(), DefaultTeamState.Implement.name(),
            TransitionType.ToPageAsDefault);
      teamWorkflowDefinition.addPageTransition(DefaultTeamState.Implement.name(), DefaultTeamState.Completed.name(),
            TransitionType.ToPageAsDefault);

      // Add return transitions
      teamWorkflowDefinition.addPageTransition(DefaultTeamState.Authorize.name(), DefaultTeamState.Analyze.name(),
            TransitionType.ToPageAsReturn);
      teamWorkflowDefinition.addPageTransition(DefaultTeamState.Implement.name(), DefaultTeamState.Analyze.name(),
            TransitionType.ToPageAsReturn);
      teamWorkflowDefinition.addPageTransition(DefaultTeamState.Implement.name(), DefaultTeamState.Authorize.name(),
            TransitionType.ToPageAsReturn);
      teamWorkflowDefinition.addPageTransition(DefaultTeamState.Completed.name(), DefaultTeamState.Implement.name(),
            TransitionType.ToPageAsReturn);

      // Add cancelled transitions
      teamWorkflowDefinition.addPageTransitionToPageAndReturn(DefaultTeamState.Endorse.name(),
            DefaultTeamState.Cancelled.name());
      teamWorkflowDefinition.addPageTransitionToPageAndReturn(DefaultTeamState.Analyze.name(),
            DefaultTeamState.Cancelled.name());
      teamWorkflowDefinition.addPageTransitionToPageAndReturn(DefaultTeamState.Authorize.name(),
            DefaultTeamState.Cancelled.name());
      teamWorkflowDefinition.addPageTransitionToPageAndReturn(DefaultTeamState.Implement.name(),
            DefaultTeamState.Cancelled.name());
   }
}
