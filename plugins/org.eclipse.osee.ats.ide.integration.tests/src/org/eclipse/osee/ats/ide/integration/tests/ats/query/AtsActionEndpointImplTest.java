/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.query;

import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.Task;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.TeamWorkflow;
import static org.eclipse.osee.ats.api.demo.DemoWorkDefinitions.WorkDef_Team_Demo_Req;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.demo.DemoWorkType;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.RecentlyVisistedItem;
import org.eclipse.osee.ats.api.util.RecentlyVisitedItems;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.web.WorkflowData;
import org.eclipse.osee.ats.api.workflow.AtsActionEndpointApi;
import org.eclipse.osee.ats.api.workflow.AtsActionUiEndpointApi;
import org.eclipse.osee.ats.api.workflow.Attribute;
import org.eclipse.osee.ats.api.workflow.AttributeKey;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.api.workflow.NewActionResult;
import org.eclipse.osee.ats.api.workflow.journal.JournalData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionData;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.ats.core.test.AtsTestUtilCore;
import org.eclipse.osee.ats.core.workflow.TeamWorkflow;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.resource.AbstractRestTest;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.ide.util.AtsApiIde;
import org.eclipse.osee.ats.ide.util.AtsDeleteManager;
import org.eclipse.osee.ats.ide.util.AtsDeleteManager.DeleteOption;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test unit for {@link AtsActionEndpointImpl}
 *
 * @author Donald G. Dunne
 */
public class AtsActionEndpointImplTest extends AbstractRestTest {
   private static final String committedCodeWfId = DemoArtifactToken.SAW_Commited_Code_TeamWf.getIdString();
   private static final String codeWfId = DemoArtifactToken.SAW_Code.getIdString();
   private final JaxRsApi jaxRsApi = AtsApiService.get().jaxRsApi();
   private IAtsTeamWorkflow teamWf;
   private AtsApiIde atsApi;
   private AtsActionEndpointApi actionEp;

   @Before
   public void setup() {
      atsApi = AtsApiService.get();
      actionEp = atsApi.getServerEndpoints().getActionEndpoint();
   }

   @Test
   // /ats/action/data/id
   public void testGetWorkflowData() {
      WorkflowData wData = actionEp.getWorkflowData(DemoArtifactToken.SAW_UnCommited_Req_TeamWf);
      Assert.assertNotNull(wData);
      Assert.assertEquals(TeamState.Implement.getName(), wData.getCurrentStateName().getValue());
      Assert.assertTrue(wData.getAssigneeNames().stream().map(
         (x) -> x.getAttributePojo().getValue().equals(DemoUsers.Joe_Smith.getName())) != null);
      Assert.assertEquals(WorkDef_Team_Demo_Req.getName(), wData.getWorkDefName());
      Assert.assertEquals(6, wData.getWorkDefStates().size());
   }

   @Test
   // /ats/action/query/workitems/ids
   public void testQueryByids() {
      AtsSearchData data = new AtsSearchData();
      data.setTeamDefIds(Arrays.asList(DemoArtifactToken.SAW_Requirements.getId()));
      data.setStateTypes(Arrays.asList(StateType.Working));
      XResultData rd = actionEp.queryIds(data);

      Assert.assertTrue(rd.isSuccess());
   }

   // /ats/action/query/workitems
   @Test
   public void testGetMyWorldWithUser() {
      AtsSearchData data = new AtsSearchData();
      data.setTeamDefIds(Arrays.asList(DemoArtifactToken.SAW_Requirements.getId()));
      data.setStateTypes(Arrays.asList(StateType.Working));

      Collection<IAtsWorkItem> items = actionEp.query(data);
      Assert.assertNotNull(items);
      Assert.assertFalse(items.isEmpty());
   }

   @Test
   public void testUnreleasedVersions() {
      Object object = getFirstAndCount("ats/action/" + committedCodeWfId + "/UnreleasedVersions", 2);
      Assert.assertEquals(DemoBranches.SAW_Bld_2.getName(), object);
   }

   @Test
   public void testTransitionToStates() {
      Object object = getFirstAndCount("ats/action/" + committedCodeWfId + "/TransitionToStates", 5);
      Assert.assertEquals(TeamState.Completed.getName(), object);
   }

   @Test
   public void testQueryOpenLastMod() {
      getFirstAndCount("ats/action/query/lastmod?ArtTypeId=" + Task.getId(), 14);
   }

   @Test
   public void testQueryOpenWorkItems() {
      if (AtsApiService.get().getStoreService().getJdbcService().getClient().getDbType().isPaginationOrderingSupported()) {
         String query = String.format("ats/action/query/workitems?artType=%s", TeamWorkflow.getId());
         getFirstAndCount(query, 26);
      }
   }

   @Test
   public void testQueryOpenWorkItemsCount() {
      String query = String.format("ats/action/query/workitems/count?artType=%s", Task.getId());
      String json = getJson(query);
      Assert.assertTrue(json.equals("14"));
   }

   @Test
   public void testTransitionValidate() {
      AtsApi atsApi = AtsApiService.get();
      TransitionData tData = new TransitionData();
      tData.setWorkItemIds(Arrays.asList(DemoArtifactToken.SAW_UnCommited_Req_TeamWf.getToken()));
      tData.setTransitionUser(atsApi.getUserService().getCurrentUser());
      tData.setToStateName(TeamState.Authorize.getName());
      tData.setName("testTransitionValidate");

      TransitionResults tResults = atsApi.getServerEndpoints().getActionEndpoint().transitionValidate(tData);
      Assert.assertTrue(tResults.isErrors());
      Assert.assertTrue(tResults.toString().contains("Working Branch exists"));
   }

   @Test
   public void testGetPoints() {
      Collection<String> pointValues = AtsApiService.get().getServerEndpoints().getActionEndpoint().getPointValues();
      Assert.assertEquals(12, pointValues.size());
      Assert.assertTrue(pointValues.contains("Epic"));
   }

