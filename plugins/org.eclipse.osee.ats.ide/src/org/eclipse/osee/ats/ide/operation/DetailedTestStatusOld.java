/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.ide.operation;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.core.util.PercentCompleteTotalUtil;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.util.XVersionList;
import org.eclipse.osee.ats.ide.util.widgets.XAtsProgramComboWidget;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.define.ide.traceability.RequirementTraceabilityData;
import org.eclipse.osee.define.ide.traceability.ScriptTraceabilityOperation;
import org.eclipse.osee.define.ide.traceability.TraceUnitExtensionManager;
import org.eclipse.osee.define.ide.traceability.TraceUnitExtensionManager.TraceHandler;
import org.eclipse.osee.define.ide.traceability.TraceabilityProviderOperation;
import org.eclipse.osee.define.ide.traceability.report.RequirementStatus;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.HashCollectionSet;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.branch.ViewApplicabilityUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.XBranchSelectWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.ote.define.OteArtifactTypes;
import org.eclipse.osee.ote.define.artifacts.ArtifactTestRunOperator;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Ryan D. Brooks
 */
public class DetailedTestStatusOld extends AbstractBlam {
   private static final String REQUIREMENTS_BRANCH = "Requirements Branch";
   private static final String PROGRAM = "Program";
   private static final Pattern taskNamePattern = Pattern.compile("(?:\"([^\"]+)\")? for \"([^\"]+)\"");
   private final Matcher taskNameMatcher = taskNamePattern.matcher("");
   private CharBackedInputStream charBak;
   private ISheetWriter excelWriter;

   private final CompositeKeyHashMap<String, String, RequirementStatus> reqTaskMap = new CompositeKeyHashMap<>();
   private final StringBuilder sumFormula = new StringBuilder(500);
   private HashCollectionSet<Artifact, String> requirementToCodeUnitsMap;
   private final HashMap<String, String> testProcedureInfo = new HashMap<>();
   private final HashCollection<String, IAtsUser> legacyIdToImplementers = new HashCollection<>();
   private final HashMap<String, Artifact> testRunArtifacts = new HashMap<>();
   private final HashMap<String, String> scriptCategories = new HashMap<>();
   private final HashSet<IAtsUser> testPocs = new HashSet<>();
   private final HashSet<String> requirementPocs = new HashSet<>();
   private final ArrayList<String[]> statusLines = new ArrayList<>();
   private final ArrayList<RequirementStatus> statuses = new ArrayList<>(100);
   private Collection<IAtsVersion> versions;

   private XBranchSelectWidget requirementsBranchWidget;
   private XBranchSelectWidget testProcedureBranchWidget;
   private XVersionList versionsListViewer;

   private static final String TRACE_HANDLER_CHECKBOX =
      "<XWidget xwidgetType=\"XCheckBox\" displayName=\"%s\" labelAfter=\"true\" horizontalLabel=\"true\"/>";
   private Collection<String> availableTraceHandlers;
   private XCombo branchViewWidget;
   private XBranchSelectWidget branchWidget;

   private BranchId selectedBranch;
   private IAtsProgram selectedProgram;
   private static final int MAX_EXCEL_COLUMNS = 256;

   private enum Index {
      Category,
      TEST_POC,
      PARTITION,
      SUBSYSTEM,
      REQUIREMENT_NAME,
      QUALIFICATION_METHOD,
      REQUIREMENT_POC,
      SW_ENHANCEMENT,
      TEST_PROCEDURE,
      TEST_SCRIPT,
      RUN_DATE,
      TOTAL_TP,
      FAILED_TP,
      HOURS_REMAINING
   };

   public static SimpleDateFormat getDateFormatter() {
      return new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
   }

