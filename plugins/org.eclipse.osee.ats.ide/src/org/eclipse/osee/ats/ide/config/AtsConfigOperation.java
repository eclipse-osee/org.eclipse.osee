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

package org.eclipse.osee.ats.ide.config;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.core.config.ActionableItems;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.ide.AtsOpenOption;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.AtsEditors;
import org.eclipse.osee.ats.ide.workdef.AtsWorkDefinitionSheetProviders;
import org.eclipse.osee.ats.ide.workdef.provider.AtsWorkDefinitionImporter;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
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
   private String workDefName;
   private final String teamDefName;
   private final Collection<String> versionNames;
   private final Collection<String> actionableItemsNames;
   private IAtsTeamDefinition teamDef;
   private Collection<IAtsActionableItem> actionableItems;
   private IAtsWorkDefinition workDefinition = null;

   /**
    * @param name = name of work definition if workDefName is not set
    * @param teamDefName - name of team definition to use
    * @param versionNames - list of version names (if team is using versions)
    * @param actionableItems - list of actionable items
    */
   public AtsConfigOperation(String name, String teamDefName, Collection<String> versionNames, Collection<String> actionableItems) {
      super(name, Activator.PLUGIN_ID);
      this.name = name;
      this.teamDefName = teamDefName;
      this.versionNames = versionNames;
      this.actionableItemsNames = actionableItems;
   }

   public IAtsWorkDefinition getWorkDefinition() {
      return workDefinition;
   }

   public Collection<IAtsActionableItem> getActionableItems() {
      return actionableItems;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      monitor.worked(calculateWork(0.10));

      XResultData resultData = new XResultData();
      this.workDefinition = createOrGetWorkflowDefinition(resultData);
      if (resultData.isErrors()) {
         throw new OseeStateException("Error created new Work Definition for Team Def %s", teamDef.toStringWithId());
      }

      IAtsChangeSet changes = AtsClientService.get().createChangeSet(name);

      teamDef = createTeamDefinition(changes, AtsClientService.get());

      // Relate new team def to workflow artifact
      ArtifactToken workDefArt =
         AtsClientService.get().getWorkDefinitionService().getWorkDefArt(workDefinition.getName());
      AtsClientService.get().getWorkDefinitionService().setWorkDefinitionAttrs(teamDef, workDefArt, changes);

      actionableItems = createActionableItems(changes, teamDef, AtsClientService.get());

      createVersions(changes, teamDef);

      changes.execute();
      monitor.worked(calculateWork(0.30));
   }

   private IAtsTeamDefinition createTeamDefinition(IAtsChangeSet changes, AtsApi atsApi) {
      IAtsTeamDefinition teamDef = AtsClientService.get().createTeamDefinition(teamDefName, changes, atsApi);
      changes.relate(TeamDefinitions.getTopTeamDefinition(AtsClientService.get().getQueryService()),
         AtsRelationTypes.TeamMember_Member, AtsClientService.get().getUserService().getCurrentUser());
      changes.relate(TeamDefinitions.getTopTeamDefinition(AtsClientService.get().getQueryService()),
         AtsRelationTypes.TeamLead_Lead, AtsClientService.get().getUserService().getCurrentUser());
      changes.relate(TeamDefinitions.getTopTeamDefinition(AtsClientService.get().getQueryService()),
         CoreRelationTypes.Default_Hierarchical__Child, teamDef);
      return teamDef;
   }

   private Collection<IAtsActionableItem> createActionableItems(IAtsChangeSet changes, IAtsTeamDefinition teamDef, AtsApi atsApi) {
      Collection<IAtsActionableItem> aias = new ArrayList<>();

      // Create top actionable item
      IAtsActionableItem parentAi = AtsClientService.get().createActionableItem(teamDefName, changes, atsApi);
      changes.setSoleAttributeValue(parentAi, AtsAttributeTypes.Actionable, false);
      changes.relate(teamDef, AtsRelationTypes.TeamActionableItem_ActionableItem, parentAi);
      changes.relate(ActionableItems.getTopActionableItem(AtsClientService.get()),
         CoreRelationTypes.Default_Hierarchical__Child, parentAi);

      aias.add(parentAi);

      // Create children actionable item
      for (String name : actionableItemsNames) {
         IAtsActionableItem childAi = AtsClientService.get().createActionableItem(name, changes, atsApi);
         changes.setSoleAttributeValue(childAi, AtsAttributeTypes.Actionable, true);
         changes.addChild(parentAi.getStoreObject(), childAi.getStoreObject());
         aias.add(childAi);
      }

      return aias;
   }

   private void createVersions(IAtsChangeSet changes, IAtsTeamDefinition teamDef) {
      if (versionNames != null) {
         for (String name : versionNames) {
            IAtsVersion version = AtsClientService.get().getVersionService().createVersion(name, changes);
            teamDef.getVersions().add(version);
            AtsClientService.get().getVersionService().setTeamDefinition(version, teamDef, changes);
         }
      }
   }

   private IAtsWorkDefinition createOrGetWorkflowDefinition(XResultData resultData) {
      IAtsWorkDefinition workDef = null;
      if (Strings.isValid(workDefName)) {
         workDef = AtsClientService.get().getWorkDefinitionService().getWorkDefinition(workDefName);
      } else if (Strings.isValid(name)) {
         workDef = AtsClientService.get().getWorkDefinitionService().getWorkDefinition(name);
      }
      // If can't be found, create a new one
      if (workDef == null) {
         IAtsChangeSet changes = AtsClientService.get().getStoreService().createAtsChangeSet(
            "Create Work Definition - " + name, AtsClientService.get().getUserService().getCurrentUser());
         workDef = generateDefaultWorkflow(name, resultData, changes);
         Artifact workDefArt = null;
         try {
            String workDefXml = AtsClientService.get().getWorkDefinitionService().getStorageString(workDef, resultData);
            if (resultData.isErrors()) {
               throw new OseeStateException("Error getting store string for WorkDef [%s]: Errors %s", workDef.getName(),
                  resultData.toString());
            }
            workDefArt = AtsWorkDefinitionImporter.get().importWorkDefinitionToDb(workDefXml, workDef.getName(), name,
               resultData, null, null, changes);
            Artifact folder =
               AtsClientService.get().getQueryServiceClient().getArtifact(AtsArtifactToken.WorkDefinitionsFolder);
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

   private IAtsWorkDefinition generateDefaultWorkflow(String name, XResultData resultData, IAtsChangeSet changes) {
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
                  Artifact teamDefArt = AtsClientService.get().getQueryServiceClient().getArtifact(teamDef);
                  AtsEditors.openATSAction(teamDefArt, AtsOpenOption.OpenAll);
                  for (IAtsActionableItem aia : aias) {
                     AtsEditors.openATSAction(AtsClientService.get().getQueryService().getArtifact(aia),
                        AtsOpenOption.OpenAll);
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

   public String getWorkDefName() {
      return workDefName;
   }

   public void setWorkDefName(String workDefName) {
      this.workDefName = workDefName;
   }

}
