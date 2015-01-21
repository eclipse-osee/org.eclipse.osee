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
package org.eclipse.osee.ats.ev;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.api.util.IColumn;
import org.eclipse.osee.ats.core.client.ev.EarnedValueReportOperation;
import org.eclipse.osee.ats.core.client.ev.EarnedValueReportResult;
import org.eclipse.osee.ats.core.client.ev.WorkPackageArtifact;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.util.xviewer.column.XViewerIColumnAdapter;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.ArtifactDoubleClick;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorProvider;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.xresults.IResultsEditorTableListener;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class EarnedValueWorkPacakgeReportBlam extends AbstractBlam {

   private final static String WORK_PACKAGES = "Work Packages";

   public EarnedValueWorkPacakgeReportBlam() {
      // do nothing
   }

   @Override
   public void runOperation(final VariableMap variableMap, IProgressMonitor monitor) {
      try {
         List<Artifact> workPackageArts =
            org.eclipse.osee.framework.jdk.core.util.Collections.castAll(Artifact.class,
               variableMap.getArtifacts(WORK_PACKAGES));

         runReport(getName(), workPackageArts);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public static void runReport(final String name, Collection<Artifact> workPackageArts) {
      if (workPackageArts.isEmpty()) {
         AWorkbench.popup("ERROR", "Must drag in Work Packages(s).");
         return;
      }
      List<IAtsWorkPackage> workPackages = new ArrayList<IAtsWorkPackage>();
      for (Artifact artifact : workPackageArts) {
         workPackages.add(new WorkPackageArtifact(artifact));
      }
      final EarnedValueReportOperation operation = new EarnedValueReportOperation(name, workPackages);
      Operations.executeAsJob(operation, true, Job.LONG, new JobChangeAdapter() {

         @Override
         public void done(IJobChangeEvent event) {
            super.done(event);
            openReport(name, operation.getResults());
         }

      });
   }

   private static void openReport(final String name, final List<EarnedValueReportResult> results) {

      ResultsEditor.open(new IResultsEditorProvider() {

         private List<IResultsEditorTab> tabs;

         @Override
         public String getEditorName() {
            return name;
         }

         @Override
         public List<IResultsEditorTab> getResultsEditorTabs() {
            if (tabs == null) {
               tabs = new LinkedList<IResultsEditorTab>();
               tabs.add(createDataTab(results));
            }
            return tabs;
         }
      });
   }

   private static IResultsEditorTab createDataTab(List<EarnedValueReportResult> results) {
      List<XViewerColumn> columns = new ArrayList<XViewerColumn>();
      for (IColumn column : EarnedValueReportOperation.columns) {
         columns.add(new XViewerIColumnAdapter(column));
      }

      List<IResultsXViewerRow> rows = new ArrayList<IResultsXViewerRow>();
      for (EarnedValueReportResult result : results) {
         String strs[] = new String[EarnedValueReportOperation.columns.size()];
         int x = 0;
         for (IColumn column : EarnedValueReportOperation.columns) {
            strs[x++] = result.getValue(column);
         }
         rows.add(new ResultsXViewerRow(strs, result));
      }
      return new ResultsEditorTableTab("Data", columns, rows, null, null, Arrays.asList(listener));

   }

   private static final IResultsEditorTableListener listener = new IResultsEditorTableListener() {

      @Override
      public void handleDoubleClick(ArrayList<ResultsXViewerRow> selectedRows) {
         Object obj = selectedRows.iterator().next().getData();
         if (obj instanceof EarnedValueReportResult) {
            EarnedValueReportResult result = (EarnedValueReportResult) obj;
            MessageDialog dialog =
               new MessageDialog(Displays.getActiveShell(), "Open EV Item", null, "Select Item to Open",
                  MessageDialog.NONE, new String[] {"Work Package", "Action", "Cancel"}, 2);
            int sel = dialog.open();
            if (sel < 2) {
               Artifact openArt = result.getArtifact();
               if (sel == 0) {
                  openArt =
                     ArtifactQuery.getArtifactFromId(result.getWorkPackage().getGuid(), AtsUtilCore.getAtsBranch());
               }
               if (openArt != null) {
                  ArtifactDoubleClick.open(openArt);
               }
            }
         }
      }
   };

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XListDropViewer\" displayName=\"" + WORK_PACKAGES + "\" />" +
      //
      "</xWidgets>";
   }

   @Override
   public String getDescriptionUsage() {
      return "Generate a report of Actions to Work Packages for the purpose of EV rollup.";
   }

   @Override
   public String getName() {
      return "Earned Value Work Package Report";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("ATS/Reports");
   }

}