   private class BranchChangeListener implements ISelectionChangedListener {
      @Override
      public void selectionChanged(SelectionChangedEvent event) {
         IStructuredSelection versionArtifactSelection =
            (IStructuredSelection) event.getSelectionProvider().getSelection();
         Iterator<?> iter = versionArtifactSelection.iterator();
         if (iter.hasNext()) {
            IAtsVersion version = (IAtsVersion) iter.next();

            try {
               if (version != null) {
                  selectedBranch = version.getBaselineBranchIdInherited();
                  requirementsBranchWidget.setSelection(selectedBranch);
                  testProcedureBranchWidget.setSelection(selectedBranch);
               }
            } catch (OseeCoreException ex) {
               log(ex);
            }
         }
      }
   }
   private final BranchChangeListener branchSelectionListener = new BranchChangeListener();

   private class ProgramSelectionListener implements ISelectionChangedListener {
      @Override
      public void selectionChanged(SelectionChangedEvent event) {
         IStructuredSelection selection = (IStructuredSelection) event.getSelectionProvider().getSelection();

         Iterator<?> iter = selection.iterator();
         if (iter.hasNext()) {
            selectedProgram = (IAtsProgram) iter.next();
            selectedBranch = null;

            try {
               Collection<IAtsVersion> versionArtifacts =
                  AtsClientService.get().getProgramService().getTeamDefHoldingVersions(selectedProgram).getVersions();
               versionsListViewer.setInputAtsObjects(versionArtifacts);

               requirementsBranchWidget.setSelection(null);
               testProcedureBranchWidget.setSelection(null);

               versionsListViewer.addSelectionChangedListener(branchSelectionListener);
            } catch (OseeCoreException ex) {
               log(ex);
            }
         }
      }
   }

   private void init() throws IOException {
      charBak = new CharBackedInputStream();
      excelWriter = new ExcelXmlWriter(charBak.getWriter());
      reqTaskMap.clear();
      testRunArtifacts.clear();
      testProcedureInfo.clear();
      legacyIdToImplementers.clear();
   }

   private String getScriptName(String scriptPath) {
      return scriptPath.substring(scriptPath.lastIndexOf(File.separatorChar) + 1,
         scriptPath.length() - ".java".length());
   }

   private void loadTestRunArtifacts(BranchId scriptsBranch) {
      Collection<Artifact> testRuns =
         ArtifactQuery.getArtifactListFromType(OteArtifactTypes.TestRun, scriptsBranch, DeletionFlag.EXCLUDE_DELETED);

      for (Artifact testRun : testRuns) {
         String shortName = testRun.getName();
         shortName = shortName.substring(shortName.lastIndexOf('.') + 1);
         Artifact previousTestRun = testRunArtifacts.put(shortName, testRun);

         if (previousTestRun != null) {
            Date date = new ArtifactTestRunOperator(testRun).getEndDate();
            Date previousDate = new ArtifactTestRunOperator(previousTestRun).getEndDate();
            if (previousDate.after(date)) {
               testRunArtifacts.put(shortName, previousTestRun);
            }
         }
      }
   }

