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

import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Donald G. Dunne
 */
public class AtsConfig {

   private static AtsConfig instance = new AtsConfig();
   public static String FOLDER_ARTIFACT = "Folder";
   public static String ATS_HEADING = "Action Tracking System";
   public static String MSA_TOOLS_HEADING = "MSA Tools";
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
      if (!art.getAttributesToStringList(StaticIdQuery.STATIC_ID_ATTRIBUTE).contains(WORK_RULES_FOLDER)) {
         art.addAttribute(StaticIdQuery.STATIC_ID_ATTRIBUTE, WORK_RULES_FOLDER);
      }
      validateATSHeadingParent(art, transaction);
      return art;
   }

   public Artifact getOrCreateWorkPagesFolderArtifact(SkynetTransaction transaction) throws OseeCoreException {
      Artifact art = Artifacts.getOrCreateArtifact(AtsPlugin.getAtsBranch(), FOLDER_ARTIFACT, WORK_PAGES_FOLDER);
      if (!art.getAttributesToStringList(StaticIdQuery.STATIC_ID_ATTRIBUTE).contains(WORK_PAGES_FOLDER)) {
         art.addAttribute(StaticIdQuery.STATIC_ID_ATTRIBUTE, WORK_PAGES_FOLDER);
      }
      validateATSHeadingParent(art, transaction);
      return art;
   }

   public Artifact getOrCreateWorkWidgetsFolderArtifact(SkynetTransaction transaction) throws OseeCoreException {
      Artifact art = Artifacts.getOrCreateArtifact(AtsPlugin.getAtsBranch(), FOLDER_ARTIFACT, WORK_WIDGETS_FOLDER);
      if (!art.getAttributesToStringList(StaticIdQuery.STATIC_ID_ATTRIBUTE).contains(WORK_WIDGETS_FOLDER)) {
         art.addAttribute(StaticIdQuery.STATIC_ID_ATTRIBUTE, WORK_WIDGETS_FOLDER);
      }
      validateATSHeadingParent(art, transaction);
      return art;
   }

   public Artifact getOrCreateWorkFlowsFolderArtifact(SkynetTransaction transaction) throws OseeCoreException {
      Artifact art = Artifacts.getOrCreateArtifact(AtsPlugin.getAtsBranch(), FOLDER_ARTIFACT, WORK_FLOWS_FOLDER);
      if (!art.getAttributesToStringList(StaticIdQuery.STATIC_ID_ATTRIBUTE).contains(WORK_FLOWS_FOLDER)) {
         art.addAttribute(StaticIdQuery.STATIC_ID_ATTRIBUTE, WORK_FLOWS_FOLDER);
      }
      validateATSHeadingParent(art, transaction);
      return art;
   }

   public ActionableItemArtifact getOrCreateActionableItemsHeadingArtifact(SkynetTransaction transaction) throws OseeCoreException {
      Artifact art =
            Artifacts.getOrCreateArtifact(AtsPlugin.getAtsBranch(), ActionableItemArtifact.ARTIFACT_NAME,
                  ACTIONABLE_ITEMS_HEADING);
      if (!art.getAttributesToStringList(StaticIdQuery.STATIC_ID_ATTRIBUTE).contains(
            ActionableItemArtifact.TOP_AI_STATIC_ID)) {
         art.addAttribute(StaticIdQuery.STATIC_ID_ATTRIBUTE, ActionableItemArtifact.TOP_AI_STATIC_ID);
      }
      validateATSHeadingParent(art, transaction);
      return (ActionableItemArtifact) art;
   }

   public TeamDefinitionArtifact getOrCreateTeamsDefinitionArtifact(SkynetTransaction transaction) throws OseeCoreException {
      Artifact art =
            Artifacts.getOrCreateArtifact(AtsPlugin.getAtsBranch(), TeamDefinitionArtifact.ARTIFACT_NAME, TEAMS_HEADING);
      if (!art.getAttributesToStringList(StaticIdQuery.STATIC_ID_ATTRIBUTE).contains(
            TeamDefinitionArtifact.TOP_TEAM_STATIC_ID)) {
         art.addAttribute(StaticIdQuery.STATIC_ID_ATTRIBUTE, TeamDefinitionArtifact.TOP_TEAM_STATIC_ID);
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
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
   }

   public Artifact getOrCreateAtsHeadingArtifact(SkynetTransaction transaction) throws OseeCoreException {
      Artifact art = Artifacts.getOrCreateArtifact(AtsPlugin.getAtsBranch(), FOLDER_ARTIFACT, ATS_HEADING);
      if (!art.hasParent()) {
         Artifact rootArt = ArtifactPersistenceManager.getDefaultHierarchyRootArtifact(AtsPlugin.getAtsBranch());
         rootArt.addChild(art);
         art.persistAttributesAndRelations(transaction);
      }
      return art;
   }

   //   public Artifact getOrCreateMsaToolsHeadingArtifact(SkynetTransaction transaction) throws OseeCoreException {
   //      Artifact art = Artifacts.getOrCreateArtifact(AtsPlugin.getAtsBranch(), FOLDER_ARTIFACT, MSA_TOOLS_HEADING);
   //      if (art.getParent() == null) {
   //         try {
   //            Artifact rootArt = ArtifactPersistenceManager.getDefaultHierarchyRootArtifact(AtsPlugin.getAtsBranch());
   //            rootArt.addChild(art);
   //            art.persistAttributesAndRelations(transaction);
   //         } catch (Exception ex) {
   //            OSEELog.logException(AtsPlugin.class, ex, true);
   //         }
   //      }
   //      return art;
   //   }

}
