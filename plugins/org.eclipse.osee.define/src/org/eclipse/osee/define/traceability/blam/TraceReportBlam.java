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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.define.internal.Activator;
import org.eclipse.osee.define.traceability.data.BaseTraceDataCache;
import org.eclipse.osee.define.traceability.data.CodeUnitData;
import org.eclipse.osee.define.traceability.data.RequirementData;
import org.eclipse.osee.define.traceability.data.TestUnitData;
import org.eclipse.osee.define.traceability.report.AbstractArtifactRelationReport;
import org.eclipse.osee.define.traceability.report.ArtifactToRelatedArtifact;
import org.eclipse.osee.define.traceability.report.ArtifactTraceCount;
import org.eclipse.osee.define.traceability.report.ArtifactsWithoutRelations;
import org.eclipse.osee.define.traceability.report.IReportDataCollector;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.plugin.core.util.IExceptionableRunnable;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.branch.ViewApplicabilityUtil;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorProvider;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.html.ResultsEditorHtmlTab;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.widgets.XBranchSelectWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Roberto E. Escobar
 */
@SuppressWarnings("deprecation")
public class TraceReportBlam extends AbstractBlam {
   private static final String BRANCH = "Branch";
   private final List<IResultsEditorTab> resultsTabs;
   private XCombo branchViewWidget;
   private XBranchSelectWidget branchWidget;

   public TraceReportBlam() {
      this.resultsTabs = new ArrayList<>();
   }

   @Override
   public String getName() {
      return "Trace Report";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Define.Trace");
   }

   @Override
   public String getDescriptionUsage() {
      return "Generates a trace report by scanning the selected branch for the selected trace unit types.";
   }

   private String getOperationsCheckBoxes(String value) {
      StringBuilder builder = new StringBuilder();
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"");
      builder.append(value);
      builder.append("\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
      return builder.toString();
   }

