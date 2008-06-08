/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow.flow;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.artifact.TaskArtifact.TaskStates;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.workflow.page.AtsCancelledWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsCompletedWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsTaskInWorkPageDefinition;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;

/**
 * @author Donald G. Dunne
 */
public class TaskWorkflowDefinition extends WorkFlowDefinition {

   public static String ID = "osee.ats.taskWorkflow";
   public static String TASK_COMPLETED_STATE_ID = ID + "." + DefaultTeamState.Completed.name();
   public static String TASK_CANCELLED_STATE_ID = ID + "." + DefaultTeamState.Cancelled.name();

   public TaskWorkflowDefinition() {
      this("Task Workflow Definition", ID);
      startPageId = AtsTaskInWorkPageDefinition.ID;
   }

   public TaskWorkflowDefinition(Artifact artifact) throws Exception {
      super(artifact);
      throw new IllegalStateException("This constructor should never be used.");
   }

   public static List<WorkItemDefinition> getAtsWorkDefinitions() {
      List<WorkItemDefinition> workItems = new ArrayList<WorkItemDefinition>();

      // Add Task Page and Workflow Definition
      workItems.add(new AtsTaskInWorkPageDefinition());
      workItems.add(new WorkPageDefinition(DefaultTeamState.Completed.name(),
            TaskWorkflowDefinition.TASK_COMPLETED_STATE_ID, AtsCompletedWorkPageDefinition.ID));
      workItems.add(new WorkPageDefinition(DefaultTeamState.Cancelled.name(),
            TaskWorkflowDefinition.TASK_CANCELLED_STATE_ID, AtsCancelledWorkPageDefinition.ID));
      workItems.add(new TaskWorkflowDefinition());

      return workItems;
   }

   /**
    * @param name
    * @param id
    * @param parentId
    */
   public TaskWorkflowDefinition(String name, String id) {
      super(name, id, null);
      addPageTransition(TaskStates.InWork.name(), TASK_COMPLETED_STATE_ID, TransitionType.ToPageAsDefault);

      // Add return transitions
      addPageTransition(TASK_COMPLETED_STATE_ID, AtsTaskInWorkPageDefinition.ID, TransitionType.ToPageAsReturn);

      // Add cancelled transitions
      addPageTransitionToPageAndReturn(AtsTaskInWorkPageDefinition.ID, TASK_CANCELLED_STATE_ID);
   }

}
