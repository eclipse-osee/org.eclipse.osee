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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
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
import org.eclipse.osee.define.ide.traceability.report.RequirementStatus;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
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
import org.eclipse.osee.framework.ui.skynet.widgets.XBranchSelectWidget;
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
public class DetailedTestStatusBlam extends AbstractBlam {
   private static final Pattern taskNamePattern = Pattern.compile("(?:\"([^\"]+)\")? for \"([^\"]+)\"");
   private final Matcher taskNameMatcher = taskNamePattern.matcher("");
   private CharBackedInputStream charBak;
   private ISheetWriter excelWriter;

   private CompositeKeyHashMap<String, String, RequirementStatus> reqTaskMap;
   private StringBuilder sumFormula;
   private HashCollection<String, IAtsUser> legacyIdToImplementers;
   private HashMap<String, Artifact> testRunArtifacts;
   private HashMap<String, String> scriptCategories;
   private HashSet<IAtsUser> testPocs;
   private HashSet<String> requirementPocs;
   private ArrayList<String[]> statusLines;
   private ArrayList<RequirementStatus> statuses;
   private Collection<IAtsVersion> versions;
   private HashCollectionSet<String, Artifact> requirementNameToTestProcedures;

   private XBranchSelectWidget reportBranchWidget;
   private XVersionList versionsListViewer;

