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

package org.eclipse.osee.ats.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.AtsOpenOption;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.AtsFolderUtil;
import org.eclipse.osee.ats.util.AtsFolderUtil.AtsFolder;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.workflow.editor.wizard.AtsWorkflowConfigCreationWizard.WorkflowData;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.StringAttribute;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinitionFactory;
import org.eclipse.ui.progress.UIJob;

/**
 * This class creates a simple configuration of ATS given team definition name, version names (if desired), actionable
 * items and workflow id.
 * 
 * @author Donald G. Dunne
 */
public class AtsConfigManager extends AbstractOperation {

   public static interface Display {
      public void openAtsConfigurationEditors(TeamDefinitionArtifact teamDef, Collection<ActionableItemArtifact> aias, WorkFlowDefinition workFlowDefinition);
   }

   private final String namespace;
   private final String teamDefName;
   private final Collection<String> versionNames;
   private final Collection<String> actionableItems;
   private final String workflowId;
   private final Display display;

   /**
    * @param namespace
    * @param teamDefName - name of team definition to use
    * @param versionNames - list of version names (if team is using versions)
    * @param actionableItems - list of actionable items
    * @param workflowId - workflowId to use (if null, workflow will be created)
    */
   public AtsConfigManager(Display display, String namespace, String teamDefName, Collection<String> versionNames, Collection<String> actionableItems, String workflowId) {
      super("Configure Ats", AtsPlugin.PLUGIN_ID);
      this.namespace = namespace;
      this.teamDefName = teamDefName;
      this.versionNames = versionNames;
      this.actionableItems = actionableItems;
      this.workflowId = workflowId;
      this.display = display;
   }

   private void checkWorkItemNamespaceUnique() throws OseeCoreException {
      WorkItemDefinition definitionCheck = WorkItemDefinitionFactory.getWorkItemDefinition(namespace);
      if (definitionCheck != null) {
         throw new OseeArgumentException("Configuration Namespace already used, choose a unique namespace.");
      }
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      checkWorkItemNamespaceUnique();
      monitor.worked(calculateWork(0.10));

      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Configure ATS for Default Team");

      TeamDefinitionArtifact teamDefinition = createTeamDefinition(monitor, 0.10, transaction);

      Collection<ActionableItemArtifact> actionableItems =
         createActionableItems(monitor, 0.10, transaction, teamDefinition);

      createVersions(monitor, 0.10, transaction, teamDefinition);

      WorkFlowDefinition workFlowDefinition = createWorkflow(monitor, 0.20, transaction, teamDefinition);

      transaction.execute();
      monitor.worked(calculateWork(0.30));

      display.openAtsConfigurationEditors(teamDefinition, actionableItems, workFlowDefinition);
      monitor.worked(calculateWork(0.10));
   }

   private TeamDefinitionArtifact createTeamDefinition(IProgressMonitor monitor, double workAmount, SkynetTransaction transaction) throws OseeCoreException {
      TeamDefinitionArtifact teamDefinition =
         (TeamDefinitionArtifact) ArtifactTypeManager.addArtifact(AtsArtifactTypes.TeamDefinition,
            AtsUtil.getAtsBranch(), teamDefName);
      if (versionNames == null || versionNames.size() > 0) {
         teamDefinition.setSoleAttributeValue(AtsAttributeTypes.TeamUsesVersions, true);
      }
      teamDefinition.addRelation(AtsRelationTypes.TeamLead_Lead, UserManager.getUser());
      teamDefinition.addRelation(AtsRelationTypes.TeamMember_Member, UserManager.getUser());
      AtsFolderUtil.getFolder(AtsFolder.Teams).addChild(teamDefinition);
      teamDefinition.persist(transaction);
      return teamDefinition;
   }

   private Collection<ActionableItemArtifact> createActionableItems(IProgressMonitor monitor, double workAmount, SkynetTransaction transaction, TeamDefinitionArtifact teamDefinition) throws OseeCoreException {
      Collection<ActionableItemArtifact> aias = new ArrayList<ActionableItemArtifact>();

      // Create top actionable item
      ActionableItemArtifact topAia =
         (ActionableItemArtifact) ArtifactTypeManager.addArtifact(AtsArtifactTypes.ActionableItem,
            AtsUtil.getAtsBranch(), teamDefName);
      topAia.setSoleAttributeValue(AtsAttributeTypes.Actionable, false);
      topAia.persist(transaction);

      AtsFolderUtil.getFolder(AtsFolder.ActionableItem).addChild(topAia);
      teamDefinition.addRelation(AtsRelationTypes.TeamActionableItem_ActionableItem, topAia);
      teamDefinition.persist(transaction);

      aias.add(topAia);

      // Create children actionable item
      for (String name : actionableItems) {
         ActionableItemArtifact aia =
            (ActionableItemArtifact) ArtifactTypeManager.addArtifact(AtsArtifactTypes.ActionableItem,
               AtsUtil.getAtsBranch(), name);
         aia.setSoleAttributeValue(AtsAttributeTypes.Actionable, true);
         topAia.addChild(aia);
         aia.persist(transaction);
         aias.add(aia);
      }
      return aias;
   }

