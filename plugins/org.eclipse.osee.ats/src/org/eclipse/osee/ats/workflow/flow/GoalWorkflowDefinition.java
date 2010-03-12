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
import org.eclipse.osee.ats.artifact.GoalArtifact.GoalState;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.ats.workflow.page.AtsCancelledWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsCompletedWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsGoalInWorkPageDefinition;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;

public class GoalWorkflowDefinition extends WorkFlowDefinition {

   public static String ID = "osee.ats.goalWorkflow";

   public GoalWorkflowDefinition() {
      this(ID, ID);
      startPageId = GoalState.InWork.name();
   }

   public GoalWorkflowDefinition(Artifact artifact) throws OseeCoreException {
      super(artifact);
      throw new OseeStateException("This constructor should never be used.");
   }

   public void config(WriteType writeType, XResultData xResultData) throws OseeCoreException {
      AtsWorkDefinitions.importWorkItemDefinitionsIntoDb(writeType, xResultData, getAtsWorkDefinitions());
   }

   public static List<WorkItemDefinition> getAtsWorkDefinitions() {
      List<WorkItemDefinition> workItems = new ArrayList<WorkItemDefinition>();

      // Add Task Page and Workflow Definition
      workItems.add(new AtsGoalInWorkPageDefinition());
      workItems.add(new WorkPageDefinition(GoalState.Completed.name(), ID + "." + GoalState.Completed.name(),
            AtsCompletedWorkPageDefinition.ID));
      workItems.add(new WorkPageDefinition(GoalState.Cancelled.name(), ID + "." + GoalState.Cancelled.name(),
            AtsCancelledWorkPageDefinition.ID));
      workItems.add(new GoalWorkflowDefinition());

      return workItems;
   }

   /**
    * @param name
    * @param id
    * @param parentId
    */
   public GoalWorkflowDefinition(String name, String id) {
      super(name, id, null);
      addPageTransition(GoalState.InWork.name(), GoalState.Completed.name(), TransitionType.ToPageAsDefault);

      // Add return transitions
      addPageTransition(GoalState.Completed.name(), GoalState.InWork.name(), TransitionType.ToPageAsReturn);

      // Add cancelled transitions
      addPageTransitionToPageAndReturn(GoalState.InWork.name(), GoalState.Cancelled.name());
   }

}
