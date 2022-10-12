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
package org.eclipse.osee.framework.ui.skynet.links;

import java.net.URL;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.account.rest.model.Link;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.plugin.xnavigate.IXNavigateMenuItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateUrlItem;
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
public class LinksXNavigateMenuItem implements IXNavigateMenuItem {

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
                  LinkUtil.deleteLink(UserManager.getUser(), link);
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
         if (link.getTeam().equals(LinkUtil.ANONYMOUS)) {
            dialog.setChecked(true);
         }
         if (dialog.open() == Window.OK) {
            try {
               LinkUtil.upateLinkFromDialog(dialog, link);
            } catch (Exception ex) {
               OseeLog.log(LinksNavigateViewItems.class, OseeLevel.SEVERE_POPUP,
                  "Database was not updated or no changes were made." + Lib.exceptionToString(ex));
            }
         }
      }
   }

}
