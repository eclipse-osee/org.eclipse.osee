/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.orcs.rest;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTokens.DefaultHierarchyRoot;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.InputStream;
import java.util.logging.Level;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.client.test.framework.NotProductionDataStoreRule;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.orcs.rest.model.ArtifactEndpoint;
import org.eclipse.osee.orcs.rest.model.RelationEndpoint;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

/**
 * Integration test for the Generic Report endpoint using the relationLevel feature.
 * <p>
 * Sets up a ReportTemplate artifact with JavaCode that uses relationLevel, creates test artifacts with a
 * Code-Requirement relation, then calls the report endpoint and verifies the response.
 *
 * @author David W. Miller
 */
public class ReportEndpointTest {

   @Rule
   public NotProductionDataStoreRule notProduction = new NotProductionDataStoreRule();

   private static JaxRsApi jaxRsApi;
   private static ArtifactEndpoint commonArtifactEndpoint;
   private static ArtifactEndpoint workingBranchArtifactEndpoint;
   private static RelationEndpoint relationEndpoint;

   private static ArtifactToken templateArt;
   private static ArtifactToken followForkTemplateArt;
   private static ArtifactToken followForkComprehensiveTemplateArt;
   private static ArtifactToken columnsAndTypeTemplateArt;
   private static ArtifactToken hierarchyTemplateArt;
   private static ArtifactToken andIdFollowChainTemplateArt;
   private static ArtifactToken filterTemplateArt;
   private static ArtifactToken subsystemReqArt;
   private static ArtifactToken subsystemReqExcludedArt;
   private static ArtifactToken codeUnitArt;
   private static ArtifactToken lowerLevelReqArt;
   private static ArtifactToken simReqArt;
   private static ArtifactToken supportingInfoArt;

   // @formatter:off
   private static final String TEMPLATE_CODE = getTemplateCode();

   private static String getTemplateCode() {
      StringBuilder sb = new StringBuilder();
      sb.append("package org.eclipse.osee.orcs.rest.internal.writers;\n");
      sb.append("import org.eclipse.osee.framework.core.data.ArtifactId;\n");
      sb.append("import org.eclipse.osee.framework.core.data.ArtifactTypeToken;\n");
      sb.append("import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;\n");
      sb.append("import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;\n");
      sb.append("import org.eclipse.osee.framework.core.enums.CoreRelationTypes;\n");
      sb.append("import org.eclipse.osee.orcs.rest.model.GenericReport;\n");
      sb.append("/**\n");
      sb.append(" * @author David W. Miller\n");
      sb.append(" */\n");
      sb.append("public class GenericReportCode {\n");
      sb.append("   public void testNamedRelation(GenericReport report) {\n");
      sb.append("      report.level(\"Subsystem Requirements\", report.query().andIsOfType(CoreArtifactTypes.SubsystemRequirementMsWord)). //\n");
      sb.append("         column(\"Artifact ID\"). //\n");
      sb.append("         column(\"Requirement Name\", CoreAttributeTypes.Name); //\n");
      sb.append("      report.relationLevel(\"Related Code unit\", \"Code-Requirement\", \"SIDE_A\"). //\n");
      sb.append("         column(\"Artifact ID\"). //\n");
      sb.append("         column(\"Code Unit\", CoreAttributeTypes.Name). //\n");
      sb.append("         column(\"File System Path\", CoreAttributeTypes.FileSystemPath); //\n");
      sb.append("   }\n");
      sb.append("}\n");
      return sb.toString();
   }

   private static final String FOLLOW_FORK_TEMPLATE_CODE = getFollowForkTemplateCode();

   private static String getFollowForkTemplateCode() {
      StringBuilder sb = new StringBuilder();
      sb.append("package org.eclipse.osee.orcs.rest.internal.writers;\n");
      sb.append("import org.eclipse.osee.framework.core.data.ArtifactId;\n");
      sb.append("import org.eclipse.osee.framework.core.data.ArtifactTypeToken;\n");
      sb.append("import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;\n");
      sb.append("import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;\n");
      sb.append("import org.eclipse.osee.framework.core.enums.CoreRelationTypes;\n");
      sb.append("import org.eclipse.osee.orcs.rest.model.GenericReport;\n");
      sb.append("/**\n");
      sb.append(" * @author David W. Miller\n");
      sb.append(" */\n");
      sb.append("public class GenericReportCode {\n");
      sb.append("   public void testFollowFork(GenericReport report) {\n");
      sb.append("      report.level(\"Subsystem Requirements\", report.query().andIsOfType(CoreArtifactTypes.SubsystemRequirementMsWord)). //\n");
      sb.append("         column(\"Artifact ID\"). //\n");
      sb.append("         column(\"Requirement Name\", CoreAttributeTypes.Name); //\n");
      sb.append("      report.relationLevel(\"Related Code unit\", \"Code-Requirement\", \"SIDE_A\"). //\n");
      sb.append("         followFork(\"Supporting Info\", \"SIDE_B\"). //\n");
      sb.append("         followFork(\"Requirement Trace\", \"SIDE_B\"). //\n");
      sb.append("         column(\"Artifact ID\"). //\n");
      sb.append("         column(\"Code Unit\", CoreAttributeTypes.Name). //\n");
      sb.append("         column(\"File System Path\", CoreAttributeTypes.FileSystemPath); //\n");
      sb.append("   }\n");
      sb.append("}\n");
      return sb.toString();
   }