   @Override
   public void runOperation(VariableMap variableMap, final IProgressMonitor monitor) throws Exception {
      if (!blamReadyToExecute()) {
         monitor.setCanceled(true);
         return;
      }

      BranchId requirementsBranch = variableMap.getBranch(REQUIREMENTS_BRANCH);
      BranchId scriptsBranch = variableMap.getBranch("Test Results Branch");
      BranchId procedureBranch = variableMap.getBranch("Test Procedure Branch");

      Object view = variableMap.getValue(BRANCH_VIEW);
      setViewId(view);

      File scriptDir = new File(variableMap.getString("Script Root Directory"));
      versions = new ArrayList<>();
      for (IAtsVersion version : variableMap.getCollection(IAtsVersion.class, "Versions")) {
         versions.add(version);
      }
      init();

      loadTestRunArtifacts(scriptsBranch);

      Collection<TraceHandler> traceHandlers = new LinkedList<>();
      for (String handler : availableTraceHandlers) {
         if (variableMap.getBoolean(handler)) {
            TraceHandler traceHandler = TraceUnitExtensionManager.getInstance().getTraceHandlerByName(handler);
            traceHandlers.add(traceHandler);
         }
      }

      // Load Requirements Data
      TraceabilityProviderOperation provider =
         new ScriptTraceabilityOperation(scriptDir, requirementsBranch, false, traceHandlers, false, viewId, false);
      RequirementTraceabilityData traceabilityData = new RequirementTraceabilityData(procedureBranch, provider, viewId);

      IStatus status = traceabilityData.initialize(monitor);
      switch (status.getSeverity()) {
         case IStatus.OK:
            requirementToCodeUnitsMap = traceabilityData.getRequirementsToCodeUnits();

            loadReqTaskMap();

            writeStatusSheet(traceabilityData.getAllSwRequirements());

            writeTestScriptSheet(traceabilityData.getCodeUnits());

            excelWriter.endWorkbook();
            IFile iFile = OseeData.getIFile("Detailed_Test_Status_" + Lib.getDateTimeString() + ".xml");
            AIFile.writeToFile(iFile, charBak);
            Program.launch(iFile.getLocation().toOSString());
            break;
         case IStatus.CANCEL:
            monitor.setCanceled(true);
            break;
         default:
            throw new OseeCoreException(status.getMessage(), status.getException());
      }
   }

