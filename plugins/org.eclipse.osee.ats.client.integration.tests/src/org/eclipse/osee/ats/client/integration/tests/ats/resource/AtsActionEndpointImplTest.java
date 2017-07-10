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
package org.eclipse.osee.ats.client.integration.tests.ats.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.client.demo.DemoUtil;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.jaxrs.client.JaxRsClient;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test unit for {@link AtsActionEndpointImpl}
 *
 * @author Donald G. Dunne
 */
public class AtsActionEndpointImplTest extends AbstractRestTest {

   @Test
   public void testQueryTitle() throws Exception {
      queryAndConfirmCount("ats/action/query?Title=SAW", 18);
   }

   @Test
   public void testQueryPriority() throws Exception {
      queryAndConfirmCount("ats/action/query?Priority=1&Priority=3", 24);
   }

   @Test
   public void testQueryWorking() throws Exception {
      queryAndConfirmCount("ats/action/query?StateType=Working", 42);
   }

   @Test
   public void testQueryAssignee() throws Exception {
      queryAndConfirmCount("ats/action/query?Assignee=4444&Assignee=3333", 35);
   }

   @Test
   public void testQueryOriginator() throws Exception {
      queryAndConfirmCount("ats/action/query?Originator=3333", 44);
   }

   @Test
   public void testQueryTeam() throws Exception {
      queryAndConfirmCount("ats/action/query?Team=30013695", 3);
   }

   @Test
   public void testQueryTeamPriorityAndWorking() throws Exception {
      queryAndConfirmCount("ats/action/query?Team=30013695&Priority=3&Priority=2&StateType=Working", 2);
   }

   private void queryAndConfirmCount(String appendedUrl, int count) throws URISyntaxException, MalformedURLException, Exception {
      String url = String.format("%s/%s", getAppServerAddr(), appendedUrl);
      URI uri = new URL(url).toURI();
      JsonArray array = getAndCheckArray(uri);
      Assert.assertEquals(count, array.size());
   }

   @Test
   public void testQuerySingle() throws Exception {
      TeamWorkFlowArtifact sawCodeCommittedWf = DemoUtil.getSawCodeCommittedWf();
      String url = String.format("%s/ats/action/query?ats%%2EId=%s", getAppServerAddr(), sawCodeCommittedWf.getAtsId());
      URI uri = new URL(url).toURI();

      JsonArray array = getAndCheckArray(uri);
      Assert.assertEquals(1, array.size());
      JsonObject obj = (JsonObject) array.iterator().next();
      testAction(obj);
      String atsId = obj.get("AtsId").getAsString();
      Assert.assertEquals(atsId, sawCodeCommittedWf.getAtsId());

      url =
         String.format("%s/ats/action/query?1152921504606847877=%s", getAppServerAddr(), sawCodeCommittedWf.getAtsId());
      uri = new URL(url).toURI();

      array = getAndCheckArray(uri);
      Assert.assertEquals(1, array.size());
      obj = (JsonObject) array.iterator().next();
      testAction(obj);
      atsId = obj.get("AtsId").getAsString();
      Assert.assertEquals(atsId, sawCodeCommittedWf.getAtsId());

   }

   @Test
   public void testQueryMulti() throws Exception {
      String name = DemoUtil.SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW.replaceAll(" ", "%20");
      URI uri = UriBuilder.fromUri(getAppServerAddr()).path("/ats/action/query").queryParam("Name", name).build();

      JsonArray array = getAndCheckArray(uri);
      Assert.assertEquals(3, array.size());
      JsonObject obj = (JsonObject) array.iterator().next();
      testAction(obj);
      String atsId = obj.get("AtsId").getAsString();
      Assert.assertEquals(atsId, obj.get("ats.Id").getAsString());
   }

   @Test
   public void testGet() throws Exception {
      String results = getAndCheckStr("/ats/action");
      Assert.assertTrue(results.contains("Action Resource"));
   }

   @Test
   public void testAtsActionsRestCall() throws Exception {
      Collection<TeamWorkFlowArtifact> wfs = DemoUtil.getSawCommittedWfs();
      String atsIds = Collections.toString(",", AtsObjects.toAtsIds(wfs));
      JsonArray array = getAndCheckArray("/ats/action/" + atsIds);
      Assert.assertEquals(3, array.size());
      for (JsonElement elment : array) {
         JsonObject obj = (JsonObject) elment;
         testAction(obj);
      }
   }

