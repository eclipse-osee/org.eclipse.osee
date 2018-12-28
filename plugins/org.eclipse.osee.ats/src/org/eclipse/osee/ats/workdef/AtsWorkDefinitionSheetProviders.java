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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.core.workdef.WorkDefinitionSheet;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDsl;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.workdef.config.ImportAIsAndTeamDefinitionsToDb;
import org.eclipse.osee.ats.workdef.provider.AtsWorkDefinitionImporter;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.PluginUtil;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.osgi.framework.Bundle;

/**
 * @author Donald G. Dunne
 */
public final class AtsWorkDefinitionSheetProviders {

   private static Set<IAtsWorkDefinitionSheetProvider> teamWorkflowExtensionItems;
   public static String WORK_DEF_TEAM_DEFAULT = "WorkDef_Team_Default";
   private static final Map<String, ArtifactToken> sheetNameToArtifactIdMap = new HashMap<>();

   private AtsWorkDefinitionSheetProviders() {
      // Utility Class
   }

   public static void initializeDatabase(XResultData resultData, String dbType) {
      IAtsChangeSet changes = AtsClientService.get().createChangeSet("Import ATS Work Definitions, Teams and AIs");
      Artifact folder = OseeSystemArtifacts.getOrCreateArtifact(AtsArtifactToken.WorkDefinitionsFolder,
         AtsClientService.get().getAtsBranch());
      if (folder.isDirty()) {
         changes.add(folder);
      }
      List<WorkDefinitionSheet> sheets = getWorkDefinitionSheets(dbType);
      Set<String> stateNames = new HashSet<>();
      importWorkDefinitionSheets(resultData, changes, folder, sheets, stateNames);
      importTeamsAndAis(resultData, changes, folder, sheets, dbType);
      changes.execute();
   }

   public static void importWorkDefinitionSheets(XResultData resultData, IAtsChangeSet changes, Artifact folder, Collection<WorkDefinitionSheet> sheets, Set<String> stateNames) {
      OseeLog.logf(Activator.class, Level.INFO, "Importing ATS Work Definitions");
      for (WorkDefinitionSheet sheet : sheets) {
         Artifact artifact = AtsWorkDefinitionImporter.get().importWorkDefinitionSheetToDb(sheet, resultData,
            stateNames, sheetNameToArtifactIdMap, sheet.getArtifact(), changes);
         if (artifact != null) {
            folder.addChild(artifact);
            changes.add(artifact);
         }
      }
   }

   public static void importTeamsAndAis(XResultData resultData, IAtsChangeSet changes, Artifact folder, Collection<WorkDefinitionSheet> sheets, String dbType) {
      OseeLog.logf(Activator.class, Level.INFO, "Importing ATS Teams and AIs");
      for (WorkDefinitionSheet sheet : sheets) {
         importAIsAndTeamsToDb(sheet, changes);
      }
   }

   public static void importAIsAndTeamsToDatabase(String dbType) {

      IAtsChangeSet changes = AtsClientService.get().createChangeSet("Import ATS AIs and Team Definitions");
      for (WorkDefinitionSheet sheet : getWorkDefinitionSheets(dbType)) {
         OseeLog.logf(Activator.class, Level.INFO, "Importing ATS AIs and Teams sheet");
         importAIsAndTeamsToDb(sheet, changes);
      }
      changes.execute();
   }

   public static void importAIsAndTeamsToDb(WorkDefinitionSheet sheet, IAtsChangeSet changes) {
      String modelName = sheet.getName() + ".ats";
      AtsDsl atsDsl = AtsDslUtil.getFromSheet(sheet);
      ImportAIsAndTeamDefinitionsToDb importer =
         new ImportAIsAndTeamDefinitionsToDb(modelName, atsDsl, sheetNameToArtifactIdMap, changes);
      importer.execute();
   }

   public static List<WorkDefinitionSheet> getWorkDefinitionSheets(String dbType) {
      List<WorkDefinitionSheet> sheets = new ArrayList<>();
      if (dbType.equals("ats")) {
         sheets.add(new WorkDefinitionSheet(WORK_DEF_TEAM_DEFAULT, AtsWorkDefinitionSheetProviders.class));
         sheets.add(new WorkDefinitionSheet("WorkDef_Task_Default", AtsWorkDefinitionSheetProviders.class));
         sheets.add(new WorkDefinitionSheet("WorkDef_Review_Decision", AtsWorkDefinitionSheetProviders.class));
         sheets.add(new WorkDefinitionSheet("WorkDef_Review_PeerToPeer", AtsWorkDefinitionSheetProviders.class));
         sheets.add(new WorkDefinitionSheet("WorkDef_Team_Simple", AtsWorkDefinitionSheetProviders.class));
         sheets.add(new WorkDefinitionSheet("WorkDef_Goal", AtsWorkDefinitionSheetProviders.class));
         sheets.add(new WorkDefinitionSheet("WorkDef_Sprint", AtsWorkDefinitionSheetProviders.class));
      }
      for (IAtsWorkDefinitionSheetProvider provider : getProviders(dbType)) {
         sheets.addAll(provider.getWorkDefinitionSheets());
      }
      return sheets;
   }

   public static File getSupportFile(String pluginId, String filename) {
      try {
         PluginUtil util = new PluginUtil(pluginId);
         return util.getPluginFile(filename);
      } catch (IOException ex) {
         OseeLog.logf(Activator.class, Level.SEVERE, ex, "Unable to access work definition sheet [%s]", filename);
      }
      return null;
   }

   /*
    * due to lazy initialization, this function is non-reentrant therefore, the synchronized keyword is necessary
    */
   private synchronized static Set<IAtsWorkDefinitionSheetProvider> getProviders(String dbType) {
      teamWorkflowExtensionItems = new HashSet<>();

      IExtensionPoint point =
         Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.ats.AtsWorkDefinitionSheetProvider");
      if (point == null) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP,
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
               String actualDbType = el.getAttribute("db_type");
               if (actualDbType != null && actualDbType.equals(dbType)) {
                  if (classname != null && bundleName != null) {
                     Bundle bundle = Platform.getBundle(bundleName);
                     try {
                        Class<?> taskClass = bundle.loadClass(classname);
                        Object obj = taskClass.newInstance();
                        teamWorkflowExtensionItems.add((IAtsWorkDefinitionSheetProvider) obj);
                     } catch (Exception ex) {
                        OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP,
                           "Error loading AtsWorkDefinitionSheetProvider extension", ex);
                     }
                  }
               }
            }
         }
      }
      return teamWorkflowExtensionItems;
   }

}
