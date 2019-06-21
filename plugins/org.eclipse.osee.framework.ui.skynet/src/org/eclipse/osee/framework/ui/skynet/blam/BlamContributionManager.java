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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.access.UserGroupService;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.xnavigate.IXNavigateCommonItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemBlam;

/**
 * @author Donald G. Dunne
 */
public class BlamContributionManager implements IXNavigateCommonItem {

   private static TreeMap<String, AbstractBlam> blams;

   public synchronized static Map<String, AbstractBlam> getBlamMap() {
      if (blams == null) {
         blams = new TreeMap<>();
         ExtensionDefinedObjects<AbstractBlam> definedObjects = new ExtensionDefinedObjects<>(
            "org.eclipse.osee.framework.ui.skynet.BlamOperation", "Operation", "className");
         for (AbstractBlam blam : definedObjects.getObjects()) {
            blams.put(blam.getName(), blam);
         }
      }
      return blams;
   }

   public static Collection<AbstractBlam> getBlamOperations() {
      return getBlamMap().values();
   }

   private static void createCategories(String[] categoryElements, int index, XNavigateItem parentItem, Map<String, XNavigateItem> nameToParent) {
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
         thisCategoryItem = new XNavigateItem(parentItem, firstElement, PluginUiImage.FOLDER);
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

   @Override
   public void createCommonSection(List<XNavigateItem> items, List<String> excludeSectionIds) {
      Map<String, XNavigateItem> nameToParent = new HashMap<>();
      XNavigateItem blamOperationItems = new XNavigateItem(null, "Blam Operations", FrameworkImage.BLAM);
      Collection<IUserGroupArtifactToken> userGroups = UserGroupService.getUserGrps();
      for (AbstractBlam blamOperation : getBlamOperations()) {
         Collection<IUserGroupArtifactToken> blamUserGroups = blamOperation.getUserGroups();
         if (!Collections.setIntersection(userGroups, blamUserGroups).isEmpty()) {
            // Create categories first (so can have them up top)
            for (String category : blamOperation.getCategories()) {
               try {
                  if (AccessControlManager.isOseeAdmin() || !category.contains("Admin") || category.contains(
                     "Admin") && AccessControlManager.isOseeAdmin()) {
                     createCategories(category.split("\\."), 0, blamOperationItems, nameToParent);
                  }
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
         }
      }
      // Add blams to categories
      for (AbstractBlam blamOperation : BlamContributionManager.getBlamOperations()) {
         Collection<IUserGroupArtifactToken> blamUserGroups = blamOperation.getUserGroups();
         if (!Collections.setIntersection(userGroups, blamUserGroups).isEmpty()) {
            // If categories not specified, add to top level
            if (blamOperation.getCategories().isEmpty()) {
               new XNavigateItemBlam(blamOperationItems, blamOperation);
            }
            for (String category : blamOperation.getCategories()) {
               // Category will be null if admin category and not admin
               if (nameToParent.get(category) != null) {
                  new XNavigateItemBlam(nameToParent.get(category), blamOperation);
               }
            }
         }
      }
      items.add(blamOperationItems);
   }

   @Override
   public String getSectionId() {
      return "Blam";
   }
}
