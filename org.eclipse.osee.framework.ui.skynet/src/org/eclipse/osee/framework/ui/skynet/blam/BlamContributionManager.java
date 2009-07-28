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

package org.eclipse.osee.framework.ui.skynet.blam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemBlam;

/**
 * @author Donald G. Dunne
 */
public class BlamContributionManager {

   private BlamContributionManager() {
   }

   public static Collection<AbstractBlam> getBlamOperationsNameSort() {
      ArrayList<AbstractBlam> blamsSortedByName = new ArrayList<AbstractBlam>();
      Map<String, AbstractBlam> blamMap = new HashMap<String, AbstractBlam>();
      for (AbstractBlam blam : getBlamOperations()) {
         blamMap.put(blam.getName(), blam);
      }
      String names[] = blamMap.keySet().toArray(new String[blamMap.keySet().size()]);
      Arrays.sort(names);
      for (String name : names) {
         blamsSortedByName.add(blamMap.get(name));
      }
      return blamsSortedByName;
   }

   public static Collection<AbstractBlam> getBlamOperations() {
      ExtensionDefinedObjects<AbstractBlam> definedObjects =
            new ExtensionDefinedObjects<AbstractBlam>("org.eclipse.osee.framework.ui.skynet.BlamOperation",
                  "Operation", "className");
      return definedObjects.getObjects();
   }

   public static void addBlamOperationsToNavigator(List<XNavigateItem> items) throws OseeCoreException {
      Map<String, XNavigateItem> nameToParent = new HashMap<String, XNavigateItem>();
      XNavigateItem blamOperationItems = new XNavigateItem(null, "Blam Operations", FrameworkImage.BLAM);
      for (AbstractBlam blamOperation : BlamContributionManager.getBlamOperationsNameSort()) {

         // Create categories first (so can have them up top)
         for (String category : blamOperation.getCategories()) {
            if (AccessControlManager.isOseeAdmin() || !category.contains("Admin") || category.contains("Admin") && AccessControlManager.isOseeAdmin()) {
               createCategories(category.split("\\."), 0, blamOperationItems, nameToParent);
            }
         }
      }
      // Add blams to categories
      for (AbstractBlam blamOperation : BlamContributionManager.getBlamOperationsNameSort()) {
         // If categories not specified, add to top level
         if (blamOperation.getCategories().size() == 0) {
            new XNavigateItemBlam(blamOperationItems, blamOperation);
         }
         for (String category : blamOperation.getCategories()) {
            // Category will be null if admin category and not admin
            if (nameToParent.get(category) != null) {
               new XNavigateItemBlam(nameToParent.get(category), blamOperation);
            }
         }
      }
      items.add(blamOperationItems);
   }

   private static void createCategories(String[] categoryElements, int index, XNavigateItem parentItem, Map<String, XNavigateItem> nameToParent) throws OseeCoreException {
      String firstElement = categoryElements[index];
      XNavigateItem thisCategoryItem = null;
      for (XNavigateItem childItem : parentItem.getChildren()) {
         if (childItem.getName().equals(firstElement)) {
            thisCategoryItem = childItem;
            break;
         }
      }
      // Create new folder category
      if (thisCategoryItem == null) {
         // Add to parentItem
         thisCategoryItem = new XNavigateItem(parentItem, firstElement, FrameworkImage.FOLDER);
         String catName = "";
         for (int x = 0; x <= index; x++) {
            if (!catName.equals("")) {
               catName += ".";
            }
            catName += categoryElements[x];
         }
         // Add to lookup map
         nameToParent.put(catName, thisCategoryItem);
      }
      // Process children categories
      if (categoryElements.length > index + 1) {
         createCategories(categoryElements, index + 1, thisCategoryItem, nameToParent);
      }
   }
}
