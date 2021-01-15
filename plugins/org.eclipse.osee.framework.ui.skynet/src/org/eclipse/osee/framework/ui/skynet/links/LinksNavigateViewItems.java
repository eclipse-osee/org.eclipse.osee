/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.framework.ui.skynet.links;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.xnavigate.IXNavigateCommonItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateCommonItems;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateViewItems;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionUtility;

/**
 * @author Donald G. Dunne
 */
public class LinksNavigateViewItems implements XNavigateViewItems, IXNavigateCommonItem {

   private final List<XNavigateItem> items = new CopyOnWriteArrayList<>();
   private static LinksNavigateViewItems navigateItem;
   private boolean ensurePopulatedRanOnce = false;
   private static XNavigateItem linkNavItem;

   public LinksNavigateViewItems() {
      navigateItem = this;
   }

   public static LinksNavigateViewItems getInstance() {
      return navigateItem;
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
            linkNavItem = new LinksNavigateViewItem(null);
            items.add(linkNavItem);

            XNavigateCommonItems.addCommonNavigateItems(items, Arrays.asList(getSectionId()));
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   @Override
   public void createCommonSection(List<XNavigateItem> items, List<String> excludeSectionIds) {
      try {
         linkNavItem = new LinksNavigateViewItem(null);
         items.add(linkNavItem);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Can't create OSEE Links section", ex);
      }
   }

   @Override
   public String getSectionId() {
      return "Links";
   }

   public static void reloadLinks() {
      linkNavItem.getChildren().clear();
   }

}
