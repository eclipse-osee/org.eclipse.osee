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
import org.eclipse.osee.ats.core.config.ActionableItemArtifact;
import org.eclipse.osee.ats.core.config.AtsArtifactToken;
import org.eclipse.osee.ats.core.config.TeamDefinitionArtifact;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.type.AtsRelationTypes;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.workdef.ConvertAtsDslToWorkDefinition;
import org.eclipse.osee.ats.core.workdef.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.WorkDefinitionFactory;
import org.eclipse.osee.ats.core.workdef.WorkDefinitionMatch;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDsl;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.workdef.AtsWorkDefinitionSheetProviders;
import org.eclipse.osee.ats.workdef.provider.AtsWorkDefinitionProvider;
import org.eclipse.osee.ats.workdef.provider.ConvertWorkDefinitionToAtsDsl;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.ui.progress.UIJob;

/**
 * This class creates a simple configuration of ATS given team definition name, version names (if desired), actionable
 * items and workflow id.
 * 
 * @author Donald G. Dunne
 */
public class AtsConfigManager extends AbstractOperation {

   public static interface Display {
      public void openAtsConfigurationEditors(TeamDefinitionArtifact teamDef, Collection<ActionableItemArtifact> aias, WorkDefinition workDefinition);
   }

   private final String name;
   private final String teamDefName;
   private final Collection<String> versionNames;
   private final Collection<String> actionableItems;
   private final Display display;

   /**
    * @param teamDefName - name of team definition to use
    * @param versionNames - list of version names (if team is using versions)
    * @param actionableItems - list of actionable items
    */
   public AtsConfigManager(Display display, String name, String teamDefName, Collection<String> versionNames, Collection<String> actionableItems) {
      super("Configure Ats", AtsPlugin.PLUGIN_ID);
      this.name = name;
      this.teamDefName = teamDefName;
      this.versionNames = versionNames;
      this.actionableItems = actionableItems;
      this.display = display;
   }

   private void checkWorkItemNamespaceUnique() throws OseeCoreException {
      WorkDefinitionMatch match = WorkDefinitionFactory.getWorkDefinition(name);
      if (match.isMatched()) {
         throw new OseeArgumentException(String.format(
            "Configuration Namespace [%s] already used, choose a unique namespace.", name));
      }
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      checkWorkItemNamespaceUnique();
      monitor.worked(calculateWork(0.10));

      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Configure ATS for Default Team");

      TeamDefinitionArtifact teamDefinition = createTeamDefinition(transaction);

      Collection<ActionableItemArtifact> actionableItems = createActionableItems(transaction, teamDefinition);

      createVersions(transaction, teamDefinition);

      XResultData resultData = new XResultData();
      WorkDefinition workDefinition = createWorkflow(transaction, resultData, teamDefinition);

      transaction.execute();
      monitor.worked(calculateWork(0.30));

      display.openAtsConfigurationEditors(teamDefinition, actionableItems, workDefinition);
      monitor.worked(calculateWork(0.10));
   }

   private TeamDefinitionArtifact createTeamDefinition(SkynetTransaction transaction) throws OseeCoreException {
      TeamDefinitionArtifact teamDefinition =
         (TeamDefinitionArtifact) ArtifactTypeManager.addArtifact(AtsArtifactTypes.TeamDefinition,
            AtsUtil.getAtsBranch(), teamDefName);
      if (versionNames == null || versionNames.size() > 0) {
         teamDefinition.setSoleAttributeValue(AtsAttributeTypes.TeamUsesVersions, true);
      }
      teamDefinition.addRelation(AtsRelationTypes.TeamLead_Lead, UserManager.getUser());
      teamDefinition.addRelation(AtsRelationTypes.TeamMember_Member, UserManager.getUser());
      AtsUtilCore.getFromToken(AtsArtifactToken.TopTeamDefinition).addChild(teamDefinition);
      teamDefinition.persist(transaction);
      return teamDefinition;
   }

