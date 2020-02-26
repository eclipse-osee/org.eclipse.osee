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
package org.eclipse.osee.ats.ide.integration.tests.ats.resource;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.demo.DemoWorkflowTitles;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.AtsActionEndpointApi;
import org.eclipse.osee.ats.api.workflow.Attribute;
import org.eclipse.osee.ats.api.workflow.AttributeKey;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.api.workflow.NewActionResult;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.ide.demo.DemoUtil;
import org.eclipse.osee.ats.ide.integration.tests.AtsClientService;
import org.eclipse.osee.ats.ide.util.AtsDeleteManager;
import org.eclipse.osee.ats.ide.util.AtsDeleteManager.DeleteOption;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.util.JsonUtil;
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
   public void testUnreleasedVersions() {
      Object object = getFirstAndCount(
         "ats/action/" + DemoArtifactToken.SAW_Commited_Code_TeamWf.getIdString() + "/UnreleasedVersions", 2);
      Assert.assertEquals(DemoBranches.SAW_Bld_2.getName(), object);
   }

   @Test
   public void testTransitionToStates() {
      Object object = getFirstAndCount(
         "ats/action/" + DemoArtifactToken.SAW_Commited_Code_TeamWf.getIdString() + "/TransitionToStates", 4);
      Assert.assertEquals(TeamState.Completed.getName(), object);
   }

   @Test
   public void testQueryTitle() {
      getFirstAndCount(String.format("ats/action/query?Team=%s&Title=SAW", DemoArtifactToken.SAW_Code.getIdString()),
         3);
   }

   @Test
   public void testQueryPriority() {

      getAndCountWorkItems(
         String.format("ats/action/query?Team=%s&Priority=1&Priority=3", DemoArtifactToken.SAW_Code.getIdString()), 4);
   }

   @Test
   public void testQueryWorking() {
      getAndCountWorkItems(
         String.format("ats/action/query?Team=%s&StateType=Working", DemoArtifactToken.SAW_Code.getIdString()), 4);
   }

   @Test
   public void testQueryAssignee() {
      getAndCountWorkItems(String.format("ats/action/query?Team=%s&Assignee=4444&Assignee=3333",
         DemoArtifactToken.SAW_Code.getIdString()), 4);
   }

   @Test
   public void testQueryOriginator() {
      getAndCountWorkItems(
         String.format("ats/action/query?Team=%s&Originator=3333", DemoArtifactToken.SAW_Code.getIdString()), 4);
   }

   @Test
   public void testQueryTeam() {
      getFirstAndCount("ats/action/query?Team=30013695", 4);
   }

   @Test
   public void testQueryTeamPriorityAndWorking() {
      getFirstAndCount("ats/action/query?Team=30013695&Priority=3&Priority=2&StateType=Working", 2);
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
   public void testActionStateById() {
      String results = getJson("ats/action/" + getCodeWorkflow().getIdString() + "/state");
      Assert.assertTrue(results, results.contains(getCodeWorkflow().getIdString()));
   }

   @Test
   public void testActionStateByAtsId() {
      String results = getJson("ats/action/" + getCodeWorkflow().getAtsId() + "/state");
      Assert.assertTrue(results, results.contains(getCodeWorkflow().getIdString()));
   }

   @Test
   public void testActionStateByLegacyId() {
      getCodeWorkflow();
      String results = getJson("ats/action/PCR8125/legacy/state");
      Assert.assertTrue(results, results.contains(getCodeWorkflow().getIdString()));
   }

   @Test
   public void testQuerySingle() {
      TeamWorkFlowArtifact sawCodeCommittedWf = DemoUtil.getSawCodeCommittedWf();

      String url = String.format("/ats/action/query?Team=%s&ats%%2EId=%s",
         sawCodeCommittedWf.getTeamDefinition().getIdString(), sawCodeCommittedWf.getAtsId());
      JsonNode action = testActionRestCall(url, 1);
      Assert.assertEquals(action.get("AtsId").asText(), sawCodeCommittedWf.getAtsId());

      url = String.format("/ats/action/query?Team=%s&1152921504606847877=%s",
         sawCodeCommittedWf.getTeamDefinition().getIdString(), sawCodeCommittedWf.getAtsId());

      action = testActionRestCall(url, 1);
      Assert.assertEquals(action.get("AtsId").asText(), sawCodeCommittedWf.getAtsId());
   }

   private JsonNode testActionRestCall(String url, int size) {
      return testActionRestCall(toURI(url), size);
   }

   private JsonNode testActionRestCall(URI uri, int size) {
      String json = getJson(uri);
      if (json.length() < 3) {
         json = getJson(uri.toString());
         String ur = uri.toString().replaceAll("%3F", "?");
         json = getJson(ur);
      }
      JsonNode arrayNode = JsonUtil.readTree(json);
      Assert.assertEquals(size, arrayNode.size());

      arrayNode.forEach(this::testAction);
      return arrayNode.get(0);
   }

   @Test
   public void testQueryMulti() {
      String name =
         DemoWorkflowTitles.SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW.replaceAll(" ", "%20").replaceAll("\\(",
            "%28").replaceAll("\\)", "%29");
      URI uri = UriBuilder.fromUri(OseeClientProperties.getOseeApplicationServer()).path(
         String.format("/ats/action/query?Team=%s&Team=%s&Name=%s", DemoArtifactToken.SAW_Code.getIdString(),
            DemoArtifactToken.SAW_Test.getIdString(), name)).build();
      JsonNode action = testActionRestCall(uri, 2);
      Assert.assertEquals(action.get("AtsId").asText(), action.get("ats.Id").asText());
   }

   @Test
   public void testGet() {
      String results = getHtml("/ats/action");
      Assert.assertTrue(results.contains("Action Resource"));
   }

   @Test
   public void testAtsActionsRestCall() {
      String url = "/ats/action/" + DemoUtil.getSawAtsIdsStr();
      testActionRestCall(url, 3);
   }

   @Test
   public void testAtsActionsDetailsRestCall() {
      String url = "/ats/action/" + DemoUtil.getSawCodeCommittedWf().getIdString() + "/details";
      JsonNode action = testActionRestCall(url, 1);
      Assert.assertEquals(action.get("AtsId").asText(), action.get("ats.Id").asText());
      Assert.assertFalse(Strings.isNumeric(action.get("ats.Created Date").asText()));
   }

   @Test
   public void testAtsActionsChildRestCall() {
      String url = "/ats/action/" + DemoUtil.getSawCodeCommittedWf().getParentAction().getIdString() + "/child";
      JsonNode action = testActionRestCall(url, 3);
      Assert.assertEquals(action.get("TargetedVersion").asText().replaceAll("\n", ""),
         DemoBranches.SAW_Bld_2.toString());
   }

   private JsonNode testAction(JsonNode action) {
      Assert.assertEquals(DemoWorkflowTitles.SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, action.get("Name").asText());
      Assert.assertNotNull(action.has("id"));
      Assert.assertNotNull(action.has("AtsId"));
      Assert.assertEquals("/ats/ui/action/" + action.get("AtsId").asText(), action.get("actionLocation").asText());
      return action;
   }

   @Test
   public void testAtsActionRestCall() {
      testActionRestCall("/ats/action/" + DemoUtil.getSawCodeCommittedWf().getAtsId(), 1);
   }

   @Test
   public void testAtsActionDetailsRestCall() {
      JsonNode action =
         testActionRestCall("/ats/action/" + DemoUtil.getSawCodeCommittedWf().getAtsId() + "/details", 1);
      Assert.assertEquals(action.get("AtsId").asText(), action.get("ats.Id").asText());
   }

   @Test
   public void testGetActionAttributeByType() {
      testAttributeTypeMatchesRestAttributes(AtsAttributeTypes.State);
   }

   private TeamWorkFlowArtifact testAttributeTypeMatchesRestAttributes(AttributeTypeId attrType) {
      TeamWorkFlowArtifact teamWf = DemoUtil.getSawCodeCommittedWf();
      AtsActionEndpointApi actionEp = AtsClientService.get().getServerEndpoints().getActionEndpoint();
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
      AtsActionEndpointApi actionEp = AtsClientService.get().getServerEndpoints().getActionEndpoint();

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

      // test that search by id or atsId work as well
      Attribute attribute =
         actionEp.getActionAttributeByType(teamWf.getIdString(), CoreAttributeTypes.StaticId.getIdString());
      Assert.assertEquals(2, attribute.getValues().size());
      attribute = actionEp.getActionAttributeByType(teamWf.getAtsId(), CoreAttributeTypes.StaticId.getIdString());
      Assert.assertEquals(2, attribute.getValues().size());

   }

   @Test
   public void testSetActionFloatAttributeByType() {
      AtsActionEndpointApi actionEp = AtsClientService.get().getServerEndpoints().getActionEndpoint();

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
      AtsActionEndpointApi actionEp = AtsClientService.get().getServerEndpoints().getActionEndpoint();

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
      AtsActionEndpointApi actionEp = AtsClientService.get().getServerEndpoints().getActionEndpoint();

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
      AtsActionEndpointApi actionEp = AtsClientService.get().getServerEndpoints().getActionEndpoint();

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
   public void testQueryByLegacyIds() {
      AtsActionEndpointApi actionEp = AtsClientService.get().getServerEndpoints().getActionEndpoint();
      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getName());
      TeamWorkFlowArtifact teamWf = DemoUtil.getSawCodeCommittedWf();
      changes.setSoleAttributeValue((IAtsTeamWorkflow) teamWf, AtsAttributeTypes.LegacyPcrId, "PCR 2344");
      TeamWorkFlowArtifact teamWf2 = DemoUtil.getSawTestCommittedWf();
      changes.setSoleAttributeValue((IAtsTeamWorkflow) teamWf2, AtsAttributeTypes.LegacyPcrId, "PCR2345");
      changes.execute();

      List<IAtsWorkItem> workItems = actionEp.query("PCR 2344, PCR2345");

      boolean found1 = false, found2 = false;
      for (IAtsWorkItem workItem : workItems) {
         if (workItem.getAtsId().equals(teamWf.getAtsId())) {
            found1 = true;
         }
         if (workItem.getAtsId().equals(teamWf2.getAtsId())) {
            found2 = true;
         }
      }
      Assert.assertTrue(found1);
      Assert.assertTrue(found2);
   }

   @Test
   public void testSetActionTitleByKey() {
      AtsActionEndpointApi actionEp = AtsClientService.get().getServerEndpoints().getActionEndpoint();
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
      testSetActionByKey("", "5", AtsAttributeTypes.Priority, AttributeKey.Priority);
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
   public void testSetActionVersionByKey() {
      TeamWorkFlowArtifact teamWf = DemoUtil.getSawCodeCommittedWf();

      IAtsVersion version = AtsClientService.get().getVersionService().getTargetedVersion(teamWf);
      Assert.assertEquals(DemoArtifactToken.SAW_Bld_2, version);

      AtsActionEndpointApi actionEp = AtsClientService.get().getServerEndpoints().getActionEndpoint();

      // Set to Build 1 by id
      IAtsVersion sawBld1Ver = AtsClientService.get().getVersionService().getVersionById(DemoArtifactToken.SAW_Bld_1);
      actionEp.setActionAttributeByType(teamWf.getIdString(), AttributeKey.Version.name(),
         Collections.singletonList(sawBld1Ver.getIdString()));

      AtsClientService.get().getStoreService().reload(Collections.singleton(teamWf));
      IAtsVersion newVer = AtsClientService.get().getVersionService().getTargetedVersion(teamWf);
      Assert.assertEquals(sawBld1Ver, newVer);

      // Clear version
      actionEp.setActionAttributeByType(teamWf.getIdString(), AttributeKey.Version.name(), Collections.emptyList());

      AtsClientService.get().getStoreService().reload(Collections.singleton(teamWf));
      IAtsVersion newVer3 = AtsClientService.get().getVersionService().getTargetedVersion(teamWf);
      Assert.assertNull(newVer3);

      // Set back to Build 2 by name
      actionEp.setActionAttributeByType(teamWf.getIdString(), AttributeKey.Version.name(),
         Collections.singletonList(DemoArtifactToken.SAW_Bld_2.getName()));

      AtsClientService.get().getStoreService().reload(Collections.singleton(teamWf));
      IAtsVersion newVer2 = AtsClientService.get().getVersionService().getTargetedVersion(teamWf);
      Assert.assertEquals(DemoArtifactToken.SAW_Bld_2.getName(), newVer2.getName());

   }

   @Test
   public void testSetActionAssigneesByKey() {
      AtsActionEndpointApi actionEp = AtsClientService.get().getServerEndpoints().getActionEndpoint();
      TeamWorkFlowArtifact teamWf = DemoUtil.getSawCodeCommittedWf();
      List<AtsUser> assignees = teamWf.getStateMgr().getAssignees();
      Assert.assertEquals(1, assignees.size());
      Assert.assertEquals(DemoUsers.Joe_Smith, assignees.iterator().next());

      actionEp.setActionAttributeByType(teamWf.getIdString(), AttributeKey.Assignee.name(),
         Arrays.asList(DemoUsers.Joe_Smith.getIdString(), DemoUsers.Kay_Jones.getIdString()));

      teamWf.reloadAttributesAndRelations();
      assignees = teamWf.getStateMgr().getAssignees();
      Assert.assertEquals(2, assignees.size());
      Assert.assertTrue(
         assignees.contains(AtsClientService.get().getUserService().getUserByAccountId(DemoUsers.Kay_Jones.getId())));
      Assert.assertTrue(
         assignees.contains(AtsClientService.get().getUserService().getUserByAccountId(DemoUsers.Joe_Smith.getId())));

      // reset back to Joe
      actionEp.setActionAttributeByType(teamWf.getIdString(), AttributeKey.Assignee.name(),
         Arrays.asList(DemoUsers.Joe_Smith.getIdString()));
      teamWf.reloadAttributesAndRelations();
      assignees = teamWf.getStateMgr().getAssignees();
      Assert.assertEquals(1, assignees.size());
      Assert.assertEquals(DemoUsers.Joe_Smith, assignees.iterator().next());
   }

   @Test
   public void testSetActionOriginatorByKey() {
      AtsActionEndpointApi actionEp = AtsClientService.get().getServerEndpoints().getActionEndpoint();
      TeamWorkFlowArtifact teamWf = DemoUtil.getSawCodeCommittedWf();
      AtsUser createdBy = teamWf.getCreatedBy();
      Assert.assertEquals(DemoUsers.Joe_Smith, createdBy);

      actionEp.setActionAttributeByType(teamWf.getIdString(), AttributeKey.Originator.name(),
         Arrays.asList(DemoUsers.Kay_Jones.getIdString()));
   }

   public void testSetActionByKey(String defaultAttrValue, String newValue, AttributeTypeToken attrType, AttributeKey attrKey) {
      AtsActionEndpointApi actionEp = AtsClientService.get().getServerEndpoints().getActionEndpoint();

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
   public void testCreateActionFromActionData() {
      NewActionData data = new NewActionData();
      data.setAsUserId(AtsClientService.get().getUserService().getCurrentUserId());
      data.setTitle("My Action");
      Date createDate = new Date();
      data.setCreatedDateLong(String.valueOf(createDate.getTime()));
      data.setCreatedByUserId(DemoUsers.Alex_Kay.getUserId());
      data.setChangeType(ChangeType.Improvement);
      data.setDescription("desc");
      Date needBy = new Date();
      data.setNeedByDateLong(String.valueOf(needBy.getTime()));
      data.setPriority("3");
      IAtsActionableItem ai =
         AtsClientService.get().getActionableItemService().getActionableItemById(DemoArtifactToken.SAW_Code_AI);
      data.setAiIds(Arrays.asList(ai.getIdString()));
      data.addAttrValue(AtsAttributeTypes.ColorTeam, "Blue Team");
      data.addAttrValue(AtsAttributeTypes.IPT, "My IPT");
      NewActionResult result = AtsClientService.get().getServerEndpoints().getActionEndpoint().createAction(data);
      Assert.assertFalse(result.getResults().toString(), result.getResults().isErrors());
      Assert.assertNotNull(result.getAction());
      Assert.assertEquals(1, result.getTeamWfs().size());
      ArtifactId teamWfArt = result.getTeamWfs().iterator().next();
      IAtsTeamWorkflow teamWf = AtsClientService.get().getQueryService().getTeamWf(teamWfArt);
      Assert.assertNotNull(teamWf);
      Assert.assertEquals("My Action", teamWf.getName());
      Assert.assertEquals("desc", teamWf.getDescription());
      Assert.assertEquals(createDate, teamWf.getCreatedDate());
      Assert.assertEquals(DemoUsers.Alex_Kay, teamWf.getCreatedBy().getStoreObject());
      Assert.assertEquals(DemoUsers.Joe_Smith,
         AtsClientService.get().getQueryServiceClient().getArtifact(teamWf).getLastModifiedBy());
      Assert.assertEquals(ChangeType.Improvement.name(),
         AtsClientService.get().getAttributeResolver().getSoleAttributeValue(teamWf, AtsAttributeTypes.ChangeType, ""));
      Assert.assertEquals("3",
         AtsClientService.get().getAttributeResolver().getSoleAttributeValue(teamWf, AtsAttributeTypes.Priority, ""));
      Assert.assertEquals(ai, teamWf.getActionableItems().iterator().next());
      Assert.assertEquals(needBy,
         AtsClientService.get().getAttributeResolver().getSoleAttributeValue(teamWf, AtsAttributeTypes.NeedBy, ""));
      Assert.assertEquals("Blue Team",
         AtsClientService.get().getAttributeResolver().getSoleAttributeValue(teamWf, AtsAttributeTypes.ColorTeam, ""));
      Assert.assertEquals("My IPT",
         AtsClientService.get().getAttributeResolver().getSoleAttributeValue(teamWf, AtsAttributeTypes.IPT, ""));

      AtsDeleteManager.handleDeletePurgeAtsObject(
         Arrays.asList(AtsClientService.get().getQueryServiceClient().getArtifact(teamWf)), true, DeleteOption.Delete);
   }

   @Test
   public void testCreateAction() throws Exception {

      TeamWorkFlowArtifact teamArt = null;
      try {
         Form form = new Form();
         postAndValidateResponse("title is not valid", form);

         form.param("ats_title", getClass().getSimpleName() + " - here");
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

         teamArt = (TeamWorkFlowArtifact) ArtifactQuery.getArtifactFromAttribute(AtsAttributeTypes.AtsId, atsId,
            AtsClientService.get().getAtsBranch());
         Assert.assertNotNull(teamArt);
      } catch (Exception ex) {
         // do nothing
      }

      // Cleanup test
      if (teamArt != null) {
         IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getName());
         changes.deleteArtifact(teamArt.getParentActionArtifact());
         changes.deleteArtifact((Artifact) teamArt);
         changes.executeIfNeeded();
      }
   }

   @Test
   public void testCreateEmptyAction() throws Exception {
      IAtsActionableItem ai =
         AtsClientService.get().getActionableItemService().getActionableItemById(DemoArtifactToken.SAW_Code_AI);
      String aiStr = ai.getIdString();

      String newAction = AtsClientService.get().getServerEndpoints().getActionEndpoint().createEmptyAction(
         AtsClientService.get().getUserService().getCurrentUserId(), aiStr, "New Action");

      JsonNode root = JsonUtil.readTree(newAction);
      Long id = root.path("id").asLong();

      TeamWorkFlowArtifact teamArt =
         (TeamWorkFlowArtifact) ArtifactQuery.getArtifactFromId(id, AtsClientService.get().getAtsBranch());
      Assert.assertNotNull(teamArt);

      // Cleanup test
      AtsClientService.get().getQueryServiceClient().getArtifact(teamArt.getParentAction()).deleteAndPersist();
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
