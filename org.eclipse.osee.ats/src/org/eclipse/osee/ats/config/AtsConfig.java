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
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.workflow.editor.AtsWorkflowConfigEditor;
import org.eclipse.osee.ats.workflow.editor.wizard.AtsWorkflowConfigCreationWizard;
import org.eclipse.osee.ats.workflow.editor.wizard.AtsWorkflowConfigCreationWizard.WorkflowData;
import org.eclipse.osee.framework.db.connection.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.ats.AtsOpenOption;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinitionFactory;

/**
 * @author Donald G. Dunne
 */
public class AtsConfig {

   private static AtsConfig instance = new AtsConfig();
   public static String FOLDER_ARTIFACT = "Folder";
   public static String ATS_HEADING = "Action Tracking System";
   public static String WORK_FLOWS_FOLDER = "Work Flows";
   public static String WORK_RULES_FOLDER = "Work Rules";
   public static String WORK_WIDGETS_FOLDER = "Work Widgets";
   public static String WORK_PAGES_FOLDER = "Work Pages";
   public static String TEAMS_HEADING = "Teams";
   public static String ACTIONABLE_ITEMS_HEADING = "Actionable Items";

   private AtsConfig() {
      super();
   }

   public static AtsConfig getInstance() {
      return instance;
   }

   public Artifact getOrCreateWorkRulesFolderArtifact(SkynetTransaction transaction) throws OseeCoreException {
      Artifact art = Artifacts.getOrCreateArtifact(AtsPlugin.getAtsBranch(), FOLDER_ARTIFACT, WORK_RULES_FOLDER);
      if (!art.getAttributesToStringList(StaticIdManager.STATIC_ID_ATTRIBUTE).contains(WORK_RULES_FOLDER)) {
         StaticIdManager.setSingletonAttributeValue(art, WORK_RULES_FOLDER);
      }
      validateATSHeadingParent(art, transaction);
      return art;
   }

   public Artifact getOrCreateWorkPagesFolderArtifact(SkynetTransaction transaction) throws OseeCoreException {
      Artifact art = Artifacts.getOrCreateArtifact(AtsPlugin.getAtsBranch(), FOLDER_ARTIFACT, WORK_PAGES_FOLDER);
      if (!art.getAttributesToStringList(StaticIdManager.STATIC_ID_ATTRIBUTE).contains(WORK_PAGES_FOLDER)) {
         StaticIdManager.setSingletonAttributeValue(art, WORK_PAGES_FOLDER);
      }
      validateATSHeadingParent(art, transaction);
      return art;
   }

   public Artifact getOrCreateWorkWidgetsFolderArtifact(SkynetTransaction transaction) throws OseeCoreException {
      Artifact art = Artifacts.getOrCreateArtifact(AtsPlugin.getAtsBranch(), FOLDER_ARTIFACT, WORK_WIDGETS_FOLDER);
      if (!art.getAttributesToStringList(StaticIdManager.STATIC_ID_ATTRIBUTE).contains(WORK_WIDGETS_FOLDER)) {
         StaticIdManager.setSingletonAttributeValue(art, WORK_WIDGETS_FOLDER);
      }
      validateATSHeadingParent(art, transaction);
      return art;
   }

   public Artifact getOrCreateWorkFlowsFolderArtifact(SkynetTransaction transaction) throws OseeCoreException {
      Artifact art = Artifacts.getOrCreateArtifact(AtsPlugin.getAtsBranch(), FOLDER_ARTIFACT, WORK_FLOWS_FOLDER);
      if (!art.getAttributesToStringList(StaticIdManager.STATIC_ID_ATTRIBUTE).contains(WORK_FLOWS_FOLDER)) {
         StaticIdManager.setSingletonAttributeValue(art, WORK_FLOWS_FOLDER);
      }
      validateATSHeadingParent(art, transaction);
      return art;
   }

   public ActionableItemArtifact getOrCreateActionableItemsHeadingArtifact(SkynetTransaction transaction) throws OseeCoreException {
      Artifact art =
            Artifacts.getOrCreateArtifact(AtsPlugin.getAtsBranch(), ActionableItemArtifact.ARTIFACT_NAME,
                  ACTIONABLE_ITEMS_HEADING);
      if (!art.getAttributesToStringList(StaticIdManager.STATIC_ID_ATTRIBUTE).contains(
            ActionableItemArtifact.TOP_AI_STATIC_ID)) {
         StaticIdManager.setSingletonAttributeValue(art, ActionableItemArtifact.TOP_AI_STATIC_ID);
      }
      validateATSHeadingParent(art, transaction);
      return (ActionableItemArtifact) art;
   }

   public TeamDefinitionArtifact getOrCreateTeamsDefinitionArtifact(SkynetTransaction transaction) throws OseeCoreException {
      Artifact art =
            Artifacts.getOrCreateArtifact(AtsPlugin.getAtsBranch(), TeamDefinitionArtifact.ARTIFACT_NAME, TEAMS_HEADING);
      if (!art.getAttributesToStringList(StaticIdManager.STATIC_ID_ATTRIBUTE).contains(
            TeamDefinitionArtifact.TOP_TEAM_STATIC_ID)) {
         StaticIdManager.setSingletonAttributeValue(art, TeamDefinitionArtifact.TOP_TEAM_STATIC_ID);
      }
      validateATSHeadingParent(art, transaction);
      return (TeamDefinitionArtifact) art;
   }

