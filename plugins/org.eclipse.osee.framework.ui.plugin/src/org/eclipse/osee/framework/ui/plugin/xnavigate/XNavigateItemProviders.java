/*********************************************************************
 * Copyright (c) 2011 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.plugin.xnavigate;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.internal.UiPluginConstants;
import org.osgi.framework.Bundle;

public class XNavigateItemProviders {

   private static Set<XNavigateItemProvider> items;
   public static boolean debug = false;

   public static synchronized Set<XNavigateItemProvider> getProviders() {
      if (items != null) {
         return items;
      }
      items = new HashSet<>();

      IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(
         "org.eclipse.osee.framework.ui.plugin.XNavigateItemProvider");
      if (point == null) {
         OseeLog.log(UiPluginConstants.class, OseeLevel.SEVERE_POPUP,
            "Can't access XNavigateItemProvider extension point");
         return items;
      }
      IExtension[] extensions = point.getExtensions();
      for (IExtension extension : extensions) {
         IConfigurationElement[] elements = extension.getConfigurationElements();
         String classname = null;
         String bundleName = null;
         for (IConfigurationElement el : elements) {
            if (el.getName().equals("XNavigateItemProvider")) {
               classname = el.getAttribute("classname");
               bundleName = el.getContributor().getName();
               if (classname != null && bundleName != null) {
                  Bundle bundle = Platform.getBundle(bundleName);
                  try {
                     Class<?> taskClass = bundle.loadClass(classname);
                     Object obj = taskClass.newInstance();
                     items.add((XNavigateItemProvider) obj);
                  } catch (Exception ex) {
                     OseeLog.log(UiPluginConstants.class, OseeLevel.SEVERE_POPUP,
                        "Error loading XNavigateItemProvider extension", ex);
                  }
               }
            }
         }
      }
      return items;
   }

}
