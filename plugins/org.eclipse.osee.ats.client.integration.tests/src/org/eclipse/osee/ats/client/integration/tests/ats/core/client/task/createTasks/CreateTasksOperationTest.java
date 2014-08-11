/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.core.client.task.createTasks;

import java.rmi.activation.Activator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.client.demo.DemoSawBuilds;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.ats.core.client.AtsTestUtil;
import org.eclipse.osee.ats.core.client.branch.AtsBranchManagerCore;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.task.createtasks.CreateTasksOperation;
import org.eclipse.osee.ats.core.client.task.createtasks.GenerateTaskOpList;
import org.eclipse.osee.ats.core.client.task.createtasks.ITaskTitleProvider;
import org.eclipse.osee.ats.core.client.task.createtasks.TaskEnum;
import org.eclipse.osee.ats.core.client.task.createtasks.TaskMetadata;
import org.eclipse.osee.ats.core.client.task.createtasks.TaskOpModify;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.util.AtsChangeSet;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.core.operation.NullOperationLogger;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.IChangeWorker;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData;
import org.eclipse.osee.framework.skynet.core.revision.LoadChangeType;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * Test unit for {@link CreateTasksOperation}
 * 
 * @author Shawn F. Cook
 */
public class CreateTasksOperationTest {

   private final static String mockTaskTitlePrefix = "Task for ChangedArt:";
   private final static String artifactNamePrefix = CreateTasksOperationTest.class.getSimpleName();
   private boolean isPopulated = false;
   private GenerateTaskOpList genTaskOpList;
   private TeamWorkFlowArtifact destTeamWf1_Proper;
   private TeamWorkFlowArtifact destTeamWf2_ChangesWithoutTasks;
   private TeamWorkFlowArtifact destTeamWf3_TasksWithoutChanges;
   private TeamWorkFlowArtifact reqTeamWf;
   private IAtsActionableItem aia1_Proper;
   private IAtsActionableItem aia2_ChangesWithoutTasks;
   private IAtsActionableItem aia3_TasksWithoutChanges;
   private IAtsVersion ver1_Proper;
   private IAtsVersion ver2_ChangesWithoutTasks;
   private IAtsVersion ver3_TasksWithoutChanges;
   private ChangeData changeData_Proper;
   private ChangeData changeData_ChangesWithoutTasks;
   private ChangeData changeData_TasksWithoutChanges;

   @BeforeClass
   @AfterClass
   public static void cleanup() throws Exception {
      AtsTestUtil.cleanup();
   }

   private void assert_Tasks_OriginalData(TeamWorkFlowArtifact teamWf) throws MultipleAttributesExist, OseeCoreException {
      Collection<TaskArtifact> taskArts = teamWf.getTaskArtifacts();
      for (TaskArtifact taskArt : taskArts) {
         //Verify that none of the tasks are the generated tasks.
         Assert.assertTrue(taskArt.getName().contains(artifactNamePrefix));
         Assert.assertTrue(!taskArt.getName().contains(mockTaskTitlePrefix));

         //Verify that non of the tasks had their notes modified.
         String currentNoteValue = taskArt.getSoleAttributeValueAsString(AtsAttributeTypes.SmaNote, "");
         Assert.assertTrue(!currentNoteValue.contains(TaskOpModify.NO_MATCHING_CHANGE_REPORT_ARTIFACT));
      }
   }

   private void assert_Tasks_NotesModified(TeamWorkFlowArtifact teamWf) throws MultipleAttributesExist, OseeCoreException {
      Collection<TaskArtifact> taskArts = teamWf.getTaskArtifacts();
      for (TaskArtifact taskArt : taskArts) {
         String currentNoteValue = taskArt.getSoleAttributeValueAsString(AtsAttributeTypes.SmaNote, "");
         Assert.assertTrue(currentNoteValue.contains(TaskOpModify.NO_MATCHING_CHANGE_REPORT_ARTIFACT));
      }
   }

   private void assert_Tasks_Generated(TeamWorkFlowArtifact teamWf) throws MultipleAttributesExist, OseeCoreException {
      Collection<TaskArtifact> taskArts = teamWf.getTaskArtifacts();
      for (TaskArtifact taskArt : taskArts) {
         Assert.assertTrue(taskArt.getName().contains(mockTaskTitlePrefix));
      }
   }

