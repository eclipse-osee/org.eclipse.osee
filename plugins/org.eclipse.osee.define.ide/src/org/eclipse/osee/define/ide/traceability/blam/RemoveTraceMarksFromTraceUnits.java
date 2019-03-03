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
package org.eclipse.osee.define.ide.traceability.blam;

import java.io.File;
import java.net.URI;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.define.ide.internal.Activator;
import org.eclipse.osee.define.ide.traceability.ITraceParser;
import org.eclipse.osee.define.ide.traceability.ITraceUnitResourceLocator;
import org.eclipse.osee.define.ide.traceability.TraceUnitExtensionManager;
import org.eclipse.osee.define.ide.traceability.TraceUnitExtensionManager.TraceHandler;
import org.eclipse.osee.define.ide.utility.IResourceHandler;
import org.eclipse.osee.define.ide.utility.UriResourceContentFinder;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.IExceptionableRunnable;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorProvider;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.html.ResultsEditorHtmlTab;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Roberto E. Escobar
 */
@SuppressWarnings("deprecation")
public class RemoveTraceMarksFromTraceUnits extends AbstractBlam {

   @Override
   public String getName() {
      return "Remove Trace Marks from Resource";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Define.Trace");
   }

   @Override
   public String getDescriptionUsage() {
      return "Removes trace marks from files selected.\n*** WARNING_OVERLAY: When \"Persist Changes\" is selected, files will be modified in place.\n There is no way to undo this operation - make sure you know what you are doing. ***\n ";
   }

