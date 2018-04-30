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
package org.eclipse.osee.define.ide.blam.operation;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelColumn;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.swt.program.Program;

/**
 * Test: @link: TestPlanComplianceReportTest
 *
 * @author: Karol M. Wilk
 */
public final class TestPlanComplianceReport extends AbstractBlam {
   public static final String TEST_PLANS = "Test Plans";
   public static final String MAX_ENTRIES_PER_CELL = "Max Entries Per Cell";
   public static final String FONT_SIZE = "Font Size";
   private static final int DEFAULT_MAX = 25;
   private static final int DEFAULT_FONT_SIZE = 11;
   private static final String BLANK_SPACE = " ";

   private Collection<Artifact> inputArtifacts;
   private Collection<Artifact> testPlans;

   private boolean performFileWrite;

   private int maxRowsPerCell = DEFAULT_MAX;
   private int fontSize = DEFAULT_FONT_SIZE;
   private CharBackedInputStream charBackedStream = null;
   private Writer defaultWriter = null;
   private ExcelXmlWriter excelWriter = null;

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

   private void processArtifacts(Artifact node) throws IOException {
      Collection<Artifact> children = node.getChildren();

      if (isTestPlan(node)) {
         String testPlan = getArtifactNameAndParagraph(node);
         List<String> perfSpecs = getRequirementsCellOutput(node, CoreRelationTypes.Verification_Plan__Requirement);
         List<String> pids = getRequirementsCellOutput(node, CoreRelationTypes.Validation__Requirement);
         List<Artifact> testProcedures = node.getRelatedArtifacts(CoreRelationTypes.Executes__Test_Procedure);

         List<String> testProcedureNames = Lists.newLinkedList(Artifacts.getNames(testProcedures));
         TestStatusAndResults testStatusAndResults = calculateTestStatusAndResults(testProcedures);

         writeRow(testPlan, perfSpecs, pids, testProcedureNames, testStatusAndResults.testStatus,
            testStatusAndResults.testResults, testStatusAndResults.errors);
      } else {
         excelWriter.writeRow(node, BLANK_SPACE, BLANK_SPACE, "N/A (" + node.getArtifactTypeName() + ")", BLANK_SPACE,
            BLANK_SPACE, BLANK_SPACE);
      }
      for (Artifact child : children) {
         processArtifacts(child);
      }
   }

   private void writeRow(String testPlan, List<String> perfSpecs, List<String> pids, List<String> testProcNames, List<String> testProcStatuses, List<String> testResultNames, List<String> errors) throws IOException {
      List<List<List<String>>> allPartitions = new LinkedList<>();
      allPartitions.add(Lists.partition(perfSpecs, maxRowsPerCell));
      allPartitions.add(Lists.partition(pids, maxRowsPerCell));
      allPartitions.add(Lists.partition(testProcNames, maxRowsPerCell));
      allPartitions.add(Lists.partition(testProcStatuses, maxRowsPerCell));
      allPartitions.add(Lists.partition(testResultNames, maxRowsPerCell));
      allPartitions.add(Lists.partition(errors, maxRowsPerCell));

      int max = 0;
      for (List<List<String>> list : allPartitions) {
         max = Math.max(max, list.size());
      }

      for (int i = 0; i < max; i++) {
         excelWriter.writeCell(testPlan);
         testPlan = BLANK_SPACE;

         // size - 1 to handle error cells separately
         for (int j = 0; j < allPartitions.size() - 1; j++) {
            List<List<String>> list = allPartitions.get(j);
            if (list.size() > i) {
               excelWriter.writeCell(org.eclipse.osee.framework.jdk.core.util.Collections.toString("\n", list.get(i)));
            } else {
               excelWriter.writeCell(BLANK_SPACE);
            }
         }

         List<List<String>> errorPartitions = allPartitions.get(allPartitions.size() - 1);
         if (errorPartitions.size() > i) {
            excelWriter.setCellStyle(ExcelXmlWriter.STYLE.ERROR, 6);
            excelWriter.writeCell(
               org.eclipse.osee.framework.jdk.core.util.Collections.toString("\n", errorPartitions.get(i)));
         } else {
            excelWriter.writeCell(BLANK_SPACE);
         }
         excelWriter.endRow();
      }

   }

   private class TestStatusAndResults {
      List<String> testStatus;
      List<String> testResults;
      List<String> errors;
   }

