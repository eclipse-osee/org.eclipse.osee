/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.client.integration.tests.integration.orcs.rest;

import javax.ws.rs.core.Response;
import org.eclipse.osee.client.demo.DemoChoice;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.synchronization.api.SynchronizationEndpoint;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for the synchronization endpoint.
 *
 * @author Loren K. Ashley
 */

public class SynchronizationEndpointTest {

   private static OseeClient oseeClient;
   private static SynchronizationEndpoint synchronizationEndpoint;

   @BeforeClass
   public static void testSetup() {
      SynchronizationEndpointTest.oseeClient = OsgiUtil.getService(DemoChoice.class, OseeClient.class);

      SynchronizationEndpointTest.synchronizationEndpoint =
         SynchronizationEndpointTest.oseeClient.getSynchronizationEndpoint();

      Assert.assertNotNull(SynchronizationEndpointTest.synchronizationEndpoint);
   }

   @Test
   public void getByBranchIdArtifactIdOk() {
      BranchId branchId = DemoBranches.SAW_PL_Working_Branch;
      ArtifactId artifactId = CoreArtifactTokens.DefaultHierarchyRoot;
      String synchronizationArtifactType = "reqif";

      Response response = SynchronizationEndpointTest.synchronizationEndpoint.getSynchronizationArtifact(branchId,
         artifactId, synchronizationArtifactType);

      Assert.assertNotNull(response);

      int statusCode = response.getStatus();

      Assert.assertEquals(Response.Status.OK.getStatusCode(), statusCode);
   }

   @Test
   public void getByBranchIdArtifactIdKoBadArtifactType() {
      BranchId branchId = DemoBranches.SAW_PL_Working_Branch;
      ArtifactId artifactId = CoreArtifactTokens.DefaultHierarchyRoot;
      String synchronizationArtifactType = "ZooCreatures";
      boolean exceptionCought = false;

      try {
         Response response = SynchronizationEndpointTest.synchronizationEndpoint.getSynchronizationArtifact(branchId,
            artifactId, synchronizationArtifactType);
      } catch (OseeCoreException exception) {
         exceptionCought = true;

         String message = exception.getMessage();
         System.out.println("******************************************");
         System.out.println(message);
         System.out.println("******************************************");
         Assert.assertTrue(message.contains("HTTP Reason: Bad Request."));
         Assert.assertTrue(message.contains("Request for a Synchronization Artifact with an unknown artifact type."));
      }

      Assert.assertTrue(exceptionCought);
   }

   @Test
   public void getByRootsOk() {
      String branchId = DemoBranches.SAW_PL_Working_Branch.getIdString();
      String artifactId = CoreArtifactTokens.DefaultHierarchyRoot.getIdString();
      String roots = branchId + ":" + artifactId;
      String synchronizationArtifactType = "reqif";

      Response response = SynchronizationEndpointTest.synchronizationEndpoint.getSynchronizationArtifact(roots,
         synchronizationArtifactType);

      Assert.assertNotNull(response);

      int statusCode = response.getStatus();

      Assert.assertEquals(Response.Status.OK.getStatusCode(), statusCode);
   }

   @Test
   public void getByRootsKoBadRootsNonDigitFirstBranch() {
      String roots = "10a:1,2,3;11:4,5,6";
      String synchronizationArtifactType = "ReqIF";
      boolean exceptionCought = false;

      try {
         Response response = SynchronizationEndpointTest.synchronizationEndpoint.getSynchronizationArtifact(roots,
            synchronizationArtifactType);
      } catch (OseeCoreException exception) {
         exceptionCought = true;

         String message = exception.getMessage();

         Assert.assertTrue(message.contains("HTTP Reason: Bad Request."));
         Assert.assertTrue(message.contains("ERROR: \"roots\" parameter is invalid."));
      }

      Assert.assertTrue(exceptionCought);

   }