   private void runCreateTasksOperation(IAtsVersion destinationVersion, IAtsActionableItem actionableItemArt, ChangeData changeData) throws OseeCoreException {
      OperationLogger stringLogger = NullOperationLogger.getSingleton();
      MockTaskTitleProvider taskTitleProvider = new MockTaskTitleProvider();
      AtsChangeSet changes = new AtsChangeSet(artifactNamePrefix + " - testCreateTasksOperation");
      XResultData resultData = new XResultData();
      resultData.clear();

      //Notice that the Actionable Item used is what will determine which TeamWF the CreateTasksOperation will chose
      // Kind of more complicated testing environment than I would prefer, but that's how it goes.
      CreateTasksOperation createTasksOp =
         new CreateTasksOperation(destinationVersion, actionableItemArt, changeData, reqTeamWf, false, resultData,
            changes, stringLogger, taskTitleProvider);
      Operations.executeWorkAndCheckStatus(createTasksOp);
      if (!changes.isEmpty()) {
         changes.execute();
      }
   }

   @org.junit.Test
   public void test_Case_Proper() throws OseeCoreException, InterruptedException {
      ensurePopulated();

      assert_Tasks_OriginalData(destTeamWf1_Proper);
      assert_Tasks_OriginalData(destTeamWf2_ChangesWithoutTasks);
      assert_Tasks_OriginalData(destTeamWf3_TasksWithoutChanges);

      //There should be NO changes to any of the data before or after this test.
      runCreateTasksOperation(ver1_Proper, aia1_Proper, changeData_Proper);

      assert_Tasks_OriginalData(destTeamWf1_Proper);
      assert_Tasks_OriginalData(destTeamWf2_ChangesWithoutTasks);
      assert_Tasks_OriginalData(destTeamWf3_TasksWithoutChanges);

      cleanupAndReset();
   }

   @org.junit.Test
   public void test_Case_ChangesWithoutTasks() throws OseeCoreException, InterruptedException {
      ensurePopulated();

      assert_Tasks_OriginalData(destTeamWf1_Proper);
      assert_Tasks_OriginalData(destTeamWf2_ChangesWithoutTasks);
      assert_Tasks_OriginalData(destTeamWf3_TasksWithoutChanges);

      //There should be NO changes to any of the data before or after this test.
      runCreateTasksOperation(ver2_ChangesWithoutTasks, aia2_ChangesWithoutTasks, changeData_ChangesWithoutTasks);

      assert_Tasks_OriginalData(destTeamWf1_Proper);
      assert_Tasks_Generated(destTeamWf2_ChangesWithoutTasks);
      assert_Tasks_OriginalData(destTeamWf3_TasksWithoutChanges);

      cleanupAndReset();
   }

   @org.junit.Test
   public void test_Case_TasksWithoutChanges() throws OseeCoreException, InterruptedException {
      ensurePopulated();

      assert_Tasks_OriginalData(destTeamWf1_Proper);
      assert_Tasks_OriginalData(destTeamWf2_ChangesWithoutTasks);
      assert_Tasks_OriginalData(destTeamWf3_TasksWithoutChanges);

      //There should be NO changes to any of the data before or after this test.
      runCreateTasksOperation(ver3_TasksWithoutChanges, aia3_TasksWithoutChanges, changeData_TasksWithoutChanges);

      assert_Tasks_OriginalData(destTeamWf1_Proper);
      assert_Tasks_OriginalData(destTeamWf2_ChangesWithoutTasks);
      assert_Tasks_NotesModified(destTeamWf3_TasksWithoutChanges);

      cleanupAndReset();
   }

   @org.junit.Test
   public void testGenerateTaskOpList() throws OseeCoreException, InterruptedException {
      ensurePopulated();

      //All changes and tasks should be accounted for - so no changes should be needed.
      List<TaskMetadata> metadatasProper = genTaskOpList.generate(changeData_Proper, destTeamWf1_Proper);
      Assert.assertEquals(0, metadatasProper.size());

      //Changes without tasks should result in task creation - one for each change
      List<TaskMetadata> metadatasChangesWithoutTasks =
         genTaskOpList.generate(changeData_ChangesWithoutTasks, destTeamWf2_ChangesWithoutTasks);
      Assert.assertEquals(changeData_ChangesWithoutTasks.getChanges().size(), metadatasChangesWithoutTasks.size());
      for (TaskMetadata metadata : metadatasChangesWithoutTasks) {
         Assert.assertEquals(metadata.getTaskEnum(), TaskEnum.CREATE);
      }

      //Tasks without changes should result in task modification - one for each task
      List<TaskMetadata> metadatasTasksWithoutChanges =
         genTaskOpList.generate(changeData_TasksWithoutChanges, destTeamWf3_TasksWithoutChanges);
      Assert.assertEquals(destTeamWf3_TasksWithoutChanges.getTaskArtifacts().size(),
         metadatasTasksWithoutChanges.size());
      for (TaskMetadata metadata : metadatasProper) {
         Assert.assertEquals(metadata.getTaskEnum(), TaskEnum.MODIFY);
      }

      cleanupAndReset();
   }

