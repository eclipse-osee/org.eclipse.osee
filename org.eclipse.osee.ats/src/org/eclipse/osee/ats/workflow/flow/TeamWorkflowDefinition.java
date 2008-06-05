/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow.flow;

import org.eclipse.osee.ats.util.DefaultTeamState;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;

/**
 * @author Donald G. Dunne
 */
public class TeamWorkflowDefinition extends WorkFlowDefinition {

   public static String ID = "osee.ats.defaultTeam";

   public TeamWorkflowDefinition() {
      this("Team Work Flow Definition", ID);
   }

   public TeamWorkflowDefinition(Artifact artifact) throws Exception {
      super(artifact);
      throw new IllegalStateException("This constructor should never be used.");
   }

   /**
    * @param name
    * @param id
    * @param parentId
    */
   public TeamWorkflowDefinition(String name, String workflowId) {
      super(name, workflowId, null);
      addDefaultTransitions(this, workflowId);
      startPageId = workflowId + "." + DefaultTeamState.Endorse;
   }

   public static void addDefaultTransitions(WorkFlowDefinition teamWorkflowDefinition, String workflowId) {
      teamWorkflowDefinition.addPageTransition(workflowId + "." + DefaultTeamState.Endorse,
            workflowId + "." + DefaultTeamState.Analyze, TransitionType.ToPageAsDefault);
      teamWorkflowDefinition.addPageTransition(workflowId + "." + DefaultTeamState.Analyze,
            workflowId + "." + DefaultTeamState.Authorize, TransitionType.ToPageAsDefault);
      teamWorkflowDefinition.addPageTransition(workflowId + "." + DefaultTeamState.Authorize,
            workflowId + "." + DefaultTeamState.Implement, TransitionType.ToPageAsDefault);
      teamWorkflowDefinition.addPageTransition(workflowId + "." + DefaultTeamState.Implement,
            workflowId + "." + DefaultTeamState.Completed, TransitionType.ToPageAsDefault);

      // Add return transitions
      teamWorkflowDefinition.addPageTransition(workflowId + "." + DefaultTeamState.Authorize,
            workflowId + "." + DefaultTeamState.Analyze, TransitionType.ToPageAsReturn);
      teamWorkflowDefinition.addPageTransition(workflowId + "." + DefaultTeamState.Implement,
            workflowId + "." + DefaultTeamState.Analyze, TransitionType.ToPageAsReturn);
      teamWorkflowDefinition.addPageTransition(workflowId + "." + DefaultTeamState.Implement,
            workflowId + "." + DefaultTeamState.Authorize, TransitionType.ToPageAsReturn);
      teamWorkflowDefinition.addPageTransition(workflowId + "." + DefaultTeamState.Completed,
            workflowId + "." + DefaultTeamState.Implement, TransitionType.ToPageAsReturn);

      // Add cancelled transitions
      teamWorkflowDefinition.addPageTransitionToPageAndReturn(workflowId + "." + DefaultTeamState.Endorse,
            workflowId + "." + DefaultTeamState.Cancelled);
      teamWorkflowDefinition.addPageTransitionToPageAndReturn(workflowId + "." + DefaultTeamState.Analyze,
            workflowId + "." + DefaultTeamState.Cancelled);
      teamWorkflowDefinition.addPageTransitionToPageAndReturn(workflowId + "." + DefaultTeamState.Authorize,
            workflowId + "." + DefaultTeamState.Cancelled);
      teamWorkflowDefinition.addPageTransitionToPageAndReturn(workflowId + "." + DefaultTeamState.Implement,
            workflowId + "." + DefaultTeamState.Cancelled);
   }
}
