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
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.AtsOpenOption;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.core.client.util.AtsUtilClient;
import org.eclipse.osee.ats.core.config.ActionableItems;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.workdef.AtsWorkDefinitionSheetProviders;
import org.eclipse.osee.ats.workdef.provider.AtsWorkDefinitionImporter;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.ui.progress.UIJob;

/**
 * This class creates a simple configuration of ATS given team definition name, version names (if desired), actionable
 * items and workflow id.
 *
 * @author Donald G. Dunne
 */
public class AtsConfigOperation extends AbstractOperation {

   public static interface Display {
      public void openAtsConfigurationEditors(IAtsTeamDefinition teamDef, Collection<IAtsActionableItem> aias, IAtsWorkDefinition workDefinition);
   }

   private final String name;
   private final String teamDefName;
   private final Collection<String> versionNames;
   private final Collection<String> actionableItemsNames;
   private IAtsTeamDefinition teamDef;
   private Collection<IAtsActionableItem> actionableItems;
   private IAtsWorkDefinition workDefinition = null;
   private ArtifactToken teamDefToken = null;
   private ArtifactToken actionableItemToken = null;

   /**
    * @param teamDefName - name of team definition to use
    * @param versionNames - list of version names (if team is using versions)
    * @param actionableItems - list of actionable items
    */
   public AtsConfigOperation(String name, String teamDefName, Collection<String> versionNames, Collection<String> actionableItems) {
      super("Configure Ats", Activator.PLUGIN_ID);
      this.name = name;
      this.teamDefName = teamDefName;
      this.versionNames = versionNames;
      this.actionableItemsNames = actionableItems;
   }

   public AtsConfigOperation(String name, ArtifactToken teamDefToken, Collection<String> versionNames, ArtifactToken actionableItemToken) {
      this(name, teamDefToken.getName(), versionNames, Arrays.asList(actionableItemToken.getName()));
      this.teamDefToken = teamDefToken;
      this.actionableItemToken = actionableItemToken;
   }

   private void checkWorkItemNamespaceUnique() throws OseeCoreException {
      Artifact workDefArt = ArtifactQuery.getArtifactFromTypeAndNameNoException(AtsArtifactTypes.WorkDefinition, name,
         AtsClientService.get().getAtsBranch());
      if (workDefArt != null) {
         throw new OseeArgumentException(
            String.format("Configuration Namespace [%s] already used, choose a unique namespace.", name));
      }
   }

   public IAtsWorkDefinition getWorkDefinition() {
      return workDefinition;
   }

   public Collection<IAtsActionableItem> getActionableItems() {
      return actionableItems;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      checkWorkItemNamespaceUnique();
      monitor.worked(calculateWork(0.10));

      XResultData resultData = new XResultData();
      this.workDefinition = createWorkflowDefinition(resultData);
      if (resultData.isErrors()) {
         throw new OseeStateException("Error created new Work Definition for Team Def %s", teamDef.toStringWithId());
      }

      IAtsChangeSet changes = AtsClientService.get().createChangeSet("Configure ATS for Default Team");

      teamDef = createTeamDefinition(changes, AtsClientService.get());
      // Relate new team def to workflow artifact
      changes.setSoleAttributeValue(teamDef, AtsAttributeTypes.WorkflowDefinition, workDefinition.getName());

      actionableItems = createActionableItems(changes, teamDef, AtsClientService.get());

      createVersions(changes, teamDef);

      changes.execute();
      monitor.worked(calculateWork(0.30));
   }

   private IAtsTeamDefinition createTeamDefinition(IAtsChangeSet changes, IAtsServices services) throws OseeCoreException {
      IAtsTeamDefinition teamDef = null;
      if (teamDefToken == null) {
         teamDef = AtsClientService.get().createTeamDefinition(teamDefName, changes, services);
      } else {
         teamDef = AtsClientService.get().createTeamDefinition(teamDefToken.getName(), teamDefToken.getId(), changes,
            services);
      }
      changes.relate(TeamDefinitions.getTopTeamDefinition(AtsClientService.get().getQueryService()),
         AtsRelationTypes.TeamMember_Member, AtsClientService.get().getUserService().getCurrentUser());
      changes.relate(TeamDefinitions.getTopTeamDefinition(AtsClientService.get().getQueryService()),
         AtsRelationTypes.TeamLead_Lead, AtsClientService.get().getUserService().getCurrentUser());
      changes.relate(TeamDefinitions.getTopTeamDefinition(AtsClientService.get().getQueryService()),
         CoreRelationTypes.Default_Hierarchical__Child, teamDef);
      return teamDef;
   }

