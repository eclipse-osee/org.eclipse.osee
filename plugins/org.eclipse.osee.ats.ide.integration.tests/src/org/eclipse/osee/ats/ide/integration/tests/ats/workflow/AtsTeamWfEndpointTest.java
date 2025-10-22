/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.workflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.util.AtsTopicEvent;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.AtsTeamWfEndpointApi;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.WorkflowAttachment;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.resource.AbstractRestTest;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.ide.util.AtsApiIde;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test case for {@link AtsTeamWfEndpoint}
 *
 * @author Stephen Molaro
 * @author Kenn Luecke
 */
public class AtsTeamWfEndpointTest extends AbstractRestTest {

   private static AtsTeamWfEndpointApi teamWfEp;
   private static AtsApiIde atsApi;
   private static TeamWorkFlowArtifact codeTeamWorkFlow;
   private static JaxRsApi jaxRsApi;

   @BeforeClass
   public static void setup() {
      atsApi = AtsApiService.get();
      teamWfEp = AtsApiService.get().getServerEndpoints().getTeamWfEp();
      codeTeamWorkFlow = (TeamWorkFlowArtifact) DemoUtil.getSawCodeUnCommittedWf();

      jaxRsApi = ServiceUtil.getOseeClient().jaxRsApi();
   }

   //   @Path("{aiId}/version")
   @Test
   public void testGetVersionsbyTeamDefinition() {
      IAtsActionableItem ai = atsApi.getActionableItemService().getActionableItemById(DemoArtifactToken.SAW_Code_AI);
      Assert.assertNotNull(ai);

      testUrl("ats/teamwf/" + ai.getIdString() + "/version", 3, "SAW_Bld_1", "name", true);
   }

   //   @Path("{id}")
   @Test
   public void testGetTeamWorkflow() {
      IAtsTeamWorkflow buttonSTeamWf = DemoTestUtil.getButtonSTeamWf();
      Assert.assertNotNull(buttonSTeamWf);

      testUrl("ats/teamwf/" + buttonSTeamWf.getAtsId(), 44);
   }

   //   @Path("ids/{id}/")
   @Test
   public void testGetTeamWorkflows() {
      testUrl("ats/teamwf/ids/TW5,TW6", 2);

      testUrl(String.format("ats/teamwf/ids/%s,%s", DemoArtifactToken.ButtonSDoesntWorkOnHelp_TeamWf.getIdString(),
         DemoArtifactToken.ButtonWDoesntWorkOnSituationPage_TeamWf.getIdString()), 2);
   }

   //   @Path("{id}/changeTypes")
   @Test
   public void testGetChangeTypes() {
      Collection<ChangeTypes> changeTypes =
         teamWfEp.getChangeTypes(DemoUtil.getSawCodeUnCommittedWf().getArtifactId().getIdString(), "true");
      Assert.assertTrue(changeTypes.size() == 4);
   }

   //   @Path("{id}/goals")
   @Test
   public void testGetGoals() {
      IAtsTeamWorkflow buttonSTeamWf = DemoTestUtil.getButtonSTeamWf();
      testUrl("ats/teamwf/" + buttonSTeamWf.getAtsId() + "/goal", 0);
      IAtsTeamWorkflow buttonWTeamWf = DemoTestUtil.getButtonWTeamWf();
      testUrl("ats/teamwf/" + buttonWTeamWf.getAtsId() + "/goal", 1);
   }

   //   @Path("details/{id}")
   @Test
   public void testGetTeamWorkflowDetails() {
      IAtsTeamWorkflow buttonSTeamWf = DemoTestUtil.getButtonSTeamWf();
      Assert.assertNotNull(buttonSTeamWf);

      testUrl("ats/teamwf/details/" + buttonSTeamWf.getId(), 50);
   }

   //   @Path("release/{release}")
   @Test
   public void testGetWfByRelease() {
      IAtsChangeSet changes = atsApi.getStoreService().createAtsChangeSet(
         getClass().getSimpleName() + " - Create Release Artifact", AtsCoreUsers.SYSTEM_USER);
      ArtifactToken release = changes.createArtifact(AtsArtifactTypes.ReleaseArtifact, "G123456.0");
      changes.relate(codeTeamWorkFlow.getArtifactId(), AtsRelationTypes.TeamWorkflowToRelease_Release, release);
      changes.execute();

      Collection<ArtifactToken> workflows = teamWfEp.getWfByRelease("G123456.0");
      Assert.assertTrue(workflows.contains(codeTeamWorkFlow));

      changes = atsApi.getStoreService().createAtsChangeSet(getClass().getSimpleName() + " - Cleanup Releases",
         AtsCoreUsers.SYSTEM_USER);
      changes.unrelate(codeTeamWorkFlow.getArtifactId(), AtsRelationTypes.TeamWorkflowToRelease_Release, release);
      changes.deleteArtifact(release);
      changes.execute();
   }