   private static final String FOLLOW_FORK_COMPREHENSIVE_TEMPLATE_CODE = getFollowForkComprehensiveTemplateCode();

   /**
    * Comprehensive template that exercises:
    * <ul>
    * <li>Level 0: Query-based level matching SubsystemRequirementMsWord</li>
    * <li>Level 1: relationLevel using "Requirement Trace" (follow) with forks for "Requirement Trace - Aircraft to
    * Simulation" and "Supporting Info"</li>
    * <li>The follow loads the lower-level requirement; forks additionally load sim requirements and supporting info
    * artifacts so getRelated can access them without extra DB calls</li>
    * </ul>
    */
   private static String getFollowForkComprehensiveTemplateCode() {
      StringBuilder sb = new StringBuilder();
      sb.append("package org.eclipse.osee.orcs.rest.internal.writers;\n");
      sb.append("import org.eclipse.osee.framework.core.data.ArtifactId;\n");
      sb.append("import org.eclipse.osee.framework.core.data.ArtifactTypeToken;\n");
      sb.append("import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;\n");
      sb.append("import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;\n");
      sb.append("import org.eclipse.osee.framework.core.enums.CoreRelationTypes;\n");
      sb.append("import org.eclipse.osee.orcs.rest.model.GenericReport;\n");
      sb.append("/**\n");
      sb.append(" * @author David W. Miller\n");
      sb.append(" */\n");
      sb.append("public class GenericReportCode {\n");
      sb.append("   public void testComprehensiveFollowFork(GenericReport report) {\n");
      sb.append("      report.level(\"Higher Level Req\", report.query().andIsOfType(CoreArtifactTypes.SubsystemRequirementMsWord)). //\n");
      sb.append("         column(\"Artifact ID\"). //\n");
      sb.append("         column(\"Requirement Name\", CoreAttributeTypes.Name); //\n");
      sb.append("      report.relationLevel(\"Lower Level Req\", \"Requirement Trace\", \"SIDE_B\"). //\n");
      sb.append("         followFork(\"Requirement Trace - Aircraft to Simulation\", \"SIDE_B\"). //\n");
      sb.append("         followFork(\"Supporting Info\", \"SIDE_B\"). //\n");
      sb.append("         column(\"Artifact ID\"). //\n");
      sb.append("         column(\"Lower Req Name\", CoreAttributeTypes.Name); //\n");
      sb.append("   }\n");
      sb.append("}\n");
      return sb.toString();
   }

   private static final String COLUMNS_AND_TYPE_TEMPLATE_CODE = getColumnsAndTypeTemplateCode();

   /**
    * Template that showcases attribute columns and the type() column.
    */
   private static String getColumnsAndTypeTemplateCode() {
      StringBuilder sb = new StringBuilder();
      sb.append("package org.eclipse.osee.orcs.rest.internal.writers;\n");
      sb.append("import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;\n");
      sb.append("import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;\n");
      sb.append("import org.eclipse.osee.framework.core.enums.CoreRelationTypes;\n");
      sb.append("import org.eclipse.osee.orcs.rest.model.GenericReport;\n");
      sb.append("public class GenericReportCode {\n");
      sb.append("   public void testColumnsAndType(GenericReport report) {\n");
      sb.append("      report.level(\"Requirements\", report.query().andIsOfType(CoreArtifactTypes.SubsystemRequirementMsWord)). //\n");
      sb.append("         column(\"Artifact ID\"). //\n");
      sb.append("         column(\"Name\", CoreAttributeTypes.Name). //\n");
      sb.append("         type(\"Artifact Type\"). //\n");
      sb.append("         column(\"Subsystem\", CoreAttributeTypes.Subsystem); //\n");
      sb.append("   }\n");
      sb.append("}\n");
      return sb.toString();
   }

