/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.blam.operation;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.nebula.widgets.xviewer.Activator;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.define.blam.operation.FixAttributeOperation.Display;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorProvider;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Angel Avila
 */

public class FixAttributesUIReport implements Display {

   @Override
   public void displayReport(String reportName, List<String[]> values) {
      List<XViewerColumn> columns = new ArrayList<>();
      addColumn(columns, "Branch Name");
      addColumn(columns, "Artifact Guid");
      addColumn(columns, "Artifact Name");
      addColumn(columns, "Attribute Type");
      addColumn(columns, "Was Value");
      addColumn(columns, "Fixed Value");

      List<IResultsXViewerRow> rows = new ArrayList<>();
      for (String[] row : values) {
         rows.add(new ResultsXViewerRow(row));
      }

      List<IResultsEditorTab> tabs = new ArrayList<>();
      tabs.add(new ResultsEditorTableTab("Results", columns, rows));

      ReportProvider report = new ReportProvider(reportName, tabs);
      openReport(report);
   }

   private void addColumn(List<XViewerColumn> columns, String name) {
      columns.add(new XViewerColumn(name, name, 120, XViewerAlign.Left, true, SortDataType.String, false, ""));
   }

   private void openReport(final ReportProvider report) {
      Job job = new UIJob(report.getEditorName()) {
         @Override
         public IStatus runInUIThread(IProgressMonitor monitor) {
            IStatus status;
            try {
               ResultsEditor.open(report);
               status = Status.OK_STATUS;
            } catch (Exception ex) {
               String message = String.format("Error creating [%s] Report", getName());
               status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, message, ex);
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
      public String getEditorName() {
         return editorName;
      }

      @Override
      public List<IResultsEditorTab> getResultsEditorTabs() {
         return resultsTabs;
      }
   }
}