   @Override
   public String getXWidgetsXml() {
      StringBuilder builder = new StringBuilder();
      builder.append("<xWidgets>");
      builder.append(
         "<XWidget xwidgetType=\"XLabel\" displayName=\"Select File Or Folder (file can have a list of folders separated by newlines)\"/>");
      builder.append("<XWidget xwidgetType=\"XFileSelectionDialog\" displayName=\"Select File\" />");
      builder.append("<XWidget xwidgetType=\"XDirectorySelectionDialog\" displayName=\"Select Folder\" />");
      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\"Select Trace Types:\"/>");
      for (TraceHandler handler : getTraceHandlers()) {
         builder.append(getOperationsCheckBoxes(handler.getName()));
      }
      builder.append(
         "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Persist Changes\" labelAfter=\"true\" horizontalLabel=\"true\" />");
      builder.append(
         "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Include Sub-Folders\" labelAfter=\"true\" horizontalLabel=\"true\" />");
      builder.append(
         "<XWidget xwidgetType=\"XCheckBox\" displayName=\"File With Embedded Paths\" labelAfter=\"true\" horizontalLabel=\"true\" />");
      builder.append("</xWidgets>");
      return builder.toString();
   }

   private TraceHandler getCheckedTraceHandler(VariableMap variableMap) {
      List<TraceHandler> toReturn = new ArrayList<>();
      for (TraceHandler handler : getTraceHandlers()) {
         if (variableMap.getBoolean(handler.getName())) {
            toReturn.add(handler);
         }
      }
      if (toReturn.isEmpty()) {
         throw new OseeArgumentException("Please select a trace type");
      } else if (toReturn.size() > 1) {
         throw new OseeArgumentException("Only (1) trace type can be selected per run. Please de-select other types.");
      }
      return toReturn.get(0);
   }

   private void checkPath(String filePath, String type) {
      if (!Strings.isValid(filePath)) {
         throw new OseeArgumentException("Please enter a valid %s path", type);
      }
      File file = new File(filePath);
      if (!file.exists()) {
         throw new OseeArgumentException("%s path [%s] is not accessible", type, filePath);
      }
   }

   private URI getSourceURI(VariableMap variableMap) {
      String filePath = variableMap.getString("Select File");
      String folderPath = variableMap.getString("Select Folder");

      String pathToUse = null;
      if (Strings.isValid(folderPath) && Strings.isValid(filePath)) {
         throw new OseeArgumentException("Enter file or folder but not both");
      } else if (Strings.isValid(folderPath)) {
         checkPath(folderPath, "folder");
         pathToUse = folderPath;
      } else {
         checkPath(filePath, "file");
         pathToUse = filePath;
      }
      return new File(pathToUse).toURI();
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      try {
         final URI source = getSourceURI(variableMap);
         final TraceHandler handler = getCheckedTraceHandler(variableMap);
         final boolean isInPlaceStorageAllowed = variableMap.getBoolean("Persist Changes");
         final boolean isRecursionAllowed = variableMap.getBoolean("Include Sub-Folders");
         final boolean isFileWithMultiplePaths = variableMap.getBoolean("File With Embedded Paths");

         final int TOTAL_WORK = Integer.MAX_VALUE;
         monitor.beginTask(getName(), TOTAL_WORK);

         final MutableBoolean isProcessingAllowed = new MutableBoolean(false);
         Job job = new UIJob(getName()) {

            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
               isProcessingAllowed.setValue(isInPlaceStorageAllowed ? MessageDialog.openConfirm(new Shell(),
                  super.getName(), "Are you sure you want to remove trace marks from files?") : true);
               return Status.OK_STATUS;
            }
         };
         Jobs.startJob(job, true);
         job.join();

         if (isProcessingAllowed.getValue()) {
            ITraceUnitResourceLocator locator = handler.getLocator();
            ITraceParser parser = handler.getParser();

            ReportCreator reportCreator = new ReportCreator(monitor);
            UriResourceContentFinder resourceFinder =
               new UriResourceContentFinder(Arrays.asList(source), isRecursionAllowed, isFileWithMultiplePaths);
            resourceFinder.addLocator(locator,
               new TraceRemover(isInPlaceStorageAllowed, locator, parser, reportCreator));

            SubProgressMonitor subMonitor = new SubProgressMonitor(monitor, TOTAL_WORK);
            resourceFinder.execute(subMonitor);

            reportCreator.openReport();
         }
      } finally {
         monitor.done();
      }
   }

   private String getOperationsCheckBoxes(String value) {
      StringBuilder builder = new StringBuilder();
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"");
      builder.append(value);
      builder.append("\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
      return builder.toString();
   }

   private List<TraceHandler> getTraceHandlers() {
      List<TraceHandler> handlers = new ArrayList<>();
      try {
         for (TraceHandler handler : TraceUnitExtensionManager.getInstance().getAllTraceHandlers()) {
            if (handler.getParser().isTraceRemovalAllowed()) {
               handlers.add(handler);
            }
         }
      } catch (Exception ex) {
         log(ex);
      }
      return handlers;
   }

   private static final class TraceRemover implements IResourceHandler {
      private final ITraceParser traceParser;
      private final ITraceUnitResourceLocator traceUnitLocator;
      private final boolean isStorageAllowed;
      private final ReportCreator reportCreator;

      public TraceRemover(boolean isStorageAllowed, ITraceUnitResourceLocator traceUnitLocator, ITraceParser traceParser, ReportCreator reportCreator) {
         this.isStorageAllowed = isStorageAllowed;
         this.traceParser = traceParser;
         this.traceUnitLocator = traceUnitLocator;
         this.reportCreator = reportCreator;
      }

      @Override
      public void onResourceFound(URI uriPath, String name, CharBuffer fileBuffer) {
         ArtifactTypeToken traceUnitType = traceUnitLocator.getTraceUnitType(name, fileBuffer);
         if (!traceUnitType.equals(ITraceUnitResourceLocator.UNIT_TYPE_UNKNOWN)) {
            if (traceParser.isTraceRemovalAllowed()) {
               CharBuffer modifiedBuffer = traceParser.removeTraceMarks(fileBuffer);
               if (modifiedBuffer != null) {
                  reportCreator.addModifiedItem(name, fileBuffer, modifiedBuffer);
                  if (isStorageAllowed) {
                     try {
                        Lib.writeCharBufferToFile(modifiedBuffer, new File(uriPath));
                     } catch (Exception ex) {
                        OseeLog.log(Activator.class, Level.SEVERE, ex);
                     }
                  }
               } else {
                  reportCreator.addNoChangeItem(name);
               }
            }
         }
      }
   }

   private final class ReportCreator {
      private List<IResultsXViewerRow> modifiedRows;
      private List<IResultsXViewerRow> noChangeRows;
      private final IProgressMonitor monitor;

      public ReportCreator(IProgressMonitor monitor) {
         this.modifiedRows = null;
         this.noChangeRows = null;
         this.monitor = monitor;
      }

      public void addModifiedItem(String name, CharBuffer original, CharBuffer modified) {
         if (modifiedRows == null) {
            modifiedRows = new ArrayList<>();
         }
         String delta = getDelta(original, modified);
         String[] entries = delta.split("(\\n|;)");
         for (String diff : entries) {
            diff = diff.trim();
            if (Strings.isValid(diff)) {
               modifiedRows.add(new ResultsXViewerRow(new String[] {name, diff}));
            }
         }
      }

      private String getDelta(CharBuffer original, CharBuffer modified) {
         StringBuilder buffer = new StringBuilder();
         int originalLength = original.length();
         int modifiedLength = modified.length();

         int origIndex = 0;
         int modIndex = 0;
         while (origIndex < originalLength || modIndex < modifiedLength) {
            char origChar = original.get(origIndex);
            char modChar = modified.get(modIndex);
            if (origChar != modChar) {
               buffer.append(origChar);
               if (originalLength > modifiedLength) {
                  origIndex++;
               } else {
                  modIndex++;
               }
            } else {
               origIndex++;
               modIndex++;
            }
            if (monitor.isCanceled()) {
               break;
            }
         }
         return buffer.toString();
      }

      public void addNoChangeItem(String... name) {
         if (noChangeRows == null) {
            noChangeRows = new ArrayList<>();
         }
         noChangeRows.add(new ResultsXViewerRow(name));
      }

      private List<XViewerColumn> getNoChangeHeaders() {
         return createColumnHelper("Trace Unit Without Change");
      }

      private List<XViewerColumn> getModifiedHeaders() {
         return createColumnHelper("Modified Trace Unit", "Removed");
      }

      private List<XViewerColumn> createColumnHelper(String... headers) {
         List<XViewerColumn> columns = new ArrayList<>();
         for (String name : headers) {
            columns.add(new XViewerColumn(name, name, 80, XViewerAlign.Left, true, SortDataType.String, false, ""));
         }
         return columns;
      }

      public void openReport() {
         IExceptionableRunnable runnable = new IExceptionableRunnable() {

            @Override
            public IStatus run(IProgressMonitor monitor) throws Exception {
               ResultsEditor.open(new IResultsEditorProvider() {
                  @Override
                  public String getEditorName() {
                     return getName();
                  }

                  @Override
                  public List<IResultsEditorTab> getResultsEditorTabs() {
                     List<IResultsEditorTab> resultsTabs = new ArrayList<>();
                     if (modifiedRows != null && !modifiedRows.isEmpty()) {
                        resultsTabs.add(
                           new ResultsEditorTableTab("Modified Trace Units", getModifiedHeaders(), modifiedRows));
                     }
                     if (noChangeRows != null && !noChangeRows.isEmpty()) {
                        resultsTabs.add(
                           new ResultsEditorTableTab("Unmodified Items", getNoChangeHeaders(), noChangeRows));
                     }
                     if (resultsTabs.isEmpty()) {
                        resultsTabs.add(new ResultsEditorHtmlTab(getName(), getName(), "No changes Reported"));
                     }
                     return resultsTabs;
                  }
               });
               return Status.OK_STATUS;
            }
         };
         Jobs.runInJob(getName(), runnable, Activator.class, Activator.PLUGIN_ID);
      }
   }

   @Override
   public String getTarget() {
      return TARGET_ALL;
   }

}