   private static final String AND_ID_FOLLOW_CHAIN_TEMPLATE_CODE = getAndIdFollowChainTemplateCode();

   /**
    * Template that starts with query.andId on a specific artifact, then uses multiple query.follow levels to traverse
    * relations deeper.
    */
   private static String getAndIdFollowChainTemplateCode() {
      StringBuilder sb = new StringBuilder();
      sb.append("package org.eclipse.osee.orcs.rest.internal.writers;\n");
      sb.append("import org.eclipse.osee.framework.core.data.ArtifactId;\n");
      sb.append("import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;\n");
      sb.append("import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;\n");
      sb.append("import org.eclipse.osee.framework.core.enums.CoreRelationTypes;\n");
      sb.append("import org.eclipse.osee.orcs.rest.model.GenericReport;\n");
      sb.append("public class GenericReportCode {\n");
      sb.append("   public void testAndIdFollowChain(GenericReport report) {\n");
      sb.append("      report.level(\"Starting Artifact\", report.query().andId(ArtifactId.valueOf(%dL))). //\n");
      sb.append("         column(\"Artifact ID\"). //\n");
      sb.append("         column(\"Name\", CoreAttributeTypes.Name); //\n");
      sb.append("      report.level(\"Lower Level\", report.query().follow(CoreRelationTypes.RequirementTrace_LowerLevelRequirement)). //\n");
      sb.append("         column(\"Artifact ID\"). //\n");
      sb.append("         column(\"Lower Req Name\", CoreAttributeTypes.Name); //\n");
      sb.append("      report.level(\"Sim Level\", report.query().follow(CoreRelationTypes.RequirementTrace_SimRequirement)). //\n");
      sb.append("         column(\"Artifact ID\"). //\n");
      sb.append("         column(\"Sim Req Name\", CoreAttributeTypes.Name); //\n");
      sb.append("   }\n");
      sb.append("}\n");
      return sb.toString();
   }

   private static final String FILTER_TEMPLATE_CODE = getFilterTemplateCode();

   /**
    * Template that uses a filter on the Name column to exclude artifacts matching a regex pattern.
    */
   private static String getFilterTemplateCode() {
      StringBuilder sb = new StringBuilder();
      sb.append("package org.eclipse.osee.orcs.rest.internal.writers;\n");
      sb.append("import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;\n");
      sb.append("import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;\n");
      sb.append("import org.eclipse.osee.framework.core.enums.CoreRelationTypes;\n");
      sb.append("import org.eclipse.osee.orcs.rest.model.GenericReport;\n");
      sb.append("public class GenericReportCode {\n");
      sb.append("   public void testFilter(GenericReport report) {\n");
      sb.append("      report.level(\"Requirements\", report.query().andIsOfType(CoreArtifactTypes.SubsystemRequirementMsWord)). //\n");
      sb.append("         column(\"Artifact ID\"). //\n");
      sb.append("         column(\"Name\", CoreAttributeTypes.Name). //\n");
      sb.append("         filter(CoreAttributeTypes.Name, \".*Excluded.*\"); //\n");
      sb.append("   }\n");
      sb.append("}\n");
      return sb.toString();
   }
   // @formatter:on

