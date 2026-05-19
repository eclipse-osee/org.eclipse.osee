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
import static org.junit.Assert.assertTrue;
import java.io.InputStream;
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
   private static ArtifactToken subsystemReqArt;
   private static ArtifactToken codeUnitArt;

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

      // Create a SubsystemRequirementMsWord artifact on the working branch
      subsystemReqArt = workingBranchArtifactEndpoint.createArtifact(DemoBranches.SAW_PL_Working_Branch,
         CoreArtifactTypes.SubsystemRequirementMsWord, DefaultHierarchyRoot, "ReportEndpointTest Subsystem Req");

      // Create a CodeUnit artifact on the working branch
      codeUnitArt = workingBranchArtifactEndpoint.createArtifact(DemoBranches.SAW_PL_Working_Branch,
         CoreArtifactTypes.CodeUnit, DefaultHierarchyRoot, "ReportEndpointTest Code Unit");
      workingBranchArtifactEndpoint.setSoleAttributeValue(DemoBranches.SAW_PL_Working_Branch, codeUnitArt,
         CoreAttributeTypes.FileSystemPath, "/src/test/TestFile.java");

      // Create the Code-Requirement relation (CodeUnit is SIDE_A, Requirement is SIDE_B)
      Response res =
         relationEndpoint.createRelationByType(codeUnitArt, subsystemReqArt, CoreRelationTypes.CodeRequirement);
      res.close();
   }

   @AfterClass
   public static void testCleanup() {
      if (codeUnitArt != null) {
         workingBranchArtifactEndpoint.deleteArtifact(DemoBranches.SAW_PL_Working_Branch, codeUnitArt);
      }
      if (subsystemReqArt != null) {
         workingBranchArtifactEndpoint.deleteArtifact(DemoBranches.SAW_PL_Working_Branch, subsystemReqArt);
      }
      if (templateArt != null) {
         commonArtifactEndpoint.deleteArtifact(COMMON, templateArt);
      }
   }

   @Test
   public void testReportEndpointWithRelationLevel() {
      String branchId = DemoBranches.SAW_PL_Working_Branch.getIdString();
      String viewId = ArtifactId.SENTINEL.getIdString();
      String templateId = templateArt.getIdString();

      String url = String.format("orcs/report/%s/view/%s/template/%s", branchId, viewId, templateId);

      Response response = jaxRsApi.newTarget(url).request(MediaType.APPLICATION_XML).get();

      assertEquals(Family.SUCCESSFUL, response.getStatusInfo().getFamily());

      String responseBody = "";
      try {
         Object entity = response.getEntity();
         if (entity instanceof InputStream) {
            responseBody = Lib.inputStreamToString((InputStream) entity);
         } else {
            responseBody = entity.toString();
         }
      } catch (Exception ex) {
         throw new RuntimeException("Failed to read response entity", ex);
      }

      // Verify the response contains Excel XML content
      assertTrue("Response should contain XML worksheet data", responseBody.contains("<Worksheet"));

      // Verify the report contains our test data
      assertTrue("Response should contain the subsystem requirement name",
         responseBody.contains("ReportEndpointTest Subsystem Req"));
      assertTrue("Response should contain the code unit name",
         responseBody.contains("ReportEndpointTest Code Unit"));
      assertTrue("Response should contain the file system path",
         responseBody.contains("/src/test/TestFile.java"));

      // Verify the report structure headers
      assertTrue("Response should contain the level name 'Subsystem Requirements'",
         responseBody.contains("Subsystem Requirements"));
      assertTrue("Response should contain the level name 'Related Code unit'",
         responseBody.contains("Related Code unit"));
   }

   @Test
   public void testReportEndpointWithInvalidTemplate() {
      String branchId = DemoBranches.SAW_PL_Working_Branch.getIdString();
      String viewId = ArtifactId.SENTINEL.getIdString();
      String invalidTemplateId = "-1";

      String url = String.format("orcs/report/%s/view/%s/template/%s", branchId, viewId, invalidTemplateId);

      Response response = jaxRsApi.newTarget(url).request(MediaType.APPLICATION_XML).get();

      // The endpoint should still return a response (with error info in the debug sheet)
      assertEquals(Family.SUCCESSFUL, response.getStatusInfo().getFamily());
   }
}
