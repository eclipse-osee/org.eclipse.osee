/*********************************************************************
 * Copyright (c) 2012 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.CreateTeamOption;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.navigate.AtsNavigateViewItems;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.ide.world.AtsWorldEditorRenderer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
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

      IAtsChangeSet changes = AtsApiService.get().createChangeSet("Create Porting Workflow(s)");
      List<Artifact> destinationWorkflows = createDestinationWorkflows(changes, actionableItems);
      changes.execute();

      AtsWorldEditorRenderer renderer = new AtsWorldEditorRenderer();
      renderer.open(destinationWorkflows, PresentationType.SPECIALIZED_EDIT);
   }

   private List<Artifact> createDestinationWorkflows(IAtsChangeSet changes, List<IAtsActionableItem> actionableItems) {
      IAtsTeamDefinition teamDefinition = actionableItems.get(0).getTeamDefinition();
      List<Artifact> destinationWorkflows = new ArrayList<>();
      AtsUser createdBy = AtsApiService.get().getUserService().getCurrentUser();
      Date createdDate = new Date();

      for (TeamWorkFlowArtifact sourceWorkflow : sourceWorkflows) {
         IAtsTeamWorkflow destinationWorkflow;
         if (sourceWorkflow.getRelatedArtifacts(AtsRelationTypes.Port_To).isEmpty()) {
            List<AtsUser> assignees = sourceWorkflow.getStateMgr().getAssignees();

            destinationWorkflow = AtsApiService.get().getActionService().createTeamWorkflow(
               sourceWorkflow.getParentAction(), teamDefinition, actionableItems, assignees, changes, createdDate,
               createdBy, null, CreateTeamOption.Duplicate_If_Exists);

            changes.setName(destinationWorkflow, sourceWorkflow.getName());
            changes.relate(sourceWorkflow, AtsRelationTypes.Port_To, destinationWorkflow);
         } else {
            destinationWorkflow = AtsApiService.get().getWorkItemService().getTeamWf(
               AtsApiService.get().getRelationResolver().getRelatedOrNull((ArtifactId) sourceWorkflow,
                  AtsRelationTypes.Port_To));
            log("Reusing destination workflow " + destinationWorkflow);
         }

         destinationWorkflows.add(AtsApiService.get().getQueryServiceIde().getArtifact(destinationWorkflow));
      }

      return destinationWorkflows;
   }

   private List<IAtsActionableItem> getActionableItems(IAtsActionableItem actionableItem) {
      List<IAtsActionableItem> actionableItems;
      if (actionableItem == null) {
         actionableItems =
            new ArrayList<>(AtsApiService.get().getActionableItemService().getActionableItems(sourceWorkflows.get(0)));
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
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(AtsNavigateViewItems.ATS_ADMIN, XNavItemCat.OSEE_ADMIN);
   }

}