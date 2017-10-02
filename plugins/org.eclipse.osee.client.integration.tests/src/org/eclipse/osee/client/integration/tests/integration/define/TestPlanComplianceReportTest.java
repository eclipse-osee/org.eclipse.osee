/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.client.integration.tests.integration.define;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.define.blam.operation.TestPlanComplianceReport;
import org.eclipse.osee.define.blam.operation.TestStatusEnum;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 * @link: TestPlanComplianceReport
 * @author: Karol M. Wilk
 */
public final class TestPlanComplianceReportTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   private static final String EXPECTED_NOT_PERFORMED_DATA =
      "    <Cell><Data ss:Type=\"String\">Sample Test Procedure_0</Data></Cell>\n" + //
         "    <Cell><Data ss:Type=\"String\">Not Performed</Data></Cell>\n" + //
         "    <Cell><Data ss:Type=\"String\"> </Data></Cell>";
   private static final String EXPECTED_OSEE_ERROR_STYLE = "    <Cell ss:StyleID=\"OseeErrorStyle\">";
   private static final String EXPECTED_MULTIPLE_RESULTS_IN_1_CELL = //
      "    <Cell><Data ss:Type=\"String\">Sample_Test_Result_0&#10;Sample_Test_Result_1</Data></Cell>";

   private final TestPlanComplianceReport testCompReport = new TestPlanComplianceReport();
   private Collection<Artifact> dummyArtifactList = null;

   private StringWriter resultBuffer = null;

   @Test
   public void testNotPerformedStatus() throws Exception {
      buildTest(1, TestStatusEnum.NOT_PERFORMED_CODE, 0);
      Assert.assertTrue("No \"Not Performed\" string found.",
         resultBuffer.toString().contains(EXPECTED_NOT_PERFORMED_DATA));
      Operations.executeWorkAndCheckStatus(new PurgeArtifacts(dummyArtifactList));
   }

   //@formatter:off
   /**
    * Not performed state cannot be have results,
    * if it does it should indicate an error in appropriate column/row.
    */
   //@formatter:on
   @Test
   public void testNotPerformedWithResults() throws Exception {
      buildTest(1, TestStatusEnum.NOT_PERFORMED_CODE, 1);
      Assert.assertTrue("No \"OseeErrorStyle\" string found.",
         resultBuffer.toString().contains(EXPECTED_OSEE_ERROR_STYLE));
      Operations.executeWorkAndCheckStatus(new PurgeArtifacts(dummyArtifactList));
   }

   //@formatter:off
   /**
    * 'Completed', 'Completed with Issues' and 'Completed with Issues Resolved' must
    * have results file if not, indicate error somehow.
    */
   //@formatter:on
   @Test
   public void testCompletedStates() throws Exception {
      buildTest(1, TestStatusEnum.COMPLETED_PASSED_CODE, 0);
      Assert.assertTrue("No \"OseeErrorStyle\" string found.",
         resultBuffer.toString().contains(EXPECTED_OSEE_ERROR_STYLE));
      Operations.executeWorkAndCheckStatus(new PurgeArtifacts(dummyArtifactList));

      buildTest(1, TestStatusEnum.COMPLETED_WITH_ISSUES_CODE, 0);
      Assert.assertTrue("No \"OseeErrorStyle\" string found.",
         resultBuffer.toString().contains(EXPECTED_OSEE_ERROR_STYLE));
      Operations.executeWorkAndCheckStatus(new PurgeArtifacts(dummyArtifactList));

      buildTest(1, TestStatusEnum.COMPLETED_WITH_ISSUES_RESOLVED_CODE, 0);
      Assert.assertTrue("No \"OseeErrorStyle\" string found.",
         resultBuffer.toString().contains(EXPECTED_OSEE_ERROR_STYLE));
      Operations.executeWorkAndCheckStatus(new PurgeArtifacts(dummyArtifactList));
   }

   @Test
   public void testResultFilesGroupedInOneCell() throws Exception {
      buildTest(1, TestStatusEnum.COMPLETED_PASSED_CODE, 2);
      Assert.assertTrue("All test result files should be in 1 cell",
         resultBuffer.toString().contains(EXPECTED_MULTIPLE_RESULTS_IN_1_CELL));
      Operations.executeWorkAndCheckStatus(new PurgeArtifacts(dummyArtifactList));
   }

   private void buildTest(int amountOfTestProcedures, TestStatusEnum testProcedureStatus, int testResultsAmount) throws Exception {
      resultBuffer = new StringWriter();
      testCompReport.runOperation(loadArtifacts(amountOfTestProcedures, testProcedureStatus, testResultsAmount),
         new NullProgressMonitor(), resultBuffer, false);
   }

   private VariableMap loadArtifacts(int amountOfTestProcedures, TestStatusEnum testProcedureStatus, int testResultsAmount) {
      Artifact testPlan =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.TestPlanElement, SAW_Bld_1, "Sample_Test_Plan");
      testPlan.persist(getClass().getSimpleName());

      dummyArtifactList = new ArrayList<>();
      dummyArtifactList.add(testPlan);

      for (int i = 0; i < amountOfTestProcedures; i++) {
         Artifact testProcedure = ArtifactTypeManager.addArtifact(CoreArtifactTypes.TestProcedure, SAW_Bld_1,
            "Sample Test Procedure" + "_" + i);

         testProcedure.setSoleAttributeValue(CoreAttributeTypes.TestProcedureStatus, testProcedureStatus.testStatus);

         testPlan.addRelation(CoreRelationTypes.Executes__Test_Procedure, testProcedure);
         testPlan.persist(getClass().getSimpleName());

         for (int j = 0; j < testResultsAmount; j++) {
            Artifact testResult =
               ArtifactTypeManager.addArtifact(CoreArtifactTypes.TestResultWML, SAW_Bld_1, "Sample_Test_Result_" + j);
            testProcedure.addRelation(CoreRelationTypes.Test_Unit_Result__Test_Result, testResult);
            testProcedure.persist(getClass().getSimpleName());
         }
      }

      return new VariableMap(TestPlanComplianceReport.TEST_PLANS, dummyArtifactList,
         TestPlanComplianceReport.MAX_ENTRIES_PER_CELL, "10", TestPlanComplianceReport.FONT_SIZE, "11");

   }
}
