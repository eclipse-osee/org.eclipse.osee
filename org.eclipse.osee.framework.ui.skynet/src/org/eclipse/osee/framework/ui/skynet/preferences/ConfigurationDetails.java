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
package org.eclipse.osee.framework.ui.skynet.preferences;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.OseeDb;
import org.eclipse.osee.framework.db.connection.core.OseeApplicationServer;
import org.eclipse.osee.framework.db.connection.info.DbInformation;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.linking.HttpUrlBuilder;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Roberto E. Escobar
 */
public class ConfigurationDetails extends PreferencePage implements IWorkbenchPreferencePage {
   private static final Image STATUS_OK = SkynetGuiPlugin.getInstance().getImage("green_light.gif");
   private static final Image STATUS_ERROR = SkynetGuiPlugin.getInstance().getImage("red_light.gif");

   public ConfigurationDetails() {
      super();
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
    */
   @Override
   protected Control createContents(Composite parent) {
      Composite content = new Composite(parent, SWT.NONE);
      GridLayout layout = new GridLayout();
      layout.marginHeight = 0;
      layout.marginWidth = 0;
      content.setLayout(layout);
      content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      Group composite = new Group(content, SWT.NONE);
      GridLayout layout1 = new GridLayout();
      layout1.marginHeight = 0;
      layout1.marginWidth = 0;
      composite.setLayout(layout1);
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      composite.setText("Connections");

      TableViewer tableViewer = new TableViewer(composite, SWT.READ_ONLY);
      tableViewer.setContentProvider(new ArrayContentProvider());
      Table table = tableViewer.getTable();
      table.setLayout(new GridLayout());
      table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      table.setHeaderVisible(true);
      table.setLinesVisible(true);

      TableViewerColumn tc1 = new TableViewerColumn(tableViewer, SWT.LEFT);
      TableViewerColumn tc2 = new TableViewerColumn(tableViewer, SWT.LEFT);
      TableViewerColumn tc3 = new TableViewerColumn(tableViewer, SWT.CENTER);

      tc1.getColumn().setText("Type");
      tc2.getColumn().setText("Info");
      tc3.getColumn().setText("Status");

      tc1.getColumn().setWidth(125);
      tableViewer.setLabelProvider(new CellLabelProvider() {
         @Override
         public void update(ViewerCell cell) {
            DataRecord record = (DataRecord) cell.getElement();
            switch (cell.getColumnIndex()) {
               case 0:
                  String text = record.getLabel() + ":";
                  cell.setText(text);
                  cell.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_BLUE));
                  break;
               case 1:
                  cell.setText(record.getData());
                  cell.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
                  break;
               case 2:
                  cell.setImage(record.getStatus() ? STATUS_OK : STATUS_ERROR);
                  break;
               default:
                  break;
            }
         }
      });
      tableViewer.setInput(getConfigurationDetails());
      tc2.getColumn().pack();
      tc3.getColumn().pack();
      table.layout();
      return content;
   }

   private List<DataRecord> getConfigurationDetails() {
      List<DataRecord> configurationDetails = new ArrayList<DataRecord>();
      String defaultApplicationServer = null;
      try {
         defaultApplicationServer = HttpUrlBuilder.getInstance().getApplicationServerPrefix();
      } catch (SQLException ex) {
         OseeLog.log(this.getClass(), Level.SEVERE, ex.toString(), ex);
      }
      DataRecord record =
            new DataRecord("Application Server", defaultApplicationServer != null ? defaultApplicationServer : "");
      record.setStatus(OseeApplicationServer.isApplicationServerAlive());
      configurationDetails.add(record);

      boolean dbConnection = false;
      String dbStatus = "Unavailable";
      Connection connection = null;
      try {
         connection = ConnectionHandler.getPooledConnection();
         DatabaseMetaData dbData = connection.getMetaData();
         dbStatus = getDbStatus(dbData.getDatabaseProductName(), dbData.getDatabaseProductVersion(), dbData.getURL());
         dbConnection = true;
      } catch (Exception ex) {
         DbInformation info = OseeDb.getDefaultDatabaseService();
         dbStatus = getDbStatus(info.getDatabaseDetails().getId(), "", info.getConnectionUrl());
         dbConnection = false;
      } finally {
         if (connection != null) {
            try {
               connection.close();
            } catch (SQLException ex) {
            }
         }
      }
      record = new DataRecord("Database", dbStatus);
      record.setStatus(dbConnection);
      configurationDetails.add(record);
      return configurationDetails;
   }

   private String getDbStatus(String dbName, String dbVersion, String url) {
      StringBuffer dbInfo = new StringBuffer();
      if (Strings.isValid(dbName)) {
         dbName = dbName.replaceAll("\n", "");
         dbName = dbName.replaceAll("\r", "");
      }
      dbInfo.append(dbName);
      dbInfo.append(" ");
      dbInfo.append(dbVersion);
      dbInfo.append(" - ");
      Pattern pattern = Pattern.compile("//(.*)?.*?/(.*)");
      Matcher matcher = pattern.matcher(url);
      if (matcher.find()) {
         dbInfo.append(matcher.group(1));
         dbInfo.append(" - ");
         dbInfo.append(matcher.group(2));
      }
      dbInfo.append("  ");
      return dbInfo.toString();
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
    */
   public void init(IWorkbench workbench) {
      setPreferenceStore(SkynetActivator.getInstance().getPreferenceStore());
      setDescription("See below for OSEE configuration details.");
   }

   private class DataRecord {
      private String label;
      private String data;
      private boolean status;

      DataRecord(String label, String data) {
         this.label = label;
         this.data = data != null ? data : "";
         this.status = false;
      }

      public String getLabel() {
         return label;
      }

      public String getData() {
         return data;
      }

      public boolean getStatus() {
         return status;
      }

      public void setStatus(boolean status) {
         this.status = status;
      }
   }

}
