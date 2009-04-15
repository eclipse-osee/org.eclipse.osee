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
package org.eclipse.osee.define.traceability.blam;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.osee.define.DefinePlugin;
import org.eclipse.osee.define.traceability.data.BaseTraceDataCache;
import org.eclipse.osee.define.traceability.data.CodeUnitData;
import org.eclipse.osee.define.traceability.data.RequirementData;
import org.eclipse.osee.define.traceability.data.TestUnitData;
import org.eclipse.osee.define.traceability.report.AbstractArtifactRelationReport;
import org.eclipse.osee.define.traceability.report.ArtifactToRelatedArtifact;
import org.eclipse.osee.define.traceability.report.ArtifactTraceCount;
import org.eclipse.osee.define.traceability.report.ArtifactsWithoutRelations;
import org.eclipse.osee.define.traceability.report.IReportDataCollector;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.IExceptionableRunnable;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration;
import org.eclipse.osee.framework.ui.plugin.util.AIFile;
import org.eclipse.osee.framework.ui.plugin.util.OseeData;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorProvider;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.program.Program;

/**
 * @author Roberto E. Escobar
 */
public class TraceReportBlam extends AbstractBlam {
   private List<IResultsEditorTableTab> resultsTabs;

   public TraceReportBlam() {
      this.resultsTabs = new ArrayList<IResultsEditorTableTab>();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#getName()
    */
   @Override
   public String getName() {
      return "Trace Report";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getCategories()
    */
   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Define.Trace");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#getDescriptionUsage()
    */
   @Override
   public String getDescriptionUsage() {
      return "Usage Info here";
   }

   private String getOperationsCheckBoxes(String value) {
      StringBuilder builder = new StringBuilder();
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"");
      builder.append(value);
      builder.append("\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
      return builder.toString();
   }

   public String getXWidgetsXml() {
      StringBuilder builder = new StringBuilder();
      builder.append("<xWidgets>");
      builder.append("<XWidget xwidgetType=\"XFileSelectionDialog\" displayName=\"Select UI List File\" />");
      builder.append("<XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Requirements Branch\" />");
      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\"Select Trace Types:\"/>");
      for (TraceTypeEnum traceType : TraceTypeEnum.values()) {
         builder.append(getOperationsCheckBoxes(traceType.asLabel()));
      }
      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\"Select Report Output:\"/>");
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"Result Editor\" labelAfter=\"true\" horizontalLabel=\"true\" />");
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"Excel\" labelAfter=\"true\" horizontalLabel=\"true\" />");
      builder.append("</xWidgets>");
      return builder.toString();
   }

   private String getUIsFilterFromFile(IProgressMonitor monitor, String filePath) throws Exception {
      String input;
      File file = new File(filePath);
      if (file == null || !file.exists()) {
         throw new OseeArgumentException("UI list file not accessible");
      }
      IFileStore fileStore = EFS.getStore(file.toURI());
      InputStream inputStream = null;
      try {
         inputStream = new BufferedInputStream(fileStore.openInputStream(EFS.NONE, monitor));
         input = Lib.inputStreamToString(inputStream);
      } finally {
         if (inputStream != null) {
            inputStream.close();
         }
      }
      return input;
   }

   private List<TraceTypeEnum> getCheckedTraceItems(VariableMap variableMap) throws OseeArgumentException {
      List<TraceTypeEnum> toReturn = new ArrayList<TraceTypeEnum>();
      for (TraceTypeEnum traceType : TraceTypeEnum.values()) {
         if (variableMap.getBoolean(traceType.asLabel())) {
            toReturn.add(traceType);
         }
      }
      return toReturn;
   }

   public boolean isTestSupportNeeded(List<TraceTypeEnum> traceTypes) {
      return traceTypes.contains(TraceTypeEnum.Used_By_Test_Unit_Trace);
   }

   public boolean isTestCaseNeeded(List<TraceTypeEnum> traceTypes) {
      return traceTypes.contains(TraceTypeEnum.Verified_By_Test_Unit_Trace);
   }

   public boolean isCodeUnitNeeded(List<TraceTypeEnum> traceTypes) {
      return traceTypes.contains(TraceTypeEnum.Code_Unit_Trace);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.VariableMap, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      //      String fileName = variableMap.getString("Select UI List File");
      Branch branch = variableMap.getBranch("Requirements Branch");
      if (branch == null) {
         throw new OseeArgumentException("Branch cannot be null");
      }
      List<TraceTypeEnum> traceTypes = getCheckedTraceItems(variableMap);
      if (traceTypes.isEmpty()) {
         throw new OseeArgumentException("Please select a trace type");
      }
      boolean isExcelOutput = variableMap.getBoolean("Excel");
      boolean isResultEditorOutput = variableMap.getBoolean("Result Editor");
      OutputType output = OutputType.asType(isExcelOutput, isResultEditorOutput);
      if (output == null) {
         throw new OseeArgumentException("Please select a report output type");
      }
      ISheetWriter writer = null;
      CharBackedInputStream excelInputStream = null;
      List<AbstractArtifactRelationReport> reports = new ArrayList<AbstractArtifactRelationReport>();
      List<BaseTraceDataCache> traceCache = new ArrayList<BaseTraceDataCache>();
      RequirementData reqData = new RequirementData(branch);
      CodeUnitData codeUnit = null;
      TestUnitData testUnit = null;
      try {
         reqData.initialize(monitor);
         traceCache.add(reqData);

         if (isCodeUnitNeeded(traceTypes)) {
            codeUnit = new CodeUnitData(branch);
            codeUnit.initialize(monitor);
            traceCache.add(codeUnit);
         }

         if (isTestCaseNeeded(traceTypes) || isTestSupportNeeded(traceTypes)) {
            testUnit = new TestUnitData(branch);
            testUnit.initialize(monitor);
            traceCache.add(testUnit);
         }

         if (!monitor.isCanceled()) {
            if (isExcelOutput(output)) {
               excelInputStream = new CharBackedInputStream();
               writer = new ExcelXmlWriter(excelInputStream.getWriter());
            }
            buildReport(reports, "Trace Unit <-> Requirement", output, writer, getTraceUnitToReqReport(codeUnit,
                  testUnit, traceTypes));
            buildReport(reports, "Disconnected Trace Unit", output, writer, getDisconnectedTraceUnitReport(codeUnit,
                  testUnit, traceTypes));
            buildReport(reports, "Requirement Trace Counts", output, writer, getTraceCountReport(reqData, traceTypes));

            for (AbstractArtifactRelationReport report : reports) {
               if (monitor.isCanceled()) break;
               report.process(monitor);
               report.clear();
            }
         }
         if (!resultsTabs.isEmpty()) {
            if (excelInputStream != null) {
               writer.endWorkbook();
               openExcel(excelInputStream);
            }
            openReport(resultsTabs);
         }
      } finally {
         for (AbstractArtifactRelationReport report : reports) {
            report.clear();
         }
         for (BaseTraceDataCache cache : traceCache) {
            cache.reset();
         }
         reports.clear();
         traceCache.clear();

         resultsTabs.clear();
      }
   }

   private boolean isExcelOutput(OutputType output) {
      return output == OutputType.Excel || output == OutputType.Both;
   }

   private void buildReport(List<AbstractArtifactRelationReport> reports, String title, OutputType output, ISheetWriter writer, AbstractArtifactRelationReport report) {
      if (isExcelOutput(output)) {
         report.addReportDataCollector(new ExcelReport(title, writer));
      }
      if (output == OutputType.ResultsEditor || output == OutputType.Both) {
         report.addReportDataCollector(new ResultEditorReport(title));
      }
      reports.add(report);
   }

   private void openExcel(final InputStream inputStream) throws OseeCoreException {
      IFile iFile = OseeData.getIFile("Trace_Report_" + Lib.getDateTimeString() + ".xml");
      AIFile.writeToFile(iFile, inputStream);
      Program.launch(iFile.getLocation().toOSString());
      if (inputStream != null) {
         try {
            inputStream.close();
         } catch (IOException ex) {
            // Do Nothing
         }
      }
   }

   private void openReport(final List<IResultsEditorTableTab> resultsTabs) {
      final List<IResultsEditorTab> results = new ArrayList<IResultsEditorTab>(resultsTabs);
      if (!results.isEmpty()) {
         IExceptionableRunnable runnable = new IExceptionableRunnable() {

            @Override
            public void run(IProgressMonitor monitor) throws Exception {
               try {
                  ResultsEditor.open(new IResultsEditorProvider() {
                     @Override
                     public String getEditorName() throws OseeCoreException {
                        return getName();
                     }

                     @Override
                     public List<IResultsEditorTab> getResultsEditorTabs() throws OseeCoreException {
                        return results;
                     }
                  });
               } catch (OseeCoreException ex) {
                  OseeLog.log(DefinePlugin.class, Level.SEVERE, ex);
               }
            }
         };
         Jobs.run(getName(), runnable, DefinePlugin.class, DefinePlugin.PLUGIN_ID);
      }
   }

   private void addRelationToCheck(List<TraceTypeEnum> traceTypes, AbstractArtifactRelationReport report, boolean fromTraceUnit) {
      for (TraceTypeEnum traceTypeEnum : traceTypes) {
         IRelationEnumeration relation =
               fromTraceUnit ? traceTypeEnum.getRelatedToRequirement() : traceTypeEnum.getRelatedToTraceUnit();
         report.addRelationToCheck(relation);
      }
   }

   private void addArtifacts(CodeUnitData codeUnit, TestUnitData testUnit, List<TraceTypeEnum> traceTypes, AbstractArtifactRelationReport report) {
      if (isCodeUnitNeeded(traceTypes)) {
         report.setArtifacts(codeUnit.getAllCodeUnits());
      }
      if (isTestCaseNeeded(traceTypes)) {
         report.setArtifacts(testUnit.getTestCases());
      }
      if (isTestSupportNeeded(traceTypes)) {
         report.setArtifacts(testUnit.getTestSupportItems());
      }
   }

   private AbstractArtifactRelationReport getTraceCountReport(RequirementData reqData, List<TraceTypeEnum> traceTypes) {
      ArtifactTraceCount report = new ArtifactTraceCount();
      report.setArtifacts(reqData.getDirectSwRequirements());
      addRelationToCheck(traceTypes, report, false);
      return report;
   }

   private AbstractArtifactRelationReport getDisconnectedTraceUnitReport(CodeUnitData codeUnit, TestUnitData testUnit, List<TraceTypeEnum> traceTypes) {
      ArtifactsWithoutRelations report = new ArtifactsWithoutRelations();
      addArtifacts(codeUnit, testUnit, traceTypes, report);
      addRelationToCheck(traceTypes, report, true);
      return report;
   }

   private AbstractArtifactRelationReport getTraceUnitToReqReport(CodeUnitData codeUnit, TestUnitData testUnit, List<TraceTypeEnum> traceTypes) {
      ArtifactToRelatedArtifact report = new ArtifactToRelatedArtifact();
      addArtifacts(codeUnit, testUnit, traceTypes, report);
      addRelationToCheck(traceTypes, report, true);
      return report;
   }

   private enum OutputType {
      ResultsEditor, Excel, Both;

      public static OutputType asType(boolean isExcel, boolean isEditor) {
         if (isExcel && isEditor) {
            return OutputType.Both;
         } else if (isExcel) {
            return OutputType.Excel;
         } else if (isEditor) {
            return OutputType.ResultsEditor;
         }
         return null;
      }
   }

   private enum TraceTypeEnum {
      Code_Unit_Trace(CoreRelationEnumeration.CodeRequirement_Requirement, CoreRelationEnumeration.CodeRequirement_CodeUnit),
      Verified_By_Test_Unit_Trace(CoreRelationEnumeration.Verification__Requirement, CoreRelationEnumeration.Verification__Verifier),
      Used_By_Test_Unit_Trace(CoreRelationEnumeration.Uses__Requirement, CoreRelationEnumeration.Uses__TestUnit);

      private IRelationEnumeration toReq;
      private IRelationEnumeration toTraceUnit;

      TraceTypeEnum(IRelationEnumeration toReq, IRelationEnumeration toTraceUnit) {
         this.toReq = toReq;
         this.toTraceUnit = toTraceUnit;
      }

      public IRelationEnumeration getRelatedToRequirement() {
         return toReq;
      }

      public IRelationEnumeration getRelatedToTraceUnit() {
         return toTraceUnit;
      }

      public String asLabel() {
         return name().replaceAll("_", " ");
      }

      public static TraceTypeEnum fromLabel(String label) {
         label = label.replaceAll(" ", "_");
         for (TraceTypeEnum traceType : TraceTypeEnum.values()) {
            if (traceType.name().equalsIgnoreCase(label)) {
               return traceType;
            }
         }
         return null;
      }
   }

   private final class ExcelReport implements IReportDataCollector {
      private final String title;
      private final ISheetWriter sheetWriter;

      public ExcelReport(String title, ISheetWriter sheetWriter) {
         this.sheetWriter = sheetWriter;
         this.title = title;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.define.traceability.report.IReportDataCollector#addRow(java.lang.String[])
       */
      @Override
      public void addRow(String... data) {
         try {
            sheetWriter.writeRow(data);
         } catch (IOException ex) {
            OseeLog.log(DefinePlugin.class, Level.SEVERE, ex);
         }
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.define.traceability.report.IReportDataCollector#addTableHeader(java.lang.String[])
       */
      @Override
      public void addTableHeader(String... header) {
         try {
            sheetWriter.startSheet(title, header.length);
            sheetWriter.writeRow(header);
         } catch (IOException ex) {
            OseeLog.log(DefinePlugin.class, Level.SEVERE, ex);
         }
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.define.traceability.report.IReportDataCollector#endTable()
       */
      @Override
      public void endTable() {
         try {
            sheetWriter.endSheet();
         } catch (IOException ex) {
            OseeLog.log(DefinePlugin.class, Level.SEVERE, ex);
         }
      }
   }

   private final class ResultEditorReport implements IReportDataCollector {
      private final String title;
      private List<XViewerColumn> columns;
      private List<IResultsXViewerRow> rows;

      public ResultEditorReport(String title) {
         this.title = title;
         this.columns = null;
         this.rows = null;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.define.traceability.report.IReportDataCollector#addRow(java.lang.String[])
       */
      @Override
      public void addRow(String... data) {
         if (rows == null) {
            rows = new ArrayList<IResultsXViewerRow>();
         }
         rows.add(new ResultsXViewerRow(data));
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.define.traceability.report.IReportDataCollector#addTableHeader(java.lang.String[])
       */
      @Override
      public void addTableHeader(String... header) {
         this.columns = new ArrayList<XViewerColumn>();
         for (String name : header) {
            columns.add(new XViewerColumn(name, name, 80, SWT.LEFT, true, SortDataType.String, false, ""));
         }
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.define.traceability.report.IReportDataCollector#endTable()
       */
      @Override
      public void endTable() {
         resultsTabs.add(new ResultsEditorTableTab(title, columns, rows));
      }
   }
}
