/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow.flow;

import org.eclipse.osee.ats.util.DefaultTeamState;
import org.eclipse.osee.ats.workflow.page.AtsTaskInWorkPageDefinition;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;

/**
 * @author Donald G. Dunne
 */
public class TaskWorkflowDefinition extends WorkFlowDefinition {

   public static String ID = "osee.ats.defaultTask";
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

   /**
    * @param name
    * @param id
    * @param parentId
    */
   public TaskWorkflowDefinition(String name, String id) {
      super(name, id, null);
      addPageTransition(AtsTaskInWorkPageDefinition.ID, TASK_COMPLETED_STATE_ID, TransitionType.ToPageAsDefault);

      // Add return transitions
      addPageTransition(TASK_COMPLETED_STATE_ID, AtsTaskInWorkPageDefinition.ID, TransitionType.ToPageAsReturn);

      // Add cancelled transitions
      addPageTransitionToPageAndReturn(AtsTaskInWorkPageDefinition.ID, TASK_CANCELLED_STATE_ID);
   }

}