   @Override
   public String getXWidgetsXml() {
      StringBuilder builder = new StringBuilder();
      builder.append("<xWidgets>");
      //      builder.append("<XWidget xwidgetType=\"XFileSelectionDialog\" displayName=\"Select UI List File\" />");
      builder.append("<XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Requirements Branch\" />");
      builder.append(BRANCH_VIEW_WIDGET);
      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\"Select Trace Types:\"/>");
      for (TraceTypeEnum traceType : TraceTypeEnum.values()) {
         builder.append(getOperationsCheckBoxes(traceType.asLabel()));
      }
      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\"Select Report Output:\"/>");
      builder.append(
         "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Result Editor\" labelAfter=\"true\" horizontalLabel=\"true\" />");
      builder.append(
         "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Excel\" labelAfter=\"true\" horizontalLabel=\"true\" />");
      builder.append("</xWidgets>");
      return builder.toString();
   }

   private List<TraceTypeEnum> getCheckedTraceItems(VariableMap variableMap) {
      List<TraceTypeEnum> toReturn = new ArrayList<>();
      for (TraceTypeEnum traceType : TraceTypeEnum.values()) {
         if (variableMap.getBoolean(traceType.asLabel())) {
            toReturn.add(traceType);
         }
      }
      return toReturn;
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {

      //      String fileName = variableMap.getString("Select UI List File");
      BranchId branch = variableMap.getBranch("Requirements Branch");
      if (branch == null) {
         throw new OseeArgumentException("Branch cannot be null");
      }
      Object view = variableMap.getValue(BRANCH_VIEW);
      setViewId(view);

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
      Map<String, AbstractArtifactRelationReport> reports = new LinkedHashMap<>();
      List<BaseTraceDataCache> traceCache = new ArrayList<>();
      RequirementData reqData = new RequirementData(branch, viewId);
      CodeUnitData codeUnit = null;
      TestUnitData testUnit = null;
      try {
         int TOTAL_WORK = Integer.MAX_VALUE;
         int TASK_WORK = TOTAL_WORK / 5;
         monitor.beginTask("Generate Trace Report", TOTAL_WORK);

         SubProgressMonitor subMonitor = new SubProgressMonitor(monitor, TASK_WORK);
         reqData.initialize(subMonitor);
         traceCache.add(reqData);

         subMonitor = new SubProgressMonitor(monitor, TASK_WORK);
         if (TraceTypeEnum.Code_Unit_Trace.isIn(traceTypes)) {
            codeUnit = new CodeUnitData(branch);
            codeUnit.initialize(subMonitor);
            traceCache.add(codeUnit);
         } else {
            subMonitor.done();
         }

         subMonitor = new SubProgressMonitor(monitor, TASK_WORK);
         if (TraceTypeEnum.containsTestEnum(traceTypes)) {
            testUnit = new TestUnitData(branch);
            testUnit.initialize(subMonitor);
            traceCache.add(testUnit);
         } else {
            subMonitor.done();
         }

         if (!monitor.isCanceled()) {
            if (isExcelOutput(output)) {
               excelInputStream = new CharBackedInputStream();
               writer = new ExcelXmlWriter(excelInputStream.getWriter());
            }
            buildReport(reports, "Trace Unit <-> Requirement", output, writer,
               getTraceUnitToReqReport(codeUnit, testUnit, traceTypes));
            buildReport(reports, "Trace Unit with No Valid Traceability", output, writer,
               getDisconnectedTraceUnitReport(codeUnit, testUnit, traceTypes));
            buildReport(reports, "Requirement Trace Counts", output, writer, getTraceCountReport(reqData, traceTypes));

            subMonitor = new SubProgressMonitor(monitor, TASK_WORK);
            executeReports(subMonitor, reports);
         }
         subMonitor = new SubProgressMonitor(monitor, TASK_WORK);
         displayReports(subMonitor, writer, excelInputStream, output);
      } finally {
         try {
            for (AbstractArtifactRelationReport report : reports.values()) {
               report.clear();
            }
            for (BaseTraceDataCache cache : traceCache) {
               cache.reset();
            }
            reports.clear();
            traceCache.clear();

            resultsTabs.clear();
         } finally {
            monitor.done();
         }
      }
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener xModListener, boolean isEditable) {
      super.widgetCreated(xWidget, toolkit, art, dynamicXWidgetLayout, xModListener, isEditable);
      if (xWidget.getLabel().equals(BRANCH)) {
         branchWidget = (XBranchSelectWidget) xWidget;
         branchWidget.addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               if (branchViewWidget != null) {
                  branchViewWidget.setEditable(true);
                  BranchId branch = branchWidget.getSelection();
                  if (branch != null && branch.isValid()) {
                     branchViews = ViewApplicabilityUtil.getBranchViews(ViewApplicabilityUtil.getParentBranch(branch));
                     branchViewWidget.setDataStrings(branchViews.values());
                  }
               }
            }
         });
      } else if (xWidget.getLabel().equals(BRANCH_VIEW)) {
         branchViewWidget = (XCombo) xWidget;
         branchViewWidget.setEditable(false);
      }
   }

   private void executeReports(IProgressMonitor monitor, Map<String, AbstractArtifactRelationReport> reports) {
      try {
         monitor.beginTask("Create Reports", reports.size());
         for (String key : reports.keySet()) {
            monitor.subTask(String.format("Creating [%s]", key));
            if (monitor.isCanceled()) {
               break;
            }
            AbstractArtifactRelationReport report = reports.get(key);
            report.process(monitor);
            report.clear();
            monitor.worked(1);
         }
      } finally {
         monitor.done();
      }
   }

   private void displayReports(IProgressMonitor monitor, ISheetWriter excelWriter, CharBackedInputStream excelInputStream, OutputType outputType) throws IOException {
      try {
         boolean wasEmpty = resultsTabs == null || resultsTabs.isEmpty();
         monitor.beginTask("Open Reports", 2);
         boolean createReport = true;
         boolean excelValid = excelInputStream != null && excelInputStream.available() > 0;

         if (wasEmpty && !excelValid) {
            resultsTabs.add(new ResultsEditorHtmlTab(getName(), getName(), "Nothing Reported"));
            createReport = false;
         }

         openReport(resultsTabs);
         monitor.worked(1);

         if (createReport) {
            if (excelWriter != null && isExcelOutput(outputType)) {
               excelWriter.endWorkbook();
               openExcel(excelInputStream);
            }
         }
         monitor.worked(1);
      } finally {
         monitor.done();
      }
   }

   private boolean isExcelOutput(OutputType output) {
      return output == OutputType.Excel || output == OutputType.Both;
   }

   private boolean isEditorOutput(OutputType output) {
      return output == OutputType.ResultsEditor || output == OutputType.Both;
   }

   private void buildReport(Map<String, AbstractArtifactRelationReport> reports, String title, OutputType output, ISheetWriter writer, AbstractArtifactRelationReport report) {
      if (isExcelOutput(output)) {
         report.addReportDataCollector(new ExcelReport(title, writer));
      }
      if (isEditorOutput(output)) {
         report.addReportDataCollector(new ResultEditorReport(title));
      }
      reports.put(title, report);
   }

   private void openExcel(final InputStream inputStream) {
      IFile iFile = OseeData.getIFile("Trace_Report_" + Lib.getDateTimeString() + ".xml");
      AIFile.writeToFile(iFile, inputStream);
      if (inputStream != null) {
         try {
            inputStream.close();
         } catch (IOException ex) {
            // Do Nothing
         }
      }
      Program.launch(iFile.getLocation().toOSString());
   }

   private void openReport(final List<IResultsEditorTab> resultsTabs) {
      final List<IResultsEditorTab> results = new ArrayList<>(resultsTabs);
      if (!results.isEmpty()) {
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
                     return results;
                  }
               });
               return Status.OK_STATUS;
            }
         };
         Jobs.runInJob(getName(), runnable, Activator.class, Activator.PLUGIN_ID);
      }
   }

   private void addRelationToCheck(List<TraceTypeEnum> traceTypes, AbstractArtifactRelationReport report, boolean fromTraceUnit) {
      for (TraceTypeEnum traceTypeEnum : traceTypes) {
         RelationTypeSide relation =
            fromTraceUnit ? traceTypeEnum.getRelatedToRequirement() : traceTypeEnum.getRelatedToTraceUnit();
         report.addRelationToCheck(relation);
      }
   }

   private void addArtifacts(CodeUnitData codeUnit, TestUnitData testUnit, List<TraceTypeEnum> traceTypes, AbstractArtifactRelationReport report) {
      if (TraceTypeEnum.Code_Unit_Trace.isIn(traceTypes)) {
         report.setArtifacts(codeUnit.getAllCodeUnits());
      }
      if (TraceTypeEnum.Verified_By_Test_Unit_Trace.isIn(traceTypes)) {
         report.setArtifacts(testUnit.getTestCases());
      }
      if (TraceTypeEnum.Used_By_Test_Unit_Trace.isIn(traceTypes)) {
         report.setArtifacts(testUnit.getTestSupportItems());
      }
      if (TraceTypeEnum.Validation_By_TestProcedure.isIn(traceTypes)) {
         report.setArtifacts(testUnit.getTestProcedures());
      }
   }

   private AbstractArtifactRelationReport getTraceCountReport(RequirementData reqData, List<TraceTypeEnum> traceTypes) {
      ArtifactTraceCount report = new ArtifactTraceCount();
      report.setArtifacts(reqData.getDirectRequirements());
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
      ResultsEditor,
      Excel,
      Both;

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
      Code_Unit_Trace(CoreRelationTypes.CodeRequirement_Requirement, CoreRelationTypes.CodeRequirement_CodeUnit, false),
      Verified_By_Test_Unit_Trace(CoreRelationTypes.Verification__Requirement, CoreRelationTypes.Verification__Verifier, true),
      Used_By_Test_Unit_Trace(CoreRelationTypes.Uses__Requirement, CoreRelationTypes.Uses__TestUnit, true),
      Validation_By_TestProcedure(CoreRelationTypes.Validation__Requirement, CoreRelationTypes.Validation__Validator, true);

      private RelationTypeSide toReq;
      private RelationTypeSide toTraceUnit;
      private boolean isTestType;

      TraceTypeEnum(RelationTypeSide toReq, RelationTypeSide toTraceUnit, boolean isTestType) {
         this.toReq = toReq;
         this.toTraceUnit = toTraceUnit;
         this.isTestType = isTestType;
      }

      public RelationTypeSide getRelatedToRequirement() {
         return toReq;
      }

      public RelationTypeSide getRelatedToTraceUnit() {
         return toTraceUnit;
      }

      public boolean isTestType() {
         return isTestType;
      }

      public String asLabel() {
         return name().replaceAll("_", " ");
      }

      public boolean isIn(Collection<TraceTypeEnum> traceTypes) {
         return traceTypes.contains(this);
      }

      public static boolean containsTestEnum(List<TraceTypeEnum> traceTypes) {
         for (TraceTypeEnum type : TraceTypeEnum.values()) {
            if (type.isTestType() && type.isIn(traceTypes)) {
               return true;
            }
         }
         return false;
      }
   }

   private static final class ExcelReport implements IReportDataCollector {
      private final String title;
      private final ISheetWriter sheetWriter;

      public ExcelReport(String title, ISheetWriter sheetWriter) {
         this.sheetWriter = sheetWriter;
         this.title = title;
      }

      @Override
      public void addRow(Object data, String... row) {
         try {
            sheetWriter.writeRow((Object[]) row);
         } catch (IOException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }

      @Override
      public void addTableHeader(String... header) {
         try {
            sheetWriter.startSheet(title, header.length);
            sheetWriter.writeRow((Object[]) header);
         } catch (IOException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }

      @Override
      public void endTable() {
         try {
            sheetWriter.endSheet();
         } catch (IOException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
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

      @Override
      public void addRow(Object data, String... row) {
         if (rows == null) {
            rows = new ArrayList<>();
         }
         rows.add(new ResultsXViewerRow(row, data));
      }

      @Override
      public void addTableHeader(String... header) {
         this.columns = new ArrayList<>();
         for (String name : header) {
            columns.add(new XViewerColumn(name, name, 80, XViewerAlign.Left, true, SortDataType.String, false, ""));
         }
      }

      @Override
      public void endTable() {
         resultsTabs.add(new ResultsEditorTableTab(title, columns, rows));
      }
   }
}