   @BeforeClass
   public static void testSetup() {
      jaxRsApi = ServiceUtil.getOseeClient().jaxRsApi();
      commonArtifactEndpoint = ServiceUtil.getOseeClient().getArtifactEndpoint(COMMON);
      workingBranchArtifactEndpoint =
         ServiceUtil.getOseeClient().getArtifactEndpoint(DemoBranches.SAW_PL_Working_Branch);
      relationEndpoint = ServiceUtil.getOseeClient().getRelationEndpoint(DemoBranches.SAW_PL_Working_Branch);

      // Create the ReportTemplate artifact on COMMON branch with the JavaCode attribute
      templateArt = commonArtifactEndpoint.createArtifact(COMMON, CoreArtifactTypes.ReportTemplate,
         DefaultHierarchyRoot, "ReportEndpointTest Template");
      commonArtifactEndpoint.setSoleAttributeValue(COMMON, templateArt, CoreAttributeTypes.JavaCode, TEMPLATE_CODE);

      // Create a second ReportTemplate for followFork testing
      followForkTemplateArt = commonArtifactEndpoint.createArtifact(COMMON, CoreArtifactTypes.ReportTemplate,
         DefaultHierarchyRoot, "ReportEndpointTest FollowFork Template");
      commonArtifactEndpoint.setSoleAttributeValue(COMMON, followForkTemplateArt, CoreAttributeTypes.JavaCode,
         FOLLOW_FORK_TEMPLATE_CODE);

      // Create a third ReportTemplate for comprehensive follow/followFork testing
      followForkComprehensiveTemplateArt =
         commonArtifactEndpoint.createArtifact(COMMON, CoreArtifactTypes.ReportTemplate, DefaultHierarchyRoot,
            "ReportEndpointTest Comprehensive FollowFork Template");
      commonArtifactEndpoint.setSoleAttributeValue(COMMON, followForkComprehensiveTemplateArt,
         CoreAttributeTypes.JavaCode, FOLLOW_FORK_COMPREHENSIVE_TEMPLATE_CODE);

      // Create a SubsystemRequirementMsWord artifact on the working branch (higher-level requirement)
      subsystemReqArt = workingBranchArtifactEndpoint.createArtifact(DemoBranches.SAW_PL_Working_Branch,
         CoreArtifactTypes.SubsystemRequirementMsWord, DefaultHierarchyRoot, "ReportEndpointTest Subsystem Req");

      // Create a CodeUnit artifact on the working branch
      codeUnitArt = workingBranchArtifactEndpoint.createArtifact(DemoBranches.SAW_PL_Working_Branch,
         CoreArtifactTypes.CodeUnit, DefaultHierarchyRoot, "ReportEndpointTest Code Unit");
      workingBranchArtifactEndpoint.setSoleAttributeValue(DemoBranches.SAW_PL_Working_Branch, codeUnitArt,
         CoreAttributeTypes.FileSystemPath, "/src/test/TestFile.java");

      // Create a lower-level requirement (target of Requirement Trace from subsystemReqArt)
      lowerLevelReqArt = workingBranchArtifactEndpoint.createArtifact(DemoBranches.SAW_PL_Working_Branch,
         CoreArtifactTypes.SoftwareRequirementMsWord, DefaultHierarchyRoot, "ReportEndpointTest Lower Level Req");

      // Create a simulation requirement (target of Requirement Trace - Aircraft to Simulation from subsystemReqArt)
      simReqArt = workingBranchArtifactEndpoint.createArtifact(DemoBranches.SAW_PL_Working_Branch,
         CoreArtifactTypes.SoftwareRequirementMsWord, DefaultHierarchyRoot, "ReportEndpointTest Sim Req");

      // Create a supporting info artifact (target of Supporting Info from subsystemReqArt)
      supportingInfoArt = workingBranchArtifactEndpoint.createArtifact(DemoBranches.SAW_PL_Working_Branch,
         CoreArtifactTypes.TestInformationSheetMsWord, DefaultHierarchyRoot, "ReportEndpointTest Supporting Info");

      // Create the Code-Requirement relation (CodeUnit SIDE_A, Requirement SIDE_B)
      Response res =
         relationEndpoint.createRelationByType(codeUnitArt, subsystemReqArt, CoreRelationTypes.CodeRequirement);
      res.close();

      // Create Requirement Trace relation (subsystemReqArt SIDE_A = higher-level, lowerLevelReqArt SIDE_B = lower-level)
      res =
         relationEndpoint.createRelationByType(subsystemReqArt, lowerLevelReqArt, CoreRelationTypes.RequirementTrace);
      res.close();

      // Create Requirement Trace - Aircraft to Simulation (subsystemReqArt SIDE_A = Aircraft, simReqArt SIDE_B = Sim)
      res = relationEndpoint.createRelationByType(subsystemReqArt, simReqArt,
         CoreRelationTypes.RequirementsTraceAircraftToSim);
      res.close();

      // Create Supporting Info relation (subsystemReqArt SIDE_A = is supported by, supportingInfoArt SIDE_B = supporting info)
      res = relationEndpoint.createRelationByType(subsystemReqArt, supportingInfoArt, CoreRelationTypes.SupportingInfo);
      res.close();

      // Create a Requirement Trace from lowerLevelReqArt to simReqArt (for the andId follow chain test)
      res = relationEndpoint.createRelationByType(lowerLevelReqArt, simReqArt,
         CoreRelationTypes.RequirementsTraceAircraftToSim);
      res.close();

      // Create a second SubsystemRequirement that will be excluded by the filter test
      subsystemReqExcludedArt = workingBranchArtifactEndpoint.createArtifact(DemoBranches.SAW_PL_Working_Branch,
         CoreArtifactTypes.SubsystemRequirementMsWord, DefaultHierarchyRoot,
         "ReportEndpointTest Excluded Subsystem Req");

      // Create template for columns and type column test
      columnsAndTypeTemplateArt = commonArtifactEndpoint.createArtifact(COMMON, CoreArtifactTypes.ReportTemplate,
         DefaultHierarchyRoot, "ReportEndpointTest ColumnsType Template");
      commonArtifactEndpoint.setSoleAttributeValue(COMMON, columnsAndTypeTemplateArt, CoreAttributeTypes.JavaCode,
         COLUMNS_AND_TYPE_TEMPLATE_CODE);

      // Create template for andId follow chain test (inject the actual artifact ID)
      andIdFollowChainTemplateArt = commonArtifactEndpoint.createArtifact(COMMON, CoreArtifactTypes.ReportTemplate,
         DefaultHierarchyRoot, "ReportEndpointTest AndIdFollowChain Template");
      String andIdCode = String.format(AND_ID_FOLLOW_CHAIN_TEMPLATE_CODE, subsystemReqArt.getId());
      commonArtifactEndpoint.setSoleAttributeValue(COMMON, andIdFollowChainTemplateArt, CoreAttributeTypes.JavaCode,
         andIdCode);

      // Create template for filter test
      filterTemplateArt = commonArtifactEndpoint.createArtifact(COMMON, CoreArtifactTypes.ReportTemplate,
         DefaultHierarchyRoot, "ReportEndpointTest Filter Template");
      commonArtifactEndpoint.setSoleAttributeValue(COMMON, filterTemplateArt, CoreAttributeTypes.JavaCode,
         FILTER_TEMPLATE_CODE);
   }

