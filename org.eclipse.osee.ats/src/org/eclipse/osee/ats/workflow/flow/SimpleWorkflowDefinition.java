/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow.flow;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;

/**
 * @author Donald G. Dunne
 */
public class SimpleWorkflowDefinition extends WorkFlowDefinition {

   public static String ID = "osee.ats.simpleTeam";
   public static enum SimpleState {
      Endorse, InWork, Completed, Cancelled
   };
   public static String ENDORSE_STATE_ID = ID + "." + SimpleState.Endorse.name();
   public static String INWORK_STATE_ID = ID + "." + SimpleState.InWork.name();
   public static String COMPLETED_STATE_ID = ID + "." + SimpleState.Completed.name();
   public static String CANCELLED_STATE_ID = ID + "." + SimpleState.Cancelled.name();

   public SimpleWorkflowDefinition() {
      this("Simple Work Flow Definition", ID);
   }

   public SimpleWorkflowDefinition(Artifact artifact) throws Exception {
      super(artifact);
      throw new IllegalStateException("This constructor should never be used.");
   }

   /**
    * @param name
    * @param id
    * @param parentId
    */
   public SimpleWorkflowDefinition(String name, String workflowId) {
      super(name, workflowId, null);
      addDefaultTransitions(this, workflowId);
      startPageId = workflowId + "." + SimpleState.Endorse;
   }

   public static void addDefaultTransitions(WorkFlowDefinition teamWorkflowDefinition, String workflowId) {
      teamWorkflowDefinition.addPageTransition(workflowId + "." + SimpleState.Endorse,
            workflowId + "." + SimpleState.InWork, TransitionType.ToPageAsDefault);
      teamWorkflowDefinition.addPageTransition(workflowId + "." + SimpleState.InWork,
            workflowId + "." + SimpleState.Completed, TransitionType.ToPageAsDefault);

      // Add return transitions
      teamWorkflowDefinition.addPageTransition(workflowId + "." + SimpleState.InWork,
            workflowId + "." + SimpleState.Endorse, TransitionType.ToPageAsReturn);
      teamWorkflowDefinition.addPageTransition(workflowId + "." + SimpleState.Completed,
            workflowId + "." + SimpleState.InWork, TransitionType.ToPageAsReturn);

      // Add cancelled transitions
      teamWorkflowDefinition.addPageTransitionToPageAndReturn(workflowId + "." + SimpleState.Endorse,
            workflowId + "." + SimpleState.Cancelled);
      teamWorkflowDefinition.addPageTransitionToPageAndReturn(workflowId + "." + SimpleState.InWork,
            workflowId + "." + SimpleState.Cancelled);
   }
}
