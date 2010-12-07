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
package org.eclipse.osee.define.test.blam;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.define.blam.operation.TestPlanComplianceReport;
import org.eclipse.osee.define.blam.operation.TestStatusEnum;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.support.test.util.DemoSawBuilds;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @link: TestPlanComplianceReport
 * @author: Karol M. Wilk
 */
public final class TestPlanComplianceReportTest {
   private static final String EXPECTED_NOT_PERFORMED_DATA =
      "    <Cell><Data ss:Type=\"String\">Sample Test Procedure_0</Data></Cell>\n" + //
      "    <Cell><Data ss:Type=\"String\">Not Performed</Data></Cell>\n" + //
      "    <Cell><Data ss:Type=\"String\"> </Data></Cell>";
   private static final String EXPECTED_OSEE_ERROR_STYLE = "    <Cell ss:StyleID=\"OseeErrorStyle\">";
   private static final String EXPECTED_OSEE_BOLD_STYLE = "    <Cell ss:StyleID=\"OseeBoldStyle\">";
   private static final String EXPECTED_MULTIPLE_RESULTS_IN_1_CELL = //
      "    <Cell><Data ss:Type=\"String\">Sample_Test_Result_0.pdf&#10;Sample_Test_Result_1.pdf</Data></Cell>";

   private static SevereLoggingMonitor monitorLog;
   private final TestPlanComplianceReport testCompReport = new TestPlanComplianceReport();
   private Collection<Artifact> dummyArtifactList = null;

   private StringWriter resultBuffer = null;

   @Test
   public void testNotPerformedStatus() throws Exception {
      buildTest(1, TestStatusEnum.NOT_PERFORMED_CODE, 0);
      Assert.assertTrue("No \"Not Performed\" string found.",
         resultBuffer.toString().contains(EXPECTED_NOT_PERFORMED_DATA));
      new PurgeArtifacts(dummyArtifactList).execute();
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
      new PurgeArtifacts(dummyArtifactList).execute();
   }

   //@formatter:off
   /**
    * 'Completed', 'Completed with Issues' and 'Completed with Issues Resolved' must
    * have results file if not bold or indicate error somehow.
    */
   //@formatter:on
   @Test
   public void testCompletedStates() throws Exception {
      buildTest(1, TestStatusEnum.COMPLETED_PASSED_CODE, 0);
      Assert.assertTrue("No \"OseeErrorStyle\" string found.",
         resultBuffer.toString().contains(EXPECTED_OSEE_ERROR_STYLE));
      Assert.assertTrue("No \"OseeBoldStyle\" string found.",
         resultBuffer.toString().contains(EXPECTED_OSEE_BOLD_STYLE));
      new PurgeArtifacts(dummyArtifactList).execute();

      buildTest(1, TestStatusEnum.COMPLETED_WITH_ISSUES_CODE, 0);
      Assert.assertTrue("No \"OseeErrorStyle\" string found.",
         resultBuffer.toString().contains(EXPECTED_OSEE_ERROR_STYLE));
      Assert.assertTrue("No \"OseeBoldStyle\" string found.",
         resultBuffer.toString().contains(EXPECTED_OSEE_BOLD_STYLE));
      new PurgeArtifacts(dummyArtifactList).execute();

      buildTest(1, TestStatusEnum.COMPLETED_WITH_ISSUES_RESOLVED_CODE, 0);
      Assert.assertTrue("No \"OseeErrorStyle\" string found.",
         resultBuffer.toString().contains(EXPECTED_OSEE_ERROR_STYLE));
      Assert.assertTrue("No \"OseeBoldStyle\" string found.",
         resultBuffer.toString().contains(EXPECTED_OSEE_BOLD_STYLE));
      new PurgeArtifacts(dummyArtifactList).execute();
   }

   @Test
   public void testResultFilesGroupedInOneCell() throws Exception {
      buildTest(1, TestStatusEnum.COMPLETED_PASSED_CODE, 2);
      Assert.assertTrue("All test result files should be in 1 cell",
         resultBuffer.toString().contains(EXPECTED_MULTIPLE_RESULTS_IN_1_CELL));
   }

   @BeforeClass
   public static void setUp() throws Exception {
      monitorLog = TestUtil.severeLoggingStart();
   }

   @AfterClass
   public static void tearDown() throws Exception {
      TestUtil.severeLoggingEnd(monitorLog);
   }

   private void buildTest(int amountOfTestProcedures, TestStatusEnum testProcedureStatus, int testResultsAmount) throws Exception {
      resultBuffer = new StringWriter();
      testCompReport.runOperation(loadArtifacts(amountOfTestProcedures, testProcedureStatus, testResultsAmount),
         new NullProgressMonitor(), resultBuffer, false);
   }

   private VariableMap loadArtifacts(int amountOfTestProcedures, TestStatusEnum testProcedureStatus, int testResultsAmount) throws OseeCoreException {
      Artifact testPlan =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.TestPlanElement, DemoSawBuilds.SAW_Bld_1, "Sample_Test_Plan");
      testPlan.persist();

      dummyArtifactList = new ArrayList<Artifact>();
      dummyArtifactList.add(testPlan);

      for (int i = 0; i < amountOfTestProcedures; i++) {
         Artifact testProcedure =
            ArtifactTypeManager.addArtifact(CoreArtifactTypes.TestProcedure, DemoSawBuilds.SAW_Bld_1,
               "Sample Test Procedure" + "_" + i);

         testProcedure.setSoleAttributeValue(CoreAttributeTypes.TestProcedureStatus, testProcedureStatus.testStatus);

         testPlan.addRelation(CoreRelationTypes.Executes__Test_Procedure, testProcedure);
         testPlan.persist();

         for (int j = 0; j < testResultsAmount; j++) {
            Artifact testResult =
               ArtifactTypeManager.addArtifact(CoreArtifactTypes.TestResultWML, DemoSawBuilds.SAW_Bld_1,
                  "Sample_Test_Result_" + j);
            testResult.setSoleAttributeValue(CoreAttributeTypes.Extension, "pdf");
            testProcedure.addRelation(CoreRelationTypes.Test_Unit_Result__Test_Result, testResult);
            testProcedure.persist();
         }
      }
      return new VariableMap(TestPlanComplianceReport.TEST_PLANS, dummyArtifactList);
   }
}
