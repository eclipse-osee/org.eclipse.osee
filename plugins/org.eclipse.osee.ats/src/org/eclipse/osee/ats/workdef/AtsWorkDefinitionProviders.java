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

import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.osgi.framework.Bundle;

/**
 * @author Donald G. Dunne
 */
public final class AtsWorkDefinitionProviders {

   private static IAtsWorkDefinitionProvider provider = null;

   private AtsWorkDefinitionProviders() {
      // private constructor
   }

   public static boolean providerExists() {
      return getProvider() != null;
   }

   public static Artifact importWorkDefinitionSheetToDb(WorkDefinitionSheet sheet, SkynetTransaction transaction) throws OseeCoreException {
      if (!providerExists()) {
         return null;
      }
      return getProvider().importWorkDefinitionSheetToDb(sheet, transaction);
   }

   public static void importAIsAndTeamsToDb(WorkDefinitionSheet sheet, SkynetTransaction transaction) throws OseeCoreException {
      if (!providerExists()) {
         return;
      }
      getProvider().importAIsAndTeamsToDb(sheet, transaction);
   }

   public static WorkDefinition loadWorkFlowDefinitionFromFile(WorkDefinitionSheet sheet) throws OseeCoreException {
      if (!providerExists()) {
         return null;
      }
      return getProvider().loadWorkFlowDefinitionFromFile(sheet);
   }

   public static WorkDefinition loadTeamWorkDefFromFileOldWay() {
      if (!providerExists()) {
         return null;
      }
      return getProvider().loadTeamWorkDefFromFileOldWay();
   }

   public static WorkDefinition loadTeamWorkDefFromFileNewWay() {
      if (!providerExists()) {
         return null;
      }
      return getProvider().loadTeamWorkDefFromFileNewWay();
   }

   public static WorkDefinition getWorkFlowDefinition(String id) throws OseeCoreException {
      if (!providerExists()) {
         return null;
      }
      return getProvider().getWorkFlowDefinition(id);
   }

   public static void convertAndOpenAtsDsl(WorkDefinition workDef, XResultData resultData, String filename) throws OseeCoreException {
      if (!providerExists()) {
         return;
      }
      getProvider().convertAndOpenAtsDsl(workDef, resultData, filename);
   }

   public static void convertAndOpenAIandTeamAtsDsl(XResultData resultData) throws OseeCoreException {
      if (!providerExists()) {
         return;
      }
      getProvider().convertAndOpenAIandTeamAtsDsl(resultData);
   }

   /*
    * due to lazy initialization, this function is non-reentrant therefore, the synchronized keyword is necessary
    */
   private synchronized static IAtsWorkDefinitionProvider getProvider() {
      if (provider != null) {
         return provider;
      }

      IExtensionPoint point =
         Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.ats.AtsWorkDefinitionProvider");
      if (point == null) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Can't access AtsWorkDefinitionProvider extension point");
         return null;
      }
      IExtension[] extensions = point.getExtensions();
      for (IExtension extension : extensions) {
         IConfigurationElement[] elements = extension.getConfigurationElements();
         String classname = null;
         String bundleName = null;
         for (IConfigurationElement el : elements) {
            if (el.getName().equals("AtsWorkDefinitionProvider")) {
               if (provider != null) {
                  OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP,
                     "Error can not be two AtsWorkDefinitionProviders");
               }
               classname = el.getAttribute("classname");
               bundleName = el.getContributor().getName();
               if (classname != null && bundleName != null) {
                  Bundle bundle = Platform.getBundle(bundleName);
                  try {
                     Class<?> taskClass = bundle.loadClass(classname);
                     Object obj = taskClass.newInstance();
                     provider = ((IAtsWorkDefinitionProvider) obj);
                  } catch (Exception ex) {
                     OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP,
                        "Error loading AtsWorkDefinitionProvider extension", ex);
                  }
               }
            }
         }
      }
      if (provider == null) {
         OseeLog.log(AtsPlugin.class, Level.INFO, "No AtsWorkDefinitionProvider Loaded");
      }
      return provider;
   }
}
