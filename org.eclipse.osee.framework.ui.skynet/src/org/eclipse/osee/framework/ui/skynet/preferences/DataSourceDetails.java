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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.database.core.OseeInfo;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Roberto E. Escobar
 */
public class DataSourceDetails extends PreferencePage implements IWorkbenchPreferencePage {
   private static final String HTML_HEADER =
         "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html14/loose.dtd\">\n";

   private static final String CSS_SHEET =
         "<style type=\"text/css\"> table.oseeTable { font: 0.7em \"arial\", serif; border-width: 1px 1px 1px 1px; border-spacing: 2px; border-style: solid solid solid solid; border-color: blue blue blue blue; border-collapse: separate; background-color: rgb(255, 250, 250); } " + " table.oseeTable th { border-width: 1px 1px 1px 1px; padding: 4px 4px 4px 4px; border-style: solid solid solid solid; border-color: black black black black; background-color: white; -moz-border-radius: 0px 0px 0px 0px; } " + " table.oseeTable td { border-width: 1px 1px 1px 1px; padding: 4px 4px 4px 4px; border-style: solid solid solid solid; border-color: black black black black; background-color: white; -moz-border-radius: 0px 0px 0px 0px; } </style>\n";

   private static final String PAGE_TEMPLATE =
         HTML_HEADER + "<html>\n<head>\n" + CSS_SHEET + "</head>\n<body>\n%s</body>\n</html>";

   private Browser browser;

   public DataSourceDetails() {
      super();
      this.browser = null;
   }

   public void init(IWorkbench workbench) {
      setPreferenceStore(SkynetGuiPlugin.getInstance().getPreferenceStore());
      setDescription("See below for OSEE Data Source Details.");
   }

   @Override
   protected Control createContents(Composite parent) {
      Composite content = new Composite(parent, SWT.NONE);
      GridLayout layout = new GridLayout();
      layout.marginHeight = 0;
      layout.marginWidth = 0;
      content.setLayout(layout);
      content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      Group composite = new Group(content, SWT.NONE);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      composite.setText("Data Source(s)");

      browser = new Browser(composite, SWT.READ_ONLY | SWT.BORDER);
      browser.setLayout(new FillLayout());
      browser.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));

      Display.getDefault().asyncExec(new Runnable() {
         public void run() {
            generatePage();
         }
      });
      return content;
   }

   private void generatePage() {
      StringBuilder builder = new StringBuilder();
      builder.append(getDatabaseSourceInfo());
      builder.append("<br/>");
      builder.append(getDatabaseImportSource());
      browser.setText(String.format(PAGE_TEMPLATE, builder.toString()));
   }

   private String getDatabaseSourceInfo() {
      StringBuilder builder = new StringBuilder();
      builder.append("<table class=\"oseeTable\" width=\"100%\">");
      builder.append(AHTML.addHeaderRowMultiColumnTable(new String[] {"Data Source"}));
      try {
         builder.append(AHTML.addRowMultiColumnTable(String.format(
               "<b>Name:</b> %s<br/><b>Schema:</b> %s<br/><b>Driver:</b> %s<br/><b>Is Production:</b> %s<br/><b>ID:</b> %s<br/>",
               ClientSessionManager.getDataStoreName(), ClientSessionManager.getDataStoreLoginName(),
               ClientSessionManager.getDataStoreDriver(), ClientSessionManager.isProductionDataStore(),
               OseeInfo.getDatabaseGuid())));
      } catch (Exception ex) {
         builder.append(Lib.exceptionToString(ex));
      } finally {
         builder.append(AHTML.endMultiColumnTable());
      }
      return builder.toString();
   }

   private String getDatabaseImportSource() {
      DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
      StringBuilder builder = new StringBuilder();
      builder.append("<table class=\"oseeTable\" width=\"100%\">");
      builder.append(AHTML.addHeaderRowMultiColumnTable(new String[] {"Source Id", "Exported On", "Imported On"}));
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery("select * from osee_import_source");
         while (chStmt.next()) {
            builder.append(AHTML.addRowMultiColumnTable(chStmt.getString("db_source_guid"),
                  dateFormat.format(chStmt.getTimestamp("source_export_date")),
                  dateFormat.format(chStmt.getTimestamp("date_imported"))));
         }
      } catch (Exception ex) {
         builder.append(AHTML.addRowSpanMultiColumnTable(Lib.exceptionToString(ex), 3));
      } finally {
         builder.append(AHTML.endMultiColumnTable());
         chStmt.close();
      }
      return builder.toString().replaceAll("\n", "<br/>");
   }
}
