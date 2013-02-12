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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.AtsOpenOption;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.core.client.AtsClient;
import org.eclipse.osee.ats.core.client.config.AtsArtifactToken;
import org.eclipse.osee.ats.core.client.config.store.ActionableItemArtifactStore;
import org.eclipse.osee.ats.core.client.config.store.TeamDefinitionArtifactStore;
import org.eclipse.osee.ats.core.client.config.store.VersionArtifactStore;
import org.eclipse.osee.ats.core.client.util.AtsUsersClient;
import org.eclipse.osee.ats.core.client.util.AtsUtilCore;
import org.eclipse.osee.ats.core.client.workdef.AtsWorkDefinitionStore;
import org.eclipse.osee.ats.core.config.AtsConfigCache;
import org.eclipse.osee.ats.core.config.AtsVersionService;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.core.workdef.AtsWorkDefinitionService;
import org.eclipse.osee.ats.core.workdef.WorkDefinitionMatch;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.workdef.AtsWorkDefinitionSheetProviders;
import org.eclipse.osee.ats.workdef.provider.AtsWorkDefinitionImporter;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
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
   private final Display display;
   private IAtsTeamDefinition teamDefinition;
   private Collection<IAtsActionableItem> actionableItems;

   /**
    * @param teamDefName - name of team definition to use
    * @param versionNames - list of version names (if team is using versions)
    * @param actionableItems - list of actionable items
    */
   public AtsConfigOperation(Display display, String name, String teamDefName, Collection<String> versionNames, Collection<String> actionableItems) {
      super("Configure Ats", Activator.PLUGIN_ID);
      this.name = name;
      this.teamDefName = teamDefName;
      this.versionNames = versionNames;
      this.actionableItemsNames = actionableItems;
      this.display = display;
   }

   private void checkWorkItemNamespaceUnique() throws OseeCoreException {
      WorkDefinitionMatch match = null;
      try {
         match = AtsClient.getWorkDefFactory().getWorkDefinition(name);
      } catch (Exception ex) {
         return;
      }
      if (match.isMatched()) {
         throw new OseeArgumentException(String.format(
            "Configuration Namespace [%s] already used, choose a unique namespace.", name));
      }
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      checkWorkItemNamespaceUnique();
      monitor.worked(calculateWork(0.10));

      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsUtil.getAtsBranch(), "Configure ATS for Default Team");

      teamDefinition = createTeamDefinition(transaction);

      actionableItems = createActionableItems(transaction, teamDefinition);

      createVersions(transaction, teamDefinition);

      XResultData resultData = new XResultData();
      IAtsWorkDefinition workDefinition = createWorkflow(transaction, resultData, teamDefinition);

      transaction.execute();
      monitor.worked(calculateWork(0.30));

      display.openAtsConfigurationEditors(teamDefinition, actionableItems, workDefinition);
      monitor.worked(calculateWork(0.10));
   }

   private IAtsTeamDefinition createTeamDefinition(SkynetTransaction transaction) throws OseeCoreException {
      IAtsTeamDefinition teamDef =
         AtsConfigCache.instance.getTeamDefinitionFactory().createTeamDefinition(GUID.create(), teamDefName);
      teamDef.getLeads().add(AtsUsersClient.getUser());
      teamDef.getMembers().add(AtsUsersClient.getUser());
      TeamDefinitions.getTopTeamDefinition().getChildrenTeamDefinitions().add(teamDef);
      TeamDefinitionArtifactStore teamDefStore = new TeamDefinitionArtifactStore(teamDef);
      teamDefStore.getArtifactOrCreate(transaction);
      teamDefStore.saveToArtifact(transaction);
      AtsConfigCache.instance.cache(teamDef);
      return teamDef;
   }

   private Collection<IAtsActionableItem> createActionableItems(SkynetTransaction transaction, IAtsTeamDefinition teamDef) throws OseeCoreException {
      Collection<IAtsActionableItem> aias = new ArrayList<IAtsActionableItem>();

      // Create top actionable item
      IAtsActionableItem topAia =
         AtsConfigCache.instance.getActionableItemFactory().createActionableItem(GUID.create(), teamDefName);
      topAia.setActionable(false);
      topAia.setTeamDefinition(teamDef);
      ActionableItemArtifactStore topAiaStore = new ActionableItemArtifactStore(topAia);
      topAiaStore.getArtifactOrCreate(transaction);
      topAiaStore.saveToArtifact(transaction);

      teamDef.getActionableItems().add(topAia);
      new TeamDefinitionArtifactStore(teamDef).saveToArtifact(transaction);

      aias.add(topAia);

      // Create children actionable item
      for (String name : actionableItemsNames) {
         IAtsActionableItem childAi =
            AtsConfigCache.instance.getActionableItemFactory().createActionableItem(GUID.create(), name);
         childAi.setActionable(true);
         topAia.getChildrenActionableItems().add(childAi);
         childAi.setParentActionableItem(topAia);
         ActionableItemArtifactStore childAiStore = new ActionableItemArtifactStore(childAi);
         childAiStore.getArtifactOrCreate(transaction);
         childAiStore.saveToArtifact(transaction);
         aias.add(childAi);
      }
      topAiaStore.saveToArtifact(transaction);
      return aias;
   }

   private void createVersions(SkynetTransaction transaction, IAtsTeamDefinition teamDef) throws OseeCoreException {
      if (versionNames != null) {
         for (String name : versionNames) {
            IAtsVersion version = AtsConfigCache.instance.getVersionFactory().createVersion(name);
            teamDef.getVersions().add(version);
            VersionArtifactStore verStore = new VersionArtifactStore(version);
            verStore.getArtifactOrCreate(transaction);
            verStore.saveToArtifact(transaction);
            AtsVersionService.get().setTeamDefinition(version, teamDef);
         }
      }
   }

   private IAtsWorkDefinition createWorkflow(SkynetTransaction transaction, XResultData resultData, IAtsTeamDefinition teamDef) throws OseeCoreException {
      WorkDefinitionMatch workDefMatch = AtsClient.getWorkDefFactory().getWorkDefinition(name);
      IAtsWorkDefinition workDef = null;
      // If can't be found, create a new one
      if (!workDefMatch.isMatched()) {
         workDef = generateDefaultWorkflow(name, resultData, transaction, teamDef);
         try {
            String workDefXml = AtsWorkDefinitionService.getService().getStorageString(workDef, resultData);
            Artifact workDefArt =
               AtsWorkDefinitionImporter.get().importWorkDefinitionToDb(workDefXml, workDef.getName(), name,
                  resultData, transaction);
            Artifact folder = AtsUtilCore.getFromToken(AtsArtifactToken.WorkDefinitionsFolder);
            folder.addChild(workDefArt);
            folder.persist(transaction);
         } catch (Exception ex) {
            throw new OseeWrappedException(ex);
         }
      } else {
         workDef = workDefMatch.getWorkDefinition();
      }
      // Relate new team def to workflow artifact
      teamDef.setWorkflowDefinition(workDef.getId());
      new TeamDefinitionArtifactStore(teamDef).saveToArtifact(transaction);
      return workDef;
   }

   private IAtsWorkDefinition generateDefaultWorkflow(String name, XResultData resultData, SkynetTransaction transaction, IAtsTeamDefinition teamDef) {
      IAtsWorkDefinition defaultWorkDef =
         AtsClient.getWorkDefFactory().getWorkDefinition(AtsWorkDefinitionSheetProviders.WORK_DEF_TEAM_DEFAULT).getWorkDefinition();

      // Duplicate default team workflow definition w/ namespace changes

      IAtsWorkDefinition newWorkDef =
         AtsWorkDefinitionService.getService().copyWorkDefinition(name, defaultWorkDef, resultData,
            AtsWorkDefinitionStore.getInstance().getAttributeResolver(),
            AtsWorkDefinitionStore.getInstance().getUserResolver());
      return newWorkDef;
   }

   public static final class OpenAtsConfigEditors implements Display {

      @Override
      public void openAtsConfigurationEditors(final IAtsTeamDefinition teamDef, final Collection<IAtsActionableItem> aias, final IAtsWorkDefinition workDefinition) {
         Job job = new UIJob("Open Ats Configuration Editors") {
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
               try {
                  Artifact teamDefArt = new TeamDefinitionArtifactStore(teamDef).getArtifact();
                  AtsUtil.openATSAction(teamDefArt, AtsOpenOption.OpenAll);
                  for (IAtsActionableItem aia : aias) {
                     AtsUtil.openATSAction(new ActionableItemArtifactStore(aia).getArtifact(), AtsOpenOption.OpenAll);
                  }
                  RendererManager.open(ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.WorkDefinition,
                     workDefinition.getName(), AtsUtil.getAtsBranch()), PresentationType.SPECIALIZED_EDIT, monitor);
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
      return teamDefinition;
   }

}