   @Test
   public void testSetAndCheckApprovalAndCancel() {

      // If no sign-off widgets, call should return true
      TeamWorkflow sawReqNoBranchWf = DemoUtil.getSawReqNoBranchWf();
      Assert.assertNotNull(sawReqNoBranchWf);
      boolean checkApproval = actionEp.checkApproval(sawReqNoBranchWf.getAtsId());
      Assert.assertTrue(checkApproval);

      // Create PLE workflow that does have approval widgets
      AtsApi atsApi = AtsApiService.get();
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(AtsTestUtilCore.class.getSimpleName());
      IAtsActionableItem plAi =
         atsApi.getActionableItemService().getActionableItemById(DemoArtifactToken.SAW_PL_ARB_AI);
      String title = "testSetAndCheckApproval";
      NewActionData data = atsApi.getActionService().createActionData(title, title, "description", Arrays.asList(plAi));
      NewActionData newData = atsApi.getActionService().createAction(data);
      Assert.assertTrue(newData.getRd().isSuccess());
      IAtsTeamWorkflow teamWf = newData.getActResult().getAtsTeamWfs().iterator().next();
      TransitionManager transMgr = new TransitionManager(
         new TransitionData(title, Arrays.asList(teamWf), TeamState.Review.getName(), null, null, changes));
      transMgr.handleTransition(new TransitionResults());
      TransactionToken tx = changes.execute();
      Assert.assertTrue(tx.isValid());

      Assert.assertNull(
         atsApi.getAttributeResolver().getSoleAttributeValue(teamWf, AtsAttributeTypes.ProductLineApprovedBy, null));
      Assert.assertNull(
         atsApi.getAttributeResolver().getSoleAttributeValue(teamWf, AtsAttributeTypes.ProductLineApprovedDate, null));

      AtsActionEndpointApi actionEp = atsApi.getServerEndpoints().getActionEndpoint();
      checkApproval = actionEp.checkApproval(teamWf.getAtsId());
      Assert.assertFalse(checkApproval);

      actionEp.setApproval(teamWf.getAtsId());

      Artifact teamWfArt = (Artifact) teamWf.getStoreObject();
      teamWfArt.reloadAttributesAndRelations();
      teamWf = atsApi.getWorkItemService().getTeamWf(teamWfArt);

      Assert.assertNotNull(
         atsApi.getAttributeResolver().getSoleAttributeValue(teamWf, AtsAttributeTypes.ProductLineApprovedBy, null));
      Assert.assertNotNull(
         atsApi.getAttributeResolver().getSoleAttributeValue(teamWf, AtsAttributeTypes.ProductLineApprovedDate, null));

      checkApproval = actionEp.checkApproval(teamWf.getAtsId());
      Assert.assertTrue(checkApproval);

      // Test cancel endpoint with existing teamWf so don't have to create another workflow
      try (Response r = actionEp.cancelAction(teamWf.getIdString());) {
         // Close resource created from cancelAction
      }

      teamWfArt.reloadAttributesAndRelations();
      Assert.assertEquals(TeamState.Cancelled.getName(), teamWf.getCurrentStateName());

      Operations.executeWorkAndCheckStatus(new PurgeArtifacts(Arrays.asList(teamWfArt)));
   }

   @Test
   public void testSetByArtifactToken() {
      AtsTestUtil.cleanupAndReset("testSetByArtifactToken");
      AtsApi atsApi = AtsApiService.get();
      AtsActionEndpointApi actionEp = atsApi.getServerEndpoints().getActionEndpoint();
      IAtsTeamWorkflow teamWf = AtsTestUtil.getTeamWf();
      IAtsVersion ver = atsApi.getVersionService().getTargetedVersion(teamWf);
      Assert.assertNull(ver);

      IAtsVersion toVer = atsApi.getVersionService().getVersion(DemoArtifactToken.SAW_PL_SBVT1);
      Assert.assertNotNull(toVer);
      actionEp.setByArtifactToken(teamWf.getIdString(), "Version", Arrays.asList(toVer.getArtifactToken()));

      ((Artifact) teamWf.getStoreObject()).reloadAttributesAndRelations();
      teamWf = atsApi.getQueryService().getTeamWf(teamWf.getId());
      IAtsVersion newVer = atsApi.getVersionService().getTargetedVersion(teamWf);
      Assert.assertEquals(toVer, newVer);
      AtsTestUtil.cleanup();
   }

   @Test
   public void testQueryTitle() {
      WebTarget target = createWebTarget( //
         "Team", codeWfId, //
         "Title", "SAW"//
      );

      getFirstAndCount(target, 4);
   }

   @Test
   public void testQueryPriority() {
      WebTarget target = createWebTarget( //
         "Team", codeWfId, //
         AtsAttributeTypes.Priority.getIdString(), "1", //
         AtsAttributeTypes.Priority.getIdString(), "3"//
      );

      getAndCountWorkItems(target, 4);
   }

   @Test
   public void testQueryWorking() {
      WebTarget target = createWebTarget( //
         "Team", codeWfId, //
         "StateType", StateType.Working.name()//
      );
      getAndCountWorkItems(target, 4);
   }

   @Test
   public void testQueryAssignee() {
      WebTarget target = createWebTarget( //
         "Team", codeWfId, //
         "Assignee", "4444", //
         "Assignee", "3333");
      getAndCountWorkItems(target, 4);
   }

   @Test
   public void testQueryOriginator() {
      WebTarget target = createWebTarget( //
         "Team", codeWfId, //
         "Originator", "3333" //
      );
      getAndCountWorkItems(target, 4);
   }

   @Test
   public void testQueryTeam() {
      WebTarget target = createWebTarget("Team", codeWfId);
      getFirstAndCount(target, 4);
   }

