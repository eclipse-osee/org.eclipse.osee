/*
 * Created on Feb 25, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
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
import org.eclipse.osee.framework.ui.plugin.internal.OseePluginUiActivator;
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
         OseeLog.log(OseePluginUiActivator.class, Level.SEVERE, ex);
      }
   }

   private synchronized static Set<IXNavigateCommonItem> getProviders() {
      if (items != null) {
         return items;
      }
      items = new HashSet<IXNavigateCommonItem>();

      IExtensionPoint point =
         Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.framework.ui.plugin.XCommonNavigateItem");
      if (point == null) {
         OseeLog.log(OseePluginUiActivator.class, OseeLevel.SEVERE_POPUP,
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
                     OseeLog.log(OseePluginUiActivator.class, OseeLevel.SEVERE_POPUP,
                        "Error loading XCommonNavigateItem extension", ex);
                  }
               }
            }
         }
      }
      return items;
   }

}
