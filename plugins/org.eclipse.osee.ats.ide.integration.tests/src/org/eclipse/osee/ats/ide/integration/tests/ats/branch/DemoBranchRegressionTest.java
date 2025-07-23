/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.branch;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskData;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.ats.core.task.CreateChangeReportTaskCommitHook;
import org.eclipse.osee.ats.core.task.TaskSetDefinitionTokensDemo;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TeamWorkFlowManager;
import org.eclipse.osee.ats.ide.branch.BranchRegressionTest;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.junit.Assert;

/**
 * @author Donald G. Dunne
 */
public class DemoBranchRegressionTest extends BranchRegressionTest {

   private final List<String> BranchNames = Arrays.asList(DemoBranches.SAW_Bld_1.getName(),
      DemoBranches.SAW_Bld_2.getName(), DemoBranches.SAW_Bld_3.getName());
   private List<String> taskNames;
   private List<String> firstSecondExpected;
   private List<String> thirdFourthFifthExpected;
   private List<String> createReqArtToDelExpected;
   private Set<String> deleteReqArtToDelExpected;
   private List<String> changeNameToReqArtToDelExpected;
   private Collection<IAtsTeamWorkflow> teamWfs;

   public DemoBranchRegressionTest() {
      super();
      ArtifactModifiedNames.addAll(Arrays.asList("Events", DemoArtifactToken.MsWordHeadingNoTask.getName()));
      NonRelArtifactModifedNames.addAll(Arrays.asList(DemoArtifactToken.MsWordHeadingNoTask.getName(), "Events"));
   }

   @Override
   public void testSetup() {
      super.testSetup();
   }

   @Override
   public void testCleanup() {
      super.testCleanup();
      AtsTestUtil.validateArtifactCache();
   }

   @Override
   public void testCreateAction() {
      AtsApi atsApi = AtsApiService.get();

      Collection<IAtsActionableItem> aias = DemoUtil.getActionableItems(DemoArtifactToken.SAW_Requirements_AI,
         DemoArtifactToken.SAW_Code_AI, DemoArtifactToken.SAW_Test_AI);

      Date createdDate = new Date();
      AtsUser createdBy = AtsApiService.get().getUserService().getCurrentUser();
      String priority = "1";

      NewActionData data = atsApi.getActionService() //
         .createActionData(getClass().getSimpleName(), getClass().getSimpleName(), "Problem with the Diagram View") //
         .andAis(aias).andChangeType(ChangeTypes.Problem).andPriority(priority) //
         .andCreatedBy(createdBy) //
         .andCreatedDate(createdDate) //
         .andAttr(AtsAttributeTypes.LegacyPcrId, getLegacyPcrId()) //
         .andVersion(DemoArtifactToken.SAW_Bld_2);
      NewActionData newActionData = atsApi.getActionService().createAction(data);
      Assert.assertTrue(newActionData.getRd().toString(), newActionData.getRd().isSuccess());

      teamWfs = newActionData.getActResult().getAtsTeamWfs();

      Assert.assertEquals(3, teamWfs.size());
      testTeamWorkflows(teamWfs);

      IAtsChangeSet changes = atsApi.createChangeSet(getClass().getSimpleName());
      changes = AtsApiService.get().createChangeSet(getClass().getSimpleName() + " - transition req wf");
      TeamWorkFlowManager mgr = new TeamWorkFlowManager(reqTeamWf, AtsApiService.get());
      mgr.transitionTo(TeamState.Implement, AtsApiService.get().getUserService().getCurrentUser(), false, changes);
      changes.execute();
   }

