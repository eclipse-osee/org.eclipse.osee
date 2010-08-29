/*******************************************************************************
 * Copyright (c) 2004, 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.blam.operation;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.swt.program.Program;

/**
 * Test: @link: TestPlanComplianceReportTest
 * 
 * @author: Karol M. Wilk
 */
public final class TestPlanComplianceReport extends AbstractBlam {
   private static final String BLANK_SPACE = " ";
   private static int initCase = -1;
   private int previousArtifactId = initCase;
   private String[] testPlanStorageArray = null;
   private Map<Integer, StringBuilder> testResultStorage = null;
   private final String[] columnHeaders = {
      "Test Plan & Paragraph",
      "Perf Spec Requirement(s)",
      "Test Procedure",
      "Test Status",
      "Test Result File",
      "Errors"};
   private Collection<Artifact> inputArtifacts;
   private Collection<Artifact> testPlans;

   private boolean performFileWrite;

   private CharBackedInputStream charBackedStream = null;
   private Writer defaultWriter = null;
   private ExcelXmlWriter excelWriter = null;
   private final boolean markErrorOnLastColumn = false;

   private List<Integer> testProcedureOrderList = null;
   private Map<Integer, String[]> testProcedureStorage = null;

   //Used for delayed writing of simple rows simple row could be a folder.
   private List<String[]> simpleDataStorageList = null;

