/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.config;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.core.client.server.HttpRequest.HttpMethod;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.junit.Assert;
import org.junit.Test;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Unit Test for {@link ProgramResource}
 *
 * @author Donald G. Dunne
 */
public class ProgramResourceTest extends AbstractConfigurationRestTest {

   @Test
   public void testAtsProgramsRestCall() throws Exception {
      JsonArray array = getAndCheck("/ats/program");
      Assert.assertEquals(2, array.size());
      JsonObject obj = getObjectNamed("SAW Program", array);
      Assert.assertNotNull("Did not find value SAW Program", obj);
      Assert.assertFalse(obj.has("ats.Description"));
   }

   @Test
   public void testAtsProgramsDetailsRestCall() throws Exception {
      JsonArray array = getAndCheck("/ats/program/details");
      Assert.assertEquals(2, array.size());
      JsonObject obj = getObjectNamed("SAW Program", array);
      Assert.assertNotNull("Did not find value SAW Program", obj);
      Assert.assertTrue(obj.has("ats.Description"));
   }

   @Test
   public void testAtsProgramRestCall() throws Exception {
      JsonArray array = getAndCheck("/ats/program/" + getSawProgram().getUuid());
      Assert.assertEquals(1, array.size());
      JsonObject obj = getObjectNamed("SAW Program", array);
      Assert.assertNotNull("Did not find value SAW Program", obj);
      Assert.assertFalse(obj.has("ats.Description"));
   }

   @Test
   public void testAtsProgramDetailsRestCall() throws Exception {
      JsonArray array = getAndCheck("/ats/program/" + getSawProgram().getUuid() + "/details");
      Assert.assertEquals(1, array.size());
      JsonObject obj = getObjectNamed("SAW Program", array);
      Assert.assertNotNull("Did not find value SAW Program", obj);
      Assert.assertTrue(obj.has("ats.Description"));
   }