   private void cleanupAndReset() throws OseeCoreException {
      AtsTestUtil.cleanupAndReset(artifactNamePrefix);
      isPopulated = false;
   }

   private void ensurePopulated() throws OseeCoreException, InterruptedException {
      if (!isPopulated) {
         AtsTestUtil.cleanupAndReset(artifactNamePrefix);
         genTaskOpList = new GenerateTaskOpList();
         destTeamWf1_Proper = AtsTestUtil.getTeamWf();
         destTeamWf2_ChangesWithoutTasks = AtsTestUtil.getTeamWf2();
         destTeamWf3_TasksWithoutChanges = AtsTestUtil.getTeamWf3();
         destTeamWf1_Proper.setName(destTeamWf1_Proper.getName() + " Proper");
         destTeamWf2_ChangesWithoutTasks.setName(destTeamWf2_ChangesWithoutTasks.getName() + " ChangesWithoutTasks");
         destTeamWf3_TasksWithoutChanges.setName(destTeamWf3_TasksWithoutChanges.getName() + " TasksWithoutChanges");

         ver1_Proper = AtsTestUtil.getVerArt1();
         ver2_ChangesWithoutTasks = AtsTestUtil.getVerArt2();
         ver3_TasksWithoutChanges = AtsTestUtil.getVerArt3();
         ver1_Proper.setName(ver1_Proper.getName() + " Proper");
         ver2_ChangesWithoutTasks.setName(ver2_ChangesWithoutTasks.getName() + " ChangesWithoutTasks");
         ver3_TasksWithoutChanges.setName(ver3_TasksWithoutChanges.getName() + " TasksWithoutChanges");

         AtsClientService.get().getVersionService().setTargetedVersion(destTeamWf1_Proper, ver1_Proper);
         AtsClientService.get().getVersionService().setTargetedVersion(destTeamWf2_ChangesWithoutTasks, ver2_ChangesWithoutTasks);
         AtsClientService.get().getVersionService().setTargetedVersion(destTeamWf3_TasksWithoutChanges, ver3_TasksWithoutChanges);

         IAtsVersion verArt4 = AtsTestUtil.getVerArt4();
         verArt4.setBaselineBranchUuid(DemoSawBuilds.SAW_Bld_1.getUuid());
         verArt4.setAllowCreateBranch(true);

         reqTeamWf = AtsTestUtil.getTeamWf4();
         Result result = AtsBranchManagerCore.createWorkingBranch_Validate(reqTeamWf);
         Assert.assertTrue(result.isTrue());

         Job createBranchJob = AtsBranchManagerCore.createWorkingBranch_Create(reqTeamWf);
         createBranchJob.join();
         int count = 0;
         while (count++ < 10 && reqTeamWf.getWorkingBranch() == null) {
            Thread.sleep(200);//Needed due to some multi-threaded nonsense
         }
         reqTeamWf.setRelations(AtsRelationTypes.Derive_To, Collections.getAggregate(destTeamWf1_Proper,
            destTeamWf2_ChangesWithoutTasks, destTeamWf3_TasksWithoutChanges));
         destTeamWf1_Proper.setRelations(AtsRelationTypes.Derive_From, Collections.getAggregate(reqTeamWf));
         destTeamWf2_ChangesWithoutTasks.setRelations(AtsRelationTypes.Derive_From, Collections.getAggregate(reqTeamWf));
         destTeamWf3_TasksWithoutChanges.setRelations(AtsRelationTypes.Derive_From, Collections.getAggregate(reqTeamWf));

         changeData_Proper = createProperChangesAndTasks(destTeamWf1_Proper);
         changeData_ChangesWithoutTasks = createChangesWithoutTasks();
         changeData_TasksWithoutChanges = createTasksWithoutChanges(destTeamWf3_TasksWithoutChanges);

         aia1_Proper = AtsTestUtil.getTestAi();
         aia2_ChangesWithoutTasks = AtsTestUtil.getTestAi2();
         aia3_TasksWithoutChanges = AtsTestUtil.getTestAi3();
         aia1_Proper.setName(aia1_Proper.getName() + " Proper");
         aia2_ChangesWithoutTasks.setName(aia2_ChangesWithoutTasks.getName() + " ChangesWithoutTasks");
         aia3_TasksWithoutChanges.setName(aia1_Proper.getName() + " TasksWithoutChanges");

         isPopulated = true;
      }
   }