   private boolean blamReadyToExecute() {
      final List<String> items = new ArrayList<>();

      if (selectedProgram == null) {
         items.add("program");
      }
      if (selectedBranch == null) {
         items.add("branch");
      }

      boolean ready = items.isEmpty();
      if (!ready) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               MessageDialog.openInformation(Displays.getActiveShell(), "Problem",
                  String.format("Select a %s ...", Strings.buildStatment(items)));
            }
         });
      }
      return ready;
   }

   private void writeTestScriptSheet(Set<String> scripts) throws IOException {
      excelWriter.startSheet("Scripts", 8);
      excelWriter.writeRow("Category", CoreArtifactTypes.TestCase.getName(), "Run Date", "Total Test Points",
         "Failed Test Points", "Duration", "Aborted", "Last Author");

      for (String scriptPath : scripts) {
         String scriptName = getScriptName(scriptPath);
         Artifact testRunArtifact = testRunArtifacts.get(scriptName);
         String totalTestPoints = null;
         String failedTestPoints = null;
         String category = scriptCategories.get(scriptPath);
         String runDate = null;
         String duration = null;
         String aborated = null;
         String lastAuthor = null;
         if (testRunArtifact != null) {
            ArtifactTestRunOperator runOperator = new ArtifactTestRunOperator(testRunArtifact);
            try {
               runDate = getDateFormatter().format(runOperator.getEndDate());
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
            totalTestPoints = String.valueOf(runOperator.getTotalTestPoints());
            failedTestPoints = String.valueOf(runOperator.getTestPointsFailed());
            duration = runOperator.getRunDuration();
            aborated = runOperator.wasAborted() ? "aborted" : null;
            lastAuthor = runOperator.getLastAuthor();
         }

         excelWriter.writeRow(category, scriptName, runDate, totalTestPoints, failedTestPoints, duration, aborated,
            lastAuthor);
      }

      excelWriter.endSheet();
   }

   private void writeStatusSheet(Collection<Artifact> requirements) throws IOException {
      excelWriter.startSheet("SW Req Status", 256);
      excelWriter.writeRow(null, null, null, null, "Hours per UI per RPCR", "=4");
      excelWriter.writeRow(null, null, null, null, "Hours to integrate all scripts for a UI", "=11");
      excelWriter.writeRow(null, null, null, null, "Hours to develop a new script", "=20");
      excelWriter.writeRow();
      excelWriter.writeRow();
      excelWriter.writeRow("Category", "Test POCs", CoreAttributeTypes.Partition.getName(),
         CoreAttributeTypes.Subsystem.getName(), "Requirement Name", CoreAttributeTypes.QualificationMethod.getName(),
         "Requirement POCs", "SW Enhancement", CoreArtifactTypes.TestProcedure.getName(),
         CoreArtifactTypes.TestCase.getName(), "Run Date", "Total Test Points", "Failed Test Points", "Hours Remaining",
         "RPCR", "Hours", "Resolution by Partition");

      for (Artifact requirement : requirements) {
         writeRequirementStatusLines(requirement);
      }

      excelWriter.endSheet();
   }

   private void setScriptCategories(Artifact requirement, Collection<String> scripts) {
      try {
         String reqCategory = requirement.getSoleAttributeValue(CoreAttributeTypes.Category, "");

         for (String scriptPath : scripts) {
            String scriptCategory = scriptCategories.get(scriptPath);
            if (scriptCategory == null || scriptCategory.compareTo(reqCategory) > 0) {
               scriptCategories.put(scriptPath, reqCategory);
            }
         }
      } catch (Exception ex) {
         // really don't care because we handle unknown priorities later
      }
   }

   private void processRpcrStatuses(Artifact requirement, String[] statusLine) {
      int columnIndex = Index.HOURS_REMAINING.ordinal() + 1;

      sumFormula.append(",");
      for (String requirementName : getAliases(requirement)) {
         Collection<RequirementStatus> tempStatuses = reqTaskMap.getValues(requirementName);

         if (tempStatuses != null) {
            statuses.clear();
            statuses.addAll(tempStatuses);
            java.util.Collections.sort(statuses);
            for (int i = statuses.size() - 1; i >= 0; i--) {
               RequirementStatus status = statuses.get(i);
               statusLine[columnIndex++] = status.getLegacyId();
               statusLine[columnIndex++] = "=R1C6*(100-" + status.getRolledupPercentComplete() + ")/100";

               sumFormula.append("RC");
               sumFormula.append(columnIndex);
               sumFormula.append(",");

               statusLine[columnIndex++] = status.getPartitionStatuses();

               Collection<IAtsUser> implementers = legacyIdToImplementers.getValues(status.getLegacyId());
               if (implementers != null) {
                  for (IAtsUser implementer : implementers) {
                     requirementPocs.add(implementer.getName());
                  }
               }
               testPocs.addAll(status.getTestPocs());
            }
         }
      }

      sumFormula.setCharAt(sumFormula.length() - 1, ')');
   }

   private void processTestScriptsAndProcedures(Artifact requirement, String[] statusLine) {
      Collection<String> scripts = requirementToCodeUnitsMap.getValues(requirement);
      if (scripts == null) {
         if (requirement.isOfType(CoreArtifactTypes.IndirectSoftwareRequirementMsWord) || requirement.isOfType(
            CoreArtifactTypes.AbstractImplementationDetails)) {
            statusLine[Index.TEST_SCRIPT.ordinal()] = requirement.getArtifactTypeName();
            sumFormula.insert(0, "=sum(0");
            statusLine[Index.HOURS_REMAINING.ordinal()] = sumFormula.toString();
         } else {
            statusLine[Index.TEST_SCRIPT.ordinal()] = "No script found";
            statusLine[Index.HOURS_REMAINING.ordinal()] = "=R3C6";
         }
         statusLines.add(statusLine);
      } else {
         setScriptCategories(requirement, scripts);
         int testPointTotalForScripts = 0;
         int testPointFailsForScripts = 0;

         for (String script : scripts) {
            String scriptName = getScriptName(script);
            statusLine[Index.TEST_SCRIPT.ordinal()] = scriptName;
            Artifact testRunArtifact = testRunArtifacts.get(scriptName);
            if (testRunArtifact != null) {
               ArtifactTestRunOperator runOperator = new ArtifactTestRunOperator(testRunArtifact);

               try {
                  statusLine[Index.RUN_DATE.ordinal()] = getDateFormatter().format(runOperator.getEndDate());
               } catch (Exception ex) {
                  log(ex);
               }
               if (runOperator.wasAborted()) {
                  statusLine[Index.TOTAL_TP.ordinal()] = "Aborted";
                  statusLine[Index.FAILED_TP.ordinal()] = "Aborted";
               } else {
                  int individualTestPointsFailed = runOperator.getTestPointsFailed();
                  int individualTestPointTotal = runOperator.getTotalTestPoints();

                  statusLine[Index.TOTAL_TP.ordinal()] = String.valueOf(individualTestPointTotal);
                  statusLine[Index.FAILED_TP.ordinal()] = String.valueOf(individualTestPointsFailed);

                  testPointFailsForScripts += individualTestPointsFailed;
                  testPointTotalForScripts += individualTestPointTotal;
               }
            }
            statusLines.add(statusLine);
            String[] oldStatusLine = statusLine;
            statusLine = new String[100];
            initStatusLine(oldStatusLine, statusLine);
         }

         String failRatio = "1";
         if (testPointTotalForScripts != 0) {
            failRatio = testPointFailsForScripts + "/" + testPointTotalForScripts;
         }
         sumFormula.insert(0, "=sum(R2C6*" + failRatio);
         statusLines.get(0)[Index.HOURS_REMAINING.ordinal()] = sumFormula.toString();
      }

   }

   private void writeRequirementStatusLines(Artifact requirement) throws IOException {
      statusLines.clear();
      testPocs.clear();
      requirementPocs.clear();
      sumFormula.delete(0, 99999);
      String[] statusLine = new String[MAX_EXCEL_COLUMNS];

      processRpcrStatuses(requirement, statusLine);

      statusLine[Index.Category.ordinal()] = requirement.getSoleAttributeValue(CoreAttributeTypes.Category, "");
      if (requirement.isOfType(CoreArtifactTypes.IndirectSoftwareRequirementMsWord) || requirement.isOfType(
         CoreArtifactTypes.AbstractImplementationDetails)) {
         statusLine[Index.Category.ordinal()] = "I";
      }

      statusLine[Index.TEST_POC.ordinal()] = AtsObjects.toString("; ", testPocs);
      statusLine[Index.PARTITION.ordinal()] = requirement.getAttributesToString(CoreAttributeTypes.Partition);
      statusLine[Index.SUBSYSTEM.ordinal()] = requirement.getSoleAttributeValue(CoreAttributeTypes.Subsystem, "");
      statusLine[Index.REQUIREMENT_NAME.ordinal()] = requirement.getName();
      statusLine[Index.QUALIFICATION_METHOD.ordinal()] =
         requirement.getAttributesToStringSorted(CoreAttributeTypes.QualificationMethod);
      statusLine[Index.REQUIREMENT_POC.ordinal()] = Collections.toString(",", requirementPocs);

      Collection<RequirementStatus> reqStats = reqTaskMap.getValues(requirement.getName());
      statusLine[Index.SW_ENHANCEMENT.ordinal()] =
         reqStats.isEmpty() ? "" : reqStats.iterator().next().getSwEnhancement();

      processTestScriptsAndProcedures(requirement, statusLine);

      for (String[] line : statusLines) {
         excelWriter.writeRow((Object[]) line);
      }
   }

   private void initStatusLine(String[] oldStatusLine, String[] newStatusLine) {
      System.arraycopy(oldStatusLine, 0, newStatusLine, 0, Index.RUN_DATE.ordinal());
   }

   private void loadReqTaskMap() throws Exception {
      for (IAtsVersion version : versions) {
         for (IAtsTeamWorkflow workflow : AtsClientService.get().getVersionService().getTargetedForTeamWorkflows(
            version)) {
            loadTasksFromWorkflow((TeamWorkFlowArtifact) workflow.getStoreObject());
         }
      }
   }

   @Override
   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) {
      String widgetLabel = xWidget.getLabel();

      if (widgetLabel.equals("Versions")) {
         versionsListViewer = (XVersionList) xWidget;
      } else if (widgetLabel.equals(REQUIREMENTS_BRANCH)) {
         requirementsBranchWidget = (XBranchSelectWidget) xWidget;
      } else if (widgetLabel.equals("Test Procedure Branch")) {
         testProcedureBranchWidget = (XBranchSelectWidget) xWidget;
      }
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) {
      String widgetName = xWidget.getLabel();
      if (widgetName.equals(PROGRAM)) {
         XAtsProgramComboWidget programWidget = (XAtsProgramComboWidget) xWidget;
         programWidget.getComboViewer().addSelectionChangedListener(new ProgramSelectionListener());
      } else if (xWidget.getLabel().equals(REQUIREMENTS_BRANCH)) {
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

   private void loadTasksFromWorkflow(TeamWorkFlowArtifact workflow) {
      Collection<IAtsTask> tasks = AtsClientService.get().getTaskService().getTasks(workflow);
      String legacyId = workflow.getSoleAttributeValue(AtsAttributeTypes.LegacyPcrId, "");

      List<IAtsUser> implementers = workflow.getImplementers();
      legacyIdToImplementers.put(legacyId, implementers);

      for (IAtsTask task : tasks) {

         taskNameMatcher.reset(task.getName());
         if (taskNameMatcher.find()) {
            String requirementName = taskNameMatcher.group(2);
            RequirementStatus requirementStatus = reqTaskMap.get(requirementName, legacyId);
            if (requirementStatus == null) {
               requirementStatus = new RequirementStatus(requirementName, legacyId,
                  workflow.getSoleAttributeValueAsString(AtsAttributeTypes.SwEnhancement, ""));
               reqTaskMap.put(requirementName, legacyId, requirementStatus);
            }

            int percentComplete =
               PercentCompleteTotalUtil.getPercentCompleteTotal(task, AtsClientService.get().getServices());
            requirementStatus.addPartitionStatus(percentComplete, taskNameMatcher.group(1),
               task.getStateMgr().getCurrentStateName());
            requirementStatus.setTestPocs(task.getImplementers());
         } else {
            logf("odd task:  [%s]", task.getName());
         }
      }
   }

   /*
    * returns a collection of all the names the given artifact has ever had
    */
   private Collection<String> getAliases(Artifact artifact) {
      // TODO: this method should return history of names
      ArrayList<String> aliases = new ArrayList<>(1);
      aliases.add(artifact.getName());
      return aliases;
   }

   @Override
   public String getXWidgetsXml() {
      StringBuilder sb = new StringBuilder();
      sb.append("<xWidgets>");
      sb.append("<XWidget xwidgetType=\"XAtsProgramComboWidget\" horizontalLabel=\"true\" displayName=\"Program\" />");
      sb.append("<XWidget xwidgetType=\"XVersionList\" displayName=\"Versions\" multiSelect=\"true\" />");
      sb.append(
         "<XWidget xwidgetType=\"XText\" displayName=\"Script Root Directory\" defaultValue=\"C:/UserData/workspaceScripts\" />");

      availableTraceHandlers = new LinkedList<>();
      sb.append("<XWidget xwidgetType=\"XLabel\" displayName=\"Select appropriate script parser:\" />");
      Collection<String> traceHandlers = TraceUnitExtensionManager.getInstance().getAllTraceHandlerNames();
      for (String handler : traceHandlers) {
         sb.append(String.format(TRACE_HANDLER_CHECKBOX, handler));
         availableTraceHandlers.add(handler);
      }

      sb.append(
         "<XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Requirements Branch\" toolTip=\"Select a requirements branch.\" />");
      sb.append(
         "<XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Test Results Branch\" toolTip=\"Select a scripts results branch.\" />");
      sb.append(
         "<XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Test Procedure Branch\" toolTip=\"Select a test procedures branch.\" />");
      sb.append(BRANCH_VIEW_WIDGET);
      sb.append("</xWidgets>");
      return sb.toString();
   }

   @Override
   public String getDescriptionUsage() {
      return "Generates an excel spreadsheet with detailed test status for scripts and procedures";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("OTE");
   }

   @Override
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      return java.util.Collections.singleton(CoreUserGroups.Everyone);
   }

}
