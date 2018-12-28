/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.CreateTeamOption;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.world.AtsWorldEditorRenderer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Ryan D. Brooks
 */
public class WorkflowPortingBlam extends AbstractBlam {
   private final static String SOURCE_WORKFLOWS = "Source Workflows (drop here)";
   private final static String ACTIONABLE_ITEM = "Destination actionable item(s)";
   private List<TeamWorkFlowArtifact> sourceWorkflows;

   @Override
   public void runOperation(final VariableMap variableMap, IProgressMonitor monitor) {
      sourceWorkflows = Collections.castAll(variableMap.getArtifacts(SOURCE_WORKFLOWS));
      Conditions.checkNotNullOrEmpty(sourceWorkflows, SOURCE_WORKFLOWS);
      List<IAtsActionableItem> actionableItems =
         getActionableItems((IAtsActionableItem) variableMap.getValue(ACTIONABLE_ITEM));
      Conditions.checkNotNullOrEmpty(actionableItems, ACTIONABLE_ITEM);

      IAtsChangeSet changes = AtsClientService.get().createChangeSet("Create Porting Workflow(s)");
      List<Artifact> destinationWorkflows = createDestinationWorkflows(changes, actionableItems);
      changes.execute();

      AtsWorldEditorRenderer renderer = new AtsWorldEditorRenderer(new HashMap<RendererOption, Object>());
      renderer.open(destinationWorkflows, PresentationType.SPECIALIZED_EDIT);
   }

   private List<Artifact> createDestinationWorkflows(IAtsChangeSet changes, List<IAtsActionableItem> actionableItems) {
      IAtsTeamDefinition teamDefinition = actionableItems.get(0).getTeamDefinition();
      List<Artifact> destinationWorkflows = new ArrayList<>();
      IAtsUser createdBy = AtsClientService.get().getUserService().getCurrentUser();
      Date createdDate = new Date();

      for (TeamWorkFlowArtifact sourceWorkflow : sourceWorkflows) {
         IAtsTeamWorkflow destinationWorkflow;
         if (sourceWorkflow.getRelatedArtifacts(AtsRelationTypes.Port_To).isEmpty()) {
            List<IAtsUser> assignees = sourceWorkflow.getStateMgr().getAssignees();

            destinationWorkflow = AtsClientService.get().getActionFactory().createTeamWorkflow(
               sourceWorkflow.getParentActionArtifact(), teamDefinition, actionableItems, assignees, changes,
               createdDate, createdBy, null, CreateTeamOption.Duplicate_If_Exists);

            changes.setName(destinationWorkflow, sourceWorkflow.getName());
            changes.relate(sourceWorkflow, AtsRelationTypes.Port_To, destinationWorkflow);
         } else {
            destinationWorkflow = AtsClientService.get().getWorkItemService().getTeamWf(
               AtsClientService.get().getRelationResolver().getRelatedOrNull((ArtifactId) sourceWorkflow,
                  AtsRelationTypes.Port_To));
            log("Reusing destination workflow " + destinationWorkflow);
         }

         destinationWorkflows.add(AtsClientService.get().getQueryServiceClient().getArtifact(destinationWorkflow));
      }

      return destinationWorkflows;
   }

   private List<IAtsActionableItem> getActionableItems(IAtsActionableItem actionableItem) {
      List<IAtsActionableItem> actionableItems;
      if (actionableItem == null) {
         actionableItems = new ArrayList<>(
            AtsClientService.get().getWorkItemService().getActionableItemService().getActionableItems(
               sourceWorkflows.get(0)));
      } else {
         actionableItems = java.util.Collections.singletonList(actionableItem);
      }
      return actionableItems;
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XListDropViewer\" displayName=\"" + SOURCE_WORKFLOWS + "\" /><XWidget xwidgetType=\"XActionableItemCombo\" displayName=\"" + ACTIONABLE_ITEM + "\" /></xWidgets>";
   }

   @Override
   public String getDescriptionUsage() {
      return "Create porting workflows related by port relation for selected actionable items (or same as source workflow)";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("ATS");
   }
}