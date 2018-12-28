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
package org.eclipse.osee.framework.ui.skynet.dbHealth;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.result.Manipulations;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorProvider;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditorConverter;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultPage;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseIntegrityCheckApplication implements IApplication {

   private final ResultsEditorConverter converter;

   public DatabaseIntegrityCheckApplication() {
      converter = new ResultsEditorConverter();
   }

   @Override
   public Object start(IApplicationContext context) throws Exception {
      UserManager.getUser();
      String currentDir = System.getProperty("user.dir");
      File reportsDirectory = new File(currentDir, "databaseIntegrity_" + Lib.getDateTimeString());
      reportsDirectory.mkdirs();

      SummaryTab data = new SummaryTab("Database Integrity Summary");

      for (DatabaseHealthOperation op : DatabaseHealthOpsExtensionManager.getVerifyOperations()) {
         try {
            executeCheck(data, op, reportsDirectory);
         } catch (Exception ex) {
            data.addError(op, ex);
         }
      }

      String fileName = generateFileName("html", "Summary");
      File reportFile = new File(reportsDirectory, fileName);
      Writer writer = new BufferedWriter(new FileWriter(reportFile));
      try {
         converter.convert("html", writer, data);
      } finally {
         writer.close();
      }

      return Status.OK_STATUS;
   }

   private void executeCheck(SummaryTab data, DatabaseHealthOperation operation, File reportsDirectory) throws Exception {
      operation.setFixOperationEnabled(false);
      Operations.executeWorkAndCheckStatus(operation);

      List<String> summaryLinks = new ArrayList<>();
      int count = operation.getItemsToFixCount();
      if (count > 0) {
         writeReport(operation, reportsDirectory, summaryLinks);
      }
      data.add(operation, summaryLinks);
   }

   private void writeReport(DatabaseHealthOperation operation, File reportsDirectory, List<String> summaryLinks) throws Exception {
      String detailedReport = operation.getDetailedReport().toString();
      if (Strings.isValid(detailedReport)) {
         XResultData result = new XResultData();
         result.addRaw(detailedReport.toString());
         XResultPage page = XResultDataUI.getReport(result, operation.getName(), Manipulations.RAW_HTML);

         String fileName = generateFileName("html", operation);
         summaryLinks.add(fileName);
         File reportFile = new File(reportsDirectory, fileName);
         Lib.writeStringToFile(page.getManipulatedHtml(), reportFile);
      } else {
         String fileName = generateFileName("xml", operation);
         summaryLinks.add(fileName);
         File reportFile = new File(reportsDirectory, fileName);
         Writer writer = new BufferedWriter(new FileWriter(reportFile));
         try {
            converter.convert("Excel", writer, operation.getResultsProvider());
         } finally {
            writer.close();
         }

         fileName = generateFileName("html", operation);
         summaryLinks.add(fileName);
         reportFile = new File(reportsDirectory, fileName);
         writer = new BufferedWriter(new FileWriter(reportFile));
         try {
            converter.convert("html", writer, operation.getResultsProvider());
         } finally {
            writer.close();
         }
      }
   }

   private String generateFileName(String extension, DatabaseHealthOperation operation) {
      return generateFileName(extension, operation.getName().replaceAll(" ", "_"));
   }

   private String generateFileName(String extension, String name) {
      return String.format("%s_%s.%s", name, Lib.getDateTimeString(), extension);
   }

   @Override
   public void stop() {
      // do nothing
   }

   private final class SummaryTab implements IResultsEditorProvider {

      private final String title;
      private final List<IResultsEditorTab> tabs;
      private final ResultsEditorTableTab mainTab;

      public SummaryTab(String title) {
         this.title = title;
         this.tabs = new ArrayList<>();

         mainTab = new ResultsEditorTableTab(title);
         mainTab.addColumn(
            new XViewerColumn("1", "Operation", 220, XViewerAlign.Left, true, SortDataType.String, false, ""));
         mainTab.addColumn(
            new XViewerColumn("2", "Status", 80, XViewerAlign.Left, true, SortDataType.String, false, ""));
         mainTab.addColumn(
            new XViewerColumn("3", "Count", 80, XViewerAlign.Left, true, SortDataType.String, false, ""));
         mainTab.addColumn(
            new XViewerColumn("4", "Links", 80, XViewerAlign.Left, true, SortDataType.String, false, ""));
         tabs.add(mainTab);
      }

      public void add(DatabaseHealthOperation operation, List<String> links) {
         int count = operation.getItemsToFixCount();
         if (links.isEmpty()) {
            mainTab.addRow(new ResultsXViewerRow(
               new String[] {operation.getName(), count > 0 ? "Failed" : "Passed", String.valueOf(count), ""}));
         } else {
            int index = 0;
            for (String link : links) {
               String value = String.format("<a href=\"%s\">%s</a>", link, link);
               if (index == 0) {
                  mainTab.addRow(new ResultsXViewerRow(new String[] {
                     operation.getName(),
                     count > 0 ? "Failed" : "Passed",
                     String.valueOf(count),
                     value}));
               } else {
                  mainTab.addRow(new ResultsXViewerRow(new String[] {"", "", "", value}));
               }
               index++;
            }
         }
      }

      public void addError(DatabaseHealthOperation operation, Throwable th) {
         mainTab.addRow(
            new ResultsXViewerRow(new String[] {operation.getName(), "Expection", "0", Lib.exceptionToString(th), ""}));
      }

      @Override
      public String getEditorName() {
         return title;
      }

      @Override
      public List<IResultsEditorTab> getResultsEditorTabs() {
         return tabs;
      }

   }

}
