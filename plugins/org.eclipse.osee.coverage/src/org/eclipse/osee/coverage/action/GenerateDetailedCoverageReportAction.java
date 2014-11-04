/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.action;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.coverage.editor.CoverageEditorOverviewTab;
import org.eclipse.osee.coverage.editor.xcover.CoverageLabelProvider;
import org.eclipse.osee.coverage.editor.xcover.CoverageXViewerFactory;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoveragePackageBase;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.framework.core.model.TableData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.program.Program;

/**
 * @author Angel Avila
 */
public class GenerateDetailedCoverageReportAction extends Action {

   private final ICoveragePackageHandler coveragePackageHandler;
   private final Collection<XViewerColumn> detailedColumns = Arrays.asList(CoverageXViewerFactory.Namespace,
      CoverageXViewerFactory.Parent_Coverage_Unit, CoverageXViewerFactory.Unit, CoverageXViewerFactory.Method_Number,
      CoverageXViewerFactory.Execution_Number, CoverageXViewerFactory.Coverage_Method,
      CoverageXViewerFactory.Coverage_Rationale);

   private final Collection<XViewerColumn> summaryColumns =
      Arrays.asList(CoverageXViewerFactory.Unit, CoverageXViewerFactory.Lines_Covered,
         CoverageXViewerFactory.Total_Lines, CoverageXViewerFactory.Coverage_Percent);

