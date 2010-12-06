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
import org.eclipse.osee.ats.util.TeamState;
import org.eclipse.osee.ats.workflow.item.AtsStatePercentCompleteWeightSimpleWorkflowRule;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.ats.workflow.page.AtsCancelledWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsCompletedWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsEndorseWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsSimpleInWorkWorkPageDefinition;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageType;

/**
 * @author Donald G. Dunne
 */
public class SimpleWorkflowDefinition extends WorkFlowDefinition {

   public final static String ID = "osee.ats.simpleTeamWorkflow";
   public static enum SimpleState {
      Endorse,
      InWork,
      Completed,
      Cancelled
   };
   public final static String ENDORSE_STATE_ID = ID + "." + SimpleState.Endorse.name();
   public final static String INWORK_STATE_ID = ID + "." + SimpleState.InWork.name();
   public final static String COMPLETED_STATE_ID = ID + "." + SimpleState.Completed.name();
   public final static String CANCELLED_STATE_ID = ID + "." + SimpleState.Cancelled.name();

   public SimpleWorkflowDefinition(Artifact artifact) throws OseeCoreException {
      super(artifact);
      throw new OseeStateException("This constructor should never be used.");
   }

   public SimpleWorkflowDefinition() {
      this(ID, ID);
      addWorkItem(AtsStatePercentCompleteWeightSimpleWorkflowRule.ID);
   }

   public SimpleWorkflowDefinition(String name, String workflowId) {
      super(name, workflowId, null);
      addDefaultTransitions(this, workflowId);
      startPageId = SimpleState.Endorse.name();
   }

   public void config(WriteType writeType, XResultData xResultData) throws OseeCoreException {
      AtsWorkDefinitions.importWorkItemDefinitionsIntoDb(writeType, xResultData, getAtsWorkDefinitions());
   }

   public static void addDefaultTransitions(WorkFlowDefinition teamWorkflowDefinition, String workflowId) {
      teamWorkflowDefinition.addPageTransition(SimpleState.Endorse.name(), SimpleState.InWork.name(),
         TransitionType.ToPageAsDefault);
      teamWorkflowDefinition.addPageTransition(SimpleState.InWork.name(), SimpleState.Completed.name(),
         TransitionType.ToPageAsDefault);

      // Add return transitions
      teamWorkflowDefinition.addPageTransition(SimpleState.InWork.name(), SimpleState.Endorse.name(),
         TransitionType.ToPageAsReturn);
      teamWorkflowDefinition.addPageTransition(SimpleState.Completed.name(), SimpleState.InWork.name(),
         TransitionType.ToPageAsReturn);

      // Add cancelled transitions
      teamWorkflowDefinition.addPageTransitionToPageAndReturn(SimpleState.Endorse.name(), SimpleState.Cancelled.name());
      teamWorkflowDefinition.addPageTransitionToPageAndReturn(SimpleState.InWork.name(), SimpleState.Cancelled.name());
   }

   public static List<WorkItemDefinition> getAtsWorkDefinitions() {
      List<WorkItemDefinition> workItems = new ArrayList<WorkItemDefinition>();

      workItems.add(new WorkPageDefinition(SimpleState.Endorse.name(), SimpleWorkflowDefinition.ENDORSE_STATE_ID,
         AtsEndorseWorkPageDefinition.ID, WorkPageType.Working, 1));
      workItems.add(new AtsSimpleInWorkWorkPageDefinition(2));
      workItems.add(new WorkPageDefinition(TeamState.Completed.getPageName(),
         SimpleWorkflowDefinition.COMPLETED_STATE_ID, AtsCompletedWorkPageDefinition.ID, WorkPageType.Completed, 3));
      workItems.add(new WorkPageDefinition(TeamState.Cancelled.getPageName(),
         SimpleWorkflowDefinition.CANCELLED_STATE_ID, AtsCancelledWorkPageDefinition.ID, WorkPageType.Cancelled, 4));
      workItems.add(new SimpleWorkflowDefinition());

      return workItems;
   }
}
