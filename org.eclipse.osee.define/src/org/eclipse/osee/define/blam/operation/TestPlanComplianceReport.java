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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.CoreArtifacts;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultPage.Manipulations;

public class TestPlanComplianceReport extends AbstractBlam {
   private final String EMPTY_SET = "------";
   private String[] previousCells = {EMPTY_SET, EMPTY_SET, EMPTY_SET, EMPTY_SET};
   private final String[] columnHeaders = {"Type", "Name", "System Requirement", "Test Procedure", "Test Result"};
   private Collection<Artifact> inputArtifacts;
   private Collection<Artifact> testPlans;
   private StringBuilder report;

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      init(variableMap);

      for (Artifact input : inputArtifacts) {
         processTestPlanTree(input, 0);
      }

      report();
   }

   private void processTestPlanTree(Artifact node, int depth) throws OseeCoreException, IOException {
      Collection<Artifact> children = node.getChildren();

      if (isTestPlan(node)) {
         processTestPlan(node);
      } else {
         reportLine(node, EMPTY_SET, EMPTY_SET);
         for (Artifact child : children) {
            processTestPlanTree(child, depth + 1);
         }
      }
   }

   private boolean isTestPlan(Artifact src) {
      if (src.getArtifactType().inheritsFrom(CoreArtifacts.TestPlanElement)) {
         return true;
      } else {
         return false;
      }
   }

   private void processTestPlan(Artifact testPlan) throws OseeCoreException, IOException {
      Collection<Artifact> testProcedures = getTestProcedures(testPlan);

      if (testProcedures.isEmpty()) {
         reportLine(testPlan, EMPTY_SET, EMPTY_SET);
      } else {
         for (Artifact testProc : testProcedures) {
            processTestProcedure(testPlan, testProc);
         }
      }
   }

   private void processTestProcedure(Artifact testPlan, Artifact testProc) throws IOException, OseeCoreException {
      Collection<Artifact> testResults = getTestResults(testProc);
      if (testResults.isEmpty()) {
         reportLine(testPlan, testProc.getName(), EMPTY_SET);
      } else {
         for (Artifact testResult : testResults) {
            reportLine(testPlan, testProc.getName(), testResult.getName());
         }
      }
   }

   private String getName(Artifact art) throws OseeCoreException {
      if (isTestPlan(art)) {
         String testPlanNumber = art.getSoleAttributeValue("Paragraph Number", "##");
         String testPlanOutput = testPlanNumber + " " + art.getName();
         return testPlanOutput;
      } else {
         return art.getName();
      }
   }

   private Collection<Artifact> getTestProcedures(Artifact testPlan) throws OseeCoreException {
      Collection<Artifact> ret = testPlan.getRelatedArtifacts(CoreRelationEnumeration.EXECUTES__TEST_PROCEDURE);

      return ret;
   }

   private Collection<Artifact> getTestResults(Artifact testProc) throws OseeCoreException {
      Collection<Artifact> ret = testProc.getRelatedArtifacts(CoreRelationEnumeration.TEST_UNIT_RESULT__TEST_RESULT);

      return ret;
   }

   private String getRequirementsFor(Artifact testPlan) throws OseeCoreException {
      Collection<Artifact> requirementArtifacts =
            testPlan.getRelatedArtifacts(CoreRelationEnumeration.VERIFICATION_PLAN);
      Collection<String> requirementNames = new ArrayList<String>();
      for (Artifact req : requirementArtifacts) {
         String paragraphNumber = req.getSoleAttributeValueAsString("Paragraph Number", "##");
         requirementNames.add(paragraphNumber + " " + req.getName());
      }
      String ret = StringUtils.join(requirementNames, "\n");
      if (ret.equals("")) {
         ret = EMPTY_SET;
      }

      return ret;
   }

   private void reportLine(Artifact art, String testProc, String testResult) throws IOException, OseeCoreException {
      String[] outputCells = new String[5];
      String testPlanOutput = getName(art);
      String requirements = getRequirementsFor(art);
      String[] cells = new String[] {testPlanOutput, requirements, testProc, testResult};
      outputCells[0] = art.getArtifactTypeName();
      for (int i = 0; i < cells.length; i++) {
         if (previousCells[i].equals(cells[i])) {
            if (i == 0 || outputCells[i].equals(" ")) {
               outputCells[i + 1] = " ";
               outputCells[0] = " ";
            } else {
               outputCells[i + 1] = cells[i];
            }
         } else {
            outputCells[i + 1] = cells[i];
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
      RelationManager.getRelatedArtifacts(testPlans, 1, CoreRelationEnumeration.VERIFICATION_PLAN);
      Collection<Artifact> temp =
            RelationManager.getRelatedArtifacts(testPlans, 1, CoreRelationEnumeration.EXECUTES__TEST_PROCEDURE);
      RelationManager.getRelatedArtifacts(temp, 1, CoreRelationEnumeration.TEST_UNIT_RESULT__TEST_RESULT);
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
