/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.links;

import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.account.rest.model.AccountWebPreferences;
import org.eclipse.osee.account.rest.model.Link;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.plugin.xnavigate.IXNavigateCommonItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.IXNavigateMenuItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateCommonItems;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateUrlItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateViewItems;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionUtility;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

/**
 * @author Donald G. Dunne
 */
public class LinksNavigateViewItems implements XNavigateViewItems, IXNavigateCommonItem {

   private final static LinksNavigateViewItems instance = new LinksNavigateViewItems();
   public static final long TOP_LINK_ID = 4561556789L;
   private final List<XNavigateItem> items = new CopyOnWriteArrayList<>();
   private boolean ensurePopulatedRanOnce = false;

   public static LinksNavigateViewItems getInstance() {
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
            addOseeLinksSectionChildren(null, items);

            XNavigateCommonItems.addCommonNavigateItems(items, Arrays.asList(getSectionId()));
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   public static void addOseeLinksSectionChildren(XNavigateItem parentItem, List<XNavigateItem> resultItems) {
      new AddNewLinkNavigateItem(parentItem);
      new EditLinksNavigateItem(parentItem, false);
      new EditLinksNavigateItem(parentItem, true);
      try {
         AccountWebPreferences data = LinkUtil.getAccountsPreferencesData(false);
         for (Link link : data.getLinks().values()) {
            XNavigateUrlItem urlItem = new XNavigateUrlItem(parentItem, link.getName() + getTagsStr(link, false),
               link.getUrl(), true, FrameworkImage.LINK);
            urlItem.addMenuItem(editListener);
            urlItem.setData(link);
            resultItems.add(urlItem);
         }

         data = LinkUtil.getAccountsPreferencesData(true);
         for (Link link : data.getLinks().values()) {
            XNavigateUrlItem urlItem = new XNavigateUrlItem(parentItem, link.getName() + getTagsStr(link, true),
               link.getUrl(), true, FrameworkImage.LINK);
            urlItem.setData(link);
            urlItem.addMenuItem(editListener);
            resultItems.add(urlItem);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private static final IXNavigateMenuItem editListener = new IXNavigateMenuItem() {

      @Override
      public void addMenuItems(final Menu menu, final TreeItem selectedTreeItem) {
         if (((XNavigateUrlItem) selectedTreeItem.getData()).getData() instanceof Link) {
            openExternallyMenuItem(menu, selectedTreeItem);
            openInternallyMenuItem(menu, selectedTreeItem);
            new MenuItem(menu, SWT.SEPARATOR);
            addEditMenuItem(menu, selectedTreeItem);
            addDeleteMenuItem(menu, selectedTreeItem);
         }
      }

      private void openExternallyMenuItem(final Menu menu, final TreeItem selectedTreeItem) {
         final XNavigateItem navItem = (XNavigateItem) selectedTreeItem.getData();
         final Link link = (Link) navItem.getData();
         MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
         menuItem.setText(String.format("Open Externally"));
         menuItem.setToolTipText(link.getUrl());
         menuItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               super.widgetSelected(e);
               Program.launch(((Link) navItem.getData()).getUrl());
            }
         });
      }

      private void openInternallyMenuItem(final Menu menu, final TreeItem selectedTreeItem) {
         final XNavigateItem navItem = (XNavigateItem) selectedTreeItem.getData();
         final Link link = (Link) navItem.getData();
         MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
         menuItem.setText(String.format("Open Internally"));
         menuItem.setToolTipText(link.getUrl());
         menuItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               super.widgetSelected(e);
               IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
               try {
                  IWebBrowser browser =
                     browserSupport.createBrowser(SWT.None, "osee.links." + link.getId(), link.getName(), "");
                  browser.openURL(new URL(link.getUrl()));
               } catch (Exception ex) {
                  OseeLog.log(LinksNavigateViewItems.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         });
      }

      private void addEditMenuItem(final Menu menu, final TreeItem selectedTreeItem) {
         final XNavigateItem navItem = (XNavigateItem) selectedTreeItem.getData();
         Link link = (Link) navItem.getData();
         if (link != null) {
            MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
            menuItem.setText(String.format("Edit [%s]", link.getName()));
            menuItem.addSelectionListener(new SelectionAdapter() {

               @Override
               public void widgetSelected(SelectionEvent e) {
                  super.widgetSelected(e);
                  handleEditMenuItemSelected(navItem, selectedTreeItem);
               }
            });
         }
      }

      private void addDeleteMenuItem(final Menu menu, final TreeItem selectedTreeItem) {
         final XNavigateItem navItem = (XNavigateItem) selectedTreeItem.getData();
         Link link = (Link) navItem.getData();
         if (link != null) {
            MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
            menuItem.setText(String.format("Delete [%s]", link.getName()));
            menuItem.addSelectionListener(new SelectionAdapter() {

               @Override
               public void widgetSelected(SelectionEvent e) {
                  super.widgetSelected(e);
                  try {
                     LinkUtil.deleteLink(String.valueOf(UserManager.getUser().getArtId()), link);
                  } catch (Exception ex) {
                     OseeLog.log(LinksNavigateViewItems.class, OseeLevel.SEVERE_POPUP, ex);
                  }
               }
            });
         }
      }

      public void handleEditMenuItemSelected(XNavigateItem urlItem, TreeItem selectedTreeItem) {
         Link link = (Link) urlItem.getData();
         if (link != null) {
            EditLinkDialog dialog = new EditLinkDialog(link);
            if (dialog.open() == Window.OK) {
               try {
                  LinkUtil.upateLinkFromDialog(dialog, link);
               } catch (Exception ex) {
                  OseeLog.log(LinksNavigateViewItems.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         }
      }

   };

   private static String getTagsStr(Link link, boolean global) {
      if (!link.getTags().isEmpty()) {
         return " (" + Collections.toString(",  ", link.getTags()) + (global ? ", global" : "") + ")";
      }
      return "";
   }

   @Override
   public void createCommonSection(List<XNavigateItem> items, List<String> excludeSectionIds) {
      try {
         XNavigateItem linkItem = new XNavigateItem(null, "Links", FrameworkImage.LINK);
         linkItem.setId(TOP_LINK_ID);
         addOseeLinksSectionChildren(linkItem, new LinkedList<>());
         items.add(linkItem);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Can't create OSEE Links section", ex);
      }
   }

   @Override
   public String getSectionId() {
      return "Links";
   }

   public static void reloadLinks(XNavigateItem linkItem) {
      linkItem.getChildren().clear();
      addOseeLinksSectionChildren(linkItem, new LinkedList<>());
   }

}
