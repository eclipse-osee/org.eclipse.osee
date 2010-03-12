/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.workflow.flow;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.artifact.TaskArtifact.TaskStates;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.ats.workflow.page.AtsCancelledWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsCompletedWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsTaskInWorkPageDefinition;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;

/**
 * @author Donald G. Dunne
 */
public class TaskWorkflowDefinition extends WorkFlowDefinition {

   public static String ID = "osee.ats.taskWorkflow";

   public TaskWorkflowDefinition() {
      this(ID, ID);
      startPageId = TaskStates.InWork.name();
   }

   public TaskWorkflowDefinition(Artifact artifact) throws OseeCoreException {
      super(artifact);
      throw new OseeStateException("This constructor should never be used.");
   }

   public void config(WriteType writeType, XResultData xResultData) throws OseeCoreException {
      AtsWorkDefinitions.importWorkItemDefinitionsIntoDb(writeType, xResultData, getAtsWorkDefinitions());
   }

   public static List<WorkItemDefinition> getAtsWorkDefinitions() {
      List<WorkItemDefinition> workItems = new ArrayList<WorkItemDefinition>();

      // Add Task Page and Workflow Definition
      workItems.add(new AtsTaskInWorkPageDefinition());
      workItems.add(new WorkPageDefinition(DefaultTeamState.Completed.name(), ID + "." + TaskStates.Completed.name(),
            AtsCompletedWorkPageDefinition.ID));
      workItems.add(new WorkPageDefinition(DefaultTeamState.Cancelled.name(), ID + "." + TaskStates.Cancelled.name(),
            AtsCancelledWorkPageDefinition.ID));
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
      addPageTransition(TaskStates.InWork.name(), TaskStates.Completed.name(), TransitionType.ToPageAsDefault);

      // Add return transitions
      addPageTransition(TaskStates.Completed.name(), TaskStates.InWork.name(), TransitionType.ToPageAsReturn);

      // Add cancelled transitions
      addPageTransitionToPageAndReturn(TaskStates.InWork.name(), TaskStates.Cancelled.name());
   }

}
