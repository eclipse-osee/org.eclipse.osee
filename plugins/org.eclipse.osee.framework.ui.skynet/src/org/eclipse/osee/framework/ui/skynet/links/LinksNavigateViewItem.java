/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.links;

import java.util.logging.Level;
import org.eclipse.osee.account.rest.model.AccountWebPreferences;
import org.eclipse.osee.account.rest.model.Link;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateUrlItem;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class LinksNavigateViewItem extends XNavigateItemAction {

   private static LinksNavigateViewItem topNavigateItem;

   public LinksNavigateViewItem(XNavigateItem parent) {
      super(parent, "Links", FrameworkImage.LINK);
      topNavigateItem = this;
      refresh();
   }

   @Override
   public void refresh() {
      if (topNavigateItem != null) {
         Thread refresh = new Thread(topNavigateItem.getClass().getSimpleName()) {
            @Override
            public void run() {
               load();
               if (refresher != null) {
                  Displays.ensureInDisplayThread(new Runnable() {

                     @Override
                     public void run() {
                        refresher.refresh(topNavigateItem);
                     }
                  });
               }
            }
         };
         refresh.start();
      }
   }

   public static void clearAndReload() {
      topNavigateItem.refresh();
   }

   public void load() {
      topNavigateItem.getChildren().clear();
      XNavigateItem parentItem = this;
      new AddNewLinkNavigateItem(parentItem);
      new EditLinksNavigateItem(parentItem, false);
      new EditLinksNavigateItem(parentItem, true);
      try {
         AccountWebPreferences data = LinkUtil.getAccountsPreferencesData(false);
         for (Link link : data.getLinks().values()) {
            XNavigateUrlItem urlItem = new XNavigateUrlItem(parentItem, link.getName() + getTagsStr(link, false),
               link.getUrl(), true, FrameworkImage.LINK);
            urlItem.addMenuItem(new LinksXNavigateMenuItem());
            urlItem.setData(link);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      try {
         AccountWebPreferences data = LinkUtil.getAccountsPreferencesData(true);
         for (Link link : data.getLinks().values()) {
            XNavigateUrlItem urlItem = new XNavigateUrlItem(parentItem, link.getName() + getTagsStr(link, true),
               link.getUrl(), true, FrameworkImage.LINK);
            urlItem.setData(link);
            urlItem.addMenuItem(new LinksXNavigateMenuItem());
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private static String getTagsStr(Link link, boolean global) {
      if (!link.getTags().isEmpty()) {
         String label = Collections.toString(",  ", link.getTags()) + (global ? ", global" : "");
         label = label.replaceFirst(", ", "");
         return " (" + label + ")";
      }
      return "";
   }

}