   private Collection<IAtsActionableItem> createActionableItems(IAtsChangeSet changes, IAtsTeamDefinition safetyTeamDef, IAtsServices services) throws OseeCoreException {
      Collection<IAtsActionableItem> aias = new ArrayList<>();

      // Create top actionable item
      IAtsActionableItem safetyAi = AtsClientService.get().createActionableItem(teamDefName, changes, services);
      changes.setSoleAttributeValue(safetyAi, AtsAttributeTypes.Actionable, false);
      changes.relate(safetyTeamDef, AtsRelationTypes.TeamActionableItem_ActionableItem, safetyAi);
      changes.relate(ActionableItems.getTopActionableItem(AtsClientService.get()),
         CoreRelationTypes.Default_Hierarchical__Child, safetyAi);

      aias.add(safetyAi);

      // Create children actionable item
      if (actionableItemToken == null) {
         for (String name : actionableItemsNames) {
            IAtsActionableItem childAi = AtsClientService.get().createActionableItem(name, changes, services);
            addChildAi(safetyAi, childAi, changes, aias);
         }
      } else {
         IAtsActionableItem childAi = AtsClientService.get().createActionableItem(actionableItemToken.getName(),
            actionableItemToken.getId(), changes, services);
         addChildAi(safetyAi, childAi, changes, aias);
      }
      return aias;
   }

   private void addChildAi(IAtsActionableItem topAia, IAtsActionableItem childAi, IAtsChangeSet changes, Collection<IAtsActionableItem> aias) {
      changes.setSoleAttributeValue(childAi, AtsAttributeTypes.Actionable, true);
      changes.relate(topAia, CoreRelationTypes.Default_Hierarchical__Child, childAi);
      aias.add(childAi);
   }

   private void createVersions(IAtsChangeSet changes, IAtsTeamDefinition teamDef) throws OseeCoreException {
      if (versionNames != null) {
         for (String name : versionNames) {
            IAtsVersion version = AtsClientService.get().getVersionService().createVersion(name, changes);
            teamDef.getVersions().add(version);
            AtsClientService.get().getVersionService().setTeamDefinition(version, teamDef, changes);
         }
      }
   }

   private IAtsWorkDefinition createWorkflowDefinition(XResultData resultData) throws OseeCoreException {
      IAtsWorkDefinition workDef = AtsClientService.get().getWorkDefinitionService().getWorkDefinition(name);
      // If can't be found, create a new one
      if (workDef == null) {
         IAtsChangeSet changes = AtsClientService.get().getStoreService().createAtsChangeSet(
            "Create Work Definition - " + name, AtsClientService.get().getUserService().getCurrentUser());
         workDef = generateDefaultWorkflow(name, resultData, changes);
         Artifact workDefArt = null;
         try {
            String workDefXml = AtsClientService.get().getWorkDefinitionService().getStorageString(workDef, resultData);
            workDefArt = AtsWorkDefinitionImporter.get().importWorkDefinitionToDb(workDefXml, workDef.getName(), name,
               null, resultData, changes);
            Artifact folder = AtsUtilClient.getFromToken(AtsArtifactToken.WorkDefinitionsFolder);
            folder.addChild(workDefArt);
            changes.add(folder);
         } catch (Exception ex) {
            throw new OseeWrappedException(ex);
         }
         TransactionId transactionId = changes.execute();
         if (transactionId == null || !workDefArt.isInDb()) {
            throw new OseeStateException("Work Def didn't persist");
         }
         AtsClientService.get().getWorkDefinitionService().addWorkDefinition(workDef);
      }
      return workDef;
   }

   private IAtsWorkDefinition generateDefaultWorkflow(String name, XResultData resultData, IAtsChangeSet changes) throws OseeCoreException {
      IAtsWorkDefinition defaultWorkDef = AtsClientService.get().getWorkDefinitionService().getWorkDefinition(
         AtsWorkDefinitionSheetProviders.WORK_DEF_TEAM_DEFAULT);

      // Duplicate default team workflow definition w/ namespace changes

      IAtsWorkDefinition newWorkDef =
         AtsClientService.get().getWorkDefinitionService().copyWorkDefinition(name, defaultWorkDef, resultData);
      return newWorkDef;
   }

   public static final class OpenAtsConfigEditors implements Display {

      @Override
      public void openAtsConfigurationEditors(final IAtsTeamDefinition teamDef, final Collection<IAtsActionableItem> aias, final IAtsWorkDefinition workDefinition) {
         Job job = new UIJob("Open Ats Configuration Editors") {
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
               try {
                  Artifact teamDefArt = AtsClientService.get().getConfigArtifact(teamDef);
                  AtsUtil.openATSAction(teamDefArt, AtsOpenOption.OpenAll);
                  for (IAtsActionableItem aia : aias) {
                     AtsUtil.openATSAction(AtsClientService.get().getConfigArtifact(aia), AtsOpenOption.OpenAll);
                  }
                  RendererManager.open(ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.WorkDefinition,
                     workDefinition.getName(), AtsClientService.get().getAtsBranch()),
                     PresentationType.SPECIALIZED_EDIT, monitor);
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
               return Status.OK_STATUS;
            }
         };
         Jobs.startJob(job, true);
      }
   }

   public IAtsTeamDefinition getTeamDefinition() {
      return teamDef;
   }

}
