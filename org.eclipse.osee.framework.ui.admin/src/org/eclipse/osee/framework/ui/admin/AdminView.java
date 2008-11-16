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
package org.eclipse.osee.framework.ui.admin;

import java.util.ArrayList;
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.event.BroadcastEventType;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.ui.admin.dbtabletab.DbItem;
import org.eclipse.osee.framework.ui.admin.dbtabletab.DbTableTab;
import org.eclipse.osee.framework.ui.admin.dbtabletab.SiteGssflRpcr;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.access.OseeSecurityManager;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.part.ViewPart;

/**
 * Allows administration of access for OSEE environment <li>Database tables <li>OSEE user permissions
 * 
 * @author Jeff C. Phillips
 */

public class AdminView extends ViewPart implements IActionable {
   public static final OseeUiActivator plugin = AdminPlugin.getInstance();
   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.admin.AdminView";
   public static OseeSecurityManager sm;
   private static Action saveAction;
   private TabFolder tabFolder;
   private final ArrayList<DbItem> dbItems;
   private final Cursor handCursor;

   /**
    * The constructor.
    */
   public AdminView() {
      sm = OseeSecurityManager.getInstance();
      dbItems = new ArrayList<DbItem>();
      dbItems.add(new SiteGssflRpcr());
      handCursor = new Cursor(null, SWT.CURSOR_HAND);

      // permissionList.addPermission(PermissionEnum.);
   }

   @Override
   public void dispose() {
      super.dispose();
      handCursor.dispose();
   }

   @Override
   public void setFocus() {
   }

   protected void createActions() throws OseeCoreException {

      saveAction = new Action("Save") {
         @Override
         public void run() {
            save();
         }
      };
      saveAction.setImageDescriptor(plugin.getImageDescriptor("saved.gif"));
      saveAction.setToolTipText("Save");

      Action refreshAction = new Action("Refresh") {

         @Override
         public void run() {
            try {
               DbTableTab.refresh();
            } catch (OseeDataStoreException ex) {
               OseeLog.log(AdminView.class, Level.SEVERE, ex);
            }
         }
      };
      refreshAction.setImageDescriptor(plugin.getImageDescriptor("refresh.gif"));
      refreshAction.setToolTipText("Refresh");

      Action broadcastMessage = new Action("Broadcast Message") {

         @Override
         public void run() {
            handleBroadcastMessage();
         }
      };

      broadcastMessage.setToolTipText("Broadcast Message");
      broadcastMessage.setEnabled(AccessControlManager.isOseeAdmin());

      Action pingAction = new Action("Ping OSEE Clients") {

         @Override
         public void run() {
            handlePing();
         }
      };

      pingAction.setImageDescriptor(plugin.getImageDescriptor("help.gif"));
      pingAction.setToolTipText("Ping OSEE Clients");
      pingAction.setEnabled(AccessControlManager.isOseeAdmin());

      OseeAts.addBugToViewToolbar(this, this, AdminPlugin.getInstance(), VIEW_ID, "Admin");

      IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
      toolbarManager.add(saveAction);
      toolbarManager.add(refreshAction);
      toolbarManager.add(broadcastMessage);
      if (AccessControlManager.isOseeAdmin()) {
         toolbarManager.add(pingAction);
      }
   }

   public void handleBroadcastMessage() {
      EntryDialog ed =
            new EntryDialog(Display.getCurrent().getActiveShell(), "Broadcast Message to OSEE Instantiations", null,
                  "Enter Message", MessageDialog.QUESTION, new String[] {"OK", "Cancel"}, 0);
      if (ed.open() == 0) {
         String message = ed.getEntry();
         if (!message.equals("")) {
            if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Broadcast Message",
                  "Broadcast message\n\n\"" + message + "\"\n\nAre you sure?")) {
               try {
                  OseeEventManager.kickBroadcastEvent(this, BroadcastEventType.Message, new String[] {}, message);
                  AWorkbench.popup("Success", "Message sent.");
               } catch (Exception ex) {
                  OSEELog.logException(SkynetGuiPlugin.class, ex, true);
               }
            }
         }
      }
   }

   public void handlePing() {
      if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Ping OSEE Instantiations?",
            "Ping OSEE Instantiations?")) {
         try {
            OseeEventManager.kickBroadcastEvent(this, BroadcastEventType.Ping, new String[] {},
                  ClientSessionManager.getSession().toString());
            AWorkbench.popup("Success", "Ping Sent");
         } catch (Exception ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, true);
         }
      }
   }

   /*
    * @see IWorkbenchPart#createPartControl(Composite)
    */
   @Override
   public void createPartControl(Composite parent) {
      try {
         // IStatusLineManager slManager= getViewSite().getActionBars().getStatusLineManager();
         // slManager.setErrorMessage("error");

         GridData gridData = new GridData();
         gridData.verticalAlignment = GridData.FILL;
         gridData.horizontalAlignment = GridData.FILL;
         gridData.grabExcessVerticalSpace = true;
         gridData.grabExcessHorizontalSpace = true;

         GridLayout gridLayout = new GridLayout(1, false);
         gridData.heightHint = 1000;
         gridData.widthHint = 1000;
         parent.setLayout(gridLayout);

         tabFolder = new TabFolder(parent, SWT.BORDER);
         tabFolder.setLayoutData(gridData);

         // ModeChecker.check(parent);

         new OseeClientsTab(tabFolder);

         new DbTableTab(tabFolder);

         parent.layout();

         createActions();
      } catch (Exception ex) {
         OseeLog.log(AdminPlugin.class, Level.SEVERE, ex);
      }
   }

   /**
    * handles saving to the database for every tab item
    */
   public void save() {
      // database tab
      if (tabFolder.getSelectionIndex() == 2) {
         DbTableTab.dbTableViewer.save();
         setSaveNeeded(false);
      }
   }

   public static void setSaveNeeded(boolean needed) {

      if (needed)
         saveAction.setImageDescriptor(plugin.getImageDescriptor("needSave.gif"));
      else
         saveAction.setImageDescriptor(plugin.getImageDescriptor("saved.gif"));
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.ats.IActionable#getActionDescription()
    */
   public String getActionDescription() {
      String desc = "";
      TabItem items[] = tabFolder.getSelection();
      if (items.length == 1) {
         String tabName = items[0].getText();
         desc += String.format("Tab = %s ", tabName);
      }
      return desc;
   }

}