   @AfterClass
   public static void testCleanup() {
      safeDelete(() -> workingBranchArtifactEndpoint.deleteArtifact(DemoBranches.SAW_PL_Working_Branch, codeUnitArt),
         codeUnitArt);
      safeDelete(
         () -> workingBranchArtifactEndpoint.deleteArtifact(DemoBranches.SAW_PL_Working_Branch, lowerLevelReqArt),
         lowerLevelReqArt);
      safeDelete(() -> workingBranchArtifactEndpoint.deleteArtifact(DemoBranches.SAW_PL_Working_Branch, simReqArt),
         simReqArt);
      safeDelete(
         () -> workingBranchArtifactEndpoint.deleteArtifact(DemoBranches.SAW_PL_Working_Branch, supportingInfoArt),
         supportingInfoArt);
      safeDelete(
         () -> workingBranchArtifactEndpoint.deleteArtifact(DemoBranches.SAW_PL_Working_Branch, subsystemReqArt),
         subsystemReqArt);
      safeDelete(() -> workingBranchArtifactEndpoint.deleteArtifact(DemoBranches.SAW_PL_Working_Branch,
         subsystemReqExcludedArt), subsystemReqExcludedArt);
      safeDelete(() -> commonArtifactEndpoint.deleteArtifact(COMMON, templateArt), templateArt);
      safeDelete(() -> commonArtifactEndpoint.deleteArtifact(COMMON, followForkTemplateArt), followForkTemplateArt);
      safeDelete(() -> commonArtifactEndpoint.deleteArtifact(COMMON, followForkComprehensiveTemplateArt),
         followForkComprehensiveTemplateArt);
      safeDelete(() -> commonArtifactEndpoint.deleteArtifact(COMMON, columnsAndTypeTemplateArt),
         columnsAndTypeTemplateArt);
      safeDelete(() -> commonArtifactEndpoint.deleteArtifact(COMMON, hierarchyTemplateArt), hierarchyTemplateArt);
      safeDelete(() -> commonArtifactEndpoint.deleteArtifact(COMMON, andIdFollowChainTemplateArt),
         andIdFollowChainTemplateArt);
      safeDelete(() -> commonArtifactEndpoint.deleteArtifact(COMMON, filterTemplateArt), filterTemplateArt);
   }

   private static void safeDelete(Runnable deleteAction, ArtifactToken artifact) {
      if (artifact != null) {
         try {
            deleteAction.run();
         } catch (Exception ex) {
            OseeLog.log(ReportEndpointTest.class, Level.SEVERE, "Cleanup failed for artifact " + artifact.getIdString(),
               ex);
         }
      }
   }

   private static String readResponseBody(Response response) {
      try {
         Object entity = response.getEntity();
         if (entity instanceof InputStream) {
            return Lib.inputStreamToString((InputStream) entity);
         } else {
            return entity.toString();
         }
      } catch (Exception ex) {
         throw new RuntimeException("Failed to read response entity", ex);
      }
   }