   private TestStatusAndResults calculateTestStatusAndResults(List<Artifact> testProcedures) {
      List<String> testProcStatus = new LinkedList<>();
      List<String> testResultNames = new LinkedList<>();
      List<String> errors = new LinkedList<>();
      for (Artifact testProc : testProcedures) {
         String status = testProc.getSoleAttributeValue(CoreAttributeTypes.TestProcedureStatus, BLANK_SPACE);
         Collection<Artifact> testResults =
            testProc.getRelatedArtifacts(CoreRelationTypes.Test_Unit_Result__Test_Result);
         testProcStatus.add(status);

         TestStatusEnum enumStatus = TestStatusEnum.fromString(status);
         if (testResults.isEmpty()) {
            switch (enumStatus) {
               case COMPLETED_PASSED_CODE:
               case COMPLETED_WITH_ISSUES_CODE:
               case COMPLETED_WITH_ISSUES_RESOLVED_CODE:
                  errors.add("No test result files found...");
                  break;
               default:
                  break;
            }
         } else {
            if (enumStatus == TestStatusEnum.NOT_PERFORMED_CODE) {
               errors.add("Results with NOT_PERFORMED_CODE status");
            }

            for (Artifact testResult : testResults) {
               String extension = testResult.getSoleAttributeValueAsString(CoreAttributeTypes.Extension, "");
               testResultNames.add(
                  testResult.getName() + (extension.equals("") ? extension : "." + extension.toLowerCase()));
            }
         }

      }

      TestStatusAndResults toReturn = new TestStatusAndResults();
      toReturn.testResults = testResultNames;
      toReturn.testStatus = testProcStatus;
      toReturn.errors = errors;
      return toReturn;
   }

   private boolean isTestPlan(Artifact src) {
      return src.isOfType(CoreArtifactTypes.TestPlanElement);
   }

   private List<String> getRequirementsCellOutput(Artifact art, RelationTypeSide rts) {
      List<String> result = null;
      if (art.isOfType(CoreArtifactTypes.TestPlanElement)) {
         result = getRequirementsAsString(art, rts);
      }
      return result;
   }

   private List<String> getRequirementsAsString(Artifact testPlan, RelationTypeSide rts) {
      Collection<Artifact> requirementArtifacts = testPlan.getRelatedArtifacts(rts);
      List<String> requirementNames = new ArrayList<>();
      for (Artifact req : requirementArtifacts) {
         String paragraphNumber = req.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "");
         requirementNames.add(paragraphNumber + BLANK_SPACE + req.getName());
      }

      return requirementNames;
   }

   private String getArtifactNameAndParagraph(Artifact art) {
      return art.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "") + BLANK_SPACE + art.getName();
   }

   private void report() throws IOException {
      excelWriter.endSheet();
      excelWriter.endWorkbook();
      if (performFileWrite) {
         IFile iFile = OseeData.getIFile("TestPlanComplianceReport_" + Lib.getDateTimeString() + ".xml");
         AIFile.writeToFile(iFile, charBackedStream);
         Program.launch(iFile.getLocation().toOSString());
      }
   }

   private void init(VariableMap variableMap) throws IOException {
      inputArtifacts = variableMap.getArtifacts(TEST_PLANS);
      String max = variableMap.getString(MAX_ENTRIES_PER_CELL);
      if (!Strings.isNumeric(max)) {
         log(String.format("[%s] is not a valid number, using [%d] for max entries per cell", max, maxRowsPerCell));
      } else {
         maxRowsPerCell = Integer.parseInt(max);
      }

      String font = variableMap.getString(FONT_SIZE);
      if (!Strings.isNumeric(font)) {
         log(String.format("[%s] is not a valid number, using [%d] for font size", font, fontSize));
      } else {
         fontSize = Integer.parseInt(font);
      }
      initReport();
      load();
   }

   private void initReport() throws IOException {
      excelWriter = new ExcelXmlWriter(defaultWriter, null, ExcelXmlWriter.defaultEmptyString, fontSize);
      String[] columnHeaders = {
         "Test Plan & Paragraph",
         "Perf Spec Requirement(s)",
         "PIDS",
         "Test Procedure",
         "Test Status",
         "Test Result File",
         "Errors"};

      ExcelColumn[] columns = new ExcelColumn[columnHeaders.length];
      for (int i = 0; i < columnHeaders.length; i++) {
         String header = columnHeaders[i];
         ExcelColumn newCol = ExcelColumn.newCol(header, 250, ExcelXmlWriter.WrappedStyle);
         columns[i] = newCol;
      }

      excelWriter.startSheet(getName(), columns);
   }

   private void load() {
      testPlans = new ArrayList<>();
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
      StringBuilder sb = new StringBuilder();
      sb.append("<xWidgets>");
      sb.append(String.format("<XWidget xwidgetType=\"XListDropViewer\" displayName=\"%s\" />", TEST_PLANS));
      sb.append(String.format("<XWidget xwidgetType=\"XText\" displayName=\"%s\" defaultValue=\"%d\"/>",
         MAX_ENTRIES_PER_CELL, DEFAULT_MAX));
      sb.append(String.format("<XWidget xwidgetType=\"XText\" displayName=\"%s\" defaultValue=\"%d\"/>", FONT_SIZE,
         DEFAULT_FONT_SIZE));
      sb.append("</xWidgets>");
      return sb.toString();
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