   private Collection<ActionableItemArtifact> createActionableItems(SkynetTransaction transaction, TeamDefinitionArtifact teamDefinition) throws OseeCoreException {
      Collection<ActionableItemArtifact> aias = new ArrayList<ActionableItemArtifact>();

      // Create top actionable item
      ActionableItemArtifact topAia =
         (ActionableItemArtifact) ArtifactTypeManager.addArtifact(AtsArtifactTypes.ActionableItem,
            AtsUtil.getAtsBranch(), teamDefName);
      topAia.setSoleAttributeValue(AtsAttributeTypes.Actionable, false);
      topAia.persist(transaction);

      AtsUtilCore.getFromToken(AtsArtifactToken.TopActionableItem).addChild(topAia);
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

   private void createVersions(SkynetTransaction transaction, TeamDefinitionArtifact teamDefinition) throws OseeCoreException {
      List<Artifact> versions = new ArrayList<Artifact>();
      if (versionNames != null) {
         for (String name : versionNames) {
            Artifact version = ArtifactTypeManager.addArtifact(AtsArtifactTypes.Version, AtsUtil.getAtsBranch(), name);
            teamDefinition.addRelation(AtsRelationTypes.TeamDefinitionToVersion_Version, version);
            versions.add(version);
            version.persist(transaction);
         }
      }
   }

   private WorkDefinition createWorkflow(SkynetTransaction transaction, XResultData resultData, TeamDefinitionArtifact teamDefinition) throws OseeCoreException {
      WorkDefinitionMatch workDefMatch = WorkDefinitionFactory.getWorkDefinition(name);
      WorkDefinition workDef = null;
      // If can't be found, create a new one
      if (!workDefMatch.isMatched()) {
         workDef = generateDefaultWorkflow(name, resultData, transaction, teamDefinition);
         String workDefXml = AtsWorkDefinitionProvider.get().workFlowDefinitionToString(workDef, resultData);
         Artifact workDefArt =
            AtsWorkDefinitionProvider.get().importWorkDefinitionToDb(workDefXml, workDef.getName(), name, resultData,
               transaction);

         Artifact folder = AtsUtilCore.getFromToken(AtsArtifactToken.WorkDefinitionsFolder);
         folder.addChild(workDefArt);
         folder.persist(transaction);
      } else {
         workDef = workDefMatch.getWorkDefinition();
      }
      // Relate new team def to workflow artifact
      teamDefinition.setSoleAttributeValue(AtsAttributeTypes.WorkflowDefinition, workDef.getIds().iterator().next());
      teamDefinition.persist(transaction);

      return workDef;
   }

   public static WorkDefinition generateDefaultWorkflow(String name, XResultData resultData, SkynetTransaction transaction, TeamDefinitionArtifact teamDef) throws OseeCoreException {

      WorkDefinition defaultWorkDef =
         WorkDefinitionFactory.getWorkDefinition(AtsWorkDefinitionSheetProviders.WORK_DEF_TEAM_DEFAULT).getWorkDefinition();

      // Duplicate default team workflow definition w/ namespace changes
      ConvertWorkDefinitionToAtsDsl converter = new ConvertWorkDefinitionToAtsDsl(defaultWorkDef, resultData);
      AtsDsl atsDsl = converter.convert(name);

      // Convert back to WorkDefinition
      ConvertAtsDslToWorkDefinition converter2 = new ConvertAtsDslToWorkDefinition(name, atsDsl);
      WorkDefinition newWorkDef = converter2.convert();
      newWorkDef.getIds().clear();
      newWorkDef.getIds().add(name);
      newWorkDef.setName(name);
      return newWorkDef;

   }

   public static final class OpenAtsConfigEditors implements Display {

      @Override
      public void openAtsConfigurationEditors(final TeamDefinitionArtifact teamDef, final Collection<ActionableItemArtifact> aias, final WorkDefinition workDefinition) {
         Job job = new UIJob("Open Ats Configuration Editors") {
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
               AtsUtil.openATSAction(teamDef, AtsOpenOption.OpenAll);
               for (ActionableItemArtifact aia : aias) {
                  AtsUtil.openATSAction(aia, AtsOpenOption.OpenAll);
               }
               try {
                  RendererManager.open(ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.WorkDefinition,
                     workDefinition.getName(), AtsUtil.getAtsBranch()), PresentationType.SPECIALIZED_EDIT, monitor);
               } catch (OseeCoreException ex) {
                  OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
               }
               return Status.OK_STATUS;
            }
         };
         Jobs.startJob(job, true);
      }
   }

   public static IOperation createAtsConfigOperation(String name, String teamDefName, Collection<String> versionNames, Collection<String> actionableItems) {
      return new AtsConfigManager(new OpenAtsConfigEditors(), name, teamDefName, versionNames, actionableItems);
   }
}