   @Override
   protected void createThirdFourthFifthReqArt() {
      super.createThirdFourthFifthReqArt();

      // add ms word heading which should be ignored; no task should be created
      Artifact headingArt = createSoftwareArtifact(DemoArtifactToken.MsWordHeadingNoTask, softReqArt,
         getThirdArtifactCscis(), workingBranch);
      Assert.assertNotNull(headingArt);

      // add sw req with paragraph only change; no task should be created
      Artifact existingEventsSwReq =
         ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.SoftwareRequirementMsWord, "Events", workingBranch);
      Assert.assertNotNull(existingEventsSwReq);
      existingEventsSwReq.setSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "2.2.2");
      existingEventsSwReq.persist(getClass().getSimpleName());

   }

   private IAtsTeamWorkflow getCodeTeamWf() {
      IAtsTeamWorkflow codeWf = null;
      for (IAtsTeamWorkflow teamWf : teamWfs) {
         if (teamWf.isOfType(getCodeTeamWfArtType())) {
            codeWf = teamWf;
            break;
         }
      }
      Assert.assertNotNull(codeWf);
      return codeWf;
   }

   @Override
   public String getLegacyPcrId() {
      return "2019";
   }

   @Override
   public String getBranchNameContains() {
      return getClass().getSimpleName();
   }

   @Override
   public String[] getPreBranchCscis() {
      return new String[] {"Communication", "Aircraft Systems"};
   }

   @Override
   public String[] getFirstArtifactCscis() {
      return new String[] {"Communication", "Aircraft Systems"};
   }

   @Override
   public String[] getSecondArtifactCscis() {
      return new String[] {"Communication", "Navigation"};
   }

   @Override
   public String[] getThirdArtifactCscis() {
      return new String[] {"Aircraft Systems"};
   }

   @Override
   public String[] getInBranchArtifactCscis() {
      return new String[] {"Aircraft Systems"};
   }

   @Override
   public AttributeTypeId getCsciAttribute() {
      return CoreAttributeTypes.Partition;
   }

   @Override
   public BranchToken getProgramBranch() {
      return DemoBranches.SAW_Bld_2;
   }

   @Override
   public List<String> getBranchNames() {
      return BranchNames;
   }

   @Override
   public int getExpectedBranchConfigItems() {
      return 3;
   }

   @Override
   public ArtifactTypeToken getCodeTeamWfArtType() {
      return AtsArtifactTypes.DemoCodeTeamWorkflow;
   }

   @Override
   public ArtifactTypeToken getTestTeamWfArtType() {
      return AtsArtifactTypes.DemoTestTeamWorkflow;
   }

   @Override
   protected Collection<String> getFinalTaskNames() {
      if (taskNames == null) {
         taskNames = Arrays.asList( //
            "Handle Add change to [Fifth Artifact - Unspecified CSCI]", //
            "Handle Add change to [First Artifact]", //
            "Handle Add change to [Fourth Artifact - No CSCI]", //
            "Handle Add change to [In-Branch Artifact to Delete Changed]", //
            "Handle Add change to [In-Branch Artifact to Delete]", //
            "Handle Add change to [Second Artifact]", //
            "Handle Add change to [Third Artifact]", //
            "Handle Deleted change to [Pre-Branch Artifact to Delete] (Deleted)", //
            "Handle Relation change to [Fifth Artifact - Unspecified CSCI]", //
            "Handle Relation change to [First Artifact]", //
            "Handle Relation change to [Fourth Artifact - No CSCI]", //
            "Handle Relation change to [In-Branch Artifact to Delete Changed]", //
            "Handle Relation change to [In-Branch Artifact to Delete]", //
            "Handle Relation change to [System Req Artifact]", //
            "Handle Relation change to [Second Artifact]", //
            "Handle Relation change to [Software Requirements]", //
            "Handle Relation change to [Third Artifact]", //
            "My Manual Task" //
         );
      }
      return taskNames;
   }

   private IAtsTeamWorkflow runCreateCodeTestTasks() {
      IAtsTeamWorkflow codeWf = getCodeTeamWf();
      AtsUser asUser = AtsApiService.get().getUserService().getCurrentUser();

      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      ChangeReportTaskData data = CreateChangeReportTaskCommitHook.runChangeReportTaskOperation(codeWf,
         TaskSetDefinitionTokensDemo.SawCreateTasksFromReqChanges, false, changes, asUser);
      changes.executeIfNeeded();

      Assert.assertFalse(data.getResults().toString(), data.getResults().isErrors());
      return codeWf;
   }

   private void testTasksAgainstExpected(Collection<String> expected) {
      IAtsTeamWorkflow codeWf = runCreateCodeTestTasks();

      Collection<IAtsTask> tasks = AtsApiService.get().getTaskService().getTasks(codeWf);

      int expectedSize = expected.size();
      int actualSize = tasks.size();
      Assert.assertEquals(expectedSize, actualSize);

      for (IAtsTask task : tasks) {
         boolean contains = expected.contains(task.getName());
         Assert.assertTrue(String.format("Expected task [%s] and not found in %s", task.getName(), expected), contains);

         boolean deReferenced = AtsApiService.get().getTaskService().isAutoGenDeReferenced(task);
         boolean autoGenTask = AtsApiService.get().getTaskService().isAutoGen(task);

         if (autoGenTask) {
            Assert.assertFalse(deReferenced);
         } else {
            Assert.assertTrue(deReferenced);
         }
      }
   }

   @Override
   public void testCodeTaskCreationAfterFirstAndSecond() {
      firstSecondExpected = new ArrayList<String>();
      firstSecondExpected.addAll(Arrays.asList( //
         "Handle Add change to [First Artifact]", //
         "Handle Add change to [Second Artifact]", //
         "Handle Relation change to [First Artifact]", //
         "Handle Relation change to [Second Artifact]", //
         "Handle Relation change to [Software Requirements]", //
         "My Manual Task"));

      testTasksAgainstExpected(firstSecondExpected);
   }

   @Override
   public void testCodeTaskCreationAfterThirdFourthFifth() {
      thirdFourthFifthExpected = new ArrayList<>();
      thirdFourthFifthExpected.addAll(Arrays.asList( //
         "Handle Add change to [Third Artifact]", //
         "Handle Relation change to [Third Artifact]", //
         "Handle Add change to [Fifth Artifact - Unspecified CSCI]",
         "Handle Relation change to [Fifth Artifact - Unspecified CSCI]",
         "Handle Add change to [Fourth Artifact - No CSCI]", //
         "Handle Relation change to [Fourth Artifact - No CSCI]"));
      thirdFourthFifthExpected.addAll(firstSecondExpected);

      testTasksAgainstExpected(thirdFourthFifthExpected);
   }

   @Override
   public void testCodeTaskCreationAfterCreateReqArtToDelete() {
      createReqArtToDelExpected = new ArrayList<>();
      createReqArtToDelExpected.addAll(Arrays.asList( //
         "Handle Add change to [In-Branch Artifact to Delete]",
         "Handle Relation change to [In-Branch Artifact to Delete]"));
      createReqArtToDelExpected.addAll(thirdFourthFifthExpected);

      testTasksAgainstExpected(createReqArtToDelExpected);

      testAtsActionsDetailsDerivedRestCall();
   }

   public void testAtsActionsDetailsDerivedRestCall() {
      // Test DerivedFrom with ATS Id
      String url = String.format("ats/action/%s/details", codeTeamWf.getAtsId());
      JsonNode action = testActionRestCall(url, 1);
      JsonNode derived = action.get("DerivedFrom");
      Assert.assertNotNull(derived);
      JsonNode derived1 = derived.get(0);
      Assert.assertEquals(reqTeamWf.getAtsId(), derived1.get("AtsId").asText());
      Assert.assertEquals(reqTeamWf.getIdString(), derived1.get("id").asText());
      Assert.assertEquals(reqTeamWf.getName(), derived1.get("name").asText());
      Assert.assertEquals(reqTeamWf.getArtifactType().toStringWithId(), derived1.get("type").asText());
      Assert.assertEquals(reqTeamWf.getCurrentStateName(), derived1.get("state").asText());

      // Test DerivedFrom with id
      String url2 = String.format("ats/action/%s/details", codeTeamWf.getIdString());
      JsonNode action2 = testActionRestCall(url2, 1);
      JsonNode derived2 = action2.get("DerivedFrom");
      Assert.assertNotNull(derived2);
      Assert.assertEquals(reqTeamWf.getAtsId(), derived2.get(0).get("AtsId").asText());

      // Test derivedTo
      String url3 = String.format("ats/action/%s/details", reqTeamWf.getIdString());
      JsonNode action3 = testActionRestCall(url3, 1);
      JsonNode derived3 = action3.get("DerivedTo");
      Assert.assertNotNull(derived3);
      Assert.assertEquals(codeTeamWf.getAtsId(), derived3.get(0).get("AtsId").asText());
   }

   private JsonNode testActionRestCall(String url, int size) {
      WebTarget target = AtsApiService.get().jaxRsApi().newTargetQuery(url);
      String json = target.request(MediaType.APPLICATION_JSON_TYPE).get().readEntity(String.class);
      JsonNode arrayNode = AtsApiService.get().jaxRsApi().readTree(json);
      Assert.assertEquals(size, arrayNode.size());
      return arrayNode.get(0);
   }

   @Override
   public void testCodeTestCreationAfterChangeToReqArtifactToDelete() {
      changeNameToReqArtToDelExpected = new ArrayList<>();
      /**
       * If task generated and artifact name changes, old task will be marked as de-referenced and new task with new
       * name generated.
       */
      changeNameToReqArtToDelExpected.addAll(Arrays.asList( //
         "Handle Add change to [In-Branch Artifact to Delete Changed]",
         "Handle Relation change to [In-Branch Artifact to Delete Changed]"));
      changeNameToReqArtToDelExpected.addAll(createReqArtToDelExpected);
      testTasksAgainstExpected(changeNameToReqArtToDelExpected);

      IAtsTeamWorkflow codeWf = runCreateCodeTestTasks();
      Collection<IAtsTask> tasks = AtsApiService.get().getTaskService().getTasks(codeWf);
      int count = 0;
      for (IAtsTask task : tasks) {
         if (task.getName().contains("[In-Branch Artifact to Delete]")) {
            count++;
            testTaskIsDeReferenced(task);
         }
      }
      Assert.assertEquals(2, count);
   }

   @Override
   public void testCodeTestCreationAfterDeleteReqArtifactToDelete() {
      deleteReqArtToDelExpected = new HashSet<>();
      deleteReqArtToDelExpected.addAll(createReqArtToDelExpected);
      deleteReqArtToDelExpected.addAll(Arrays.asList( //
         "Handle Add change to [In-Branch Artifact to Delete]",
         "Handle Relation change to [In-Branch Artifact to Delete]",
         "Handle Add change to [In-Branch Artifact to Delete Changed]",
         "Handle Relation change to [In-Branch Artifact to Delete Changed]"));

      IAtsTeamWorkflow codeWf = runCreateCodeTestTasks();
      Collection<IAtsTask> tasks = AtsApiService.get().getTaskService().getTasks(codeWf);
      int count = 0;
      for (IAtsTask task : tasks) {
         if (task.getName().contains("[In-Branch Artifact to Delete]") || task.getName().contains(
            "[In-Branch Artifact to Delete Changed]")) {
            count++;
            testTaskIsDeReferenced(task);
         }
      }
      Assert.assertEquals(4, count);
   }

   private void testTaskIsDeReferenced(IAtsTask task) {
      List<IAtsWorkItem> reload = AtsApiService.get().getStoreService().reload(Collections.singleton(task));
      task = (IAtsTask) reload.iterator().next();

      // Test that task has AutoGenTask static id removed
      Assert.assertTrue(task.getTags().isEmpty());
      Assert.assertTrue(AtsApiService.get().getTaskService().isAutoGenDeReferenced(task));
   }
}
