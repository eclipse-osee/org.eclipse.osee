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
package org.eclipse.osee.define.navigate;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.define.DefinePlugin;
import org.eclipse.osee.framework.ui.skynet.blam.BlamOperations;
import org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemBlam;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateViewItems;
import org.osgi.framework.Bundle;

/**
 * @author Donald G. Dunne
 */
public class DefineNavigateViewItems extends XNavigateViewItems {
   private static DefineNavigateViewItems navigateItems = new DefineNavigateViewItems();

   public DefineNavigateViewItems() {
      super();
   }

   public static DefineNavigateViewItems getInstance() {
      return navigateItems;
   }

   public List<XNavigateItem> getSearchNavigateItems() {
      List<XNavigateItem> items = new ArrayList<XNavigateItem>();

      XNavigateItem blamOperationItems = new XNavigateItem(null, "Blam Operations");
      for (BlamOperation blamOperation : BlamOperations.getBlamOperations()) {
         new XNavigateItemBlam(blamOperationItems, blamOperation);
      }
      items.add(blamOperationItems);

      addExtensionPointItems(items);

      return items;
   }

   @SuppressWarnings("deprecation")
   public void addExtensionPointItems(List<XNavigateItem> items) {
      IExtensionPoint point =
            Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.define.DefineNavigateItem");
      if (point == null) OSEELog.logSevere(DefinePlugin.class, "Can't access DefineNavigateItem extension point", true);
      IExtension[] extensions = point.getExtensions();
      for (IExtension extension : extensions) {
         IConfigurationElement[] elements = extension.getConfigurationElements();
         String classname = null;
         String bundleName = null;
         for (IConfigurationElement el : elements) {
            if (el.getName().equals("IDefineNavigateItem")) {
               classname = el.getAttribute("classname");
               bundleName = el.getContributor().getName();
            }
         }
         if (classname != null && bundleName != null) {
            Bundle bundle = Platform.getBundle(bundleName);
            try {
               Class<?> taskClass = bundle.loadClass(classname);
               Object obj = taskClass.newInstance();
               IDefineNavigateItem task = (IDefineNavigateItem) obj;
               items.addAll(task.getNavigateItems());
            } catch (Exception ex) {
               OSEELog.logException(DefinePlugin.class, "Error loading DefineNavigateItem extension", ex, true);
            }
         }
      }
   }
}