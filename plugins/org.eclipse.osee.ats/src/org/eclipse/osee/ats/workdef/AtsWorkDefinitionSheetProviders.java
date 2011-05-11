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
package org.eclipse.osee.ats.workdef;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.ats.core.config.AtsArtifactToken;
import org.eclipse.osee.ats.core.workdef.WorkDefinitionSheet;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.workdef.provider.AtsWorkDefinitionProvider;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.PluginUtil;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.osgi.framework.Bundle;

/**
 * @author Donald G. Dunne
 */
public final class AtsWorkDefinitionSheetProviders {

   private static Set<IAtsWorkDefinitionSheetProvider> teamWorkflowExtensionItems;
   public static String WORK_DEF_TEAM_DEFAULT = "WorkDef_Team_Default";

   private AtsWorkDefinitionSheetProviders() {
      // private constructor
   }

   public static String getOverrideId(String legacyId) {
      for (WorkDefinitionSheet sheet : getWorkDefinitionSheets()) {
         if (sheet.getLegacyOverrideId().equals(legacyId)) {
            return sheet.getName();
         }
      }
      return null;
   }

   public static String getReverseOverrideId(String sheetName) {
      for (WorkDefinitionSheet sheet : getWorkDefinitionSheets()) {
         if (sheet.getName().equals(sheetName)) {
            return sheet.getLegacyOverrideId();
         }
      }
      return null;
   }

   public static void initializeDatabase(XResultData resultData, boolean onlyWorkDefinitions) throws OseeCoreException {
      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Import ATS Work Definitions");
      Artifact folder =
         OseeSystemArtifacts.getOrCreateArtifact(AtsArtifactToken.WorkDefinitionsFolder, AtsUtil.getAtsBranch());
      if (folder.isDirty()) {
         folder.persist(transaction);
      }
      importWorkDefinitionSheets(resultData, onlyWorkDefinitions, transaction, folder, getWorkDefinitionSheets());
      transaction.execute();
   }

   public static void importWorkDefinitionSheets(XResultData resultData, boolean onlyWorkDefinitions, SkynetTransaction transaction, Artifact folder, Collection<WorkDefinitionSheet> sheets) throws OseeCoreException {
      for (WorkDefinitionSheet sheet : sheets) {
         if (isValidSheet(sheet)) {
            String logStr = String.format("Importing ATS sheet [%s]", sheet.getName());
            System.out.println(logStr);
            Artifact artifact =
               AtsWorkDefinitionProvider.get().importWorkDefinitionSheetToDb(sheet, resultData, onlyWorkDefinitions,
                  transaction);
            if (artifact != null) {
               folder.addChild(artifact);
               artifact.persist(transaction);
            }
         }
      }
   }

   public static void importAIsAndTeamsToDatabase() throws OseeCoreException {
      SkynetTransaction transaction =
         new SkynetTransaction(AtsUtil.getAtsBranch(), "Import ATS AIs and Team Definitions");
      for (WorkDefinitionSheet sheet : getWorkDefinitionSheets()) {
         if (isValidSheet(sheet)) {
            String logStr = String.format("Importing ATS AIs and Teams sheet [%s]", sheet.getName());
            OseeLog.log(AtsPlugin.class, Level.INFO, logStr);
            AtsWorkDefinitionProvider.get().importAIsAndTeamsToDb(sheet, transaction);
         }
      }
      transaction.execute();
   }

