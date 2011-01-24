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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsFolderUtil;
import org.eclipse.osee.ats.util.AtsFolderUtil.AtsFolder;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.PluginUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.osgi.framework.Bundle;

/**
 * @author Donald G. Dunne
 */
public final class AtsWorkDefinitionSheetProviders {

   private static Set<IAtsWorkDefinitionSheetProvider> teamWorkflowExtensionItems;

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

   public static void initializeDatabase() throws OseeCoreException {
      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Import ATS Work Definitions");
      Artifact folder = AtsFolderUtil.getFolder(AtsFolder.WorkDefinitions);
      folder.persist(transaction);
      for (WorkDefinitionSheet sheet : getWorkDefinitionSheets()) {
         Artifact artifact = AtsWorkDefinitionProviders.importWorkDefinitionSheetToDb(sheet);
         if (artifact != null) {
            folder.addChild(artifact);
            artifact.persist(transaction);
         }
      }
      transaction.execute();
   }

   private static List<WorkDefinitionSheet> getWorkDefinitionSheets() {
      List<WorkDefinitionSheet> sheets = new ArrayList<WorkDefinitionSheet>();
      sheets.add(new WorkDefinitionSheet("WorkDef_Team_Default", "osee.ats.teamWorkflow",
         getSupportFile("support/WorkDef_Team_Default.ats")));
      sheets.add(new WorkDefinitionSheet("WorkDef_Task_Default", "osee.ats.taskWorkflow",
         getSupportFile("support/WorkDef_Task_Default.ats")));
      for (IAtsWorkDefinitionSheetProvider provider : getProviders()) {
         sheets.addAll(provider.getWorkDefinitionSheets());
      }
      return sheets;
   }

   public static File getSupportFile(String filename) {
      try {
         PluginUtil util = new PluginUtil(AtsPlugin.PLUGIN_ID);
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