   @Test
   public void testQueryTeamPriorityAndWorking() {
      WebTarget target = createWebTarget( //
         "Team", codeWfId, //
         "Priority", "2", //
         "Priority", "3", //
         "StateType", StateType.Working.name() //
      );
      getFirstAndCount(target, 2);
   }

   public IAtsTeamWorkflow getCodeWorkflow() {
      if (teamWf == null) {
         teamWf = DemoUtil.getSawCodeCommittedWf();
      }
      return teamWf;
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
      IAtsTeamWorkflow teamWf = getCodeWorkflow();
      TransactionToken tx =
         teamWf.setSoleAttributeValue(AtsAttributeTypes.LegacyPcrId, "PCR8125", getClass().getSimpleName());
      Assert.assertTrue(tx.isValid());
      String results = getJson("ats/action/PCR8125/legacy/state");
      Assert.assertTrue(results, results.contains(getCodeWorkflow().getIdString()));
   }

   private WebTarget createWebTarget(String... queryParmPairs) {
      return jaxRsApi.newTargetQuery("ats/action/query", queryParmPairs);
   }

   @Test
   public void testQuerySingle() {
      IAtsTeamWorkflow sawCodeCommittedWf = DemoUtil.getSawCodeCommittedWf();

      String idString = sawCodeCommittedWf.getTeamDefinition().getIdString();
      WebTarget target = createWebTarget( //
         "Team", idString, //
         AtsAttributeTypes.AtsId.getIdString(), sawCodeCommittedWf.getAtsId() //
      );

      JsonNode action = testActionRestCall(target, 1);
      Assert.assertEquals(action.get("AtsId").asText(), sawCodeCommittedWf.getAtsId());

      target = createWebTarget( //
         "Team", idString, //
         AtsAttributeTypes.AtsId.getName(), sawCodeCommittedWf.getAtsId() //
      );

      action = testActionRestCall(target, 1);
      Assert.assertEquals(action.get("AtsId").asText(), sawCodeCommittedWf.getAtsId());
   }

   private JsonNode testActionRestCall(String path, int size) {
      return testActionRestCall(jaxRsApi.newTarget(path), size);
   }

   private JsonNode testActionRestCall(WebTarget target, int size) {
      String json = target.request(MediaType.APPLICATION_JSON_TYPE).get().readEntity(String.class);
      JsonNode arrayNode = jaxRsApi.readTree(json);
      Assert.assertEquals(size, arrayNode.size());

      arrayNode.forEach(this::testAction);
      return arrayNode.get(0);
   }

   @Test
   public void testQueryMulti() {
      String name =
         DemoArtifactToken.SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW.replaceAll(" ", "%20").replaceAll("\\(",
            "%28").replaceAll("\\)", "%29");

      WebTarget target = createWebTarget( //
         "Team", codeWfId, //
         "Team", DemoArtifactToken.SAW_Test.getIdString(), //
         "Name", name //
      );
      JsonNode action = testActionRestCall(target, 2);
      Assert.assertEquals(action.get("AtsId").asText(), action.get("ats.Id").asText());
   }

   @Test
   public void testAtsActionsRestCall() {
      String url = "ats/action/" + DemoUtil.getSawAtsIdsStr();
      testActionRestCall(url, 3);
   }

   @Test
   public void testAtsActionsDetailsRestCall() {
      String url = "ats/action/" + DemoUtil.getSawCodeCommittedWf().getIdString() + "/details";
      JsonNode action = testActionRestCall(url, 1);
      Assert.assertEquals(action.get("AtsId").asText(), action.get("ats.Id").asText());
      Assert.assertFalse(Strings.isNumeric(action.get("ats.Created Date").asText()));
      Assert.assertNotNull(action.get("DerivedFrom"));
      // Note: derivedfrom results tested from DemoBranchRegresstionTest
   }

   @Test
   public void testAtsActionsChildRestCall() {
      String url = "ats/action/" + DemoUtil.getSawCodeCommittedWf().getParentAction().getIdString() + "/child";
      JsonNode action = testActionRestCall(url, 3);
      Assert.assertEquals(action.get("TargetedVersion").asText().replaceAll("\n", ""),
         DemoBranches.SAW_Bld_2.toString());
   }

   @Test
   public void testAtsActionsSiblingsRestCall() {
      String url = "ats/action/" + DemoUtil.getSawCodeCommittedWf().getAtsId() + "/sibling";
      JsonNode action = testActionRestCall(url, 2);
      Assert.assertEquals(action.get("TargetedVersion").asText().replaceAll("\n", ""),
         DemoBranches.SAW_Bld_2.toString());
   }

   private JsonNode testAction(JsonNode action) {
      Assert.assertEquals(DemoArtifactToken.SAW_COMMITTED_REQT_CHANGES_FOR_DIAGRAM_VIEW, action.get("Name").asText());
      Assert.assertNotNull(action.has("id"));
      Assert.assertNotNull(action.has("AtsId"));
      Assert.assertEquals("/ats/ui/action/" + action.get("AtsId").asText(), action.get("actionLocation").asText());
      return action;
   }

   @Test
   public void testAtsActionRestCall() {
      String atsId = "ats/action/" + DemoUtil.getSawCodeCommittedWf().getAtsId();
      testActionRestCall(atsId, 1);
   }

   @Test
   public void testAtsActionDetailsRestCall() {
      String atsId = DemoUtil.getSawCodeCommittedWf().getAtsId();
      JsonNode action = testActionRestCall("ats/action/" + atsId + "/details", 1);
      Assert.assertEquals(action.get("AtsId").asText(), action.get("ats.Id").asText());
   }

   @Test
   public void testGetActionAttributeByType() {
      testAttributeTypeMatchesRestAttributes(AtsAttributeTypes.CurrentStateName);
   }

