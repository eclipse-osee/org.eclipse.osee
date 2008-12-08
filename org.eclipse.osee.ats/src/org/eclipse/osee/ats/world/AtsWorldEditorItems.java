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
package org.eclipse.osee.ats.world;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.osgi.framework.Bundle;

/**
 * @author Donald G. Dunne
 */
public class AtsWorldEditorItems {

   private static List<IAtsWorldEditorItem> items = new ArrayList<IAtsWorldEditorItem>();

   @SuppressWarnings( {"unchecked"})
   private static void loadAllStateItems() {
      if (items.size() > 0) return;
      IExtensionPoint point =
            Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.ats.AtsWorldEditorItem");
      if (point == null) {
         OSEELog.logSevere(AtsPlugin.class, "Can't access AtsWorldEditorItem extension point", true);
         return;
      }
      IExtension[] extensions = point.getExtensions();
      for (IExtension extension : extensions) {
         IConfigurationElement[] elements = extension.getConfigurationElements();
         String classname = null;
         String bundleName = null;
         for (IConfigurationElement el : elements) {
            if (el.getName().equals("AtsWorldEditorItem")) {
               classname = el.getAttribute("classname");
               bundleName = el.getContributor().getName();
               if (classname != null && bundleName != null) {
                  Bundle bundle = Platform.getBundle(bundleName);
                  try {
                     Class taskClass = bundle.loadClass(classname);
                     Object obj = taskClass.newInstance();
                     if (obj == null) {
                        OseeLog.log(AtsPlugin.class, Level.SEVERE,
                              "Error Instantiating AtsWorldEditorItem extension \"" + classname + "\"", null);
                     } else {
                        items.add((IAtsWorldEditorItem) obj);
                     }
                  } catch (Exception ex) {
                     OSEELog.logException(AtsPlugin.class, "Error loading AtsWorldEditorItem extension", ex, true);
                  }
               }
            }
         }
      }
   }

   /**
    * @return the stateItems
    */
   public static List<IAtsWorldEditorItem> getItems() {
      loadAllStateItems();
      return items;
   }
}
