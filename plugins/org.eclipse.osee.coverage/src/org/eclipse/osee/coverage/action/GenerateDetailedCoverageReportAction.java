/*******************************************************************************
 * Copyright (c) 2010 Boeing.
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
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
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
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.TableWriterAdaptor;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.program.Program;
import com.lowagie.text.Table;

/**
 * @author Donald G. Dunne
 */
public class GenerateDetailedCoverageReportAction extends Action {

   private final ICoveragePackageHandler coveragePackageHandler;
   private Collection<XViewerColumn> columns =
         Arrays.asList(CoverageXViewerFactory.Namespace, CoverageXViewerFactory.Coverage_Method,
               CoverageXViewerFactory.Guid, CoverageXViewerFactory.Name);

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
         Date date = new Date();
         File file =
               OseeData.getFile("coverage_" + XDate.getDateStr(date, XDate.YYYY_MM_DD).replaceAll("\\\\", "_") + ".pdf");
         OutputStream outputStream = new FileOutputStream(file, true);
         TableWriterAdaptor masterAdaptor = new TableWriterAdaptor("pdf", outputStream);

         CoveragePackageBase coveragePackageBase = this.coveragePackageHandler.getCoveragePackageBase();

         List<String> sortedHeaders = CoverageEditorOverviewTab.getSortedHeaders(coveragePackageBase);
         masterAdaptor.writeHeader(sortedHeaders.toArray(new String[sortedHeaders.size()]));
         masterAdaptor.getTable().setWidth(100);
         for (String[] values : CoverageEditorOverviewTab.getRows(sortedHeaders, coveragePackageBase, false)) {
            masterAdaptor.writeRow(values);
         }

         masterAdaptor.writeTitle("Detailed Coverage Report as of " + XDate.getDateStr(date, XDate.MMDDYYHHMM) + " for " + this.coveragePackageHandler.getCoveragePackageBase().getName());
         masterAdaptor.openDocument();
         masterAdaptor.writeDocument();

         masterAdaptor.addTable(getDetailTable());
         masterAdaptor.close();

         Program.launch(file.getAbsolutePath());
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   private Table getDetailTable() throws Exception {
      TableWriterAdaptor detailAdaptor = new TableWriterAdaptor("pdf", null);
      detailAdaptor.writeHeader(getColumns());
      detailAdaptor.getTable().setWidths(new float[] {400, 75, 75, 200});
      detailAdaptor.getTable().setWidth(100);
      writeRows(detailAdaptor);
      return detailAdaptor.getTable();
   }

   private void writeRows(TableWriterAdaptor writerAdaptor) {
      CoverageLabelProvider labelProvider = new CoverageLabelProvider(null);
      for (CoverageItem item : this.coveragePackageHandler.getCoveragePackageBase().getCoverageItems()) {
         List<String> values = new ArrayList<String>();
         for (XViewerColumn column : columns) {
            try {
               if (column.equals(CoverageXViewerFactory.Namespace)) {
                  values.add(String.format("%s[%s][%s]", CoverageUtil.getFullPath(item), labelProvider.getColumnText(
                        item, CoverageXViewerFactory.Method_Number, 0), labelProvider.getColumnText(item,
                        CoverageXViewerFactory.Execution_Number, 0)));
               } else if (column.equals(CoverageXViewerFactory.Coverage_Method)) {
                  String rationale = labelProvider.getColumnText(item, CoverageXViewerFactory.Coverage_Rationale, 0);
                  values.add(String.format("%s%s", labelProvider.getColumnText(item,
                        CoverageXViewerFactory.Coverage_Method, 0),
                        Strings.isValid(rationale) ? "Rationale: " + rationale : ""));
               } else {
                  values.add(labelProvider.getColumnText(item, column, 0));
               }
            } catch (OseeCoreException ex) {
               values.add("Exception: " + ex.getLocalizedMessage());
               OseeLog.log(Activator.class, OseeLevel.SEVERE, ex);
            }
         }
         writerAdaptor.writeRow(values.toArray(new String[values.size()]));
      }
   }

   private String[] getColumns() {
      List<String> columnNames = new ArrayList<String>();
      for (XViewerColumn column : columns) {
         columnNames.add(column.getName());
      }
      return columnNames.toArray(new String[columnNames.size()]);
   }
}
