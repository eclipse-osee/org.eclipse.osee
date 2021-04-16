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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.demo.AtsDemoOseeTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskData;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.INewActionListener;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.core.task.CreateChangeReportTaskTransitionHook;
import org.eclipse.osee.ats.core.task.TaskSetDefinitionTokensDemo;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TeamWorkFlowManager;
import org.eclipse.osee.ats.ide.branch.BranchRegressionTest;
import org.eclipse.osee.ats.ide.demo.config.DemoDbUtil;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
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
   private ActionResult actionResult;
   private List<String> firstSecondExpected;
   private List<String> thirdFourthFifthExpected;
   private List<String> createReqArtToDelExpected;
   private Set<String> deleteReqArtToDelExpected;
   private List<String> changeNameToReqArtToDelExpected;

   public DemoBranchRegressionTest() {
      super();
      ArtifactModifiedNames.addAll(
         Arrays.asList(DemoArtifactToken.EventsSwReq.getName(), DemoArtifactToken.MsWordHeadingNoTask.getName()));
      NonRelArtifactModifedNames.addAll(
         Arrays.asList(DemoArtifactToken.MsWordHeadingNoTask.getName(), DemoArtifactToken.EventsSwReq.getName()));
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
      String rpcrNumber = getLegacyPcrId();
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());

      Collection<IAtsActionableItem> aias = DemoDbUtil.getActionableItems(DemoArtifactToken.SAW_Requirements_AI,
         DemoArtifactToken.SAW_Code_AI, DemoArtifactToken.SAW_Test_AI);
      Date createdDate = new Date();
      AtsUser createdBy = AtsApiService.get().getUserService().getCurrentUser();
      String priority = "1";

      actionResult = AtsApiService.get().getActionService().createAction(null, getClass().getSimpleName(),
         "Problem with the Diagram View", ChangeType.Problem, priority, false, null, aias, createdDate, createdBy,
         Arrays.asList(new PcrNumberActionListener()), changes);

      if (actionResult.getResults().isErrors()) {
         throw new OseeStateException(actionResult.getResults().toString());
      }
      changes.execute();

      Collection<IAtsTeamWorkflow> teamWfs =
         AtsApiService.get().getQueryService().createQuery(WorkItemType.TeamWorkflow).andAttr(
            AtsAttributeTypes.LegacyPcrId, rpcrNumber).getItems();

      Assert.assertEquals(3, teamWfs.size());
      testTeamWorkflows(teamWfs);

      changes.reset(getClass().getSimpleName() + " - transition req wf");
      TeamWorkFlowManager mgr = new TeamWorkFlowManager(reqTeam, AtsApiService.get());
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
      Artifact existingEventsSwReq = ArtifactQuery.getArtifactFromId(DemoArtifactToken.EventsSwReq, workingBranch);
      Assert.assertNotNull(existingEventsSwReq);
      existingEventsSwReq.setSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "2.2.2");
      existingEventsSwReq.persist(getClass().getSimpleName());

   }

   private IAtsTeamWorkflow getCodeTeamWf() {
      IAtsTeamWorkflow codeWf = null;
      for (IAtsTeamWorkflow teamWf : actionResult.getTeams()) {
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

   private class PcrNumberActionListener implements INewActionListener {

      @Override
      public void teamCreated(IAtsAction action, IAtsTeamWorkflow teamWf, IAtsChangeSet changes) {
         INewActionListener.super.teamCreated(action, teamWf, changes);
         changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.LegacyPcrId, getLegacyPcrId());
         IAtsVersion version = AtsApiService.get().getVersionService().getVersionById(DemoArtifactToken.SAW_Bld_2);
         AtsApiService.get().getVersionService().setTargetedVersion(teamWf, version, changes);
      }

   }

   @Override
   public ArtifactTypeToken getCodeTeamWfArtType() {
      return AtsDemoOseeTypes.DemoCodeTeamWorkflow;
   }

   @Override
   public ArtifactTypeToken getTestTeamWfArtType() {
      return AtsDemoOseeTypes.DemoTestTeamWorkflow;
   }

   @Override
   protected Collection<String> getFinalTaskNames() {
      if (taskNames == null) {
         taskNames = Arrays.asList( //
            "Handle AddMod change to [Fifth Artifact - Unspecified CSCI]", //
            "Handle AddMod change to [First Artifact]", //
            "Handle AddMod change to [Fourth Artifact - No CSCI]", //
            "Handle AddMod change to [In-Branch Artifact to Delete Changed]", //
            "Handle AddMod change to [In-Branch Artifact to Delete]", //
            "Handle AddMod change to [Second Artifact]", //
            "Handle AddMod change to [Third Artifact]", //
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

      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
      ChangeReportTaskData data = CreateChangeReportTaskTransitionHook.runChangeReportTaskOperation(codeWf,
         TaskSetDefinitionTokensDemo.SawCreateTasksFromReqChanges, changes);
      changes.executeIfNeeded();

      Assert.assertFalse(data.getResults().toString(), data.getResults().isErrors());
      return codeWf;
   }

   private void testTasksAgainstExpected(Collection<String> expected) {
      IAtsTeamWorkflow codeWf = runCreateCodeTestTasks();

      Collection<IAtsTask> tasks = AtsApiService.get().getTaskService().getTasks(codeWf);

      Assert.assertEquals(expected.size(), tasks.size());

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
         "Handle AddMod change to [First Artifact]", //
         "Handle AddMod change to [Second Artifact]", //
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
         "Handle AddMod change to [Third Artifact]", //
         "Handle Relation change to [Third Artifact]", //
         "Handle AddMod change to [Fifth Artifact - Unspecified CSCI]",
         "Handle Relation change to [Fifth Artifact - Unspecified CSCI]",
         "Handle AddMod change to [Fourth Artifact - No CSCI]",
         "Handle Relation change to [Fourth Artifact - No CSCI]"));
      thirdFourthFifthExpected.addAll(firstSecondExpected);

      testTasksAgainstExpected(thirdFourthFifthExpected);
   }

   @Override
   public void testCodeTaskCreationAfterCreateReqArtToDelete() {
      createReqArtToDelExpected = new ArrayList<>();
      createReqArtToDelExpected.addAll(Arrays.asList( //
         "Handle AddMod change to [In-Branch Artifact to Delete]",
         "Handle Relation change to [In-Branch Artifact to Delete]"));
      createReqArtToDelExpected.addAll(thirdFourthFifthExpected);

      testTasksAgainstExpected(createReqArtToDelExpected);
   }

   @Override
   public void testCodeTestCreationAfterChangeToReqArtifactToDelete() {
      changeNameToReqArtToDelExpected = new ArrayList<>();
      /**
       * If task generated and artifact name changes, old task will be marked as de-referenced and new task with new
       * name generated.
       */
      changeNameToReqArtToDelExpected.addAll(Arrays.asList( //
         "Handle AddMod change to [In-Branch Artifact to Delete Changed]",
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
         "Handle AddMod change to [In-Branch Artifact to Delete]",
         "Handle Relation change to [In-Branch Artifact to Delete]",
         "Handle AddMod change to [In-Branch Artifact to Delete Changed]",
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