   @Test
   public void testGetWfByReleaseById() {
      IAtsChangeSet changes = atsApi.getStoreService().createAtsChangeSet(
         getClass().getSimpleName() + " - Create Release Artifact", AtsCoreUsers.SYSTEM_USER);
      ArtifactToken release = changes.createArtifact(AtsArtifactTypes.ReleaseArtifact, "G123456.0");
      changes.relate(codeTeamWorkFlow.getArtifactId(), AtsRelationTypes.TeamWorkflowToRelease_Release, release);
      changes.execute();

      Collection<ArtifactToken> workflows = teamWfEp.getWfByReleaseById(release);
      Assert.assertTrue(workflows.contains(codeTeamWorkFlow));

      changes = atsApi.getStoreService().createAtsChangeSet(getClass().getSimpleName() + " - Cleanup Releases",
         AtsCoreUsers.SYSTEM_USER);
      changes.unrelate(codeTeamWorkFlow.getArtifactId(), AtsRelationTypes.TeamWorkflowToRelease_Release, release);
      changes.deleteArtifact(release);
      changes.execute();
   }

   //    @Path("build/{build}")
   @Test
   public void testRelateReleaseToWorkflow() {
      String changeId = "IO98293838";
      List<String> changeIds = new ArrayList<>();
      changeIds.add(changeId);

      IAtsChangeSet changes = atsApi.getStoreService().createAtsChangeSet(
         getClass().getSimpleName() + " - Create Release artifact/Set GitChangeId", AtsCoreUsers.SYSTEM_USER);
      ArtifactToken release = changes.createArtifact(AtsArtifactTypes.ReleaseArtifact, "G123456.8");
      changes.addAttribute(codeTeamWorkFlow.getArtifactId(), CoreAttributeTypes.GitChangeId, changeId);
      changes.execute();

      XResultData rd = teamWfEp.relateReleaseToWorkflow(release.getName(), changeIds);
      TransactionId txId = TransactionId.SENTINEL;
      if (Strings.isNumeric(rd.getTxId())) {
         txId = TransactionId.valueOf(rd.getTxId());
      }
      Collection<IAtsWorkItem> workItems = new ArrayList<>();
      workItems.add(codeTeamWorkFlow);

      atsApi.getEventService().postAtsWorkItemTopicEvent(AtsTopicEvent.WORK_ITEM_MODIFIED, workItems, txId);
      atsApi.getStoreService().reload(workItems);

      boolean isRelease = atsApi.getRelationResolver().areRelated(codeTeamWorkFlow.getArtifactId(),
         AtsRelationTypes.TeamWorkflowToRelease_Release, release);
      Assert.assertTrue(isRelease);

      // delete change that was just added
      changes = atsApi.getStoreService().createAtsChangeSet(getClass().getSimpleName() + " - Cleanup Releases",
         AtsCoreUsers.SYSTEM_USER);
      changes.unrelate(codeTeamWorkFlow.getArtifactId(), AtsRelationTypes.TeamWorkflowToRelease_Release, release);
      changes.deleteArtifact(release);
      changes.deleteAttributes(codeTeamWorkFlow.getArtifactId(), CoreAttributeTypes.GitChangeId);
      changes.execute();

   }

   @Test
   public void testGetWfAttachments() {
      // Create attachments
      String json = OseeInf.getResourceContents("create_attachments.json", getClass());
      Response response = jaxRsApi.newTarget("orcs/txs").request(MediaType.APPLICATION_JSON).post(Entity.json(json));
      assertEquals(Family.SUCCESSFUL, response.getStatusInfo().getFamily());

      List<WorkflowAttachment> attachments =
         teamWfEp.getWfAttachments(ArtifactId.valueOf(DemoArtifactToken.SAW_UnCommited_Req_TeamWf.getId()));

      int attachmentsCount = attachments.size();
      int expectedCount = 2;

      assertEquals("Expected " + expectedCount + " attachments, but there were " + attachmentsCount, expectedCount,
         attachmentsCount);

      WorkflowAttachment expectedAttachment = attachments.get(0);
      WorkflowAttachment attachment = teamWfEp.getWfAttachment(ArtifactId.valueOf(expectedAttachment.getId()));

      String expectedName = expectedAttachment.getName();
      String name = attachment.getName();

      assertNotNull(name);
      assertEquals("Expected attachment name: " + expectedName + " but was: " + name, expectedName, name);
   }
}