   private void createVersions(IProgressMonitor monitor, double workAmount, SkynetTransaction transaction, TeamDefinitionArtifact teamDefinition) throws OseeCoreException {
      List<VersionArtifact> versions = new ArrayList<VersionArtifact>();
      if (versionNames != null) {
         for (String name : versionNames) {
            VersionArtifact version =
               (VersionArtifact) ArtifactTypeManager.addArtifact(AtsArtifactTypes.Version, AtsUtil.getAtsBranch(), name);
            teamDefinition.addRelation(AtsRelationTypes.TeamDefinitionToVersion_Version, version);
            versions.add(version);
            version.persist(transaction);
         }
      }
   }

   private WorkFlowDefinition createWorkflow(IProgressMonitor monitor, double workAmount, SkynetTransaction transaction, TeamDefinitionArtifact teamDefinition) throws OseeCoreException {
      Artifact workflowArt =
         ArtifactQuery.checkArtifactFromTypeAndName(CoreArtifactTypes.WorkFlowDefinition, workflowId,
            AtsUtil.getAtsBranch());
      WorkFlowDefinition workFlowDefinition;
      // If can't be found, create a new one
      if (workflowArt == null) {
         WorkflowData workflowData = generateDefaultWorkflow(namespace, transaction, teamDefinition);
         workFlowDefinition = workflowData.getWorkDefinition();
         workflowArt = workflowData.getWorkFlowArtifact();
         workflowArt.persist(transaction);
      }
      // Else, use existing one
      else {
         workFlowDefinition = (WorkFlowDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(workflowId);
      }
      // Relate new team def to workflow artifact
      teamDefinition.addRelation(CoreRelationTypes.WorkItem__Child, workflowArt);
      teamDefinition.persist(transaction);
      return workFlowDefinition;
   }

   public static WorkflowData generateDefaultWorkflow(String namespace, SkynetTransaction transaction, TeamDefinitionArtifact teamDef) throws OseeCoreException {
      // Duplicate default workflow artifact w/ namespace changes
      Artifact defaultWorkflow = WorkItemDefinitionFactory.getWorkItemDefinitionArtifact("osee.ats.teamWorkflow");
      Artifact newWorkflowArt = defaultWorkflow.duplicate(AtsUtil.getAtsBranch());
      for (Attribute<?> attr : newWorkflowArt.getAttributes()) {
         if (attr instanceof StringAttribute) {
            attr.setFromString(attr.getDisplayableString().replaceAll("osee.ats.teamWorkflow", namespace));
         }
      }

      AtsWorkDefinitions.addUpdateWorkItemToDefaultHeirarchy(newWorkflowArt, transaction);
      newWorkflowArt.persist(transaction);

      // Duplicate work pages w/ namespace changes
      for (DefaultTeamState state : DefaultTeamState.values()) {
         Artifact defaultStateArt =
            WorkItemDefinitionFactory.getWorkItemDefinitionArtifact("osee.ats.teamWorkflow." + state.name());
         Artifact newStateArt = defaultStateArt.duplicate(AtsUtil.getAtsBranch());
         for (Attribute<?> attr : newStateArt.getAttributes()) {
            if (attr instanceof StringAttribute) {
               attr.setFromString(attr.getDisplayableString().replaceAll("osee.ats.teamWorkflow", namespace));
            }
         }
         if (state == DefaultTeamState.Completed || state == DefaultTeamState.Cancelled) {
            newStateArt.setSoleAttributeFromString(CoreAttributeTypes.WorkParentId,
               "osee.ats.teamWorkflow." + state.name());
         }

         // Add same relations as default work pages to new work pages (widgets and rules)
         for (Artifact art : defaultStateArt.getRelatedArtifacts(CoreRelationTypes.WorkItem__Child)) {
            newStateArt.addRelation(CoreRelationTypes.WorkItem__Child, art);
         }

         AtsWorkDefinitions.addUpdateWorkItemToDefaultHeirarchy(newStateArt, transaction);
         newStateArt.persist(transaction);
      }
      return new WorkflowData(new WorkFlowDefinition(newWorkflowArt), newWorkflowArt);

   }

   public static final class OpenAtsConfigEditors implements Display {

      @Override
      public void openAtsConfigurationEditors(final TeamDefinitionArtifact teamDef, final Collection<ActionableItemArtifact> aias, final WorkFlowDefinition workFlowDefinition) {
         Job job = new UIJob("Open Ats Configuration Editors") {
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
               AtsUtil.openATSAction(teamDef, AtsOpenOption.OpenAll);
               for (ActionableItemArtifact aia : aias) {
                  AtsUtil.openATSAction(aia, AtsOpenOption.OpenAll);
               }
               try {
                  RendererManager.open(workFlowDefinition.getArtifact(), PresentationType.SPECIALIZED_EDIT, monitor);
               } catch (OseeCoreException ex) {
                  OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
               }
               return Status.OK_STATUS;
            }
         };
         Jobs.startJob(job, true);
      }
   }

   public static IOperation createAtsConfigOperation(String namespace, String teamDefName, Collection<String> versionNames, Collection<String> actionableItems, String workflowId) {
      return new AtsConfigManager(new OpenAtsConfigEditors(), namespace, teamDefName, versionNames, actionableItems,
         workflowId);
   }
}