   public GenerateDetailedCoverageReportAction(ICoveragePackageHandler coveragePackageHandler) {
      super("Generate Detailed Coverage Report");
      this.coveragePackageHandler = coveragePackageHandler;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.REPORT);
   }

   @Override
   public void run() {
      if (!MessageDialog.openConfirm(Displays.getActiveShell(), getText(), getText())) {
         return;
      }
      try {
         File file = OseeData.getFile("coverage_" + Lib.getDateTimeString() + ".xml");

         CharBackedInputStream charBak = new CharBackedInputStream();
         ISheetWriter excelWriter = new ExcelXmlWriter(charBak.getWriter(), null, "");

         // Cover Sheet
         writeSheet(excelWriter, createCoverSheetTable());
         // Detail sheet
         writeSheet(excelWriter, createDetailedTable());
         // Summary Sheet
         writeSheet(excelWriter, createSummaryTable());

         excelWriter.endWorkbook();

         IFile iFile = OseeData.getIFile(file.getName());
         AIFile.writeToFile(iFile, charBak);
         charBak.close();
         Program.launch(file.getAbsolutePath());

      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   private void writeSheet(ISheetWriter excelWriter, TableData data) throws IOException {
      excelWriter.startSheet(data.getTitle(), detailedColumns.size());
      excelWriter.writeRow((Object[]) data.getColumns());
      for (Object[] row : data.getRows()) {
         excelWriter.writeRow(row);
      }

      excelWriter.endSheet();
   }

   private TableData createSummaryTable() throws Exception {
      String title = "Summary";
      String[] columnsNames = getColumnNames(summaryColumns);
      List<String[]> rows =
         getSummaryRows(coveragePackageHandler.getCoveragePackageBase().getCoverageUnits(), summaryColumns);

      TableData tableData = new TableData(title, columnsNames, rows);
      return tableData;
   }

   private TableData createDetailedTable() throws Exception {
      String title = "Detailed";
      String[] columnsNames = getColumnNames(detailedColumns);
      List<String[]> rows = getDetailedRows(detailedColumns);

      TableData tableData = new TableData(title, columnsNames, rows);
      return tableData;
   }

   private TableData createCoverSheetTable() throws Exception {
      String title = "Cover Sheet";

      CoveragePackageBase coveragePackageBase = this.coveragePackageHandler.getCoveragePackageBase();
      List<String> sortedHeaders = CoverageEditorOverviewTab.getSortedHeaders(coveragePackageBase);
      String[] columnsNames = sortedHeaders.toArray(new String[sortedHeaders.size()]);

      List<String[]> rows = new ArrayList<String[]>();
      for (String[] values : CoverageEditorOverviewTab.getRows(sortedHeaders, coveragePackageBase, false)) {
         rows.add(values);
      }

      TableData tableData = new TableData(title, columnsNames, rows);
      return tableData;
   }

   private String[] getColumnNames(Collection<XViewerColumn> columns) {
      List<String> names = new ArrayList<String>();

      for (XViewerColumn column : columns) {
         names.add(column.getName());
      }

      String[] namesArray = names.toArray(new String[names.size()]);
      return namesArray;
   }

   private ArrayList<String[]> getSummaryRows(List<CoverageUnit> units, Collection<XViewerColumn> cols) {
      ArrayList<String[]> rows = new ArrayList<String[]>();
      CoverageLabelProvider labelProvider = new CoverageLabelProvider(null);

      for (CoverageUnit unit : units) {
         if (unit.getCoverageItems().size() == 0) { // Check to make sure we're not dealing with Coverage Units at Method level
            List<String> row = new ArrayList<String>();
            for (XViewerColumn col : cols) {
               try {
                  if (col.equals(CoverageXViewerFactory.Unit)) {
                     row.add(labelProvider.getColumnText(unit, col, 0));
                  } else if (col.equals(CoverageXViewerFactory.Lines_Covered)) {
                     row.add(labelProvider.getColumnText(unit, col, 0));
                  } else if (col.equals(CoverageXViewerFactory.Total_Lines)) {
                     row.add(labelProvider.getColumnText(unit, col, 0));
                  } else if (col.equals(CoverageXViewerFactory.Coverage_Percent)) {
                     String percentString = labelProvider.getColumnText(unit, col, 0);
                     row.add(percentString.replaceAll("-.*", "")); // replace everything after '-' since we only care about the pure percentage in this column
                  }
               } catch (OseeCoreException ex) {
                  row.add("Exception: " + ex.getLocalizedMessage());
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }

            String[] rowAsArray = row.toArray(new String[row.size()]);
            rows.add(rowAsArray);

            // Recurse down through each coverage unit
            rows.addAll(getSummaryRows(unit.getCoverageUnits(), cols));
         }
      }
      return rows;
   }

   private ArrayList<String[]> getDetailedRows(Collection<XViewerColumn> cols) {
      ArrayList<String[]> rows = new ArrayList<String[]>();
      CoverageLabelProvider labelProvider = new CoverageLabelProvider(null);
      for (CoverageItem item : this.coveragePackageHandler.getCoveragePackageBase().getCoverageItems()) {
         List<String> row = new ArrayList<String>();
         for (XViewerColumn column : cols) {
            try {
               if (column.equals(CoverageXViewerFactory.Namespace)) {
                  row.add(labelProvider.getColumnText(item, CoverageXViewerFactory.Namespace, 0));
               } else if (column.equals(CoverageXViewerFactory.Method_Number)) {
                  row.add(labelProvider.getColumnText(item, CoverageXViewerFactory.Method_Number, 0));
               } else if (column.equals(CoverageXViewerFactory.Line_Number)) {
                  row.add(labelProvider.getColumnText(item, CoverageXViewerFactory.Execution_Number, 0));
               } else if (column.equals(CoverageXViewerFactory.Parent_Coverage_Unit)) {
                  row.add(item.getParent().getParent().getName());
               } else if (column.equals(CoverageXViewerFactory.Coverage_Method)) {
                  row.add(labelProvider.getColumnText(item, CoverageXViewerFactory.Coverage_Method, 0));
               } else if (column.equals(CoverageXViewerFactory.Coverage_Rationale)) {
                  String rationale = labelProvider.getColumnText(item, CoverageXViewerFactory.Coverage_Rationale, 0);
                  if (rationale != "") {
                     row.add(rationale);
                  } else {
                     row.add("N/A");
                  }
               } else {
                  row.add(labelProvider.getColumnText(item, column, 0));
               }
            } catch (OseeCoreException ex) {
               row.add("Exception: " + ex.getLocalizedMessage());
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
         String[] rowAsArray = row.toArray(new String[row.size()]);
         rows.add(rowAsArray);
      }

      class Column2Comparator implements Comparator<String[]> {

         @Override
         public int compare(String[] array, String[] toArray) {
            int result;
            if (array.length > 3 && toArray.length > 3) {
               result = array[2].compareTo(toArray[2]);
            } else {
               result = array.toString().compareTo(toArray.toString());
            }

            return result;
         }
      }
      ;

      Collections.sort(rows, new Column2Comparator());
      return rows;

   }
}