   private IAtsTeamWorkflow testAttributeTypeMatchesRestAttributes(AttributeTypeToken attributeType) {
      IAtsTeamWorkflow teamWf = DemoUtil.getSawCodeCommittedWf();
      teamWf.reload();
      AtsActionEndpointApi actionEp = AtsApiService.get().getServerEndpoints().getActionEndpoint();
      Attribute attribute = actionEp.getActionAttributeByType(teamWf.getIdString(), attributeType);
      Assert.assertEquals(teamWf, attribute.getArtId());
      Assert.assertEquals(attributeType, attribute.getAttributeType());
      Assert.assertEquals(teamWf.getAttributeCount(attributeType), attribute.getValues().size());

      for (String attrValue : teamWf.getAttributesToStringList(attributeType)) {
         Assert.assertTrue(attribute.getValues().values().contains(attrValue));
      }
      return teamWf;
   }

   @Test
   public void testSetActionStringAttributeByType() {
      AtsActionEndpointApi actionEp = AtsApiService.get().getServerEndpoints().getActionEndpoint();

      IAtsTeamWorkflow teamWf = testAttributeTypeMatchesRestAttributes(CoreAttributeTypes.StaticId);
      Assert.assertTrue(teamWf.getTags().isEmpty());

      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      changes.addAttribute(teamWf, CoreAttributeTypes.StaticId, "asdf");
      changes.addAttribute(teamWf, CoreAttributeTypes.StaticId, "qwer");
      changes.addAttribute(teamWf, CoreAttributeTypes.StaticId, "zxcv");
      changes.execute();

      teamWf = testAttributeTypeMatchesRestAttributes(CoreAttributeTypes.StaticId);
      Assert.assertEquals(3, teamWf.getTags().size());

      actionEp.setActionAttributeByType(teamWf.getIdString(), CoreAttributeTypes.StaticId.getIdString(),
         Arrays.asList("asdf", "zxcv"));

      teamWf.reload();
      teamWf = testAttributeTypeMatchesRestAttributes(CoreAttributeTypes.StaticId);
      Assert.assertEquals(2, teamWf.getTags().size());

      // test that search by id or atsId work as well
      Attribute attribute = actionEp.getActionAttributeByType(teamWf.getIdString(), CoreAttributeTypes.StaticId);
      Assert.assertEquals(2, attribute.getValues().size());
      attribute = actionEp.getActionAttributeByType(teamWf.getAtsId(), CoreAttributeTypes.StaticId);
      Assert.assertEquals(2, attribute.getValues().size());

   }

   @Test
   public void testSetActionFloatAttributeByType() {
      AtsActionEndpointApi actionEp = AtsApiService.get().getServerEndpoints().getActionEndpoint();

      IAtsTeamWorkflow teamWf = DemoUtil.getSawCodeCommittedWf();

      Assert.assertEquals(0, AtsApiService.get().getAttributeResolver().getAttributesToStringList(teamWf,
         AtsAttributeTypes.EstimatedHours).size());

      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.EstimatedHours, 3.5);
      changes.execute();

      teamWf = testAttributeTypeMatchesRestAttributes(AtsAttributeTypes.EstimatedHours);
      Assert.assertEquals(1, AtsApiService.get().getAttributeResolver().getAttributesToStringList(teamWf,
         AtsAttributeTypes.EstimatedHours).size());

      actionEp.setActionAttributeByType(teamWf.getIdString(), AtsAttributeTypes.EstimatedHours.getIdString(),
         Arrays.asList("4.5"));

