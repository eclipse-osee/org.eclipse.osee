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
package org.eclipse.osee.framework.ui.admin.dbtabletab;

import java.util.ArrayList;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.ui.admin.AdminPlugin;
import org.eclipse.osee.framework.ui.admin.AdminView;
import org.eclipse.osee.framework.ui.skynet.widgets.XCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class DbTableTab {
   protected Browser browser;
   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.admin.AdminView";
   public static User person;
   public static DbTableViewer dbTableViewer;
   private static XCombo filterCombo;
   private Label readOnlyLabel, addRecordLabel, publishLabel;
   private ArrayList<DbItem> dbItems;
   private Cursor handCursor;
   private boolean noListener = true;
   private Composite parent;

   public DbTableTab(TabFolder tabFolder) {
      super();

      dbItems = new ArrayList<DbItem>();
      dbItems.add(new SiteGssflRpcr());
      dbItems.add(new OseeInfoDbItem());
      handCursor = new Cursor(null, SWT.CURSOR_HAND);

      TabItem dbTab = new TabItem(tabFolder, SWT.NULL);
      dbTab.setText("Database");

      // Filter Composite
      int numColumns = 5;
      Composite c = new Composite(tabFolder, SWT.NONE);
      c.setLayout(new GridLayout(numColumns, false));
      c.setLayoutData(new GridData());

      c.setLayoutData(new GridData(GridData.FILL_BOTH));
      this.parent = c;

      filterCombo = new XCombo("DB Table", "");
      filterCombo.setDataStrings(getDbTableNames());
      filterCombo.createWidgets(c, 2);
      filterCombo.addModifyListener(new ModifyListener() {

         public void modifyText(ModifyEvent e) {
            try {
               handleTableSelect();
            } catch (OseeDataStoreException ex) {
               OseeLog.log(AdminPlugin.class, Level.SEVERE, ex);
            }
            AdminView.setSaveNeeded(false);
         }
      });

      readOnlyLabel = new Label(c, SWT.NONE);
      readOnlyLabel.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));

      addRecordLabel = new Label(c, SWT.NONE);
      addRecordLabel.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
      addRecordLabel.addMouseTrackListener(new MouseTrackListener() {

         public void mouseEnter(MouseEvent e) {
            if (getSelectedDbItem() != null && getSelectedDbItem().isWriteAccess()) if (addRecordLabel != null) addRecordLabel.setCursor(handCursor);
         }

         public void mouseExit(MouseEvent e) {
            if (addRecordLabel != null) addRecordLabel.setCursor(null);
         };

         public void mouseHover(MouseEvent e) {
         }
      });

      publishLabel = new Label(c, SWT.NONE);
      publishLabel.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
      publishLabel.addMouseTrackListener(new MouseTrackListener() {

         public void mouseEnter(MouseEvent e) {
            if (getSelectedDbItem() != null && getSelectedDbItem().isWriteAccess()) if (publishLabel != null) publishLabel.setCursor(handCursor);
         }

         public void mouseExit(MouseEvent e) {
            if (publishLabel != null) publishLabel.setCursor(null);
         };

         public void mouseHover(MouseEvent e) {
         }
      });

      updateReadOnly();
      dbTab.setControl(c);
   }

   Listener addRecordListener = new Listener() {
      public void handleEvent(org.eclipse.swt.widgets.Event event) {
         System.out.println("I am adding a row...");
         handleAddRecord();
      };
   };

   public void handleAddRecord() {
      System.out.println("trying to add a record");
      dbTableViewer.addRecord();
   }

   public void updateReadOnly() {
      DbItem selItem = getSelectedDbItem();
      if (selItem == null) {
         readOnlyLabel.setText("");
         addRecordLabel.setText("                ");
      } else if (selItem.isWriteAccess()) {
         readOnlyLabel.setText("    WRITE ACCESS");
         addRecordLabel.setText("   Add Record   ");

         if (noListener) addRecordLabel.addListener(SWT.MouseUp, addRecordListener);
         noListener = false;
      } else {
         readOnlyLabel.setText("    READ ONLY ACCESS");
         addRecordLabel.setText("                ");
         addRecordLabel.removeListener(SWT.MouseUp, addRecordListener);
      }
   }

   public DbItem getSelectedDbItem() {
      for (DbItem d : dbItems) {
         if (filterCombo.get().equals(d.getTableName())) return d;
      }
      return null;
   }

   public void handleTableSelect() throws OseeDataStoreException {
      if (dbTableViewer != null) dbTableViewer.dispose();
      updateReadOnly();
      if (filterCombo.get().equals("")) return;

      dbTableViewer = new DbTableViewer(parent, 5, this, getSelectedDbItem());
      dbTableViewer.load();
      parent.layout();
   }

   public static void refresh() throws OseeDataStoreException {
      if (filterCombo.get().equals("")) return;
      if (dbTableViewer != null) {
         dbTableViewer.load();
         dbTableViewer.refresh();
      }
   }

   public XCombo getFilterCombo() {
      return filterCombo;
   }

   public String[] getDbTableNames() {
      String names[] = new String[dbItems.size()];
      for (int x = 0; x < dbItems.size(); x++) {
         names[x] = dbItems.get(x).getTableName();
      }
      return names;
   }

}
