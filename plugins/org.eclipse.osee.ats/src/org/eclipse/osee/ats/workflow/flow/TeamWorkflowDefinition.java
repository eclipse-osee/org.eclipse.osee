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
import org.eclipse.osee.ats.workflow.item.AtsStatePercentCompleteWeightDefaultWorkflowRule;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.ats.workflow.page.AtsAnalyzeWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsAuthorizeWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsCancelledWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsCompletedWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsEndorseWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsImplementWorkPageDefinition;
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
public class TeamWorkflowDefinition extends WorkFlowDefinition {

   public final static String ID = "osee.ats.teamWorkflow";

   public TeamWorkflowDefinition() {
      this(ID, ID);
      addWorkItem(AtsStatePercentCompleteWeightDefaultWorkflowRule.ID);
   }

   public TeamWorkflowDefinition(Artifact artifact) throws OseeCoreException {
      super(artifact);
      throw new OseeStateException("This constructor should never be used.");
   }

   /**
    * Instantiate workflow as inherited from parentWorkflowId. Default transitions and startPageId are not set as they
    * will most likely come from parent.
    */
   public TeamWorkflowDefinition(String name, String workflowId, String parentWorkflowId) {
      super(name, workflowId, parentWorkflowId);
   }

   public void config(WriteType writeType, XResultData xResultData) throws OseeCoreException {
      AtsWorkDefinitions.importWorkItemDefinitionsIntoDb(writeType, xResultData, getAtsWorkDefinitions());
   }

   /**
    * Instantiate workflow as a TeamWorkflowDefinition with default transitions and startPageId set.
    */
   public TeamWorkflowDefinition(String name, String workflowId) {
      super(name, workflowId, null);
      addDefaultTransitions(this, workflowId);
      startPageId = TeamState.Endorse.getPageName();
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

   public static List<WorkItemDefinition> getWorkPageDefinitionsForId(String workflowId) {
      List<WorkItemDefinition> workItems = new ArrayList<WorkItemDefinition>();
      // Add Team Page and Workflow Definition
      workItems.add(new WorkPageDefinition(TeamState.Endorse.getPageName(),
         workflowId + "." + TeamState.Endorse.getPageName(), AtsEndorseWorkPageDefinition.ID, WorkPageType.Working));
      workItems.add(new WorkPageDefinition(TeamState.Analyze.getPageName(),
         workflowId + "." + TeamState.Analyze.getPageName(), AtsAnalyzeWorkPageDefinition.ID, WorkPageType.Working));
      workItems.add(new WorkPageDefinition(TeamState.Authorize.getPageName(),
         workflowId + "." + TeamState.Authorize.getPageName(), AtsAuthorizeWorkPageDefinition.ID, WorkPageType.Working));
      workItems.add(new WorkPageDefinition(TeamState.Implement.getPageName(),
         workflowId + "." + TeamState.Implement.getPageName(), AtsImplementWorkPageDefinition.ID, WorkPageType.Working));
      workItems.add(new WorkPageDefinition(TeamState.Completed.getPageName(),
         workflowId + "." + TeamState.Completed.getPageName(), AtsCompletedWorkPageDefinition.ID,
         WorkPageType.Completed));
      workItems.add(new WorkPageDefinition(TeamState.Cancelled.getPageName(),
         workflowId + "." + TeamState.Cancelled.getPageName(), AtsCancelledWorkPageDefinition.ID,
         WorkPageType.Cancelled));
      return workItems;
   }

   public static void addDefaultTransitions(WorkFlowDefinition teamWorkflowDefinition, String workflowId) {
      teamWorkflowDefinition.addPageTransition(TeamState.Endorse.getPageName(), TeamState.Analyze.getPageName(),
         TransitionType.ToPageAsDefault);
      teamWorkflowDefinition.addPageTransition(TeamState.Analyze.getPageName(), TeamState.Authorize.getPageName(),
         TransitionType.ToPageAsDefault);
      teamWorkflowDefinition.addPageTransition(TeamState.Authorize.getPageName(), TeamState.Implement.getPageName(),
         TransitionType.ToPageAsDefault);
      teamWorkflowDefinition.addPageTransition(TeamState.Implement.getPageName(), TeamState.Completed.getPageName(),
         TransitionType.ToPageAsDefault);

      // Add return transitions
      teamWorkflowDefinition.addPageTransition(TeamState.Analyze.getPageName(), TeamState.Endorse.getPageName(),
         TransitionType.ToPageAsReturn);
      teamWorkflowDefinition.addPageTransition(TeamState.Authorize.getPageName(), TeamState.Analyze.getPageName(),
         TransitionType.ToPageAsReturn);
      teamWorkflowDefinition.addPageTransition(TeamState.Implement.getPageName(), TeamState.Analyze.getPageName(),
         TransitionType.ToPageAsReturn);
      teamWorkflowDefinition.addPageTransition(TeamState.Implement.getPageName(), TeamState.Authorize.getPageName(),
         TransitionType.ToPageAsReturn);
      teamWorkflowDefinition.addPageTransition(TeamState.Completed.getPageName(), TeamState.Implement.getPageName(),
         TransitionType.ToPageAsReturn);

      // Add cancelled transitions
      teamWorkflowDefinition.addPageTransitionToPageAndReturn(TeamState.Endorse.getPageName(),
         TeamState.Cancelled.getPageName());
      teamWorkflowDefinition.addPageTransitionToPageAndReturn(TeamState.Analyze.getPageName(),
         TeamState.Cancelled.getPageName());
      teamWorkflowDefinition.addPageTransitionToPageAndReturn(TeamState.Authorize.getPageName(),
         TeamState.Cancelled.getPageName());
      teamWorkflowDefinition.addPageTransitionToPageAndReturn(TeamState.Implement.getPageName(),
         TeamState.Cancelled.getPageName());
   }
}