   @Test
   public void testReportEndpointWithRelationLevel() {
      String branchId = DemoBranches.SAW_PL_Working_Branch.getIdString();
      String viewId = ArtifactId.SENTINEL.getIdString();
      String templateId = templateArt.getIdString();

      assertNotNull("branchId should not be null", branchId);
      assertNotNull("viewId should not be null", viewId);
      assertNotNull("templateId should not be null", templateId);
      assertFalse("branchId should not be empty", branchId.isEmpty());
      assertFalse("viewId should not be empty", viewId.isEmpty());
      assertFalse("templateId should not be empty", templateId.isEmpty());

      String url = String.format("orcs/report/%s/view/%s/template/%s", branchId, viewId, templateId);

      Response response = jaxRsApi.newTarget(url).request(MediaType.APPLICATION_XML).get();
      try {
         assertEquals(Family.SUCCESSFUL, response.getStatusInfo().getFamily());

         String responseBody = readResponseBody(response);

         // Verify the response contains Excel XML content
         assertTrue("Response should contain XML worksheet data", responseBody.contains("<Worksheet"));

         // Verify the report contains our test data
         assertTrue("Response should contain the subsystem requirement name",
            responseBody.contains("ReportEndpointTest Subsystem Req"));
         assertTrue("Response should contain the code unit name",
            responseBody.contains("ReportEndpointTest Code Unit"));
         assertTrue("Response should contain the file system path", responseBody.contains("/src/test/TestFile.java"));

         // Verify the report structure headers
         assertTrue("Response should contain the level name 'Subsystem Requirements'",
            responseBody.contains("Subsystem Requirements"));
         assertTrue("Response should contain the level name 'Related Code unit'",
            responseBody.contains("Related Code unit"));
      } finally {
         response.close();
      }
   }

   @Test
   public void testReportEndpointWithInvalidTemplate() {
      String branchId = DemoBranches.SAW_PL_Working_Branch.getIdString();
      String viewId = ArtifactId.SENTINEL.getIdString();
      String invalidTemplateId = ArtifactId.SENTINEL.getIdString();

      String url = String.format("orcs/report/%s/view/%s/template/%s", branchId, viewId, invalidTemplateId);

      Response response = jaxRsApi.newTarget(url).request(MediaType.APPLICATION_XML).get();
      try {
         // The endpoint should still return a response (with error info in the debug sheet)
         assertEquals(Family.SUCCESSFUL, response.getStatusInfo().getFamily());
      } finally {
         response.close();
      }
   }

   @Test
   public void testReportEndpointAsyncKicksOff() {
      String branchId = DemoBranches.SAW_PL_Working_Branch.getIdString();
      String viewId = ArtifactId.SENTINEL.getIdString();
      String templateId = templateArt.getIdString();
      String email = "test@example.com";

      assertNotNull("branchId should not be null", branchId);
      assertNotNull("viewId should not be null", viewId);
      assertNotNull("templateId should not be null", templateId);
      assertFalse("branchId should not be empty", branchId.isEmpty());
      assertFalse("viewId should not be empty", viewId.isEmpty());
      assertFalse("templateId should not be empty", templateId.isEmpty());

      String url = String.format("orcs/report/%s/view/%s/template/%s/async/%s", branchId, viewId, templateId, email);

      Response response = jaxRsApi.newTarget(url).request(MediaType.APPLICATION_JSON).get();
      try {
         assertEquals(Family.SUCCESSFUL, response.getStatusInfo().getFamily());

         String responseBody = readResponseBody(response);

         assertTrue("Response should indicate report generation started",
            responseBody.contains("Report generation started"));
         assertTrue("Response should contain the file name", responseBody.contains("Generic_Trace_Report_"));
         assertTrue("Response should contain the branch", responseBody.contains(branchId));
         assertTrue("Response should contain the template", responseBody.contains(templateId));
         assertTrue("Response should contain the email recipient", responseBody.contains(email));
      } finally {
         response.close();
      }
   }

   @Test
   public void testReportEndpointWithFollowFork() {
      String branchId = DemoBranches.SAW_PL_Working_Branch.getIdString();
      String viewId = ArtifactId.SENTINEL.getIdString();
      String templateId = followForkTemplateArt.getIdString();

      assertNotNull("branchId should not be null", branchId);
      assertNotNull("viewId should not be null", viewId);
      assertNotNull("templateId should not be null", templateId);
      assertFalse("branchId should not be empty", branchId.isEmpty());
      assertFalse("viewId should not be empty", viewId.isEmpty());
      assertFalse("templateId should not be empty", templateId.isEmpty());

      String url = String.format("orcs/report/%s/view/%s/template/%s", branchId, viewId, templateId);

      Response response = jaxRsApi.newTarget(url).request(MediaType.APPLICATION_XML).get();
      try {
         assertEquals(Family.SUCCESSFUL, response.getStatusInfo().getFamily());

         String responseBody = readResponseBody(response);

         // Verify the response contains Excel XML content
         assertTrue("Response should contain XML worksheet data", responseBody.contains("<Worksheet"));

         // Verify the report structure parsed correctly (followFork didn't cause errors)
         assertTrue("Response should contain the level name 'Subsystem Requirements'",
            responseBody.contains("Subsystem Requirements"));
         assertTrue("Response should contain the level name 'Related Code unit'",
            responseBody.contains("Related Code unit"));

         // Verify data is present (followFork eagerly loads additional relations but doesn't prevent base results)
         assertTrue("Response should contain the subsystem requirement name",
            responseBody.contains("ReportEndpointTest Subsystem Req"));
         assertTrue("Response should contain the code unit name",
            responseBody.contains("ReportEndpointTest Code Unit"));
      } finally {
         response.close();
      }
   }