   private ChangeData createProperChangesAndTasks(TeamWorkFlowArtifact destTeamWf) throws OseeCoreException {

      //Create MockChange objects
      Artifact changeArt01 =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, reqTeamWf.getWorkingBranch(),
            artifactNamePrefix + " Change Art 01 - Proper");
      Artifact changeArt02 =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, reqTeamWf.getWorkingBranch(),
            artifactNamePrefix + " Change Art 02 - Proper");
      Artifact changeArt03 =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, reqTeamWf.getWorkingBranch(),
            artifactNamePrefix + " Change Art 03 - Proper");
      Artifact changeArt04 =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, reqTeamWf.getWorkingBranch(),
            artifactNamePrefix + " Change Art 04 - Proper");
      Artifact changeArt05 =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, reqTeamWf.getWorkingBranch(),
            artifactNamePrefix + " Change Art 05 - Proper");

      Change mockChange01 = new MockChange(changeArt01);
      Change mockChange02 = new MockChange(changeArt02);
      Change mockChange03 = new MockChange(changeArt03);
      Change mockChange04 = new MockChange(changeArt04);
      Change mockChange05 = new MockChange(changeArt05);

      Date createdDate = new Date();

      AtsChangeSet changes = new AtsChangeSet(artifactNamePrefix + " - createProperChangesAndTasks");
      TaskArtifact task01 =
         destTeamWf.createNewTask(artifactNamePrefix + " Task 01", createdDate, AtsCoreUsers.SYSTEM_USER, changes);
      TaskArtifact task02 =
         destTeamWf.createNewTask(artifactNamePrefix + " Task 02", createdDate, AtsCoreUsers.SYSTEM_USER, changes);
      TaskArtifact task03 =
         destTeamWf.createNewTask(artifactNamePrefix + " Task 03", createdDate, AtsCoreUsers.SYSTEM_USER, changes);
      TaskArtifact task04 =
         destTeamWf.createNewTask(artifactNamePrefix + " Task 04", createdDate, AtsCoreUsers.SYSTEM_USER, changes);
      TaskArtifact task05 =
         destTeamWf.createNewTask(artifactNamePrefix + " Task 05", createdDate, AtsCoreUsers.SYSTEM_USER, changes);
      task01.setSoleAttributeFromString(AtsAttributeTypes.TaskToChangedArtifactReference, changeArt01.getGuid());
      task02.setSoleAttributeFromString(AtsAttributeTypes.TaskToChangedArtifactReference, changeArt02.getGuid());
      task03.setSoleAttributeFromString(AtsAttributeTypes.TaskToChangedArtifactReference, changeArt03.getGuid());
      task04.setSoleAttributeFromString(AtsAttributeTypes.TaskToChangedArtifactReference, changeArt04.getGuid());
      task05.setSoleAttributeFromString(AtsAttributeTypes.TaskToChangedArtifactReference, changeArt05.getGuid());
      changes.execute();

      Collection<Change> change =
         new ArrayList<Change>(Collections.getAggregate(mockChange01, mockChange02, mockChange03, mockChange04,
            mockChange05));

      ChangeData changeData = new ChangeData(change);

      return changeData;
   }

   private ChangeData createChangesWithoutTasks() throws OseeCoreException {
      //Create MockChange objects
      Artifact changeArt01 =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, reqTeamWf.getWorkingBranch(),
            artifactNamePrefix + " Change Art 01 - No task");
      Artifact changeArt02 =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, reqTeamWf.getWorkingBranch(),
            artifactNamePrefix + " Change Art 02 - No task");
      Artifact changeArt03 =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, reqTeamWf.getWorkingBranch(),
            artifactNamePrefix + " Change Art 03 - No task");
      Artifact changeArt04 =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, reqTeamWf.getWorkingBranch(),
            artifactNamePrefix + " Change Art 04 - No task");
      Artifact changeArt05 =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.GeneralData, reqTeamWf.getWorkingBranch(),
            artifactNamePrefix + " Change Art 05 - No task");

      Change mockChange01 = new MockChange(changeArt01);
      Change mockChange02 = new MockChange(changeArt02);
      Change mockChange03 = new MockChange(changeArt03);
      Change mockChange04 = new MockChange(changeArt04);
      Change mockChange05 = new MockChange(changeArt05);

      SkynetTransaction transaction =
         TransactionManager.createTransaction(reqTeamWf.getWorkingBranch(),
            artifactNamePrefix + " - createChangesWithoutTasks");
      changeArt01.persist(transaction);
      changeArt02.persist(transaction);
      changeArt03.persist(transaction);
      changeArt04.persist(transaction);
      changeArt05.persist(transaction);
      transaction.execute();

      Collection<Change> changes =
         new ArrayList<Change>(Collections.getAggregate(mockChange01, mockChange02, mockChange03, mockChange04,
            mockChange05));

      ChangeData changeData = new ChangeData(changes);

      return changeData;
   }

   private ChangeData createTasksWithoutChanges(TeamWorkFlowArtifact destTeamWf) throws OseeCoreException {
      Date createdDate = new Date();
      AtsChangeSet changes = new AtsChangeSet(artifactNamePrefix + " - createTasksWithoutChanges");

      destTeamWf.createNewTask(artifactNamePrefix + " Task 01 - No changed artifact", createdDate,
         AtsCoreUsers.SYSTEM_USER, changes);
      destTeamWf.createNewTask(artifactNamePrefix + " Task 02 - No changed artifact", createdDate,
         AtsCoreUsers.SYSTEM_USER, changes);
      destTeamWf.createNewTask(artifactNamePrefix + " Task 03 - No changed artifact", createdDate,
         AtsCoreUsers.SYSTEM_USER, changes);
      destTeamWf.createNewTask(artifactNamePrefix + " Task 04 - No changed artifact", createdDate,
         AtsCoreUsers.SYSTEM_USER, changes);
      destTeamWf.createNewTask(artifactNamePrefix + " Task 05 - No changed artifact", createdDate,
         AtsCoreUsers.SYSTEM_USER, changes);

      changes.execute();

      ChangeData changeData = new ChangeData(new ArrayList<Change>());
      return changeData;
   }
   private class MockChange extends Change {

      public MockChange(Artifact changeArtifact) {
         super(null, 0, 0, null, null, false, changeArtifact, null);
      }

      @Override
      public long getItemTypeId() {
         OseeLog.log(Activator.class, Level.WARNING,
            "CreateTasksOperationTest.MockChange.getItemTypeId() - Unimplemented method called");
         return 0L;
      }

      @Override
      public String getIsValue() {
         OseeLog.log(Activator.class, Level.WARNING,
            "CreateTasksOperationTest.MockChange.getIsValue() - Unimplemented method called");
         return null;
      }

      @Override
      public String getWasValue() {
         OseeLog.log(Activator.class, Level.WARNING,
            "CreateTasksOperationTest.MockChange.getWasValue() - Unimplemented method called");
         return null;
      }

      @Override
      public String getItemTypeName() {
         OseeLog.log(Activator.class, Level.WARNING,
            "CreateTasksOperationTest.MockChange.getItemTypeName() - Unimplemented method called");
         return null;
      }

      @Override
      public String getName() {
         return this.getChangeArtifact().getName();
      }

      @Override
      public String getItemKind() {
         OseeLog.log(Activator.class, Level.WARNING,
            "CreateTasksOperationTest.MockChange.getItemKind() - Unimplemented method called");
         return null;
      }

      @Override
      public int getItemId() {
         OseeLog.log(Activator.class, Level.WARNING,
            "CreateTasksOperationTest.MockChange.getItemId() - Unimplemented method called");
         return 0;
      }

      @Override
      public LoadChangeType getChangeType() {
         OseeLog.log(Activator.class, Level.WARNING,
            "CreateTasksOperationTest.MockChange.getChangeType() - Unimplemented method called");
         return null;
      }

      @Override
      public Class<? extends IChangeWorker> getWorker() {
         OseeLog.log(Activator.class, Level.WARNING,
            "CreateTasksOperationTest.MockChange.getWorker() - Unimplemented method called");
         return null;
      }
   }

   private class MockTaskTitleProvider implements ITaskTitleProvider {
      @Override
      public String getTaskTitle(TaskMetadata metadata) {
         Artifact changedArt = metadata.getChangedArtifact();
         String changedArtGuid = changedArt.getGuid();
         return mockTaskTitlePrefix + changedArtGuid;
      }

   }
}
