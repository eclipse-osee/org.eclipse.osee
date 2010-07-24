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
package org.eclipse.osee.ote.ui.navigate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateContributionManager;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateExtensionPointData;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateViewItems;

/**
 * @author Donald G. Dunne
 */
public class OteNavigateViewItems extends XNavigateViewItems {
   private static OteNavigateViewItems navigateItems = new OteNavigateViewItems();

   public OteNavigateViewItems() {
      super();
   }

   public static OteNavigateViewItems getInstance() {
      return navigateItems;
   }

   @Override
   public List<XNavigateItem> getSearchNavigateItems() {
      List<XNavigateItem> items = new ArrayList<XNavigateItem>();
      addExtensionPointItems(items);
      return items;
   }

   private void addExtensionPointItems(List<XNavigateItem> items) {
      Collection<XNavigateExtensionPointData> oteNavigateItemExtensions =
         XNavigateContributionManager.getNavigateItems(OteNavigateView.VIEW_ID);
      Map<String, XNavigateItem> categoryToNavigateItem =
         createCategoriesAndAddToItems(items, oteNavigateItemExtensions);
      for (XNavigateExtensionPointData data : oteNavigateItemExtensions) {
         XNavigateItem item = categoryToNavigateItem.get(data.getCategory());
         try {
            if (item == null) {
               items.addAll(data.getNavigateItems());
            } else {

               for (XNavigateItem navItem : data.getNavigateItems()) {
                  item.addChild(navItem);
               }
            }
         } catch (Throwable th) {
            OseeLog.log(OteNavigateViewItems.class, Level.SEVERE, th);
         }
      }
   }

   private Map<String, XNavigateItem> createCategoriesAndAddToItems(List<XNavigateItem> items, Collection<XNavigateExtensionPointData> oteNavigateItemExtensions) {
      Map<String, XNavigateItem> categoryToNavigateItem = new HashMap<String, XNavigateItem>();
      for (XNavigateExtensionPointData data : oteNavigateItemExtensions) {
         if (!categoryToNavigateItem.containsKey(data.getCategory())) {
            String[] path = data.getItemPath();
            StringBuilder keyBuilder = new StringBuilder(256);
            XNavigateItem lastItem = null;
            for (int i = 0; i < path.length; i++) {

               keyBuilder.append(path[i]);
               String key = keyBuilder.toString();
               XNavigateItem foundItem = categoryToNavigateItem.get(key);
               if (foundItem == null) {
                  foundItem = new XNavigateItem(lastItem, path[i], PluginUiImage.FOLDER);
                  categoryToNavigateItem.put(key, foundItem);
                  // if(lastItem != null){
                  // lastItem.addChild(foundItem);
                  // }
                  if (i == 0) {
                     items.add(foundItem);
                  }
               }
               lastItem = foundItem;
               keyBuilder.append('.');
            }
         }
      }
      return categoryToNavigateItem;
   }

}