   /**
    * Comprehensive test that exercises follow and followFork together:
    * <ul>
    * <li>Level 0: SubsystemRequirementMsWord (subsystemReqArt)</li>
    * <li>Level 1: relationLevel "Requirement Trace" SIDE_B (lower-level req) with forks for "Requirement Trace -
    * Aircraft to Simulation" SIDE_B (sim req) and "Supporting Info" SIDE_B (supporting info)</li>
    * </ul>
    * The follow loads lowerLevelReqArt as the main relation traversal. The forks pre-load simReqArt and
    * supportingInfoArt so they are accessible via getRelated without additional DB calls. The report should show
    * lowerLevelReqArt data in the second level columns because it's the main relation target.
    */
   @Test
   public void testReportEndpointComprehensiveFollowFork() {
      String branchId = DemoBranches.SAW_PL_Working_Branch.getIdString();
      String viewId = ArtifactId.SENTINEL.getIdString();
      String templateId = followForkComprehensiveTemplateArt.getIdString();

      String url = String.format("orcs/report/%s/view/%s/template/%s", branchId, viewId, templateId);

      Response response = jaxRsApi.newTarget(url).request(MediaType.APPLICATION_XML).get();
      try {
         assertEquals("Report endpoint should return successful response", Family.SUCCESSFUL,
            response.getStatusInfo().getFamily());

         String responseBody = readResponseBody(response);

         // Verify the response is valid Excel XML
         assertTrue("Response should contain XML worksheet data", responseBody.contains("<Worksheet"));

         // Verify level headers
         assertTrue("Response should contain level name 'Higher Level Req'", responseBody.contains("Higher Level Req"));
         assertTrue("Response should contain level name 'Lower Level Req'", responseBody.contains("Lower Level Req"));

         // Verify column headers
         assertTrue("Response should contain column 'Requirement Name'", responseBody.contains("Requirement Name"));
         assertTrue("Response should contain column 'Lower Req Name'", responseBody.contains("Lower Req Name"));

         // Verify Level 0 data: the subsystem requirement artifact
         assertTrue("Response should contain the higher-level requirement name",
            responseBody.contains("ReportEndpointTest Subsystem Req"));

         // Verify Level 1 data: the lower-level requirement (main follow relation target)
         assertTrue("Response should contain the lower-level requirement name (via Requirement Trace follow)",
            responseBody.contains("ReportEndpointTest Lower Level Req"));

         // The forked relations (Aircraft-to-Sim, Supporting Info) pre-load artifacts into memory.
         // They appear in getAllRelations() so their targets should also show in the report data
         // (getArtsForLevel iterates all relations for the level).
         assertTrue(
            "Response should contain the sim requirement name (via Requirement Trace - Aircraft to Simulation fork)",
            responseBody.contains("ReportEndpointTest Sim Req"));
         assertTrue("Response should contain the supporting info name (via Supporting Info fork)",
            responseBody.contains("ReportEndpointTest Supporting Info"));
      } finally {
         response.close();
      }
   }

   /**
    * Tests attribute columns and the type() column. Verifies that the artifact type name appears in output alongside
    * attribute values and artifact IDs.
    */
   @Test
   public void testReportEndpointColumnsAndType() {
      String branchId = DemoBranches.SAW_PL_Working_Branch.getIdString();
      String viewId = ArtifactId.SENTINEL.getIdString();
      String templateId = columnsAndTypeTemplateArt.getIdString();

      String url = String.format("orcs/report/%s/view/%s/template/%s", branchId, viewId, templateId);

      Response response = jaxRsApi.newTarget(url).request(MediaType.APPLICATION_XML).get();
      try {
         assertEquals("Columns+Type report should return successful response", Family.SUCCESSFUL,
            response.getStatusInfo().getFamily());

         String responseBody = readResponseBody(response);

         assertTrue("Response should contain XML worksheet data", responseBody.contains("<Worksheet"));

         // Verify column headers
         assertTrue("Response should contain column 'Artifact ID'", responseBody.contains("Artifact ID"));
         assertTrue("Response should contain column 'Name'", responseBody.contains("Name"));
         assertTrue("Response should contain column 'Artifact Type'", responseBody.contains("Artifact Type"));

         // Verify data: artifact name and type name
         assertTrue("Response should contain the requirement name",
            responseBody.contains("ReportEndpointTest Subsystem Req"));
         assertTrue("Response should contain the artifact type 'Subsystem Requirement - MS Word'",
            responseBody.contains("Subsystem Requirement - MS Word"));
      } finally {
         response.close();
      }
   }

