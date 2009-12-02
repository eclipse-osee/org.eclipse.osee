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
package org.eclipse.osee.framework.types.bridge.operations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.osee.framework.core.data.TableData;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.types.bridge.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorProvider;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;
import org.eclipse.swt.SWT;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Roberto E. Escobar
 */
public class CreateEditorReportOperation extends AbstractOperation {
   private final Collection<TableData> tableData;
   private final String reportName;

   public CreateEditorReportOperation(String reportName, Collection<TableData> tableData) {
      super("Generate Report", Activator.PLUGIN_ID);
      this.tableData = tableData;
      this.reportName = reportName;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      List<IResultsEditorTab> tabs = new ArrayList<IResultsEditorTab>();
      for (TableData data : tableData) {
         List<XViewerColumn> columns = new ArrayList<XViewerColumn>();
         for (String name : data.getColumns()) {
            columns.add(new XViewerColumn(name, name, 80, SWT.LEFT, true, SortDataType.String, false, ""));
         }
         List<IResultsXViewerRow> rows = new ArrayList<IResultsXViewerRow>();
         for (String[] row : data.getRows()) {
            rows.add(new ResultsXViewerRow(row));
         }
         tabs.add(new ResultsEditorTableTab(data.getTitle(), columns, rows));
      }
      openReport(tabs);
   }

   private void openReport(final List<IResultsEditorTab> resultsTabs) {
      Job job = new UIJob(reportName) {

         @Override
         public IStatus runInUIThread(IProgressMonitor monitor) {
            IStatus status;
            try {
               ResultsEditor.open(new ReportProvider(getName(), resultsTabs));
               status = Status.OK_STATUS;
            } catch (Exception ex) {
               status =
                     new Status(IStatus.ERROR, Activator.PLUGIN_ID, String.format("Error creating %s Report",
                           reportName), ex);
            }
            return status;
         }
      };
      Operations.scheduleJob(job, true, Job.SHORT, null);
   }

   private static final class ReportProvider implements IResultsEditorProvider {
      private final List<IResultsEditorTab> resultsTabs;
      private final String editorName;

      public ReportProvider(String editorName, List<IResultsEditorTab> resultsTabs) {
         this.resultsTabs = resultsTabs;
         this.editorName = editorName;
      }

      @Override
      public String getEditorName() throws OseeCoreException {
         return editorName;
      }

      @Override
      public List<IResultsEditorTab> getResultsEditorTabs() throws OseeCoreException {
         return resultsTabs;
      }
   }
}