   @Test
   public void testAtsProgramInsertionNegativeProgramIdRestCall() throws Exception {
      String result =
         doRequestString("/ats/program/" + Integer.toString(-5) + "/insertion", HttpMethod.GET, null,
            javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
      Assert.assertTrue(result.contains("SERVER_ERROR"));
   }

   @Test
   public void testAtsProgramInsertionBadProgramIdRestCall() throws Exception {
      String result =
         doRequestString("/ats/program/" + getSawTeam() + "/insertion", HttpMethod.GET, null,
            javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
      Assert.assertTrue(result.contains("SERVER_ERROR"));
   }

   @Test
   public void testAtsProgramInsertionCrudRestCalls() throws Exception {
      String insertion = "{\"name\":\"JAX Insertion\",\"uuid\":12345678}";
      String updatedInsertion = "{\"name\":\"Renamed Insertion\",\"uuid\":12345678}";
      testCreateInsertion(insertion);
      testGetInsertion();
      testUpdateInsertion(updatedInsertion);
      testDeleteInsertion();
   }

   @Test
   public void testAtsProgramInsertionFeatureNegativeInsertionRestCall() throws Exception {
      String result =
         doRequestString(
            "/ats/program/" + getSawProgram().getUuid() + "/insertion/" + Integer.toString(-5) + "/feature",
            HttpMethod.GET, null, javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
      Assert.assertTrue(result.contains("SERVER_ERROR"));
   }

   @Test
   public void testAtsProgramInsertionFeatureBadInsertionRestCall() throws Exception {
      String result =
         doRequestString("/ats/program/" + getSawProgram().getUuid() + "/insertion/" + getSawTeam() + "/feature",
            HttpMethod.GET, null, javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
      Assert.assertTrue(result.contains("SERVER_ERROR"));
   }

   @Test
   public void testAtsProgramInsertionFeatureCrudRestCalls() throws Exception {
      String insertion = "{\"name\":\"JAX Insertion\",\"uuid\":1234567800}";
      String insertionFeature = "{\"name\":\"JAX Insertion Feature\",\"uuid\":87654321}";
      String insertionFeatureUpdate = "{\"name\":\"Renamed Insertion Feature\",\"uuid\":87654321}";
      testCreateInsertionFeature(insertion, insertionFeature);
      testGetInsertionFeature();
      testUpdateInsertionFeature(insertionFeatureUpdate);
      testDeleteInsertionFeature();
   }

   private Artifact getSawProgram() {
      return ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.Program, "SAW Program",
         AtsUtilCore.getAtsBranch());
   }

   private Long getSawTeam() {
      return org.eclipse.osee.ats.client.demo.DemoArtifactToken.SAW_Requirements.getUuid();
   }

   private void testCreateInsertion(String insertion) throws Exception {
      String url = "/ats/program/" + getSawProgram().getUuid() + "/insertion";
      JsonArray array = postAndCheck(url, Entity.entity(insertion, MediaType.APPLICATION_JSON_TYPE));
      Assert.assertEquals(1, array.size());
      JsonObject obj = getObjectNamed("JAX Insertion", array);
      Assert.assertNotNull("Did not find value Jax Insertion", obj);
   }

   private void testGetInsertion() throws Exception {
      String url = "/ats/program/" + getSawProgram().getUuid() + "/insertion";
      JsonArray array = getAndCheck(url);
      JsonObject obj = getObjectNamed("JAX Insertion", array);
      Assert.assertNotNull("Did not find value Jax Insertion", obj);
   }

   private void testUpdateInsertion(String updatedInsertion) throws Exception {
      String url = "/ats/program/" + getSawProgram().getUuid() + "/insertion";
      JsonArray array = putAndCheck(url, Entity.entity(updatedInsertion, MediaType.APPLICATION_JSON_TYPE));
      Assert.assertEquals(1, array.size());
      JsonObject obj = getObjectNamed("Renamed Insertion", array);
      Assert.assertNotNull("Did not find value Renamed Insertion", obj);
   }

   private void testDeleteInsertion() throws Exception {
      String url = "/ats/program/" + getSawProgram().getUuid() + "/insertion/12345678";
      deleteAndCheck(url);
   }

   private void testCreateInsertionFeature(String insertion, String insertionFeature) throws Exception {
      String urlInsertion = "/ats/program/" + getSawProgram().getUuid() + "/insertion";
      String urlFeature = "/ats/program/" + getSawProgram().getUuid() + "/insertion/1234567800/feature";
      JsonArray setup = postAndCheck(urlInsertion, Entity.entity(insertion, MediaType.APPLICATION_JSON_TYPE));
      JsonArray array = postAndCheck(urlFeature, Entity.entity(insertionFeature, MediaType.APPLICATION_JSON_TYPE));
      Assert.assertEquals(1, array.size());
      JsonObject obj = getObjectNamed("JAX Insertion Feature", array);
      Assert.assertNotNull("Did not find value Jax Insertion Feature", obj);
   }

   private void testGetInsertionFeature() throws Exception {
      String url = "/ats/program/" + getSawProgram().getUuid() + "/insertion/1234567800/feature";
      JsonArray array = getAndCheck(url);
      Assert.assertEquals(1, array.size());
      JsonObject obj = getObjectNamed("JAX Insertion Feature", array);
      Assert.assertNotNull("Did not find value Jax Insertion Feature", obj);
   }

   private void testUpdateInsertionFeature(String insertion) throws Exception {
      String url = "/ats/program/" + getSawProgram().getUuid() + "/insertion/1234567800/feature";
      JsonArray array = putAndCheck(url, Entity.entity(insertion, MediaType.APPLICATION_JSON_TYPE));
      Assert.assertEquals(1, array.size());
      JsonObject obj = getObjectNamed("Renamed Insertion Feature", array);
      Assert.assertNotNull("Did not find value Renamed Insertion Feature", obj);
   }

   private void testDeleteInsertionFeature() throws Exception {
      String url = "/ats/program/" + getSawProgram().getUuid() + "/insertion/1234567800/feature/87654321";
      deleteAndCheck(url);
      // clean up insertion, too
      url = "/ats/program/" + getSawProgram().getUuid() + "/insertion/1234567800";
      deleteAndCheck(url);
   }
}