   @Test
   public void testAtsActionsDetailsRestCall() throws Exception {
      Collection<TeamWorkFlowArtifact> wfs = DemoUtil.getSawCommittedWfs();
      String atsIds = Collections.toString(",", AtsObjects.toAtsIds(wfs));
      JsonArray array = getAndCheckArray("/ats/action/" + atsIds + "/details");
      Assert.assertEquals(3, array.size());
      JsonObject obj = (JsonObject) array.iterator().next();
      testAction(obj);
      String atsId = obj.get("AtsId").getAsString();
      Assert.assertEquals(atsId, obj.get("ats.Id").getAsString());
   }

   private void testAction(JsonObject obj) {
      Assert.assertEquals(DemoUtil.SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, obj.get("Name").getAsString());
      Assert.assertNotNull(obj.has("uuid"));
      Assert.assertNotNull(obj.has("AtsId"));
      Assert.assertEquals("/ats/ui/action/" + obj.get("AtsId").getAsString(), obj.get("actionLocation").getAsString());
   }

   @Test
   public void testAtsActionRestCall() throws Exception {
      Collection<TeamWorkFlowArtifact> wfs = DemoUtil.getSawCommittedWfs();
      TeamWorkFlowArtifact teamWf = wfs.iterator().next();
      JsonArray array = getAndCheckArray("/ats/action/" + teamWf.getAtsId());
      Assert.assertEquals(1, array.size());
      JsonObject obj = (JsonObject) array.iterator().next();
      testAction(obj);
   }

   @Test
   public void testAtsActionDetailsRestCall() throws Exception {
      Collection<TeamWorkFlowArtifact> wfs = DemoUtil.getSawCommittedWfs();
      TeamWorkFlowArtifact teamWf = wfs.iterator().next();
      JsonArray array = getAndCheckArray("/ats/action/" + teamWf.getAtsId() + "/details");
      Assert.assertEquals(1, array.size());
      JsonObject obj = (JsonObject) array.iterator().next();
      testAction(obj);
      String atsId = obj.get("AtsId").getAsString();
      Assert.assertEquals(atsId, obj.get("ats.Id").getAsString());
   }

   @Test
   public void testCreateAction() throws Exception {

      Form form = new Form();
      postAndValidateResponse("title is not valid", form);

      form.param("ats_title", getClass().getSimpleName());
      postAndValidateResponse("actionableItems is not valid", form);

      form.param("desc", "this is the description");
      postAndValidateResponse("actionableItems is not valid", form);

      form.param("actionableItems", "not valid ai name");
      postAndValidateResponse("actionableItems [not valid ai name] is not valid", form);

      form.asMap().remove("actionableItems");
      form.param("actionableItems", "SAW Code");
      postAndValidateResponse("userId is not valid", form);

      form.param("userId", "asdf");
      postAndValidateResponse("userId [asdf] is not valid", form);

      form.asMap().remove("userId");
      form.param("userId", "3333");
      postAndValidateResponse("changeType is not valid", form);

      form.param("changeType", "invalid change type");
      postAndValidateResponse("changeType [invalid change type] is not valid", form);

      form.asMap().remove("changeType");
      form.param("changeType", "Improvement");
      postAndValidateResponse("priority is not valid", form);

      form.param("priority", "invalid priority");
      postAndValidateResponse("priority [invalid priority] is not valid", form);

      form.asMap().remove("priority");
      form.param("priority", "3");
      Response response = post(form);

      Assert.assertEquals(Status.SEE_OTHER.getStatusCode(), response.getStatus());
      String urlStr = response.getLocation().toString();
      URL url = new URL(urlStr);
      String path = url.getPath();
      Assert.assertTrue(String.format("Invalid url [%s]", url), path.contains("/ats/ui/action/ATS"));
      String atsId = path.replaceFirst("^.*/", "");

      TeamWorkFlowArtifact teamArt =
         (TeamWorkFlowArtifact) ArtifactQuery.getArtifactFromAttribute(AtsAttributeTypes.AtsId, atsId,
            AtsClientService.get().getAtsBranch());
      Assert.assertNotNull(teamArt);

      // Cleanup test
      ((Artifact) teamArt.getParentAction().getStoreObject()).deleteAndPersist();
      teamArt.deleteAndPersist();
   }

   private void postAndValidateResponse(String errorMessage, Form form) throws IOException {
      Response response = post(form);
      validateResponse(response, errorMessage);
   }

   private Response post(Form form) {
      String appServer = OseeClientProperties.getOseeApplicationServer();
      URI uri = UriBuilder.fromUri(appServer).path("/ats/action").build();
      Response response = JaxRsClient.newBuilder().followRedirects(false).build().target(uri).request(
         MediaType.APPLICATION_JSON_TYPE).post(Entity.form(form));
      return response;
   }

   private void validateResponse(Response response, String errorMessage) throws IOException {
      Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
      Assert.assertEquals(errorMessage, Lib.inputStreamToString((InputStream) response.getEntity()));
   }
}