   private BranchId selectedBranch;
   private IAtsProgram selectedProgram;

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
                  selectedBranch = AtsClientService.get().getVersionService().getBaselineBranchIdInherited(version);
                  reportBranchWidget.setSelection(selectedBranch);
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
               IAtsTeamDefinition teamDefHoldingVersions =
                  AtsClientService.get().getTeamDefinitionService().getTeamDefHoldingVersions(selectedProgram);
               if (teamDefHoldingVersions == null) {
                  versionsListViewer.setInputAtsObjects(new ArrayList<>());
                  reportBranchWidget.setSelection(null);
               } else {
                  Collection<IAtsVersion> versionArtifacts =
                     AtsClientService.get().getVersionService().getVersions(teamDefHoldingVersions);
                  versionsListViewer.setInputAtsObjects(versionArtifacts);
                  reportBranchWidget.setSelection(null);
                  versionsListViewer.addSelectionChangedListener(branchSelectionListener);
               }
            } catch (OseeCoreException ex) {
               log(ex);
            }
         }
      }
   }

   private String getScriptName(String scriptPath) {
      String scriptName = scriptPath;
      int endOfPackageName = scriptName.lastIndexOf(".");
      if (endOfPackageName != -1) {
         scriptName = scriptName.substring(endOfPackageName + 1) + ".java";
      }
      return scriptName.substring(scriptName.lastIndexOf(File.separatorChar) + 1,
         scriptName.length() - ".java".length());
   }

   private void loadTestRunArtifacts(BranchId scriptsBranch, IProgressMonitor monitor) {
      monitor.subTask("Loading Test Run Artifacts");
      Collection<Artifact> testRuns =
         ArtifactQuery.getArtifactListFromType(OteArtifactTypes.TestRun, scriptsBranch, DeletionFlag.EXCLUDE_DELETED);

      double increment = 100.0 / testRuns.size();
      double progress = 0;

      for (Artifact testRun : testRuns) {
         progress += increment;
         monitor.worked((int) Math.min(1.0, progress));
         if (progress > 1.0) {
            progress = 0;
         }
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

      BranchId reportBranch = variableMap.getBranch("Requirements Branch");
      BranchId resultsBranch = variableMap.getBranch("Test Results Branch");
      versions = variableMap.getCollection(IAtsVersion.class, "Versions");

      reqTaskMap = new CompositeKeyHashMap<>();
      legacyIdToImplementers = new HashCollection<>();
      testRunArtifacts = new HashMap<>();
      scriptCategories = new HashMap<>();
      testPocs = new HashSet<>();
      requirementPocs = new HashSet<>();
      statusLines = new ArrayList<>();
      statuses = new ArrayList<>(100);
      charBak = new CharBackedInputStream();
      excelWriter = new ExcelXmlWriter(charBak.getWriter());
      sumFormula = new StringBuilder();

      //100
      monitor.beginTask("Detailed Test Status", 500);
      loadTestRunArtifacts(resultsBranch, monitor);

      //100
      requirementNameToTestProcedures = getTestProcedureTraceability(reportBranch, monitor);

      //100
      loadReqTaskMap(monitor);

      List<Artifact> allSwReqs = ArtifactQuery.getArtifactListFromTypeWithInheritence(
         CoreArtifactTypes.AbstractSoftwareRequirement, reportBranch, DeletionFlag.EXCLUDE_DELETED);
      allSwReqs.addAll(ArtifactQuery.getArtifactListFromTypeWithInheritence(
         CoreArtifactTypes.AbstractImplementationDetails, reportBranch, DeletionFlag.EXCLUDE_DELETED));

      //100
      writeStatusSheet(allSwReqs, monitor);

      reqTaskMap = null;
      legacyIdToImplementers = null;
      versions = null;
      requirementNameToTestProcedures = null;
      testPocs = null;
      requirementPocs = null;
      statusLines = null;
      statuses = null;
      versions = null;

      List<Artifact> testCases = ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.TestCase, reportBranch);

      //100
      writeTestScriptSheet(testCases, monitor);

      testRunArtifacts = null;
      scriptCategories = null;

      excelWriter.endWorkbook();
      IFile iFile = OseeData.getIFile("Detailed_Test_Status_" + Lib.getDateTimeString() + ".xml");
      AIFile.writeToFile(iFile, charBak);
      Program.launch(iFile.getLocation().toOSString());
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

   private void writeTestScriptSheet(List<Artifact> testCases, IProgressMonitor monitor) throws IOException {
      monitor.subTask("Writing test script sheet");
      excelWriter.startSheet("Scripts", 8);
      excelWriter.writeRow("Category", CoreArtifactTypes.TestCase.getName(), "Run Date", "Total Test Points",
         "Failed Test Points", "Duration", "Aborted", "Last Author");

      double increment = 100.0 / testCases.size();
      double progress = 0;

      for (Artifact testCase : testCases) {
         progress += increment;
         monitor.worked((int) Math.min(1.0, progress));
         if (progress > 1.0) {
            progress = 0;
         }
         String scriptPath = testCase.getName();
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
               runDate = DetailedTestStatusOld.getDateFormatter().format(runOperator.getEndDate());
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

   private void writeStatusSheet(Collection<Artifact> requirements, IProgressMonitor monitor) throws IOException {
      monitor.subTask("Writing status sheet");
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

      double increment = 100.0 / requirements.size();
      double progress = 0;

      for (Artifact requirement : requirements) {
         progress += increment;
         monitor.worked((int) Math.min(1.0, progress));
         if (progress > 1.0) {
            progress = 0;
         }
         writeRequirementStatusLines(requirement);
      }

      excelWriter.endSheet();
   }

   private void setScriptCategories(Artifact requirement, Collection<Artifact> scripts) {
      try {
         String reqCategory = requirement.getSoleAttributeValue(CoreAttributeTypes.Category, "");

         for (Artifact scriptPath : scripts) {
            String scriptCategory = scriptCategories.get(scriptPath.getName());
            if (scriptCategory == null || scriptCategory.compareTo(reqCategory) > 0) {
               scriptCategories.put(scriptPath.getName(), reqCategory);
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
      Collection<Artifact> scripts = requirement.getRelatedArtifacts(CoreRelationTypes.Verification_Verifier);
      if (scripts.isEmpty()) {
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

         for (Artifact script : scripts) {
            String scriptName = getScriptName(script.getName());
            statusLine[Index.TEST_SCRIPT.ordinal()] = scriptName;
            Artifact testRunArtifact = testRunArtifacts.get(scriptName);
            if (testRunArtifact != null) {
               ArtifactTestRunOperator runOperator = new ArtifactTestRunOperator(testRunArtifact);

               try {
                  statusLine[Index.RUN_DATE.ordinal()] =
                     DetailedTestStatusOld.getDateFormatter().format(runOperator.getEndDate());
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

      addTestProcedureNames(requirement.getName());
   }

   private void writeRequirementStatusLines(Artifact requirement) throws IOException {
      statusLines.clear();
      testPocs.clear();
      requirementPocs.clear();
      sumFormula.delete(0, 99999);
      String[] statusLine = new String[100];

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

      for (Object[] line : statusLines) {
         excelWriter.writeRow(line);
      }
   }

   private HashCollectionSet<String, Artifact> getTestProcedureTraceability(BranchId testProcedureBranch, IProgressMonitor monitor) {
      monitor.subTask("Gathering test procedures");
      HashCollectionSet<String, Artifact> requirementNameToTestProcedures = new HashCollectionSet<>(HashSet::new);
      // Map Software Requirements from TestProcedure IOseeBranch to Requirements IOseeBranch
      List<Artifact> tpReqs =
         ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.SoftwareRequirementMsWord, testProcedureBranch);
      double increment = 100.0 / tpReqs.size();
      double progress = 0;

      for (Artifact tpRequirement : tpReqs) {
         progress += increment;
         monitor.worked((int) Math.min(1.0, progress));
         if (progress > 1.0) {
            progress = 0;
         }
         Set<Artifact> foundProcedures =
            new HashSet<>(tpRequirement.getRelatedArtifacts(CoreRelationTypes.Validation_Validator));
         Set<Artifact> toAdd = new HashSet<>();
         toAdd = foundProcedures;
         requirementNameToTestProcedures.put(tpRequirement.getName(), toAdd);
      }

      return requirementNameToTestProcedures;
   }

   private void addTestProcedureNames(String requirementName) {
      Collection<Artifact> testProcedures = requirementNameToTestProcedures.getValues(requirementName);
      if (testProcedures != null) {
         int index = 0;
         String[] firstStatusLine = statusLines.get(index);
         String lastTestProcedure = null;
         for (Artifact testProcedure : testProcedures) {
            if (index < statusLines.size()) {
               statusLines.get(index++)[Index.TEST_PROCEDURE.ordinal()] = testProcedure.getName();
               lastTestProcedure = testProcedure.getName();
            } else {
               String[] statusLine = new String[Index.RUN_DATE.ordinal()];
               initStatusLine(firstStatusLine, statusLine);
               statusLine[Index.TEST_PROCEDURE.ordinal()] = testProcedure.getName();
               statusLines.add(statusLine);
            }
         }
         while (index < statusLines.size()) {
            statusLines.get(index++)[Index.TEST_PROCEDURE.ordinal()] = lastTestProcedure;
         }
      }
   }

   private void initStatusLine(String[] oldStatusLine, String[] newStatusLine) {
      System.arraycopy(oldStatusLine, 0, newStatusLine, 0, Index.RUN_DATE.ordinal());
   }

   private void loadReqTaskMap(IProgressMonitor monitor) throws Exception {
      monitor.subTask("Loading tasks");

      for (IAtsVersion version : versions) {
         Collection<IAtsTeamWorkflow> targetedForTeamArtifacts =
            AtsClientService.get().getVersionService().getTargetedForTeamWorkflows(version);
         double increment = 100.0 / targetedForTeamArtifacts.size();
         double progress = 0;
         for (IAtsTeamWorkflow workflow : targetedForTeamArtifacts) {
            progress += increment;
            monitor.worked((int) Math.min(1.0, progress));
            if (progress > 1.0) {
               progress = 0;
            }
            loadTasksFromWorkflow((TeamWorkFlowArtifact) workflow.getStoreObject());
         }
      }
   }

   @Override
   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) {
      String widgetLabel = xWidget.getLabel();

      if (widgetLabel.equals("Versions")) {
         versionsListViewer = (XVersionList) xWidget;
      } else if (widgetLabel.equals("Requirements Branch")) {
         reportBranchWidget = (XBranchSelectWidget) xWidget;
      }
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) {
      String widgetName = xWidget.getLabel();
      if (widgetName.equals("Program")) {
         XAtsProgramComboWidget programWidget = (XAtsProgramComboWidget) xWidget;
         programWidget.getComboViewer().addSelectionChangedListener(new ProgramSelectionListener());
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
      sb.append(
         "<XWidget xwidgetType=\"XAtsProgramActiveComboWidget\" horizontalLabel=\"true\" displayName=\"Program\" />");
      sb.append("<XWidget xwidgetType=\"XVersionList\" displayName=\"Versions\" multiSelect=\"true\" />");
      sb.append(
         "<XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Requirements Branch\" toolTip=\"Select a requirements branch.\" />");
      sb.append(
         "<XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Test Results Branch\" toolTip=\"Select a scripts results branch.\" />");
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
