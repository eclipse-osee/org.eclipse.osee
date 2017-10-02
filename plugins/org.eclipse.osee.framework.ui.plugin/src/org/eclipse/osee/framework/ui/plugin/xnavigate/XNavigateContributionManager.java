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
package org.eclipse.osee.framework.ui.plugin.xnavigate;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.OseeActivator;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.ui.plugin.internal.UiPluginConstants;
import org.osgi.framework.Bundle;

/**
 * @author Roberto E. Escobar
 */
public final class XNavigateContributionManager {

   private XNavigateContributionManager() {
      //Utility Class
   }

   public static Set<XNavigateExtensionPointData> getNavigateItems(String viewIdToMatch)  {
      Conditions.checkNotNull(viewIdToMatch, "viewIdToMatch");
      Set<XNavigateExtensionPointData> toReturn = new HashSet<>();
      List<IConfigurationElement> elements =
         ExtensionPoints.getExtensionElements(UiPluginConstants.PLUGIN_ID + ".XNavigateItem", "XNavigateItem");
      for (IConfigurationElement element : elements) {
         String viewId = element.getAttribute("viewId");
         if (viewIdToMatch.equals(viewId)) {
            String className = element.getAttribute("classname");
            String bundleName = element.getContributor().getName();
            if (Strings.isValid(bundleName) && Strings.isValid(className)) {
               IXNavigateContainer navigateContainer = createXNavigateContainer(className, bundleName);
               if (navigateContainer != null) {
                  String category = element.getAttribute("category");
                  XNavigateExtensionPointData data = createXNavigateData(viewId, category, navigateContainer);
                  toReturn.add(data);
               }
            }
         }
      }
      return toReturn;
   }

   private static XNavigateExtensionPointData createXNavigateData(String viewId, String category, IXNavigateContainer navigateContainer) {
      String categoryToSet = category != null ? category : "";
      return new XNavigateExtensionPointData(viewId, categoryToSet, navigateContainer);
   }

   private static IXNavigateContainer createXNavigateContainer(String className, String bundleName) {
      IXNavigateContainer toReturn = null;
      Bundle bundle = Platform.getBundle(bundleName);
      try {
         Class<?> taskClass = bundle.loadClass(className);
         try {
            Method getInstance = taskClass.getMethod("getInstance", new Class[] {});
            toReturn = (IXNavigateContainer) getInstance.invoke(null, new Object[] {});
         } catch (Exception ex) {
            toReturn = (IXNavigateContainer) taskClass.newInstance();
         }
      } catch (Exception ex) {
         OseeLog.logf(OseeActivator.class, Level.SEVERE, ex, "Unable to Load: [%s - %s]", bundleName, className);
      } catch (LinkageError error) {
         OseeLog.logf(OseeActivator.class, Level.SEVERE, error, "Unable to Load: [%s - %s]", bundleName, className);
      }
      return toReturn;
   }
}