   private void validateATSHeadingParent(Artifact art, SkynetTransaction transaction) {
      try {
         if (!art.hasParent()) {
            Artifact atsHeadingArtifact = getOrCreateAtsHeadingArtifact(transaction);
            atsHeadingArtifact.addChild(art);
            art.persistAttributesAndRelations(transaction);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public Artifact getOrCreateAtsHeadingArtifact(SkynetTransaction transaction) throws OseeCoreException {
      Artifact art = Artifacts.getOrCreateArtifact(AtsPlugin.getAtsBranch(), FOLDER_ARTIFACT, ATS_HEADING);
      if (!art.hasParent()) {
         Artifact rootArt = ArtifactQuery.getDefaultHierarchyRootArtifact(AtsPlugin.getAtsBranch());
         rootArt.addChild(art);
         art.persistAttributesAndRelations(transaction);
      }
      return art;
   }

   /**
    * This method creates a simple configuration of ATS given teamdef name, version names (if desired), actionable items
    * and workflow id.
    * 
    * @param namespace
    * @param teamDefName - name of team definition to use
    * @param versionNames - list of version names (if team is using versions)
    * @param actionableItems - list of actionable items
    * @param workflowId - workflowId to use (if null, workflow will be created)
    * @return Result of creation
    * @throws OseeCoreException
    */
   public static Result configureAtsForDefaultTeam(String namespace, String teamDefName, Collection<String> versionNames, Collection<String> actionableItems, String workflowId) throws OseeCoreException {
      try {
         if (WorkItemDefinitionFactory.getWorkItemDefinition(namespace) != null) {
            return new Result("Configuration Namespace already used, choose a unique namespace.");
         }
      } catch (OseeCoreException ex) {
         // do nothing
      }

      SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());

      // Create team def
      TeamDefinitionArtifact teamDef =
            (TeamDefinitionArtifact) ArtifactTypeManager.addArtifact(TeamDefinitionArtifact.ARTIFACT_NAME,
                  AtsPlugin.getAtsBranch(), teamDefName);
      if (versionNames == null || versionNames.size() > 0) {
         teamDef.setSoleAttributeValue(ATSAttributes.TEAM_USES_VERSIONS_ATTRIBUTE.getStoreName(), true);
      }
      teamDef.addRelation(AtsRelation.TeamLead_Lead, UserManager.getUser());
      teamDef.addRelation(AtsRelation.TeamMember_Member, UserManager.getUser());
      AtsConfig.getInstance().getOrCreateTeamsDefinitionArtifact(transaction).addChild(teamDef);
      teamDef.persistAttributesAndRelations(transaction);

      // Create actionable items
      List<ActionableItemArtifact> aias = new ArrayList<ActionableItemArtifact>();
      // Create top actionable item
      ActionableItemArtifact topAia =
            (ActionableItemArtifact) ArtifactTypeManager.addArtifact(ActionableItemArtifact.ARTIFACT_NAME,
                  AtsPlugin.getAtsBranch(), teamDefName);
      topAia.setSoleAttributeValue(ATSAttributes.ACTIONABLE_ATTRIBUTE.getStoreName(), false);
      topAia.persistAttributesAndRelations(transaction);

      AtsConfig.getInstance().getOrCreateActionableItemsHeadingArtifact(transaction).addChild(topAia);
      teamDef.addRelation(AtsRelation.TeamActionableItem_ActionableItem, topAia);
      teamDef.persistAttributesAndRelations(transaction);

      aias.add(topAia);
      // Create childrent actionable item
      for (String name : actionableItems) {
         ActionableItemArtifact aia =
               (ActionableItemArtifact) ArtifactTypeManager.addArtifact(ActionableItemArtifact.ARTIFACT_NAME,
                     AtsPlugin.getAtsBranch(), name);
         aia.setSoleAttributeValue(ATSAttributes.ACTIONABLE_ATTRIBUTE.getStoreName(), true);
         topAia.addChild(aia);
         aia.persistAttributesAndRelations(transaction);
         aias.add(aia);
      }

      // Create versions
      List<VersionArtifact> versions = new ArrayList<VersionArtifact>();
      if (versionNames != null) {
         for (String name : versionNames) {
            VersionArtifact version =
                  (VersionArtifact) ArtifactTypeManager.addArtifact(VersionArtifact.ARTIFACT_NAME,
                        AtsPlugin.getAtsBranch(), name);
            teamDef.addRelation(AtsRelation.TeamDefinitionToVersion_Version, version);
            versions.add(version);
            version.persistAttributesAndRelations(transaction);
         }
      }

      // create workflow
      Artifact workflowArt = null;
      try {
         workflowArt =
               ArtifactQuery.getArtifactFromTypeAndName(WorkFlowDefinition.ARTIFACT_NAME, workflowId,
                     AtsPlugin.getAtsBranch());
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      }
      WorkFlowDefinition workFlowDefinition;
      // If can't be found, create a new one
      if (workflowArt == null) {
         WorkflowData workflowData =
               AtsWorkflowConfigCreationWizard.generateDefaultWorkflow(namespace, transaction, teamDef);
         workFlowDefinition = workflowData.getWorkDefinition();
         workflowArt = workflowData.getWorkFlowArtifact();
         workflowArt.persistAttributesAndRelations(transaction);
      }
      // Else, use existing one
      else {
         workFlowDefinition = (WorkFlowDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(workflowId);
      }
      // Relate new team def to workflow artifact
      teamDef.addRelation(AtsRelation.WorkItem__Child, workflowArt);
      teamDef.persistAttributesAndRelations(transaction);

      transaction.execute();

      // open everything in editors
      AtsLib.openAtsAction(teamDef, AtsOpenOption.OpenAll);
      for (ActionableItemArtifact aia : aias) {
         AtsLib.openAtsAction(aia, AtsOpenOption.OpenAll);
      }
      AtsWorkflowConfigEditor.editWorkflow(workFlowDefinition);

      return Result.TrueResult;

   }
}