      teamWf.reload();
      teamWf = testAttributeTypeMatchesRestAttributes(AtsAttributeTypes.EstimatedHours);
      Assert.assertEquals((Double) 4.5, AtsApiService.get().getAttributeResolver().getSoleAttributeValue(
         (IAtsObject) teamWf, AtsAttributeTypes.EstimatedHours, 1.0));
   }

   @Test
   public void testSetActionIntegerAttributeByType() {
      AtsActionEndpointApi actionEp = AtsApiService.get().getServerEndpoints().getActionEndpoint();

      IAtsTeamWorkflow teamWf = DemoUtil.getSawCodeCommittedWf();

      Assert.assertEquals(0, AtsApiService.get().getAttributeResolver().getAttributesToStringList(teamWf,
         AtsAttributeTypes.PercentRework).size());

      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.PercentRework, 3);
      changes.execute();

      teamWf = testAttributeTypeMatchesRestAttributes(AtsAttributeTypes.PercentRework);
      Assert.assertEquals((Integer) 3, AtsApiService.get().getAttributeResolver().getSoleAttributeValue(
         (IAtsObject) teamWf, AtsAttributeTypes.PercentRework, 4));

      actionEp.setActionAttributeByType(teamWf.getIdString(), AtsAttributeTypes.PercentRework.getIdString(),
         Arrays.asList("4"));

      teamWf.reload();
      teamWf = testAttributeTypeMatchesRestAttributes(AtsAttributeTypes.PercentRework);
      Assert.assertEquals((Integer) 4, AtsApiService.get().getAttributeResolver().getSoleAttributeValue(
         (IAtsObject) teamWf, AtsAttributeTypes.PercentRework, 1.0));
   }

   @Test
   public void testSetActionDateAttributeByType() {
      AtsActionEndpointApi actionEp = AtsApiService.get().getServerEndpoints().getActionEndpoint();

      IAtsTeamWorkflow teamWf = DemoUtil.getSawCodeCommittedWf();

      Assert.assertEquals((Date) null, AtsApiService.get().getAttributeResolver().getSoleAttributeValue(
         (IAtsObject) teamWf, AtsAttributeTypes.NeedBy, null));

      Date date = new Date();

      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.NeedBy, date);
      changes.execute();

      teamWf = testAttributeTypeMatchesRestAttributes(AtsAttributeTypes.NeedBy);
      Assert.assertEquals(1,
         AtsApiService.get().getAttributeResolver().getAttributesToStringList(teamWf, AtsAttributeTypes.NeedBy).size());

      actionEp.setActionAttributeByType(teamWf.getIdString(), AtsAttributeTypes.NeedBy.getIdString(),
         Arrays.asList("446579845"));

      teamWf.reload();
      teamWf = testAttributeTypeMatchesRestAttributes(AtsAttributeTypes.NeedBy);
      Date date2 = (Date) AtsApiService.get().getAttributeResolver().getSoleAttributeValue((IAtsObject) teamWf,
         AtsAttributeTypes.NeedBy, null);

      if (date2 != null) {
         Long dateTime = date2.getTime();
         Assert.assertEquals(446579845, dateTime.intValue());
      }
   }

   @Test
   public void testSetActionBooleanAttributeByType() {
      AtsActionEndpointApi actionEp = AtsApiService.get().getServerEndpoints().getActionEndpoint();

      IAtsTeamWorkflow teamWf = DemoUtil.getSawCodeCommittedWf();

      Assert.assertEquals(false, AtsApiService.get().getAttributeResolver().getSoleAttributeValue((IAtsObject) teamWf,
         AtsAttributeTypes.ValidationRequired, false));

      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.ValidationRequired, true);
      changes.execute();

      teamWf = testAttributeTypeMatchesRestAttributes(AtsAttributeTypes.ValidationRequired);
      Assert.assertEquals(true, AtsApiService.get().getAttributeResolver().getSoleAttributeValue((IAtsObject) teamWf,
         AtsAttributeTypes.ValidationRequired, false));

      actionEp.setActionAttributeByType(teamWf.getIdString(), AtsAttributeTypes.ValidationRequired.getIdString(),
         Arrays.asList("false"));

      teamWf.reload();
      teamWf = testAttributeTypeMatchesRestAttributes(AtsAttributeTypes.ValidationRequired);
      Assert.assertEquals(false, AtsApiService.get().getAttributeResolver().getSoleAttributeValue((IAtsObject) teamWf,
         AtsAttributeTypes.ValidationRequired, true));
   }

   @Test
   public void testQueryByLegacyIds() {
      AtsActionEndpointApi actionEp = AtsApiService.get().getServerEndpoints().getActionEndpoint();
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getName());
      IAtsTeamWorkflow teamWf = DemoUtil.getSawCodeCommittedWf();
      changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.LegacyPcrId, "PCR 2344");
      IAtsTeamWorkflow teamWf2 = DemoUtil.getSawTestCommittedWf();
      changes.setSoleAttributeValue(teamWf2, AtsAttributeTypes.LegacyPcrId, "PCR2345");
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
      AtsActionEndpointApi actionEp = AtsApiService.get().getServerEndpoints().getActionEndpoint();
      IAtsTeamWorkflow teamWf = DemoUtil.getSawCodeCommittedWf();

      String origTitle = teamWf.getName();
      testSetActionByKey("", origTitle + "-1", AtsAttributeTypes.Title, AttributeKey.Title);

      actionEp.setActionAttributeByType(teamWf.getIdString(), AttributeKey.Title.name(), Arrays.asList(origTitle));

      teamWf.reload();
      Assert.assertEquals(origTitle, teamWf.getName());

      teamWf = testAttributeTypeMatchesRestAttributes(CoreAttributeTypes.Name);
   }

   @Test
   public void testSetActionPriorityByKey() {
      testSetActionByKey("", "5", AtsAttributeTypes.Priority, AttributeKey.Priority);
   }

   @Test
   public void testSetActionVersionByKey() {
      IAtsTeamWorkflow teamWf = DemoUtil.getSawCodeCommittedWf();

      IAtsVersion version = AtsApiService.get().getVersionService().getTargetedVersion(teamWf);
      Assert.assertEquals(DemoArtifactToken.SAW_Bld_2, version);

      AtsActionEndpointApi actionEp = AtsApiService.get().getServerEndpoints().getActionEndpoint();

      // Set to Build 1 by id
      IAtsVersion sawBld1Ver = AtsApiService.get().getVersionService().getVersionById(DemoArtifactToken.SAW_Bld_1);
      actionEp.setActionAttributeByType(teamWf.getIdString(), AttributeKey.Version.name(),
         Collections.singletonList(sawBld1Ver.getIdString()));

      AtsApiService.get().getStoreService().reload(Collections.singleton(teamWf));
      IAtsVersion newVer = AtsApiService.get().getVersionService().getTargetedVersion(teamWf);
      Assert.assertEquals(sawBld1Ver, newVer);

      // Clear version
      actionEp.setActionAttributeByType(teamWf.getIdString(), AttributeKey.Version.name(), Collections.emptyList());

      AtsApiService.get().getStoreService().reload(Collections.singleton(teamWf));
      IAtsVersion newVer3 = AtsApiService.get().getVersionService().getTargetedVersion(teamWf);
      Assert.assertNull(newVer3);

      // Set back to Build 2 by name
      actionEp.setActionAttributeByType(teamWf.getIdString(), AttributeKey.Version.name(),
         Collections.singletonList(DemoArtifactToken.SAW_Bld_2.getName()));

      AtsApiService.get().getStoreService().reload(Collections.singleton(teamWf));
      IAtsVersion newVer2 = AtsApiService.get().getVersionService().getTargetedVersion(teamWf);
      Assert.assertEquals(DemoArtifactToken.SAW_Bld_2.getName(), newVer2.getName());

   }

   @Test
   public void testSetActionAssigneesByKey() {
      AtsActionEndpointApi actionEp = AtsApiService.get().getServerEndpoints().getActionEndpoint();
      IAtsTeamWorkflow teamWf = DemoUtil.getSawCodeCommittedWf();
      List<AtsUser> assignees = teamWf.getAssignees();
      Assert.assertEquals(1, assignees.size());
      Assert.assertEquals(DemoUsers.Joe_Smith, assignees.iterator().next());

      actionEp.setActionAttributeByType(teamWf.getIdString(), AttributeKey.Assignee.name(),
         Arrays.asList(DemoUsers.Joe_Smith.getIdString(), DemoUsers.Kay_Jones.getIdString()));

      teamWf.reload();
      assignees = teamWf.getAssignees();
      Assert.assertEquals(2, assignees.size());
      Assert.assertTrue(assignees.contains(AtsApiService.get().getUserService().getUserById(DemoUsers.Kay_Jones)));
      Assert.assertTrue(assignees.contains(AtsApiService.get().getUserService().getUserById(DemoUsers.Joe_Smith)));

      // reset back to Joe
      actionEp.setActionAttributeByType(teamWf.getIdString(), AttributeKey.Assignee.name(),
         Arrays.asList(DemoUsers.Joe_Smith.getIdString()));
      teamWf.reload();
      assignees = teamWf.getAssignees();
      Assert.assertEquals(1, assignees.size());
      Assert.assertEquals(DemoUsers.Joe_Smith, assignees.iterator().next());
   }

   @Test
   public void testSetActionOriginatorByKey() {
      AtsActionEndpointApi actionEp = AtsApiService.get().getServerEndpoints().getActionEndpoint();
      IAtsTeamWorkflow teamWf = DemoUtil.getSawCodeCommittedWf();
      AtsUser createdBy = teamWf.getCreatedBy();
      Assert.assertEquals(DemoUsers.Joe_Smith, createdBy);

      actionEp.setActionAttributeByType(teamWf.getIdString(), AttributeKey.Originator.name(),
         Arrays.asList(DemoUsers.Kay_Jones.getIdString()));
   }

   public void testSetActionByKey(String defaultAttrValue, String newValue, AttributeTypeToken attrType,
      AttributeKey attrKey) {
      AtsActionEndpointApi actionEp = AtsApiService.get().getServerEndpoints().getActionEndpoint();

      IAtsTeamWorkflow teamWf = DemoUtil.getSawCodeCommittedWf();
      String orig = AtsApiService.get().getAttributeResolver().getSoleAttributeValue((IAtsObject) teamWf, attrType,
         defaultAttrValue);
      teamWf = testAttributeTypeMatchesRestAttributes(attrType);

      Assert.assertNotEquals(orig, newValue);
      actionEp.setActionAttributeByType(teamWf.getIdString(), attrKey.name(), Arrays.asList(newValue));

      teamWf.reload();
      Assert.assertEquals(newValue, AtsApiService.get().getAttributeResolver().getSoleAttributeValue(
         (IAtsObject) teamWf, attrType, defaultAttrValue));
   }

   @Test
   public void testCreateAction() {
      AtsApiIde atsApi = AtsApiService.get();

      NewActionData data = new NewActionData();
      data.setOpName(getClass().getSimpleName());
      data.setAsUser(atsApi.user().getArtifactId());
      data.setTitle("My Action");
      Date createDate = new Date();
      data.setCreatedDateLong(String.valueOf(createDate.getTime()));
      data.setCreatedByUserArtId(DemoUsers.Alex_Kay.getIdString());
      data.setChangeType(ChangeTypes.Improvement);
      data.setDescription("desc");
      Date needBy = new Date();
      data.setNeedByDateLong(String.valueOf(needBy.getTime()));
      data.setPriority("3");
      data.setVersionId(DemoArtifactToken.SAW_Product_Line);
      data.setAiIds(Arrays.asList(DemoArtifactToken.SAW_Code_AI.getIdString()));

      NewActionData newData = atsApi.getServerEndpoints().getActionEndpoint().createAction(data);
      Assert.assertTrue(newData.getRd().toString(), newData.getRd().isSuccess());

      Assert.assertTrue(newData.getActResult().getAction().isValid());
      // This field is only loaded on return to IDE client, not when call endpoint directly
      Assert.assertNull(newData.getActResult().getAtsAction());

      Assert.assertEquals(1, newData.getActResult().getTeamWfs().size());
      Assert.assertTrue(newData.getActResult().getTeamWfs().iterator().next().isValid());
      // This field is only loaded on return to IDE client, not when call endpoint directly
      Assert.assertEquals(0, newData.getActResult().getAtsTeamWfs().size());

      IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) atsApi.getWorkItemService().getWorkItem(
         newData.getActResult().getTeamWfs().iterator().next().getId());

      Assert.assertNotNull(teamWf);
      Assert.assertEquals("My Action", teamWf.getName());
      Assert.assertEquals("desc", teamWf.getDescription());
      Assert.assertEquals(createDate, teamWf.getCreatedDate());
      Assert.assertEquals(DemoUsers.Alex_Kay, teamWf.getCreatedBy().getStoreObject());
      Assert.assertEquals(DemoUsers.Joe_Smith, atsApi.getQueryServiceIde().getArtifact(teamWf).getLastModifiedBy());
      Assert.assertEquals(ChangeTypes.Improvement.name(),
         atsApi.getAttributeResolver().getSoleAttributeValue(teamWf, AtsAttributeTypes.ChangeType, ""));
      Assert.assertEquals("3",
         atsApi.getAttributeResolver().getSoleAttributeValue(teamWf, AtsAttributeTypes.Priority, ""));
      Assert.assertEquals(DemoArtifactToken.SAW_Code_AI, teamWf.getActionableItems().iterator().next());
      Assert.assertEquals(needBy,
         atsApi.getAttributeResolver().getSoleAttributeValue(teamWf, AtsAttributeTypes.NeedBy, ""));

      AtsDeleteManager.handleDeletePurgeAtsObject(Arrays.asList(atsApi.getQueryServiceIde().getArtifact(teamWf)), true,
         DeleteOption.Delete);
   }

   @Test
   public void testCreateActionFromFormData() throws Exception {

      TeamWorkFlowArtifact teamWfArt = null;
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
         try (Response response = post(form);) {

            Assert.assertEquals(Status.SEE_OTHER.getStatusCode(), response.getStatus());
            if (response.getLocation() != null) {
               String urlStr = response.getLocation().toString();
               URL url = new URL(urlStr);
               String path = url.getPath();
               Assert.assertTrue(String.format("Invalid url [%s]", url), path.contains("/ats/ui/action/TW"));
               String atsId = path.replaceFirst("^.*/", "");

               teamWfArt = (TeamWorkFlowArtifact) atsApi.getWorkItemService().getWorkItemByAtsId(atsId);
               Assert.assertNotNull(teamWfArt);
            }
         } catch (Exception ex) {
            Assert.fail(Lib.exceptionToString(ex));
         }
      } catch (Exception ex) {
         Assert.fail(Lib.exceptionToString(ex));
      }

      // Cleanup test
      if (teamWfArt != null) {
         IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getName());
         changes.deleteArtifact(teamWfArt.getParentAction().getStoreObject());
         changes.deleteArtifact((ArtifactId) teamWfArt);
         changes.execute();
      }
   }

   @Test
   public void testCreateEmptyAction() throws Exception {
      AtsApi atsApi = AtsApiService.get();

      IAtsActionableItem ai = atsApi.getActionableItemService().getActionableItemById(DemoArtifactToken.SAW_Code_AI);
      String aiStr = ai.getIdString();

      String newAction = atsApi.getServerEndpoints().getActionEndpoint().createEmptyAction(
         AtsApiService.get().getUserService().getCurrentUserId(), aiStr, "New Action");

      JsonNode root = jaxRsApi.readTree(newAction);
      Long id = root.path("id").asLong();

      IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) atsApi.getWorkItemService().getWorkItem(id);
      Assert.assertNotNull(teamWf);

      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getName());
      changes.deleteArtifact(teamWf.getParentAction().getStoreObject());
      changes.deleteArtifact(teamWf.getStoreObject());
      changes.executeIfNeeded();
   }

   private void postAndValidateResponse(String errorMessage, Form form) throws IOException {
      Response response = post(form);
      validateResponse(response, errorMessage);
   }

   private Response post(Form form) {
      WebTarget target = jaxRsApi.newTargetNoRedirect("ats/action");
      return target.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.form(form));
   }

   private void validateResponse(Response response, String errorMessage) throws IOException {
      Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
      Assert.assertEquals(errorMessage, Lib.inputStreamToString((InputStream) response.getEntity()));
   }

   @Test
   public void testJournalFormPost() {
      AtsApi atsApi = AtsApiService.get();
      AtsActionEndpointApi actionEp = atsApi.getServerEndpoints().getActionEndpoint();
      IAtsTeamWorkflow codeWf = DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      String atsId = codeWf.getAtsId();

      MultivaluedMap<String, String> form = new MultivaluedHashMap<String, String>();
      form.add("atsid", atsId);
      form.add("desc", "testJournalFormPost");
      form.add("useraid", DemoUsers.Joe_Smith.getIdString());

      try (Response resp = actionEp.journal(form);) {
         Assert.assertTrue(Status.OK.getStatusCode() == resp.getStatus());

         JournalData jd = actionEp.getJournalData(atsId);
         Assert.assertTrue(jd.getResults().isSuccess());
         Assert.assertFalse(jd.getSubscribed().isEmpty());
         Assert.assertTrue(jd.getCurrentMsg().contains("testJournalFormPost"));
      }
   }

   @Test
   public void testJournal() {
      AtsApi atsApi = AtsApiService.get();
      AtsActionEndpointApi actionEp = atsApi.getServerEndpoints().getActionEndpoint();
      IAtsTeamWorkflow codeWf = DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      String atsId = codeWf.getAtsId();

      JournalData jd = actionEp.getJournalData(atsId);
      Assert.assertTrue(jd.getResults().isSuccess());
      Assert.assertTrue(jd.getSubscribed().isEmpty());
      Assert.assertTrue(jd.getTransaction().isInvalid());

      jd = new JournalData();
      String addMsg = "My Journal Entry";
      jd.setAddMsg(addMsg);
      jd.setUser(atsApi.getUserService().getCurrentUser());
      jd = actionEp.addJournal(atsId, jd);
      Assert.assertTrue(jd.getResults().isSuccess());
      Assert.assertTrue(jd.getSubscribed().size() == 1);
      // Transaction comes back with valid add
      Assert.assertTrue(jd.getTransaction().isValid());
      Assert.assertTrue(jd.getCurrentMsg().contains(addMsg));

      jd = actionEp.getJournalData(atsId);
      Assert.assertTrue(jd.getResults().isSuccess());
      Assert.assertTrue(jd.getSubscribed().size() == 1);
      // No transaction comes back with just getJournalData
      Assert.assertTrue(jd.getTransaction().isInvalid());
      Assert.assertTrue(jd.getCurrentMsg().contains(addMsg));

      String html = actionEp.getJournalText(atsId);
      Assert.assertTrue(html.contains(addMsg));

      AtsActionUiEndpointApi actionUiEp = atsApi.getServerEndpoints().getActionUiEndpoint();
      html = actionUiEp.getJournal(atsId, atsApi.getUserService().getCurrentUser().getIdString());
      Assert.assertTrue(html.contains(addMsg));
      Assert.assertTrue(html.contains(atsApi.getUserService().getCurrentUser().getName()));

   }

   /**
    * POST "visited/{userArtId} <br/>
    * GET "visited/{userArtId}
    */
   @Test
   public void testVisited() {
      AtsApi atsApi = AtsApiService.get();
      AtsActionEndpointApi actionEp = atsApi.getServerEndpoints().getActionEndpoint();

      RecentlyVisitedItems visitedBefore =
         actionEp.getVisited(atsApi.getUserService().getCurrentUser().getArtifactId());
      Assert.assertEquals(0, visitedBefore.getVisited().size());

      IAtsTeamWorkflow codeWf = DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      RecentlyVisitedItems visitedItems = new RecentlyVisitedItems();
      visitedItems.addVisited(codeWf);

      actionEp.storeVisited(atsApi.getUserService().getCurrentUser().getArtifactId(), visitedItems);

      RecentlyVisitedItems visitedAfter = actionEp.getVisited(atsApi.getUserService().getCurrentUser().getArtifactId());
      Assert.assertEquals(1, visitedAfter.getVisited().size());
      RecentlyVisistedItem item = visitedAfter.getVisited().iterator().next();
      Assert.assertEquals(codeWf.getId(), item.getWorkflowId());
      Assert.assertEquals(codeWf.getName(), item.getWorkflowName());
      Assert.assertEquals(codeWf.getArtifactType().getId(), item.getArtifactTypeId());

      IAtsTeamWorkflow testWf = DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Test);
      visitedAfter.addVisited(testWf);
      // code is first
      Assert.assertEquals(codeWf.getId(), visitedAfter.getVisited().iterator().next().getWorkflowId());

      // Should store both in order
      actionEp.storeVisited(atsApi.getUserService().getCurrentUser().getArtifactId(), visitedAfter);

      RecentlyVisitedItems visitedAfter2 =
         actionEp.getVisited(atsApi.getUserService().getCurrentUser().getArtifactId());
      Assert.assertEquals(2, visitedAfter2.getVisited().size());
      // code is first
      Assert.assertEquals(codeWf.getId(), visitedAfter2.getVisited().iterator().next().getWorkflowId());

      List<RecentlyVisistedItem> reverseVisited = visitedAfter2.getReverseVisited();
      Assert.assertEquals(testWf.getId(), reverseVisited.iterator().next().getWorkflowId());

   }

   @Test
   public void testBranchAndCommitOfPlArbWorkflow() {
      AtsApi atsApi = AtsApiService.get();
      IAtsActionableItem plAi =
         atsApi.getActionableItemService().getActionableItemById(DemoArtifactToken.SAW_PL_ARB_AI);
      String title = getClass().getSimpleName() + " - testBranchAndCommit";
      NewActionData data = atsApi.getActionService().createActionData(title, title, "description", Arrays.asList(plAi)) //
         .andVersion(DemoArtifactToken.SAW_Bld_2);

      // Clear user and originator as endpoint should handle if not sent in
      data.setAsUser(ArtifactId.SENTINEL);
      data.setCreatedByUserArtId(ArtifactId.SENTINEL.getIdString());

      NewActionResult actResult = atsApi.getServerEndpoints().getActionEndpoint().createActionAndWorkingBranch(data);
      Assert.assertTrue(actResult.getResults().toString(), actResult.getResults().isSuccess());
      ArtifactId teamWfId = actResult.getTeamWfs().iterator().next();
      IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) atsApi.getWorkItemService().getWorkItem(teamWfId.getId());
      Assert.assertNotNull(teamWf);

      // Ensure that even though user wasn't sent it, they are originator and author of commit
      Assert.assertEquals(DemoUsers.Joe_Smith, teamWf.getCreatedBy());
      TransactionId tx1 = actResult.getTransaction();
      TransactionRecord txRec = atsApi.getStoreService().getTransaction(tx1);
      Assert.assertEquals(DemoUsers.Joe_Smith.getId(), txRec.getAuthor().getId());

      Collection<BranchToken> workingBranches = BranchManager.getBranchesByAssocArt(teamWf.getStoreObject());
      BranchToken workingBranch = workingBranches.iterator().next();
      Assert.assertNotNull(workingBranch);

      ArtifactToken robotArt = atsApi.getQueryService().getArtifactByName(CoreArtifactTypes.SoftwareRequirementMsWord,
         "Robot Interfaces", workingBranch);
      Assert.assertNotNull(robotArt);

      IAtsChangeSet changes = atsApi.createChangeSet(title, workingBranch);
      changes.addAttribute(robotArt, CoreAttributeTypes.StaticId, "new static id");
      TransactionToken tx2 = changes.execute();
      Assert.assertTrue(tx2.isValid());

      BranchToken parentBranch = atsApi.getBranchService().getBranch(DemoArtifactToken.SAW_Bld_2.getBranch());

      // Needed approval for commit
      changes = atsApi.createChangeSet(title);
      changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.ProductLineApprovedBy, DemoUsers.Joe_Smith.getId());
      changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.ProductLineApprovedDate, new Date());
      changes.execute();

      XResultData rd =
         atsApi.getServerEndpoints().getActionEndpoint().commitWorkingBranch(teamWf.getIdsStr(), parentBranch);
      Assert.assertTrue(rd.toString(), rd.isSuccess());

      ArtifactToken robotArtNew = atsApi.getQueryService().getArtifactByName(
         CoreArtifactTypes.SoftwareRequirementMsWord, "Robot Interfaces", parentBranch);
      Assert.assertNotNull(robotArtNew);
      Assert.assertTrue(
         atsApi.getAttributeResolver().getAttributesToStringList(robotArtNew, CoreAttributeTypes.StaticId).contains(
            "new static id"));

      // Clear associated art from working branch so don't get error in future tests from deleted
      atsApi.getBranchService().setAssociatedArtId(workingBranch, AtsCoreUsers.SYSTEM_USER.getArtifactId());
      atsApi.getBranchService().deleteBranch(workingBranch);
      AtsTestUtil.cleanupSimpleTest(title);
   }

}
