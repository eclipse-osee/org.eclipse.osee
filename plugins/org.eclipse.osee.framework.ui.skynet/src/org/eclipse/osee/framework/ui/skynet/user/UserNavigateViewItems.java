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
package org.eclipse.osee.framework.ui.skynet.user;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.access.UserGroupService;
import org.eclipse.osee.framework.ui.plugin.xnavigate.IXNavigateCommonItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateCommonItems;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateViewItems;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.blam.operation.CreateNewUser;
import org.eclipse.osee.framework.ui.skynet.blam.operation.PopulateUserGroupBlam;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionUtility;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemBlam;

/**
 * @author Donald G. Dunne
 */
public class UserNavigateViewItems implements XNavigateViewItems, IXNavigateCommonItem {

   private final static UserNavigateViewItems instance = new UserNavigateViewItems();
   private final List<XNavigateItem> items = new CopyOnWriteArrayList<>();
   private boolean ensurePopulatedRanOnce = false;

   public static UserNavigateViewItems getInstance() {
      return instance;
   }

   @Override
   public List<XNavigateItem> getSearchNavigateItems() {
      ensurePopulated();
      return items;
   }

   private synchronized void ensurePopulated() {
      if (!ensurePopulatedRanOnce) {
         if (DbConnectionUtility.areOSEEServicesAvailable().isFalse()) {
            return;
         }
         this.ensurePopulatedRanOnce = true;

         try {
            addOseePeerSectionChildren(null);

            XNavigateCommonItems.addCommonNavigateItems(items, Arrays.asList(getSectionId()));
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   public void addOseePeerSectionChildren(XNavigateItem parentItem) {
      try {
         items.add(new XNavigateItemAction(parentItem, new OpenUsersInMassEditor("Open Active Users", Active.Active),
            FrameworkImage.USER));
         items.add(new XNavigateItemAction(parentItem, new OpenUsersInMassEditor("Open All Users", Active.Both),
            FrameworkImage.USER));

         if (UserGroupService.getOseeAdmin().isCurrentUserMember()) {
            items.add(new XNavigateItemBlam(parentItem, new CreateNewUser(), FrameworkImage.ADD_GREEN));
            items.add(new XNavigateItemBlam(parentItem, new PopulateUserGroupBlam(), FrameworkImage.GROUP));
         }

         ExtensionDefinedObjects<IUserNavigateItem> objects = new ExtensionDefinedObjects<>(
            "org.eclipse.osee.framework.ui.skynet.UserNavigateItem", "UserNavigateItem", "classname");
         for (IUserNavigateItem newItem : objects.getObjects()) {
            for (XNavigateItem item : newItem.getNavigateItems(parentItem)) {
               items.add(item);
            }
         }

      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void createCommonSection(List<XNavigateItem> items, List<String> excludeSectionIds) {
      try {
         boolean admin = UserGroupService.getOseeAdmin().isCurrentUserMember();
         if (UserGroupService.isInUserGrp(CoreUserGroups.Everyone) || admin) {
            XNavigateItem reviewItem = new XNavigateItem(null, "User Management", FrameworkImage.USER);
            addOseePeerSectionChildren(reviewItem);
            items.add(reviewItem);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Can't create User Management section");
      }
   }

   @Override
   public String getSectionId() {
      return "Users";
   }
}
