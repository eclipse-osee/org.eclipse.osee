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

package org.eclipse.osee.ats.ide.integration.tests.ats.report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.metrics.MetricsEndpointApi;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.resource.AbstractRestTest;
import org.eclipse.osee.ats.ide.util.AtsApiIde;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration test for {@link org.eclipse.osee.ats.rest.metrics.SoftwareReqVolatilityMetrics}
 * <p>
 * Tests the SoftwareReqVolatility endpoint using demo data. Uses SAW_Bld_2 which has the committed Requirements
 * workflow (SAW_Commited_Req_TeamWf) targeted to it in the Implement state.
 *
 * @author Stephen J. Molaro
 */
public class SoftwareReqVolatilityMetricsTest extends AbstractRestTest {

   private static AtsApiIde atsApi;
   private static MetricsEndpointApi metricsEp;

   @BeforeClass
   public static void setup() {
      atsApi = AtsApiService.get();
      metricsEp = atsApi.getServerEndpoints().getMetricsEp();
   }

   /**
    * Test that the SoftwareReqVolatility endpoint returns a successful 200 response with valid Excel XML structure.
    */
   @Test
   public void testSoftwareReqVolatilityReturnsValidResponse() throws Exception {
      String versionId = DemoArtifactToken.SAW_Bld_2.getIdString();

      Response response = metricsEp.softwareReqVolatility(versionId, null, null, true, false);
      assertNotNull("Response should not be null", response);
      assertEquals("Expected 200 OK response", 200, response.getStatus());

      String content = readResponseContent(response);
      assertNotNull("Response content should not be null", content);
      assertTrue("Response should contain Excel XML workbook declaration",
         content.contains("<?xml") || content.contains("<Workbook"));
      // The report MUST produce a sheet with data — SAW_Bld_2 has Requirements workflows targeted to it
      assertTrue("Report must contain the SRV worksheet with data", content.contains("SRV"));
      assertTrue("Report must contain column headers", content.contains("Action Id"));
   }

   /**
    * Test that the endpoint returns a successful response with countImpacts=true and produces a valid workbook.
    */
   @Test
   public void testSoftwareReqVolatilityWithImpacts() throws Exception {
      String versionId = DemoArtifactToken.SAW_Bld_2.getIdString();

      Response response = metricsEp.softwareReqVolatility(versionId, null, null, true, true);
      assertNotNull("Response should not be null", response);
      assertEquals("Expected 200 OK response", 200, response.getStatus());

      String content = readResponseContent(response);
      assertNotNull("Response content should not be null", content);
      assertTrue("Response should contain Excel XML workbook declaration",
         content.contains("<?xml") || content.contains("<Workbook"));
   }

   /**
    * Test that the endpoint returns a successful response with date filters that exclude all workflows.
    */
   @Test
   public void testSoftwareReqVolatilityWithDateFilterNoResults() throws Exception {
      String versionId = DemoArtifactToken.SAW_Bld_2.getIdString();

      // Use a date range far in the future so no workflows fall in it
      Date futureStart = new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000);
      Date futureEnd = new Date(System.currentTimeMillis() + 2 * 365L * 24 * 60 * 60 * 1000);

      Response response = metricsEp.softwareReqVolatility(versionId, futureStart, futureEnd, false, false);
      assertNotNull("Response should not be null", response);
      assertEquals("Expected 200 OK response", 200, response.getStatus());
   }

   /**
    * Test the endpoint via the REST URL to ensure it's correctly wired and returns proper headers.
    */
   @Test
   public void testSoftwareReqVolatilityViaUrl() throws Exception {
      String versionId = DemoArtifactToken.SAW_Bld_2.getIdString();
      String url = String.format("ats/metrics/SoftwareReqVolatility/%s?allTime=true&countImpacts=false", versionId);

      Response response = getResponse(url, MediaType.APPLICATION_OCTET_STREAM_TYPE);
      assertNotNull("Response should not be null", response);
      assertEquals("Expected 200 OK response", 200, response.getStatus());

      String fileName = response.getHeaderString("FileName");
      assertNotNull("FileName header should be present", fileName);
      assertTrue("FileName should start with SoftwareRequirementsVolatilityMetrics",
         fileName.startsWith("SoftwareRequirementsVolatilityMetrics"));
      assertTrue("FileName should end with .xml", fileName.endsWith(".xml"));
   }

   /**
    * Test that the report contains the SRV sheet and expected column headers when Requirements workflows are found. If
    * no qualifying workflows exist for the version, the report will be an empty workbook — this is valid behavior.
    */
   @Test
   public void testSoftwareReqVolatilityStructure() throws Exception {
      String versionId = DemoArtifactToken.SAW_Bld_2.getIdString();

      Response response = metricsEp.softwareReqVolatility(versionId, null, null, true, true);
      assertEquals("Expected 200 OK response", 200, response.getStatus());

      String content = readResponseContent(response);

      // The response is always a valid Excel XML workbook
      assertTrue("Response should be an XML document", content.contains("<?xml"));

      // If the report found qualifying workflows, it should have a sheet with column headers
      if (content.contains("SRV")) {
         assertTrue("Should contain Action Id column", content.contains("Action Id"));
         assertTrue("Should contain Worflow Id column", content.contains("Worflow Id"));
         assertTrue("Should contain Action Name column", content.contains("Action Name"));
         assertTrue("Should contain Program column", content.contains("Program"));
         assertTrue("Should contain Build column", content.contains("Build"));
         assertTrue("Should contain Creation Date column", content.contains("Creation Date"));
         assertTrue("Should contain Added (Software Reqs) column", content.contains("Added (Software Reqs)"));
         assertTrue("Should contain Safety column when countImpacts=true",
            content.contains("Safety Related Requirements"));
         assertTrue("Should contain Security column when countImpacts=true",
            content.contains("Security Related Requirements"));
      }
   }

   private String readResponseContent(Response response) throws Exception {
      Object entity = response.getEntity();
      if (entity instanceof InputStream) {
         InputStream inputStream = (InputStream) entity;
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         byte[] buffer = new byte[4096];
         int bytesRead;
         while ((bytesRead = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
         }
         return baos.toString("UTF-8");
      } else if (entity instanceof String) {
         return (String) entity;
      }
      return response.readEntity(String.class);
   }
}
