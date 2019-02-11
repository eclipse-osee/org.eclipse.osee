/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.plugin.xnavigate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.internal.UiPluginConstants;
import org.osgi.framework.Bundle;

public class XNavigateCommonItems {

   private static Set<IXNavigateCommonItem> items;

   public static void addCommonNavigateItems(List<XNavigateItem> items, List<String> excludeSectionIds) {
      try {
         for (IXNavigateCommonItem item : getProviders()) {
            if (!excludeSectionIds.contains(item.getSectionId())) {
               item.createCommonSection(items, excludeSectionIds);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(UiPluginConstants.class, Level.SEVERE, ex);
      }

      createUtilItemsSection(items, excludeSectionIds);
   }

   public static void createUtilItemsSection(List<XNavigateItem> items, List<String> excludeSectionIds) {
      try {
         XNavigateItem utilItems = new XNavigateItem(null, "Util", PluginUiImage.GEAR);

         for (IXNavigateCommonItem item : getProviders()) {
            item.addUtilItems(utilItems);
         }

         items.add(utilItems);

      } catch (Exception ex) {
         OseeLog.log(UiPluginConstants.class, Level.SEVERE, ex);
      }
   }

   private synchronized static Set<IXNavigateCommonItem> getProviders() {
      if (items != null) {
         return items;
      }
      items = new HashSet<>();

      IExtensionPoint point =
         Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.framework.ui.plugin.XCommonNavigateItem");
      if (point == null) {
         OseeLog.log(UiPluginConstants.class, OseeLevel.SEVERE_POPUP,
            "Can't access XCommonNavigateItem extension point");
         return items;
      }
      IExtension[] extensions = point.getExtensions();
      for (IExtension extension : extensions) {
         IConfigurationElement[] elements = extension.getConfigurationElements();
         String classname = null;
         String bundleName = null;
         for (IConfigurationElement el : elements) {
            if (el.getName().equals("XCommonNavigateItem")) {
               classname = el.getAttribute("classname");
               bundleName = el.getContributor().getName();
               if (classname != null && bundleName != null) {
                  Bundle bundle = Platform.getBundle(bundleName);
                  try {
                     Class<?> taskClass = bundle.loadClass(classname);
                     Object obj = taskClass.newInstance();
                     items.add((IXNavigateCommonItem) obj);
                  } catch (Exception ex) {
                     OseeLog.log(UiPluginConstants.class, OseeLevel.SEVERE_POPUP,
                        "Error loading XCommonNavigateItem extension", ex);
                  }
               }
            }
         }
      }
      return items;
   }

}
