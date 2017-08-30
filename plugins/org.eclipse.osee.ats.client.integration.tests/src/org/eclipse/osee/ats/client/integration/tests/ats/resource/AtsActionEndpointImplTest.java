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
import java.util.Arrays;
import java.util.Date;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.AtsActionEndpointApi;
import org.eclipse.osee.ats.api.workflow.Attribute;
import org.eclipse.osee.ats.api.workflow.AttributeKey;
import org.eclipse.osee.ats.api.workflow.WorkItemWriterOptions;
import org.eclipse.osee.ats.client.demo.DemoUtil;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.demo.api.DemoWorkflowTitles;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
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

   private TeamWorkFlowArtifact teamWfArt;

   @Test
   public void testQueryTitle() throws Exception {
      queryAndConfirmCount("ats/action/query?Title=SAW", 21);
   }

   @Test
   public void testQueryPriority() throws Exception {
      queryAndConfirmCount("ats/action/query?Priority=1&Priority=3", 24);
   }

   @Test
   public void testQueryWorking() throws Exception {
      queryAndConfirmCount("ats/action/query?StateType=Working", 52);
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

   public TeamWorkFlowArtifact getCodeWorkflow() {
      if (teamWfArt == null) {
         teamWfArt = DemoUtil.getSawCodeCommittedWf();
         teamWfArt.setSoleAttributeValue(AtsAttributeTypes.LegacyPcrId, "PCR8125");
         teamWfArt.persist(getClass().getSimpleName());
      }
      return teamWfArt;
   }

   @Test
   public void testActionStateById() throws Exception {
      String results =
         getAndCheck("ats/action/" + getCodeWorkflow().getIdString() + "/state", MediaType.APPLICATION_JSON_TYPE);
      Assert.assertTrue(results, results.contains(getCodeWorkflow().getIdString()));
   }

   @Test
   public void testActionStateByAtsId() throws Exception {
      String results =
         getAndCheck("ats/action/" + getCodeWorkflow().getAtsId() + "/state", MediaType.APPLICATION_JSON_TYPE);
      Assert.assertTrue(results, results.contains(getCodeWorkflow().getIdString()));
   }

   @Test
   public void testActionStateByLegacyId() throws Exception {
      getCodeWorkflow();
      String results = getAndCheck("ats/action/PCR8125/legacy/state", MediaType.APPLICATION_JSON_TYPE);
      Assert.assertTrue(results, results.contains(getCodeWorkflow().getIdString()));
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
      String name = DemoWorkflowTitles.SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW.replaceAll(" ", "%20");
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
      String url = "/ats/action/" + DemoUtil.getSawAtsIdsStr();
      JsonArray array = getAndCheckArray(url);
      Assert.assertEquals(3, array.size());
      for (JsonElement elment : array) {
         JsonObject obj = (JsonObject) elment;
         testAction(obj);
      }
   }

   @Test
   public void testAtsActionsDetailsRestCall() throws Exception {
      String url = "/ats/action/" + DemoUtil.getSawCodeCommittedWf().getIdString() + "/details";
      JsonArray array = getAndCheckArray(url);
      Assert.assertEquals(1, array.size());
      JsonObject obj = (JsonObject) array.iterator().next();
      testAction(obj);
      String atsId = obj.get("AtsId").getAsString();
      Assert.assertEquals(atsId, obj.get("ats.Id").getAsString());
      Assert.assertFalse(Strings.isNumeric(obj.get("ats.Created Date").getAsString()));
   }

   @Test
   public void testAtsActionsFieldsAsIdsAndDatesAsLong() throws Exception {
      String url =
         "/ats/action/" + DemoUtil.getSawCodeCommittedWf().getIdString() + "/details?" + WorkItemWriterOptions.FieldsAsIds.name() + "=true&" + WorkItemWriterOptions.DatesAsLong.name() + "=true";
      JsonArray array = getAndCheckArray(url);
      Assert.assertEquals(1, array.size());
      JsonObject obj = (JsonObject) array.iterator().next();

      // FieldsAsIds should replace attr type names with id as the field
      Assert.assertFalse(obj.has(AtsAttributeTypes.CreatedDate.getName()));
      String teamDefByAttrTypeId = obj.get(AtsAttributeTypes.CreatedDate.getIdString()).getAsString();
      Assert.assertTrue(Strings.isNumeric(teamDefByAttrTypeId));

      // DatesAsLong should replace date value with long time value
      String dateValue = obj.get(AtsAttributeTypes.CreatedDate.getIdString()).getAsString();
      Assert.assertTrue(Strings.isNumeric(dateValue));
   }

   private void testAction(JsonObject obj) {
      Assert.assertEquals(DemoWorkflowTitles.SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW,
         obj.get("Name").getAsString());
      Assert.assertNotNull(obj.has("uuid"));
      Assert.assertNotNull(obj.has("AtsId"));
      Assert.assertEquals("/ats/ui/action/" + obj.get("AtsId").getAsString(), obj.get("actionLocation").getAsString());
   }

   @Test
   public void testAtsActionRestCall() throws Exception {
      JsonArray array = getAndCheckArray("/ats/action/" + DemoUtil.getSawCodeCommittedWf().getAtsId());
      Assert.assertEquals(1, array.size());
      JsonObject obj = (JsonObject) array.iterator().next();
      testAction(obj);
   }

   @Test
   public void testAtsActionDetailsRestCall() throws Exception {
      JsonArray array = getAndCheckArray("/ats/action/" + DemoUtil.getSawCodeCommittedWf().getAtsId() + "/details");
      Assert.assertEquals(1, array.size());
      JsonObject obj = (JsonObject) array.iterator().next();
      testAction(obj);
      String atsId = obj.get("AtsId").getAsString();
      Assert.assertEquals(atsId, obj.get("ats.Id").getAsString());
   }

   @Test
   public void testGetActionAttributeByType() {
      testAttributeTypeMatchesRestAttributes(AtsAttributeTypes.State);
   }

   @SuppressWarnings("deprecation")
   private TeamWorkFlowArtifact testAttributeTypeMatchesRestAttributes(AttributeTypeId attrType) {
      TeamWorkFlowArtifact teamWf = DemoUtil.getSawCodeCommittedWf();
      AtsActionEndpointApi actionEp = AtsClientService.getActionEndpoint();
      Attribute attribute = actionEp.getActionAttributeByType(teamWf.getIdString(), attrType.getIdString());
      Assert.assertEquals(teamWf.getIdString(), attribute.getArtId().getIdString());
      Assert.assertEquals(attrType.getIdString(), attribute.getAttrTypeId().getIdString());
      Assert.assertEquals(teamWf.getAttributeCount(attrType), attribute.getValues().size());

      for (org.eclipse.osee.framework.skynet.core.artifact.Attribute<Object> attr : teamWf.getAttributes(attrType)) {
         Assert.assertTrue(attribute.getValues().values().contains(String.valueOf(attr.getValue())));
      }
      return teamWf;
   }

   @Test
   public void testSetActionStringAttributeByType() {
      AtsActionEndpointApi actionEp = AtsClientService.getActionEndpoint();

      TeamWorkFlowArtifact teamWf = testAttributeTypeMatchesRestAttributes(CoreAttributeTypes.StaticId);
      Assert.assertEquals(0,
         AtsClientService.get().getAttributeResolver().getAttributesToStringList((IAtsObject) teamWf,
            CoreAttributeTypes.StaticId).size());

      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      changes.addAttribute((IAtsObject) teamWf, CoreAttributeTypes.StaticId, "asdf");
      changes.addAttribute((IAtsObject) teamWf, CoreAttributeTypes.StaticId, "qwer");
      changes.addAttribute((IAtsObject) teamWf, CoreAttributeTypes.StaticId, "zxcv");
      changes.execute();

      teamWf = testAttributeTypeMatchesRestAttributes(CoreAttributeTypes.StaticId);
      Assert.assertEquals(3,
         AtsClientService.get().getAttributeResolver().getAttributesToStringList((IAtsObject) teamWf,
            CoreAttributeTypes.StaticId).size());

      actionEp.setActionAttributeByType(teamWf.getIdString(), CoreAttributeTypes.StaticId.getIdString(),
         Arrays.asList("asdf", "zxcv"));

      teamWf.reloadAttributesAndRelations();
      teamWf = testAttributeTypeMatchesRestAttributes(CoreAttributeTypes.StaticId);
      Assert.assertEquals(2,
         AtsClientService.get().getAttributeResolver().getAttributesToStringList((IAtsObject) teamWf,
            CoreAttributeTypes.StaticId).size());

      // test that search by guid or atsId work as well
      Attribute attribute =
         actionEp.getActionAttributeByType(teamWf.getGuid(), CoreAttributeTypes.StaticId.getIdString());
      Assert.assertEquals(2, attribute.getValues().size());
      attribute = actionEp.getActionAttributeByType(teamWf.getAtsId(), CoreAttributeTypes.StaticId.getIdString());
      Assert.assertEquals(2, attribute.getValues().size());

   }

   @Test
   public void testSetActionFloatAttributeByType() {
      AtsActionEndpointApi actionEp = AtsClientService.getActionEndpoint();

      TeamWorkFlowArtifact teamWf = DemoUtil.getSawCodeCommittedWf();

      Assert.assertEquals(0,
         AtsClientService.get().getAttributeResolver().getAttributesToStringList((IAtsObject) teamWf,
            AtsAttributeTypes.EstimatedHours).size());

      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      changes.setSoleAttributeValue((IAtsObject) teamWf, AtsAttributeTypes.EstimatedHours, 3.5);
      changes.execute();

      teamWf = testAttributeTypeMatchesRestAttributes(AtsAttributeTypes.EstimatedHours);
      Assert.assertEquals(1,
         AtsClientService.get().getAttributeResolver().getAttributesToStringList((IAtsObject) teamWf,
            AtsAttributeTypes.EstimatedHours).size());

      actionEp.setActionAttributeByType(teamWf.getIdString(), AtsAttributeTypes.EstimatedHours.getIdString(),
         Arrays.asList("4.5"));

      teamWf.reloadAttributesAndRelations();
      teamWf = testAttributeTypeMatchesRestAttributes(AtsAttributeTypes.EstimatedHours);
      Assert.assertEquals((Double) 4.5, AtsClientService.get().getAttributeResolver().getSoleAttributeValue(
         (IAtsObject) teamWf, AtsAttributeTypes.EstimatedHours, 1.0));
   }

   @Test
   public void testSetActionIntegerAttributeByType() {
      AtsActionEndpointApi actionEp = AtsClientService.getActionEndpoint();

      TeamWorkFlowArtifact teamWf = DemoUtil.getSawCodeCommittedWf();

      Assert.assertEquals(0,
         AtsClientService.get().getAttributeResolver().getAttributesToStringList((IAtsObject) teamWf,
            AtsAttributeTypes.PercentRework).size());

      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      changes.setSoleAttributeValue((IAtsObject) teamWf, AtsAttributeTypes.PercentRework, 3);
      changes.execute();

      teamWf = testAttributeTypeMatchesRestAttributes(AtsAttributeTypes.PercentRework);
      Assert.assertEquals((Integer) 3, AtsClientService.get().getAttributeResolver().getSoleAttributeValue(
         (IAtsObject) teamWf, AtsAttributeTypes.PercentRework, 4));

      actionEp.setActionAttributeByType(teamWf.getIdString(), AtsAttributeTypes.PercentRework.getIdString(),
         Arrays.asList("4"));

      teamWf.reloadAttributesAndRelations();
      teamWf = testAttributeTypeMatchesRestAttributes(AtsAttributeTypes.PercentRework);
      Assert.assertEquals((Integer) 4, AtsClientService.get().getAttributeResolver().getSoleAttributeValue(
         (IAtsObject) teamWf, AtsAttributeTypes.PercentRework, 1.0));
   }

   @Test
   public void testSetActionDateAttributeByType() {
      AtsActionEndpointApi actionEp = AtsClientService.getActionEndpoint();

      TeamWorkFlowArtifact teamWf = DemoUtil.getSawCodeCommittedWf();

      Assert.assertEquals((Date) null, AtsClientService.get().getAttributeResolver().getSoleAttributeValue(
         (IAtsObject) teamWf, AtsAttributeTypes.NeedBy, null));

      Date date = new Date();

      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      changes.setSoleAttributeValue((IAtsObject) teamWf, AtsAttributeTypes.NeedBy, date);
      changes.execute();

      teamWf = testAttributeTypeMatchesRestAttributes(AtsAttributeTypes.NeedBy);
      Assert.assertEquals(1,
         AtsClientService.get().getAttributeResolver().getAttributesToStringList((IAtsObject) teamWf,
            AtsAttributeTypes.NeedBy).size());

      actionEp.setActionAttributeByType(teamWf.getIdString(), AtsAttributeTypes.NeedBy.getIdString(),
         Arrays.asList("446579845"));

      teamWf.reloadAttributesAndRelations();
      teamWf = testAttributeTypeMatchesRestAttributes(AtsAttributeTypes.NeedBy);
      Date date2 = (Date) AtsClientService.get().getAttributeResolver().getSoleAttributeValue((IAtsObject) teamWf,
         AtsAttributeTypes.NeedBy, null);
      Long dateTime = date2.getTime();
      Assert.assertEquals(446579845, dateTime.intValue());
   }

   @Test
   public void testSetActionBooleanAttributeByType() {
      AtsActionEndpointApi actionEp = AtsClientService.getActionEndpoint();

      TeamWorkFlowArtifact teamWf = DemoUtil.getSawCodeCommittedWf();

      Assert.assertEquals(false, AtsClientService.get().getAttributeResolver().getSoleAttributeValue(
         (IAtsObject) teamWf, AtsAttributeTypes.ValidationRequired, false));

      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName());
      changes.setSoleAttributeValue((IAtsObject) teamWf, AtsAttributeTypes.ValidationRequired, true);
      changes.execute();

      teamWf = testAttributeTypeMatchesRestAttributes(AtsAttributeTypes.ValidationRequired);
      Assert.assertEquals(true, AtsClientService.get().getAttributeResolver().getSoleAttributeValue((IAtsObject) teamWf,
         AtsAttributeTypes.ValidationRequired, false));

      actionEp.setActionAttributeByType(teamWf.getIdString(), AtsAttributeTypes.ValidationRequired.getIdString(),
         Arrays.asList("false"));

      teamWf.reloadAttributesAndRelations();
      teamWf = testAttributeTypeMatchesRestAttributes(AtsAttributeTypes.ValidationRequired);
      Assert.assertEquals(false, AtsClientService.get().getAttributeResolver().getSoleAttributeValue(
         (IAtsObject) teamWf, AtsAttributeTypes.ValidationRequired, true));
   }

   @Test
   public void testSetActionTitleByKey() {
      AtsActionEndpointApi actionEp = AtsClientService.getActionEndpoint();
      TeamWorkFlowArtifact teamWf = DemoUtil.getSawCodeCommittedWf();

      String origTitle = teamWf.getName();
      testSetActionByKey("", origTitle + "-1", AtsAttributeTypes.Title, AttributeKey.Title);

      actionEp.setActionAttributeByType(teamWf.getIdString(), AttributeKey.Title.name(), Arrays.asList(origTitle));
      teamWf.reloadAttributesAndRelations();
      Assert.assertEquals(origTitle, teamWf.getName());

      teamWf = testAttributeTypeMatchesRestAttributes(CoreAttributeTypes.Name);
   }

   @Test
   public void testSetActionPriorityByKey() {
      testSetActionByKey("", "5", AtsAttributeTypes.PriorityType, AttributeKey.Priority);
   }

   @Test
   public void testSetActionColorTeamByKey() {
      testSetActionByKey("", "Red Team", AtsAttributeTypes.ColorTeam, AttributeKey.ColorTeam);
   }

   @Test
   public void testSetActionIptByKey() {
      testSetActionByKey("", "My IPT", AtsAttributeTypes.IPT, AttributeKey.IPT);
   }

   @Test
   public void testSetActionOriginatorByKey() {
      AtsActionEndpointApi actionEp = AtsClientService.getActionEndpoint();
      TeamWorkFlowArtifact teamWf = DemoUtil.getSawCodeCommittedWf();
      IAtsUser createdBy = teamWf.getCreatedBy();
      Assert.assertEquals(DemoUsers.Joe_Smith, createdBy);

      actionEp.setActionAttributeByType(teamWf.getIdString(), AttributeKey.Originator.name(),
         Arrays.asList(DemoUsers.Kay_Jones.getId().toString()));
   }

   public void testSetActionByKey(String defaultAttrValue, String newValue, AttributeTypeId attrType, AttributeKey attrKey) {
      AtsActionEndpointApi actionEp = AtsClientService.getActionEndpoint();

      TeamWorkFlowArtifact teamWf = DemoUtil.getSawCodeCommittedWf();
      String orig = AtsClientService.get().getAttributeResolver().getSoleAttributeValue((IAtsObject) teamWf, attrType,
         defaultAttrValue);
      teamWf = testAttributeTypeMatchesRestAttributes(attrType);

      Assert.assertNotEquals(orig, newValue);
      actionEp.setActionAttributeByType(teamWf.getIdString(), attrKey.name(), Arrays.asList(newValue));

      teamWf.reloadAttributesAndRelations();
      Assert.assertEquals(newValue, AtsClientService.get().getAttributeResolver().getSoleAttributeValue(
         (IAtsObject) teamWf, attrType, defaultAttrValue));
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
