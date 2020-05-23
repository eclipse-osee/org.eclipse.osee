/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.plugin.widgets;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.ui.plugin.internal.UiPluginConstants;
import org.osgi.framework.Bundle;

/**
 * @author Roberto E. Escobar
 */
public class PropertyStoreControlContributions {

   private PropertyStoreControlContributions() {
      // do nothing
   }

   public static List<IPropertyStoreBasedControl> getContributions(String viewIdToMatch) {
      List<IPropertyStoreBasedControl> toReturn = new ArrayList<>();
      List<IConfigurationElement> elements = ExtensionPoints.getExtensionElements(
         UiPluginConstants.PLUGIN_ID + ".PropertyStoreControl", "PropertyStoreControl");
      for (IConfigurationElement element : elements) {
         String className = element.getAttribute("classname");
         String viewId = element.getAttribute("viewId");

         if (viewIdToMatch != null && viewIdToMatch.equals(viewId)) {
            String bundleName = element.getContributor().getName();
            if (Strings.isValid(bundleName) && Strings.isValid(className)) {
               try {
                  Bundle bundle = Platform.getBundle(bundleName);
                  Class<?> taskClass = bundle.loadClass(className);
                  Object object;
                  try {
                     Method getInstance = taskClass.getMethod("getInstance", new Class[] {});
                     object = getInstance.invoke(null, new Object[] {});
                  } catch (Exception ex) {
                     object = taskClass.newInstance();
                  }
                  toReturn.add((IPropertyStoreBasedControl) object);
               } catch (Exception ex) {
                  OseeLog.logf(UiPluginConstants.class, Level.SEVERE, ex, "Unable to Load: [%s - %s]", bundleName,
                     className);
               }
            }
         }
      }
      return toReturn;
   }
}