   private static boolean isValidSheet(WorkDefinitionSheet sheet) throws OseeCoreException {
      if (!Strings.isValid(sheet.getLegacyOverrideId())) {
         return true;
      }
      try {
         Artifact artifact =
            ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.WorkFlowDefinition, sheet.getLegacyOverrideId(),
               AtsUtil.getAtsBranch());
         if (artifact == null) {
            throw new OseeStateException(
               "WorkDefinitionSheet [%s] has legacy id that does not match an existing WorkFlowDefinition name", sheet);
         }
      } catch (ArtifactDoesNotExist ex) {
         // do nothing; this is what we want
      }
      return true;
   }

   public static List<WorkDefinitionSheet> getWorkDefinitionSheets() {
      List<WorkDefinitionSheet> sheets = new ArrayList<WorkDefinitionSheet>();
      sheets.add(new WorkDefinitionSheet(WORK_DEF_TEAM_DEFAULT, "osee.ats.teamWorkflow", getSupportFile(
         AtsPlugin.PLUGIN_ID, "support/WorkDef_Team_Default.ats")));
      sheets.add(new WorkDefinitionSheet("WorkDef_Task_Default", "osee.ats.taskWorkflow", getSupportFile(
         AtsPlugin.PLUGIN_ID, "support/WorkDef_Task_Default.ats")));
      sheets.add(new WorkDefinitionSheet("WorkDef_Review_Decision", "osee.ats.decisionReview", getSupportFile(
         AtsPlugin.PLUGIN_ID, "support/WorkDef_Review_Decision.ats")));
      sheets.add(new WorkDefinitionSheet("WorkDef_Review_PeerToPeer", "osee.ats.peerToPeerReview", getSupportFile(
         AtsPlugin.PLUGIN_ID, "support/WorkDef_Review_PeerToPeer.ats")));
      sheets.add(new WorkDefinitionSheet("WorkDef_Team_Simple", "osee.ats.simpleTeamWorkflow", getSupportFile(
         AtsPlugin.PLUGIN_ID, "support/WorkDef_Team_Simple.ats")));
      sheets.add(new WorkDefinitionSheet("WorkDef_Goal", "osee.ats.goalWorkflow", getSupportFile(AtsPlugin.PLUGIN_ID,
         "support/WorkDef_Goal.ats")));
      for (IAtsWorkDefinitionSheetProvider provider : getProviders()) {
         sheets.addAll(provider.getWorkDefinitionSheets());
      }
      return sheets;
   }

   public static File getSupportFile(String pluginId, String filename) {
      try {
         PluginUtil util = new PluginUtil(pluginId);
         return util.getPluginFile(filename);
      } catch (IOException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE,
            String.format("Unable to access work definition sheet [%s]", filename), ex);
      }
      return null;
   }

   /*
    * due to lazy initialization, this function is non-reentrant therefore, the synchronized keyword is necessary
    */
   private synchronized static Set<IAtsWorkDefinitionSheetProvider> getProviders() {
      if (teamWorkflowExtensionItems != null) {
         return teamWorkflowExtensionItems;
      }
      teamWorkflowExtensionItems = new HashSet<IAtsWorkDefinitionSheetProvider>();

      IExtensionPoint point =
         Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.ats.AtsWorkDefinitionSheetProvider");
      if (point == null) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP,
            "Can't access AtsWorkDefinitionSheetProvider extension point");
         return teamWorkflowExtensionItems;
      }
      IExtension[] extensions = point.getExtensions();
      for (IExtension extension : extensions) {
         IConfigurationElement[] elements = extension.getConfigurationElements();
         String classname = null;
         String bundleName = null;
         for (IConfigurationElement el : elements) {
            if (el.getName().equals("AtsWorkDefinitionSheetProvider")) {
               classname = el.getAttribute("classname");
               bundleName = el.getContributor().getName();
               if (classname != null && bundleName != null) {
                  Bundle bundle = Platform.getBundle(bundleName);
                  try {
                     Class<?> taskClass = bundle.loadClass(classname);
                     Object obj = taskClass.newInstance();
                     teamWorkflowExtensionItems.add((IAtsWorkDefinitionSheetProvider) obj);
                  } catch (Exception ex) {
                     OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP,
                        "Error loading AtsWorkDefinitionSheetProvider extension", ex);
                  }
               }
            }
         }
      }
      return teamWorkflowExtensionItems;
   }

}
