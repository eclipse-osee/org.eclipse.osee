/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.plugin.xnavigate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Collector that collects and categorizes XNavigateItems for use in XNavigateComposite
 *
 * @author Donald G. Dunne
 */
public class NavigateItemCollector {

   public List<XNavigateItem> compNavItems = new ArrayList<XNavigateItem>();
   /**
    * Need to map to cat String cause not all sub-categories are XNavItemCat and this allows providers to insert on any
    * item
    */
   private final Map<String, XNavigateItem> catStrToItem = new HashMap<String, XNavigateItem>();
   private final Collection<XNavigateItemProvider> providers;
   List<XNavigateItem> items = new ArrayList<XNavigateItem>();
   private final INavigateItemRefresher refresher;
   private final XResultData rd;
   private Collection<? extends ArtifactId> currUserUserGroups;

   public NavigateItemCollector(Collection<XNavigateItemProvider> providers, INavigateItemRefresher refresher, XResultData rd) {
      this.providers = providers;
      this.refresher = refresher;
      this.rd = rd;
   }

   public List<XNavigateItem> getComputedNavItems(Collection<? extends ArtifactId> currUserUserGroups) {
      this.currUserUserGroups = currUserUserGroups;

      if (compNavItems.isEmpty()) {
         for (XNavigateItemProvider provider : providers) {
            if (provider.isApplicable()) {
               provider.getNavigateItemsForCurrentUser(items);
            }
         }

         if (refresher != null) {
            for (XNavigateItem item : items) {
               item.setRefresher(refresher);
            }
         }

         createCategoryHierarchy();

         createChildren();

      }
      return compNavItems;
   }

   private void createChildren() {
      // Attach remaining items to their parent category folder. All category/sub-category folders were
      // already placed by createCategoryHierarchy(), so anything left here is a leaf under a known category.
      for (XNavigateItem item : items) {
         for (XNavItemCat itemCat : item.getCategories()) {
            if (itemCat == null) {
               rd.errorf(
                  "Navigator item [%s] has a null category. Fix: give it a valid XNavItemCat in its constructor.\n",
                  item.getName());
               continue;
            }

            if (!currentUserInUserGroups(item)) {
               continue;
            }

            XNavigateItem foundParent = catStrToItem.get(itemCat.getName());
            if (foundParent == null) {
               rd.errorf("Navigator item [%s] could not be placed: no parent folder registered for category [%s].\n" //
                  + "   Likely cause: the parent folder was never added by a provider, or it is gated to a user " //
                  + "group the current user is not in while this child is not (so the folder was filtered out " //
                  + "but the child was not).\n" //
                  + "   Fix: ensure a folder item with category [%s] is added, and gate the child and its parent " //
                  + "folder with the SAME user group (getUserGroups()).\n", item.getName(), itemCat.getName(),
                  itemCat.getName());
            } else {
               foundParent.addChild(item);
            }
         }
      }
   }

   private boolean currentUserInUserGroups(XNavigateItem item) {
      Collection<IUserGroupArtifactToken> itemUserGroups = item.getUserGroups();
      if (itemUserGroups.isEmpty()) {
         return true;
      }
      for (ArtifactId userUserGroup : currUserUserGroups) {
         if (itemUserGroups.contains(userUserGroup)) {
            return true;
         }
      }
      return false;
   }

   private void createCategoryHierarchy() {
      rd.log("Building OSEE Navigator tree: registering top-level folders, then nesting sub-category folders, " //
         + "then attaching leaf items. Errors below indicate items whose parent folder could not be found " //
         + "(usually a gating mismatch or a missing folder).\n");
      // Create top items
      for (XNavItemCat topCat : XNavItemCat.orderedValues()) {
         for (XNavigateItem item : new ArrayList<XNavigateItem>(items)) {

            if (!currentUserInUserGroups(item)) {
               continue;
            }

            Collection<XNavItemCat> categories = item.getCategories();
            if (categories.contains(topCat)) {
               rd.logf("Registered top-level folder [%s]\n", item.getName());
               // prevent duplicates
               if (!catStrToItem.containsKey(item.getName())) {
                  catStrToItem.put(item.getName(), item);
                  compNavItems.add(item);
               }
               items.remove(item);
            }
         }
      }

      // Create sub categories
      for (XNavigateItem item : new ArrayList<XNavigateItem>(items)) {

         if (!currentUserInUserGroups(item)) {
            continue;
         }

         Collection<XNavItemCat> categories = item.getCategories();
         if (categories.contains(XNavItemCat.SUBCAT)) {
            categories.remove(XNavItemCat.SUBCAT);
         } else {
            continue;
         }

         for (XNavItemCat itemCat : categories) {
            String catStr = itemCat.getName();
            while (Strings.isValid(catStr) && catStr.contains(".")) {
               XNavigateItem parentItem = catStrToItem.get(catStr);
               if (parentItem == null) {
                  // Walk up the dotted category path (e.g. "ATS.Admin.Testing" -> "ATS.Admin" -> "ATS")
                  // looking for a registered parent folder to attach this sub-category under.
                  catStr = getReducedCat(catStr);
                  if (Strings.isValid(catStr)) {
                     parentItem = catStrToItem.get(catStr);
                     if (parentItem != null) {
                        parentItem.addChild(item);
                        rd.logf("Nested folder [%s] under [%s]\n", item.getName(), catStr);
                        catStrToItem.put(itemCat.getName(), item);
                        items.remove(item);
                        break;
                     } else {
                        rd.errorf(
                           "Sub-category folder [%s] could not be placed: no parent folder registered for [%s].\n" //
                              + "   Likely cause: the parent folder [%s] was never added, or it is gated to a user " //
                              + "group the current user is not in (so it was filtered out before this sub-category " //
                              + "was processed).\n" //
                              + "   Fix: add a folder item for category [%s], and gate it with the same user group " //
                              + "as its children.\n",
                           item.getName(), catStr, catStr, catStr);
                     }
                  }
               } else {
                  break;
               }
            }
         }
      }
   }

   private String getReducedCat(String catStr) {
      String names[] = catStr.split("\\.");
      StringBuilder sb = new StringBuilder();
      for (int x = 0; x < names.length - 1; x++) {
         if (x > 0) {
            sb.append(".");
         }
         sb.append(names[x]);
      }
      return sb.toString();
   }

   public List<XNavigateItem> getItems() {
      return items;
   }
}