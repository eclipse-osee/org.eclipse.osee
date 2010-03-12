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
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.ui.plugin.internal.OseePluginUiActivator;
import org.osgi.framework.Bundle;

/**
 * @author Roberto E. Escobar
 */
public final class XNavigateContributionManager {

   private XNavigateContributionManager() {
   }

   public static Set<XNavigateExtensionPointData> getNavigateItems(String viewIdToMatch) {
      Set<XNavigateExtensionPointData> toReturn = new HashSet<XNavigateExtensionPointData>();
      List<IConfigurationElement> elements =
            ExtensionPoints.getExtensionElements(OseePluginUiActivator.PLUGIN_ID + ".XNavigateItem", "XNavigateItem");
      for (IConfigurationElement element : elements) {
         String className = element.getAttribute("classname");
         String category = element.getAttribute("category");
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
                  toReturn.add(new XNavigateExtensionPointData(viewId, category, (IXNavigateContainer) object));
               } catch (Exception ex) {
                  throw new IllegalArgumentException(String.format("Unable to Load: [%s - %s]", bundleName, className),
                        ex);
               }
            }
         }
      }
      return toReturn;
   }

}