   // Stores ids of procedures that contains errors. Used for marking lines that need to be decorated with error styling.
   private final Set<Integer> setOfTestProcedureErrorIds = new HashSet<Integer>();

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      charBackedStream = new CharBackedInputStream();
      runOperation(variableMap, monitor, charBackedStream.getWriter(), true);
   }

   /**
    * @formatter:off
    * Sets internal data structure for storage to argument writer, toggles performFileWrite
    * to actually write the file or not. Afterwards calls runOperation(...).
    *
    * Usually used for unit-testing.
    */
   //@formatter:on
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor, Writer writer, boolean performFileWrite) throws Exception {
      defaultWriter = writer;
      this.performFileWrite = performFileWrite;

      init(variableMap);

      for (Artifact input : inputArtifacts) {
         processArtifacts(input);
      }

      report();
   }

   private void processArtifacts(Artifact node) throws OseeCoreException, IOException {
      Collection<Artifact> children = node.getChildren();

      if (isTestPlan(node)) {
         processTestPlan(node);
      } else {
         reportLine(node, "N/A (" + node.getArtifactTypeName() + ")", BLANK_SPACE, BLANK_SPACE);
      }
      for (Artifact child : children) {
         processArtifacts(child);
      }
   }

   private boolean isTestPlan(Artifact src) {
      return src.getArtifactType().inheritsFrom(CoreArtifactTypes.TestPlanElement);
   }

   private void processTestPlan(Artifact testPlan) throws OseeCoreException, IOException {
      Collection<Artifact> testProcedures = testPlan.getRelatedArtifacts(CoreRelationTypes.Executes__Test_Procedure);

      if (testProcedures.isEmpty()) {
         reportLine(testPlan, BLANK_SPACE, BLANK_SPACE, BLANK_SPACE);
      } else {
         for (Artifact testProc : testProcedures) {
            processTestProcedure(testPlan, testProc);
         }
      }
   }

   private void processTestProcedure(Artifact testPlan, Artifact testProc) throws IOException, OseeCoreException {
      String status = testProc.getSoleAttributeValue(CoreAttributeTypes.TestProcedureStatus, BLANK_SPACE);
      Collection<Artifact> testResults = testProc.getRelatedArtifacts(CoreRelationTypes.Test_Unit_Result__Test_Result);

      TestStatusEnum enumStatus = TestStatusEnum.fromString(status);
      if (testResults.isEmpty()) {
         switch (enumStatus) {
            case COMPLETED_PASSED_CODE:
            case COMPLETED_WITH_ISSUES_CODE:
            case COMPLETED_WITH_ISSUES_RESOLVED_CODE:
               //markErrorOnLastColumn = true;
               setOfTestProcedureErrorIds.add(testProc.getArtId());
               reportLine(testPlan, testProc, testProc.getName(), status, "No test result files found...");
               break;
            default:
               reportLine(testPlan, testProc.getName(), status, BLANK_SPACE);
               break;
         }
      } else {
         for (Artifact testResult : testResults) {
            String extension = testResult.getSoleAttributeValueAsString(CoreAttributeTypes.Extension, "");

            if (TestStatusEnum.NOT_PERFORMED_CODE == enumStatus) {
               setOfTestProcedureErrorIds.add(testProc.getArtId());
               // markErrorOnLastColumn = true;
            }

            reportLine(testPlan, testProc, testProc.getName(), status,
               testResult.getName() + (extension.equals("") ? extension : "." + extension.toLowerCase()));
         }
      }
   }

   private String getRequirementsCellOutput(Artifact art) throws OseeCoreException {
      if (art.getArtifactType().inheritsFrom(CoreArtifactTypes.TestPlanElement)) {
         return getRequirementsCellOutputForTestPlan(art);
      }
      return BLANK_SPACE;
   }

   private String getRequirementsCellOutputForTestPlan(Artifact testPlan) throws OseeCoreException {
      String ret = getRequirementsAsString(testPlan);
      if (ret.isEmpty()) {
         ret = BLANK_SPACE;
      }
      return ret;
   }

   private String getRequirementsAsString(Artifact testPlan) throws OseeCoreException {
      Collection<Artifact> requirementArtifacts =
         testPlan.getRelatedArtifacts(CoreRelationTypes.Verification_Plan__Requirement);
      Collection<String> requirementNames = new ArrayList<String>();
      for (Artifact req : requirementArtifacts) {
         String paragraphNumber = req.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "");
         requirementNames.add(paragraphNumber + BLANK_SPACE + req.getName());
      }

      return StringUtils.join(requirementNames, "\n");
   }

   private void reportLine(Artifact testPlanArt, String testProcedureName, String testStatus, String testResult) throws OseeCoreException, IOException {
      reportLine(testPlanArt, null, testProcedureName, testStatus, testResult);
   }

   private void reportLine(Artifact testPlanArt, Artifact testProcedure, String testProcedureName, String testStatus, String testResult) throws OseeCoreException, IOException {
      if (testProcedure != null) {
         if (previousArtifactId != testPlanArt.getArtId()) {
            writeLastData();
            previousArtifactId = testPlanArt.getArtId();
            newUpDataStructures();
            storeNewTestPlan(testPlanArt);
            storeNewTestProcedure(testProcedure, testStatus, testResult);
         } else {
            appendTestProcedureToExistingTestPlan(testProcedure, testStatus, testResult);
         }
      } else {
         addSimpleRow(testPlanArt, testProcedureName, testStatus, testResult);
      }
   }

   private void addSimpleRow(Artifact testPlanArt, String testProcedureName, String testStatus, String testResult) throws OseeCoreException {
      if (simpleDataStorageList == null) {
         simpleDataStorageList = new ArrayList<String[]>();
      }
      simpleDataStorageList.add(new String[] {
         testPlanArt.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "") + BLANK_SPACE + testPlanArt.getName(),
         getRequirementsCellOutput(testPlanArt),
         testProcedureName,
         testStatus,
         testResult});
   }

   private void appendTestProcedureToExistingTestPlan(Artifact testProcedure, String testStatus, String testResult) {
      if (testProcedureOrderList.get(testProcedureOrderList.size() - 1) == testProcedure.getArtId()) {
         testResultStorage.get(testProcedure.getArtId()).append("\n" + testResult);
      } else {
         storeNewTestProcedure(testProcedure, testStatus, testResult);
      }
   }

   private void newUpDataStructures() {
      testProcedureOrderList = new ArrayList<Integer>(); //test plan id to string[]
      testProcedureStorage = new HashMap<Integer, String[]>(); //test proc id to string[]
      testResultStorage = new HashMap<Integer, StringBuilder>(); //test proc id to StringBuilder of testResults...
   }

   private void storeNewTestProcedure(Artifact testProcedure, String testStatus, String testResult) {
      int testProcedureId = testProcedure.getArtId();
      testProcedureOrderList.add(testProcedureId);
      testProcedureStorage.put(testProcedureId, new String[] {
         BLANK_SPACE,
         BLANK_SPACE,
         testProcedure.getName(),
         testStatus,
         null});
      testResultStorage.put(testProcedureId, new StringBuilder(testResult));
   }

   private void storeNewTestPlan(Artifact artifact) throws OseeCoreException {
      if (markErrorOnLastColumn) {
         testPlanStorageArray =
            new String[] {
               artifact.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "") + BLANK_SPACE + artifact.getName(),
               getRequirementsCellOutput(artifact),
               null,
               null,
               null,
               "Error"};
      } else {
         testPlanStorageArray =
            new String[] {
               artifact.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "") + BLANK_SPACE + artifact.getName(),
               getRequirementsCellOutput(artifact),
               null,
               null,
               null};
      }
   }

   private void writeLastData() throws IOException {
      if (testPlanStorageArray != null) {
         writeAdvancedRows();
         writeSimpleRows();
      } else {
         writeSimpleRows();
         writeAdvancedRows();
      }
   }

   private void writeAdvancedRows() throws IOException {
      if (testPlanStorageArray != null) {
         for (int testIndex = 0; testIndex < testProcedureOrderList.size(); testIndex++) {
            int testProcId = testProcedureOrderList.get(testIndex);
            String[] testProcedureArr = testProcedureStorage.remove(testProcedureOrderList.get(testIndex));
            if (testIndex == 0) {
               testProcedureArr[0] = testPlanStorageArray[0];
               testProcedureArr[1] = testPlanStorageArray[1];
            }
            //write to results column, skip the error column:
            testProcedureArr[testProcedureArr.length - 1] = testResultStorage.remove(testProcId).toString();

            if (setOfTestProcedureErrorIds.remove(testProcId)) {
               excelWriter.setCellStyle(ExcelXmlWriter.STYLE.ERROR, 3);
               excelWriter.setCellStyle(ExcelXmlWriter.STYLE.BOLD, 4);
            }

            excelWriter.writeRow(testProcedureArr);
            testPlanStorageArray = null;
         }
         testProcedureOrderList.clear();
      }
   }

   private void writeSimpleRows() throws IOException {
      if (simpleDataStorageList != null) {
         for (String[] rowToWrite : simpleDataStorageList) {
            excelWriter.writeRow(rowToWrite);
         }
         simpleDataStorageList.clear();
      }
   }

   private void report() throws OseeCoreException, IOException {
      writeLastData();
      previousArtifactId = initCase;
      setOfTestProcedureErrorIds.clear();
      excelWriter.endSheet();
      excelWriter.endWorkbook();
      if (performFileWrite) {
         IFile iFile = OseeData.getIFile("TestPlanComplianceReport_" + Lib.getDateTimeString() + ".xml");
         AIFile.writeToFile(iFile, charBackedStream);
         Program.launch(iFile.getLocation().toOSString());
      }
   }

   private void init(VariableMap variableMap) throws OseeCoreException, IOException {
      inputArtifacts = variableMap.getArtifacts("artifacts");
      initReport();
      load();
   }

   private void initReport() throws IOException {
      excelWriter = new ExcelXmlWriter(defaultWriter);
      excelWriter.startSheet(getName(), 200);
      excelWriter.writeRow(columnHeaders);
   }

   private void load() throws OseeCoreException {
      testPlans = new ArrayList<Artifact>();
      for (Artifact input : inputArtifacts) {
         testPlans.addAll(input.getDescendants());
      }
      RelationManager.getRelatedArtifacts(testPlans, 1, CoreRelationTypes.Verification_Plan__Requirement);
      Collection<Artifact> temp =
         RelationManager.getRelatedArtifacts(testPlans, 1, CoreRelationTypes.Executes__Test_Procedure);
      RelationManager.getRelatedArtifacts(temp, 1, CoreRelationTypes.Test_Unit_Result__Test_Result);
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XListDropViewer\" displayName=\"artifacts\" /></xWidgets>";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Define");
   }

   @Override
   public String getName() {
      return "Test Plan Compliance Report";
   }
}
