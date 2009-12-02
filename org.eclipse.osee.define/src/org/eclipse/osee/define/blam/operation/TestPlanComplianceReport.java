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
package org.eclipse.osee.define.blam.operation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultPage.Manipulations;

public class TestPlanComplianceReport extends AbstractBlam {
   private static final String MISSING = "?";
   private static final String EMPTY = "&nbsp;";
   private String[] previousCells = {MISSING, MISSING, MISSING, MISSING, MISSING};
   private final String[] columnHeaders =
         {"Test Plan & Paragraph", "Perf Spec Requirement(s)", "Test Procedure", "Test Status", "Test Result"};
   private Collection<Artifact> inputArtifacts;
   private Collection<Artifact> testPlans;
   private StringBuilder report;

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
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
         reportLine(node, "N/A (" + node.getArtifactTypeName() + ")", EMPTY, EMPTY);
      }
      for (Artifact child : children) {
         processArtifacts(child);
      }
   }

   private boolean isTestPlan(Artifact src) {
      ArtifactType temp = src.getArtifactType();
      if (temp.inheritsFrom(CoreArtifactTypes.TestPlanElement)) {
         return true;
      }

      return false;
   }

   private void processTestPlan(Artifact testPlan) throws OseeCoreException, IOException {
      Collection<Artifact> testProcedures = getTestProcedures(testPlan);

      if (testProcedures.isEmpty()) {
         reportLine(testPlan, MISSING, MISSING, MISSING);
      } else {
         for (Artifact testProc : testProcedures) {
            processTestProcedure(testPlan, testProc);
         }
      }
   }

   private void processTestProcedure(Artifact testPlan, Artifact testProc) throws IOException, OseeCoreException {
      Collection<Artifact> testResults = getTestResults(testProc);
      if (testResults.isEmpty()) {
         reportLine(testPlan, testProc.getName(), MISSING, MISSING);
      } else {
         for (Artifact testResult : testResults) {
            reportLine(testPlan, testProc.getName(), getStatus(testProc), testResult.getName());
         }
      }
   }

   private String getStatus(Artifact testProc) throws OseeCoreException {
      String returnValue = testProc.getSoleAttributeValue(CoreAttributeTypes.TEST_PROCEDURE_STATUS);

      return returnValue;
   }

   private String getName(Artifact art) throws OseeCoreException {
      String testPlanNumber = art.getSoleAttributeValue(CoreAttributeTypes.PARAGRAPH_NUMBER, "");
      String testPlanOutput = testPlanNumber + " " + art.getName();
      return testPlanOutput;
   }

   private Collection<Artifact> getTestProcedures(Artifact testPlan) throws OseeCoreException {
      Collection<Artifact> ret = testPlan.getRelatedArtifacts(CoreRelationTypes.Executes__Test_Procedure);

      return ret;
   }

   private Collection<Artifact> getTestResults(Artifact testProc) throws OseeCoreException {
      Collection<Artifact> ret = testProc.getRelatedArtifacts(CoreRelationTypes.Test_Unit_Result__Test_Result);

      return ret;
   }

   private String getRequirementsCellOutput(Artifact art) throws OseeCoreException {
      if (art.getArtifactType().inheritsFrom(CoreArtifactTypes.TestPlanElement)) {
         return getRequirementsCellOutputForTestPlan(art);
      }

      return EMPTY;
   }

   private String getRequirementsCellOutputForTestPlan(Artifact testPlan) throws OseeCoreException {
      String ret = getRequirementsAsString(testPlan);
      if (ret.isEmpty()) {
         ret = MISSING;
      }

      return ret;
   }

   private String getRequirementsAsString(Artifact testPlan) throws OseeCoreException {
      Collection<Artifact> requirementArtifacts =
            testPlan.getRelatedArtifacts(CoreRelationTypes.Verification_Plan__Requirement);
      Collection<String> requirementNames = new ArrayList<String>();
      for (Artifact req : requirementArtifacts) {
         String paragraphNumber = req.getSoleAttributeValue(CoreAttributeTypes.PARAGRAPH_NUMBER, "");
         requirementNames.add(paragraphNumber + " " + req.getName());
      }

      return StringUtils.join(requirementNames, "\n");
   }

   private void reportLine(Artifact art, String testProc, String testStatus, String testResult) throws IOException, OseeCoreException {
      String[] outputCells = new String[5];
      String testPlanOutput = getName(art);
      String requirements = getRequirementsCellOutput(art);
      String[] cells = new String[] {testPlanOutput, requirements, testProc, testStatus, testResult};
      for (int i = 0; i < cells.length; i++) {
         if (previousCells[i].equals(cells[i])) {
            if (i == 0 || outputCells[i - 1].equals(" ")) {
               outputCells[i] = " ";
            } else {
               outputCells[i] = cells[i];
            }
         } else {
            outputCells[i] = cells[i];
         }
      }
      previousCells = cells.clone();
      report.append(AHTML.addRowMultiColumnTable(outputCells));
   }

   private void report() throws OseeCoreException, IOException {
      report.append(AHTML.endMultiColumnTable());
      XResultData rd = new XResultData();
      rd.addRaw(report.toString());
      rd.report("Test Plan Compliance Report", Manipulations.RAW_HTML);
   }

   private void init(VariableMap variableMap) throws OseeCoreException, IOException {
      inputArtifacts = variableMap.getArtifacts("artifacts");
      initReport();
      load();
   }

   private void initReport() throws OseeCoreException, IOException {
      report = new StringBuilder(AHTML.beginMultiColumnTable(100, 1));
      report.append(AHTML.addHeaderRowMultiColumnTable(columnHeaders));
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
