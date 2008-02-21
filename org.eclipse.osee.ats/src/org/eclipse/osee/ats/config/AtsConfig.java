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

import java.sql.SQLException;
import java.util.Collection;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.NativeArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactStaticIdSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeNameSearch;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Donald G. Dunne
 */
public class AtsConfig {

   private static AtsConfig instance = new AtsConfig();
   public static String HEADING_ARTIFACT = "Heading";
   public static String ATS_HEADING = "Action Tracking System";
   public static String MSA_TOOLS_HEADING = "MSA Tools";
   public static String WORKFLOW_DIAGRAMS_HEADING = "Workflow Diagrams";
   public static String TEAMS_HEADING = "Teams";
   public static String ACTIONABLE_ITEMS_HEADING = "Actionable Items";

   private AtsConfig() {
      super();
   }

   public static AtsConfig getInstance() {
      return instance;
   }

   public ActionableItemArtifact getOrCreateActionableItemsHeadingArtifact() throws SQLException {
      Artifact art = getOrCreateHeadingArtifact(ActionableItemArtifact.ARTIFACT_NAME, ACTIONABLE_ITEMS_HEADING);
      if (!art.getAttributesToStringCollection(ArtifactStaticIdSearch.STATIC_ID_ATTRIBUTE).contains(
            ActionableItemArtifact.TOP_AI_STATIC_ID)) {
         art.getAttributeManager(ArtifactStaticIdSearch.STATIC_ID_ATTRIBUTE).getNewAttribute().setVarchar(
               ActionableItemArtifact.TOP_AI_STATIC_ID);
      }
      validateATSHeadingParent(art);
      return (ActionableItemArtifact) art;
   }

   public TeamDefinitionArtifact getOrCreateTeamsDefinitionArtifact() throws SQLException {
      Artifact art = getOrCreateHeadingArtifact(TeamDefinitionArtifact.ARTIFACT_NAME, TEAMS_HEADING);
      if (!art.getAttributesToStringCollection(ArtifactStaticIdSearch.STATIC_ID_ATTRIBUTE).contains(
            TeamDefinitionArtifact.TOP_TEAM_STATIC_ID)) {
         art.getAttributeManager(ArtifactStaticIdSearch.STATIC_ID_ATTRIBUTE).getNewAttribute().setVarchar(
               TeamDefinitionArtifact.TOP_TEAM_STATIC_ID);
      }
      validateATSHeadingParent(art);
      return (TeamDefinitionArtifact) art;
   }

   public Artifact getOrCreateWorkflowDiagramsArtifact() throws SQLException {
      Artifact art = getOrCreateHeadingArtifact(HEADING_ARTIFACT, WORKFLOW_DIAGRAMS_HEADING);
      validateATSHeadingParent(art);
      return art;
   }

   private void validateATSHeadingParent(Artifact art) {
      try {
         if (art.getParent() == null) {

            Artifact atsHeadingArtifact = getOrCreateAtsHeadingArtifact();
            atsHeadingArtifact.addChild(art);
            atsHeadingArtifact.persist(true);
         }
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
   }

   public Artifact getOrCreateAtsHeadingArtifact() throws SQLException {
      Artifact art = getOrCreateHeadingArtifact(HEADING_ARTIFACT, ATS_HEADING);
      if (art.getParent() == null) {
         try {
            Artifact rootArt =
                  ArtifactPersistenceManager.getInstance().getDefaultHierarchyRootArtifact(
                        BranchPersistenceManager.getInstance().getAtsBranch(), true);
            rootArt.addChild(art);
            rootArt.persist(true);
         } catch (SQLException ex) {
            OSEELog.logException(AtsPlugin.class, ex, true);
         }
      }
      return art;
   }

   public Artifact getOrCreateMsaToolsHeadingArtifact() throws SQLException {
      Artifact art = getOrCreateHeadingArtifact(HEADING_ARTIFACT, MSA_TOOLS_HEADING);
      if (art.getParent() == null) {
         try {
            Artifact rootArt =
                  ArtifactPersistenceManager.getInstance().getDefaultHierarchyRootArtifact(
                        BranchPersistenceManager.getInstance().getAtsBranch(), true);
            rootArt.addChild(art);
            rootArt.persist(true);
         } catch (SQLException ex) {
            OSEELog.logException(AtsPlugin.class, ex, true);
         }
      }
      return art;
   }

   private static Artifact getOrCreateHeadingArtifact(String artifactTypeName, String name) throws SQLException {

      // Get if it already exists
      ArtifactTypeNameSearch srch =
            new ArtifactTypeNameSearch(artifactTypeName, name, BranchPersistenceManager.getInstance().getAtsBranch());
      Collection<NativeArtifact> arts = srch.getArtifacts(NativeArtifact.class);
      if (arts.size() == 1) return arts.iterator().next();
      if (arts.size() > 0) throw new IllegalArgumentException(
            "Should be 1 \"" + name + "\" heading artifact.  Found " + arts.size());

      Artifact rootArt = null;
      try {
         rootArt =
               (Artifact) ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(artifactTypeName).makeNewArtifact(
                     BranchPersistenceManager.getInstance().getAtsBranch());
         rootArt.setDescriptiveName(name);
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }

      return rootArt;
   }

}
