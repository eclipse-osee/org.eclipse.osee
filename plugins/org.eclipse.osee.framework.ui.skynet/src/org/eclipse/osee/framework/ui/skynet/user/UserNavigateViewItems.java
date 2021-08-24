/*********************************************************************
 * Copyright (c) 2011 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.user;

import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.ui.plugin.xnavigate.IXNavigateCommonItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateViewItems;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.blam.operation.CreateNewUser;
import org.eclipse.osee.framework.ui.skynet.blam.operation.PopulateUserGroupBlam;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemBlam;

/**
 * @author Donald G. Dunne
 */
public class UserNavigateViewItems implements XNavigateViewItems, IXNavigateCommonItem {

   private final static UserNavigateViewItems instance = new UserNavigateViewItems();

   public static UserNavigateViewItems getInstance() {
      return instance;
   }

   @Override
   public void createCommonSection(List<XNavigateItem> items, List<String> excludeSectionIds) {
      try {
         boolean admin = ServiceUtil.accessControlService().isOseeAdmin();
         boolean inUserGroup =
            ServiceUtil.getOseeClient().getAccessControlService().getUserGroupService().isInUserGroup(
               CoreUserGroups.UserMgmtAdmin);

         XNavigateItem userMgmtItem = null;
         if (admin || inUserGroup) {
            userMgmtItem = new XNavigateItem(null, "User Management", FrameworkImage.USER);
            items.add(userMgmtItem);
            new XNavigateItemAction(userMgmtItem, new OpenUsersInMassEditor("Open Active Users", Active.Active),
               FrameworkImage.USER);
            new XNavigateItemAction(userMgmtItem, new OpenUsersInMassEditor("Open All Users", Active.Both),
               FrameworkImage.USER);
         }

         if (admin) {
            new XNavigateItemBlam(userMgmtItem, new CreateNewUser(), FrameworkImage.ADD_GREEN);
            new XNavigateItemBlam(userMgmtItem, new PopulateUserGroupBlam(), FrameworkImage.GROUP);
         }

         ExtensionDefinedObjects<IUserNavigateItem> objects = new ExtensionDefinedObjects<>(
            "org.eclipse.osee.framework.ui.skynet.UserNavigateItem", "UserNavigateItem", "classname");
         for (IUserNavigateItem newItem : objects.getObjects()) {
            for (XNavigateItem item : newItem.getNavigateItems(userMgmtItem, admin, inUserGroup)) {
               items.add(item);
            }
         }

      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Can't create User Management section");
      }
   }

   @Override
   public String getSectionId() {
      return "Users";
   }

   @Override
   public List<XNavigateItem> getSearchNavigateItems() {
      return java.util.Collections.emptyList();
   }
}