   /**
    * Tests the typical pattern of starting with query.andId on a specific artifact, then following relations through
    * multiple levels. Level 0 starts at subsystemReqArt by ID, level 1 follows RequirementTrace to lowerLevelReqArt,
    * level 2 follows RequirementsTraceAircraftToSim to simReqArt.
    */
   @Test
   public void testReportEndpointAndIdFollowChain() {
      String branchId = DemoBranches.SAW_PL_Working_Branch.getIdString();
      String viewId = ArtifactId.SENTINEL.getIdString();
      String templateId = andIdFollowChainTemplateArt.getIdString();

      String url = String.format("orcs/report/%s/view/%s/template/%s", branchId, viewId, templateId);

      Response response = jaxRsApi.newTarget(url).request(MediaType.APPLICATION_XML).get();
      try {
         assertEquals("AndId follow-chain report should return successful response", Family.SUCCESSFUL,
            response.getStatusInfo().getFamily());

         String responseBody = readResponseBody(response);

         assertTrue("Response should contain XML worksheet data", responseBody.contains("<Worksheet"));

         // Verify level headers
         assertTrue("Response should contain level 'Starting Artifact'", responseBody.contains("Starting Artifact"));
         assertTrue("Response should contain level 'Lower Level'", responseBody.contains("Lower Level"));
         assertTrue("Response should contain level 'Sim Level'", responseBody.contains("Sim Level"));

         // Verify Level 0: the starting artifact (subsystemReqArt found by ID)
         assertTrue("Response should contain the starting artifact name",
            responseBody.contains("ReportEndpointTest Subsystem Req"));

         // Verify Level 1: the lower-level requirement (via Requirement Trace)
         assertTrue("Response should contain the lower-level req (via follow RequirementTrace)",
            responseBody.contains("ReportEndpointTest Lower Level Req"));

         // Verify Level 2: the sim requirement (via Requirement Trace - Aircraft to Simulation from lowerLevelReqArt)
         assertTrue("Response should contain the sim req (via follow RequirementTrace_SimRequirement)",
            responseBody.contains("ReportEndpointTest Sim Req"));
      } finally {
         response.close();
      }
   }

   /**
    * Tests the filter feature. Creates two SubsystemRequirementMsWord artifacts — one with a normal name and one with
    * "Excluded" in the name. The template applies a Name filter with regex ".*Excluded.*". The report should contain
    * the normal artifact but not the excluded one.
    */
   @Test
   public void testReportEndpointFilter() {
      String branchId = DemoBranches.SAW_PL_Working_Branch.getIdString();
      String viewId = ArtifactId.SENTINEL.getIdString();
      String templateId = filterTemplateArt.getIdString();

      String url = String.format("orcs/report/%s/view/%s/template/%s", branchId, viewId, templateId);

      Response response = jaxRsApi.newTarget(url).request(MediaType.APPLICATION_XML).get();
      try {
         assertEquals("Filter report should return successful response", Family.SUCCESSFUL,
            response.getStatusInfo().getFamily());

         String responseBody = readResponseBody(response);

         assertTrue("Response should contain XML worksheet data", responseBody.contains("<Worksheet"));

         // Verify column headers
         assertTrue("Response should contain column 'Name'", responseBody.contains("Name"));

         // Verify the non-excluded artifact appears
         assertTrue("Response should contain the non-excluded requirement",
            responseBody.contains("ReportEndpointTest Subsystem Req"));

         // Verify the excluded artifact does NOT appear in data cells
         // (it may appear in DebugInfo sheet logs, so we check it's not in a Data cell context)
         // The filter regex ".*Excluded.*" should match "ReportEndpointTest Excluded Subsystem Req"
         assertFalse("Response should NOT contain the excluded requirement in report data",
            responseBody.contains(">ReportEndpointTest Excluded Subsystem Req<"));
      } finally {
         response.close();
      }
   }
}
