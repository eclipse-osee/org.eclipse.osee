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
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.workflow.item.AtsStatePercentCompleteWeightDefaultWorkflowRule;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.ats.workflow.page.AtsAnalyzeWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsAuthorizeWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsCancelledWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsCompletedWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsEndorseWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsImplementWorkPageDefinition;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;

/**
 * @author Donald G. Dunne
 */
public class TeamWorkflowDefinition extends WorkFlowDefinition {

   public static String ID = "osee.ats.teamWorkflow";

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
    * 
    * @param name
    * @param workflowId
    * @param parentWorkflowId
    */
   public TeamWorkflowDefinition(String name, String workflowId, String parentWorkflowId) {
      super(name, workflowId, parentWorkflowId);
   }

   public void config(WriteType writeType, XResultData xResultData) throws OseeCoreException {
      AtsWorkDefinitions.importWorkItemDefinitionsIntoDb(writeType, xResultData, getAtsWorkDefinitions());
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

   public static List<WorkItemDefinition> getWorkPageDefinitionsForId(String workflowId) {
      List<WorkItemDefinition> workItems = new ArrayList<WorkItemDefinition>();
      // Add Team Page and Workflow Definition
      workItems.add(new WorkPageDefinition(DefaultTeamState.Endorse.name(),
            workflowId + "." + DefaultTeamState.Endorse.name(), AtsEndorseWorkPageDefinition.ID));
      workItems.add(new WorkPageDefinition(DefaultTeamState.Analyze.name(),
            workflowId + "." + DefaultTeamState.Analyze.name(), AtsAnalyzeWorkPageDefinition.ID));
      workItems.add(new WorkPageDefinition(DefaultTeamState.Authorize.name(),
            workflowId + "." + DefaultTeamState.Authorize.name(), AtsAuthorizeWorkPageDefinition.ID));
      workItems.add(new WorkPageDefinition(DefaultTeamState.Implement.name(),
            workflowId + "." + DefaultTeamState.Implement.name(), AtsImplementWorkPageDefinition.ID));
      workItems.add(new WorkPageDefinition(DefaultTeamState.Completed.name(),
            workflowId + "." + DefaultTeamState.Completed.name(), AtsCompletedWorkPageDefinition.ID));
      workItems.add(new WorkPageDefinition(DefaultTeamState.Cancelled.name(),
            workflowId + "." + DefaultTeamState.Cancelled.name(), AtsCancelledWorkPageDefinition.ID));
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