   @Test
   public void getByRootsKoBadRootsNonDigitSecondBranch() {
      String roots = "10:1,2,3;11z:4,5,6";
      String synchronizationArtifactType = "ReqIF";
      boolean exceptionCought = false;

      try {
         Response response = SynchronizationEndpointTest.synchronizationEndpoint.getSynchronizationArtifact(roots,
            synchronizationArtifactType);
      } catch (OseeCoreException exception) {
         exceptionCought = true;

         String message = exception.getMessage();

         Assert.assertTrue(message.contains("HTTP Reason: Bad Request."));
         Assert.assertTrue(message.contains("ERROR: \"roots\" parameter is invalid."));
      }

      Assert.assertTrue(exceptionCought);

   }

   @Test
   public void getByRootsKoBadRootsNonDigitFirstArtifact() {
      String roots = "10:1a,2,3;11:4,5,6";
      String synchronizationArtifactType = "ReqIF";
      boolean exceptionCought = false;

      try {
         Response response = SynchronizationEndpointTest.synchronizationEndpoint.getSynchronizationArtifact(roots,
            synchronizationArtifactType);
      } catch (OseeCoreException exception) {
         exceptionCought = true;

         String message = exception.getMessage();

         Assert.assertTrue(message.contains("HTTP Reason: Bad Request."));
         Assert.assertTrue(message.contains("ERROR: \"roots\" parameter is invalid."));
      }

      Assert.assertTrue(exceptionCought);

   }

   @Test
   public void getByRootsKoBadRootsNonDigitLastArtifact() {
      String roots = "10:1,2,3;11:4,5,6a";
      String synchronizationArtifactType = "ReqIF";
      boolean exceptionCought = false;

      try {
         Response response = SynchronizationEndpointTest.synchronizationEndpoint.getSynchronizationArtifact(roots,
            synchronizationArtifactType);
      } catch (OseeCoreException exception) {
         exceptionCought = true;

         String message = exception.getMessage();

         Assert.assertTrue(message.contains("HTTP Reason: Bad Request."));
         Assert.assertTrue(message.contains("ERROR: \"roots\" parameter is invalid."));
      }

      Assert.assertTrue(exceptionCought);

   }

   @Test
   public void getByRootsKoBadRootsBranchDelimiterOutOfPlace() {
      String roots = "10:1:2,3;11:4,5,6";
      String synchronizationArtifactType = "ReqIF";
      boolean exceptionCought = false;

      try {
         Response response = SynchronizationEndpointTest.synchronizationEndpoint.getSynchronizationArtifact(roots,
            synchronizationArtifactType);
      } catch (OseeCoreException exception) {
         exceptionCought = true;

         String message = exception.getMessage();

         Assert.assertTrue(message.contains("HTTP Reason: Bad Request."));
         Assert.assertTrue(message.contains("ERROR: \"roots\" parameter is invalid."));
      }

      Assert.assertTrue(exceptionCought);

   }

   @Test
   public void getByRootsKoBadRootsArtifactDelimiterOutOfPlace() {
      String roots = "10:1,2,3;11,4,5,6";
      String synchronizationArtifactType = "ReqIF";
      boolean exceptionCought = false;

      try {
         Response response = SynchronizationEndpointTest.synchronizationEndpoint.getSynchronizationArtifact(roots,
            synchronizationArtifactType);
      } catch (OseeCoreException exception) {
         exceptionCought = true;

         String message = exception.getMessage();

         Assert.assertTrue(message.contains("HTTP Reason: Bad Request."));
         Assert.assertTrue(message.contains("ERROR: \"roots\" parameter is invalid."));
      }

      Assert.assertTrue(exceptionCought);

   }

   @Test
   public void getByRootsKoBadRootsSpecificationDelimiterOutOfPlace() {
      String roots = "10:1,2,3;11;4,5,6";
      String synchronizationArtifactType = "ReqIF";
      boolean exceptionCought = false;

      try {
         Response response = SynchronizationEndpointTest.synchronizationEndpoint.getSynchronizationArtifact(roots,
            synchronizationArtifactType);
      } catch (OseeCoreException exception) {
         exceptionCought = true;

         String message = exception.getMessage();

         Assert.assertTrue(message.contains("HTTP Reason: Bad Request."));
         Assert.assertTrue(message.contains("ERROR: \"roots\" parameter is invalid."));
      }

      Assert.assertTrue(exceptionCought);

   }

}

/* EOF */
