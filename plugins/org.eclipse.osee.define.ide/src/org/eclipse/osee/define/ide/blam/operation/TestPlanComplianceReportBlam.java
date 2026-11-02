/*********************************************************************
 * Copyright (c) 2010 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.define.ide.blam.operation;

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Extension;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.ParagraphNumber;
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
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelColumn;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.swt.program.Program;
import org.osgi.service.component.annotations.Component;

/**
 * Test: @link: TestPlanComplianceReportTest
 *
 * @author: Karol M. Wilk
 */
@Component(service = AbstractBlam.class, immediate = true)
public class TestPlanComplianceReportBlam extends AbstractBlam {

   private static final String BLAM_NAME = "Test Plan Compliance Report";
   public static final String TEST_PLANS = "Test Plans";
   public static final String MAX_ENTRIES_PER_CELL = "Max Entries Per Cell";
   public static final String FONT_SIZE = "Font Size";
   private static final String BLANK_SPACE = " ";
   private static final int DEFAULT_MAX = 25;
   private static final int DEFAULT_FONT_SIZE = 11;

   private Collection<Artifact> inputArtifacts;
   private Collection<Artifact> testPlans;
   private boolean performFileWrite;
   private int maxRowsPerCell = DEFAULT_MAX;
   private int fontSize = DEFAULT_FONT_SIZE;
   private CharBackedInputStream charBackedStream = null;
   private Writer defaultWriter = null;
   private ExcelXmlWriter excelWriter = null;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public TestPlanComplianceReportBlam() {
      super(BLAM_NAME, CoreImage.PUBLISH);
   }

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wb.andListDropWidget(TEST_PLANS);
      wb.andTextWidget(MAX_ENTRIES_PER_CELL).andDefault(DEFAULT_MAX);
      wb.andTextWidget(FONT_SIZE).andDefault(DEFAULT_FONT_SIZE);
      return wb.getXWidgetDatas();
   }

   //////////////////////////////////////////
   // Operation
   //////////////////////////////////////////

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      charBackedStream = new CharBackedInputStream();
      runOperation(variableMap, monitor, charBackedStream.getWriter(), true);
   }

   // Sets internal data structure for storage to argument writer, toggles performFileWrite
   // to actually write the file or not. Afterwards calls runOperation(...).
   // Usually used for unit-testing.
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor, Writer writer, boolean performFileWrite)
      throws Exception {
      defaultWriter = writer;
      this.performFileWrite = performFileWrite;

      init(variableMap);

      for (Artifact input : inputArtifacts) {
         processArtifacts(input);
      }

      report();
   }

   //////////////////////////////////////////
   // Helpers
   //////////////////////////////////////////

   private void processArtifacts(Artifact node) throws IOException {
      Collection<Artifact> children = node.getChildren();

      if (isTestPlan(node)) {
         String testPlan = getArtifactNameAndParagraph(node);
         List<String> perfSpecs = getRequirementsCellOutput(node, CoreRelationTypes.VerificationPlan_Requirement);
         List<String> pids = getRequirementsCellOutput(node, CoreRelationTypes.Validation_Requirement);
         List<Artifact> testProcedures = node.getRelatedArtifacts(CoreRelationTypes.Executes_TestProcedure);

         List<String> testProcedureNames = Lists.newLinkedList(Named.getNames(testProcedures));
         TestStatusAndResults testStatusAndResults = calculateTestStatusAndResults(testProcedures);

         writeRow(testPlan, perfSpecs, pids, testProcedureNames, testStatusAndResults.testStatus,
            testStatusAndResults.testResults, testStatusAndResults.errors);
      } else {
         excelWriter.writeRow(node, BLANK_SPACE, BLANK_SPACE, String.format("N/A (%s)", node.getArtifactTypeName()),
            BLANK_SPACE, BLANK_SPACE, BLANK_SPACE);
      }
      for (Artifact child : children) {
         processArtifacts(child);
      }
   }

   private void writeRow(String testPlan, List<String> perfSpecs, List<String> pids, List<String> testProcNames,
      List<String> testProcStatuses, List<String> testResultNames, List<String> errors) throws IOException {
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
         Collection<Artifact> testResults = testProc.getRelatedArtifacts(CoreRelationTypes.ResultsData_TestResult);
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
               String extension = testResult.getSoleAttributeValueAsString(Extension, "");
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
      return src.isOfType(CoreArtifactTypes.TestPlanElementMsWord);
   }

   private List<String> getRequirementsCellOutput(Artifact art, RelationTypeSide rts) {
      List<String> result = null;
      if (art.isOfType(CoreArtifactTypes.TestPlanElementMsWord)) {
         result = getRequirementsAsString(art, rts);
      }
      return result;
   }

   private List<String> getRequirementsAsString(Artifact testPlan, RelationTypeSide rts) {
      Collection<Artifact> requirementArtifacts = testPlan.getRelatedArtifacts(rts);
      List<String> requirementNames = new ArrayList<>();
      for (Artifact req : requirementArtifacts) {
         String paragraphNumber = req.getSoleAttributeValue(ParagraphNumber, "");
         requirementNames.add(paragraphNumber + BLANK_SPACE + req.getName());
      }

      return requirementNames;
   }

   private String getArtifactNameAndParagraph(Artifact art) {
      return art.getSoleAttributeValue(ParagraphNumber, "") + BLANK_SPACE + art.getName();
   }

   private void report() throws IOException {
      excelWriter.endSheet();
      excelWriter.endWorkbook();
      if (performFileWrite) {
         IFile iFile = OseeData.getIFile(String.format("TestPlanComplianceReport_%s.xml", Lib.getDateTimeString()));
         AIFile.writeToFile(iFile, charBackedStream);
         Program.launch(iFile.getLocation().toOSString());
      }
   }

   private void init(VariableMap variableMap) throws IOException {
      inputArtifacts = variableMap.getArtifacts(TEST_PLANS);
      String max = variableMap.getString(MAX_ENTRIES_PER_CELL);
      if (!Strings.isNumeric(max)) {
         logf("[%s] is not a valid number, using [%d] for max entries per cell", max, maxRowsPerCell);
      } else {
         maxRowsPerCell = Integer.parseInt(max);
      }

      String font = variableMap.getString(FONT_SIZE);
      if (!Strings.isNumeric(font)) {
         logf("[%s] is not a valid number, using [%d] for font size", font, fontSize);
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
      RelationManager.getRelatedArtifacts(testPlans, 1, CoreRelationTypes.VerificationPlan_Requirement);
      Collection<Artifact> temp =
         RelationManager.getRelatedArtifacts(testPlans, 1, CoreRelationTypes.Executes_TestProcedure);
      RelationManager.getRelatedArtifacts(temp, 1, CoreRelationTypes.ResultsData_TestResult);
   }

   //////////////////////////////////////////
   // Categories & Description
   //////////////////////////////////////////

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.REPORTS);
   }

   @Override
   public String getDescriptionUsage() {
      return "Generates an Excel compliance report for dropped test plans. Shows traceability from test plan scripts to requirements including paragraph numbers, test script status, and related test results.";
   }

}
